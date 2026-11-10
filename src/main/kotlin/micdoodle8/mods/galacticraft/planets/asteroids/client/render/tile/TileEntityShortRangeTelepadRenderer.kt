package micdoodle8.mods.galacticraft.planets.asteroids.client.render.tile

import com.google.common.base.Function
import com.google.common.collect.ImmutableList
import micdoodle8.mods.galacticraft.core.client.model.OBJLoaderGC
import micdoodle8.mods.galacticraft.core.util.ClientUtil
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import micdoodle8.mods.galacticraft.planets.asteroids.tile.TileEntityShortRangeTelepad
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.IModel
import net.minecraftforge.client.model.obj.OBJModel
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11

@SideOnly(Side.CLIENT)
open class TileEntityShortRangeTelepadRenderer : TileEntitySpecialRenderer<TileEntityShortRangeTelepad>() {
    private fun updateModels() {
        if (teleporterTop == null) {
            try {
                val model: IModel = OBJLoaderGC.instance.loadModel(
                    ResourceLocation(
                        GalacticraftPlanets.ASSET_PREFIX,
                        "block/telepad_short.obj"
                    )
                )
                val spriteFunction: Function<ResourceLocation?, TextureAtlasSprite?> =
                    Function { location: ResourceLocation? ->
                        Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString())
                    }

                teleporterTop = model.bake(
                    OBJModel.OBJState(ImmutableList.of<String?>("Top", "Connector"), false),
                    DefaultVertexFormats.ITEM,
                    spriteFunction
                ) as OBJModel.OBJBakedModel
                teleporterBottom = model.bake(
                    OBJModel.OBJState(ImmutableList.of<String?>("Bottom"), false),
                    DefaultVertexFormats.ITEM,
                    spriteFunction
                ) as OBJModel.OBJBakedModel
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
    }

    override fun render(
        te: TileEntityShortRangeTelepad,
        x: Double,
        y: Double,
        z: Double,
        partialTicks: Float,
        destroyStage: Int,
        alpha: Float
    ) {
        GL11.glPushMatrix()

        RenderHelper.disableStandardItemLighting()
        this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
        if (Minecraft.isAmbientOcclusionEnabled()) {
            GlStateManager.shadeModel(GL11.GL_SMOOTH)
        } else {
            GlStateManager.shadeModel(GL11.GL_FLAT)
        }

        updateModels()

        GL11.glTranslatef(x.toFloat() + 0.5f, y.toFloat(), z.toFloat() + 0.5f)

        GL11.glScalef(0.745f, 1.0f, 0.745f)

        ClientUtil.drawBakedModel((teleporterBottom)!!)
        GL11.glTranslatef(0.0f, -0.7f, 0.0f)
        ClientUtil.drawBakedModel((teleporterTop)!!)

        GL11.glPopMatrix()
    }

    companion object {
        private var teleporterTop: OBJModel.OBJBakedModel? = null
        private var teleporterBottom: OBJModel.OBJBakedModel? = null
    }
}
