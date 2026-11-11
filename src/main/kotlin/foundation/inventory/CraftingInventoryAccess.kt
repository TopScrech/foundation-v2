package foundation.inventory

import net.minecraft.world.inventory.CraftingContainer
import net.minecraft.world.item.ItemStack

fun CraftingContainer.getSizeInventory(): Int = containerSize
fun CraftingContainer.getStackInSlot(slot: Int): ItemStack = getItem(slot)
fun CraftingContainer.getStackInRowAndColumn(row: Int, column: Int): ItemStack =
    if (row in 0 until width && column in 0 until height) getItem(row + column * width) else ItemStack.EMPTY
