package asmodeuscore.core.astronomy.dimension.world.data

import asmodeuscore.core.prefab.entities.EntityAdvMeteor
import asmodeuscore.core.astronomy.WeatherData.WeatherType
import asmodeuscore.core.astronomy.dimension.world.data.MeteoricRainSaveData
import asmodeuscore.api.dimension.IProviderWeather
import micdoodle8.mods.galacticraft.core.GCBlocks
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.server.MinecraftServer
import net.minecraft.world.World
import net.minecraft.world.storage.MapStorage
import net.minecraft.world.storage.WorldSavedData
import net.minecraftforge.fml.common.FMLCommonHandler
import java.util.Random

open class MeteoricRainSaveData constructor(name: String) : WorldSavedData(name) {
    @JvmField
    var tickMeteoricRain: Int = 0

    @JvmField
    var tickClearWeather: Int = 24000 * 5

    override fun readFromNBT(nbt: NBTTagCompound) {
        tickMeteoricRain = nbt.getInteger("tickMeteoricRain")
        tickClearWeather = nbt.getInteger("tickClearWeather")
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        compound.setInteger("tickMeteoricRain", tickMeteoricRain)
        compound.setInteger("tickClearWeather", tickClearWeather)
        return compound
    }

    open fun isMeteoricStorm(): Boolean {
        return this.tickMeteoricRain > 0
    }

    companion object {
        @JvmStatic
        fun get(world: World, name: String?): MeteoricRainSaveData {
            var name: String? = name
            name += "_meteoricrain"

            val storage: MapStorage = world.getPerWorldStorage()
            var instance: MeteoricRainSaveData? =
                storage.getOrLoadData(MeteoricRainSaveData::class.java, name) as MeteoricRainSaveData?

            if (instance == null) {
                instance = MeteoricRainSaveData((name)!!)
                storage.setData(name, instance)
            }
            return instance
        }

        @JvmStatic
        fun calculateWeather(data: MeteoricRainSaveData, rand: Random) {
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


                    if (meteoricRain <= 0) {
                        data.tickClearWeather = rand.nextInt(168000) + 12000
                    }
                }
            }

            data.markDirty()
        }

        @JvmStatic
        fun spawnMeteor(data: MeteoricRainSaveData) {
            val server: MinecraftServer = FMLCommonHandler.instance().getMinecraftServerInstance()

            if (!data.isMeteoricStorm()) return

            for (player: EntityPlayerMP in server.getPlayerList().getPlayers()) {
                throwMeteors(player, 12, 3)
            }
        }

        @JvmStatic
        private fun throwMeteors(player: EntityPlayerMP, freq: Int, chanceSpawnBlock: Int) {
            val world: World = player.world
            if (world.provider is IProviderWeather && !world.isRemote) {
                if (((world.provider as IProviderWeather).getWeather())!!.getWeatherType() == WeatherType.METEORIC_RAIN) {
                    if (world.rand.nextInt(freq) == 0) {
                        val closestPlayer: EntityPlayer? = world.getClosestPlayerToEntity(player, 100.0)

                        if (closestPlayer == null || closestPlayer.getEntityId() <= player.getEntityId()) {
                            val x: Int
                            val y: Int
                            val z: Int
                            val motX: Double
                            val motZ: Double
                            x = world.rand.nextInt(60) - 30
                            y = world.rand.nextInt(20) + 200
                            z = world.rand.nextInt(60) - 30
                            motX = world.rand.nextDouble() * 1
                            motZ = world.rand.nextDouble() * 1

                            var block: IBlockState? = null
                            if (chanceSpawnBlock > 0) block =
                                if (world.rand.nextInt(100) <= chanceSpawnBlock) (GCBlocks.fallenMeteor)!!.getDefaultState() else null

                            val meteor: EntityAdvMeteor = EntityAdvMeteor(
                                world,
                                player.posX + x,
                                player.posY + y,
                                player.posZ + z,
                                motX - 0.5,
                                0.0,
                                motZ - 0.5,
                                world.rand.nextInt(3)
                            ).setSpawnBlock(block)

                            world.spawnEntity(meteor)
                        }
                    }
                }
            }
        }
    }
}
