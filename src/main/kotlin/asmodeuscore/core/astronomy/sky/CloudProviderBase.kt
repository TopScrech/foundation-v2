package asmodeuscore.core.astronomy.sky

import org.lwjgl.opengl.GL11
import asmodeuscore.core.astronomy.sky.CloudProviderBase
import asmodeuscore.core.utils.Utils
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel as WorldClient
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.world.entity.Entity
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.util.Mth as MathHelper
import net.minecraft.world.phys.Vec3 as Vec3d
import net.minecraft.world.level.Level as World
import net.minecraftforge.client.IRenderHandler
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

abstract class CloudProviderBase : IRenderHandler() {
    @JvmField
    var cloudTicks: Long = 0

    @JvmField
    var cloudTicksPrev: Long = 0

    @JvmField
    var cloudSpeed: Float = 2f

    @JvmField
    var default_clouds: ResourceLocation = ResourceLocation.parse("textures/environment/clouds.png")

    @JvmField
    var default_color: Vec3d = Vec3d(150.0, 150.0, 150.0)

    override fun render(partialTicks: Float, world: WorldClient, mc: Minecraft) {
        if (world.provider.getCloudRenderer() is CloudProviderBase) {
            if (mc.gameSettings.shouldRenderClouds() >= 1) {
                GL11.glPushMatrix()

                if (mc.gameSettings.fancyGraphics) GL11.glEnable(GL11.GL_FOG)

                this.renderClouds(partialTicks)
                GL11.glDisable(GL11.GL_FOG)

                GL11.glPopMatrix()
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    fun renderClouds(renderPartialTicks: Float) {
        GlStateManager.disableCull()
        val entity: Entity? = mc.getRenderViewEntity()
        val yOffset: Float =
            (entity!!.lastTickPosY + (entity.y - entity.lastTickPosY) * renderPartialTicks.toDouble()).toFloat()
        val cloudSections: Byte = 2
        val tessellator: Tessellator = Tessellator.getInstance()
        val vertexbuffer: BufferBuilder = tessellator.getBuffer()
        var viewX: Double =
            (entity.prevPosX + (entity.x - entity.prevPosX) * renderPartialTicks.toDouble() + getCloudMovementX(
                entity.level(),
                cloudTicksPrev.toFloat(),
                cloudTicks.toFloat()
            ) * 0.029999999329447746) / 12.0
        var viewZ: Double =
            (entity.prevPosZ + (entity.z - entity.prevPosZ) * renderPartialTicks.toDouble() + getCloudMovementZ(
                entity.level(),
                cloudTicksPrev.toFloat(),
                cloudTicks.toFloat()
            ) * 0.029999999329447746) / 12.0
        val cloudHeight: Float = mc.world.provider.getCloudHeight() - yOffset + 0.33f
        viewX = viewX - (MathHelper.floor(viewX / 2048.0) * 2048).toDouble()
        viewZ = viewZ - (MathHelper.floor(viewZ / 2048.0) * 2048).toDouble()
        mc.renderEngine.bindTexture(getCloudTexture())
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(
            GlStateManager.SourceFactor.SRC_ALPHA,
            GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
            GlStateManager.SourceFactor.ONE,
            GlStateManager.DestFactor.ZERO
        )

        val r: Float = getCloudColor(renderPartialTicks)!!.x.toFloat() / 255f
        val g: Float = getCloudColor(renderPartialTicks)!!.y.toFloat() / 255f
        val b: Float = getCloudColor(renderPartialTicks)!!.z.toFloat() / 255f
        var alpha: Float = (1.0f - (entity.y / 700)).toFloat()
        if (alpha < 0.0f) alpha = 0.0f

        val f17: Float = MathHelper.floor(viewX).toFloat() * 0.00390625f
        val f18: Float = MathHelper.floor(viewZ).toFloat() * 0.00390625f
        val f19: Float = (viewX - MathHelper.floor(viewX - 6).toDouble()).toFloat()
        val f20: Float = (viewZ - MathHelper.floor(viewZ - 6).toDouble()).toFloat()
        GlStateManager.scale(12.0f, 1.0f, 12.0f)

        for (pass: Int in 0..1) {
            if (pass == 0) {
                GL11.glColorMask(false, false, false, false)
            } else {
                GL11.glColorMask(true, true, true, true)
            }

            for (x: Int in -cloudSections + 1..cloudSections) {
                for (z: Int in -cloudSections + 1..cloudSections) {
                    vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL)
                    val cU: Float = (x * 8).toFloat()
                    val cV: Float = (z * 8).toFloat()
                    val cX: Float = cU - f19
                    val cZ: Float = cV - f20

                    if (cloudHeight > -5.0f) {
                        vertexbuffer.pos(
                            (cX + 0.0f).toDouble(),
                            (cloudHeight + 0.0f).toDouble(),
                            (cZ + 8.0f).toDouble()
                        )
                            .tex(
                                ((cU + 0.0f) * 0.00390625f + f17).toDouble(),
                                ((cV + 8.0f) * 0.00390625f + f18).toDouble()
                            )
                            .color(r, g, b, alpha).normal(0.0f, -1.0f, 0.0f).endVertex()
                        vertexbuffer.pos(
                            (cX + 8.0f).toDouble(),
                            (cloudHeight + 0.0f).toDouble(),
                            (cZ + 8.0f).toDouble()
                        )
                            .tex(
                                ((cU + 8.0f) * 0.00390625f + f17).toDouble(),
                                ((cV + 8.0f) * 0.00390625f + f18).toDouble()
                            )
                            .color(r, g, b, alpha).normal(0.0f, -1.0f, 0.0f).endVertex()
                        vertexbuffer.pos(
                            (cX + 8.0f).toDouble(),
                            (cloudHeight + 0.0f).toDouble(),
                            (cZ + 0.0f).toDouble()
                        )
                            .tex(
                                ((cU + 8.0f) * 0.00390625f + f17).toDouble(),
                                ((cV + 0.0f) * 0.00390625f + f18).toDouble()
                            )
                            .color(r, g, b, alpha).normal(0.0f, -1.0f, 0.0f).endVertex()
                        vertexbuffer.pos(
                            (cX + 0.0f).toDouble(),
                            (cloudHeight + 0.0f).toDouble(),
                            (cZ + 0.0f).toDouble()
                        )
                            .tex(
                                ((cU + 0.0f) * 0.00390625f + f17).toDouble(),
                                ((cV + 0.0f) * 0.00390625f + f18).toDouble()
                            )
                            .color(r, g, b, alpha).normal(0.0f, -1.0f, 0.0f).endVertex()
                    }

                    if (cloudHeight <= 5.0f) {
                        vertexbuffer
                            .pos(
                                (cX + 0.0f).toDouble(),
                                (cloudHeight + 4.0f - 9.765625E-4f).toDouble(),
                                (cZ + 8.0f).toDouble()
                            )
                            .tex(
                                ((cU + 0.0f) * 0.00390625f + f17).toDouble(),
                                ((cV + 8.0f) * 0.00390625f + f18).toDouble()
                            )
                            .color(r, g, b, alpha).normal(0.0f, 1.0f, 0.0f).endVertex()
                        vertexbuffer
                            .pos(
                                (cX + 8.0f).toDouble(),
                                (cloudHeight + 4.0f - 9.765625E-4f).toDouble(),
                                (cZ + 8.0f).toDouble()
                            )
                            .tex(
                                ((cU + 8.0f) * 0.00390625f + f17).toDouble(),
                                ((cV + 8.0f) * 0.00390625f + f18).toDouble()
                            )
                            .color(r, g, b, alpha).normal(0.0f, 1.0f, 0.0f).endVertex()
                        vertexbuffer
                            .pos(
                                (cX + 8.0f).toDouble(),
                                (cloudHeight + 4.0f - 9.765625E-4f).toDouble(),
                                (cZ + 0.0f).toDouble()
                            )
                            .tex(
                                ((cU + 8.0f) * 0.00390625f + f17).toDouble(),
                                ((cV + 0.0f) * 0.00390625f + f18).toDouble()
                            )
                            .color(r, g, b, alpha).normal(0.0f, 1.0f, 0.0f).endVertex()
                        vertexbuffer
                            .pos(
                                (cX + 0.0f).toDouble(),
                                (cloudHeight + 4.0f - 9.765625E-4f).toDouble(),
                                (cZ + 0.0f).toDouble()
                            )
                            .tex(
                                ((cU + 0.0f) * 0.00390625f + f17).toDouble(),
                                ((cV + 0.0f) * 0.00390625f + f18).toDouble()
                            )
                            .color(r, g, b, alpha).normal(0.0f, 1.0f, 0.0f).endVertex()
                    }

                    if (x > -1) {
                        for (v: Int in 0..7) {
                            vertexbuffer
                                .pos(
                                    (cX + v.toFloat() + 0.0f).toDouble(),
                                    (cloudHeight + 0.0f).toDouble(),
                                    (cZ + 8.0f).toDouble()
                                )
                                .tex(
                                    ((cU + v.toFloat() + 0.5f) * 0.00390625f + f17).toDouble(),
                                    ((cV + 8.0f) * 0.00390625f + f18).toDouble()
                                )
                                .color(r, g, b, alpha).normal(-1.0f, 0.0f, 0.0f).endVertex()
                            vertexbuffer
                                .pos(
                                    (cX + v.toFloat() + 0.0f).toDouble(),
                                    (cloudHeight + 4.0f).toDouble(),
                                    (cZ + 8.0f).toDouble()
                                )
                                .tex(
                                    ((cU + v.toFloat() + 0.5f) * 0.00390625f + f17).toDouble(),
                                    ((cV + 8.0f) * 0.00390625f + f18).toDouble()
                                )
                                .color(r, g, b, alpha).normal(-1.0f, 0.0f, 0.0f).endVertex()
                            vertexbuffer
                                .pos(
                                    (cX + v.toFloat() + 0.0f).toDouble(),
                                    (cloudHeight + 4.0f).toDouble(),
                                    (cZ + 0.0f).toDouble()
                                )
                                .tex(
                                    ((cU + v.toFloat() + 0.5f) * 0.00390625f + f17).toDouble(),
                                    ((cV + 0.0f) * 0.00390625f + f18).toDouble()
                                )
                                .color(r, g, b, alpha).normal(-1.0f, 0.0f, 0.0f).endVertex()
                            vertexbuffer
                                .pos(
                                    (cX + v.toFloat() + 0.0f).toDouble(),
                                    (cloudHeight + 0.0f).toDouble(),
                                    (cZ + 0.0f).toDouble()
                                )
                                .tex(
                                    ((cU + v.toFloat() + 0.5f) * 0.00390625f + f17).toDouble(),
                                    ((cV + 0.0f) * 0.00390625f + f18).toDouble()
                                )
                                .color(r, g, b, alpha).normal(-1.0f, 0.0f, 0.0f).endVertex()
                        }
                    }

                    if (x <= 1) {
                        for (v: Int in 0..7) {
                            vertexbuffer
                                .pos(
                                    (cX + v.toFloat() + 1.0f - 9.765625E-4f).toDouble(), (cloudHeight
                                            + 0.0f).toDouble(), (cZ + 8.0f).toDouble()
                                )
                                .tex(
                                    ((cU + v.toFloat() + 0.5f) * 0.00390625f
                                            + f17).toDouble(), ((cV + 8.0f) * 0.00390625f + f18).toDouble()
                                )
                                .color(r, g, b, alpha).normal(1.0f, 0.0f, 0.0f).endVertex()
                            vertexbuffer
                                .pos(
                                    (cX + v.toFloat() + 1.0f - 9.765625E-4f).toDouble(), (cloudHeight
                                            + 4.0f).toDouble(), (cZ + 8.0f).toDouble()
                                )
                                .tex(
                                    ((cU + v.toFloat() + 0.5f) * 0.00390625f
                                            + f17).toDouble(), ((cV + 8.0f) * 0.00390625f + f18).toDouble()
                                )
                                .color(r, g, b, alpha).normal(1.0f, 0.0f, 0.0f).endVertex()
                            vertexbuffer
                                .pos(
                                    (cX + v.toFloat() + 1.0f - 9.765625E-4f).toDouble(), (cloudHeight
                                            + 4.0f).toDouble(), (cZ + 0.0f).toDouble()
                                )
                                .tex(
                                    ((cU + v.toFloat() + 0.5f) * 0.00390625f
                                            + f17).toDouble(), ((cV + 0.0f) * 0.00390625f + f18).toDouble()
                                )
                                .color(r, g, b, alpha).normal(1.0f, 0.0f, 0.0f).endVertex()
                            vertexbuffer
                                .pos(
                                    (cX + v.toFloat() + 1.0f - 9.765625E-4f).toDouble(), (cloudHeight
                                            + 0.0f).toDouble(), (cZ + 0.0f).toDouble()
                                )
                                .tex(
                                    ((cU + v.toFloat() + 0.5f) * 0.00390625f
                                            + f17).toDouble(), ((cV + 0.0f) * 0.00390625f + f18).toDouble()
                                )
                                .color(r, g, b, alpha).normal(1.0f, 0.0f, 0.0f).endVertex()
                        }
                    }

                    if (z > -1) {
                        for (v: Int in 0..7) {
                            vertexbuffer
                                .pos(
                                    (cX + 0.0f).toDouble(),
                                    (cloudHeight + 4.0f).toDouble(),
                                    (cZ + v.toFloat() + 0.0f).toDouble()
                                )
                                .tex(
                                    ((cU + 0.0f) * 0.00390625f
                                            + f17).toDouble(),
                                    ((cV + v.toFloat() + 0.5f) * 0.00390625f + f18).toDouble()
                                )
                                .color(r, g, b, alpha).normal(0.0f, 0.0f, -1.0f).endVertex()
                            vertexbuffer
                                .pos(
                                    (cX + 8.0f).toDouble(),
                                    (cloudHeight + 4.0f).toDouble(),
                                    (cZ + v.toFloat() + 0.0f).toDouble()
                                )
                                .tex(
                                    ((cU + 8.0f) * 0.00390625f
                                            + f17).toDouble(),
                                    ((cV + v.toFloat() + 0.5f) * 0.00390625f + f18).toDouble()
                                )
                                .color(r, g, b, alpha).normal(0.0f, 0.0f, -1.0f).endVertex()
                            vertexbuffer
                                .pos(
                                    (cX + 8.0f).toDouble(),
                                    (cloudHeight + 0.0f).toDouble(),
                                    (cZ + v.toFloat() + 0.0f).toDouble()
                                )
                                .tex(
                                    ((cU + 8.0f) * 0.00390625f
                                            + f17).toDouble(),
                                    ((cV + v.toFloat() + 0.5f) * 0.00390625f + f18).toDouble()
                                )
                                .color(r, g, b, alpha).normal(0.0f, 0.0f, -1.0f).endVertex()
                            vertexbuffer
                                .pos(
                                    (cX + 0.0f).toDouble(),
                                    (cloudHeight + 0.0f).toDouble(),
                                    (cZ + v.toFloat() + 0.0f).toDouble()
                                )
                                .tex(
                                    ((cU + 0.0f) * 0.00390625f
                                            + f17).toDouble(),
                                    ((cV + v.toFloat() + 0.5f) * 0.00390625f + f18).toDouble()
                                )
                                .color(r, g, b, alpha).normal(0.0f, 0.0f, -1.0f).endVertex()
                        }
                    }

                    if (z <= 1) {
                        for (v: Int in 0..7) {
                            vertexbuffer
                                .pos(
                                    (cX + 0.0f).toDouble(),
                                    (cloudHeight + 4.0f).toDouble(),
                                    (cZ + v.toFloat() + 1.0f - 9.765625E-4f).toDouble()
                                )
                                .tex(
                                    ((cU + 0.0f) * 0.00390625f
                                            + f17).toDouble(),
                                    ((cV + v.toFloat() + 0.5f) * 0.00390625f + f18).toDouble()
                                )
                                .color(r, g, b, alpha).normal(0.0f, 0.0f, 1.0f).endVertex()
                            vertexbuffer
                                .pos(
                                    (cX + 8.0f).toDouble(),
                                    (cloudHeight + 4.0f).toDouble(),
                                    (cZ + v.toFloat() + 1.0f - 9.765625E-4f).toDouble()
                                )
                                .tex(
                                    ((cU + 8.0f) * 0.00390625f
                                            + f17).toDouble(),
                                    ((cV + v.toFloat() + 0.5f) * 0.00390625f + f18).toDouble()
                                )
                                .color(r, g, b, alpha).normal(0.0f, 0.0f, 1.0f).endVertex()
                            vertexbuffer
                                .pos(
                                    (cX + 8.0f).toDouble(),
                                    (cloudHeight + 0.0f).toDouble(),
                                    (cZ + v.toFloat() + 1.0f - 9.765625E-4f).toDouble()
                                )
                                .tex(
                                    ((cU + 8.0f) * 0.00390625f
                                            + f17).toDouble(),
                                    ((cV + v.toFloat() + 0.5f) * 0.00390625f + f18).toDouble()
                                )
                                .color(r, g, b, alpha).normal(0.0f, 0.0f, 1.0f).endVertex()
                            vertexbuffer
                                .pos(
                                    (cX + 0.0f).toDouble(),
                                    (cloudHeight + 0.0f).toDouble(),
                                    (cZ + v.toFloat() + 1.0f - 9.765625E-4f).toDouble()
                                )
                                .tex(
                                    ((cU + 0.0f) * 0.00390625f
                                            + f17).toDouble(),
                                    ((cV + v.toFloat() + 0.5f) * 0.00390625f + f18).toDouble()
                                )
                                .color(r, g, b, alpha).normal(0.0f, 0.0f, 1.0f).endVertex()
                        }
                    }

                    tessellator.draw()
                }
            }
        }
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        GlStateManager.disableBlend()
        GlStateManager.enableCull()
    }

    private fun getCloudMovementX(world: World?, cloudTicksPrev: Float, cloudTicks: Float): Double {
        return Utils.interpolateRotation(cloudTicksPrev, cloudTicks, Minecraft.getInstance().getRenderPartialTicks())
            .toDouble()
    }

    private fun getCloudMovementZ(world: World?, cloudTicksPrev: Float, cloudTicks: Float): Double {
        return 3.0
    }


    abstract fun getCloudMovementSpeed(world: World?): Float

    @OnlyIn(Dist.CLIENT)
    abstract fun getCloudTexture(): ResourceLocation?

    @OnlyIn(Dist.CLIENT)
    abstract fun getCloudColor(renderPartialTicks: Float): Vec3d?

    companion object {
        @JvmField
        var mc: Minecraft = Minecraft.getInstance()
    }
}
