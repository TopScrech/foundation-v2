package micdoodle8.mods.galacticraft.core.tile

import foundation.persistence.containsTag

import micdoodle8.mods.galacticraft.core.energy.EnergyConfigHandler
import micdoodle8.mods.miccore.Annotations.NetworkedField

import java.util.EnumSet
import micdoodle8.mods.galacticraft.api.transmission.NetworkType
import micdoodle8.mods.galacticraft.api.transmission.tile.IOxygenReceiver
import micdoodle8.mods.galacticraft.api.transmission.tile.IOxygenStorage
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.core.GCFluids
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseElectricBlock
import micdoodle8.mods.galacticraft.core.fluid.FluidNetwork
import micdoodle8.mods.galacticraft.core.fluid.NetworkHelper
import micdoodle8.mods.galacticraft.core.wrappers.FluidHandlerWrapper
import micdoodle8.mods.galacticraft.core.wrappers.IFluidHandlerWrapper
import micdoodle8.mods.miccore.Annotations
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.core.Direction as EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidTankInfo
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fml.LogicalSide as Side
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

abstract class TileEntityOxygen(maxOxygen: Int, @JvmField var oxygenPerTick: Int) : TileBaseElectricBlock(),
    IOxygenReceiver, IOxygenStorage, IFluidHandlerWrapper {
    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var tank: FluidTankGC

    @JvmField
    var lastStoredOxygen: Float = 0f

    init {
        this.tank = FluidTankGC(maxOxygen, this)
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

    fun getScaledOxygenLevel(scale: Int): Int {
        return floor((this.getOxygenStored() * scale / (this.getMaxOxygenStored() - this.oxygenPerTick)).toDouble()).toInt()
    }

    abstract fun shouldUseOxygen(): Boolean

    open fun getCappedScaledOxygenLevel(scale: Int): Int {
        return max(
            min(
                floor(this.tank.getFluidAmount().toDouble() / this.tank.getCapacity().toDouble() * scale),
                scale.toDouble()
            ), 0.0
        ).toInt()
    }

    override fun update() {
        super.update()

        if (!this.world.isRemote) {
            if (timeSinceOxygenRequest > 0) {
                timeSinceOxygenRequest--
            }

            if (this.shouldUseOxygen()) {
                if (this.tank.getFluid() != null) {
                    val fluid: FluidStack = this.tank.getFluid()!!.copy()
                    fluid.amount = max(fluid.amount - this.oxygenPerTick, 0)
                    this.tank.setFluid(fluid)
                }
            }
        }

        this.lastStoredOxygen = this.tank.getFluidAmount().toFloat()
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)

        if (nbt.containsTag("storedOxygen")) {
            this.tank.setFluid(FluidStack(GCFluids.fluidOxygenGas, nbt.getIntOr("storedOxygen", 0)))
        } else if (nbt.containsTag("storedOxygenF")) {
            var oxygen: Int = nbt.getFloatOr("storedOxygenF", 0f).toInt()
            oxygen = min(this.tank.getCapacity(), oxygen)
            this.tank.setFluid(FluidStack(GCFluids.fluidOxygenGas, oxygen))
        } else {
            this.tank.readFromNBT(nbt)
        }
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)
        this.tank.writeToNBT(nbt)
        return nbt
    }

    override fun getUpdateTag(): NBTTagCompound {
        return this.writeToNBT(NBTTagCompound())
    }

    override fun setOxygenStored(oxygen: Int) {
        this.tank.setFluid(FluidStack(GCFluids.fluidOxygenGas, max(min(oxygen, this.getMaxOxygenStored()), 0)))
    }

    override fun getOxygenStored(): Int {
        return this.tank.getFluidAmount()
    }

    override fun getMaxOxygenStored(): Int {
        return this.tank.getCapacity()
    }

    open fun getOxygenInputDirections(): EnumSet<EnumFacing?> {
        return EnumSet.allOf<EnumFacing?>(EnumFacing::class.java)
    }

    open fun getOxygenOutputDirections(): EnumSet<EnumFacing?> {
        return EnumSet.noneOf<EnumFacing?>(EnumFacing::class.java)
    }

    override fun canConnect(direction: EnumFacing?, type: NetworkType?): Boolean {
        if (direction == null) {
            return false
        }

        if (type == NetworkType.FLUID) {
            return this.getOxygenInputDirections().contains(direction) || this.getOxygenOutputDirections()
                .contains(direction)
        }
        if (type == NetworkType.POWER)  //			return this.nodeAvailable(new EnergySourceAdjacent(direction));
        {
            return super.canConnect(direction, type)
        }

        return false
    }

    override fun receiveOxygen(from: EnumFacing?, receive: Int, doReceive: Boolean): Int {
        if (this.getOxygenInputDirections().contains(from)) {
            if (!doReceive) {
                return this.getOxygenRequest(from)
            }

            return this.receiveOxygen(receive, doReceive)
        }

        return 0
    }

    fun receiveOxygen(receive: Int, doReceive: Boolean): Int {
        if (receive > 0) {
            val prevOxygenStored: Int = this.getOxygenStored()
            val newStoredOxygen: Int = min(prevOxygenStored + receive, this.getMaxOxygenStored())

            if (doReceive) {
                timeSinceOxygenRequest = 20
                this.setOxygenStored(newStoredOxygen)
            }

            return max(newStoredOxygen - prevOxygenStored, 0)
        }

        return 0
    }

    override fun provideOxygen(from: EnumFacing?, request: Int, doProvide: Boolean): Int {
        if (this.getOxygenOutputDirections().contains(from)) {
            return this.drawOxygen(request, doProvide)
        }

        return 0
    }

    fun drawOxygen(request: Int, doProvide: Boolean): Int {
        if (request > 0) {
            val requestedOxygen: Int = min(request, this.getOxygenStored())

            if (doProvide) {
                this.setOxygenStored(this.getOxygenStored() - requestedOxygen)
            }

            return requestedOxygen
        }

        return 0
    }

    fun produceOxygen() {
        if (!this.world.isRemote) {
            for (direction: EnumFacing? in this.getOxygenOutputDirections()) {
                if (direction != null) {
                    this.produceOxygen(direction)
                }
            }
        }
    }

    fun produceOxygen(outputDirection: EnumFacing): Boolean {
        val provide: Int = this.getOxygenProvide(outputDirection)

        if (provide > 0) {
            val outputTile: TileEntity? = BlockVec3(this).getTileEntityOnSide(this.world, outputDirection)
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
                    val acceptedOxygen: Int =
                        (outputTile as IOxygenReceiver).receiveOxygen(outputDirection.getOpposite(), provide, true)
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
//                    GasStack toSend = new GasStack((Gas) EnergyConfigHandler.gasOxygen, (int) Math.floor(provide));
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

    override fun getOxygenRequest(direction: EnumFacing?): Int {
        if (this.shouldPullOxygen()) {
            return this.oxygenPerTick * 2
        } else {
            return 0
        }
    }

    override fun shouldPullOxygen(): Boolean {
        return this.getOxygenStored() < this.getMaxOxygenStored()
    }

    /**
     * Make sure this does not exceed the oxygen stored.
     * This should return 0 if no oxygen is stored.
     * Implementing tiles must respect this or you will generate infinite oxygen.
     */
    override fun getOxygenProvide(direction: EnumFacing?): Int {
        return 0
    }

    override fun fill(from: EnumFacing?, resource: FluidStack?, doFill: Boolean): Int {
        if (this.getOxygenInputDirections().contains(from) && resource != null) {
            if (!doFill) {
                return this.getOxygenRequest(from)
            }

            return this.receiveOxygen(resource.amount, doFill)
        }

        return 0
    }

    override fun drain(from: EnumFacing?, resource: FluidStack?, doDrain: Boolean): FluidStack? {
        return if (resource == null) null else drain(from, resource.amount, doDrain)
    }

    override fun drain(from: EnumFacing?, maxDrain: Int, doDrain: Boolean): FluidStack? {
        if (this.getOxygenOutputDirections().contains(from)) {
            return FluidStack(GCFluids.fluidOxygenGas, this.drawOxygen(maxDrain, doDrain))
        }

        return null
    }

    override fun canFill(from: EnumFacing?, fluid: Fluid?): Boolean {
        return this.getOxygenInputDirections().contains(from) && (fluid == null || fluid.getName() == "oxygen")
    }

    override fun canDrain(from: EnumFacing?, fluid: Fluid?): Boolean {
        return this.getOxygenOutputDirections().contains(from) && (fluid == null || fluid.getName() == "oxygen")
    }

    override fun getTankInfo(from: EnumFacing?): Array<FluidTankInfo?> {
        if (canConnect(from, NetworkType.FLUID)) {
            return arrayOf<FluidTankInfo?>(this.tank.getInfo())
        }

        return arrayOf<FluidTankInfo?>()
    }

    companion object {
        @JvmField
        var timeSinceOxygenRequest: Int = 0
    }
}
