package galaxyspace.systems.SolarSystem.moons.io.world.gen.we

import foundation.block.getStateFromMeta

import galaxyspace.core.GSBlocks
import galaxyspace.systems.SolarSystem.moons.io.world.gen.we.Io_Volcano
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_BiomeLayer
import asmodeuscore.core.utils.worldengine.WE_Biome
import net.minecraft.world.level.block.Blocks

open class Io_Volcano constructor() :
    WE_Biome(BiomeProperties("io_volcano"), intArrayOf(0x00CC00, 0xFFFFFF, 0x00CC00)) {
    init {
        biomeMinValueOnMap = 0.5
        biomeMaxValueOnMap = 1.0
        biomePersistence = 1.1
        biomeNumberOfOctaves = 4
        biomeScaleX = 180.0
        biomeScaleY = 1.9
        biomeSurfaceHeight = 95
        biomeInterpolateQuality = 35

        decorateChunkGen_List.clear()
        createChunkGen_InXZ_List.clear()

        val standardBiomeLayers: WE_BiomeLayer = WE_BiomeLayer()
        standardBiomeLayers.add(
            GSBlocks.IO_BLOCKS.getStateFromMeta(2),
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
