package micdoodle8.mods.galacticraft.planets.mars.client.gui

import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementCheckbox.ICheckBoxCallback
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket
import java.util.ArrayList

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.client.gui.container.GuiContainerGC
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementCheckbox
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementInfoRegion
import micdoodle8.mods.galacticraft.core.energy.EnergyDisplayHelper
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.RedstoneUtil
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import micdoodle8.mods.galacticraft.planets.mars.inventory.ContainerTerraformer
import micdoodle8.mods.galacticraft.planets.mars.tile.TileEntityTerraformer
import net.minecraft.client.gui.GuiButton
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.entity.player.Inventory as InventoryPlayer
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraftforge.fml.client.FMLClientHandler
import org.lwjgl.opengl.GL11
import kotlin.math.min

open class GuiTerraformer constructor(par1InventoryPlayer: InventoryPlayer?, terraformer: TileEntityTerraformer) :
    GuiContainerGC(
        ContainerTerraformer(
            (par1InventoryPlayer)!!,
            terraformer,
            FMLClientHandler.instance().getClient().player
        )
    ), GuiElementCheckbox.ICheckBoxCallback {
    private val terraformer: TileEntityTerraformer
    private var enableTreesButton: GuiButton? = null
    private var enableGrassButton: GuiButton? = null
    private var checkboxRenderBubble: GuiElementCheckbox? = null
    private val electricInfoRegion = GuiElementInfoRegion(0, 0, 52, 9, null, 0, 0, this)
    private val waterTankInfoRegion = GuiElementInfoRegion(0, 0, 41, 28, null, 0, 0, this)

    init {
        this.ySize = 237
        this.terraformer = terraformer
    }

    override fun drawScreen(par1: Int, par2: Int, par3: Float) {
        if (this.terraformer.disableCooldown > 0) {
            this.enableTreesButton!!.enabled = false
            this.enableGrassButton!!.enabled = false
        } else {
            this.enableTreesButton!!.enabled = true
            this.enableGrassButton!!.enabled = true
        }

        this.enableTreesButton!!.displayString =
            GCCoreUtil.translate(if (this.terraformer.treesDisabled) "gui.button.enable.name" else "gui.button.disable.name") + " " + GCCoreUtil.translate(
                "gui.message.trees.name"
            )
        this.enableGrassButton!!.displayString =
            GCCoreUtil.translate(if (this.terraformer.grassDisabled) "gui.button.enable.name" else "gui.button.disable.name") + " " + GCCoreUtil.translate(
                "gui.message.grass.name"
            )

        super.drawScreen(par1, par2, par3)
    }

    override fun initGui() {
        super.initGui()
        this.electricInfoRegion.tooltipStrings = (ArrayList<String?>())!!
        this.electricInfoRegion.xPosition = (this.width - this.xSize) / 2 + 44
        this.electricInfoRegion.yPosition = (this.height - this.ySize) / 2 + 47
        this.electricInfoRegion.parentWidth = this.width
        this.electricInfoRegion.parentHeight = this.height
        this.infoRegions.add(this.electricInfoRegion)
        var batterySlotDesc: MutableList<String?> = ArrayList<String?>()
        batterySlotDesc.add(GCCoreUtil.translate("gui.battery_slot.desc.0"))
        batterySlotDesc.add(GCCoreUtil.translate("gui.battery_slot.desc.1"))
        this.infoRegions.add(
            GuiElementInfoRegion(
                (this.width - this.xSize) / 2 + 24,
                (this.height - this.ySize) / 2 + 38,
                18,
                18,
                batterySlotDesc,
                this.width,
                this.height,
                this
            )
        )
        batterySlotDesc = ArrayList<String?>()
        batterySlotDesc.add(GCCoreUtil.translate("gui.show_bubble.desc.0"))
        this.infoRegions.add(
            GuiElementInfoRegion(
                (this.width - this.xSize) / 2 + 85,
                (this.height - this.ySize) / 2 + 132,
                85,
                13,
                batterySlotDesc,
                this.width,
                this.height,
                this
            )
        )
        this.waterTankInfoRegion.tooltipStrings = (ArrayList<String?>())!!
        this.waterTankInfoRegion.xPosition = (this.width - this.xSize) / 2 + 55
        this.waterTankInfoRegion.yPosition = (this.height - this.ySize) / 2 + 17
        this.waterTankInfoRegion.parentWidth = this.width
        this.waterTankInfoRegion.parentHeight = this.height
        this.infoRegions.add(this.waterTankInfoRegion)
        this.buttonList.clear()
        val var5: Int = (this.width - this.xSize) / 2
        val var6: Int = (this.height - this.ySize) / 2
        this.enableTreesButton =
            GuiButton(0, var5 + 98, var6 + 85, 72, 20, GCCoreUtil.translate("gui.message.enable_trees.name"))
        this.enableGrassButton =
            GuiButton(1, var5 + 98, var6 + 109, 72, 20, GCCoreUtil.translate("gui.message.enable_grass.name"))
        this.buttonList.add(this.enableTreesButton)
        this.buttonList.add(this.enableGrassButton)
        this.checkboxRenderBubble =
            GuiElementCheckbox(2, this, var5 + 85, var6 + 132, GCCoreUtil.translate("gui.message.bubble_visible.name"))
        this.buttonList.add(this.checkboxRenderBubble)
    }

    override fun actionPerformed(par1GuiButton: GuiButton) {
        if (par1GuiButton.enabled) {
            when (par1GuiButton.id) {
                0 -> (GalacticraftCore.packetPipeline)!!.sendToServer(
                    PacketSimple(
                        PacketSimple.EnumSimplePacket.S_UPDATE_DISABLEABLE_BUTTON,
                        GCCoreUtil.getDimensionID(mc.world),
                        arrayOf<Any>(this.terraformer.getPos(), 0)
                    )
                )

                1 -> (GalacticraftCore.packetPipeline)!!.sendToServer(
                    PacketSimple(
                        PacketSimple.EnumSimplePacket.S_UPDATE_DISABLEABLE_BUTTON,
                        GCCoreUtil.getDimensionID(mc.world),
                        arrayOf<Any>(this.terraformer.getPos(), 1)
                    )
                )

                2 -> {}
                else -> {}
            }
        }
    }

    override fun drawGuiContainerForegroundLayer(par1: Int, par2: Int) {
        val displayString: String = terraformer.getName()
        this.fontRenderer.drawString(
            displayString,
            this.xSize / 2 - this.fontRenderer.getStringWidth(displayString) / 2,
            5,
            4210752
        )
        this.fontRenderer.drawString(GCCoreUtil.translate("container.inventory"), 8, 144, 4210752)
        this.fontRenderer.drawSplitString(this.getStatus(), 105, 24, this.xSize - 105, 4210752)
        //		this.fontRenderer.drawString(ElectricityDisplay.getDisplay(this.terraformer.ueWattsPerTick * 20, ElectricUnit.WATT), 105, 56, 4210752);
        //		this.fontRenderer.drawString(ElectricityDisplay.getDisplay(this.terraformer.getVoltage(), ElectricUnit.VOLTAGE), 105, 68, 4210752);
    }

    private fun getStatus(): String {
        if (RedstoneUtil.isBlockReceivingRedstone(this.terraformer.getWorld(), this.terraformer.getPos())) {
            return EnumColor.RED.toString() + GCCoreUtil.translate("gui.status.off.name")
        }

        if (this.terraformer.getEnergyStoredGC() <= 0.0f) {
            return EnumColor.RED.toString() + GCCoreUtil.translate("gui.message.no_energy.name")
        }

        if (this.terraformer.grassDisabled && this.terraformer.treesDisabled) {
            return EnumColor.ORANGE.toString() + GCCoreUtil.translate("gui.status.disabled.name")
        }

        if (this.terraformer.waterTank.getFluid() == null || this.terraformer.waterTank.getFluid()!!.amount <= 0) {
            return EnumColor.RED.toString() + GCCoreUtil.translate("gui.message.no_water.name")
        }

        if (this.terraformer.getFirstBonemealStack().isEmpty()) {
            return EnumColor.RED.toString() + GCCoreUtil.translate("gui.message.no_bonemeal.name")
        }

        if (!this.terraformer.grassDisabled && this.terraformer.getFirstSeedStack().isEmpty()) {
            return EnumColor.RED.toString() + GCCoreUtil.translate("gui.message.no_seeds.name")
        }

        if (!this.terraformer.treesDisabled && this.terraformer.getFirstSaplingStack().isEmpty()) {
            return EnumColor.RED.toString() + GCCoreUtil.translate("gui.message.no_saplings.name")
        }

        if (this.terraformer.getBubbleSize() < this.terraformer.MAX_SIZE - 0.5) {
            return EnumColor.YELLOW.toString() + GCCoreUtil.translate("gui.message.bubble_exp.name")
        }

        if (!this.terraformer.treesDisabled && this.terraformer.grassBlocksListSize <= 0) {
            return EnumColor.RED.toString() + GCCoreUtil.translate("gui.message.trees.name") + ": " + GCCoreUtil.translate(
                "gui.message.no_valid_blocks.name"
            )
        } else if (!this.terraformer.grassDisabled && this.terraformer.terraformableBlocksListSize <= 0) {
            return EnumColor.RED.toString() + GCCoreUtil.translate("gui.message.grass.name") + ": " + GCCoreUtil.translate(
                "gui.message.no_valid_blocks.name"
            )
        }

        return EnumColor.BRIGHT_GREEN.toString() + GCCoreUtil.translate("gui.message.terraforming.name")
    }

    override fun drawGuiContainerBackgroundLayer(par1: Float, par2: Int, par3: Int) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        this.mc.renderEngine.bindTexture(terraformerGui)
        val var5: Int = (this.width - this.xSize) / 2
        val var6: Int = (this.height - this.ySize) / 2
        this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize)

        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        val scale: Int = this.terraformer.getScaledElecticalLevel(54)
        this.drawTexturedModalRect(var5 + 45, var6 + 48, 176, 26, min(scale, 54), 7)

        val electricityDesc: MutableList<String?> = ArrayList<String?>()
        electricityDesc.add(GCCoreUtil.translate("gui.energy_storage.desc.0"))
        EnergyDisplayHelper.getEnergyDisplayTooltip(
            this.terraformer.getEnergyStoredGC(),
            this.terraformer.getMaxEnergyStoredGC(),
            electricityDesc
        )
        this.electricInfoRegion.tooltipStrings = electricityDesc

        var waterLevel: Int = this.terraformer.getScaledWaterLevel(100)
        val processDesc: MutableList<String?> = ArrayList<String?>()
        processDesc.clear()
        processDesc.add(GCCoreUtil.translate("gui.terraformer.desc.0") + ": " + waterLevel + "%")
        this.waterTankInfoRegion.tooltipStrings = processDesc

        waterLevel = this.terraformer.getScaledWaterLevel(26)
        this.drawTexturedModalRect(
            (this.width - this.xSize) / 2 + 56,
            (this.height - this.ySize) / 2 + 17 + 27 - waterLevel,
            176,
            26 - waterLevel,
            39,
            waterLevel
        )

        this.checkboxRenderBubble!!.isSelected = this.terraformer.shouldRenderBubble
    }

    override fun onSelectionChanged(checkbox: GuiElementCheckbox?, newSelected: Boolean) {
        this.terraformer.shouldRenderBubble = newSelected
        (GalacticraftCore.packetPipeline)!!.sendToServer(
            PacketSimple(
                PacketSimple.EnumSimplePacket.S_ON_ADVANCED_GUI_CLICKED_INT,
                GCCoreUtil.getDimensionID(mc.world),
                arrayOf<Any>(6, this.terraformer.getPos(), if (newSelected) 1 else 0)
            )
        )
    }

    override fun canPlayerEdit(checkbox: GuiElementCheckbox?, player: EntityPlayer?): Boolean {
        return true
    }

    override fun getInitiallySelected(checkbox: GuiElementCheckbox?): Boolean {
        return this.terraformer.shouldRenderBubble
    }

    override fun onIntruderInteraction() {
    }

    companion object {
        private val terraformerGui = ResourceLocation.fromNamespaceAndPath(GalacticraftPlanets.ASSET_PREFIX, "textures/gui/terraformer.png")
    }
}
