package micdoodle8.mods.galacticraft.planets.venus.world.gen.layer

import micdoodle8.mods.galacticraft.planets.venus.VenusModule
import micdoodle8.mods.miccore.IntCache
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.gen.layer.GenLayer

open class GenLayerVenusBiomes : GenLayer {
    constructor(l: Long, parent: GenLayer?) : super(l) {
        this.parent = parent
    }

    constructor(l: Long) : super(l)

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
        private val biomes = ((VenusModule.planetVenus)!!.biomesToGenerate)!!.toTypedArray<Biome?>()
    }
}
