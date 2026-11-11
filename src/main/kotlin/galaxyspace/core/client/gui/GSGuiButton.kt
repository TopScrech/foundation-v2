package galaxyspace.core.client.gui

import galaxyspace.GalaxySpace
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.sounds.SimpleSoundInstance as PositionedSoundRecord
import net.minecraft.client.sounds.SoundManager as SoundHandler
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.sounds.SoundEvents
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

@OnlyIn(Dist.CLIENT)
open class GSGuiButton constructor(id: Int, x: Int, y: Int, width: Int, height: Int, buttonText: String) :
    GuiButton(id, x, y, width, height, buttonText) {
    constructor(id: Int, x: Int, y: Int, title: String) : this(id, x, y, 200, 20, title)

    override fun getHoverState(mouseOver: Boolean): Int {
        var i: Int = 1

        if (!this.enabled) {
            i = 0
        } else if (mouseOver) {
            i = 2
        }

        return i
    }

    /**
     * Draws this button to the screen.
     */
    override fun drawButton(mc: Minecraft, mouseX: Int, mouseY: Int, partialTicks: Float) {
        if (this.visible) {
            val fontrenderer: FontRenderer = mc.fontRenderer
            mc.getTextureManager().bindTexture(BUTTON_TEXTURES)
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
            this.hovered =
                mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height
            val i: Int = this.getHoverState(this.hovered)
            GlStateManager.enableBlend()
            GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
            )
            GlStateManager.blendFunc(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
            )
            this.drawTexturedModalRect(this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height)
            this.drawTexturedModalRect(
                this.x + this.width / 2,
                this.y,
                200 - this.width / 2,
                46 + i * 20,
                this.width / 2,
                this.height
            )
            this.mouseDragged(mc, mouseX, mouseY)
            var j: Int = 14737632

            if (packedFGColour != 0) {
                j = packedFGColour
            } else if (!this.enabled) {
                j = 10526880
            } else if (this.hovered) {
                j = 16777120
            }

            this.drawCenteredString(
                fontrenderer,
                this.displayString,
                this.x + this.width / 2,
                this.y + (this.height - 8) / 2,
                j
            )
        }
    }

    /**
     * Fired when the mouse button is dragged. Equivalent of MouseListener.mouseDragged(MouseEvent e).
     */
    override fun mouseDragged(mc: Minecraft, mouseX: Int, mouseY: Int) {
    }

    /**
     * Fired when the mouse button is released. Equivalent of MouseListener.mouseReleased(MouseEvent e).
     */
    override fun mouseReleased(mouseX: Int, mouseY: Int) {
    }

    /**
     * Returns true if the mouse has been pressed on this control. Equivalent of MouseListener.mousePressed(MouseEvent
     * e).
     */
    override fun mousePressed(mc: Minecraft, mouseX: Int, mouseY: Int): Boolean {
        return this.enabled && this.visible && mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height
    }

    /**
     * Whether the mouse cursor is currently over the button.
     */
    override fun isMouseOver(): Boolean {
        return this.hovered
    }

    override fun drawButtonForegroundLayer(mouseX: Int, mouseY: Int) {
    }

    override fun playPressSound(soundHandlerIn: SoundHandler) {
        soundHandlerIn.play(PositionedSoundRecord.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f))
    }

    override fun getButtonWidth(): Int {
        return this.width
    }

    override fun setWidth(width: Int) {
        this.width = width
    }

    companion object {
        @JvmField
        protected val BUTTON_TEXTURES: ResourceLocation =
            ResourceLocation.parse(GalaxySpace.Companion.ASSET_PREFIX + ":" + "textures/gui/widgets.png")
    }
}
