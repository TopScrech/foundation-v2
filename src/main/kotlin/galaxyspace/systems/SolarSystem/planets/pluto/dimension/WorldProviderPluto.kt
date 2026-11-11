package galaxyspace.systems.SolarSystem.planets.pluto.dimension

import asmodeuscore.core.astronomy.dimension.world.gen.WorldProviderAdvancedSpace
import asmodeuscore.api.dimension.IProviderFreeze
import galaxyspace.systems.SolarSystem.SolarSystemBodies
import galaxyspace.systems.SolarSystem.planets.pluto.dimension.ChunkProviderPluto
import galaxyspace.systems.SolarSystem.planets.pluto.world.gen.BiomeProviderPluto
import galaxyspace.systems.SolarSystem.planets.pluto.dimension.sky.SkyProviderPluto
import galaxyspace.core.util.GSDimensions
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.client.CloudRenderer
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import net.minecraft.world.level.block.Block
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.util.Mth as MathHelper
import net.minecraft.world.level.dimension.DimensionType
import net.minecraft.world.biome.BiomeProvider
import net.minecraft.world.gen.IChunkGenerator
import net.minecraftforge.client.IRenderHandler
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist


open class WorldProviderPluto : WorldProviderAdvancedSpace(), IProviderFreeze {
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
        return true
    }


    override fun getCelestialBody(): CelestialBody? {
        return SolarSystemBodies.planetPluto
    }

    //Created later
    override fun getChunkProviderClass(): Class<out IChunkGenerator> {
        return ChunkProviderPluto::class.java
    }

    override fun getBiomeProviderClass(): Class<out BiomeProvider> {
        return BiomeProviderPluto::class.java
    }

    override fun getFogColor(): Vector3 {
        val f: Float = 1.0f - this.getStarBrightness(1.5f)
        return Vector3((32 / 255.0f * f).toDouble(), (32 / 255.0f * f).toDouble(), (32 / 255.0f * f).toDouble())
    }

    override fun getSkyColor(): Vector3 {
        val f: Float = 1.0f - this.getStarBrightness(1.0f)
        return Vector3((12 / 255.0f * f).toDouble(), (12 / 255.0f * f).toDouble(), (12 / 255.0f * f).toDouble())
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

    @OnlyIn(Dist.CLIENT)
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

    @OnlyIn(Dist.CLIENT)
    override fun getSunBrightness(par1: Float): Float {
        val f1: Float = this.world.getCelestialAngle(1.0f)
        var f2: Float = 1.25f - (MathHelper.cos(f1 * 3.1415927f * 2.0f) * 2.0f + 0.2f)
        val f3: Float = this.world.getWorldTime().toFloat()
        if (f2 < 0.0f) {
            f2 = 0.0f
        }

        if (f2 > 1.0f) {
            f2 = 1.0f
        }

        f2 = 1.2f - f2
        return f2 * 0.25f
    }

    override fun getCloudRenderer(): IRenderHandler? {
        return CloudRenderer()
    }

    @OnlyIn(Dist.CLIENT)
    override fun getSkyRenderer(): IRenderHandler? {
        if (super.getSkyRenderer() == null) {
            this.setSkyRenderer(SkyProviderPluto())
        }

        return super.getSkyRenderer()
    }

    override fun getDungeonSpacing(): Int {
        return 0
    }

    override fun getDungeonChestType(): ResourceLocation? {
        return null
    }

    override fun getSurfaceBlocks(): MutableList<Block?>? {
        return null
    }

    override fun getDimensionType(): DimensionType {
        return (GSDimensions.PLUTO)!!
    }

    override fun getThermalValueMod(): Float {
        return 0.02f
    }
}
