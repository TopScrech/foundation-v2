package galaxyspace.systems.SolarSystem.planets.overworld.gui

import galaxyspace.core.client.gui.tile.GuiTileBase
import galaxyspace.systems.SolarSystem.planets.overworld.inventory.ContainerModernStorageModule
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityModernStorageModule
import micdoodle8.mods.galacticraft.core.energy.EnergyDisplayHelper
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Slot
import net.minecraft.util.text.TextFormatting
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11

@SideOnly(Side.CLIENT)
open class GuiModernStorageModule constructor(
    par1InventoryPlayer: InventoryPlayer?,
    private val tileEntity: TileEntityModernStorageModule
) : GuiTileBase(ContainerModernStorageModule((par1InventoryPlayer)!!, tileEntity), 2, 1) {
    init {
        this.header = 3
    }

    public override fun initGui() {
        super.initGui()
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of
     * the items)
     */
    protected override fun drawGuiContainerForegroundLayer(par1: Int, par2: Int) {
        super.drawGuiContainerForegroundLayer(par1, par2)

        var energy: Float = this.tileEntity.getEnergyStoredGC()
        if (energy + 49 > this.tileEntity.getMaxEnergyStoredGC()) {
            energy = this.tileEntity.getMaxEnergyStoredGC()
        }
        val color: TextFormatting = if (getStyle() == Style.MODERN) TextFormatting.WHITE else TextFormatting.DARK_GRAY

        var displayStr: String = EnumColor.YELLOW.toString() + EnergyDisplayHelper.getEnergyDisplayS(energy)
        this.fontRenderer.drawString(displayStr, 112 - this.fontRenderer.getStringWidth(displayStr) / 2, 25, 4210752)
        displayStr =
            color.toString() + GCCoreUtil.translate("gui.message.of.name") + " " + EnergyDisplayHelper.getEnergyDisplayS(
                this.tileEntity.getMaxEnergyStoredGC()
            )
        this.fontRenderer.drawString(displayStr, 112 - this.fontRenderer.getStringWidth(displayStr) / 2, 34, 4210752)
        displayStr =
            color.toString() + GCCoreUtil.translate("gui.max_output.desc") + ": " + EnergyDisplayHelper.getEnergyDisplayS(
                this.tileEntity.storage.getMaxExtract()
            ) + "/t"
        this.fontRenderer.drawString(displayStr, 116 - this.fontRenderer.getStringWidth(displayStr) / 2, 62, 4210752)
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

        this.drawTexturedModalRect(containerWidth + 82, containerHeight + 46, 180, 47, 56, 9)

        val scale: Int = ((this.tileEntity.getEnergyStoredGC()) / this.tileEntity.getMaxEnergyStoredGC() * 56).toInt()
        this.drawTexturedModalRect(containerWidth + 83, containerHeight + 47, 180, 0, scale, 7)
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
