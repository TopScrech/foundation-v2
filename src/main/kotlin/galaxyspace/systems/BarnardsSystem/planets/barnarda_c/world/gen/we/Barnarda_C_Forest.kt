package galaxyspace.systems.BarnardsSystem.planets.barnarda_c.world.gen.we

import foundation.block.getStateFromMeta

import foundation.level.offset

import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Blocks.EnumBlockBarnardaC
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Dandelions
import galaxyspace.systems.BarnardsSystem.core.BRBlocks
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Grass
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_BiomeLayer
import galaxyspace.systems.BarnardsSystem.core.configs.BRConfigCore
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.world.gen.we.Barnarda_C_Forest
import galaxyspace.core.prefab.world.gen.we.WE_LakesGen
import asmodeuscore.core.astronomy.dimension.world.gen.features.trees.WorldGenTree_Forest
import asmodeuscore.core.astronomy.dimension.world.gen.features.trees.WorldGenTree_Forest2
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Blocks
import asmodeuscore.core.utils.worldengine.WE_Biome
import net.minecraft.block.material.Material
import net.minecraft.entity.monster.EntityEnderman
import net.minecraft.entity.monster.EntitySkeleton
import net.minecraft.entity.monster.EntitySpider
import net.minecraft.entity.monster.EntityZombie
import net.minecraft.entity.passive.EntityChicken
import net.minecraft.entity.passive.EntityPig
import net.minecraft.entity.passive.EntitySheep
import net.minecraft.entity.passive.EntitySquid
import net.minecraft.world.level.block.Blocks
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import java.util.Random

open class Barnarda_C_Forest constructor(min: Double, max: Double) :
    WE_Biome(BiomeProperties("barnarda_c_forest"), intArrayOf(grasscolor, 0x11FF66, 0x985cff)) {
    init {
        biomeMinValueOnMap = min
        biomeMaxValueOnMap = max
        biomePersistence = 1.8
        biomeNumberOfOctaves = 4
        biomeScaleX = 280.0
        biomeScaleY = 1.7
        biomeSurfaceHeight = 75
        biomeInterpolateQuality = 30
        biomeTemerature = 0.7
        //-//
        decorateChunkGen_List.clear()
        createChunkGen_InXZ_List.clear()

        this.spawnableCreatureList.add(SpawnListEntry(EntitySheep::class.java, 3, 3, 7))
        this.spawnableCreatureList.add(SpawnListEntry(EntityChicken::class.java, 5, 3, 7))
        this.spawnableCreatureList.add(SpawnListEntry(EntityPig::class.java, 4, 3, 7))

        this.spawnableMonsterList.add(SpawnListEntry(EntityZombie::class.java, 10, 1, 2))
        this.spawnableMonsterList.add(SpawnListEntry(EntitySpider::class.java, 10, 1, 2))
        this.spawnableMonsterList.add(SpawnListEntry(EntitySkeleton::class.java, 10, 1, 2))
        this.spawnableMonsterList.add(SpawnListEntry(EntityEnderman::class.java, 10, 1, 1))

        this.spawnableWaterCreatureList.add(SpawnListEntry(EntitySquid::class.java, 40, 1, 7))


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

        standardBiomeLayers.add(Blocks.BEDROCK.defaultBlockState(), 0, 2, 0, 0, true)
        createChunkGen_InXZ_List.add(standardBiomeLayers)

        var lakes: WE_LakesGen = WE_LakesGen()
        lakes.lakeBlock = Blocks.WATER.defaultBlockState()
        lakes.iceGen = false
        lakes.chunksForLake = 6
        decorateChunkGen_List.add(lakes)

        lakes = WE_LakesGen()
        lakes.lakeBlock = Blocks.LAVA.defaultBlockState()
        lakes.iceGen = false
        lakes.chunksForLake = 100
        decorateChunkGen_List.add(lakes)
    }

    override fun decorateBiome(world: World?, rand: Random?, x: Int, z: Int) {
        var randPosX: Int
        var randPosZ: Int
        var pos: BlockPos

        for (i: Int in 0..39) {
            randPosX = x + (rand)!!.nextInt(16) + 8
            randPosZ = z + (rand)!!.nextInt(16) + 8
            pos = (world)!!.getHeight(BlockPos(randPosX, 0, randPosZ))

            if ((world)!!.getBlockState(pos.below()) === BRBlocks.BARNARDA_C_GRASS.defaultBlockState()
                    .setValue(
                        Barnarda_C_Grass.BASIC_TYPE,
                        Barnarda_C_Grass.EnumBlockGrass.GRASS
                    )
            ) (world)!!.setBlockAndUpdate(
                pos,
                BRBlocks.BARNARDA_C_DANDELIONS.defaultBlockState()
                    .setValue(
                        Barnarda_C_Dandelions.BASIC_TYPE,
                        Barnarda_C_Dandelions.EnumBlockDandelions.GRASS
                    )
            )
        }

        if ((rand)!!.nextInt(5) == 0) {
            randPosX = x + (rand)!!.nextInt(16) + 8
            randPosZ = z + (rand)!!.nextInt(16) + 8
            pos = (world)!!.getHeight(BlockPos(randPosX, 0, randPosZ))

            if ((world)!!.getBlockState(pos.below()) === BRBlocks.BARNARDA_C_GRASS.defaultBlockState()
                    .setValue(
                        Barnarda_C_Grass.BASIC_TYPE,
                        Barnarda_C_Grass.EnumBlockGrass.GRASS
                    )
            ) (world)!!.setBlockAndUpdate(
                pos,
                BRBlocks.BARNARDA_C_DANDELIONS.defaultBlockState()
                    .setValue(
                        Barnarda_C_Dandelions.BASIC_TYPE,
                        Barnarda_C_Dandelions.EnumBlockDandelions.HOPPER
                    )
            )
        }

        for (i: Int in 0..104) {
            randPosX = x + (rand)!!.nextInt(16) + 8
            randPosZ = z + (rand)!!.nextInt(16) + 8
            pos = (world)!!.getHeight(BlockPos(randPosX, 0, randPosZ))

            var cangen: Boolean = true

            cangen = true
            for (pos1: BlockPos in BlockPos.getAllInBox(pos.offset(-3, -1, -3), pos.offset(3, -1, 3))) if ((world)!!.isEmptyBlock(
                    pos1
                ) || (world)!!.getBlockState(pos1) === BRBlocks.BARNARDA_C_VIOLET_LOG.getStateFromMeta(0)
            ) cangen = (world)!!.getBlockState(pos1).getMaterial() === Material.WATER

            if (!(world)!!.isAreaLoaded(
                    pos,
                    18,
                    false
                )
            ) if (cangen && pos.getY() > 50 && (world)!!.getBlockState(pos.below()) === BRBlocks.BARNARDA_C_GRASS.defaultBlockState()
                    .setValue(
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

        for (i: Int in 0..1) {
            for (y: Int in 40..149) {
                randPosX = x + (rand)!!.nextInt(16) + 8
                randPosZ = z + (rand)!!.nextInt(16) + 8
                pos = BlockPos(randPosX, y, randPosZ)

                if ((world)!!.isEmptyBlock(pos.below()) && (world)!!.getBlockState(pos.above()) === BRBlocks.BARNARDA_C_LEAVES.getStateFromMeta(
                        0
                    )
                ) {
                    (world)!!.setBlockAndUpdate(
                        pos,
                        BRBlocks.BARNARDA_C_DANDELIONS.defaultBlockState()
                            .setValue(
                                Barnarda_C_Dandelions.BASIC_TYPE,
                                Barnarda_C_Dandelions.EnumBlockDandelions.LEAVES_BALLS
                            )
                    )
                }
            }
        }

        for (i: Int in 0..39) {
            randPosX = x + (rand)!!.nextInt(16) + 8
            randPosZ = z + (rand)!!.nextInt(16) + 8
            pos = (world)!!.getHeight(BlockPos(randPosX, 0, randPosZ))

            var cangen: Boolean = false
            val range: Int = 2
            for (pos1: BlockPos in BlockPos.getAllInBox(
                pos.offset(-range, -range, -range),
                pos.offset(range, range, range)
            )) if ((world)!!.getBlockState(pos1).getMaterial() === Material.WATER) cangen = true

            if (cangen &&
                ((world)!!.getBlockState(pos.below()) === BRBlocks.BARNARDA_C_GRASS.defaultBlockState()
                    .setValue(
                        Barnarda_C_Grass.BASIC_TYPE,
                        Barnarda_C_Grass.EnumBlockGrass.GRASS
                    ) ||
                        (world)!!.getBlockState(pos.below()) === BRBlocks.BARNARDA_C_BLOCKS.defaultBlockState()
                    .setValue(
                        Barnarda_C_Blocks.BASIC_TYPE,
                        EnumBlockBarnardaC.DIRT
                    ))
            ) {
                (world)!!.setBlockAndUpdate(
                    pos,
                    BRBlocks.BARNARDA_C_DANDELIONS.defaultBlockState()
                        .setValue(
                            Barnarda_C_Dandelions.BASIC_TYPE,
                            Barnarda_C_Dandelions.EnumBlockDandelions.LIGHT_BALLS
                        )
                )
            }
        }
    }

    companion object {
        private val grasscolor = if (BRConfigCore.enableGreenBarnardaC) 0x55BB44 else 0xba55d3
    }
}
