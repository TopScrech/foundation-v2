package micdoodle8.mods.galacticraft.core.client.gui.container

import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementCheckbox.ICheckBoxCallback
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementDropdown.IDropboxCallback
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementTextBox.ITextBoxCallback
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementCheckbox
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementDropdown
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementTextBox
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.tile.TileEntityAirLockController
import micdoodle8.mods.galacticraft.core.util.ColorUtil
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.PlayerUtil
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.screens.Screen as GuiScreen
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.resources.Identifier as ResourceLocation
import org.lwjgl.input.Keyboard
import java.io.IOException

open class GuiAirLockController constructor(private val controller: TileEntityAirLockController) : GuiScreen(),
    GuiElementCheckbox.ICheckBoxCallback, GuiElementDropdown.IDropboxCallback, GuiElementTextBox.ITextBoxCallback {
    private val xSize = 181
    private val ySize = 139
    private var checkboxRedstoneSignal: GuiElementCheckbox? = null
    private var checkboxPlayerDistance: GuiElementCheckbox? = null
    private var dropdownPlayerDistance: GuiElementDropdown? = null
    private var checkboxOpenForPlayer: GuiElementCheckbox? = null
    private var textBoxPlayerToOpenFor: GuiElementTextBox? = null
    private var checkboxInvertSelection: GuiElementCheckbox? = null
    private var checkboxHorizontalMode: GuiElementCheckbox? = null
    private var cannotEditTimer = 0

    override fun initGui() {
        super.initGui()
        this.buttonList.clear()
        val var5: Int = (this.width - this.xSize) / 2
        val var6: Int = (this.height - this.ySize) / 2
        this.checkboxRedstoneSignal = GuiElementCheckbox(
            0,
            this,
            this.width / 2 - 84,
            var6 + 18,
            GCCoreUtil.translate("gui.checkbox.redstone_signal.name")
        )
        this.checkboxPlayerDistance = GuiElementCheckbox(
            1,
            this,
            this.width / 2 - 84,
            var6 + 33,
            GCCoreUtil.translate("gui.checkbox.player_within.name") + ": "
        )
        val dropboxStrings: Array<String?> = arrayOf<String?>(
            GCCoreUtil.translate("gui.dropbox.player_distance.name.0"),
            GCCoreUtil.translate("gui.dropbox.player_distance.name.1"),
            GCCoreUtil.translate("gui.dropbox.player_distance.name.2"),
            GCCoreUtil.translate("gui.dropbox.player_distance.name.3")
        )
        this.dropdownPlayerDistance = GuiElementDropdown(2, this, var5 + 99, var6 + 32, *(dropboxStrings as Array<out String>))
        this.checkboxOpenForPlayer = GuiElementCheckbox(
            3,
            this,
            this.width / 2 - 68,
            var6 + 49,
            GCCoreUtil.translate("gui.checkbox.player_name.name") + ": "
        )
        this.textBoxPlayerToOpenFor =
            GuiElementTextBox(4, this, this.width / 2 - 61, var6 + 64, 110, 15, "", false, 16, false)
        this.checkboxInvertSelection = GuiElementCheckbox(
            5,
            this,
            this.width / 2 - 84,
            var6 + 80,
            GCCoreUtil.translate("gui.checkbox.invert.name")
        )
        this.checkboxHorizontalMode = GuiElementCheckbox(
            6,
            this,
            this.width / 2 - 84,
            var6 + 96,
            GCCoreUtil.translate("gui.checkbox.horizontal.name")
        )
        this.buttonList.add(this.checkboxRedstoneSignal)
        this.buttonList.add(this.checkboxPlayerDistance)
        this.buttonList.add(this.dropdownPlayerDistance)
        this.buttonList.add(this.checkboxOpenForPlayer)
        this.buttonList.add(this.textBoxPlayerToOpenFor)
        this.buttonList.add(this.checkboxInvertSelection)
        this.buttonList.add(this.checkboxHorizontalMode)
    }

    @Throws(IOException::class)
    override fun keyTyped(keyChar: Char, keyID: Int) {
        if (keyID != Keyboard.KEY_ESCAPE) {
            if (this.textBoxPlayerToOpenFor!!.keyTyped(keyChar, keyID)) {
                return
            }
        }

        super.keyTyped(keyChar, keyID)
    }

    override fun doesGuiPauseGame(): Boolean {
        return false
    }

    override fun actionPerformed(par1GuiButton: GuiButton) {
        if (par1GuiButton.enabled) {
            when (par1GuiButton.id) {
                0 -> {}
            }
        }
    }

    override fun drawScreen(par1: Int, par2: Int, par3: Float) {
        val var5: Int = (this.width - this.xSize) / 2
        val var6: Int = (this.height - this.ySize) / 2

        this.mc.renderEngine.bindTexture(airLockControllerGui)
        this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize)

        this.drawTexturedModalRect(var5 + 11, var6 + 51, 181, 0, 7, 9)

        var displayString: String = GCCoreUtil.translateWithFormat("gui.title.air_lock.name", this.controller.ownerName)
        this.fontRenderer.drawString(
            displayString,
            this.width / 2 - this.fontRenderer.getStringWidth(displayString) / 2,
            this.height / 2 - 65,
            4210752
        )

        if (this.cannotEditTimer > 0) {
            this.fontRenderer.drawString(
                this.controller.ownerName,
                this.width / 2 - this.fontRenderer.getStringWidth(displayString) / 2,
                this.height / 2 - 65,
                if (this.cannotEditTimer % 30 < 15) ColorUtil.to32BitColor(255, 255, 100, 100) else 4210752
            )
            this.cannotEditTimer--
        }

        displayString = GCCoreUtil.translate("gui.message.status.name") + ":"
        this.fontRenderer.drawString(
            displayString,
            this.width / 2 - this.fontRenderer.getStringWidth(displayString) / 2,
            this.height / 2 + 45,
            4210752
        )
        displayString = EnumColor.RED.toString() + GCCoreUtil.translate("gui.status.air_lock_closed.name")

        if (!this.controller.active) {
            displayString = EnumColor.BRIGHT_GREEN.toString() + GCCoreUtil.translate("gui.status.air_lock_open.name")
        }

        this.fontRenderer.drawString(
            displayString,
            this.width / 2 - this.fontRenderer.getStringWidth(displayString) / 2,
            this.height / 2 + 55,
            4210752
        )

        super.drawScreen(par1, par2, par3)
    }

    override fun onSelectionChanged(checkbox: GuiElementCheckbox?, newSelected: Boolean) {
        if (checkbox == this.checkboxRedstoneSignal) {
            this.controller.redstoneActivation = newSelected
            (GalacticraftCore.packetPipeline)!!.sendToServer(
                PacketSimple(
                    PacketSimple.EnumSimplePacket.S_ON_ADVANCED_GUI_CLICKED_INT,
                    GCCoreUtil.getDimensionID(mc.world),
                    arrayOf<Any>(0, this.controller.getPos(), if (this.controller.redstoneActivation) 1 else 0)
                )
            )
            // PacketDispatcher.sendPacketToServer(PacketUtil.createPacket(GalacticraftCore.CHANNEL,
            // EnumPacketServer.ON_ADVANCED_GUI_CLICKED_INT, new Object[] { 0,
            // this.controller.xCoord, this.controller.yCoord,
            // this.controller.zCoord, this.controller.redstoneActivation ? 1 :
            // 0 }));
        } else if (checkbox == this.checkboxPlayerDistance) {
            this.controller.playerDistanceActivation = newSelected
            (GalacticraftCore.packetPipeline)!!.sendToServer(
                PacketSimple(
                    PacketSimple.EnumSimplePacket.S_ON_ADVANCED_GUI_CLICKED_INT,
                    GCCoreUtil.getDimensionID(mc.world),
                    arrayOf<Any>(1, this.controller.getPos(), if (this.controller.playerDistanceActivation) 1 else 0)
                )
            )
            // PacketDispatcher.sendPacketToServer(PacketUtil.createPacket(GalacticraftCore.CHANNEL,
            // EnumPacketServer.ON_ADVANCED_GUI_CLICKED_INT, new Object[] { 1,
            // this.controller.xCoord, this.controller.yCoord,
            // this.controller.zCoord, this.controller.playerDistanceActivation
            // ? 1 : 0 }));
        } else if (checkbox == this.checkboxOpenForPlayer) {
            this.controller.playerNameMatches = newSelected
            (GalacticraftCore.packetPipeline)!!.sendToServer(
                PacketSimple(
                    PacketSimple.EnumSimplePacket.S_ON_ADVANCED_GUI_CLICKED_INT,
                    GCCoreUtil.getDimensionID(mc.world),
                    arrayOf<Any>(3, this.controller.getPos(), if (this.controller.playerNameMatches) 1 else 0)
                )
            )
            // PacketDispatcher.sendPacketToServer(PacketUtil.createPacket(GalacticraftCore.CHANNEL,
            // EnumPacketServer.ON_ADVANCED_GUI_CLICKED_INT, new Object[] { 3,
            // this.controller.xCoord, this.controller.yCoord,
            // this.controller.zCoord, this.controller.playerNameMatches ? 1 : 0
            // }));
        } else if (checkbox == this.checkboxInvertSelection) {
            this.controller.invertSelection = newSelected
            (GalacticraftCore.packetPipeline)!!.sendToServer(
                PacketSimple(
                    PacketSimple.EnumSimplePacket.S_ON_ADVANCED_GUI_CLICKED_INT,
                    GCCoreUtil.getDimensionID(mc.world),
                    arrayOf<Any>(4, this.controller.getPos(), if (this.controller.invertSelection) 1 else 0)
                )
            )
            // PacketDispatcher.sendPacketToServer(PacketUtil.createPacket(GalacticraftCore.CHANNEL,
            // EnumPacketServer.ON_ADVANCED_GUI_CLICKED_INT, new Object[] { 4,
            // this.controller.xCoord, this.controller.yCoord,
            // this.controller.zCoord, this.controller.invertSelection ? 1 : 0
            // }));
        } else if (checkbox == this.checkboxHorizontalMode) {
            this.controller.lastHorizontalModeEnabled = this.controller.horizontalModeEnabled
            this.controller.horizontalModeEnabled = newSelected
            (GalacticraftCore.packetPipeline)!!.sendToServer(
                PacketSimple(
                    PacketSimple.EnumSimplePacket.S_ON_ADVANCED_GUI_CLICKED_INT,
                    GCCoreUtil.getDimensionID(mc.world),
                    arrayOf<Any>(5, this.controller.getPos(), if (this.controller.horizontalModeEnabled) 1 else 0)
                )
            )
            // PacketDispatcher.sendPacketToServer(PacketUtil.createPacket(GalacticraftCore.CHANNEL,
            // EnumPacketServer.ON_ADVANCED_GUI_CLICKED_INT, new Object[] { 5,
            // this.controller.xCoord, this.controller.yCoord,
            // this.controller.zCoord, this.controller.horizontalModeEnabled ? 1
            // : 0 }));
        }
    }

    override fun canPlayerEdit(checkbox: GuiElementCheckbox?, player: EntityPlayer?): Boolean {
        return PlayerUtil.getName(player) == this.controller.ownerName
    }

    override fun getInitiallySelected(checkbox: GuiElementCheckbox?): Boolean {
        if (checkbox == this.checkboxRedstoneSignal) {
            return this.controller.redstoneActivation
        } else if (checkbox == this.checkboxPlayerDistance) {
            return this.controller.playerDistanceActivation
        } else if (checkbox == this.checkboxOpenForPlayer) {
            return this.controller.playerNameMatches
        } else if (checkbox == this.checkboxInvertSelection) {
            return this.controller.invertSelection
        } else if (checkbox == this.checkboxHorizontalMode) {
            return this.controller.horizontalModeEnabled
        }

        return false
    }

    override fun canBeClickedBy(dropdown: GuiElementDropdown?, player: EntityPlayer?): Boolean {
        return PlayerUtil.getName(player) == this.controller.ownerName
    }

    override fun onSelectionChanged(dropdown: GuiElementDropdown?, selection: Int) {
        if (dropdown == this.dropdownPlayerDistance) {
            this.controller.playerDistanceSelection = selection
            (GalacticraftCore.packetPipeline)!!.sendToServer(
                PacketSimple(
                    PacketSimple.EnumSimplePacket.S_ON_ADVANCED_GUI_CLICKED_INT,
                    GCCoreUtil.getDimensionID(mc.world),
                    arrayOf<Any>(2, this.controller.getPos(), this.controller.playerDistanceSelection)
                )
            )
        }
    }

    override fun getInitialSelection(dropdown: GuiElementDropdown?): Int {
        return this.controller.playerDistanceSelection
    }

    override fun canPlayerEdit(textBox: GuiElementTextBox?, player: EntityPlayer?): Boolean {
        return PlayerUtil.getName(player) == this.controller.ownerName
    }

    override fun onTextChanged(textBox: GuiElementTextBox?, newText: String?) {
        if (textBox == this.textBoxPlayerToOpenFor) {
            this.controller.playerToOpenFor = if (newText != null) newText else ""
            (GalacticraftCore.packetPipeline)!!.sendToServer(
                PacketSimple(
                    PacketSimple.EnumSimplePacket.S_ON_ADVANCED_GUI_CLICKED_STRING,
                    GCCoreUtil.getDimensionID(mc.world),
                    arrayOf<Any?>(0, this.controller.getPos(), this.controller.playerToOpenFor)
                )
            )
        }
    }

    override fun getInitialText(textBox: GuiElementTextBox?): String? {
        if (textBox == this.textBoxPlayerToOpenFor) {
            return this.controller.playerToOpenFor
        }

        return null
    }

    override fun getTextColor(textBox: GuiElementTextBox?): Int {
        return ColorUtil.to32BitColor(255, 200, 200, 200)
    }

    override fun onIntruderInteraction() {
        this.cannotEditTimer = 50
    }

    override fun onIntruderInteraction(textBox: GuiElementTextBox?) {
        this.cannotEditTimer = 50
    }

    companion object {
        private val airLockControllerGui =
            ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/gui/air_lock_controller.png")
    }
}
