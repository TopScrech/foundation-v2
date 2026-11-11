package micdoodle8.mods.galacticraft.core.client.render.tile

import java.util.HashMap

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.blocks.BlockPlatform
import micdoodle8.mods.galacticraft.core.tile.TileEntityPlatform
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.client.model.ModelBase
import net.minecraft.client.model.ModelRenderer
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import org.lwjgl.opengl.GL11
import kotlin.math.abs

@OnlyIn(Dist.CLIENT)
open class TileEntityPlatformRenderer : TileEntitySpecialRenderer<TileEntityPlatform>() {
    open inner class ModelPlatform @JvmOverloads constructor(var1: Float = 0.0f) : ModelBase() {
        @JvmField
        var panelMain: ModelRenderer

        init {
            this.textureWidth = 128
            this.textureHeight = 128
            this.panelMain = ModelRenderer(this, 0, 0)
            this.panelMain.addBox(-22f, -3.5f, -22f, 44, 7, 44)
            this.panelMain.setRotationPoint(0f, 0f, 0f)
            this.panelMain.setTextureSize(128, 128)
            this.panelMain.mirror = true
            this.setRotation(this.panelMain, 0f, 0f, 0f)
        }

        private fun setRotation(model: ModelRenderer, x: Float, y: Float, z: Float) {
            model.rotateAngleX = x
            model.rotateAngleY = y
            model.rotateAngleZ = z
        }

        open fun render() {
            this.panelMain.render(1.125f / 44f)
        }
    }

    private val platform: ModelPlatform = ModelPlatform()
    override fun render(
        tileEntity: TileEntityPlatform,
        d: Double,
        d1: Double,
        d2: Double,
        f: Float,
        par9: Int,
        alpha: Float
    ) {
        if (f != lastPartialTicks) {
            lastPartialTicks = f
            lastYMap.clear()
        }
        val b: IBlockState = tileEntity.getWorld().getBlockState(tileEntity.getPos())
        val yOffset: Float = tileEntity.getYOffset(f)
        if (b.getBlock() === GCBlocks.platform && b.getValue(BlockPlatform.CORNER) == BlockPlatform.EnumCorner.NW) {
            GlStateManager.pushMatrix()
            GlStateManager.translate(d.toFloat() + 0.5f, d1.toFloat() + 0.5f, d2.toFloat() + 0.5f)
            GlStateManager.rotate(90f, 0f, 1f, 0f)
            GlStateManager.translate(-0.5f, -0.5f, 0.5f)
            RenderHelper.disableStandardItemLighting()
            GlStateManager.disableRescaleNormal()

            // Render a moving platform
            var renderPlatformForThisTE: Boolean = false
            val newY: Float = d1.toFloat() + yOffset
            var xz: Int = tenLSB(tileEntity.getPos().getX()) shl 10
            xz += tenLSB(tileEntity.getPos().getZ())
            val lastYF: Float? = lastYMap.get(xz)
            val lastY: Float = if (lastYF == null) -1f else lastYF
            if (!tileEntity.isMoving() || abs(newY - lastY) > 0.001f || abs(f - lastPartialTicks) > 0.001f) {
                renderPlatformForThisTE = true
                lastYMap.put(xz, newY)
                GlStateManager.pushMatrix()
                if (tileEntity.isMoving()) {
                    OpenGlHelper.setLightmapTextureCoords(
                        OpenGlHelper.lightmapTexUnit,
                        tileEntity.getMeanLightX(yOffset),
                        tileEntity.getMeanLightZ(yOffset)
                    )
                } else {
                    val light: Int = tileEntity.getBlendedLight()
                    OpenGlHelper.setLightmapTextureCoords(
                        OpenGlHelper.lightmapTexUnit,
                        (light % 65536).toFloat(),
                        (light / 65536).toFloat()
                    )
                }
                GlStateManager.translate(0f, 0.79f + yOffset, 0f)
                this.bindTexture(platformTexture)
                this.platform.render()
                GlStateManager.popMatrix()
            }

            if (tileEntity.lightEnabled() || tileEntity.isMoving()) {
                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f)
                GlStateManager.disableLighting()
                GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
                GlStateManager.disableTexture2D()

                this.bindTexture(lightTexture)
                val tess: Tessellator = Tessellator.getInstance()
                val worldRenderer: BufferBuilder = tess.getBuffer()
                var frameA: Float
                var frameB: Float
                var frameC: Float
                var frameD: Float

                // Draw the moving platform side-lights 
                if (tileEntity.isMoving() && renderPlatformForThisTE) {
                    GlStateManager.pushMatrix()
                    GlStateManager.translate(0f, 0.26f + yOffset, 0f)
                    GlStateManager.color(1.0f, 0.84f, 65f / 255f, 1.0f)

                    val frameRadius: Float = 1.126f / 2f
                    frameB = frameRadius - 0.04f
                    frameC = -frameB
                    frameA = 0.552f
                    frameD = 0.578f
                    worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
                    worldRenderer.pos(-frameRadius.toDouble(), frameA.toDouble(), frameB.toDouble()).endVertex()
                    worldRenderer.pos(-frameRadius.toDouble(), frameD.toDouble(), frameB.toDouble()).endVertex()
                    worldRenderer.pos(-frameRadius.toDouble(), frameD.toDouble(), frameC.toDouble()).endVertex()
                    worldRenderer.pos(-frameRadius.toDouble(), frameA.toDouble(), frameC.toDouble()).endVertex()
                    tess.draw()
                    frameA = 0.482f
                    frameD = 0.504666f
                    worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
                    worldRenderer.pos(-frameRadius.toDouble(), frameA.toDouble(), frameB.toDouble()).endVertex()
                    worldRenderer.pos(-frameRadius.toDouble(), frameD.toDouble(), frameB.toDouble()).endVertex()
                    worldRenderer.pos(-frameRadius.toDouble(), frameD.toDouble(), frameC.toDouble()).endVertex()
                    worldRenderer.pos(-frameRadius.toDouble(), frameA.toDouble(), frameC.toDouble()).endVertex()
                    tess.draw()
                    GlStateManager.rotate(90f, 0f, 1f, 0f)
                    frameA = 0.552f
                    frameD = 0.578f
                    worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
                    worldRenderer.pos(-frameRadius.toDouble(), frameA.toDouble(), frameB.toDouble()).endVertex()
                    worldRenderer.pos(-frameRadius.toDouble(), frameD.toDouble(), frameB.toDouble()).endVertex()
                    worldRenderer.pos(-frameRadius.toDouble(), frameD.toDouble(), frameC.toDouble()).endVertex()
                    worldRenderer.pos(-frameRadius.toDouble(), frameA.toDouble(), frameC.toDouble()).endVertex()
                    tess.draw()
                    frameA = 0.482f
                    frameD = 0.504666f
                    worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
                    worldRenderer.pos(-frameRadius.toDouble(), frameA.toDouble(), frameB.toDouble()).endVertex()
                    worldRenderer.pos(-frameRadius.toDouble(), frameD.toDouble(), frameB.toDouble()).endVertex()
                    worldRenderer.pos(-frameRadius.toDouble(), frameD.toDouble(), frameC.toDouble()).endVertex()
                    worldRenderer.pos(-frameRadius.toDouble(), frameA.toDouble(), frameC.toDouble()).endVertex()
                    tess.draw()
                    GlStateManager.rotate(90f, 0f, 1f, 0f)
                    frameA = 0.552f
                    frameD = 0.578f
                    worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
                    worldRenderer.pos(-frameRadius.toDouble(), frameA.toDouble(), frameB.toDouble()).endVertex()
                    worldRenderer.pos(-frameRadius.toDouble(), frameD.toDouble(), frameB.toDouble()).endVertex()
                    worldRenderer.pos(-frameRadius.toDouble(), frameD.toDouble(), frameC.toDouble()).endVertex()
                    worldRenderer.pos(-frameRadius.toDouble(), frameA.toDouble(), frameC.toDouble()).endVertex()
                    tess.draw()
                    frameA = 0.482f
                    frameD = 0.504666f
                    worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
                    worldRenderer.pos(-frameRadius.toDouble(), frameA.toDouble(), frameB.toDouble()).endVertex()
                    worldRenderer.pos(-frameRadius.toDouble(), frameD.toDouble(), frameB.toDouble()).endVertex()
                    worldRenderer.pos(-frameRadius.toDouble(), frameD.toDouble(), frameC.toDouble()).endVertex()
                    worldRenderer.pos(-frameRadius.toDouble(), frameA.toDouble(), frameC.toDouble()).endVertex()
                    tess.draw()
                    GlStateManager.rotate(90f, 0f, 1f, 0f)
                    frameA = 0.552f
                    frameD = 0.578f
                    worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
                    worldRenderer.pos(-frameRadius.toDouble(), frameA.toDouble(), frameB.toDouble()).endVertex()
                    worldRenderer.pos(-frameRadius.toDouble(), frameD.toDouble(), frameB.toDouble()).endVertex()
                    worldRenderer.pos(-frameRadius.toDouble(), frameD.toDouble(), frameC.toDouble()).endVertex()
                    worldRenderer.pos(-frameRadius.toDouble(), frameA.toDouble(), frameC.toDouble()).endVertex()
                    tess.draw()
                    frameA = 0.482f
                    frameD = 0.504666f
                    worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
                    worldRenderer.pos(-frameRadius.toDouble(), frameA.toDouble(), frameB.toDouble()).endVertex()
                    worldRenderer.pos(-frameRadius.toDouble(), frameD.toDouble(), frameB.toDouble()).endVertex()
                    worldRenderer.pos(-frameRadius.toDouble(), frameD.toDouble(), frameC.toDouble()).endVertex()
                    worldRenderer.pos(-frameRadius.toDouble(), frameA.toDouble(), frameC.toDouble()).endVertex()
                    tess.draw()

                    GlStateManager.popMatrix()
                }

                // Draw the activation lights 
                if (tileEntity.lightEnabled()) {
                    val greyLevel: Float = 125f / 255f
                    when (tileEntity.lightColor()) {
                        1 -> GlStateManager.color(1.0f, 115f / 255f, 115f / 255f, 1.0f)
                        else -> GlStateManager.color(greyLevel, 1.0f, greyLevel, 1.0f)
                    }

                    val frameY: Float = 0.9376f
                    frameC = 0.7f
                    frameD = 0.58f
                    frameB = frameC + 0.02f
                    frameA = -frameD
                    worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
                    worldRenderer.pos(frameA.toDouble(), frameY.toDouble(), frameB.toDouble()).endVertex()
                    worldRenderer.pos(frameD.toDouble(), frameY.toDouble(), frameB.toDouble()).endVertex()
                    worldRenderer.pos(frameD.toDouble(), frameY.toDouble(), frameC.toDouble()).endVertex()
                    worldRenderer.pos(frameA.toDouble(), frameY.toDouble(), frameC.toDouble()).endVertex()
                    tess.draw()
                    GlStateManager.rotate(90f, 0f, 1f, 0f)
                    worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
                    worldRenderer.pos(frameA.toDouble(), frameY.toDouble(), frameB.toDouble()).endVertex()
                    worldRenderer.pos(frameD.toDouble(), frameY.toDouble(), frameB.toDouble()).endVertex()
                    worldRenderer.pos(frameD.toDouble(), frameY.toDouble(), frameC.toDouble()).endVertex()
                    worldRenderer.pos(frameA.toDouble(), frameY.toDouble(), frameC.toDouble()).endVertex()
                    tess.draw()
                    GlStateManager.rotate(90f, 0f, 1f, 0f)
                    worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
                    worldRenderer.pos(frameA.toDouble(), frameY.toDouble(), frameB.toDouble()).endVertex()
                    worldRenderer.pos(frameD.toDouble(), frameY.toDouble(), frameB.toDouble()).endVertex()
                    worldRenderer.pos(frameD.toDouble(), frameY.toDouble(), frameC.toDouble()).endVertex()
                    worldRenderer.pos(frameA.toDouble(), frameY.toDouble(), frameC.toDouble()).endVertex()
                    tess.draw()
                    GlStateManager.rotate(90f, 0f, 1f, 0f)
                    worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
                    worldRenderer.pos(frameA.toDouble(), frameY.toDouble(), frameB.toDouble()).endVertex()
                    worldRenderer.pos(frameD.toDouble(), frameY.toDouble(), frameB.toDouble()).endVertex()
                    worldRenderer.pos(frameD.toDouble(), frameY.toDouble(), frameC.toDouble()).endVertex()
                    worldRenderer.pos(frameA.toDouble(), frameY.toDouble(), frameC.toDouble()).endVertex()
                    tess.draw()
                }

                // Restore the lighting state
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
                //? need to undo GlStateManager.glBlendFunc()?
                GlStateManager.enableLighting()
                GlStateManager.enableTexture2D()
                //                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightMapSaveX, lightMapSaveY);
            }

            GlStateManager.enableRescaleNormal()
            RenderHelper.enableStandardItemLighting()
            GlStateManager.popMatrix()
        }
    }

    private fun tenLSB(x: Int): Int {
        if (x < 0) return x % 1024 + 1024

        return x % 1024
    }

    companion object {
        @JvmField
        val platformTexture: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/model/platform_moving.png")

        @JvmField
        val lightTexture: ResourceLocation = ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/misc/light.png")
        private val lastYMap: MutableMap<Int,Float> = HashMap<Int,Float>()
        private var lastPartialTicks = -1f
    }
}
