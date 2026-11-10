package galaxyspace.systems.BarnardsSystem.planets.barnarda_c.world.gen.we

import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Dandelions
import galaxyspace.systems.BarnardsSystem.core.BRBlocks
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Grass
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.world.gen.WorldGenTree_Jungle
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_BiomeLayer
import galaxyspace.systems.BarnardsSystem.core.configs.BRConfigCore
import galaxyspace.core.prefab.world.gen.we.WE_LakesGen
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.world.gen.we.Barnarda_C_Jungle
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.world.gen.WorldGenTree_Small
import asmodeuscore.core.utils.worldengine.WE_Biome
import net.minecraft.entity.monster.EntityEnderman
import net.minecraft.entity.monster.EntitySkeleton
import net.minecraft.entity.monster.EntitySpider
import net.minecraft.entity.monster.EntityZombie
import net.minecraft.entity.passive.EntityChicken
import net.minecraft.entity.passive.EntityOcelot
import net.minecraft.entity.passive.EntitySquid
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.Random

open class Barnarda_C_Jungle constructor(min: Double, max: Double) :
    WE_Biome(BiomeProperties("barnarda_c_jungle"), intArrayOf(grasscolor, 0x11FF66, 0x985cff)) {
    init {
        biomeMinValueOnMap = min
        biomeMaxValueOnMap = max
        biomePersistence = 1.4
        biomeNumberOfOctaves = 4
        biomeScaleX = 280.0
        biomeScaleY = 1.7
        biomeSurfaceHeight = 68
        biomeInterpolateQuality = 30
        biomeTemerature = 0.9
        //-//
        decorateChunkGen_List.clear()
        createChunkGen_InXZ_List.clear()

        this.spawnableCreatureList.add(SpawnListEntry(EntityChicken::class.java, 10, 4, 4))
        this.spawnableMonsterList.add(SpawnListEntry(EntityOcelot::class.java, 2, 1, 1))

        this.spawnableMonsterList.add(SpawnListEntry(EntityZombie::class.java, 10, 1, 2))
        this.spawnableMonsterList.add(SpawnListEntry(EntitySpider::class.java, 10, 1, 2))
        this.spawnableMonsterList.add(SpawnListEntry(EntitySkeleton::class.java, 10, 1, 2))
        this.spawnableMonsterList.add(SpawnListEntry(EntityEnderman::class.java, 10, 1, 1))

        this.spawnableWaterCreatureList.add(SpawnListEntry(EntitySquid::class.java, 10, 1, 3))


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

        /*	
		WE_LakesGen lakes = new WE_LakesGen();
		lakes.lakeBlock = Blocks.WATER.getDefaultState();
		lakes.iceGen = false;
		lakes.chunksForLake = 20;
		decorateChunkGen_List.add(lakes);
		
		lakes = new WE_LakesGen();
		lakes.lakeBlock = Blocks.LAVA.getDefaultState();
		lakes.iceGen = false;
		lakes.chunksForLake = 100;
		decorateChunkGen_List.add(lakes);	*/
    }

    override fun decorateBiome(world: World?, rand: Random?, x: Int, z: Int) {
        var randPosX: Int = x + (rand)!!.nextInt(16) + 8
        var randPosZ: Int = z + (rand)!!.nextInt(16) + 8
        var pos: BlockPos = (world)!!.getHeight(BlockPos(randPosX, 0, randPosZ))


        for (i: Int in 0..4) {
            randPosX = x + (rand)!!.nextInt(16)
            randPosZ = z + (rand)!!.nextInt(16)
            pos = (world)!!.getHeight(BlockPos(randPosX, 0, randPosZ))

            var cangen: Boolean = false
            val range: Int = 5
            for (kx: Int in -range..<range) {
                if (cangen) break
                for (kz: Int in -range..<range) if ((world)!!.isAirBlock(pos.down()) || pos.getY() < 64) cangen = true
            }

            if (!cangen) {
                if ((rand)!!.nextInt(50) == 0) WorldGenTree_Jungle(
                    BRBlocks.BARNARDA_C_VIOLET_LOG.getStateFromMeta(0),
                    BRBlocks.BARNARDA_C_LEAVES.getStateFromMeta(0),
                    2,
                    1
                )
                    .generate((world)!!, (rand)!!, pos)
                else WorldGenTree_Jungle(
                    BRBlocks.BARNARDA_C_VIOLET_LOG.getStateFromMeta(0),
                    BRBlocks.BARNARDA_C_LEAVES.getStateFromMeta(0),
                    1,
                    (rand)!!.nextInt(2) + 1
                ).generate((world)!!, (rand)!!, pos)
            }
        }

        for (i: Int in 0..15) {
            randPosX = x + (rand)!!.nextInt(16) + 8
            randPosZ = z + (rand)!!.nextInt(16) + 8
            pos = (world)!!.getHeight(BlockPos(randPosX, 0, randPosZ))

            if ((world)!!.getBlockState(pos.down()) === BRBlocks.BARNARDA_C_GRASS.getDefaultState()
                    .withProperty(
                        Barnarda_C_Grass.BASIC_TYPE,
                        Barnarda_C_Grass.EnumBlockGrass.GRASS
                    )
            ) {
                (world)!!.setBlockState(pos, BRBlocks.BARNARDA_C_VIOLET_LOG.getStateFromMeta(0))

                if ((rand)!!.nextInt(10) == 0) {
                    (world)!!.setBlockState(pos.up(), BRBlocks.BARNARDA_C_VIOLET_LOG.getStateFromMeta(0))
                    pos = pos.add(0, 1, 0)
                }

                (world)!!.setBlockState(pos.east(), BRBlocks.BARNARDA_C_LEAVES.getStateFromMeta(0))
                (world)!!.setBlockState(pos.west(), BRBlocks.BARNARDA_C_LEAVES.getStateFromMeta(0))
                (world)!!.setBlockState(pos.north(), BRBlocks.BARNARDA_C_LEAVES.getStateFromMeta(0))
                (world)!!.setBlockState(pos.south(), BRBlocks.BARNARDA_C_LEAVES.getStateFromMeta(0))

                (world)!!.setBlockState(pos.east(2), BRBlocks.BARNARDA_C_LEAVES.getStateFromMeta(0))
                (world)!!.setBlockState(pos.west(2), BRBlocks.BARNARDA_C_LEAVES.getStateFromMeta(0))
                (world)!!.setBlockState(pos.north(2), BRBlocks.BARNARDA_C_LEAVES.getStateFromMeta(0))
                (world)!!.setBlockState(pos.south(2), BRBlocks.BARNARDA_C_LEAVES.getStateFromMeta(0))

                (world)!!.setBlockState(pos.east().north(), BRBlocks.BARNARDA_C_LEAVES.getStateFromMeta(0))
                (world)!!.setBlockState(pos.east().south(), BRBlocks.BARNARDA_C_LEAVES.getStateFromMeta(0))
                (world)!!.setBlockState(pos.west().north(), BRBlocks.BARNARDA_C_LEAVES.getStateFromMeta(0))
                (world)!!.setBlockState(pos.west().south(), BRBlocks.BARNARDA_C_LEAVES.getStateFromMeta(0))

                (world)!!.setBlockState(pos.up(), BRBlocks.BARNARDA_C_LEAVES.getStateFromMeta(0))
            }
        }

        for (i: Int in 0..1) {
            randPosX = x + (rand)!!.nextInt(16) + 12
            randPosZ = z + (rand)!!.nextInt(16) + 12
            pos = (world)!!.getHeight(BlockPos(randPosX, 0, randPosZ))

            if ((world)!!.getBlockState(pos.down()) === BRBlocks.BARNARDA_C_GRASS.getDefaultState()
                    .withProperty(
                        Barnarda_C_Grass.BASIC_TYPE,
                        Barnarda_C_Grass.EnumBlockGrass.GRASS
                    )
            ) WorldGenTree_Small(
                BRBlocks.BARNARDA_C_VIOLET_LOG.getDefaultState(),
                BRBlocks.BARNARDA_C_LEAVES.getStateFromMeta(0),
                (rand)!!.nextInt(3)
            ).generate((world)!!, (rand)!!, pos)
        }

        for (i: Int in 0..19) {
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

        if ((rand)!!.nextInt(100) < 10) for (i: Int in 0..14) {
            randPosX = x + (rand)!!.nextInt(16) + 8
            randPosZ = z + (rand)!!.nextInt(16) + 8
            pos = (world)!!.getHeight(BlockPos(randPosX, 0, randPosZ))

            if ((world)!!.getBlockState(pos.down()) === BRBlocks.BARNARDA_C_GRASS.getDefaultState()
                    .withProperty(
                        Barnarda_C_Grass.BASIC_TYPE,
                        Barnarda_C_Grass.EnumBlockGrass.GRASS
                    )
            ) (world)!!.setBlockState(pos, BRBlocks.BARNARDA_C_GLOW_DANDELIONS.getStateFromMeta((rand)!!.nextInt(4)))
        }

        for (i: Int in 0..7) {
            for (y: Int in 40..149) {
                randPosX = x + (rand)!!.nextInt(16) + 8
                randPosZ = z + (rand)!!.nextInt(16) + 8
                pos = BlockPos(randPosX, y, randPosZ)

                if ((world)!!.isAirBlock(pos.down()) && (world)!!.getBlockState(pos.up()) === BRBlocks.BARNARDA_C_LEAVES.getStateFromMeta(
                        0
                    )
                ) {
                    (world)!!.setBlockState(
                        pos,
                        BRBlocks.BARNARDA_C_DANDELIONS.getDefaultState()
                            .withProperty(
                                Barnarda_C_Dandelions.BASIC_TYPE,
                                Barnarda_C_Dandelions.EnumBlockDandelions.LEAVES_BALLS
                            )
                    )
                }
            }
        }

        for (i: Int in 0..35) {
            randPosX = x + (rand)!!.nextInt(16) + 8
            randPosZ = z + (rand)!!.nextInt(16) + 8
            pos = BlockPos(randPosX, 64 + (rand)!!.nextInt(50), randPosZ)

            if ((world)!!.getBlockState(pos) === BRBlocks.BARNARDA_C_VIOLET_LOG.getStateFromMeta(0)) (world)!!.setBlockState(
                pos,
                BRBlocks.BARNARDA_C_VIOLET_GLOW_LOG.getStateFromMeta(0)
            )
        }

        for (i: Int in 0..11) {
            for (y: Int in 40..149) {
                randPosX = x + (rand)!!.nextInt(16) + 8
                randPosZ = z + (rand)!!.nextInt(16) + 8
                pos = BlockPos(randPosX, y, randPosZ)

                if ((world)!!.getBlockState(pos.west()) === BRBlocks.BARNARDA_C_LEAVES.getStateFromMeta(0) || (world)!!.getBlockState(
                        pos.west()
                    ) === BRBlocks.BARNARDA_C_VIOLET_LOG.getStateFromMeta(0)
                ) for (size: Int in 0..<(rand)!!.nextInt(15) + 5) if ((world)!!.isAirBlock(pos.down(size))) (world)!!.setBlockState(
                    pos.down(size),
                    Blocks.VINE.getStateFromMeta(2)
                )
                else break

                if ((world)!!.getBlockState(pos.east()) === BRBlocks.BARNARDA_C_LEAVES.getStateFromMeta(0) || (world)!!.getBlockState(
                        pos.east()
                    ) === BRBlocks.BARNARDA_C_VIOLET_LOG.getStateFromMeta(0)
                ) for (size: Int in 0..<(rand)!!.nextInt(15) + 5) if ((world)!!.isAirBlock(pos.down(size))) (world)!!.setBlockState(
                    pos.down(size),
                    Blocks.VINE.getStateFromMeta(8)
                )
                else break

                if ((world)!!.getBlockState(pos.north()) === BRBlocks.BARNARDA_C_LEAVES.getStateFromMeta(0) || (world)!!.getBlockState(
                        pos.north()
                    ) === BRBlocks.BARNARDA_C_VIOLET_LOG.getStateFromMeta(0)
                ) for (size: Int in 0..<(rand)!!.nextInt(15) + 5) if ((world)!!.isAirBlock(pos.down(size))) (world)!!.setBlockState(
                    pos.down(size),
                    Blocks.VINE.getStateFromMeta(4)
                )
                else break

                if ((world)!!.getBlockState(pos.south()) === BRBlocks.BARNARDA_C_LEAVES.getStateFromMeta(0) || (world)!!.getBlockState(
                        pos.south()
                    ) === BRBlocks.BARNARDA_C_VIOLET_LOG.getStateFromMeta(0)
                ) for (size: Int in 0..<(rand)!!.nextInt(15) + 5) if ((world)!!.isAirBlock(pos.down(size))) (world)!!.setBlockState(
                    pos.down(size),
                    Blocks.VINE.getStateFromMeta(1)
                )
                else break
            }
        }
    }

    companion object {
        private val grasscolor = if (BRConfigCore.enableGreenBarnardaC) 0x45AC33 else 0xdf73ff
    }
}
