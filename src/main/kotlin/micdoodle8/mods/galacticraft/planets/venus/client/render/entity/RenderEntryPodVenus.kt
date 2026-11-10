package micdoodle8.mods.galacticraft.planets.venus.client.render.entity

import com.google.common.base.Function
import org.lwjgl.opengl.GL11
import com.google.common.collect.ImmutableList
import micdoodle8.mods.galacticraft.core.client.model.OBJLoaderGC
import micdoodle8.mods.galacticraft.core.util.ClientUtil
import micdoodle8.mods.galacticraft.core.util.ColorUtil
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import micdoodle8.mods.galacticraft.planets.mars.client.model.ModelBalloonParachute
import micdoodle8.mods.galacticraft.planets.venus.entities.EntityEntryPodVenus
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.culling.ICamera
import net.minecraft.client.renderer.entity.Render
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB
import net.minecraftforge.client.model.IModel
import net.minecraftforge.client.model.obj.OBJModel
import java.lang.Exception
import java.lang.RuntimeException
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

open class RenderEntryPodVenus constructor(manager: RenderManager) : Render<EntityEntryPodVenus>(manager) {
    private var modelEntryPod: OBJModel.OBJBakedModel? = null
    private var modelFlame: OBJModel.OBJBakedModel? = null

    @JvmField
    protected var parachuteModel: ModelBalloonParachute = ModelBalloonParachute()

    private fun updateModels() {
        if (modelEntryPod == null) {
            try {
                val model: IModel =
                    OBJLoaderGC.instance.loadModel(ResourceLocation(GalacticraftPlanets.ASSET_PREFIX, "pod_flame.obj"))
                val spriteFunction: Function<ResourceLocation?, TextureAtlasSprite?> =
                    Function { location: ResourceLocation? ->
                        Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString())
                    }

                modelEntryPod = model.bake(
                    OBJModel.OBJState(ImmutableList.of<String?>("PodBody"), false),
                    DefaultVertexFormats.ITEM,
                    spriteFunction
                ) as OBJModel.OBJBakedModel
                modelFlame = model.bake(
                    OBJModel.OBJState(ImmutableList.of<String?>("Flame_Sphere"), false),
                    DefaultVertexFormats.ITEM,
                    spriteFunction
                ) as OBJModel.OBJBakedModel
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
    }

    override fun doRender(
        entityEntryPod: EntityEntryPodVenus,
        x: Double,
        y: Double,
        z: Double,
        entityYaw: Float,
        partialTicks: Float
    ) {
        GlStateManager.disableRescaleNormal()
        GlStateManager.pushMatrix()
        val var24: Float =
            entityEntryPod.prevRotationPitch + (entityEntryPod.rotationPitch - entityEntryPod.prevRotationPitch) * partialTicks
        val var25: Float =
            entityEntryPod.prevRotationYaw + (entityEntryPod.rotationYaw - entityEntryPod.prevRotationYaw) * partialTicks

        GlStateManager.translate(x.toFloat(), y.toFloat(), z.toFloat())
        GlStateManager.rotate(180.0f - entityYaw, 0.0f, 1.0f, 0.0f)
        GlStateManager.rotate(180.0f - var24, 0.0f, 0.0f, 1.0f)
        GlStateManager.rotate(-var25, 0.0f, 1.0f, 0.0f)

        this.updateModels()
        this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)

        GlStateManager.scale(-1.0f, -1.0f, 1.0f)
        GlStateManager.scale(0.65f, 0.6f, 0.65f)
        ClientUtil.drawBakedModel((modelEntryPod)!!)

        if (entityEntryPod.posY > 382.0f) {
            RenderHelper.disableStandardItemLighting()
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f)
            GlStateManager.disableLighting()
            GlStateManager.enableBlend()
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE)
            GlStateManager.cullFace(GlStateManager.CullFace.FRONT)

            val color: Int = ColorUtil.to32BitColor(
                if (entityEntryPod.posY >= 790.0f) 255 else max(
                    min(
                        255.0,
                        -(entityEntryPod.motionY + 0.6f) * 100.0f
                    ), 0.0
                ).toInt(), 255, 255, 255
            )

            GlStateManager.pushMatrix()
            val `val`: Float = (sin(entityEntryPod.ticksExisted.toDouble()) / 20.0f + 0.5f).toFloat()
            GlStateManager.scale(1.0f, 1.0f + `val`, 1.0f)
            GlStateManager.rotate(entityEntryPod.ticksExisted * 20.0f, 0.0f, 1.0f, 0.0f)
            ClientUtil.drawBakedModelColored((modelFlame)!!, color)
            GlStateManager.popMatrix()

            GlStateManager.scale(1.0f, 1.0f + `val` / 6.0f, 1.0f)
            GlStateManager.rotate(entityEntryPod.ticksExisted * 5.0f, 0.0f, 1.0f, 0.0f)
            ClientUtil.drawBakedModelColored((modelFlame)!!, color)

            GlStateManager.cullFace(GlStateManager.CullFace.BACK)
            GlStateManager.enableCull()
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            RenderHelper.enableStandardItemLighting()
        }

        if (entityEntryPod.getGroundPosY() != null && entityEntryPod.posY - (entityEntryPod.getGroundPosY())!! > 5.0f && entityEntryPod.posY <= 242.0f) {
            GlStateManager.pushMatrix()
            GlStateManager.translate(-1.4f, 1.5f, -0.3f)
            GlStateManager.scale(2.5f, 3.0f, 2.5f)
            this.parachuteModel.renderAll()
            GlStateManager.popMatrix()
        }
        GlStateManager.popMatrix()
    }

    override fun getEntityTexture(entityEntryPod: EntityEntryPodVenus): ResourceLocation? {
        return ResourceLocation("missing")
    }

    override fun shouldRender(
        lander: EntityEntryPodVenus,
        camera: ICamera,
        camX: Double,
        camY: Double,
        camZ: Double
    ): Boolean {
        val axisalignedbb: AxisAlignedBB = lander.getEntityBoundingBox().grow(1.0, 2.0, 1.0)
        return lander.isInRangeToRender3d(camX, camY, camZ) && camera.isBoundingBoxInFrustum(axisalignedbb)
    }
}
