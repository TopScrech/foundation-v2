package asmodeuscore.core.astronomy.dimension.world.data

import asmodeuscore.core.astronomy.dimension.world.data.DustStormSaveData
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.util.Mth as MathHelper
import net.minecraft.world.level.Level as World
import com.mojang.serialization.Codec
import foundation.persistence.FoundationSavedData
import net.minecraft.world.level.saveddata.SavedData
import net.minecraft.util.RandomSource

open class DustStormSaveData constructor(@Suppress("UNUSED_PARAMETER") name: String) : SavedData() {
    private var isDustStorm = false

    @JvmField
var prevStormStrength: Float = 0f

    @JvmField
var stormStrength: Float = 0.0f

    @JvmField
    var tickDustStorm: Int = 0

    @JvmField
    var tickClearWeather: Int = 24000 * 3

    open fun readFromNBT(nbt: NBTTagCompound) {
        isDustStorm = nbt.getBooleanOr("isDustStorm", false)
        tickDustStorm = nbt.getIntOr("tickDustStorm", 0)
        tickClearWeather = nbt.getIntOr("tickClearWeather", 0)
        prevStormStrength = nbt.getFloatOr("prevStormStrength", 0f)
        stormStrength = nbt.getFloatOr("stormStrength", 0f)
    }

    open fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        compound.putBoolean("isDustStorm", isDustStorm)
        compound.putInt("tickDustStorm", tickDustStorm)
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

    open fun isDustStorm(): Boolean {
        return this.isDustStorm
    }

    open fun setDustStorm(status: Boolean) {
        this.isDustStorm = status
    }

    companion object {
        @JvmField
        val CODEC: Codec<DustStormSaveData> = NBTTagCompound.CODEC.xmap(
            { tag -> DustStormSaveData("").also { it.readFromNBT(tag) } },
            { data -> data.writeToNBT(NBTTagCompound()) }
        )

        @JvmStatic
        fun get(world: World, name: String?): DustStormSaveData {
            return FoundationSavedData.get(world, "${name}_stormdust", { DustStormSaveData("${name}_stormdust") }, CODEC)
        }

        @JvmStatic
        fun calculateWeather(data: DustStormSaveData, rand: RandomSource) {
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
            data.setDirty()
        }
    }
}
