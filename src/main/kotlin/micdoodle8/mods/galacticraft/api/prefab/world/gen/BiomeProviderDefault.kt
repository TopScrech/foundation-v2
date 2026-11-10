package micdoodle8.mods.galacticraft.api.prefab.world.gen

import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody
import micdoodle8.mods.galacticraft.api.world.BiomeGenBaseGC
import net.minecraft.util.math.BlockPos
import net.minecraft.world.biome.Biome
import net.minecraft.world.gen.layer.IntCache
import java.util.ArrayList
import java.util.Random

/**
 * A Biome Provider for Galacticraft planets (and other bodies!) which only have 1 biome
 *
 * Makes use of Galacticraft's BiomeAdaptive so that only one Biome is registered in Forge
 * (this BiomeProvider in combination with BiomeAdaptive makes sure the correct actual biome is seen)
 */
open class BiomeProviderDefault constructor(private val body: CelestialBody?) : BiomeProviderSpace() {
    private val biomesToSpawnIn: MutableList<Biome?>

    private val biomeTrue: BiomeGenBaseGC?

    init {
        biomeTrue = BiomeAdaptive.Companion.getDefaultBiomeFor((body)!!)
        this.biomesToSpawnIn = ArrayList<Biome?>()
        this.biomesToSpawnIn.add(this.getBiome())
    }

    override fun getBiome(): Biome {
        if (body != null) BiomeAdaptive.Companion.setBody(body)
        return (BiomeAdaptive.Companion.biomeDefault)!!
    }

    open fun getBiomeTrue(): Biome? {
        return biomeTrue
    }

    override fun getBiomesToSpawnIn(): MutableList<Biome?> {
        return this.biomesToSpawnIn
    }

    override fun getBiome(pos: BlockPos, defaultBiome: Biome?): Biome {
        return this.getBiome()
    }

    override fun getTemperatureAtHeight(par1: Float, par2: Int): Float {
        return par1
    }

    override fun getBiomesForGeneration(
        arrayToReuse: Array<Biome?>?,
        x: Int,
        z: Int,
        width: Int,
        length: Int
    ): Array<Biome?> {
        return this.getBiomes(arrayToReuse, x, z, width, length, true)
    }

    override fun getBiomes(
        par1ArrayOfBiome: Array<Biome?>?,
        par2: Int,
        par3: Int,
        par4: Int,
        par5: Int
    ): Array<Biome?> {
        return this.getBiomes(par1ArrayOfBiome, par2, par3, par4, par5, true)
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
        BiomeAdaptive.Companion.setBody((body)!!)
        IntCache.resetIntCache()
        val size: Int = width * length

        if (listToReuse == null || listToReuse.size < size) {
            listToReuse = arrayOfNulls<Biome>(size)
        }

        if (size <= 256) {
            System.arraycopy(biomeCache, 0, listToReuse, 0, size)
        } else {
            System.arraycopy(biomeCache, 0, listToReuse, 0, 256)
            var i: Int = 256
            while (i < size) {
                System.arraycopy(listToReuse, 0, listToReuse, i, if ((size - i) < i) (size - i) else i)
                i += i
            }
        }

        return listToReuse
    }

    override fun areBiomesViable(par1: Int, par2: Int, par3: Int, par4List: MutableList<Biome?>): Boolean {
        return par4List.contains(this.getBiomeTrue())
    }

    override fun findBiomePosition(
        par1: Int,
        par2: Int,
        par3: Int,
        par4List: MutableList<Biome?>,
        par5Random: Random
    ): BlockPos? {
        val var6: Int = par1 - par3 shr 2
        val var7: Int = par2 - par3 shr 2
        val var8: Int = par1 + par3 shr 2
        val var10: Int = var8 - var6 + 1

        val var16: Int = var6 + 0 % var10 shl 2
        val var17: Int = var7 + 0 / var10 shl 2

        return BlockPos(var16, 0, var17)
    }

    override fun cleanupCache() {
    }

    companion object {
        private val biomeCache = arrayOfNulls<Biome>(256)

        init {
            for (i: Int in 0..15) {
                biomeCache[i] = BiomeAdaptive.Companion.biomeDefault
            }
            System.arraycopy(biomeCache, 0, biomeCache, 16, 16)
            System.arraycopy(biomeCache, 0, biomeCache, 32, 32)
            System.arraycopy(biomeCache, 0, biomeCache, 64, 64)
            System.arraycopy(biomeCache, 0, biomeCache, 128, 128)
        }
    }
}
