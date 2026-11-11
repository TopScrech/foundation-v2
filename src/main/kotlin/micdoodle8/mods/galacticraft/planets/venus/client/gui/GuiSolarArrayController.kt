package micdoodle8.mods.galacticraft.planets.venus.client.gui

import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket

import org.lwjgl.opengl.GL11
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.client.gui.container.GuiContainerGC
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementInfoRegion
import micdoodle8.mods.galacticraft.core.energy.EnergyDisplayHelper
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import micdoodle8.mods.galacticraft.planets.venus.inventory.ContainerSolarArrayController
import micdoodle8.mods.galacticraft.planets.venus.tile.TileEntitySolarArrayController
import net.minecraft.client.gui.GuiButton
import net.minecraft.world.entity.player.Inventory as InventoryPlayer
import net.minecraft.resources.Identifier as ResourceLocation
import java.util.ArrayList
import kotlin.math.floor
import kotlin.math.min

open class GuiSolarArrayController constructor(
    par1InventoryPlayer: InventoryPlayer?,
    private val solarController: TileEntitySolarArrayController
) : GuiContainerGC(ContainerSolarArrayController((par1InventoryPlayer)!!, solarController)) {
    private var buttonEnableSolar: GuiButton? = null
    private val electricInfoRegion = GuiElementInfoRegion(
        (this.width - this.xSize) / 2 + 107,
        (this.height - this.ySize) / 2 + 101,
        56,
        9,
        ArrayList<String?>(),
        this.width,
        this.height,
        this
    )

    init {
        this.ySize = 209
        this.xSize = 176
    }

    override fun actionPerformed(par1GuiButton: GuiButton) {
        when (par1GuiButton.id) {
            0 -> (GalacticraftCore.packetPipeline)!!.sendToServer(
                PacketSimple(
                    PacketSimple.EnumSimplePacket.S_UPDATE_DISABLEABLE_BUTTON,
                    GCCoreUtil.getDimensionID(this.mc.world),
                    arrayOf<Any>(this.solarController.getPos(), 0)
                )
            )
        }
    }

    override fun initGui() {
        super.initGui()
        val electricityDesc: MutableList<String?> = ArrayList<String?>()
        electricityDesc.add(GCCoreUtil.translate("gui.energy_storage.desc.0"))
        electricityDesc.add(
            EnumColor.YELLOW.toString() + GCCoreUtil.translate("gui.energy_storage.desc.1") + (floor(
                this.solarController.getEnergyStoredGC().toDouble()
            ).toInt().toString() + " / " + floor(this.solarController.getMaxEnergyStoredGC().toDouble()).toInt())
        )
        this.electricInfoRegion.tooltipStrings = electricityDesc
        this.electricInfoRegion.xPosition = (this.width - this.xSize) / 2 + 96
        this.electricInfoRegion.yPosition = (this.height - this.ySize) / 2 + 24
        this.electricInfoRegion.parentWidth = this.width
        this.electricInfoRegion.parentHeight = this.height
        this.infoRegions.add(this.electricInfoRegion)
        val batterySlotDesc: MutableList<String?> = ArrayList<String?>()
        batterySlotDesc.add(GCCoreUtil.translate("gui.battery_slot.desc.0"))
        batterySlotDesc.add(GCCoreUtil.translate("gui.battery_slot.desc.1"))
        this.infoRegions.add(
            GuiElementInfoRegion(
                (this.width - this.xSize) / 2 + 151,
                (this.height - this.ySize) / 2 + 90,
                18,
                18,
                batterySlotDesc,
                this.width,
                this.height,
                this
            )
        )
        val sunGenDesc: MutableList<String?> = ArrayList<String?>()
        val sunVisible: Float = Math.round(
            (this.solarController.getActualArraySize() / this.solarController.getPossibleArraySize().toFloat()) * 1000
        ) / 10.0f
        sunGenDesc.add(
            if (sunVisible > 0) GCCoreUtil.translate("gui.status.sun_visible.name") + ": " + sunVisible + "%" else GCCoreUtil.translate(
                "gui.status.blockedfully.name"
            )
        )
        this.infoRegions.add(
            GuiElementInfoRegion(
                (this.width - this.xSize) / 2 + 47,
                (this.height - this.ySize) / 2 + 20,
                18,
                18,
                sunGenDesc,
                this.width,
                this.height,
                this
            )
        )
        this.buttonList.add(
            GuiButton(
                0,
                this.width / 2 - 36,
                this.height / 2 - 11,
                72,
                20,
                GCCoreUtil.translate("gui.button.enable.name")
            ).also { this.buttonEnableSolar = it })
    }

    override fun drawGuiContainerForegroundLayer(par1: Int, par2: Int) {
        val offsetY: Int = 35
        this.buttonEnableSolar!!.enabled = this.solarController.disableCooldown == 0
        this.buttonEnableSolar!!.displayString =
            if (!this.solarController.getDisabled(0)) GCCoreUtil.translate("gui.button.disable.name") else GCCoreUtil.translate(
                "gui.button.enable.name"
            )
        var displayString: String = this.solarController.getName()
        this.fontRenderer.drawString(
            displayString,
            this.xSize / 2 - this.fontRenderer.getStringWidth(displayString) / 2,
            7,
            4210752
        )
        displayString = GCCoreUtil.translate("gui.message.status.name") + ": " + this.getStatus()
        this.fontRenderer.drawString(
            displayString,
            this.xSize / 2 - this.fontRenderer.getStringWidth(displayString) / 2,
            45 + 23 - 46 + offsetY,
            4210752
        )
        displayString =
            GCCoreUtil.translate("gui.message.generating.name") + ": " + (if (this.solarController.generateWatts > 0) EnergyDisplayHelper.getEnergyDisplayS(
                this.solarController.generateWatts.toFloat()
            ) + "/t" else GCCoreUtil.translate("gui.status.not_generating.name"))
        this.fontRenderer.drawString(
            displayString,
            this.xSize / 2 - this.fontRenderer.getStringWidth(displayString) / 2,
            34 + 23 - 46 + offsetY,
            4210752
        )
        val boost: Float = Math.round((this.solarController.getSolarBoost() - 1) * 1000) / 10.0f
        displayString = GCCoreUtil.translate("gui.message.environment.name") + ": " + boost + "%"
        this.fontRenderer.drawString(
            displayString,
            this.xSize / 2 - this.fontRenderer.getStringWidth(displayString) / 2,
            56 + 23 - 46 + offsetY,
            4210752
        )
        displayString = GCCoreUtil.translateWithFormat(
            "gui.message.connected_solar_controller.name",
            this.solarController.getActualArraySize()
        )
        this.fontRenderer.drawString(
            displayString,
            this.xSize / 2 - this.fontRenderer.getStringWidth(displayString) / 2,
            67 + 23 - 46 + offsetY,
            4210752
        )
        //		displayString = ElectricityDisplay.getDisplay(this.solarController.getVoltage(), ElectricUnit.VOLTAGE);
        //		this.fontRenderer.drawString(displayString, this.xSize / 2 - this.fontRenderer.getStringWidth(displayString) / 2, 68 + 23 - 46 + offsetY, 4210752);
        this.fontRenderer.drawString(GCCoreUtil.translate("container.inventory"), 8, this.ySize - 94, 4210752)
    }

    private fun getStatus(): String {
        if (this.solarController.getDisabled(0)) {
            return EnumColor.ORANGE.toString() + GCCoreUtil.translate("gui.status.disabled.name")
        }

        if (!this.solarController.getWorld().isDaytime()) {
            return EnumColor.DARK_RED.toString() + GCCoreUtil.translate("gui.status.blockedfully.name")
        }

        if (this.solarController.getWorld().isRaining() || this.solarController.getWorld().isThundering()) {
            return EnumColor.DARK_RED.toString() + GCCoreUtil.translate("gui.status.raining.name")
        }

        val sunVisible: Float = floor(
            (this.solarController.getActualArraySize() / this.solarController.getPossibleArraySize()
                .toFloat()).toDouble()
        ).toFloat()

        if (sunVisible == 0f) {
            return EnumColor.DARK_RED.toString() + GCCoreUtil.translate("gui.status.blockedfully.name")
        }

        if (sunVisible < 1.0f) {
            return EnumColor.DARK_RED.toString() + GCCoreUtil.translate("gui.status.blockedpartial.name")
        }

        if (this.solarController.generateWatts > 0) {
            return EnumColor.DARK_GREEN.toString() + GCCoreUtil.translate("gui.status.collectingenergy.name")
        }

        return EnumColor.ORANGE.toString() + GCCoreUtil.translate("gui.status.unknown.name")
    }

    override fun drawGuiContainerBackgroundLayer(var1: Float, var2: Int, var3: Int) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        this.mc.getTextureManager().bindTexture(solarGuiTexture)
        val var5: Int = (this.width - this.xSize) / 2
        val var6: Int = (this.height - this.ySize) / 2
        this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize)

        val electricityDesc: MutableList<String?> = ArrayList<String?>()
        EnergyDisplayHelper.getEnergyDisplayTooltip(
            this.solarController.getEnergyStoredGC(),
            this.solarController.getMaxEnergyStoredGC(),
            electricityDesc
        )
        //		electricityDesc.add(GCCoreUtil.translate("gui.energy_storage.desc.0"));
//		electricityDesc.add(EnumColor.YELLOW + GCCoreUtil.translate("gui.energy_storage.desc.1") + ((int) Math.floor(this.solarController.getEnergyStoredGC()) + " / " + (int) Math.floor(this.solarController.getMaxEnergyStoredGC())));
        this.electricInfoRegion.tooltipStrings = electricityDesc

        if (this.solarController.getEnergyStoredGC() > 0) {
            this.drawTexturedModalRect(var5 + 83, var6 + 24, 176, 0, 11, 10)
        }

        val sunVisible: Float = floor(
            (this.solarController.getActualArraySize() / this.solarController.getPossibleArraySize()
                .toFloat()).toDouble()
        ).toFloat()

        if (sunVisible > 0.0f) {
            this.drawTexturedModalRect(var5 + 48, var6 + 21, 176, 10, 16, 16)
        }

        this.drawTexturedModalRect(
            var5 + 97,
            var6 + 25,
            187,
            0,
            min(this.solarController.getScaledElecticalLevel(54), 54),
            7
        )
    }

    companion object {
        private val solarGuiTexture =
            ResourceLocation.fromNamespaceAndPath(GalacticraftPlanets.ASSET_PREFIX, "textures/gui/solar_array_controller.png")
    }
}
