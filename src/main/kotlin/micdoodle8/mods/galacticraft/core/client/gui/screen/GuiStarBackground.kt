package micdoodle8.mods.galacticraft.core.client.gui.screen

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.util.ClientUtil
import net.minecraft.client.gui.screens.Screen as GuiScreen
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.util.Mth as MathHelper
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.GL11
import org.lwjgl.util.glu.GLU

abstract class GuiStarBackground : GuiScreen() {
    fun drawBlackBackground() {
        val var5: ScaledResolution = ClientUtil.getScaledRes(this.mc, this.mc.displayWidth, this.mc.displayHeight)
        val var6: Int = var5.getScaledWidth()
        val var7: Int = var5.getScaledHeight()
        GL11.glDisable(GL11.GL_DEPTH_TEST)
        GL11.glDepthMask(false)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        GL11.glDisable(GL11.GL_ALPHA_TEST)
        this.mc.getTextureManager().bindTexture(blackTexture)
        val tess: Tessellator = Tessellator.getInstance()
        val worldRenderer: BufferBuilder = tess.getBuffer()
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        worldRenderer.pos(0.0, var7.toDouble(), -90.0).tex(0.0, 1.0).endVertex()
        worldRenderer.pos(var6.toDouble(), var7.toDouble(), -90.0).tex(1.0, 1.0).endVertex()
        worldRenderer.pos(var6.toDouble(), 0.0, -90.0).tex(1.0, 0.0).endVertex()
        worldRenderer.pos(0.0, 0.0, -90.0).tex(0.0, 0.0).endVertex()
        tess.draw()
        GL11.glDepthMask(true)
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glEnable(GL11.GL_ALPHA_TEST)
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
    }

    private fun drawPanorama2(par1: Float) {
        val tess: Tessellator = Tessellator.getInstance()
        val worldRenderer: BufferBuilder = tess.getBuffer()
        GL11.glMatrixMode(GL11.GL_PROJECTION)
        GL11.glPushMatrix()
        GL11.glLoadIdentity()
        GLU.gluPerspective(120.0f, 1.0f, 0.05f, 10.0f)
        GL11.glMatrixMode(GL11.GL_MODELVIEW)
        GL11.glPushMatrix()
        GL11.glLoadIdentity()
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        GL11.glRotatef(180.0f, 1.0f, 0.0f, 0.0f)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glDisable(GL11.GL_ALPHA_TEST)
        GL11.glDisable(GL11.GL_CULL_FACE)
        GL11.glDepthMask(false)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE)
        val var5: Byte = 1

        for (var6: Int in 0..<var5 * var5) {
            GL11.glPushMatrix()
            val var7: Float = ((var6 % var5).toFloat() / var5.toFloat() - 0.5f) / 128.0f
            val var8: Float = ((var6 / var5).toFloat() / var5.toFloat() - 0.5f) / 128.0f
            val var9: Float = 0.0f

            val mY: Float
            val mX: Float

            if (Mouse.getY() < this.height) {
                mY = (-this.height + Mouse.getY()) / 100f
            } else {
                mY = (-this.height + Mouse.getY()) / 100f
            }

            mX = (this.width - Mouse.getX()) / 100f

            this.doCustomTranslation(0, var7, var8, var9, mX, mY)

            for (var10: Int in 0..8) {
                GL11.glPushMatrix()

                if (var10 == 1) {
                    GL11.glTranslatef(1.96f, 0.0f, 0.0f)
                }

                if (var10 == 2) {
                    GL11.glTranslatef(-1.96f, 0.0f, 0.0f)
                }

                if (var10 == 3) {
                    GL11.glTranslatef(0.0f, 1.96f, 0.0f)
                }

                if (var10 == 4) {
                    GL11.glTranslatef(0.0f, -1.96f, 0.0f)
                }

                if (var10 == 5) {
                    GL11.glTranslatef(-1.96f, -1.96f, 0.0f)
                }

                if (var10 == 6) {
                    GL11.glTranslatef(-1.96f, 1.96f, 0.0f)
                }

                if (var10 == 7) {
                    GL11.glTranslatef(1.96f, -1.96f, 0.0f)
                }

                if (var10 == 8) {
                    GL11.glTranslatef(1.96f, 1.96f, 0.0f)
                }

                this.mc.getTextureManager().bindTexture(backgroundTexture)
                worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR)
                worldRenderer.pos(-1.0, -1.0, 1.0).tex((0.0f + 1).toDouble(), (0.0f + 1).toDouble())
                    .color(1.0f, 1.0f, 1.0f, 1.0f / (var6 + 1)).endVertex()
                worldRenderer.pos(1.0, -1.0, 1.0).tex((1.0f - 1).toDouble(), (0.0f + 1).toDouble())
                    .color(1.0f, 1.0f, 1.0f, 1.0f / (var6 + 1)).endVertex()
                worldRenderer.pos(1.0, 1.0, 1.0).tex((1.0f - 1).toDouble(), (1.0f - 1).toDouble())
                    .color(1.0f, 1.0f, 1.0f, 1.0f / (var6 + 1)).endVertex()
                worldRenderer.pos(-1.0, 1.0, 1.0).tex((0.0f + 1).toDouble(), (1.0f - 1).toDouble())
                    .color(1.0f, 1.0f, 1.0f, 1.0f / (var6 + 1)).endVertex()
                tess.draw()
                GL11.glPopMatrix()
            }

            GL11.glPopMatrix()
        }

        worldRenderer.setTranslation(0.0, 0.0, 0.0)
        GL11.glMatrixMode(GL11.GL_PROJECTION)
        GL11.glPopMatrix()
        GL11.glMatrixMode(GL11.GL_MODELVIEW)
        GL11.glPopMatrix()
        GL11.glDepthMask(true)
        GL11.glEnable(GL11.GL_CULL_FACE)
        GL11.glEnable(GL11.GL_ALPHA_TEST)
        GL11.glEnable(GL11.GL_DEPTH_TEST)
    }

    private fun drawPanorama(par1: Float) {
        val tess: Tessellator = Tessellator.getInstance()
        val worldRenderer: BufferBuilder = tess.getBuffer()
        GL11.glMatrixMode(GL11.GL_PROJECTION)
        GL11.glPushMatrix()
        GL11.glLoadIdentity()
        GLU.gluPerspective(120.0f, 1.0f, 0.05f, 10.0f)
        GL11.glMatrixMode(GL11.GL_MODELVIEW)
        GL11.glPushMatrix()
        GL11.glLoadIdentity()
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        GL11.glRotatef(180.0f, 1.0f, 0.0f, 0.0f)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glDisable(GL11.GL_ALPHA_TEST)
        GL11.glDisable(GL11.GL_CULL_FACE)
        GL11.glDepthMask(false)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE)
        val var5: Byte = 1

        for (var6: Int in 0..<var5 * var5) {
            GL11.glPushMatrix()
            val var7: Float = ((var6 % var5).toFloat() / var5.toFloat() - 0.5f) / 64.0f
            val var8: Float = ((var6 / var5).toFloat() / var5.toFloat() - 0.5f) / 64.0f
            val var9: Float = 0.0f

            val mY: Float
            val mX: Float

            if (Mouse.getY() < this.height) {
                mY = (-this.height + Mouse.getY()) / 100f
            } else {
                mY = (-this.height + Mouse.getY()) / 100f
            }

            mX = (this.width - Mouse.getX()) / 100f

            this.doCustomTranslation(1, var7, var8, var9, mX, mY)

            GL11.glRotatef(MathHelper.sin(par1 / 1000.0f) * 25.0f + 20.0f, 1.0f, 0.0f, 0.0f)
            GL11.glRotatef(-par1 * 0.005f, 0.0f, 1.0f, 0.0f)
            GL11.glRotatef(41f, 0f, 0f, 1f)

            for (var10: Int in 0..5) {
                GL11.glPushMatrix()

                if (var10 == 1) {
                    GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f)
                }

                if (var10 == 2) {
                    GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f)
                }

                if (var10 == 3) {
                    GL11.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f)
                }

                if (var10 == 4) {
                    GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f)
                }

                if (var10 == 5) {
                    GL11.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f)
                }

                this.mc.getTextureManager().bindTexture(backgroundTexture)
                worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR)
                worldRenderer.pos(-1.0, -1.0, 1.0).tex((0.0f + 1).toDouble(), (0.0f + 1).toDouble())
                    .color(1.0f, 1.0f, 1.0f, 1.0f / (var6 + 1)).endVertex()
                worldRenderer.pos(1.0, -1.0, 1.0).tex((1.0f - 1).toDouble(), (0.0f + 1).toDouble())
                    .color(1.0f, 1.0f, 1.0f, 1.0f / (var6 + 1)).endVertex()
                worldRenderer.pos(1.0, 1.0, 1.0).tex((1.0f - 1).toDouble(), (1.0f - 1).toDouble())
                    .color(1.0f, 1.0f, 1.0f, 1.0f / (var6 + 1)).endVertex()
                worldRenderer.pos(-1.0, 1.0, 1.0).tex((0.0f + 1).toDouble(), (1.0f - 1).toDouble())
                    .color(1.0f, 1.0f, 1.0f, 1.0f / (var6 + 1)).endVertex()
                tess.draw()
                GL11.glPopMatrix()
            }

            GL11.glPopMatrix()
        }

        worldRenderer.setTranslation(0.0, 0.0, 0.0)
        GL11.glMatrixMode(GL11.GL_PROJECTION)
        GL11.glPopMatrix()
        GL11.glMatrixMode(GL11.GL_MODELVIEW)
        GL11.glPopMatrix()
        GL11.glDepthMask(true)
        GL11.glEnable(GL11.GL_CULL_FACE)
        GL11.glEnable(GL11.GL_ALPHA_TEST)
        GL11.glEnable(GL11.GL_DEPTH_TEST)
    }

    private fun rotateAndBlurSkybox() {
        this.mc.getTextureManager().bindTexture(backgroundTexture)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glColorMask(true, true, true, false)
        GL11.glPushMatrix()
        GL11.glPopMatrix()
        GL11.glColorMask(true, true, true, true)
    }

    fun renderSkybox() {
        GL11.glViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight)
        GL11.glPushMatrix()
        GL11.glScalef(1.0f, 0.0f, 1.0f)
        this.drawPanorama(1f)
        this.drawPanorama2(1f)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        this.rotateAndBlurSkybox()
        val tess: Tessellator = Tessellator.getInstance()
        val worldRenderer: BufferBuilder = tess.getBuffer()
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR)
        val var5: Float = if (this.width > this.height) 120.0f / this.width else 120.0f / this.height
        val var6: Float = this.height * var5 / 256.0f
        val var7: Float = this.width * var5 / 256.0f
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR)
        val var8: Int = this.width
        val var9: Int = this.height
        worldRenderer.pos(0.0, var9.toDouble(), this.zLevel.toDouble())
            .tex((0.5f - var6).toDouble(), (0.5f + var7).toDouble()).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex()
        worldRenderer.pos(var8.toDouble(), var9.toDouble(), this.zLevel.toDouble())
            .tex((0.5f - var6).toDouble(), (0.5f - var7).toDouble()).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex()
        worldRenderer.pos(var8.toDouble(), 0.0, this.zLevel.toDouble())
            .tex((0.5f + var6).toDouble(), (0.5f - var7).toDouble()).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex()
        worldRenderer.pos(0.0, 0.0, this.zLevel.toDouble()).tex((0.5f + var6).toDouble(), (0.5f + var7).toDouble())
            .color(1.0f, 1.0f, 1.0f, 1.0f).endVertex()
        tess.draw()
        GL11.glPopMatrix()
    }

    abstract fun doCustomTranslation(type: Int, coord1: Float, coord2: Float, coord3: Float, mX: Float, mY: Float)

    companion object {
        private val backgroundTexture = ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/gui/stars.png")
        private val blackTexture = ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/gui/black.png")
    }
}
