package galaxyspace.systems.SolarSystem.planets.test.dimension

import galaxyspace.core.GSBlocks
import asmodeuscore.core.astronomy.dimension.world.gen.WorldProviderAdvancedSpace
import galaxyspace.systems.SolarSystem.SolarSystemBodies
import galaxyspace.core.util.GSDimensions
import asmodeuscore.core.utils.worldengine2.world.properties.WE_WorldProperties
import asmodeuscore.core.utils.worldengine2.util.WE_Interpolation
import asmodeuscore.core.utils.worldengine2.world.properties.WE_BiomeProperties
import asmodeuscore.core.utils.worldengine2.world.properties.WE_AbstactProperties.GenReliefConditions.PrimitiveCondition
import asmodeuscore.core.utils.worldengine2.world.biome.WE_Biome
import asmodeuscore.core.utils.worldengine2.world.biome.WE_BiomeProvider
import asmodeuscore.core.utils.worldengine2.world.gen.WE_ChunkProvider
import asmodeuscore.core.utils.worldengine2.world.gen.custom.WE_TerrainGenerator
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody
import micdoodle8.mods.galacticraft.api.vector.Vector3
import net.minecraft.block.Block
import net.minecraft.util.ResourceLocation
import net.minecraft.world.DimensionType
import net.minecraft.world.biome.BiomeProvider
import net.minecraft.world.gen.IChunkGenerator

open class WorldProviderTest_WE2 : WorldProviderAdvancedSpace() {
    private val WEWorldProps = WE_WorldProperties()

    override fun getMeteorFrequency(): Double {
        return 3.0
    }

    override fun getSoundVolReductionAmount(): Float {
        return Float.MAX_VALUE
    }

    override fun getFuelUsageMultiplier(): Double {
        return 1.0
    }

    override fun getFallDamageModifier(): Float {
        return 1f
    }

    override fun getCelestialBody(): CelestialBody? {
        return SolarSystemBodies.planetTest
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

    override fun getFogColor(): Vector3 {
        val f: Float = 1.0f - this.getStarBrightness(1.0f)
        return Vector3((0f / 255f * f).toDouble(), (0f / 255f * f).toDouble(), (0f / 255f * f).toDouble())
    }

    override fun getSkyColor(): Vector3 {
        val f: Float = 1.0f - this.getStarBrightness(1.0f)
        return Vector3((0 / 255.0f * f).toDouble(), (0 / 255.0f * f).toDouble(), (0 / 255.0f * f).toDouble())
    }

    override fun hasSunset(): Boolean {
        return false
    }

    override fun init() {
        this.hasSkyLight = true
        val terraingen: WE_TerrainGenerator = WE_TerrainGenerator()

        WEWorldProps.addReliefLayer(
            true,
            4,
            400.0,
            400.0,
            WE_Interpolation.I_VALUEFUNC_SMOOTHERSTEP,
            16,
            16,
            WE_Interpolation.I_VALUEFUNC_SMOOTHERSTEP,
            true
        )
        WEWorldProps.addMapLayer(false, 0.5, 4, 100.0, 10.0, 100.0, 0, WE_Interpolation.I_VALUEFUNC_SMOOTHERSTEP)
        WEWorldProps.addCCGXZ(terraingen)

        val p1: WE_BiomeProperties = WE_BiomeProperties()
        p1.genConditions.addCon(PrimitiveCondition.PC_ACTION_MOREEQUAL)
        (p1.genConditions.getLast().getFirstMath())!!.addNum(0, true)
        (p1.genConditions.getLast().getSecondMath())!!.addNum(0, false)
        p1.add(false, 1.0, 32.0, 96)

        val p2: WE_BiomeProperties = WE_BiomeProperties()
        p2.genConditions.addCon(PrimitiveCondition.PC_ACTION_LESS)
        (p2.genConditions.getLast().getFirstMath())!!.addNum(0, true)
        (p2.genConditions.getLast().getSecondMath())!!.addNum(0, false)
        p2.add(false, 0.4, 32.0, 56)

        val b1: WE_Biome = WE_Biome(p1)
        val b2: WE_Biome = WE_Biome(p2)
        b1.topBlock = GSBlocks.CERES_BLOCKS.getStateFromMeta(0)
        b1.fillerBlock = GSBlocks.CERES_BLOCKS.getStateFromMeta(1)

        WEWorldProps.addBiome(b1)
        WEWorldProps.addBiome(b2)

        this.biomeProvider = getBiomeProvider()
    }

    override fun createChunkGenerator(): IChunkGenerator {
        return WE_ChunkProvider(world, WEWorldProps)
    }

    override fun getBiomeProvider(): BiomeProvider {
        return WE_BiomeProvider(world, WEWorldProps)
    }

    override fun getBiomeProviderClass(): Class<out BiomeProvider>? {
        return null
    }

    override fun getChunkProviderClass(): Class<out IChunkGenerator> {
        return WE_ChunkProvider::class.java
    }

    override fun getDimensionType(): DimensionType {
        return (GSDimensions.TEST)!!
    }
}
