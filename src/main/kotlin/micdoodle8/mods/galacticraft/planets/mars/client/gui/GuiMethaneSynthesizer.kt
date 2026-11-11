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
import micdoodle8.mods.galacticraft.planets.mars.inventory.ContainerMethaneSynthesizer
import micdoodle8.mods.galacticraft.planets.mars.tile.TileEntityMethaneSynthesizer
import net.minecraft.client.gui.GuiButton
import net.minecraft.world.entity.player.Inventory as InventoryPlayer
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import org.lwjgl.opengl.GL11
import kotlin.math.min

@OnlyIn(Dist.CLIENT)
open class GuiMethaneSynthesizer constructor(
    par1InventoryPlayer: InventoryPlayer?,
    private val tileEntity: TileEntityMethaneSynthesizer
) : GuiContainerGC(
    ContainerMethaneSynthesizer(
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
    private val gasTank2Region = GuiElementInfoRegion(
        (this.width - this.xSize) / 2 + 7,
        (this.height - this.ySize) / 2 + 28,
        16,
        20,
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

        val edgeLeft: Int = (this.width - this.xSize) / 2
        val edgeTop: Int = (this.height - this.ySize) / 2

        this.gasTankRegion.xPosition = edgeLeft + 7
        this.gasTankRegion.yPosition = edgeTop + 28
        this.gasTankRegion.parentWidth = this.width
        this.gasTankRegion.parentHeight = this.height
        this.infoRegions.add(this.gasTankRegion)

        this.gasTank2Region.xPosition = edgeLeft + 28
        this.gasTank2Region.yPosition = edgeTop + 28
        this.gasTank2Region.parentWidth = this.width
        this.gasTank2Region.parentHeight = this.height
        this.infoRegions.add(this.gasTank2Region)

        val batterySlotDesc: MutableList<String?> = ArrayList<String?>()
        batterySlotDesc.add(GCCoreUtil.translate("gui.battery_slot.desc.0"))
        batterySlotDesc.add(GCCoreUtil.translate("gui.battery_slot.desc.1"))
        this.infoRegions.add(
            GuiElementInfoRegion(
                edgeLeft + 53,
                edgeTop + 53,
                18,
                18,
                batterySlotDesc,
                this.width,
                this.height,
                this
            )
        )
        val carbonSlotDesc: MutableList<String?> = ArrayList<String?>()
        carbonSlotDesc.add(GCCoreUtil.translate("gui.carbon_slot.desc.0"))
        this.infoRegions.add(
            GuiElementInfoRegion(
                edgeLeft + 27,
                edgeTop + 53,
                18,
                18,
                carbonSlotDesc,
                this.width,
                this.height,
                this
            )
        )


        this.fuelTankRegion.xPosition = edgeLeft + 153
        this.fuelTankRegion.yPosition = edgeTop + 28
        this.fuelTankRegion.parentWidth = this.width
        this.fuelTankRegion.parentHeight = this.height
        this.infoRegions.add(this.fuelTankRegion)

        var fuelSlotDesc: MutableList<String?> = ArrayList<String?>()
        fuelSlotDesc.add(GCCoreUtil.translate("gui.fuel_output.desc.0"))
        fuelSlotDesc.add(GCCoreUtil.translate("gui.fuel_output.desc.1"))
        fuelSlotDesc.add(GCCoreUtil.translate("gui.methane_output.desc.2"))
        this.infoRegions.add(
            GuiElementInfoRegion(
                edgeLeft + 152,
                edgeTop + 6,
                18,
                18,
                fuelSlotDesc,
                this.width,
                this.height,
                this
            )
        )
        this.infoRegions.add(
            GuiElementInfoRegion(
                edgeLeft + 131,
                edgeTop + 6,
                18,
                18,
                fuelSlotDesc,
                this.width,
                this.height,
                this
            )
        )

        fuelSlotDesc = ArrayList<String?>()
        fuelSlotDesc.addAll(GCCoreUtil.translateWithSplit("gui.hydrogen_input.desc.0"))
        fuelSlotDesc.addAll(GCCoreUtil.translateWithSplit("gui.hydrogen_input.desc.1"))
        fuelSlotDesc.add("(" + GCCoreUtil.translate("gui.message.with_atmosphere0.name"))
        fuelSlotDesc.add(GCCoreUtil.lowerCaseNoun("fluid.hydrogen"))
        fuelSlotDesc.add(GCCoreUtil.translate("gui.message.with_atmosphere1.name") + ")")
        this.infoRegions.add(
            GuiElementInfoRegion(
                edgeLeft + 6,
                edgeTop + 6,
                18,
                18,
                fuelSlotDesc,
                this.width,
                this.height,
                this
            )
        )

        fuelSlotDesc = ArrayList<String?>()
        fuelSlotDesc.add(GCCoreUtil.translate("item.atmospheric_valve.name"))
        fuelSlotDesc.add("(" + GCCoreUtil.translate("gui.message.with_atmosphere0.name"))
        fuelSlotDesc.add(GCCoreUtil.lowerCaseNoun("gas.carbondioxide.name"))
        fuelSlotDesc.add(GCCoreUtil.translate("gui.message.with_atmosphere1.name") + ")")
        this.infoRegions.add(
            GuiElementInfoRegion(
                edgeLeft + 27,
                edgeTop + 6,
                18,
                18,
                fuelSlotDesc,
                this.width,
                this.height,
                this
            )
        )

        this.electricInfoRegion.xPosition = edgeLeft + 66
        this.electricInfoRegion.yPosition = edgeTop + 16
        this.electricInfoRegion.parentWidth = this.width
        this.electricInfoRegion.parentHeight = this.height
        this.infoRegions.add(this.electricInfoRegion)

        this.addToolTips()

        this.buttonList.add(
            GuiButton(
                0,
                this.width / 2 - 28,
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
        this.fontRenderer.drawString(this.tileEntity.getName(), 47, 5, 4210752)
        var displayText: String = ""
        val yOffset: Int = -18

        if (RedstoneUtil.isBlockReceivingRedstone(this.tileEntity.getWorld(), this.tileEntity.getPos())) {
            displayText = EnumColor.RED.toString() + GCCoreUtil.translate("gui.status.off.name")
        } else if (!this.tileEntity.hasEnoughEnergyToRun) {
            displayText = EnumColor.RED.toString() + GCCoreUtil.translate("gui.message.low_energy.name")
        } else if ((this.tileEntity.processTicks > -8 || this.tileEntity.canProcess())) {
            displayText = EnumColor.BRIGHT_GREEN.toString() + GCCoreUtil.translate("gui.status.processing.name")
        } else if (this.tileEntity.gasTank.getFluid() == null || this.tileEntity.gasTank.getFluidAmount() == 0) {
            displayText = EnumColor.RED.toString() + GCCoreUtil.translate("gui.status.nogas.name")
        } else if (this.tileEntity.gasTank.getFluidAmount() > 0 && this.tileEntity.disabled) {
            displayText = EnumColor.ORANGE.toString() + GCCoreUtil.translate("gui.status.ready.name")
        } else if (this.tileEntity.liquidTank.getFluidAmount() == this.tileEntity.liquidTank.getCapacity()) {
            displayText = EnumColor.RED.toString() + GCCoreUtil.translate("gui.status.tankfull.name")
        } else {
            displayText = EnumColor.RED.toString() + GCCoreUtil.translate("gui.status.needs_carbon.name")
        }

        this.buttonDisable!!.enabled = this.tileEntity.disableCooldown == 0
        this.buttonDisable!!.displayString =
            if (this.tileEntity.processTicks <= -8) GCCoreUtil.translate("gui.button.liquefy.name") else GCCoreUtil.translate(
                "gui.button.liquefy_stop.name"
            )
        this.fontRenderer.drawString(
            GCCoreUtil.translate("gui.message.status.name") + ":",
            72,
            45 + 23 + yOffset,
            4210752
        )
        this.fontRenderer.drawString(displayText, 75, 45 + 33 + yOffset, 4210752)
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

        this.mc.renderEngine.bindTexture(gasTextures)
        var displayInt: Int = this.tileEntity.getScaledGasLevel(38)
        this.drawTexturedModalRect(
            edgeLeft + 7,
            edgeTop + 17 + 49 - displayInt,
            1 + 17,
            38 - displayInt,
            16,
            displayInt
        )
        displayInt = this.tileEntity.getScaledGasLevel2(20)
        this.drawTexturedModalRect(
            edgeLeft + 28,
            edgeTop + 48 - displayInt,
            1 + 2 * 17,
            38 - displayInt,
            16,
            displayInt
        )
        displayInt = this.tileEntity.getScaledFuelLevel(38)
        this.drawTexturedModalRect(
            edgeLeft + 153,
            edgeTop + 17 + 49 - displayInt,
            1 + 2 * 17,
            38 - displayInt,
            16,
            displayInt
        )

        this.addToolTips()

        this.mc.renderEngine.bindTexture(refineryTexture)

        if (this.tileEntity.getEnergyStoredGC() > 0) {
            this.drawTexturedModalRect(edgeLeft + 52, edgeTop + 16, 208, 0, 11, 10)
        }

        this.drawTexturedModalRect(
            edgeLeft + 66,
            edgeTop + 17,
            176,
            38,
            min(this.tileEntity.getScaledElecticalLevel(54), 54),
            7
        )
    }

    private fun addToolTips() {
        var gasTankDesc: MutableList<String?> = ArrayList<String?>()
        gasTankDesc.add(GCCoreUtil.translate("gui.gas_tank.desc.0"))
        var gasTankContents: FluidStack? =
            if (this.tileEntity.gasTank != null) this.tileEntity.gasTank.getFluid() else null
        if (gasTankContents != null) {
            var gasname: String? = FluidRegistry.getFluid("hydrogen").getUnlocalizedName()
            if (gasname == null || gasname == "fluid.hydrogen") {
                gasname = GCCoreUtil.translate(gasTankContents.getFluid().getUnlocalizedName())
            }
            gasTankDesc.add("(" + gasname + ")")
        } else {
            gasTankDesc.add(" ")
        }
        var gasLevel: Int = if (gasTankContents != null) gasTankContents.amount else 0
        var gasCapacity: Int = if (this.tileEntity.gasTank != null) this.tileEntity.gasTank.getCapacity() else 0
        gasTankDesc.add(EnumColor.YELLOW.toString() + " " + gasLevel + " / " + gasCapacity)
        this.gasTankRegion.tooltipStrings = gasTankDesc

        gasTankDesc = ArrayList<String?>()
        gasTankDesc.add(GCCoreUtil.translate("gas.carbondioxide.name"))
        gasTankDesc.add(GCCoreUtil.translate("gui.gas_tank.desc.0"))
        gasTankContents = if (this.tileEntity.gasTank2 != null) this.tileEntity.gasTank2.getFluid() else null
        if (gasTankContents != null) {
            var gasname: String? = FluidRegistry.getFluid("carbondioxide").getUnlocalizedName()
            if (gasname == null || gasname == "fluid.carbondioxide") {
                gasname = GCCoreUtil.translate(gasTankContents.getFluid().getUnlocalizedName())
            }
            gasTankDesc.add("(" + gasname + ")")
        } else {
            gasTankDesc.add(" ")
        }
        gasLevel = if (gasTankContents != null) gasTankContents.amount else 0
        gasCapacity = if (this.tileEntity.gasTank2 != null) this.tileEntity.gasTank2.getCapacity() else 0
        gasTankDesc.add(EnumColor.YELLOW.toString() + " " + gasLevel + " / " + gasCapacity)
        this.gasTank2Region.tooltipStrings = gasTankDesc

        val fuelTankDesc: MutableList<String?> = ArrayList<String?>()
        fuelTankDesc.add(GCCoreUtil.translate("gui.gas_tank.desc.0"))
        gasTankContents = if (this.tileEntity.liquidTank != null) this.tileEntity.liquidTank.getFluid() else null
        if (gasTankContents != null) {
            var gasname: String? = FluidRegistry.getFluid("methane").getUnlocalizedName()
            if (gasname == null || gasname == "fluid.methane") {
                gasname = GCCoreUtil.translate(gasTankContents.getFluid().getUnlocalizedName())
            }
            fuelTankDesc.add("(" + gasname + ")")
        } else {
            fuelTankDesc.add(" ")
        }
        val fuelLevel: Int = if (gasTankContents != null) gasTankContents.amount else 0
        val fuelCapacity: Int = if (this.tileEntity.liquidTank != null) this.tileEntity.liquidTank.getCapacity() else 0
        fuelTankDesc.add(EnumColor.YELLOW.toString() + " " + fuelLevel + " / " + fuelCapacity)
        this.fuelTankRegion.tooltipStrings = fuelTankDesc

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
            ResourceLocation.fromNamespaceAndPath(GalacticraftPlanets.ASSET_PREFIX, "textures/gui/methane_synthesizer.png")

        private val gasTextures =
            ResourceLocation.fromNamespaceAndPath(GalacticraftPlanets.ASSET_PREFIX, "textures/gui/gases_methane_oxygen_nitrogen.png")
    }
}
