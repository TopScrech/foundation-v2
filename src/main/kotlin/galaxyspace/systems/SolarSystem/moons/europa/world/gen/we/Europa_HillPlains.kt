package galaxyspace.systems.SolarSystem.moons.europa.world.gen.we

import foundation.block.getStateFromMeta

import foundation.level.offset

import galaxyspace.core.GSBlocks
import galaxyspace.systems.SolarSystem.moons.enceladus.blocks.EnceladusBlocks.EnumEnceladusBlocks
import galaxyspace.systems.SolarSystem.moons.enceladus.blocks.EnceladusCrystal
import galaxyspace.core.prefab.entities.EntityEvolvedColdBlaze
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_BiomeLayer
import galaxyspace.systems.SolarSystem.moons.europa.world.gen.we.Europa_HillPlains
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.BlockSurfaceIce.EnumBlockIce
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.BlockSurfaceIce
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_OreGen.BlockPredicate
import asmodeuscore.core.utils.worldengine.WE_Biome
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_TerrainGenerator
import net.minecraft.world.level.block.Blocks
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import net.minecraft.world.gen.feature.WorldGenMinable
import java.util.Random

open class Europa_HillPlains constructor(tg: WE_TerrainGenerator, value: Double) :
    WE_Biome(BiomeProperties("europa_hillplains"), intArrayOf(0x00CC00, 0xFFFFFF, 0x00CC00)) {
    init {
        //biomeMinValueOnMap      =  	-0.4D;
        biomeMaxValueOnMap = value
        biomePersistence = 1.9
        biomeNumberOfOctaves = 4
        biomeScaleX = 280.0
        biomeScaleY = 1.7
        biomeSurfaceHeight = 140
        biomeInterpolateQuality = 5

        biomeAbs = true


        //-//
        decorateChunkGen_List.clear()
        createChunkGen_InXZ_List.clear()
        spawnableMonsterList.add(SpawnListEntry(EntityEvolvedColdBlaze::class.java, 10, 1, 4))

        val layer: WE_BiomeLayer = WE_BiomeLayer()


        layer.add(Blocks.PACKED_ICE.defaultBlockState(), tg.worldStoneBlock, -256, 0, -65, -3, true)

        layer.add(
            GSBlocks.EUROPA_BLOCKS.getStateFromMeta(6),
            Blocks.PACKED_ICE.defaultBlockState(),
            -256,
            0,
            -2,
            -1,
            true
        )
        layer.add(Blocks.WATER.defaultBlockState(), tg.worldStoneBlock, -256, 0, -105, 0, true)

        layer.add(Blocks.BEDROCK.defaultBlockState(), 0, 2, 0, 0, true)


        createChunkGen_InXZ_List.add(layer)
    }

    override fun decorateBiome(world: World?, rand: Random?, x: Int, z: Int) {
        var randPosX: Int
        var randPosY: Int
        var randPosZ: Int


        for (i: Int in 0..29) {
            randPosX = x + (rand)!!.nextInt(16)
            //randPosY = rand.nextInt(120);
            randPosZ = z + (rand)!!.nextInt(16)
            val pos: BlockPos = (world)!!.getHeight(BlockPos(randPosX, 0, randPosZ))
            if ((world)!!.isAreaLoaded(pos, 13, false)) {
                WorldGenMinable(
                    GSBlocks.SURFACE_ICE.defaultBlockState()
                        .setValue(BlockSurfaceIce.BASIC_TYPE, EnumBlockIce.DRY_ICE),
                    35,
                    BlockPredicate(
                        GSBlocks.EUROPA_BLOCKS.getStateFromMeta(6)
                    )
                ).generate(world, rand, pos)
            }
        }
        for (i: Int in 0..29) {
            randPosX = x + (rand)!!.nextInt(16) + 8
            //randPosY = rand.nextInt(120);
            randPosZ = z + (rand)!!.nextInt(16) + 8
            val pos: BlockPos = (world)!!.getHeight(BlockPos(randPosX, 0, randPosZ))

            if ((world)!!.getBlockState(pos.below()) === GSBlocks.EUROPA_BLOCKS.getStateFromMeta(6) && (world)!!.isEmptyBlock(pos)) {
                (world)!!.setBlock(pos, Blocks.SNOW_LAYER.defaultBlockState(), 2)
            }
        }

        for (i: Int in 0..29) {
            randPosX = x + (rand)!!.nextInt(16)
            //randPosY = rand.nextInt(120);
            randPosZ = z + (rand)!!.nextInt(16)
            val pos: BlockPos = (world)!!.getHeight(BlockPos(randPosX, 0, randPosZ))

            if ((world)!!.isAreaLoaded(pos, 13, false)) {
                WorldGenMinable(
                    GSBlocks.EUROPA_BLOCKS.getStateFromMeta(0), 25,
                    BlockPredicate(
                        GSBlocks.EUROPA_BLOCKS.getStateFromMeta(6)
                    )
                ).generate(world, rand, pos)
            }
        }


        for (i: Int in 0..1) {
            randPosX = x + (rand)!!.nextInt(16) + 8
            //randPosY = rand.nextInt(120);
            randPosZ = z + (rand)!!.nextInt(16) + 8
            var pos: BlockPos = (world)!!.getHeight(BlockPos(randPosX, 0, randPosZ))



            if ((world)!!.getBlockState(pos.below()) === GSBlocks.EUROPA_BLOCKS.getStateFromMeta(0) && (world)!!.isEmptyBlock(pos)) {
                var check: Boolean = true
                pos = pos.below()

                for (pos1: BlockPos in BlockPos.getAllInBox(pos.offset(-1, 0, -1), pos.offset(1, 0, 1))) if ((world)!!.isEmptyBlock(
                        pos1
                    )
                ) {
                    check = false
                    break
                }

                if (check) {
                    (world)!!.setBlockAndUpdate(pos, GSBlocks.EUROPA_GEYSER.defaultBlockState())

                    for (j: Int in 1..2) (world)!!.setBlockAndUpdate(pos.below(j), Blocks.WATER.defaultBlockState())
                }


                //world.setBlock(pos.down(), GSBlocks.EUROPA_UWGEYSER.defaultBlockState(), 2);
            }
        }


        /*  if (world.getBlockState(pos.down()) == GSBlocks.ENCELADUS_BLOCKS.getStateFromMeta(EnumEnceladusBlocks.ENCELADUS_GRUNT.getMeta()) && world.isEmptyBlock(pos))
	        {	        	
                world.setBlockAndUpdate(pos, GSBlocks.ENCELADUS_CRYSTAL.defaultBlockState().setValue(EnceladusCrystal.FACING, EnumFacing.HORIZONTALS[rand.nextInt(4)]));
	        }
	        
	        if (world.getBlockState(pos.up()) == GSBlocks.ENCELADUS_BLOCKS.getStateFromMeta(EnumEnceladusBlocks.ENCELADUS_GRUNT.getMeta()) && world.isEmptyBlock(pos))
	        {	        	
                world.setBlockAndUpdate(pos, GSBlocks.ENCELADUS_CRYSTAL.defaultBlockState().setValue(EnceladusCrystal.FACING, EnumFacing.HORIZONTALS[rand.nextInt(4)]));
	        	
	        	
	        }*/
    }
}
