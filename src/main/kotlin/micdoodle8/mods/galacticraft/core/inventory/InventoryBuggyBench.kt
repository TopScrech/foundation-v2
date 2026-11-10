package micdoodle8.mods.galacticraft.core.inventory

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList

open class InventoryBuggyBench constructor(par1Container: Container) : IInventoryDefaults {
    private val stackList: NonNullList<ItemStack>
    private val inventoryWidth: Int
    private val eventHandler: Container

    init {
        val size: Int = 32
        this.stackList = NonNullList.withSize<ItemStack>(size, ItemStack.EMPTY)
        this.eventHandler = par1Container
        this.inventoryWidth = 5
    }

    override fun getSizeInventory(): Int {
        return this.stackList.size
    }

    override fun getStackInSlot(par1: Int): ItemStack {
        return if (par1 >= this.getSizeInventory()) ItemStack.EMPTY else this.stackList.get(par1)
    }

    open fun getStackInRowAndColumn(par1: Int, par2: Int): ItemStack {
        if (par1 >= 0 && par1 < this.inventoryWidth) {
            val var3: Int = par1 + par2 * this.inventoryWidth
            return this.getStackInSlot(var3)
        } else {
            return ItemStack.EMPTY
        }
    }

    override fun getName(): String {
        return "container.crafting"
    }

    override fun removeStackFromSlot(index: Int): ItemStack {
        val oldstack: ItemStack = ItemStackHelper.getAndRemove(this.stackList, index)
        if (!oldstack.isEmpty()) {
            this.markDirty()
            this.eventHandler.onCraftMatrixChanged(this)
        }
        return oldstack
    }

    override fun decrStackSize(index: Int, count: Int): ItemStack {
        val itemstack: ItemStack = ItemStackHelper.getAndSplit(this.stackList, index, count)

        if (!itemstack.isEmpty()) {
            this.markDirty()
            this.eventHandler.onCraftMatrixChanged(this)
        }

        return itemstack
    }

    override fun setInventorySlotContents(index: Int, stack: ItemStack) {
        if (stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit())
        }

        this.stackList.set(index, stack)
        this.markDirty()
        this.eventHandler.onCraftMatrixChanged(this)
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

    override fun hasCustomName(): Boolean {
        return false
    }

    override fun isItemValidForSlot(i: Int, itemstack: ItemStack): Boolean {
        return false
    }

    override fun isEmpty(): Boolean {
        for (itemstack: ItemStack in this.stackList) {
            if (!itemstack.isEmpty()) {
                return false
            }
        }

        return true
    }
}
