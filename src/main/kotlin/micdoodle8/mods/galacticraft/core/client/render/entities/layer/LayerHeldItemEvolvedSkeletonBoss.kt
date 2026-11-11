package micdoodle8.mods.galacticraft.core.client.render.entities.layer

import micdoodle8.mods.galacticraft.core.client.model.ModelEvolvedSkeletonBoss
import micdoodle8.mods.galacticraft.core.client.render.entities.RenderEvolvedSkeletonBoss
import micdoodle8.mods.galacticraft.core.entities.EntitySkeletonBoss
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.entity.layers.LayerRenderer
import net.minecraft.world.item.Items
import net.minecraft.world.item.ItemStack
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

@OnlyIn(Dist.CLIENT)
open class LayerHeldItemEvolvedSkeletonBoss constructor(private val renderer: RenderEvolvedSkeletonBoss) :
    LayerRenderer<EntitySkeletonBoss> {
    override fun doRenderLayer(
        entity: EntitySkeletonBoss,
        limbSwing: Float,
        limbSwingAmount: Float,
        partialTicks: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float,
        scale: Float
    ) {
        if (entity.throwTimer + entity.postThrowDelay == 0) {
            this.renderHeldItem(entity, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND)
            this.renderHeldItem(entity, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND)
        }
    }

    override fun shouldCombineTextures(): Boolean {
        return false
    }

    private fun renderHeldItem(entity: EntitySkeletonBoss, type: ItemCameraTransforms.TransformType) {
        val bow: ItemStack = ItemStack(Items.BOW)

        if (bow != null) {
            GlStateManager.pushMatrix()
            (this.renderer.getMainModel() as ModelEvolvedSkeletonBoss).postRenderArm(0.0625f, type)

            if (type == ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND) {
                GlStateManager.rotate(-90.0f, 1.0f, 0.0f, 0.0f)
                GlStateManager.rotate(180.0f, 0.0f, 1.0f, 0.0f)
                GlStateManager.translate(0.05f, 0.125f, -1.525f)
            } else {
                GlStateManager.translate(0.025f, 1.525f, -0.125f)
            }

            Minecraft.getInstance().getItemRenderer().renderItemSide(entity, bow, type, false)
            GlStateManager.popMatrix()
        }
    }
}
