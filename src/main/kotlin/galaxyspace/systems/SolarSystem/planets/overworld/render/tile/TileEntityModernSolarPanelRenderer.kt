package galaxyspace.systems.SolarSystem.planets.overworld.render.tile

import org.lwjgl.opengl.GL11
import galaxyspace.GalaxySpace
import com.google.common.collect.ImmutableList
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityModernSolarPanel
import asmodeuscore.api.dimension.IAdvancedSpace
import com.google.common.base.Function
import micdoodle8.mods.galacticraft.core.client.model.OBJLoaderGC
import micdoodle8.mods.galacticraft.core.util.ClientUtil
import micdoodle8.mods.galacticraft.planets.venus.dimension.WorldProviderVenus
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraftforge.client.model.IModel
import net.minecraftforge.client.model.obj.OBJModel
import java.lang.Exception
import java.lang.RuntimeException

open class TileEntityModernSolarPanelRenderer : TileEntitySpecialRenderer<TileEntityModernSolarPanel>() {
    private var main: OBJModel.OBJBakedModel? = null
    private var panels: OBJModel.OBJBakedModel? = null
    private var cell: OBJModel.OBJBakedModel? = null

    private fun updateModels() {
        if (main == null) {
            try {
                val model: IModel =
                    OBJLoaderGC.instance.loadModel(ResourceLocation.fromNamespaceAndPath(GalaxySpace.MODID, "modern_solarpanel.obj"))
                val spriteFunction: Function<ResourceLocation?, TextureAtlasSprite?> =
                    Function { location: ResourceLocation? ->
                        Minecraft.getInstance().getTextureMapBlocks().getAtlasSprite(location.toString())
                    }

                main = model.bake(
                    OBJModel.OBJState(ImmutableList.of<String?>("base"), false),
                    DefaultVertexFormats.ITEM,
                    spriteFunction
                ) as OBJModel.OBJBakedModel
                panels = model.bake(
                    OBJModel.OBJState(ImmutableList.of<String?>("panels"), false),
                    DefaultVertexFormats.ITEM,
                    spriteFunction
                ) as OBJModel.OBJBakedModel
                cell = model.bake(
                    OBJModel.OBJState(
                        ImmutableList.of<String?>(
                            "solar_cell_panels",
                            "solar_cell.001_panels",
                            "solar_cell.002_panels",
                            "solar_cell.003_panels"
                        ), false
                    ), DefaultVertexFormats.ITEM, spriteFunction
                ) as OBJModel.OBJBakedModel
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
    }

    override fun render(
        panel: TileEntityModernSolarPanel,
        x: Double,
        y: Double,
        z: Double,
        partialTicks: Float,
        destroyStage: Int,
        partial: Float
    ) {
        //OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 0F, 240F);

        GlStateManager.pushMatrix()
        GlStateManager.disableRescaleNormal()
        this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
        //GlStateManager.enableLighting();
        //RenderHelper.disableStandardItemLighting();
        //GlStateManager.disableCull();
        //GlStateManager.disableBlend();
        this.updateModels()

        if (Minecraft.isAmbientOcclusionEnabled()) {
            GlStateManager.shadeModel(GL11.GL_SMOOTH)
        } else {
            GlStateManager.shadeModel(GL11.GL_FLAT)
        }


        //GlStateManager.color(0.5F, 0.5F, 0.5F, 0.5F);
        GlStateManager.translate(x.toFloat() + 0.5f, y.toFloat() + 3.1f, z.toFloat() + 0.48f)
        GlStateManager.rotate(-90f, 0f, 1f, 0f)


        ClientUtil.drawBakedModel((main)!!)

        val celestialAngle: Float = (panel.getWorld().getCelestialAngle(1.0f) - 0.784690560f) * 360.0f
        val celestialAngle2: Float = panel.getWorld().getCelestialAngle(1.0f) * 360.0f

        var par1: Float = celestialAngle - celestialAngle2
        if (panel.getWorld().provider is WorldProviderVenus || (panel.getWorld().provider is IAdvancedSpace && (panel.getWorld().provider as IAdvancedSpace).isRetrogradeRotation())) par1 =
            celestialAngle + celestialAngle2

        if (panel.getWorld().isRaining()) par1 = 80f
        GlStateManager.rotate(panel.currentAngle - (par1), 1.0f, 0.0f, 0.0f)
        GlStateManager.scale(0.9f, 0.9f, 0.9f)

        //GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.rotate(-90f, 0f, 1f, 0f)
        ClientUtil.drawBakedModel((panels)!!)


        //ClientUtil.drawBakedModel(cell);
        GlStateManager.disableRescaleNormal()
        GlStateManager.popMatrix()
        RenderHelper.enableStandardItemLighting()
    }
}
