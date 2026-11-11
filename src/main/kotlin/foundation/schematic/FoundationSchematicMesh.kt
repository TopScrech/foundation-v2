package foundation.schematic

/** The original four 16-pixel tiles and six faces per tile, expressed in block units */
object FoundationSchematicMesh {
    data class Vertex(val x: Float, val y: Float, val z: Float, val u: Float, val v: Float, val nx: Float, val ny: Float, val nz: Float, val lightIndex: Int)
    val vertices: List<Vertex> = buildList {
        for (i in 0..1) for (j in 0..1) {
            val a = (-16 + (i + 1) * 16).toFloat()
            val b = (-16 + i * 16).toFloat()
            val c = (-16 + (j + 1) * 16).toFloat()
            val d = (-16 + j * 16).toFloat()
            val f2 = 0.5f
            val f3 = 0.75f
            val f4 = 0.8125f
            val f5 = 0.0f
            val f6 = 0.0625f
            val f7 = 0.75f
            val f8 = 0.8125f
            val f9 = 0.001953125f
            val f10 = 0.001953125f
            val f11 = 0.7519531f
            val f12 = 0.7519531f
            val f13 = 0.0f
            val f14 = 0.0625f
            val f19 = (32 - i * 16) / 32f
            val f20 = (32 - (i + 1) * 16) / 32f
            val f21 = (32 - j * 16) / 32f
            val f22 = (32 - (j + 1) * 16) / 32f
            fun vertex(x: Float, y: Float, z: Float, u: Float, v: Float, nx: Float, ny: Float, nz: Float) {
                add(Vertex(x / 16f, y / 16f, z / 16f, u, v, nx, ny, nz, i + j * 2))
            }
            vertex(a, d, (-f2), f20, f21, 0.0f, 0.0f, -1.0f)
            vertex(b, d, (-f2), f19, f21, 0.0f, 0.0f, -1.0f)
            vertex(b, c, (-f2), f19, f22, 0.0f, 0.0f, -1.0f)
            vertex(a, c, (-f2), f20, f22, 0.0f, 0.0f, -1.0f)
            vertex(a, c, f2, f3, f5, 0.0f, 0.0f, 1.0f)
            vertex(b, c, f2, f4, f5, 0.0f, 0.0f, 1.0f)
            vertex(b, d, f2, f4, f6, 0.0f, 0.0f, 1.0f)
            vertex(a, d, f2, f3, f6, 0.0f, 0.0f, 1.0f)
            vertex(a, c, (-f2), f7, f9, 0.0f, 1.0f, 0.0f)
            vertex(b, c, (-f2), f8, f9, 0.0f, 1.0f, 0.0f)
            vertex(b, c, f2, f8, f10, 0.0f, 1.0f, 0.0f)
            vertex(a, c, f2, f7, f10, 0.0f, 1.0f, 0.0f)
            vertex(a, d, f2, f7, f9, 0.0f, -1.0f, 0.0f)
            vertex(b, d, f2, f8, f9, 0.0f, -1.0f, 0.0f)
            vertex(b, d, (-f2), f8, f10, 0.0f, -1.0f, 0.0f)
            vertex(a, d, (-f2), f7, f10, 0.0f, -1.0f, 0.0f)
            vertex(a, c, f2, f12, f13, -1.0f, 0.0f, 0.0f)
            vertex(a, d, f2, f12, f14, -1.0f, 0.0f, 0.0f)
            vertex(a, d, (-f2), f11, f14, -1.0f, 0.0f, 0.0f)
            vertex(a, c, (-f2), f11, f13, -1.0f, 0.0f, 0.0f)
            vertex(b, c, (-f2), f12, f13, 1.0f, 0.0f, 0.0f)
            vertex(b, d, (-f2), f12, f14, 1.0f, 0.0f, 0.0f)
            vertex(b, d, f2, f11, f14, 1.0f, 0.0f, 0.0f)
            vertex(b, c, f2, f11, f13, 1.0f, 0.0f, 0.0f)
        }
    }
}
