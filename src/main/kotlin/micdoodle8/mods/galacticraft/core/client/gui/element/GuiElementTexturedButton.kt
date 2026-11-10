package micdoodle8.mods.galacticraft.core.client.gui.element

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.GuiButton
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11

open class GuiElementTexturedButton constructor(
    par1: Int,
    par2: Int,
    par3: Int,
    par4: Int,
    par5: Int,
    private val texture: ResourceLocation,
    private val bWidth: Int,
    private val bHeight: Int
) : GuiButton(par1, par2, par3, par4, par5, "") {
    override fun drawButton(par1Minecraft: Minecraft, par2: Int, par3: Int, partial: Float) {
        if (this.visible) {
            val var4: FontRenderer = par1Minecraft.fontRenderer
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
            this.hovered = par2 >= this.x && par3 >= this.y && par2 < this.x + this.width && par3 < this.y + this.height
            this.getHoverState(this.hovered)
            par1Minecraft.renderEngine.bindTexture(this.texture)
            this.drawTexturedModalRect(this.x, this.y, 0, 0, this.bWidth, this.bHeight)
            this.mouseDragged(par1Minecraft, par2, par3)
            var var6: Int = 14737632

            if (!this.enabled) {
                var6 = -6250336
            } else if (this.hovered) {
                var6 = 16777120
            }

            this.drawCenteredString(
                var4,
                this.displayString,
                this.x + this.width / 2,
                this.y + (this.height - 8) / 2,
                var6
            )
        }
    }
}
