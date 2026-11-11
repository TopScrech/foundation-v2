package micdoodle8.mods.galacticraft.core.client.render.tile

import com.google.common.collect.ImmutableList
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.entities.IBubbleProvider
import micdoodle8.mods.galacticraft.core.util.ClientUtil
import micdoodle8.mods.galacticraft.core.util.ColorUtil
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.resources.Identifier as ResourceLocation
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL12

@Deprecated("")
open class TileEntityBubbleProviderRenderer<E> constructor(
    private val colorRed: Float,
    private val colorGreen: Float,
    private val colorBlue: Float
) : TileEntitySpecialRenderer<E>()
        where E : TileEntity?, E : IBubbleProvider? {
    override fun render(
        provider: E?,
        x: Double,
        y: Double,
        z: Double,
        partialTicks: Float,
        destroyStage: Int,
        alpha: Float
    ) {
        if (!provider!!.getBubbleVisible()) {
            return
        }

        updateModels()

        GL11.glPushMatrix()
        GL11.glEnable(GL12.GL_RESCALE_NORMAL)
        GL11.glTranslatef(x.toFloat() + 0.5f, y.toFloat() + 1.0f, z.toFloat() + 0.5f)

        this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)

        GL11.glEnable(GL11.GL_BLEND)
        GL11.glDisable(GL11.GL_LIGHTING)
        GL11.glDisable(GL11.GL_CULL_FACE)

        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glColor4f(this.colorRed / 2.0f, this.colorGreen / 2.0f, this.colorBlue / 2.0f, 1.0f)
        GL11.glMatrixMode(GL11.GL_TEXTURE)
        GL11.glLoadIdentity()
        GL11.glMatrixMode(GL11.GL_MODELVIEW)
        GL11.glDepthMask(false)
        val lightMapSaveX: Float = OpenGlHelper.lastBrightnessX
        val lightMapSaveY: Float = OpenGlHelper.lastBrightnessY
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f)
        GL11.glScalef(provider.getBubbleSize(), provider.getBubbleSize(), provider.getBubbleSize())

        val color: Int = ColorUtil.to32BitColor(
            30,
            (this.colorBlue / 2.0f * 255).toInt(),
            (this.colorGreen / 2.0f * 255).toInt(),
            (this.colorRed / 2.0f * 255).toInt()
        )
        ClientUtil.drawBakedModelColored((sphere)!!, color)

        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        GL11.glMatrixMode(GL11.GL_TEXTURE)
        GL11.glDepthMask(true)
        GL11.glLoadIdentity()
        GL11.glMatrixMode(GL11.GL_MODELVIEW)
        GL11.glEnable(GL11.GL_LIGHTING)
        GL11.glDisable(GL11.GL_BLEND)
        GL11.glDepthFunc(GL11.GL_LEQUAL)
        GL11.glEnable(GL11.GL_CULL_FACE)

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightMapSaveX, lightMapSaveY)

        GL11.glDisable(GL12.GL_RESCALE_NORMAL)
        GL11.glPopMatrix()
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
    }

    companion object {
        private var sphere: IBakedModel? = null

        @JvmStatic
        private fun updateModels() {
            if (sphere == null) {
                try {
                    sphere = ClientUtil.modelFromOBJ(
                        ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "sphere.obj"),
                        ImmutableList.of<String?>("Sphere")
                    )
                } catch (e: Exception) {
                    throw RuntimeException(e)
                }
            }
        }
    }
}
