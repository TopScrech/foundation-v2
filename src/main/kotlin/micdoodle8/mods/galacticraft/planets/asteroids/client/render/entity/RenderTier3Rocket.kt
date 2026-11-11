package micdoodle8.mods.galacticraft.planets.asteroids.client.render.entity

import com.google.common.base.Function
import com.google.common.collect.ImmutableList
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.client.model.OBJLoaderGC
import micdoodle8.mods.galacticraft.core.util.ClientUtil
import micdoodle8.mods.galacticraft.core.util.ColorUtil
import micdoodle8.mods.galacticraft.core.util.PlayerUtil
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import micdoodle8.mods.galacticraft.planets.asteroids.entities.EntityTier3Rocket
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

@OnlyIn(Dist.CLIENT)
open class RenderTier3Rocket constructor(manager: RenderManager) : Render<EntityTier3Rocket>(manager) {
    private var rocketModel: OBJModel.OBJBakedModel? = null
    private var coneModel: OBJModel.OBJBakedModel? = null
    private var cubeModel: OBJModel.OBJBakedModel? = null

    init {
        this.shadowSize = 2f
    }

    private fun updateModel() {
        if (this.rocketModel == null) {
            try {
                val model: IModel = OBJLoaderGC.instance.loadModel(
                    ResourceLocation(
                        GalacticraftPlanets.ASSET_PREFIX,
                        "tier3rocket.obj"
                    )
                )
                val spriteFunction: Function<ResourceLocation?, TextureAtlasSprite?> =
                    Function { location: ResourceLocation? ->
                        Minecraft.getInstance().getTextureMapBlocks().getAtlasSprite(location.toString())
                    }
                this.rocketModel = model.bake(
                    OBJModel.OBJState(ImmutableList.of<String?>("Boosters", "Rocket"), false),
                    DefaultVertexFormats.ITEM,
                    spriteFunction
                ) as OBJModel.OBJBakedModel
                this.coneModel = model.bake(
                    OBJModel.OBJState(ImmutableList.of<String?>("NoseCone"), false),
                    DefaultVertexFormats.ITEM,
                    spriteFunction
                ) as OBJModel.OBJBakedModel
                this.cubeModel = model.bake(
                    OBJModel.OBJState(ImmutableList.of<String?>("Cube"), false),
                    DefaultVertexFormats.ITEM,
                    spriteFunction
                ) as OBJModel.OBJBakedModel
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
            //            Function<ResourceLocation, TextureAtlasSprite> textureGetter = new Function<ResourceLocation, TextureAtlasSprite>()
//            {
//                @Override
//                public TextureAtlasSprite apply(ResourceLocation input)
//                {
//                    return Minecraft.getInstance().getTextureMapBlocks().getAtlasSprite(input.toString());
//                }
//            };
//
//            ModelResourceLocation modelResourceLocation = new ModelResourceLocation(GalacticraftPlanets.TEXTURE_PREFIX + "rocket_t3", "inventory");
//            rocketModel = (ItemModelRocketT3) Minecraft.getInstance().getRenderItem().getItemModelMesher().getModelManager().getModel(modelResourceLocation);
        }
    }

    override fun getEntityTexture(entity: EntityTier3Rocket): ResourceLocation? {
        return TextureMap.LOCATION_BLOCKS_TEXTURE
    }

    override fun doRender(
        entity: EntityTier3Rocket,
        x: Double,
        y: Double,
        z: Double,
        entityYaw: Float,
        partialTicks: Float
    ) {
        val pitch: Float =
            entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks + 180
        GlStateManager.disableRescaleNormal()
        GlStateManager.pushMatrix()
        GlStateManager.translate(x.toFloat(), y.toFloat(), z.toFloat())
        GlStateManager.rotate(180.0f - entityYaw, 0.0f, 1.0f, 0.0f)
        GlStateManager.rotate(-pitch, 0.0f, 0.0f, 1.0f)
        GlStateManager.translate(0.0f, entity.getRenderOffsetY(), 0.0f)
        val rollAmplitude: Float = entity.rollAmplitude / 3 - partialTicks

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

        val teamColor: Vector3? =
            ClientUtil.updateTeamColor(PlayerUtil.getName(Minecraft.getInstance().player), true)

        if (teamColor != null) {
            val color: Int = ColorUtil.to32BitColor(
                255,
                (teamColor.floatZ() * 255).toInt(),
                (teamColor.floatY() * 255).toInt(),
                (teamColor.floatX() * 255).toInt()
            )
            GlStateManager.disableTexture2D()
            ClientUtil.drawBakedModelColored((coneModel)!!, color)
        } else {
            ClientUtil.drawBakedModel((coneModel)!!)
            GlStateManager.disableTexture2D()
        }

        GlStateManager.disableLighting()

        val red: Boolean = Minecraft.getInstance().player.tickCount / 10 % 2 < 1
        val color: Int = ColorUtil.to32BitColor(255, 0, if (red) 0 else 255, if (red) 255 else 0)
        ClientUtil.drawBakedModelColored((cubeModel)!!, color)

        GlStateManager.enableTexture2D()
        GlStateManager.enableLighting()

        GlStateManager.color(1f, 1f, 1f)
        GlStateManager.popMatrix()
        RenderHelper.enableStandardItemLighting()
    }

    override fun shouldRender(
        rocket: EntityTier3Rocket,
        camera: ICamera,
        camX: Double,
        camY: Double,
        camZ: Double
    ): Boolean {
        val axisalignedbb: AxisAlignedBB = rocket.getEntityBoundingBox().grow(0.5, 0.0, 0.5)
        return rocket.isInRangeToRender3d(camX, camY, camZ) && camera.isBoundingBoxInFrustum(axisalignedbb)
    }
}
