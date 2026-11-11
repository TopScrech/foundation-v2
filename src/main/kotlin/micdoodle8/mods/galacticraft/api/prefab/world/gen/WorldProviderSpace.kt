package micdoodle8.mods.galacticraft.api.prefab.world.gen

import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.api.world.EnumAtmosphericGas
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.JavaUtil
import net.minecraft.command.CommandTime
import net.minecraft.world.entity.Entity
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth as MathHelper
import net.minecraft.world.phys.Vec3 as Vec3d
import net.minecraft.village.VillageCollection
import net.minecraft.world.level.Level as World
import net.minecraft.world.WorldProvider
import net.minecraft.world.biome.BiomeProvider
import net.minecraft.world.level.chunk.LevelChunk as Chunk
import net.minecraft.world.gen.IChunkGenerator
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.lang.Exception
import java.lang.reflect.Constructor
import java.lang.reflect.Field

abstract class WorldProviderSpace : WorldProvider(), IGalacticraftWorldProvider {
    private var timeCurrentOffset = 0L

    @JvmField
    var preTickTime: Long = Long.MIN_VALUE
    private var saveTCO = 0L

    /**
     * The fog color in this dimension
     */
    abstract fun getFogColor(): Vector3

    /**
     * The sky color in this dimension
     */
    abstract fun getSkyColor(): Vector3

    /**
     * Whether or not there will be rain or snow in this dimension
     *
     */
    @Deprecated("Use new shouldDisablePrecipitation method in IGalacticraftWorldProvider interface")
    open fun canRainOrSnow(): Boolean {
        return false
    }

    /**
     * Whether or not to render vanilla sunset (can be overridden with custom sky provider)
     */
    abstract fun hasSunset(): Boolean

    /**
     * The length of day in this dimension
     *
     *
     * Default: 24000
     */
    abstract fun getDayLength(): Long

    abstract fun getChunkProviderClass(): Class<out IChunkGenerator>

    @Deprecated("")
    /**
     * If possible you should not override this so that BiomeProviderDefault is used (see Moon, Mars and Asteroids for examples)
     *
     * But this can be used for planets where you have multiple biomes
     * In that case if using BiomeAdaptive (see Venus for example) this will need a BiomeAdaptive.setBodyMultiBiome() call;
     */
    open fun getBiomeProviderClass(): Class<out BiomeProvider>? {
        return null
    }

    override fun setDimension(var1: Int) {
        super.setDimension(var1)
    }

    override fun updateWeather() {
        if (!this.world.isRemote) {
            val newTime: Long = world.getWorldInfo().getWorldTime()
            if (this.preTickTime == Long.MIN_VALUE) {
                //First tick: get the timeCurrentOffset from saved ticks in villages.dat :)
                var savedTick: Int = 0
                try {
                    tickCounter!!.setAccessible(true)
                    savedTick = tickCounter!!.getInt(this.world.villageCollection)
                    if (savedTick < 0) savedTick = 0
                } catch (ignore: Exception) {
                }
                this.timeCurrentOffset = savedTick - newTime
            } else {
                //Detect jumps in world time (e.g. because of bed use on Overworld) and reverse them for this world
                val diff: Long = (newTime - this.preTickTime)
                if (diff > 1L) {
                    this.timeCurrentOffset -= diff - 1L
                    this.saveTime()
                }
            }
            this.preTickTime = newTime
            this.saveTCO = 0L
        }

        if (this.shouldDisablePrecipitation()) {
            this.world.getWorldInfo().setRainTime(0)
            this.world.getWorldInfo().setRaining(false)
            this.world.getWorldInfo().setThunderTime(0)
            this.world.getWorldInfo().setThundering(false)
            this.world.rainingStrength = 0.0f
            this.world.thunderingStrength = 0.0f
        } else {
            this.updateWeatherOverride()
        }
    }

    /*
     * Override this to circumvent vanilla updateWeather()
     */
    protected open fun updateWeatherOverride() {
        super.updateWeather()
    }

    override fun getSaveFolder(): String? {
        return "DIM" + (this.getCelestialBody())!!.getDimensionID()
    }

    override fun isGasPresent(gas: EnumAtmosphericGas?): Boolean {
        return (this.getCelestialBody())!!.atmosphere.isGasPresent(gas)
    }

    override fun hasNoAtmosphere(): Boolean {
        return (this.getCelestialBody())!!.atmosphere.hasNoGases()
    }

    override fun hasBreathableAtmosphere(): Boolean {
        return (this.getCelestialBody())!!.atmosphere.isBreathable()
    }

    override fun shouldDisablePrecipitation(): Boolean {
        return !(this.getCelestialBody())!!.atmosphere.hasPrecipitation()
    }

    override fun getSoundVolReductionAmount(): Float {
        return foundation.level.FoundationAtmosphere.soundReduction(this.getCelestialBody()!!.atmosphere.relativeDensity())
    }

    override fun getMeteorFrequency(): Double {
        return foundation.level.FoundationAtmosphere.meteorFrequency(this.getCelestialBody()!!.atmosphere.relativeDensity())
    }

    override fun getThermalLevelModifier(): Float {
        return (this.getCelestialBody())!!.atmosphere.thermalLevel()
    }

    override fun getWindLevel(): Float {
        return (this.getCelestialBody())!!.atmosphere.windLevel()
    }

    override fun shouldCorrodeArmor(): Boolean {
        return (this.getCelestialBody())!!.atmosphere.isCorrosive()
    }

    override fun canBlockFreeze(pos: BlockPos, byWater: Boolean): Boolean {
        //TODO: if block is water and world temperature is low, freeze
        return super.canBlockFreeze(pos, byWater)
    }

    override fun canDoLightning(chunk: Chunk): Boolean {
        return !this.shouldDisablePrecipitation()
    }

    override fun canDoRainSnowIce(chunk: Chunk): Boolean {
        return !this.shouldDisablePrecipitation()
    }

    override fun getSolarSize(): Float {
        return 1.0f / (this.getCelestialBody())!!.getRelativeDistanceFromCenter().unScaledDistance
    }

    override fun calcSunriseSunsetColors(var1: Float, var2: Float): FloatArray? {
        return if (this.hasSunset()) super.calcSunriseSunsetColors(var1, var2) else null
    }

    override fun calculateCelestialAngle(par1: Long, par3: Float): Float {
        return foundation.level.FoundationPlanetClocks.celestialAngle(this.getWorldTime(), par3, this.getDayLength())
    }

    @OnlyIn(Dist.CLIENT)
    override fun getFogColor(var1: Float, var2: Float): Vec3d {
        val fogColor: Vector3 = this.getFogColor()
        return Vec3d(fogColor.floatX().toDouble(), fogColor.floatY().toDouble(), fogColor.floatZ().toDouble())
    }

    override fun getSkyColor(cameraEntity: Entity, partialTicks: Float): Vec3d {
        val skyColor: Vector3 = this.getSkyColor()
        return Vec3d(skyColor.floatX().toDouble(), skyColor.floatY().toDouble(), skyColor.floatZ().toDouble())
    }

    override fun isSkyColored(): Boolean {
        return true
    }

    /**
     * Do not override this.
     *
     * Returns true on clients (to allow rendering of sky etc, maybe even clouds).
     * Returns false on servers (to disable Nether Portal mob spawning and sleeping in beds).
     */
    override fun isSurfaceWorld(): Boolean {
        return if (this.world == null) false else this.world.isRemote
    }

    /**
     * This must normally return false, so that if the dimension is set for 'static' loading
     * it will not keep chunks around the dimension spawn position permanently loaded.
     * It is also needed to be false so that the 'Force Overworld Respawn' setting in core.conf
     * will work correctly - see also WorldProviderS[ace.getRespawnDimension().
     *
     * But: returning 'false' will cause beds to explode in this dimension.
     * If you want beds NOT to explode, you can override this, like in WorldProviderMoon.
     */
    override fun canRespawnHere(): Boolean {
        return false
    }

    /**
     * Do NOT override this in your add-ons.
     *
     * This controls whether the player will respawn in the space dimension or the Overworld
     * in accordance with the 'Force Overworld Respawn' setting on core.conf.
     */
    override fun getRespawnDimension(player: EntityPlayerMP): Int {
        return if (this.shouldForceRespawn()) this.getDimension() else 0
    }

    /**
     * If true, the the player should respawn in this dimension upon death.
     *
     * Obeying the 'Force Overworld Respawn' setting from core.conf is an important protection
     * for players are endlessly dying in a space dimension: for example respawning
     * in an airless environment with no oxygen tanks and no oxygen machinery.
     */
    open fun shouldForceRespawn(): Boolean {
        return !ConfigManagerCore.forceOverworldRespawn
    }

    /**
     * If false (the default) then Nether Portals will have no function on this world.
     * Nether Portals can still be constructed, if the player can make fire, they just
     * won't do anything.
     *
     * @return True if Nether Portals should work like on the Overworld.
     */
    override fun netherPortalsOperational(): Boolean {
        return false
    }

    override fun getArrowGravity(): Float {
        return 0.005f
    }

    override fun createChunkGenerator(): IChunkGenerator? {
        try {
            val chunkProviderClass: Class<out IChunkGenerator> = this.getChunkProviderClass()

            val constructors: Array<Constructor<*>> = chunkProviderClass.getConstructors()
            for (i: Int in constructors.indices) {
                val constr: Constructor<*> = constructors[i]
                if (constr.getParameterTypes().contentEquals(
                        arrayOf<Any>(
                            World::class.java,
                            Long::class.javaPrimitiveType!!,
                            Boolean::class.javaPrimitiveType!!
                        )
                    )
                ) {
                    return constr.newInstance(
                        this.world,
                        this.world.getSeed(),
                        this.world.getWorldInfo().isMapFeaturesEnabled()
                    ) as IChunkGenerator
                } else if (constr.getParameterTypes().size == 0) {
                    return constr.newInstance() as IChunkGenerator
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    override fun init() {
        this.hasSkyLight = true

        if (this.getBiomeProviderClass() != null) {
            try {
                val biomeProviderClass: Class<out BiomeProvider>? = this.getBiomeProviderClass()

                val constructors: Array<Constructor<*>> = biomeProviderClass!!.getConstructors()
                for (constr: Constructor<*> in constructors) {
                    if (constr.getParameterTypes().contentEquals(arrayOf<Any>(World::class.java))) {
                        this.biomeProvider = constr.newInstance(this.world) as BiomeProvider?
                    } else if (constr.getParameterTypes().size == 0) {
                        this.biomeProvider = constr.newInstance() as BiomeProvider?
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            this.biomeProvider = BiomeProviderDefault(this.getCelestialBody())
        }
    }

    override fun getBiomeProvider(): BiomeProvider {
        if (this.getBiomeProviderClass() == null) {
            BiomeAdaptive.Companion.setBody((this.getCelestialBody())!!)
        }
        return biomeProvider
    }

    override fun shouldMapSpin(entity: String, x: Double, y: Double, z: Double): Boolean {
        return false
    }

    //Work around vanilla feature: worlds which are not the Overworld cannot change the time, as the worldInfo is a DerivedWorldInfo
    //Therefore each Galacticraft dimension maintains its own timeCurrentOffset
    override fun setWorldTime(time: Long) {
        world.getWorldInfo().setWorldTime(time)
        if (!world.isRemote) {
            if (JavaUtil.instance.isCalledBy(CommandTime::class.java)) {
                this.timeCurrentOffset = this.saveTCO
                this.saveTime()
                this.preTickTime = time
            } else {
                val newTCO: Long = time - world.getWorldInfo().getWorldTime()
                val diff: Long = newTCO - this.timeCurrentOffset
                if (diff > 1L || diff < -1L) {
                    this.timeCurrentOffset = newTCO
                    this.saveTime()
                    this.preTickTime = time
                }
            }
            this.saveTCO = 0
        }
    }

    override fun getWorldTime(): Long {
        if (JavaUtil.instance.isCalledBy(CommandTime::class.java)) {
            this.saveTCO = this.timeCurrentOffset
        }
        return world.getWorldInfo().getWorldTime() + this.timeCurrentOffset
    }

    /**
     * Adjust time offset on Galacticraft worlds when the Overworld time jumps and you don't want the time
     * on all the other Galacticraft worlds to jump also - see WorldUtil.setNextMorning() for example
     */
    fun adjustTimeOffset(diff: Long) {
        if (diff != 0L) {
            this.timeCurrentOffset -= diff
            this.preTickTime += diff
            this.saveTime()
        }
    }

    fun adjustTime(newTime: Long) {
        val diff: Long = newTime - this.preTickTime
        if (diff != 0L) {
            this.timeCurrentOffset -= diff
            this.preTickTime = newTime
            this.saveTime()
        }
    }

    /**
     * Save this world's custom time (from timeCurrentOffset) into this world's villages.dat :)
     */
    private fun saveTime() {
        try {
            val vc: VillageCollection = this.world.villageCollection
            tickCounter!!.setAccessible(true)
            tickCounter!!.setInt(vc, (this.getWorldTime()).toInt())
            vc.markDirty()
        } catch (ignore: Exception) {
        }
    }

    companion object {
        @JvmField
        var tickCounter: Field? = null

        init {
            try {
                tickCounter =
                    VillageCollection::class.java.getDeclaredField(if (GCCoreUtil.isDeobfuscated()) "tickCounter" else "field_75553_e")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
