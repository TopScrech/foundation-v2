package micdoodle8.mods.galacticraft.core.client.gui.container

import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket
import java.util.ArrayList

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementInfoRegion
import micdoodle8.mods.galacticraft.core.energy.EnergyDisplayHelper
import micdoodle8.mods.galacticraft.core.inventory.ContainerFuelLoader
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.tile.TileEntityFuelLoader
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.gui.GuiButton
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import kotlin.math.floor
import kotlin.math.min

open class GuiFuelLoader constructor(
    par1InventoryPlayer: InventoryPlayer?,
    private val fuelLoader: TileEntityFuelLoader
) : GuiContainerGC(ContainerFuelLoader((par1InventoryPlayer)!!, fuelLoader)) {
    private var buttonLoadFuel: GuiButton? = null
    private val electricInfoRegion = GuiElementInfoRegion(
        (this.width - this.xSize) / 2 + 112,
        (this.height - this.ySize) / 2 + 65,
        56,
        9,
        ArrayList<String?>(),
        this.width,
        this.height,
        this
    )

    init {
        this.ySize = 180
    }

    override fun actionPerformed(par1GuiButton: GuiButton) {
        when (par1GuiButton.id) {
            0 -> (GalacticraftCore.packetPipeline)!!.sendToServer(
                PacketSimple(
                    PacketSimple.EnumSimplePacket.S_UPDATE_DISABLEABLE_BUTTON,
                    GCCoreUtil.getDimensionID(this.fuelLoader.getWorld()),
                    arrayOf<Any>(this.fuelLoader.getPos(), 0)
                )
            )
        }
    }

    override fun initGui() {
        super.initGui()
        val fuelTankDesc: MutableList<String?> = ArrayList<String?>()
        fuelTankDesc.add(GCCoreUtil.translate("gui.fuel_tank.desc.2"))
        fuelTankDesc.add(GCCoreUtil.translate("gui.fuel_tank.desc.3"))
        this.infoRegions.add(
            GuiElementInfoRegion(
                (this.width - this.xSize) / 2 + 7,
                (this.height - this.ySize) / 2 + 33,
                16,
                38,
                fuelTankDesc,
                this.width,
                this.height,
                this
            )
        )
        val batterySlotDesc: MutableList<String?> = ArrayList<String?>()
        batterySlotDesc.add(GCCoreUtil.translate("gui.battery_slot.desc.0"))
        batterySlotDesc.add(GCCoreUtil.translate("gui.battery_slot.desc.1"))
        this.infoRegions.add(
            GuiElementInfoRegion(
                (this.width - this.xSize) / 2 + 50,
                (this.height - this.ySize) / 2 + 54,
                18,
                18,
                batterySlotDesc,
                this.width,
                this.height,
                this
            )
        )
        val electricityDesc: MutableList<String?> = ArrayList<String?>()
        electricityDesc.add(GCCoreUtil.translate("gui.energy_storage.desc.0"))
        electricityDesc.add(
            EnumColor.YELLOW.toString() + GCCoreUtil.translate("gui.energy_storage.desc.1") + (floor(
                this.fuelLoader.getEnergyStoredGC().toDouble()
            ).toInt().toString() + " / " + floor(this.fuelLoader.getMaxEnergyStoredGC().toDouble()).toInt())
        )
        this.electricInfoRegion.tooltipStrings = electricityDesc
        this.electricInfoRegion.xPosition = (this.width - this.xSize) / 2 + 112
        this.electricInfoRegion.yPosition = (this.height - this.ySize) / 2 + 65
        this.electricInfoRegion.parentWidth = this.width
        this.electricInfoRegion.parentHeight = this.height
        this.infoRegions.add(this.electricInfoRegion)
        this.buttonList.add(
            GuiButton(
                0,
                this.width / 2 + 2,
                this.height / 2 - 49,
                76,
                20,
                GCCoreUtil.translate("gui.button.loadfuel.name")
            ).also { this.buttonLoadFuel = it })
    }

    override fun drawGuiContainerForegroundLayer(par1: Int, par2: Int) {
        this.fontRenderer.drawString(this.fuelLoader.getName(), 60, 10, 4210752)
        this.buttonLoadFuel!!.enabled =
            this.fuelLoader.disableCooldown == 0 && !((this.fuelLoader.fuelTank)!!.getFluid() == null || (this.fuelLoader.fuelTank)!!.getFluid()!!.amount == 0)
        this.buttonLoadFuel!!.displayString =
            if (!this.fuelLoader.getDisabled(0)) GCCoreUtil.translate("gui.button.stoploading.name") else GCCoreUtil.translate(
                "gui.button.loadfuel.name"
            )
        this.fontRenderer.drawString(
            GCCoreUtil.translate("gui.message.status.name") + ": " + this.getStatus(),
            28,
            45 + 23 - 46,
            4210752
        )
        //this.fontRenderer.drawString("" + this.fuelLoader.storage.getMaxExtract(), 28, 56 + 23 - 46, 4210752);
        //		this.fontRenderer.drawString(ElectricityDisplay.getDisplay(this.fuelLoader.getVoltage(), ElectricUnit.VOLTAGE), 28, 68 + 23 - 46, 4210752);
        this.fontRenderer.drawString(GCCoreUtil.translate("container.inventory"), 8, this.ySize - 118 + 2 + 11, 4210752)
    }

    private fun getStatus(): String? {
        if ((this.fuelLoader.fuelTank)!!.getFluid() == null || (this.fuelLoader.fuelTank)!!.getFluid()!!.amount == 0) {
            return EnumColor.DARK_RED.toString() + GCCoreUtil.translate("gui.status.nofuel.name")
        }

        return this.fuelLoader.getGUIstatus()
    }

    override fun drawGuiContainerBackgroundLayer(var1: Float, var2: Int, var3: Int) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        this.mc.getTextureManager().bindTexture(fuelLoaderTexture)
        val var5: Int = (this.width - this.xSize) / 2
        val var6: Int = (this.height - this.ySize) / 2
        this.drawTexturedModalRect(var5, var6 + 5, 0, 0, this.xSize, 181)

        val fuelLevel: Int = this.fuelLoader.getScaledFuelLevel(38)
        this.drawTexturedModalRect(
            (this.width - this.xSize) / 2 + 7,
            (this.height - this.ySize) / 2 + 17 + 54 - fuelLevel,
            176,
            38 - fuelLevel,
            16,
            fuelLevel
        )

        val electricityDesc: MutableList<String?> = ArrayList<String?>()
        electricityDesc.add(GCCoreUtil.translate("gui.energy_storage.desc.0"))
        EnergyDisplayHelper.getEnergyDisplayTooltip(
            this.fuelLoader.getEnergyStoredGC(),
            this.fuelLoader.getMaxEnergyStoredGC(),
            electricityDesc
        )
        //		electricityDesc.add(EnumColor.YELLOW + GCCoreUtil.translate("gui.energy_storage.desc.1") + ((int) Math.floor(this.fuelLoader.getEnergyStoredGC()) + " / " + (int) Math.floor(this.fuelLoader.getMaxEnergyStoredGC())));
        this.electricInfoRegion.tooltipStrings = electricityDesc

        if (this.fuelLoader.getEnergyStoredGC() > 0) {
            this.drawTexturedModalRect(var5 + 99, var6 + 65, 192, 7, 11, 10)
        }

        this.drawTexturedModalRect(
            var5 + 113,
            var6 + 66,
            192,
            0,
            min(this.fuelLoader.getScaledElecticalLevel(54), 54),
            7
        )
    }

    companion object {
        private val fuelLoaderTexture = ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/fuel_loader.png")
    }
}
