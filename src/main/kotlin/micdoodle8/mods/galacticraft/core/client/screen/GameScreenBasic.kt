package micdoodle8.mods.galacticraft.core.client.screen

import micdoodle8.mods.galacticraft.api.client.IGameScreen
import micdoodle8.mods.galacticraft.api.client.IScreenManager
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.client.render.RenderPlanet
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.texture.TextureManager
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraftforge.fml.client.FMLClientHandler
import org.lwjgl.opengl.GL11
import kotlin.math.min

open class GameScreenBasic constructor() : IGameScreen {
    private var renderEngine: TextureManager? = null

    private var frameA = 0f
    private var frameBx = 0f
    private var frameBy = 0f
    private var textureAx = 0.0
    private var textureAy = 0.0
    private var textureBx = 1.0
    private var textureBy = 1.0

    init {
        //This can be called from either server or client, so don't include
        //client-side only code on the server.
        if (GCCoreUtil.getEffectiveSide().isClient()) {
            renderEngine = FMLClientHandler.instance().getClient().renderEngine
        }
    }

    override fun setFrameSize(frameSize: Float) {
        this.frameA = frameSize
    }

    override fun render(type: Int, ticks: Float, scaleX: Float, scaleY: Float, scr: IScreenManager?) {
        frameBx = scaleX - frameA
        frameBy = scaleY - frameA

        if (scaleX == scaleY) {
            textureAx = 0.0
            textureAy = 0.0
            textureBx = 1.0
            textureBy = 1.0
        } else if (scaleX < scaleY) {
            textureAx = (1.0 - (scaleX / scaleY)) / 2.0
            textureAy = 0.0
            textureBx = 1.0 - textureAx
            textureBy = 1.0
        } else if (scaleY < scaleX) {
            textureAx = 0.0
            textureAy = (1.0 - (scaleY / scaleX)) / 2.0
            textureBx = 1.0
            textureBy = 1.0 - textureAy
        }

        when (type) {
            0 -> drawBlackBackground(0.09f)
            1 -> if (scr is DrawGameScreen && scr.mapDone) {
                GlStateManager.bindTexture((DrawGameScreen.Companion.reusableMap)!!.getGlTextureId())
                draw2DTexture()
            } else if (ClientProxyCore.overworldTexturesValid) {
                GlStateManager.pushMatrix()
                val centreX: Float = scaleX / 2
                val centreY: Float = scaleY / 2
                GlStateManager.translate(centreX, centreY, 0f)
                RenderPlanet.renderPlanet(
                    (ClientProxyCore.overworldTextureWide)!!.getGlTextureId(),
                    min(scaleX, scaleY) - 0.2f,
                    ticks,
                    45f
                )
                GlStateManager.popMatrix()
            } else {
                this.renderEngine!!.bindTexture(
                    ResourceLocation(
                        Constants.ASSET_PREFIX,
                        "textures/gui/celestialbodies/earth.png"
                    )
                )
                if (!ClientProxyCore.overworldTextureRequestSent) {
                    (GalacticraftCore.packetPipeline)!!.sendToServer(
                        PacketSimple(
                            PacketSimple.EnumSimplePacket.S_REQUEST_OVERWORLD_IMAGE,
                            GCCoreUtil.getDimensionID(FMLClientHandler.instance().getClient().world),
                            arrayOf<Any?>()
                        )
                    )
                    ClientProxyCore.overworldTextureRequestSent = true
                }
                ////                 Overworld texture is 48x48 in a 64x64 .png file
//                this.textureBx -= 0.25D;
//                this.textureBy -= 0.25D;
                draw2DTexture()
                //                this.textureBx += 0.25D;
//                this.textureBy += 0.25D;
            }
        }
    }

    private fun draw2DTexture() {
        val tess: Tessellator = Tessellator.getInstance()
        val worldRenderer: BufferBuilder = tess.getBuffer()
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)

        worldRenderer.pos(frameA.toDouble(), frameBy.toDouble(), 0.0).tex(textureAx, textureBy).endVertex()
        worldRenderer.pos(frameBx.toDouble(), frameBy.toDouble(), 0.0).tex(textureBx, textureBy).endVertex()
        worldRenderer.pos(frameBx.toDouble(), frameA.toDouble(), 0.0).tex(textureBx, textureAy).endVertex()
        worldRenderer.pos(frameA.toDouble(), frameA.toDouble(), 0.0).tex(textureAx, textureAy).endVertex()
        tess.draw()
    }

    private fun drawBlackBackground(greyLevel: Float) {
        GlStateManager.disableLighting()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        GlStateManager.disableTexture2D()
        val tess: Tessellator = Tessellator.getInstance()
        val worldRenderer: BufferBuilder = tess.getBuffer()
        GlStateManager.color(greyLevel, greyLevel, greyLevel, 1.0f)
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)

        worldRenderer.pos(frameA.toDouble(), frameBy.toDouble(), 0.005).endVertex()
        worldRenderer.pos(frameBx.toDouble(), frameBy.toDouble(), 0.005).endVertex()
        worldRenderer.pos(frameBx.toDouble(), frameA.toDouble(), 0.005).endVertex()
        worldRenderer.pos(frameA.toDouble(), frameA.toDouble(), 0.005).endVertex()
        tess.draw()

        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        GlStateManager.enableTexture2D()
        GlStateManager.enableLighting()
    }
}
