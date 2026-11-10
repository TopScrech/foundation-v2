package galaxyspace.systems.SolarSystem.planets.overworld.gui

import java.util.ArrayList

import galaxyspace.GalaxySpace
import galaxyspace.core.GSItems
import galaxyspace.core.client.gui.tile.GuiTileBase
import galaxyspace.core.util.GSUtils
import galaxyspace.systems.SolarSystem.planets.overworld.inventory.ContainerLiquidExtractor
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityLiquidExtractor
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementInfoRegion
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import net.minecraft.util.text.TextFormatting
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11

@SideOnly(Side.CLIENT)
open class GuiLiquidExtractor constructor(
    par1InventoryPlayer: InventoryPlayer?,
    private val tileEntity: TileEntityLiquidExtractor
) : GuiTileBase(ContainerLiquidExtractor((par1InventoryPlayer)!!, tileEntity), 2, 1) {
    private val fuelTankRegion = GuiElementInfoRegion(
        (this.width - this.xSize) / 2 + 7,
        (this.height - this.ySize) / 2 + 28,
        16,
        38,
        ArrayList<String?>(),
        this.width,
        this.height,
        this
    )

    init {
        this.ySize = 190
        moduleInfoX = this.inventorySlots.getSlotFromInventory(tileEntity, 3)!!.xPos
        moduleInfoY = this.inventorySlots.getSlotFromInventory(tileEntity, 3)!!.yPos
        moduleList = arrayOf<ItemStack>(ItemStack(GSItems.UPGRADES, 1, 0), ItemStack(GSItems.UPGRADES, 1, 3))
    }

    public override fun initGui() {
        super.initGui()


        val fuelTankDesc: MutableList<String?> = ArrayList<String?>()
        val fuelLevel: Int =
            if (this.tileEntity.waterTank != null && this.tileEntity.waterTank.getFluid() != null) this.tileEntity.waterTank.getFluid()!!.amount else 0
        val fuelCapacity: Int = if (this.tileEntity.waterTank != null) this.tileEntity.waterTank.getCapacity() else 0
        fuelTankDesc.add(EnumColor.YELLOW.toString() + GCCoreUtil.translate("gui.message.fuel.name") + ": " + fuelLevel + " / " + fuelCapacity)
        this.fuelTankRegion.tooltipStrings = fuelTankDesc
        this.fuelTankRegion.xPosition = (this.width - this.xSize) / 2 + 80
        this.fuelTankRegion.yPosition = (this.height - this.ySize) / 2 + 20
        this.fuelTankRegion.parentWidth = this.width
        this.fuelTankRegion.parentHeight = this.height
        this.infoRegions.add(this.fuelTankRegion)
    }

    protected override fun drawGuiContainerForegroundLayer(par1: Int, par2: Int) {
        super.drawGuiContainerForegroundLayer(par1, par2)

        val displayText: String

        if (this.tileEntity.processTicks > 0) {
            displayText = EnumColor.BRIGHT_GREEN.toString() + GCCoreUtil.translate("gui.status.running.name")
        } else {
            displayText = EnumColor.ORANGE.toString() + GCCoreUtil.translate("gui.status.idle.name")
        }

        val color: TextFormatting = if (getStyle() == Style.MODERN) TextFormatting.WHITE else TextFormatting.DARK_GRAY
        val str: String = color.toString() + GCCoreUtil.translate("gui.message.status.name") + ": " + displayText
        this.fontRenderer.drawString(str, 135 - this.fontRenderer.getStringWidth(str) / 2, 84, 4210752)
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



        this.drawTexturedModalRect(containerWidth + 45, containerHeight + 36, 221, 87, 22, 22)
        this.drawTexturedModalRect(containerWidth + 45, containerHeight + 56, 221, 87, 22, 22)

        this.drawTexturedModalRect(containerWidth + 105, containerHeight + 36, 221, 87, 22, 22)
        this.drawTexturedModalRect(containerWidth + 105, containerHeight + 56, 221, 87, 22, 22)


        // this.drawTexturedModalRect(containerWidth + 105, containerHeight + 36, 230, 60+26, 24, 26);
        // this.drawTexturedModalRect(containerWidth + 105, containerHeight + 56, 230, 60+26, 24, 26);


        //Energy
        this.renderEnergyBar(
            containerWidth + 30,
            containerHeight + 84,
            this.tileEntity.getScaledElecticalLevel(55),
            this.tileEntity.getEnergyStoredGC(),
            this.tileEntity.getMaxEnergyStoredGC()
        )


        //FluidTank
        this.drawTexturedModalRect(containerWidth + this.xSize / 2 - 20, containerHeight + 18, 180, 67, 20, 42)

        val fluid: FluidStack? = this.tileEntity.waterTank.getFluid()
        if (fluid != null) GSUtils.displayGauge(
            containerWidth + this.xSize / 2 - 19,
            containerHeight - 1,
            tileEntity.getScaledTankLevel(38),
            tileEntity.waterTank.getFluid(),
            0
        )

        this.mc.renderEngine.bindTexture(this.getTexture())
        this.drawTexturedModalRect(containerWidth + this.xSize / 2 - 20, containerHeight + 18, 200, 67, 16, 42)


        //Info FLuidTank
        val fuelTankDesc: MutableList<String?> = ArrayList<String?>()
        val fuelLevel: Int =
            if (this.tileEntity.waterTank != null && this.tileEntity.waterTank.getFluid() != null) this.tileEntity.waterTank.getFluid()!!.amount else 0
        val fuelCapacity: Int = if (this.tileEntity.waterTank != null) this.tileEntity.waterTank.getCapacity() else 0
        if (fluid != null) fuelTankDesc.add(
            EnumColor.YELLOW.toString() + this.tileEntity.waterTank.getFluid()!!
                .getLocalizedName() + ": " + fuelLevel + " / " + fuelCapacity
        )
        this.fuelTankRegion.tooltipStrings = fuelTankDesc

        if (GalaxySpace.debug) GSUtils.renderDebugGui(this, containerWidth, containerHeight)
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
