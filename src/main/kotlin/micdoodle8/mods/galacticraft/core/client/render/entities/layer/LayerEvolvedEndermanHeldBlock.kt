package micdoodle8.mods.galacticraft.core.client.render.entities.layer

import micdoodle8.mods.galacticraft.core.client.render.entities.RenderEvolvedEnderman
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedEnderman
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BlockRendererDispatcher
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.entity.layers.LayerRenderer
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

@OnlyIn(Dist.CLIENT)
open class LayerEvolvedEndermanHeldBlock constructor(private val render: RenderEvolvedEnderman) :
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
        val iblockstate: IBlockState? = entity.getHeldBlockState()

        if (iblockstate != null) {
            val blockrendererdispatcher: BlockRendererDispatcher = Minecraft.getInstance().getBlockRendererDispatcher()
            GlStateManager.enableRescaleNormal()
            GlStateManager.pushMatrix()
            GlStateManager.translate(0.0f, 0.6875f, -0.75f)
            GlStateManager.rotate(20.0f, 1.0f, 0.0f, 0.0f)
            GlStateManager.rotate(45.0f, 0.0f, 1.0f, 0.0f)
            GlStateManager.translate(0.25f, 0.1875f, 0.25f)
            val f7: Float = 0.5f
            GlStateManager.scale(-f7, -f7, f7)
            val i: Int = entity.getBrightnessForRender()
            val j: Int = i % 65536
            val k: Int = i / 65536
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j.toFloat(), k.toFloat())
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
            this.render.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
            blockrendererdispatcher.renderBlockBrightness(iblockstate, 1.0f)
            GlStateManager.popMatrix()
            GlStateManager.disableRescaleNormal()
        }
    }
}
