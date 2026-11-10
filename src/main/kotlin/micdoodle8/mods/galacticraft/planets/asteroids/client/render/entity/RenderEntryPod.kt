package micdoodle8.mods.galacticraft.planets.asteroids.client.render.entity

import com.google.common.collect.ImmutableList
import micdoodle8.mods.galacticraft.core.util.ClientUtil
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import micdoodle8.mods.galacticraft.planets.asteroids.entities.EntityEntryPod
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.culling.ICamera
import net.minecraft.client.renderer.entity.Render
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB
import org.lwjgl.opengl.GL11

open class RenderEntryPod constructor(manager: RenderManager) : Render<EntityEntryPod>(manager) {
    private var modelEntryPod: IBakedModel? = null

    private fun updateModels() {
        if (this.modelEntryPod == null) {
            try {
                this.modelEntryPod = ClientUtil.modelFromOBJ(
                    ResourceLocation(GalacticraftPlanets.ASSET_PREFIX, "pod.obj"),
                    ImmutableList.of<String?>("PodBody")
                )
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
    }

    override fun doRender(
        entity: EntityEntryPod,
        x: Double,
        y: Double,
        z: Double,
        entityYaw: Float,
        partialTicks: Float
    ) {
        val pitch: Float = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks
        val yaw: Float = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks
        GlStateManager.disableRescaleNormal()
        GlStateManager.pushMatrix()
        GlStateManager.translate(x.toFloat(), y.toFloat(), z.toFloat())
        GlStateManager.rotate(180.0f - entityYaw, 0.0f, 1.0f, 0.0f)
        GlStateManager.rotate(180.0f - pitch, 0.0f, 0.0f, 1.0f)
        GlStateManager.rotate(-yaw, 0.0f, 1.0f, 0.0f)

        this.updateModels()
        this.bindEntityTexture(entity)

        if (Minecraft.isAmbientOcclusionEnabled()) {
            GlStateManager.shadeModel(GL11.GL_SMOOTH)
        } else {
            GlStateManager.shadeModel(GL11.GL_FLAT)
        }

        GlStateManager.scale(-1.0f, -1.0f, 1.0f)
        GlStateManager.scale(0.65f, 0.6f, 0.65f)
        ClientUtil.drawBakedModel((this.modelEntryPod)!!)
        GlStateManager.popMatrix()
        RenderHelper.enableStandardItemLighting()
    }

    override fun getEntityTexture(entity: EntityEntryPod): ResourceLocation? {
        return TextureMap.LOCATION_BLOCKS_TEXTURE
    }

    override fun shouldRender(
        lander: EntityEntryPod,
        camera: ICamera,
        camX: Double,
        camY: Double,
        camZ: Double
    ): Boolean {
        val axisalignedbb: AxisAlignedBB = lander.getEntityBoundingBox().grow(1.0, 2.0, 1.0)
        return lander.isInRangeToRender3d(camX, camY, camZ) && camera.isBoundingBoxInFrustum(axisalignedbb)
    }
}
