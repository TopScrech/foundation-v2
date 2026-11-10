package micdoodle8.mods.galacticraft.core.client.render.entities

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.client.model.ModelEvolvedSkeletonBoss
import micdoodle8.mods.galacticraft.core.client.render.entities.layer.LayerHeldItemEvolvedSkeletonBoss
import micdoodle8.mods.galacticraft.core.entities.EntitySkeletonBoss
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.RenderLiving
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import kotlin.math.pow

@SideOnly(Side.CLIENT)
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
        private val skeletonBossTexture = ResourceLocation(Constants.ASSET_PREFIX, "textures/model/skeletonboss.png")
    }
}
