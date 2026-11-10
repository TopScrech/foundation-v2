package galaxyspace.systems.BarnardsSystem.planets.barnarda_c.world.gen.we

import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Dandelions
import galaxyspace.systems.BarnardsSystem.core.BRBlocks
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Grass
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_BiomeLayer
import galaxyspace.systems.BarnardsSystem.core.configs.BRConfigCore
import galaxyspace.core.prefab.world.gen.we.WE_LakesGen
import asmodeuscore.core.astronomy.dimension.world.gen.features.trees.WorldGenTree_Forest
import asmodeuscore.core.astronomy.dimension.world.gen.features.trees.WorldGenTree_Forest2
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.world.gen.we.Barnarda_C_Plains
import asmodeuscore.core.utils.worldengine.WE_Biome
import net.minecraft.entity.monster.EntityEnderman
import net.minecraft.entity.monster.EntitySkeleton
import net.minecraft.entity.monster.EntitySpider
import net.minecraft.entity.monster.EntityZombie
import net.minecraft.entity.passive.EntityChicken
import net.minecraft.entity.passive.EntitySheep
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.Random

open class Barnarda_C_Plains constructor(min: Double, max: Double) :
    WE_Biome(BiomeProperties("barnarda_c_plains"), intArrayOf(grasscolor, 0x11FF66, 0x985cff)) {
    init {
        biomeMinValueOnMap = min
        biomeMaxValueOnMap = max
        biomePersistence = 1.4
        biomeNumberOfOctaves = 4
        biomeScaleX = 280.0
        biomeScaleY = 1.7
        biomeSurfaceHeight = 69
        biomeInterpolateQuality = 25
        biomeTemerature = 0.4
        //-//
        decorateChunkGen_List.clear()
        createChunkGen_InXZ_List.clear()

        this.spawnableCreatureList.add(SpawnListEntry(EntitySheep::class.java, 4, 3, 7))
        this.spawnableCreatureList.add(SpawnListEntry(EntityChicken::class.java, 4, 3, 7))

        this.spawnableMonsterList.add(SpawnListEntry(EntityZombie::class.java, 10, 2, 6))
        this.spawnableMonsterList.add(SpawnListEntry(EntitySpider::class.java, 10, 1, 4))
        this.spawnableMonsterList.add(SpawnListEntry(EntitySkeleton::class.java, 10, 2, 6))
        this.spawnableMonsterList.add(SpawnListEntry(EntityEnderman::class.java, 10, 1, 1))

        val standardBiomeLayers: WE_BiomeLayer = WE_BiomeLayer()
        standardBiomeLayers.add(
            BRBlocks.BARNARDA_C_BLOCKS.getStateFromMeta(3),
            BRBlocks.BARNARDA_C_BLOCKS.getStateFromMeta(1),
            -256,
            0,
            -5,
            -1,
            true
        )
        standardBiomeLayers.add(
            BRBlocks.BARNARDA_C_BLOCKS.getStateFromMeta(0),
            BRBlocks.BARNARDA_C_BLOCKS.getStateFromMeta(3),
            -256,
            0,
            -1,
            -1,
            true
        )
        standardBiomeLayers.add(
            BRBlocks.BARNARDA_C_GRASS.getStateFromMeta(0),
            BRBlocks.BARNARDA_C_BLOCKS.getStateFromMeta(0),
            -256,
            0,
            -256,
            0,
            false
        )
        standardBiomeLayers.add(Blocks.BEDROCK.getDefaultState(), 0, 2, 0, 0, true)
        createChunkGen_InXZ_List.add(standardBiomeLayers)

        val lakes: WE_LakesGen = WE_LakesGen()
        lakes.lakeBlock = Blocks.WATER.getDefaultState()
        lakes.iceGen = false
        lakes.chunksForLake = 8
        decorateChunkGen_List.add(lakes)
    }

    override fun decorateBiome(world: World?, rand: Random?, x: Int, z: Int) {
        var randPosX: Int
        var randPosZ: Int
        var pos: BlockPos

        for (i: Int in 0..179) {
            randPosX = x + (rand)!!.nextInt(16) + 8
            randPosZ = z + (rand)!!.nextInt(16) + 8
            pos = (world)!!.getHeight(BlockPos(randPosX, 0, randPosZ))

            if ((world)!!.getBlockState(pos.down()) === BRBlocks.BARNARDA_C_GRASS.getDefaultState()
                    .withProperty(
                        Barnarda_C_Grass.BASIC_TYPE,
                        Barnarda_C_Grass.EnumBlockGrass.GRASS
                    )
            ) (world)!!.setBlockState(
                pos,
                BRBlocks.BARNARDA_C_DANDELIONS.getDefaultState()
                    .withProperty(
                        Barnarda_C_Dandelions.BASIC_TYPE,
                        Barnarda_C_Dandelions.EnumBlockDandelions.GRASS
                    )
            )
        }

        if ((rand)!!.nextInt(5) == 0) {
            randPosX = x + (rand)!!.nextInt(16) + 8
            randPosZ = z + (rand)!!.nextInt(16) + 8
            pos = (world)!!.getHeight(BlockPos(randPosX, 0, randPosZ))

            var cangen: Boolean = true

            cangen = true
            for (pos1: BlockPos in BlockPos.getAllInBox(pos.add(-3, -1, -3), pos.add(3, -1, 3))) if ((world)!!.isAirBlock(
                    pos1
                ) || (world)!!.getBlockState(pos1) === BRBlocks.BARNARDA_C_VIOLET_GLOW_LOG.getStateFromMeta(0)
            ) cangen = false

            if (!(world)!!.isAreaLoaded(
                    pos,
                    13,
                    false
                )
            ) if (cangen && (world)!!.getBlockState(pos.down()) === BRBlocks.BARNARDA_C_GRASS.getDefaultState()
                    .withProperty(
                        Barnarda_C_Grass.BASIC_TYPE,
                        Barnarda_C_Grass.EnumBlockGrass.GRASS
                    )
            ) {
                when ((rand)!!.nextInt(2)) {
                    0 -> WorldGenTree_Forest(
                        BRBlocks.BARNARDA_C_VIOLET_LOG.getStateFromMeta(0),
                        BRBlocks.BARNARDA_C_LEAVES.getStateFromMeta(0),
                        (rand)!!.nextInt(3)
                    ).generate((world)!!, (rand)!!, pos)

                    1 -> WorldGenTree_Forest2(
                        BRBlocks.BARNARDA_C_VIOLET_LOG.getStateFromMeta(0),
                        BRBlocks.BARNARDA_C_LEAVES.getStateFromMeta(0),
                        (rand)!!.nextInt(3)
                    ).generate((world)!!, (rand)!!, pos)
                }
            }
        }
    }

    companion object {
        private val grasscolor = if (BRConfigCore.enableGreenBarnardaC) 0x88BB44 else 0x822899
    }
}
