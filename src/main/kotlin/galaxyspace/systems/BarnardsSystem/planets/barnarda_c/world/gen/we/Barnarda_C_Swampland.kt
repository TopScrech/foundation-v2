package galaxyspace.systems.BarnardsSystem.planets.barnarda_c.world.gen.we

import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Blocks.EnumBlockBarnardaC
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Dandelions
import galaxyspace.systems.BarnardsSystem.core.BRBlocks
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Grass
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_BiomeLayer
import galaxyspace.systems.BarnardsSystem.core.configs.BRConfigCore
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Blocks
import galaxyspace.core.prefab.world.gen.WorldGenCircleBlock
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.world.gen.we.Barnarda_C_Swampland
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.world.gen.WorldGenTree_Swampland
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.world.gen.WorldGenTree_Swampland_2
import asmodeuscore.core.utils.worldengine.WE_Biome
import net.minecraft.block.material.Material
import net.minecraft.entity.monster.EntitySkeleton
import net.minecraft.entity.monster.EntitySlime
import net.minecraft.entity.monster.EntitySpider
import net.minecraft.entity.monster.EntityZombie
import net.minecraft.entity.passive.EntityPig
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.Random

open class Barnarda_C_Swampland constructor(min: Double, max: Double) :
    WE_Biome(BiomeProperties("barnarda_c_swampland"), intArrayOf(grasscolor, 0x11CC44, 0x985cff)) {
    init {
        biomeMinValueOnMap = min
        biomeMaxValueOnMap = max
        biomePersistence = 1.1
        biomeNumberOfOctaves = 6
        biomeScaleX = 280.0
        biomeScaleY = 1.7
        biomeSurfaceHeight = 64
        biomeInterpolateQuality = 5
        biomeTemerature = 0.3
        //-//
        decorateChunkGen_List.clear()
        createChunkGen_InXZ_List.clear()

        this.spawnableCreatureList.add(SpawnListEntry(EntityPig::class.java, 10, 1, 4))

        this.spawnableMonsterList.add(SpawnListEntry(EntityZombie::class.java, 10, 1, 4))
        this.spawnableMonsterList.add(SpawnListEntry(EntitySpider::class.java, 10, 1, 4))
        this.spawnableMonsterList.add(SpawnListEntry(EntitySkeleton::class.java, 10, 1, 4))
        this.spawnableMonsterList.add(SpawnListEntry(EntitySlime::class.java, 10, 1, 1))

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
    }

    override fun decorateBiome(world: World?, rand: Random?, x: Int, z: Int) {
        var randPosX: Int = x + (rand)!!.nextInt(16) + 8
        var randPosZ: Int = z + (rand)!!.nextInt(16) + 8
        var pos: BlockPos = (world)!!.getHeight(BlockPos(randPosX, 0, randPosZ))


        var circle: WorldGenCircleBlock = WorldGenCircleBlock(
            Blocks.CLAY.getDefaultState(),
            (rand)!!.nextInt(5) + 3,
            BRBlocks.BARNARDA_C_BLOCKS.getStateFromMeta(0)
        )
        if ((rand)!!.nextInt(8) == 0) {
            randPosX = x + (rand)!!.nextInt(8) + 8
            randPosZ = z + (rand)!!.nextInt(8) + 8
            pos = (world)!!.getHeight(BlockPos(randPosX, 0, randPosZ))


            //if(world.getBlockState(pos).getMaterial() == Material.WATER)
            circle.generate((world)!!, (rand)!!, pos.down())
        }
        circle = WorldGenCircleBlock(
            Blocks.GRAVEL.getDefaultState(),
            (rand)!!.nextInt(8) + 5,
            BRBlocks.BARNARDA_C_BLOCKS.getStateFromMeta(0)
        )
        if ((rand)!!.nextInt(2) == 0) {
            randPosX = x + (rand)!!.nextInt(8) + 8
            randPosZ = z + (rand)!!.nextInt(8) + 8
            pos = (world)!!.getHeight(BlockPos(randPosX, 0, randPosZ))
            circle.generate((world)!!, (rand)!!, pos.down())
        }

        for (i: Int in 0..4) {
            randPosX = x + (rand)!!.nextInt(16) + 8
            randPosZ = z + (rand)!!.nextInt(16) + 8
            pos = (world)!!.getHeight(BlockPos(randPosX, 0, randPosZ))


            var cangen: Boolean = true
            for (pos1: BlockPos in BlockPos.getAllInBox(pos.add(-3, -1, -3), pos.add(3, -1, 3))) if ((world)!!.isAirBlock(
                    pos1
                ) || (world)!!.getBlockState(pos1) === BRBlocks.BARNARDA_C_VIOLET_LOG.getStateFromMeta(0)
            ) cangen = (world)!!.getBlockState(pos1).getMaterial() === Material.WATER

            while (!(world)!!.isBlockNormalCube(pos, true)) {
                pos = pos.down()
            }
            if (!(world)!!.isAreaLoaded(
                    pos,
                    10,
                    false
                )
            ) if (cangen && ((world)!!.getBlockState(pos.down()) === BRBlocks.BARNARDA_C_GRASS.getDefaultState()
                    .withProperty(
                        Barnarda_C_Grass.BASIC_TYPE,
                        Barnarda_C_Grass.EnumBlockGrass.GRASS
                    ) || (world)!!.getBlockState(pos.down()) === BRBlocks.BARNARDA_C_BLOCKS.getDefaultState()
                    .withProperty(
                        Barnarda_C_Blocks.BASIC_TYPE,
                        EnumBlockBarnardaC.DIRT
                    ) || (world)!!.getBlockState(pos.down()) === BRBlocks.BARNARDA_C_BLOCKS.getDefaultState()
                    .withProperty(
                        Barnarda_C_Blocks.BASIC_TYPE,
                        EnumBlockBarnardaC.DIRT_1
                    ))
            ) {
                when ((rand)!!.nextInt(2)) {
                    0 -> WorldGenTree_Swampland(
                        BRBlocks.BARNARDA_C_VIOLET_LOG.getStateFromMeta(0),
                        BRBlocks.BARNARDA_C_LEAVES.getStateFromMeta(0),
                        (rand)!!.nextInt(3)
                    ).generate((world)!!, (rand)!!, pos)

                    1 -> WorldGenTree_Swampland_2(
                        BRBlocks.BARNARDA_C_VIOLET_LOG.getStateFromMeta(0),
                        BRBlocks.BARNARDA_C_LEAVES.getStateFromMeta(0),
                        (rand)!!.nextInt(3)
                    ).generate((world)!!, (rand)!!, pos)
                }
            }
        }

        for (i: Int in 0..15) {
            for (k: Int in 0..15) {
                randPosX = x + i + 8
                randPosZ = z + k + 8

                pos = (world)!!.getTopSolidOrLiquidBlock(BlockPos(randPosX, 0, randPosZ))

                if ((world)!!.getBlockState(pos).getMaterial() === Material.WATER && (rand)!!.nextInt(2) == 0) {
                    (world)!!.setBlockState(pos.up(), BRBlocks.BARNARDA_C_WATER_GRASS.getDefaultState())
                }
            }
        }

        for (i: Int in 0..29) {
            randPosX = x + (rand)!!.nextInt(16) + 8
            randPosZ = z + (rand)!!.nextInt(16) + 8
            pos = (world)!!.getHeight(BlockPos(randPosX, 0, randPosZ))

            var cangen: Boolean = false
            val range: Int = 2
            for (pos1: BlockPos in BlockPos.getAllInBox(
                pos.add(-range, -range, -range),
                pos.add(range, range, range)
            )) if ((world)!!.getBlockState(pos1).getMaterial() === Material.WATER) cangen = true

            if (cangen &&
                ((world)!!.getBlockState(pos.down()) === BRBlocks.BARNARDA_C_GRASS.getDefaultState()
                    .withProperty(
                        Barnarda_C_Grass.BASIC_TYPE,
                        Barnarda_C_Grass.EnumBlockGrass.GRASS
                    ) ||
                        (world)!!.getBlockState(pos.down()) === BRBlocks.BARNARDA_C_BLOCKS.getDefaultState()
                    .withProperty(
                        Barnarda_C_Blocks.BASIC_TYPE,
                        EnumBlockBarnardaC.DIRT
                    ))
            ) {
                if ((rand)!!.nextInt(15) == 0) {
                    for (size: Int in 0..<2 + (rand)!!.nextInt(2)) {
                        if ((rand)!!.nextBoolean()) (world)!!.setBlockState(
                            pos.up(size),
                            BRBlocks.BARNARDA_C_DANDELIONS.getDefaultState()
                                .withProperty(
                                    Barnarda_C_Dandelions.BASIC_TYPE,
                                    Barnarda_C_Dandelions.EnumBlockDandelions.REEDS
                                )
                        )
                        else (world)!!.setBlockState(
                            pos.up(size),
                            BRBlocks.BARNARDA_C_DANDELIONS.getDefaultState()
                                .withProperty(
                                    Barnarda_C_Dandelions.BASIC_TYPE,
                                    Barnarda_C_Dandelions.EnumBlockDandelions.REEDS_FRUITS
                                )
                        )
                    }
                } else (world)!!.setBlockState(
                    pos,
                    BRBlocks.BARNARDA_C_DANDELIONS.getDefaultState()
                        .withProperty(
                            Barnarda_C_Dandelions.BASIC_TYPE,
                            Barnarda_C_Dandelions.EnumBlockDandelions.LIGHT_BALLS
                        )
                )
            }
        }
    }

    companion object {
        private val grasscolor = if (BRConfigCore.enableGreenBarnardaC) 0x54732a else 0x822899
    }
}
