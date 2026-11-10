package galaxyspace.systems.SolarSystem.planets.overworld.gui

import java.util.ArrayList
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementCheckbox.ICheckBoxCallback
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket

import galaxyspace.GalaxySpace
import galaxyspace.core.GSItems
import galaxyspace.core.client.gui.tile.GuiTileBase
import galaxyspace.core.util.GSUtils
import galaxyspace.systems.SolarSystem.planets.overworld.inventory.ContainerRadiationStabiliser
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityRadiationStabiliser
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementCheckbox
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementInfoRegion
import micdoodle8.mods.galacticraft.core.energy.EnergyDisplayHelper
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.RedstoneUtil
import net.minecraft.client.gui.GuiButton
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import net.minecraft.util.text.TextFormatting
import org.lwjgl.opengl.GL11

open class GuiRadiationStabiliser constructor(
    par1InventoryPlayer: InventoryPlayer?,
    tileEntity: TileEntityRadiationStabiliser
) : GuiTileBase(ContainerRadiationStabiliser((par1InventoryPlayer)!!, tileEntity), 2, 1),
    GuiElementCheckbox.ICheckBoxCallback {
    private val tileEntity: TileEntityRadiationStabiliser
    private var checkboxRenderBubble: GuiElementCheckbox? = null
    private var disableButton: GuiButton? = null

    @JvmField
    val BTN_ENABLE: Int = 6

    @JvmField
    val FIELD_STRENGTH: Int = 10

    init {
        this.ySize = 204
        this.tileEntity = tileEntity
        this.header = 3
        moduleInfoX = this.inventorySlots.getSlotFromInventory(tileEntity, 1)!!.xPos
        moduleInfoY = this.inventorySlots.getSlotFromInventory(tileEntity, 1)!!.yPos
        moduleList = arrayOf<ItemStack>(ItemStack(GSItems.UPGRADES, 1, 0), ItemStack(GSItems.UPGRADES, 1, 3))
    }

    override fun drawScreen(par1: Int, par2: Int, par3: Float) {
        super.drawScreen(par1, par2, par3)
    }

    public override fun initGui() {
        super.initGui()

        var desc: MutableList<String?> = ArrayList<String?>()
        desc = ArrayList<String?>()
        desc.add(GCCoreUtil.translate("gui.show_bubble.desc.0"))
        this.infoRegions.add(
            GuiElementInfoRegion(
                (this.width - this.xSize) / 2 + 10,
                (this.height - this.ySize) / 2 + 67,
                85,
                13,
                desc,
                this.width,
                this.height,
                this
            )
        )

        this.buttonList.clear()

        val var5: Int = (this.width - this.xSize) / 2
        val var6: Int = (this.height - this.ySize) / 2

        disableButton =
            GuiButton(BTN_ENABLE, var5 + 110, var6 + 64, 50, 20, GCCoreUtil.translate("gui.button.disable.name"))

        this.checkboxRenderBubble = GuiElementCheckbox(
            0,
            this,
            var5 + 10,
            var6 + 67,
            EnumColor.WHITE.toString() + GCCoreUtil.translate("gui.message.bubble_visible.name")
        )
        this.buttonList.add(this.checkboxRenderBubble)
        this.buttonList.add(disableButton)
    }

    override fun actionPerformed(par1GuiButton: GuiButton) {
        when (par1GuiButton.id) {
            BTN_ENABLE -> (GalacticraftCore.packetPipeline)!!.sendToServer(
                PacketSimple(
                    PacketSimple.EnumSimplePacket.S_UPDATE_DISABLEABLE_BUTTON,
                    GCCoreUtil.getDimensionID(this.mc.world),
                    arrayOf<Any>(this.tileEntity.getPos(), 0)
                )
            )

            else -> {}
        }
    }

    protected override fun drawGuiContainerForegroundLayer(par1: Int, par2: Int) {
        super.drawGuiContainerForegroundLayer(par1, par2)
        val color: TextFormatting = if (getStyle() == Style.MODERN) TextFormatting.WHITE else TextFormatting.DARK_GRAY
        this.fontRenderer.drawSplitString(
            color.toString() + GCCoreUtil.translate("gui.message.status.name") + ": " + this.getStatus(),
            105,
            98,
            this.xSize - 105,
            4210752
        )
    }

    private fun getStatus(): String {
        if (RedstoneUtil.isBlockReceivingRedstone(this.tileEntity.getWorld(), this.tileEntity.getPos())) {
            return EnumColor.RED.toString() + GCCoreUtil.translate("gui.status.off.name")
        }

        if (this.tileEntity.getEnergyStoredGC() <= 0.0f) {
            return EnumColor.RED.toString() + GCCoreUtil.translate("gui.message.no_energy.name")
        }

        if (tileEntity.getDisabled(0)) {
            return EnumColor.RED.toString() + GCCoreUtil.translate("gui.status.disabled.name")
        }

        if (this.tileEntity.hasEnoughEnergyToRun) {
            return EnumColor.BRIGHT_GREEN.toString() + GCCoreUtil.translate("gui.status.running.name")
        }

        return EnumColor.ORANGE.toString() + GCCoreUtil.translate("gui.status.idle.name")
    }

    protected override fun drawGuiContainerBackgroundLayer(par1: Float, par2: Int, par3: Int) {
        super.drawGuiContainerBackgroundLayer(par1, par2, par3)
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        val containerWidth: Int = (this.width - this.xSize) / 2
        val containerHeight: Int = (this.height - this.ySize) / 2
        //Energy
        this.renderEnergyBar(
            containerWidth + 5,
            containerHeight + 102,
            this.tileEntity.getScaledElecticalLevel(55),
            this.tileEntity.getEnergyStoredGC(),
            this.tileEntity.getMaxEnergyStoredGC()
        )

        this.checkboxRenderBubble!!.isSelected = this.tileEntity.shouldRenderBubble

        this.disableButton!!.displayString =
            GCCoreUtil.translate(if (this.tileEntity.getDisabled(0)) "gui.button.enable.name" else "gui.button.disable.name")


        var gen: String =
            "Consumption: " + EnergyDisplayHelper.getEnergyDisplayS(this.tileEntity.storage.getMaxExtract()) + "/t"
        this.drawString(fontRenderer, gen, containerWidth + 30, containerHeight + 40, 0xFFFFFF)
        gen = "Bubble size: " + this.tileEntity.bubbleSize.toInt() + " blocks"
        this.drawString(
            fontRenderer,
            gen,
            containerWidth + 30,
            containerHeight + 40 + fontRenderer.FONT_HEIGHT,
            0xFFFFFF
        )

        if (GalaxySpace.debug) GSUtils.renderDebugGui(this, containerWidth, containerHeight)
    }

    override fun onSelectionChanged(checkbox: GuiElementCheckbox?, newSelected: Boolean) {
        this.tileEntity.shouldRenderBubble = newSelected
        (GalacticraftCore.packetPipeline)!!.sendToServer(
            PacketSimple(
                PacketSimple.EnumSimplePacket.S_ON_ADVANCED_GUI_CLICKED_INT,
                GCCoreUtil.getDimensionID(mc.world),
                arrayOf<Any>(6, this.tileEntity.getPos(), if (newSelected) 1 else 0)
            )
        )
    }

    override fun canPlayerEdit(checkbox: GuiElementCheckbox?, player: EntityPlayer?): Boolean {
        return true
    }

    override fun getInitiallySelected(checkbox: GuiElementCheckbox?): Boolean {
        return this.tileEntity.shouldRenderBubble
    }

    override fun onIntruderInteraction() {
    }

    override fun isModuleSupport(): Boolean {
        return true
    }

    override fun getName(): String {
        return tileEntity.getName()
    }

    override fun getBatterySlot(): Slot? {
        return inventorySlots.getSlotFromInventory(tileEntity, 0)
    }
}
