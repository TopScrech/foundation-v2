package micdoodle8.mods.galacticraft.core.client.render.entities

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.client.model.ModelMeteorChunk
import micdoodle8.mods.galacticraft.core.entities.EntityMeteorChunk
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.Render
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.util.ResourceLocation

open class RenderMeteorChunk constructor(renderManager: RenderManager) :
    Render<EntityMeteorChunk>(renderManager) {
    private val modelMeteor: ModelMeteorChunk

    init {
        this.shadowSize = 0.1f
        this.modelMeteor = ModelMeteorChunk()
    }

    override fun getEntityTexture(entity: EntityMeteorChunk): ResourceLocation? {
        if (entity.isHot()) {
            return meteorChunkHotTexture
        } else {
            return meteorChunkTexture
        }
    }

    override fun doRender(
        entity: EntityMeteorChunk,
        x: Double,
        y: Double,
        z: Double,
        entityYaw: Float,
        partialTicks: Float
    ) {
        GlStateManager.pushMatrix()
        val pitch: Float = entity.rotationPitch
        val yaw: Float = entity.rotationYaw
        GlStateManager.translate(x.toFloat(), y.toFloat(), z.toFloat())
        GlStateManager.scale(0.3f, 0.3f, 0.3f)
        GlStateManager.rotate(yaw, 1.0f, 0.0f, 0.0f)
        GlStateManager.rotate(pitch, 0.0f, 0.0f, 1.0f)
        this.bindEntityTexture(entity)
        this.modelMeteor.render(entity, 0.0f, 0.0f, -0.1f, 0.0f, 0.0f, 0.0625f)
        GlStateManager.popMatrix()
    }

    companion object {
        private val meteorChunkTexture = ResourceLocation(Constants.ASSET_PREFIX, "textures/blocks/meteor_chunk.png")
        private val meteorChunkHotTexture =
            ResourceLocation(Constants.ASSET_PREFIX, "textures/blocks/meteor_chunk_hot.png")
    }
}
