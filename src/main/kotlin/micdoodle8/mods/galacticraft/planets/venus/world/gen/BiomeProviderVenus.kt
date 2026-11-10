package micdoodle8.mods.galacticraft.planets.venus.world.gen

import java.util.ArrayList
import java.util.Random

import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeAdaptive
import micdoodle8.mods.galacticraft.planets.venus.VenusModule
import micdoodle8.mods.galacticraft.planets.venus.world.gen.layer.GenLayerVenus
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.WorldType
import net.minecraft.world.biome.Biome
import net.minecraft.world.biome.BiomeCache
import net.minecraft.world.biome.BiomeProvider
import net.minecraft.world.gen.layer.GenLayer
import net.minecraft.world.gen.layer.IntCache
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.*

open class BiomeProviderVenus protected constructor() : BiomeProvider() {
    private var unzoomedBiomes: GenLayer? = null
    private var zoomedBiomes: GenLayer? = null
    private val biomeCache: BiomeCache
    private val biomesToSpawnIn: MutableList<Biome?>
    private val body: CelestialBody?

    init {
        body = VenusModule.planetVenus
        biomeCache = BiomeCache(this)
        biomesToSpawnIn = ArrayList<Biome?>()
        //        biomesToSpawnIn.add()
    }

    constructor(seed: Long, type: WorldType?) : this() {
        val genLayers: Array<GenLayer> = (GenLayerVenus.createWorld(seed) as Array<GenLayer>)
        this.unzoomedBiomes = genLayers[0]
        this.zoomedBiomes = genLayers[1]
    }

    constructor(world: World) : this(world.getSeed(), world.getWorldInfo().getTerrainType())

    override fun getBiomesToSpawnIn(): MutableList<Biome?> {
        return this.biomesToSpawnIn
    }

    override fun getBiome(pos: BlockPos, defaultBiome: Biome?): Biome {
        BiomeAdaptive.setBodyMultiBiome((body)!!)
        return this.biomeCache.getBiome(pos.getX(), pos.getZ(), BiomeAdaptive.biomeDefault)
    }

    @SideOnly(Side.CLIENT)
    override fun getTemperatureAtHeight(par1: Float, par2: Int): Float {
        return par1
    }

    override fun getBiomesForGeneration(biomes: Array<Biome?>?, x: Int, z: Int, length: Int, width: Int): Array<Biome?> {
        var biomes: Array<Biome?>? = biomes
        IntCache.resetIntCache()
        BiomeAdaptive.setBodyMultiBiome((body)!!)

        if (biomes == null || biomes.size < length * width) {
            biomes = arrayOfNulls<Biome>(length * width)
        }

        val intArray: IntArray = unzoomedBiomes!!.getInts(x, z, length, width)

        for (i: Int in 0..<length * width) {
            if (intArray[i] >= 0) {
                biomes[i] = Biome.getBiome(intArray[i])
            } else {
                biomes[i] = BiomeAdaptive.biomeDefault
            }
        }

        return biomes
    }

    override fun getBiomes(oldBiomeList: Array<Biome?>?, x: Int, z: Int, width: Int, depth: Int): Array<Biome?> {
        return getBiomes(oldBiomeList, x, z, width, depth, true)
    }

    override fun getBiomes(
        listToReuse: Array<Biome?>?,
        x: Int,
        z: Int,
        width: Int,
        length: Int,
        cacheFlag: Boolean
    ): Array<Biome?> {
        var listToReuse: Array<Biome?>? = listToReuse
        IntCache.resetIntCache()
        BiomeAdaptive.setBodyMultiBiome((body)!!)

        if (listToReuse == null || listToReuse.size < length * width) {
            listToReuse = arrayOfNulls<Biome>(width * length)
        }

        if (cacheFlag && width == 16 && length == 16 && (x and 15) == 0 && (z and 15) == 0) {
            val cached: Array<Biome?> = this.biomeCache.getCachedBiomes(x, z)
            System.arraycopy(cached, 0, listToReuse, 0, width * length)
            return listToReuse
        }

        val zoomed: IntArray = zoomedBiomes!!.getInts(x, z, width, length)

        for (i: Int in 0..<width * length) {
            if (zoomed[i] >= 0) {
                listToReuse[i] = Biome.getBiome(zoomed[i])
            } else {
                listToReuse[i] = BiomeAdaptive.biomeDefault
            }
        }

        return listToReuse
    }

    override fun areBiomesViable(x: Int, z: Int, range: Int, viables: MutableList<Biome?>): Boolean {
        val i: Int = x - range shr 2
        val j: Int = z - range shr 2
        val k: Int = x + range shr 2
        val l: Int = z + range shr 2
        val diffX: Int = (k - i) + 1
        val diffZ: Int = (l - j) + 1
        val unzoomed: IntArray = this.unzoomedBiomes!!.getInts(i, j, diffX, diffZ)

        for (a: Int in 0..<diffX * diffZ) {
            val biome: Biome? = Biome.getBiome(unzoomed[a])

            if (!viables.contains(biome)) {
                return false
            }
        }

        return true
    }

    override fun findBiomePosition(x: Int, z: Int, range: Int, biomes: MutableList<Biome?>, random: Random): BlockPos? {
        val i: Int = x - range shr 2
        val j: Int = z - range shr 2
        val k: Int = x + range shr 2
        val l: Int = z + range shr 2
        val diffX: Int = (k - i) + 1
        val diffZ: Int = (l - j) + 1
        val unzoomed: IntArray = this.unzoomedBiomes!!.getInts(i, j, diffX, diffZ)
        var blockPos: BlockPos? = null
        var count: Int = 0

        for (a: Int in unzoomed.indices) {
            val x0: Int = i + a % diffX shl 2
            val z0: Int = j + a / diffX shl 2
            val biome: Biome? = Biome.getBiome(unzoomed[a])

            if (biomes.contains(biome) && (blockPos == null || random.nextInt(count + 1) == 0)) {
                blockPos = BlockPos(x0, 0, z0)
                count++
            }
        }

        return blockPos
    }

    override fun cleanupCache() {
        this.biomeCache.cleanupCache()
    }
}
