package micdoodle8.mods.galacticraft.core.tile

import net.minecraft.world.ContainerHelper
import java.util.EnumSet
import micdoodle8.mods.galacticraft.core.blocks.BlockOxygenCompressor
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase
import micdoodle8.mods.galacticraft.core.inventory.IInventoryDefaults
import micdoodle8.mods.galacticraft.core.items.ItemCanisterOxygenInfinite
import micdoodle8.mods.galacticraft.core.items.ItemOxygenTank
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.WorldlyContainer as ISidedInventory
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.core.NonNullList
import kotlin.math.min

open class TileEntityOxygenDecompressor constructor() : TileEntityOxygen(1200, 0), IInventoryDefaults,
    ISidedInventory {
    override fun getDisplayName(): net.minecraft.network.chat.Component = super<TileEntityOxygen>.getDisplayName()

    private var stacks: NonNullList<ItemStack> = NonNullList.withSize<ItemStack>(2, ItemStack.EMPTY)

    private var usingEnergy = false

    override fun update() {
        super.update()

        if (!this.world.isRemote) {
            this.usingEnergy = false
            val tank1: ItemStack = this.stacks.get(0)

            if (!tank1.isEmpty() && this.hasEnoughEnergyToRun && this.getOxygenStored() < this.getMaxOxygenStored()) {
                if (tank1.getItem() is ItemOxygenTank && ItemOxygenTank.used(tank1) < ItemOxygenTank.capacityOf(tank1)) {
                    ItemOxygenTank.setUsed(tank1, ItemOxygenTank.used(tank1) + 1)
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

        this.stacks = NonNullList.withSize<ItemStack>(this.getContainerSize(), ItemStack.EMPTY)
        ItemStackHelper.loadAllItems(nbt, this.stacks)
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)
        ItemStackHelper.saveAllItems(nbt, this.stacks)
        return nbt
    }

    override fun getContainerSize(): Int {
        return this.stacks.size
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
        val oldstack: ItemStack = ContainerHelper.takeItem(this.stacks, index)
        if (!oldstack.isEmpty()) {
            this.setChanged()
        }
        return oldstack
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

    override fun getName(): net.minecraft.network.chat.Component {
        return net.minecraft.network.chat.Component.translatable("container.oxygendecompressor.name")
    }

    override fun getMaxStackSize(): Int {
        return 1
    }

    override fun stillValid(par1EntityPlayer: EntityPlayer): Boolean {
        return this.world.getBlockEntity(this.getPos()) === this && par1EntityPlayer.getDistanceSq(
            this.getPos().getX() + 0.5, this.getPos().getY() + 0.5, this.getPos().getZ() + 0.5
        ) <= 64.0
    }

    // ISidedInventory Implementation:
    override fun getSlotsForFace(side: EnumFacing): IntArray {
        return intArrayOf(0, 1)
    }

    override fun canPlaceItemThroughFace(slotID: Int, itemstack: ItemStack, side: EnumFacing?): Boolean {
        if (this.canPlaceItem(slotID, itemstack)) {
            when (slotID) {
                0 -> return ItemOxygenTank.used(itemstack) < ItemOxygenTank.capacityOf(itemstack)
                1 -> return ItemElectricBase.isElectricItemCharged(itemstack)
                else -> return false
            }
        }
        return false
    }

    override fun canTakeItemThroughFace(slotID: Int, itemstack: ItemStack, side: EnumFacing): Boolean {
        if (this.canPlaceItem(slotID, itemstack)) {
            when (slotID) {
                0 -> return ItemOxygenTank.used(itemstack) == ItemOxygenTank.capacityOf(itemstack)
                1 -> return ItemElectricBase.isElectricItemEmpty(itemstack)
                else -> return false
            }
        }
        return false
    }

    override fun canPlaceItem(slotID: Int, itemstack: ItemStack): Boolean {
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
        return this.getItem(1)
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
