package micdoodle8.mods.galacticraft.planets.mars.client.gui

import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementTextBox.ITextBoxCallback
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket
import micdoodle8.mods.galacticraft.planets.mars.network.PacketSimpleMars.EnumSimplePacketMars
import java.util.ArrayList
import java.util.Random

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.client.gui.container.GuiContainerGC
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementInfoRegion
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementTextBox
import micdoodle8.mods.galacticraft.core.energy.EnergyDisplayHelper
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.util.ColorUtil
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.PlayerUtil
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import micdoodle8.mods.galacticraft.planets.mars.inventory.ContainerLaunchController
import micdoodle8.mods.galacticraft.planets.mars.network.PacketSimpleMars
import micdoodle8.mods.galacticraft.planets.mars.tile.TileEntityLaunchController
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiLabel
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ChatAllowedCharacters
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.client.FMLClientHandler
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL12
import java.io.IOException
import java.util.*
import kotlin.math.min

open class GuiLaunchController constructor(
    playerInventory: InventoryPlayer?,
    launchController: TileEntityLaunchController
) : GuiContainerGC(
    ContainerLaunchController(
        (playerInventory)!!,
        launchController,
        FMLClientHandler.instance().getClient().player
    )
), GuiElementTextBox.ITextBoxCallback {
    private val launchController: TileEntityLaunchController

    private var enableControllerButton: GuiButton? = null
    private var hideDestinationFrequency: GuiButton? = null
    private var openAdvancedConfig: GuiButton? = null
    private var frequency: GuiElementTextBox? = null
    private var destinationFrequency: GuiElementTextBox? = null
    private val electricInfoRegion = GuiElementInfoRegion(0, 0, 52, 9, null, 0, 0, this)
    private val waterTankInfoRegion = GuiElementInfoRegion(0, 0, 41, 28, null, 0, 0, this)

    private var cannotEditTimer = 0

    init {
        this.ySize = 209
        this.launchController = launchController
    }

    override fun drawScreen(par1: Int, par2: Int, par3: Float) {
        if (this.launchController.disableCooldown > 0) {
            this.enableControllerButton!!.enabled = false
            this.hideDestinationFrequency!!.enabled = false
        } else {
            val isOwner: Boolean = PlayerUtil.getName(this.mc.player) == this.launchController.getOwnerName()
            this.enableControllerButton!!.enabled = isOwner
            this.hideDestinationFrequency!!.enabled = isOwner
        }

        this.enableControllerButton!!.displayString =
            if (this.launchController.getDisabled(0)) GCCoreUtil.translate("gui.button.enable.name") else GCCoreUtil.translate(
                "gui.button.disable.name"
            )
        this.hideDestinationFrequency!!.displayString =
            if (!this.launchController.getDisabled(2)) GCCoreUtil.translate("gui.button.unhide_dest.name") else GCCoreUtil.translate(
                "gui.button.hide_dest.name"
            )
        // Hacky way of rendering buttons properly, possibly bugs here:
        val buttonList: MutableList<GuiButton?> = ArrayList<GuiButton?>(this.buttonList)
        val labelList: MutableList<GuiLabel?> = ArrayList<GuiLabel?>(this.labelList)
        val infoRegions: MutableList<GuiElementInfoRegion?> = ArrayList<GuiElementInfoRegion?>(this.infoRegions)
        this.buttonList.clear()
        this.labelList.clear()
        this.infoRegions.clear()
        super.drawScreen(par1, par2, par3)

        GL11.glColor3f(1f, 1f, 1f)
        GL11.glDisable(GL12.GL_RESCALE_NORMAL)
        RenderHelper.disableStandardItemLighting()
        GL11.glDisable(GL11.GL_LIGHTING)
        GL11.glDisable(GL11.GL_DEPTH_TEST)

        var k: Int
        k = 0
        while (k < buttonList.size) {
            (buttonList.get(k) as GuiButton).drawButton(this.mc, par1, par2, par3)
            ++k
        }

        k = 0
        while (k < labelList.size) {
            (labelList.get(k) as GuiLabel).drawLabel(this.mc, par1, par2)
            ++k
        }

        k = 0
        while (k < infoRegions.size) {
            infoRegions.get(k)!!.drawRegion(par1, par2)
            ++k
        }

        this.buttonList = buttonList
        this.labelList = labelList
        this.infoRegions = (infoRegions as MutableList<GuiElementInfoRegion>)

        //		GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LIGHTING)
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        RenderHelper.enableStandardItemLighting()

        if (Math.random() < 0.025 && !destinationFrequency!!.isTextFocused) {
            if (PlayerUtil.getName(this.mc.player) != this.launchController.getOwnerName() && !this.launchController.getDisabled(
                    2
                )
            ) {
                // in case the player is not equal to the owner of the controller,
                // scramble the destination number such that other players can't
                // fly to it directly
                val r: Random = Random()
                var fakefrequency: String = ""
                for (i: Int in 0..<this.destinationFrequency!!.getMaxLength()) {
                    fakefrequency += (r.nextInt(126 - 33) + 33).toChar()
                }
                destinationFrequency!!.text = fakefrequency
            } else {
                destinationFrequency!!.text = this.launchController.destFrequency.toString()
            }
        }
    }

    @Throws(IOException::class)
    override fun keyTyped(keyChar: Char, keyID: Int) {
        if (keyID != Keyboard.KEY_ESCAPE && keyID != this.mc.gameSettings.keyBindInventory.getKeyCode()) {
            if (this.frequency!!.keyTyped(keyChar, keyID)) {
                return
            }

            if (this.destinationFrequency!!.keyTyped(keyChar, keyID)) {
                return
            }
        }

        super.keyTyped(keyChar, keyID)
    }

    open fun isValid(string: String): Boolean {
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
    }

    override fun initGui() {
        super.initGui()
        this.buttonList.clear()
        val xLeft: Int = (this.width - this.xSize) / 2
        val yTop: Int = (this.height - this.ySize) / 2
        this.enableControllerButton =
            GuiButton(0, xLeft + 70 + 124 - 72, yTop + 16, 48, 20, GCCoreUtil.translate("gui.button.enable.name"))
        this.frequency = GuiElementTextBox(4, this, xLeft + 66, yTop + 16, 48, 20, "", true, 6, false)
        this.destinationFrequency = GuiElementTextBox(5, this, xLeft + 45, yTop + 16 + 22, 48, 20, "", true, 6, false)
        this.hideDestinationFrequency =
            GuiButton(6, xLeft + 95, yTop + 16 + 22, 39, 20, GCCoreUtil.translate("gui.button.hide_dest.name"))
        this.openAdvancedConfig =
            GuiButton(7, xLeft + 48, yTop + 62, 80, 20, GCCoreUtil.translate("gui.launch_controller.advanced") + "...")
        this.buttonList.add(this.enableControllerButton)
        this.buttonList.add(this.frequency)
        this.buttonList.add(this.destinationFrequency)
        this.buttonList.add(this.hideDestinationFrequency)
        this.buttonList.add(this.openAdvancedConfig)
        this.electricInfoRegion.tooltipStrings = ArrayList<String?>()
        this.electricInfoRegion.xPosition = xLeft + 98
        this.electricInfoRegion.yPosition = yTop + 113
        this.electricInfoRegion.parentWidth = this.width
        this.electricInfoRegion.parentHeight = this.height
        this.infoRegions.add(this.electricInfoRegion)
        var batterySlotDesc: MutableList<String?> = ArrayList<String?>()
        batterySlotDesc.add(GCCoreUtil.translate("gui.battery_slot.desc.0"))
        batterySlotDesc.add(GCCoreUtil.translate("gui.battery_slot.desc.1"))
        this.infoRegions.add(
            GuiElementInfoRegion(
                xLeft + 151,
                yTop + 104,
                18,
                18,
                batterySlotDesc,
                this.width,
                this.height,
                this
            )
        )
        batterySlotDesc = ArrayList<String?>()
        batterySlotDesc.addAll(GCCoreUtil.translateWithSplit("gui.launch_controller.desc.0"))
        this.infoRegions.add(
            GuiElementInfoRegion(
                xLeft + 5,
                yTop + 20,
                109,
                13,
                batterySlotDesc,
                this.width,
                this.height,
                this
            )
        )
        batterySlotDesc = ArrayList<String?>()
        batterySlotDesc.addAll(GCCoreUtil.translateWithSplit("gui.launch_controller.desc.1"))
        this.infoRegions.add(
            GuiElementInfoRegion(
                xLeft + 5,
                yTop + 42,
                87,
                13,
                batterySlotDesc,
                this.width,
                this.height,
                this
            )
        )
        batterySlotDesc = ArrayList<String?>()
        batterySlotDesc.addAll(GCCoreUtil.translateWithSplit("gui.launch_controller.desc.4"))
        this.infoRegions.add(
            GuiElementInfoRegion(
                xLeft + 95,
                yTop + 38,
                38,
                20,
                batterySlotDesc,
                this.width,
                this.height,
                this
            )
        )
    }

    @Throws(IOException::class)
    override fun mouseClicked(px: Int, py: Int, par3: Int) {
        super.mouseClicked(px, py, par3)
    }

    override fun actionPerformed(par1GuiButton: GuiButton) {
        if (PlayerUtil.getName(this.mc.player) != this.launchController.getOwnerName()) {
            this.cannotEditTimer = 50
            return
        }

        if (par1GuiButton.enabled) {
            when (par1GuiButton.id) {
                0 -> (GalacticraftCore.packetPipeline)!!.sendToServer(
                    PacketSimple(
                        PacketSimple.EnumSimplePacket.S_UPDATE_DISABLEABLE_BUTTON,
                        mc.world.provider.getDimension(),
                        arrayOf<Any>(this.launchController.getPos(), 0)
                    )
                )

                6 -> (GalacticraftCore.packetPipeline)!!.sendToServer(
                    PacketSimple(
                        PacketSimple.EnumSimplePacket.S_UPDATE_DISABLEABLE_BUTTON,
                        mc.world.provider.getDimension(),
                        arrayOf<Any>(this.launchController.getPos(), 2)
                    )
                )

                7 -> (GalacticraftCore.packetPipeline)!!.sendToServer(
                    PacketSimpleMars(
                        PacketSimpleMars.EnumSimplePacketMars.S_SWITCH_LAUNCH_CONTROLLER_GUI,
                        GCCoreUtil.getDimensionID(mc.world),
                        arrayOf<Any>(this.launchController.getPos(), 0)
                    )
                )

                else -> {}
            }
        }
    }

    override fun drawGuiContainerForegroundLayer(par1: Int, par2: Int) {
        var displayString: String = this.launchController.getOwnerName() + "\'s " + this.launchController.getName()
        this.fontRenderer.drawString(
            displayString,
            this.xSize / 2 - this.fontRenderer.getStringWidth(displayString) / 2,
            5,
            4210752
        )

        if (this.cannotEditTimer > 0) {
            this.fontRenderer.drawString(
                this.launchController.getOwnerName(),
                this.xSize / 2 - this.fontRenderer.getStringWidth(displayString) / 2,
                5,
                if (this.cannotEditTimer % 30 < 15) ColorUtil.to32BitColor(255, 255, 100, 100) else 4210752
            )
            this.cannotEditTimer--
        }

        this.fontRenderer.drawString(GCCoreUtil.translate("container.inventory"), 8, 115, 4210752)
        displayString = this.getStatus()
        this.fontRenderer.drawString(
            displayString,
            this.xSize / 2 - this.fontRenderer.getStringWidth(displayString) / 2,
            86,
            4210752
        )
        //		displayString = ElectricityDisplay.getDisplay(this.launchController.ueWattsPerTick * 20, ElectricUnit.WATT);
        //		this.fontRenderer.drawString(displayString, this.xSize - 26 - this.fontRenderer.getStringWidth(displayString), 94, 4210752);
        //		displayString = ElectricityDisplay.getDisplay(this.launchController.getVoltage(), ElectricUnit.VOLTAGE);
        //		this.fontRenderer.drawString(displayString, this.xSize - 26 - this.fontRenderer.getStringWidth(displayString), 104, 4210752);
        this.fontRenderer.drawString(GCCoreUtil.translate("gui.message.frequency.name") + ":", 7, 22, 4210752)
        this.fontRenderer.drawString(GCCoreUtil.translate("gui.message.dest_frequency.name") + ":", 7, 44, 4210752)
    }

    private fun getStatus(): String {
        if (!this.launchController.frequencyValid) {
            return EnumColor.RED.toString() + GCCoreUtil.translate("gui.message.invalid_freq.name")
        }

        if (this.launchController.getEnergyStoredGC() <= 0.0f) {
            return EnumColor.RED.toString() + GCCoreUtil.translate("gui.message.no_energy.name")
        }

        if (this.launchController.getDisabled(0)) {
            return EnumColor.ORANGE.toString() + GCCoreUtil.translate("gui.status.disabled.name")
        }

        return EnumColor.BRIGHT_GREEN.toString() + GCCoreUtil.translate("gui.status.active.name")
    }

    override fun drawGuiContainerBackgroundLayer(par1: Float, par2: Int, par3: Int) {
        GL11.glPushMatrix()
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        this.mc.renderEngine.bindTexture(launchControllerGui)
        val var5: Int = (this.width - this.xSize) / 2
        val var6: Int = (this.height - this.ySize) / 2
        this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize)

        val electricityDesc: MutableList<String?> = ArrayList<String?>()
        electricityDesc.add(GCCoreUtil.translate("gui.energy_storage.desc.0"))
        EnergyDisplayHelper.getEnergyDisplayTooltip(
            this.launchController.getEnergyStoredGC(),
            this.launchController.getMaxEnergyStoredGC(),
            electricityDesc
        )
        this.electricInfoRegion.tooltipStrings = electricityDesc

        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)

        if (this.launchController.getEnergyStoredGC() > 0) {
            val scale: Int = this.launchController.getScaledElecticalLevel(54)
            this.drawTexturedModalRect(var5 + 99, var6 + 114, 176, 0, min(scale, 54), 7)
        }

        GL11.glPopMatrix()
    }

    override fun canPlayerEdit(textBox: GuiElementTextBox?, player: EntityPlayer?): Boolean {
        return PlayerUtil.getName(player) == this.launchController.getOwnerName()
    }

    override fun onTextChanged(textBox: GuiElementTextBox?, newText: String?) {
        if (PlayerUtil.getName(this.mc.player) == this.launchController.getOwnerName()) {
            if (textBox == this.frequency) {
                this.launchController.frequency = (textBox)!!.getIntegerValue()
                (GalacticraftCore.packetPipeline)!!.sendToServer(
                    PacketSimpleMars(
                        PacketSimpleMars.EnumSimplePacketMars.S_UPDATE_ADVANCED_GUI,
                        GCCoreUtil.getDimensionID(mc.world),
                        arrayOf<Any>(0, this.launchController.getPos(), this.launchController.frequency)
                    )
                )
            } else if (textBox == this.destinationFrequency) {
                this.launchController.destFrequency = (textBox)!!.getIntegerValue()
                (GalacticraftCore.packetPipeline)!!.sendToServer(
                    PacketSimpleMars(
                        PacketSimpleMars.EnumSimplePacketMars.S_UPDATE_ADVANCED_GUI,
                        GCCoreUtil.getDimensionID(mc.world),
                        arrayOf<Any>(2, this.launchController.getPos(), this.launchController.destFrequency)
                    )
                )
            }
        }
    }

    override fun getInitialText(textBox: GuiElementTextBox?): String {
        if (textBox == this.frequency) {
            return this.launchController.frequency.toString()
        } else if (textBox == this.destinationFrequency) {
            if (PlayerUtil.getName(this.mc.player) == this.launchController.getOwnerName() || this.launchController.getDisabled(
                    2
                )
            ) {
                return this.launchController.destFrequency.toString()
            } else {
                // in case the player is not equal to the owner of the controller,
                // scramble the destination number such that other players can't
                // fly to it directly
                val r: Random = Random()
                var fakefrequency: String = ""
                for (i: Int in 0..<this.destinationFrequency!!.getMaxLength()) {
                    fakefrequency += (r.nextInt(126 - 33) + 33).toChar()
                }
                return fakefrequency
            }
        }

        return ""
    }

    override fun getTextColor(textBox: GuiElementTextBox?): Int {
        if (textBox == this.frequency) {
            return if (this.launchController.frequencyValid) ColorUtil.to32BitColor(
                255,
                20,
                255,
                20
            ) else ColorUtil.to32BitColor(255, 255, 25, 25)
        } else if (textBox == this.destinationFrequency) {
            return if (this.launchController.destFrequencyValid) ColorUtil.to32BitColor(
                255,
                20,
                255,
                20
            ) else ColorUtil.to32BitColor(255, 255, 25, 25)
        }

        return 0
    }

    override fun onIntruderInteraction(textBox: GuiElementTextBox?) {
        this.cannotEditTimer = 50
    }

    companion object {
        private val launchControllerGui =
            ResourceLocation(GalacticraftPlanets.ASSET_PREFIX, "textures/gui/launch_controller.png")
    }
}
