package micdoodle8.mods.galacticraft.core.client.gui.container

import java.util.ArrayList

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementInfoRegion
import micdoodle8.mods.galacticraft.core.energy.EnergyDisplayHelper
import micdoodle8.mods.galacticraft.core.inventory.ContainerElectricFurnace
import micdoodle8.mods.galacticraft.core.tile.TileEntityElectricFurnace
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11

@SideOnly(Side.CLIENT)
open class GuiElectricFurnace constructor(
    par1InventoryPlayer: InventoryPlayer,
    private val tileEntity: TileEntityElectricFurnace
) : GuiContainerGC(ContainerElectricFurnace(par1InventoryPlayer, tileEntity)) {
    private val electricInfoRegion = GuiElementInfoRegion(0, 0, 56, 9, null, 0, 0, this)

    private val texture: ResourceLocation

    init {
        texture = if (tileEntity.tierGC == 2) arcFurnaceTexture else electricFurnaceTexture
    }

    override fun initGui() {
        super.initGui()
        this.electricInfoRegion.tooltipStrings = (ArrayList<String?>())!!
        this.electricInfoRegion.xPosition = (this.width - this.xSize) / 2 + 39
        this.electricInfoRegion.yPosition = (this.height - this.ySize) / 2 + 52
        this.electricInfoRegion.parentWidth = this.width
        this.electricInfoRegion.parentHeight = this.height
        this.infoRegions.add(this.electricInfoRegion)
        val batterySlotDesc: MutableList<String?> = ArrayList<String?>()
        batterySlotDesc.add(GCCoreUtil.translate("gui.battery_slot.desc.0"))
        batterySlotDesc.add(GCCoreUtil.translate("gui.battery_slot.desc.1"))
        this.infoRegions.add(
            GuiElementInfoRegion(
                (this.width - this.xSize) / 2 + 7,
                (this.height - this.ySize) / 2 + 48,
                18,
                18,
                batterySlotDesc,
                this.width,
                this.height,
                this
            )
        )
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of
     * the items)
     */
    override fun drawGuiContainerForegroundLayer(par1: Int, par2: Int) {
        this.fontRenderer.drawString(this.tileEntity.getName(), 45, 6, 4210752)
        var displayText: String = ""

        if (this.tileEntity.processTicks > 0) {
            displayText = EnumColor.BRIGHT_GREEN.toString() + GCCoreUtil.translate("gui.status.running.name")
        } else {
            displayText = EnumColor.ORANGE.toString() + GCCoreUtil.translate("gui.status.idle.name")
        }

        this.fontRenderer.drawString(GCCoreUtil.translate("gui.message.status.name") + ": ", 97, 52, 4210752)
        this.fontRenderer.drawString(this.tileEntity.getGUIstatus(null, displayText, true), 107, 62, 4210752)
        //		this.fontRenderer.drawString("" + this.tileEntity.storage.getMaxExtract(), 97, 56, 4210752);
//		this.fontRenderer.drawString("Voltage: " + (int) (this.tileEntity.getVoltage() * 1000.0F), 97, 68, 4210752);
        this.fontRenderer.drawString(GCCoreUtil.translate("container.inventory"), 8, this.ySize - 96 + 2, 4210752)
    }

    /**
     * Draw the background layer for the GuiContainer (everything behind the
     * items)
     */
    override fun drawGuiContainerBackgroundLayer(par1: Float, par2: Int, par3: Int) {
        this.mc.renderEngine.bindTexture(this.texture)
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
                (this.tileEntity.processTicks.toDouble() / this.tileEntity.processTimeRequired.toDouble() * 23).toInt()
            this.drawTexturedModalRect(containerWidth + 78, containerHeight + 24, 176, 0, 23 - scale, 15)
        }

        if (this.tileEntity.getEnergyStoredGC() > 0) {
            scale = this.tileEntity.getScaledElecticalLevel(54)
            this.drawTexturedModalRect(containerWidth + 40, containerHeight + 53, 176, 15, scale, 7)
            this.drawTexturedModalRect(containerWidth + 26, containerHeight + 52, 176, 22, 11, 10)
        }
    }

    companion object {
        private val electricFurnaceTexture =
            ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/electric_furnace.png")
        private val arcFurnaceTexture =
            ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/electric_arc_furnace.png")
    }
}
