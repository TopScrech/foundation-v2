package foundation.math

/** Preserves the float lookup arithmetic used by the existing terrain algorithms */
object LegacyTerrainMath {
    private val sine = FloatArray(65536) { index ->
        Math.sin(index.toDouble() * Math.PI * 2.0 / 65536.0).toFloat()
    }

    fun sin(angle: Float): Float = sine[(angle * 10430.378f).toInt() and 65535]

    fun cos(angle: Float): Float = sine[(angle * 10430.378f + 16384f).toInt() and 65535]
}
