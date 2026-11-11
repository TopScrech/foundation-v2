package galaxyspace.systems.SolarSystem.satellites.jupiter.dimension

import galaxyspace.systems.SolarSystem.SolarSystemBodies
import galaxyspace.core.util.GSDimensions
import galaxyspace.systems.SolarSystem.satellites.jupiter.dimension.ChunkProviderJupiterSS
import galaxyspace.systems.SolarSystem.planets.ceres.world.gen.BiomeProviderCeres
import galaxyspace.systems.SolarSystem.satellites.mars.dimension.sky.SkyProviderMarsSS
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody
import micdoodle8.mods.galacticraft.api.galaxies.IChildBody
import micdoodle8.mods.galacticraft.api.galaxies.Moon
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.api.world.IExitHeight
import micdoodle8.mods.galacticraft.api.world.IOrbitDimension
import micdoodle8.mods.galacticraft.api.world.ISolarLevel
import micdoodle8.mods.galacticraft.api.world.IZeroGDimension
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.client.CloudRenderer
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderSpaceStation
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.world.gen.dungeon.RoomTreasure
import net.minecraft.world.level.block.Block
import net.minecraft.world.entity.Entity
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.util.Mth as MathHelper
import net.minecraft.world.level.dimension.DimensionType
import net.minecraft.world.biome.BiomeProvider
import net.minecraft.world.gen.IChunkGenerator
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.util.HashSet

open class WorldProviderJupiterSS : WorldProviderSpaceStation(), IOrbitDimension, IZeroGDimension, ISolarLevel,
    IExitHeight {
    @JvmField
    var freefallingEntities: MutableSet<Entity?> = HashSet<Entity?>()


    override fun shouldForceRespawn(): Boolean {
        return false
    }

    override fun getDimensionType(): DimensionType {
        return (GSDimensions.CERES)!!
    }

    override fun getFogColor(): Vector3 {
        return Vector3(0.0, 0.0, 0.0)
    }

    override fun getSkyColor(): Vector3 {
        return Vector3(0.0, 0.0, 0.0)
    }

    override fun hasSunset(): Boolean {
        return false
    }

    override fun getDayLength(): Long {
        return 48000L
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

        return var3 * var3 * 0.5f + 0.3f
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
        return SolarSystemBodies.jupiterSpaceStation
    }

    override fun getGravity(): Float {
        return 0.075f
    }

    override fun getMeteorFrequency(): Double {
        return 0.0
    }

    override fun getFuelUsageMultiplier(): Double {
        return 0.5
    }

    override fun getChunkProviderClass(): Class<out IChunkGenerator> {
        return ChunkProviderJupiterSS::class.java
    }

    override fun getBiomeProviderClass(): Class<out BiomeProvider> {
        return BiomeProviderCeres::class.java
    }

    override fun getPlanetToOrbit(): String {
        return "ceres"
    }

    override fun getYCoordToTeleportToPlanet(): Int {
        return -500
    }

    override fun getSaveFolder(): String? {
        return "DIM_SPACESTATION" + this.getDimension()
    }

    override fun getSolarEnergyMultiplier(): Double {
        var solarMultiplier: Double = -1.0
        if (solarMultiplier < 0.0) {
            val s: Double =
                (if (this.getCelestialBody() is IChildBody) this.getSolarSizeForMoon() else this.getSolarSize()).toDouble()
            solarMultiplier = s * s * s * ConfigManagerCore.spaceStationEnergyScalar
        }
        return solarMultiplier
    }

    open fun getSolarSizeForMoon(): Float {
        return 1.0f / ((this.getCelestialBody() as Moon).getParentPlanet())!!
            .getRelativeDistanceFromCenter().unScaledDistance
    }

    override fun getYCoordinateToTeleport(): Double {
        return 1000.0
    }

    override fun canSpaceshipTierPass(tier: Int): Boolean {
        return tier > 2
    }

    override fun getFallDamageModifier(): Float {
        return 0.4f
    }

    override fun inFreefall(entity: Entity?): Boolean {
        return freefallingEntities.contains(entity)
    }

    override fun setInFreefall(entity: Entity?) {
        freefallingEntities.add(entity)
    }

    override fun updateWeather() {
        freefallingEntities.clear()
        super.updateWeather()
    }

    @OnlyIn(Dist.CLIENT)
    public override fun setSpinDeltaPerTick(angle: Float) {
        val skyProvider: SkyProviderMarsSS? = (this.getSkyRenderer() as SkyProviderMarsSS?)
        if (skyProvider != null) skyProvider.spinDeltaPerTick = angle
    }

    @OnlyIn(Dist.CLIENT)
    override fun getSkyRotation(): Float {
        val skyProvider: SkyProviderMarsSS? = (this.getSkyRenderer() as SkyProviderMarsSS?)
        return skyProvider!!.spinAngle
    }

    @OnlyIn(Dist.CLIENT)
    public override fun createSkyProvider() {
        this.setSkyRenderer(SkyProviderMarsSS())
        this.setSpinDeltaPerTick(this.getSpinManager().getSpinRate())

        if (this.getCloudRenderer() == null) this.setCloudRenderer(CloudRenderer())
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
}
