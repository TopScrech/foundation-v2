package micdoodle8.mods.galacticraft.core.client.gui.overlay

import micdoodle8.mods.galacticraft.core.entities.EntityTier1Rocket
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.util.ClientUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.entity.Render
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.Entity
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11
import kotlin.math.floor

@SideOnly(Side.CLIENT)
object OverlayRocket : Overlay() {
    private val minecraft: Minecraft = FMLClientHandler.instance().getClient()

    /**
     * Render the GUI when player is in inventory
     */
    @JvmStatic
    fun renderSpaceshipOverlay(guiTexture: ResourceLocation?) {
        if (guiTexture == null) {
            return
        }

        val scaledresolution: ScaledResolution =
            ClientUtil.getScaledRes(minecraft, minecraft.displayWidth, minecraft.displayHeight)
        scaledresolution.getScaledWidth()
        val height: Int = scaledresolution.getScaledHeight()
        minecraft.entityRenderer.setupOverlayRendering()
        GlStateManager.depthMask(true)
        GlStateManager.enableTexture2D()
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(guiTexture)

        var var1: Float = 0f
        var var2: Float = (height / 2 - 170 / 2).toFloat()
        var var3: Float = 0.0f
        var var3b: Float = 0.0f
        var var4: Float = 0.0f
        var var5: Float = 1.0f
        var var6: Float = 1.0f
        var var7: Float = 1.0f
        var var8: Float = 1.0f
        val sizeScale: Float = 0.65f

        val tess: Tessellator = Tessellator.getInstance()
        val worldRenderer: BufferBuilder = tess.getBuffer()
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        worldRenderer.pos((var1 + 0).toDouble(), (var2 + 242.0f * sizeScale).toDouble(), 0.0)
            .tex(((var3 + 0) * var7).toDouble(), ((var4 + var6) * var8).toDouble()).endVertex()
        worldRenderer.pos((var1 + 20.0f * sizeScale).toDouble(), (var2 + 242.0f * sizeScale).toDouble(), 0.0)
            .tex(((var3 + var5) * var7).toDouble(), ((var4 + var6) * var8).toDouble()).endVertex()
        worldRenderer.pos((var1 + 20.0f * sizeScale).toDouble(), (var2 + 0).toDouble(), 0.0)
            .tex(((var3 + var5) * var7).toDouble(), ((var4 + 0) * var8).toDouble()).endVertex()
        worldRenderer.pos((var1 + 0).toDouble(), (var2 + 0).toDouble(), 0.0)
            .tex(((var3 + 0) * var7).toDouble(), ((var4 + 0) * var8).toDouble()).endVertex()
        tess.draw()

        GlStateManager.color(1.0f, 1.0f, 1.0f)

        val rocket: Entity? = minecraft.player.getRidingEntity()
        var headOffset: Float = 0f
        if (rocket is EntityTier1Rocket) {
            headOffset = 5f
        }
        val spaceshipRender: Render<*> =
            minecraft.getRenderManager().entityRenderMap.get(rocket!!.javaClass) as Render<*>

        val y1: Int =
            height / 2 + 60 - floor((Overlay.Companion.getPlayerPositionY(minecraft.player) / 10.5f).toDouble()).toInt()
        var1 = 2.5f
        var2 = y1.toFloat()
        var3 = 8f
        var3b = 40f
        var4 = 8f
        var5 = 8f
        var6 = 8f
        var7 = 1.0f / 64.0f
        var8 = 1.0f / 64.0f

        GlStateManager.pushMatrix()
        val i: Int = rocket.getBrightnessForRender()
        val j: Int = i % 65536
        val k: Int = i / 65536
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j / 1.0f, k / 1.0f)
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        GlStateManager.enableColorMaterial()
        GlStateManager.translate(var1 + 4, var2 + 6, 50f)
        GlStateManager.scale(5f, 5f, 5f)
        GlStateManager.rotate(180f, 1f, 0f, 0f)
        GlStateManager.rotate(90f, 0f, 1f, 0f)

        try {
            (spaceshipRender as Render<net.minecraft.entity.Entity>).doRender(
                rocket.javaClass.getConstructor(World::class.java).newInstance(minecraft.player.world),
                0.0,
                0.0,
                0.0,
                0f,
                0f
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

        GlStateManager.popMatrix()

        minecraft.renderEngine.bindTexture(ClientProxyCore.playerHead)

        GlStateManager.disableLighting()
        GlStateManager.enableBlend()
        GlStateManager.blendFunc(770, 771)
        GlStateManager.translate(0f, -12f + headOffset, 60f)

        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        worldRenderer.pos((var1 + 0).toDouble(), (var2 + var6).toDouble(), 0.0)
            .tex(((var3 + 0) * var7).toDouble(), ((var4 + var6) * var8).toDouble()).endVertex()
        worldRenderer.pos((var1 + var5).toDouble(), (var2 + var6).toDouble(), 0.0)
            .tex(((var3 + var5) * var7).toDouble(), ((var4 + var6) * var8).toDouble()).endVertex()
        worldRenderer.pos((var1 + var5).toDouble(), (var2 + 0).toDouble(), 0.0)
            .tex(((var3 + var5) * var7).toDouble(), ((var4 + 0) * var8).toDouble()).endVertex()
        worldRenderer.pos((var1 + 0).toDouble(), (var2 + 0).toDouble(), 0.0)
            .tex(((var3 + 0) * var7).toDouble(), ((var4 + 0) * var8).toDouble()).endVertex()
        tess.draw()

        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        worldRenderer.pos((var1 + 0).toDouble(), (var2 + var6).toDouble(), 0.0)
            .tex(((var3b + 0) * var7).toDouble(), ((var4 + var6) * var8).toDouble()).endVertex()
        worldRenderer.pos((var1 + var5).toDouble(), (var2 + var6).toDouble(), 0.0)
            .tex(((var3b + var5) * var7).toDouble(), ((var4 + var6) * var8).toDouble()).endVertex()
        worldRenderer.pos((var1 + var5).toDouble(), (var2 + 0).toDouble(), 0.0)
            .tex(((var3b + var5) * var7).toDouble(), ((var4 + 0) * var8).toDouble()).endVertex()
        worldRenderer.pos((var1 + 0).toDouble(), (var2 + 0).toDouble(), 0.0)
            .tex(((var3b + 0) * var7).toDouble(), ((var4 + 0) * var8).toDouble()).endVertex()
        tess.draw()

        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        GlStateManager.enableLighting()
        GlStateManager.disableBlend()
    }
}
