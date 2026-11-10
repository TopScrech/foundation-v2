package micdoodle8.mods.galacticraft.core.inventory

import javax.annotation.Nonnull

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraft.util.text.ITextComponent

open class PersistantInventoryCrafting constructor() : InventoryCrafting(null, 3, 3) {
    @JvmField
    var stacks: NonNullList<ItemStack>

    private val inventoryWidth: Int
    private val inventoryHeight: Int

    @JvmField
    var craftingEventHandler: Container? = null

    init {
        val k: Int = 9
        this.stacks = NonNullList.withSize<ItemStack>(k, ItemStack.EMPTY)
        this.inventoryWidth = 3
        this.inventoryHeight = 3
    }

    override fun getSizeInventory(): Int {
        return this.stacks.size
    }

    @Nonnull
    override fun getStackInSlot(index: Int): ItemStack {
        return this.stacks.get(index)
    }

    override fun decrStackSize(index: Int, count: Int): ItemStack {
        val itemstack: ItemStack = ItemStackHelper.getAndSplit(this.stacks, index, count)

        if (!itemstack.isEmpty()) {
            this.markDirty()
            if (this.craftingEventHandler != null) {
                this.craftingEventHandler!!.onCraftMatrixChanged(this)
            }
        }

        return itemstack
    }

    override fun getStackInRowAndColumn(row: Int, column: Int): ItemStack {
        return if (row >= 0 && row < this.inventoryWidth && column >= 0 && column < this.inventoryHeight) this.getStackInSlot(
            row + column * this.inventoryWidth
        ) else ItemStack.EMPTY
    }

    override fun removeStackFromSlot(index: Int): ItemStack {
        return ItemStackHelper.getAndRemove(this.stacks, index)
    }

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

    open fun setInventorySlotContentsNoUpdate(index: Int, stack: ItemStack) {
        this.stacks.set(index, stack)

        if (stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit())
        }

        this.markDirty()
    }

    override fun isEmpty(): Boolean {
        for (itemstack: ItemStack in this.stacks) {
            if (!itemstack.isEmpty()) {
                return false
            }
        }

        return true
    }

    override fun getName(): String {
        return "container.crafting"
    }

    override fun hasCustomName(): Boolean {
        return false
    }

    override fun getInventoryStackLimit(): Int {
        return 64
    }

    override fun markDirty() {
    }

    override fun isUsableByPlayer(par1EntityPlayer: EntityPlayer): Boolean {
        return true
    }

    override fun openInventory(player: EntityPlayer) {
    }

    override fun closeInventory(player: EntityPlayer) {
    }

    override fun isItemValidForSlot(par1: Int, par2ItemStack: ItemStack): Boolean {
        return true
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
    }

    override fun getDisplayName(): ITextComponent? {
        return null
    }
}
