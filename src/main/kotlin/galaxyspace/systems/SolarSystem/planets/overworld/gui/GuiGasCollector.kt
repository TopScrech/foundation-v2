package galaxyspace.systems.SolarSystem.planets.overworld.gui

import java.util.ArrayList
import java.util.List

import galaxyspace.GalaxySpace
import galaxyspace.core.GSItems
import galaxyspace.core.client.gui.tile.GuiTileBase
import galaxyspace.core.util.GSUtils
import galaxyspace.systems.SolarSystem.planets.overworld.inventory.ContainerGasCollector
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityGasCollector
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementInfoRegion
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.world.entity.player.Inventory as InventoryPlayer
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import org.lwjgl.opengl.GL11

@OnlyIn(Dist.CLIENT)
open class GuiGasCollector constructor(
    par1InventoryPlayer: InventoryPlayer?,
    private val tileEntity: TileEntityGasCollector
) : GuiTileBase(ContainerGasCollector((par1InventoryPlayer)!!, tileEntity), 2, 1) {
    private val gasTankRegion = GuiElementInfoRegion(
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
        this.ySize = 204
        moduleInfoX = this.inventorySlots.getSlotFromInventory(tileEntity, 3)!!.xPos
        moduleInfoY = this.inventorySlots.getSlotFromInventory(tileEntity, 3)!!.yPos
        moduleList = arrayOf<ItemStack>(ItemStack(GSItems.UPGRADES, 1, 0))
    }

    public override fun initGui() {
        super.initGui()

        val fuelTankDesc: MutableList<String?> = ArrayList<String?>()
        val fuelLevel: Int =
            if (this.tileEntity.gasTank != null && this.tileEntity.gasTank.getFluid() != null) this.tileEntity.gasTank.getFluid()!!.amount else 0
        val fuelCapacity: Int = if (this.tileEntity.gasTank != null) this.tileEntity.gasTank.getCapacity() else 0
        fuelTankDesc.add(EnumColor.YELLOW.toString() + GCCoreUtil.translate("gui.message.fuel.name") + ": " + fuelLevel + " / " + fuelCapacity)
        this.gasTankRegion.tooltipStrings = fuelTankDesc
        this.gasTankRegion.xPosition = (this.width - this.xSize) / 2 + 80
        this.gasTankRegion.yPosition = (this.height - this.ySize) / 2 + 20
        this.gasTankRegion.parentWidth = this.width
        this.gasTankRegion.parentHeight = this.height
        this.infoRegions.add(this.gasTankRegion)

        var desc: MutableList<String?> = ArrayList<String?>()
        desc = ArrayList<String?>()
        desc.add(EnumColor.BRIGHT_GREEN.toString() + GCCoreUtil.translate("gui.available_modules.desc"))
        desc.add("")
        desc.add("- " + ItemStack(GSItems.UPGRADES, 1, 0).getDisplayName())
        desc.add("- " + ItemStack(GSItems.UPGRADES, 1, 3).getDisplayName())
        this.infoRegions.add(
            GuiElementInfoRegion(
                (this.width + this.xSize) / 2,
                (this.height - this.ySize) / 2 + 16,
                18,
                21 * 4,
                desc,
                this.width,
                this.height,
                this
            )
        )

        desc = ArrayList<String?>()
        desc.add(GCCoreUtil.translate("gui.atmospheric_value_work.desc"))
        this.infoRegions.add(
            GuiElementInfoRegion(
                (this.width - this.xSize) / 2 + 6,
                (this.height - this.ySize) / 2 + 17,
                18,
                18,
                desc,
                this.width,
                this.height,
                this
            )
        )
    }

    protected override fun drawGuiContainerForegroundLayer(par1: Int, par2: Int) {
        super.drawGuiContainerForegroundLayer(par1, par2)


        val displayText: String

        if (this.tileEntity.hasEnoughEnergyToRun && this.tileEntity.canProcess()) {
            displayText = EnumColor.BRIGHT_GREEN.toString() + GCCoreUtil.translate("gui.status.running.name")
        } else {
            displayText = EnumColor.ORANGE.toString() + GCCoreUtil.translate("gui.status.idle.name")
        }

        val str: String =
            EnumColor.WHITE.toString() + GCCoreUtil.translate("gui.message.status.name") + ": " + displayText
        this.fontRenderer.drawString(str, 128 - this.fontRenderer.getStringWidth(str) / 2, 104, 4210752)
    }

    /**
     * Draw the background layer for the GuiContainer (everything behind the
     * items)
     */
    protected override fun drawGuiContainerBackgroundLayer(par1: Float, par2: Int, par3: Int) {
        super.drawGuiContainerBackgroundLayer(par1, par2, par3)


        // this.mc.renderEngine.bindTexture(GuiGasCollector.guiTexture);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)

        val containerWidth: Int = (this.width - this.xSize) / 2
        val containerHeight: Int = (this.height - this.ySize) / 2


        //this.drawTexturedModalRect(containerWidth, containerHeight, 0, 0, this.xSize, this.ySize); // Base Gui


        /*       int scale;

        List<String?> electricityDesc = new ArrayList<String?>();
        electricityDesc.add(GCCoreUtil.translate("gui.energy_storage.desc.0"));
        EnergyDisplayHelper.getEnergyDisplayTooltip(this.tileEntity.getEnergyStoredGC(), this.tileEntity.getMaxEnergyStoredGC(), electricityDesc);
        this.electricInfoRegion.tooltipStrings = electricityDesc;

        if (this.tileEntity.processTicks > 0)
        {
            scale = (int) ((double) this.tileEntity.processTicks / (double) this.tileEntity.processTimeRequired * 100);
        }
        else
        {
            scale = 100;
        }
        / *this.drawTexturedModalRect(containerWidth + 45, containerHeight + 36, 230, 60+26, 24, 26);
        this.drawTexturedModalRect(containerWidth + 45, containerHeight + 56, 230, 60+26, 24, 26);

        this.drawTexturedModalRect(containerWidth + 105, containerHeight + 36, 230, 60+26, 24, 26);
        this.drawTexturedModalRect(containerWidth + 105, containerHeight + 56, 230, 60+26, 24, 26);
*/


        //Energy
        this.renderEnergyBar(
            containerWidth + 7,
            containerHeight + 102,
            this.tileEntity.getScaledElecticalLevel(55),
            this.tileEntity.getEnergyStoredGC(),
            this.tileEntity.getMaxEnergyStoredGC()
        )


        //FluidTank
        this.drawTexturedModalRect(containerWidth + this.xSize / 2 - 19, containerHeight + 18, 180, 67, 20, 42)

        val fluid: FluidStack? = this.tileEntity.gasTank.getFluid()

        if (fluid != null) GSUtils.displayGauge(
            containerWidth + this.xSize / 2 - 18,
            containerHeight + 0,
            tileEntity.getScaledTankLevel(38),
            tileEntity.gasTank.getFluid(),
            0
        )

        this.mc.renderEngine.bindTexture(getTexture())
        this.drawTexturedModalRect(containerWidth + this.xSize / 2 - 19, containerHeight + 18, 200, 67, 20, 42)


        //Info FLuidTank
        val fuelTankDesc: MutableList<String?> = ArrayList<String?>()
        val fuelLevel: Int =
            if (this.tileEntity.gasTank != null && this.tileEntity.gasTank.getFluid() != null) this.tileEntity.gasTank.getFluid()!!.amount else 0
        val fuelCapacity: Int = if (this.tileEntity.gasTank != null) this.tileEntity.gasTank.getCapacity() else 0
        if (fluid != null) fuelTankDesc.add(
            EnumColor.YELLOW.toString() + this.tileEntity.gasTank.getFluid()!!
                .getLocalizedName() + ": " + fuelLevel + " / " + fuelCapacity
        )
        this.gasTankRegion.tooltipStrings = fuelTankDesc

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
