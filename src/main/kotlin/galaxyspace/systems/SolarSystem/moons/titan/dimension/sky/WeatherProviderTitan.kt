package galaxyspace.systems.SolarSystem.moons.titan.dimension.sky

import org.lwjgl.opengl.GL11
import galaxyspace.systems.SolarSystem.moons.titan.dimension.sky.WeatherProviderTitan
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
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
import net.minecraftforge.client.IRenderHandler
import java.lang.Exception
import java.lang.reflect.Field
import java.util.Random

open class WeatherProviderTitan constructor() : IRenderHandler() {
    private val random = Random()
    private val rainXCoords = FloatArray(1024)
    private val rainYCoords = FloatArray(1024)

    init {
        for (i: Int in 0..31) {
            val f1: Float = (i - 16).toFloat()
            for (j: Int in 0..31) {
                val f: Float = (j - 16).toFloat()
                val f2: Float = MathHelper.sqrt(f * f + f1 * f1)
                this.rainXCoords[i shl 5 or j] = -f1 / f2
                this.rainYCoords[i shl 5 or j] = f / f2
            }
        }
    }

    override fun render(partialTicks: Float, world: WorldClient, mc: Minecraft) {
        val strength: Float = world.getRainStrength(partialTicks)

        if (strength > 0.0f) {
            var rendererUpdateCount: Int = 0
            try {
                val fieldRUC: Field =
                    mc.entityRenderer.javaClass.getDeclaredField(if (GCCoreUtil.isDeobfuscated()) "rendererUpdateCount" else "field_78529_t")
                fieldRUC.setAccessible(true)
                rendererUpdateCount = fieldRUC.getInt(mc.entityRenderer)
            } catch (e: Exception) {
            }
            mc.entityRenderer.enableLightmap()
            val entity: Entity? = mc.getRenderViewEntity()

            val tessellator: Tessellator = Tessellator.getInstance()
            val worldrenderer: BufferBuilder = tessellator.getBuffer()
            GlStateManager.disableCull()
            GL11.glNormal3f(0.0f, 1.0f, 0.0f)
            GlStateManager.enableBlend()
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
            GlStateManager.alphaFunc(516, 0.1f)
            val d0: Double = entity!!.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks.toDouble()
            val d1: Double = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks.toDouble()
            val d2: Double = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks.toDouble()
            val l: Int = MathHelper.floor(d1)

            var r: Int = 4
            if (mc.gameSettings.fancyGraphics) {
                r = 8
            }

            var drawFlag: Int = -1
            worldrenderer.setTranslation(-d0, -d1, -d2)
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)

            val mutablePos: BlockPos.MutableBlockPos = BlockPos.MutableBlockPos()
            val px: Int = MathHelper.floor(entity.posX)
            val py: Int = MathHelper.floor(entity.posY)
            val pz: Int = MathHelper.floor(entity.posZ)

            for (z: Int in pz - r..pz + r) {
                val indexZ: Int = (z - pz + 16) * 32
                for (x: Int in px - r..px + r) {
                    mutablePos.setPos(x, 0, z)
                    val yHeight: Int = world.getPrecipitationHeight(mutablePos).getY() + 4 - (4.8f * strength).toInt()
                    var y: Int = py - r
                    var ymax: Int = py + r

                    if (y < yHeight) {
                        y = yHeight
                    }

                    if (ymax < yHeight) {
                        ymax = yHeight
                    }

                    var yBase: Int = yHeight

                    if (yHeight < l) {
                        yBase = l
                    }

                    if (y != ymax) {
                        this.random.setSeed((x * x * 3121 + x * 45238971 xor z * z * 418711 + z * 13761).toLong())

                        if (drawFlag != 0) {
                            if (drawFlag >= 0) {
                                tessellator.draw()
                            }

                            drawFlag = 0
                            mc.getTextureManager().bindTexture(RAIN_TEXTURES)
                            worldrenderer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP)
                        }

                        val index: Int = indexZ + x - px + 16
                        val dx: Double = this.rainXCoords[index].toDouble() * 0.5
                        val dz: Double = this.rainYCoords[index].toDouble() * 0.5
                        val dy: Double =
                            -((rendererUpdateCount + x * x * 3121 + x * 45238971 + z * z * 418711 + z * 13761 and 31).toDouble() + partialTicks.toDouble()) / 80.0 * (6.0 + this.random.nextDouble())
                        val yo: Double = this.random.nextDouble() / 1.8
                        val xx: Double = x + 0.5 - entity.posX
                        val zz: Double = z + 0.5 - entity.posZ
                        val rr: Float = MathHelper.sqrt(xx * xx + zz * zz) / r
                        val alpha: Float =
                            ((1.0f - rr * rr) * 0.5f + 0.5f) * strength / 0.6f //0.6F is the max rainstrength on Venus
                        mutablePos.setPos(x, yBase, z)
                        val light: Int = world.getCombinedLight(mutablePos, 0)
                        val lx: Int = light shr 16 and 65535
                        val ly: Int = light and 65535
                        val xc: Double = x + 0.5
                        val zc: Double = z + 0.5
                        worldrenderer.pos(xc - dx, ymax.toDouble() - yo, zc - dz).tex(0.0, y.toDouble() * 0.25 + dy)
                            .color(0.8f, 0.4f, 0.6f, alpha).lightmap(lx, ly).endVertex()
                        worldrenderer.pos(xc + dx, ymax.toDouble() - yo, zc + dz).tex(1.0, y.toDouble() * 0.25 + dy)
                            .color(0.8f, 0.4f, 0.6f, alpha).lightmap(lx, ly).endVertex()
                        worldrenderer.pos(xc + dx, y.toDouble() - yo, zc + dz).tex(1.0, ymax.toDouble() * 0.25 + dy)
                            .color(0.8f, 0.4f, 0.6f, alpha).lightmap(lx, ly).endVertex()
                        worldrenderer.pos(xc - dx, y.toDouble() - yo, zc - dz).tex(0.0, ymax.toDouble() * 0.25 + dy)
                            .color(0.8f, 0.4f, 0.6f, alpha).lightmap(lx, ly).endVertex()
                    }
                }
            }

            if (drawFlag >= 0) {
                tessellator.draw()
            }

            worldrenderer.setTranslation(0.0, 0.0, 0.0)
            GlStateManager.enableCull()
            GlStateManager.disableBlend()
            GlStateManager.alphaFunc(516, 0.1f)
            mc.entityRenderer.disableLightmap()
        }
    }

    companion object {
        private val RAIN_TEXTURES = ResourceLocation(GalacticraftPlanets.ASSET_PREFIX, "textures/misc/rain_venus.png")
    }
}
