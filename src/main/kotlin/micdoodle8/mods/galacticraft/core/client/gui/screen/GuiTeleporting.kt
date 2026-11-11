package micdoodle8.mods.galacticraft.core.client.gui.screen

import micdoodle8.mods.galacticraft.api.world.IZeroGDimension
import micdoodle8.mods.galacticraft.core.tick.TickHandlerClient
import micdoodle8.mods.galacticraft.core.util.ColorUtil
import net.minecraft.client.gui.screens.Screen as GuiScreen

open class GuiTeleporting constructor(private val targetDimensionID: Int) : GuiScreen() {
    private val message: String

    init {
        val possibleStrings: Array<String> =
            arrayOf<String>("Taking one small step", "Taking one giant leap", "Prepare for entry!")
        this.message = possibleStrings[(Math.random() * possibleStrings.size).toInt()]
        TickHandlerClient.teleportingGui = this
    }

    override fun initGui() {
        super.initGui()
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.drawScreen(mouseX, mouseY, partialTicks)
        this.drawBackground(0)
        this.drawCenteredString(
            this.fontRenderer,
            this.message,
            this.width / 2,
            this.height / 2,
            ColorUtil.to32BitColor(255, 255, 255, 255)
        )
    }

    override fun updateScreen() {
        super.updateScreen()
        if (mc.player != null && mc.player.world != null) {
            // Screen will exit when the player is in the target dimension and has started moving down
            if (mc.player.world.provider.getDimension() == this.targetDimensionID) {
                if ((mc.player.world.provider is IZeroGDimension || (mc.player.posY - mc.player.lastTickPosY) < 0.0)) {
                    mc.displayGuiScreen(null)
                    TickHandlerClient.teleportingGui = null
                }
            }
        }
    }

    override fun doesGuiPauseGame(): Boolean {
        return false
    }
}
