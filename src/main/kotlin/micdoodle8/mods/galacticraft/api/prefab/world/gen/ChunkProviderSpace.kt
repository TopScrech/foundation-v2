package micdoodle8.mods.galacticraft.api.prefab.world.gen

import foundation.block.getStateFromMeta

import java.util.List

import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair
import micdoodle8.mods.galacticraft.api.world.ChunkProviderBase
import micdoodle8.mods.galacticraft.core.perlin.generator.Gradient
import micdoodle8.mods.galacticraft.core.util.GCLog
import micdoodle8.mods.galacticraft.core.world.gen.EnumCraterSize
import net.minecraft.world.level.block.Block
import net.minecraft.block.BlockFalling
import net.minecraft.entity.EnumCreatureType
import net.minecraft.world.level.block.Blocks
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.chunk.LevelChunk as Chunk
import net.minecraft.world.chunk.ChunkPrimer
import java.lang.Exception
import java.util.Random
import kotlin.math.abs

/**
 * Do not include this prefab class in your released mod download.
 */
abstract class ChunkProviderSpace(@JvmField protected val world: World, seed: Long, mapFeaturesEnabled: Boolean) :
    ChunkProviderBase() {
    @JvmField
    protected val rand: Random

    private val noiseGen1: Gradient
    private val noiseGen2: Gradient
    private val noiseGen3: Gradient
    private val noiseGen4: Gradient
    private val noiseGen5: Gradient
    private val noiseGen6: Gradient
    private val noiseGen7: Gradient

    private var biomesForGeneration: Array<Biome?> = this.getBiomesForGeneration()

    private val TERRAIN_HEIGHT_MOD = this.getHeightModifier()
    private val SMALL_FEATURE_HEIGHT_MOD = this.getSmallFeatureHeightModifier()
    private val MOUNTAIN_HEIGHT_MOD = this.getMountainHeightModifier()
    private val VALLEY_HEIGHT_MOD = this.getValleyHeightModifier()
    private val CRATER_PROB = this.getCraterProbability()

    // DO NOT CHANGE
    private val MID_HEIGHT = this.getSeaLevel()
    private var worldGenerators: MutableList<MapGenBaseMeta>? = null

    init {
        this.rand = Random(seed)

        this.noiseGen1 = Gradient(this.rand.nextLong(), 4, 0.25f)
        this.noiseGen2 = Gradient(this.rand.nextLong(), 4, 0.25f)
        this.noiseGen3 = Gradient(this.rand.nextLong(), 4, 0.25f)
        this.noiseGen4 = Gradient(this.rand.nextLong(), 2, 0.25f)
        this.noiseGen5 = Gradient(this.rand.nextLong(), 1, 0.25f)
        this.noiseGen6 = Gradient(this.rand.nextLong(), 1, 0.25f)
        this.noiseGen7 = Gradient(this.rand.nextLong(), 1, 0.25f)
    }

    fun generateTerrain(chunkX: Int, chunkZ: Int, primer: ChunkPrimer) {
        this.noiseGen1.setFrequency(0.015f)
        this.noiseGen2.setFrequency(0.01f)
        this.noiseGen3.setFrequency(0.01f)
        this.noiseGen4.setFrequency(0.01f)
        this.noiseGen5.setFrequency(0.01f)
        this.noiseGen6.setFrequency(0.001f)
        this.noiseGen7.setFrequency(0.005f)

        for (x: Int in 0..<CHUNK_SIZE_X) {
            for (z: Int in 0..<CHUNK_SIZE_Z) {
                val baseHeight: Double = this.noiseGen1.getNoise(
                    (chunkX * 16 + x).toFloat(),
                    (chunkZ * 16 + z).toFloat()
                ) * this.TERRAIN_HEIGHT_MOD
                val smallHillHeight: Double = this.noiseGen2.getNoise(
                    (chunkX * 16 + x).toFloat(),
                    (chunkZ * 16 + z).toFloat()
                ) * this.SMALL_FEATURE_HEIGHT_MOD
                var mountainHeight: Double =
                    abs(this.noiseGen3.getNoise((chunkX * 16 + x).toFloat(), (chunkZ * 16 + z).toFloat())).toDouble()
                var valleyHeight: Double =
                    abs(this.noiseGen4.getNoise((chunkX * 16 + x).toFloat(), (chunkZ * 16 + z).toFloat())).toDouble()
                val featureFilter: Double = this.noiseGen5.getNoise(
                    (chunkX * 16 + x).toFloat(),
                    (chunkZ * 16 + z).toFloat()
                ) * MAIN_FEATURE_FILTER_MOD
                val largeFilter: Double = this.noiseGen6.getNoise(
                    (chunkX * 16 + x).toFloat(),
                    (chunkZ * 16 + z).toFloat()
                ) * LARGE_FEATURE_FILTER_MOD
                val smallFilter: Double = this.noiseGen7.getNoise(
                    (chunkX * 16 + x).toFloat(),
                    (chunkZ * 16 + z).toFloat()
                ) * SMALL_FEATURE_FILTER_MOD - 0.5
                mountainHeight = this.lerp(
                    smallHillHeight,
                    mountainHeight * this.MOUNTAIN_HEIGHT_MOD,
                    this.fade(this.clamp(mountainHeight * 2, 0.0, 1.0))
                )
                valleyHeight = this.lerp(
                    smallHillHeight,
                    valleyHeight * this.VALLEY_HEIGHT_MOD - this.VALLEY_HEIGHT_MOD + 9,
                    this.fade(this.clamp((valleyHeight + 2) * 4, 0.0, 1.0))
                )

                var yDev: Double = this.lerp(valleyHeight, mountainHeight, this.fade(largeFilter))
                yDev = this.lerp(smallHillHeight, yDev, smallFilter)
                yDev = this.lerp(baseHeight, yDev, featureFilter)

                for (y: Int in 0..<CHUNK_SIZE_Y) {
                    if (y < this.MID_HEIGHT + yDev) {
                        primer.setBlockState(
                            x,
                            y,
                            z,
                            (this.getStoneBlock()!!.getBlock())!!
                                .getStateFromMeta(this.getStoneBlock()!!.getMetadata().toInt())
                        )
                        //                        idArray[this.getIndex(x, y, z)] = this.getStoneBlock().getBlock();
//                        metaArray[this.getIndex(x, y, z)] = this.getStoneBlock().getMetadata();
                    }
                }
            }
        }
    }

    private fun lerp(d1: Double, d2: Double, t: Double): Double {
        if (t < 0.0) {
            return d1
        } else if (t > 1.0) {
            return d2
        } else {
            return d1 + (d2 - d1) * t
        }
    }

    private fun fade(n: Double): Double {
        return n * n * n * (n * (n * 6 - 15) + 10)
    }

    private fun clamp(x: Double, min: Double, max: Double): Double {
        if (x < min) {
            return min
        }
        if (x > max) {
            return max
        }
        return x
    }

    //    public void replaceBiomeBlocks(int x, int z, ChunkPrimer primer, Biome[] biomesIn)
    fun replaceBiomeBlocks(x: Int, z: Int, primer: ChunkPrimer, biomesIn: Array<Biome?>?) {
        val var5: Int = 20
        val var6: Float = 0.03125f
        this.noiseGen4.setFrequency(var6 * 2)
        for (var8: Int in 0..15) {
            for (var9: Int in 0..15) {
                val var12: Int = (this.noiseGen4.getNoise(
                    (x * 16 + var8).toFloat(),
                    (z * 16 + var9).toFloat()
                ) / 3.0 + 3.0 + this.rand.nextDouble() * 0.25).toInt()
                var var13: Int = -1
                var var14: Block = (this.getGrassBlock()!!.getBlock())!!
                var var14m: Byte = this.getGrassBlock()!!.getMetadata()
                var var15: Block = (this.getDirtBlock()!!.getBlock())!!
                var var15m: Byte = this.getDirtBlock()!!.getMetadata()

                for (var16: Int in CHUNK_SIZE_Y - 1 downTo 0) {
                    val index: Int = this.getIndex(var8, var16, var9)

                    if (var16 <= 0 + this.rand.nextInt(5)) {
                        primer.setBlockState(var8, var16, var9, Blocks.BEDROCK.defaultBlockState())
                        //                        arrayOfIDs[index] = Blocks.BEDROCK;
                    } else {
//                        final Block var18 = arrayOfIDs[index];
                        val var18: Block = primer.getBlockState(var8, var16, var9).getBlock()

                        if (Blocks.AIR === var18) {
                            var13 = -1
                        } else if (var18 === this.getStoneBlock()!!.getBlock()) {
//                            arrayOfMeta[index] = this.getStoneBlock().getMetadata();

                            if (var13 == -1) {
                                if (var12 <= 0) {
                                    var14 = Blocks.AIR
                                    var14m = 0
                                    var15 = (this.getStoneBlock()!!.getBlock())!!
                                    var15m = this.getStoneBlock()!!.getMetadata()
                                } else if (var16 >= var5 - -16 && var16 <= var5 + 1) {
                                    var14 = (this.getGrassBlock()!!.getBlock())!!
                                    var14m = this.getGrassBlock()!!.getMetadata()
                                    var14 = (this.getDirtBlock()!!.getBlock())!!
                                    var14m = this.getDirtBlock()!!.getMetadata()
                                }

                                var13 = var12

                                if (var16 >= var5 - 1) {
//                                    arrayOfIDs[index] = var14;
//                                    arrayOfMeta[index] = var14m;
                                    primer.setBlockState(var8, var16, var9, var14.getStateFromMeta(var14m.toInt()))
                                } else {
//                                    arrayOfIDs[index] = var15;
//                                    arrayOfMeta[index] = var15m;
                                    primer.setBlockState(var8, var16, var9, var15.getStateFromMeta(var15m.toInt()))
                                }
                            } else if (var13 > 0) {
                                --var13
                                //                                arrayOfIDs[index] = var15;
//                                arrayOfMeta[index] = var15m;
                                primer.setBlockState(var8, var16, var9, var15.getStateFromMeta(var15m.toInt()))
                            }
                        }
                    }
                }
            }
        }
    }

    override fun generateChunk(x: Int, z: Int): Chunk {
        val primer: ChunkPrimer = ChunkPrimer()
        try {
            this.rand.setSeed(x * 341873128712L + z * 132897987541L)
            this.generateTerrain(x, z, primer)
            this.createCraters(x, z, primer)
            this.biomesForGeneration =
                this.world.getBiomeProvider().getBiomes(this.biomesForGeneration, x * 16, z * 16, 16, 16)
            this.replaceBiomeBlocks(x, z, primer, this.biomesForGeneration)

            if (this.worldGenerators == null) {
                this.worldGenerators = this.getWorldGenerators()
            }

            for (generator: MapGenBaseMeta in this.worldGenerators!!) {
                generator.generate(this.world, x, z, primer)
            }

            this.onChunkProvide(x, z, primer)
        } catch (e: Exception) {
            GCLog.severe("Error caught in planetary worldgen at coords " + x + "," + z + ".  If the next 2 lines are showing an Add-On mod name, please report to that mod's author!")
            e.printStackTrace()
        }

        val var4: Chunk = Chunk(this.world, primer, x, z)
        val var5: ByteArray = var4.getBiomeArray()

        for (var6: Int in var5.indices) {
            var5[var6] = Biome.getIdForBiome(this.biomesForGeneration[var6]).toByte()
        }

        var4.generateSkylightMap()
        return var4
    }

    fun createCraters(chunkX: Int, chunkZ: Int, primer: ChunkPrimer) {
        this.noiseGen5.setFrequency(0.015f)
        for (cx: Int in chunkX - 2..chunkX + 2) {
            for (cz: Int in chunkZ - 2..chunkZ + 2) {
                for (x: Int in 0..<CHUNK_SIZE_X) {
                    for (z: Int in 0..<CHUNK_SIZE_Z) {
                        if (abs(
                                this.randFromPoint(
                                    cx * 16 + x,
                                    (cz * 16 + z) * 1000
                                )
                            ) < this.noiseGen5.getNoise(
                                (cx * 16 + x).toFloat(),
                                (cz * 16 + z).toFloat()
                            ) / this.CRATER_PROB
                        ) {
                            val random: Random = Random((cx * 16 + x + (cz * 16 + z) * 5000).toLong())
                            val cSize: EnumCraterSize =
                                EnumCraterSize.sizeArray[random.nextInt(EnumCraterSize.sizeArray.size)]
                            val size: Int = random.nextInt(cSize.MAX_SIZE - cSize.MIN_SIZE) + cSize.MIN_SIZE + 15
                            this.makeCrater(cx * 16 + x, cz * 16 + z, chunkX * 16, chunkZ * 16, size, primer)
                        }
                    }
                }
            }
        }
    }

    fun makeCrater(craterX: Int, craterZ: Int, chunkX: Int, chunkZ: Int, size: Int, primer: ChunkPrimer) {
        for (x: Int in 0..<CHUNK_SIZE_X) {
            for (z: Int in 0..<CHUNK_SIZE_Z) {
                var xDev: Double = (craterX - (chunkX + x)).toDouble()
                var zDev: Double = (craterZ - (chunkZ + z)).toDouble()
                if (xDev * xDev + zDev * zDev < size * size) {
                    xDev /= size.toDouble()
                    zDev /= size.toDouble()
                    val sqrtY: Double = xDev * xDev + zDev * zDev
                    var yDev: Double = sqrtY * sqrtY * 6
                    yDev = 5 - yDev
                    var helper: Int = 0
                    for (y: Int in 127 downTo 1) {
                        if (Blocks.AIR !== primer.getBlockState(x, y, z).getBlock() && helper <= yDev) {
                            primer.setBlockState(x, y, z, Blocks.AIR.defaultBlockState())
                            //                            chunkArray[this.getIndex(x, y, z)] = Blocks.AIR;
//                            metaArray[this.getIndex(x, y, z)] = 0;
                            helper++
                        }

                        if (helper > yDev) {
                            break
                        }
                    }
                }
            }
        }
    }

    private fun getIndex(x: Int, y: Int, z: Int): Int {
        return (x * 16 + z) * 256 + y
    }

    private fun randFromPoint(x: Int, z: Int): Double {
        var n: Int
        n = x + z * 57
        n = n shl 13 xor n
        return 1.0 - (n * (n * n * 15731 + 789221) + 1376312589 and 0x7fffffff) / 1073741824.0
    }

    fun decoratePlanet(par1World: World?, par2Random: Random?, par3: Int, par4: Int) {
        this.getBiomeGenerator()!!.decorate(par1World, par2Random, par3, par4)
    }

    override fun populate(x: Int, z: Int) {
        BlockFalling.fallInstantly = true
        val var4: Int = x * 16
        val var5: Int = z * 16
        this.world.getBiome(BlockPos(var4 + 16, 0, var5 + 16))
        this.rand.setSeed(this.world.getSeed())
        val var7: Long = this.rand.nextLong() / 2L * 2L + 1L
        val var9: Long = this.rand.nextLong() / 2L * 2L + 1L
        this.rand.setSeed(x * var7 + z * var9 xor this.world.getSeed())
        this.decoratePlanet(this.world, this.rand, var4, var5)
        this.onPopulate(x, z)

        BlockFalling.fallInstantly = false
    }

    override fun getPossibleCreatures(
        creatureType: EnumCreatureType,
        pos: BlockPos
    ): MutableList<Biome.SpawnListEntry?> {
        val biomegenbase: Biome = this.world.getBiome(pos)
        return biomegenbase.getSpawnableList(creatureType)
    }

    /**
     * Do not return null
     *
     * @return The biome generator for this world, handles ore, flower, etc
     * generation. See GCBiomeDecoratorBase.
     */
    protected abstract fun getBiomeGenerator(): BiomeDecoratorSpace?

    /**
     * Do not return null, have at least one biome for generation
     *
     * @return Biome instance for generation
     */
    protected abstract fun getBiomesForGeneration(): Array<Biome?>

    /**
     * @return The average terrain level. Default is 64.
     */
    protected abstract fun getSeaLevel(): Int

    /**
     * List of all world generators to use. Caves, ravines, structures, etc.
     *
     *
     * Return an empty list for no world generators. Do not return null.
     *
     * @return
     */
    protected abstract fun getWorldGenerators(): MutableList<MapGenBaseMeta>?

    /**
     * The grass block to be generated. Doesn't have to be grass of course.
     *
     * @return BlockMetaPair instance containing ID and metadata for grass
     * block.
     */
    protected abstract fun getGrassBlock(): BlockMetaPair?

    /**
     * The dirt block to be generated. Doesn't have to be dirt of course.
     *
     * @return BlockMetaPair instance containing ID and metadata for dirt block.
     */
    protected abstract fun getDirtBlock(): BlockMetaPair?

    /**
     * The stone block to be generated. Doesn't have to be stone of course.
     *
     * @return BlockMetaPair instance containing ID and metadata for stone
     * block.
     */
    protected abstract fun getStoneBlock(): BlockMetaPair?

    /**
     * @return Base height modifier
     */
    abstract fun getHeightModifier(): Double

    /**
     * @return Height modifier for small hills
     */
    abstract fun getSmallFeatureHeightModifier(): Double

    /**
     * @return Height modifier for mountains
     */
    abstract fun getMountainHeightModifier(): Double

    /**
     * @return Height modifier for valleys
     */
    abstract fun getValleyHeightModifier(): Double

    /**
     * @return Probability that craters will be generated
     */
    abstract fun getCraterProbability(): Int

    abstract fun onChunkProvide(cX: Int, cZ: Int, primer: ChunkPrimer?)

    abstract fun onPopulate(cX: Int, cZ: Int)

    companion object {
        private const val CHUNK_SIZE_X = 16
        private const val CHUNK_SIZE_Y = 256
        private const val CHUNK_SIZE_Z = 16
        private const val MAIN_FEATURE_FILTER_MOD = 4.0
        private const val LARGE_FEATURE_FILTER_MOD = 8.0
        private const val SMALL_FEATURE_FILTER_MOD = 8.0
    }
}
