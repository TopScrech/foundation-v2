package micdoodle8.mods.galacticraft.core.client.gui.container

import java.util.ArrayList

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementInfoRegion
import micdoodle8.mods.galacticraft.core.inventory.ContainerIngotCompressor
import micdoodle8.mods.galacticraft.core.tile.TileEntityIngotCompressor
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11

@SideOnly(Side.CLIENT)
open class GuiIngotCompressor constructor(
    par1InventoryPlayer: InventoryPlayer?,
    private val tileEntity: TileEntityIngotCompressor
) : GuiContainerGC(ContainerIngotCompressor((par1InventoryPlayer)!!, tileEntity)) {
    private val processInfoRegion = GuiElementInfoRegion(0, 0, 52, 25, null, 0, 0, this)

    init {
        this.ySize = 192
    }

    override fun initGui() {
        super.initGui()
        this.processInfoRegion.tooltipStrings = (ArrayList<String?>())!!
        this.processInfoRegion.xPosition = (this.width - this.xSize) / 2 + 77
        this.processInfoRegion.yPosition = (this.height - this.ySize) / 2 + 30
        this.processInfoRegion.parentWidth = this.width
        this.processInfoRegion.parentHeight = this.height
        this.infoRegions.add(this.processInfoRegion)
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of
     * the items)
     */
    override fun drawGuiContainerForegroundLayer(par1: Int, par2: Int) {
        this.fontRenderer.drawString(this.tileEntity.getName(), 10, 6, 4210752)
        var displayText: String = GCCoreUtil.translate("gui.message.fuel.name") + ":"
        this.fontRenderer.drawString(displayText, 50 - this.fontRenderer.getStringWidth(displayText), 79, 4210752)

        if (this.tileEntity.processTicks > 0) {
            displayText = EnumColor.BRIGHT_GREEN.toString() + GCCoreUtil.translate("gui.status.compressing.name")
        } else {
            displayText = EnumColor.ORANGE.toString() + GCCoreUtil.translate("gui.status.idle.name")
        }

        var str: String = GCCoreUtil.translate("gui.message.status.name") + ":"
        this.fontRenderer.drawString(
            GCCoreUtil.translate("gui.message.status.name") + ":",
            120 - this.fontRenderer.getStringWidth(str) / 2,
            70,
            4210752
        )
        str = displayText
        this.fontRenderer.drawString(displayText, 120 - this.fontRenderer.getStringWidth(str) / 2, 80, 4210752)
        this.fontRenderer.drawString(GCCoreUtil.translate("container.inventory"), 8, this.ySize - 96 + 2, 4210752)
    }

    /**
     * Draw the background layer for the GuiContainer (everything behind the
     * items)
     */
    override fun drawGuiContainerBackgroundLayer(par1: Float, par2: Int, par3: Int) {
        this.mc.renderEngine.bindTexture(electricFurnaceTexture)
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)

        val process: Int
        val containerWidth: Int = (this.width - this.xSize) / 2
        val containerHeight: Int = (this.height - this.ySize) / 2
        this.drawTexturedModalRect(containerWidth, containerHeight, 0, 0, this.xSize, this.ySize)

        if (this.tileEntity.processTicks > 0) {
            process =
                (this.tileEntity.processTicks.toDouble() / TileEntityIngotCompressor.PROCESS_TIME_REQUIRED.toDouble() * 100).toInt()
        } else {
            process = 0
        }

        val processDesc: MutableList<String?> = ArrayList<String?>()
        processDesc.clear()
        processDesc.add(GCCoreUtil.translate("gui.electric_compressor.desc.0") + ": " + process + "%")
        this.processInfoRegion.tooltipStrings = processDesc

        if (this.tileEntity.processTicks > 0) {
            val scale: Int =
                (this.tileEntity.processTicks.toDouble() / TileEntityIngotCompressor.PROCESS_TIME_REQUIRED.toDouble() * 54).toInt()
            this.drawTexturedModalRect(containerWidth + 77, containerHeight + 36, 176, 13, scale, 17)
        }

        if (this.tileEntity.furnaceBurnTime > 0) {
            val scale: Int =
                (this.tileEntity.furnaceBurnTime.toDouble() / this.tileEntity.currentItemBurnTime.toDouble() * 14).toInt()
            this.drawTexturedModalRect(
                containerWidth + 81,
                containerHeight + 27 + 14 - scale,
                176,
                30 + 14 - scale,
                14,
                scale
            )
        }

        if (this.tileEntity.processTicks > TileEntityIngotCompressor.PROCESS_TIME_REQUIRED / 2) {
            this.drawTexturedModalRect(containerWidth + 101, containerHeight + 28, 176, 0, 15, 13)
        }
    }

    companion object {
        private val electricFurnaceTexture =
            ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/ingot_compressor.png")
    }
}
