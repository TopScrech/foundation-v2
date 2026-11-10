package micdoodle8.mods.galacticraft.core.client.render

import micdoodle8.mods.galacticraft.core.Constants
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.texture.TextureManager
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import net.minecraftforge.fml.client.FMLClientHandler
import org.lwjgl.opengl.GL11

object RenderPlanet {
    private val renderEngine: TextureManager = FMLClientHandler.instance().getClient().renderEngine

    private val textureEuropa = ResourceLocation(Constants.ASSET_PREFIX, "textures/misc/planets/europa.png")
    private val textureGanymede = ResourceLocation(Constants.ASSET_PREFIX, "textures/misc/planets/ganymede.png")
    private val textureIo = ResourceLocation(Constants.ASSET_PREFIX, "textures/misc/planets/io.png")
    private val textureSaturn = ResourceLocation(Constants.ASSET_PREFIX, "textures/misc/planets/saturn.png")
    private val textureJupiterInner =
        ResourceLocation(Constants.ASSET_PREFIX, "textures/misc/planets/jupiter_inner.png")
    private val textureJupiterUpper =
        ResourceLocation(Constants.ASSET_PREFIX, "textures/misc/planets/jupiter_upper.png")

    @JvmStatic
    fun renderPlanet(textureId: Int, scale: Float, ticks: Float, relSize: Float) {
        var ticks: Float = ticks
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId)
        val size: Float = relSize / 70 * scale
        ticks = (System.nanoTime().toFloat()) / 50000000f
        drawTexturedRectUV(-size / 2, -size / 2, size, size, ticks)
    }

    @JvmStatic
    fun renderPlanet(texture: ResourceLocation, scale: Float, ticks: Float, relSize: Float) {
        var ticks: Float = ticks
        renderEngine.bindTexture(texture)
        val size: Float = relSize / 70 * scale
        ticks = (System.nanoTime().toFloat()) / 50000000f
        drawTexturedRectUV(-size / 2, -size / 2, size, size, ticks)
    }

    @JvmStatic
    fun renderID(id: Int, scale: Float, ticks: Float) {
        val texture: ResourceLocation
        when (id) {
            0 -> texture = textureEuropa
            1 -> texture = textureGanymede
            2 -> texture = textureIo
            3 -> texture = textureJupiterInner
            4 -> texture = textureSaturn
            else -> texture = textureGanymede
        }
        if (id == 3)  //Jupiter
        {
            val relSize: Float = 48f
            var size: Float = relSize / 70 * scale
            renderEngine.bindTexture(texture)
            drawTexturedRectUV(-size / 2, -size / 2, size, size, ticks)
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
            GL11.glTranslatef(0f, 0f, -0.001f)
            renderEngine.bindTexture(textureJupiterUpper)
            size *= 1.001f
            drawTexturedRectUV(-size / 2, -size / 2, size, size, ticks * 0.85f)
        } else {
            renderPlanet(texture, scale, ticks, 8f)
        }
    }

    @JvmStatic
    fun drawTexturedRectUV(x: Float, y: Float, width: Float, height: Float, ticks: Float) {
        for (ysect: Int in 0..5) {
//    		drawTexturedRectUVSixth(x, y, width, height, (ticks / 600F) % 1F, ysect / 6F);
            // - 80F * MathHelper.sin(angle)
            val factor: Float = 1f + MathHelper.cos((7.5f + 10f * ysect) / 62f)
            drawTexturedRectUVSixth(
                x,
                y,
                width,
                height,
                (ticks / 1100f) % 1f - (1f - factor) * 0.15f,
                ysect / 6f,
                0.16f * factor
            )
        }
    }

    @JvmStatic
    fun drawTexturedRectUVSixth(x: Float, y: Float, width: Float, height: Float, prog: Float, y0: Float, span: Float) {
        var prog: Float = prog
        var y0: Float = y0
        y0 /= 2f
        if (prog < 0f) {
            prog += 1.0f
        }
        prog = 1.0f - prog
        val y1: Float = y0 + 1 / 12f
        val y2: Float = 1f - y1
        val y3: Float = 1f - y0
        val yaa: Float = y + height * y0
        val yab: Float = y + height * y1
        val yba: Float = y + height * y2
        val ybb: Float = y + height * y3
        val tessellator: Tessellator = Tessellator.getInstance()
        val worldRenderer: BufferBuilder = tessellator.getBuffer()
        if (prog <= 1f - span) {
            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
            worldRenderer.pos(x.toDouble(), yab.toDouble(), 0.0).tex(prog.toDouble(), y1.toDouble()).endVertex()
            worldRenderer.pos((x + width).toDouble(), yab.toDouble(), 0.0).tex((prog + span).toDouble(), y1.toDouble())
                .endVertex()
            worldRenderer.pos((x + width).toDouble(), yaa.toDouble(), 0.0).tex((prog + span).toDouble(), y0.toDouble())
                .endVertex()
            worldRenderer.pos(x.toDouble(), yaa.toDouble(), 0.0).tex(prog.toDouble(), y0.toDouble()).endVertex()
            tessellator.draw()
            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
            worldRenderer.pos(x.toDouble(), ybb.toDouble(), 0.0).tex(prog.toDouble(), y3.toDouble()).endVertex()
            worldRenderer.pos((x + width).toDouble(), ybb.toDouble(), 0.0).tex((prog + span).toDouble(), y3.toDouble())
                .endVertex()
            worldRenderer.pos((x + width).toDouble(), yba.toDouble(), 0.0).tex((prog + span).toDouble(), y2.toDouble())
                .endVertex()
            worldRenderer.pos(x.toDouble(), yba.toDouble(), 0.0).tex(prog.toDouble(), y2.toDouble()).endVertex()
            tessellator.draw()
        } else {
            val xp: Double = (x + width * (1f - prog) / span).toDouble()
            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
            worldRenderer.pos(x.toDouble(), yab.toDouble(), 0.0).tex(prog.toDouble(), y1.toDouble()).endVertex()
            worldRenderer.pos(xp, yab.toDouble(), 0.0).tex(1.0, y1.toDouble()).endVertex()
            worldRenderer.pos(xp, yaa.toDouble(), 0.0).tex(1.0, y0.toDouble()).endVertex()
            worldRenderer.pos(x.toDouble(), yaa.toDouble(), 0.0).tex(prog.toDouble(), y0.toDouble()).endVertex()
            tessellator.draw()
            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
            worldRenderer.pos(x.toDouble(), ybb.toDouble(), 0.0).tex(prog.toDouble(), y3.toDouble()).endVertex()
            worldRenderer.pos(xp, ybb.toDouble(), 0.0).tex(1.0, y3.toDouble()).endVertex()
            worldRenderer.pos(xp, yba.toDouble(), 0.0).tex(1.0, y2.toDouble()).endVertex()
            worldRenderer.pos(x.toDouble(), yba.toDouble(), 0.0).tex(prog.toDouble(), y2.toDouble()).endVertex()
            tessellator.draw()
            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
            worldRenderer.pos(xp, yab.toDouble(), 0.0).tex(0.0, y1.toDouble()).endVertex()
            worldRenderer.pos((x + width).toDouble(), yab.toDouble(), 0.0)
                .tex((prog - 1f + span).toDouble(), y1.toDouble()).endVertex()
            worldRenderer.pos((x + width).toDouble(), yaa.toDouble(), 0.0)
                .tex((prog - 1f + span).toDouble(), y0.toDouble()).endVertex()
            worldRenderer.pos(xp, yaa.toDouble(), 0.0).tex(0.0, y0.toDouble()).endVertex()
            tessellator.draw()
            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
            worldRenderer.pos(xp, ybb.toDouble(), 0.0).tex(0.0, y3.toDouble()).endVertex()
            worldRenderer.pos((x + width).toDouble(), ybb.toDouble(), 0.0)
                .tex((prog - 1f + span).toDouble(), y3.toDouble()).endVertex()
            worldRenderer.pos((x + width).toDouble(), yba.toDouble(), 0.0)
                .tex((prog - 1f + span).toDouble(), y2.toDouble()).endVertex()
            worldRenderer.pos(xp, yba.toDouble(), 0.0).tex(0.0, y2.toDouble()).endVertex()
            tessellator.draw()
        }
    }
}
