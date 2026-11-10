package galaxyspace.systems.SolarSystem.moons.triton.world.gen.we

import galaxyspace.core.GSBlocks
import galaxyspace.systems.SolarSystem.moons.triton.blocks.TritonBlocks.EnumTritonBlocks
import galaxyspace.systems.SolarSystem.moons.triton.blocks.TritonBlocks
import galaxyspace.systems.SolarSystem.moons.triton.world.gen.we.Triton_Mountains
import galaxyspace.core.prefab.entities.EntityEvolvedColdBlaze
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_BiomeLayer
import asmodeuscore.core.utils.worldengine.WE_Biome
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedEnderman
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedSkeleton
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedSpider
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedZombie
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fluids.FluidRegistry
import java.util.Random

open class Triton_Mountains constructor(min: Double, max: Double) :
    WE_Biome(BiomeProperties("triton_mountains"), intArrayOf(0x00CC00, 0xFFFFFF, 0x00CC00)) {
    init {
        biomeMinValueOnMap = min
        biomeMaxValueOnMap = max
        biomePersistence = 2.5
        biomeNumberOfOctaves = 4
        biomeScaleX = 280.0
        biomeScaleY = 1.7
        biomeSurfaceHeight = 120
        biomeInterpolateQuality = 25


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
        standardBiomeLayers.add(Blocks.BEDROCK.getDefaultState(), 0, 2, 0, 0, true)
        createChunkGen_InXZ_List.add(standardBiomeLayers)
    }

    override fun decorateBiome(world: World?, rand: Random?, x: Int, z: Int) {
        val randPosX: Int
        val randPosY: Int
        val randPosZ: Int

        if ((rand)!!.nextInt(3000) < 10) {
            randPosX = x + (rand)!!.nextInt(16) + 8
            randPosZ = z + (rand)!!.nextInt(16) + 8
            randPosY = (world)!!.getHeight(randPosX, randPosZ) //rand.nextInt(90);

            val pos: BlockPos = BlockPos(randPosX, randPosY, randPosZ)

            if ((world)!!.isAirBlock(pos.up())) {
                val state: IBlockState = (world)!!.getBlockState(pos.down())
                if (state === GSBlocks.TRITON_BLOCKS.getDefaultState()
                        .withProperty(
                            TritonBlocks.Companion.BASIC_TYPE,
                            EnumTritonBlocks.TRITON_GRUNT
                        )
                ) {
                    (world)!!.setBlockState(pos.down(2), FluidRegistry.WATER.getBlock().getDefaultState())
                    (world)!!.setBlockState(
                        pos.down(),
                        GSBlocks.TRITON_BLOCKS.getDefaultState().withProperty(
                            TritonBlocks.Companion.BASIC_TYPE,
                            EnumTritonBlocks.TRITON_GEYSER
                        ),
                        3
                    )
                    (world)!!.scheduleBlockUpdate(
                        pos.down(),
                        GSBlocks.TRITON_BLOCKS.getDefaultState().withProperty(
                            TritonBlocks.Companion.BASIC_TYPE,
                            EnumTritonBlocks.TRITON_GEYSER
                        ).getBlock(),
                        0,
                        0
                    )
                }
            }
        }
    }
}
