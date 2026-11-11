package asmodeuscore.core.astronomy.dimension.world.data

import asmodeuscore.core.astronomy.dimension.world.data.FrozenStormSaveData
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.util.Mth as MathHelper
import net.minecraft.world.level.Level as World
import com.mojang.serialization.Codec
import foundation.persistence.FoundationSavedData
import net.minecraft.world.level.saveddata.SavedData
import net.minecraft.util.RandomSource

open class FrozenStormSaveData constructor(@Suppress("UNUSED_PARAMETER") name: String) : SavedData() {
    private var isStorm = false

    @JvmField
var prevStormStrength: Float = 0f

    @JvmField
var stormStrength: Float = 0.0f

    @JvmField
    var tickStorm: Int = 0

    @JvmField
    var tickClearWeather: Int = 24000 * 5

    open fun readFromNBT(nbt: NBTTagCompound) {
        isStorm = nbt.getBooleanOr("isFrozenStorm", false)
        tickStorm = nbt.getIntOr("tickFrozenStorm", 0)
        tickClearWeather = nbt.getIntOr("tickClearWeather", 0)
        prevStormStrength = nbt.getFloatOr("prevStormStrength", 0f)
        stormStrength = nbt.getFloatOr("stormStrength", 0f)
    }

    open fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        compound.putBoolean("isFrozenStorm", isStorm)
        compound.putInt("tickFrozenStorm", tickStorm)
        compound.putInt("tickClearWeather", tickClearWeather)
        compound.putFloat("prevStormStrength", prevStormStrength)
        compound.putFloat("stormStrength", stormStrength)
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
        @JvmField
        val CODEC: Codec<FrozenStormSaveData> = NBTTagCompound.CODEC.xmap(
            { tag -> FrozenStormSaveData("").also { it.readFromNBT(tag) } },
            { data -> data.writeToNBT(NBTTagCompound()) }
        )

        @JvmStatic
        fun get(world: World, name: String?): FrozenStormSaveData {
            return FoundationSavedData.get(world, "${name}_frozenstorm", { FrozenStormSaveData("${name}_frozenstorm") }, CODEC)
        }

        @JvmStatic
        fun calculateWeather(data: FrozenStormSaveData, rand: RandomSource) {
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
            data.setDirty()
        }
    }
}
