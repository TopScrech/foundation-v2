package galaxyspace.systems.SolarSystem.moons.europa.world.gen.we

import foundation.level.offset

import galaxyspace.core.GSBlocks
import galaxyspace.core.prefab.entities.EntityEvolvedColdBlaze
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_BiomeLayer
import galaxyspace.systems.SolarSystem.moons.europa.world.gen.we.Europa_Ravine
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.BlockSurfaceIce.EnumBlockIce
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.BlockSurfaceIce
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_OreGen.BlockPredicate
import asmodeuscore.core.utils.worldengine.WE_Biome
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_TerrainGenerator
import micdoodle8.mods.galacticraft.core.world.gen.WorldGenMinableMeta
import net.minecraft.world.level.block.Blocks
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import net.minecraft.world.gen.feature.WorldGenMinable
import java.util.Random

open class Europa_Ravine constructor(tg: WE_TerrainGenerator) :
    WE_Biome(BiomeProperties("europa_ravine"), intArrayOf(0x00CC00, 0xFFFFFF, 0x00CC00)) {
    init {
        //biomeMinValueOnMap      =  	-0.4D;
        biomeMaxValueOnMap = 0.0
        biomePersistence = 1.2
        biomeNumberOfOctaves = 4
        biomeScaleX = 280.0
        biomeScaleY = 1.5
        biomeSurfaceHeight = 70
        biomeInterpolateQuality = 2

        biomeAbs = true


        //-//
        decorateChunkGen_List.clear()
        createChunkGen_InXZ_List.clear()
        spawnableMonsterList.add(SpawnListEntry(EntityEvolvedColdBlaze::class.java, 10, 1, 4))

        val layer: WE_BiomeLayer = WE_BiomeLayer()
        layer.add(Blocks.PACKED_ICE.defaultBlockState(), tg.worldStoneBlock, -256, 0, -15, -3, true)
        layer.add(Blocks.WATER.defaultBlockState(), tg.worldStoneBlock, -256, 0, -63, 0, true)


        //layer.add(Blocks.PACKED_ICE.defaultBlockState(), Blocks.PACKED_ICE.defaultBlockState(), -256, 0,   -2, -1,  true);
        layer.add(Blocks.BEDROCK.defaultBlockState(), 0, 2, 0, 0, true)


        createChunkGen_InXZ_List.add(layer)
    }

    override fun decorateBiome(world: World?, rand: Random?, x: Int, z: Int) {
        var randPosX: Int
        var randPosY: Int
        var randPosZ: Int

        for (i: Int in 0..11) {
            randPosX = x + (rand)!!.nextInt(16)
            //randPosY = rand.nextInt(120);
            randPosZ = z + (rand)!!.nextInt(16)
            val pos: BlockPos = (world)!!.getHeight(BlockPos(randPosX, 0, randPosZ))
            if ((world)!!.isAreaLoaded(pos, 13, false) && (world)!!.isEmptyBlock(pos.above())) {
                WorldGenMinable(
                    GSBlocks.SURFACE_ICE.defaultBlockState()
                        .setValue(BlockSurfaceIce.BASIC_TYPE, EnumBlockIce.DRY_ICE),
                    25,
                    BlockPredicate(
                        Blocks.PACKED_ICE.defaultBlockState()
                    )
                ).generate(world, rand, pos)
            }
        }

        for (i: Int in 0..11) {
            randPosX = x + (rand)!!.nextInt(16)
            randPosY = (rand)!!.nextInt(110)
            randPosZ = z + (rand)!!.nextInt(16)
            val pos: BlockPos = BlockPos(randPosX, randPosY, randPosZ)

            WorldGenMinableMeta(GSBlocks.EUROPA_BLOCKS, 30, 2, true, Blocks.PACKED_ICE, 0).generate((world)!!, rand, pos)
        }

        if ((world)!!.random.nextInt(3) == 0) {
            randPosX = x + (rand)!!.nextInt(16) + 8
            //randPosY = rand.nextInt(120);
            randPosZ = z + (rand)!!.nextInt(16) + 8
            var pos: BlockPos = (world)!!.getHeight(BlockPos(randPosX, 0, randPosZ))



            if ((world)!!.getBlockState(pos.below()) === Blocks.PACKED_ICE.defaultBlockState() && (world)!!.isEmptyBlock(pos)) {
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
                    (world)!!.setBlockAndUpdate(pos, GSBlocks.EUROPA_UWGEYSER.defaultBlockState())

                    for (i: Int in 1..14) (world)!!.setBlockAndUpdate(pos.below(i), Blocks.WATER.defaultBlockState())
                }


                //world.setBlock(pos.down(), GSBlocks.EUROPA_UWGEYSER.defaultBlockState(), 2);
            }
        }
    }
}
