package micdoodle8.mods.galacticraft.planets.mars.client.render.entity

import com.google.common.collect.ImmutableList
import micdoodle8.mods.galacticraft.core.util.ClientUtil
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import micdoodle8.mods.galacticraft.planets.mars.client.model.ModelBalloonParachute
import micdoodle8.mods.galacticraft.planets.mars.entities.EntityLandingBalloons
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.culling.ICamera
import net.minecraft.client.renderer.entity.Render
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import org.lwjgl.opengl.GL11

@OnlyIn(Dist.CLIENT)
open class RenderLandingBalloons constructor(manager: RenderManager) : Render<EntityLandingBalloons>(manager) {
    private var balloonModel: IBakedModel? = null

    @JvmField
    protected var parachuteModel: ModelBalloonParachute = ModelBalloonParachute()

    init {
        this.shadowSize = 1.2f
    }

    private fun updateModels() {
        if (this.balloonModel == null) {
            try {
                this.balloonModel = ClientUtil.modelFromOBJ(
                    ResourceLocation.fromNamespaceAndPath(GalacticraftPlanets.ASSET_PREFIX, "landing_balloon.obj"),
                    ImmutableList.of<String?>("Sphere")
                )
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
    }

    override fun getEntityTexture(entity: EntityLandingBalloons): ResourceLocation? {
        return TextureMap.LOCATION_BLOCKS_TEXTURE
    }

    override fun doRender(
        entity: EntityLandingBalloons,
        x: Double,
        y: Double,
        z: Double,
        entityYaw: Float,
        partialTicks: Float
    ) {
        val pitch: Float = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks
        GlStateManager.disableRescaleNormal()
        GlStateManager.pushMatrix()
        GlStateManager.translate(x.toFloat(), y.toFloat() + 0.8f, z.toFloat())
        GlStateManager.rotate(entityYaw, 0.0f, 1.0f, 0.0f)
        GlStateManager.rotate(180.0f, 1.0f, 0.0f, 0.0f)
        GlStateManager.rotate(pitch, 0.0f, 0.0f, 1.0f)

        this.updateModels()
        this.bindEntityTexture(entity)

        if (Minecraft.isAmbientOcclusionEnabled()) {
            GlStateManager.shadeModel(GL11.GL_SMOOTH)
        } else {
            GlStateManager.shadeModel(GL11.GL_FLAT)
        }

        GlStateManager.scale(-1.0f, -1.0f, 1.0f)
        GlStateManager.scale(0.5f, 0.5f, 0.5f)
        ClientUtil.drawBakedModel((this.balloonModel)!!)
        GlStateManager.popMatrix()

        if (entity.posY >= 500.0f) {
            GlStateManager.pushMatrix()
            GlStateManager.translate(x.toFloat() - 1.25f, y.toFloat() - 0.93f, z.toFloat() - 0.3f)
            GlStateManager.scale(2.5f, 3.0f, 2.5f)
            this.parachuteModel.renderAll()
            GlStateManager.popMatrix()
        }
        RenderHelper.enableStandardItemLighting()
    }

    override fun shouldRender(
        lander: EntityLandingBalloons,
        camera: ICamera,
        camX: Double,
        camY: Double,
        camZ: Double
    ): Boolean {
        val axisalignedbb: AxisAlignedBB = lander.getEntityBoundingBox().grow(2.0, 1.0, 2.0)
        return lander.isInRangeToRender3d(camX, camY, camZ) && camera.isBoundingBoxInFrustum(axisalignedbb)
    }
}
