package galaxyspace.systems.BarnardsSystem.planets.barnarda_c.world.gen.we

import foundation.block.getStateFromMeta

import galaxyspace.systems.BarnardsSystem.core.BRBlocks
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.world.gen.we.Barnarda_C_Beach
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_BiomeLayer
import galaxyspace.systems.BarnardsSystem.core.configs.BRConfigCore
import asmodeuscore.core.utils.worldengine.WE_Biome
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.Level as World
import java.util.Random

open class Barnarda_C_Beach constructor(min: Double, max: Double, tier: Int) :
    WE_Biome(BiomeProperties("barnarda_c_beach" + tier), intArrayOf(grasscolor, 0x11FF66, 0x985cff)) {
    init {
        biomeMinValueOnMap = min
        biomeMaxValueOnMap = max
        biomePersistence = 1.3
        biomeNumberOfOctaves = 3
        biomeScaleX = 280.0
        biomeScaleY = 1.7
        biomeSurfaceHeight = 68
        biomeInterpolateQuality = 20
        biomeTemerature = 0.5

        //-//
        decorateChunkGen_List.clear()
        createChunkGen_InXZ_List.clear()

        val standardBiomeLayers: WE_BiomeLayer = WE_BiomeLayer()
        standardBiomeLayers.add(
            Blocks.SANDSTONE.defaultBlockState(),
            BRBlocks.BARNARDA_C_BLOCKS.getStateFromMeta(1),
            -256,
            0,
            -4,
            -1,
            true
        )
        standardBiomeLayers.add(
            BRBlocks.BARNARDA_C_FALLING_BLOCKS.getStateFromMeta(0),
            Blocks.SANDSTONE.defaultBlockState(),
            -256,
            0,
            -256,
            0,
            true
        )
        standardBiomeLayers.add(Blocks.BEDROCK.defaultBlockState(), 0, 2, 0, 0, true)
        createChunkGen_InXZ_List.add(standardBiomeLayers)
    }

    override fun decorateBiome(world: World?, rand: Random?, x: Int, z: Int) {
    }

    companion object {
        private val grasscolor = if (BRConfigCore.enableGreenBarnardaC) 0x89AC76 else 0x822899
    }
}
