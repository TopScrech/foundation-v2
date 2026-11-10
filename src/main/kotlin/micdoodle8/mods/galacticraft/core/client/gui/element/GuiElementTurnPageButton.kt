package micdoodle8.mods.galacticraft.core.client.gui.element

import micdoodle8.mods.galacticraft.core.Constants
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11

@SideOnly(Side.CLIENT)
open class GuiElementTurnPageButton constructor(par1: Int, par2: Int, par3: Int, private val nextPage: Boolean) :
    GuiButton(par1, par2, par3, 23, 13, "") {
    override fun drawButton(par1Minecraft: Minecraft, par2: Int, par3: Int, partial: Float) {
        if (this.visible) {
            val var4: Boolean =
                par2 >= this.x && par3 >= this.y && par2 < this.x + this.width && par3 < this.y + this.height
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
            par1Minecraft.getTextureManager().bindTexture(background)
            var var5: Int = 0
            var var6: Int = 192

            if (var4) {
                var5 += 23
            }

            if (!this.nextPage) {
                var6 += 13
            }

            this.drawTexturedModalRect(this.x, this.y, var5, var6, 23, 13)
        }
    }

    companion object {
        private val background = ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/bookleft.png")
    }
}
