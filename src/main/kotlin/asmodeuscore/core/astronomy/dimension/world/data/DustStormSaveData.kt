package asmodeuscore.core.astronomy.dimension.world.data

import asmodeuscore.core.astronomy.dimension.world.data.DustStormSaveData
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import net.minecraft.world.storage.MapStorage
import net.minecraft.world.storage.WorldSavedData
import java.util.Random

open class DustStormSaveData constructor(name: String) : WorldSavedData(name) {
    private var isDustStorm = false

    @JvmField
var prevStormStrength: Float = 0f

    @JvmField
var stormStrength: Float = 0.0f

    @JvmField
    var tickDustStorm: Int = 0

    @JvmField
    var tickClearWeather: Int = 24000 * 3

    override fun readFromNBT(nbt: NBTTagCompound) {
        isDustStorm = nbt.getBoolean("isDustStorm")
        tickDustStorm = nbt.getInteger("tickDustStorm")
        tickClearWeather = nbt.getInteger("tickClearWeather")
        prevStormStrength = nbt.getFloat("prevStormStrength")
        stormStrength = nbt.getFloat("stormStrength")
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        compound.setBoolean("isDustStorm", isDustStorm)
        compound.setInteger("tickDustStorm", tickDustStorm)
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

    open fun isDustStorm(): Boolean {
        return this.isDustStorm
    }

    open fun setDustStorm(status: Boolean) {
        this.isDustStorm = status
    }

    companion object {
        @JvmStatic
        fun get(world: World, name: String?): DustStormSaveData {
            var name: String? = name
            name += "_stormdust"
            val storage: MapStorage = world.getPerWorldStorage()
            var instance: DustStormSaveData? =
                storage.getOrLoadData(DustStormSaveData::class.java, name) as DustStormSaveData?

            if (instance == null) {
                instance = DustStormSaveData((name)!!)
                storage.setData(name, instance)
            }
            return instance
        }

        @JvmStatic
        fun calculateWeather(data: DustStormSaveData, rand: Random) {
            data.prevStormStrength = data.stormStrength

            if (!data.isDustStorm()) {
                val clearWeather: Int = data.tickClearWeather

                if (clearWeather >= 0) {
                    data.tickClearWeather--
                    if (data.stormStrength > 0.0f) data.stormStrength = (data.stormStrength.toDouble() - 0.01).toFloat()

                    if (clearWeather <= 0) {
                        data.setDustStorm(true)
                        data.tickDustStorm = rand.nextInt(24000) + 12000
                    }
                }
            } else {
                val stormDust: Int = data.tickDustStorm
                if (stormDust >= 0) {
                    data.tickDustStorm--
                    data.stormStrength = (data.stormStrength.toDouble() + 0.01).toFloat()

                    if (stormDust <= 0) {
                        data.setDustStorm(false)
                        data.tickClearWeather = rand.nextInt(168000) + 12000
                    }
                }
            }

            data.stormStrength = MathHelper.clamp(data.stormStrength, 0.0f, 1.0f)
            data.markDirty()
        }
    }
}
