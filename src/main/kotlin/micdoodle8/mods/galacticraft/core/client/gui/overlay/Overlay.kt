package micdoodle8.mods.galacticraft.core.client.gui.overlay

import micdoodle8.mods.galacticraft.core.entities.EntityTier1Rocket
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.player.EntityPlayer
import org.lwjgl.opengl.GL11
import kotlin.math.floor

open class Overlay {
    companion object {
        /**
         * Get the player's spaceship height off ground
         *
         * @param player thePlayer
         * @return position of player's spaceship
         */
        @JvmStatic
        protected fun getPlayerPositionY(player: EntityPlayer): Int {
            if (player.getRidingEntity() != null && player.getRidingEntity() is EntityTier1Rocket) {
                return floor((player.getRidingEntity() as EntityTier1Rocket).posY).toInt()
            }

            return floor(player.posY).toInt()
        }

        /**
         * Draw a textured rectangle at the specified position
         *
         * @param par1 xpos
         * @param par2 ypos
         * @param par3 u
         * @param par4 v
         * @param par5 width
         * @param par6 height
         */
        @JvmStatic
        protected fun drawTexturedModalRect(par1: Int, par2: Int, par3: Int, par4: Int, par5: Int, par6: Int) {
            val var7: Float = 0.00390625f
            val var8: Float = 0.00390625f
            val tess: Tessellator = Tessellator.getInstance()
            val worldRenderer: BufferBuilder = tess.getBuffer()
            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
            worldRenderer.pos((par1 + 0).toDouble(), (par2 + par6).toDouble(), 0.0)
                .tex(((par3 + 0) * var7).toDouble(), ((par4 + par6) * var8).toDouble()).endVertex()
            worldRenderer.pos((par1 + par5).toDouble(), (par2 + par6).toDouble(), 0.0)
                .tex(((par3 + par5) * var7).toDouble(), ((par4 + par6) * var8).toDouble()).endVertex()
            worldRenderer.pos((par1 + par5).toDouble(), (par2 + 0).toDouble(), 0.0)
                .tex(((par3 + par5) * var7).toDouble(), ((par4 + 0) * var8).toDouble()).endVertex()
            worldRenderer.pos((par1 + 0).toDouble(), (par2 + 0).toDouble(), 0.0)
                .tex(((par3 + 0) * var7).toDouble(), ((par4 + 0) * var8).toDouble()).endVertex()
            tess.draw()
        }

        /**
         * Draws a rectangle with middle at point specified
         *
         * @param var1 x
         * @param var3 y
         * @param var5 depth
         * @param var7 width
         * @param var9 height
         */
        @JvmStatic
        protected fun drawCenteringRectangle(var1: Double, var3: Double, var5: Double, var7: Double, var9: Double) {
            var var7: Double = var7
            var var9: Double = var9
            var7 *= 0.5
            var9 *= 0.5
            val tess: Tessellator = Tessellator.getInstance()
            val worldRenderer: BufferBuilder = tess.getBuffer()
            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
            worldRenderer.pos(var1 - var7, var3 + var9, var5).tex(0.0, 1.0).endVertex()
            worldRenderer.pos(var1 + var7, var3 + var9, var5).tex(1.0, 1.0).endVertex()
            worldRenderer.pos(var1 + var7, var3 - var9, var5).tex(1.0, 0.0).endVertex()
            worldRenderer.pos(var1 - var7, var3 - var9, var5).tex(0.0, 0.0).endVertex()
            tess.draw()
        }
    }
}
