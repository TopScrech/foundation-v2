package micdoodle8.mods.galacticraft.planets.venus.client.render.entity

import org.lwjgl.opengl.GL11
import kotlin.math.pow
import com.google.common.collect.ImmutableList
import micdoodle8.mods.galacticraft.core.util.ClientUtil
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import micdoodle8.mods.galacticraft.planets.venus.client.model.ModelSpiderQueen
import micdoodle8.mods.galacticraft.planets.venus.entities.EntitySpiderQueen
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.entity.RenderLiving
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.lang.Exception
import java.lang.RuntimeException

@OnlyIn(Dist.CLIENT)
open class RenderSpiderQueen constructor(renderManager: RenderManager) :
    RenderLiving<EntitySpiderQueen>(renderManager, ModelSpiderQueen(), 1.0f) {
    private fun updateModels() {
        if (webModel == null) {
            try {
                webModel = ClientUtil.modelFromOBJ(
                    ResourceLocation.fromNamespaceAndPath(GalacticraftPlanets.ASSET_PREFIX, "web.obj"),
                    ImmutableList.of<String?>("Sphere")
                )
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
    }

    override fun preRenderCallback(entity: EntitySpiderQueen, partialTickTime: Float) {
        if (entity.getBurrowedCount() >= 0) {
            GL11.glRotatef(180.0f, 0.0f, 0.0f, 1.0f)
            GL11.glTranslatef(0.0f, entity.height, 0.0f)
        }
        GL11.glScalef(1.5f, 1.5f, 1.5f)
        GL11.glRotatef(
            (entity.deathTicks.toDouble().pow(2.0) / 5.0f + (entity.deathTicks.toDouble()
                .pow(2.0) / 5.0f - (entity.deathTicks - 1).toDouble().pow(2.0) / 5.0f) * partialTickTime).toFloat(),
            0.0f,
            1.0f,
            0.0f
        )
        super.preRenderCallback(entity, partialTickTime)
    }

    override fun doRender(
        entity: EntitySpiderQueen,
        x: Double,
        y: Double,
        z: Double,
        entityYaw: Float,
        partialTicks: Float
    ) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks)

        GL11.glPushMatrix()
        GL11.glTranslatef(x.toFloat(), y.toFloat() + 0.8f, z.toFloat())
        GL11.glScalef(1.4f, 1.5f, 1.4f)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

        this.updateModels()

        RenderHelper.disableStandardItemLighting()
        this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)

        if (Minecraft.isAmbientOcclusionEnabled()) {
            GlStateManager.shadeModel(GL11.GL_SMOOTH)
        } else {
            GlStateManager.shadeModel(GL11.GL_FLAT)
        }

        if (entity.getBurrowedCount() >= 0) {
            GL11.glDisable(GL11.GL_CULL_FACE)
            ClientUtil.drawBakedModel((webModel)!!)
            GL11.glScalef(1.05f, 1.1f, 1.05f)
            GL11.glRotatef(192.5f, 0.0f, 1.0f, 0.0f)
            ClientUtil.drawBakedModel((webModel)!!)
            GL11.glEnable(GL11.GL_CULL_FACE)
        }

        RenderHelper.enableStandardItemLighting()

        GL11.glPopMatrix()
    }

    override fun getEntityTexture(juicer: EntitySpiderQueen): ResourceLocation? {
        return spiderTexture
    }

    companion object {
        private val spiderTexture =
            ResourceLocation.fromNamespaceAndPath(GalacticraftPlanets.ASSET_PREFIX, "textures/model/spider_queen.png")

        @JvmField
        var webModel: IBakedModel? = null
    }
}
