package galaxyspace.systems.ACentauriSystem.planets.proxima_b.world.gen.we

import foundation.block.getStateFromMeta

import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_BiomeLayer
import galaxyspace.systems.ACentauriSystem.core.ACBlocks
import galaxyspace.systems.ACentauriSystem.planets.proxima_b.blocks.Proxima_B_Blocks.EnumBlockProximaB
import galaxyspace.systems.ACentauriSystem.planets.proxima_b.blocks.Proxima_B_Blocks
import galaxyspace.core.prefab.world.gen.WorldGenPool
import asmodeuscore.core.utils.worldengine.WE_Biome
import micdoodle8.mods.galacticraft.core.GCFluids
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedEnderman
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedSkeleton
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedSpider
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedZombie
import net.minecraft.world.level.block.Blocks
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import java.util.Random

open class Proxima_B_Plains constructor() :
    WE_Biome(BiomeProperties("proxima_b_plains"), intArrayOf(0x00FF00, 0xEEDD44, 0x00FF00)) {
    init {
        biomeMinValueOnMap = -0.4
        biomeMaxValueOnMap = -0.2
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
            ACBlocks.PROXIMA_B_BLOCKS.getStateFromMeta(0),
            ACBlocks.PROXIMA_B_BLOCKS.getStateFromMeta(1),
            -256,
            0,
            -256,
            0,
            false
        )
        standardBiomeLayers.add(Blocks.BEDROCK.defaultBlockState(), 0, 2, 0, 0, true)
        createChunkGen_InXZ_List.add(standardBiomeLayers)
    }

    override fun decorateBiome(world: World?, rand: Random?, x: Int, z: Int) {
        var randPosX: Int = x + (rand)!!.nextInt(16) + 8
        var randPosZ: Int = z + (rand)!!.nextInt(16) + 8
        var pos: BlockPos = (world)!!.getHeight(BlockPos(randPosX, 0, randPosZ))

        for (i: Int in 0..2) {
            randPosX = x + (rand)!!.nextInt(16) + 8
            //randPosY = this.rand.nextInt(256);
            randPosZ = z + (rand)!!.nextInt(16) + 8
            pos = (world)!!.getHeight(BlockPos(randPosX, 0, randPosZ))

            if ((world)!!.getBlockState(pos.below()) === ACBlocks.PROXIMA_B_BLOCKS.defaultBlockState()
                    .setValue(
                        Proxima_B_Blocks.BASIC_TYPE,
                        EnumBlockProximaB.SURFACE
                    )
            ) (world)!!.setBlockAndUpdate(pos, Blocks.DEADBUSH.defaultBlockState())
        }

        val kx: Int = x + (rand)!!.nextInt(16) + 8
        val kz: Int = x + (rand)!!.nextInt(16) + 8

        if ((rand)!!.nextInt(50) == 0) {
            val l2: Int = (world)!!.getTopSolidOrLiquidBlock(BlockPos(kx, 0, kz)).getY() - 20 - (rand)!!.nextInt(25)
            WorldGenPool(
                ACBlocks.PROXIMA_B_BLOCKS.defaultBlockState().setValue(
                    Proxima_B_Blocks.BASIC_TYPE,
                    EnumBlockProximaB.STONE
                ), (GCFluids.fluidOil)!!.getBlock().defaultBlockState(), 5
            ).generate((world)!!, (rand)!!, BlockPos(x, l2, z))
        }
    }
}
