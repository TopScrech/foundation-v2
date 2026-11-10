package micdoodle8.mods.galacticraft.core.inventory

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList

open class InventoryRocketBench constructor(par1Container: Container) : IInventoryDefaults {
    @JvmField
    var stacks: NonNullList<ItemStack>
    private val inventoryWidth: Int
    private val eventHandler: Container

    init {
        val size: Int = 18
        this.stacks = NonNullList.withSize<ItemStack>(size, ItemStack.EMPTY)
        this.eventHandler = par1Container
        this.inventoryWidth = 5
    }

    open fun getStackInRowAndColumn(par1: Int, par2: Int): ItemStack {
        if (par1 >= 0 && par1 < this.inventoryWidth) {
            val var3: Int = par1 + par2 * this.inventoryWidth
            if (var3 >= 18) {
                return ItemStack.EMPTY
            }
            return this.getStackInSlot(var3)
        } else {
            return ItemStack.EMPTY
        }
    }

    override fun getSizeInventory(): Int {
        return this.stacks.size
    }

    override fun getStackInSlot(index: Int): ItemStack {
        return this.stacks.get(index)
    }

    override fun decrStackSize(index: Int, count: Int): ItemStack {
        val itemstack: ItemStack = ItemStackHelper.getAndSplit(this.stacks, index, count)

        if (!itemstack.isEmpty()) {
            this.markDirty()
            this.eventHandler.onCraftMatrixChanged(this)
        }

        return itemstack
    }

    override fun removeStackFromSlot(index: Int): ItemStack {
        val oldstack: ItemStack = ItemStackHelper.getAndRemove(this.stacks, index)
        if (!oldstack.isEmpty()) {
            this.markDirty()
            this.eventHandler.onCraftMatrixChanged(this)
        }
        return oldstack
    }

    override fun setInventorySlotContents(index: Int, stack: ItemStack) {
        if (stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit())
        }
        this.stacks.set(index, stack)
        this.markDirty()
        this.eventHandler.onCraftMatrixChanged(this)
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

    override fun getInventoryStackLimit(): Int {
        return 64
    }

    override fun markDirty() {
    }

    override fun isUsableByPlayer(par1EntityPlayer: EntityPlayer): Boolean {
        return true
    }

    override fun isItemValidForSlot(i: Int, itemstack: ItemStack): Boolean {
        return false
    }
}
