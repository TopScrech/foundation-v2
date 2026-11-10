package galaxyspace.systems.SolarSystem.planets.ceres.dimension

import galaxyspace.systems.SolarSystem.SolarSystemBodies
import asmodeuscore.core.astronomy.dimension.world.gen.WorldProviderAdvancedSpace
import asmodeuscore.api.dimension.IProviderFreeze
import galaxyspace.core.util.GSDimensions
import galaxyspace.core.configs.GSConfigCore
import galaxyspace.systems.SolarSystem.planets.ceres.world.gen.dungeon.RoomTreasureCeres
import asmodeuscore.api.dimension.IProviderWeather
import galaxyspace.systems.SolarSystem.planets.ceres.dimension.ChunkProviderCeres
import galaxyspace.systems.SolarSystem.planets.ceres.world.gen.BiomeProviderCeres
import galaxyspace.systems.SolarSystem.planets.ceres.dimension.sky.SkyProviderCeres
import asmodeuscore.core.astronomy.WeatherData
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.client.CloudRenderer
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import net.minecraft.block.Block
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import net.minecraft.world.DimensionType
import net.minecraft.world.biome.BiomeProvider
import net.minecraft.world.gen.IChunkGenerator
import net.minecraftforge.client.IRenderHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly


open class WorldProviderCeres : WorldProviderAdvancedSpace(), IProviderFreeze, IProviderWeather {
    override fun getHorizon(): Double {
        return 44.0
    }

    override fun getFallDamageModifier(): Float {
        return 0.16f
    }

    override fun getFuelUsageMultiplier(): Double {
        return 0.8
    }

    override fun getMeteorFrequency(): Double {
        return 3.0
    }

    override fun getSoundVolReductionAmount(): Float {
        return Float.MAX_VALUE
    }

    override fun canRainOrSnow(): Boolean {
        return false
    }

    override fun getCelestialBody(): CelestialBody? {
        return SolarSystemBodies.planetCeres
    }

    //Created later
    override fun getChunkProviderClass(): Class<out IChunkGenerator> {
        return ChunkProviderCeres::class.java
    }

    override fun getBiomeProviderClass(): Class<out BiomeProvider> {
        return BiomeProviderCeres::class.java
    }

    override fun getFogColor(): Vector3 {
        return Vector3(0.0, 0.0, 0.0)
    }

    override fun getSkyColor(): Vector3 {
        return Vector3(0.0, 0.0, 0.0)
    }


    override fun isSkyColored(): Boolean {
        return false
    }


    override fun hasSunset(): Boolean {
        return false
    }


    //Can players respawn here?
    override fun shouldForceRespawn(): Boolean {
        return !ConfigManagerCore.forceOverworldRespawn
    }

    @SideOnly(Side.CLIENT)
    override fun getStarBrightness(par1: Float): Float {
        val var2: Float = this.world.getCelestialAngle(par1)
        var var3: Float = 1.0f - (MathHelper.cos(var2 * Math.PI.toFloat() * 2.0f) * 2.0f + 0.25f)

        if (var3 < 0.0f) {
            var3 = 0.0f
        }

        if (var3 > 1.0f) {
            var3 = 1.0f
        }

        return var3 * var3 * 0.5f + 0.3f
    }

    override fun getCloudRenderer(): IRenderHandler? {
        return CloudRenderer()
    }

    @SideOnly(Side.CLIENT)
    override fun getSkyRenderer(): IRenderHandler? {
        if (super.getSkyRenderer() == null) {
            this.setSkyRenderer(SkyProviderCeres())
        }

        return super.getSkyRenderer()
    }

    override fun getDungeonSpacing(): Int {
        return 700
    }

    override fun getDungeonChestType(): ResourceLocation? {
        return RoomTreasureCeres.CERESCHEST
    }

    override fun getSurfaceBlocks(): MutableList<Block?>? {
        return null
    }

    override fun getDimensionType(): DimensionType {
        return (GSDimensions.CERES)!!
    }

    override fun getThermalValueMod(): Float {
        return 0.01f
    }

    override fun getWeather(): WeatherData? {
        if (GSConfigCore.enableExtraWeatherOnBodies) return IProviderWeather.meteoric_rain
        return null
    }
}
