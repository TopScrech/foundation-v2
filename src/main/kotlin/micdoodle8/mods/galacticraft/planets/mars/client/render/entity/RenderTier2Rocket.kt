package micdoodle8.mods.galacticraft.planets.mars.client.render.entity

import micdoodle8.mods.galacticraft.core.util.ClientUtil
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import micdoodle8.mods.galacticraft.planets.mars.client.render.item.ItemModelRocketT2
import micdoodle8.mods.galacticraft.planets.mars.entities.EntityTier2Rocket
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
import net.minecraft.util.math.MathHelper
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11

@SideOnly(Side.CLIENT)
open class RenderTier2Rocket constructor(manager: RenderManager) : Render<EntityTier2Rocket>(manager) {
    private var rocketModel: ItemModelRocketT2? = null

    init {
        this.shadowSize = 2f
    }

    private fun updateModel() {
        if (this.rocketModel == null) {
            val modelResourceLocation: ModelResourceLocation =
                ModelResourceLocation(GalacticraftPlanets.TEXTURE_PREFIX + "rocket_t2", "inventory")
            this.rocketModel =
                FMLClientHandler.instance().getClient().getRenderItem().getItemModelMesher().getModelManager()
                    .getModel(modelResourceLocation) as ItemModelRocketT2
        }
    }

    override fun getEntityTexture(entity: EntityTier2Rocket): ResourceLocation? {
        return TextureMap.LOCATION_BLOCKS_TEXTURE
    }

    override fun doRender(
        entity: EntityTier2Rocket,
        x: Double,
        y: Double,
        z: Double,
        entityYaw: Float,
        partialTicks: Float
    ) {
        val pitch: Float =
            entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks + 180
        val rollAmplitude: Float = entity.rollAmplitude / 3 - partialTicks
        GlStateManager.disableRescaleNormal()
        GlStateManager.pushMatrix()
        GlStateManager.translate(x.toFloat(), y.toFloat(), z.toFloat())
        GlStateManager.rotate(180.0f - entityYaw, 0.0f, 1.0f, 0.0f)
        GlStateManager.rotate(-pitch, 0.0f, 0.0f, 1.0f)
        GlStateManager.translate(0.0f, entity.getRenderOffsetY(), 0.0f)

        if (rollAmplitude > 0.0f) {
            val i: Float =
                if (entity.getLaunched()) (5 - MathHelper.floor((entity.timeUntilLaunch / 85).toFloat())) / 10f else 0.3f
            GlStateManager.rotate(MathHelper.sin(rollAmplitude) * rollAmplitude * i * partialTicks, 1.0f, 0.0f, 0.0f)
            GlStateManager.rotate(MathHelper.sin(rollAmplitude) * rollAmplitude * i * partialTicks, 1.0f, 0.0f, 1.0f)
        }

        this.updateModel()
        this.bindEntityTexture(entity)

        if (Minecraft.isAmbientOcclusionEnabled()) {
            GlStateManager.shadeModel(GL11.GL_SMOOTH)
        } else {
            GlStateManager.shadeModel(GL11.GL_FLAT)
        }

        GlStateManager.scale(-1.0f, -1.0f, 1.0f)
        GlStateManager.scale(0.8f, 0.8f, 0.8f)
        ClientUtil.drawBakedModel((this.rocketModel)!!)
        GlStateManager.popMatrix()
        RenderHelper.enableStandardItemLighting()
    }

    override fun shouldRender(
        rocket: EntityTier2Rocket,
        camera: ICamera,
        camX: Double,
        camY: Double,
        camZ: Double
    ): Boolean {
        val axisalignedbb: AxisAlignedBB = rocket.getEntityBoundingBox().grow(0.6, 1.0, 0.6)
        return rocket.isInRangeToRender3d(camX, camY, camZ) && camera.isBoundingBoxInFrustum(axisalignedbb)
    }
}
