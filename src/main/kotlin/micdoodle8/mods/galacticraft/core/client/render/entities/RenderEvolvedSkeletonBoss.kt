package micdoodle8.mods.galacticraft.core.client.render.entities

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.client.model.ModelEvolvedSkeletonBoss
import micdoodle8.mods.galacticraft.core.client.render.entities.layer.LayerHeldItemEvolvedSkeletonBoss
import micdoodle8.mods.galacticraft.core.entities.EntitySkeletonBoss
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.RenderLiving
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import kotlin.math.pow

@OnlyIn(Dist.CLIENT)
open class RenderEvolvedSkeletonBoss constructor(manager: RenderManager) :
    RenderLiving<EntitySkeletonBoss>(manager, ModelEvolvedSkeletonBoss(), 0.9f) {
    init {
        this.addLayer<EntitySkeletonBoss?, LayerHeldItemEvolvedSkeletonBoss?>(LayerHeldItemEvolvedSkeletonBoss(this))
    }

    override fun getEntityTexture(entity: EntitySkeletonBoss): ResourceLocation? {
        return skeletonBossTexture
    }

    override fun preRenderCallback(entity: EntitySkeletonBoss, partialTicks: Float) {
        GlStateManager.scale(1.2f, 1.2f, 1.2f)
        GlStateManager.rotate(
            (entity.deathTicks.toDouble().pow(2.0) / 5.0f + (entity.deathTicks.toDouble()
                .pow(2.0) / 5.0f - (entity.deathTicks - 1).toDouble().pow(2.0) / 5.0f) * partialTicks).toFloat(),
            0.0f,
            1.0f,
            0.0f
        )
    }

    companion object {
        private val skeletonBossTexture = ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/model/skeletonboss.png")
    }
}
