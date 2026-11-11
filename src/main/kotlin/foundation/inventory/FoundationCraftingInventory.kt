package foundation.inventory

import net.minecraft.core.NonNullList
import net.minecraft.network.chat.Component
import net.minecraft.world.ContainerHelper
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.player.StackedItemContents
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.CraftingContainer
import net.minecraft.world.item.ItemStack

/** A persistent grid can outlive its menu and retain all stack components */
open class FoundationCraftingInventory(private val gridWidth: Int, private val gridHeight: Int) : CraftingContainer {
    @JvmField var stacks: NonNullList<ItemStack> = NonNullList.withSize(gridWidth * gridHeight, ItemStack.EMPTY)

    protected open fun eventHandler(): AbstractContainerMenu? = null
    override fun getWidth(): Int = gridWidth
    override fun getHeight(): Int = gridHeight
    override fun getContainerSize(): Int = stacks.size
    override fun isEmpty(): Boolean = stacks.all { it.isEmpty }
    override fun getItem(slot: Int): ItemStack = stacks[slot]
    override fun getItems(): List<ItemStack> = java.util.List.copyOf(stacks)
    override fun getMaxStackSize(): Int = 64
    override fun setChanged() = Unit
    override fun stillValid(player: Player): Boolean = true
    override fun canPlaceItem(slot: Int, stack: ItemStack): Boolean = true

    override fun removeItem(slot: Int, count: Int): ItemStack = ContainerHelper.removeItem(stacks, slot, count).also {
        if (!it.isEmpty) { setChanged(); eventHandler()?.slotsChanged(this) }
    }
    override fun removeItemNoUpdate(slot: Int): ItemStack = ContainerHelper.takeItem(stacks, slot)
    override fun setItem(slot: Int, stack: ItemStack) {
        setItemNoUpdate(slot, stack)
        eventHandler()?.slotsChanged(this)
    }
    open fun setItemNoUpdate(slot: Int, stack: ItemStack) {
        stacks[slot] = stack
        if (stack.count > maxStackSize) stack.count = maxStackSize
        setChanged()
    }
    override fun clearContent() {
        if (isEmpty) return
        stacks.clear()
        setChanged()
        eventHandler()?.slotsChanged(this)
    }
    override fun fillStackedContents(contents: StackedItemContents) = stacks.forEach(contents::accountSimpleStack)

    open fun getStackInRowAndColumn(row: Int, column: Int): ItemStack =
        if (row in 0 until gridWidth && column in 0 until gridHeight) getItem(row + column * gridWidth) else ItemStack.EMPTY

    // Existing machine and menu callers are migrated independently of their stored grid
    open fun getSizeInventory(): Int = containerSize
    open fun getStackInSlot(slot: Int): ItemStack = getItem(slot)
    open fun decrStackSize(slot: Int, count: Int): ItemStack = removeItem(slot, count)
    open fun removeStackFromSlot(slot: Int): ItemStack = removeItemNoUpdate(slot)
    open fun setInventorySlotContents(slot: Int, stack: ItemStack) = setItem(slot, stack)
    open fun setInventorySlotContentsNoUpdate(slot: Int, stack: ItemStack) = setItemNoUpdate(slot, stack)
    open fun getInventoryStackLimit(): Int = maxStackSize
    open fun markDirty() = setChanged()
    open fun isUsableByPlayer(player: Player): Boolean = stillValid(player)
    open fun isItemValidForSlot(slot: Int, stack: ItemStack): Boolean = canPlaceItem(slot, stack)
    open fun clear() = clearContent()
    open fun getName(): String = "container.crafting"
    open fun hasCustomName(): Boolean = false
    open fun getDisplayName(): Component = Component.translatable(getName())
}
