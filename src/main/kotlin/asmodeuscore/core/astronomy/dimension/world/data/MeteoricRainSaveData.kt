package asmodeuscore.core.astronomy.dimension.world.data

import com.mojang.serialization.Codec
import foundation.persistence.FoundationSavedData
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.util.RandomSource
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.saveddata.SavedData

open class MeteoricRainSaveData constructor(@Suppress("UNUSED_PARAMETER") name: String) : SavedData() {
    @JvmField
    var tickMeteoricRain: Int = 0

    @JvmField
    var tickClearWeather: Int = 24000 * 5

    open fun readFromNBT(nbt: NBTTagCompound) {
        tickMeteoricRain = nbt.getIntOr("tickMeteoricRain", 0)
        tickClearWeather = nbt.getIntOr("tickClearWeather", 0)
    }

    open fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        compound.putInt("tickMeteoricRain", tickMeteoricRain)
        compound.putInt("tickClearWeather", tickClearWeather)
        return compound
    }

    open fun isMeteoricStorm(): Boolean {
        return this.tickMeteoricRain > 0
    }

    companion object {
        @JvmField
        val CODEC: Codec<MeteoricRainSaveData> = NBTTagCompound.CODEC.xmap(
            { tag -> MeteoricRainSaveData("").also { it.readFromNBT(tag) } },
            { data -> data.writeToNBT(NBTTagCompound()) }
        )

        @JvmStatic
        fun get(world: World, name: String?): MeteoricRainSaveData {
            return FoundationSavedData.get(world, "${name}_meteoricrain", { MeteoricRainSaveData("${name}_meteoricrain") }, CODEC)
        }

        @JvmStatic
        fun calculateWeather(data: MeteoricRainSaveData, rand: RandomSource) {
            if (!data.isMeteoricStorm()) {
                val clearWeather: Int = data.tickClearWeather

                if (clearWeather >= 0) {
                    data.tickClearWeather--

                    if (clearWeather <= 0) {
                        data.tickMeteoricRain = rand.nextInt(6000) + 6000
                    }
                }
            } else {
                val meteoricRain: Int = data.tickMeteoricRain
                if (meteoricRain >= 0) {
                    data.tickMeteoricRain--


                    if (data.tickMeteoricRain <= 0) {
                        data.tickClearWeather = rand.nextInt(168000) + 12000
                    }
                }
            }

            data.setDirty()
        }

    }
}
