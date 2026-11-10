package micdoodle8.mods.galacticraft.core.client.render.entities

import micdoodle8.mods.galacticraft.api.recipe.SchematicRegistry
import micdoodle8.mods.galacticraft.core.entities.EntityHangingSchematic
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.entity.Render
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.EntityHanging
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@SideOnly(Side.CLIENT)
open class RenderSchematic constructor(manager: RenderManager) : Render<EntityHangingSchematic>(manager) {
    override fun getEntityTexture(entity: EntityHangingSchematic): ResourceLocation? {
        return SchematicRegistry.getSchematicTexture(entity.schematic)
    }

    override fun doRender(
        entity: EntityHangingSchematic,
        x: Double,
        y: Double,
        z: Double,
        entityYaw: Float,
        partialTicks: Float
    ) {
        GlStateManager.pushMatrix()
        GlStateManager.translate(x, y, z)
        GlStateManager.rotate(180.0f - entityYaw, 0.0f, 1.0f, 0.0f)
        GlStateManager.enableRescaleNormal()
        this.bindEntityTexture(entity)
        val f: Float = 0.0625f
        GlStateManager.scale(f, f, f)
        this.renderPainting(entity, entity.getWidthPixels(), entity.getHeightPixels())
        GlStateManager.disableRescaleNormal()
        GlStateManager.popMatrix()
        super.doRender(entity, x, y, z, entityYaw, partialTicks)
    }

    private fun renderPainting(painting: EntityHangingSchematic, width: Int, height: Int) {
        val f: Float = (-width).toFloat() / 2.0f
        val f1: Float = (-height).toFloat() / 2.0f
        val f2: Float = 0.5f
        val f3: Float = 0.75f
        val f4: Float = 0.8125f
        val f5: Float = 0.0f
        val f6: Float = 0.0625f
        val f7: Float = 0.75f
        val f8: Float = 0.8125f
        val f9: Float = 0.001953125f
        val f10: Float = 0.001953125f
        val f11: Float = 0.7519531f
        val f12: Float = 0.7519531f
        val f13: Float = 0.0f
        val f14: Float = 0.0625f

        for (i: Int in 0..<width / 16) {
            for (j: Int in 0..<height / 16) {
                val a: Double = (f + ((i + 1) * 16).toFloat()).toDouble()
                val b: Double = (f + (i * 16).toFloat()).toDouble()
                val c: Double = (f1 + ((j + 1) * 16).toFloat()).toDouble()
                val d: Double = (f1 + (j * 16).toFloat()).toDouble()
                this.setLightmap(painting, (a + b) / 2.0f, (c + d) / 2.0f)
                val f19: Float = (width - i * 16).toFloat() / 32.0f
                val f20: Float = (width - (i + 1) * 16).toFloat() / 32.0f
                val f21: Float = (height - j * 16).toFloat() / 32.0f
                val f22: Float = (height - (j + 1) * 16).toFloat() / 32.0f
                val tessellator: Tessellator = Tessellator.getInstance()
                val worldrenderer: BufferBuilder = tessellator.getBuffer()
                worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL)
                worldrenderer.pos(a, d, (-f2).toDouble()).tex(f20.toDouble(), f21.toDouble()).normal(0.0f, 0.0f, -1.0f)
                    .endVertex()
                worldrenderer.pos(b, d, (-f2).toDouble()).tex(f19.toDouble(), f21.toDouble()).normal(0.0f, 0.0f, -1.0f)
                    .endVertex()
                worldrenderer.pos(b, c, (-f2).toDouble()).tex(f19.toDouble(), f22.toDouble()).normal(0.0f, 0.0f, -1.0f)
                    .endVertex()
                worldrenderer.pos(a, c, (-f2).toDouble()).tex(f20.toDouble(), f22.toDouble()).normal(0.0f, 0.0f, -1.0f)
                    .endVertex()
                worldrenderer.pos(a, c, f2.toDouble()).tex(f3.toDouble(), f5.toDouble()).normal(0.0f, 0.0f, 1.0f)
                    .endVertex()
                worldrenderer.pos(b, c, f2.toDouble()).tex(f4.toDouble(), f5.toDouble()).normal(0.0f, 0.0f, 1.0f)
                    .endVertex()
                worldrenderer.pos(b, d, f2.toDouble()).tex(f4.toDouble(), f6.toDouble()).normal(0.0f, 0.0f, 1.0f)
                    .endVertex()
                worldrenderer.pos(a, d, f2.toDouble()).tex(f3.toDouble(), f6.toDouble()).normal(0.0f, 0.0f, 1.0f)
                    .endVertex()
                worldrenderer.pos(a, c, (-f2).toDouble()).tex(f7.toDouble(), f9.toDouble()).normal(0.0f, 1.0f, 0.0f)
                    .endVertex()
                worldrenderer.pos(b, c, (-f2).toDouble()).tex(f8.toDouble(), f9.toDouble()).normal(0.0f, 1.0f, 0.0f)
                    .endVertex()
                worldrenderer.pos(b, c, f2.toDouble()).tex(f8.toDouble(), f10.toDouble()).normal(0.0f, 1.0f, 0.0f)
                    .endVertex()
                worldrenderer.pos(a, c, f2.toDouble()).tex(f7.toDouble(), f10.toDouble()).normal(0.0f, 1.0f, 0.0f)
                    .endVertex()
                worldrenderer.pos(a, d, f2.toDouble()).tex(f7.toDouble(), f9.toDouble()).normal(0.0f, -1.0f, 0.0f)
                    .endVertex()
                worldrenderer.pos(b, d, f2.toDouble()).tex(f8.toDouble(), f9.toDouble()).normal(0.0f, -1.0f, 0.0f)
                    .endVertex()
                worldrenderer.pos(b, d, (-f2).toDouble()).tex(f8.toDouble(), f10.toDouble()).normal(0.0f, -1.0f, 0.0f)
                    .endVertex()
                worldrenderer.pos(a, d, (-f2).toDouble()).tex(f7.toDouble(), f10.toDouble()).normal(0.0f, -1.0f, 0.0f)
                    .endVertex()
                worldrenderer.pos(a, c, f2.toDouble()).tex(f12.toDouble(), f13.toDouble()).normal(-1.0f, 0.0f, 0.0f)
                    .endVertex()
                worldrenderer.pos(a, d, f2.toDouble()).tex(f12.toDouble(), f14.toDouble()).normal(-1.0f, 0.0f, 0.0f)
                    .endVertex()
                worldrenderer.pos(a, d, (-f2).toDouble()).tex(f11.toDouble(), f14.toDouble()).normal(-1.0f, 0.0f, 0.0f)
                    .endVertex()
                worldrenderer.pos(a, c, (-f2).toDouble()).tex(f11.toDouble(), f13.toDouble()).normal(-1.0f, 0.0f, 0.0f)
                    .endVertex()
                worldrenderer.pos(b, c, (-f2).toDouble()).tex(f12.toDouble(), f13.toDouble()).normal(1.0f, 0.0f, 0.0f)
                    .endVertex()
                worldrenderer.pos(b, d, (-f2).toDouble()).tex(f12.toDouble(), f14.toDouble()).normal(1.0f, 0.0f, 0.0f)
                    .endVertex()
                worldrenderer.pos(b, d, f2.toDouble()).tex(f11.toDouble(), f14.toDouble()).normal(1.0f, 0.0f, 0.0f)
                    .endVertex()
                worldrenderer.pos(b, c, f2.toDouble()).tex(f11.toDouble(), f13.toDouble()).normal(1.0f, 0.0f, 0.0f)
                    .endVertex()
                tessellator.draw()
            }
        }
    }

    private fun setLightmap(painting: EntityHanging, p_77008_2_: Double, p_77008_3_: Double) {
        var i: Int = MathHelper.floor(painting.posX)
        val j: Int = MathHelper.floor(painting.posY + (p_77008_3_ / 16.0f))
        var k: Int = MathHelper.floor(painting.posZ)
        val enumfacing: EnumFacing? = painting.facingDirection

        if (enumfacing == EnumFacing.NORTH) {
            i = MathHelper.floor(painting.posX + (p_77008_2_ / 16.0f))
        }

        if (enumfacing == EnumFacing.WEST) {
            k = MathHelper.floor(painting.posZ - (p_77008_2_ / 16.0f))
        }

        if (enumfacing == EnumFacing.SOUTH) {
            i = MathHelper.floor(painting.posX - (p_77008_2_ / 16.0f))
        }

        if (enumfacing == EnumFacing.EAST) {
            k = MathHelper.floor(painting.posZ + (p_77008_2_ / 16.0f))
        }

        val l: Int = this.renderManager.world.getCombinedLight(BlockPos(i, j, k), 0)
        val i1: Int = l % 65536
        val j1: Int = l / 65536
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, i1.toFloat(), j1.toFloat())
        GlStateManager.color(1.0f, 1.0f, 1.0f)
    }
}
