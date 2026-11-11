package galaxyspace.systems.SolarSystem.satellites.venus.dimension

import galaxyspace.systems.SolarSystem.SolarSystemBodies
import galaxyspace.core.util.GSDimensions
import galaxyspace.systems.SolarSystem.planets.ceres.world.gen.BiomeProviderCeres
import galaxyspace.systems.SolarSystem.satellites.venus.dimension.ChunkProviderVenusSS
import galaxyspace.systems.SolarSystem.satellites.venus.dimension.sky.SkyProviderVenusSS
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.api.world.IExitHeight
import micdoodle8.mods.galacticraft.api.world.IOrbitDimension
import micdoodle8.mods.galacticraft.api.world.ISolarLevel
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderSpaceStation
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.world.gen.dungeon.RoomTreasure
import micdoodle8.mods.galacticraft.planets.venus.VenusModule
import net.minecraft.world.level.block.Block
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.util.Mth as MathHelper
import net.minecraft.world.level.dimension.DimensionType
import net.minecraft.world.biome.BiomeProvider
import net.minecraft.world.gen.IChunkGenerator
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class WorldProviderVenusSS : WorldProviderSpaceStation(), IOrbitDimension, ISolarLevel, IExitHeight {
    override fun getDimensionType(): DimensionType {
        return (GSDimensions.VENUS_SS)!!
    }

    @OnlyIn(Dist.CLIENT)
    override fun getFogColor(): Vector3 {
        val night: Float = this.getStarBrightness(1.0f)
        val day: Float = 1.0f - this.getStarBrightness(1.0f)
        val dayColR: Float = 203.0f / 255.0f
        val dayColG: Float = 147.0f / 255.0f
        val dayColB: Float = 0.0f / 255.0f
        val nightColR: Float = 1.0f / 255.0f
        val nightColG: Float = 1.0f / 255.0f
        val nightColB: Float = 1.0f / 255.0f
        return Vector3(
            (dayColR * day + nightColR * night).toDouble(),
            (dayColG * day + nightColG * night).toDouble(),
            (dayColB * day + nightColB * night).toDouble()
        )
    }

    @OnlyIn(Dist.CLIENT)
    override fun getSkyColor(): Vector3 {
        val night: Float = this.getStarBrightness(1.0f)
        val day: Float = 1.0f - this.getStarBrightness(1.0f)
        val dayColR: Float = 105.0f / 255.0f
        val dayColG: Float = 107.0f / 255.0f
        val dayColB: Float = 281.0f / 255.0f
        val nightColR: Float = 2.0f / 255.0f
        val nightColG: Float = 2.0f / 255.0f
        val nightColB: Float = 2.0f / 255.0f
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
        return 24000L
    }

    override fun isDaytime(): Boolean {
        val a: Float = this.world.getCelestialAngle(0f)
        //TODO: adjust this according to size of planet below
        return a < 0.42f || a > 0.58f
    }

    @OnlyIn(Dist.CLIENT)
    override fun getStarBrightness(par1: Float): Float {
        val var2: Float = this.world.getCelestialAngle(par1)
        var var3: Float = 1.0f - (MathHelper.cos(var2 * Constants.twoPI) * 2.0f + 0.25f)

        if (var3 < 0.0f) {
            var3 = 0.0f
        }

        if (var3 > 1.0f) {
            var3 = 1.0f
        }

        return var3 * var3 * 0.5f + 0.2f
    }

    override fun isSkyColored(): Boolean {
        return false
    }

    override fun getHorizon(): Double {
        return 44.0
    }

    override fun getAverageGroundLevel(): Int {
        return 64
    }

    override fun canCoordinateBeSpawn(var1: Int, var2: Int): Boolean {
        return true
    }

    override fun getCelestialBody(): CelestialBody? {
        return SolarSystemBodies.venusSpaceStation
    }

    override fun getGravity(): Float {
        return 0.068f
    }

    override fun getMeteorFrequency(): Double {
        return 0.0
    }

    override fun getFuelUsageMultiplier(): Double {
        return 0.5
    }

    override fun getPlanetToOrbit(): String? {
        return (VenusModule.planetVenus)!!.getTranslationKey()
    }

    override fun getYCoordToTeleportToPlanet(): Int {
        return 30
    }

    override fun getChunkProviderClass(): Class<out IChunkGenerator> {
        return ChunkProviderVenusSS::class.java
    }

    override fun getSaveFolder(): String? {
        return "DIM_SPACESTATION_VENUS" + this.getDimension()
    }

    override fun getSolarEnergyMultiplier(): Double {
        return ConfigManagerCore.spaceStationEnergyScalar
    }

    override fun getYCoordinateToTeleport(): Double {
        return 1000.0
    }

    override fun canSpaceshipTierPass(tier: Int): Boolean {
        return tier > 0
    }

    override fun getFallDamageModifier(): Float {
        return 0.4f
    }

    override fun updateWeather() {
        super.updateWeather()
    }

    @OnlyIn(Dist.CLIENT)
    public override fun setSpinDeltaPerTick(angle: Float) {
        val skyProvider: SkyProviderVenusSS? = (this.getSkyRenderer() as SkyProviderVenusSS?)
        if (skyProvider != null) skyProvider.spinDeltaPerTick = angle
    }

    @OnlyIn(Dist.CLIENT)
    override fun getSkyRotation(): Float {
        val skyProvider: SkyProviderVenusSS? = (this.getSkyRenderer() as SkyProviderVenusSS?)
        return skyProvider!!.spinAngle
    }

    @OnlyIn(Dist.CLIENT)
    public override fun createSkyProvider() {
        this.setSkyRenderer(SkyProviderVenusSS())
        this.setSpinDeltaPerTick(this.getSpinManager().getSpinRate())

        if (this.getCloudRenderer() == null) this.setCloudRenderer(super.getCloudRenderer())
    }

    override fun getDungeonSpacing(): Int {
        return 0
    }

    override fun getDungeonChestType(): ResourceLocation? {
        return RoomTreasure.MOONCHEST
    }

    override fun getSurfaceBlocks(): MutableList<Block?>? {
        return null
    }

    override fun getBiomeProviderClass(): Class<out BiomeProvider> {
        return BiomeProviderCeres::class.java
    }

    @OnlyIn(Dist.CLIENT)
    override fun getCloudHeight(): Float {
        return 10.0f
    }

    override fun getThermalLevelModifier(): Float {
        val angle: Float = this.world.getCelestialAngle(this.getDayLength().toFloat())
        var value: Float = MathHelper.cos(angle * Math.PI.toFloat() * 2.0f) * getThermalValueMod()

        val def: Float = this.getCelestialBody()!!.atmosphere.thermalLevel()
        if (def < 0.0) value *= -1f

        return if (def == 0f) value else value * def + def
    }

    protected open fun getThermalValueMod(): Float {
        return 0.5f
    }
}
