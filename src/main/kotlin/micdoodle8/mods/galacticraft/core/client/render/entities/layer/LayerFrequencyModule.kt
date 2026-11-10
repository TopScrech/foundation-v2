package micdoodle8.mods.galacticraft.core.client.render.entities.layer

import com.google.common.base.Function
import com.google.common.collect.ImmutableList
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.client.model.ModelPlayerGC
import micdoodle8.mods.galacticraft.core.client.model.OBJLoaderGC
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerHandler
import micdoodle8.mods.galacticraft.core.util.ClientUtil
import micdoodle8.mods.galacticraft.core.wrappers.PlayerGearData
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.AbstractClientPlayer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.RenderPlayer
import net.minecraft.client.renderer.entity.layers.LayerRenderer
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.IModel
import net.minecraftforge.client.model.obj.OBJModel
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import kotlin.math.cos
import kotlin.math.sin

@SideOnly(Side.CLIENT)
open class LayerFrequencyModule constructor(private val playerRenderer: RenderPlayer) :
    LayerRenderer<AbstractClientPlayer> {
    private var moduleModel: OBJModel.OBJBakedModel? = null
    private var radarModel: OBJModel.OBJBakedModel? = null

    private fun updateModels() {
        if (this.moduleModel == null) {
            try {
                val model: IModel =
                    OBJLoaderGC.instance.loadModel(ResourceLocation(Constants.ASSET_PREFIX, "frequency_module.obj"))
                val spriteFunction: Function<ResourceLocation?, TextureAtlasSprite?> =
                    Function { location: ResourceLocation? ->
                        Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString())
                    }
                this.moduleModel = model.bake(
                    OBJModel.OBJState(ImmutableList.of<String?>("Main"), false),
                    DefaultVertexFormats.ITEM,
                    spriteFunction
                ) as OBJModel.OBJBakedModel
                this.radarModel = model.bake(
                    OBJModel.OBJState(ImmutableList.of<String?>("Radar"), false),
                    DefaultVertexFormats.ITEM,
                    spriteFunction
                ) as OBJModel.OBJBakedModel
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
    }

    override fun doRenderLayer(
        player: AbstractClientPlayer,
        f5: Float,
        f6: Float,
        partialTicks: Float,
        f8: Float,
        f2: Float,
        f7: Float,
        scale: Float
    ) {
        if (!player.isInvisible()) {
            val gearData: PlayerGearData? = (GalacticraftCore.proxy)!!.getGearData(player)

            if (gearData != null) {
                val wearingModule: Boolean = gearData.getFrequencyModule() != GCPlayerHandler.GEAR_NOT_PRESENT
                val wearingHelmet: Boolean = gearData.getMask() != GCPlayerHandler.GEAR_NOT_PRESENT
                FMLClientHandler.instance().getClient().renderEngine.bindTexture(ModelPlayerGC.playerTexture)

                if (wearingModule) {
                    this.updateModels()
                    GlStateManager.enableRescaleNormal()
                    GlStateManager.pushMatrix()
                    Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)

                    GlStateManager.rotate(180f, 1f, 0f, 0f)
                    GlStateManager.pushMatrix()
                    GlStateManager.rotate(
                        (this.playerRenderer.getMainModel().bipedHeadwear.rotateAngleY * -Constants.RADIANS_TO_DEGREES),
                        0f,
                        1f,
                        0f
                    )
                    GlStateManager.rotate(
                        (this.playerRenderer.getMainModel().bipedHeadwear.rotateAngleX * Constants.RADIANS_TO_DEGREES),
                        1f,
                        0f,
                        0f
                    )
                    GlStateManager.scale(0.3f, 0.3f, 0.3f)

                    if (wearingHelmet) {
                        GlStateManager.translate(-1.1f, if (player.isSneaking()) 0.35f else 1.2f, 0f)
                    } else {
                        GlStateManager.translate(-0.9f, if (player.isSneaking()) 0.1f else 0.9f, 0f)
                    }

                    ClientUtil.drawBakedModel((this.moduleModel)!!)
                    GlStateManager.translate(0.0f, 1.3f, 0.0f)
                    GlStateManager.rotate((sin(player.ticksExisted * 0.05) * 50.0f).toFloat(), 1f, 0f, 0f)
                    GlStateManager.rotate((cos(player.ticksExisted * 0.1) * 50.0f).toFloat(), 0f, 1f, 0f)
                    ClientUtil.drawBakedModel((this.radarModel)!!)
                    GlStateManager.popMatrix()
                    GlStateManager.color(1.0f, 1.0f, 1.0f)
                    GlStateManager.popMatrix()
                }
            }
        }
    }

    override fun shouldCombineTextures(): Boolean {
        return true
    }
}
