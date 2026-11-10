package micdoodle8.mods.galacticraft.planets.mars.tile

import micdoodle8.mods.galacticraft.core.energy.EnergyConfigHandler
import micdoodle8.mods.miccore.Annotations.NetworkedField

import micdoodle8.mods.galacticraft.api.tile.IDisableableMachine
import micdoodle8.mods.galacticraft.api.transmission.NetworkType
import micdoodle8.mods.galacticraft.api.transmission.tile.IOxygenReceiver
import micdoodle8.mods.galacticraft.api.transmission.tile.IOxygenStorage
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.core.GCFluids
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseElectricBlockWithInventory
import micdoodle8.mods.galacticraft.core.fluid.FluidNetwork
import micdoodle8.mods.galacticraft.core.fluid.NetworkHelper
import micdoodle8.mods.galacticraft.core.tile.TileEntityOxygenStorageModule
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.FluidUtil
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.wrappers.FluidHandlerWrapper
import micdoodle8.mods.galacticraft.core.wrappers.IFluidHandlerWrapper
import micdoodle8.mods.galacticraft.planets.asteroids.AsteroidsModule
import micdoodle8.mods.galacticraft.planets.asteroids.items.ItemAtmosphericValve
import micdoodle8.mods.galacticraft.planets.mars.blocks.BlockMachineMarsT2
import micdoodle8.mods.miccore.Annotations
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Items
import net.minecraft.inventory.ISidedInventory
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
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
import kotlin.math.max
import kotlin.math.min

open class TileEntityElectrolyzer constructor() : TileBaseElectricBlockWithInventory(), ISidedInventory,
    IDisableableMachine, IFluidHandlerWrapper, IOxygenStorage, IOxygenReceiver {
    private val tankCapacity = 4000

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var waterTank: FluidTank = FluidTank(this.tankCapacity)

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var liquidTank: FluidTank = FluidTank(this.tankCapacity)

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var liquidTank2: FluidTank = FluidTank(this.tankCapacity)

    @JvmField
    var processTimeRequired: Int = 3

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var processTicks: Int = 0
    private var stacks: NonNullList<ItemStack> = NonNullList.withSize<ItemStack>(4, ItemStack.EMPTY)

    init {
        this.storage.setMaxExtract((if (ConfigManagerCore.hardMode) 150 else 120).toFloat())
        this.setTierGC(2)
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        if (capability === CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) return true



        return super.hasCapability(capability, facing)
    }

    override fun <T> getCapability(capability: Capability<T?>, facing: EnumFacing?): T? {
        if (capability === CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return FluidHandlerWrapper(this, facing) as T
        }

        return super.getCapability<T?>(capability, facing)
    }

    override fun update() {
        super.update()

        if (!this.world.isRemote) {
            val liquid: FluidStack? = FluidUtil.getFluidContained(this.stacks.get(1))
            if (FluidUtil.isFluidStrict(liquid, FluidRegistry.WATER.getName())) {
                FluidUtil.loadFromContainer(waterTank, FluidRegistry.WATER, this.stacks, 1, liquid!!.amount)
            }

            //Only drain with atmospheric valve
            checkFluidTankTransfer(2, this.liquidTank)
            checkFluidTankTransfer(3, this.liquidTank2)

            if (this.hasEnoughEnergyToRun && this.canProcess()) {
                //50% extra speed boost for Tier 2 machine if powered by Tier 2 power
                if (this.tierGC == 2) {
                    this.processTimeRequired = max(1, 4 - this.poweredByTierGC)
                }

                if (this.processTicks == 0) {
                    this.processTicks = this.processTimeRequired
                } else {
                    if (--this.processTicks <= 0) {
                        this.doElectrolysis()
                        this.processTicks = if (this.canProcess()) this.processTimeRequired else 0
                    }
                }
            } else {
                this.processTicks = 0
            }

            this.produceOxygen(this.getOxygenOutputDirection())
            this.produceHydrogen(this.getHydrogenOutputDirection())
        }
    }

    private fun doElectrolysis() {
        //Can't be called if the gasTank fluid is null
        val waterAmount: Int = this.waterTank.getFluid()!!.amount
        if (waterAmount == 0) {
            return
        }

        this.placeIntoFluidTanks(2)
        this.waterTank.drain(1, true)
    }

    private fun placeIntoFluidTanks(amountToDrain: Int): Int {
        var amountToDrain: Int = amountToDrain
        val fuelSpace: Int = this.liquidTank.getCapacity() - this.liquidTank.getFluidAmount()
        val fuelSpace2: Int = this.liquidTank2.getCapacity() - this.liquidTank2.getFluidAmount()
        var amountToDrain2: Int = amountToDrain * 2

        if (amountToDrain > fuelSpace) {
            amountToDrain = fuelSpace
        }
        this.liquidTank.fill(FluidRegistry.getFluidStack("oxygen", amountToDrain), true)

        if (amountToDrain2 > fuelSpace2) {
            amountToDrain2 = fuelSpace2
        }
        this.liquidTank2.fill(FluidRegistry.getFluidStack("hydrogen", amountToDrain2), true)

        return amountToDrain
    }

    private fun checkFluidTankTransfer(slot: Int, tank: FluidTank) {
        if (!this.stacks.get(slot).isEmpty() && this.stacks.get(slot).getItem() is ItemAtmosphericValve) {
            tank.drain(4, true)
        }
    }

    open fun getScaledGasLevel(i: Int): Int {
        return if (this.waterTank.getFluid() != null) this.waterTank.getFluid()!!.amount * i / this.waterTank.getCapacity() else 0
    }

    open fun getScaledFuelLevel(i: Int): Int {
        return if (this.liquidTank.getFluid() != null) this.liquidTank.getFluid()!!.amount * i / this.liquidTank.getCapacity() else 0
    }

    open fun getScaledFuelLevel2(i: Int): Int {
        return if (this.liquidTank2.getFluid() != null) this.liquidTank2.getFluid()!!.amount * i / this.liquidTank2.getCapacity() else 0
    }

    open fun canProcess(): Boolean {
        if (this.waterTank.getFluid() == null || this.waterTank.getFluid()!!.amount <= 0 || this.getDisabled(0)) {
            return false
        }

        val tank1HasSpace: Boolean = this.liquidTank.getFluidAmount() < this.liquidTank.getCapacity()
        val tank2HasSpace: Boolean = this.liquidTank2.getFluidAmount() < this.liquidTank2.getCapacity()

        return tank1HasSpace || tank2HasSpace
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        this.processTicks = nbt.getInteger("processTicks")
        this.stacks = this.readStandardItemsFromNBT(nbt)

        if (nbt.hasKey("waterTank")) {
            this.waterTank.readFromNBT(nbt.getCompoundTag("waterTank"))
        }

        if (nbt.hasKey("gasTank")) {
            this.liquidTank.readFromNBT(nbt.getCompoundTag("gasTank"))
        }
        if (nbt.hasKey("gasTank2")) {
            this.liquidTank2.readFromNBT(nbt.getCompoundTag("gasTank2"))
        }
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)
        nbt.setInteger("processTicks", this.processTicks)
        this.writeStandardItemsToNBT(nbt, this.stacks)

        if (this.waterTank.getFluid() != null) {
            nbt.setTag("waterTank", this.waterTank.writeToNBT(NBTTagCompound()))
        }

        if (this.liquidTank.getFluid() != null) {
            nbt.setTag("gasTank", this.liquidTank.writeToNBT(NBTTagCompound()))
        }
        if (this.liquidTank2.getFluid() != null) {
            nbt.setTag("gasTank2", this.liquidTank2.writeToNBT(NBTTagCompound()))
        }

        return nbt
    }

    override fun getContainingItems(): NonNullList<ItemStack>? {
        return this.stacks
    }

    override fun hasCustomName(): Boolean {
        return true
    }

    override fun getName(): String {
        return GCCoreUtil.translate("tile.mars_machine.6.name")
    }

    // ISidedInventory Implementation:
    override fun getSlotsForFace(side: EnumFacing): IntArray {
        return intArrayOf(0, 1)
    }

    override fun canInsertItem(slotID: Int, itemstack: ItemStack, side: EnumFacing): Boolean {
        if (this.isItemValidForSlot(slotID, itemstack)) {
            when (slotID) {
                0 -> return ItemElectricBase.isElectricItemCharged(itemstack)
                1 -> return itemstack.getItem() === Items.WATER_BUCKET
                else -> return false
            }
        }
        return false
    }

    override fun canExtractItem(slotID: Int, itemstack: ItemStack, side: EnumFacing): Boolean {
        if (this.isItemValidForSlot(slotID, itemstack)) {
            when (slotID) {
                0 -> return ItemElectricBase.isElectricItemEmpty(itemstack)
                1 -> return itemstack.getItem() === Items.BUCKET
                else -> return false
            }
        }
        return false
    }

    override fun isItemValidForSlot(slotID: Int, itemstack: ItemStack): Boolean {
        val item: Item = itemstack.getItem()
        when (slotID) {
            0 -> return ItemElectricBase.isElectricItem(item)
            1 -> return item === Items.BUCKET || item === Items.WATER_BUCKET
        }

        return false
    }

    override fun shouldUseEnergy(): Boolean {
        return this.canProcess()
    }

    override fun getPacketRange(): Double {
        return 320.0
    }

    override fun getElectricInputDirection(): EnumFacing {
        return EnumFacing.DOWN
    }

    override fun canDrain(from: EnumFacing?, fluid: Fluid?): Boolean {
        if (from == this.getHydrogenOutputDirection()) {
            return this.liquidTank2.getFluid() != null && this.liquidTank2.getFluidAmount() > 0
        }
        if (from == this.getOxygenOutputDirection()) {
            return this.liquidTank.getFluid() != null && this.liquidTank.getFluidAmount() > 0
        }

        return false
    }

    override fun drain(from: EnumFacing?, resource: FluidStack?, doDrain: Boolean): FluidStack? {
        if (from == this.getHydrogenOutputDirection()) {
            if (resource != null && resource.isFluidEqual(this.liquidTank2.getFluid())) {
                return this.liquidTank2.drain(resource.amount, doDrain)
            }
        }

        if (from == this.getOxygenOutputDirection()) {
            if (resource != null && resource.isFluidEqual(this.liquidTank.getFluid())) {
                return this.liquidTank.drain(resource.amount, doDrain)
            }
        }

        return null
    }

    override fun drain(from: EnumFacing?, maxDrain: Int, doDrain: Boolean): FluidStack? {
        if (from == this.getHydrogenOutputDirection()) {
            return this.liquidTank2.drain(maxDrain, doDrain)
        }

        if (from == this.getOxygenOutputDirection()) {
            return this.liquidTank.drain(maxDrain, doDrain)
        }

        return null
    }

    override fun canFill(from: EnumFacing?, fluid: Fluid?): Boolean {
        if (from == this.getFront().rotateY()) {
            //Can fill with water
            return fluid == null || fluid.getName() == FluidRegistry.WATER.getName()
        }

        return false
    }

    override fun fill(from: EnumFacing?, resource: FluidStack?, doFill: Boolean): Int {
        var used: Int = 0

        if (resource != null && this.canFill(from, resource.getFluid())) {
            used = this.waterTank.fill(resource, doFill)
        }

        return used
    }

    override fun getTankInfo(from: EnumFacing?): Array<FluidTankInfo?> {
        var tankInfo: Array<FluidTankInfo?> = arrayOf<FluidTankInfo?>()

        if (from == this.getFront().rotateY()) {
            tankInfo = ((arrayOf<FluidTankInfo>(FluidTankInfo(this.waterTank)) as Array<FluidTankInfo?>?))!!
        } else if (from == this.getHydrogenOutputDirection()) {
            tankInfo = ((arrayOf<FluidTankInfo>(FluidTankInfo(this.liquidTank2)) as Array<FluidTankInfo?>?))!!
        } else if (from == this.getOxygenOutputDirection()) {
            tankInfo = ((arrayOf<FluidTankInfo>(FluidTankInfo(this.liquidTank)) as Array<FluidTankInfo?>?))!!
        }

        return tankInfo
    }

    override fun getBlockMetadata(): Int {
        return getBlockType().getMetaFromState(this.world.getBlockState(getPos()))
    }

    override fun setOxygenStored(amount: Int) {
        this.liquidTank.setFluid(FluidStack(AsteroidsModule.fluidOxygenGas, amount))
    }

    override fun getOxygenStored(): Int {
        return this.liquidTank.getFluidAmount()
    }

    open fun getHydrogenStored(): Int {
        return this.liquidTank2.getFluidAmount()
    }

    override fun getMaxOxygenStored(): Int {
        return this.liquidTank.getCapacity()
    }

    private fun getOxygenOutputDirection(): EnumFacing {
        return this.getFront().getOpposite()
    }

    private fun getHydrogenOutputDirection(): EnumFacing {
        return this.getFront().rotateYCCW()
    }

    private fun produceOxygen(outputDirection: EnumFacing): Boolean {
        val provide: Int = this.getOxygenProvide(outputDirection)

        if (provide > 0) {
            val outputTile: TileEntity? =
                BlockVec3(this).modifyPositionFromSide(outputDirection).getTileEntity(this.world)
            val outputNetwork: FluidNetwork? = NetworkHelper.getFluidNetworkFromTile(outputTile, outputDirection)

            if (outputNetwork != null) {
                val gasRequested: Int = outputNetwork.getRequest()

                if (gasRequested > 0) {
                    val usedGas: Int = outputNetwork.emitToBuffer(
                        FluidStack(GCFluids.fluidOxygenGas, min(gasRequested, provide)),
                        true
                    )

                    this.drawOxygen(usedGas, true)
                    return true
                }
            } else if (outputTile is IOxygenReceiver) {
                val requestedOxygen: Float =
                    (outputTile as IOxygenReceiver).getOxygenRequest(outputDirection.getOpposite()).toFloat()

                if (requestedOxygen > 0) {
                    val toSend: Int = min(this.getOxygenStored(), provide)
                    val acceptedOxygen: Int =
                        (outputTile as IOxygenReceiver).receiveOxygen(outputDirection.getOpposite(), toSend, true)
                    this.drawOxygen(acceptedOxygen, true)
                    return true
                }
            }

            //            {
//                //TODO Oxygen item handling - internal tank (IGasItem)
//                //int acceptedOxygen = GasTransmission.addGas(itemStack, type, amount);
//                //this.drawOxygen(acceptedOxygen, true);
//
//                if (outputTile instanceof IGasHandler && ((IGasHandler) outputTile).canReceiveGas(outputDirection.getOpposite(), (Gas) EnergyConfigHandler.gasOxygen))
//                {
//                    GasStack toSend = new GasStack((Gas) EnergyConfigHandler.gasOxygen, (int) Math.floor(Math.min(this.getOxygenStored(), provide)));
//                    int acceptedOxygen = 0;
//                    try {
//                    	acceptedOxygen = ((IGasHandler) outputTile).receiveGas(outputDirection.getOpposite(), toSend);
//                    } catch (Exception e) { }
//                    this.drawOxygen(acceptedOxygen, true);
//                    return true;
//                }
//            }
        }

        return false
    }

    private fun produceHydrogen(outputDirection: EnumFacing): Boolean {
        val provide: Int = this.getHydrogenProvide(outputDirection)

        if (provide > 0) {
            val outputTile: TileEntity? =
                BlockVec3(this).modifyPositionFromSide(outputDirection).getTileEntity(this.world)
            val outputNetwork: FluidNetwork? = NetworkHelper.getFluidNetworkFromTile(outputTile, outputDirection)

            if (outputNetwork != null) {
                val gasRequested: Int = outputNetwork.getRequest()

                if (gasRequested > 0) {
                    val usedGas: Int = outputNetwork.emitToBuffer(
                        FluidStack(
                            FluidRegistry.getFluid("hydrogen"),
                            min(gasRequested, provide)
                        ), true
                    )

                    this.drawHydrogen(usedGas, true)
                    return true
                }
            } else if (outputTile is TileEntityMethaneSynthesizer) {
                val requestedHydrogen: Float = outputTile.getHydrogenRequest(outputDirection.getOpposite()).toFloat()

                if (requestedHydrogen > 0) {
                    val toSend: Int = min(this.getHydrogenStored(), provide)
                    val acceptedHydrogen: Int =
                        outputTile.receiveHydrogen(outputDirection.getOpposite(), toSend.toFloat(), true)
                    this.drawHydrogen(acceptedHydrogen, true)
                    return true
                }
            }

            //            {
//                //TODO Gas item handling - internal tank (IGasItem)
//                //int acceptedHydrogen = GasTransmission.addGas(itemStack, type, amount);
//                //this.drawHydrogen(acceptedHydrogen, true);
//
//                if (outputTile instanceof IGasHandler && ((IGasHandler) outputTile).canReceiveGas(outputDirection.getOpposite(), (Gas) EnergyConfigHandler.gasHydrogen))
//                {
//                    GasStack toSend = new GasStack((Gas) EnergyConfigHandler.gasHydrogen, (int) Math.floor(Math.min(this.getHydrogenStored(), provide)));
//                    int acceptedHydrogen = 0;
//                    try {
//                    	acceptedHydrogen = ((IGasHandler) outputTile).receiveGas(outputDirection.getOpposite(), toSend);
//                    } catch (Exception e) { }
//                    this.drawHydrogen(acceptedHydrogen, true);
//                    return true;
//                }
//            }
        }

        return false
    }

    override fun provideOxygen(from: EnumFacing?, request: Int, doProvide: Boolean): Int {
        if (this.getOxygenOutputDirection() == from) {
            return this.drawOxygen(request, doProvide)
        }

        return 0
    }

    open fun drawOxygen(request: Int, doProvide: Boolean): Int {
        if (request > 0) {
            val requestedOxygen: Int = min(request, this.liquidTank.getFluidAmount())

            if (doProvide) {
                this.setOxygenStored(this.liquidTank.getFluidAmount() - requestedOxygen)
            }

            return requestedOxygen
        }

        return 0
    }

    open fun drawHydrogen(request: Int, doProvide: Boolean): Int {
        if (request > 0) {
            val currentHydrogen: Int = this.liquidTank2.getFluidAmount()
            val requestedHydrogen: Int = min(request, currentHydrogen)

            if (doProvide) {
                this.liquidTank2.setFluid(
                    FluidStack(
                        FluidRegistry.getFluid("hydrogen"),
                        currentHydrogen - requestedHydrogen
                    )
                )
            }

            return requestedHydrogen
        }

        return 0
    }

    override fun getOxygenProvide(direction: EnumFacing?): Int {
        return if (this.getOxygenOutputDirection() == direction) min(
            TileEntityOxygenStorageModule.OUTPUT_PER_TICK,
            this.getOxygenStored()
        ) else 0
    }

    open fun getHydrogenProvide(direction: EnumFacing?): Int {
        return if (this.getHydrogenOutputDirection() == direction) min(
            TileEntityOxygenStorageModule.OUTPUT_PER_TICK,
            this.getHydrogenStored()
        ) else 0
    }

    override fun shouldPullOxygen(): Boolean {
        return false
    }

    override fun receiveOxygen(from: EnumFacing?, receive: Int, doReceive: Boolean): Int {
        return 0
    }

    override fun getOxygenRequest(direction: EnumFacing?): Int {
        return 0
    }

    override fun canConnect(direction: EnumFacing?, type: NetworkType?): Boolean {
        if (direction == null) {
            return false
        }

        if (type == NetworkType.FLUID) {
            return this.getOxygenOutputDirection() == direction || this.getHydrogenOutputDirection() == direction || direction == this.getFront()
                .rotateY()
        }

        if (type == NetworkType.POWER) {
            return direction == this.getElectricInputDirection()
        }

        return false
    }

    override fun getFront(): EnumFacing {
        val state: IBlockState = this.world.getBlockState(getPos())
        if (state.getBlock() is BlockMachineMarsT2) {
            return state.getValue(BlockMachineMarsT2.FACING)
        }
        return EnumFacing.NORTH
    }
}
