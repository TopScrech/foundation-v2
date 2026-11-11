package micdoodle8.mods.galacticraft.planets.venus.dimension

import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeAdaptive
import micdoodle8.mods.galacticraft.api.prefab.world.gen.WorldProviderSpace
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.api.world.ISolarLevel
import micdoodle8.mods.galacticraft.api.world.IWeatherProvider
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.event.EventHandlerGC
import micdoodle8.mods.galacticraft.planets.GCPlanetDimensions
import micdoodle8.mods.galacticraft.planets.venus.VenusBlocks
import micdoodle8.mods.galacticraft.planets.venus.VenusModule
import micdoodle8.mods.galacticraft.planets.venus.client.fx.ParticleAcidVapor
import micdoodle8.mods.galacticraft.planets.venus.world.gen.BiomeProviderVenus
import micdoodle8.mods.galacticraft.planets.venus.world.gen.ChunkProviderVenus
import micdoodle8.mods.galacticraft.planets.venus.world.gen.dungeon.RoomTreasureVenus
import net.minecraft.world.level.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel as WorldClient
import net.minecraft.client.particle.Particle
import net.minecraft.sounds.SoundEvents
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.sounds.SoundSource as SoundCategory
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth as MathHelper
import net.minecraft.world.level.dimension.DimensionType
import net.minecraft.world.level.Level as World
import net.minecraft.world.biome.BiomeProvider
import net.minecraft.world.level.chunk.LevelChunk as Chunk
import net.minecraft.world.gen.IChunkGenerator
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.util.LinkedList
import java.util.Random

open class WorldProviderVenus : WorldProviderSpace(), IGalacticraftWorldProvider, ISolarLevel, IWeatherProvider {
    private var solarMultiplier = -0.36
    private val prevRainingStrength = 0f
    private val rainingStrength = 0f
    private var raining = false
    private var rainTime = 100
    private var rainChange = 100
    private var targetRain = 0.0f

    override fun getFogColor(): Vector3 {
        val night: Float = this.getStarBrightness(1.0f)
        val day: Float = 1.0f - this.getStarBrightness(1.0f)
        val dayColR: Float = 203.0f / 255.0f
        val dayColG: Float = 147.0f / 255.0f
        val dayColB: Float = 0.0f / 255.0f
        val nightColR: Float = 131.0f / 255.0f
        val nightColG: Float = 108.0f / 255.0f
        val nightColB: Float = 46.0f / 255.0f
        return Vector3(
            (dayColR * day + nightColR * night).toDouble(),
            (dayColG * day + nightColG * night).toDouble(),
            (dayColB * day + nightColB * night).toDouble()
        )
    }

    override fun getSkyColor(): Vector3 {
        val night: Float = this.getStarBrightness(1.0f)
        val day: Float = 1.0f - this.getStarBrightness(1.0f)
        val dayColR: Float = 255.0f / 255.0f
        val dayColG: Float = 207.0f / 255.0f
        val dayColB: Float = 81.0f / 255.0f
        val nightColR: Float = 118.0f / 255.0f
        val nightColG: Float = 89.0f / 255.0f
        val nightColB: Float = 21.0f / 255.0f
        return Vector3(
            (dayColR * day + nightColR * night).toDouble(),
            (dayColG * day + nightColG * night).toDouble(),
            (dayColB * day + nightColB * night).toDouble()
        )
    }

    override fun hasSunset(): Boolean {
        return false
    }

    override fun getDayLength(): Long {
        return 720000L // 30 times longer than earth
    }

    override fun getChunkProviderClass(): Class<out IChunkGenerator> {
        return ChunkProviderVenus::class.java
    }

    override fun getBiomeProviderClass(): Class<out BiomeProvider> {
        BiomeAdaptive.setBodyMultiBiome((VenusModule.planetVenus)!!)
        return BiomeProviderVenus::class.java
    }

    @OnlyIn(Dist.CLIENT)
    override fun getStarBrightness(par1: Float): Float {
        val f1: Float = this.world.getCelestialAngle(par1)
        var f2: Float = 1.0f - (MathHelper.cos(f1 * Constants.twoPI) * 2.0f + 0.25f)

        if (f2 < 0.0f) {
            f2 = 0.0f
        }

        if (f2 > 1.0f) {
            f2 = 1.0f
        }

        return f2 * f2 * 0.75f
    }

    override fun getHorizon(): Double {
        return 44.0
    }

    override fun getAverageGroundLevel(): Int {
        return 76
    }

    override fun canCoordinateBeSpawn(var1: Int, var2: Int): Boolean {
        return true
    }

    //Overriding so that beds do not explode on Mars
    public override fun canRespawnHere(): Boolean {
        if (EventHandlerGC.bedActivated) {
            EventHandlerGC.bedActivated = false
            return true
        }
        return false
    }

    override fun getGravity(): Float {
        return 0.0375f
    }

    override fun getFuelUsageMultiplier(): Double {
        return 0.9
    }

    override fun canSpaceshipTierPass(tier: Int): Boolean {
        return tier >= 3
    }

    override fun getFallDamageModifier(): Float {
        return 0.38f
    }

    override fun getCelestialBody(): CelestialBody? {
        return VenusModule.planetVenus
    }

    override fun getSolarEnergyMultiplier(): Double {
        if (this.solarMultiplier < 0.0) {
            val s: Double = this.getSolarSize().toDouble()
            this.solarMultiplier = s * s * s
        }
        return this.solarMultiplier
    }

    override fun shouldDisablePrecipitation(): Boolean {
        return false
    }

    override fun getDimensionType(): DimensionType {
        return (GCPlanetDimensions.VENUS)!!
    }

    override fun getDungeonSpacing(): Int {
        return 704
    }

    override fun getArrowGravity(): Float {
        return 0.0275f
    }

    override fun getDungeonChestType(): ResourceLocation? {
        return RoomTreasureVenus.VENUSCHEST
    }

    override fun getSurfaceBlocks(): MutableList<Block?> {
        val list: MutableList<Block?> = LinkedList<Block?>()
        list.add(VenusBlocks.venusBlock)
        return list
    }

    override fun canDoRainSnowIce(chunk: Chunk): Boolean {
        return true
    }

    @OnlyIn(Dist.CLIENT)
    override fun getParticle(world: WorldClient?, x: Double, y: Double, z: Double): Particle {
        return ParticleAcidVapor((world)!!, x, y, z, 0.0, 0.0, 0.0, 0.95f)
    }

    override fun updateWeatherOverride() {
        if (!this.world.isRemote) {
            if (--this.rainTime <= 0) {
                this.raining = !this.raining
                if (this.raining) {
                    this.rainTime = (this.world.rand.nextInt(3600) + 1000)
                } else {
                    this.rainTime = (this.world.rand.nextInt(2000) + 1000)
                }
            }

            if (--this.rainChange <= 0) {
                this.targetRain = 0.15f + this.world.rand.nextFloat() * 0.45f
                this.rainChange = (this.world.rand.nextInt(200) + 100)
            }

            var strength: Float = this.world.rainingStrength
            this.world.prevRainingStrength = strength
            if (this.raining && strength < this.targetRain) {
                strength += 0.004f
            } else if (!this.raining || strength > this.targetRain) {
                strength -= 0.004f
            }
            this.world.rainingStrength = MathHelper.clamp(strength, 0.0f, 0.6f)
        }
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
                SoundEvents.BLOCK_LAVA_EXTINGUISH,
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
                SoundEvents.BLOCK_LAVA_EXTINGUISH,
                SoundCategory.WEATHER,
                0.04f,
                0.8f + (random)!!.nextFloat() * 0.06f + (random)!!.nextFloat() * 0.06f,
                false
            )
        }
    }

    override fun getSoundInterval(rainStrength: Float): Int {
        return 80 - (rainStrength * 88f).toInt()
    }
}
