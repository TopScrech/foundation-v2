package foundation.inventory

import net.minecraft.core.NonNullList
import net.minecraft.network.chat.Component
import net.minecraft.world.Container
import net.minecraft.world.ContainerHelper
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack

/** Slot-addressed schematics are not rectangular vanilla crafting grids */
open class FoundationBenchInventory(size: Int, private val menu: AbstractContainerMenu?) : Container {
    @JvmField var stacks: NonNullList<ItemStack> = NonNullList.withSize(size, ItemStack.EMPTY)

    init { require(size > 0) }

    override fun getContainerSize(): Int = stacks.size
    override fun getItem(slot: Int): ItemStack = stacks.getOrNull(slot) ?: ItemStack.EMPTY
    override fun isEmpty(): Boolean = stacks.all { it.isEmpty }
    override fun getMaxStackSize(): Int = 64
    override fun stillValid(player: Player): Boolean = true
    override fun canPlaceItem(slot: Int, stack: ItemStack): Boolean = false
    override fun setChanged() { menu?.slotsChanged(this) }

    override fun setItem(slot: Int, stack: ItemStack) {
        stacks[slot] = stack
        stack.limitSize(getMaxStackSize(stack))
        setChanged()
    }

    override fun removeItem(slot: Int, count: Int): ItemStack = ContainerHelper.removeItem(stacks, slot, count).also {
        if (!it.isEmpty) setChanged()
    }

    override fun removeItemNoUpdate(slot: Int): ItemStack = ContainerHelper.takeItem(stacks, slot)

    override fun clearContent() {
        if (isEmpty) return
        stacks.clear()
        setChanged()
    }

    open fun getStackInRowAndColumn(column: Int, row: Int): ItemStack =
        if (column in 0 until 5 && row >= 0 && row <= (containerSize - 1) / 5) getItem(column + row * 5) else ItemStack.EMPTY

    // Preserve internal call sites while their menus are migrated to native Slot methods
    open fun getSizeInventory(): Int = containerSize
    open fun getStackInSlot(slot: Int): ItemStack = getItem(slot)
    open fun decrStackSize(slot: Int, count: Int): ItemStack = removeItem(slot, count)
    open fun removeStackFromSlot(slot: Int): ItemStack = removeItemNoUpdate(slot).also { if (!it.isEmpty) setChanged() }
    open fun setInventorySlotContents(slot: Int, stack: ItemStack) = setItem(slot, stack)
    open fun getInventoryStackLimit(): Int = maxStackSize
    open fun markDirty() = setChanged()
    open fun isUsableByPlayer(player: Player): Boolean = stillValid(player)
    open fun isItemValidForSlot(slot: Int, stack: ItemStack): Boolean = canPlaceItem(slot, stack)
    open fun clear() = clearContent()
    open fun getName(): String = "container.crafting"
    open fun hasCustomName(): Boolean = false
    open fun getDisplayName(): Component = Component.translatable(getName())
}
