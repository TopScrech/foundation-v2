package asmodeuscore.core.utils.worldengine

import kotlin.math.pow
import kotlin.math.abs

object WE_PerlinNoise {
    @JvmStatic
    fun NumberNoise2D(seed: Long, x: Long, y: Long): Double {
        var n: Long = x + y * 31L + seed * 11L
        n = n shl 13 xor n
        return 1.0 - (n * (n * n * 15731L + 789221L) + 1376312589L and 2147483647L).toDouble() / 1.0737418E9f
    }

    @JvmStatic
    fun SmoothNoise2D(seed: Long, x: Long, y: Long): Double {
        val corners: Double = ((NumberNoise2D(seed, x - 1L, y - 1L)
                + NumberNoise2D(seed, x + 1L, y - 1L)
                + NumberNoise2D(seed, x - 1L, y + 1L)
                + NumberNoise2D(seed, x + 1L, y + 1L)
                )
                / 16.0)
        val sides: Double = (NumberNoise2D(seed, x - 1L, y) + NumberNoise2D(seed, x + 1L, y) + NumberNoise2D(
            seed,
            x,
            y - 1L
        ) + NumberNoise2D(seed, x, y + 1L)) / 8.0
        val center: Double = NumberNoise2D(seed, x, y) / 4.0
        return corners + sides + center
    }

    @JvmStatic
    fun CosineInterpolate(a: Double, b: Double, x: Double): Double {
        val f: Double = x * x * x * (x * -9.0 + 10.0)
        return a * (1.0 - f) + b * f
    }

    @JvmStatic
    fun CosineInterpolatedNoise2D(seed: Long, x: Double, y: Double): Double {
        val lx: Long = x.toLong()
        val ly: Long = y.toLong()
        val fx: Double = abs(x) - abs(lx).toDouble()
        val fy: Double = abs(y) - abs(ly).toDouble()
        val v1: Double = SmoothNoise2D(seed, lx, ly)
        val v2: Double = SmoothNoise2D(seed, if (abs(x) == x) lx + 1L else lx - 1L, ly)
        val v3: Double = SmoothNoise2D(seed, lx, if (abs(y) == y) ly + 1L else ly - 1L)
        val v4: Double =
            SmoothNoise2D(seed, if (abs(x) == x) lx + 1L else lx - 1L, if (abs(y) == y) ly + 1L else ly - 1L)
        val i1: Double = CosineInterpolate(v1, v2, fx)
        val i2: Double = CosineInterpolate(v3, v4, fx)
        return CosineInterpolate(i1, i2, fy)
    }

    @JvmStatic
    fun PerlinNoise2D(seed: Long, x: Double, y: Double, persistence: Double, number_of_octaves: Int): Double {
        var total: Double = 0.0
        var amplitude: Double = 0.0
        var frequency: Double = 0.0

        for (i: Int in 0..number_of_octaves) {
            frequency = (1 shl i).toDouble()
            amplitude = persistence.pow(i.toDouble())
            total += CosineInterpolatedNoise2D(seed, x * frequency, y * frequency) * amplitude
        }

        return total
    }
}
