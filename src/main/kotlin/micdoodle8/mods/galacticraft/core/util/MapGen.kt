package micdoodle8.mods.galacticraft.core.util

import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean
import java.nio.channels.FileChannel
import java.nio.ByteOrder
import micdoodle8.mods.miccore.IntCache
import net.minecraft.init.Biomes
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import net.minecraft.world.WorldType
import net.minecraft.world.biome.Biome
import net.minecraft.world.biome.BiomeCache
import net.minecraft.world.biome.BiomeProvider
import net.minecraft.world.gen.ChunkGeneratorSettings
import net.minecraft.world.gen.NoiseGeneratorOctaves
import net.minecraft.world.gen.NoiseGeneratorPerlin
import net.minecraft.world.gen.layer.GenLayer
import net.minecraft.world.storage.WorldInfo
import net.minecraftforge.event.terraingen.InitNoiseGensEvent
import net.minecraftforge.event.terraingen.TerrainGen
import org.apache.commons.io.FileUtils
import java.io.File
import java.lang.Exception
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Random
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.min

open class MapGen constructor(
    worldIn: World,
    sx: Int,
    sz: Int,
    cx: Int,
    cz: Int,
    private val biomeMapFactor: Int,
    file: File
) : BiomeProvider(), Runnable {
    @JvmField
    var mapNeedsCalculating: Boolean = false

    @JvmField
    var finishedCalculating: AtomicBoolean? = null
    private var paused: AtomicBoolean? = null
    private var aborted: AtomicBoolean? = null

    private var progressX: AtomicInteger? = null
    private var progressZ: Int = 0
    private var biomeMapX: Int = 0
    private var biomeMapZ: Int = 0
    private var biomeMap0: Int = 0
    private var biomeMapCx: Int = 0
    private var biomeMapCz: Int = 0
    private val tickLimit: Int
    private var imagefactor: Int = 0

    private var biomeCache: BiomeCache? = null
    private var mapBiomeGenerator: GenLayer? = null
    private var mapBiomeIndexLayer: GenLayer? = null

    @JvmField
    var biomeMapFile: File? = null
    private var biomeAndHeightArray: ByteArray? = null
    private var biomeMapSizeX: Int = 0
    private var biomeMapSizeZ: Int = 0

    private var rand: Random? = null
    private var heights: IntArray? = null
    private var heighttemp: DoubleArray? = null
    private var world: World? = null
    private var worldType: WorldType? = null
    private var worldInfo: WorldInfo? = null
    private var settings: ChunkGeneratorSettings? = null

    private var biomesGrid: IntArray? = null //Memory efficient to keep re-using the same one.
    private var biomesGridHeights: Array<Biome>? = null
    private var biomeCount: IntArray? = null
    private val dimID: Int

    /**
     * Returns -1 if the map file saved by the server on disk is already complete.
     * Returns 0 if a new file is needed
     * Returns >0 if an existing file is in progress (in this case, initialises
     * biomeAndHeightArray and smaller arrays to match that existing file.
     */
    private fun checkProgress(file: File): Int {
        if (!file.exists()) return 0

        if (file.length() != (biomeMapSizeX * biomeMapSizeZ * 2).toLong()) {
            return 0
        }

        var progress: Int = 0
        if (file.getName() == "Overworld" + MapUtil.OVERWORLD_LARGEMAP_WIDTH + ".bin") {
            val len: Int = file.length().toInt()
            val fc: FileChannel
            try {
                fc = (FileChannel.open(file.toPath()))
                fc.position((len - 8).toLong())
                val flagdata: ByteArray = ByteArray(8)
                val databuff: ByteBuffer = ByteBuffer.wrap(flagdata)
                fc.read(databuff)
                if (testFlag(flagdata)) {
                    databuff.order(ByteOrder.BIG_ENDIAN)
                    databuff.position(0)
                    progress = databuff.getInt()
                } else {
                    //No progress flag data, therefore the file must be complete
                    return -1
                }
                fc.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            if (progress > 0 && progress <= biomeMapSizeX - imagefactor) {
                try {
                    this.biomeAndHeightArray = FileUtils.readFileToByteArray(file)
                    this.initialiseSmallerArrays()
                    return progress
                } catch (e: IOException) {
                    e.printStackTrace()
                    this.biomeAndHeightArray = null
                }
            }

            return 0
        }

        //Smaller files are always complete, if present and name and size match
        return -1
    }

    override fun run() {
        //Allow some time for the pause on any other map gen thread to become effective 
        try {
            Thread.sleep(90)
        } catch (e: InterruptedException) {
        }

        val seed: Long = worldInfo!!.getSeed()
        this.initialise(seed)


        //Generate this map from start to finish within the thread
        while (!this.aborted!!.get()) {
            if (this.paused!!.get()) {
                try {
                    //Sleep for a bit, next time around maybe will not be paused?
                    Thread.sleep(1211)
                } catch (e: InterruptedException) {
                }
            } else {
                //Do the actual work of the thread
                if (this.BiomeMapOneTick()) {
                    break //finished!
                }
            }
        }

        this.finishedCalculating!!.set(true)
    }

    open fun pause() {
        this.paused!!.set(true)
    }

    open fun resume() {
        this.paused!!.set(false)
    }

    open fun abort() {
        this.aborted!!.set(true)
    }

    private fun flagProgress() {
        val progX: Int = this.progressX!!.get()
        if (progX > biomeMapSizeX - imagefactor) return

        GCLog.debug("Saving partial map image progress " + progX)
        val offset: Int = this.biomeAndHeightArray!!.size
        this.biomeAndHeightArray!![offset - 1] = 0xFE.toByte()
        this.biomeAndHeightArray!![offset - 2] = 0x06.toByte()
        this.biomeAndHeightArray!![offset - 3] = 0x03.toByte()
        this.biomeAndHeightArray!![offset - 4] = 0x0E.toByte()
        this.biomeAndHeightArray!![offset - 5] = (progX and 0xFF).toByte()
        this.biomeAndHeightArray!![offset - 6] = (progX shr 8 and 0xFF).toByte()
        this.biomeAndHeightArray!![offset - 7] = (progX shr 16 and 0xFF).toByte()
        this.biomeAndHeightArray!![offset - 8] = (progX shr 24 and 0xFF).toByte()
    }

    private fun resumeProgress(progress: Int) {
        var multifactor: Int = biomeMapFactor shr 4
        if (multifactor < 1) {
            multifactor = 1
        }
        val progCount: Int = progress / imagefactor

        progressX!!.set(progress)
        biomeMapX = multifactor * progCount - (biomeMapSizeX * biomeMapFactor / 32)
        if (biomeMapX > -biomeMap0 * 4) {
            biomeMapX += biomeMap0 * 8
        }
    }

    /**
     * This is outside the multithreaded portion of the code
     * This should be called after the finishedCalculating flag is set.
     */
    open fun writeOutputFile(sendToClientImmediately: Boolean) {
        if (this.biomeAndHeightArray == null) return

        if (!this.aborted!!.get())  //It should be error-free if it wasn't aborted 
        {
            try {
                if (!this.biomeMapFile!!.exists() || (this.biomeMapFile!!.canWrite() && this.biomeMapFile!!.canRead())) {
                    this.flagProgress()
                    FileUtils.writeByteArrayToFile(this.biomeMapFile, this.biomeAndHeightArray)
                }
            } catch (ex: IOException) {
                ex.printStackTrace()
            }

            if (sendToClientImmediately) {
                MapUtil.sendMapPacketToAll(this.biomeMapCx shl 4, this.biomeMapCz shl 4, (this.biomeAndHeightArray)!!)
            }
        }

        this.biomeAndHeightArray = null
    }

    private fun initialiseSmallerArrays() {
        this.heights = IntArray(256)
        this.heighttemp = DoubleArray(825)
        this.biomeCount = IntArray(this.tickLimit * this.tickLimit)
    }

    /*
	 * Return false while there are further ticks to carry out 
	 * Return true when completed
	 */
    open fun BiomeMapOneTick(): Boolean {
        if (this.biomeAndHeightArray == null) {
            this.biomeAndHeightArray = ByteArray(biomeMapSizeX * biomeMapSizeZ * 2)
            this.initialiseSmallerArrays()
        }
        var multifactor: Int = biomeMapFactor shr 4
        if (multifactor < 1) {
            multifactor = 1
        }
        val progX: Int = this.progressX!!.get()
        try {
            biomeMapOneChunk(biomeMapCx + biomeMapX, biomeMapCz + biomeMapZ, progX, progressZ, tickLimit)
        } catch (e: Exception) {
            GCLog.severe("Galacticraft background map image generator hit an error (probably a mod conflict?)")
            GCLog.severe("--> Please report this at https://github.com/micdoodle8/Galacticraft/issues/2544 <--")
            e.printStackTrace()
            disabled = true
            this.aborted!!.set(true)
            return true
        }
        biomeMapZ += multifactor
        progressZ += imagefactor
        if (progressZ > biomeMapSizeZ - imagefactor) {
            progressZ = 0
            //            if (progX % 3 == 0)
//            {
//                System.out.println("Finished map column " + progX + " at " + (biomeMapCx + biomeMapX) + "," + (biomeMapCz + biomeMapZ));
//            }
            (this.progressX)!!.set(progX + imagefactor)
            biomeMapZ = biomeMap0
            biomeMapX += multifactor
            if (biomeMapX > -biomeMap0 * 4) {
                biomeMapX += biomeMap0 * 8
            }
            return progX > biomeMapSizeX - imagefactor - imagefactor
        }
        return false
    }

    private fun biomeMapOneChunk(x0: Int, z0: Int, ix: Int, iz: Int, limit: Int) {
        var ix: Int = ix
        var iz: Int = iz
        biomesGrid = this.getBiomeGenAt(biomesGrid, x0 shl 4, z0 shl 4, 16, 16)
        if (biomesGrid == null) {
            return
        }
        this.getHeightMap(x0, z0)
        val factor: Int = this.biomeMapFactor
        val halfFactor: Int = limit * limit / 2
        val cols: ArrayList<Int> = ArrayList<Int>()
        for (j: Int in biomeCount!!.indices) {
            biomeCount!![j] = 0
        }
        var x: Int = 0
        while (x < 16) {
            val izstore: Int = iz
            var z: Int = 0
            while (z < 16) {
                cols.clear()
                var maxcount: Int = 0
                var maxindex: Int = -1
                var biome: Int = -1
                var lastcol: Int = -1
                var idx: Int = 0
                var avgHeight: Int = 0
                var divisor: Int = 0
                //TODO: start in centre instead of top left
                BIOMEDONE@ for (xx: Int in 0..<limit) {
                    val hidx: Int = ((xx + x) shl 4) + z
                    for (zz: Int in 0..<limit) {
                        val height: Int = heights!![hidx + zz]
                        avgHeight += height
                        divisor++
                        biome = biomesGrid!![xx + x + ((zz + z) shl 4)]
                        if (biome != lastcol) {
                            idx = cols.indexOf(biome)
                            if (idx == -1) {
                                idx = cols.size
                                cols.add(biome)
                            }
                            lastcol = biome
                        }
                        biomeCount!![idx] = biomeCount!![idx] + 1
                        if (biomeCount!![idx] > maxcount) {
                            maxcount = biomeCount!![idx]
                            maxindex = idx
                            if (maxcount > halfFactor) {
                                break@BIOMEDONE
                            }
                        }
                    }
                }
                //Clear the array for next time
                Companion.arrayClear(biomeCount!!, cols.size)

                val arrayIndex: Int = (ix * biomeMapSizeZ + iz) * 2
                this.biomeAndHeightArray!![arrayIndex] = (cols.get(maxindex))!!.toByte()
                this.biomeAndHeightArray!![arrayIndex + 1] = ((avgHeight + (divisor + 1) / 2) / divisor).toByte()
                iz++
                z += factor
            }
            iz = izstore
            ix++
            x += factor
        }
    }

    open fun getHeightMap(cx: Int, cz: Int) {
        rand!!.setSeed(cx.toLong() * 341873128712L + cz.toLong() * 132897987541L)
        biomesGridHeights = this.getBiomesForGeneration(biomesGridHeights!!, cx * 4 - 2, cz * 4 - 2, 10, 10)
        this.generateHeightMap(cx * 4, 0, cz * 4)

        val d0: Double = 0.125
        val d9: Double = 0.25

        for (xx: Int in 0..3) {
            val xa: Int = xx * 5
            val xb: Int = xa + 5

            for (zz: Int in 0..3) {
                val aa: Int = (xa + zz) * 33
                val ab: Int = aa + 33
                val ba: Int = (xb + zz) * 33
                val bb: Int = ba + 33

                for (yy: Int in 2..17) {
                    var d1: Double = heighttemp!![aa + yy]
                    var d2: Double = heighttemp!![ab + yy]
                    var d3: Double = heighttemp!![ba + yy]
                    var d4: Double = heighttemp!![bb + yy]
                    val d5: Double = (heighttemp!![aa + yy + 1] - d1) * d0
                    val d6: Double = (heighttemp!![ab + yy + 1] - d2) * d0
                    val d7: Double = (heighttemp!![ba + yy + 1] - d3) * d0
                    val d8: Double = (heighttemp!![bb + yy + 1] - d4) * d0

                    for (y: Int in 0..7) {
                        var d10: Double = d1
                        var d11: Double = d2
                        val d12: Double = (d3 - d1) * d9
                        val d13: Double = (d4 - d2) * d9

                        val truey: Int = yy * 8 + y
                        for (x: Int in 0..3) {
                            val idx: Int = x + xx * 4 shl 4 or zz * 4
                            val d16: Double = (d11 - d10) * d9
                            var d15: Double = d10 - d16

                            for (z: Int in 0..3) {
                                if ((d16.let { d15 += it; d15 }) > 0.0) {
                                    heights!![idx + z] = truey
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

    private var noiseGen1: NoiseGeneratorOctaves? = null
    private var noiseGen2: NoiseGeneratorOctaves? = null
    private var noiseGen3: NoiseGeneratorOctaves? = null

    @JvmField
    var noiseGen4: NoiseGeneratorOctaves? = null

    init {
        this.dimID = GCCoreUtil.getDimensionID(worldIn)
        this.tickLimit = min(biomeMapFactor, 16)
        run initialization@ {
        if (disabled) {
            this.mapNeedsCalculating = false
            return@initialization
        }
        this.biomeMapSizeX = sx
        this.biomeMapSizeZ = sz
        val progress: Int = this.checkProgress(file)
        if (progress < 0) {
            this.mapNeedsCalculating = false
            return@initialization
        }
        this.mapNeedsCalculating = true

        this.rand = Random()
        this.finishedCalculating = AtomicBoolean()
        this.paused = AtomicBoolean()
        this.aborted = AtomicBoolean()

        this.biomeMapCx = cx shr 4
        this.biomeMapCz = cz shr 4
        this.biomeMapFile = file
        this.imagefactor = 16 / biomeMapFactor
        if (this.imagefactor < 1) {
            this.imagefactor = 1
        }
        val limitX: Int = biomeMapSizeX * biomeMapFactor / 32
        val limitZ: Int = biomeMapSizeZ * biomeMapFactor / 32
        this.biomeMap0 = -limitZ
        this.biomeMapX = -limitX
        this.biomeMapZ = this.biomeMap0
        this.progressX = AtomicInteger()
        this.progressZ = 0
        this.world = worldIn
        this.worldInfo = worldIn.getWorldInfo()
        this.worldType = (worldInfo)!!.getTerrainType()
        val seed: Long = (worldInfo)!!.getSeed()
        this.biomeCache = BiomeCache(this)
        val options: String = (worldInfo)!!.getGeneratorOptions()
        var agenlayer: Array<GenLayer>
        try {
            if (options != null) {
                this.settings = ChunkGeneratorSettings.Factory.jsonToFactory(options).build()
            }
            run {
                agenlayer = GenLayer.initializeAllBiomeGenerators(seed, worldType, this.settings)
            }
            agenlayer = getModdedBiomeGenerators(worldType, seed, agenlayer)
        } catch (e: Exception) {
            GCLog.severe("Galacticraft background map image generator not able to run (probably a mod conflict?)")
            GCLog.severe("Please report this at https://github.com/micdoodle8/Galacticraft/issues/2481")
            e.printStackTrace()
            this.mapNeedsCalculating = false
            disabled = true
            return@initialization
        }
        this.mapBiomeGenerator = agenlayer[0]
        this.mapBiomeIndexLayer = agenlayer[1]

        GCLog.debug("Starting map generation " + file.getName() + " top left " + ((biomeMapCx - limitX) * 16) + "," + ((biomeMapCz - limitZ) * 16))
        if (progress > 0) {
            this.resumeProgress(progress)
        }
    
}
}

    open fun initialise(seed: Long) {
        rand = Random(seed)
        noiseGen1 = NoiseGeneratorOctaves(rand, 16)
        noiseGen2 = NoiseGeneratorOctaves(rand, 16)
        noiseGen3 = NoiseGeneratorOctaves(rand, 8)
        val ignore1: NoiseGeneratorPerlin = NoiseGeneratorPerlin(this.rand, 4)
        val ignore2: NoiseGeneratorOctaves = NoiseGeneratorOctaves(this.rand, 10)
        noiseGen4 = NoiseGeneratorOctaves(rand, 16)
        val ignore3: NoiseGeneratorOctaves = NoiseGeneratorOctaves(this.rand, 8)
        var ctx: InitNoiseGensEvent.ContextOverworld =
            InitNoiseGensEvent.ContextOverworld(noiseGen1, noiseGen2, noiseGen3, ignore1, ignore2, noiseGen4, ignore3)
        ctx = TerrainGen.getModdedNoiseGenerators<InitNoiseGensEvent.ContextOverworld>(this.world, this.rand, ctx)
        noiseGen1 = ctx.getLPerlin1()
        noiseGen2 = ctx.getLPerlin2()
        noiseGen3 = ctx.getPerlin()
        noiseGen4 = ctx.getDepth()
    }

    private fun generateHeightMap(cx: Int, cy: Int, cz: Int) {
        val f: Float = this.settings!!.coordinateScale
        val f1: Float = this.settings!!.heightScale
        depthRegion = noiseGen4!!.generateNoiseOctaves(
            depthRegion,
            cx,
            cz,
            5,
            5,
            this.settings!!.depthNoiseScaleX.toDouble(),
            this.settings!!.depthNoiseScaleZ.toDouble(),
            this.settings!!.depthNoiseScaleExponent.toDouble()
        )
        mainNoiseRegion = noiseGen3!!.generateNoiseOctaves(
            mainNoiseRegion,
            cx,
            cy,
            cz,
            5,
            33,
            5,
            (f / this.settings!!.mainNoiseScaleX).toDouble(),
            (f1 / this.settings!!.mainNoiseScaleY).toDouble(),
            (f / this.settings!!.mainNoiseScaleZ).toDouble()
        )
        minLimitRegion = noiseGen1!!.generateNoiseOctaves(
            minLimitRegion,
            cx,
            cy,
            cz,
            5,
            33,
            5,
            f.toDouble(),
            f1.toDouble(),
            f.toDouble()
        )
        maxLimitRegion = noiseGen2!!.generateNoiseOctaves(
            maxLimitRegion,
            cx,
            cy,
            cz,
            5,
            33,
            5,
            f.toDouble(),
            f1.toDouble(),
            f.toDouble()
        )
        val amplified: Boolean = this.worldType === WorldType.AMPLIFIED
        val minLimitScale: Double = this.settings!!.lowerLimitScale.toDouble()
        val maxLimitScale: Double = this.settings!!.upperLimitScale.toDouble()
        val stretchY: Double = this.settings!!.stretchY.toDouble() * 128.0 / 256.0
        val baseSize: Double = this.settings!!.baseSize.toDouble()
        var i: Int =
            2 //start at 2 and later skip 19-33 - because these heightMap entries are never referenced in our code in this class
        var j: Int = 0

        for (xx: Int in 0..4) {
            for (zz: Int in 0..4) {
                var f2: Float = 0.0f
                var f3: Float = 0.0f
                var f4: Float = 0.0f
                val theMinHeight: Float = biomesGridHeights!![xx + 22 + zz * 10].getBaseHeight()

                for (x: Int in -2..2) {
                    val baseIndex: Int = xx + x + 22
                    for (z: Int in -2..2) {
                        val biomegenbase1: Biome? = biomesGridHeights!![baseIndex + (zz + z) * 10]
                        var f5: Float =
                            this.settings!!.biomeDepthOffSet + biomegenbase1!!.getBaseHeight() * this.settings!!.biomeDepthWeight
                        var f6: Float =
                            this.settings!!.biomeScaleOffset + biomegenbase1.getHeightVariation() * this.settings!!.biomeScaleWeight

                        if (amplified && f5 > 0.0f) {
                            f5 = 1.0f + f5 + f5
                            f6 = 1.0f + f6 * 4.0f
                        }

                        var f7: Float = parabolicField[x + 12 + z * 5] / (f5 + 2.0f)

                        if (biomegenbase1.getBaseHeight() > theMinHeight) {
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
                var d7: Double = (depthRegion)!![j] / 8000.0

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
                d8 = d8 * baseSize / 8.0
                val d0: Double = baseSize + d8 * 4.0

                for (j2: Int in 2..18) {
                    var d1: Double = (j2.toDouble() - d0) * stretchY / d9
                    if (d1 < 0.0) {
                        d1 *= 4.0
                    }

                    val d2: Double = (minLimitRegion)!![i] / minLimitScale
                    val d3: Double = (maxLimitRegion)!![i] / maxLimitScale
                    val d4: Double = ((mainNoiseRegion)!![i] / 10.0 + 1.0) / 2.0
                    val d5: Double = MathHelper.clampedLerp(d2, d3, d4) - d1
                    heighttemp!![i] = d5
                    ++i
                }
                i += 16 //We skip j2 = 0-1 and 19-32
            }
        }
    }

    /**
     * REPLICATES method in WorldChunkManager
     * Returns an array of biomes for the location input, used for generating the height map
     */
    override fun getBiomesForGeneration(biomes: Array<Biome>?, x: Int, z: Int, width: Int, height: Int): Array<Biome> {
        var biomes: Array<Biome>? = biomes
        IntCache.resetIntCacheGC()
        val aint: IntArray = this.mapBiomeGenerator!!.getInts(x, z, width, height)

        val size: Int = width * height
        if (biomes == null || biomes.size < size) {
            biomes = (arrayOfNulls<Biome>(size) as Array<Biome>)
        }
        for (i: Int in 0..<size) {
            val biomeId: Int = aint[i]
            val biomegenbase: Biome = Biome.getBiome(biomeId, Biomes.DEFAULT)
            //        	else
//        		System.err.println("MapGen: Biome ID is out of bounds: " + biomeId + ", defaulting to 0 (Ocean)");
            biomes[i] = if (biomegenbase == null) Biomes.OCEAN else biomegenbase
        }

        return biomes
    }

    /**
     * REPLICATES method in WorldChunkManager (with higher performance!)
     * Return a list of ints representing mapgen biomes at the specified coordinates. Args: listToReuse, x, y, width, height
     * This is after all genlayers (oceans, islands, hills, rivers, etc)
     */
    open fun getBiomeGenAt(listToReuse: IntArray?, x: Int, z: Int, width: Int, height: Int): IntArray {
        var listToReuse: IntArray? = listToReuse
        IntCache.resetIntCacheGC()
        val aint: IntArray = this.mapBiomeIndexLayer!!.getInts(x, z, width, height)

        val size: Int = width * height
        if (listToReuse == null || listToReuse.size < size) {
            listToReuse = IntArray(size)
        }
        System.arraycopy(aint, 0, listToReuse, 0, size)

        return listToReuse
    }

    companion object {
        @JvmField
        var disabled: Boolean = false
        private val parabolicField = FloatArray(25)

        init {
            for (j: Int in -2..2) {
                for (k: Int in -2..2) {
                    val f: Float = 10.0f / MathHelper.sqrt((j * j + k * k).toFloat() + 0.2f)
                    parabolicField[j + 2 + (k + 2) * 5] = f
                }
            }
        }

        @JvmStatic
        fun testFlag(bb: ByteArray): Boolean {
            return (bb[7].toInt() and 0xFF) == 0xFE && bb[6].toInt() == 0x06 && bb[5].toInt() == 0x03 && bb[4].toInt() == 0x0E
        }

        @JvmStatic
        fun arrayClear(array: IntArray, len: Int) {
            val lenB: Int = if (len < 16) len else 16
            for (i: Int in 0..<lenB) {
                array[i] = 0
            }
            var i: Int = 16
            while (i < len) {
                System.arraycopy(array, 0, array, i, if (i + i > len) i else len - i)
                i += i
            }
        }

        @JvmField
        var mainNoiseRegion: DoubleArray? = null

        @JvmField
        var minLimitRegion: DoubleArray? = null

        @JvmField
        var maxLimitRegion: DoubleArray? = null

        @JvmField
        var depthRegion: DoubleArray? = null
    }
}
