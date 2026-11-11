package micdoodle8.mods.galacticraft.core.client.gui.element

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.util.ColorUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.GuiButton
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.resources.Identifier as ResourceLocation
import org.lwjgl.opengl.GL11
import kotlin.math.max

open class GuiElementDropdown constructor(
    id: Int,
    parentClass: IDropboxCallback,
    x: Int,
    y: Int,
    vararg text: String
) : GuiButton(id, x, y, 15, 15, "") {
    @JvmField
    var dropdownClicked: Boolean = false

    @JvmField
    var optionStrings: Array<String>

    @JvmField
    var selectedOption: Int = -1

    @JvmField
    var font: FontRenderer
    private val parentClass: IDropboxCallback

    init {
        val mc: Minecraft = Minecraft.getInstance()
        this.parentClass = parentClass
        this.font = mc.fontRenderer
        this.optionStrings = (text as Array<String>)

        var largestString: Int = Int.MIN_VALUE

        for (element: String in text) {
            largestString = max(largestString, this.font.getStringWidth(element))
        }

        this.width = largestString + 8 + 15
    }

    override fun drawButton(par1Minecraft: Minecraft, par2: Int, par3: Int, partial: Float) {
        if (this.selectedOption == -1) {
            this.selectedOption = this.parentClass.getInitialSelection(this)
        }

        if (this.visible) {
//            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
//            RenderHelper.disableStandardItemLighting();
//            GL11.glDisable(GL11.GL_LIGHTING);
//            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glPushMatrix()

            this.zLevel = 300.0f
            GL11.glTranslatef(0f, 0f, 500f)
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
            this.hovered = par2 >= this.x && par3 >= this.y && par2 < this.x + this.width && par3 < this.y + this.height
            drawRect(
                this.x,
                this.y,
                this.x + this.width - 15,
                this.y + (if (this.dropdownClicked) this.height * this.optionStrings.size else this.height),
                ColorUtil.to32BitColor(255, 0, 0, 0)
            )
            drawRect(
                this.x + 1,
                this.y + 1,
                this.x + this.width - 16,
                this.y + (if (this.dropdownClicked) this.height * this.optionStrings.size else this.height) - 1,
                ColorUtil.to32BitColor(255, 150, 150, 150)
            )
            drawRect(
                this.x + this.width - 15,
                this.y,
                this.x + this.width - 1,
                this.y + this.height,
                ColorUtil.to32BitColor(255, 0, 0, 0)
            )
            drawRect(
                this.x + this.width - 15,
                this.y + 1,
                this.x + this.width - 2,
                this.y + this.height - 1,
                ColorUtil.to32BitColor(255, 150, 150, 150)
            )

            if (this.dropdownClicked && par2 >= this.x && par3 >= this.y && par2 < this.x + this.width && par3 < this.y + this.height * this.optionStrings.size) {
                val hoverPos: Int = (par3 - this.y) / this.height
                drawRect(
                    this.x + 1,
                    this.y + this.height * hoverPos + 1,
                    this.x + this.width - 16,
                    this.y + this.height * (hoverPos + 1) - 1,
                    ColorUtil.to32BitColor(255, 175, 175, 175)
                )
            }

            this.mouseDragged(par1Minecraft, par2, par3)

            if (this.dropdownClicked) {
                for (i: Int in this.optionStrings.indices) {
                    this.font.drawStringWithShadow(
                        this.optionStrings[i],
                        (this.x + this.width / 2 - 7 - this.font.getStringWidth(this.optionStrings[i]) / 2).toFloat(),
                        (this.y + (this.height - 6) / 2 + this.height * i).toFloat(),
                        ColorUtil.to32BitColor(255, 255, 255, 255)
                    )
                }
            } else {
                this.font.drawStringWithShadow(
                    this.optionStrings[this.selectedOption],
                    (this.x + this.width / 2 - 7 - this.font.getStringWidth(this.optionStrings[this.selectedOption]) / 2).toFloat(),
                    (this.y + (this.height - 6) / 2).toFloat(),
                    ColorUtil.to32BitColor(255, 255, 255, 255)
                )
            }

            par1Minecraft.renderEngine.bindTexture(texture)
            this.drawTexturedModalRect(this.x + this.width - 12, this.y + 5, 185, 0, 7, 4)

            GL11.glPopMatrix()
            this.zLevel = 0.0f
            //            GL11.glEnable(GL11.GL_LIGHTING);
//            GL11.glEnable(GL11.GL_DEPTH_TEST);
//            RenderHelper.enableStandardItemLighting();
//            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        }
    }

    override fun mousePressed(par1Minecraft: Minecraft, par2: Int, par3: Int): Boolean {
        if (!this.dropdownClicked) {
            if (this.enabled && this.visible && par2 >= this.x && par3 >= this.y && par2 < this.x + this.width && par3 < this.y + this.height) {
                if (this.parentClass.canBeClickedBy(this, par1Minecraft.player)) {
                    this.dropdownClicked = true
                    return true
                } else {
                    this.parentClass.onIntruderInteraction()
                }
            }
        } else {
            if (this.enabled && this.visible && par2 >= this.x && par3 >= this.y && par2 < this.x + this.width && par3 < this.y + this.height * this.optionStrings.size) {
                if (this.parentClass.canBeClickedBy(this, par1Minecraft.player)) {
                    val optionClicked: Int = (par3 - this.y) / this.height
                    this.selectedOption = optionClicked % this.optionStrings.size
                    this.dropdownClicked = false
                    this.parentClass.onSelectionChanged(this, this.selectedOption)
                    return true
                } else {
                    this.parentClass.onIntruderInteraction()
                }
            } else {
                this.dropdownClicked = false
            }
        }

        return false
    }

    interface IDropboxCallback {
        fun canBeClickedBy(dropdown: GuiElementDropdown?, player: EntityPlayer?): Boolean

        fun onSelectionChanged(dropdown: GuiElementDropdown?, selection: Int)

        fun getInitialSelection(dropdown: GuiElementDropdown?): Int

        fun onIntruderInteraction()
    }

    companion object {
        @JvmField
        protected val texture: ResourceLocation = ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/gui/gui.png")
    }
}
