package asmodeuscore.core.astronomy.dimension.world.worldengine

import asmodeuscore.core.astronomy.dimension.world.worldengine.WE_WorldProviderSpace
import asmodeuscore.core.utils.worldengine.WE_WorldProvider
import asmodeuscore.api.dimension.IAdvancedSpace
import asmodeuscore.api.dimension.IProviderFreeze
import asmodeuscore.core.astronomy.dimension.world.worldengine.WE_ChunkProviderSpace
import asmodeuscore.api.space.IExBody
import galaxyspace.core.configs.GSConfigCore
import micdoodle8.mods.galacticraft.api.galaxies.IChildBody
import micdoodle8.mods.galacticraft.api.galaxies.Moon
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeAdaptive
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeProviderDefault
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.api.world.EnumAtmosphericGas
import micdoodle8.mods.galacticraft.api.world.IExitHeight
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.api.world.ISolarLevel
import micdoodle8.mods.galacticraft.core.event.EventHandlerGC
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.JavaUtil
import net.minecraft.world.level.block.Block
import net.minecraft.command.CommandTime
import net.minecraft.world.entity.Entity
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.util.Mth as MathHelper
import net.minecraft.world.phys.Vec3 as Vec3d
import net.minecraft.village.VillageCollection
import net.minecraft.world.level.Level as World
import net.minecraft.world.biome.BiomeProvider
import net.minecraft.world.level.chunk.LevelChunk as Chunk
import net.minecraft.world.chunk.ChunkPrimer
import net.minecraft.world.gen.IChunkGenerator
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.lang.Exception
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import kotlin.math.cos

abstract class WE_WorldProviderSpace : WE_WorldProvider(), IGalacticraftWorldProvider, ISolarLevel, IExitHeight,
    IAdvancedSpace, IProviderFreeze {
    private var timeCurrentOffset = 0L

    @JvmField
    var preTickTime: Long = Long.MIN_VALUE
    private var saveTCO = 0L
    override fun init() {
        //super.init();

        this.hasSkyLight = true
        if (this.getBiomeProviderClass() != null) {
            try {
                val biomeProviderClass: Class<out BiomeProvider> = this.getBiomeProviderClass()

                val constructors: Array<Constructor<*>> = biomeProviderClass.getConstructors()
                for (constr: Constructor<*> in constructors) {
                    println(constr.toString())
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

        println("Init WE for: " + (this.getCelestialBody())!!.getName())
        println("Biome Provider: " + this.biomeProvider.toString())
    }

    override fun createChunkGenerator(): IChunkGenerator {
        println("WorldEngine: -Starting WorldEngine...")
        chunk_provider = WE_ChunkProviderSpace(this)
        println("WorldEngine: -WorldEngine started successfully!")
        //-//
        return (chunk_provider)!!
    }

    override fun updateWeather() {
        if (!this.world.isRemote) {
            val newTime: Long = world.getWorldInfo().getWorldTime()
            if (this.preTickTime == Long.MIN_VALUE) {
                // First tick: get the timeCurrentOffset from saved ticks in villages.dat :)
                var savedTick: Int = 0
                try {
                    tickCounter!!.setAccessible(true)
                    savedTick = tickCounter!!.getInt(this.world.villageCollection)
                    if (savedTick < 0) savedTick = 0
                } catch (ignore: Exception) {
                }
                this.timeCurrentOffset = savedTick - newTime
            } else {
                // Detect jumps in world time (e.g. because of bed use on Overworld) and reverse
                // them for this world
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

    override fun getGravity(): Float {
        if (this.getCelestialBody() is IExBody) return (this.getCelestialBody() as IExBody).getGravity()

        return 0.058f
    }

    override fun getThermalLevelModifier(): Float {
        if (enableAdvancedThermalLevel() && this.world != null) {
            val angle: Float = this.world.getCelestialAngle(this.getDayLength().toFloat())
            var value: Float = MathHelper.cos(angle * Math.PI.toFloat() * 2.0f) * getThermalValueMod()

            val def: Float = (this.getCelestialBody())!!.atmosphere.thermalLevel()
            if (def < 0.0) value *= -1f

            if (value * def + def < -9.102f) return -9.102f

            return if (def == 0f) value else value * def + def
        }
        return (this.getCelestialBody())!!.atmosphere.thermalLevel()
    }

    override fun canSpaceshipTierPass(tier: Int): Boolean {
        return tier >= (this.getCelestialBody())!!.getTierRequirement()
    }

    fun getSolarSizeForMoon(): Float {
        return 1.0f / ((this.getCelestialBody() as Moon).getParentPlanet())!!
            .getRelativeDistanceFromCenter().unScaledDistance
    }

    override fun getSolarEnergyMultiplier(): Double {
        var solarMultiplier: Double = -1.0
        if (solarMultiplier < 0.0) {
            val s: Double =
                (if (this.getCelestialBody() is IChildBody) this.getSolarSizeForMoon() else this.getSolarSize()).toDouble()
            solarMultiplier += s * s * ConfigManagerCore.spaceStationEnergyScalar
        }
        return solarMultiplier
    }

    override fun getSolarWindMultiplier(): Double {
        var solarMultiplier: Double = -1.0
        if (solarMultiplier < 0.0) {
            val s: Double =
                (if (this.getCelestialBody() is IChildBody) this.getSolarSizeForMoon() else this.getSolarSize()).toDouble()
            solarMultiplier = s * s
        }
        return solarMultiplier
    }

    override fun getYCoordinateToTeleport(): Double {
        return 1000.0
    }

    override fun getMoonPhase(time: Long): Int {
        val timeday: Long = if (this.getDayLength() > 0) this.getDayLength() else 1
        return (time / timeday % 8L + 8L).toInt() % 8
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
        val d: Float = (this.getCelestialBody())!!.atmosphere.relativeDensity()
        if (d <= 0.0f) {
            return 20.0f
        }
        if (d > 5.0f) {
            return 0.2f
        }
        return 1.0f / d
    }

    override fun getMeteorFrequency(): Double {
        val d: Float = (this.getCelestialBody())!!.atmosphere.relativeDensity()
        if (d <= 0.0f) {
            return 5.0
        }
        return d * 100.0
    }

    override fun getWindLevel(): Float {
        return (this.getCelestialBody())!!.atmosphere.windLevel()
    }

    override fun shouldCorrodeArmor(): Boolean {
        return (this.getCelestialBody())!!.atmosphere.isCorrosive()
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

    open fun enableAdvancedThermalLevel(): Boolean {
        return GSConfigCore.enableAdvancedThermalSystem
    }

    protected open fun getThermalValueMod(): Float {
        return 0.5f
    }

    fun getDayLength(): Long {
        if (this.getCelestialBody() is IExBody) return (this.getCelestialBody() as IExBody).getDayLength()

        return 24000L
    }

    override fun calculateCelestialAngle(par1: Long, par3: Float): Float {
        var par1: Long = par1
        par1 = this.getWorldTime()
        var dayLen: Long = this.getDayLength()
        if (dayLen == 0L) dayLen = 1
        val j: Int = (par1 % dayLen).toInt()
        var f1: Float = (j + par3) / dayLen - 0.25f

        if (f1 < 0.0f) {
            ++f1
        }

        if (f1 > 1.0f) {
            --f1
        }

        val f2: Float = 1.0f - ((cos(f1.toDouble() * Math.PI) + 1.0) / 2.0).toFloat()
        f1 = f1 + (f2 - f1) / 3.0f
        return f1
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
        return if (this.world == null) false else true //this.world.isRemote;
    }

    override fun canRespawnHere(): Boolean {
        if (EventHandlerGC.bedActivated) {
            EventHandlerGC.bedActivated = false
            return true
        }
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

    open fun shouldForceRespawn(): Boolean {
        return !ConfigManagerCore.forceOverworldRespawn
    }

    override fun netherPortalsOperational(): Boolean {
        return false
    }

    override fun getArrowGravity(): Float {
        return 0.005f
    }

    open fun getBiomeProviderClass(): Class<out BiomeProvider> {
        return WE_BiomeProvider::class.java
    }

    override fun getBiomeProvider(): BiomeProvider {
        if (this.getBiomeProviderClass() == null) {
            BiomeAdaptive.setBody((this.getCelestialBody())!!)
        }
        return biomeProvider
    }

    override fun shouldMapSpin(entity: String, x: Double, y: Double, z: Double): Boolean {
        return false
    }

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

    private fun saveTime() {
        try {
            val vc: VillageCollection = this.world.villageCollection
            tickCounter!!.setAccessible(true)
            tickCounter!!.setInt(vc, (this.getWorldTime()).toInt())
            vc.markDirty()
        } catch (ignore: Exception) {
        }
    }

    protected fun updateWeatherOverride() {
        super.updateWeather()
    }

    override fun getSurfaceBlocks(): MutableList<Block?>? {
        return null
    }

    abstract fun hasSunset(): Boolean
    abstract fun getFogColor(): Vector3
    abstract fun getSkyColor(): Vector3
    abstract fun getChunkProviderClass(): Class<out IChunkGenerator>?

    abstract override fun onChunkProvider(cX: Int, cZ: Int, primer: ChunkPrimer?)
    abstract override fun onPopulate(cX: Int, cZ: Int)
    abstract override fun recreateStructures(chunkIn: Chunk?, x: Int, z: Int)

    companion object {
        @JvmField
        var tickCounter: Field? = null

        init {
            try {
                tickCounter = VillageCollection::class.java
                    .getDeclaredField(if (GCCoreUtil.isDeobfuscated()) "tickCounter" else "field_75553_e")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
