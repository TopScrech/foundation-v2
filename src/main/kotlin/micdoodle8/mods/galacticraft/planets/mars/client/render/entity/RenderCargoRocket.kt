package micdoodle8.mods.galacticraft.planets.mars.client.render.entity

import micdoodle8.mods.galacticraft.core.util.ClientUtil
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import micdoodle8.mods.galacticraft.planets.asteroids.client.render.item.ItemModelCargoRocket
import micdoodle8.mods.galacticraft.planets.mars.entities.EntityCargoRocket
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.block.model.ModelResourceLocation
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
open class RenderCargoRocket constructor(renderManager: RenderManager) :
    Render<EntityCargoRocket>(renderManager) {
    private var rocketModel: ItemModelCargoRocket? = null

    init {
        this.shadowSize = 0.5f
    }

    private fun updateModel() {
        if (this.rocketModel == null) {
            val modelResourceLocation: ModelResourceLocation =
                ModelResourceLocation(GalacticraftPlanets.TEXTURE_PREFIX + "rocket_cargo", "inventory")
            this.rocketModel =
                Minecraft.getInstance().getRenderItem().getItemModelMesher().getModelManager()
                    .getModel(modelResourceLocation) as ItemModelCargoRocket
        }
    }

    override fun getEntityTexture(entity: EntityCargoRocket): ResourceLocation? {
        return TextureMap.LOCATION_BLOCKS_TEXTURE
    }

    override fun doRender(
        entity: EntityCargoRocket,
        x: Double,
        y: Double,
        z: Double,
        entityYaw: Float,
        partialTicks: Float
    ) {
        val pitch: Float = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks
        GlStateManager.disableRescaleNormal()
        GlStateManager.pushMatrix()
        GlStateManager.translate(x.toFloat(), y.toFloat() + entity.getRenderOffsetY(), z.toFloat())
        GlStateManager.scale(0.4f, 0.4f, 0.4f)
        GlStateManager.rotate(180.0f - entityYaw, 0.0f, 1.0f, 0.0f)
        GlStateManager.rotate(-pitch, 0.0f, 0.0f, 1.0f)
        this.bindEntityTexture(entity)

        if (Minecraft.isAmbientOcclusionEnabled()) {
            GlStateManager.shadeModel(GL11.GL_SMOOTH)
        } else {
            GlStateManager.shadeModel(GL11.GL_FLAT)
        }

        this.updateModel()
        ClientUtil.drawBakedModel((this.rocketModel)!!)
        GlStateManager.popMatrix()
        RenderHelper.enableStandardItemLighting()
    }

    override fun shouldRender(
        rocket: EntityCargoRocket,
        camera: ICamera,
        camX: Double,
        camY: Double,
        camZ: Double
    ): Boolean {
        val axisalignedbb: AxisAlignedBB = rocket.getEntityBoundingBox()
        return rocket.isInRangeToRender3d(camX, camY, camZ) && camera.isBoundingBoxInFrustum(axisalignedbb)
    }
}
