package micdoodle8.mods.galacticraft.core.dimension

import java.util.HashSet

import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.api.world.IExitHeight
import micdoodle8.mods.galacticraft.api.world.IOrbitDimension
import micdoodle8.mods.galacticraft.api.world.ISolarLevel
import micdoodle8.mods.galacticraft.api.world.IZeroGDimension
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.client.CloudRenderer
import micdoodle8.mods.galacticraft.core.client.SkyProviderOrbit
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.world.gen.dungeon.RoomTreasure
import net.minecraft.world.level.block.Block
import net.minecraft.world.entity.Entity
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.util.Mth as MathHelper
import net.minecraft.world.level.dimension.DimensionType
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class WorldProviderOverworldOrbit : WorldProviderSpaceStation(), IOrbitDimension, IZeroGDimension, ISolarLevel,
    IExitHeight {
    @JvmField
    var freefallingEntities: MutableSet<Entity?> = HashSet<Entity?>()

    override fun getDimensionType(): DimensionType {
        return (GCDimensions.ORBIT)!!
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

    //	@Override
    //	public String getWelcomeMessage()
    //	{
    //		return "Entering Earth Orbit";
    //	}
    //
    //	@Override
    //	public String getDepartMessage()
    //	{
    //		return "Leaving Earth Orbit";
    //	}
    override fun getCelestialBody(): CelestialBody? {
        return GalacticraftCore.satelliteSpaceStation
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

    override fun getPlanetToOrbit(): String {
        return "overworld"
    }

    override fun getYCoordToTeleportToPlanet(): Int {
        return 30
    }

    override fun getSaveFolder(): String? {
        return "DIM_SPACESTATION" + this.getDimension()
    }

    override fun getSolarEnergyMultiplier(): Double {
        return ConfigManagerCore.spaceStationEnergyScalar
    }

    override fun getYCoordinateToTeleport(): Double {
        return 750.0
    }

    override fun canSpaceshipTierPass(tier: Int): Boolean {
        return tier > 0
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
        val skyProvider: SkyProviderOrbit? = (this.getSkyRenderer() as SkyProviderOrbit?)
        if (skyProvider != null) skyProvider.spinDeltaPerTick = angle
    }

    @OnlyIn(Dist.CLIENT)
    override fun getSkyRotation(): Float {
        val skyProvider: SkyProviderOrbit? = (this.getSkyRenderer() as SkyProviderOrbit?)
        return skyProvider!!.spinAngle
    }

    @OnlyIn(Dist.CLIENT)
    public override fun createSkyProvider() {
        this.setSkyRenderer(
            SkyProviderOrbit(
                ResourceLocation(
                    Constants.ASSET_PREFIX,
                    "textures/gui/celestialbodies/earth.png"
                ), true, true
            )
        )
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
