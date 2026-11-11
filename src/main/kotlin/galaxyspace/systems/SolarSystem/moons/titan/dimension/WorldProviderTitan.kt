package galaxyspace.systems.SolarSystem.moons.titan.dimension

import galaxyspace.systems.SolarSystem.SolarSystemBodies
import asmodeuscore.core.astronomy.dimension.world.gen.WorldProviderAdvancedSpace
import asmodeuscore.api.dimension.IProviderFreeze
import galaxyspace.core.util.GSDimensions
import asmodeuscore.api.dimension.IProviderFog
import galaxyspace.systems.SolarSystem.moons.titan.dimension.ChunkProviderTitan
import galaxyspace.systems.SolarSystem.moons.titan.world.gen.BiomeProviderTitan
import galaxyspace.systems.SolarSystem.moons.titan.dimension.sky.WeatherProviderTitan
import galaxyspace.systems.SolarSystem.moons.titan.dimension.sky.SkyProviderTitan
import galaxyspace.core.client.fx.ParticleRainCustom
import galaxyspace.core.util.GSUtils
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.api.world.IWeatherProvider
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import net.minecraft.world.level.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel as WorldClient
import net.minecraft.client.particle.Particle
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.EnumParticleTypes
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.sounds.SoundSource as SoundCategory
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth as MathHelper
import net.minecraft.world.phys.Vec3 as Vec3d
import net.minecraft.world.level.dimension.DimensionType
import net.minecraft.world.level.Level as World
import net.minecraft.world.biome.BiomeProvider
import net.minecraft.world.gen.IChunkGenerator
import net.minecraftforge.client.IRenderHandler
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.util.Random


open class WorldProviderTitan : WorldProviderAdvancedSpace(), IProviderFreeze, IProviderFog, IWeatherProvider {
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
        return 0.0
    }

    override fun getSoundVolReductionAmount(): Float {
        return Float.MAX_VALUE
    }

    override fun canRainOrSnow(): Boolean {
        return true
    }

    override fun getCelestialBody(): CelestialBody? {
        return SolarSystemBodies.titanSaturn
    }

    override fun getChunkProviderClass(): Class<out IChunkGenerator> {
        return ChunkProviderTitan::class.java
    }

    override fun getBiomeProviderClass(): Class<out BiomeProvider> {
        return BiomeProviderTitan::class.java
    }

    override fun getFogColor(): Vector3 {
        val f: Float = 1.0f
        return Vector3((224 / 255.0f * f).toDouble(), (174 / 255.0f * f).toDouble(), (132 / 255.0f * f).toDouble())
    }

    override fun getSkyColor(): Vector3 {
        val f: Float = 1.0f - this.getStarBrightness(1.0f) - (this.world.getRainStrength(1.0f) / 4)
        return Vector3((204 / 255.0f * f).toDouble(), (121 / 255.0f * f).toDouble(), (53 / 255.0f * f).toDouble())
    }


    override fun isSkyColored(): Boolean {
        return true
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
    override fun getCloudRenderer(): IRenderHandler? {
        return super.getCloudRenderer()
    }

    @OnlyIn(Dist.CLIENT)
    override fun getCloudColor(partialTicks: Float): Vec3d {
        val f: Float = this.world.getCelestialAngle(partialTicks)
        var f1: Float = MathHelper.cos(f * (Math.PI.toFloat() * 2f)) * 2.0f + 0.5f
        f1 = MathHelper.clamp(f1, 0.0f, 1.0f)
        var r: Float = 1.0f
        var g: Float = 1.0f
        var b: Float = 1.0f
        val a: Float = this.world.getRainStrength(partialTicks)

        if (a > 0.0f) {
            val f6: Float = (r * 0.3f + g * 0.59f + b * 0.11f) * 0.6f
            val f7: Float = 1.0f - a * 0.95f
            r = r * f7 + f6 * (1.0f - f7)
            g = g * f7 + f6 * (1.0f - f7)
            b = b * f7 + f6 * (1.0f - f7)
        }

        r = r * (f1 * 0.9f + 0.1f)
        g = g * (f1 * 0.9f + 0.1f)
        b = b * (f1 * 0.85f + 0.15f)
        val f9: Float = this.world.getThunderStrength(partialTicks)

        if (f9 > 0.0f) {
            val f10: Float = (r * 0.3f + g * 0.59f + b * 0.11f) * 0.2f
            val f8: Float = 1.0f - f9 * 0.95f
            r = r * f8 + f10 * (1.0f - f8)
            g = g * f8 + f10 * (1.0f - f8)
            b = b * f8 + f10 * (1.0f - f8)
        }

        return Vec3d(r.toDouble(), g.toDouble(), b.toDouble())
    }

    @OnlyIn(Dist.CLIENT)
    override fun getCloudHeight(): Float {
        return 180.0f
    }

    @OnlyIn(Dist.CLIENT)
    override fun getWeatherRenderer(): IRenderHandler? {
        return WeatherProviderTitan()
    }

    @OnlyIn(Dist.CLIENT)
    override fun getSkyRenderer(): IRenderHandler? {
        if (super.getSkyRenderer() == null) {
            this.setSkyRenderer(SkyProviderTitan())
        }

        return super.getSkyRenderer()
    }

    @OnlyIn(Dist.CLIENT)
    override fun getParticle(world: WorldClient?, x: Double, y: Double, z: Double): Particle {
        return ParticleRainCustom(
            (world)!!,
            x,
            y + 0.1,
            z,
            0.0,
            0.0,
            0.0,
            EnumParticleTypes.SMOKE_NORMAL.getParticleID(),
            1.0f,
            Vector3(1.0, 0.4, 0.0)
        )
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
        return (GSDimensions.TITAN)!!
    }

    override fun getFogDensity(x: Int, y: Int, z: Int): Float {
        return 0.97f
    }

    override fun getFogColor(x: Int, y: Int, z: Int): Int {
        return GSUtils.getColor(224, 174, 132, 1)
    }

    override fun weatherSounds(
        j: Int,
        mc: Minecraft?,
        world: World?,
        blockpos: BlockPos?,
        xx: Double,
        yy: Double,
        zz: Double,
        random: Random?
    ) {
        if (yy.toInt() >= (blockpos)!!.getY() + 1 && (world)!!.getPrecipitationHeight(blockpos).getY() > (blockpos)!!.getY()) {
            (mc)!!.world.playSound(
                xx,
                yy,
                zz,
                SoundEvents.WEATHER_RAIN_ABOVE,
                SoundCategory.WEATHER,
                0.025f,
                0.6f + (random)!!.nextFloat() * 0.2f,
                false
            )
        } else {
            (mc)!!.world.playSound(
                xx,
                yy,
                zz,
                SoundEvents.WEATHER_RAIN,
                SoundCategory.WEATHER,
                0.04f,
                0.8f + (random)!!.nextFloat() * 0.06f + (random)!!.nextFloat() * 0.06f,
                false
            )
        }
    }

    override fun getSoundInterval(rainStrength: Float): Int {
        val result: Int = 80 - (rainStrength * 88f).toInt()
        return if (result > 0) result else 1
    }

    override fun enableAdvancedThermalLevel(): Boolean {
        return true
    }

    override fun getThermalValueMod(): Float {
        return 0.001f
    }
}
