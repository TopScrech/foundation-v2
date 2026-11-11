package foundation.verification

import asmodeuscore.core.utils.worldengine.perlinnoise.PerlinNoise
import asmodeuscore.core.utils.worldengine2.util.ISmartMathFunction
import asmodeuscore.core.utils.worldengine2.util.WE_RegionBuffer
import asmodeuscore.core.utils.worldengine2.world.gen.noise.WE_PerlinNoise
import foundation.math.LegacyTerrainMath
import java.util.Random

fun verifyTerrainNoise() {
    // Captured from MathHelper in the cached Forge 1.12.2 runtime with this seed and input sequence
    var hash = 1L
    val random = Random(81179)
    repeat(10000) {
        val angle = (random.nextFloat() - 0.5f) * 200000f
        hash = hash * 31 + LegacyTerrainMath.sin(angle).toRawBits()
        hash = hash * 31 + LegacyTerrainMath.cos(angle).toRawBits()
    }
    check(hash == -1473618173651944488L) { "Terrain trigonometry diverged from the 1.12.2 reference" }
    val cache = WE_RegionBuffer<Long>(32, ISmartMathFunction { x, z -> x * 391L + z * 733L })
    val coordinates = listOf(-1000L, -17L, -9L, -8L, -1L, 0L, 1L, 8L, 9L, 17L, 1000L)
    for (x in coordinates) for (z in coordinates) check(cache.get(x, z) == x * 391L + z * 733L)
    for (z in coordinates.reversed()) for (x in coordinates.reversed()) check(cache.get(x, z) == x * 391L + z * 733L)

    val old = PerlinNoise(12345, 0.5, 4, 64.0, 20.0, 64.0, 80, 2)
    val oldAgain = PerlinNoise(12345, 0.5, 4, 64.0, 20.0, 64.0, 80, 2)
    val modern = WE_PerlinNoise(12345, 0.5, 4, 64.0, 20.0, 64.0, 80, 2)
    val modernAgain = WE_PerlinNoise(12345, 0.5, 4, 64.0, 20.0, 64.0, 80, 2)
    val points = listOf(-1025.25, -512.5, -32.25, -1.5, 0.0, 0.25, 31.5, 512.25, 1025.5)
    val oldHeights = points.associateWith { x -> points.map { z -> old.genNoise2d(x, z) } }
    val modernHeights = points.associateWith { x -> points.map { z -> modern.genNoise2D(x, z) } }
    for (x in points.reversed()) for ((index, z) in points.withIndex()) {
        val a = oldAgain.genNoise2d(x, z)
        val b = modernAgain.genNoise2D(x, z)
        check(a.isFinite() && b.isFinite())
        check(a.toRawBits() == oldHeights.getValue(x)[index].toRawBits())
        check(b.toRawBits() == modernHeights.getValue(x)[index].toRawBits())
    }
}
