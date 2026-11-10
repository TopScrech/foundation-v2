package micdoodle8.mods.galacticraft.core.energy.tile

import micdoodle8.mods.galacticraft.api.power.IEnergyStorageGC
import net.minecraft.nbt.NBTTagCompound
import kotlin.math.max
import kotlin.math.min

open class EnergyStorage constructor(
    @JvmField protected var capacity: Float,
    @JvmField protected var maxReceive: Float,
    @JvmField var maxExtract: Float
) : IEnergyStorageGC {
    @JvmField
    protected var energy: Float = 0f

    @JvmField
    var maxExtractRemaining: Float

    constructor(capacity: Float) : this(capacity, 60f, 30f)

    constructor(capacity: Float, maxTransfer: Float) : this(capacity, 2.5f * maxTransfer, maxTransfer)

    init {
        this.maxExtractRemaining = maxExtract
    }

    open fun readFromNBT(nbt: NBTTagCompound): EnergyStorage {
        this.energy = nbt.getFloat("EnergyF")
        return this
    }

    open fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        if (this.energy < 0) {
            this.energy = 0f
        }

        nbt.setFloat("EnergyF", min(this.energy, this.capacity))
        return nbt
    }

    open fun setCapacity(capacity: Float) {
        this.capacity = capacity

        if (this.energy > capacity) {
            this.energy = capacity
        }
    }

    /*
     * Sets the maximum energy transfer rate on input
     * Call this AFTER setMaxExtract().
     */
    open fun setMaxReceive(maxReceive: Float) {
        this.maxReceive = maxReceive
    }

    /*
     * Sets the energy consumption rate in gJ/t
     * (For machines, this is the energy used per tick.)
     * (For energy sources, this is the maximum output.)
     *
     * Also sets the receive rate at a default value
     * of 2 * the energy consumption rate - so the machine's
     * energy store can charge up even while it is working.
     *
     * If that is not required, call setMaxReceive() AFTER
     * calling setMaxExtract().
     */
    open fun setMaxExtract(maxExtract: Float) {
        this.maxExtract = maxExtract
        this.maxExtractRemaining = maxExtract
        this.maxReceive = 2.5f * maxExtract
    }

    open fun setEnergyStored(energy: Float) {
        this.energy = max(0f, min(energy, this.capacity))
    }

    open fun getMaxReceive(): Float {
        return this.maxReceive
    }

    open fun getMaxExtract(): Float {
        return this.maxExtract
    }

    override fun receiveEnergyGC(maxReceive: Float, simulate: Boolean): Float {
        val energyReceived: Float = min(this.capacity - this.energy, min(this.maxReceive, maxReceive))

        if (!simulate) {
            this.energy += energyReceived
        }

        return energyReceived
    }

    open fun receiveEnergyGC(amount: Float): Float {
        val energyReceived: Float = min(this.capacity - this.energy, min(this.maxReceive, amount))
        this.energy += energyReceived
        return energyReceived
    }

    override fun extractEnergyGC(amount: Float, simulate: Boolean): Float {
        val energyExtracted: Float = min(this.energy, min(this.maxExtractRemaining, amount))

        if (!simulate) {
            this.energy -= energyExtracted
        }

        return energyExtracted
    }

    open fun extractEnergyGCnoMax(amount: Float, simulate: Boolean): Float {
        val energyExtracted: Float = min(this.energy, amount)

        if (!simulate) {
            this.energy -= energyExtracted
        }

        return energyExtracted
    }

    override fun getEnergyStoredGC(): Float {
        return this.energy
    }

    override fun getCapacityGC(): Float {
        return this.capacity
    }

    override fun equals(obj: Any?): Boolean {
        if (obj == null) {
            return false
        }
        if (obj is EnergyStorage) {
            val storage: EnergyStorage = obj
            return (storage.getEnergyStoredGC() == energy && storage.getCapacityGC() == capacity && storage.getMaxReceive() == maxReceive && storage.getMaxExtract() == maxExtract)
        }
        return false
    }
}
