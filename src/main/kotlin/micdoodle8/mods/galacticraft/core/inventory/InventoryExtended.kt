package micdoodle8.mods.galacticraft.core.inventory

import javax.annotation.Nonnull

import micdoodle8.mods.galacticraft.api.inventory.IInventoryGC
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.util.NonNullList
import net.minecraft.util.text.ITextComponent

open class InventoryExtended : IInventoryGC {
    @JvmField
    var stacks: NonNullList<ItemStack> = NonNullList.withSize<ItemStack>(11, ItemStack.EMPTY)

    override fun isEmpty(): Boolean {
        for (itemstack: ItemStack in this.stacks) {
            if (!itemstack.isEmpty()) {
                return false
            }
        }

        return true
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
        }

        return itemstack
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
    }

    override fun getName(): String {
        return "Galacticraft Player Inventory"
    }

    override fun hasCustomName(): Boolean {
        return true
    }

    override fun getInventoryStackLimit(): Int {
        return 64
    }

    override fun markDirty() {
    }

    override fun isUsableByPlayer(entityplayer: EntityPlayer): Boolean {
        return true
    }

    override fun openInventory(player: EntityPlayer) {
    }

    override fun closeInventory(player: EntityPlayer) {
    }

    override fun isItemValidForSlot(i: Int, itemstack: ItemStack): Boolean {
        return false
    }

    override fun dropExtendedItems(player: EntityPlayer?) {
        for (i: Int in this.stacks.indices) {
            val stack: ItemStack = this.stacks.get(i)

            if (!stack.isEmpty()) {
                (player)!!.dropItem(stack, true)
            }

            this.stacks.set(i, ItemStack.EMPTY)
        }
    }

    // Backwards compatibility for old inventory
    open fun readFromNBTOld(par1NBTTagList: NBTTagList) {
        this.stacks = NonNullList.withSize<ItemStack>(11, ItemStack.EMPTY)

        for (i: Int in 0..<par1NBTTagList.tagCount()) {
            val nbttagcompound: NBTTagCompound = par1NBTTagList.getCompoundTagAt(i)
            val j: Int = nbttagcompound.getByte("Slot").toInt() and 255
            val itemstack: ItemStack = ItemStack(nbttagcompound)

            if (!itemstack.isEmpty()) {
                if (j >= 200 && j < this.stacks.size + 200 - 1) {
                    this.stacks.set(j - 200, itemstack)
                }
            }
        }
    }

    open fun readFromNBT(tagList: NBTTagList) {
        this.stacks = NonNullList.withSize<ItemStack>(11, ItemStack.EMPTY)

        for (i: Int in 0..<tagList.tagCount()) {
            val nbttagcompound: NBTTagCompound = tagList.getCompoundTagAt(i)
            val j: Int = nbttagcompound.getByte("Slot").toInt() and 255
            val itemstack: ItemStack = ItemStack(nbttagcompound)

            if (!itemstack.isEmpty()) {
                this.stacks.set(j, itemstack)
            }
        }
    }

    open fun writeToNBT(tagList: NBTTagList): NBTTagList? {
        var nbttagcompound: NBTTagCompound

        for (i: Int in this.stacks.indices) {
            if (!this.stacks.get(i).isEmpty()) {
                nbttagcompound = NBTTagCompound()
                nbttagcompound.setByte("Slot", i.toByte())
                this.stacks.get(i).writeToNBT(nbttagcompound)
                tagList.appendTag(nbttagcompound)
            }
        }

        return tagList
    }

    override fun copyInventory(par1InventoryPlayer: IInventoryGC?) {
        val toCopy: InventoryExtended = par1InventoryPlayer as InventoryExtended
        for (i: Int in this.stacks.indices) {
            this.stacks.set(i, toCopy.stacks.get(i).copy())
        }
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
