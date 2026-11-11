package micdoodle8.mods.galacticraft.core.client.gui.container

import java.util.ArrayList

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementInfoRegion
import micdoodle8.mods.galacticraft.core.energy.EnergyDisplayHelper
import micdoodle8.mods.galacticraft.core.inventory.ContainerCircuitFabricator
import micdoodle8.mods.galacticraft.core.tile.TileEntityCircuitFabricator
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.world.entity.player.Inventory as InventoryPlayer
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import org.lwjgl.opengl.GL11

@OnlyIn(Dist.CLIENT)
open class GuiCircuitFabricator constructor(
    par1InventoryPlayer: InventoryPlayer,
    private val tileEntity: TileEntityCircuitFabricator
) : GuiContainerGC(ContainerCircuitFabricator(par1InventoryPlayer, tileEntity)) {
    private val electricInfoRegion = GuiElementInfoRegion(0, 0, 56, 9, null, 0, 0, this)
    private val processInfoRegion = GuiElementInfoRegion(0, 0, 53, 12, null, 0, 0, this)

    init {
        this.ySize = 192
    }

    override fun initGui() {
        super.initGui()
        this.electricInfoRegion.tooltipStrings = (ArrayList<String?>())!!
        this.electricInfoRegion.xPosition = (this.width - this.xSize) / 2 + 17
        this.electricInfoRegion.yPosition = (this.height - this.ySize) / 2 + 88
        this.electricInfoRegion.parentWidth = this.width
        this.electricInfoRegion.parentHeight = this.height
        this.infoRegions.add(this.electricInfoRegion)
        val batterySlotDesc: MutableList<String?> = ArrayList<String?>()
        batterySlotDesc.add(GCCoreUtil.translate("gui.battery_slot.desc.0"))
        batterySlotDesc.add(GCCoreUtil.translate("gui.battery_slot.desc.1"))
        this.infoRegions.add(
            GuiElementInfoRegion(
                (this.width - this.xSize) / 2 + 5,
                (this.height - this.ySize) / 2 + 68,
                18,
                18,
                batterySlotDesc,
                this.width,
                this.height,
                this
            )
        )
        this.processInfoRegion.tooltipStrings = (ArrayList<String?>())!!
        this.processInfoRegion.xPosition = (this.width - this.xSize) / 2 + 87
        this.processInfoRegion.yPosition = (this.height - this.ySize) / 2 + 19
        this.processInfoRegion.parentWidth = this.width
        this.processInfoRegion.parentHeight = this.height
        this.infoRegions.add(this.processInfoRegion)
    }

    override fun drawGuiContainerForegroundLayer(par1: Int, par2: Int) {
        this.fontRenderer.drawString(this.tileEntity.getName(), 10, 6, 4210752)
        var displayText: String

        if (this.tileEntity.processTicks > 0) {
            displayText = EnumColor.BRIGHT_GREEN.toString() + GCCoreUtil.translate("gui.status.running.name")
        } else {
            displayText = EnumColor.ORANGE.toString() + GCCoreUtil.translate("gui.status.idle.name")
        }

        val str: String = GCCoreUtil.translate("gui.message.status.name") + ":"
        this.fontRenderer.drawString(str, 115 - this.fontRenderer.getStringWidth(str) / 2, 80, 4210752)
        displayText = this.tileEntity.getGUIstatus(displayText, null, true)
        this.fontRenderer.drawString(displayText, 115 - this.fontRenderer.getStringWidth(displayText) / 2, 90, 4210752)
        this.fontRenderer.drawString(GCCoreUtil.translate("container.inventory"), 8, this.ySize - 93, 4210752)
        //		str = "" + this.tileEntity.storage.getMaxExtract();
//		this.fontRenderer.drawString(str, 5, 42, 4210752);
//		//		str = ElectricityDisplay.getDisplay(this.tileEntity.getVoltage(), ElectricUnit.VOLTAGE);
//		this.fontRenderer.drawString(str, 5, 52, 4210752);
    }

    override fun drawGuiContainerBackgroundLayer(par1: Float, par2: Int, par3: Int) {
        this.mc.renderEngine.bindTexture(circuitFabricatorTexture)
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)

        val containerWidth: Int = (this.width - this.xSize) / 2
        val containerHeight: Int = (this.height - this.ySize) / 2
        this.drawTexturedModalRect(containerWidth, containerHeight, 0, 0, this.xSize, this.ySize)
        this.drawTexturedModalRect(containerWidth + 5, containerHeight + 68, 176, 47, 18, 18)
        this.drawTexturedModalRect(containerWidth + 5, containerHeight + 89, 194, 47, 9, 8)
        this.drawTexturedModalRect(containerWidth + 17, containerHeight + 88, 176, 65, 56, 9)
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
            scale = (this.tileEntity.processTicks * 100) / this.tileEntity.getProcessTimeRequired()
        } else {
            scale = 0
        }

        val processDesc: MutableList<String?> = ArrayList<String?>()
        processDesc.clear()
        processDesc.add(GCCoreUtil.translate("gui.electric_compressor.desc.0") + ": " + scale + "%")
        this.processInfoRegion.tooltipStrings = processDesc

        if (this.tileEntity.processTicks > 0) {
            scale = (this.tileEntity.processTicks * 51) / this.tileEntity.getProcessTimeRequired()
            this.drawTexturedModalRect(
                containerWidth + 88,
                containerHeight + 20,
                176,
                17 + this.tileEntity.processTicks % 9 / 3 * 10,
                scale,
                10
            )
        }

        if (this.tileEntity.getEnergyStoredGC() > 0) {
            scale = this.tileEntity.getScaledElecticalLevel(54)
            this.drawTexturedModalRect(containerWidth + 116 - 98, containerHeight + 89, 176, 0, scale, 7)
            this.drawTexturedModalRect(containerWidth + 4, containerHeight + 88, 176, 7, 11, 10)
        }
    }

    companion object {
        private val circuitFabricatorTexture =
            ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/gui/circuit_fabricator.png")
    }
}
