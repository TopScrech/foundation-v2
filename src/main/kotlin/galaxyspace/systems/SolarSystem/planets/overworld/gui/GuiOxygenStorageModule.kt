package galaxyspace.systems.SolarSystem.planets.overworld.gui

import java.util.ArrayList

import galaxyspace.GalaxySpace
import galaxyspace.core.client.gui.tile.GuiTileBase
import galaxyspace.core.util.GSUtils
import galaxyspace.systems.SolarSystem.planets.overworld.inventory.ContainerOxygenStorageModule
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityAdvOxygenStorageModule
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementInfoRegion
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.world.entity.player.Inventory as InventoryPlayer
import net.minecraft.world.inventory.Slot
import net.minecraft.ChatFormatting as TextFormatting
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import org.lwjgl.opengl.GL11

@OnlyIn(Dist.CLIENT)
open class GuiOxygenStorageModule constructor(
    par1InventoryPlayer: InventoryPlayer?,
    private val tileEntity: TileEntityAdvOxygenStorageModule
) : GuiTileBase(ContainerOxygenStorageModule((par1InventoryPlayer)!!, tileEntity), 2, 1) {
    init {
        this.header = 0
    }

    public override fun initGui() {
        super.initGui()
        val oxygenSlotDesc: MutableList<String?> = ArrayList<String?>()
        oxygenSlotDesc.add(GCCoreUtil.translate("gui.oxygen_slot.desc.0"))
        oxygenSlotDesc.add(GCCoreUtil.translate("gui.oxygen_slot.desc.1"))
        this.infoRegions.add(
            GuiElementInfoRegion(
                (this.width - this.xSize) / 2 + 16,
                (this.height - this.ySize) / 2 + 21,
                18,
                18,
                oxygenSlotDesc,
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
    protected override fun drawGuiContainerForegroundLayer(par1: Int, par2: Int) {
        super.drawGuiContainerForegroundLayer(par1, par2)

        val displayJoules: String = (this.tileEntity.getOxygenStored() + 0.5f).toInt()
            .toString() + " " + GCCoreUtil.translate("gui.message.of.name")
        val displayMaxJoules: String = "" + this.tileEntity.getMaxOxygenStored()
        val maxOutputLabel: String =
            GCCoreUtil.translate("gui.max_output.desc") + ": " + TileEntityAdvOxygenStorageModule.OUTPUT_PER_TICK * 20 + GCCoreUtil.translate(
                "gui.per_second"
            )

        val color: TextFormatting = if (getStyle() == Style.MODERN) TextFormatting.WHITE else TextFormatting.DARK_GRAY

        this.fontRenderer.drawString(
            EnumColor.YELLOW.toString() + displayJoules,
            122 - this.fontRenderer.getStringWidth(displayJoules) / 2 - 35,
            30,
            4210752
        )
        this.fontRenderer.drawString(
            color.toString() + displayMaxJoules,
            122 - this.fontRenderer.getStringWidth(displayMaxJoules) / 2 - 35,
            40,
            4210752
        )
        this.fontRenderer.drawString(
            color.toString() + maxOutputLabel,
            122 - this.fontRenderer.getStringWidth(maxOutputLabel) / 2 - 35,
            60,
            4210752
        )
    }

    /**
     * Draw the background layer for the GuiContainer (everything behind the
     * items)
     */
    protected override fun drawGuiContainerBackgroundLayer(par1: Float, par2: Int, par3: Int) {
        super.drawGuiContainerBackgroundLayer(par1, par2, par3)
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)

        val containerWidth: Int = (this.width - this.xSize) / 2
        val containerHeight: Int = (this.height - this.ySize) / 2
        // Background energy bar
        this.drawTexturedModalRect(containerWidth + 52, containerHeight + 52, 180, 226, 72, 5)
        // Foreground energy bar
        val scale: Int = (this.tileEntity.getOxygenStored().toDouble() / this.tileEntity.getMaxOxygenStored()
            .toDouble() * 72).toInt()
        this.drawTexturedModalRect(containerWidth + 53, containerHeight + 53, 180, 222, scale, 3)

        if (GalaxySpace.debug) GSUtils.renderDebugGui(this, containerWidth, containerHeight)
    }

    override fun isModuleSupport(): Boolean {
        return false
    }

    override fun getName(): String {
        return tileEntity.getName()
    }

    override fun getBatterySlot(): Slot? {
        return null
    }
}
