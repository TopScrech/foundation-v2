package micdoodle8.mods.galacticraft.core.client.gui.element

import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.util.ClientUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraftforge.fml.client.config.GuiUtils
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.GL11
import kotlin.math.floor

open class GuiElementSlider @JvmOverloads constructor(
    id: Int,
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    private val isVertical: Boolean,
    private val firstColor: Vector3,
    private val lastColor: Vector3,
    displayString: String = ""
) : GuiButton(id, x, y, width, height, displayString) {
    private var sliderPos = 0

    override fun drawButton(par1Minecraft: Minecraft, par2: Int, par3: Int, partial: Float) {
        if (this.visible) {
            this.hovered = par2 >= this.x && par3 >= this.y && par2 < this.x + this.width && par3 < this.y + this.height

            if (Mouse.isButtonDown(0) && this.hovered) {
                if (this.isVertical) {
                    this.sliderPos = par3 - this.y
                } else {
                    this.sliderPos = par2 - this.x
                }
            }

            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
            GL11.glDisable(GL11.GL_TEXTURE_2D)
            GL11.glEnable(GL11.GL_BLEND)
            GL11.glDisable(GL11.GL_ALPHA_TEST)
            OpenGlHelper.glBlendFunc(770, 771, 1, 0)
            GL11.glShadeModel(GL11.GL_SMOOTH)
            val tessellator: Tessellator = Tessellator.getInstance()
            val worldRenderer: BufferBuilder = tessellator.getBuffer()

            if (this.isVertical) {
                worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR)
                worldRenderer.pos(this.x.toDouble() + this.width, this.y.toDouble(), this.zLevel.toDouble())
                    .color(0f, 0f, 0f, 1.0f).endVertex()
                worldRenderer.pos(this.x.toDouble(), this.y.toDouble(), this.zLevel.toDouble()).color(0f, 0f, 0f, 1.0f)
                    .endVertex()
                worldRenderer.pos(this.x.toDouble(), this.y.toDouble() + this.height, this.zLevel.toDouble())
                    .color(0f, 0f, 0f, 1.0f).endVertex()
                worldRenderer.pos(
                    this.x.toDouble() + this.width,
                    this.y.toDouble() + this.height,
                    this.zLevel.toDouble()
                ).color(0f, 0f, 0f, 1.0f).endVertex()
                tessellator.draw()

                worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR)
                worldRenderer.pos(this.x.toDouble() + this.width - 1, this.y.toDouble() + 1, this.zLevel.toDouble())
                    .color(this.firstColor.floatX(), this.firstColor.floatY(), this.firstColor.floatZ(), 1.0f)
                    .endVertex()
                worldRenderer.pos(this.x.toDouble() + 1, this.y.toDouble() + 1, this.zLevel.toDouble())
                    .color(this.firstColor.floatX(), this.firstColor.floatY(), this.firstColor.floatZ(), 1.0f)
                    .endVertex()
                worldRenderer.pos(this.x.toDouble() + 1, this.y.toDouble() + this.height - 1, this.zLevel.toDouble())
                    .color(this.lastColor.floatX(), this.lastColor.floatY(), this.lastColor.floatZ(), 1.0f).endVertex()
                worldRenderer.pos(
                    this.x.toDouble() + this.width - 1,
                    this.y.toDouble() + this.height - 1,
                    this.zLevel.toDouble()
                ).color(this.lastColor.floatX(), this.lastColor.floatY(), this.lastColor.floatZ(), 1.0f).endVertex()
                tessellator.draw()

                worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR)
                worldRenderer.pos(
                    this.x.toDouble() + this.width,
                    this.y.toDouble() + this.sliderPos - 1,
                    this.zLevel.toDouble()
                ).color(1f, 1f, 1f, 1.0f).endVertex()
                worldRenderer.pos(this.x.toDouble(), this.y.toDouble() + this.sliderPos - 1, this.zLevel.toDouble())
                    .color(1f, 1f, 1f, 1.0f).endVertex()
                worldRenderer.pos(this.x.toDouble(), this.y.toDouble() + this.sliderPos + 1, this.zLevel.toDouble())
                    .color(1f, 1f, 1f, 1.0f).endVertex()
                worldRenderer.pos(
                    this.x.toDouble() + this.width,
                    this.y.toDouble() + this.sliderPos + 1,
                    this.zLevel.toDouble()
                ).color(1f, 1f, 1f, 1.0f).endVertex()
                tessellator.draw()
            } else {
                worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR)
                worldRenderer.pos(this.x.toDouble() + this.width, this.y.toDouble(), this.zLevel.toDouble())
                    .color(0f, 0f, 0f, 1.0f).endVertex()
                worldRenderer.pos(this.x.toDouble(), this.y.toDouble(), this.zLevel.toDouble()).color(0f, 0f, 0f, 1.0f)
                    .endVertex()
                worldRenderer.pos(this.x.toDouble(), this.y.toDouble() + this.height, this.zLevel.toDouble())
                    .color(0f, 0f, 0f, 1.0f).endVertex()
                worldRenderer.pos(
                    this.x.toDouble() + this.width,
                    this.y.toDouble() + this.height,
                    this.zLevel.toDouble()
                ).color(0f, 0f, 0f, 1.0f).endVertex()
                tessellator.draw()

                worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR)
                worldRenderer.pos(this.x.toDouble() + this.width - 1, this.y.toDouble() + 1, this.zLevel.toDouble())
                    .color(this.lastColor.floatX(), this.lastColor.floatY(), this.lastColor.floatZ(), 1.0f).endVertex()
                worldRenderer.pos(this.x.toDouble() + 1, this.y.toDouble() + 1, this.zLevel.toDouble())
                    .color(this.firstColor.floatX(), this.firstColor.floatY(), this.firstColor.floatZ(), 1.0f)
                    .endVertex()
                worldRenderer.pos(this.x.toDouble() + 1, this.y.toDouble() + this.height - 1, this.zLevel.toDouble())
                    .color(this.firstColor.floatX(), this.firstColor.floatY(), this.firstColor.floatZ(), 1.0f)
                    .endVertex()
                worldRenderer.pos(
                    this.x.toDouble() + this.width - 1,
                    this.y.toDouble() + this.height - 1,
                    this.zLevel.toDouble()
                ).color(this.lastColor.floatX(), this.lastColor.floatY(), this.lastColor.floatZ(), 1.0f).endVertex()
                tessellator.draw()

                GL11.glShadeModel(GL11.GL_FLAT)
                GL11.glDisable(GL11.GL_BLEND)
                GL11.glEnable(GL11.GL_ALPHA_TEST)
                GL11.glEnable(GL11.GL_TEXTURE_2D)

                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
                GL11.glDisable(GL11.GL_TEXTURE_2D)
                GL11.glEnable(GL11.GL_BLEND)
                GL11.glDisable(GL11.GL_ALPHA_TEST)
                OpenGlHelper.glBlendFunc(770, 771, 1, 0)
                GL11.glShadeModel(GL11.GL_SMOOTH)

                worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR)
                worldRenderer.pos(this.x.toDouble() + this.sliderPos + 1, this.y.toDouble(), this.zLevel.toDouble())
                    .color(1f, 1f, 1f, 1.0f).endVertex()
                worldRenderer.pos(this.x.toDouble() + this.sliderPos - 1, this.y.toDouble(), this.zLevel.toDouble())
                    .color(1f, 1f, 1f, 1.0f).endVertex()
                worldRenderer.pos(
                    this.x.toDouble() + this.sliderPos - 1,
                    this.y.toDouble() + this.height,
                    this.zLevel.toDouble()
                ).color(1f, 1f, 1f, 1.0f).endVertex()
                worldRenderer.pos(
                    this.x.toDouble() + this.sliderPos + 1,
                    this.y.toDouble() + this.height,
                    this.zLevel.toDouble()
                ).color(1f, 1f, 1f, 1.0f).endVertex()
                tessellator.draw()
            }

            GL11.glShadeModel(GL11.GL_FLAT)
            GL11.glDisable(GL11.GL_BLEND)
            GL11.glEnable(GL11.GL_ALPHA_TEST)
            GL11.glEnable(GL11.GL_TEXTURE_2D)
        }
    }

    open fun drawHoveringText() {
        if (this.hovered) {
            val font: FontRenderer? = Minecraft.getInstance().fontRenderer
            val mc: Minecraft = Minecraft.getInstance()
            val scaledresolution: ScaledResolution = ClientUtil.getScaledRes(mc, mc.displayWidth, mc.displayHeight)
            val width: Int = scaledresolution.getScaledWidth()
            val height: Int = scaledresolution.getScaledHeight()
            val x: Int = Mouse.getX() * width / mc.displayWidth
            val y: Int = height - Mouse.getY() * height / mc.displayHeight - 1
            GuiUtils.drawHoveringText(mutableListOf<String?>(this.displayString), x, y, width, height, -1, font)
        }
    }

    open fun setSliderPos(pos: Float) {
        this.sliderPos = floor((this.height * pos).toDouble()).toInt()
    }

    open fun getSliderPos(): Int {
        return this.sliderPos
    }

    open fun getNormalizedValue(): Float {
        return this.sliderPos / this.height.toFloat()
    }

    open fun getColorValueD(): Double {
        return (this.sliderPos * 255.0) / (this.height - 1)
    }

    open fun getButtonHeight(): Int {
        return this.height
    }
}
