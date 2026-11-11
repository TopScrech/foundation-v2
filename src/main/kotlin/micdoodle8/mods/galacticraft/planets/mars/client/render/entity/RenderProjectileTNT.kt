package micdoodle8.mods.galacticraft.planets.mars.client.render.entity

import micdoodle8.mods.galacticraft.planets.mars.entities.EntityProjectileTNT
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BlockRendererDispatcher
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.Render
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.world.level.block.Blocks
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

@OnlyIn(Dist.CLIENT)
open class RenderProjectileTNT constructor(renderManager: RenderManager) :
    Render<EntityProjectileTNT>(renderManager) {
    init {
        this.shadowSize = 0.5f
    }

    override fun doRender(
        entity: EntityProjectileTNT,
        x: Double,
        y: Double,
        z: Double,
        par8: Float,
        partialTicks: Float
    ) {
        val blockrendererdispatcher: BlockRendererDispatcher = Minecraft.getInstance().getBlockRendererDispatcher()
        GlStateManager.pushMatrix()
        GlStateManager.translate(x.toFloat(), y.toFloat() + 0.5f, z.toFloat())

        val f2: Float = (1.0f - (entity.ticksExisted.toFloat() - partialTicks + 1.0f) / 100.0f) * 0.1f
        this.bindEntityTexture(entity)
        GlStateManager.translate(-0.5f, -0.5f, 0.5f)
        blockrendererdispatcher.renderBlockBrightness(Blocks.TNT.defaultBlockState(), entity.getBrightness())
        GlStateManager.translate(0.0f, 0.0f, 1.0f)

        if (entity.ticksExisted % 2 == 0) {
            GlStateManager.disableTexture2D()
            GlStateManager.disableLighting()
            GlStateManager.enableBlend()
            GlStateManager.blendFunc(770, 772)
            GlStateManager.color(1.0f, 1.0f, 1.0f, f2)
            GlStateManager.doPolygonOffset(-3.0f, -3.0f)
            GlStateManager.enablePolygonOffset()
            blockrendererdispatcher.renderBlockBrightness(Blocks.TNT.defaultBlockState(), 0.2f)
            GlStateManager.doPolygonOffset(0.0f, 0.0f)
            GlStateManager.disablePolygonOffset()
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
            GlStateManager.disableBlend()
            GlStateManager.enableLighting()
            GlStateManager.enableTexture2D()
        }

        GlStateManager.popMatrix()
        super.doRender(entity, x, y, z, par8, partialTicks)
    }

    override fun getEntityTexture(entity: EntityProjectileTNT): ResourceLocation? {
        return TextureMap.LOCATION_BLOCKS_TEXTURE
    }
}
