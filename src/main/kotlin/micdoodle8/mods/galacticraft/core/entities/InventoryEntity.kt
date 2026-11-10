package micdoodle8.mods.galacticraft.core.entities

import micdoodle8.mods.galacticraft.core.inventory.IInventoryDefaults
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.NonNullList
import net.minecraft.world.World

abstract class InventoryEntity(par1World: World?) : NetworkedEntity((par1World)!!), IInventoryDefaults {
    override fun hasCustomName(): Boolean = super<NetworkedEntity>.hasCustomName()

    override fun getDisplayName(): net.minecraft.util.text.ITextComponent? = super<NetworkedEntity>.getDisplayName()

    @JvmField
    protected var stacks: NonNullList<ItemStack> = NonNullList.withSize<ItemStack>(0, ItemStack.EMPTY)

    override fun readEntityFromNBT(nbt: NBTTagCompound) {
        ItemStackHelper.loadAllItems(nbt, this.stacks)
    }

    override fun writeEntityToNBT(nbt: NBTTagCompound) {
        ItemStackHelper.saveAllItems(nbt, this.stacks)
    }

    override fun getStackInSlot(var1: Int): ItemStack {
        return this.stacks.get(var1)
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

    override fun isUsableByPlayer(entityplayer: EntityPlayer): Boolean {
        return true
    }
}
