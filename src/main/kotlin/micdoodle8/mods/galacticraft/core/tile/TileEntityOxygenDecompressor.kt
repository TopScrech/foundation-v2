package micdoodle8.mods.galacticraft.core.tile

import java.util.EnumSet
import micdoodle8.mods.galacticraft.core.blocks.BlockOxygenCompressor
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase
import micdoodle8.mods.galacticraft.core.inventory.IInventoryDefaults
import micdoodle8.mods.galacticraft.core.items.ItemCanisterOxygenInfinite
import micdoodle8.mods.galacticraft.core.items.ItemOxygenTank
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.ISidedInventory
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.NonNullList
import kotlin.math.min

open class TileEntityOxygenDecompressor constructor() : TileEntityOxygen(1200, 0), IInventoryDefaults,
    ISidedInventory {
    override fun getDisplayName(): net.minecraft.util.text.ITextComponent? = super<TileEntityOxygen>.getDisplayName()

    private var stacks: NonNullList<ItemStack> = NonNullList.withSize<ItemStack>(2, ItemStack.EMPTY)

    private var usingEnergy = false

    override fun update() {
        super.update()

        if (!this.world.isRemote) {
            this.usingEnergy = false
            val tank1: ItemStack = this.stacks.get(0)

            if (!tank1.isEmpty() && this.hasEnoughEnergyToRun && this.getOxygenStored() < this.getMaxOxygenStored()) {
                if (tank1.getItem() is ItemOxygenTank && tank1.getItemDamage() < tank1.getMaxDamage()) {
                    tank1.setItemDamage(tank1.getItemDamage() + 1)
                    this.receiveOxygen(1, true)
                    this.usingEnergy = true
                } else if (tank1.getItem() is ItemCanisterOxygenInfinite) {
                    this.receiveOxygen(1, true)
                    this.usingEnergy = true
                }
            }

            this.produceOxygen()
        }
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)

        this.stacks = NonNullList.withSize<ItemStack>(this.getSizeInventory(), ItemStack.EMPTY)
        ItemStackHelper.loadAllItems(nbt, this.stacks)
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)
        ItemStackHelper.saveAllItems(nbt, this.stacks)
        return nbt
    }

    override fun getSizeInventory(): Int {
        return this.stacks.size
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
        val oldstack: ItemStack = ItemStackHelper.getAndRemove(this.stacks, index)
        if (!oldstack.isEmpty()) {
            this.markDirty()
        }
        return oldstack
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

    override fun getName(): String {
        return GCCoreUtil.translate("container.oxygendecompressor.name")
    }

    override fun getInventoryStackLimit(): Int {
        return 1
    }

    override fun isUsableByPlayer(par1EntityPlayer: EntityPlayer): Boolean {
        return this.world.getTileEntity(this.getPos()) === this && par1EntityPlayer.getDistanceSq(
            this.getPos().getX() + 0.5, this.getPos().getY() + 0.5, this.getPos().getZ() + 0.5
        ) <= 64.0
    }

    // ISidedInventory Implementation:
    override fun getSlotsForFace(side: EnumFacing): IntArray {
        return intArrayOf(0, 1)
    }

    override fun canInsertItem(slotID: Int, itemstack: ItemStack, side: EnumFacing): Boolean {
        if (this.isItemValidForSlot(slotID, itemstack)) {
            when (slotID) {
                0 -> return itemstack.getItemDamage() < itemstack.getMaxDamage()
                1 -> return ItemElectricBase.isElectricItemCharged(itemstack)
                else -> return false
            }
        }
        return false
    }

    override fun canExtractItem(slotID: Int, itemstack: ItemStack, side: EnumFacing): Boolean {
        if (this.isItemValidForSlot(slotID, itemstack)) {
            when (slotID) {
                0 -> return itemstack.getItemDamage() == itemstack.getMaxDamage()
                1 -> return ItemElectricBase.isElectricItemEmpty(itemstack)
                else -> return false
            }
        }
        return false
    }

    override fun isItemValidForSlot(slotID: Int, itemstack: ItemStack): Boolean {
        when (slotID) {
            0 -> return itemstack.getItem() is ItemOxygenTank
            1 -> return ItemElectricBase.isElectricItem(itemstack.getItem())
        }

        return false
    }

    override fun hasCustomName(): Boolean {
        return true
    }

    override fun shouldUseEnergy(): Boolean {
        return this.usingEnergy
    }

    override fun getFront(): EnumFacing {
        val state: IBlockState = this.world.getBlockState(getPos())
        if (state.getBlock() is BlockOxygenCompressor) {
            return state.getValue(BlockOxygenCompressor.FACING).rotateY()
        }
        return EnumFacing.NORTH
    }

    override fun getElectricInputDirection(): EnumFacing {
        return getFront()
    }

    override fun getBatteryInSlot(): ItemStack {
        return this.getStackInSlot(1)
    }

    open fun getOxygenOutputDirection(): EnumFacing {
        return this.getElectricInputDirection().getOpposite()
    }

    override fun getOxygenInputDirections(): EnumSet<EnumFacing?> {
        return EnumSet.noneOf<EnumFacing?>(EnumFacing::class.java)
    }

    override fun getOxygenOutputDirections(): EnumSet<EnumFacing?> {
        return EnumSet.of<EnumFacing?>(this.getElectricInputDirection().getOpposite())
    }

    override fun shouldPullOxygen(): Boolean {
        return false
    }

    override fun shouldUseOxygen(): Boolean {
        return false
    }

    override fun getOxygenProvide(direction: EnumFacing?): Int {
        return if (this.getOxygenOutputDirections().contains(direction)) min(
            OUTPUT_PER_TICK,
            this.getOxygenStored()
        ) else 0
    }

    companion object {
        const val OUTPUT_PER_TICK: Int = 100
    }
}
