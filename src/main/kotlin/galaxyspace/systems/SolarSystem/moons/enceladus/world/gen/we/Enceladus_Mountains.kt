package galaxyspace.systems.SolarSystem.moons.enceladus.world.gen.we

import foundation.block.getStateFromMeta

import galaxyspace.GalaxySpace
import galaxyspace.core.GSBlocks
import galaxyspace.systems.SolarSystem.moons.enceladus.blocks.EnceladusBlocks.EnumEnceladusBlocks
import galaxyspace.systems.SolarSystem.moons.enceladus.blocks.EnceladusCrystal
import galaxyspace.core.prefab.entities.EntityEvolvedColdBlaze
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_BiomeLayer
import asmodeuscore.core.utils.worldengine.WE_Biome
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedEnderman
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedSkeleton
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedSpider
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedZombie
import net.minecraft.world.level.block.Blocks
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import java.util.Random

open class Enceladus_Mountains constructor() :
    WE_Biome(BiomeProperties("enceladus_mountains"), intArrayOf(0x00CC00, 0xFFFFFF, 0x00CC00)) {
    init {
        biomeMinValueOnMap = 0.0
        biomeMaxValueOnMap = 2.0
        biomePersistence = 1.6
        biomeNumberOfOctaves = 4
        biomeScaleX = 280.0
        biomeScaleY = 1.7
        biomeSurfaceHeight = 120
        biomeInterpolateQuality = 10


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
            Blocks.PACKED_ICE.defaultBlockState(),
            GSBlocks.ENCELADUS_BLOCKS.getStateFromMeta(1),
            -256,
            0,
            -40,
            -2,
            true
        )
        standardBiomeLayers.add(
            GSBlocks.ENCELADUS_BLOCKS.getStateFromMeta(0),
            Blocks.PACKED_ICE.defaultBlockState(),
            -256,
            0,
            -4,
            -1,
            true
        )
        //standardBiomeLayers.add(GSBlocks.ENCELADUS_BLOCKS, (byte)1, GSBlocks.ENCELADUS_BLOCKS, (byte)2, -256, 0, -256,  0, false);
        standardBiomeLayers.add(Blocks.BEDROCK.defaultBlockState(), 0, 2, 0, 0, true)
        createChunkGen_InXZ_List.add(standardBiomeLayers)
    }

    override fun decorateBiome(world: World?, rand: Random?, x: Int, z: Int) {
        var randPosX: Int
        var randPosY: Int
        var randPosZ: Int

        //GalaxySpace.instance.debug(x + " | " + z);
        for (i: Int in 0..49) {
            randPosX = x + (rand)!!.nextInt(16) + 8
            randPosY = (rand)!!.nextInt(120)
            randPosZ = z + (rand)!!.nextInt(16) + 8

            for (j: Int in 1..<(rand)!!.nextInt(3) + 3) {
                val pos: BlockPos = BlockPos(
                    randPosX + j * (if ((rand)!!.nextBoolean()) 1 else -1),
                    randPosY,
                    randPosZ + j * (if ((rand)!!.nextBoolean()) 1 else -1)
                )
                if ((world)!!.getBlockState(pos.below()) === GSBlocks.ENCELADUS_BLOCKS.getStateFromMeta(EnumEnceladusBlocks.ENCELADUS_GRUNT.getMeta()) && (world)!!.isEmptyBlock(
                        pos
                    )
                ) {
                    (world)!!.setBlockAndUpdate(
                        pos,
                        GSBlocks.ENCELADUS_CRYSTAL.defaultBlockState().setValue(
                            EnceladusCrystal.FACING,
                            EnumFacing.HORIZONTALS[(rand)!!.nextInt(4)]
                        )
                    )
                }

                if ((world)!!.getBlockState(pos.above()) === GSBlocks.ENCELADUS_BLOCKS.getStateFromMeta(EnumEnceladusBlocks.ENCELADUS_GRUNT.getMeta()) && (world)!!.isEmptyBlock(
                        pos
                    )
                ) {
                    (world)!!.setBlockAndUpdate(
                        pos,
                        GSBlocks.ENCELADUS_CRYSTAL.defaultBlockState().setValue(
                            EnceladusCrystal.FACING,
                            EnumFacing.HORIZONTALS[(rand)!!.nextInt(4)]
                        )
                    )
                }
            }
        }
    }
}
