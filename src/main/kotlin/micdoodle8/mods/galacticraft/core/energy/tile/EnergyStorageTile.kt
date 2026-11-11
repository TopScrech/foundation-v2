package micdoodle8.mods.galacticraft.core.energy.tile

import micdoodle8.mods.galacticraft.api.power.EnergySource.EnergySourceAdjacent
import micdoodle8.mods.miccore.Annotations.NetworkedField

import micdoodle8.mods.galacticraft.api.power.EnergySource
import micdoodle8.mods.galacticraft.api.power.IEnergyHandlerGC
import micdoodle8.mods.galacticraft.api.transmission.NetworkType
import micdoodle8.mods.galacticraft.api.transmission.tile.IElectrical
import micdoodle8.mods.galacticraft.core.tile.ReceiverMode
import micdoodle8.mods.galacticraft.core.tile.TileEntityAdvanced
import micdoodle8.mods.miccore.Annotations
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.core.Direction as EnumFacing
import net.minecraftforge.fml.LogicalSide as Side
import kotlin.math.min

abstract class EnergyStorageTile : TileEntityAdvanced(), IEnergyHandlerGC, IElectrical {
    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var storage: EnergyStorage = EnergyStorage(STANDARD_CAPACITY, 10f)

    @JvmField
    var tierGC: Int = 1

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var poweredByTierGC: Int = 1

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        this.storage.readFromNBT(nbt)
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)
        this.storage.writeToNBT(nbt)
        return nbt
    }

    override fun getUpdateTag(): NBTTagCompound {
        return this.writeToNBT(NBTTagCompound())
    }

    abstract fun getModeFromDirection(direction: EnumFacing?): ReceiverMode?

    override fun receiveEnergyGC(from: EnergySource?, amount: Float, simulate: Boolean): Float {
        return this.storage.receiveEnergyGC(amount, simulate)
    }

    override fun extractEnergyGC(from: EnergySource?, amount: Float, simulate: Boolean): Float {
        return this.storage.extractEnergyGC(amount, simulate)
    }

    override fun nodeAvailable(from: EnergySource?): Boolean {
        if (from !is EnergySource.EnergySourceAdjacent) {
            return false
        }

        return this.getModeFromDirection(from.direction) != ReceiverMode.UNDEFINED
    }

    override fun getEnergyStoredGC(from: EnergySource?): Float {
        return this.storage.getEnergyStoredGC()
    }

    fun getEnergyStoredGC(): Float {
        return this.storage.getEnergyStoredGC()
    }

    override fun getMaxEnergyStoredGC(from: EnergySource?): Float {
        return this.storage.getCapacityGC()
    }

    fun getMaxEnergyStoredGC(): Float {
        return this.storage.getCapacityGC()
    }

    override fun canConnect(direction: EnumFacing?, type: NetworkType?): Boolean {
        return false
    }

    //Five methods for compatibility with basic electricity
    override fun receiveElectricity(from: EnumFacing?, receive: Float, tier: Int, doReceive: Boolean): Float {
        this.poweredByTierGC = if (tier < 6) tier else 6
        return this.storage.receiveEnergyGC(receive, !doReceive)
    }

    override fun provideElectricity(from: EnumFacing?, request: Float, doProvide: Boolean): Float {
        return this.storage.extractEnergyGC(request, !doProvide)
    }

    override fun getRequest(direction: EnumFacing?): Float {
        return min(this.storage.getCapacityGC() - this.storage.getEnergyStoredGC(), this.storage.getMaxReceive())
    }

    override fun getProvide(direction: EnumFacing?): Float {
        return 0f
    }

    override fun getTierGC(): Int {
        return this.tierGC
    }

    fun setTierGC(newTier: Int) {
        this.tierGC = newTier
    }

    companion object {
        const val STANDARD_CAPACITY: Float = 16000f
    }
}
