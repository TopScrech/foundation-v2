package galaxyspace.systems.BarnardsSystem.planets.barnarda_c.world.gen.we

import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Blocks.EnumBlockBarnardaC
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Dandelions
import galaxyspace.systems.BarnardsSystem.core.BRBlocks
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Grass
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_BiomeLayer
import galaxyspace.systems.BarnardsSystem.core.configs.BRConfigCore
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Blocks
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.world.gen.WorldGenTree_Small
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.world.gen.we.Barnarda_C_Mountains
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_SnowGen
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_OreGen.BlockPredicate
import asmodeuscore.core.utils.worldengine.WE_Biome
import net.minecraft.block.material.Material
import net.minecraft.entity.monster.EntityEnderman
import net.minecraft.entity.monster.EntitySkeleton
import net.minecraft.entity.monster.EntitySpider
import net.minecraft.entity.monster.EntityZombie
import net.minecraft.entity.passive.EntityCow
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.gen.feature.WorldGenMinable
import java.util.Random

open class Barnarda_C_Mountains constructor(min: Double, max: Double, height: Int, per: Double, octaves: Int) :
    WE_Biome(BiomeProperties("barnarda_c_mountains_" + height), intArrayOf(grasscolor, 0x11FF66, 0x985cff)) {
    init {
        biomeMinValueOnMap = min
        biomeMaxValueOnMap = max
        biomePersistence = per
        biomeNumberOfOctaves = octaves
        biomeScaleX = 280.0
        biomeScaleY = 1.7
        biomeSurfaceHeight = height
        biomeInterpolateQuality = 35
        biomeTemerature = (if (height > 100) 0.2f else 0.4f).toDouble()


        //-//
        decorateChunkGen_List.clear()
        createChunkGen_InXZ_List.clear()

        this.spawnableMonsterList.add(SpawnListEntry(EntityZombie::class.java, 10, 1, 2))
        this.spawnableMonsterList.add(SpawnListEntry(EntitySpider::class.java, 10, 1, 1))
        this.spawnableMonsterList.add(SpawnListEntry(EntitySkeleton::class.java, 10, 1, 2))
        this.spawnableMonsterList.add(SpawnListEntry(EntityEnderman::class.java, 10, 1, 2))

        this.spawnableCreatureList.add(SpawnListEntry(EntityCow::class.java, 4, 4, 4))

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

        val snowGen: WE_SnowGen = WE_SnowGen()
        snowGen.snowPoint = 120
        snowGen.randomSnowPoint = 8
        snowGen.snowBlock = Blocks.SNOW.getDefaultState()
        snowGen.iceBlock = Blocks.ICE.getDefaultState()
        snowGen.freezeMaterial = Material.WATER
        createChunkGen_InXZ_List.add(snowGen)
    }

    override fun decorateBiome(world: World?, rand: Random?, x: Int, z: Int) {
        var randPosX: Int = x + (rand)!!.nextInt(16) + 8
        var randPosZ: Int = z + (rand)!!.nextInt(16) + 8
        var pos: BlockPos = (world)!!.getHeight(BlockPos(randPosX, 0, randPosZ))

        if ((rand)!!.nextInt(2) == 0) {
            randPosX = x + (rand)!!.nextInt(16) + 8
            randPosZ = z + (rand)!!.nextInt(16) + 8
            pos = (world)!!.getHeight(BlockPos(randPosX, 0, randPosZ))

            if (pos.getY() > 120 && pos.getY() < 125) {
                (world)!!.setBlockState(pos, Blocks.FLOWING_WATER.getDefaultState(), 3)
            }
        }

        for (i: Int in 0..39) {
            randPosX = x + (rand)!!.nextInt(16) + 8
            randPosZ = z + (rand)!!.nextInt(16) + 8
            pos = (world)!!.getHeight(BlockPos(randPosX, 0, randPosZ))

            if ((world)!!.isAreaLoaded(pos, 13, false)) WorldGenMinable(
                BRBlocks.BARNARDA_C_BLOCKS.getDefaultState().withProperty(
                    Barnarda_C_Blocks.BASIC_TYPE,
                    EnumBlockBarnardaC.STONE
                ), 25,
                BlockPredicate(
                    BRBlocks.BARNARDA_C_GRASS.getDefaultState(),
                    BRBlocks.BARNARDA_C_BLOCKS.getDefaultState().withProperty(
                        Barnarda_C_Blocks.BASIC_TYPE,
                        EnumBlockBarnardaC.DIRT
                    ),
                    BRBlocks.BARNARDA_C_BLOCKS.getDefaultState().withProperty(
                        Barnarda_C_Blocks.BASIC_TYPE,
                        EnumBlockBarnardaC.DIRT_1
                    ),
                    BRBlocks.BARNARDA_C_BLOCKS.getDefaultState().withProperty(
                        Barnarda_C_Blocks.BASIC_TYPE,
                        EnumBlockBarnardaC.STONE
                    )
                )
            ).generate(world, rand, pos)
        }

        for (y: Int in 0..9) {
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

        if ((rand)!!.nextInt(10) == 0 && (world)!!.getBlockState(pos.down()) === BRBlocks.BARNARDA_C_GRASS.getDefaultState()
                .withProperty(
                    Barnarda_C_Grass.BASIC_TYPE,
                    Barnarda_C_Grass.EnumBlockGrass.GRASS
                )
        ) {
            randPosX = x + (rand)!!.nextInt(16) + 8
            randPosZ = z + (rand)!!.nextInt(16) + 8
            pos = (world)!!.getHeight(BlockPos(randPosX, 0, randPosZ))

            WorldGenTree_Small(
                BRBlocks.BARNARDA_C_VIOLET_LOG.getDefaultState(),
                BRBlocks.BARNARDA_C_LEAVES.getStateFromMeta(0),
                (rand)!!.nextInt(3)
            ).generate((world)!!, (rand)!!, pos)
        }
    }

    companion object {
        private val grasscolor = if (BRConfigCore.enableGreenBarnardaC) 0x89AC76 else 0x822899
    }
}
