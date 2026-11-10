package micdoodle8.mods.galacticraft.planets.mars.client.gui

import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket
import java.util.ArrayList

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.client.gui.container.GuiContainerGC
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementInfoRegion
import micdoodle8.mods.galacticraft.core.energy.EnergyDisplayHelper
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.RedstoneUtil
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import micdoodle8.mods.galacticraft.planets.mars.inventory.ContainerElectrolyzer
import micdoodle8.mods.galacticraft.planets.mars.tile.TileEntityElectrolyzer
import net.minecraft.client.gui.GuiButton
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11
import kotlin.math.min

@SideOnly(Side.CLIENT)
open class GuiWaterElectrolyzer constructor(
    par1InventoryPlayer: InventoryPlayer?,
    private val tileEntity: TileEntityElectrolyzer
) : GuiContainerGC(
    ContainerElectrolyzer(
        (par1InventoryPlayer)!!,
        tileEntity,
        FMLClientHandler.instance().getClient().player
    )
) {
    private var buttonDisable: GuiButton? = null

    private val fuelTankRegion = GuiElementInfoRegion(
        (this.width - this.xSize) / 2 + 153,
        (this.height - this.ySize) / 2 + 28,
        16,
        38,
        ArrayList<String?>(),
        this.width,
        this.height,
        this
    )
    private val fuelTank2Region = GuiElementInfoRegion(
        (this.width - this.xSize) / 2 + 153,
        (this.height - this.ySize) / 2 + 28,
        16,
        38,
        ArrayList<String?>(),
        this.width,
        this.height,
        this
    )
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
    private val electricInfoRegion = GuiElementInfoRegion(
        (this.width - this.xSize) / 2 + 62,
        (this.height - this.ySize) / 2 + 16,
        56,
        9,
        ArrayList<String?>(),
        this.width,
        this.height,
        this
    )

    init {
        this.ySize = 168
    }

    override fun initGui() {
        super.initGui()

        this.gasTankRegion.xPosition = (this.width - this.xSize) / 2 + 7
        this.gasTankRegion.yPosition = (this.height - this.ySize) / 2 + 28
        this.gasTankRegion.parentWidth = this.width
        this.gasTankRegion.parentHeight = this.height
        this.infoRegions.add(this.gasTankRegion)

        val batterySlotDesc: MutableList<String?> = ArrayList<String?>()
        batterySlotDesc.add(GCCoreUtil.translate("gui.battery_slot.desc.0"))
        batterySlotDesc.add(GCCoreUtil.translate("gui.battery_slot.desc.1"))
        this.infoRegions.add(
            GuiElementInfoRegion(
                (this.width - this.xSize) / 2 + 33,
                (this.height - this.ySize) / 2 + 49,
                18,
                18,
                batterySlotDesc,
                this.width,
                this.height,
                this
            )
        )

        this.fuelTankRegion.xPosition = (this.width - this.xSize) / 2 + 132
        this.fuelTankRegion.yPosition = (this.height - this.ySize) / 2 + 28
        this.fuelTankRegion.parentWidth = this.width
        this.fuelTankRegion.parentHeight = this.height
        this.infoRegions.add(this.fuelTankRegion)

        this.fuelTank2Region.xPosition = (this.width - this.xSize) / 2 + 153
        this.fuelTank2Region.yPosition = (this.height - this.ySize) / 2 + 28
        this.fuelTank2Region.parentWidth = this.width
        this.fuelTank2Region.parentHeight = this.height
        this.infoRegions.add(this.fuelTank2Region)

        //this.infoRegions.add(new GuiElementInfoRegion((this.width - this.xSize) / 2 + 152, (this.height - this.ySize) / 2 + 6, 18, 18, fuelSlotDesc, this.width, this.height, this));
        //this.infoRegions.add(new GuiElementInfoRegion((this.width - this.xSize) / 2 + 131, (this.height - this.ySize) / 2 + 6, 18, 18, fuelSlotDesc, this.width, this.height, this));
        val fuelSlotDesc: MutableList<String?> = ArrayList<String?>()
        fuelSlotDesc.addAll(GCCoreUtil.translateWithSplit("gui.water_bucket_slot.desc"))
        this.infoRegions.add(
            GuiElementInfoRegion(
                (this.width - this.xSize) / 2 + 6,
                (this.height - this.ySize) / 2 + 6,
                18,
                18,
                fuelSlotDesc,
                this.width,
                this.height,
                this
            )
        )

        this.electricInfoRegion.xPosition = (this.width - this.xSize) / 2 + 42
        this.electricInfoRegion.yPosition = (this.height - this.ySize) / 2 + 16
        this.electricInfoRegion.parentWidth = this.width
        this.electricInfoRegion.parentHeight = this.height
        this.infoRegions.add(this.electricInfoRegion)

        this.addToolTips()

        this.buttonList.add(
            GuiButton(
                0,
                this.width / 2 - 49,
                this.height / 2 - 56,
                76,
                20,
                GCCoreUtil.translate("gui.button.liquefy.name")
            ).also { this.buttonDisable = it })
    }

    override fun actionPerformed(par1GuiButton: GuiButton) {
        when (par1GuiButton.id) {
            0 -> (GalacticraftCore.packetPipeline)!!.sendToServer(
                PacketSimple(
                    PacketSimple.EnumSimplePacket.S_UPDATE_DISABLEABLE_BUTTON,
                    GCCoreUtil.getDimensionID(this.tileEntity.getWorld()),
                    arrayOf<Any>(this.tileEntity.getPos(), 0)
                )
            )
        }
    }

    override fun drawGuiContainerForegroundLayer(par1: Int, par2: Int) {
        this.fontRenderer.drawString(this.tileEntity.getName(), 30, 5, 4210752)
        var displayText: String = ""
        val yOffset: Int = -18

        if (RedstoneUtil.isBlockReceivingRedstone(this.tileEntity.getWorld(), this.tileEntity.getPos())) {
            displayText = EnumColor.RED.toString() + GCCoreUtil.translate("gui.status.off.name")
        } else if (!this.tileEntity.hasEnoughEnergyToRun) {
            displayText = EnumColor.RED.toString() + GCCoreUtil.translate("gui.message.low_energy.name")
        } else if (this.tileEntity.waterTank.getFluid() == null || this.tileEntity.waterTank.getFluidAmount() == 0) {
            displayText = EnumColor.RED.toString() + GCCoreUtil.translate("gui.message.zero_water.name")
        } else if (this.tileEntity.waterTank.getFluidAmount() > 0 && this.tileEntity.disabled) {
            displayText = EnumColor.ORANGE.toString() + GCCoreUtil.translate("gui.status.ready.name")
        } else if (this.tileEntity.canProcess()) {
            displayText = EnumColor.BRIGHT_GREEN.toString() + GCCoreUtil.translate("gui.status.running.name")
        } else if (this.tileEntity.liquidTank.getFluidAmount() == this.tileEntity.liquidTank.getCapacity() && this.tileEntity.liquidTank2.getFluidAmount() == this.tileEntity.liquidTank2.getCapacity()) {
            displayText = EnumColor.RED.toString() + GCCoreUtil.translate("gui.status.tanksfull.name")
        } else {
            displayText = EnumColor.RED.toString() + GCCoreUtil.translate("gui.status.unknown.name")
        }

        this.buttonDisable!!.enabled = this.tileEntity.disableCooldown == 0
        this.buttonDisable!!.displayString =
            if (this.tileEntity.processTicks == 0) GCCoreUtil.translate("gui.button.liquefy.name") else GCCoreUtil.translate(
                "gui.button.liquefy_stop.name"
            )
        this.fontRenderer.drawString(
            GCCoreUtil.translate("gui.message.status.name") + ":",
            56,
            45 + 23 + yOffset,
            4210752
        )
        this.fontRenderer.drawString(displayText, 62, 45 + 33 + yOffset, 4210752)
        //		this.fontRenderer.drawString(ElectricityDisplay.getDisplay(this.tileEntity.ueWattsPerTick * 20, ElectricUnit.WATT), 72, 56 + 23 + yOffset, 4210752);
        //		this.fontRenderer.drawString(ElectricityDisplay.getDisplay(this.tileEntity.getVoltage(), ElectricUnit.VOLTAGE), 72, 68 + 23 + yOffset, 4210752);
        this.fontRenderer.drawString(GCCoreUtil.translate("container.inventory"), 8, this.ySize - 118 + 2 + 23, 4210752)
    }

    override fun drawGuiContainerBackgroundLayer(par1: Float, par2: Int, par3: Int) {
        this.mc.renderEngine.bindTexture(refineryTexture)
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)

        val edgeLeft: Int = (this.width - this.xSize) / 2
        val edgeTop: Int = (this.height - this.ySize) / 2
        this.drawTexturedModalRect(edgeLeft, edgeTop, 0, 0, this.xSize, this.ySize)

        val waterLevel: Int = this.tileEntity.getScaledGasLevel(38)
        //Water
        this.mc.renderEngine.bindTexture(gasTextures)
        this.drawTexturedModalRect(
            edgeLeft + 7,
            edgeTop + 17 + 49 - waterLevel,
            1 + 4 * 17,
            38 - waterLevel,
            16,
            waterLevel
        )
        //Oxygen
        var displayInt: Int = this.tileEntity.getScaledFuelLevel(38)
        this.drawTexturedModalRect(
            edgeLeft + 132,
            edgeTop + 17 + 49 - displayInt,
            34 + 1,
            38 - displayInt,
            16,
            displayInt
        )
        //Hydrogen
        displayInt = this.tileEntity.getScaledFuelLevel2(38)
        this.drawTexturedModalRect(
            edgeLeft + 153,
            edgeTop + 17 + 49 - displayInt,
            17 + 1,
            38 - displayInt,
            16,
            displayInt
        )

        this.addToolTips()

        this.mc.renderEngine.bindTexture(refineryTexture)

        if (this.tileEntity.getEnergyStoredGC() > 0) {
            this.drawTexturedModalRect(edgeLeft + 28, edgeTop + 16, 208, 0, 11, 10)
        }

        this.drawTexturedModalRect(
            edgeLeft + 42,
            edgeTop + 17,
            176,
            38,
            min(this.tileEntity.getScaledElecticalLevel(54), 54),
            7
        )
    }

    private fun addToolTips() {
        val gasTankDesc: MutableList<String?> = ArrayList<String?>()
        gasTankDesc.add(GCCoreUtil.translate("gui.terraformer.desc.0"))
        this.gasTankRegion.tooltipStrings = gasTankDesc

        var fuelTankDesc: MutableList<String?> = ArrayList<String?>()
        fuelTankDesc.add(GCCoreUtil.translate("gui.gas_tank.desc.0"))
        var gasTankContents: FluidStack? =
            if (this.tileEntity.liquidTank != null) this.tileEntity.liquidTank.getFluid() else null
        if (gasTankContents != null) {
            val gasname: String? = GCCoreUtil.translate(gasTankContents.getFluid().getUnlocalizedName())
            fuelTankDesc.add("(" + gasname + ")")
        } else {
            fuelTankDesc.add(" ")
        }
        var fuelLevel: Int = if (gasTankContents != null) gasTankContents.amount else 0
        var fuelCapacity: Int = if (this.tileEntity.liquidTank != null) this.tileEntity.liquidTank.getCapacity() else 0
        fuelTankDesc.add(EnumColor.YELLOW.toString() + " " + fuelLevel + " / " + fuelCapacity)
        this.fuelTankRegion.tooltipStrings = fuelTankDesc

        fuelTankDesc = ArrayList<String?>()
        fuelTankDesc.add(GCCoreUtil.translate("gui.gas_tank.desc.0"))
        gasTankContents = if (this.tileEntity.liquidTank2 != null) this.tileEntity.liquidTank2.getFluid() else null
        if (gasTankContents != null) {
            val gasname: String? = GCCoreUtil.translate(gasTankContents.getFluid().getUnlocalizedName())
            fuelTankDesc.add("(" + gasname + ")")
        } else {
            fuelTankDesc.add(" ")
        }
        fuelLevel = if (gasTankContents != null) gasTankContents.amount else 0
        fuelCapacity = if (this.tileEntity.liquidTank2 != null) this.tileEntity.liquidTank2.getCapacity() else 0
        fuelTankDesc.add(EnumColor.YELLOW.toString() + " " + fuelLevel + " / " + fuelCapacity)
        this.fuelTank2Region.tooltipStrings = fuelTankDesc

        val electricityDesc: MutableList<String?> = ArrayList<String?>()
        electricityDesc.add(GCCoreUtil.translate("gui.energy_storage.desc.0"))
        //		electricityDesc.add(EnumColor.YELLOW + GCCoreUtil.translate("gui.energy_storage.desc.1") + ((int) Math.floor(this.tileEntity.getEnergyStoredGC()) + " / " + (int) Math.floor(this.tileEntity.getMaxEnergyStoredGC())));
        EnergyDisplayHelper.getEnergyDisplayTooltip(
            this.tileEntity.getEnergyStoredGC(),
            this.tileEntity.getMaxEnergyStoredGC(),
            electricityDesc
        )
        this.electricInfoRegion.tooltipStrings = electricityDesc
    }

    companion object {
        private val refineryTexture =
            ResourceLocation(GalacticraftPlanets.ASSET_PREFIX, "textures/gui/gas_liquefier.png")

        private val gasTextures =
            ResourceLocation(GalacticraftPlanets.ASSET_PREFIX, "textures/gui/gases_methane_oxygen_nitrogen.png")
    }
}
