package foundation.verification

import foundation.inventory.FoundationRocketAssemblySlot
import foundation.item.FoundationItemComponents
import foundation.item.FoundationItemVariants
import galaxyspace.systems.SolarSystem.planets.overworld.inventory.RocketAssemblyInventory
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

fun verifyRocketAssemblySlots() {
    val parts = requireNotNull(BuiltInRegistries.ITEM.getValue(Identifier.parse("galaxyspace:rocket_parts")))
    val modules = requireNotNull(BuiltInRegistries.ITEM.getValue(Identifier.parse("galaxyspace:rocket_modules")))
    val inventory = RocketAssemblyInventory()
    val positions = listOf(2 to 0, 3 to 1, 4 to 1, 5 to 2, 6 to 3, 7 to 3, 8 to 4, 9 to 4)
    for ((position, part) in positions) {
        val slot = FoundationRocketAssemblySlot(inventory, position, 0, 0, parts, modules)
        for (meta in 0..19) check(slot.mayPlace(FoundationItemVariants.stack(parts, 1, meta)) == (meta % 5 == part))
        check(slot.maxStackSize == 1 && slot.getMaxStackSize(ItemStack(parts)) == 1)
        check(!slot.mayPlace(ItemStack.EMPTY) && !slot.mayPlace(ItemStack(Items.STONE)))
        check(!slot.mayPlace(ItemStack(parts).also { it.set(FoundationItemComponents.LEGACY_METADATA, 20 + part) }))
    }
    val moduleSlot = FoundationRocketAssemblySlot(inventory, 10, 0, 0, parts, modules)
    for (meta in 0..10) check(moduleSlot.mayPlace(FoundationItemVariants.stack(modules, 1, meta)) == (meta == 2 || meta in 8..10))
    for (position in 11..13) {
        val slot = FoundationRocketAssemblySlot(inventory, position, 0, 0, parts, modules)
        check(slot.mayPlace(ItemStack(Items.CHEST)) && !slot.mayPlace(ItemStack(Items.STONE)))
        check(!slot.mayPlace(FoundationItemVariants.stack(parts, 1, 0)))
    }
    for (position in listOf(0, 1, 14, 15)) {
        val slot = FoundationRocketAssemblySlot(inventory, position, 0, 0, parts, modules)
        check(!slot.mayPlace(ItemStack(Items.CHEST)))
        check(!slot.mayPlace(FoundationItemVariants.stack(parts, 1, 0)))
        check(!slot.mayPlace(FoundationItemVariants.stack(modules, 1, 2)))
    }
    println("Passed native rocket assembly part, module, chest, reserved-slot, and stack-limit policies")
}
