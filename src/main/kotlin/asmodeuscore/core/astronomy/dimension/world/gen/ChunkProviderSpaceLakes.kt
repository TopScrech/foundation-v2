package asmodeuscore.core.astronomy.dimension.world.gen

import java.util.List
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair
import net.minecraft.block.Block

import asmodeuscore.core.astronomy.dimension.world.gen.ACBiome
import asmodeuscore.core.astronomy.dimension.world.gen.ChunkProviderSpaceLakes.GenType
import asmodeuscore.core.astronomy.dimension.world.gen.ChunkProviderSpaceLakes
import asmodeuscore.core.astronomy.dimension.world.gen.ChunkProviderSpaceLakes.GenBlocks
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace
import micdoodle8.mods.galacticraft.api.prefab.world.gen.MapGenBaseMeta
import micdoodle8.mods.galacticraft.api.world.ChunkProviderBase
import micdoodle8.mods.galacticraft.core.perlin.generator.Gradient
import micdoodle8.mods.galacticraft.core.world.gen.EnumCraterSize
import net.minecraft.block.BlockFalling
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EnumCreatureType
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import net.minecraft.world.WorldProvider
import net.minecraft.world.biome.Biome
import net.minecraft.world.biome.BiomeCache
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.chunk.ChunkPrimer
import net.minecraft.world.gen.NoiseGeneratorOctaves
import net.minecraft.world.gen.NoiseGeneratorPerlin
import net.minecraftforge.fml.common.FMLLog
import java.lang.Exception
import java.util.ArrayList
import java.util.Random
import kotlin.math.abs


abstract class ChunkProviderSpaceLakes(@JvmField protected var worldObj: World, seed: Long, flag: Boolean) :
    ChunkProviderBase() {
    enum class GenType {
        GC,
        VANILLA,
        END
    }

    private val TERRAIN_HEIGHT_MOD = this.getHeightModifier()
    private val SMALL_FEATURE_HEIGHT_MOD = this.getSmallFeatureHeightModifier()
    private val MOUNTAIN_HEIGHT_MOD = this.getMountainHeightModifier()
    private val VALLEY_HEIGHT_MOD = this.getValleyHeightModifier()
    private val CRATER_PROB = this.getCraterProbability()

    private val MID_HEIGHT = this.getTerrainLevel()

    @JvmField
    protected var rand: Random
    private val myBiomeCache: BiomeCache? = null
    private val noiseGen4: NoiseGeneratorOctaves

    @JvmField
    var noiseGen5: NoiseGeneratorOctaves

    @JvmField
    var noiseGen6: NoiseGeneratorOctaves

    @JvmField
    var mobSpawnerNoise: NoiseGeneratorOctaves
    private val stoneNoise: DoubleArray
    private var biomesForGeneration: Array<Biome> = this.getBiomesForGeneration()

    @JvmField
    var noise3: DoubleArray? = null

    @JvmField
    var noise1: DoubleArray? = null

    @JvmField
    var noise2: DoubleArray? = null

    @JvmField
    var noise5: DoubleArray? = null

    @JvmField
    var noise6: DoubleArray? = null

    @JvmField
    var squareTable: FloatArray? = null
    private val field_147431_j: NoiseGeneratorOctaves
    private val field_147432_k: NoiseGeneratorOctaves
    private val field_147429_l: NoiseGeneratorOctaves
    private val field_147430_m: NoiseGeneratorPerlin
    private val terrainCalcs: DoubleArray
    private val parabolicField: FloatArray

    @JvmField
    var field_147427_d: DoubleArray? = null

    @JvmField
    var field_147428_e: DoubleArray? = null

    @JvmField
    var field_147425_f: DoubleArray? = null

    @JvmField
    var field_147426_g: DoubleArray? = null

    @JvmField
    var field_73219_j: Array<IntArray?> = Array<IntArray?>(32) { IntArray(32) }
    private val noiseGen8: Gradient

    private val gcnoiseGen1: Gradient
    private val gcnoiseGen2: Gradient
    private val gcnoiseGen3: Gradient
    private val gcnoiseGen4: Gradient
    private val gcnoiseGen5: Gradient
    private val gcnoiseGen6: Gradient
    private val gcnoiseGen7: Gradient

    private var worldGenerators: MutableList<MapGenBaseMeta>? = null

    init {
        this.stoneNoise = DoubleArray(256)
        this.rand = Random(seed)
        this.noiseGen4 = NoiseGeneratorOctaves(this.rand, 4)
        this.noiseGen5 = NoiseGeneratorOctaves(this.rand, 10)
        this.noiseGen6 = NoiseGeneratorOctaves(this.rand, 16)
        this.noiseGen8 = Gradient(this.rand.nextLong(), 2, 0.25f)
        this.mobSpawnerNoise = NoiseGeneratorOctaves(this.rand, 8)
        this.field_147431_j = NoiseGeneratorOctaves(this.rand, 16)
        this.field_147432_k = NoiseGeneratorOctaves(this.rand, 16)
        this.field_147429_l = NoiseGeneratorOctaves(this.rand, 8)
        this.field_147430_m = NoiseGeneratorPerlin(this.rand, 4)
        this.terrainCalcs = DoubleArray(825)
        this.parabolicField = FloatArray(25)

        for (j: Int in -2..2) {
            for (k: Int in -2..2) {
                val f: Float = 10.0f / MathHelper.sqrt(j * j + k * k + 0.2f)
                this.parabolicField[j + 2 + (k + 2) * 5] = f
            }
        }

        this.gcnoiseGen1 = Gradient(this.rand.nextLong(), 4, 0.25f)
        this.gcnoiseGen2 = Gradient(this.rand.nextLong(), 4, 0.25f)
        this.gcnoiseGen3 = Gradient(this.rand.nextLong(), 4, 0.25f)
        this.gcnoiseGen4 = Gradient(this.rand.nextLong(), 2, 0.25f)
        this.gcnoiseGen5 = Gradient(this.rand.nextLong(), 1, 0.25f)
        this.gcnoiseGen6 = Gradient(this.rand.nextLong(), 1, 0.25f)
        this.gcnoiseGen7 = Gradient(this.rand.nextLong(), 1, 0.25f)


        // this.setBlocks(GSBiomeGenBase.GSSpace,	Blocks.grass, (byte) 0, Blocks.dirt, (byte) 0);
    }

    override fun generateChunk(x: Int, z: Int): Chunk {
        val primer: ChunkPrimer = ChunkPrimer()
        try {
            this.rand.setSeed(x.toLong() * 341873128712L + z.toLong() * 132897987541L)
            this.generateTerrain(x, z, primer)
            if (this.getCraterProbability() > 0) this.createCraters(x, z, primer)

            this.biomesForGeneration =
                this.worldObj.getBiomeProvider().getBiomes(this.biomesForGeneration, x * 16, z * 16, 16, 16)
            this.replaceBlocksForBiome(x, z, primer, this.biomesForGeneration)

            if (this.worldGenerators == null) {
                this.worldGenerators = this.getWorldGenerators()
            }

            for (generator: MapGenBaseMeta in this.worldGenerators!!) {
                generator.generate(this.worldObj, x, z, primer)
            }

            this.onChunkProvider(x, z, primer)
        } catch (e: Exception) {
            FMLLog.warning("Error caught in planetary worldgen at coords " + x + "," + z + ".")
            e.printStackTrace()
        }

        val chunk: Chunk = Chunk(this.worldObj, primer, x, z)
        val chunkBiomes: ByteArray = chunk.getBiomeArray()

        for (i: Int in chunkBiomes.indices) {
            chunkBiomes[i] = Biome.getIdForBiome(this.biomesForGeneration[i]).toByte()
        }
        chunk.generateSkylightMap()
        return chunk
    }

    fun generateTerrain(chunkX: Int, chunkZ: Int, primer: ChunkPrimer) {
        val seaLevel: Int = this.getWaterLevel()
        this.biomesForGeneration =
            this.worldObj.getBiomeProvider().getBiomes(this.biomesForGeneration, chunkX * 16, chunkZ * 16, 16, 16)

        if (this.getGenType() != GenType.GC) {
            this.makeLandPerBiome2(chunkX * 4, 0, chunkZ * 4)

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
                        val d5: Double = (this.terrainCalcs[i1 + i2 + 1] - d1) * 0.125
                        val d6: Double = (this.terrainCalcs[j1 + i2 + 1] - d2) * 0.125
                        val d7: Double = (this.terrainCalcs[k1 + i2 + 1] - d3) * 0.125
                        val d8: Double = (this.terrainCalcs[l1 + i2 + 1] - d4) * 0.125

                        for (j2: Int in 0..7) {
                            val d9: Double = 0.25
                            var d10: Double = d1
                            var d11: Double = d2
                            val d12: Double = (d3 - d1) * 0.25
                            val d13: Double = (d4 - d2) * 0.25

                            for (k2: Int in 0..3) {
                                val d14: Double = 0.25
                                val d16: Double = (d11 - d10) * 0.25
                                var lvt_45_1_: Double = d10 - d16

                                for (l2: Int in 0..3) {
                                    if ((d16.let { lvt_45_1_ += it; lvt_45_1_ }) > 0.0) {
                                        primer.setBlockState(i * 4 + k2, i2 * 8 + j2, l * 4 + l2, this.getStoneBlock())
                                    } else if (i2 * 8 + j2 < seaLevel && this.canGenerateWaterBlock()) {
                                        primer.setBlockState(i * 4 + k2, i2 * 8 + j2, l * 4 + l2, this.getWaterBlock())
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
        } else this.makeGCland(chunkX, chunkZ, primer)
    }

    fun makeGCland(chunkX: Int, chunkZ: Int, primer: ChunkPrimer) {
        this.gcnoiseGen1.setFrequency(0.015f)
        this.gcnoiseGen2.setFrequency(0.01f)
        this.gcnoiseGen3.setFrequency(0.01f)
        this.gcnoiseGen4.setFrequency(0.01f)
        this.gcnoiseGen5.setFrequency(0.01f)
        this.gcnoiseGen6.setFrequency(0.001f)
        this.gcnoiseGen7.setFrequency(0.005f)

        for (x: Int in 0..<CHUNK_SIZE_X) {
            for (z: Int in 0..<CHUNK_SIZE_Z) {
                val biomes: Biome = this.worldObj.getBiome(BlockPos(x + chunkX * 16, 0, z + chunkZ * 16))

                val baseHeight: Double = this.gcnoiseGen1.getNoise(
                    (chunkX * 16 + x).toFloat(),
                    (chunkZ * 16 + z).toFloat()
                ) * this.TERRAIN_HEIGHT_MOD + biomes.getBaseHeight()
                val smallHillHeight: Double = this.gcnoiseGen2.getNoise(
                    (chunkX * 16 + x).toFloat(),
                    (chunkZ * 16 + z).toFloat()
                ) * this.SMALL_FEATURE_HEIGHT_MOD
                var mountainHeight: Double =
                    abs(this.gcnoiseGen3.getNoise((chunkX * 16 + x).toFloat(), (chunkZ * 16 + z).toFloat())).toDouble()
                var valleyHeight: Double =
                    abs(this.gcnoiseGen4.getNoise((chunkX * 16 + x).toFloat(), (chunkZ * 16 + z).toFloat())).toDouble()
                val featureFilter: Double = this.gcnoiseGen5.getNoise(
                    (chunkX * 16 + x).toFloat(),
                    (chunkZ * 16 + z).toFloat()
                ) * MAIN_FEATURE_FILTER_MOD
                val largeFilter: Double = this.gcnoiseGen6.getNoise(
                    (chunkX * 16 + x).toFloat(),
                    (chunkZ * 16 + z).toFloat()
                ) * LARGE_FEATURE_FILTER_MOD
                val smallFilter: Double = this.gcnoiseGen7.getNoise(
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
                        if (this.enableBiomeGenBaseBlock() && !genBlocks.isEmpty()) {
                            //idArray[this.getIndex(x, y, z)] = blockStone.get(biome.indexOf(biomes));
                            //metaArray[this.getIndex(x, y, z)] = metaStone.get(biome.indexOf(biomes)); 

                            var index: Int = -1 //array index start with 0;
                            for (genBlock: GenBlocks in genBlocks) {
                                index++
                                if (this.worldObj.provider === genBlock.getWorldProvider() && biomes === genBlock.getBiome()) {
                                    break
                                }
                            }

                            if (genBlocks.get(index).getStoneBlock() != null) primer.setBlockState(
                                x,
                                y,
                                z,
                                genBlocks.get(index).getStoneBlock()
                            )
                            else primer.setBlockState(x, y, z, this.getStoneBlock())


                            //idArray[this.getIndex(x, y, z)] = genBlocks.get(index).getStoneBlock().getBlock();
                            //metaArray[this.getIndex(x, y, z)] = genBlocks.get(index).getStoneBlock().getMetadata();
                        } else {
                            primer.setBlockState(x, y, z, this.getStoneBlock())
                            //                          
                            //idArray[this.getIndex(x, y, z)] = this.getStoneBlock().getBlock();
                            //metaArray[this.getIndex(x, y, z)] = this.getStoneBlock().getMetadata();  
                        }
                    }
                }
            }
        }
    }

    private fun makeLandPerBiome2(x: Int, zero: Int, z: Int) {
        this.field_147426_g = this.noiseGen6.generateNoiseOctaves(this.field_147426_g, x, z, 5, 5, 200.0, 200.0, 0.5)
        this.field_147427_d = this.field_147429_l.generateNoiseOctaves(
            this.field_147427_d,
            x,
            zero,
            z,
            5,
            33,
            5,
            8.555150000000001,
            4.277575000000001,
            8.555150000000001
        )
        this.field_147428_e = this.field_147431_j.generateNoiseOctaves(
            this.field_147428_e,
            x,
            zero,
            z,
            5,
            33,
            5,
            684.41200000000003,
            684.41200000000003,
            684.41200000000003
        )
        this.field_147425_f = this.field_147432_k.generateNoiseOctaves(
            this.field_147425_f,
            x,
            zero,
            z,
            5,
            33,
            5,
            684.41200000000003,
            684.41200000000003,
            684.41200000000003
        )
        var terrainIndex: Int = 0
        var noiseIndex: Int = 0

        for (ax: Int in 0..4) {
            for (az: Int in 0..4) {
                var totalVariation: Float = 0.0f
                var totalHeight: Float = 0.0f
                var totalFactor: Float = 0.0f
                val two: Byte = 2
                val biomegenbase: Biome = this.biomesForGeneration[ax + 2 + (az + 2) * 10]

                for (ox: Int in -two..two) {
                    for (oz: Int in -two..two) {
                        val biomegenbase1: Biome = this.biomesForGeneration[ax + ox + 2 + (az + oz + 2) * 10]
                        val rootHeight: Float = biomegenbase1.getBaseHeight()
                        val heightVariation: Float = biomegenbase1.getHeightVariation()
                        var heightFactor: Float = this.parabolicField[ox + 2 + (oz + 2) * 5] / (rootHeight + 2.0f)

                        if (biomegenbase1.getBaseHeight() > biomegenbase.getBaseHeight()) {
                            heightFactor /= 2.0f
                        }
                        totalVariation += heightVariation * heightFactor
                        totalHeight += rootHeight * heightFactor
                        totalFactor += heightFactor
                    }
                }
                totalVariation /= totalFactor
                totalHeight /= totalFactor
                totalVariation = totalVariation * 0.9f + 0.1f
                totalHeight = (totalHeight * 4.0f - 1.0f) / 8.0f
                var terrainNoise: Double = (this.field_147426_g)!![noiseIndex] / 8000.0

                if (terrainNoise < 0.0) {
                    terrainNoise = -terrainNoise * 0.3
                }

                terrainNoise = terrainNoise * 3.0 - 2.0

                if (terrainNoise < 0.0) {
                    terrainNoise /= 2.0

                    if (terrainNoise < -1.0) {
                        terrainNoise = -1.0
                    }
                    terrainNoise /= 1.4
                    terrainNoise /= 2.0
                } else {
                    if (terrainNoise > 1.0) {
                        terrainNoise = 1.0
                    }
                    terrainNoise /= 8.0
                }
                noiseIndex++
                var heightCalc: Double = totalHeight.toDouble()
                val variationCalc: Double = totalVariation * this.getHeightModifier() / 10.0
                heightCalc += terrainNoise * 0.2
                heightCalc = heightCalc * 8.5 / 8.0
                val d5: Double = 8.5 + heightCalc * 4.0

                for (ay: Int in 0..32) {
                    var d6: Double = (ay - d5) * 12.0 * 128.0 / 256.0 / variationCalc

                    if (d6 < 0.0) {
                        d6 *= 4.0
                    }
                    val d7: Double = (this.field_147428_e)!![terrainIndex] / 512.0
                    val d8: Double = (this.field_147425_f)!![terrainIndex] / 512.0
                    val d9: Double = ((this.field_147427_d)!![terrainIndex] / 10.0 + 1.0) / 2.0
                    var terrainCalc: Double = MathHelper.clampedLerp(d7, d8, d9) - d6

                    if (ay > 29) {
                        val d11: Double = ((ay - 29) / 3.0f).toDouble()
                        terrainCalc = terrainCalc * (1.0 - d11) + -10.0 * d11
                    }
                    this.terrainCalcs[terrainIndex] = terrainCalc
                    terrainIndex++
                }
            }
        }
    }

    fun replaceBlocksForBiome(x: Int, z: Int, primer: ChunkPrimer, par4ArrayOfBiomeGenBase: Array<Biome>) {
        val var5: Int = 20
        val var6: Float = 0.03125f
        this.noiseGen8.setFrequency(var6 * 2)
        if (this.getGenType() != GenType.END) {
            for (var8: Int in 0..15) {
                for (var9: Int in 0..15) {
                    val biomegenbase: ACBiome? = par4ArrayOfBiomeGenBase[var8 + var9 * 16] as ACBiome?

                    val var12: Int = (this.noiseGen8.getNoise(
                        (x * 16 + var8).toFloat(),
                        (z * 16 + var9).toFloat()
                    ) / 3.0 + getDirtLayerSize() + this.rand.nextDouble() * 0.25).toInt()
                    var var13: Int = -1

                    var grassblock: IBlockState = this.getGrassBlock()
                    var dirtblock: IBlockState = this.getDirtBlock()
                    var stoneblock: IBlockState? = this.getStoneBlock()


                    /* Block var14;
	                byte var14m;
	                Block var15;
	                byte var15m;*/
                    /*
	                Block stone;
	                byte mStone;*/
                    if (this.enableBiomeGenBaseBlock() && !genBlocks.isEmpty()) {
                        //GalaxySpace.debug("Chunk: " + biome.indexOf(par4ArrayOfBiomeGenBase[var8 + var9 * 16]) + "|" + block.get(biome.indexOf(par4ArrayOfBiomeGenBase[var8 + var9 * 16]))+ "|" + block.size() + "|" + biome.indexOf(par4ArrayOfBiomeGenBase[var8 + var9 * 16]));

                        var index: Int = -1
                        for (genBlock: GenBlocks in genBlocks) {
                            index++
                            if (this.worldObj.provider === genBlock.getWorldProvider() && par4ArrayOfBiomeGenBase[var8 + var9 * 16] === genBlock.getBiome()) {
                                break
                            }
                        }
                        //GalaxySpace.debug(index + " | " + genBlocks.get(index).getGrassBlock().getBlock());
                        grassblock = genBlocks.get(index).getGrassBlock()
                        dirtblock = genBlocks.get(index).getDirtBlock()

                        if (genBlocks.get(index).getStoneBlock() != null) stoneblock =
                            genBlocks.get(index).getStoneBlock()
                    }

                    for (var16: Int in 256 - 1 downTo 0) {
                        val index: Int = this.getIndex(var8, var16, var9)

                        if (this.canGenerateIceBlock()) {
                            if (var16 == 5 || var16 == 6 + this.rand.nextInt(3)) {
                                primer.setBlockState(var8, var16, var9, this.getIceBlock())
                            }
                        }

                        if (var16 <= 0 + this.rand.nextInt(5)) {
                            primer.setBlockState(var8, var16, var9, Blocks.BEDROCK.getDefaultState())
                        } else {
                            val var18: IBlockState = primer.getBlockState(var8, var16, var9)

                            if (Blocks.AIR === var18) {
                                var13 = -1
                            } else if (var18 === stoneblock) {
                                if (var13 == -1) {
                                    if (var12 <= 0) {
                                        grassblock = Blocks.AIR.getDefaultState()
                                    } else if (var16 >= var5 - -16 && var16 <= var5 + 1) {
                                        if (this.enableBiomeGenBaseBlock() && !genBlocks.isEmpty()) {
                                            grassblock = genBlocks.get(index).getGrassBlock()
                                            dirtblock = genBlocks.get(index).getDirtBlock()
                                        } else {
                                            grassblock = this.getGrassBlock()
                                            dirtblock = this.getDirtBlock()
                                        }
                                    }
                                    var13 = var12


                                    /*  if (var16 >= this.getWaterLevel() - 1)
	                                {
	                                	*/
                                    primer.setBlockState(var8, var16, var9, grassblock)

                                    /*}
	                                else
	                                {
	                                    arrayOfIDs[index] = var15;
	                                    arrayOfMeta[index] = var15m;
	                                }*/
                                } else if (var13 > 0) {
                                    --var13
                                    primer.setBlockState(var8, var16, var9, dirtblock)
                                }
                            } else if (var16 < this.getWaterLevel() && this.getGenType() == GenType.GC && this.canGenerateWaterBlock()) {
                                primer.setBlockState(var8, var16, var9, this.getWaterBlock())
                            }
                        }
                    }
                }
            }
        }
    }

    fun createCraters(chunkX: Int, chunkZ: Int, primer: ChunkPrimer) {
        this.gcnoiseGen5.setFrequency(0.015f)
        for (cx: Int in chunkX - 2..chunkX + 2) {
            for (cz: Int in chunkZ - 2..chunkZ + 2) {
                for (x: Int in 0..<CHUNK_SIZE_X) {
                    for (z: Int in 0..<CHUNK_SIZE_Z) {
                        if (abs(
                                this.randFromPoint(
                                    cx * 16 + x,
                                    (cz * 16 + z) * 1000
                                )
                            ) < this.gcnoiseGen5.getNoise(
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
                            primer.setBlockState(x, y, z, Blocks.AIR.getDefaultState())
                            this.getCraterAdditions(primer, x, y, z)

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

    private fun randFromPoint(x: Int, z: Int): Double {
        var n: Int
        n = x + z * 57
        n = n shl 13 xor n
        return 1.0 - (n * (n * n * 15731 + 789221) + 1376312589 and 0x7fffffff) / 1073741824.0
    }

    protected fun getIndex(x: Int, y: Int, z: Int): Int {
        return (x * 16 + z) * 256 + y
    }

    override fun populate(x: Int, z: Int) {
        BlockFalling.fallInstantly = true
        val posX: Int = x shl 4
        val posZ: Int = z shl 4
        this.worldObj.getBiome(BlockPos(posX + 16, 0, posZ + 16))
        this.rand.setSeed(this.worldObj.getSeed())
        val var7: Long = this.rand.nextLong() / 2L * 2L + 1L
        val var9: Long = this.rand.nextLong() / 2L * 2L + 1L
        this.rand.setSeed(x * var7 + z * var9 xor this.worldObj.getSeed())


        this.decoratePlanet(this.worldObj, this.rand, posX, posZ)
        this.onPopulate(x, z)
        BlockFalling.fallInstantly = false
    }

    fun decoratePlanet(world: World?, rand: Random?, x: Int, z: Int) {
        this.getBiomeGenerator()!!.decorate(world, rand, x, z)
    }

    /*
    @Override
    public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos)
    {
        if (creatureType == EnumCreatureType.MONSTER)
        {
            final List monsters = new ArrayList();

            for (SpawnListEntry monster : this.getMonsters())
            {
                monsters.add(monster);
            }

            return monsters;
        }
        else if (creatureType == EnumCreatureType.CREATURE)
        {
            final List creatures = new ArrayList();

            for (SpawnListEntry creature : this.getCreatures())
            {
                creatures.add(creature);
            }

            return creatures;
        }
        else if (creatureType == EnumCreatureType.WATER_CREATURE)
        {
            final List watercreatures = new ArrayList();

            for (SpawnListEntry watercreature : this.getWaterCreatures())
            {
                watercreatures.add(watercreature);
            }

            return watercreatures;
        }
        else
        {
            return null;
        }
    }*/
    override fun getPossibleCreatures(
        creatureType: EnumCreatureType,
        pos: BlockPos
    ): MutableList<Biome.SpawnListEntry?> {
        val biomegenbase: Biome = this.worldObj.getBiome(pos)
        return biomegenbase.getSpawnableList(creatureType)
    }

    protected fun lerp(d1: Double, d2: Double, t: Double): Double {
        if (t < 0.0) {
            return d1
        } else if (t > 1.0) {
            return d2
        } else {
            return d1 + (d2 - d1) * t
        }
    }

    protected fun fade(n: Double): Double {
        return n * n * n * (n * (n * 6 - 15) + 10)
    }

    protected fun clamp(x: Double, min: Double, max: Double): Double {
        if (x < min) {
            return min
        }
        if (x > max) {
            return max
        }
        return x
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
    protected abstract fun getBiomesForGeneration(): Array<Biome>

    abstract fun onChunkProvider(cX: Int, cZ: Int, primer: ChunkPrimer?)

    abstract fun onPopulate(x: Int, z: Int)


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
     * @return Base height modifier
     */
    abstract fun getWaterLevel(): Int

    /**
     * @return Generate Water Block on sealevel.
     */
    abstract fun canGenerateWaterBlock(): Boolean

    /**
     * @return Generate Ice Block on bedrocklevel.
     */
    abstract fun canGenerateIceBlock(): Boolean

    /**
     * @return Probability that craters will be generated
     */
    abstract fun getCraterProbability(): Int

    /**
     * The water block to be generated.
     *
     * @return BlockMetaPair instance containing ID and metadata for water
     * block.
     */
    protected abstract fun getWaterBlock(): IBlockState?
    protected abstract fun getGrassBlock(): IBlockState
    protected abstract fun getDirtBlock(): IBlockState
    protected abstract fun getStoneBlock(): IBlockState?

    protected abstract fun enableBiomeGenBaseBlock(): Boolean
    protected abstract fun getGenType(): GenType?

    fun getCraterSize(): Int {
        return 0
    }

    open fun getTerrainLevel(): Int {
        return this.getWaterLevel()
    }

    open fun getDirtLayerSize(): Double {
        return 3.0
    }

    protected open fun getIceBlock(): IBlockState {
        return Blocks.PACKED_ICE.getDefaultState()
    }

    open fun getCraterAdditions(primer: ChunkPrimer?, x: Int, y: Int, z: Int) {}

    open inner class GenBlocks constructor(
        private val provider: WorldProvider?,
        private val biome: Biome?,
        private val grass: IBlockState,
        private val dirt: IBlockState
    ) {
        private var customstone: IBlockState? = null

        open fun setCustomStone(stone: IBlockState?): GenBlocks {
            this.customstone = stone
            return this
        }

        open fun getWorldProvider(): WorldProvider? {
            return this.provider
        }

        open fun getBiome(): Biome? {
            return this.biome
        }

        open fun getGrassBlock(): IBlockState {
            return this.grass
        }

        open fun getDirtBlock(): IBlockState {
            return this.dirt
        }

        open fun getStoneBlock(): IBlockState? {
            return this.customstone
        }
    }

    companion object {
        protected const val CHUNK_SIZE_X: Int = 16
        private const val CHUNK_SIZE_Y = 256

        protected const val CHUNK_SIZE_Z: Int = 16

        private const val MAIN_FEATURE_FILTER_MOD = 4.0
        private const val LARGE_FEATURE_FILTER_MOD = 8.0
        private const val SMALL_FEATURE_FILTER_MOD = 8.0

        @JvmField
        var genBlocks: MutableList<GenBlocks> = (ArrayList<Any?>() as MutableList<ChunkProviderSpaceLakes.GenBlocks>)

        /*
    public static void setBlocks(BiomeGenBase biomes, Block blocks, byte metas, Block blocks2, byte metas2, Block blocks3, byte metas3)
    {
    	biome.add(biomes);
    	block.add(blocks);
    	meta.add(metas);
    	block2.add(blocks2);
    	meta2.add(metas2);
    	blockStone.add(blocks3);
    	metaStone.add(metas3);
    };
    */
        @JvmStatic
        fun setBlocks(array: GenBlocks) {
            genBlocks.add(array)
        }
    }
}
