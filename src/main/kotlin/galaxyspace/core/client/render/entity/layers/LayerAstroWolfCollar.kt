package galaxyspace.core.client.render.entity.layers

import galaxyspace.GalaxySpace
import galaxyspace.core.client.render.entity.RenderAstroWolf
import galaxyspace.core.prefab.entities.EntityAstroWolf
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.layers.LayerRenderer
import net.minecraft.util.ResourceLocation

open class LayerAstroWolfCollar constructor(private val wolfRenderer: RenderAstroWolf) :
    LayerRenderer<EntityAstroWolf> {
    override fun doRenderLayer(
        entitylivingbaseIn: EntityAstroWolf,
        limbSwing: Float,
        limbSwingAmount: Float,
        partialTicks: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float,
        scale: Float
    ) {
        if (entitylivingbaseIn.isTamed() && !entitylivingbaseIn.isInvisible()) {
            this.wolfRenderer.bindTexture(WOLF_COLLAR)
            val afloat: FloatArray = entitylivingbaseIn.getCollarColor().getColorComponentValues()
            GlStateManager.color(afloat[0], afloat[1], afloat[2])
            this.wolfRenderer.getMainModel()
                .render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale)
        }
    }

    override fun shouldCombineTextures(): Boolean {
        return true
    }

    companion object {
        private val WOLF_COLLAR = ResourceLocation(GalaxySpace.ASSET_PREFIX, "textures/model/astro_wolf_collar.png")
    }
}
