package micdoodle8.mods.galacticraft.planets.venus.world.gen

import foundation.level.offset

import java.util.Random

import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeAdaptive
import micdoodle8.mods.galacticraft.api.prefab.world.gen.MapGenBaseMeta
import micdoodle8.mods.galacticraft.api.world.ChunkProviderBase
import micdoodle8.mods.galacticraft.core.perlin.generator.Gradient
import micdoodle8.mods.galacticraft.planets.venus.VenusBlocks
import micdoodle8.mods.galacticraft.planets.venus.blocks.BlockBasicVenus
import micdoodle8.mods.galacticraft.planets.venus.world.gen.dungeon.DungeonConfigurationVenus
import micdoodle8.mods.galacticraft.planets.venus.world.gen.dungeon.MapGenDungeonVenus
import micdoodle8.mods.galacticraft.planets.venus.world.gen.dungeon.RoomBossVenus
import micdoodle8.mods.galacticraft.planets.venus.world.gen.dungeon.RoomTreasureVenus
import net.minecraft.block.BlockFalling
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.entity.EnumCreatureType
import net.minecraft.core.BlockPos
import net.minecraft.world.level.ChunkPos
import net.minecraft.util.Mth as MathHelper
import net.minecraft.world.level.Level as World
import net.minecraft.world.WorldEntitySpawner
import net.minecraft.world.WorldType
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.chunk.LevelChunk as Chunk
import net.minecraft.world.chunk.ChunkPrimer
import net.minecraft.world.gen.NoiseGenerator
import net.minecraft.world.gen.NoiseGeneratorOctaves
import net.minecraft.world.gen.NoiseGeneratorPerlin
import java.util.*

open class ChunkProviderVenus constructor(private val world: World, seed: Long, mapFeaturesEnabled: Boolean) :
    ChunkProviderBase() {
    private val biomeDecoratorVenus = BiomeDecoratorVenus()
    private val rand: Random
    private var noiseGen1: NoiseGeneratorOctaves
    private var noiseGen2: NoiseGeneratorOctaves
    private var noiseGen3: NoiseGeneratorOctaves
    private var noiseGen4: NoiseGeneratorPerlin
    private var noiseGen5: NoiseGeneratorOctaves
    private var noiseGen6: NoiseGeneratorOctaves
    private var mobSpawnerNoise: NoiseGeneratorOctaves
    private val noiseGenSmooth1: Gradient
    private val worldType: WorldType
    private val terrainCalcs: DoubleArray
    private val parabolicField: FloatArray
    private var stoneNoise = DoubleArray(256)
    private val caveGenerator: MapGenBaseMeta = MapGenCaveVenus()
    private val lavaCaveGenerator: MapGenBaseMeta = MapGenLavaVenus()
    private var biomesForGeneration: Array<Biome>? = null
    private var octaves1: DoubleArray? = null
    private var octaves2: DoubleArray? = null
    private var octaves3: DoubleArray? = null
    private var octaves4: DoubleArray? = null
    private val dungeonGenerator = MapGenDungeonVenus(
        DungeonConfigurationVenus(
            (VenusBlocks.venusBlock)!!.defaultBlockState()
                .setValue(
                    BlockBasicVenus.BASIC_TYPE_VENUS,
                    BlockBasicVenus.EnumBlockBasicVenus.DUNGEON_BRICK_1
                ),
            (VenusBlocks.venusBlock)!!.defaultBlockState()
                .setValue(
                    BlockBasicVenus.BASIC_TYPE_VENUS,
                    BlockBasicVenus.EnumBlockBasicVenus.DUNGEON_BRICK_2
                ),
            30, 8, 16, 7, 7, RoomBossVenus::class.java, RoomTreasureVenus::class.java
        )
    )

    init {
        this.worldType = world.getWorldInfo().getTerrainType()
        this.rand = Random(seed)
        this.noiseGen1 = NoiseGeneratorOctaves(this.rand, 16)
        this.noiseGen2 = NoiseGeneratorOctaves(this.rand, 16)
        this.noiseGen3 = NoiseGeneratorOctaves(this.rand, 8)
        this.noiseGen4 = NoiseGeneratorPerlin(this.rand, 4)
        this.noiseGen5 = NoiseGeneratorOctaves(this.rand, 10)
        this.noiseGen6 = NoiseGeneratorOctaves(this.rand, 16)
        this.mobSpawnerNoise = NoiseGeneratorOctaves(this.rand, 8)
        this.noiseGenSmooth1 = Gradient(this.rand.nextLong(), 4, 0.25f)
        this.terrainCalcs = DoubleArray(825)
        this.parabolicField = FloatArray(25)

        for (i: Int in -2..2) {
            for (j: Int in -2..2) {
                val f: Float = 10.0f / MathHelper.sqrt((i * i + j * j).toFloat() + 0.2f)
                this.parabolicField[i + 2 + (j + 2) * 5] = f
            }
        }

        val noiseGens: Array<NoiseGenerator?> =
            arrayOf<NoiseGenerator?>(noiseGen1, noiseGen2, noiseGen3, noiseGen4, noiseGen5, noiseGen6, mobSpawnerNoise)
        this.noiseGen1 = noiseGens[0] as NoiseGeneratorOctaves
        this.noiseGen2 = noiseGens[1] as NoiseGeneratorOctaves
        this.noiseGen3 = noiseGens[2] as NoiseGeneratorOctaves
        this.noiseGen4 = noiseGens[3] as NoiseGeneratorPerlin
        this.noiseGen5 = (noiseGens[4] as net.minecraft.world.gen.NoiseGeneratorOctaves?)!!
        this.noiseGen6 = noiseGens[5] as NoiseGeneratorOctaves
        this.mobSpawnerNoise = (noiseGens[6] as net.minecraft.world.gen.NoiseGeneratorOctaves?)!!
    }

    private fun setBlocksInChunk(chunkX: Int, chunkZ: Int, primer: ChunkPrimer) {
        this.noiseGenSmooth1.setFrequency(0.015f)
        this.biomesForGeneration = this.world.getBiomeProvider()
            .getBiomesForGeneration(this.biomesForGeneration, chunkX * 4 - 2, chunkZ * 4 - 2, 10, 10)
        this.createLandPerBiome(chunkX * 4, chunkZ * 4)

        for (i: Int in 0..3) {
            val j: Int = i * 5
            val k: Int = (i + 1) * 5

            for (l: Int in 0..3) {
                val i1: Int = (j + l) * 33
                val j1: Int = (j + l + 1) * 33
                val k1: Int = (k + l) * 33
                val l1: Int = (k + l + 1) * 33

                for (i2: Int in 0..31) {
                    val d0: Double = 0.125
                    var d1: Double = this.terrainCalcs[i1 + i2]
                    var d2: Double = this.terrainCalcs[j1 + i2]
                    var d3: Double = this.terrainCalcs[k1 + i2]
                    var d4: Double = this.terrainCalcs[l1 + i2]
                    val d5: Double = (this.terrainCalcs[i1 + i2 + 1] - d1) * d0
                    val d6: Double = (this.terrainCalcs[j1 + i2 + 1] - d2) * d0
                    val d7: Double = (this.terrainCalcs[k1 + i2 + 1] - d3) * d0
                    val d8: Double = (this.terrainCalcs[l1 + i2 + 1] - d4) * d0

                    for (j2: Int in 0..7) {
                        val d9: Double = 0.25
                        var d10: Double = d1
                        var d11: Double = d2
                        val d12: Double = (d3 - d1) * d9
                        val d13: Double = (d4 - d2) * d9

                        for (k2: Int in 0..3) {
                            val d14: Double = 0.25
                            val d16: Double = (d11 - d10) * d14
                            var lvt_45_1_: Double = d10 - d16

                            for (l2: Int in 0..3) {
                                if ((d16.let { lvt_45_1_ += it; lvt_45_1_ }) > this.noiseGenSmooth1.getNoise(
                                        (chunkX * 16 + (i * 4 + k2)).toFloat(),
                                        (chunkZ * 16 + (l * 4 + l2)).toFloat()
                                    ) * 20.0
                                ) {
                                    primer.setBlockState(i * 4 + k2, i2 * 8 + j2, l * 4 + l2, BLOCK_FILL)
                                }
                            }

                            d10 += d12
                            d11 += d13
                        }

                        d1 += d5
                        d2 += d6
                        d3 += d7
                        d4 += d8
                    }
                }
            }
        }
    }

    private fun replaceBlocksForBiome(
        p_180517_1_: Int,
        p_180517_2_: Int,
        p_180517_3_: ChunkPrimer,
        p_180517_4_: Array<Biome>
    ) {
        val d0: Double = 0.03125
        this.stoneNoise = this.noiseGen4.getRegion(
            this.stoneNoise,
            (p_180517_1_ * 16).toDouble(),
            (p_180517_2_ * 16).toDouble(),
            16,
            16,
            d0 * 2.0,
            d0 * 2.0,
            1.0
        )

        for (i: Int in 0..15) {
            for (j: Int in 0..15) {
                val biomegenbase: Biome = p_180517_4_[j + i * 16]
                biomegenbase.genTerrainBlocks(
                    this.world,
                    this.rand,
                    p_180517_3_,
                    p_180517_1_ * 16 + i,
                    p_180517_2_ * 16 + j,
                    this.stoneNoise[j + i * 16]
                )
            }
        }
    }

    override fun generateChunk(x: Int, z: Int): Chunk {
        this.rand.setSeed(x.toLong() * 341873128712L + z.toLong() * 132897987541L)
        val chunkprimer: ChunkPrimer = ChunkPrimer()
        this.setBlocksInChunk(x, z, chunkprimer)
        this.biomesForGeneration =
            this.world.getBiomeProvider().getBiomes(this.biomesForGeneration, x * 16, z * 16, 16, 16)

        this.lavaCaveGenerator.generate(this.world, x, z, chunkprimer)

        this.replaceBlocksForBiome(x, z, chunkprimer, (this.biomesForGeneration)!!)

        this.caveGenerator.generate(this.world, x, z, chunkprimer)
        this.dungeonGenerator.generate(this.world, x, z, chunkprimer)

        val chunk: Chunk = Chunk(this.world, chunkprimer, x, z)
        val abyte: ByteArray = chunk.getBiomeArray()

        for (i: Int in abyte.indices) {
            abyte[i] = Biome.getIdForBiome((this.biomesForGeneration)!![i]).toByte()
        }

        chunk.generateSkylightMap()
        return chunk
    }

    private fun createLandPerBiome(x: Int, z: Int) {
        this.octaves4 = this.noiseGen6.generateNoiseOctaves(this.octaves4, x, z, 5, 5, 2000.0, 2000.0, 0.5)
        this.octaves1 = this.noiseGen3.generateNoiseOctaves(
            this.octaves1,
            x,
            0,
            z,
            5,
            33,
            5,
            8.555150000000001,
            4.277575000000001,
            8.555150000000001
        )
        this.octaves2 = this.noiseGen1.generateNoiseOctaves(this.octaves2, x, 0, z, 5, 33, 5, 684.412, 684.412, 684.412)
        this.octaves3 = this.noiseGen2.generateNoiseOctaves(this.octaves3, x, 0, z, 5, 33, 5, 684.412, 684.412, 684.412)
        var i: Int = 0
        var j: Int = 0

        for (k: Int in 0..4) {
            for (l: Int in 0..4) {
                var f2: Float = 0.0f
                var f3: Float = 0.0f
                var f4: Float = 0.0f
                val i1: Int = 2
                val biomegenbase: Biome = ((this)!!.biomesForGeneration)!![k + 2 + (l + 2) * 10]

                for (j1: Int in -i1..i1) {
                    for (k1: Int in -i1..i1) {
                        val biomegenbase1: Biome = ((this)!!.biomesForGeneration)!![k + j1 + 2 + (l + k1 + 2) * 10]
                        var f5: Float = biomegenbase1.getBaseHeight()
                        var f6: Float = biomegenbase1.getHeightVariation()

                        if (this.worldType === WorldType.AMPLIFIED && f5 > 0.0f) {
                            f5 = 1.0f + f5 * 2.0f
                            f6 = 1.0f + f6 * 4.0f
                        }

                        var f7: Float = this.parabolicField[j1 + 2 + (k1 + 2) * 5] / (f5 + 2.0f)

                        if (biomegenbase1.getBaseHeight() > biomegenbase.getBaseHeight()) {
                            f7 /= 2.0f
                        }

                        f2 += f6 * f7
                        f3 += f5 * f7
                        f4 += f7
                    }
                }

                f2 = f2 / f4
                f3 = f3 / f4
                f2 = f2 * 0.9f + 0.1f
                f3 = (f3 * 4.0f - 1.0f) / 8.0f
                var d7: Double = (this.octaves4)!![j] / 4000.0

                if (d7 < 0.0) {
                    d7 = -d7 * 0.3
                }

                d7 = d7 * 3.0 - 2.0

                if (d7 < 0.0) {
                    d7 = d7 / 2.0

                    if (d7 < -1.0) {
                        d7 = -1.0
                    }

                    d7 = d7 / 1.4
                    d7 = d7 / 2.0
                } else {
                    if (d7 > 1.0) {
                        d7 = 1.0
                    }

                    d7 = d7 / 8.0
                }

                ++j
                var d8: Double = f3.toDouble()
                val d9: Double = f2.toDouble()
                d8 = d8 + d7 * 0.2
                d8 = d8 * 8.5 / 8.0
                val d0: Double = 8.5 + d8 * 4.0

                for (l1: Int in 0..32) {
                    var d1: Double = (l1.toDouble() - d0) * 12.0 * 128.0 / 256.0 / d9

                    if (d1 < 0.0) {
                        d1 *= 4.0
                    }

                    val d2: Double = (this.octaves2)!![i] / 512.0
                    val d3: Double = (this.octaves3)!![i] / 1024.0
                    val d4: Double = ((this.octaves1)!![i] / 10.0 + 1.0) / 2.0
                    //                    double d5 = MathHelper.clampedLerp(d2, d3, d4) - d1;
                    var d5: Double = d3 - d1

                    if (l1 > 29) {
                        val d6: Double = ((l1 - 29).toFloat() / 3.0f).toDouble()
                        d5 = d5 * (1.0 - d6) + -10.0 * d6
                    }

                    this.terrainCalcs[i] = d5
                    ++i
                }
            }
        }
    }

    override fun populate(x: Int, z: Int) {
        BlockFalling.fallInstantly = true
        val i: Int = x * 16
        val j: Int = z * 16
        val blockpos: BlockPos = BlockPos(i, 0, j)
        val biomegenbase: Biome = this.world.getBiome(blockpos.offset(16, 0, 16))
        this.rand.setSeed(this.world.getSeed())
        val k: Long = this.rand.nextLong() / 2L * 2L + 1L
        val l: Long = this.rand.nextLong() / 2L * 2L + 1L
        this.rand.setSeed(x.toLong() * k + z.toLong() * l xor this.world.getSeed())
        val isValley: Boolean =
            biomegenbase is BiomeAdaptive && biomegenbase.isInstance(BiomeGenVenusValley::class.java)

        if (this.rand.nextInt(if (isValley) 3 else 10) == 0) {
            val i2: Int = this.rand.nextInt(16) + 8
            val l2: Int = this.rand.nextInt(this.rand.nextInt(248) + 8)
            val k3: Int = this.rand.nextInt(16) + 8

            (WorldGenLakesVenus()).generate(this.world, this.rand, blockpos.offset(i2, l2, k3))
        }


        if (isValley) {
            if (this.rand.nextInt(5) == 0) {
                val i2: Int = this.rand.nextInt(16) + 8
                val k3: Int = this.rand.nextInt(16) + 8
                val l2: Int =
                    this.world.getTopSolidOrLiquidBlock(blockpos.offset(i2, 0, k3)).getY() - 10 - this.rand.nextInt(5)

                (WorldGenVaporPool()).generate(this.world, this.rand, blockpos.offset(i2, l2, k3))
            } else if (this.rand.nextInt(190) == 0) {
                val i2: Int = this.rand.nextInt(16) + 8
                val k3: Int = this.rand.nextInt(16) + 8
                val l2: Int = this.world.getTopSolidOrLiquidBlock(blockpos.offset(i2, 0, k3)).getY()

                (WorldGenCrashedProbe()).generate(this.world, this.rand, blockpos.offset(i2, l2, k3))
            }
        }

        this.dungeonGenerator.generateStructure(this.world, this.rand, ChunkPos(x, z))

        biomegenbase.decorate(this.world, this.rand, BlockPos(i, 0, j))
        WorldEntitySpawner.performWorldGenSpawning(this.world, biomegenbase, i + 8, j + 8, 16, 16, this.rand)

        BlockFalling.fallInstantly = false
    }

    override fun getPossibleCreatures(
        creatureType: EnumCreatureType,
        pos: BlockPos
    ): MutableList<Biome.SpawnListEntry?> {
        val biomegenbase: Biome = this.world.getBiome(pos)

        return biomegenbase.getSpawnableList(creatureType)
    }

    override fun recreateStructures(chunk: Chunk, x: Int, z: Int) {
        this.dungeonGenerator.generate(this.world, x, z, null)
    }

    companion object {
        @JvmField
        val BLOCK_FILL: IBlockState = (VenusBlocks.venusBlock)!!.defaultBlockState()
            .setValue(
                BlockBasicVenus.BASIC_TYPE_VENUS,
                BlockBasicVenus.EnumBlockBasicVenus.ROCK_HARD
            )
    }
}
