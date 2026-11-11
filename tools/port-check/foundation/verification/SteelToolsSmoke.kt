package foundation.verification

import com.google.gson.JsonParser
import com.mojang.serialization.JsonOps
import foundation.item.FoundationItemProperties
import foundation.persistence.FoundationItemStackPersistence
import io.netty.buffer.Unpooled
import net.minecraft.client.renderer.item.CuboidItemModelWrapper
import net.minecraft.client.renderer.item.ItemModels
import net.minecraft.core.HolderLookup
import net.minecraft.core.RegistryAccess
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.BlockTransformers
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.block.Blocks
import java.nio.file.Files
import java.nio.file.Path

fun verifySteelTools(resources: Path, lookup: HolderLookup.Provider) {
    val access = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY)
    val enchantments = lookup.lookupOrThrow(Registries.ENCHANTMENT)
    val profiles = listOf(
        Triple("steel_pickaxe", 3f, -2.8f), Triple("steel_axe", 6f, -3f),
        Triple("steel_shovel", 3.5f, -3f), Triple("steel_hoe", 0f, -1f), Triple("steel_sword", 5f, -2.4f)
    )
    for ((name, damage, speed) in profiles) {
        val item = FoundationItemProperties.registered(Identifier.parse("galacticraftcore:$name"))
        val stack = ItemStack(item)
        check(stack.maxDamage == 768 && stack.maxStackSize == 1)
        val modifiers = requireNotNull(stack.get(DataComponents.ATTRIBUTE_MODIFIERS)).modifiers()
        check(modifiers.single { it.attribute() == Attributes.ATTACK_DAMAGE }.modifier().amount() == damage.toDouble())
        check(modifiers.single { it.attribute() == Attributes.ATTACK_SPEED }.modifier().amount() == speed.toDouble())
        if (name == "steel_hoe") check(!stack.has(DataComponents.ENCHANTABLE))
        else check(stack.get(DataComponents.ENCHANTABLE)?.value() == 8)
        check(enchantments.getOrThrow(Enchantments.UNBREAKING).value().isSupportedItem(stack))
        if (name != "steel_sword") check(enchantments.getOrThrow(Enchantments.EFFICIENCY).value().isSupportedItem(stack))
        else check(enchantments.getOrThrow(Enchantments.SHARPNESS).value().isSupportedItem(stack))
        if (name != "steel_hoe") {
            val enchantment = if (name == "steel_sword") Enchantments.SHARPNESS else Enchantments.EFFICIENCY
            check(enchantments.getOrThrow(enchantment).value().isPrimaryItem(stack))
        }
        val transformer = stack.get(DataComponents.BLOCK_TRANSFORMER)?.unwrapKey()?.orElseThrow()
        when (name) {
            "steel_axe" -> check(transformer == BlockTransformers.AXE)
            "steel_hoe" -> check(transformer == BlockTransformers.HOE)
            "steel_shovel" -> check(transformer == BlockTransformers.SHOVEL)
            else -> check(transformer == null)
        }
        val weapon = requireNotNull(stack.get(DataComponents.WEAPON))
        check(weapon.itemDamagePerAttack() == if (name in listOf("steel_hoe", "steel_sword")) 1 else 2)
        val damaged = stack.copy().also { it.damageValue = 367 }
        check(ItemStack.matches(damaged, FoundationItemStackPersistence.read(FoundationItemStackPersistence.write(damaged, lookup), lookup)))
        val legacy = CompoundTag().also {
            it.putString("id", "galacticraftcore:$name"); it.putByte("Count", 1); it.putShort("Damage", 367)
        }
        check(FoundationItemStackPersistence.read(legacy, lookup).damageValue == 367)
        val buffer = RegistryFriendlyByteBuf(Unpooled.buffer(), access)
        try {
            ItemStack.STREAM_CODEC.encode(buffer, damaged)
            check(ItemStack.matches(damaged, ItemStack.STREAM_CODEC.decode(buffer)))
        } finally { buffer.release() }
        val json = JsonParser.parseString(Files.readString(resources.resolve("assets/galacticraftcore/items/$name.json")))
        val model = ItemModels.CODEC.parse(JsonOps.INSTANCE, json.asJsonObject["model"]).getOrThrow() as CuboidItemModelWrapper.Unbaked
        check(model.model() == Identifier.parse("galacticraftcore:item/$name"))
    }
    val axe = ItemStack(FoundationItemProperties.registered(Identifier.parse("galacticraftcore:steel_axe")))
    val tool = requireNotNull(axe.get(DataComponents.TOOL))
    for (block in listOf(Blocks.OAK_LOG, Blocks.CHEST, Blocks.VINE, Blocks.DANDELION, Blocks.SHORT_GRASS, Blocks.WHEAT, Blocks.OAK_SAPLING, Blocks.LILY_PAD)) {
        check(tool.getMiningSpeed(block.defaultBlockState()) == 5f) { "Steel axe lost efficient mining for $block" }
    }
    check(tool.getMiningSpeed(Blocks.STONE.defaultBlockState()) == 1f)
    check(tool.getMiningSpeed(Blocks.OAK_LEAVES.defaultBlockState()) == 1f)
    val shovel = ItemStack(FoundationItemProperties.registered(Identifier.parse("galacticraftcore:steel_shovel")))
    check(requireNotNull(shovel.get(DataComponents.TOOL)).getMiningSpeed(Blocks.DIRT.defaultBlockState()) == 5f)
    println("Passed five native steel tool profiles, legacy combat attributes and durability, hoe enchantability and hit cost, native enchantment tags, block transformer keys, legacy axe plant efficiency, model codecs, saves, and packets")
}
