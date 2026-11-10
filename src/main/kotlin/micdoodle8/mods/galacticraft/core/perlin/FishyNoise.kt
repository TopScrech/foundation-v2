package micdoodle8.mods.galacticraft.core.perlin

import java.util.Random

open class FishyNoise
constructor(seed: Long) {
    @JvmField
    var perm: IntArray = IntArray(512)

    @JvmField
    var grad2d: Array<FloatArray> = ((arrayOf<FloatArray?>(
        floatArrayOf(1f, 0f),
        floatArrayOf(.9239f, .3827f),
        floatArrayOf(.707107f, 0.707107f),
        floatArrayOf(.3827f, .9239f),
        floatArrayOf(0f, 1f),
        floatArrayOf(-.3827f, .9239f),
        floatArrayOf(-.707107f, 0.707107f),
        floatArrayOf(-.9239f, .3827f),
        floatArrayOf(-1f, 0f),
        floatArrayOf(-.9239f, -.3827f),
        floatArrayOf(-.707107f, -0.707107f),
        floatArrayOf(-.3827f, -.9239f),
        floatArrayOf(0f, -1f),
        floatArrayOf(.3827f, -.9239f),
        floatArrayOf(.707107f, -0.707107f),
        floatArrayOf(.9239f, -.3827f)
    ) as Array<FloatArray>?))!!

    @JvmField
    var grad3d: Array<IntArray> = ((arrayOf<IntArray?>(
        intArrayOf(1, 1, 0),
        intArrayOf(-1, 1, 0),
        intArrayOf(1, -1, 0),
        intArrayOf(-1, -1, 0),
        intArrayOf(1, 0, 1),
        intArrayOf(-1, 0, 1),
        intArrayOf(1, 0, -1),
        intArrayOf(-1, 0, -1),
        intArrayOf(0, 1, 1),
        intArrayOf(0, -1, 1),
        intArrayOf(0, 1, -1),
        intArrayOf(0, -1, -1),
        intArrayOf(1, 1, 0),
        intArrayOf(-1, 1, 0),
        intArrayOf(0, -1, 1),
        intArrayOf(0, -1, -1)
    ) as Array<IntArray>?))!!

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

    open fun noise2d(x: Float, y: Float): Float {
        var x: Float = x
        var y: Float = y
        var largeX: Int = if (x > 0) x.toInt() else x.toInt() - 1
        var largeY: Int = if (y > 0) y.toInt() else y.toInt() - 1
        x -= largeX.toFloat()
        y -= largeY.toFloat()
        largeX = largeX and 255
        largeY = largeY and 255

        val u: Float = x * x * x * (x * (x * 6 - 15) + 10)
        val v: Float = y * y * y * (y * (y * 6 - 15) + 10)

        val randY: Int = this.perm[largeY] + largeX
        val randY1: Int = this.perm[largeY + 1] + largeX
        var grad2: FloatArray = this.grad2d[this.perm[randY] and 15]
        val grad00: Float = grad2[0] * x + grad2[1] * y
        grad2 = this.grad2d[this.perm[randY1] and 15]
        val grad01: Float = grad2[0] * x + grad2[1] * (y - 1)
        grad2 = this.grad2d[this.perm[1 + randY1] and 15]
        val grad11: Float = grad2[0] * (x - 1) + grad2[1] * (y - 1)
        grad2 = this.grad2d[this.perm[1 + randY] and 15]
        val grad10: Float = grad2[0] * (x - 1) + grad2[1] * y

        val lerpX0: Float = grad00 + u * (grad10 - grad00)
        return lerpX0 + v * (grad01 + u * (grad11 - grad01) - lerpX0)
    }

    open fun noise3d(x: Float, y: Float, z: Float): Float {
        var x: Float = x
        var y: Float = y
        var z: Float = z
        var unitX: Int = if (x > 0) x.toInt() else x.toInt() - 1
        var unitY: Int = if (y > 0) y.toInt() else y.toInt() - 1
        var unitZ: Int = if (z > 0) z.toInt() else z.toInt() - 1

        x -= unitX.toFloat()
        y -= unitY.toFloat()
        z -= unitZ.toFloat()

        unitX = unitX and 255
        unitY = unitY and 255
        unitZ = unitZ and 255

        val u: Float = x * x * x * (x * (x * 6 - 15) + 10)
        val v: Float = y * y * y * (y * (y * 6 - 15) + 10)
        val w: Float = z * z * z * (z * (z * 6 - 15) + 10)

        val randZ: Int = this.perm[unitZ] + unitY
        val randZ1: Int = this.perm[unitZ + 1] + unitY
        val randYZ: Int = this.perm[randZ] + unitX
        val randY1Z: Int = this.perm[1 + randZ] + unitX
        val randYZ1: Int = this.perm[randZ1] + unitX
        val randY1Z1: Int = this.perm[1 + randZ1] + unitX
        var grad3: IntArray = this.grad3d[this.perm[randYZ] and 15]
        val grad000: Float = grad3[0] * x + grad3[1] * y + grad3[2] * z
        grad3 = this.grad3d[this.perm[1 + randYZ] and 15]
        val grad100: Float = grad3[0] * (x - 1) + grad3[1] * y + grad3[2] * z
        grad3 = this.grad3d[this.perm[randY1Z] and 15]
        val grad010: Float = grad3[0] * x + grad3[1] * (y - 1) + grad3[2] * z
        grad3 = this.grad3d[this.perm[1 + randY1Z] and 15]
        val grad110: Float = grad3[0] * (x - 1) + grad3[1] * (y - 1) + grad3[2] * z
        z--
        grad3 = this.grad3d[this.perm[randYZ1] and 15]
        val grad001: Float = grad3[0] * x + grad3[1] * y + grad3[2] * z
        grad3 = this.grad3d[this.perm[1 + randYZ1] and 15]
        val grad101: Float = grad3[0] * (x - 1) + grad3[1] * y + grad3[2] * z
        grad3 = this.grad3d[this.perm[randY1Z1] and 15]
        val grad011: Float = grad3[0] * x + grad3[1] * (y - 1) + grad3[2] * z
        grad3 = this.grad3d[this.perm[1 + randY1Z1] and 15]
        val grad111: Float = grad3[0] * (x - 1) + grad3[1] * (y - 1) + grad3[2] * z

        val f1: Float = grad000 + u * (grad100 - grad000)
        val f2: Float = grad010 + u * (grad110 - grad010)
        val f3: Float = grad001 + u * (grad101 - grad001)
        val f4: Float = grad011 + u * (grad111 - grad011)
        val lerp1: Float = f1 + v * (f2 - f1)
        return lerp1 + w * (f3 + v * (f4 - f3) - lerp1)
    }

    companion object {
        @JvmStatic
        private fun lerp(x: Float, y: Float, n: Float): Float {
            return x + n * (y - x)
        }

        @JvmStatic
        private fun fastFloor(x: Float): Int {
            return if (x > 0) x.toInt() else x.toInt() - 1
        }

        @JvmStatic
        private fun fade(n: Float): Float {
            return n * n * n * (n * (n * 6 - 15) + 10)
        }

        @JvmStatic
        private fun dot2(grad2: FloatArray, x: Float, y: Float): Float {
            return grad2[0] * x + grad2[1] * y
        }

        @JvmStatic
        private fun dot3(grad3: IntArray, x: Float, y: Float, z: Float): Float {
            return grad3[0] * x + grad3[1] * y + grad3[2] * z
        }
    }
}
