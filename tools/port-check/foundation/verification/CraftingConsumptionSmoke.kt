package foundation.verification

import foundation.inventory.FoundationCraftingConsumption
import foundation.item.FoundationItemVariants
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

fun verifyCraftingConsumption() {
    val moon = requireNotNull(BuiltInRegistries.ITEM.getValue(Identifier.parse("galacticraftcore:item_basic_moon")))
    val remainderItem = requireNotNull(BuiltInRegistries.ITEM.getValue(Identifier.parse("foundation_test:crafting_remainder")))
    val failingItem = requireNotNull(BuiltInRegistries.ITEM.getValue(Identifier.parse("foundation_test:failed_remainder")))
    val grid = SimpleContainer(3)
    grid.setItem(0, ItemStack(Items.WATER_BUCKET))
    grid.setItem(1, FoundationItemVariants.stack(moon, 3, 2))
    val inserted = ArrayList<ItemStack>()
    val dropped = ArrayList<ItemStack>()
    FoundationCraftingConsumption.consume(grid, insert = {
        inserted.add(it.copy())
        it.count = 0
    }, drop = { dropped.add(it.copy()) })
    check(grid.getItem(0).isEmpty && grid.getItem(1).count == 2)
    check(FoundationItemVariants.metadata(grid.getItem(1)) == 2)
    check(inserted.single().item === Items.BUCKET && inserted.single().count == 1 && dropped.isEmpty())

    // A full player inventory returns the remainder to the emptied ingredient slot
    grid.clearContent()
    grid.setItem(2, ItemStack(Items.WATER_BUCKET))
    FoundationCraftingConsumption.consume(grid, insert = {}, drop = { dropped.add(it.copy()) })
    check(grid.getItem(2).item === Items.BUCKET && dropped.isEmpty())

    // Partial insertion only drops the remaining count when the ingredient slot is still occupied
    grid.clearContent()
    grid.setItem(0, ItemStack(remainderItem, 3))
    FoundationCraftingConsumption.consume(grid, insert = { it.shrink(1) }, drop = { dropped.add(it.copy()) })
    check(grid.getItem(0).item === remainderItem && grid.getItem(0).count == 2)
    val custom = dropped.single()
    check(custom.count == 1 && FoundationItemVariants.metadata(custom) == 2)
    check(custom.get(DataComponents.CUSTOM_DATA)?.copyTag()?.getLongOr("linkedUUIDMost", 0L) == 12345L)

    // A hook failure leaves every ingredient intact, including earlier slots
    grid.clearContent()
    grid.setItem(0, ItemStack(Items.WATER_BUCKET))
    grid.setItem(2, ItemStack(failingItem, 2))
    check(runCatching { FoundationCraftingConsumption.consume(grid, insert = {}, drop = {}) }.isFailure)
    check(grid.getItem(0).item === Items.WATER_BUCKET && grid.getItem(0).count == 1)
    check(grid.getItem(2).item === failingItem && grid.getItem(2).count == 2)
    grid.clearContent()
    FoundationCraftingConsumption.consume(grid, insert = { error("Empty grid inserted a remainder") }, drop = { error("Empty grid dropped a remainder") })
    check(grid.isEmpty)
    println("Passed native crafting consumption, last-item remainders, partial insertion, component preservation, and failed-hook input protection")
}
