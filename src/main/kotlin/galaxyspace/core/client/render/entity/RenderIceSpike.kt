package galaxyspace.core.client.render.entity

import galaxyspace.GalaxySpace
import galaxyspace.core.prefab.entities.EntityIceSpike
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.entity.Render
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.util.Mth as MathHelper

open class RenderIceSpike constructor(renderManager: RenderManager) : Render<EntityIceSpike>(renderManager) {
    override fun getEntityTexture(entity: EntityIceSpike): ResourceLocation? {
        return null
    }

    override fun doRender(
        entity: EntityIceSpike,
        x: Double,
        y: Double,
        z: Double,
        entityYaw: Float,
        partialTicks: Float
    ) {
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 0f, 240f)


        //this.bindEntityTexture(entity);
        this.renderManager.renderEngine.bindTexture(ballTextures)
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        GlStateManager.pushMatrix()
        GlStateManager.disableLighting()
        GlStateManager.translate(x.toFloat(), y.toFloat(), z.toFloat())
        GlStateManager.rotate(
            entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks - 90.0f,
            0.0f,
            1.0f,
            0.0f
        )
        GlStateManager.rotate(
            entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks,
            0.0f,
            0.0f,
            1.0f
        )
        val tessellator: Tessellator = Tessellator.getInstance()
        val bufferbuilder: BufferBuilder = tessellator.getBuffer()
        val i: Int = 0
        val f: Float = 0.0f
        val f1: Float = 0.5f
        val f2: Float = 0.0f
        val f3: Float = 0.15625f
        val f4: Float = 0.0f
        val f5: Float = 0.15625f
        val f6: Float = 0.15625f
        val f7: Float = 0.3125f
        val f8: Float = 0.05625f
        GlStateManager.enableRescaleNormal()
        val f9: Float = 0 - partialTicks

        if (f9 > 0.0f) {
            val f10: Float = -MathHelper.sin(f9 * 3.0f) * f9
            GlStateManager.rotate(f10, 0.0f, 0.0f, 1.0f)
        }

        GlStateManager.rotate(45.0f, 1.0f, 0.0f, 0.0f)
        GlStateManager.scale(0.05625f, 0.05625f, 0.05625f)
        GlStateManager.translate(-4.0f, 0.0f, 0.0f)

        if (this.renderOutlines) {
            GlStateManager.enableColorMaterial()
            GlStateManager.enableOutlineMode(this.getTeamColor(entity))
        }

        GlStateManager.glNormal3f(0.05625f, 0.0f, 0.0f)
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX)
        bufferbuilder.pos(-7.0, -2.0, -2.0).tex(0.0, 0.15625).endVertex()
        bufferbuilder.pos(-7.0, -2.0, 2.0).tex(0.15625, 0.15625).endVertex()
        bufferbuilder.pos(-7.0, 2.0, 2.0).tex(0.15625, 0.3125).endVertex()
        bufferbuilder.pos(-7.0, 2.0, -2.0).tex(0.0, 0.3125).endVertex()
        tessellator.draw()
        GlStateManager.glNormal3f(-0.05625f, 0.0f, 0.0f)
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX)
        bufferbuilder.pos(-7.0, 2.0, -2.0).tex(0.0, 0.15625).endVertex()
        bufferbuilder.pos(-7.0, 2.0, 2.0).tex(0.15625, 0.15625).endVertex()
        bufferbuilder.pos(-7.0, -2.0, 2.0).tex(0.15625, 0.3125).endVertex()
        bufferbuilder.pos(-7.0, -2.0, -2.0).tex(0.0, 0.3125).endVertex()
        tessellator.draw()

        for (j: Int in 0..3) {
            GlStateManager.rotate(90.0f, 1.0f, 0.0f, 0.0f)
            GlStateManager.glNormal3f(0.0f, 0.0f, 0.05625f)
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX)
            bufferbuilder.pos(-8.0, -2.0, 0.0).tex(0.0, 0.0).endVertex()
            bufferbuilder.pos(8.0, -2.0, 0.0).tex(0.5, 0.0).endVertex()
            bufferbuilder.pos(8.0, 2.0, 0.0).tex(0.5, 0.15625).endVertex()
            bufferbuilder.pos(-8.0, 2.0, 0.0).tex(0.0, 0.15625).endVertex()
            tessellator.draw()
        }

        if (this.renderOutlines) {
            GlStateManager.disableOutlineMode()
            GlStateManager.disableColorMaterial()
        }

        GlStateManager.disableRescaleNormal()
        GlStateManager.enableLighting()
        GlStateManager.popMatrix()
        super.doRender(entity, x, y, z, entityYaw, partialTicks)
    }

    companion object {
        private val ballTextures =
            ResourceLocation.parse(GalaxySpace.ASSET_PREFIX + ":" + "textures/model/particles/ice_spike.png")
    }
}
