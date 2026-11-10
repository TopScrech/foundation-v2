package micdoodle8.mods.galacticraft.core.client.gui.element

import java.util.ArrayList

import micdoodle8.mods.galacticraft.core.util.ColorUtil
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.Gui
import net.minecraftforge.fml.client.FMLClientHandler
import org.lwjgl.input.Mouse
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

open class GuiElementGradientList constructor(
    @JvmField var xPosition: Int,
    @JvmField var yPosition: Int,
    @JvmField var width: Int,
    @JvmField var height: Int
) : Gui() {
    open class ListElement
    constructor(@JvmField var value: String?, @JvmField var color: Int)

    @JvmField
    var listContents: MutableList<ListElement?> = ArrayList<ListElement?>()

    @JvmField
    var sliderPos: Int
    private var lastMousePosY = 0
    private var sliderGrabbed = false
    private var sliderEnabled = false
    private var selectedIndex = -1

    init {
        this.sliderPos = this.yPosition + 1
    }

    open fun updateListContents(newContents: MutableList<ListElement?>) {
        this.listContents = newContents
        this.sliderEnabled = this.listContents.size * BUTTON_HEIGHT > this.height
        if (this.selectedIndex >= this.listContents.size) {
            this.selectedIndex = -1
        }
    }

    open fun draw(mousePosX: Int, mousePosY: Int) {
        if (this.sliderEnabled) {
            if (this.sliderGrabbed || mousePosX >= this.xPosition + this.width - 9 && mousePosX < this.xPosition + this.width && mousePosY >= this.yPosition && mousePosY < this.yPosition + this.height) {
                if (Mouse.isButtonDown(0)) {
                    this.sliderGrabbed = true

                    if (this.lastMousePosY > 0) {
                        if (mousePosY >= this.sliderPos && mousePosY < this.sliderPos + 15) {
                            val deltaY: Int = this.lastMousePosY - this.sliderPos
                            this.sliderPos = mousePosY - deltaY
                        } else {
                            this.sliderPos = mousePosY - 7
                        }
                    }

                    this.lastMousePosY = mousePosY
                } else {
                    this.sliderGrabbed = false
                }
            } else {
                this.lastMousePosY = 0
            }
        }

        if (Mouse.isButtonDown(0)) {
            if (mousePosX >= this.xPosition && mousePosX < this.xPosition + this.width - 10 && mousePosY >= this.yPosition && mousePosY < this.yPosition + this.height) {
                val clickPosY: Int =
                    mousePosY - this.yPosition + floor(((this.listContents.size * BUTTON_HEIGHT - this.height) * this.getSliderPercentage()).toDouble()).toInt()
                this.selectedIndex = clickPosY / BUTTON_HEIGHT

                if (this.selectedIndex < 0 || this.selectedIndex >= this.listContents.size) {
                    this.selectedIndex = -1
                }
            }
        }

        this.sliderPos = min(max(this.yPosition, this.sliderPos), this.yPosition + this.height - 15)

        this.drawGradientRect(
            this.xPosition,
            this.yPosition,
            this.xPosition + this.width - 10,
            this.yPosition + this.height,
            ColorUtil.to32BitColor(255, 30, 30, 30),
            ColorUtil.to32BitColor(255, 30, 30, 30)
        )
        this.drawGradientRect(
            this.xPosition + this.width - 9,
            this.yPosition,
            this.xPosition + this.width,
            this.yPosition + this.height,
            ColorUtil.to32BitColor(255, 50, 50, 50),
            ColorUtil.to32BitColor(255, 50, 50, 50)
        )
        var sliderColor: Int =
            if (this.sliderEnabled) ColorUtil.to32BitColor(255, 90, 90, 90) else ColorUtil.to32BitColor(255, 40, 40, 40)
        this.drawGradientRect(
            this.xPosition + this.width - 9,
            this.sliderPos,
            this.xPosition + this.width,
            this.sliderPos + 15,
            sliderColor,
            sliderColor
        )

        drawRect(
            this.xPosition + this.width - 1,
            this.yPosition,
            this.xPosition + this.width,
            this.yPosition + this.height,
            ColorUtil.to32BitColor(255, 0, 0, 0)
        )
        drawRect(
            this.xPosition + this.width - 10,
            this.yPosition,
            this.xPosition + this.width - 9,
            this.yPosition + this.height,
            ColorUtil.to32BitColor(255, 0, 0, 0)
        )
        drawRect(
            this.xPosition,
            this.yPosition,
            this.xPosition + 1,
            this.yPosition + this.height,
            ColorUtil.to32BitColor(255, 0, 0, 0)
        )
        drawRect(
            this.xPosition,
            this.yPosition,
            this.xPosition + this.width,
            this.yPosition + 1,
            ColorUtil.to32BitColor(255, 0, 0, 0)
        )
        drawRect(
            this.xPosition,
            this.yPosition + this.height - 1,
            this.xPosition + this.width,
            this.yPosition + this.height,
            ColorUtil.to32BitColor(255, 0, 0, 0)
        )

        sliderColor = if (this.sliderEnabled) ColorUtil.to32BitColor(255, 120, 120, 120) else ColorUtil.to32BitColor(
            255,
            60,
            60,
            60
        )
        drawRect(
            this.xPosition + this.width - 9,
            this.sliderPos + 1,
            this.xPosition + this.width - 8,
            this.sliderPos + 14,
            sliderColor
        )
        drawRect(
            this.xPosition + this.width - 2,
            this.sliderPos + 1,
            this.xPosition + this.width - 1,
            this.sliderPos + 14,
            sliderColor
        )
        drawRect(
            this.xPosition + this.width - 9,
            this.sliderPos,
            this.xPosition + this.width - 1,
            this.sliderPos + 1,
            sliderColor
        )
        drawRect(
            this.xPosition + this.width - 9,
            this.sliderPos + 15,
            this.xPosition + this.width - 1,
            this.sliderPos + 14,
            sliderColor
        )

        var currentDrawHeight: Int =
            this.yPosition + 1 - floor(((this.listContents.size * BUTTON_HEIGHT - this.height) * this.getSliderPercentage()).toDouble()).toInt()
        val fontRenderer: FontRenderer = FMLClientHandler.instance().getClient().fontRenderer

        for (i: Int in this.listContents.indices) {
            val displayButton: ListElement? = this.listContents.get(i)

            if (displayButton != null && displayButton.value != null && !displayButton.value!!.isEmpty()) {
                var yCoord0: Int = currentDrawHeight
                var yCoord1: Int = currentDrawHeight + BUTTON_HEIGHT - 1

                if (yCoord1 > this.yPosition && yCoord0 < this.yPosition + this.height) {
                    yCoord0 = max(this.yPosition + 1, yCoord0)
                    yCoord1 = min(this.yPosition + this.height - 1, yCoord1)

                    val color: Int = if (i == this.selectedIndex) ColorUtil.to32BitColor(
                        255,
                        35,
                        35,
                        35
                    ) else ColorUtil.to32BitColor(255, 25, 25, 25)

                    drawRect(this.xPosition + 1, yCoord0, this.xPosition + this.width - 10, yCoord1, color)

                    if (currentDrawHeight + BUTTON_HEIGHT / 2 - fontRenderer.FONT_HEIGHT / 2 > this.yPosition && currentDrawHeight + BUTTON_HEIGHT / 2 + fontRenderer.FONT_HEIGHT / 2 < this.yPosition + this.height) {
                        fontRenderer.drawString(
                            displayButton.value,
                            this.xPosition + (this.width - 10) / 2 - fontRenderer.getStringWidth(displayButton.value) / 2,
                            currentDrawHeight + BUTTON_HEIGHT / 2 - fontRenderer.FONT_HEIGHT / 2,
                            displayButton.color
                        )
                    }
                }

                currentDrawHeight += BUTTON_HEIGHT
            }
        }
    }

    open fun update() {
    }

    private fun getSliderPercentage(): Float {
        if (!this.sliderEnabled) {
            return 0.0f
        }

        return (this.sliderPos - this.yPosition) / (this.height - 15).toFloat()
    }

    open fun getSelectedElement(): ListElement? {
        if (this.selectedIndex == -1) {
            return null
        }

        return this.listContents.get(this.selectedIndex)
    }

    companion object {
        const val BUTTON_HEIGHT: Int = 20
    }
}
