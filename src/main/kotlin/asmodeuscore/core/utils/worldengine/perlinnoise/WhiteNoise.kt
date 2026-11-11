package asmodeuscore.core.utils.worldengine.perlinnoise

import foundation.math.LegacyTerrainMath

import kotlin.math.pow

open class WhiteNoise constructor(genSeed: Long) {
    @JvmField
    var seed: Long

    init {
        this.seed = genSeed.toDouble().pow(11.0).toLong() * 17L + 514L
    }

    open fun gen2d(x: Long, z: Long): Double {
        var n: Long = this.seed + x * 4L + z * 341L
        n = n shl 13 xor n
        return 1.0 - (n * (n * n * 15731L + 789221L) + 1376312589L and 2147483647L).toDouble() / 1.0737418E9f
    }

    open fun gen2f(x: Int, z: Int): Float {
        var n: Int = this.seed.toInt() + x * 4 + z * 341
        n = n shl 13 xor n
        return 1.0f - (n * (n * n * 15731 + 789221) + 1376312589 and 2147483647).toFloat() / 1.0737418E9f
    }

    open fun vecGen2d(x: Long, z: Long): DoubleArray {
        val angle: Double = this.gen2d(x, z) * Math.PI
        return doubleArrayOf(LegacyTerrainMath.cos(angle.toFloat()).toDouble(), LegacyTerrainMath.sin(angle.toFloat()).toDouble())
    }

    open fun vecGen2f(x: Int, z: Int): FloatArray {
        val angle: Float = this.gen2f(x, z) * Math.PI.toFloat()
        return floatArrayOf(LegacyTerrainMath.cos(angle), LegacyTerrainMath.sin(angle))
    }

    open fun smartGen2d(x: Long, z: Long): Double {
        return this.gen2d(x, z)
    }

    open fun smartGen2f(x: Int, z: Int): Float {
        return this.gen2f(x, z)
    }

    open fun smartVecGen2d(x: Long, z: Long): DoubleArray {
        return this.vecGen2d(x, z)
    }

    open fun smartVecGen2f(x: Int, z: Int): FloatArray {
        return this.vecGen2f(x, z)
    }
}
