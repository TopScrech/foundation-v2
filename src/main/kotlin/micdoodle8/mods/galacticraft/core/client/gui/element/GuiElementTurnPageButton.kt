package micdoodle8.mods.galacticraft.core.client.gui.element

import micdoodle8.mods.galacticraft.core.Constants
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import org.lwjgl.opengl.GL11

@OnlyIn(Dist.CLIENT)
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
        private val background = ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/gui/bookleft.png")
    }
}
