package micdoodle8.mods.galacticraft.core.energy.tile

import micdoodle8.mods.galacticraft.api.power.IEnergyStorageGC
import net.minecraft.nbt.CompoundTag as NBTTagCompound
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
        require(capacity.isFinite() && capacity >= 0f) { "Energy capacity must be finite and nonnegative" }
        require(maxReceive.isFinite() && maxReceive >= 0f) { "Energy receive rate must be finite and nonnegative" }
        require(maxExtract.isFinite() && maxExtract >= 0f) { "Energy extraction rate must be finite and nonnegative" }
        this.maxExtractRemaining = maxExtract
    }

    open fun readFromNBT(nbt: NBTTagCompound): EnergyStorage {
        setEnergyStored(nbt.getFloatOr("EnergyF", 0f))
        return this
    }

    open fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        setEnergyStored(this.energy)
        nbt.putFloat("EnergyF", this.energy)
        return nbt
    }

    open fun setCapacity(capacity: Float) {
        require(capacity.isFinite() && capacity >= 0f) { "Energy capacity must be finite and nonnegative" }
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
        require(maxReceive.isFinite() && maxReceive >= 0f) { "Energy receive rate must be finite and nonnegative" }
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
        require(maxExtract.isFinite() && maxExtract >= 0f) { "Energy extraction rate must be finite and nonnegative" }
        this.maxExtract = maxExtract
        this.maxExtractRemaining = maxExtract
        this.maxReceive = 2.5f * maxExtract
    }

    open fun setEnergyStored(energy: Float) {
        this.energy = if (energy.isFinite()) energy.coerceIn(0f, capacity) else 0f
    }

    open fun getMaxReceive(): Float {
        return this.maxReceive
    }

    open fun getMaxExtract(): Float {
        return this.maxExtract
    }

    override fun receiveEnergyGC(maxReceive: Float, simulate: Boolean): Float {
        if (!maxReceive.isFinite() || maxReceive <= 0f) return 0f
        val energyReceived: Float = min(this.capacity - this.energy, min(this.maxReceive, maxReceive))

        if (!simulate) {
            this.energy += energyReceived
        }

        return energyReceived
    }

    open fun receiveEnergyGC(amount: Float): Float = receiveEnergyGC(amount, false)

    override fun extractEnergyGC(amount: Float, simulate: Boolean): Float {
        if (!amount.isFinite() || amount <= 0f) return 0f
        val energyExtracted: Float = min(this.energy, min(this.maxExtractRemaining.coerceAtLeast(0f), amount))

        if (!simulate) {
            this.energy -= energyExtracted
        }

        return energyExtracted
    }

    open fun extractEnergyGCnoMax(amount: Float, simulate: Boolean): Float {
        if (!amount.isFinite() || amount <= 0f) return 0f
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
