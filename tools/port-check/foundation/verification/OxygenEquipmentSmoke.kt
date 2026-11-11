package foundation.verification

import com.google.gson.JsonParser
import com.mojang.serialization.JsonOps
import foundation.item.FoundationEquipmentItem
import foundation.item.FoundationItemComponents
import foundation.item.FoundationItemVariants
import foundation.persistence.FoundationItemStackPersistence
import foundation.recipe.FoundationStackIngredient
import io.netty.buffer.Unpooled
import micdoodle8.mods.galacticraft.core.inventory.InventoryExtended
import micdoodle8.mods.galacticraft.core.items.ItemOxygenTank
import micdoodle8.mods.galacticraft.core.items.ItemParaChute
import net.minecraft.client.renderer.item.ItemModels
import net.minecraft.client.renderer.item.CuboidItemModelWrapper
import net.minecraft.core.HolderLookup
import net.minecraft.core.RegistryAccess
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.Item
import net.minecraft.world.item.component.TooltipDisplay
import net.minecraft.world.item.crafting.CraftingInput
import net.minecraft.world.item.crafting.RepairItemRecipe
import java.nio.file.Files
import java.nio.file.Path
import java.util.function.Consumer

fun verifyOxygenEquipment(resources: Path, lookup: HolderLookup.Provider) {
    fun item(name: String): Item = requireNotNull(BuiltInRegistries.ITEM.getValue(Identifier.parse("galacticraftcore:$name")))
    for ((name, slot) in listOf("oxygen_mask" to 0, "oxygen_gear" to 1, "parachute" to 4)) {
        val equipment = item(name) as FoundationEquipmentItem
        val inventory = InventoryExtended()
        val held = if (name == "parachute") FoundationItemVariants.stack(item(name), 1, 13) else ItemStack(item(name), 3)
        held.set(DataComponents.CUSTOM_NAME, Component.literal("Retained equipment"))
        check(equipment.equipmentSlots == listOf(slot) && equipment.equip(inventory, held))
        check(inventory.getItem(slot).count == 1 && inventory.getItem(slot).get(DataComponents.CUSTOM_NAME)?.string == "Retained equipment")
        check(held.count == if (name == "parachute") 0 else 2)
        val another = ItemStack(item(name))
        check(!equipment.equip(inventory, another) && another.count == 1)
        check(!equipment.equip(InventoryExtended(), ItemStack(Items.DIAMOND)))
        if (name == "parachute") check(FoundationItemVariants.metadata(inventory.getItem(slot)) == 13)
    }
    val access = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY)
    for ((tier, name) in listOf(1 to "oxygen_tank_light_full", 2 to "oxygen_tank_med_full", 3 to "oxygen_tank_heavy_full")) {
        val tank = item(name) as ItemOxygenTank
        val capacity = tier * 900
        check(tank.capacity == capacity && tank.equipmentSlots == listOf(2, 3))
        check(tank.creativeStacks().map(ItemOxygenTank::used) == listOf(0, capacity))
        val inventory = InventoryExtended()
        for (used in listOf(0, 1, capacity / 2, capacity)) {
            val stack = ItemOxygenTank.stack(tank, 1, used)
            check(ItemOxygenTank.used(stack) == used && ItemOxygenTank.capacityOf(stack) == capacity)
            check(tank.remainingOxygen(stack) == capacity - used)
            check(stack.maxStackSize == 1 && !stack.isDamageableItem)
            check(!stack.has(DataComponents.MAX_DAMAGE) && !stack.has(DataComponents.DAMAGE))
            check(!stack.has(DataComponents.ENCHANTABLE) && !stack.has(DataComponents.REPAIRABLE))
            check(tank.isBarVisible(stack) == (used > 0))
            check(tank.getBarWidth(stack) in 0..13)
            val lines = ArrayList<Component>()
            tank.appendHoverText(stack, Item.TooltipContext.EMPTY, TooltipDisplay.DEFAULT, Consumer(lines::add), TooltipFlag.NORMAL)
            check(lines.single().string.endsWith((capacity - used).toString()))
            val restored = FoundationItemStackPersistence.read(FoundationItemStackPersistence.write(stack, lookup), lookup)
            check(ItemStack.matches(stack, restored))
            val old = CompoundTag().also {
                it.putString("id", "galacticraftcore:$name"); it.putByte("Count", 1); it.putShort("Damage", used.toShort())
                it.put("tag", CompoundTag().also { custom -> custom.putInt("preserved", 42) })
            }
            val imported = FoundationItemStackPersistence.read(old, lookup)
            check(ItemOxygenTank.used(imported) == used && !imported.isDamageableItem)
            check(imported.get(DataComponents.CUSTOM_DATA)?.copyTag()?.getIntOr("preserved", 0) == 42)
            val buffer = RegistryFriendlyByteBuf(Unpooled.buffer(), access)
            try {
                ItemStack.STREAM_CODEC.encode(buffer, stack)
                check(ItemStack.matches(stack, ItemStack.STREAM_CODEC.decode(buffer)))
            } finally { buffer.release() }
            val copied = stack.copy()
            ItemOxygenTank.setUsed(copied, capacity + 1)
            check(ItemOxygenTank.used(stack) == used && ItemOxygenTank.used(copied) == capacity)
            ItemOxygenTank.setUsed(copied, -1)
            check(ItemOxygenTank.used(copied) == 0 && !copied.has(FoundationItemComponents.OXYGEN_USED))
            val recipeInput = CraftingInput.of(2, 1, listOf(stack.copy(), stack.copy()))
            check(RepairItemRecipe.INSTANCE.assemble(recipeInput).isEmpty)
        }
        check(tank.getBarWidth(ItemOxygenTank.stack(tank, 1, 0)) == 13)
        check(tank.getBarWidth(ItemOxygenTank.stack(tank, 1, capacity)) == 0)
        for (used in listOf(13, 27)) {
            val held = ItemOxygenTank.stack(tank, 1, used)
            check(tank.equip(inventory, held) && held.isEmpty)
        }
        check(ItemOxygenTank.used(inventory.getItem(2)) == 13 && ItemOxygenTank.used(inventory.getItem(3)) == 27)
        val third = ItemOxygenTank.stack(tank, 1, 37)
        check(!tank.equip(inventory, third) && ItemOxygenTank.used(third) == 37)
        val full = ItemOxygenTank.stack(tank, 1, 0)
        val empty = ItemOxygenTank.stack(tank, 1, capacity)
        check(!FoundationItemVariants.sameVariant(full, empty))
        check(FoundationStackIngredient.of(empty).test(empty) && !FoundationStackIngredient.of(empty).test(full))
        check(runCatching { ItemOxygenTank.stack(tank, 1, capacity + 1) }.isFailure)
        val bad = FoundationItemStackPersistence.write(empty, lookup)
        bad.getCompoundOrEmpty("components").putInt("foundation:oxygen_used", capacity + 1)
        check(runCatching { FoundationItemStackPersistence.read(bad, lookup) }.isFailure)
    }
    for (name in listOf("oxygen_mask", "oxygen_gear", "oxygen_tank_light_full", "oxygen_tank_med_full", "oxygen_tank_heavy_full")) {
        val json = JsonParser.parseString(Files.readString(resources.resolve("assets/galacticraftcore/items/$name.json")))
        val model = ItemModels.CODEC.parse(JsonOps.INSTANCE, json.asJsonObject["model"]).getOrThrow() as CuboidItemModelWrapper.Unbaked
        check(model.model() == Identifier.parse("galacticraftcore:item/$name"))
    }
    val legacyDyes = listOf(1, 13, 7, 4, 5, 12, 14, 8, 6, 11, 3, 15, 2, 9, 10, 0)
    for (meta in 0..15) check(ItemParaChute.getParachuteDamageValueFromDye(meta) == legacyDyes[meta])
    for (color in DyeColor.entries) check(ItemParaChute.getDyeEnumFromParachuteDamage(ItemParaChute.getParachuteDamageValueFromDyeEnum(color)) == color)
    check(ItemParaChute.getParachuteDamageValueFromDye(99) == 1)
    println("Passed native mask, gear, parachute and tank equipment transfers, three oxygen capacities, legacy damage imports, gauges, packet codecs, native repair rejection, resource ingredient matching, and sixteen parachute dye mappings")
}
