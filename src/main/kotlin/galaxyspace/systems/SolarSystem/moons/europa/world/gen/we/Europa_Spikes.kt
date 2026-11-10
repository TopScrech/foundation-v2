package galaxyspace.systems.SolarSystem.moons.europa.world.gen.we

import galaxyspace.core.GSBlocks
import galaxyspace.core.prefab.entities.EntityEvolvedColdBlaze
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_BiomeLayer
import galaxyspace.systems.SolarSystem.moons.europa.world.gen.we.Europa_Spikes
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_OreGen.BlockPredicate
import asmodeuscore.core.utils.worldengine.WE_Biome
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_TerrainGenerator
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.gen.feature.WorldGenMinable
import java.util.Random

open class Europa_Spikes constructor(tg: WE_TerrainGenerator) :
    WE_Biome(BiomeProperties("europa_spikes"), intArrayOf(0x00CC00, 0xFFFFFF, 0x00CC00)) {
    init {
        //biomeMinValueOnMap      =  	-0.4D;
        biomeMaxValueOnMap = 2.5
        biomePersistence = 1.6
        biomeNumberOfOctaves = 5
        biomeScaleX = 280.0
        biomeScaleY = 1.7
        biomeSurfaceHeight = 130
        biomeInterpolateQuality = 1

        biomeAbs = true


        //-//
        decorateChunkGen_List.clear()
        createChunkGen_InXZ_List.clear()
        spawnableMonsterList.add(SpawnListEntry(EntityEvolvedColdBlaze::class.java, 10, 1, 4))

        val layer: WE_BiomeLayer = WE_BiomeLayer()
        layer.add(Blocks.PACKED_ICE.getDefaultState(), tg.worldStoneBlock, -256, 0, 65, -3, true)


        layer.add(
            GSBlocks.EUROPA_BLOCKS.getStateFromMeta(0),
            Blocks.PACKED_ICE.getDefaultState(),
            -256,
            0,
            -2,
            -1,
            true
        )
        layer.add(Blocks.WATER.getDefaultState(), tg.worldStoneBlock, -256, 0, -100, 0, true)
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
                    GSBlocks.SURFACE_ICE.getStateFromMeta(4), 45,
                    BlockPredicate(
                        GSBlocks.EUROPA_BLOCKS.getStateFromMeta(0)
                    )
                ).generate(world, rand, pos)
            }
        }
    }
}
