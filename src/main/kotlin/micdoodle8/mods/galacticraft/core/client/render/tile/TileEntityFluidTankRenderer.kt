package micdoodle8.mods.galacticraft.core.client.render.tile

import micdoodle8.mods.galacticraft.core.tile.TileEntityFluidTank
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.core.Direction as EnumFacing
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidTankInfo
import org.lwjgl.opengl.GL11
import kotlin.math.min

open class TileEntityFluidTankRenderer : TileEntitySpecialRenderer<TileEntityFluidTank>() {
    override fun render(
        tank: TileEntityFluidTank,
        x: Double,
        y: Double,
        z: Double,
        partialTicks: Float,
        destroyStage: Int,
        alpha: Float
    ) {
        val info: Array<FluidTankInfo?> = tank.getTankInfo(EnumFacing.DOWN)

        if (info.size != 1) {
            return
        }
        val tankFluid: FluidStack? = info[0]!!.fluid
        if (tankFluid == null || tankFluid.getFluid() == null || (!tankFluid.getFluid()
                .isGaseous() && tankFluid.amount == 0)
        ) {
            return
        }

        val tankAbove: TileEntityFluidTank? = tank.getNextTank(tank.getPos())
        val tankBelow: TileEntityFluidTank? = tank.getPreviousTank(tank.getPos())

        this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
        val sprite: TextureAtlasSprite =
            Minecraft.getInstance().getTextureMapBlocks().getAtlasSprite(tankFluid.getFluid().getStill().toString())
        val uMin: Double = sprite.getMinU().toDouble()
        val uMax: Double = sprite.getMaxU().toDouble()
        val vMin: Double = sprite.getMinV().toDouble()
        val vMax: Double = sprite.getMaxV().toDouble()
        GL11.glPushMatrix()
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        GL11.glTranslatef(x.toFloat(), y.toFloat() + 1.5f, z.toFloat() + 1.0f)
        GL11.glScalef(1.0f, -1.0f, -1.0f)
        GL11.glTranslatef(0.5f, 0.5f, 0.5f)

        GlStateManager.disableLighting()
        GlStateManager.enableBlend()
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        val tess: Tessellator = Tessellator.getInstance()
        val worldRenderer: BufferBuilder = tess.getBuffer()

        var level: Float = 1.0f
        var levelInv: Float = 0.0f
        var opacity: Float = 1.0f

        val compositeGaseous: Boolean = tankFluid.getFluid().isGaseous()

        if (compositeGaseous) {
            opacity = min(tankFluid.amount / info[0]!!.capacity.toFloat() * 0.8f + 0.2f, 1f)
        } else {
            level = (tank.fluidTank)!!.getFluidAmount() / 16400.0f
            if (level <= 0.012f) {
                levelInv = 1.0f //Empty tanks render empty - see #3222
            } else {
                levelInv =
                    0.988f - level //1.2% inset from each end of the tank, to avoid z-fighting with blocks above/below
            }
        }

        GL11.glColor4f(1.0f, 1.0f, 1.0f, opacity)

        if (levelInv < 1.0f) {
            // North
            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
            worldRenderer.pos(-0.4, levelInv.toDouble(), -0.399).tex(uMin, vMin).endVertex()
            worldRenderer.pos(-0.4, 1.0, -0.399).tex(uMin, vMin + (vMax - vMin) * level).endVertex()
            worldRenderer.pos(0.4, 1.0, -0.399).tex(uMax, vMin + (vMax - vMin) * level).endVertex()
            worldRenderer.pos(0.4, levelInv.toDouble(), -0.399).tex(uMax, vMin).endVertex()
            tess.draw()

            // South
            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
            worldRenderer.pos(0.4, levelInv.toDouble(), 0.399).tex(uMax, vMin).endVertex()
            worldRenderer.pos(0.4, 1.0, 0.399).tex(uMax, vMin + (vMax - vMin) * level).endVertex()
            worldRenderer.pos(-0.4, 1.0, 0.399).tex(uMin, vMin + (vMax - vMin) * level).endVertex()
            worldRenderer.pos(-0.4, levelInv.toDouble(), 0.399).tex(uMin, vMin).endVertex()
            tess.draw()

            // West
            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
            worldRenderer.pos(-0.399, 1.0, -0.4).tex(uMin, vMin + (vMax - vMin) * level).endVertex()
            worldRenderer.pos(-0.399, levelInv.toDouble(), -0.4).tex(uMin, vMin).endVertex()
            worldRenderer.pos(-0.399, levelInv.toDouble(), 0.4).tex(uMax, vMin).endVertex()
            worldRenderer.pos(-0.399, 1.0, 0.4).tex(uMax, vMin + (vMax - vMin) * level).endVertex()
            tess.draw()

            // East
            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
            worldRenderer.pos(0.399, 1.0, 0.4).tex(uMax, vMin + (vMax - vMin) * level).endVertex()
            worldRenderer.pos(0.399, levelInv.toDouble(), 0.4).tex(uMax, vMin).endVertex()
            worldRenderer.pos(0.399, levelInv.toDouble(), -0.4).tex(uMin, vMin).endVertex()
            worldRenderer.pos(0.399, 1.0, -0.4).tex(uMin, vMin + (vMax - vMin) * level).endVertex()
            tess.draw()

            if (tankAbove == null || ((tankAbove.fluidTank)!!.getFluidAmount() == 0 && !compositeGaseous)) {
                worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
                worldRenderer.pos(0.4, 0.01 + levelInv, 0.4).tex(uMax, vMax).endVertex()
                worldRenderer.pos(-0.4, 0.01 + levelInv, 0.4).tex(uMax, vMin).endVertex()
                worldRenderer.pos(-0.4, 0.01 + levelInv, -0.4).tex(uMin, vMin).endVertex()
                worldRenderer.pos(0.4, 0.01 + levelInv, -0.4).tex(uMin, vMax).endVertex()
                tess.draw()
            }

            if (tankBelow == null || ((tankBelow.fluidTank)!!.getFluidAmount() == 0 && !compositeGaseous)) {
                worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
                worldRenderer.pos(0.4, 0.99, 0.4).tex(uMax, vMax).endVertex()
                worldRenderer.pos(0.4, 0.99, -0.4).tex(uMin, vMax).endVertex()
                worldRenderer.pos(-0.4, 0.99, -0.4).tex(uMin, vMin).endVertex()
                worldRenderer.pos(-0.4, 0.99, 0.4).tex(uMax, vMin).endVertex()
                tess.draw()
            }
        }

        GlStateManager.enableLighting()
        GlStateManager.disableBlend()

        GL11.glPopMatrix()
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
    }
}
