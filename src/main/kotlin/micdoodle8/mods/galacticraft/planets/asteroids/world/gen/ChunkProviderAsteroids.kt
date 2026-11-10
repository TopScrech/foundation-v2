package micdoodle8.mods.galacticraft.planets.asteroids.world.gen

import net.minecraft.block.BlockFlower.EnumFlowerType

import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeAdaptive
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.api.world.ChunkProviderBase
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.perlin.NoiseModule
import micdoodle8.mods.galacticraft.core.perlin.generator.Billowed
import micdoodle8.mods.galacticraft.core.perlin.generator.Gradient
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.planets.asteroids.ConfigManagerAsteroids
import micdoodle8.mods.galacticraft.planets.asteroids.blocks.AsteroidBlocks
import micdoodle8.mods.galacticraft.planets.asteroids.dimension.WorldProviderAsteroids
import micdoodle8.mods.galacticraft.planets.asteroids.world.gen.base.MapGenAbandonedBase
import net.minecraft.block.Block
import net.minecraft.block.BlockAir
import net.minecraft.block.BlockFlower
import net.minecraft.block.BlockLeaves
import net.minecraft.block.BlockOldLeaf
import net.minecraft.block.BlockOldLog
import net.minecraft.block.BlockPlanks
import net.minecraft.block.BlockTallGrass
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EnumCreatureType
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.World
import net.minecraft.world.biome.Biome
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.chunk.ChunkPrimer
import net.minecraft.world.chunk.storage.ExtendedBlockStorage
import net.minecraft.world.gen.feature.WorldGenFlowers
import net.minecraft.world.gen.feature.WorldGenLakes
import net.minecraft.world.gen.feature.WorldGenTallGrass
import net.minecraft.world.gen.feature.WorldGenTrees
import java.util.ArrayList
import java.util.HashSet
import java.util.LinkedList
import java.util.Random
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

open class ChunkProviderAsteroids constructor(private val world: World, par2: Long, par4: Boolean) :
    ChunkProviderBase() {
    @JvmField
    val ASTEROID_STONE: Block = (AsteroidBlocks.blockBasic)!!

    @JvmField
    val ASTEROID_STONE_META_0: Byte = 0

    @JvmField
    val ASTEROID_STONE_META_1: Byte = 1

    @JvmField
    val ASTEROID_STONE_META_2: Byte = 2

    @JvmField
    val DIRT: Block = Blocks.DIRT

    @JvmField
    val DIRT_META: Byte = 0

    @JvmField
    val GRASS: Block = Blocks.GRASS

    @JvmField
    val GRASS_META: Byte = 0

    @JvmField
    val LIGHT: Block = Blocks.GLOWSTONE

    @JvmField
    val LIGHT_META: Byte = 0

    @JvmField
    var GRASS_TYPE: BlockTallGrass.EnumType = BlockTallGrass.EnumType.GRASS

    @JvmField
    val FLOWER: BlockFlower = Blocks.RED_FLOWER

    @JvmField
    val LAVA: Block = Blocks.LAVA

    @JvmField
    val LAVA_META: Byte = 0

    @JvmField
    val WATER: Block = Blocks.WATER

    @JvmField
    val WATER_META: Byte = 0

    private val rand: Random

    private val asteroidDensity: NoiseModule

    private val asteroidTurbulance: NoiseModule

    private val asteroidSkewX: NoiseModule
    private val asteroidSkewY: NoiseModule
    private val asteroidSkewZ: NoiseModule

    private val coreHandler: SpecialAsteroidBlockHandler
    private val shellHandler: SpecialAsteroidBlockHandler

    private val largeAsteroids = LinkedList<AsteroidData>()
    private var largeCount = 0
    private var largeAsteroidsLastChunkX = 0
    private var largeAsteroidsLastChunkZ = 0
    private val dungeonGenerator = MapGenAbandonedBase()

    init {
        this.rand = Random(par2)

        this.asteroidDensity = Billowed(this.rand.nextLong(), 2, .25f)
        this.asteroidDensity.setFrequency(.009f)
        this.asteroidDensity.amplitude = .6f

        this.asteroidTurbulance = Gradient(this.rand.nextLong(), 1, .2f)
        this.asteroidTurbulance.setFrequency(.08f)
        this.asteroidTurbulance.amplitude = .5f

        this.asteroidSkewX = Gradient(this.rand.nextLong(), 1, 1f)
        this.asteroidSkewX.amplitude = MAX_ASTEROID_SKEW.toFloat()
        this.asteroidSkewX.frequencyX = 0.005f

        this.asteroidSkewY = Gradient(this.rand.nextLong(), 1, 1f)
        this.asteroidSkewY.amplitude = MAX_ASTEROID_SKEW.toFloat()
        this.asteroidSkewY.frequencyY = 0.005f

        this.asteroidSkewZ = Gradient(this.rand.nextLong(), 1, 1f)
        this.asteroidSkewZ.amplitude = MAX_ASTEROID_SKEW.toFloat()
        this.asteroidSkewZ.frequencyZ = 0.005f

        this.coreHandler = SpecialAsteroidBlockHandler()
        this.coreHandler.addBlock(SpecialAsteroidBlock(this.ASTEROID_STONE, this.ASTEROID_STONE_META_2, 5, .3))
        this.coreHandler.addBlock(SpecialAsteroidBlock(this.ASTEROID_STONE, this.ASTEROID_STONE_META_1, 7, .3))
        this.coreHandler.addBlock(SpecialAsteroidBlock(this.ASTEROID_STONE, this.ASTEROID_STONE_META_0, 11, .25))

        if (!ConfigManagerAsteroids.disableAluminumGen) this.coreHandler.addBlock(
            SpecialAsteroidBlock(
                this.ASTEROID_STONE,
                3.toByte(),
                5,
                .2
            )
        )
        if (!ConfigManagerAsteroids.disableIlmeniteGen) this.coreHandler.addBlock(
            SpecialAsteroidBlock(
                this.ASTEROID_STONE,
                4.toByte(),
                4,
                .15
            )
        )
        if (!ConfigManagerAsteroids.disableIronGen) this.coreHandler.addBlock(
            SpecialAsteroidBlock(
                this.ASTEROID_STONE,
                5.toByte(),
                3,
                .2
            )
        )
        if (ConfigManagerCore.enableSiliconOreGen) this.coreHandler.addBlock(
            SpecialAsteroidBlock(
                GCBlocks.basicBlock,
                8.toByte(),
                2,
                .15
            )
        ) //TODO: Asteroids version of silicon ore

        //Solid Meteoric Iron - has no config to disable
        this.coreHandler.addBlock(SpecialAsteroidBlock(GCBlocks.basicBlock, 12.toByte(), 2, .13))
        //Diamond ore - has no config to disable
        this.coreHandler.addBlock(
            SpecialAsteroidBlock(
                Blocks.DIAMOND_ORE,
                0.toByte(),
                1,
                .1
            )
        ) //TODO: Asteroids version of diamond ore

        this.shellHandler = SpecialAsteroidBlockHandler()
        this.shellHandler.addBlock(SpecialAsteroidBlock(this.ASTEROID_STONE, this.ASTEROID_STONE_META_0, 1, .15))
        this.shellHandler.addBlock(SpecialAsteroidBlock(this.ASTEROID_STONE, this.ASTEROID_STONE_META_1, 3, .15))
        this.shellHandler.addBlock(SpecialAsteroidBlock(this.ASTEROID_STONE, this.ASTEROID_STONE_META_2, 1, .15))
        this.shellHandler.addBlock(SpecialAsteroidBlock(AsteroidBlocks.blockDenseIce, 0.toByte(), 1, .15))
    }

    open fun generateTerrain(chunkX: Int, chunkZ: Int, primer: ChunkPrimer?, flagDataOnly: Boolean) {
        this.largeAsteroids.clear()
        this.largeCount = 0
        val random: Random = Random()
        val asteroidChance: Int = ASTEROID_CHANCE
        val rangeY: Int = MAX_ASTEROID_Y - MIN_ASTEROID_Y
        val rangeSize: Int = MAX_ASTEROID_RADIUS - MIN_ASTEROID_RADIUS

        //If there is an asteroid centre nearby, it might need to generate some asteroid parts in this chunk
        for (i: Int in chunkX - 3..<chunkX + 3) {
            val minX: Int = i * 16
            val maxX: Int = minX + CHUNK_SIZE_X
            for (k: Int in chunkZ - 3..<chunkZ + 3) {
                val minZ: Int = k * 16
                val maxZ: Int = minZ + CHUNK_SIZE_Z

                //NOTE: IF UPDATING THIS CODE also update addLargeAsteroids() which is the same algorithm
                //??? ^^ this now seems redundant
                var x: Int = minX
                while (x < maxX) {
                    var z: Int = minZ
                    while (z < maxZ) {
                        //The next line is called 3136 times per chunk generated.  getNoise is a little slow.
                        if (this.randFromPointPos(x, z) < (this.asteroidDensity.getNoise(
                                x.toFloat(),
                                z.toFloat()
                            ) + .4) / asteroidChance
                        ) {
                            random.setSeed((x + z * 3067).toLong())
                            val y: Int = random.nextInt(rangeY) + MIN_ASTEROID_Y
                            val size: Int = random.nextInt(rangeSize) + MIN_ASTEROID_RADIUS

                            //Generate the parts of the asteroid which are in this chunk
                            this.generateAsteroid(
                                random,
                                x,
                                y,
                                z,
                                chunkX shl 4,
                                chunkZ shl 4,
                                size,
                                primer,
                                flagDataOnly
                            )
                            this.largeCount++
                        }
                        z += 2
                    }
                    x += 2
                }
            }
        }
    }

    private fun generateAsteroid(
        rand: Random,
        asteroidX: Int,
        asteroidY: Int,
        asteroidZ: Int,
        chunkX: Int,
        chunkZ: Int,
        size: Int,
        primer: ChunkPrimer?,
        flagDataOnly: Boolean
    ) {
        val core: SpecialAsteroidBlock = this.coreHandler.getBlock(rand, size)

        var shell: SpecialAsteroidBlock? = null
        if (rand.nextInt(ASTEROID_SHELL_CHANCE) == 0) {
            shell = this.shellHandler.getBlock(rand, size)
        }

        var isHollow: Boolean = false
        val hollowSize: Float = rand.nextFloat() * (MAX_HOLLOW_SIZE - MIN_HOLLOW_SIZE) + MIN_HOLLOW_SIZE
        if (rand.nextInt(HOLLOW_CHANCE) == 0 && size >= MIN_RADIUS_FOR_HOLLOW) {
            isHollow = true
            shell = SpecialAsteroidBlock(AsteroidBlocks.blockDenseIce, 0.toByte(), 1, .15)
        }

        //Add to the list of asteroids for external use
        (this.world.provider as WorldProviderAsteroids).addAsteroid(
            asteroidX,
            asteroidY,
            asteroidZ,
            size,
            if (isHollow) -1 else core.index
        )

        val xMin: Int = this.clamp(max(chunkX, asteroidX - size - MAX_ASTEROID_SKEW - 2) - chunkX, 0, 16)
        val zMin: Int = this.clamp(max(chunkZ, asteroidZ - size - MAX_ASTEROID_SKEW - 2) - chunkZ, 0, 16)
        val yMin: Int = asteroidY - size - MAX_ASTEROID_SKEW - 2
        val yMax: Int = asteroidY + size + MAX_ASTEROID_SKEW + 2
        val xMax: Int = this.clamp(min(chunkX + 16, asteroidX + size + MAX_ASTEROID_SKEW + 2) - chunkX, 0, 16)
        val zMax: Int = this.clamp(min(chunkZ + 16, asteroidZ + size + MAX_ASTEROID_SKEW + 2) - chunkZ, 0, 16)
        val xSize: Int = xMax - xMin
        val ySize: Int = yMax - yMin
        val zSize: Int = zMax - zMin

        if (xSize <= 0 || ySize <= 0 || zSize <= 0) {
            return
        }

        val noiseOffsetX: Float = this.randFromPoint(asteroidX, asteroidY, asteroidZ) * NOISE_OFFSET_SIZE + chunkX
        val noiseOffsetY: Float = this.randFromPoint(asteroidX * 7, asteroidY * 11, asteroidZ * 13) * NOISE_OFFSET_SIZE
        val noiseOffsetZ: Float =
            this.randFromPoint(asteroidX * 17, asteroidY * 23, asteroidZ * 29) * NOISE_OFFSET_SIZE + chunkZ
        this.setOtherAxisFrequency(1f / (size * 2f / 2f))

        val sizeXArray: FloatArray = FloatArray(ySize * zSize)
        val sizeZArray: FloatArray = FloatArray(xSize * ySize)
        val sizeYArray: FloatArray = FloatArray(xSize * zSize)

        for (x: Int in 0..<xSize) {
            val xx: Int = x * zSize
            val xxx: Float = x + noiseOffsetX
            for (z: Int in 0..<zSize) {
                sizeYArray[xx + z] = this.asteroidSkewY.getNoise(xxx, z + noiseOffsetZ)
            }
        }

        val asteroidData: AsteroidData = AsteroidData(
            isHollow,
            sizeYArray,
            xMin,
            zMin,
            xMax,
            zMax,
            zSize,
            size,
            asteroidX,
            asteroidY,
            asteroidZ
        )
        this.largeAsteroids.add(asteroidData)
        this.largeAsteroidsLastChunkX = chunkX
        this.largeAsteroidsLastChunkZ = chunkZ

        if (flagDataOnly) {
            return
        }

        for (y: Int in 0..<ySize) {
            val yy: Int = y * zSize
            val yyy: Float = y + noiseOffsetY
            for (z: Int in 0..<zSize) {
                sizeXArray[yy + z] = this.asteroidSkewX.getNoise(yyy, z + noiseOffsetZ)
            }
        }

        for (x: Int in 0..<xSize) {
            val xx: Int = x * ySize
            val xxx: Float = x + noiseOffsetX
            for (y: Int in 0..<ySize) {
                sizeZArray[xx + y] = this.asteroidSkewZ.getNoise(xxx, y + noiseOffsetY)
            }
        }

        var shellThickness: Double = 0.0
        var terrainY: Int = 0
        var terrainYY: Int = 0

        var asteroidShell: IBlockState? = null
        if (shell != null) {
            asteroidShell = (shell.block)!!.getStateFromMeta(shell.meta.toInt())
            shellThickness = 1.0 - shell.thickness
        }

        val asteroidCore: IBlockState = (core.block)!!.getStateFromMeta(core.meta.toInt())
        val asteroidRock0: IBlockState = this.ASTEROID_STONE.getStateFromMeta(this.ASTEROID_STONE_META_0.toInt())
        val asteroidRock1: IBlockState = this.ASTEROID_STONE.getStateFromMeta(this.ASTEROID_STONE_META_1.toInt())
        val airBlock: IBlockState = Blocks.AIR.getDefaultState()
        val dirtBlock: IBlockState = this.DIRT.getStateFromMeta(this.DIRT_META.toInt())
        val grassBlock: IBlockState = this.GRASS.getStateFromMeta(this.GRASS_META.toInt())

        for (x: Int in xMax - 1 downTo xMin) {
            val indexXY: Int = (x - xMin) * ySize - yMin
            val indexXZ: Int = (x - xMin) * zSize - zMin
            val distanceX: Int = asteroidX - (x + chunkX)
            val indexBaseX: Int = x * CHUNK_SIZE_Y shl 4
            val xx: Float = (x + chunkX).toFloat()

            for (z: Int in zMin..<zMax) {
                if (isHollow) {
                    val sizeModY: Float = sizeYArray[indexXZ + z]
                    terrainY = this.getTerrainHeightFor(sizeModY, asteroidY, size)
                    terrainYY = this.getTerrainHeightFor(sizeModY, asteroidY - 1, size)
                }

                var sizeY: Float = size + sizeYArray[indexXZ + z]
                sizeY *= sizeY
                val distanceZ: Int = asteroidZ - (z + chunkZ)
                val indexBase: Int = indexBaseX or z * CHUNK_SIZE_Y
                val zz: Float = (z + chunkZ).toFloat()

                for (y: Int in yMin..<yMax) {
                    var dSizeX: Float = distanceX / (size + sizeXArray[(y - yMin) * zSize + z - zMin])
                    var dSizeZ: Float = distanceZ / (size + sizeZArray[indexXY + y])
                    dSizeX *= dSizeX
                    dSizeZ *= dSizeZ
                    var distanceY: Int = asteroidY - y
                    distanceY *= distanceY
                    var distance: Float = dSizeX + distanceY / sizeY + dSizeZ
                    var distanceAbove: Float = distance
                    distance += this.asteroidTurbulance.getNoise(xx, y.toFloat(), zz)

                    if (isHollow && distance <= hollowSize) {
                        distanceAbove += this.asteroidTurbulance.getNoise(xx, (y + 1).toFloat(), zz)
                        if (distanceAbove <= 1) {
                            if ((y - 1) == terrainYY) {
                                val index: Int = indexBase or (y + 1)
                                primer!!.setBlockState(
                                    x,
                                    y + 1,
                                    z,
                                    this.LIGHT.getStateFromMeta(this.LIGHT_META.toInt())
                                )
                                //                                blockArray[index] = this.LIGHT;
//                                metaArray[index] = this.LIGHT_META;
                            }
                        }
                    }

                    if (distance <= 1) {
                        val index: Int = indexBase or y
                        if (isHollow && distance <= hollowSize) {
                            if (y == terrainY) {
                                primer!!.setBlockState(x, y, z, grassBlock)
                                //                                blockArray[index] = this.GRASS;
//                                metaArray[index] = this.GRASS_META;
                            } else if (y < terrainY) {
                                primer!!.setBlockState(x, y, z, dirtBlock)
                                //                                blockArray[index] = this.DIRT;
//                                metaArray[index] = this.DIRT_META;
                            } else {
                                primer!!.setBlockState(x, y, z, airBlock)
                                //                                blockArray[index] = Blocks.air;
//                                metaArray[index] = 0;
                            }
                        } else if (distance <= core.thickness) {
                            if (rand.nextBoolean()) {
                                primer!!.setBlockState(x, y, z, asteroidCore)
                                //	                        	blockArray[index] = core.block;
//	                            metaArray[index] = core.meta;
                            } else {
                                primer!!.setBlockState(x, y, z, asteroidRock0)
                                //	                        	blockArray[index] = this.ASTEROID_STONE;
//	                            metaArray[index] = this.ASTEROID_STONE_META_0;
                            }
                        } else if (shell != null && distance >= shellThickness) {
                            primer!!.setBlockState(x, y, z, asteroidShell)
                            //                            blockArray[index] = shell.block;
//                            metaArray[index] = shell.meta;
                        } else {
                            primer!!.setBlockState(x, y, z, asteroidRock1)
                            //                            blockArray[index] = this.ASTEROID_STONE;
//                            metaArray[index] = this.ASTEROID_STONE_META_1;
                        }
                    }
                }
            }
        }

        if (isHollow) {
            shellThickness = 0.0
            if (shell != null) {
                shellThickness = 1.0 - shell.thickness
            }
            for (x: Int in xMin..<xMax) {
                val indexXY: Int = (x - xMin) * ySize - yMin
                val indexXZ: Int = (x - xMin) * zSize - zMin
                var distanceX: Int = asteroidX - (x + chunkX)
                distanceX *= distanceX

                for (z: Int in zMin..<zMax) {
                    val sizeModY: Float = sizeYArray[indexXZ + z]
                    var sizeY: Float = size + sizeYArray[indexXZ + z]
                    sizeY *= sizeY
                    var distanceZ: Int = asteroidZ - (z + chunkZ)
                    distanceZ *= distanceZ

                    for (y: Int in yMin..<yMax) {
                        var sizeX: Float = size + sizeXArray[(y - yMin) * zSize + z - zMin]
                        var sizeZ: Float = size + sizeZArray[indexXY + y]
                        sizeX *= sizeX
                        sizeZ *= sizeZ
                        var distanceY: Int = asteroidY - y
                        distanceY *= distanceY
                        var distance: Float = distanceX / sizeX + distanceY / sizeY + distanceZ / sizeZ
                        distance += this.asteroidTurbulance.getNoise(
                            (x + chunkX).toFloat(),
                            y.toFloat(),
                            (z + chunkZ).toFloat()
                        )

                        if (distance <= 1) {
                            val state: IBlockState = primer!!.getBlockState(x, y, z)
                            val stateAbove: IBlockState = primer.getBlockState(x, y + 1, z)
                            if (Blocks.AIR === stateAbove.getBlock() && (state.getBlock() === ASTEROID_STONE || state.getBlock() === GRASS)) {
                                if (this.rand.nextInt(GLOWSTONE_CHANCE) == 0) {
                                    primer.setBlockState(
                                        x,
                                        y,
                                        z,
                                        this.LIGHT.getStateFromMeta(this.LIGHT_META.toInt())
                                    )
                                    //                                    blockArray[index] = this.LIGHT;
//                                    metaArray[index] = this.LIGHT_META;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setOtherAxisFrequency(frequency: Float) {
        this.asteroidSkewX.frequencyY = frequency
        this.asteroidSkewX.frequencyZ = frequency

        this.asteroidSkewY.frequencyX = frequency
        this.asteroidSkewY.frequencyZ = frequency

        this.asteroidSkewZ.frequencyX = frequency
        this.asteroidSkewZ.frequencyY = frequency
    }

    private fun clamp(x: Int, min: Int, max: Int): Int {
        var x: Int = x
        if (x < min) {
            x = min
        } else if (x > max) {
            x = max
        }
        return x
    }

    private fun clamp(x: Double, min: Double, max: Double): Double {
        var x: Double = x
        if (x < min) {
            x = min
        } else if (x > max) {
            x = max
        }
        return x
    }

    private fun getTerrainHeightFor(yMod: Float, asteroidY: Int, asteroidSize: Int): Int {
        return (asteroidY - asteroidSize / 4 + yMod * 1.5f).toInt()
    }

    private fun getTerrainHeightAt(
        x: Int,
        z: Int,
        yModArray: FloatArray,
        xMin: Int,
        zMin: Int,
        zSize: Int,
        asteroidY: Int,
        asteroidSize: Int
    ): Int {
        val index: Int = (x - xMin) * zSize - zMin
        if (index < yModArray.size && index >= 0) {
            val yMod: Float = yModArray[index]
            return this.getTerrainHeightFor(yMod, asteroidY, asteroidSize)
        }
        return 1
    }

    override fun generateChunk(par1: Int, par2: Int): Chunk {
        val primer: ChunkPrimer = ChunkPrimer()
        //        long time1 = System.nanoTime();
        this.rand.setSeed(par1 * 341873128712L + par2 * 132897987541L)
        //        final Block[] ids = new Block[65536];
//        final byte[] meta = new byte[65536];
        this.generateTerrain(par1, par2, primer, false)

        //this.biomesForGeneration = this.world.getWorldChunkManager().loadBlockGeneratorData(this.biomesForGeneration, par1 * 16, par2 * 16, 16, 16);
        if (this.world.provider is WorldProviderAsteroids && (this.world.provider as WorldProviderAsteroids).checkHasAsteroids()) {
            this.dungeonGenerator.generate(this.world, par1, par2, primer)
        }

        //        long time2 = System.nanoTime();
        val var4: Chunk = Chunk(this.world, primer, par1, par2)
        val biomesArray: ByteArray = var4.getBiomeArray()

        val b: Byte = Biome.getIdForBiome(BiomeAdaptive.biomeDefault).toByte()
        for (i: Int in biomesArray.indices) {
            biomesArray[i] = b
        }

        //        long time3 = System.nanoTime();
        this.generateSkylightMap(var4, par1, par2)
        //        long time4 = System.nanoTime();
//        if (ConfigManagerCore.enableDebug)
//        {       
//	        BlockVec3 vec = new BlockVec3(par1, par2, 0);
//	        if (chunksDone.contains(vec)) System.out.println("Done chunk already at "+par1+","+par2);
//	        else chunksDone.add(vec);
//        	System.out.println("Chunk gen: " + timeString(time1, time4) + " at "+par1+","+par2 + " - L"+this.largeCount+ " H"+this.largeAsteroids.size()+ " Terrain:"+timeString(time1, time2)+ " Biomes:"+timeString(time2,time3)+ " Light:"+timeString(time3, time4));
//        }
        return var4
    }

    private fun getIndex(x: Int, y: Int, z: Int): Int {
        return x * CHUNK_SIZE_Y * 16 or z * CHUNK_SIZE_Y or y
    }

    private fun timeString(time1: Long, time2: Long): String {
        val ms100: Int = ((time2 - time1) / 10000).toInt()
        val msdecimal: Int = ms100 % 100
        val msd: String = (if (ms100 < 10) "0" else "") + ms100
        return "" + ms100 / 100 + "." + msd + "ms"
    }

    private fun randFromPoint(x: Int, y: Int, z: Int): Float {
        var n: Int = x + z * 57 + y * 571
        n = n xor (n shl 13)
        n = n * (n * n * 15731 + 789221) + 1376312589 and 0x7fffffff
        return 1.0f - n / 1073741824.0f
    }

    private fun randFromPoint(x: Int, z: Int): Float {
        var n: Int = x + z * 57
        n = n xor (n shl 13)
        n = n * (n * n * 15731 + 789221) + 1376312589 and 0x7fffffff
        return 1.0f - n / 1073741824.0f
    }

    private fun randFromPointPos(x: Int, z: Int): Float {
        var n: Int = x + z * 57
        n = n xor (n shl 13)
        n = n * (n * n * 15731 + 789221) + 1376312589 and 0x3fffffff
        return 1.0f - n / 1073741824.0f
    }

    override fun populate(chunkX: Int, chunkZ: Int) {
        var x: Int = chunkX shl 4
        var z: Int = chunkZ shl 4
        if (!chunksDone.add(BlockVec3(x, 0, z))) {
            return
        }

        //        BlockFalling.fallInstantly = true;
//        this.world.getBiome(new BlockPos(x + 16, 0, z + 16));
//        BlockFalling.fallInstantly = false;
        this.rand.setSeed(this.world.getSeed())
        val var7: Long = this.rand.nextLong() / 2L * 2L + 1L
        val var9: Long = this.rand.nextLong() / 2L * 2L + 1L
        this.rand.setSeed(chunkX * var7 + chunkZ * var9 xor this.world.getSeed())

        //50:50 chance to include small blocks each chunk
        if (this.rand.nextBoolean()) {
            val density: Double = this.asteroidDensity.getNoise((chunkX * 16).toFloat(), (chunkZ * 16).toFloat()) * 0.54
            val numOfBlocks: Double = this.clamp(
                this.randFromPoint(chunkX, chunkZ).toDouble(),
                .4,
                1.0
            ) * MAX_BLOCKS_PER_CHUNK * density + MIN_BLOCKS_PER_CHUNK
            val y0: Int = this.rand.nextInt(2)
            var block: Block
            var meta: Int
            val yRange: Int = MAX_ASTEROID_Y - MIN_ASTEROID_Y
            x += 4
            z += 4

            var i: Int = 0
            while (i < numOfBlocks) {
                val y: Int = this.rand.nextInt(yRange) + MIN_ASTEROID_Y

                //50:50 chance vertically as well
                if (y0 == (y / 16) % 2) {
                    val px: Int = x + this.rand.nextInt(CHUNK_SIZE_X)
                    val pz: Int = z + this.rand.nextInt(CHUNK_SIZE_Z)

                    block = this.ASTEROID_STONE
                    meta = this.ASTEROID_STONE_META_1.toInt()

                    if (this.rand.nextInt(ILMENITE_CHANCE) == 0) {
                        meta = 4

                        if (ConfigManagerAsteroids.disableIlmeniteGen) {
                            i++
                            continue
                        }
                    } else if (this.rand.nextInt(IRON_CHANCE) == 0) {
                        meta = 5

                        if (ConfigManagerAsteroids.disableIronGen) {
                            i++
                            continue
                        }
                    } else if (this.rand.nextInt(ALUMINUM_CHANCE) == 0) {
                        meta = 3

                        if (ConfigManagerAsteroids.disableAluminumGen) {
                            i++
                            continue
                        }
                    }

                    world.setBlockState(BlockPos(px, y, pz), block.getStateFromMeta(meta), 2)
                    var count: Int = 9
                    if (world.getBlockState(BlockPos(px - 1, y, pz)).getBlock() !is BlockAir) {
                        count = 1
                    } else if (world.getBlockState(BlockPos(px - 2, y, pz)).getBlock() !is BlockAir) {
                        count = 3
                    } else if (world.getBlockState(BlockPos(px - 3, y, pz)).getBlock() !is BlockAir) {
                        count = 5
                    } else if (world.getBlockState(BlockPos(px - 4, y, pz)).getBlock() !is BlockAir) {
                        count = 7
                    }
                    //LIGHTEMP                    world.setLightFor(EnumSkyBlock.BLOCK, new BlockPos(px - (count > 1 ? 1 : 0), y, pz), count);
                }
                i++
            }
        }

        if (this.largeAsteroidsLastChunkX != chunkX || this.largeAsteroidsLastChunkZ != chunkZ) {
            this.generateTerrain(chunkX, chunkZ, null, true)
        }

        this.rand.setSeed(chunkX * var7 + chunkZ * var9 xor this.world.getSeed())

        //Look for hollow asteroids to populate
        if (!this.largeAsteroids.isEmpty()) {
            for (asteroidIndex: AsteroidData in ArrayList<AsteroidData>(this.largeAsteroids)) {
                if (!asteroidIndex.isHollow) {
                    continue
                }

                val sizeYArray: FloatArray = asteroidIndex.sizeYArray
                val xMin: Int = asteroidIndex.xMinArray
                val zMin: Int = asteroidIndex.zMinArray
                val zSize: Int = asteroidIndex.zSizeArray
                val asteroidY: Int = asteroidIndex.asteroidYArray
                val asteroidSize: Int = asteroidIndex.asteroidSizeArray
                var treesdone: Boolean = false

                if (ConfigManagerCore.challengeAsteroidPopulation || rand.nextInt(TREE_CHANCE) == 0) {
                    var treeType: Int = rand.nextInt(3)
                    if (treeType == 1) {
                        treeType = 0
                    }
                    val log: IBlockState = Blocks.LOG.getDefaultState()
                        .withProperty(
                            BlockOldLog.VARIANT,
                            BlockPlanks.EnumType.OAK
                        )
                    val leaves: IBlockState = Blocks.LEAVES.getDefaultState()
                        .withProperty(
                            BlockOldLeaf.VARIANT,
                            BlockPlanks.EnumType.OAK
                        ).withProperty(BlockLeaves.CHECK_DECAY, false)
                    val wg: WorldGenTrees = WorldGenTrees(false, 2, log, leaves, false)
                    for (tries: Int in 0..4) {
                        val i: Int = rand.nextInt(16) + x + 8
                        val k: Int = rand.nextInt(16) + z + 8
                        if (wg.generate(
                                world,
                                rand,
                                BlockPos(
                                    i,
                                    this.getTerrainHeightAt(
                                        i - x,
                                        k - z,
                                        sizeYArray,
                                        xMin,
                                        zMin,
                                        zSize,
                                        asteroidY,
                                        asteroidSize
                                    ),
                                    k
                                )
                            )
                        ) {
                            break
                        }
                    }
                    treesdone = true
                }
                if (!treesdone || rand.nextInt(TALL_GRASS_CHANCE) == 0) {
                    val i: Int = rand.nextInt(16) + x + 8
                    val k: Int = rand.nextInt(16) + z + 8
                    WorldGenTallGrass(GRASS_TYPE).generate(
                        world,
                        rand,
                        BlockPos(
                            i,
                            this.getTerrainHeightAt(
                                i - x,
                                k - z,
                                sizeYArray,
                                xMin,
                                zMin,
                                zSize,
                                asteroidY,
                                asteroidSize
                            ),
                            k
                        )
                    )
                }
                if (rand.nextInt(FLOWER_CHANCE) == 0) {
                    val i: Int = rand.nextInt(16) + x + 8
                    val k: Int = rand.nextInt(16) + z + 8
                    val types: IntArray = intArrayOf(2, 4, 5, 7)
                    WorldGenFlowers(
                        this.FLOWER,
                        BlockFlower.EnumFlowerType.getType(
                            BlockFlower.EnumFlowerColor.RED,
                            types[rand.nextInt(types.size)]
                        )
                    ).generate(
                        world,
                        rand,
                        BlockPos(
                            i,
                            this.getTerrainHeightAt(
                                i - x,
                                k - z,
                                sizeYArray,
                                xMin,
                                zMin,
                                zSize,
                                asteroidY,
                                asteroidSize
                            ),
                            k
                        )
                    )
                }
                if (rand.nextInt(LAVA_CHANCE) == 0) {
                    val i: Int = rand.nextInt(16) + x + 8
                    val k: Int = rand.nextInt(16) + z + 8
                    WorldGenLakes(this.LAVA).generate(
                        world,
                        rand,
                        BlockPos(
                            i,
                            this.getTerrainHeightAt(
                                i - x,
                                k - z,
                                sizeYArray,
                                xMin,
                                zMin,
                                zSize,
                                asteroidY,
                                asteroidSize
                            ),
                            k
                        )
                    )
                }
                if (rand.nextInt(WATER_CHANCE) == 0) {
                    val i: Int = rand.nextInt(16) + x + 8
                    val k: Int = rand.nextInt(16) + z + 8
                    WorldGenLakes(this.WATER).generate(
                        world,
                        rand,
                        BlockPos(
                            i,
                            this.getTerrainHeightAt(
                                i - x,
                                k - z,
                                sizeYArray,
                                xMin,
                                zMin,
                                zSize,
                                asteroidY,
                                asteroidSize
                            ),
                            k
                        )
                    )
                }
            }
        }

        //Update all block lighting
        for (xx: Int in 0..15) {
            val xPos: Int = x + xx
            for (zz: Int in 0..15) {
                val zPos: Int = z + zz

                //Asteroid at min height 48, size 20, can't have lit blocks below 16
                for (y: Int in 16..239) {
//LIGHTTEMP                    world.checkLightFor(EnumSkyBlock.BLOCK, new BlockPos(xPos, y, zPos));
                }
            }
        }

        this.dungeonGenerator.generateStructure(this.world, this.rand, ChunkPos(chunkX, chunkZ))
    }

    override fun recreateStructures(chunk: Chunk, x: Int, z: Int) {
        this.dungeonGenerator.generate(this.world, x, z, null)
    }

    open fun generateSkylightMap(chunk: Chunk, cx: Int, cz: Int) {
//        World w = chunk.getWorld();
//        boolean flagXChunk = w.getChunkProvider().chunkExists(cx - 1, cz);
//        boolean flagZUChunk = w.getChunkProvider().chunkExists(cx, cz + 1);
//        boolean flagZDChunk = w.getChunkProvider().chunkExists(cx, cz - 1);
//        boolean flagXZUChunk = w.getChunkProvider().chunkExists(cx - 1, cz + 1);
//        boolean flagXZDChunk = w.getChunkProvider().chunkExists(cx - 1, cz - 1);

        val flag: Boolean = world.provider.hasSkyLight()
        for (j: Int in 0..15) {
            if (chunk.getBlockStorageArray()[j] == null) {
                chunk.getBlockStorageArray()[j] = ExtendedBlockStorage(j shl 4, flag)
            }
        }

        val i: Int = chunk.getTopFilledSegment()
        chunk.heightMapMinimum = Int.MAX_VALUE

        for (j: Int in 0..15) {
            var k: Int = 0

            while (k < 16) {
                chunk.precipitationHeightMap[j + (k shl 4)] = -999
                var y: Int = i + 15

                while (true) {
                    if (y > 0) {
                        if (chunk.getBlockLightOpacity(BlockPos(j, y - 1, k)) == 0) {
                            --y
                            continue
                        }

                        chunk.heightMap[k shl 4 or j] = y

                        if (y < chunk.heightMapMinimum) {
                            chunk.heightMapMinimum = y
                        }
                    }

                    ++k
                    break
                }
            }
        }

        for (a: AsteroidData in this.largeAsteroids) {
            var yMin: Int = a.asteroidYArray - a.asteroidSizeArray
            var yMax: Int = a.asteroidYArray + a.asteroidSizeArray
            var xMin: Int = a.xMinArray
            if (yMin < 0) {
                yMin = 0
            }
            if (yMax > 255) {
                yMax = 255
            }
            if (xMin == 0) {
                xMin = 1
            }
            for (x: Int in a.xMax - 1 downTo xMin) {
                for (z: Int in a.zMinArray..<a.zMax) {
                    for (y: Int in yMin..<yMax) {
                        if (chunk.getBlockState(x - 1, y, z).getBlock() is BlockAir && chunk.getBlockState(x, y, z)
                                .getBlock() !is BlockAir
                        ) {
                            var count: Int = 2

                            if (x > 1) {
                                if ((chunk.getBlockState(x - 2, y, z).getBlock() is BlockAir)) {
                                    count += 2
                                }
                            }
                            if (x > 2) {
                                if ((chunk.getBlockState(x - 3, y, z).getBlock() is BlockAir)) {
                                    count += 2
                                }
                                if ((chunk.getBlockState(x - 3, y + 1, z).getBlock() is BlockAir)) {
                                    count++
                                }
                                if ((chunk.getBlockState(x - 3, y + 1, z).getBlock() is BlockAir)) {
                                    count++
                                }
                                if ((z > 0 /*|| ((xPos & 15) > 2 ? flagZDChunk : flagXZDChunk)*/) && (chunk.getBlockState(
                                        x - 3,
                                        y,
                                        z - 1
                                    ).getBlock() is BlockAir)
                                ) {
                                    count++
                                }
                                if ((z < 15 /* || ((xPos & 15) > 2 ? flagZUChunk : flagXZUChunk)*/) && (chunk.getBlockState(
                                        x - 3,
                                        y,
                                        z + 1
                                    ).getBlock() is BlockAir)
                                ) {
                                    count++
                                }
                            }
                            if ( /*flagXChunk || */x > 3) {
                                if ((chunk.getBlockState(x - 4, y, z).getBlock() is BlockAir)) {
                                    count += 2
                                }
                                if ((chunk.getBlockState(x - 4, y + 1, z).getBlock() is BlockAir)) {
                                    count++
                                }
                                if ((chunk.getBlockState(x - 4, y + 1, z).getBlock() is BlockAir)) {
                                    count++
                                }
                                if ((z > 0 /* || ((xPos & 15) > 3 ? flagZDChunk : flagXZDChunk)*/) && chunk.getBlockState(
                                        x - 4,
                                        y,
                                        z - 1
                                    ).getBlock() !is BlockAir
                                ) {
                                    count++
                                }
                                if ((z < 15 /* || ((xPos & 15) > 3 ? flagZUChunk : flagXZUChunk)*/) && chunk.getBlockState(
                                        x - 4,
                                        y,
                                        z + 1
                                    ).getBlock() !is BlockAir
                                ) {
                                    count++
                                }
                            }
                            if (count > 12) {
                                count = 12
                            }
                            if (count > 12) count = 12
                            chunk.setBlockState(BlockPos(x - 1, y, z), (GCBlocks.brightAir)!!.getStateFromMeta(13 - count))
                            val extendedblockstorage: ExtendedBlockStorage? = chunk.getBlockStorageArray()[y shr 4]
                            if (extendedblockstorage != null) {
                                extendedblockstorage.setBlockLight(x - 1, y and 15, z, count + 2)
                            }
                        }
                    }
                }
            }
        }

        chunk.setModified(true)
    }

    open fun resetBase() {
        this.dungeonGenerator.reset()
    }

    override fun getPossibleCreatures(
        creatureType: EnumCreatureType,
        pos: BlockPos
    ): MutableList<Biome.SpawnListEntry?> {
        val biome: Biome = this.world.getBiome(pos)
        return biome.getSpawnableList(creatureType)
    }

    /**
     * Whether a large asteroid is located the provided coordinates
     *
     * @param x0 X-Coordinate to check, in Block Coords
     * @param z0 Z-Coordinate to check, in Block Coords
     * @return True if large asteroid is located here, False if not
     */
    open fun isLargeAsteroidAt(x0: Int, z0: Int): BlockVec3? {
        var xToCheck: Int
        var zToCheck: Int
        for (i0: Int in 0..32) {
            for (i1: Int in -i0..i0) {
                xToCheck = (x0 shr 4) + i0
                zToCheck = (z0 shr 4) + i1

                if (isLargeAsteroidAt0(xToCheck * 16, zToCheck * 16)) {
                    return BlockVec3(xToCheck * 16, 0, zToCheck * 16)
                }

                xToCheck = (x0 shr 4) + i0
                zToCheck = (z0 shr 4) - i1

                if (isLargeAsteroidAt0(xToCheck * 16, zToCheck * 16)) {
                    return BlockVec3(xToCheck * 16, 0, zToCheck * 16)
                }

                xToCheck = (x0 shr 4) - i0
                zToCheck = (z0 shr 4) + i1

                if (isLargeAsteroidAt0(xToCheck * 16, zToCheck * 16)) {
                    return BlockVec3(xToCheck * 16, 0, zToCheck * 16)
                }

                xToCheck = (x0 shr 4) - i0
                zToCheck = (z0 shr 4) - i1

                if (isLargeAsteroidAt0(xToCheck * 16, zToCheck * 16)) {
                    return BlockVec3(xToCheck * 16, 0, zToCheck * 16)
                }
            }
        }

        return null
    }

    private fun isLargeAsteroidAt0(x0: Int, z0: Int): Boolean {
        var x: Int = x0
        while (x < x0 + CHUNK_SIZE_X) {
            var z: Int = z0
            while (z < z0 + CHUNK_SIZE_Z) {
                if ((abs(this.randFromPoint(x, z)) < (this.asteroidDensity.getNoise(
                        x.toFloat(),
                        z.toFloat()
                    ) + .4) / ASTEROID_CHANCE)
                ) {
                    return true
                }
                z += 2
            }
            x += 2
        }

        return false
    }

    private open inner class AsteroidData
    constructor(
        @JvmField var isHollow: Boolean,
        sizeYArray2: FloatArray,
        @JvmField var xMinArray: Int,
        @JvmField var zMinArray: Int,
        @JvmField var xMax: Int,
        @JvmField var zMax: Int,
        @JvmField var zSizeArray: Int,
        @JvmField var asteroidSizeArray: Int,
        @JvmField var asteroidXArray: Int,
        @JvmField var asteroidYArray: Int,
        @JvmField var asteroidZArray: Int
    ) {
        @JvmField
        var sizeYArray: FloatArray

        init {
            this.sizeYArray = sizeYArray2.clone()
        }
    }

    companion object {
        // DO NOT CHANGE
        private const val CHUNK_SIZE_X = 16
        private const val CHUNK_SIZE_Y = 256
        private const val CHUNK_SIZE_Z = 16

        private const val MAX_ASTEROID_RADIUS = 25
        private const val MIN_ASTEROID_RADIUS = 5

        private const val MAX_ASTEROID_SKEW = 8

        private const val MIN_ASTEROID_Y = 48
        private val MAX_ASTEROID_Y: Int = CHUNK_SIZE_Y - 48

        private const val ASTEROID_CHANCE = 800 //About 1 / n chance per XZ pair

        private const val ASTEROID_CORE_CHANCE = 2 //1 / n chance per asteroid
        private const val ASTEROID_SHELL_CHANCE = 2 //1 / n chance per asteroid

        private const val MIN_BLOCKS_PER_CHUNK = 50
        private const val MAX_BLOCKS_PER_CHUNK = 200

        private const val ILMENITE_CHANCE = 400
        private const val IRON_CHANCE = 300
        private const val ALUMINUM_CHANCE = 250

        private const val RANDOM_BLOCK_FADE_SIZE = 32
        private const val FADE_BLOCK_CHANCE = 5 //1 / n chance of a block being in the fade zone

        private const val NOISE_OFFSET_SIZE = 256

        private const val MIN_HOLLOW_SIZE = .6f
        private const val MAX_HOLLOW_SIZE = .8f
        private const val HOLLOW_CHANCE = 10 //1 / n chance per asteroid
        private const val MIN_RADIUS_FOR_HOLLOW = 15
        private const val HOLLOW_LAVA_SIZE = .12f

        //Per chunk per asteroid
        private const val TREE_CHANCE = 2
        private const val TALL_GRASS_CHANCE = 2
        private const val FLOWER_CHANCE = 2
        private const val WATER_CHANCE = 2
        private const val LAVA_CHANCE = 2
        private const val GLOWSTONE_CHANCE = 20

        private val chunksDone = HashSet<BlockVec3?>()

        @JvmStatic
        fun reset() {
            chunksDone.clear()
        }
    }
}
