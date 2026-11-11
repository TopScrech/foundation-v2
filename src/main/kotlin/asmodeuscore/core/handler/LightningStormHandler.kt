package asmodeuscore.core.handler

import asmodeuscore.api.dimension.IProviderWeather
import asmodeuscore.core.handler.LightningStormHandler
import asmodeuscore.core.astronomy.WeatherData.WeatherType
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.effect.EntityLightningBolt
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3 as Vec3d
import net.minecraft.world.level.Level as World
import java.util.Random

object LightningStormHandler {
    @JvmStatic
    fun spawnLightning(player: EntityPlayerMP) {
        val world: World = player.level()
        if (world.provider is IProviderWeather) {
            if ((world.provider as IProviderWeather).getWeather() == null) return

            if (((world.provider as IProviderWeather).getWeather())!!.getWeatherType() == WeatherType.LIGHTNING_STORM) {
                val f: Int = (((world.provider as IProviderWeather).getWeather())!!.getWeatherFrequence() * 100.0).toInt()

                if (f > 0) {
                    if (world.random.nextInt(f) == 0) createLightning(player, world)


                    if (world.random.nextInt(f * 3) == 0) createLightning(player, world)
                }
            }
        }
    }


    @JvmStatic
    private fun createLightning(player: EntityPlayerMP, world: World) {
        val closestPlayer: EntityPlayer? = world.getNearestPlayer(player, 100.0)

        if (closestPlayer == null || closestPlayer.getId() <= player.getId()) {
            val x: Double = player.x + world.random.nextInt(40) - 50
            val z: Double = player.z + world.random.nextInt(40) - 50
            //double y = world.getTopSolidOrLiquidBlock((int)x, (int)z) - 1;//player.posY + (int)(((ILightning)world.provider).getYPosLightning());
            val y: Double =
                world.getTopSolidOrLiquidBlock(BlockPos(x.toInt().toDouble(), player.y, z.toInt().toDouble())).getY()
                    .toDouble()


            //doRender(4, x,y,z, new Vec3d(0.45F, 0.45F, 0.5F), 0.3F);
            val lightning: EntityLightningBolt = EntityLightningBolt(world, x, y, z, false)

            //world.spawnEntity(lightning);
            world.addWeatherEffect(lightning)
        }
    }

    @JvmStatic
    private fun doRender(boltVertex: Int, x: Double, y: Double, z: Double, color: Vec3d, alpha: Float) {
        val tessellator: Tessellator = Tessellator.getInstance()
        val bufferbuilder: BufferBuilder = tessellator.getBuffer()
        GlStateManager.disableTexture2D()
        GlStateManager.disableLighting()
        GlStateManager.enableBlend()
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE)
        val adouble: DoubleArray = DoubleArray(8)
        val adouble1: DoubleArray = DoubleArray(8)
        var d0: Double = 0.0
        var d1: Double = 0.0
        val random: Random = Random(boltVertex.toLong())

        for (i: Int in 7 downTo 0) {
            adouble[i] = d0
            adouble1[i] = d1
            d0 += (random.nextInt(11) - 5).toDouble()
            d1 += (random.nextInt(11) - 5).toDouble()
        }

        for (k1: Int in 0..3) {
            val random1: Random = Random(boltVertex.toLong())

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

                    bufferbuilder.begin(5, DefaultVertexFormats.POSITION_COLOR)
                    val f: Float = 0.5f
                    val f1: Float = 0.45f
                    val f2: Float = 0.45f
                    val f3: Float = 0.5f
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

                        bufferbuilder.pos(d10 + d2, y + (i1 * 16).toDouble(), d11 + d3)
                            .color(color.x.toFloat(), color.y.toFloat(), color.z.toFloat(), alpha).endVertex()
                        bufferbuilder.pos(d8 + d4, y + ((i1 + 1) * 16).toDouble(), d9 + d5)
                            .color(color.x.toFloat(), color.y.toFloat(), color.z.toFloat(), alpha).endVertex()
                    }

                    tessellator.draw()
                }
            }
        }

        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA)
        GlStateManager.disableBlend()
        GlStateManager.enableLighting()
        GlStateManager.enableTexture2D()
    }
}
