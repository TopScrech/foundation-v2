package micdoodle8.mods.galacticraft.planets.venus.world.gen.layer

import micdoodle8.mods.miccore.IntCache
import net.minecraft.world.gen.layer.GenLayer

open class GenLayerVenusSurround : GenLayer {
    constructor(l: Long, parent: GenLayer?) : super(l) {
        this.parent = parent
    }

    constructor(l: Long) : super(l)

    override fun getInts(x: Int, z: Int, width: Int, depth: Int): IntArray {
        val nx: Int = x - 1
        val nz: Int = z - 1
        val nwidth: Int = width + 2
        val ndepth: Int = depth + 2
        val input: IntArray? = parent.getInts(nx, nz, nwidth, ndepth)
        val output: IntArray? = IntCache.getIntCache(width * depth)
        for (dz: Int in 0..<depth) {
            for (dx: Int in 0..<width) {
                val right: Int = input!![dx + 0 + (dz + 1) * nwidth]
                val left: Int = input[dx + 2 + (dz + 1) * nwidth]
                val up: Int = input[dx + 1 + (dz + 0) * nwidth]
                val down: Int = input[dx + 1 + (dz + 2) * nwidth]
                val center: Int = input[dx + 1 + (dz + 1) * nwidth]
                //                if (surrounded(BiomeVenus.venusMountain.biomeID, center, right, left, up, down) && nextInt(25) == 0)
//                {
//                    output[dx + dz * width] = BiomeVenus.venusVolcano.biomeID;
//                }
//                else
                run {
                    output!![dx + dz * width] = center
                }
            }
        }

        return output!!
    }

    open fun surrounded(biome: Int, center: Int, right: Int, left: Int, up: Int, down: Int): Boolean {
        if (center != biome) {
            return false
        }

        if (right != biome) {
            return false
        }
        if (left != biome) {
            return false
        }
        if (up != biome) {
            return false
        }
        if (down != biome) {
            return false
        }

        return true
    }
}
