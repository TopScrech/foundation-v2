package micdoodle8.mods.galacticraft.core.tile

import java.util.EnumSet
import micdoodle8.mods.galacticraft.api.item.IItemOxygenSupply
import micdoodle8.mods.galacticraft.core.blocks.BlockOxygenCompressor
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase
import micdoodle8.mods.galacticraft.core.inventory.IInventoryDefaults
import micdoodle8.mods.galacticraft.core.items.ItemOxygenTank
import micdoodle8.mods.galacticraft.core.util.FluidUtil
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.ISidedInventory
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.NonNullList
import kotlin.math.floor
import kotlin.math.min

open class TileEntityOxygenCompressor constructor() : TileEntityOxygen(1200, 16), IInventoryDefaults,
    ISidedInventory {
    override fun getDisplayName(): net.minecraft.util.text.ITextComponent? = super<TileEntityOxygen>.getDisplayName()

    private var stacks: NonNullList<ItemStack> = NonNullList.withSize<ItemStack>(3, ItemStack.EMPTY)

    private var usingEnergy = false

    init {
        this.storage.setMaxExtract(15f)
    }

    override fun update() {
        if (!this.world.isRemote) {
            val oxygenItemStack: ItemStack = this.getStackInSlot(2)
            if (oxygenItemStack != null && oxygenItemStack.getItem() is IItemOxygenSupply) {
                val oxygenItem: IItemOxygenSupply = oxygenItemStack.getItem() as IItemOxygenSupply
                val oxygenDraw: Int = floor(
                    min(
                        this.oxygenPerTick * 2.5f,
                        (this.getMaxOxygenStored() - this.getOxygenStored()).toFloat()
                    ).toDouble()
                ).toInt()
                this.setOxygenStored(getOxygenStored() + oxygenItem.discharge(oxygenItemStack, oxygenDraw))
                if (this.getOxygenStored() > this.getMaxOxygenStored()) {
                    this.setOxygenStored(this.getOxygenStored())
                }
            }
        }

        super.update()

        if (!this.world.isRemote) {
            this.usingEnergy = false
            if (this.getOxygenStored() > 0 && this.hasEnoughEnergyToRun) {
                val tank0: ItemStack = this.stacks.get(0)

                if (!tank0.isEmpty()) {
                    if (tank0.getItem() is ItemOxygenTank && tank0.getItemDamage() > 0) {
                        tank0.setItemDamage(tank0.getItemDamage() - TANK_TRANSFER_SPEED)
                        this.setOxygenStored(this.getOxygenStored() - TANK_TRANSFER_SPEED)
                        this.usingEnergy = true
                    }
                }
            }
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
        return GCCoreUtil.translate("container.oxygencompressor.name")
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
        return intArrayOf(0, 1, 2)
    }

    override fun canInsertItem(slotID: Int, itemstack: ItemStack, side: EnumFacing): Boolean {
        if (this.isItemValidForSlot(slotID, itemstack)) {
            when (slotID) {
                0 -> return itemstack.getItemDamage() > 1
                1 -> return ItemElectricBase.isElectricItemCharged(itemstack)
                2 -> return itemstack.getItemDamage() < itemstack.getItem().getMaxDamage()
                else -> return false
            }
        }
        return false
    }

    override fun canExtractItem(slotID: Int, itemstack: ItemStack, side: EnumFacing): Boolean {
        when (slotID) {
            0 -> return itemstack.getItem() is ItemOxygenTank && itemstack.getItemDamage() == 0
            1 -> return ItemElectricBase.isElectricItemEmpty(itemstack)
            2 -> return FluidUtil.isEmptyContainer(itemstack)
            else -> return false
        }
    }

    override fun isItemValidForSlot(slotID: Int, itemstack: ItemStack): Boolean {
        when (slotID) {
            0 -> return itemstack.getItem() is ItemOxygenTank
            1 -> return ItemElectricBase.isElectricItem(itemstack.getItem())
            2 -> return itemstack.getItem() is IItemOxygenSupply
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

    override fun shouldUseOxygen(): Boolean {
        return false
    }

    override fun getOxygenInputDirections(): EnumSet<EnumFacing?> {
        return EnumSet.of<EnumFacing?>(this.getElectricInputDirection().getOpposite())
    }

    override fun getOxygenOutputDirections(): EnumSet<EnumFacing?> {
        return EnumSet.noneOf<EnumFacing?>(EnumFacing::class.java)
    }

    companion object {
        const val TANK_TRANSFER_SPEED: Int = 2
    }
}
