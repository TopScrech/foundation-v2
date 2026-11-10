package galaxyspace.systems.SolarSystem.moons.enceladus.world.gen.we

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
import net.minecraft.init.Blocks
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.Random

open class Enceladus_Plains constructor() :
    WE_Biome(BiomeProperties("enceladus_plains"), intArrayOf(0x00CC00, 0xFFFFFF, 0x00CC00)) {
    init {
        biomeMinValueOnMap = -0.4
        biomeMaxValueOnMap = 0.0
        biomePersistence = 1.4
        biomeNumberOfOctaves = 5
        biomeScaleX = 280.0
        biomeScaleY = 1.7
        biomeSurfaceHeight = 80
        biomeInterpolateQuality = 15


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
            Blocks.PACKED_ICE.getDefaultState(),
            GSBlocks.ENCELADUS_BLOCKS.getStateFromMeta(1),
            -256,
            0,
            -25,
            -2,
            true
        )
        standardBiomeLayers.add(
            GSBlocks.ENCELADUS_BLOCKS.getStateFromMeta(0),
            Blocks.PACKED_ICE.getDefaultState(),
            -256,
            0,
            -2,
            -1,
            true
        )
        //standardBiomeLayers.add(GSBlocks.ENCELADUS_BLOCKS, (byte)1, GSBlocks.ENCELADUS_BLOCKS, (byte)2, -256, 0, -256,  0, false);
        standardBiomeLayers.add(Blocks.BEDROCK.getDefaultState(), 0, 2, 0, 0, true)
        createChunkGen_InXZ_List.add(standardBiomeLayers)
    }

    override fun decorateBiome(world: World?, rand: Random?, x: Int, z: Int) {
        var randPosX: Int
        var randPosY: Int
        var randPosZ: Int

        for (i: Int in 0..29) {
            randPosX = x + (rand)!!.nextInt(16) + 8
            randPosY = (rand)!!.nextInt(120)
            randPosZ = z + (rand)!!.nextInt(16) + 8

            val pos: BlockPos = BlockPos(randPosX, randPosY, randPosZ)

            if ((world)!!.getBlockState(pos.down()) === GSBlocks.ENCELADUS_BLOCKS.getStateFromMeta(EnumEnceladusBlocks.ENCELADUS_GRUNT.getMeta()) && (world)!!.isAirBlock(
                    pos
                )
            ) {
                (world)!!.setBlockState(
                    pos,
                    GSBlocks.ENCELADUS_CRYSTAL.getDefaultState().withProperty(
                        EnceladusCrystal.FACING,
                        EnumFacing.HORIZONTALS[(rand)!!.nextInt(4)]
                    )
                )
            }

            if ((world)!!.getBlockState(pos.up()) === GSBlocks.ENCELADUS_BLOCKS.getStateFromMeta(EnumEnceladusBlocks.ENCELADUS_GRUNT.getMeta()) && (world)!!.isAirBlock(
                    pos
                )
            ) {
                (world)!!.setBlockState(
                    pos,
                    GSBlocks.ENCELADUS_CRYSTAL.getDefaultState().withProperty(
                        EnceladusCrystal.FACING,
                        EnumFacing.HORIZONTALS[(rand)!!.nextInt(4)]
                    )
                )
            }
        }
    }
}
