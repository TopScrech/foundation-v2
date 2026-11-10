package micdoodle8.mods.galacticraft.core.tile

import micdoodle8.mods.miccore.Annotations.NetworkedField

import micdoodle8.mods.galacticraft.api.transmission.NetworkType
import micdoodle8.mods.galacticraft.core.GCFluids
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.blocks.BlockRefinery
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseElectricBlockWithInventory
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.FluidUtil
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.wrappers.FluidHandlerWrapper
import micdoodle8.mods.galacticraft.core.wrappers.IFluidHandlerWrapper
import micdoodle8.mods.miccore.Annotations
import net.minecraft.block.state.IBlockState
import net.minecraft.inventory.ISidedInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.NonNullList
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidTank
import net.minecraftforge.fluids.FluidTankInfo
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fml.relauncher.Side
import kotlin.math.min

open class TileEntityRefinery constructor() : TileBaseElectricBlockWithInventory(), ISidedInventory,
    IFluidHandlerWrapper {
    private val tankCapacity = 24000

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var oilTank: FluidTank = FluidTank(this.tankCapacity)

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var fuelTank: FluidTank = FluidTank(this.tankCapacity)

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var processTicks: Int = 0
    private var stacks: NonNullList<ItemStack> = NonNullList.withSize<ItemStack>(3, ItemStack.EMPTY)

    init {
        this.storage.setMaxExtract((if (ConfigManagerCore.hardMode) 90 else 60).toFloat())
        this.oilTank.setFluid(FluidStack(GCFluids.fluidOil, 0))
        this.fuelTank.setFluid(FluidStack(GCFluids.fluidFuel, 0))
    }

    override fun update() {
        super.update()

        if (!this.world.isRemote) {
            val liquid: FluidStack? = FluidUtil.getFluidContained(this.stacks.get(1))
            if (FluidUtil.isOil(liquid)) {
                FluidUtil.loadFromContainer(this.oilTank, (GCFluids.fluidOil)!!, this.stacks, 1, liquid!!.amount)
            }

            checkFluidTankTransfer(2, this.fuelTank)

            if (this.canProcess() && this.hasEnoughEnergyToRun) {
                if (this.processTicks == 0) {
                    this.processTicks = this.getProcessTimeRequired()
                } else {
                    if (--this.processTicks <= 0) {
                        this.smeltItem()
                        this.processTicks = if (this.canProcess()) this.getProcessTimeRequired() else 0
                    }
                }
            } else {
                this.processTicks = 0
            }
        }
    }

    private fun getProcessTimeRequired(): Int {
        return if (this.poweredByTierGC > 1) 1 else PROCESS_TIME_REQUIRED
    }

    private fun checkFluidTankTransfer(slot: Int, tank: FluidTank) {
        FluidUtil.tryFillContainerFuel(tank, this.stacks, slot)
    }

    open fun getScaledOilLevel(i: Int): Int {
        return this.oilTank.getFluidAmount() * i / this.oilTank.getCapacity()
    }

    open fun getScaledFuelLevel(i: Int): Int {
        return this.fuelTank.getFluidAmount() * i / this.fuelTank.getCapacity()
    }

    open fun canProcess(): Boolean {
        if (this.oilTank.getFluidAmount() <= 0) {
            return false
        }

        if (this.fuelTank.getFluidAmount() >= this.fuelTank.getCapacity()) {
            return false
        }

        return !this.getDisabled(0)
    }

    open fun smeltItem() {
        if (this.canProcess()) {
            val oilAmount: Int = this.oilTank.getFluidAmount()
            val fuelSpace: Int = this.fuelTank.getCapacity() - this.fuelTank.getFluidAmount()

            val amountToDrain: Int = min(min(oilAmount, fuelSpace), OUTPUT_PER_SECOND)

            this.oilTank.drain(amountToDrain, true)
            this.fuelTank.fill(
                FluidRegistry.getFluidStack(
                    if (ConfigManagerCore.useOldFuelFluidID) "fuelgc" else "fuel",
                    amountToDrain
                ), true
            )
        }
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        this.processTicks = nbt.getInteger("smeltingTicks")
        this.stacks = this.readStandardItemsFromNBT(nbt)

        if (nbt.hasKey("oilTank")) {
            this.oilTank.readFromNBT(nbt.getCompoundTag("oilTank"))
        }
        if (this.oilTank.getFluid() != null && this.oilTank.getFluid()!!.getFluid() !== GCFluids.fluidOil) {
            this.oilTank.setFluid(FluidStack(GCFluids.fluidOil, this.oilTank.getFluidAmount()))
        }


        if (nbt.hasKey("fuelTank")) {
            this.fuelTank.readFromNBT(nbt.getCompoundTag("fuelTank"))
        }
        if (this.fuelTank.getFluid() != null && this.fuelTank.getFluid()!!.getFluid() !== GCFluids.fluidFuel) {
            this.fuelTank.setFluid(FluidStack(GCFluids.fluidFuel, this.fuelTank.getFluidAmount()))
        }
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)
        nbt.setInteger("smeltingTicks", this.processTicks)
        this.writeStandardItemsToNBT(nbt, this.stacks)

        if (this.oilTank.getFluid() != null) {
            nbt.setTag("oilTank", this.oilTank.writeToNBT(NBTTagCompound()))
        }

        if (this.fuelTank.getFluid() != null) {
            nbt.setTag("fuelTank", this.fuelTank.writeToNBT(NBTTagCompound()))
        }
        return nbt
    }


    override fun getContainingItems(): NonNullList<ItemStack>? {
        return this.stacks
    }

    override fun getName(): String {
        return GCCoreUtil.translate("container.refinery.name")
    }

    override fun hasCustomName(): Boolean {
        return true
    }

    // ISidedInventory Implementation:
    override fun getSlotsForFace(side: EnumFacing): IntArray {
        return intArrayOf(0, 1, 2)
    }

    override fun canInsertItem(slotID: Int, itemstack: ItemStack, side: EnumFacing): Boolean {
        if (!itemstack.isEmpty() && this.isItemValidForSlot(slotID, itemstack)) {
            when (slotID) {
                0 -> return ItemElectricBase.isElectricItemCharged(itemstack)
                1 -> return FluidUtil.isOilContainerAny(itemstack)
                2 -> return FluidUtil.isPartialContainer(itemstack, GCItems.fuelCanister)
                else -> return false
            }
        }
        return false
    }

    override fun canExtractItem(slotID: Int, itemstack: ItemStack, side: EnumFacing): Boolean {
        if (!itemstack.isEmpty() && this.isItemValidForSlot(slotID, itemstack)) {
            when (slotID) {
                0 -> return ItemElectricBase.isElectricItemEmpty(itemstack) || !this.shouldPullEnergy()
                1 -> return FluidUtil.isEmptyContainer(itemstack)
                2 -> return FluidUtil.isFullContainer(itemstack)
                else -> return false
            }
        }
        return false
    }

    override fun isItemValidForSlot(slotID: Int, itemstack: ItemStack): Boolean {
        when (slotID) {
            0 -> return !itemstack.isEmpty() && ItemElectricBase.isElectricItem(itemstack.getItem())
            1, 2 -> return FluidUtil.isValidContainer(itemstack)
        }

        return false
    }

    override fun shouldUseEnergy(): Boolean {
        return this.canProcess()
    }

    override fun getElectricInputDirection(): EnumFacing {
        return EnumFacing.UP
    }

    override fun getFront(): EnumFacing {
        val state: IBlockState = this.world.getBlockState(getPos())
        if (state.getBlock() is BlockRefinery) {
            return state.getValue(BlockRefinery.FACING)
        }
        return EnumFacing.NORTH
    }

    private fun getOilPipe(): EnumFacing {
        return getFront().rotateY()
    }

    private fun getFuelPipe(): EnumFacing {
        return getFront().rotateYCCW()
    }

    override fun canDrain(from: EnumFacing?, fluid: Fluid?): Boolean {
        if (from == getFuelPipe()) {
            return this.fuelTank.getFluid() != null && this.fuelTank.getFluidAmount() > 0
        }

        return false
    }

    override fun drain(from: EnumFacing?, resource: FluidStack?, doDrain: Boolean): FluidStack? {
        if (from == getFuelPipe() && resource != null) {
            return this.fuelTank.drain(resource.amount, doDrain)
        }

        return null
    }

    override fun drain(from: EnumFacing?, maxDrain: Int, doDrain: Boolean): FluidStack? {
        if (from == getFuelPipe()) {
            return this.drain(from, FluidStack(GCFluids.fluidFuel, maxDrain), doDrain)
        }

        return null
    }

    override fun canFill(from: EnumFacing?, fluid: Fluid?): Boolean {
        if (from == getOilPipe()) {
            return this.oilTank.getFluid() == null || this.oilTank.getFluidAmount() < this.oilTank.getCapacity()
        }

        return false
    }

    override fun fill(from: EnumFacing?, resource: FluidStack?, doFill: Boolean): Int {
        var used: Int = 0

        if (from == getOilPipe() && resource != null) {
            val liquidName: String? = FluidRegistry.getFluidName(resource)

            if (liquidName != null && FluidUtil.testOil(liquidName)) {
                if (liquidName == (GCFluids.fluidOil)!!.getName()) {
                    used = this.oilTank.fill(resource, doFill)
                } else {
                    used = this.oilTank.fill(FluidStack(GCFluids.fluidOil, resource.amount), doFill)
                }
            }
        }

        return used
    }

    override fun getTankInfo(from: EnumFacing?): Array<FluidTankInfo?> {
        var tankInfo: Array<FluidTankInfo?> = arrayOf<FluidTankInfo?>()

        if (from == getOilPipe()) {
            tankInfo = ((arrayOf<FluidTankInfo>(FluidTankInfo(this.oilTank)) as Array<FluidTankInfo?>?))!!
        } else if (from == getFuelPipe()) {
            tankInfo = ((arrayOf<FluidTankInfo>(FluidTankInfo(this.fuelTank)) as Array<FluidTankInfo?>?))!!
        }

        return tankInfo
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capability === CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing)
    }

    override fun <T> getCapability(capability: Capability<T?>, facing: EnumFacing?): T? {
        if (capability === CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return FluidHandlerWrapper(this, facing) as T
        }
        return super.getCapability<T?>(capability, facing)
    }

    override fun canConnect(direction: EnumFacing?, type: NetworkType?): Boolean {
        if (direction == null) {
            return false
        }
        if (type == NetworkType.POWER) {
            return direction == this.getElectricInputDirection()
        }
        if (type == NetworkType.FLUID) {
            val pipeSide: EnumFacing = getFuelPipe()
            return direction == pipeSide || direction == pipeSide.getOpposite()
        }
        return false
    }

    companion object {
        const val PROCESS_TIME_REQUIRED: Int = 2

        const val OUTPUT_PER_SECOND: Int = 1
    }
}
