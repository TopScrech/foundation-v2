package micdoodle8.mods.galacticraft.planets.mars.inventory

import micdoodle8.mods.galacticraft.core.inventory.IInventoryDefaults
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList

open class InventorySchematicTier2Rocket constructor(private val eventHandler: Container) : IInventoryDefaults {
    private val stacks: NonNullList<ItemStack>
    private val inventoryWidth = 5

    init {
        this.stacks = NonNullList.withSize<ItemStack>(22, ItemStack.EMPTY)
    }

    override fun getSizeInventory(): Int {
        return this.stacks.size
    }

    override fun getStackInSlot(par1: Int): ItemStack {
        return if (par1 >= this.getSizeInventory()) ItemStack.EMPTY else this.stacks.get(par1)
    }

    override fun getName(): String {
        return "container.crafting"
    }

    override fun removeStackFromSlot(index: Int): ItemStack {
        val oldstack: ItemStack = ItemStackHelper.getAndRemove(this.stacks, index)
        if (!oldstack.isEmpty()) {
            this.markDirty()
            this.eventHandler.onCraftMatrixChanged(this)
        }
        return oldstack
    }

    override fun decrStackSize(index: Int, count: Int): ItemStack {
        val itemstack: ItemStack = ItemStackHelper.getAndSplit(this.stacks, index, count)

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
