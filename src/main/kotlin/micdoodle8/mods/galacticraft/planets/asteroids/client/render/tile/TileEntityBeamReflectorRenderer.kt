package micdoodle8.mods.galacticraft.planets.asteroids.client.render.tile

import com.google.common.base.Function
import com.google.common.collect.ImmutableList
import micdoodle8.mods.galacticraft.core.client.model.OBJLoaderGC
import micdoodle8.mods.galacticraft.core.util.ClientUtil
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import micdoodle8.mods.galacticraft.planets.asteroids.tile.TileEntityBeamReflector
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
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import org.lwjgl.opengl.GL11

@OnlyIn(Dist.CLIENT)
open class TileEntityBeamReflectorRenderer : TileEntitySpecialRenderer<TileEntityBeamReflector>() {
    private fun updateModels() {
        if (reflectorModelBase == null) {
            try {
                val model: IModel = OBJLoaderGC.instance.loadModel(
                    ResourceLocation(
                        GalacticraftPlanets.ASSET_PREFIX,
                        "block/reflector.obj"
                    )
                )
                val spriteFunction: Function<ResourceLocation?, TextureAtlasSprite?> =
                    Function { location: ResourceLocation? ->
                        Minecraft.getInstance().getTextureMapBlocks().getAtlasSprite(location.toString())
                    }

                reflectorModelBase = model.bake(
                    OBJModel.OBJState(ImmutableList.of<String?>("Base"), false),
                    DefaultVertexFormats.ITEM,
                    spriteFunction
                ) as OBJModel.OBJBakedModel
                reflectorModelAxle = model.bake(
                    OBJModel.OBJState(ImmutableList.of<String?>("Axle"), false),
                    DefaultVertexFormats.ITEM,
                    spriteFunction
                ) as OBJModel.OBJBakedModel
                reflectorModelEnergyBlaster = model.bake(
                    OBJModel.OBJState(ImmutableList.of<String?>("EnergyBlaster"), false),
                    DefaultVertexFormats.ITEM,
                    spriteFunction
                ) as OBJModel.OBJBakedModel
                reflectorModelRing = model.bake(
                    OBJModel.OBJState(ImmutableList.of<String?>("Ring"), false),
                    DefaultVertexFormats.ITEM,
                    spriteFunction
                ) as OBJModel.OBJBakedModel
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
    }

    override fun render(
        tile: TileEntityBeamReflector,
        d: Double,
        d1: Double,
        d2: Double,
        f: Float,
        i: Int,
        alpha: Float
    ) {
        GlStateManager.disableRescaleNormal()
        GlStateManager.pushMatrix()
        GlStateManager.translate(d.toFloat() + 0.5f, d1.toFloat(), d2.toFloat() + 0.5f)
        GlStateManager.scale(0.5f, 0.5f, 0.5f)
        this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)

        if (Minecraft.isAmbientOcclusionEnabled()) {
            GlStateManager.shadeModel(GL11.GL_SMOOTH)
        } else {
            GlStateManager.shadeModel(GL11.GL_FLAT)
        }

        this.updateModels()
        ClientUtil.drawBakedModel((reflectorModelBase)!!)
        GlStateManager.rotate(tile.yaw, 0f, 1f, 0f)
        ClientUtil.drawBakedModel((reflectorModelAxle)!!)
        val dX: Float = 0.0f
        val dY: Float = 1.13228f
        val dZ: Float = 0.0f
        GlStateManager.translate(dX, dY, dZ)
        GlStateManager.rotate(tile.pitch, 1f, 0f, 0f)
        GlStateManager.translate(-dX, -dY, -dZ)
        ClientUtil.drawBakedModel((reflectorModelEnergyBlaster)!!)
        GlStateManager.translate(dX, dY, dZ)
        GlStateManager.rotate((tile.ticks * 5).toFloat(), 0f, 0f, 1f)
        GlStateManager.translate(-dX, -dY, -dZ)
        ClientUtil.drawBakedModel((reflectorModelRing)!!)
        GlStateManager.popMatrix()
        RenderHelper.enableStandardItemLighting()
    }

    companion object {
        private var reflectorModelBase: OBJModel.OBJBakedModel? = null
        private var reflectorModelAxle: OBJModel.OBJBakedModel? = null
        private var reflectorModelEnergyBlaster: OBJModel.OBJBakedModel? = null
        private var reflectorModelRing: OBJModel.OBJBakedModel? = null
    }
}
