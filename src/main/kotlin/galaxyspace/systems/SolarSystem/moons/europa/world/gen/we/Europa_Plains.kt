package galaxyspace.systems.SolarSystem.moons.europa.world.gen.we

import galaxyspace.core.GSBlocks
import galaxyspace.systems.SolarSystem.moons.enceladus.blocks.EnceladusBlocks.EnumEnceladusBlocks
import galaxyspace.systems.SolarSystem.moons.enceladus.blocks.EnceladusCrystal
import galaxyspace.core.prefab.entities.EntityEvolvedColdBlaze
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_BiomeLayer
import galaxyspace.systems.SolarSystem.moons.europa.world.gen.we.Europa_Plains
import asmodeuscore.core.utils.worldengine.WE_Biome
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_TerrainGenerator
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.Random

open class Europa_Plains constructor(tg: WE_TerrainGenerator, value: Double) :
    WE_Biome(BiomeProperties("europa_plains"), intArrayOf(0x00CC00, 0xFFFFFF, 0x00CC00)) {
    init {
        //biomeMinValueOnMap      =  	-0.4D;
        biomeMaxValueOnMap = value
        biomePersistence = 1.9
        biomeNumberOfOctaves = 4
        biomeScaleX = 280.0
        biomeScaleY = 1.7
        biomeSurfaceHeight = 120
        biomeInterpolateQuality = 2

        biomeAbs = true


        //-//
        decorateChunkGen_List.clear()
        createChunkGen_InXZ_List.clear()
        spawnableMonsterList.add(SpawnListEntry(EntityEvolvedColdBlaze::class.java, 10, 1, 4))

        val layer: WE_BiomeLayer = WE_BiomeLayer()

        layer.add(Blocks.PACKED_ICE.getDefaultState(), tg.worldStoneBlock, -256, 0, -55, -1, true)

        layer.add(GSBlocks.SURFACE_ICE.getStateFromMeta(4), Blocks.PACKED_ICE.getDefaultState(), -256, 0, -2, -1, true)

        layer.add(Blocks.WATER.getDefaultState(), tg.worldStoneBlock, -256, 0, -100, 0, true)
        layer.add(Blocks.BEDROCK.getDefaultState(), 0, 2, 0, 0, true)


        createChunkGen_InXZ_List.add(layer)
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
}
