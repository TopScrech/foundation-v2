package galaxyspace.systems.BarnardsSystem.moons.barnarda_c1.dimension.sky

import galaxyspace.systems.BarnardsSystem.moons.barnarda_c1.dimension.sky.WeatherRendererBarnarda_C1
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.WorldClient
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.Entity
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import net.minecraft.world.biome.Biome
import net.minecraftforge.client.IRenderHandler

open class WeatherRendererBarnarda_C1 constructor() : IRenderHandler() {
    private val rainxs = FloatArray(1024)
    private val rainys = FloatArray(1024)
    private var rendererUpdateCount = 0

    init {
        for (i: Int in 0..31) {
            for (j: Int in 0..31) {
                val f: Float = (j - 16).toFloat()
                val f1: Float = (i - 16).toFloat()
                val f2: Float = MathHelper.sqrt(f * f + f1 * f1)
                this.rainxs[i shl 5 or j] = -f1 / f2
                this.rainys[i shl 5 or j] = f / f2
            }
        }
    }

    open fun tick() {
        ++this.rendererUpdateCount
    }

    override fun render(partialTicks: Float, world: WorldClient?, mc: Minecraft) {
        renderNormalWeather(partialTicks, mc)
    }

    private fun renderNormalWeather(partialTicks: Float, mc: Minecraft) {
        val f: Float = mc.world.getRainStrength(partialTicks)
        if (f > 0.0f) {
            mc.entityRenderer.enableLightmap()
            val entity: Entity? = mc.getRenderViewEntity()
            val world: World = mc.world
            val i: Int = MathHelper.floor(entity!!.posX)
            val j: Int = MathHelper.floor(entity.posY)
            val k: Int = MathHelper.floor(entity.posZ)
            val tessellator: Tessellator = Tessellator.getInstance()
            val bufferbuilder: BufferBuilder = tessellator.getBuffer()
            GlStateManager.disableCull()
            GlStateManager.glNormal3f(0.0f, 1.0f, 0.0f)
            GlStateManager.enableBlend()
            GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
            )
            GlStateManager.alphaFunc(516, 0.1f)
            val d0: Double = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks.toDouble()
            val d1: Double = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks.toDouble()
            val d2: Double = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks.toDouble()
            val l: Int = MathHelper.floor(d1)
            var i1: Int = 5

            if (mc.gameSettings.fancyGraphics) {
                i1 = 10
            }

            var j1: Int = -1
            val f1: Float = this.rendererUpdateCount.toFloat() + partialTicks
            bufferbuilder.setTranslation(-d0, -d1, -d2)
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
            val `blockpos$mutableblockpos`: BlockPos.MutableBlockPos = BlockPos.MutableBlockPos()

            for (k1: Int in k - i1..k + i1) {
                for (l1: Int in i - i1..i + i1) {
                    val i2: Int = (k1 - k + 16) * 32 + l1 - i + 16
                    val d3: Double = this.rainxs[i2].toDouble() * 0.5
                    val d4: Double = this.rainys[i2].toDouble() * 0.5
                    `blockpos$mutableblockpos`.setPos(l1, 0, k1)
                    val biome: Biome = world.getBiome(`blockpos$mutableblockpos`)

                    if (biome.canRain() || biome.getEnableSnow()) {
                        val j2: Int = world.getPrecipitationHeight(`blockpos$mutableblockpos`).getY()
                        var k2: Int = j - i1
                        var l2: Int = j + i1

                        if (k2 < j2) {
                            k2 = j2
                        }

                        if (l2 < j2) {
                            l2 = j2
                        }

                        var i3: Int = j2

                        if (j2 < l) {
                            i3 = l
                        }

                        if (k2 != l2) {
                            world.rand.setSeed((l1 * l1 * 3121 + l1 * 45238971 xor k1 * k1 * 418711 + k1 * 13761).toLong())
                            `blockpos$mutableblockpos`.setPos(l1, k2, k1)
                            val f2: Float = biome.getTemperature(`blockpos$mutableblockpos`)

                            if (world.getBiomeProvider().getTemperatureAtHeight(f2, j2) >= 0.15f) {
                                if (j1 != 0) {
                                    if (j1 >= 0) {
                                        tessellator.draw()
                                    }

                                    j1 = 0
                                    mc.getTextureManager().bindTexture(RAIN_TEXTURES)
                                    bufferbuilder.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP)
                                }

                                val d5: Double =
                                    -((this.rendererUpdateCount + l1 * l1 * 3121 + l1 * 45238971 + k1 * k1 * 418711 + k1 * 13761 and 31).toDouble() + partialTicks.toDouble()) / 32.0 * (3.0 + world.rand.nextDouble())
                                val d6: Double = (l1.toFloat() + 0.5f).toDouble() - entity.posX
                                val d7: Double = (k1.toFloat() + 0.5f).toDouble() - entity.posZ
                                val f3: Float = MathHelper.sqrt(d6 * d6 + d7 * d7) / i1.toFloat()
                                val f4: Float = ((1.0f - f3 * f3) * 0.5f + 0.5f) * f
                                `blockpos$mutableblockpos`.setPos(l1, i3, k1)
                                val j3: Int = world.getCombinedLight(`blockpos$mutableblockpos`, 0)
                                val k3: Int = j3 shr 16 and 65535
                                val l3: Int = j3 and 65535
                                bufferbuilder.pos(l1.toDouble() - d3 + 0.5, l2.toDouble(), k1.toDouble() - d4 + 0.5)
                                    .tex(0.0, k2.toDouble() * 0.25 + d5).color(1.0f, 1.0f, 1.0f, f4).lightmap(k3, l3)
                                    .endVertex()
                                bufferbuilder.pos(l1.toDouble() + d3 + 0.5, l2.toDouble(), k1.toDouble() + d4 + 0.5)
                                    .tex(1.0, k2.toDouble() * 0.25 + d5).color(1.0f, 1.0f, 1.0f, f4).lightmap(k3, l3)
                                    .endVertex()
                                bufferbuilder.pos(l1.toDouble() + d3 + 0.5, k2.toDouble(), k1.toDouble() + d4 + 0.5)
                                    .tex(1.0, l2.toDouble() * 0.25 + d5).color(1.0f, 1.0f, 1.0f, f4).lightmap(k3, l3)
                                    .endVertex()
                                bufferbuilder.pos(l1.toDouble() - d3 + 0.5, k2.toDouble(), k1.toDouble() - d4 + 0.5)
                                    .tex(0.0, l2.toDouble() * 0.25 + d5).color(1.0f, 1.0f, 1.0f, f4).lightmap(k3, l3)
                                    .endVertex()
                            } else {
                                if (j1 != 1) {
                                    if (j1 >= 0) {
                                        tessellator.draw()
                                    }

                                    j1 = 1
                                    mc.getTextureManager().bindTexture(SNOW_TEXTURES)
                                    bufferbuilder.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP)
                                }

                                val d8: Double =
                                    (-((this.rendererUpdateCount and 511).toFloat() + partialTicks) / 512.0f).toDouble()
                                val d9: Double =
                                    world.rand.nextDouble() + f1.toDouble() * 0.01 * (world.rand.nextGaussian()
                                        .toFloat()).toDouble()
                                val d10: Double = world.rand.nextDouble() + (f1 * world.rand.nextGaussian()
                                    .toFloat()).toDouble() * 0.001
                                val d11: Double = (l1.toFloat() + 0.5f).toDouble() - entity.posX
                                val d12: Double = (k1.toFloat() + 0.5f).toDouble() - entity.posZ
                                val f6: Float = MathHelper.sqrt(d11 * d11 + d12 * d12) / i1.toFloat()
                                val f5: Float = ((1.0f - f6 * f6) * 0.3f + 0.5f) * f
                                `blockpos$mutableblockpos`.setPos(l1, i3, k1)
                                val i4: Int = (world.getCombinedLight(`blockpos$mutableblockpos`, 0) * 3 + 15728880) / 4
                                val j4: Int = i4 shr 16 and 65535
                                val k4: Int = i4 and 65535
                                bufferbuilder.pos(l1.toDouble() - d3 + 0.5, l2.toDouble(), k1.toDouble() - d4 + 0.5)
                                    .tex(0.0 + d9, k2.toDouble() * 0.25 + d8 + d10).color(1.0f, 1.0f, 1.0f, f5)
                                    .lightmap(j4, k4).endVertex()
                                bufferbuilder.pos(l1.toDouble() + d3 + 0.5, l2.toDouble(), k1.toDouble() + d4 + 0.5)
                                    .tex(1.0 + d9, k2.toDouble() * 0.25 + d8 + d10).color(1.0f, 1.0f, 1.0f, f5)
                                    .lightmap(j4, k4).endVertex()
                                bufferbuilder.pos(l1.toDouble() + d3 + 0.5, k2.toDouble(), k1.toDouble() + d4 + 0.5)
                                    .tex(1.0 + d9, l2.toDouble() * 0.25 + d8 + d10).color(1.0f, 1.0f, 1.0f, f5)
                                    .lightmap(j4, k4).endVertex()
                                bufferbuilder.pos(l1.toDouble() - d3 + 0.5, k2.toDouble(), k1.toDouble() - d4 + 0.5)
                                    .tex(0.0 + d9, l2.toDouble() * 0.25 + d8 + d10).color(1.0f, 1.0f, 1.0f, f5)
                                    .lightmap(j4, k4).endVertex()
                            }
                        }
                    }
                }
            }

            if (j1 >= 0) {
                tessellator.draw()
            }

            bufferbuilder.setTranslation(0.0, 0.0, 0.0)
            GlStateManager.enableCull()
            GlStateManager.disableBlend()
            GlStateManager.alphaFunc(516, 0.1f)
            mc.entityRenderer.disableLightmap()
        }
    }

    companion object {
        private val RAIN_TEXTURES = ResourceLocation("textures/environment/rain.png")
        private val SNOW_TEXTURES = ResourceLocation("textures/environment/snow.png")
    }
}
