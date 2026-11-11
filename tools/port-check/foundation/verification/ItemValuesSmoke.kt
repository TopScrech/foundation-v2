package foundation.verification

import foundation.item.FoundationItemComponents
import foundation.item.FoundationItemVariants
import foundation.item.FoundationVariantItem
import foundation.item.legacyItemValue
import foundation.persistence.FoundationItemStackPersistence
import io.netty.buffer.Unpooled
import micdoodle8.mods.galacticraft.core.items.ItemOxygenTank
import net.minecraft.core.HolderLookup
import net.minecraft.core.RegistryAccess
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

fun verifyItemValues(lookup: HolderLookup.Provider) {
    val access = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY)
    fun roundTrip(stack: ItemStack, expected: Int) {
        check(stack.legacyItemValue == expected)
        stack.set(DataComponents.CUSTOM_NAME, Component.literal("Retained custom name"))
        val saved = FoundationItemStackPersistence.read(FoundationItemStackPersistence.write(stack, lookup), lookup)
        check(saved.legacyItemValue == expected && ItemStack.matches(saved, stack))
        val bytes = RegistryFriendlyByteBuf(Unpooled.buffer(), access)
        try {
            ItemStack.STREAM_CODEC.encode(bytes, stack)
            val received = ItemStack.STREAM_CODEC.decode(bytes)
            check(received.legacyItemValue == expected && ItemStack.matches(received, stack) && !bytes.isReadable)
        } finally { bytes.release() }
    }
    var variants = 0
    for (item in BuiltInRegistries.ITEM) {
        if (item !is FoundationVariantItem) continue
        for (metadata in 0..255) {
            if (item.variant(metadata) == null) continue
            val stack = FoundationItemVariants.stack(item, 1, metadata)
            check(stack.damageValue == 0 && !stack.isDamageableItem)
            roundTrip(stack, metadata)
            val id = BuiltInRegistries.ITEM.getKey(item).toString()
            val legacy = CompoundTag().also { it.putString("id", id); it.putByte("Count", 1); it.putShort("Damage", metadata.toShort()) }
            check(FoundationItemStackPersistence.read(legacy, lookup).legacyItemValue == metadata)
            variants++
        }
    }
    check(variants == 136) { "Expected 136 declared variants, got $variants" }
    for (name in listOf("steel_pickaxe", "steel_axe", "steel_shovel", "steel_hoe", "steel_sword", "titanium_pickaxe", "titanium_axe", "titanium_shovel", "titanium_hoe", "titanium_sword")) {
        val namespace = if (name.startsWith("steel_")) "galacticraftcore" else "galacticraftplanets"
        val item = requireNotNull(BuiltInRegistries.ITEM.getValue(Identifier.parse("$namespace:$name")))
        for (damage in listOf(0, 1, 365, ItemStack(item).maxDamage)) {
            val stack = ItemStack(item)
            stack.damageValue = damage
            check(!stack.has(FoundationItemComponents.LEGACY_METADATA))
            roundTrip(stack, damage)
        }
    }
    for (name in listOf("oxygen_tank_light_full", "oxygen_tank_med_full", "oxygen_tank_heavy_full")) {
        val item = requireNotNull(BuiltInRegistries.ITEM.getValue(Identifier.parse("galacticraftcore:$name"))) as ItemOxygenTank
        for (used in listOf(0, 1, item.capacity / 2, item.capacity)) {
            val stack = ItemOxygenTank.stack(item, 1, used)
            check(stack.damageValue == 0 && !stack.has(DataComponents.DAMAGE) && !stack.isDamageableItem)
            roundTrip(stack, used)
        }
    }
    for (damage in listOf(0, 1, 500, ItemStack(Items.DIAMOND_SWORD).maxDamage)) {
        val tool = ItemStack(Items.DIAMOND_SWORD).also { it.damageValue = damage }
        roundTrip(tool, damage)
    }
    val explicitMetadata = ItemStack(Items.IRON_INGOT).also { it.set(FoundationItemComponents.LEGACY_METADATA, 32767) }
    roundTrip(explicitMetadata, 32767)
    check(ItemStack.EMPTY.legacyItemValue == 0 && ItemStack(Items.STONE).legacyItemValue == 0)
    println("Passed native item value reads for 136 variants, ten tool profiles, three oxygen capacities, vanilla damage, explicit wildcard metadata, empty stacks, legacy imports, named stack saves, and packets")
}
