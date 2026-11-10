package micdoodle8.mods.galacticraft.core.client.gui.container

import java.util.ArrayList

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementInfoRegion
import micdoodle8.mods.galacticraft.core.energy.EnergyDisplayHelper
import micdoodle8.mods.galacticraft.core.inventory.ContainerElectricIngotCompressor
import micdoodle8.mods.galacticraft.core.tile.TileEntityElectricIngotCompressor
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11

@SideOnly(Side.CLIENT)
open class GuiElectricIngotCompressor constructor(
    par1InventoryPlayer: InventoryPlayer?,
    private val tileEntity: TileEntityElectricIngotCompressor
) : GuiContainerGC(ContainerElectricIngotCompressor((par1InventoryPlayer)!!, tileEntity)) {
    private val electricInfoRegion = GuiElementInfoRegion(0, 0, 56, 9, null, 0, 0, this)
    private val processInfoRegion = GuiElementInfoRegion(0, 0, 52, 25, null, 0, 0, this)

    init {
        this.ySize = 199
    }

    override fun initGui() {
        super.initGui()
        this.electricInfoRegion.tooltipStrings = (ArrayList<String?>())!!
        this.electricInfoRegion.xPosition = (this.width - this.xSize) / 2 + 17
        this.electricInfoRegion.yPosition = (this.height - this.ySize) / 2 + 95
        this.electricInfoRegion.parentWidth = this.width
        this.electricInfoRegion.parentHeight = this.height
        this.infoRegions.add(this.electricInfoRegion)
        val batterySlotDesc: MutableList<String?> = ArrayList<String?>()
        batterySlotDesc.add(GCCoreUtil.translate("gui.battery_slot.desc.0"))
        batterySlotDesc.add(GCCoreUtil.translate("gui.battery_slot.desc.1"))
        this.infoRegions.add(
            GuiElementInfoRegion(
                (this.width - this.xSize) / 2 + 54,
                (this.height - this.ySize) / 2 + 74,
                18,
                18,
                batterySlotDesc,
                this.width,
                this.height,
                this
            )
        )
        this.processInfoRegion.tooltipStrings = (ArrayList<String?>())!!
        this.processInfoRegion.xPosition = (this.width - this.xSize) / 2 + 77
        this.processInfoRegion.yPosition = (this.height - this.ySize) / 2 + 30
        this.processInfoRegion.parentWidth = this.width
        this.processInfoRegion.parentHeight = this.height
        this.infoRegions.add(this.processInfoRegion)
    }

    override fun drawGuiContainerForegroundLayer(par1: Int, par2: Int) {
        this.fontRenderer.drawString(this.tileEntity.getName(), 10, 6, 4210752)
        val displayText: String

        if (this.tileEntity.processTicks > 0) {
            displayText = EnumColor.BRIGHT_GREEN.toString() + GCCoreUtil.translate("gui.status.running.name")
        } else {
            displayText = EnumColor.ORANGE.toString() + GCCoreUtil.translate("gui.status.idle.name")
        }

        val str: String = GCCoreUtil.translate("gui.message.status.name") + ": " + this.tileEntity.getGUIstatus(
            displayText,
            null,
            true
        )
        this.fontRenderer.drawString(str, 120 - this.fontRenderer.getStringWidth(str) / 2, 75, 4210752)
        this.fontRenderer.drawString(GCCoreUtil.translate("container.inventory"), 8, this.ySize - 93, 4210752)
        //		str = "" + this.tileEntity.storage.getMaxExtract();
//		this.fontRenderer.drawString(str, 120 - this.fontRenderer.getStringWidth(str) / 2, 85, 4210752);
//		//		str = ElectricityDisplay.getDisplay(this.tileEntity.getVoltage(), ElectricUnit.VOLTAGE);
//		this.fontRenderer.drawString(str, 120 - this.fontRenderer.getStringWidth(str) / 2, 95, 4210752);
    }

    /**
     * Draw the background layer for the GuiContainer (everything behind the
     * items)
     */
    override fun drawGuiContainerBackgroundLayer(par1: Float, par2: Int, par3: Int) {
        this.mc.renderEngine.bindTexture(electricFurnaceTexture)
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)

        val containerWidth: Int = (this.width - this.xSize) / 2
        val containerHeight: Int = (this.height - this.ySize) / 2
        this.drawTexturedModalRect(containerWidth, containerHeight, 0, 0, this.xSize, this.ySize)
        var scale: Int

        val electricityDesc: MutableList<String?> = ArrayList<String?>()
        electricityDesc.add(GCCoreUtil.translate("gui.energy_storage.desc.0"))
        EnergyDisplayHelper.getEnergyDisplayTooltip(
            this.tileEntity.getEnergyStoredGC(),
            this.tileEntity.getMaxEnergyStoredGC(),
            electricityDesc
        )
        this.electricInfoRegion.tooltipStrings = electricityDesc

        if (this.tileEntity.processTicks > 0) {
            scale =
                (this.tileEntity.processTicks.toDouble() / this.tileEntity.processTimeRequired.toDouble() * 100).toInt()
        } else {
            scale = 0
        }

        val processDesc: MutableList<String?> = ArrayList<String?>()
        processDesc.clear()
        processDesc.add(GCCoreUtil.translate("gui.electric_compressor.desc.0") + ": " + scale + "%")
        this.processInfoRegion.tooltipStrings = processDesc

        if (this.tileEntity.processTicks > 0) {
            scale =
                (this.tileEntity.processTicks.toDouble() / this.tileEntity.processTimeRequired.toDouble() * 54).toInt()
            this.drawTexturedModalRect(containerWidth + 77, containerHeight + 38, 176, 13, scale, 17)
        }

        if (this.tileEntity.getEnergyStoredGC() > 0) {
            scale = this.tileEntity.getScaledElecticalLevel(54)
            this.drawTexturedModalRect(containerWidth + 116 - 98, containerHeight + 96, 176, 30, scale, 7)
            this.drawTexturedModalRect(containerWidth + 4, containerHeight + 95, 176, 37, 11, 10)
        }

        if (this.tileEntity.processTicks > this.tileEntity.processTimeRequired / 2) {
            this.drawTexturedModalRect(containerWidth + 101, containerHeight + 30, 176, 0, 15, 13)
        }
    }

    companion object {
        private val electricFurnaceTexture =
            ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/electric_ingot_compressor.png")
    }
}
