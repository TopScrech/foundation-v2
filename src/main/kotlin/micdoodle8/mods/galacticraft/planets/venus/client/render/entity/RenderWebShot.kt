package micdoodle8.mods.galacticraft.planets.venus.client.render.entity

import micdoodle8.mods.galacticraft.planets.venus.entities.EntityWebShot
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BlockRendererDispatcher
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.entity.Render
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.init.Blocks
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@SideOnly(Side.CLIENT)
open class RenderWebShot constructor(renderManager: RenderManager) : Render<EntityWebShot>(renderManager) {
    init {
        this.shadowSize = 0.5f
    }

    override fun doRender(entity: EntityWebShot, x: Double, y: Double, z: Double, par8: Float, partialTicks: Float) {
        val blockrendererdispatcher: BlockRendererDispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher()
        RenderHelper.disableStandardItemLighting()
        GlStateManager.pushMatrix()
        GlStateManager.translate(x.toFloat(), y.toFloat() + 0.5f, z.toFloat())
        this.bindEntityTexture(entity)
        GlStateManager.rotate((entity.ticksExisted + partialTicks) * 50.0f, 0.0f, 1.0f, 0.0f)
        GlStateManager.scale(0.5f, 0.5f, 0.5f)
        GlStateManager.translate(-0.5f, -1f, 0.5f)
        blockrendererdispatcher.renderBlockBrightness(Blocks.WEB.getDefaultState(), 1.0f)
        GlStateManager.translate(0.0f, 0.0f, 1.0f)
        GlStateManager.popMatrix()
        RenderHelper.enableStandardItemLighting()
    }

    override fun getEntityTexture(entity: EntityWebShot): ResourceLocation? {
        return TextureMap.LOCATION_BLOCKS_TEXTURE
    }
}
