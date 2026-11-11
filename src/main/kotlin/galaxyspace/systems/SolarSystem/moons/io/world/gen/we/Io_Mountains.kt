package galaxyspace.systems.SolarSystem.moons.io.world.gen.we

import foundation.block.getStateFromMeta

import galaxyspace.core.GSBlocks
import galaxyspace.systems.SolarSystem.moons.io.world.gen.we.Io_Mountains
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_BiomeLayer
import asmodeuscore.core.utils.worldengine.WE_Biome
import net.minecraft.world.level.block.Blocks

open class Io_Mountains constructor(index: Int, min: Double, max: Double, y: Int, interpolate: Int) :
    WE_Biome(BiomeProperties("io_mountains_" + index), intArrayOf(0x00CC00, 0xFFFFFF, 0x00CC00)) {
    init {
        biomeMinValueOnMap = min
        biomeMaxValueOnMap = max
        biomePersistence = 1.1
        biomeNumberOfOctaves = 4
        biomeScaleX = 180.0
        biomeScaleY = 1.8
        biomeSurfaceHeight = y
        biomeInterpolateQuality = interpolate

        decorateChunkGen_List.clear()
        createChunkGen_InXZ_List.clear()

        val standardBiomeLayers: WE_BiomeLayer = WE_BiomeLayer()
        standardBiomeLayers.add(
            GSBlocks.IO_BLOCKS.getStateFromMeta(0),
            GSBlocks.IO_BLOCKS.getStateFromMeta(1),
            -256,
            0,
            -256,
            0,
            false
        )
        standardBiomeLayers.add(Blocks.BEDROCK.defaultBlockState(), 0, 2, 0, 0, true)
        createChunkGen_InXZ_List.add(standardBiomeLayers)
    }
}
