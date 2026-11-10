package micdoodle8.mods.galacticraft.core.energy.tile

import micdoodle8.mods.miccore.Annotations.RuntimeInterface

import micdoodle8.mods.galacticraft.core.energy.EnergyConfigHandler
import micdoodle8.mods.galacticraft.core.energy.EnergyUtil
import micdoodle8.mods.miccore.Annotations
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.MathHelper
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.energy.CapabilityEnergy
import net.minecraftforge.energy.IEnergyStorage


abstract class TileBaseUniversalConductor : TileBaseConductor(), IEnergyStorage {
    override fun onNetworkChanged() {
    }

    override fun getAdjacentConnections(): Array<TileEntity?> {
        return EnergyUtil.getAdjacentPowerConnections(this)
    }

    override fun invalidate() {
        super.invalidate()
    }

    override fun onChunkUnload() {
        super.onChunkUnload()
    }

    //ForgeEnergy
    override fun receiveEnergy(maxReceive: Int, simulate: Boolean): Int {
        if (this.getNetwork() == null || EnergyConfigHandler.disableFEInput) {
            return 0
        }

        val receiveGC: Float = maxReceive * EnergyConfigHandler.RF_RATIO
        val sentGC: Float = receiveGC - ((this.getNetwork())!!.produce(receiveGC, !simulate, 1))!!
        return MathHelper.floor(sentGC / EnergyConfigHandler.RF_RATIO)
    }

    override fun canReceive(): Boolean {
        return !EnergyConfigHandler.disableFEInput
    }

    //ForgeEnergy
    override fun getEnergyStored(): Int {
        return 0
    }

    //ForgeEnergy
    override fun getMaxEnergyStored(): Int {
        if (this.getNetwork() == null || EnergyConfigHandler.disableFEInput) {
            return 0
        }

        return MathHelper.floor(((this.getNetwork())!!.getRequest(this) / EnergyConfigHandler.RF_RATIO)!!)
    }

    //ForgeEnergy
    override fun extractEnergy(maxExtract: Int, simulate: Boolean): Int {
        return 0
    }

    //ForgeEnergy
    override fun canExtract(): Boolean {
        return false
    }

    @Annotations.RuntimeInterface(clazz = "cofh.redstoneflux.api.IEnergyReceiver", modID = "")
    open fun receiveEnergy(from: EnumFacing?, maxReceive: Int, simulate: Boolean): Int {
        if (this.getNetwork() == null) {
            return 0
        }
        val receiveGC: Float = maxReceive * EnergyConfigHandler.RF_RATIO
        val sentGC: Float = receiveGC - ((this.getNetwork())!!.produce(receiveGC, !simulate, 1))!!
        return MathHelper.floor(sentGC / EnergyConfigHandler.RF_RATIO)
    }

    @Annotations.RuntimeInterface(clazz = "cofh.redstoneflux.api.IEnergyProvider", modID = "")
    fun extractEnergy(from: EnumFacing?, maxExtract: Int, simulate: Boolean): Int {
        return 0
    }

    @Annotations.RuntimeInterface(clazz = "cofh.redstoneflux.api.IEnergyHandler", modID = "")
    open fun canConnectEnergy(from: EnumFacing?): Boolean {
        return true
    }

    @Annotations.RuntimeInterface(clazz = "cofh.redstoneflux.api.IEnergyHandler", modID = "")
    fun getEnergyStored(from: EnumFacing?): Int {
        return 0
    }

    @Annotations.RuntimeInterface(clazz = "cofh.redstoneflux.api.IEnergyHandler", modID = "")
    fun getMaxEnergyStored(from: EnumFacing?): Int {
        if (this.getNetwork() == null) {
            return 0
        }

        return MathHelper.floor(((this.getNetwork())!!.getRequest(this) / EnergyConfigHandler.RF_RATIO)!!)
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        if (capability === CapabilityEnergy.ENERGY) {
            return true
        }
        return super.hasCapability(capability, facing)
    }

    override fun <T> getCapability(capability: Capability<T?>, facing: EnumFacing?): T? {
        if (capability === CapabilityEnergy.ENERGY) {
            return this as T
        }

        return super.getCapability<T?>(capability, facing)
    }
}
