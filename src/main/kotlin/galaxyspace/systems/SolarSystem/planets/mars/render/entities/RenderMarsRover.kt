package galaxyspace.systems.SolarSystem.planets.mars.render.entities

import galaxyspace.GalaxySpace
import org.lwjgl.opengl.GL11
import galaxyspace.systems.SolarSystem.planets.mars.entities.EntityMarsRover
import com.google.common.collect.ImmutableList
import com.google.common.base.Function
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
import net.minecraftforge.client.model.IModel
import net.minecraftforge.client.model.obj.OBJModel
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.lang.Exception
import java.lang.RuntimeException

@OnlyIn(Dist.CLIENT)
open class RenderMarsRover constructor(manager: RenderManager) : Render<EntityMarsRover>(manager) {
    private var mainModel: OBJModel.OBJBakedModel? = null
    private var fronbase: OBJModel.OBJBakedModel? = null
    private var backbase: OBJModel.OBJBakedModel? = null
    private var ltr: OBJModel.OBJBakedModel? = null
    private var ltl: OBJModel.OBJBakedModel? = null
    private var ltr1: OBJModel.OBJBakedModel? = null
    private var ltl1: OBJModel.OBJBakedModel? = null
    private var ant: OBJModel.OBJBakedModel? = null
    private var solarBaseR: OBJModel.OBJBakedModel? = null
    private var solarR: OBJModel.OBJBakedModel? = null
    private var solarBaseL: OBJModel.OBJBakedModel? = null
    private var solarL: OBJModel.OBJBakedModel? = null
    private var antBase: OBJModel.OBJBakedModel? = null
    private val radarDish: OBJModel.OBJBakedModel? = null
    private var wheelRight: OBJModel.OBJBakedModel? = null
    private var wheelLeft: OBJModel.OBJBakedModel? = null
    private val cargoLeft: OBJModel.OBJBakedModel? = null
    private val cargoMid: OBJModel.OBJBakedModel? = null
    private val cargoRight: OBJModel.OBJBakedModel? = null

    private var antRotate = 0.0f
    private fun updateModels() {
        if (this.mainModel == null) {
            try {
                val model: IModel =
                    OBJLoaderGC.instance.loadModel(ResourceLocation.fromNamespaceAndPath(GalaxySpace.ASSET_PREFIX, "mars_rover.obj"))
                val spriteFunction: Function<ResourceLocation?, TextureAtlasSprite?> =
                    Function { location: ResourceLocation? ->
                        Minecraft.getInstance().getTextureMapBlocks().getAtlasSprite(location.toString())
                    }

                this.mainModel = model.bake(
                    OBJModel.OBJState(ImmutableList.of<String?>("base"), false),
                    DefaultVertexFormats.ITEM,
                    spriteFunction
                ) as OBJModel.OBJBakedModel
                this.fronbase = model.bake(
                    OBJModel.OBJState(ImmutableList.of<String?>("frontbase"), false),
                    DefaultVertexFormats.ITEM,
                    spriteFunction
                ) as OBJModel.OBJBakedModel
                this.backbase = model.bake(
                    OBJModel.OBJState(ImmutableList.of<String?>("backbase"), false),
                    DefaultVertexFormats.ITEM,
                    spriteFunction
                ) as OBJModel.OBJBakedModel
                this.ltr = model.bake(
                    OBJModel.OBJState(ImmutableList.of<String?>("ltR"), false),
                    DefaultVertexFormats.ITEM,
                    spriteFunction
                ) as OBJModel.OBJBakedModel
                this.ltr1 = model.bake(
                    OBJModel.OBJState(ImmutableList.of<String?>("ltR.001"), false),
                    DefaultVertexFormats.ITEM,
                    spriteFunction
                ) as OBJModel.OBJBakedModel
                this.ltl = model.bake(
                    OBJModel.OBJState(ImmutableList.of<String?>("ltL"), false),
                    DefaultVertexFormats.ITEM,
                    spriteFunction
                ) as OBJModel.OBJBakedModel
                this.ltl1 = model.bake(
                    OBJModel.OBJState(ImmutableList.of<String?>("ltL.001"), false),
                    DefaultVertexFormats.ITEM,
                    spriteFunction
                ) as OBJModel.OBJBakedModel
                this.wheelRight = model.bake(
                    OBJModel.OBJState(ImmutableList.of<String?>("frontWheelR"), false),
                    DefaultVertexFormats.ITEM,
                    spriteFunction
                ) as OBJModel.OBJBakedModel
                this.wheelLeft = model.bake(
                    OBJModel.OBJState(ImmutableList.of<String?>("frontWheelL"), false),
                    DefaultVertexFormats.ITEM,
                    spriteFunction
                ) as OBJModel.OBJBakedModel
                this.antBase = model.bake(
                    OBJModel.OBJState(ImmutableList.of<String?>("antBase"), false),
                    DefaultVertexFormats.ITEM,
                    spriteFunction
                ) as OBJModel.OBJBakedModel
                this.ant = model.bake(
                    OBJModel.OBJState(ImmutableList.of<String?>("ant"), false),
                    DefaultVertexFormats.ITEM,
                    spriteFunction
                ) as OBJModel.OBJBakedModel
                this.solarBaseR = model.bake(
                    OBJModel.OBJState(ImmutableList.of<String?>("solarBaseR"), false),
                    DefaultVertexFormats.ITEM,
                    spriteFunction
                ) as OBJModel.OBJBakedModel
                this.solarBaseL = model.bake(
                    OBJModel.OBJState(ImmutableList.of<String?>("solarBaseL"), false),
                    DefaultVertexFormats.ITEM,
                    spriteFunction
                ) as OBJModel.OBJBakedModel
                this.solarR = model.bake(
                    OBJModel.OBJState(ImmutableList.of<String?>("solarR"), false),
                    DefaultVertexFormats.ITEM,
                    spriteFunction
                ) as OBJModel.OBJBakedModel
                this.solarL = model.bake(
                    OBJModel.OBJState(ImmutableList.of<String?>("solarL"), false),
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

    override fun getEntityTexture(entity: EntityMarsRover): ResourceLocation? {
        return TextureMap.LOCATION_BLOCKS_TEXTURE
    }

    override fun doRender(
        entity: EntityMarsRover,
        x: Double,
        y: Double,
        z: Double,
        entityYaw: Float,
        partialTicks: Float
    ) {
        val pitch: Float = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks
        val xrotation: Float =
            0f //entity.lastTickPosY - 0.4F > entity.posY ? -15F : entity.lastTickPosY - 0.4F < entity.posY ? 15F : 0F;
        GlStateManager.disableRescaleNormal()
        GlStateManager.pushMatrix()
        GlStateManager.translate(x, y, z)
        GlStateManager.scale(1.0f, 1.0f, 1.0f)
        GlStateManager.rotate(180.0f - entityYaw, 0.0f, 1.0f, 0.0f)
        GlStateManager.rotate(-pitch, 0.0f, 0.0f, 1.0f)
        GlStateManager.rotate(xrotation, 1.0f, 0.0f, 0.0f)

        //GlStateManager.scale(0.41F, 0.41F, 0.41F);
        this.updateModels()
        this.bindEntityTexture(entity)

        if (Minecraft.isAmbientOcclusionEnabled()) {
            GlStateManager.shadeModel(GL11.GL_SMOOTH)
        } else {
            GlStateManager.shadeModel(GL11.GL_FLAT)
        }


        // GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        //GlStateManager.loadIdentity();
        // GlStateManager.translate(0, 4.5F, 0);
        //GlStateManager.scale(2.0F, 2.0F, 2.0F);
        GlStateManager.translate(-1.5f, -2f, 0f)
        ClientUtil.drawBakedModel((this.mainModel)!!)
        ClientUtil.drawBakedModel((this.ltl)!!)
        ClientUtil.drawBakedModel((this.ltl1)!!)
        ClientUtil.drawBakedModel((this.ltr)!!)
        ClientUtil.drawBakedModel((this.ltr1)!!)
        ClientUtil.drawBakedModel((this.antBase)!!)

        ClientUtil.drawBakedModel((this.solarBaseL)!!)
        ClientUtil.drawBakedModel((this.solarL)!!)
        //if((partialTicks * 360) % 100 == 0)
        antRotate++

        GlStateManager.pushMatrix()

        //GlStateManager.translate(0F, 0F, -1.05F);
        GlStateManager.rotate(-35f, 1f, 0f, 0f)

        //GlStateManager.rotate(antRotate % 30, 1, 1, 0); 
        GlStateManager.translate(0f, -0.75f, 4.35f)
        ClientUtil.drawBakedModel((this.ant)!!)
        GlStateManager.popMatrix()

        val rotation: Float = entity.wheelRotationX


        //Wheel Front Right
        GlStateManager.pushMatrix()
        GlStateManager.translate(0f, 2.7f, -1.75f)
        GlStateManager.rotate(entity.wheelRotationZ, 0f, 1f, 0f)
        GlStateManager.rotate(rotation, 1f, 0f, 0f)
        GlStateManager.translate(0f, -2.75f, 1.75f)
        ClientUtil.drawBakedModel((this.wheelRight)!!)
        GlStateManager.popMatrix()


        //Wheel Front Left
        GlStateManager.pushMatrix()
        GlStateManager.translate(0f, 2.7f, -1.75f)
        GlStateManager.rotate(entity.wheelRotationZ, 0f, 1f, 0f)
        GlStateManager.rotate(rotation, 1f, 0f, 0f)
        GlStateManager.translate(0f, -2.75f, 1.8f)
        ClientUtil.drawBakedModel((this.wheelLeft)!!)
        GlStateManager.popMatrix()


        //Wheel Front Base
        GlStateManager.pushMatrix()
        GlStateManager.translate(0f, 0f, 0f)
        GlStateManager.rotate(entity.wheelRotationZ, 0f, 1f, 0f)
        ClientUtil.drawBakedModel((this.fronbase)!!)
        GlStateManager.popMatrix()


        //Wheel Back Right
        GlStateManager.pushMatrix()
        GlStateManager.translate(0f, 2.7f, 1.9f)
        GlStateManager.rotate(rotation, 1f, 0f, 0f)
        GlStateManager.translate(0f, -2.75f, 1.75f)
        ClientUtil.drawBakedModel((this.wheelRight)!!)
        GlStateManager.popMatrix()


        //Wheel Back Left
        GlStateManager.pushMatrix()
        GlStateManager.translate(0f, 2.7f, 1.9f)
        GlStateManager.rotate(rotation, 1f, 0f, 0f)
        GlStateManager.translate(0f, -2.76f, 1.79f)
        ClientUtil.drawBakedModel((this.wheelLeft)!!)
        GlStateManager.popMatrix()

        GlStateManager.pushMatrix()
        ClientUtil.drawBakedModel((this.backbase)!!)
        GlStateManager.popMatrix()


        /*
        // Radar Dish
        GlStateManager.pushMatrix();
        GlStateManager.translate(-1.178F, 4.1F, -2.397F);
        int ticks = entity.ticksExisted + entity.getEntityId() * 10000;
        GlStateManager.rotate((float) Math.sin(ticks * 0.05) * 50.0F, 1, 0, 0);
        GlStateManager.rotate((float) Math.cos(ticks * 0.1) * 50.0F, 0, 0, 1);
        ClientUtil.drawBakedModel(this.radarDish);
        GlStateManager.popMatrix();

        if (entity.buggyType > 0)
        {
            ClientUtil.drawBakedModel(this.cargoLeft);

            if (entity.buggyType > 1)
            {
                ClientUtil.drawBakedModel(this.cargoMid);

                if (entity.buggyType > 2)
                {
                    ClientUtil.drawBakedModel(this.cargoRight);
                }
            }
        }*/
        GlStateManager.popMatrix()
        RenderHelper.enableStandardItemLighting()
    }

    override fun shouldRender(
        buggy: EntityMarsRover,
        camera: ICamera,
        camX: Double,
        camY: Double,
        camZ: Double
    ): Boolean {
        val axisalignedbb: AxisAlignedBB = buggy.getEntityBoundingBox().grow(2.0, 1.0, 2.0)
        return buggy.isInRangeToRender3d(camX, camY, camZ) && camera.isBoundingBoxInFrustum(axisalignedbb)
    }
}
