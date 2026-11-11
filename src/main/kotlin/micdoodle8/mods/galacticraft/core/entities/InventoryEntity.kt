package micdoodle8.mods.galacticraft.core.entities

import net.minecraft.world.ContainerHelper
import micdoodle8.mods.galacticraft.core.inventory.IInventoryDefaults
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.core.NonNullList
import net.minecraft.world.level.Level as World

abstract class InventoryEntity(par1World: World?) : NetworkedEntity((par1World)!!), IInventoryDefaults {
    override fun hasCustomName(): Boolean = super<NetworkedEntity>.hasCustomName()

    override fun getDisplayName(): net.minecraft.network.chat.Component = super<NetworkedEntity>.getDisplayName()

    @JvmField
    protected var stacks: NonNullList<ItemStack> = NonNullList.withSize<ItemStack>(0, ItemStack.EMPTY)

    override fun readEntityFromNBT(nbt: NBTTagCompound) {
        ItemStackHelper.loadAllItems(nbt, this.stacks)
    }

    override fun writeEntityToNBT(nbt: NBTTagCompound) {
        ItemStackHelper.saveAllItems(nbt, this.stacks)
    }

    override fun getItem(var1: Int): ItemStack {
        return this.stacks.get(var1)
    }

    override fun removeItem(index: Int, count: Int): ItemStack {
        val itemstack: ItemStack = ContainerHelper.removeItem(this.stacks, index, count)

        if (!itemstack.isEmpty()) {
            this.setChanged()
        }

        return itemstack
    }

    override fun removeItemNoUpdate(index: Int): ItemStack {
        return ContainerHelper.takeItem(this.stacks, index)
    }

    override fun setItem(index: Int, stack: ItemStack) {
        this.stacks.set(index, stack)

        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize())
        }

        this.setChanged()
    }

    override fun isEmpty(): Boolean {
        for (itemstack: ItemStack in this.stacks) {
            if (!itemstack.isEmpty()) {
                return false
            }
        }

        return true
    }

    override fun getMaxStackSize(): Int {
        return 64
    }

    override fun setChanged() {
    }

    override fun stillValid(entityplayer: EntityPlayer): Boolean {
        return true
    }
}
