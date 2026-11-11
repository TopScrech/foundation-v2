package micdoodle8.mods.galacticraft.planets.asteroids.client.gui

import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementTextBox.ITextBoxCallback
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket
import java.util.ArrayList

import com.google.common.collect.Maps
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.client.gui.container.GuiContainerGC
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementInfoRegion
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementTextBox
import micdoodle8.mods.galacticraft.core.energy.EnergyDisplayHelper
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.util.ColorUtil
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.PlayerUtil
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import micdoodle8.mods.galacticraft.planets.asteroids.inventory.ContainerShortRangeTelepad
import micdoodle8.mods.galacticraft.planets.asteroids.network.PacketSimpleAsteroids
import micdoodle8.mods.galacticraft.planets.asteroids.tile.TileEntityShortRangeTelepad
import net.minecraft.client.gui.GuiButton
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.entity.player.Inventory as InventoryPlayer
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraftforge.fml.client.FMLClientHandler
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import java.io.IOException
import kotlin.math.min

open class GuiShortRangeTelepad constructor(
    playerInventory: InventoryPlayer?,
    telepad: TileEntityShortRangeTelepad
) : GuiContainerGC(
    ContainerShortRangeTelepad(
        (playerInventory)!!,
        telepad,
        FMLClientHandler.instance().getClient().player
    )
), GuiElementTextBox.ITextBoxCallback {
    private val telepad: TileEntityShortRangeTelepad

    private var enableControllerButton: GuiButton? = null
    private var address: GuiElementTextBox? = null
    private var targetAddress: GuiElementTextBox? = null
    private val electricInfoRegion = GuiElementInfoRegion(0, 0, 52, 9, null, 0, 0, this)

    private val cannotEditMap: MutableMap<Int,Int> = Maps.newHashMap<Int,Int>()

    init {
        this.ySize = 209
        this.telepad = telepad
    }

    override fun drawScreen(par1: Int, par2: Int, par3: Float) {
        if (this.telepad.disableCooldown > 0) {
            this.enableControllerButton!!.enabled = false
        } else {
            this.enableControllerButton!!.enabled = true
        }

        this.enableControllerButton!!.displayString =
            if (this.telepad.getDisabled(0)) GCCoreUtil.translate("gui.button.enable.name") else GCCoreUtil.translate("gui.button.disable.name")

        super.drawScreen(par1, par2, par3)
    }

    @Throws(IOException::class)
    override fun keyTyped(keyChar: Char, keyID: Int) {
        if (keyID != Keyboard.KEY_ESCAPE && keyID != this.mc.gameSettings.keyBindInventory.getKeyCode()) {
            if (this.address!!.keyTyped(keyChar, keyID)) {
                return
            }

            if (this.targetAddress!!.keyTyped(keyChar, keyID)) {
                return
            }
        }

        super.keyTyped(keyChar, keyID)
    }

    override fun initGui() {
        super.initGui()
        this.buttonList.clear()
        val var5: Int = (this.width - this.xSize) / 2
        val var6: Int = (this.height - this.ySize) / 2
        this.enableControllerButton =
            GuiButton(0, var5 + 70 + 124 - 72, var6 + 16, 48, 20, GCCoreUtil.translate("gui.button.enable.name"))
        this.address = GuiElementTextBox(1, this, var5 + 66, var6 + 16, 48, 20, "", true, 6, false)
        this.targetAddress = GuiElementTextBox(2, this, var5 + 122, var6 + 16 + 22, 48, 20, "", true, 6, false)
        this.buttonList.add(this.enableControllerButton)
        this.buttonList.add(this.address)
        this.buttonList.add(this.targetAddress)
        this.electricInfoRegion.tooltipStrings = (ArrayList<String?>())!!
        this.electricInfoRegion.xPosition = (this.width - this.xSize) / 2 + 98
        this.electricInfoRegion.yPosition = (this.height - this.ySize) / 2 + 113
        this.electricInfoRegion.parentWidth = this.width
        this.electricInfoRegion.parentHeight = this.height
        this.infoRegions.add(this.electricInfoRegion)
        var batterySlotDesc: MutableList<String?> = ArrayList<String?>()
        batterySlotDesc.add(GCCoreUtil.translate("gui.battery_slot.desc.0"))
        batterySlotDesc.add(GCCoreUtil.translate("gui.battery_slot.desc.1"))
        this.infoRegions.add(
            GuiElementInfoRegion(
                (this.width - this.xSize) / 2 + 151,
                (this.height - this.ySize) / 2 + 104,
                18,
                18,
                batterySlotDesc,
                this.width,
                this.height,
                this
            )
        )
        batterySlotDesc = ArrayList<String?>()
        batterySlotDesc.addAll(GCCoreUtil.translateWithSplit("gui.telepad.desc.0"))
        this.infoRegions.add(
            GuiElementInfoRegion(
                (this.width - this.xSize) / 2 + 5,
                (this.height - this.ySize) / 2 + 20,
                59,
                13,
                batterySlotDesc,
                this.width,
                this.height,
                this
            )
        )
        batterySlotDesc = ArrayList<String?>()
        batterySlotDesc.addAll(GCCoreUtil.translateWithSplit("gui.telepad.desc.1"))
        this.infoRegions.add(
            GuiElementInfoRegion(
                (this.width - this.xSize) / 2 + 5,
                (this.height - this.ySize) / 2 + 42,
                117,
                13,
                batterySlotDesc,
                this.width,
                this.height,
                this
            )
        )
    }

    //    @Override
    //    protected void mouseClicked(int px, int py, int par3)
    //    {
    //        super.mouseClicked(px, py, par3);
    //    }
    override fun actionPerformed(par1GuiButton: GuiButton) {
        if (par1GuiButton.enabled) {
            when (par1GuiButton.id) {
                0 -> (GalacticraftCore.packetPipeline)!!.sendToServer(
                    PacketSimple(
                        PacketSimple.EnumSimplePacket.S_UPDATE_DISABLEABLE_BUTTON,
                        GCCoreUtil.getDimensionID(mc.world),
                        arrayOf<Any>(this.telepad.getPos(), 0)
                    )
                )

                else -> {}
            }
        }
    }

    override fun drawGuiContainerForegroundLayer(par1: Int, par2: Int) {
        val displayString: String = this.telepad.getName()
        this.fontRenderer.drawString(
            displayString,
            this.xSize / 2 - this.fontRenderer.getStringWidth(displayString) / 2,
            5,
            4210752
        )

        this.fontRenderer.drawString(GCCoreUtil.translate("container.inventory"), 8, 115, 4210752)
        this.fontRenderer.drawString(GCCoreUtil.translate("gui.message.address.name") + ":", 7, 22, 4210752)
        this.fontRenderer.drawString(GCCoreUtil.translate("gui.message.dest_address.name") + ":", 7, 44, 4210752)
        this.fontRenderer.drawString(this.telepad.getReceivingStatus(), 7, 66, 4210752)
        if (this.telepad.getReceivingStatus() != this.telepad.getSendingStatus()) {
            this.fontRenderer.drawString(this.telepad.getSendingStatus(), 7, 88, 4210752)
        }
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
            this.telepad.getEnergyStoredGC(),
            this.telepad.getMaxEnergyStoredGC(),
            electricityDesc
        )
        this.electricInfoRegion.tooltipStrings = electricityDesc

        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)

        if (this.telepad.getEnergyStoredGC() > 0) {
            val scale: Int = this.telepad.getScaledElecticalLevel(54)
            this.drawTexturedModalRect(var5 + 99, var6 + 114, 176, 0, min(scale, 54), 7)
        }

        GL11.glPopMatrix()
    }

    override fun canPlayerEdit(textBox: GuiElementTextBox?, player: EntityPlayer?): Boolean {
        return PlayerUtil.getName(player) == this.telepad.getOwner()
    }

    override fun onTextChanged(textBox: GuiElementTextBox?, newText: String?) {
        if (textBox == this.address) {
            this.telepad.address = (textBox)!!.getIntegerValue()
            (GalacticraftCore.packetPipeline)!!.sendToServer(
                PacketSimpleAsteroids(
                    PacketSimpleAsteroids.EnumSimplePacketAsteroids.S_UPDATE_ADVANCED_GUI,
                    GCCoreUtil.getDimensionID(this.mc.world),
                    arrayOf<Any>(0, this.telepad.getPos(), this.telepad.address)
                )
            )
        } else if (textBox == this.targetAddress) {
            this.telepad.targetAddress = (textBox)!!.getIntegerValue()
            (GalacticraftCore.packetPipeline)!!.sendToServer(
                PacketSimpleAsteroids(
                    PacketSimpleAsteroids.EnumSimplePacketAsteroids.S_UPDATE_ADVANCED_GUI,
                    GCCoreUtil.getDimensionID(this.mc.world),
                    arrayOf<Any>(1, this.telepad.getPos(), this.telepad.targetAddress)
                )
            )
        }
    }

    override fun getInitialText(textBox: GuiElementTextBox?): String {
        if (textBox == this.address) {
            return this.telepad.address.toString()
        } else if (textBox == this.targetAddress) {
            return this.telepad.targetAddress.toString()
        }

        return ""
    }

    override fun getTextColor(textBox: GuiElementTextBox?): Int {
        if (textBox == this.address) {
            return if (this.telepad.addressValid) ColorUtil.to32BitColor(255, 20, 255, 20) else ColorUtil.to32BitColor(
                255,
                255,
                25,
                25
            )
        } else if (textBox == this.targetAddress) {
            if (this.telepad.targetAddressResult == TileEntityShortRangeTelepad.EnumTelepadSearchResult.VALID) {
                return ColorUtil.to32BitColor(255, 20, 255, 20)
            } else if (this.telepad.targetAddressResult == TileEntityShortRangeTelepad.EnumTelepadSearchResult.TARGET_DISABLED) {
                return ColorUtil.to32BitColor(255, 255, 170, 25)
            } else {
                return ColorUtil.to32BitColor(255, 255, 25, 25)
            }
        }

        return 0
    }

    override fun onIntruderInteraction(textBox: GuiElementTextBox?) {
        this.cannotEditMap.put((textBox)!!.id, 50)
    }

    companion object {
        private val launchControllerGui =
            ResourceLocation.fromNamespaceAndPath(GalacticraftPlanets.ASSET_PREFIX, "textures/gui/short_range_telepad.png")
    }
}
