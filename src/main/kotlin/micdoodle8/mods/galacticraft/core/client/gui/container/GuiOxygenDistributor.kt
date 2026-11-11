package micdoodle8.mods.galacticraft.core.client.gui.container

import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementCheckbox.ICheckBoxCallback
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket
import java.util.ArrayList

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementCheckbox
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementInfoRegion
import micdoodle8.mods.galacticraft.core.energy.EnergyDisplayHelper
import micdoodle8.mods.galacticraft.core.inventory.ContainerOxygenDistributor
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.tile.TileEntityOxygenDistributor
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.entity.player.Inventory as InventoryPlayer
import net.minecraft.resources.Identifier as ResourceLocation
import org.lwjgl.opengl.GL11
import kotlin.math.floor
import kotlin.math.min

open class GuiOxygenDistributor constructor(
    par1InventoryPlayer: InventoryPlayer?,
    private val distributor: TileEntityOxygenDistributor?
) : GuiContainerGC(ContainerOxygenDistributor((par1InventoryPlayer)!!, (distributor)!!)), GuiElementCheckbox.ICheckBoxCallback {
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

    private var checkboxRenderBubble: GuiElementCheckbox? = null

    init {
        this.ySize = 180
    }

    override fun initGui() {
        super.initGui()
        val var5: Int = (this.width - this.xSize) / 2
        val var6: Int = (this.height - this.ySize) / 2
        val batterySlotDesc: MutableList<String?> = ArrayList<String?>()
        batterySlotDesc.add(GCCoreUtil.translate("gui.battery_slot.desc.0"))
        batterySlotDesc.add(GCCoreUtil.translate("gui.battery_slot.desc.1"))
        this.infoRegions.add(
            GuiElementInfoRegion(
                (this.width - this.xSize) / 2 + 46,
                (this.height - this.ySize) / 2 + 26,
                18,
                18,
                batterySlotDesc,
                this.width,
                this.height,
                this
            )
        )
        val oxygenSlotDesc: MutableList<String?> = ArrayList<String?>()
        oxygenSlotDesc.add(GCCoreUtil.translate("gui.oxygen_slot.desc.0"))
        oxygenSlotDesc.add(GCCoreUtil.translate("gui.oxygen_slot.desc.1"))
        this.infoRegions.add(
            GuiElementInfoRegion(
                (this.width - this.xSize) / 2 + 16,
                (this.height - this.ySize) / 2 + 26,
                18,
                18,
                oxygenSlotDesc,
                this.width,
                this.height,
                this
            )
        )
        val oxygenDesc: MutableList<String?> = ArrayList<String?>()
        oxygenDesc.add(GCCoreUtil.translate("gui.oxygen_storage.desc.0"))
        oxygenDesc.add(
            EnumColor.YELLOW.toString() + GCCoreUtil.translate("gui.oxygen_storage.desc.1") + ": " + (floor(
                this.distributor!!.getOxygenStored().toDouble()
            ).toInt().toString() + " / " + floor(this.distributor.getMaxOxygenStored().toDouble()).toInt())
        )
        this.oxygenInfoRegion.tooltipStrings = oxygenDesc
        this.oxygenInfoRegion.xPosition = (this.width - this.xSize) / 2 + 112
        this.oxygenInfoRegion.yPosition = (this.height - this.ySize) / 2 + 24
        this.oxygenInfoRegion.parentWidth = this.width
        this.oxygenInfoRegion.parentHeight = this.height
        this.infoRegions.add(this.oxygenInfoRegion)
        val electricityDesc: MutableList<String?> = ArrayList<String?>()
        electricityDesc.add(GCCoreUtil.translate("gui.energy_storage.desc.0"))
        electricityDesc.add(
            EnumColor.YELLOW.toString() + GCCoreUtil.translate("gui.energy_storage.desc.1") + (floor(
                this.distributor.getEnergyStoredGC().toDouble()
            ).toInt().toString() + " / " + floor(this.distributor.getMaxEnergyStoredGC().toDouble()).toInt())
        )
        this.electricInfoRegion.tooltipStrings = electricityDesc
        this.electricInfoRegion.xPosition = (this.width - this.xSize) / 2 + 112
        this.electricInfoRegion.yPosition = (this.height - this.ySize) / 2 + 37
        this.electricInfoRegion.parentWidth = this.width
        this.electricInfoRegion.parentHeight = this.height
        this.infoRegions.add(this.electricInfoRegion)
        this.checkboxRenderBubble =
            GuiElementCheckbox(0, this, var5 + 85, var6 + 87, GCCoreUtil.translate("gui.message.bubble_visible.name"))
        this.buttonList.add(this.checkboxRenderBubble)
    }

    override fun drawGuiContainerForegroundLayer(par1: Int, par2: Int) {
        this.fontRenderer.drawString(this.distributor!!.getName(), 8, 10, 4210752)
        GCCoreUtil.drawStringRightAligned(
            GCCoreUtil.translate("gui.message.in.name") + ":",
            99,
            26,
            4210752,
            this.fontRenderer
        )
        GCCoreUtil.drawStringRightAligned(
            GCCoreUtil.translate("gui.message.in.name") + ":",
            99,
            38,
            4210752,
            this.fontRenderer
        )
        var status: String = GCCoreUtil.translate("gui.message.status.name") + ": " + this.getStatus()
        this.fontRenderer.drawString(status, this.xSize / 2 - this.fontRenderer.getStringWidth(status) / 2, 50, 4210752)
        status =
            GCCoreUtil.translate("gui.oxygen_use.desc") + ": " + this.distributor.oxygenPerTick * 20 + GCCoreUtil.translate(
                "gui.per_second"
            )
        this.fontRenderer.drawString(status, this.xSize / 2 - this.fontRenderer.getStringWidth(status) / 2, 60, 4210752)
        //		status = ElectricityDisplay.getDisplay(this.distributor.ueWattsPerTick * 20, ElectricUnit.WATT);
        //		this.fontRenderer.drawString(status, this.xSize / 2 - this.fontRenderer.getStringWidth(status) / 2, 70, 4210752);
        //		status = ElectricityDisplay.getDisplay(this.distributor.getVoltage(), ElectricUnit.VOLTAGE);
        //		this.fontRenderer.drawString(status, this.xSize / 2 - this.fontRenderer.getStringWidth(status) / 2, 80, 4210752);
        this.fontRenderer.drawString(GCCoreUtil.translate("container.inventory"), 8, this.ySize - 90 + 3, 4210752)
    }

    private fun getStatus(): String? {
        if (this.distributor!!.getOxygenStored() < this.distributor.oxygenPerTick) {
            return EnumColor.DARK_RED.toString() + GCCoreUtil.translate("gui.status.missingoxygen.name")
        }

        return this.distributor.getGUIstatus()
    }

    override fun drawGuiContainerBackgroundLayer(var1: Float, var2: Int, var3: Int) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        this.mc.getTextureManager().bindTexture(distributorTexture)
        val var5: Int = (this.width - this.xSize) / 2
        val var6: Int = (this.height - this.ySize) / 2
        this.drawTexturedModalRect(var5, var6 + 5, 0, 0, this.xSize, 181)

        if (this.distributor != null) {
            var scale: Int = this.distributor.getCappedScaledOxygenLevel(54)
            this.drawTexturedModalRect(var5 + 113, var6 + 25, 197, 7, min(scale, 54), 7)
            scale = this.distributor.getScaledElecticalLevel(54)
            this.drawTexturedModalRect(var5 + 113, var6 + 38, 197, 0, min(scale, 54), 7)

            if (this.distributor.getEnergyStoredGC() > 0) {
                this.drawTexturedModalRect(var5 + 99, var6 + 37, 176, 0, 11, 10)
            }

            if (this.distributor.getOxygenStored() > 0) {
                this.drawTexturedModalRect(var5 + 100, var6 + 24, 187, 0, 10, 10)
            }

            val oxygenDesc: MutableList<String?> = ArrayList<String?>()
            oxygenDesc.add(GCCoreUtil.translate("gui.oxygen_storage.desc.0"))
            oxygenDesc.add(
                EnumColor.YELLOW.toString() + GCCoreUtil.translate("gui.oxygen_storage.desc.1") + ": " + (floor(
                    this.distributor.getOxygenStored().toDouble()
                ).toInt().toString() + " / " + floor(this.distributor.getMaxOxygenStored().toDouble()).toInt())
            )
            this.oxygenInfoRegion.tooltipStrings = oxygenDesc

            val electricityDesc: MutableList<String?> = ArrayList<String?>()
            electricityDesc.add(GCCoreUtil.translate("gui.energy_storage.desc.0"))
            EnergyDisplayHelper.getEnergyDisplayTooltip(
                this.distributor.getEnergyStoredGC(),
                this.distributor.getMaxEnergyStoredGC(),
                electricityDesc
            )
            //			electricityDesc.add(EnumColor.YELLOW + GCCoreUtil.translate("gui.energy_storage.desc.1") + ((int) Math.floor(this.distributor.getEnergyStoredGC()) + " / " + (int) Math.floor(this.distributor.getMaxEnergyStoredGC())));
            this.electricInfoRegion.tooltipStrings = electricityDesc

            this.checkboxRenderBubble!!.isSelected = this.distributor.shouldRenderBubble
        }
    }

    override fun onSelectionChanged(checkbox: GuiElementCheckbox?, newSelected: Boolean) {
        this.distributor!!.setBubbleVisible(newSelected)
        (GalacticraftCore.packetPipeline)!!.sendToServer(
            PacketSimple(
                PacketSimple.EnumSimplePacket.S_ON_ADVANCED_GUI_CLICKED_INT,
                GCCoreUtil.getDimensionID(mc.world),
                arrayOf<Any>(6, this.distributor.getPos(), if (newSelected) 1 else 0)
            )
        )
    }

    override fun canPlayerEdit(checkbox: GuiElementCheckbox?, player: EntityPlayer?): Boolean {
        return true
    }

    override fun getInitiallySelected(checkbox: GuiElementCheckbox?): Boolean {
        return this.distributor!!.shouldRenderBubble
    }

    override fun onIntruderInteraction() {
    }

    companion object {
        private val distributorTexture = ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/gui/oxygen_distributor.png")
    }
}
