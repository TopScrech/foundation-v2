package asmodeuscore.core.astronomy.dimension.world.data

import asmodeuscore.core.astronomy.dimension.world.data.FrozenStormSaveData
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import net.minecraft.world.storage.MapStorage
import net.minecraft.world.storage.WorldSavedData
import java.util.Random

open class FrozenStormSaveData constructor(name: String) : WorldSavedData(name) {
    private var isStorm = false

    @JvmField
var prevStormStrength: Float = 0f

    @JvmField
var stormStrength: Float = 0.0f

    @JvmField
    var tickStorm: Int = 0

    @JvmField
    var tickClearWeather: Int = 24000 * 5

    override fun readFromNBT(nbt: NBTTagCompound) {
        isStorm = nbt.getBoolean("isFrozenStorm")
        tickStorm = nbt.getInteger("tickFrozenStorm")
        tickClearWeather = nbt.getInteger("tickClearWeather")
        prevStormStrength = nbt.getFloat("prevStormStrength")
        stormStrength = nbt.getFloat("stormStrength")
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        compound.setBoolean("isFrozenStorm", isStorm)
        compound.setInteger("tickFrozenStorm", tickStorm)
        compound.setInteger("tickClearWeather", tickClearWeather)
        compound.setFloat("prevStormStrength", prevStormStrength)
        compound.setFloat("stormStrength", stormStrength)
        return compound
    }

    open fun getStormStrength(delta: Float): Float {
        return this.prevStormStrength + (this.stormStrength - this.prevStormStrength) * delta
    }

    open fun setStormStrength(delta: Float) {
        this.stormStrength = delta
    }

    open fun isFrozenStorm(): Boolean {
        return this.isStorm
    }

    open fun setFrozenStorm(status: Boolean) {
        this.isStorm = status
    }

    companion object {
        @JvmStatic
        fun get(world: World, name: String?): FrozenStormSaveData {
            var name: String? = name
            name += "_frozenstorm"
            val storage: MapStorage = world.getPerWorldStorage()
            var instance: FrozenStormSaveData? =
                storage.getOrLoadData(FrozenStormSaveData::class.java, name) as FrozenStormSaveData?

            if (instance == null) {
                instance = FrozenStormSaveData((name)!!)
                storage.setData(name, instance)
            }
            return instance
        }

        @JvmStatic
        fun calculateWeather(data: FrozenStormSaveData, rand: Random) {
            data.prevStormStrength = data.stormStrength

            if (!data.isFrozenStorm()) {
                val clearWeather: Int = data.tickClearWeather

                if (clearWeather >= 0) {
                    data.tickClearWeather--
                    if (data.stormStrength > 0.0f) data.stormStrength = (data.stormStrength.toDouble() - 0.01).toFloat()

                    if (clearWeather <= 0) {
                        data.setFrozenStorm(true)
                        data.tickStorm = rand.nextInt(6000) + 12000
                    }
                }
            } else {
                val stormDust: Int = data.tickStorm
                if (stormDust >= 0) {
                    data.tickStorm--
                    data.stormStrength = (data.stormStrength.toDouble() + 0.01).toFloat()

                    if (stormDust <= 0) {
                        data.setFrozenStorm(false)
                        data.tickClearWeather = rand.nextInt(168000) + 12000
                    }
                }
            }

            data.stormStrength = MathHelper.clamp(data.stormStrength, 0.0f, 1.0f)
            data.markDirty()
        }
    }
}
