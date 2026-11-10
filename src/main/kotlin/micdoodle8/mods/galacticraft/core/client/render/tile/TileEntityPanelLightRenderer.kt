package micdoodle8.mods.galacticraft.core.client.render.tile

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.blocks.BlockPanelLighting
import micdoodle8.mods.galacticraft.core.tile.TileEntityPanelLight
import micdoodle8.mods.galacticraft.core.util.ColorUtil
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11

@SideOnly(Side.CLIENT)
open class TileEntityPanelLightRenderer : TileEntitySpecialRenderer<TileEntityPanelLight>() {
    override fun render(
        tileEntity: TileEntityPanelLight,
        d: Double,
        d1: Double,
        d2: Double,
        f: Float,
        par9: Int,
        alpha: Float
    ) {
        var side: Int = tileEntity.meta
        var rot: Int = side shr 3
        side = (side and 7) xor 1
        val type: BlockPanelLighting.PanelType = tileEntity.getType()

        GlStateManager.pushMatrix()
        GlStateManager.translate(d.toFloat() + 0.5f, d1.toFloat() + 0.5f, d2.toFloat() + 0.5f)

        when (side) {
            0 -> {}
            1 -> GlStateManager.rotate(180f, 1f, 0f, 0f)
            2 -> GlStateManager.rotate(90f, 1f, 0f, 0f)
            3 -> GlStateManager.rotate(90f, -1f, 0f, 0f)
            4 -> {
                GlStateManager.rotate(90f, 0f, 0f, -1f)
                rot = (rot + 1) % 4
            }

            5 -> {
                GlStateManager.rotate(90f, 0f, 0f, 1f)
                rot = (rot + 1) % 4
            }
        }

        if (rot > 0) {
            GlStateManager.rotate(90f * rot, 0f, 1f, 0f)
        }

        if (type == BlockPanelLighting.PanelType.SFDIAG) {
            GlStateManager.rotate(45f, 0f, 1f, 0f)
        }

        GlStateManager.translate(-0.5f, -0.5f, -0.5f)
        RenderHelper.disableStandardItemLighting()

        if (tileEntity.getEnabled()) {
            ColorUtil.setGLColor(tileEntity.color)
        } else {
            val greyLevel: Float = 24f / 255f
            GlStateManager.color(greyLevel, greyLevel, greyLevel, 1.0f)
        }


        //Save the lighting state
        val lightMapSaveX: Float = OpenGlHelper.lastBrightnessX
        val lightMapSaveY: Float = OpenGlHelper.lastBrightnessY
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f)
        GlStateManager.disableLighting()

        this.bindTexture(TileEntityArclampRenderer.Companion.lightTexture)
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GlStateManager.disableTexture2D()
        val tess: Tessellator = Tessellator.getInstance()
        val worldRenderer: BufferBuilder = tess.getBuffer()
        val frameY: Float = 1.01f
        var frameA: Float
        var frameB: Float
        var frameC: Float
        when (type) {
            BlockPanelLighting.PanelType.SQUARE -> {
                frameA = 0.15f
                frameB = 0.5f
                frameC = frameA
            }

            BlockPanelLighting.PanelType.SPOTS -> {
                frameA = 0.21f
                frameB = 0.29f
                frameC = frameA
            }

            BlockPanelLighting.PanelType.LINEAR -> {
                frameA = 0.08f
                frameB = 0.5f
                frameC = 0.36f
            }

            BlockPanelLighting.PanelType.SF, BlockPanelLighting.PanelType.SFDIAG -> {
                frameA = 0.1f
                frameB = 0.4f
                frameC = 0.35f
            }

            else -> {
                frameA = 0.15f
                frameB = 0.5f
                frameC = frameA
            }
        }
        if (type != BlockPanelLighting.PanelType.SFDIAG) {
            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
            worldRenderer.pos(frameA.toDouble(), frameY.toDouble(), frameB.toDouble()).endVertex()
            worldRenderer.pos(frameB.toDouble(), frameY.toDouble(), frameB.toDouble()).endVertex()
            worldRenderer.pos(frameB.toDouble(), frameY.toDouble(), frameC.toDouble()).endVertex()
            worldRenderer.pos(frameA.toDouble(), frameY.toDouble(), frameC.toDouble()).endVertex()
            tess.draw()
            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
            worldRenderer.pos((1.0f - frameA).toDouble(), frameY.toDouble(), frameC.toDouble()).endVertex()
            worldRenderer.pos((1.0f - frameB).toDouble(), frameY.toDouble(), frameC.toDouble()).endVertex()
            worldRenderer.pos((1.0f - frameB).toDouble(), frameY.toDouble(), frameB.toDouble()).endVertex()
            worldRenderer.pos((1.0f - frameA).toDouble(), frameY.toDouble(), frameB.toDouble()).endVertex()
            tess.draw()
            frameA = 1.0f - frameA
            frameB = 1.0f - frameB
            frameC = 1.0f - frameC
            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
            worldRenderer.pos(frameA.toDouble(), frameY.toDouble(), frameB.toDouble()).endVertex()
            worldRenderer.pos(frameB.toDouble(), frameY.toDouble(), frameB.toDouble()).endVertex()
            worldRenderer.pos(frameB.toDouble(), frameY.toDouble(), frameC.toDouble()).endVertex()
            worldRenderer.pos(frameA.toDouble(), frameY.toDouble(), frameC.toDouble()).endVertex()
            tess.draw()
            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
            worldRenderer.pos((1.0f - frameA).toDouble(), frameY.toDouble(), frameC.toDouble()).endVertex()
            worldRenderer.pos((1.0f - frameB).toDouble(), frameY.toDouble(), frameC.toDouble()).endVertex()
            worldRenderer.pos((1.0f - frameB).toDouble(), frameY.toDouble(), frameB.toDouble()).endVertex()
            worldRenderer.pos((1.0f - frameA).toDouble(), frameY.toDouble(), frameB.toDouble()).endVertex()
            tess.draw()
        } else {
            frameA += 0.02f
            GlStateManager.translate(0.239f, 0f, -0.345f)
            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
            worldRenderer.pos(frameA.toDouble(), frameY.toDouble(), frameB.toDouble()).endVertex()
            worldRenderer.pos(frameB.toDouble(), frameY.toDouble(), frameB.toDouble()).endVertex()
            worldRenderer.pos(frameB.toDouble(), frameY.toDouble(), frameC.toDouble()).endVertex()
            worldRenderer.pos(frameA.toDouble(), frameY.toDouble(), frameC.toDouble()).endVertex()
            tess.draw()
            frameA += 0.02f
            GlStateManager.translate(0.23f, 0f, 0.233f)
            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
            worldRenderer.pos(frameA.toDouble(), frameY.toDouble(), frameB.toDouble()).endVertex()
            worldRenderer.pos(frameB.toDouble(), frameY.toDouble(), frameB.toDouble()).endVertex()
            worldRenderer.pos(frameB.toDouble(), frameY.toDouble(), frameC.toDouble()).endVertex()
            worldRenderer.pos(frameA.toDouble(), frameY.toDouble(), frameC.toDouble()).endVertex()
            tess.draw()
            GlStateManager.translate(-0.48f, 0f, 0f)
            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
            worldRenderer.pos(frameA.toDouble(), frameY.toDouble(), frameB.toDouble()).endVertex()
            worldRenderer.pos(frameB.toDouble(), frameY.toDouble(), frameB.toDouble()).endVertex()
            worldRenderer.pos(frameB.toDouble(), frameY.toDouble(), frameC.toDouble()).endVertex()
            worldRenderer.pos(frameA.toDouble(), frameY.toDouble(), frameC.toDouble()).endVertex()
            tess.draw()
        }
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        GlStateManager.enableTexture2D()

        //? need to undo GlStateManager.glBlendFunc()?

        //Restore the lighting state
        GlStateManager.enableLighting()
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightMapSaveX, lightMapSaveY)
        RenderHelper.enableStandardItemLighting()
        GlStateManager.popMatrix()
    }

    companion object {
        @JvmField
        val lightTexture: ResourceLocation = ResourceLocation(Constants.ASSET_PREFIX, "textures/misc/light.png")
    }
}
