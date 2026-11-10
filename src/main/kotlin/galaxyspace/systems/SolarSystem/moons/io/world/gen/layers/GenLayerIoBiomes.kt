package galaxyspace.systems.SolarSystem.moons.io.world.gen.layers

import galaxyspace.systems.SolarSystem.SolarSystemBodies
import galaxyspace.systems.SolarSystem.moons.io.world.gen.layers.GenLayerIoBiomes
import micdoodle8.mods.miccore.IntCache
import net.minecraft.world.biome.Biome
import net.minecraft.world.gen.layer.GenLayer

open class GenLayerIoBiomes : GenLayer {
    constructor(l: Long, parent: GenLayer?) : super(l) {
        for (i: Int in bodybiome.indices) biomes[i] = bodybiome.get(i)

        this.parent = parent
    }

    constructor(l: Long) : super(l) {
        for (i: Int in bodybiome.indices) biomes[i] = bodybiome.get(i)
    }

    override fun getInts(x: Int, z: Int, width: Int, depth: Int): IntArray {
        val dest: IntArray = IntCache.getIntCache(width * depth)

        for (k: Int in 0..<depth) {
            for (i: Int in 0..<width) {
                initChunkSeed((x + i).toLong(), (z + k).toLong())
                dest[i + k * width] = Biome.getIdForBiome(biomes[nextInt(biomes.size)])
            }
        }

        return dest
    }

    companion object {
        private val bodybiome: MutableList<Biome?> = ((SolarSystemBodies.ioJupiter)!!.getBiomes())!!
        private val biomes = arrayOfNulls<Biome>(bodybiome.size)
    }
}
