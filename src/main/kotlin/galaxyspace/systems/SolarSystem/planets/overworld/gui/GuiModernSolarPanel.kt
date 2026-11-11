package galaxyspace.systems.SolarSystem.planets.overworld.gui

import java.util.ArrayList
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket

import galaxyspace.core.client.gui.tile.GuiTileBase
import galaxyspace.systems.SolarSystem.planets.overworld.inventory.ContainerModernSolarPanel
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityModernSolarPanel
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementInfoRegion
import micdoodle8.mods.galacticraft.core.energy.EnergyDisplayHelper
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.gui.GuiButton
import net.minecraft.world.entity.player.Inventory as InventoryPlayer
import net.minecraft.world.inventory.Slot
import org.lwjgl.opengl.GL11
import kotlin.math.min

open class GuiModernSolarPanel constructor(
    par1InventoryPlayer: InventoryPlayer?,
    private val solarPanel: TileEntityModernSolarPanel
) : GuiTileBase(ContainerModernSolarPanel((par1InventoryPlayer)!!, solarPanel), 2, 1) {
    private var buttonEnableSolar: GuiButton? = null

    init {
        this.ySize = 201
        this.header = 0
    }

    override fun actionPerformed(par1GuiButton: GuiButton) {
        when (par1GuiButton.id) {
            0 -> (GalacticraftCore.packetPipeline)!!.sendToServer(
                PacketSimple(
                    PacketSimple.EnumSimplePacket.S_UPDATE_DISABLEABLE_BUTTON,
                    GCCoreUtil.getDimensionID(this.mc.world),
                    arrayOf<Any>(this.solarPanel.getPos(), 0)
                )
            )
        }
    }

    public override fun initGui() {
        super.initGui()

        val sunGenDesc: MutableList<String?> = ArrayList<String?>()
        val sunVisible: Float = Math.round(this.solarPanel.solarStrength / 9.0f * 1000) / 10.0f
        sunGenDesc.add(
            if (this.solarPanel.solarStrength > 0) GCCoreUtil.translate("gui.status.sun_visible.name") + ": " + sunVisible + "%" else GCCoreUtil.translate(
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
                this.height / 2 - 19,
                72,
                20,
                GCCoreUtil.translate("gui.button.enable.name")
            ).also { this.buttonEnableSolar = it })
    }

    protected override fun drawGuiContainerForegroundLayer(par1: Int, par2: Int) {
        super.drawGuiContainerForegroundLayer(par1, par2)
        val offsetY: Int = 35
        this.buttonEnableSolar!!.enabled = this.solarPanel.disableCooldown == 0
        this.buttonEnableSolar!!.displayString =
            if (!this.solarPanel.getDisabled(0)) GCCoreUtil.translate("gui.button.disable.name") else GCCoreUtil.translate(
                "gui.button.enable.name"
            )
        var displayString: String = GCCoreUtil.translate("gui.message.status.name") + ": " + this.getStatus()
        this.fontRenderer.drawString(
            EnumColor.WHITE.toString() + displayString,
            this.xSize / 2 - this.fontRenderer.getStringWidth(displayString) / 2,
            45 + 23 - 46 + offsetY,
            4210752
        )
        displayString =
            GCCoreUtil.translate("gui.message.generating.name") + ": " + (if (this.solarPanel.generateWatts > 0) EnergyDisplayHelper.getEnergyDisplayS(
                this.solarPanel.generateWatts.toFloat()
            ) + "/t" else GCCoreUtil.translate("gui.status.not_generating.name"))
        this.fontRenderer.drawString(
            EnumColor.WHITE.toString() + displayString,
            this.xSize / 2 - this.fontRenderer.getStringWidth(displayString) / 2,
            34 + 23 - 46 + offsetY,
            4210752
        )
        var boost: Float = Math.round((this.solarPanel.getSolarBoost() - 1) * 1000) / 10.0f
        if (boost < 100.0f) boost = -100.0f
        displayString = GCCoreUtil.translate("gui.message.environment.name") + ": " + boost + "%"
        this.fontRenderer.drawString(
            EnumColor.WHITE.toString() + displayString,
            this.xSize / 2 - this.fontRenderer.getStringWidth(displayString) / 2,
            56 + 23 - 46 + offsetY,
            4210752
        )
    }

    private fun getStatus(): String {
        if (this.solarPanel.getDisabled(0)) {
            return EnumColor.ORANGE.toString() + GCCoreUtil.translate("gui.status.disabled.name")
        }

        if (!this.solarPanel.getWorld().isDaytime()) {
            return EnumColor.DARK_RED.toString() + GCCoreUtil.translate("gui.status.blockedfully.name")
        }

        if (this.solarPanel.getWorld().isRaining() || this.solarPanel.getWorld().isThundering()) {
            return EnumColor.DARK_RED.toString() + GCCoreUtil.translate("gui.status.raining.name")
        }

        if (this.solarPanel.solarStrength == 0) {
            return EnumColor.DARK_RED.toString() + GCCoreUtil.translate("gui.status.blockedfully.name")
        }

        if (this.solarPanel.solarStrength < 9) {
            return EnumColor.DARK_RED.toString() + GCCoreUtil.translate("gui.status.blockedpartial.name")
        }

        if (this.solarPanel.generateWatts > 0) {
            return EnumColor.DARK_GREEN.toString() + GCCoreUtil.translate("gui.status.collectingenergy.name")
        }

        return EnumColor.ORANGE.toString() + GCCoreUtil.translate("gui.status.unknown.name")
    }

    protected override fun drawGuiContainerBackgroundLayer(var1: Float, var2: Int, var3: Int) {
        super.drawGuiContainerBackgroundLayer(var1, var2, var3)
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        val var5: Int = (this.width - this.xSize) / 2
        val var6: Int = (this.height - this.ySize) / 2

        this.drawTexturedModalRect(var5 + 48, var6 + 18, 180, 198, 22, 22)

        this.renderEnergyBar(
            var5 + 83,
            var6 + 24,
            min(this.solarPanel.getScaledElecticalLevel(54), 54),
            this.solarPanel.getEnergyStoredGC(),
            this.solarPanel.getMaxEnergyStoredGC()
        )

        if (this.solarPanel.solarStrength > 0) this.drawTexturedModalRect(var5 + 51, var6 + 21, 203, 201, 16, 16)
    }

    override fun isModuleSupport(): Boolean {
        return false
    }

    override fun getName(): String {
        return solarPanel.getName()
    }

    override fun getBatterySlot(): Slot? {
        return inventorySlots.getSlotFromInventory(solarPanel, 0)
    }
}
