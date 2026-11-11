package micdoodle8.mods.galacticraft.core.client.render.entities

import com.google.common.base.Function
import com.google.common.collect.ImmutableList
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.client.model.OBJLoaderGC
import micdoodle8.mods.galacticraft.core.entities.EntityBuggy
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
import net.minecraftforge.client.model.IModel
import net.minecraftforge.client.model.obj.OBJModel
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import org.lwjgl.opengl.GL11
import kotlin.math.cos
import kotlin.math.sin

@OnlyIn(Dist.CLIENT)
open class RenderBuggy constructor(manager: RenderManager) : Render<EntityBuggy>(manager) {
    private var mainModel: OBJModel.OBJBakedModel? = null
    private var radarDish: OBJModel.OBJBakedModel? = null
    private var wheelLeftCover: OBJModel.OBJBakedModel? = null
    private var wheelRight: OBJModel.OBJBakedModel? = null
    private var wheelLeft: OBJModel.OBJBakedModel? = null
    private var wheelRightCover: OBJModel.OBJBakedModel? = null
    private var cargoLeft: OBJModel.OBJBakedModel? = null
    private var cargoMid: OBJModel.OBJBakedModel? = null
    private var cargoRight: OBJModel.OBJBakedModel? = null

    private fun updateModels() {
        if (this.mainModel == null) {
            try {
                val model: IModel =
                    OBJLoaderGC.instance.loadModel(ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "buggy.obj"))
                val spriteFunction: Function<ResourceLocation?, TextureAtlasSprite?> =
                    Function { location: ResourceLocation? ->
                        Minecraft.getInstance().getTextureMapBlocks().getAtlasSprite(location.toString())
                    }

                this.mainModel = model.bake(
                    OBJModel.OBJState(ImmutableList.of<String?>("MainBody"), false),
                    DefaultVertexFormats.ITEM,
                    spriteFunction
                ) as OBJModel.OBJBakedModel
                this.radarDish = model.bake(
                    OBJModel.OBJState(ImmutableList.of<String?>("RadarDish_Dish"), false),
                    DefaultVertexFormats.ITEM,
                    spriteFunction
                ) as OBJModel.OBJBakedModel
                this.wheelLeftCover = model.bake(
                    OBJModel.OBJState(ImmutableList.of<String?>("Wheel_Left_Cover"), false),
                    DefaultVertexFormats.ITEM,
                    spriteFunction
                ) as OBJModel.OBJBakedModel
                this.wheelRight = model.bake(
                    OBJModel.OBJState(ImmutableList.of<String?>("Wheel_Right"), false),
                    DefaultVertexFormats.ITEM,
                    spriteFunction
                ) as OBJModel.OBJBakedModel
                this.wheelLeft = model.bake(
                    OBJModel.OBJState(ImmutableList.of<String?>("Wheel_Left"), false),
                    DefaultVertexFormats.ITEM,
                    spriteFunction
                ) as OBJModel.OBJBakedModel
                this.wheelRightCover = model.bake(
                    OBJModel.OBJState(ImmutableList.of<String?>("Wheel_Right_Cover"), false),
                    DefaultVertexFormats.ITEM,
                    spriteFunction
                ) as OBJModel.OBJBakedModel
                this.cargoLeft = model.bake(
                    OBJModel.OBJState(ImmutableList.of<String?>("CargoLeft"), false),
                    DefaultVertexFormats.ITEM,
                    spriteFunction
                ) as OBJModel.OBJBakedModel
                this.cargoMid = model.bake(
                    OBJModel.OBJState(ImmutableList.of<String?>("CargoMid"), false),
                    DefaultVertexFormats.ITEM,
                    spriteFunction
                ) as OBJModel.OBJBakedModel
                this.cargoRight = model.bake(
                    OBJModel.OBJState(ImmutableList.of<String?>("CargoRight"), false),
                    DefaultVertexFormats.ITEM,
                    spriteFunction
                ) as OBJModel.OBJBakedModel
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
    }

    init {
        this.shadowSize = 1.0f
    }

    override fun getEntityTexture(entity: EntityBuggy): ResourceLocation? {
        return TextureMap.LOCATION_BLOCKS_TEXTURE
    }

    override fun doRender(entity: EntityBuggy, x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float) {
        val pitch: Float = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks
        GlStateManager.disableRescaleNormal()
        GlStateManager.pushMatrix()
        GlStateManager.translate(x.toFloat(), y.toFloat(), z.toFloat())
        GlStateManager.scale(1.0f, 1.0f, 1.0f)
        GlStateManager.rotate(180.0f - entityYaw, 0.0f, 1.0f, 0.0f)
        GlStateManager.rotate(-pitch, 0.0f, 0.0f, 1.0f)
        GlStateManager.scale(0.41f, 0.41f, 0.41f)

        this.updateModels()
        this.bindEntityTexture(entity)

        if (Minecraft.isAmbientOcclusionEnabled()) {
            GlStateManager.shadeModel(GL11.GL_SMOOTH)
        } else {
            GlStateManager.shadeModel(GL11.GL_FLAT)
        }

        // Front wheels
        GlStateManager.pushMatrix()
        var dZ: Float = -2.727f
        val dY: Float = 0.976f
        var dX: Float = 1.25f
        val rotation: Float = entity.wheelRotationX
        GlStateManager.translate(dX, dY, dZ)
        GlStateManager.rotate(entity.wheelRotationZ, 0f, 1f, 0f)
        ClientUtil.drawBakedModel((this.wheelRightCover)!!)
        GlStateManager.rotate(rotation, 1f, 0f, 0f)
        ClientUtil.drawBakedModel((this.wheelRight)!!)
        GlStateManager.popMatrix()

        GlStateManager.pushMatrix()
        GlStateManager.translate(-dX, dY, dZ)
        GlStateManager.rotate(entity.wheelRotationZ, 0f, 1f, 0f)
        ClientUtil.drawBakedModel((this.wheelLeftCover)!!)
        GlStateManager.rotate(rotation, 1f, 0f, 0f)
        ClientUtil.drawBakedModel((this.wheelLeft)!!)
        GlStateManager.popMatrix()

        // Back wheels
        GlStateManager.pushMatrix()
        dX = 1.9f
        dZ = -dZ
        GlStateManager.translate(dX, dY, dZ)
        GlStateManager.rotate(-entity.wheelRotationZ, 0f, 1f, 0f)
        GlStateManager.rotate(rotation, 1f, 0f, 0f)
        ClientUtil.drawBakedModel((this.wheelRight)!!)
        GlStateManager.popMatrix()

        GlStateManager.pushMatrix()
        GlStateManager.translate(-dX, dY, dZ)
        GlStateManager.rotate(-entity.wheelRotationZ, 0f, 1f, 0f)
        GlStateManager.rotate(rotation, 1f, 0f, 0f)
        ClientUtil.drawBakedModel((this.wheelLeft)!!)
        GlStateManager.popMatrix()

        ClientUtil.drawBakedModel((this.mainModel)!!)

        // Radar Dish
        GlStateManager.pushMatrix()
        GlStateManager.translate(-1.178f, 4.1f, -2.397f)
        val ticks: Int = entity.ticksExisted + entity.getEntityId() * 10000
        GlStateManager.rotate(sin(ticks * 0.05).toFloat() * 50.0f, 1f, 0f, 0f)
        GlStateManager.rotate(cos(ticks * 0.1).toFloat() * 50.0f, 0f, 0f, 1f)
        ClientUtil.drawBakedModel((this.radarDish)!!)
        GlStateManager.popMatrix()

        if (entity.buggyType > 0) {
            ClientUtil.drawBakedModel((this.cargoLeft)!!)

            if (entity.buggyType > 1) {
                ClientUtil.drawBakedModel((this.cargoMid)!!)

                if (entity.buggyType > 2) {
                    ClientUtil.drawBakedModel((this.cargoRight)!!)
                }
            }
        }

        GlStateManager.popMatrix()
        RenderHelper.enableStandardItemLighting()
    }

    override fun shouldRender(buggy: EntityBuggy, camera: ICamera, camX: Double, camY: Double, camZ: Double): Boolean {
        val axisalignedbb: AxisAlignedBB = buggy.getEntityBoundingBox().grow(2.0, 1.0, 2.0)
        return buggy.isInRangeToRender3d(camX, camY, camZ) && camera.isBoundingBoxInFrustum(axisalignedbb)
    }
}
