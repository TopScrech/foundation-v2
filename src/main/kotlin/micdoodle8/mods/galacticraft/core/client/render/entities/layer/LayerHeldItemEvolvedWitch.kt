package micdoodle8.mods.galacticraft.core.client.render.entities.layer

import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType

import micdoodle8.mods.galacticraft.core.client.model.ModelEvolvedWitch
import micdoodle8.mods.galacticraft.core.client.render.entities.RenderEvolvedWitch
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedWitch
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.entity.layers.LayerRenderer
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@SideOnly(Side.CLIENT)
open class LayerHeldItemEvolvedWitch constructor(private val witchRenderer: RenderEvolvedWitch) :
    LayerRenderer<EntityEvolvedWitch> {
    override fun doRenderLayer(
        entity: EntityEvolvedWitch,
        limbSwing: Float,
        limbSwingAmount: Float,
        partialTicks: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float,
        scale: Float
    ) {
        val itemstack: ItemStack = entity.getHeldItemMainhand()

        if (!itemstack.isEmpty()) {
            GlStateManager.color(1.0f, 1.0f, 1.0f)
            GlStateManager.pushMatrix()

            if (this.witchRenderer.getMainModel().isChild) {
                GlStateManager.translate(0.0f, 0.625f, 0.0f)
                GlStateManager.rotate(-20.0f, -1.0f, 0.0f, 0.0f)
                GlStateManager.scale(0.5f, 0.5f, 0.5f)
            }

            (this.witchRenderer.getMainModel() as ModelEvolvedWitch).villagerNose.postRender(0.0625f)
            GlStateManager.translate(-0.0625f, 0.53125f, 0.21875f)
            val item: Item = itemstack.getItem()
            val mc: Minecraft = Minecraft.getMinecraft()

            //            if (item instanceof ItemBlock && mc.getBlockRendererDispatcher().isRenderTypeChest(Block.getBlockFromItem(item), itemstack.getMetadata()))
//            {
//                GlStateManager.translate(0.0F, 0.0625F, -0.25F);
//                GlStateManager.rotate(30.0F, 1.0F, 0.0F, 0.0F);
//                GlStateManager.rotate(-5.0F, 0.0F, 1.0F, 0.0F);
//                GlStateManager.scale(0.375F, -0.375F, 0.375F);
//            }
            if (item === Items.BOW) {
                GlStateManager.translate(0.0f, 0.125f, -0.125f)
                GlStateManager.rotate(-45.0f, 0.0f, 1.0f, 0.0f)
                GlStateManager.scale(0.625f, -0.625f, 0.625f)
                GlStateManager.rotate(-100.0f, 1.0f, 0.0f, 0.0f)
                GlStateManager.rotate(-20.0f, 0.0f, 1.0f, 0.0f)
            } else if (item.isFull3D()) {
                if (item.shouldRotateAroundWhenRendering()) {
                    GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f)
                    GlStateManager.translate(0.0f, -0.0625f, 0.0f)
                }

                this.witchRenderer.transformHeldFull3DItemLayer()
                GlStateManager.translate(0.0625f, -0.125f, 0.0f)
                GlStateManager.scale(0.625f, -0.625f, 0.625f)
                GlStateManager.rotate(0.0f, 1.0f, 0.0f, 0.0f)
                GlStateManager.rotate(0.0f, 0.0f, 1.0f, 0.0f)
            } else {
                GlStateManager.translate(0.1875f, 0.1875f, 0.0f)
                GlStateManager.scale(0.875f, 0.875f, 0.875f)
                GlStateManager.rotate(-20.0f, 0.0f, 0.0f, 1.0f)
                GlStateManager.rotate(-60.0f, 1.0f, 0.0f, 0.0f)
                GlStateManager.rotate(-30.0f, 0.0f, 0.0f, 1.0f)
            }
            GlStateManager.rotate(-15.0f, 1.0f, 0.0f, 0.0f)
            GlStateManager.rotate(40.0f, 0.0f, 0.0f, 1.0f)
            mc.getItemRenderer()
                .renderItem(entity, itemstack, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND)
            GlStateManager.popMatrix()
        }
    }

    override fun shouldCombineTextures(): Boolean {
        return false
    }
}
