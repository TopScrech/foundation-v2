package micdoodle8.mods.galacticraft.core.energy.tile

import net.minecraft.world.ContainerHelper
import micdoodle8.mods.galacticraft.core.inventory.IInventoryDefaults
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.core.NonNullList

abstract class TileBaseElectricBlockWithInventory : TileBaseElectricBlock(), IInventoryDefaults {
    override fun getDisplayName(): net.minecraft.network.chat.Component = getName()

    fun readStandardItemsFromNBT(nbt: NBTTagCompound): NonNullList<ItemStack> {
        val stacks: NonNullList<ItemStack> = NonNullList.withSize<ItemStack>(this.getContainerSize(), ItemStack.EMPTY)
        ItemStackHelper.loadAllItems(nbt, stacks)
        return stacks
    }

    fun writeStandardItemsToNBT(nbt: NBTTagCompound, stacks: NonNullList<ItemStack>) {
        ItemStackHelper.saveAllItems(nbt, stacks)
    }

    override fun isEmpty(): Boolean {
        for (itemstack: ItemStack in this.getContainingItems()!!) {
            if (!itemstack.isEmpty()) {
                return false
            }
        }

        return true
    }

    override fun getContainerSize(): Int {
        return this.getContainingItems()!!.size
    }

    override fun getItem(index: Int): ItemStack {
        return this.getContainingItems()!!.get(index)
    }

    override fun removeItem(index: Int, count: Int): ItemStack {
        val itemstack: ItemStack = ContainerHelper.removeItem(this.getContainingItems(), index, count)

        if (!itemstack.isEmpty()) {
            this.setChanged()
        }

        return itemstack
    }

    override fun removeItemNoUpdate(index: Int): ItemStack {
        val oldstack: ItemStack = ContainerHelper.takeItem(this.getContainingItems(), index)
        if (!oldstack.isEmpty()) this.setChanged()
        return oldstack
    }

    override fun setItem(index: Int, stack: ItemStack) {
        this.getContainingItems()!!.set(index, stack)

        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize())
        }

        this.setChanged()
    }

    override fun getMaxStackSize(): Int {
        return 64
    }

    override fun stillValid(par1EntityPlayer: EntityPlayer): Boolean {
        return this.getWorld().getTileEntity(this.getPos()) === this && par1EntityPlayer.getDistanceSq(
            this.getPos().getX() + 0.5, this.getPos().getY() + 0.5, this.getPos().getZ() + 0.5
        ) <= 64.0
    }

    override fun getElectricInputDirection(): EnumFacing? {
        return EnumFacing.byHorizontalIndex(((this.getBlockMetadata() and 3) + 1) % 4)
    }

    override fun getBatteryInSlot(): ItemStack? {
        return this.getItem(0)
    }

    /*
     * Must be overridden by identical code, to get the actual containingItems
     */
    protected abstract fun getContainingItems(): NonNullList<ItemStack>?
}
