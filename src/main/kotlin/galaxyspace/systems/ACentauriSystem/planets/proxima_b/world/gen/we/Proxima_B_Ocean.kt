package galaxyspace.systems.ACentauriSystem.planets.proxima_b.world.gen.we

import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_BiomeLayer
import galaxyspace.systems.ACentauriSystem.core.ACBlocks
import galaxyspace.systems.ACentauriSystem.planets.proxima_b.blocks.Proxima_B_Blocks.EnumBlockProximaB
import galaxyspace.systems.ACentauriSystem.planets.proxima_b.blocks.Proxima_B_Blocks
import galaxyspace.core.prefab.world.gen.WorldGenPool
import asmodeuscore.core.utils.worldengine.WE_Biome
import micdoodle8.mods.galacticraft.core.GCFluids
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.Random

open class Proxima_B_Ocean constructor() :
    WE_Biome(BiomeProperties("proxima_b_ocean"), intArrayOf(0x00FF00, 0xEEDD44, 0x00FF00)) {
    init {
        biomeMinValueOnMap = 2.0
        biomeMaxValueOnMap = 2.6
        biomePersistence = 1.4
        biomeNumberOfOctaves = 4
        biomeScaleX = 280.0
        biomeScaleY = 1.7
        biomeSurfaceHeight = 55
        biomeInterpolateQuality = 15


        //-//
        decorateChunkGen_List.clear()
        createChunkGen_InXZ_List.clear()

        val standardBiomeLayers: WE_BiomeLayer = WE_BiomeLayer()
        standardBiomeLayers.add(
            ACBlocks.PROXIMA_B_BLOCKS.getStateFromMeta(1),
            ACBlocks.PROXIMA_B_BLOCKS.getStateFromMeta(2),
            -256,
            0,
            -4,
            -1,
            true
        )
        standardBiomeLayers.add(
            ACBlocks.PROXIMA_B_BLOCKS.getStateFromMeta(0),
            ACBlocks.PROXIMA_B_BLOCKS.getStateFromMeta(1),
            -256,
            0,
            -256,
            0,
            false
        )
        standardBiomeLayers.add(Blocks.BEDROCK.getDefaultState(), 0, 2, 0, 0, true)
        createChunkGen_InXZ_List.add(standardBiomeLayers)
    }

    override fun decorateBiome(world: World?, rand: Random?, x: Int, z: Int) {
        var randPosX: Int = x + (rand)!!.nextInt(16) + 8
        var randPosZ: Int = z + (rand)!!.nextInt(16) + 8
        var pos: BlockPos = (world)!!.getHeight(BlockPos(randPosX, 0, randPosZ))

        for (i: Int in 0..2) {
            randPosX = x + (rand)!!.nextInt(16) + 8
            //randPosY = this.rand.nextInt(256);
            randPosZ = z + (rand)!!.nextInt(16) + 8
            pos = (world)!!.getHeight(BlockPos(randPosX, 0, randPosZ))

            if ((world)!!.getBlockState(pos.down()) === ACBlocks.PROXIMA_B_BLOCKS.getDefaultState()
                    .withProperty(
                        Proxima_B_Blocks.BASIC_TYPE,
                        EnumBlockProximaB.SURFACE
                    )
            ) (world)!!.setBlockState(pos, Blocks.DEADBUSH.getDefaultState())
        }

        val kx: Int = x + (rand)!!.nextInt(16) + 8
        val kz: Int = x + (rand)!!.nextInt(16) + 8

        if ((rand)!!.nextInt(50) == 0) {
            val l2: Int = (world)!!.getTopSolidOrLiquidBlock(BlockPos(kx, 0, kz)).getY() - 20 - (rand)!!.nextInt(25)
            WorldGenPool(
                ACBlocks.PROXIMA_B_BLOCKS.getDefaultState().withProperty(
                    Proxima_B_Blocks.BASIC_TYPE,
                    EnumBlockProximaB.STONE
                ), (GCFluids.fluidOil)!!.getBlock().getDefaultState(), 5
            ).generate((world)!!, (rand)!!, BlockPos(x, l2, z))
        }
    }
}
