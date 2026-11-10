package micdoodle8.mods.galacticraft.planets.mars.client.gui

import micdoodle8.mods.galacticraft.api.prefab.entity.EntityAutoRocket.EnumAutoLaunch
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementCheckbox.ICheckBoxCallback
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementDropdown.IDropboxCallback
import micdoodle8.mods.galacticraft.planets.mars.network.PacketSimpleMars.EnumSimplePacketMars
import java.util.ArrayList

import micdoodle8.mods.galacticraft.api.prefab.entity.EntityAutoRocket
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.client.gui.container.GuiContainerGC
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementCheckbox
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementDropdown
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementInfoRegion
import micdoodle8.mods.galacticraft.core.util.ColorUtil
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
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.client.FMLClientHandler
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL12
import kotlin.math.min

open class GuiLaunchControllerAdvanced constructor(
    playerInventory: InventoryPlayer?,
    launchController: TileEntityLaunchController
) : GuiContainerGC(
    ContainerLaunchController(
        (playerInventory)!!,
        launchController,
        FMLClientHandler.instance().getClient().player
    )
), GuiElementDropdown.IDropboxCallback, GuiElementCheckbox.ICheckBoxCallback {
    private val launchController: TileEntityLaunchController

    private var enablePadRemovalButton: GuiElementCheckbox? = null
    private var launchWhenCheckbox: GuiElementCheckbox? = null
    private var dropdownTest: GuiElementDropdown? = null
    private var closeAdvancedConfig: GuiButton? = null

    private var cannotEditTimer = 0

    init {
        this.ySize = 209
        this.launchController = launchController
    }

    override fun drawScreen(par1: Int, par2: Int, par3: Float) {
        if (this.launchController.disableCooldown > 0) {
            this.enablePadRemovalButton!!.enabled = false
        } else {
            val isOwner: Boolean = PlayerUtil.getName(this.mc.player) == this.launchController.getOwnerName()
            this.enablePadRemovalButton!!.enabled = isOwner
        }

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
    }

    override fun initGui() {
        super.initGui()
        this.buttonList.clear()
        val xLeft: Int = (this.width - this.xSize) / 2
        val yTop: Int = (this.height - this.ySize) / 2
        this.enablePadRemovalButton = GuiElementCheckbox(
            1,
            this,
            this.width / 2 - 61,
            yTop + 20,
            GCCoreUtil.translate("gui.message.remove_pad.name")
        )
        this.launchWhenCheckbox = GuiElementCheckbox(
            2,
            this,
            this.width / 2 - 61,
            yTop + 38,
            GCCoreUtil.translate("gui.message.launch_when.name") + ": "
        )
        this.dropdownTest = GuiElementDropdown(
            3,
            this,
            xLeft + 52,
            yTop + 52,
            (EntityAutoRocket.EnumAutoLaunch.CARGO_IS_UNLOADED.getTitle())!!,
            (EntityAutoRocket.EnumAutoLaunch.CARGO_IS_FULL.getTitle())!!,
            (EntityAutoRocket.EnumAutoLaunch.ROCKET_IS_FUELED.getTitle())!!,
            (EntityAutoRocket.EnumAutoLaunch.INSTANT.getTitle())!!,
            (EntityAutoRocket.EnumAutoLaunch.TIME_10_SECONDS.getTitle())!!,
            (EntityAutoRocket.EnumAutoLaunch.TIME_30_SECONDS.getTitle())!!,
            (EntityAutoRocket.EnumAutoLaunch.TIME_1_MINUTE.getTitle())!!,
            (EntityAutoRocket.EnumAutoLaunch.REDSTONE_SIGNAL.getTitle())!!
        )
        this.closeAdvancedConfig = GuiButton(4, xLeft + 5, yTop + 5, 20, 20, "<")
        this.buttonList.add(this.enablePadRemovalButton)
        this.buttonList.add(this.launchWhenCheckbox)
        this.buttonList.add(this.dropdownTest)
        this.buttonList.add(this.closeAdvancedConfig)
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
        batterySlotDesc.addAll(GCCoreUtil.translateWithSplit("gui.launch_controller.desc.2"))
        this.infoRegions.add(
            GuiElementInfoRegion(
                xLeft + 27,
                yTop + 20,
                13,
                13,
                batterySlotDesc,
                this.width,
                this.height,
                this
            )
        )
        batterySlotDesc = ArrayList<String?>()
        batterySlotDesc.addAll(GCCoreUtil.translateWithSplit("gui.launch_controller.desc.3"))
        this.infoRegions.add(
            GuiElementInfoRegion(
                xLeft + 52,
                yTop + 53,
                99,
                13,
                batterySlotDesc,
                this.width,
                this.height,
                this
            )
        )
    }

    override fun actionPerformed(button: GuiButton) {
        if (PlayerUtil.getName(this.mc.player) != this.launchController.getOwnerName()) {
            this.cannotEditTimer = 50
            return
        }

        if (button.enabled) {
            when (button.id) {
                4 -> (GalacticraftCore.packetPipeline)!!.sendToServer(
                    PacketSimpleMars(
                        PacketSimpleMars.EnumSimplePacketMars.S_SWITCH_LAUNCH_CONTROLLER_GUI,
                        GCCoreUtil.getDimensionID(mc.world),
                        arrayOf<Any>(this.launchController.getPos(), 1)
                    )
                )

                else -> {}
            }
        }
    }

    override fun drawGuiContainerForegroundLayer(par1: Int, par2: Int) {
        val displayString: String =
            GCCoreUtil.translate("gui.launch_controller.owner") + ": " + this.launchController.getOwnerName()
        this.fontRenderer.drawString(
            displayString,
            this.xSize - this.fontRenderer.getStringWidth(displayString) - 5,
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
    }

    override fun drawGuiContainerBackgroundLayer(par1: Float, par2: Int, par3: Int) {
        GL11.glPushMatrix()
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        this.mc.renderEngine.bindTexture(launchControllerGui)
        val var5: Int = (this.width - this.xSize) / 2
        val var6: Int = (this.height - this.ySize) / 2
        this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize)

        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)

        if (this.launchController.getEnergyStoredGC() > 0) {
            val scale: Int = this.launchController.getScaledElecticalLevel(54)
            this.drawTexturedModalRect(var5 + 99, var6 + 114, 176, 0, min(scale, 54), 7)
        }

        GL11.glPopMatrix()
    }

    override fun canBeClickedBy(dropdown: GuiElementDropdown?, player: EntityPlayer?): Boolean {
        if (dropdown == this.dropdownTest) {
            return PlayerUtil.getName(player) == this.launchController.getOwnerName()
        }

        return false
    }

    override fun onSelectionChanged(dropdown: GuiElementDropdown?, selection: Int) {
        if (dropdown == this.dropdownTest) {
            this.launchController.launchDropdownSelection = selection
            (GalacticraftCore.packetPipeline)!!.sendToServer(
                PacketSimpleMars(
                    PacketSimpleMars.EnumSimplePacketMars.S_UPDATE_ADVANCED_GUI,
                    GCCoreUtil.getDimensionID(mc.world),
                    arrayOf<Any>(1, this.launchController.getPos(), this.launchController.launchDropdownSelection)
                )
            )
        }
    }

    override fun getInitialSelection(dropdown: GuiElementDropdown?): Int {
        if (dropdown == this.dropdownTest) {
            return this.launchController.launchDropdownSelection
        }

        return 0
    }

    override fun onSelectionChanged(checkbox: GuiElementCheckbox?, newSelected: Boolean) {
        if (checkbox == this.enablePadRemovalButton) {
            this.launchController.launchPadRemovalDisabled = !newSelected
            (GalacticraftCore.packetPipeline)!!.sendToServer(
                PacketSimpleMars(
                    PacketSimpleMars.EnumSimplePacketMars.S_UPDATE_ADVANCED_GUI,
                    GCCoreUtil.getDimensionID(mc.world),
                    arrayOf<Any>(
                        3,
                        this.launchController.getPos(),
                        if (this.launchController.launchPadRemovalDisabled) 1 else 0
                    )
                )
            )
        } else if (checkbox == this.launchWhenCheckbox) {
            this.launchController.launchSchedulingEnabled = newSelected
            (GalacticraftCore.packetPipeline)!!.sendToServer(
                PacketSimpleMars(
                    PacketSimpleMars.EnumSimplePacketMars.S_UPDATE_ADVANCED_GUI,
                    GCCoreUtil.getDimensionID(mc.world),
                    arrayOf<Any>(
                        4,
                        this.launchController.getPos(),
                        if (this.launchController.launchSchedulingEnabled) 1 else 0
                    )
                )
            )
        }
    }

    override fun canPlayerEdit(checkbox: GuiElementCheckbox?, player: EntityPlayer?): Boolean {
        return PlayerUtil.getName(player) == this.launchController.getOwnerName()
    }

    override fun getInitiallySelected(checkbox: GuiElementCheckbox?): Boolean {
        if (checkbox == this.enablePadRemovalButton) {
            return !this.launchController.launchPadRemovalDisabled
        } else if (checkbox == this.launchWhenCheckbox) {
            return this.launchController.launchSchedulingEnabled
        }

        return false
    }

    override fun onIntruderInteraction() {
        this.cannotEditTimer = 50
    }

    companion object {
        private val launchControllerGui =
            ResourceLocation(GalacticraftPlanets.ASSET_PREFIX, "textures/gui/launch_controller.png")
    }
}
