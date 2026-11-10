package micdoodle8.mods.galacticraft.api.prefab.world.gen

import net.minecraft.util.math.BlockPos
import net.minecraft.world.biome.Biome
import net.minecraft.world.biome.BiomeCache
import net.minecraft.world.biome.BiomeProvider
import net.minecraft.world.gen.layer.IntCache
import java.util.ArrayList
import java.util.Random

/**
 * Do not include this prefab class in your released mod download.
 *
 *
 * This chunk manager is used for single-biome dimensions, which is common on basic planets.
 */
abstract class BiomeProviderSpace : BiomeProvider() {
    private val biomeCache: BiomeCache
    private val biomesToSpawnIn: MutableList<Biome?>

    init {
        this.biomeCache = BiomeCache(this)
        this.biomesToSpawnIn = ArrayList<Biome?>()
        this.biomesToSpawnIn.add(this.getBiome())
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
        par1ArrayOfBiome: Array<Biome?>?,
        par2: Int,
        par3: Int,
        par4: Int,
        par5: Int
    ): Array<Biome?> {
        var par1ArrayOfBiome: Array<Biome?>? = par1ArrayOfBiome
        IntCache.resetIntCache()

        if (par1ArrayOfBiome == null || par1ArrayOfBiome.size < par4 * par5) {
            par1ArrayOfBiome = arrayOfNulls<Biome>(par4 * par5)
        }

        for (var7: Int in 0..<par4 * par5) {
            par1ArrayOfBiome[var7] = this.getBiome()
        }

        return par1ArrayOfBiome
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
        IntCache.resetIntCache()

        if (listToReuse == null || listToReuse.size < width * length) {
            listToReuse = arrayOfNulls<Biome>(width * length)
        }

        if (cacheFlag && width == 16 && length == 16 && (x and 15) == 0 && (z and 15) == 0) {
            val var9: Array<Biome?> = this.biomeCache.getCachedBiomes(x, z)
            System.arraycopy(var9, 0, listToReuse, 0, width * length)
            return listToReuse
        } else {
            for (var8: Int in 0..<width * length) {
                listToReuse[var8] = this.getBiome()
            }

            return listToReuse
        }
    }

    override fun areBiomesViable(par1: Int, par2: Int, par3: Int, par4List: MutableList<Biome?>): Boolean {
        return par4List.contains(this.getBiome())
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
        this.biomeCache.cleanupCache()
    }

    abstract fun getBiome(): Biome
}
