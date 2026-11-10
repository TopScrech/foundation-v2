package galaxyspace.systems.SolarSystem.moons.io.world.gen.we

import galaxyspace.core.GSBlocks
import galaxyspace.systems.SolarSystem.moons.io.world.gen.we.Io_Plains
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_BiomeLayer
import asmodeuscore.core.utils.worldengine.WE_Biome
import net.minecraft.init.Blocks

open class Io_Plains constructor() :
    WE_Biome(BiomeProperties("io_plains"), intArrayOf(0x00CC00, 0xFFFFFF, 0x00CC00)) {
    init {
        biomeMinValueOnMap = 0.0
        biomeMaxValueOnMap = 1.4
        biomePersistence = 1.6
        biomeNumberOfOctaves = 4
        biomeScaleX = 180.0
        biomeScaleY = 1.7
        biomeSurfaceHeight = 80
        biomeInterpolateQuality = 25

        decorateChunkGen_List.clear()
        createChunkGen_InXZ_List.clear()
        /*
		WE_LakeGen lavaLakes = new WE_LakeGen();
		lavaLakes.lakeBlock = Blocks.LAVA;
		lavaLakes.fGen      =       false;
		lavaLakes.maxY      =          70;
		decorateChunkGen_List.add(lavaLakes);
			*/
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
        standardBiomeLayers.add(Blocks.BEDROCK.getDefaultState(), 0, 2, 0, 0, true)
        createChunkGen_InXZ_List.add(standardBiomeLayers)
    }
}
