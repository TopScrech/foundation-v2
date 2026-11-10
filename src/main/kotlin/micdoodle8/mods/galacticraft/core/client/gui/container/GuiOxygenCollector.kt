package micdoodle8.mods.galacticraft.core.client.gui.container

import java.util.ArrayList

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementInfoRegion
import micdoodle8.mods.galacticraft.core.energy.EnergyDisplayHelper
import micdoodle8.mods.galacticraft.core.inventory.ContainerOxygenCollector
import micdoodle8.mods.galacticraft.core.tile.TileEntityOxygenCollector
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import kotlin.math.floor
import kotlin.math.min

open class GuiOxygenCollector constructor(
    par1InventoryPlayer: InventoryPlayer?,
    private val collector: TileEntityOxygenCollector?
) : GuiContainerGC(ContainerOxygenCollector((par1InventoryPlayer)!!, (collector)!!)) {
    private val oxygenInfoRegion = GuiElementInfoRegion(
        (this.width - this.xSize) / 2 + 112,
        (this.height - this.ySize) / 2 + 24,
        56,
        9,
        ArrayList<String?>(),
        this.width,
        this.height,
        this
    )
    private val electricInfoRegion = GuiElementInfoRegion(
        (this.width - this.xSize) / 2 + 112,
        (this.height - this.ySize) / 2 + 37,
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

    override fun initGui() {
        super.initGui()
        val batterySlotDesc: MutableList<String?> = ArrayList<String?>()
        batterySlotDesc.add(GCCoreUtil.translate("gui.battery_slot.desc.0"))
        batterySlotDesc.add(GCCoreUtil.translate("gui.battery_slot.desc.1"))
        this.infoRegions.add(
            GuiElementInfoRegion(
                (this.width - this.xSize) / 2 + 31,
                (this.height - this.ySize) / 2 + 26,
                18,
                18,
                batterySlotDesc,
                this.width,
                this.height,
                this
            )
        )
        this.oxygenInfoRegion.xPosition = (this.width - this.xSize) / 2 + 112
        this.oxygenInfoRegion.yPosition = (this.height - this.ySize) / 2 + 24
        this.oxygenInfoRegion.parentWidth = this.width
        this.oxygenInfoRegion.parentHeight = this.height
        this.infoRegions.add(this.oxygenInfoRegion)
        this.electricInfoRegion.xPosition = (this.width - this.xSize) / 2 + 112
        this.electricInfoRegion.yPosition = (this.height - this.ySize) / 2 + 37
        this.electricInfoRegion.parentWidth = this.width
        this.electricInfoRegion.parentHeight = this.height
        this.infoRegions.add(this.electricInfoRegion)
    }

    override fun drawGuiContainerForegroundLayer(par1: Int, par2: Int) {
        this.fontRenderer.drawString(this.collector!!.getName(), 8, 10, 4210752)
        GCCoreUtil.drawStringRightAligned(
            GCCoreUtil.translate("gui.message.out.name") + ":",
            99,
            25,
            4210752,
            this.fontRenderer
        )
        GCCoreUtil.drawStringRightAligned(
            GCCoreUtil.translate("gui.message.in.name") + ":",
            99,
            37,
            4210752,
            this.fontRenderer
        )
        GCCoreUtil.drawStringCentered(
            GCCoreUtil.translate("gui.message.status.name") + ": " + this.getStatus(),
            this.xSize / 2,
            50,
            4210752,
            this.fontRenderer
        )
        val status: String = GCCoreUtil.translate("gui.status.collecting.name") + ": " + (0.5f + min(
            this.collector.lastOxygenCollected * 20f,
            TileEntityOxygenCollector.OUTPUT_PER_TICK * 20f
        )).toInt() + GCCoreUtil.translate("gui.per_second")
        GCCoreUtil.drawStringCentered(status, this.xSize / 2, 60, 4210752, this.fontRenderer)
        this.fontRenderer.drawString(GCCoreUtil.translate("container.inventory"), 8, this.ySize - 90 + 2, 4210752)
    }

    private fun getStatus(): String {
        val returnValue: String = this.collector!!.getGUIstatus()

        if (returnValue == EnumColor.DARK_GREEN.toString() + GCCoreUtil.translate("gui.status.active.name") && this.collector.lastOxygenCollected <= 0.0f) {
            return EnumColor.DARK_RED.toString() + GCCoreUtil.translate("gui.status.missingleaves.name")
        }

        return returnValue
    }

    override fun drawGuiContainerBackgroundLayer(var1: Float, var2: Int, var3: Int) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        this.mc.getTextureManager().bindTexture(collectorTexture)
        val var5: Int = (this.width - this.xSize) / 2
        val var6: Int = (this.height - this.ySize) / 2
        this.drawTexturedModalRect(var5, var6 + 5, 0, 0, this.xSize, 181)

        if (this.collector != null) {
            var scale: Int = this.collector.getCappedScaledOxygenLevel(54)
            this.drawTexturedModalRect(var5 + 113, var6 + 25, 197, 7, min(scale, 54), 7)
            scale = this.collector.getScaledElecticalLevel(54)
            this.drawTexturedModalRect(var5 + 113, var6 + 38, 197, 0, min(scale, 54), 7)

            if (this.collector.getEnergyStoredGC() > 0) {
                this.drawTexturedModalRect(var5 + 99, var6 + 37, 176, 0, 11, 10)
            }

            if (this.collector.getOxygenStored() > 0) {
                this.drawTexturedModalRect(var5 + 100, var6 + 24, 187, 0, 10, 10)
            }

            val oxygenDesc: MutableList<String?> = ArrayList<String?>()
            oxygenDesc.add(GCCoreUtil.translate("gui.oxygen_storage.desc.0"))
            oxygenDesc.add(
                EnumColor.YELLOW.toString() + GCCoreUtil.translate("gui.oxygen_storage.desc.1") + ": " + (floor(
                    this.collector.getOxygenStored().toDouble()
                ).toInt().toString() + " / " + floor(this.collector.getMaxOxygenStored().toDouble()).toInt())
            )
            this.oxygenInfoRegion.tooltipStrings = oxygenDesc

            val electricityDesc: MutableList<String?> = ArrayList<String?>()
            electricityDesc.add(GCCoreUtil.translate("gui.energy_storage.desc.0"))
            EnergyDisplayHelper.getEnergyDisplayTooltip(
                this.collector.getEnergyStoredGC(),
                this.collector.getMaxEnergyStoredGC(),
                electricityDesc
            )
            //			electricityDesc.add(EnumColor.YELLOW + GCCoreUtil.translate("gui.energy_storage.desc.1") + ((int) Math.floor(this.collector.getEnergyStoredGC()) + " / " + (int) Math.floor(this.collector.getMaxEnergyStoredGC())));
            this.electricInfoRegion.tooltipStrings = electricityDesc
        }
    }

    companion object {
        private val collectorTexture = ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/oxygen.png")
    }
}
