package micdoodle8.mods.galacticraft.core.client.gui.element

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.util.ColorUtil
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCLog
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11

open class GuiElementCheckboxPreLaunch private constructor(
    id: Int,
    private val parentGui: ICheckBoxCallback,
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    private val texX: Int,
    private val texY: Int,
    text: String,
    private val textColor: Int
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
    ) : this(id, parentGui, x, y, 9, 9, 194, 0, text, textColor)

    override fun drawButton(par1Minecraft: Minecraft, par2: Int, par3: Int, partial: Float) {
        if (this.isSelected == null) {
            this.isSelected = this.parentGui.getInitiallySelected(this)
        }

        if (this.visible) {
            par1Minecraft.getTextureManager().bindTexture(texture)
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
            this.hovered = par2 >= this.x && par3 >= this.y && par2 < this.x + this.width && par3 < this.y + this.height
            val texWidth: Int = if ((this.isSelected)!!) 12 else 9
            val texHeight: Int = if ((this.isSelected)!!) 16 else 9
            this.drawTexturedModalRect(
                this.x,
                if ((this.isSelected)!!) this.y - 7 else this.y,
                if (this.hovered) (this.texX + 12) else this.texX,
                if ((this.isSelected)!!) this.texY + 9 else this.texY,
                texWidth,
                texHeight
            )
            this.mouseDragged(par1Minecraft, par2, par3)
            par1Minecraft.fontRenderer.drawSplitString(
                EnumColor.BLACK.toString() + this.displayString,
                this.x + this.width + 3,
                this.y + (this.height - 6) / 2,
                100,
                ColorUtil.to32BitColor(255, 5, 5, 5)
            )
        }
    }

    override fun drawTexturedModalRect(par1: Int, par2: Int, par3: Int, par4: Int, par5: Int, par6: Int) {
        val f: Float = 0.00390625f
        val f1: Float = 0.00390625f
        val texWidth: Int = if ((this.isSelected)!!) 12 else 9
        val texHeight: Int = if ((this.isSelected)!!) 16 else 9
        val tessellator: Tessellator = Tessellator.getInstance()
        val worldRenderer: BufferBuilder = tessellator.getBuffer()
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        worldRenderer.pos((par1 + 0).toDouble(), (par2 + par6).toDouble(), this.zLevel.toDouble())
            .tex(((par3 + 0) * f).toDouble(), ((par4 + texHeight) * f1).toDouble()).endVertex()
        worldRenderer.pos((par1 + par5).toDouble(), (par2 + par6).toDouble(), this.zLevel.toDouble())
            .tex(((par3 + texWidth) * f).toDouble(), ((par4 + texHeight) * f1).toDouble()).endVertex()
        worldRenderer.pos((par1 + par5).toDouble(), (par2 + 0).toDouble(), this.zLevel.toDouble())
            .tex(((par3 + texWidth) * f).toDouble(), ((par4 + 0) * f1).toDouble()).endVertex()
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

    open fun willFit(max: Int): Int {
        val size: Int = Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(
            this.displayString,
            100
        ).size * Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT
        GCLog.debug(displayString + " " + size + " " + max)
        if (size > max) {
            return -1
        }
        return size
    }

    interface ICheckBoxCallback {
        fun onSelectionChanged(checkbox: GuiElementCheckboxPreLaunch?, newSelected: Boolean)

        fun canPlayerEdit(checkbox: GuiElementCheckboxPreLaunch?, player: EntityPlayer?): Boolean

        fun getInitiallySelected(checkbox: GuiElementCheckboxPreLaunch?): Boolean

        fun onIntruderInteraction()
    }

    companion object {
        @JvmField
        protected val texture: ResourceLocation =
            ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/checklist_book.png")
    }
}
