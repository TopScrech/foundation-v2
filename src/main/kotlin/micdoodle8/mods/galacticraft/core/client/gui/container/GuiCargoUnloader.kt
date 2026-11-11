package micdoodle8.mods.galacticraft.core.client.gui.container

import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket
import java.util.ArrayList

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementInfoRegion
import micdoodle8.mods.galacticraft.core.energy.EnergyDisplayHelper
import micdoodle8.mods.galacticraft.core.inventory.ContainerCargoLoader
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.tile.TileEntityCargoUnloader
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.gui.GuiButton
import net.minecraft.world.entity.player.Inventory as InventoryPlayer
import net.minecraft.resources.Identifier as ResourceLocation
import org.lwjgl.opengl.GL11
import kotlin.math.floor
import kotlin.math.min

open class GuiCargoUnloader constructor(
    par1InventoryPlayer: InventoryPlayer?,
    private val cargoUnloader: TileEntityCargoUnloader
) : GuiContainerGC(ContainerCargoLoader((par1InventoryPlayer)!!, cargoUnloader)) {
    private var buttonLoadItems: GuiButton? = null
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
    }

    override fun actionPerformed(par1GuiButton: GuiButton) {
        when (par1GuiButton.id) {
            0 -> (GalacticraftCore.packetPipeline)!!.sendToServer(
                PacketSimple(
                    PacketSimple.EnumSimplePacket.S_UPDATE_DISABLEABLE_BUTTON,
                    GCCoreUtil.getDimensionID(this.mc.world),
                    arrayOf<Any>(this.cargoUnloader.getPos(), 0)
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
                this.cargoUnloader.getEnergyStoredGC().toDouble()
            ).toInt().toString() + " / " + floor(this.cargoUnloader.getMaxEnergyStoredGC().toDouble()).toInt())
        )
        this.electricInfoRegion.tooltipStrings = electricityDesc
        this.electricInfoRegion.xPosition = (this.width - this.xSize) / 2 + 107
        this.electricInfoRegion.yPosition = (this.height - this.ySize) / 2 + 101
        this.electricInfoRegion.parentWidth = this.width
        this.electricInfoRegion.parentHeight = this.height
        this.infoRegions.add(this.electricInfoRegion)
        val batterySlotDesc: MutableList<String?> = ArrayList<String?>()
        batterySlotDesc.add(GCCoreUtil.translate("gui.battery_slot.desc.0"))
        batterySlotDesc.add(GCCoreUtil.translate("gui.battery_slot.desc.1"))
        this.infoRegions.add(
            GuiElementInfoRegion(
                (this.width - this.xSize) / 2 + 9,
                (this.height - this.ySize) / 2 + 26,
                18,
                18,
                batterySlotDesc,
                this.width,
                this.height,
                this
            )
        )
        this.buttonList.add(
            GuiButton(
                0,
                this.width / 2 - 1,
                this.height / 2 - 23,
                76,
                20,
                GCCoreUtil.translate("gui.button.unloaditems.name")
            ).also { this.buttonLoadItems = it })
    }

    override fun drawGuiContainerForegroundLayer(par1: Int, par2: Int) {
        val offsetX: Int = -17
        val offsetY: Int = 45
        this.fontRenderer.drawString(this.cargoUnloader.getName(), 60, 12, 4210752)
        this.buttonLoadItems!!.enabled = this.cargoUnloader.disableCooldown == 0
        this.buttonLoadItems!!.displayString =
            if (!this.cargoUnloader.getDisabled(0)) GCCoreUtil.translate("gui.button.stopunloading.name") else GCCoreUtil.translate(
                "gui.button.unloaditems.name"
            )
        this.fontRenderer.drawString(
            GCCoreUtil.translate("gui.message.status.name") + ": " + this.getStatus(),
            28 + offsetX,
            45 + 23 - 46 + offsetY,
            4210752
        )
        //this.fontRenderer.drawString("" + this.cargoUnloader.storage.getMaxExtract(), 28 + offsetX, 56 + 23 - 46 + offsetY, 4210752);
        //		this.fontRenderer.drawString(ElectricityDisplay.getDisplay(this.cargoUnloader.getVoltage(), ElectricUnit.VOLTAGE), 28 + offsetX, 68 + 23 - 46 + offsetY, 4210752);
        this.fontRenderer.drawString(GCCoreUtil.translate("container.inventory"), 8, this.ySize - 90, 4210752)
    }

    private fun getStatus(): String? {
        if (this.cargoUnloader.noTarget) {
            return EnumColor.DARK_RED.toString() + GCCoreUtil.translate("gui.status.notargetunload.name")
        }

        if (this.cargoUnloader.targetEmpty) {
            return EnumColor.DARK_RED.toString() + GCCoreUtil.translate("gui.status.targetempty.name")
        }

        if (this.cargoUnloader.targetNoInventory) {
            return EnumColor.DARK_RED.toString() + GCCoreUtil.translate("gui.status.noinvtarget.name")
        }

        return this.cargoUnloader.getGUIstatus()
    }

    override fun drawGuiContainerBackgroundLayer(var1: Float, var2: Int, var3: Int) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        this.mc.getTextureManager().bindTexture(unloaderTexture)
        val var5: Int = (this.width - this.xSize) / 2
        val var6: Int = (this.height - this.ySize) / 2
        this.drawTexturedModalRect(var5, var6 + 5, 0, 0, this.xSize, this.ySize)

        val electricityDesc: MutableList<String?> = ArrayList<String?>()
        electricityDesc.add(GCCoreUtil.translate("gui.energy_storage.desc.0"))
        EnergyDisplayHelper.getEnergyDisplayTooltip(
            this.cargoUnloader.getEnergyStoredGC(),
            this.cargoUnloader.getMaxEnergyStoredGC(),
            electricityDesc
        )
        //		electricityDesc.add(EnumColor.YELLOW + GCCoreUtil.translate("gui.energy_storage.desc.1") + ((int) Math.floor(this.cargoUnloader.getEnergyStoredGC()) + " / " + (int) Math.floor(this.cargoUnloader.getMaxEnergyStoredGC())));
        this.electricInfoRegion.tooltipStrings = electricityDesc

        if (this.cargoUnloader.getEnergyStoredGC() > 0) {
            this.drawTexturedModalRect(var5 + 94, var6 + 101, 176, 0, 11, 10)
        }

        this.drawTexturedModalRect(
            var5 + 108,
            var6 + 102,
            187,
            0,
            min(this.cargoUnloader.getScaledElecticalLevel(54), 54),
            7
        )
    }

    companion object {
        private val unloaderTexture = ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/gui/cargo_loader.png")
    }
}
