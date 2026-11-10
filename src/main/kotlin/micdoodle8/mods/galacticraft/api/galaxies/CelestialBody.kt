package micdoodle8.mods.galacticraft.api.galaxies

import net.minecraft.world.biome.Biome.SpawnListEntry

import org.apache.commons.lang3.builder.EqualsBuilder
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeAdaptive
import micdoodle8.mods.galacticraft.api.world.AtmosphereInfo
import micdoodle8.mods.galacticraft.api.world.BiomeGenBaseGC
import micdoodle8.mods.galacticraft.api.world.EnumAtmosphericGas
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.api.world.IMobSpawnBiome
import net.minecraft.block.Block
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.translation.I18n
import net.minecraft.world.WorldProvider
import net.minecraft.world.biome.Biome
import java.lang.Exception
import java.util.ArrayList
import java.util.Arrays
import java.util.LinkedList

abstract class CelestialBody(@JvmField protected var unlocalizedName: String) : Comparable<CelestialBody> {
    @JvmField
    protected val bodyName: String

    @JvmField
    protected var relativeSize: Float = 1.0f

    @JvmField
    protected var relativeDistanceFromCenter: ScalableDistance = ScalableDistance(1.0f, 1.0f)

    @JvmField
    protected var relativeOrbitTime: Float = 1.0f

    @JvmField
    protected var phaseShift: Float = 0.0f

    @JvmField
    protected var dimensionID: Int = -1

    @JvmField
    protected var providerClass: Class<out WorldProvider>? = null

    @JvmField
    protected var dimensionSuffix: String? = null

    @JvmField
    protected var autoRegisterDimension: Boolean = false

    @JvmField
    protected var isReachable: Boolean = false

    @JvmField
    protected var forceStaticLoad: Boolean = true

    @JvmField
    protected var tierRequired: Int = 0

    @JvmField
    var atmosphere: AtmosphereInfo = AtmosphereInfo(false, false, false, 0.0f, 0.0f, 1.0f)

    @JvmField
    protected var biomeInfo: LinkedList<Biome?>? = null

    @JvmField
    var biomesToGenerate: LinkedList<Biome?>? = null

    @JvmField
    var biomesToAdapt: Array<BiomeGenBaseGC?>? = null

    @JvmField
    protected var mobInfo: LinkedList<Biome.SpawnListEntry?>? = null

    @JvmField
    protected var celestialBodyIcon: ResourceLocation? = null

    @JvmField
    protected var ringColorR: Float = 1.0f

    @JvmField
    protected var ringColorG: Float = 1.0f

    @JvmField
    protected var ringColorB: Float = 1.0f

    @JvmField
    protected var checklistKeys: ArrayList<String?> = ArrayList<String?>()

    init {
        this.bodyName = unlocalizedName.lowercase()
    }

    abstract fun getID(): Int

    abstract fun getUnlocalizedNamePrefix(): String?

    fun getName(): String {
        return this.bodyName
    }

    fun getTranslationKey(): String {
        return this.getUnlocalizedNamePrefix() + "." + this.unlocalizedName
    }

    fun getLocalizedName(): String {
        var s: String? = this.getTranslationKey()
        s = if (s == null) "" else I18n.translateToLocal(s)
        val comment: Int = (s)!!.indexOf('#')
        return if (comment > 0) (s)!!.substring(0, comment).trim { it <= ' ' } else (s)!!
    }

    /**
     * Used for rendering planet's location on the map.
     *
     *
     * Value of 2.0F would result in the planet being rendered twice as large as
     * earth.
     *
     * @return Size of the planet/moon relative to earth.
     */
    fun getRelativeSize(): Float {
        return this.relativeSize
    }

    /**
     * Used for rendering planet's location on the map.
     *
     *
     * Value of 2.0F would result in an ellipse with twice the radius of the
     * overworld.
     *
     * @return Distance from the center of the map relative to earth.
     */
    fun getRelativeDistanceFromCenter(): ScalableDistance {
        return this.relativeDistanceFromCenter
    }

    /**
     * Used for rendering planet's location on the map.
     *
     *
     * Value of 1π would result in the planet being rendered directly
     * accross from the original position
     *
     *
     * Value of 2π is a full rotation and therefore would be rendered at
     * the same spot as the original position
     *
     * @return Phase shift of planet for planet's revolution around the sun.
     */
    fun getPhaseShift(): Float {
        return this.phaseShift
    }

    /**
     * Multiplier for length of time relative to earth that this planet takes to
     * orbit fully.
     *
     *
     * Value of 2.0F would result in the planet rotating twice as slow (and
     * therefore take twice as long) as the earth takes to revolve around the
     * sun.
     *
     * @return Multiple value for planet's revolution around the sun.
     */
    fun getRelativeOrbitTime(): Float {
        return this.relativeOrbitTime
    }

    fun getTierRequirement(): Int {
        return this.tierRequired
    }

    fun setTierRequired(tierRequired: Int): CelestialBody {
        this.tierRequired = tierRequired
        return this
    }

    fun setRelativeSize(relativeSize: Float): CelestialBody {
        this.relativeSize = relativeSize
        return this
    }

    fun setRelativeDistanceFromCenter(relativeDistanceFromCenter: ScalableDistance): CelestialBody {
        this.relativeDistanceFromCenter = relativeDistanceFromCenter
        return this
    }

    fun setPhaseShift(phaseShift: Float): CelestialBody {
        this.phaseShift = phaseShift
        return this
    }

    fun setRelativeOrbitTime(relativeOrbitTime: Float): CelestialBody {
        this.relativeOrbitTime = relativeOrbitTime
        return this
    }

    fun setAtmosphere(atmos: AtmosphereInfo): CelestialBody {
        this.atmosphere = atmos
        return this
    }

    fun setDimensionInfo(dimID: Int, providerClass: Class<out WorldProvider>?): CelestialBody {
        return this.setDimensionInfo(dimID, providerClass, true)
    }

    open fun setDimensionInfo(
        providerId: Int,
        providerClass: Class<out WorldProvider>?,
        autoRegister: Boolean
    ): CelestialBody {
        this.dimensionID = providerId
        this.providerClass = providerClass
        this.autoRegisterDimension = autoRegister
        this.isReachable = true
        return this
    }

    fun shouldAutoRegister(): Boolean {
        return this.autoRegisterDimension
    }

    fun getDimensionID(): Int {
        return this.dimensionID
    }

    fun getWorldProvider(): Class<out WorldProvider>? {
        return this.providerClass
    }

    fun getReachable(): Boolean {
        return this.isReachable
    }

    /*
     * Use this to list the atmospheric gases on the celestial body, starting with the most abundant
     * Do not include trace gases (anything less than 0.25%)
     * (Do not use for stars!)
     */
    fun atmosphereComponent(gas: EnumAtmosphericGas?): CelestialBody {
        this.atmosphere.composition.add((gas)!!)
        return this
    }

    fun setRingColorRGB(ringColorR: Float, ringColorG: Float, ringColorB: Float): CelestialBody {
        this.ringColorR = ringColorR
        this.ringColorG = ringColorG
        this.ringColorB = ringColorB
        return this
    }

    fun getRingColorR(): Float {
        return this.ringColorR
    }

    fun getRingColorG(): Float {
        return this.ringColorG
    }

    fun getRingColorB(): Float {
        return this.ringColorB
    }

    fun getBodyIcon(): ResourceLocation? {
        return this.celestialBodyIcon
    }

    fun setBodyIcon(planetIcon: ResourceLocation?): CelestialBody {
        this.celestialBodyIcon = planetIcon
        return this
    }

    fun getForceStaticLoad(): Boolean {
        return this.forceStaticLoad
    }

    fun setForceStaticLoad(force: Boolean): CelestialBody {
        this.forceStaticLoad = force
        return this
    }

    fun addChecklistKeys(vararg keys: String?) {
        this.checklistKeys.addAll(Arrays.asList<String?>(*keys))
    }

    fun getChecklistKeys(): MutableList<String?> {
        return this.checklistKeys
    }

    override fun hashCode(): Int {
        return this.getTranslationKey().hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other is CelestialBody) {
            return EqualsBuilder().append(this.getTranslationKey(), other.getTranslationKey()).isEquals()
        }

        return false
    }

    override fun compareTo(other: CelestialBody): Int {
        val thisDistance: ScalableDistance = this.getRelativeDistanceFromCenter()
        val otherDistance: ScalableDistance = other.getRelativeDistanceFromCenter()
        return if (otherDistance.unScaledDistance < thisDistance.unScaledDistance) 1 else (if (otherDistance.unScaledDistance > thisDistance.unScaledDistance) -1 else 0)
    }

    open class ScalableDistance
    constructor(@JvmField val unScaledDistance: Float, @JvmField val scaledDistance: Float)

    fun setUnreachable() {
        this.isReachable = false
    }

    fun getDimensionSuffix(): String? {
        return dimensionSuffix
    }

    fun setDimensionSuffix(dimensionSuffix: String?) {
        this.dimensionSuffix = dimensionSuffix
    }

    fun setBiomeInfo(vararg biomes: Biome?) {
        this.biomeInfo = LinkedList<Biome?>()
        this.biomesToGenerate = LinkedList<Biome?>()
        val adaptiveBiomes: LinkedList<BiomeGenBaseGC?> = LinkedList<BiomeGenBaseGC?>()
        var index: Int = 0
        for (b: Biome? in biomes) {
            this.biomeInfo!!.add(b)
            if (b is BiomeGenBaseGC && b.isAdaptiveBiome) {
                this.biomesToGenerate!!.add(BiomeAdaptive.register(index++, b))
                adaptiveBiomes.add(b)
            } else {
                this.biomesToGenerate!!.add(b)
            }
        }
        this.biomesToAdapt = adaptiveBiomes.toTypedArray<BiomeGenBaseGC?>()
    }

    fun getBiomes(): MutableList<Biome?>? {
        return this.biomeInfo
    }

    fun addMobInfo(entry: Biome.SpawnListEntry?) {
        if (this.mobInfo == null) {
            this.mobInfo = LinkedList<Biome.SpawnListEntry?>()
        }
        this.mobInfo!!.add(entry)
    }

    fun initialiseMobSpawns() {
        if (this.biomeInfo != null && this.mobInfo != null) {
            for (biome: Biome? in (this.biomeInfo)!!) {
                if (biome is IMobSpawnBiome) {
                    (biome as IMobSpawnBiome).initialiseMobLists(this.mobInfo)
                }
            }
        }
    }

    fun getSurfaceBlocks(): MutableList<Block?>? {
        if (this.providerClass != null && IGalacticraftWorldProvider::class.java.isAssignableFrom(this.providerClass)) {
            try {
                return (this.providerClass!!.newInstance() as IGalacticraftWorldProvider).getSurfaceBlocks()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }
}
