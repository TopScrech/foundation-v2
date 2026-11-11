package micdoodle8.mods.galacticraft.core.client.render.tile

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.tile.TileEntityArclamp
import micdoodle8.mods.galacticraft.core.util.ClientUtil
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import org.lwjgl.opengl.GL11

@OnlyIn(Dist.CLIENT)
open class TileEntityArclampRenderer : TileEntitySpecialRenderer<TileEntityArclamp>() {
    override fun render(
        tileEntity: TileEntityArclamp,
        d: Double,
        d1: Double,
        d2: Double,
        f: Float,
        par9: Int,
        alpha: Float
    ) {
        this.updateModels()
        val side: Int = tileEntity.getBlockMetadata()
        var metaFacing: Int = tileEntity.facing

        GlStateManager.disableRescaleNormal()
        GlStateManager.pushMatrix()
        GlStateManager.translate(d.toFloat() + 0.5f, d1.toFloat() + 0.5f, d2.toFloat() + 0.5f)
        RenderHelper.enableStandardItemLighting()
        GlStateManager.enableRescaleNormal()

        when (side) {
            0 -> {}
            1 -> {
                GlStateManager.rotate(180f, 1f, 0f, 0f)
                if (metaFacing < 2) {
                    metaFacing = metaFacing xor 1
                }
            }

            2 -> {
                GlStateManager.rotate(90f, 1f, 0f, 0f)
                metaFacing = metaFacing xor 1
            }

            3 -> GlStateManager.rotate(90f, -1f, 0f, 0f)
            4 -> {
                GlStateManager.rotate(90f, 0f, 0f, -1f)
                metaFacing -= 2
                if (metaFacing < 0) {
                    metaFacing = 1 - metaFacing
                }
            }

            5 -> {
                GlStateManager.rotate(90f, 0f, 0f, 1f)
                metaFacing += 2
                if (metaFacing > 3) {
                    metaFacing = 5 - metaFacing
                }
            }
        }

        GlStateManager.translate(0f, -0.175f, 0f)

        when (metaFacing) {
            0 -> {}
            1 -> GlStateManager.rotate(180f, 0f, 1f, 0f)
            2 -> GlStateManager.rotate(90f, 0f, 1f, 0f)
            3 -> GlStateManager.rotate(270f, 0f, 1f, 0f)
        }

        this.bindTexture(lampTexture)
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        GlStateManager.rotate(45f, -1f, 0f, 0f)
        GlStateManager.scale(0.048f, 0.048f, 0.048f)
        ClientUtil.drawBakedModel((lampMetal)!!)
        RenderHelper.disableStandardItemLighting()

        val greyLevel: Float = if (tileEntity.getEnabled()) 1.0f else 26f / 255f
        //Save the lighting state
        val lightMapSaveX: Float = OpenGlHelper.lastBrightnessX
        val lightMapSaveY: Float = OpenGlHelper.lastBrightnessY
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f)
        GlStateManager.disableLighting()

        this.bindTexture(lightTexture)
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GlStateManager.disableTexture2D()
        val tess: Tessellator = Tessellator.getInstance()
        val worldRenderer: BufferBuilder = tess.getBuffer()
        GlStateManager.color(greyLevel, greyLevel, greyLevel, 1.0f)
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
        val frameA: Float = -3.4331f //These co-ordinates came originally from arclamp_light.obj model
        val frameB: Float = -frameA //These co-ordinates came originally from arclamp_light.obj model
        val frameY: Float = 2.3703f //These co-ordinates came originally from arclamp_light.obj model
        worldRenderer.pos(frameA.toDouble(), frameY.toDouble(), frameB.toDouble()).endVertex()
        worldRenderer.pos(frameB.toDouble(), frameY.toDouble(), frameB.toDouble()).endVertex()
        worldRenderer.pos(frameB.toDouble(), frameY.toDouble(), frameA.toDouble()).endVertex()
        worldRenderer.pos(frameA.toDouble(), frameY.toDouble(), frameA.toDouble()).endVertex()
        tess.draw()
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        GlStateManager.enableTexture2D()

        //? need to undo GlStateManager.glBlendFunc()?

        //Restore the lighting state
        GlStateManager.enableLighting()
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightMapSaveX, lightMapSaveY)
        GlStateManager.popMatrix()
    }

    private fun updateModels() {
        if (lampMetal == null) {
            try {
                lampMetal = ClientUtil.modelFromOBJ(ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "arclamp_metal.obj"))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        @JvmField
        val lampTexture: ResourceLocation = ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/misc/underoil.png")

        @JvmField
        val lightTexture: ResourceLocation = ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/misc/light.png")
        private var lampMetal: IBakedModel? = null
    }
}
