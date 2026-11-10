package asmodeuscore.core.utils.worldengine.perlinnoise

import kotlin.math.abs

open class PerlinNoise : GenNoiseTemplate {
    constructor(
        gSeed: Long,
        gPersistence: Double,
        numOfOctaves: Int,
        scl_x: Double,
        scl_y: Double,
        scl_z: Double,
        sum: Int,
        interpolation: Byte
    ) : super(gSeed, gPersistence, numOfOctaves, scl_x, scl_y, scl_z, sum, interpolation)

    constructor(
        gSeed: Long,
        gPersistence: Double,
        numOfOctaves: Int,
        scl_xz: Double,
        scl_y: Double,
        sum: Int
    ) : super(gSeed, gPersistence, numOfOctaves, scl_xz, scl_y, sum)

    override fun noiseOctave2d(x: Double, z: Double): Double {
        val squareStartX: Long = x.toLong()
        val squareStartZ: Long = z.toLong()
        var xs: Long = 1L
        var zs: Long = 1L
        if (abs(x) != x) {
            xs = -1L
        }

        if (abs(z) != z) {
            zs = -1L
        }

        val pointInQuadX: Double = abs(x) - abs(squareStartX).toDouble()
        val pointInQuadZ: Double = abs(z) - abs(squareStartZ).toDouble()
        val topLeft: DoubleArray = this.rand.smartVecGen2d(squareStartX, squareStartZ)
        val topRight: DoubleArray = this.rand.smartVecGen2d(squareStartX + xs, squareStartZ)
        val bottomLeft: DoubleArray = this.rand.smartVecGen2d(squareStartX, squareStartZ + zs)
        val bottomRight: DoubleArray = this.rand.smartVecGen2d(squareStartX + xs, squareStartZ + zs)
        val distanceToTopLeft: DoubleArray = doubleArrayOf(xs.toDouble() * pointInQuadX, zs.toDouble() * pointInQuadZ)
        val distanceToTopRight: DoubleArray =
            doubleArrayOf(xs.toDouble() * pointInQuadX - xs.toDouble(), zs.toDouble() * pointInQuadZ)
        val distanceToBottomLeft: DoubleArray =
            doubleArrayOf(xs.toDouble() * pointInQuadX, zs.toDouble() * pointInQuadZ - zs.toDouble())
        val distanceToBottomRight: DoubleArray =
            doubleArrayOf(xs.toDouble() * pointInQuadX - xs.toDouble(), zs.toDouble() * pointInQuadZ - zs.toDouble())
        val dotTopLeft: Double = this.dot2d(topLeft, distanceToTopLeft)
        val dotTopRight: Double = this.dot2d(topRight, distanceToTopRight)
        val dotBottomLeft: Double = this.dot2d(bottomLeft, distanceToBottomLeft)
        val dotBottomRight: Double = this.dot2d(bottomRight, distanceToBottomRight)
        val line1: Double = this.lerp3d(dotTopLeft, dotTopRight, this.autoSmooth1d(pointInQuadX))
        val line2: Double = this.lerp3d(dotBottomLeft, dotBottomRight, this.autoSmooth1d(pointInQuadX))
        return this.lerp3d(line1, line2, this.autoSmooth1d(pointInQuadZ))
    }

    override fun noiseOctave2f(x: Float, z: Float): Float {
        val squareStartX: Int = x.toInt()
        val squareStartZ: Int = z.toInt()
        var xs: Int = 1
        var zs: Int = 1
        if (abs(x) != x) {
            xs = -1
        }

        if (abs(z) != z) {
            zs = -1
        }

        val pointInQuadX: Float = abs(x) - abs(squareStartX).toFloat()
        val pointInQuadZ: Float = abs(z) - abs(squareStartZ).toFloat()
        val topLeft: FloatArray = this.rand.smartVecGen2f(squareStartX, squareStartZ)
        val topRight: FloatArray = this.rand.smartVecGen2f(squareStartX + xs, squareStartZ)
        val bottomLeft: FloatArray = this.rand.smartVecGen2f(squareStartX, squareStartZ + zs)
        val bottomRight: FloatArray = this.rand.smartVecGen2f(squareStartX + xs, squareStartZ + zs)
        val distanceToTopLeft: FloatArray = floatArrayOf(xs.toFloat() * pointInQuadX, zs.toFloat() * pointInQuadZ)
        val distanceToTopRight: FloatArray =
            floatArrayOf(xs.toFloat() * pointInQuadX - xs.toFloat(), zs.toFloat() * pointInQuadZ)
        val distanceToBottomLeft: FloatArray =
            floatArrayOf(xs.toFloat() * pointInQuadX, zs.toFloat() * pointInQuadZ - zs.toFloat())
        val distanceToBottomRight: FloatArray =
            floatArrayOf(xs.toFloat() * pointInQuadX - xs.toFloat(), zs.toFloat() * pointInQuadZ - zs.toFloat())
        val dotTopLeft: Float = this.dot2f(topLeft, distanceToTopLeft)
        val dotTopRight: Float = this.dot2f(topRight, distanceToTopRight)
        val dotBottomLeft: Float = this.dot2f(bottomLeft, distanceToBottomLeft)
        val dotBottomRight: Float = this.dot2f(bottomRight, distanceToBottomRight)
        val line1: Float = this.lerp3f(dotTopLeft, dotTopRight, this.autoSmooth1f(pointInQuadX))
        val line2: Float = this.lerp3f(dotBottomLeft, dotBottomRight, this.autoSmooth1f(pointInQuadX))
        return this.lerp3f(line1, line2, this.autoSmooth1f(pointInQuadZ))
    }
}
