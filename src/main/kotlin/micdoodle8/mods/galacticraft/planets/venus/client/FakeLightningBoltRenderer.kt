package micdoodle8.mods.galacticraft.planets.venus.client

import org.lwjgl.opengl.GL11
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import java.util.Random

object FakeLightningBoltRenderer {
    @JvmStatic
    fun renderBolt(boltVertex: Long, x: Double, y: Double, z: Double) {
        val tessellator: Tessellator = Tessellator.getInstance()
        val worldrenderer: BufferBuilder = tessellator.getBuffer()
        GlStateManager.disableTexture2D()
        GlStateManager.disableLighting()
        GlStateManager.enableBlend()
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE)
        val adouble: DoubleArray = DoubleArray(8)
        val adouble1: DoubleArray = DoubleArray(8)
        var d0: Double = 0.0
        var d1: Double = 0.0
        val random: Random = Random(boltVertex)

        for (i: Int in 7 downTo 0) {
            adouble[i] = d0
            adouble1[i] = d1
            d0 += (random.nextInt(11) - 5).toDouble()
            d1 += (random.nextInt(11) - 5).toDouble()
        }

        for (k1: Int in 0..3) {
            val random1: Random = Random(boltVertex)

            for (j: Int in 0..2) {
                var k: Int = 7
                var l: Int = 0

                if (j > 0) {
                    k = 7 - j
                }

                if (j > 0) {
                    l = k - 2
                }

                var d2: Double = adouble[k] - d0
                var d3: Double = adouble1[k] - d1

                for (i1: Int in k downTo l) {
                    val d4: Double = d2
                    val d5: Double = d3

                    if (j == 0) {
                        d2 += (random1.nextInt(11) - 5).toDouble()
                        d3 += (random1.nextInt(11) - 5).toDouble()
                    } else {
                        d2 += (random1.nextInt(31) - 15).toDouble()
                        d3 += (random1.nextInt(31) - 15).toDouble()
                    }

                    worldrenderer.begin(5, DefaultVertexFormats.POSITION_COLOR)
                    var d6: Double = 0.1 + k1.toDouble() * 0.2

                    if (j == 0) {
                        d6 *= i1.toDouble() * 0.1 + 1.0
                    }

                    var d7: Double = 0.1 + k1.toDouble() * 0.2

                    if (j == 0) {
                        d7 *= (i1 - 1).toDouble() * 0.1 + 1.0
                    }

                    for (j1: Int in 0..4) {
                        var d8: Double = x + 0.5 - d6
                        var d9: Double = z + 0.5 - d6

                        if (j1 == 1 || j1 == 2) {
                            d8 += d6 * 2.0
                        }

                        if (j1 == 2 || j1 == 3) {
                            d9 += d6 * 2.0
                        }

                        var d10: Double = x + 0.5 - d7
                        var d11: Double = z + 0.5 - d7

                        if (j1 == 1 || j1 == 2) {
                            d10 += d7 * 2.0
                        }

                        if (j1 == 2 || j1 == 3) {
                            d11 += d7 * 2.0
                        }

                        worldrenderer.pos(d10 + d2, y + (i1 * 16).toDouble(), d11 + d3).color(0.45f, 0.45f, 0.5f, 0.3f)
                            .endVertex()
                        worldrenderer.pos(d8 + d4, y + ((i1 + 1) * 16).toDouble(), d9 + d5)
                            .color(0.45f, 0.45f, 0.5f, 0.3f).endVertex()
                    }

                    tessellator.draw()
                }
            }
        }

        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GlStateManager.disableBlend()
        GlStateManager.enableLighting()
        GlStateManager.enableTexture2D()
    }
}
