package galaxyspace.systems.SolarSystem.planets.overworld.inventory

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraft.util.text.ITextComponent

open class RocketAssemblyInventory constructor() : InventoryCrafting(null, 2, 8) {
    /**
     * List of the stacks in the crafting matrix.
     */
    @JvmField
    var stacks: NonNullList<ItemStack>

    /**
     * the width of the crafting inventory
     */
    private val inventoryWidth = 2
    private val inventoryHeight = 8

    /**
     * Class containing the callbacks for the events on_GUIClosed and
     * on_CraftMaxtrixChanged.
     */
    @JvmField
    var craftingEventHandler: Container? = null

    init {
        this.stacks = NonNullList.withSize<ItemStack>(16, ItemStack.EMPTY)
    }

    /**
     * Returns the number of slots in the inventory.
     */
    override fun getSizeInventory(): Int {
        return this.stacks.size
    }

    /**
     * Returns the stack in slot i
     */
    override fun getStackInSlot(index: Int): ItemStack {
        return this.stacks.get(index)
    }

    /**
     * Returns the itemstack in the slot specified (Top left is 0, 0). Args:
     * row, column
     */
    override fun getStackInRowAndColumn(row: Int, column: Int): ItemStack {
        return if (row >= 0 && row < this.inventoryWidth && column >= 0 && column < this.inventoryHeight) this.getStackInSlot(
            row + column * this.inventoryWidth
        ) else ItemStack.EMPTY
    }

    /**
     * Returns the name of the inventory.
     */
    override fun getName(): String {
        return "container.crafting"
    }

    override fun removeStackFromSlot(index: Int): ItemStack {
        return ItemStackHelper.getAndRemove(this.stacks, index)
    }

    override fun decrStackSize(index: Int, count: Int): ItemStack {
        val itemstack: ItemStack = ItemStackHelper.getAndSplit(this.stacks, index, count)

        if (!itemstack.isEmpty()) {
            if (this.craftingEventHandler != null) this.craftingEventHandler!!.onCraftMatrixChanged(this)
            this.markDirty()
        }

        return itemstack
    }

    /**
     * Sets the given item stack to the specified slot in the crafting inventory
     * Updates recipe matching in the containing machine.
     */
    override fun setInventorySlotContents(index: Int, stack: ItemStack) {
        this.stacks.set(index, stack)

        if (stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit())
        }

        this.markDirty()
        if (this.craftingEventHandler != null) {
            this.craftingEventHandler!!.onCraftMatrixChanged(this)
        }
    }

    /**
     * Sets the given item stack to the specified slot in the crafting inventory.
     * No update to the containing machine.
     */
    override fun isEmpty(): Boolean {
        for (itemstack: ItemStack in this.stacks) {
            if (!itemstack.isEmpty()) {
                return false
            }
        }

        return true
    }

    override fun getInventoryStackLimit(): Int {
        return 64
    }

    override fun markDirty() {
    }

    override fun isUsableByPlayer(par1EntityPlayer: EntityPlayer): Boolean {
        return true
    }

    override fun isItemValidForSlot(i: Int, itemstack: ItemStack): Boolean {
        return true
    }

    override fun hasCustomName(): Boolean {
        // TODO Auto-generated method stub
        return false
    }

    override fun getDisplayName(): ITextComponent? {
        return null
    }

    override fun openInventory(player: EntityPlayer) {
    }

    override fun closeInventory(player: EntityPlayer) {
    }

    override fun getField(id: Int): Int {
        return 0
    }

    override fun setField(id: Int, value: Int) {
    }

    override fun getFieldCount(): Int {
        return 0
    }

    override fun clear() {
        this.stacks.clear()
    }
}
