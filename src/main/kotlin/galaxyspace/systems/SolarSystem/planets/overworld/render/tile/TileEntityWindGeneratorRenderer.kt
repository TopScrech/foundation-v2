package galaxyspace.systems.SolarSystem.planets.overworld.render.tile

import org.lwjgl.opengl.GL11
import galaxyspace.GalaxySpace
import com.google.common.collect.ImmutableList
import asmodeuscore.core.utils.Utils
import com.google.common.base.Function
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityWindGenerator
import galaxyspace.systems.SolarSystem.planets.overworld.models.ModelWindTurbine
import galaxyspace.systems.SolarSystem.planets.overworld.render.tile.TileEntityWindGeneratorRenderer
import micdoodle8.mods.galacticraft.core.client.model.OBJLoaderGC
import micdoodle8.mods.galacticraft.core.util.ClientUtil
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
import kotlin.math.min

open class TileEntityWindGeneratorRenderer : TileEntitySpecialRenderer<TileEntityWindGenerator>() {
    @JvmField
    var model: ModelWindTurbine = ModelWindTurbine()

    private var cube: OBJModel.OBJBakedModel? = null
    private var hexadecagon: OBJModel.OBJBakedModel? = null

    private fun updateModels() {
        if (cube == null) {
            try {
                val model: IModel = OBJLoaderGC.instance
                    .loadModel(ResourceLocation.fromNamespaceAndPath(GalaxySpace.MODID, "windmill_straight.obj"))
                val spriteFunction: Function<ResourceLocation?, TextureAtlasSprite?> =
                    Function { location: ResourceLocation? ->
                        Minecraft.getInstance()
                            .getTextureMapBlocks().getAtlasSprite(location.toString())
                    }

                cube = model.bake(
                    OBJModel.OBJState(ImmutableList.of<String?>("cube"), false),
                    DefaultVertexFormats.ITEM, spriteFunction
                ) as OBJModel.OBJBakedModel
                hexadecagon = model.bake(
                    OBJModel.OBJState(ImmutableList.of<String?>("hexadecagon"), false), DefaultVertexFormats.ITEM,
                    spriteFunction
                ) as OBJModel.OBJBakedModel
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
    }

    override fun render(
        wind: TileEntityWindGenerator, x: Double, y: Double, z: Double, partialTickTime: Float, par9: Int,
        alpha: Float
    ) {
        GlStateManager.pushMatrix()
        GlStateManager.disableRescaleNormal()

        if (Minecraft.isAmbientOcclusionEnabled()) {
            GlStateManager.shadeModel(GL11.GL_SMOOTH)
        } else {
            GlStateManager.shadeModel(GL11.GL_FLAT)
        }

        wind.angle = min(
            (wind.angle + ((wind.getPos().getY()) / 500f) * (wind.getWindBoost() * 10.0f)),
            wind.angle + 15f
        ) % 360

        if (wind.isAdvanced()) {
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
            GlStateManager.translate(x.toFloat() + 0.5f, y.toFloat() + 1.0f, z.toFloat() + 0.48f)
            this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
            this.updateModels()

            GlStateManager.scale(2.0, 1.0, 2.0)
            ClientUtil.drawBakedModel((hexadecagon)!!)

            GlStateManager.translate(0.0f, 2.0f, 0.0f)
            ClientUtil.drawBakedModel((hexadecagon)!!)

            if (wind.getFanType() >= 0) {
                GL11.glRotatef(wind.angle, 0f, 1f, 0f)
                var r: Float = 1.0f
                var g: Float = 1.0f
                var b: Float = 1.0f
                when (wind.getFanType()) {
                    0 -> {
                        r = 0.6f
                        g = 0.6f
                        b = 0.65f
                    }

                    1 -> {
                        r = 0.3f
                        g = 0.3f
                        b = 0.4f
                    }

                    2 -> {
                        r = 1.0f
                        g = 1.0f
                        b = 1.0f
                    }

                    3 -> {
                        r = 0.15f
                        g = 0.15f
                        b = 0.15f
                    }
                }

                val i_r: Int = (255 * r).toInt()
                val i_g: Int = (255 * g).toInt()
                val i_b: Int = (255 * b).toInt()

                ClientUtil.drawBakedModelColored((cube)!!, Utils.getIntColorWHC(i_r, i_g, i_b, 255))
            }
        } else {
            this.bindTexture(windTurbineTexture)
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
            GL11.glTranslatef(x.toFloat(), y.toFloat(), z.toFloat())
            GL11.glTranslatef(0.49f, 3.3f, 0.48f)
            GL11.glRotatef(180.0f, 0f, 0f, 1f)
            GL11.glRotatef((wind.getBlockMetadata() * 90 + 90).toFloat(), 0.0f, 1.0f, 0.0f)
            GL11.glScalef(0.87f, 0.87f, 0.87f)
            this.model.renderPole(0.0625f)
            GL11.glTranslatef(0.0f, 0.0f, 0.0f)
            GL11.glRotatef(wind.angle, 0f, 0f, 1.0f)

            if (wind.getFanType() >= 0) {
                var r: Float = 1.0f
                var g: Float = 1.0f
                var b: Float = 1.0f
                when (wind.getFanType()) {
                    0 -> {
                        r = 0.6f
                        g = 0.6f
                        b = 0.65f
                    }

                    1 -> {
                        r = 0.3f
                        g = 0.3f
                        b = 0.4f
                    }

                    2 -> {
                        r = 1.0f
                        g = 1.0f
                        b = 1.0f
                    }

                    3 -> {
                        r = 0.15f
                        g = 0.15f
                        b = 0.15f
                    }
                }
                GL11.glColor4f(r, g, b, 1.0f)
                this.model.renderFlares(0.0625f)
            }
        }

        GlStateManager.disableRescaleNormal()
        GlStateManager.popMatrix()
        RenderHelper.enableStandardItemLighting()
    } /*
	 * @Override public void render(TileEntityWindGenerator wind, double par2,
	 * double par4, double par6, float partialTickTime, int par9, float alpha) {
	 * 
	 * //OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 0F,
	 * 240F);
	 * 
	 * / *if (var1.getBlockMetadata() >= BlockSolarPanel.ULTRA_METADATA) {
	 * this.bindTexture(TileEntityWindTurbine.solarPanelUltTexture); } else {
	 * this.bindTexture(this.windTurbineTexture); //}
	 * 
	 * GL11.glPushMatrix(); GlStateManager.disableRescaleNormal();
	 * 
	 * if (Minecraft.isAmbientOcclusionEnabled()) {
	 * GlStateManager.shadeModel(GL11.GL_SMOOTH); } else {
	 * GlStateManager.shadeModel(GL11.GL_FLAT); }
	 * 
	 * GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F); GL11.glTranslatef((float) par2,
	 * (float) par4, (float) par6);
	 * 
	 * GL11.glTranslatef(0.49F, 3.3F, 0.48F);
	 * 
	 * //GL11.glTranslatef(0.0F, 1.5F, 0.0F);
	 * 
	 * GL11.glRotatef(180.0F, 0, 0, 1); GL11.glRotatef(wind.getBlockMetadata() * 90
	 * + 90, 0.0F, 1.0F, 0.0F); GL11.glScalef(0.87F, 0.87F, 0.87F);
	 * this.model.renderPole(0.0625F); //GL11.glScalef(2.0F, 2.0F, 2.0F);
	 * //this.model.renderPanel();
	 * 
	 * wind.angle = Math.min((wind.angle+((wind.getPos().getY())/500F) *
	 * (wind.getWindBoost() * 15.0F)), wind.angle + 15F) % 360;
	 * 
	 * GL11.glTranslatef(0.0F, 0.06F, 0.0F); GL11.glRotatef(wind.angle, 0F, 0F,
	 * 1.0F);
	 * 
	 * if(wind.getFanType() >= 0) { float r = 1.0F, g = 1.0F,b = 1.0F; switch
	 * (wind.getFanType()) { case 0: { r = 0.6F; g = 0.6F; b = 0.65F;
	 * 
	 * break; } case 1: { r = 0.3F; g = 0.3F; b = 0.4F;
	 * 
	 * break; } case 2: { r = 1.0F; g = 1.0F; b = 1.0F;
	 * 
	 * break; } case 3: { r = 0.15F; g = 0.15F; b = 0.15F;
	 * 
	 * break; } }
	 * 
	 * GL11.glColor4f(r, g, b, 1.0F);
	 * 
	 * this.model.renderFlares(0.0625F); GL11.glTranslatef(0.0F, -0.1F, 0.0F); }
	 * 
	 * GlStateManager.disableRescaleNormal(); GL11.glPopMatrix();
	 * 
	 * RenderHelper.enableStandardItemLighting(); }
	 */

    companion object {
        private val windTurbineTexture = ResourceLocation(
            GalaxySpace.ASSET_PREFIX,
            "textures/model/wind_turbine.png"
        )
    }
}
