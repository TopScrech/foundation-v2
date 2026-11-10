package asmodeuscore.core.utils.worldengine.perlinnoise

import kotlin.math.cos

abstract class GenNoiseTemplate(
    @JvmField var seed: Long,
    @JvmField var persistence: Double,
    @JvmField var octaves: Int,
    @JvmField var scaleX: Double,
    @JvmField var scaleY: Double,
    @JvmField var scaleZ: Double,
    @JvmField var height: Int,
    @JvmField var iType: Byte
) {
    @JvmField
    var rand: WhiteNoise

    init {
        this.rand = WhiteNoise(seed)
    }

    constructor(gSeed: Long, gPersistence: Double, numOfOctaves: Int, scl_xz: Double, scl_y: Double, sum: Int) : this(
        gSeed,
        gPersistence,
        numOfOctaves,
        scl_xz,
        scl_y,
        scl_xz,
        sum,
        3.toByte()
    )

    fun lerp3d(a: Double, b: Double, n: Double): Double {
        return a + (b - a) * n
    }

    fun lerp3f(a: Float, b: Float, n: Float): Float {
        return a + (b - a) * n
    }

    fun smoothstep1d(n: Double): Double {
        return n * n * (3.0 - 2.0 * n)
    }

    fun smoothstep1f(n: Float): Float {
        return n * n * (3.0f - 2.0f * n)
    }

    fun smootherstep1d(n: Double): Double {
        return n * n * n * (n * (n * 6.0 - 15.0) + 10.0)
    }

    fun smootherstepcosine1f(n: Float): Float {
        return ((1.0 - cos(n.toDouble() * Math.PI)) / 2.0).toFloat()
    }

    fun smootherstepcosine1d(n: Double): Double {
        return (1.0 - cos(n * Math.PI)) / 2.0
    }

    fun smootherstep1f(n: Float): Float {
        return n * n * n * (n * (n * 6.0f - 15.0f) + 10.0f)
    }

    fun autoSmooth1d(n: Double): Double {
        when ((this.iType).toInt()) {
            1 -> return this.smoothstep1d(n)
            2 -> return this.smootherstep1d(n)
            3 -> return this.smootherstepcosine1d(n)
            else -> return n
        }
    }

    fun autoSmooth1f(n: Float): Float {
        when ((this.iType).toInt()) {
            1 -> return this.smoothstep1f(n)
            2 -> return this.smootherstep1f(n)
            3 -> return this.smootherstepcosine1f(n)
            else -> return n
        }
    }

    fun dot2d(vec1: DoubleArray, vec2: DoubleArray): Double {
        return vec1[0] * vec2[0] + vec1[1] * vec2[1]
    }

    fun dot2f(vec1: FloatArray, vec2: FloatArray): Float {
        return vec1[0] * vec2[0] + vec1[1] * vec2[1]
    }

    abstract fun noiseOctave2d(var1: Double, var3: Double): Double

    abstract fun noiseOctave2f(var1: Float, var2: Float): Float

    fun genNoise2d(x: Double, z: Double): Double {
        var result: Double = 0.0
        var amplitudeMultiplier: Double = 1.0
        var nowX: Double = x
        var nowZ: Double = z

        for (i: Int in 0..<this.octaves) {
            result += this.noiseOctave2d(nowX / this.scaleX, nowZ / this.scaleZ) * amplitudeMultiplier
            amplitudeMultiplier *= this.persistence
            nowX *= 2.0
            nowZ *= 2.0
        }

        return this.height.toDouble() + result * this.scaleY
    }

    fun genNoise2f(x: Float, z: Float): Float {
        var result: Float = 0.0f
        var amplitudeMultiplier: Float = 1.0f
        var nowX: Float = x
        var nowZ: Float = z

        for (i: Int in 0..<this.octaves) {
            result += this.noiseOctave2f(
                nowX / this.scaleX.toFloat(),
                nowZ / this.scaleZ.toFloat()
            ) * amplitudeMultiplier
            amplitudeMultiplier *= this.persistence.toFloat()
            nowX *= 2.0f
            nowZ *= 2.0f
        }

        return this.height.toFloat() + result * this.scaleY.toFloat()
    }
}
