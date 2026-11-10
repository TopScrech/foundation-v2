package micdoodle8.mods.galacticraft.planets.venus.client.gui

import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket
import java.util.List

import org.lwjgl.opengl.GL11
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.client.gui.container.GuiContainerGC
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementInfoRegion
import micdoodle8.mods.galacticraft.core.energy.EnergyDisplayHelper
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import micdoodle8.mods.galacticraft.planets.venus.inventory.ContainerGeothermal
import micdoodle8.mods.galacticraft.planets.venus.tile.TileEntityGeothermalGenerator
import net.minecraft.client.gui.GuiButton
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ResourceLocation
import java.util.ArrayList
import kotlin.math.floor
import kotlin.math.min

open class GuiGeothermal constructor(
    par1InventoryPlayer: InventoryPlayer?,
    private val geothermalGenerator: TileEntityGeothermalGenerator
) : GuiContainerGC(ContainerGeothermal((par1InventoryPlayer)!!, geothermalGenerator)) {
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
        this.ySize = 201
        this.xSize = 176
    }

    override fun actionPerformed(par1GuiButton: GuiButton) {
        when (par1GuiButton.id) {
            0 -> (GalacticraftCore.packetPipeline)!!.sendToServer(
                PacketSimple(
                    PacketSimple.EnumSimplePacket.S_UPDATE_DISABLEABLE_BUTTON,
                    GCCoreUtil.getDimensionID(this.mc.world),
                    arrayOf<Any>(this.geothermalGenerator.getPos(), 0)
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
                this.geothermalGenerator.getEnergyStoredGC().toDouble()
            ).toInt().toString() + " / " + floor(this.geothermalGenerator.getMaxEnergyStoredGC().toDouble()).toInt())
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
                (this.height - this.ySize) / 2 + 82,
                18,
                18,
                batterySlotDesc,
                this.width,
                this.height,
                this
            )
        )
        //        List<String?> sunGenDesc = new ArrayList<String?>();
//        float sunVisible = Math.round(this.geothermalGenerator.solarStrength / 9.0F * 1000) / 10.0F;
//        sunGenDesc.add(this.geothermalGenerator.solarStrength > 0 ? GCCoreUtil.translate("gui.status.sun_visible.name") + ": " + sunVisible + "%" : GCCoreUtil.translate("gui.status.blockedfully.name"));
//        this.infoRegions.add(new GuiElementInfoRegion((this.width - this.xSize) / 2 + 47, (this.height - this.ySize) / 2 + 20, 18, 18, sunGenDesc, this.width, this.height, this));
        this.buttonList.add(
            GuiButton(
                0,
                this.width / 2 - 36,
                this.height / 2 - 19,
                72,
                20,
                GCCoreUtil.translate("gui.button.enable.name")
            ).also { this.buttonEnableSolar = it })
    }

    override fun drawGuiContainerForegroundLayer(par1: Int, par2: Int) {
        val offsetY: Int = 35
        this.buttonEnableSolar!!.enabled = this.geothermalGenerator.disableCooldown == 0
        this.buttonEnableSolar!!.displayString =
            if (!this.geothermalGenerator.getDisabled(0)) GCCoreUtil.translate("gui.button.disable.name") else GCCoreUtil.translate(
                "gui.button.enable.name"
            )
        var displayString: String = this.geothermalGenerator.getName()
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
        displayString = this.getStatus2()
        this.fontRenderer.drawString(
            displayString,
            this.xSize / 2 - this.fontRenderer.getStringWidth(displayString) / 2,
            56 + 23 - 46 + offsetY,
            4210752
        )
        displayString =
            GCCoreUtil.translate("gui.message.generating.name") + ": " + (if (this.geothermalGenerator.generateWatts > 0) EnergyDisplayHelper.getEnergyDisplayS(
                this.geothermalGenerator.generateWatts.toFloat()
            ) + "/t" else GCCoreUtil.translate("gui.status.not_generating.name"))
        this.fontRenderer.drawString(
            displayString,
            this.xSize / 2 - this.fontRenderer.getStringWidth(displayString) / 2,
            34 + 23 - 46 + offsetY,
            4210752
        )
        //        float boost = Math.round((this.geothermalGenerator.getSolarBoost() - 1) * 1000) / 10.0F;
//        displayString = GCCoreUtil.translate("gui.message.environment.name") + ": " + boost + "%";
//        this.fontRenderer.drawString(displayString, this.xSize / 2 - this.fontRenderer.getStringWidth(displayString) / 2, 56 + 23 - 46 + offsetY, 4210752);
        //		displayString = ElectricityDisplay.getDisplay(this.geothermalGenerator.getVoltage(), ElectricUnit.VOLTAGE);
        //		this.fontRenderer.drawString(displayString, this.xSize / 2 - this.fontRenderer.getStringWidth(displayString) / 2, 68 + 23 - 46 + offsetY, 4210752);
        this.fontRenderer.drawString(GCCoreUtil.translate("container.inventory"), 8, this.ySize - 94, 4210752)
    }

    private fun getStatus(): String {
        if (this.geothermalGenerator.getDisabled(0)) {
            return EnumColor.ORANGE.toString() + GCCoreUtil.translate("gui.status.disabled.name")
        }

        if (this.geothermalGenerator.generateWatts > 0) {
            return EnumColor.DARK_GREEN.toString() + GCCoreUtil.translate("gui.status.collectingenergy.name")
        }

        if (!this.geothermalGenerator.hasValidSpout()) {
            return EnumColor.RED.toString() + GCCoreUtil.translate("gui.status.invalid_spout_1.name")
        }

        return EnumColor.ORANGE.toString() + GCCoreUtil.translate("gui.status.unknown.name")
    }

    private fun getStatus2(): String {
        if (this.geothermalGenerator.getDisabled(0) || this.geothermalGenerator.generateWatts > 0) {
            return ""
        }

        if (!this.geothermalGenerator.hasValidSpout()) {
            return EnumColor.RED.toString() + GCCoreUtil.translate("gui.status.invalid_spout_2.name")
        }

        return ""
    }

    override fun drawGuiContainerBackgroundLayer(var1: Float, var2: Int, var3: Int) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        this.mc.getTextureManager().bindTexture(backgroundTexture)
        val var5: Int = (this.width - this.xSize) / 2
        val var6: Int = (this.height - this.ySize) / 2
        this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize)

        val electricityDesc: MutableList<String?> = ArrayList<String?>()
        EnergyDisplayHelper.getEnergyDisplayTooltip(
            this.geothermalGenerator.getEnergyStoredGC(),
            this.geothermalGenerator.getMaxEnergyStoredGC(),
            electricityDesc
        )
        this.electricInfoRegion.tooltipStrings = electricityDesc

        if (this.geothermalGenerator.getEnergyStoredGC() > 0) {
            this.drawTexturedModalRect(var5 + 83, var6 + 24, 176, 0, 11, 10)
        }

        if (this.geothermalGenerator.hasValidSpout()) {
            val ySize: Int =
                16 * this.geothermalGenerator.generateWatts / (TileEntityGeothermalGenerator.MAX_GENERATE_GJ_PER_TICK - 1)
            this.drawTexturedModalRect(
                var5 + 33,
                var6 + 21 + (16 - ySize),
                176,
                (10 + ((this.geothermalGenerator.ticks / 3) % 7) * 16) + (16 - ySize),
                46,
                ySize
            )
        }

        this.drawTexturedModalRect(
            var5 + 97,
            var6 + 25,
            187,
            0,
            min(this.geothermalGenerator.getScaledElecticalLevel(54), 54),
            7
        )
    }

    companion object {
        private val backgroundTexture =
            ResourceLocation(GalacticraftPlanets.ASSET_PREFIX, "textures/gui/geothermal.png")
    }
}
