package micdoodle8.mods.galacticraft.core.perlin

import java.util.Random

open class SimplexNoise
constructor(seed: Long) {
    @JvmField
    var perm: IntArray = IntArray(512)

    @JvmField
    var grad2d: Array<IntArray?> =
        arrayOf<IntArray?>(intArrayOf(0, 0), intArrayOf(0, 1), intArrayOf(1, 1), intArrayOf(1, 0))

    init {
        val rand: Random = Random(seed)
        for (i: Int in 0..255) {
            this.perm[i] = i // Fill up the random array with numbers 0-256
        }

        for (i: Int in 0..255)  // Shuffle those numbers for the random
        // effect
        {
            val j: Int = rand.nextInt(256)
            this.perm[i] = this.perm[i] xor this.perm[j]
            this.perm[j] = this.perm[i] xor this.perm[j]
            this.perm[i] = this.perm[i] xor this.perm[j]
        }

        System.arraycopy(this.perm, 0, this.perm, 256, 256)
    }
}
