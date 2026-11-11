package galaxyspace.systems.SolarSystem.moons.triton.world.gen.we

import foundation.block.getStateFromMeta

import galaxyspace.core.GSBlocks
import galaxyspace.systems.SolarSystem.moons.triton.blocks.TritonBlocks.EnumTritonBlocks
import galaxyspace.systems.SolarSystem.moons.triton.blocks.TritonBlocks
import galaxyspace.systems.SolarSystem.moons.triton.world.gen.we.Triton_Plains
import galaxyspace.core.prefab.entities.EntityEvolvedColdBlaze
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_BiomeLayer
import asmodeuscore.core.utils.worldengine.WE_Biome
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedEnderman
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedSkeleton
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedSpider
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedZombie
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.level.block.Blocks
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import net.minecraftforge.fluids.FluidRegistry
import java.util.Random

open class Triton_Plains constructor(min: Double, max: Double, per: Double, height: Int, interpolate: Int) :
    WE_Biome(BiomeProperties("triton_plains"), intArrayOf(0x00CC00, 0xFFFFFF, 0x00CC00)) {
    init {
        biomeMinValueOnMap = min
        biomeMaxValueOnMap = max
        biomePersistence = per
        biomeNumberOfOctaves = 4
        biomeScaleX = 280.0
        biomeScaleY = 1.7
        biomeSurfaceHeight = height
        biomeInterpolateQuality = interpolate


        //-//
        decorateChunkGen_List.clear()
        createChunkGen_InXZ_List.clear()
        spawnableMonsterList.add(SpawnListEntry(EntityEvolvedSpider::class.java, 10, 1, 2))
        spawnableMonsterList.add(SpawnListEntry(EntityEvolvedSkeleton::class.java, 10, 1, 2))
        spawnableMonsterList.add(SpawnListEntry(EntityEvolvedZombie::class.java, 10, 1, 4))
        spawnableMonsterList.add(SpawnListEntry(EntityEvolvedEnderman::class.java, 10, 1, 2))
        spawnableMonsterList.add(SpawnListEntry(EntityEvolvedColdBlaze::class.java, 10, 1, 4))

        val standardBiomeLayers: WE_BiomeLayer = WE_BiomeLayer()
        standardBiomeLayers.add(
            GSBlocks.TRITON_BLOCKS.getStateFromMeta(1),
            GSBlocks.TRITON_BLOCKS.getStateFromMeta(2),
            -256,
            0,
            -4,
            -6,
            true
        )
        standardBiomeLayers.add(
            GSBlocks.TRITON_BLOCKS.getStateFromMeta(0),
            GSBlocks.TRITON_BLOCKS.getStateFromMeta(1),
            -256,
            0,
            -2,
            -1,
            true
        )
        standardBiomeLayers.add(Blocks.BEDROCK.defaultBlockState(), 0, 2, 0, 0, true)
        createChunkGen_InXZ_List.add(standardBiomeLayers)
    }

    override fun decorateBiome(world: World?, rand: Random?, x: Int, z: Int) {
        var randPosX: Int
        var randPosY: Int
        var randPosZ: Int

        if ((rand)!!.nextInt(3000) < 10) {
            randPosX = x + (rand)!!.nextInt(16) + 8
            randPosZ = z + (rand)!!.nextInt(16) + 8
            randPosY = (world)!!.getHeight(randPosX, randPosZ) //rand.nextInt(90);

            val pos: BlockPos = BlockPos(randPosX, randPosY, randPosZ)

            if ((world)!!.isEmptyBlock(pos.above())) {
                val state: IBlockState = (world)!!.getBlockState(pos.below())
                if (state === GSBlocks.TRITON_BLOCKS.defaultBlockState()
                        .setValue(
                            TritonBlocks.Companion.BASIC_TYPE,
                            EnumTritonBlocks.TRITON_GRUNT
                        )
                ) {
                    (world)!!.setBlockAndUpdate(pos.below(2), FluidRegistry.WATER.getBlock().defaultBlockState())
                    (world)!!.setBlock(
                        pos.below(),
                        GSBlocks.TRITON_BLOCKS.defaultBlockState().setValue(
                            TritonBlocks.Companion.BASIC_TYPE,
                            EnumTritonBlocks.TRITON_GEYSER
                        ),
                        3
                    )
                    (world)!!.scheduleBlockUpdate(
                        pos.below(),
                        GSBlocks.TRITON_BLOCKS.defaultBlockState().setValue(
                            TritonBlocks.Companion.BASIC_TYPE,
                            EnumTritonBlocks.TRITON_GEYSER
                        ).getBlock(),
                        0,
                        0
                    )
                }
            }
        }

        for (i: Int in 0..0) {
            randPosX = x + (rand)!!.nextInt(16) + 8
            randPosZ = z + (rand)!!.nextInt(16) + 8
            randPosY = (world)!!.getHeight(randPosX, randPosZ)

            val pos: BlockPos = BlockPos(randPosX, randPosY, randPosZ)

            if ((world)!!.isEmptyBlock(pos.above()) && randPosX % 150 == 0) {
                val state: IBlockState = (world)!!.getBlockState(pos.below())
                if (state === GSBlocks.TRITON_BLOCKS.defaultBlockState()
                        .setValue(
                            TritonBlocks.Companion.BASIC_TYPE,
                            EnumTritonBlocks.TRITON_GRUNT
                        )
                ) {
                    (world)!!.setBlock(
                        pos.below(),
                        GSBlocks.TRITON_BLOCKS.defaultBlockState().setValue(
                            TritonBlocks.Companion.BASIC_TYPE,
                            EnumTritonBlocks.TRITON_GEYSER_2
                        ),
                        3
                    )
                    (world)!!.scheduleBlockUpdate(
                        pos.below(),
                        GSBlocks.TRITON_BLOCKS.defaultBlockState().setValue(
                            TritonBlocks.Companion.BASIC_TYPE,
                            EnumTritonBlocks.TRITON_GEYSER_2
                        ).getBlock(),
                        0,
                        0
                    )
                }
            }
        }
    }
}
