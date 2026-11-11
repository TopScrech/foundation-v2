package micdoodle8.mods.galacticraft.core.client.render.entities.layer

import micdoodle8.mods.galacticraft.core.client.model.ModelEvolvedCreeper
import micdoodle8.mods.galacticraft.core.client.render.entities.RenderEvolvedCreeper
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedCreeper
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.layers.LayerRenderer
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

@OnlyIn(Dist.CLIENT)
open class LayerEvolvedCreeperCharge constructor(private val render: RenderEvolvedCreeper) :
    LayerRenderer<EntityEvolvedCreeper> {
    private val creeperModel = ModelEvolvedCreeper(2.0f)

    override fun doRenderLayer(
        entity: EntityEvolvedCreeper,
        limbSwing: Float,
        limbSwingAmount: Float,
        partialTicks: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float,
        scale: Float
    ) {
        if (entity.getPowered()) {
            val flag: Boolean = entity.isInvisible()
            GlStateManager.depthMask(!flag)
            this.render.bindTexture(ResourceLocation.parse("textures/entity/creeper/creeper_armor.png"))
            GlStateManager.matrixMode(5890)
            GlStateManager.loadIdentity()
            val f: Float = entity.ticksExisted + partialTicks
            GlStateManager.translate(f * 0.01f, f * 0.01f, 0.0f)
            GlStateManager.matrixMode(5888)
            GlStateManager.enableBlend()
            val f1: Float = 0.5f
            GlStateManager.color(f1, f1, f1, 1.0f)
            GlStateManager.disableLighting()
            GlStateManager.blendFunc(1, 1)
            this.creeperModel.setModelAttributes(this.render.getMainModel())
            this.creeperModel.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale)
            GlStateManager.matrixMode(5890)
            GlStateManager.loadIdentity()
            GlStateManager.matrixMode(5888)
            GlStateManager.enableLighting()
            GlStateManager.disableBlend()
            GlStateManager.depthMask(flag)
        }
    }

    override fun shouldCombineTextures(): Boolean {
        return false
    }
}
