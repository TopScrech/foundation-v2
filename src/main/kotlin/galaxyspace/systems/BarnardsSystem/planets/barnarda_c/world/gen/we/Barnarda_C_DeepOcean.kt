package galaxyspace.systems.BarnardsSystem.planets.barnarda_c.world.gen.we

import galaxyspace.systems.BarnardsSystem.core.BRBlocks
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_BiomeLayer
import galaxyspace.systems.BarnardsSystem.core.configs.BRConfigCore
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.world.gen.we.Barnarda_C_DeepOcean
import asmodeuscore.core.utils.worldengine.WE_Biome
import net.minecraft.entity.passive.EntitySquid
import net.minecraft.init.Blocks
import net.minecraft.world.World
import java.util.Random

open class Barnarda_C_DeepOcean constructor(min: Double, max: Double) :
    WE_Biome(BiomeProperties("barnarda_b_deepocean"), intArrayOf(grasscolor, 0x116644, 0x985cff)) {
    init {
        biomeMinValueOnMap = min
        biomeMaxValueOnMap = max
        biomePersistence = 1.4
        biomeNumberOfOctaves = 4
        biomeScaleX = 280.0
        biomeScaleY = 1.7
        biomeSurfaceHeight = 35
        biomeInterpolateQuality = 65
        biomeTemerature = 0.0
        //-//
        decorateChunkGen_List.clear()
        createChunkGen_InXZ_List.clear()

        this.spawnableWaterCreatureList.add(SpawnListEntry(EntitySquid::class.java, 4, 1, 4))

        val standardBiomeLayers: WE_BiomeLayer = WE_BiomeLayer()
        standardBiomeLayers.add(
            Blocks.GRAVEL.getDefaultState(),
            BRBlocks.BARNARDA_C_BLOCKS.getStateFromMeta(1),
            -256,
            0,
            -4,
            -1,
            true
        )
        //standardBiomeLayers.add(BRBlocks.BARNARDA_C_GRASS.getStateFromMeta(0), BRBlocks.BARNARDA_C_BLOCKS.getStateFromMeta(0), -256, 0, -256,  0, false);
        standardBiomeLayers.add(Blocks.BEDROCK.getDefaultState(), 0, 2, 0, 0, true)
        createChunkGen_InXZ_List.add(standardBiomeLayers)
    }

    override fun decorateBiome(world: World?, rand: Random?, x: Int, z: Int) {
    }

    companion object {
        private val grasscolor = if (BRConfigCore.enableGreenBarnardaC) 0x89AC76 else 0xdf73ff
    }
}
