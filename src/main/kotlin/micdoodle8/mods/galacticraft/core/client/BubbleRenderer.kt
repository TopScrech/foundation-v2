package micdoodle8.mods.galacticraft.core.client

import com.google.common.collect.Lists
import com.google.common.collect.ImmutableList
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.entities.IBubbleProviderColored
import micdoodle8.mods.galacticraft.core.util.ClientUtil
import micdoodle8.mods.galacticraft.core.util.ColorUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import java.lang.Exception
import java.lang.RuntimeException

object BubbleRenderer {
    private var sphere: IBakedModel? = null
    private val bubbleProviders: MutableList<IBubbleProviderColored> = Lists.newArrayList<IBubbleProviderColored>()

    @JvmStatic
    fun clearBubbles() {
        bubbleProviders.clear()
    }

    @JvmStatic
    fun addBubble(tile: IBubbleProviderColored) {
        bubbleProviders.add(tile)
    }

    @JvmStatic
    private fun updateModels() {
        try {
            sphere = ClientUtil.modelFromOBJ(
                ResourceLocation(Constants.ASSET_PREFIX, "sphere.obj"),
                ImmutableList.of<String?>("Sphere")
            )
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    @JvmStatic
    fun renderBubbles(player: EntityPlayer, partialTicks: Float) {
        if (bubbleProviders.isEmpty()) {
            return
        }

        val interpPosX: Double = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks
        val interpPosY: Double = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks
        val interpPosZ: Double = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks

        if (sphere == null) {
            updateModels()
        }

        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)

        GlStateManager.pushMatrix()

        GlStateManager.enableRescaleNormal()
        GlStateManager.enableBlend()
        GlStateManager.disableLighting()
        GlStateManager.disableCull()

        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1f)
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GlStateManager.matrixMode(GL11.GL_TEXTURE)
        GlStateManager.loadIdentity()
        GlStateManager.matrixMode(GL11.GL_MODELVIEW)
        GlStateManager.depthMask(false)
        val lightMapSaveX: Float = OpenGlHelper.lastBrightnessX
        val lightMapSaveY: Float = OpenGlHelper.lastBrightnessY
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f)

        for (provider: IBubbleProviderColored in bubbleProviders) {
            val tile: TileEntity? = provider as TileEntity?
            if (!provider.getBubbleVisible()) {
                continue
            }

            GL11.glPushMatrix()

            val x: Float = (tile!!.getPos().getX() - interpPosX).toFloat()
            val y: Float = (tile.getPos().getY() - interpPosY).toFloat()
            val z: Float = (tile.getPos().getZ() - interpPosZ).toFloat()

            GL11.glTranslatef(x + 0.5f, y + 1.0f, z + 0.5f)
            GL11.glScalef(provider.getBubbleSize(), provider.getBubbleSize(), provider.getBubbleSize())

            val colorVec: Vector3 = (provider.getColor())!!
            val color: Int = ColorUtil.to32BitColor(
                30,
                (colorVec.z * 255).toInt(),
                (colorVec.y * 255).toInt(),
                (colorVec.x * 255).toInt()
            )
            ClientUtil.drawBakedModelColored((sphere)!!, color)

            GL11.glPopMatrix()
        }

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightMapSaveX, lightMapSaveY)
        GlStateManager.popMatrix()
    }
}
