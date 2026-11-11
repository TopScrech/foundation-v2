package galaxyspace.systems.SolarSystem.planets.overworld.gui

import java.util.ArrayList

import galaxyspace.GalaxySpace
import galaxyspace.core.client.gui.tile.GuiTileBase
import galaxyspace.core.util.GSUtils
import galaxyspace.systems.SolarSystem.planets.overworld.inventory.ContainerGasGenerator
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityGasGenerator
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementInfoRegion
import micdoodle8.mods.galacticraft.core.energy.EnergyDisplayHelper
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.world.entity.player.Inventory as InventoryPlayer
import net.minecraft.world.inventory.Slot
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import org.lwjgl.opengl.GL11
import kotlin.math.floor

@OnlyIn(Dist.CLIENT)
open class GuiGasGenerator constructor(
    par1InventoryPlayer: InventoryPlayer?,
    private val tileEntity: TileEntityGasGenerator
) : GuiTileBase(ContainerGasGenerator((par1InventoryPlayer)!!, tileEntity), 2, 1) {
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
        this.ySize = 170
        this.header = 4
    }

    public override fun initGui() {
        super.initGui()
        val fuelTankDesc: MutableList<String?> = ArrayList<String?>()

        this.fuelTankRegion.tooltipStrings = fuelTankDesc
        this.fuelTankRegion.xPosition = (this.width - this.xSize) / 2 + 16
        this.fuelTankRegion.yPosition = (this.height - this.ySize) / 2 + 35
        this.fuelTankRegion.parentWidth = this.width
        this.fuelTankRegion.parentHeight = this.height
        this.infoRegions.add(this.fuelTankRegion)
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of
     * the items)
     */
    protected override fun drawGuiContainerForegroundLayer(par1: Int, par2: Int) {
        super.drawGuiContainerForegroundLayer(par1, par2)
        val yOffset: Int = -18

        var displayText: String = ""

        val standart_color: EnumColor = if (getStyle() == Style.CLASSIC) EnumColor.DARK_GREY else EnumColor.WHITE
        if (this.tileEntity.heatGJperTick <= 0) {
            displayText = standart_color.toString() + GCCoreUtil.translate("gui.status.not_generating.name")
        } else if (tileEntity.getEnergyStoredGC() == tileEntity.getMaxEnergyStoredGC()) {
            displayText = EnumColor.ORANGE.toString() + GCCoreUtil.translate("gui.status.energy_storage_is_full.name")
        } else {
            displayText =
                (if (getStyle() == Style.CLASSIC) EnumColor.DARK_GREEN else EnumColor.BRIGHT_GREEN).toString() + GCCoreUtil.translate(
                    "gui.status.generating.name"
                ) + " " + EnergyDisplayHelper.getEnergyDisplayS(this.tileEntity.heatGJperTick) + "/t"
        }
        if (this.tileEntity.fuelTank.getFluid() == null && this.tileEntity.heatGJperTick <= 0) {
            displayText = EnumColor.RED.toString() + GCCoreUtil.translate("gui.status.nofuelgenerator.name")
        }
        this.fontRenderer.drawSplitString(
            standart_color.toString() + GCCoreUtil.translate("gui.message.status.name") + ": " + displayText,
            72,
            40 + yOffset,
            100,
            4210752
        )

        val displayStr: String =
            standart_color.toString() + GCCoreUtil.translate("gui.max_output.desc") + ": " + EnergyDisplayHelper.getEnergyDisplayS(
                this.tileEntity.storage.getMaxExtract()
            ) + "/t"
        this.fontRenderer.drawString(displayStr, 116 - this.fontRenderer.getStringWidth(displayStr) / 2, 72, 4210752)
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

        val fuel: FluidStack? = this.tileEntity.fuelTank.getFluid()

        this.renderProgressArray(containerWidth + 45, containerHeight + 40, 15, 0, 10)
        this.drawTexturedModalRect(containerWidth + 15, containerHeight + 35, 180, 67, 20, 42)

        if (fuel != null) GSUtils.displayGauge(
            containerWidth + 16,
            containerHeight + 16,
            tileEntity.getScaledFuelLevel(38),
            tileEntity.fuelTank.getFluid(),
            0
        )

        this.mc.renderEngine.bindTexture(getTexture())
        drawTexturedModalRect(containerWidth + 15, containerHeight + 35, 200, 67, 16, 42)


        val fuelTankDesc: MutableList<String?> = ArrayList<String?>()
        val fuelLevel: Int =
            if (this.tileEntity.fuelTank != null && this.tileEntity.fuelTank.getFluid() != null) this.tileEntity.fuelTank.getFluid()!!.amount else 0
        val fuelCapacity: Int = if (this.tileEntity.fuelTank != null) this.tileEntity.fuelTank.getCapacity() else 0
        if (fuel != null) {
            if (this.tileEntity.fuelTank.getFluid() != null) fuelTankDesc.add(
                EnumColor.YELLOW.toString() + this.tileEntity.fuelTank.getFluid()!!
                    .getLocalizedName() + ": " + fuelLevel + " / " + fuelCapacity
            )
            this.fuelTankRegion.tooltipStrings = fuelTankDesc
        }

        this.renderEnergyBar(
            containerWidth + 80,
            containerHeight + 43,
            floor((tileEntity.getEnergyStoredGC() * 54 / tileEntity.getMaxEnergyStoredGC()).toDouble()).toInt(),
            tileEntity.getEnergyStoredGC(),
            tileEntity.getMaxEnergyStoredGC()
        )



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
