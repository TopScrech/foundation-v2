package galaxyspace.core.client.render.entity

import java.util.Arrays

import com.google.common.base.Function
import galaxyspace.GalaxySpace
import micdoodle8.mods.galacticraft.api.prefab.entity.EntityAutoRocket
import micdoodle8.mods.galacticraft.core.client.model.OBJLoaderGC
import micdoodle8.mods.galacticraft.core.util.ClientUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.culling.ICamera
import net.minecraft.client.renderer.entity.Render
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraft.util.Mth as MathHelper
import net.minecraftforge.client.model.IModel
import net.minecraftforge.client.model.obj.OBJModel
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import org.lwjgl.opengl.GL11
import java.io.IOException
import java.util.*

@OnlyIn(Dist.CLIENT)
open class RenderRockets constructor(manager: RenderManager, private val model: String?, vararg parts: String?) :
    Render<EntityAutoRocket>(manager) {
    private var rocketModel: OBJModel.OBJBakedModel? = null
    private val parts: MutableList<String?>

    init {
        this.shadowSize = 2f
        this.parts = Arrays.asList<String?>(*parts)
    }

    private fun updateModel() {
        if (this.rocketModel == null) {
            try {
                val model: IModel =
                    OBJLoaderGC.instance.loadModel(ResourceLocation.fromNamespaceAndPath(GalaxySpace.ASSET_PREFIX, this.model + ".obj"))
                val spriteFunction: Function<ResourceLocation?, TextureAtlasSprite?> =
                    Function { location: ResourceLocation? ->
                        Minecraft.getInstance().getTextureMapBlocks().getAtlasSprite(location.toString())
                    }
                this.rocketModel = model.bake(
                    OBJModel.OBJState(this.parts, false),
                    DefaultVertexFormats.ITEM,
                    spriteFunction
                ) as OBJModel.OBJBakedModel
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }
    }

    override fun getEntityTexture(entity: EntityAutoRocket): ResourceLocation? {
        return TextureMap.LOCATION_BLOCKS_TEXTURE
    }

    override fun doRender(
        entity: EntityAutoRocket,
        x: Double,
        y: Double,
        z: Double,
        entityYaw: Float,
        partialTicks: Float
    ) {
        //OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 0F, 240F);    	
        val pitch: Float =
            entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks + 180
        GlStateManager.disableRescaleNormal()
        GlStateManager.pushMatrix()
        GlStateManager.disableCull()
        GlStateManager.translate(x.toFloat(), y.toFloat(), z.toFloat())
        GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f)
        GlStateManager.rotate(0.0f + entityYaw, 0.0f, 1.0f, 0.0f)
        GlStateManager.rotate(pitch, 0.0f, 0.0f, 1.0f)
        GlStateManager.translate(0.0f, entity.getRenderOffsetY(), 0.0f)
        val rollAmplitude: Float = entity.rollAmplitude / 3 - partialTicks

        if (rollAmplitude > 0.0f) {
            val i: Float =
                if (entity.getLaunched()) (5 - MathHelper.floor((entity.timeUntilLaunch / 85).toFloat())) / 10f else 0.3f
            GlStateManager.rotate(MathHelper.sin(rollAmplitude) * rollAmplitude * i * partialTicks, 1.0f, 0.0f, 0.0f)
            GlStateManager.rotate(MathHelper.sin(rollAmplitude) * rollAmplitude * i * partialTicks, 1.0f, 0.0f, 1.0f)
        }

        this.updateModel()
        this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)

        if (Minecraft.isAmbientOcclusionEnabled()) {
            GlStateManager.shadeModel(GL11.GL_SMOOTH)
        } else {
            GlStateManager.shadeModel(GL11.GL_FLAT)
        }

        //GlStateManager.scale(-1.0F, -1.0F, 1.0F);
        GlStateManager.scale(0.8f, 0.8f, 0.8f)
        ClientUtil.drawBakedModel((this.rocketModel)!!)

        GlStateManager.enableTexture2D()
        GlStateManager.enableLighting()
        GlStateManager.enableCull()
        GlStateManager.color(1f, 1f, 1f)
        GlStateManager.popMatrix()
        RenderHelper.enableStandardItemLighting()
    }

    override fun shouldRender(
        rocket: EntityAutoRocket,
        camera: ICamera,
        camX: Double,
        camY: Double,
        camZ: Double
    ): Boolean {
        val axisalignedbb: AxisAlignedBB = rocket.getEntityBoundingBox().grow(0.5, 0.0, 0.5)
        return rocket.isInRangeToRender3d(camX, camY, camZ) && camera.isBoundingBoxInFrustum(axisalignedbb)
    }
}
