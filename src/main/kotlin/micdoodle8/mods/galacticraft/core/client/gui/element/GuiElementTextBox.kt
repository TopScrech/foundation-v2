package micdoodle8.mods.galacticraft.core.client.gui.element

import micdoodle8.mods.galacticraft.core.util.ColorUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ChatAllowedCharacters
import net.minecraftforge.fml.client.FMLClientHandler
import org.lwjgl.input.Keyboard
import kotlin.math.min

open class GuiElementTextBox constructor(
    id: Int,
    private val parentGui: ITextBoxCallback,
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    initialText: String,
    @JvmField var numericOnly: Boolean,
    private val maxLength: Int,
    @JvmField var centered: Boolean
) : GuiButton(id, x, y, width, height, initialText) {
    @JvmField
    var text: String? = null

    @JvmField
    var timeBackspacePressed: Long = 0

    @JvmField
    var cursorPulse: Int = 0

    @JvmField
    var backspacePressed: Int = 0

    @JvmField
    var isTextFocused: Boolean = false

    @JvmField
    var incorrectUseTimer: Int = 0

    private val mc: Minecraft = FMLClientHandler.instance().getClient()

    /**
     * Call this from the parent GUI class in keyTyped.
     */
    open fun keyTyped(keyChar: Char, keyID: Int): Boolean {
        if (this.isTextFocused) {
            if (keyID == Keyboard.KEY_BACK) {
                if (this.text!!.length > 0) {
                    if (this.parentGui.canPlayerEdit(this, this.mc.player)) {
                        val toBeParsed: String = this.text!!.substring(0, this.text!!.length - 1)

                        if (this.isValid(toBeParsed)) {
                            this.text = toBeParsed
                            this.timeBackspacePressed = System.currentTimeMillis()
                        } else {
                            this.text = ""
                        }
                    } else {
                        this.incorrectUseTimer = 10
                        this.parentGui.onIntruderInteraction(this)
                    }
                }
            } else if (keyChar.code == 22) {
                var pastestring: String = GuiScreen.getClipboardString()

                if (pastestring == null) {
                    pastestring = ""
                }

                if (this.isValid(this.text + pastestring)) {
                    if (this.parentGui.canPlayerEdit(this, this.mc.player)) {
                        this.text = this.text + pastestring
                        this.text = this.text!!.substring(0, min(this.text.toString().length, this.maxLength))
                    } else {
                        this.incorrectUseTimer = 10
                        this.parentGui.onIntruderInteraction(this)
                    }
                }
            } else if (this.isValid(this.text + keyChar)) {
                if (this.parentGui.canPlayerEdit(this, this.mc.player)) {
                    this.text = this.text + keyChar
                    this.text = this.text!!.substring(0, min(this.text!!.length, this.maxLength))
                } else {
                    this.incorrectUseTimer = 10
                    this.parentGui.onIntruderInteraction(this)
                }
            }

            this.parentGui.onTextChanged(this, this.text)
            return true
        }

        return false
    }

    override fun drawButton(par1Minecraft: Minecraft, par2: Int, par3: Int, partial: Float) {
        if (this.text == null) {
            this.text = this.parentGui.getInitialText(this)
            this.parentGui.onTextChanged(this, this.text)
        }

        if (this.visible) {
            drawRect(
                this.x,
                this.y,
                this.x + this.width,
                this.y + this.height,
                ColorUtil.to32BitColor(140, 140, 140, 140)
            )
            drawRect(
                this.x + 1,
                this.y + 1,
                this.x + this.width - 1,
                this.y + this.height - 1,
                ColorUtil.to32BitColor(255, 0, 0, 0)
            )

            this.cursorPulse++

            if (this.timeBackspacePressed > 0) {
                if (Keyboard.isKeyDown(Keyboard.KEY_BACK) && this.text!!.length > 0) {
                    if (System.currentTimeMillis() - this.timeBackspacePressed > 200 / (1 + this.backspacePressed * 0.3f) && this.parentGui.canPlayerEdit(
                            this,
                            this.mc.player
                        )
                    ) {
                        val toBeParsed: String = this.text!!.substring(0, this.text!!.length - 1)

                        if (this.isValid(toBeParsed)) {
                            this.text = toBeParsed
                            this.parentGui.onTextChanged(this, this.text)
                        } else {
                            this.text = ""
                        }

                        this.timeBackspacePressed = System.currentTimeMillis()
                        this.backspacePressed++
                    } else if (!this.parentGui.canPlayerEdit(this, this.mc.player)) {
                        this.incorrectUseTimer = 10
                        this.parentGui.onIntruderInteraction(this)
                    }
                } else {
                    this.timeBackspacePressed = 0
                    this.backspacePressed = 0
                }
            }

            if (this.incorrectUseTimer > 0) {
                this.incorrectUseTimer--
            }

            var xPos: Int = this.x + 4

            if (this.centered) {
                xPos = this.x + this.width / 2 - this.mc.fontRenderer.getStringWidth(this.text) / 2
            }

            this.drawString(
                this.mc.fontRenderer,
                this.text + (if (this.cursorPulse / 24 % 2 == 0 && this.isTextFocused) "_" else ""),
                xPos,
                this.y + this.height / 2 - 4,
                if (this.incorrectUseTimer > 0) ColorUtil.to32BitColor(
                    255,
                    255,
                    20,
                    20
                ) else this.parentGui.getTextColor(this)
            )
        }
    }

    open fun getIntegerValue(): Int {
        try {
            return (if (this.text == "") "0" else this.text)!!.toInt()
        } catch (e: Exception) {
            return -1
        }
    }

    open fun isValid(string: String): Boolean {
        if (this.numericOnly) {
            if (string.length > 0 && ChatAllowedCharacters.isAllowedCharacter(string.get(string.length - 1))) {
                try {
                    string.toInt()
                    return true
                } catch (e: Exception) {
                    return false
                }
            } else {
                return false
            }
        } else {
            if (string.length <= 0) {
                return false
            }

            return ChatAllowedCharacters.isAllowedCharacter(string.get(string.length - 1))
        }
    }

    override fun mousePressed(par1Minecraft: Minecraft, par2: Int, par3: Int): Boolean {
        if (super.mousePressed(par1Minecraft, par2, par3)) {
            drawRect(this.x, this.y, this.x + this.width, this.y + this.height, -0x5f5f60)
            this.isTextFocused = true
            this.text = this.parentGui.getInitialText(this)
            this.parentGui.onTextChanged(this, this.text)
            return true
        } else {
            this.isTextFocused = false
            return false
        }
    }

    open fun getMaxLength(): Int {
        return maxLength
    }

    interface ITextBoxCallback {
        fun canPlayerEdit(textBox: GuiElementTextBox?, player: EntityPlayer?): Boolean

        fun onTextChanged(textBox: GuiElementTextBox?, newText: String?)

        fun getInitialText(textBox: GuiElementTextBox?): String?

        fun getTextColor(textBox: GuiElementTextBox?): Int

        fun onIntruderInteraction(textBox: GuiElementTextBox?)
    }
}
