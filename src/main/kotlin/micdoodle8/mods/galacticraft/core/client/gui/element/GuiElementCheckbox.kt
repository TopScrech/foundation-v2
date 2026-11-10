package micdoodle8.mods.galacticraft.core.client.gui.element

import micdoodle8.mods.galacticraft.core.Constants
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11

open class GuiElementCheckbox constructor(
    id: Int,
    private val parentGui: ICheckBoxCallback,
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    private val texWidth: Int,
    private val texHeight: Int,
    private val texX: Int,
    private val texY: Int,
    text: String,
    private val textColor: Int,
    private val shiftOnHover: Boolean
) : GuiButton(id, x, y, width, height, text) {
    @JvmField
    var isSelected: Boolean? = null

    @JvmOverloads
    constructor(
        id: Int,
        parentGui: ICheckBoxCallback,
        x: Int,
        y: Int,
        text: String,
        textColor: Int = 4210752
    ) : this(id, parentGui, x, y, 13, 13, 20, 24, text, textColor)

    private constructor(
        id: Int,
        parentGui: ICheckBoxCallback,
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        texX: Int,
        texY: Int,
        text: String,
        textColor: Int
    ) : this(id, parentGui, x, y, width, height, width, height, texX, texY, text, textColor, true)

    override fun drawButton(par1Minecraft: Minecraft, par2: Int, par3: Int, partial: Float) {
        if (this.isSelected == null) {
            this.isSelected = this.parentGui.getInitiallySelected(this)
        }

        if (this.visible) {
            par1Minecraft.getTextureManager().bindTexture(texture)
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
            this.hovered = par2 >= this.x && par3 >= this.y && par2 < this.x + this.width && par3 < this.y + this.height
            this.drawTexturedModalRect(
                this.x,
                this.y,
                if ((this.isSelected)!!) this.texX + this.texWidth else this.texX,
                if (this.hovered) if (this.shiftOnHover) this.texY + this.texHeight else this.texY else this.texY,
                this.width,
                this.height
            )
            this.mouseDragged(par1Minecraft, par2, par3)
            par1Minecraft.fontRenderer.drawString(
                this.displayString,
                (this.x + this.width + 3).toFloat(),
                (this.y + (this.height - 6) / 2).toFloat(),
                this.textColor,
                false
            )
        }
    }

    override fun drawTexturedModalRect(par1: Int, par2: Int, par3: Int, par4: Int, par5: Int, par6: Int) {
        val f: Float = 0.00390625f
        val f1: Float = 0.00390625f
        val tessellator: Tessellator = Tessellator.getInstance()
        val worldRenderer: BufferBuilder = tessellator.getBuffer()
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        worldRenderer.pos((par1 + 0).toDouble(), (par2 + par6).toDouble(), this.zLevel.toDouble())
            .tex(((par3 + 0) * f).toDouble(), ((par4 + this.texHeight) * f1).toDouble()).endVertex()
        worldRenderer.pos((par1 + par5).toDouble(), (par2 + par6).toDouble(), this.zLevel.toDouble())
            .tex(((par3 + this.texWidth) * f).toDouble(), ((par4 + this.texHeight) * f1).toDouble()).endVertex()
        worldRenderer.pos((par1 + par5).toDouble(), (par2 + 0).toDouble(), this.zLevel.toDouble())
            .tex(((par3 + this.texWidth) * f).toDouble(), ((par4 + 0) * f1).toDouble()).endVertex()
        worldRenderer.pos((par1 + 0).toDouble(), (par2 + 0).toDouble(), this.zLevel.toDouble())
            .tex(((par3 + 0) * f).toDouble(), ((par4 + 0) * f1).toDouble()).endVertex()
        tessellator.draw()
    }

    override fun mousePressed(par1Minecraft: Minecraft, par2: Int, par3: Int): Boolean {
        if (this.enabled && this.visible && par2 >= this.x && par3 >= this.y && par2 < this.x + this.width && par3 < this.y + this.height) {
            if (this.parentGui.canPlayerEdit(this, par1Minecraft.player)) {
                this.isSelected = !this.isSelected!!
                this.parentGui.onSelectionChanged(this, this.isSelected!!)
                return true
            } else {
                this.parentGui.onIntruderInteraction()
            }
        }

        return false
    }

    interface ICheckBoxCallback {
        fun onSelectionChanged(checkbox: GuiElementCheckbox?, newSelected: Boolean)

        fun canPlayerEdit(checkbox: GuiElementCheckbox?, player: EntityPlayer?): Boolean

        fun getInitiallySelected(checkbox: GuiElementCheckbox?): Boolean

        fun onIntruderInteraction()
    }

    companion object {
        @JvmField
        protected val texture: ResourceLocation = ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/gui.png")
    }
}
