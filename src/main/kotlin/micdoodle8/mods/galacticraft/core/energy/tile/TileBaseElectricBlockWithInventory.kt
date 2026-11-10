package micdoodle8.mods.galacticraft.core.energy.tile

import micdoodle8.mods.galacticraft.core.inventory.IInventoryDefaults
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.NonNullList

abstract class TileBaseElectricBlockWithInventory : TileBaseElectricBlock(), IInventoryDefaults {
    override fun getDisplayName(): net.minecraft.util.text.ITextComponent? = super<TileBaseElectricBlock>.getDisplayName()

    fun readStandardItemsFromNBT(nbt: NBTTagCompound): NonNullList<ItemStack> {
        val stacks: NonNullList<ItemStack> = NonNullList.withSize<ItemStack>(this.getSizeInventory(), ItemStack.EMPTY)
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

    override fun getSizeInventory(): Int {
        return this.getContainingItems()!!.size
    }

    override fun getStackInSlot(index: Int): ItemStack {
        return this.getContainingItems()!!.get(index)
    }

    override fun decrStackSize(index: Int, count: Int): ItemStack {
        val itemstack: ItemStack = ItemStackHelper.getAndSplit(this.getContainingItems(), index, count)

        if (!itemstack.isEmpty()) {
            this.markDirty()
        }

        return itemstack
    }

    override fun removeStackFromSlot(index: Int): ItemStack {
        val oldstack: ItemStack = ItemStackHelper.getAndRemove(this.getContainingItems(), index)
        if (!oldstack.isEmpty()) this.markDirty()
        return oldstack
    }

    override fun setInventorySlotContents(index: Int, stack: ItemStack) {
        this.getContainingItems()!!.set(index, stack)

        if (stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit())
        }

        this.markDirty()
    }

    override fun getInventoryStackLimit(): Int {
        return 64
    }

    override fun isUsableByPlayer(par1EntityPlayer: EntityPlayer): Boolean {
        return this.getWorld().getTileEntity(this.getPos()) === this && par1EntityPlayer.getDistanceSq(
            this.getPos().getX() + 0.5, this.getPos().getY() + 0.5, this.getPos().getZ() + 0.5
        ) <= 64.0
    }

    override fun getElectricInputDirection(): EnumFacing? {
        return EnumFacing.byHorizontalIndex(((this.getBlockMetadata() and 3) + 1) % 4)
    }

    override fun getBatteryInSlot(): ItemStack? {
        return this.getStackInSlot(0)
    }

    /*
     * Must be overridden by identical code, to get the actual containingItems
     */
    protected abstract fun getContainingItems(): NonNullList<ItemStack>?
}
