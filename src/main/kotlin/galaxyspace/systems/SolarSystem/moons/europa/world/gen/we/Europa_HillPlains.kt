package galaxyspace.systems.SolarSystem.moons.europa.world.gen.we

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
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
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


        layer.add(Blocks.PACKED_ICE.getDefaultState(), tg.worldStoneBlock, -256, 0, -65, -3, true)

        layer.add(
            GSBlocks.EUROPA_BLOCKS.getStateFromMeta(6),
            Blocks.PACKED_ICE.getDefaultState(),
            -256,
            0,
            -2,
            -1,
            true
        )
        layer.add(Blocks.WATER.getDefaultState(), tg.worldStoneBlock, -256, 0, -105, 0, true)

        layer.add(Blocks.BEDROCK.getDefaultState(), 0, 2, 0, 0, true)


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
                    GSBlocks.SURFACE_ICE.getDefaultState()
                        .withProperty(BlockSurfaceIce.BASIC_TYPE, EnumBlockIce.DRY_ICE),
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

            if ((world)!!.getBlockState(pos.down()) === GSBlocks.EUROPA_BLOCKS.getStateFromMeta(6) && (world)!!.isAirBlock(pos)) {
                (world)!!.setBlockState(pos, Blocks.SNOW_LAYER.getDefaultState(), 2)
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



            if ((world)!!.getBlockState(pos.down()) === GSBlocks.EUROPA_BLOCKS.getStateFromMeta(0) && (world)!!.isAirBlock(pos)) {
                var check: Boolean = true
                pos = pos.down()

                for (pos1: BlockPos in BlockPos.getAllInBox(pos.add(-1, 0, -1), pos.add(1, 0, 1))) if ((world)!!.isAirBlock(
                        pos1
                    )
                ) {
                    check = false
                    break
                }

                if (check) {
                    (world)!!.setBlockState(pos, GSBlocks.EUROPA_GEYSER.getDefaultState())

                    for (j: Int in 1..2) (world)!!.setBlockState(pos.down(j), Blocks.WATER.getDefaultState())
                }


                //world.setBlockState(pos.down(), GSBlocks.EUROPA_UWGEYSER.getDefaultState(), 2);	  
            }
        }


        /*  if (world.getBlockState(pos.down()) == GSBlocks.ENCELADUS_BLOCKS.getStateFromMeta(EnumEnceladusBlocks.ENCELADUS_GRUNT.getMeta()) && world.isAirBlock(pos))
	        {	        	
	        	world.setBlockState(pos, GSBlocks.ENCELADUS_CRYSTAL.getDefaultState().withProperty(EnceladusCrystal.FACING, EnumFacing.HORIZONTALS[rand.nextInt(4)]));	  
	        }
	        
	        if (world.getBlockState(pos.up()) == GSBlocks.ENCELADUS_BLOCKS.getStateFromMeta(EnumEnceladusBlocks.ENCELADUS_GRUNT.getMeta()) && world.isAirBlock(pos))
	        {	        	
	        	world.setBlockState(pos, GSBlocks.ENCELADUS_CRYSTAL.getDefaultState().withProperty(EnceladusCrystal.FACING, EnumFacing.HORIZONTALS[rand.nextInt(4)]));	  
	        	
	        	
	        }*/
    }
}
