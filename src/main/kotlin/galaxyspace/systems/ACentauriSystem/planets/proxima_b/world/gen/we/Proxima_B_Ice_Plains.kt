package galaxyspace.systems.ACentauriSystem.planets.proxima_b.world.gen.we

import galaxyspace.core.prefab.entities.EntityEvolvedColdBlaze
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_BiomeLayer
import galaxyspace.systems.ACentauriSystem.core.ACBlocks
import galaxyspace.systems.ACentauriSystem.planets.proxima_b.blocks.Proxima_B_Blocks.EnumBlockProximaB
import galaxyspace.systems.ACentauriSystem.planets.proxima_b.blocks.Proxima_B_Blocks
import asmodeuscore.core.astronomy.dimension.world.gen.features.trees.WorldGenTree_BigJungle
import galaxyspace.core.prefab.world.gen.WorldGenPool
import asmodeuscore.core.utils.worldengine.WE_Biome
import micdoodle8.mods.galacticraft.core.GCFluids
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedSkeleton
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.Random

open class Proxima_B_Ice_Plains constructor() :
    WE_Biome(BiomeProperties("proxima_b_ice_plains"), intArrayOf(0x00FF00, 0xEEDD44, 0x00FF00)) {
    init {
        biomeMinValueOnMap = 1.0
        biomeMaxValueOnMap = 1.8
        biomePersistence = 1.2
        biomeNumberOfOctaves = 4
        biomeScaleX = 280.0
        biomeScaleY = 1.7
        biomeSurfaceHeight = 74
        biomeInterpolateQuality = 15

        //-//
        decorateChunkGen_List.clear()
        createChunkGen_InXZ_List.clear()

        spawnableMonsterList.add(SpawnListEntry(EntityEvolvedSkeleton::class.java, 10, 1, 2))
        spawnableMonsterList.add(SpawnListEntry(EntityEvolvedColdBlaze::class.java, 10, 1, 4))

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
            ACBlocks.PROXIMA_B_BLOCKS.getStateFromMeta(3),
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

        if (!(world)!!.isAreaLoaded(pos, 13, false)) {
            var cangen: Boolean = true

            for (pos1: BlockPos in BlockPos.getAllInBox(pos.add(-3, -1, -3), pos.add(3, -1, 3))) if ((world)!!.isAirBlock(
                    pos1
                )
            ) cangen = false

            if (cangen && (world)!!.getBlockState(pos.down()) === ACBlocks.PROXIMA_B_BLOCKS.getDefaultState()
                    .withProperty(
                        Proxima_B_Blocks.BASIC_TYPE,
                        EnumBlockProximaB.ICE_SURFACE
                    )
            ) {
                WorldGenTree_BigJungle(
                    ACBlocks.PROXIMA_B_LOG_2.getStateFromMeta(0),
                    Blocks.AIR.getDefaultState(),
                    (rand)!!.nextInt(3)
                ).generate((world)!!, (rand)!!, pos)
            }
        }

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
