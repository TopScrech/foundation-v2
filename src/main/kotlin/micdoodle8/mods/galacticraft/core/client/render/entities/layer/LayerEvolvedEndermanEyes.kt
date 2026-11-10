package micdoodle8.mods.galacticraft.core.client.render.entities.layer

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.client.render.entities.RenderEvolvedEnderman
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedEnderman
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.entity.layers.LayerRenderer
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@SideOnly(Side.CLIENT)
open class LayerEvolvedEndermanEyes constructor(private val render: RenderEvolvedEnderman) :
    LayerRenderer<EntityEvolvedEnderman> {
    override fun shouldCombineTextures(): Boolean {
        return false
    }

    override fun doRenderLayer(
        entity: EntityEvolvedEnderman,
        limbSwing: Float,
        limbSwingAmount: Float,
        partialTicks: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float,
        scale: Float
    ) {
        this.render.bindTexture(eyesTexture)
        GlStateManager.enableBlend()
        GlStateManager.disableAlpha()
        GlStateManager.blendFunc(1, 1)
        GlStateManager.disableLighting()
        GlStateManager.depthMask(!entity.isInvisible())

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0f, 240.0f)
        GlStateManager.enableLighting()
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        this.render.getMainModel().render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale)
        this.render.setLightmap(entity)
        GlStateManager.depthMask(true)
        GlStateManager.disableBlend()
        GlStateManager.enableAlpha()
    }

    companion object {
        private val eyesTexture = ResourceLocation(Constants.ASSET_PREFIX, "textures/model/evolved_enderman_eyes.png")
    }
}
