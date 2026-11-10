package micdoodle8.mods.galacticraft.core.client.render.entities.layer

import micdoodle8.mods.galacticraft.api.entity.ICameraZoomEntity
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.AbstractClientPlayer
import net.minecraft.client.model.ModelBiped
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.entity.RenderLivingBase
import net.minecraft.client.renderer.entity.layers.LayerRenderer
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumHandSide
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@SideOnly(Side.CLIENT)
open class LayerHeldItemGC constructor(@JvmField protected val livingEntityRenderer: RenderLivingBase<*>) :
    LayerRenderer<AbstractClientPlayer> {
    override fun doRenderLayer(
        player: AbstractClientPlayer,
        limbSwing: Float,
        limbSwingAmount: Float,
        partialTicks: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float,
        scale: Float
    ) {
        if (player.getRidingEntity() is ICameraZoomEntity) {
            return
        }
        val flag: Boolean = player.getPrimaryHand() == EnumHandSide.RIGHT
        val itemstack: ItemStack = if (flag) player.getHeldItemOffhand() else player.getHeldItemMainhand()
        val itemstack1: ItemStack = if (flag) player.getHeldItemMainhand() else player.getHeldItemOffhand()

        if (!itemstack.isEmpty() || !itemstack1.isEmpty()) {
            GlStateManager.pushMatrix()

            if (this.livingEntityRenderer.getMainModel().isChild) {
                val f: Float = 0.5f
                GlStateManager.translate(0.0f, 0.75f, 0.0f)
                GlStateManager.scale(0.5f, 0.5f, 0.5f)
            }

            this.renderHeldItem(
                player,
                itemstack1,
                ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND,
                EnumHandSide.RIGHT
            )
            this.renderHeldItem(
                player,
                itemstack,
                ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND,
                EnumHandSide.LEFT
            )
            GlStateManager.popMatrix()
        }
    }

    private fun renderHeldItem(
        p_188358_1_: EntityLivingBase,
        p_188358_2_: ItemStack,
        p_188358_3_: ItemCameraTransforms.TransformType,
        handSide: EnumHandSide
    ) {
        if (!p_188358_2_.isEmpty()) {
            GlStateManager.pushMatrix()

            if (p_188358_1_.isSneaking()) {
                GlStateManager.translate(0.0f, 0.2f, 0.0f)
            }
            // Forge: moved this call down, fixes incorrect offset while sneaking.
            (this.livingEntityRenderer.getMainModel() as ModelBiped).postRenderArm(0.0625f, handSide)
            GlStateManager.rotate(-90.0f, 1.0f, 0.0f, 0.0f)
            GlStateManager.rotate(180.0f, 0.0f, 1.0f, 0.0f)
            val flag: Boolean = handSide == EnumHandSide.LEFT
            GlStateManager.translate((if (flag) -1 else 1).toFloat() / 16.0f, 0.125f, -0.625f)
            Minecraft.getMinecraft().getItemRenderer().renderItemSide(p_188358_1_, p_188358_2_, p_188358_3_, flag)
            GlStateManager.popMatrix()
        }
    }

    override fun shouldCombineTextures(): Boolean {
        return false
    }
}
