package galaxyspace.core.client.render.entity

import galaxyspace.GalaxySpace
import galaxyspace.core.prefab.entities.EntityCustomCargoRocket
import micdoodle8.mods.galacticraft.core.util.ClientUtil
import micdoodle8.mods.galacticraft.planets.asteroids.client.render.item.ItemModelCargoRocket
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.culling.ICamera
import net.minecraft.client.renderer.entity.Render
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11

@SideOnly(Side.CLIENT)
open class RenderCargoRockets constructor(renderManager: RenderManager, model: String?) :
    Render<EntityCustomCargoRocket>(renderManager) {
    private var rocketModel: ItemModelCargoRocket? = null
    private val model: String?

    init {
        this.shadowSize = 0.5f
        this.model = model
    }

    private fun updateModel() {
        if (this.rocketModel == null) {
            val modelResourceLocation: ModelResourceLocation =
                ModelResourceLocation(GalaxySpace.TEXTURE_PREFIX + this.model, "inventory")
            this.rocketModel =
                FMLClientHandler.instance().getClient().getRenderItem().getItemModelMesher().getModelManager()
                    .getModel(modelResourceLocation) as ItemModelCargoRocket
        }
    }

    override fun getEntityTexture(entity: EntityCustomCargoRocket): ResourceLocation? {
        return TextureMap.LOCATION_BLOCKS_TEXTURE
    }

    override fun doRender(
        entity: EntityCustomCargoRocket,
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
        rocket: EntityCustomCargoRocket,
        camera: ICamera,
        camX: Double,
        camY: Double,
        camZ: Double
    ): Boolean {
        val axisalignedbb: AxisAlignedBB = rocket.getEntityBoundingBox()
        return rocket.isInRangeToRender3d(camX, camY, camZ) && camera.isBoundingBoxInFrustum(axisalignedbb)
    }
}
