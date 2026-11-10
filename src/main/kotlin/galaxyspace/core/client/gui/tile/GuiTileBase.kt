package galaxyspace.core.client.gui.tile

import java.util.ArrayList

import asmodeuscore.core.utils.Utils
import galaxyspace.GalaxySpace
import galaxyspace.core.configs.GSConfigCore
import galaxyspace.core.prefab.inventory.SlotUpgrades
import galaxyspace.core.util.GSConstants
import galaxyspace.core.util.GSUtils
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.client.gui.container.GuiContainerGC
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementInfoRegion
import micdoodle8.mods.galacticraft.core.energy.EnergyDisplayHelper
import micdoodle8.mods.galacticraft.core.inventory.SlotSpecific
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.init.Items
import net.minecraft.inventory.Container
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TextFormatting
import net.minecraftforge.fluids.FluidTank
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11

@SideOnly(Side.CLIENT)
abstract class GuiTileBase(container: Container?, header: Int, split: Int) : GuiContainerGC((container)!!) {
    protected enum class Style {
        CLASSIC,
        MODERN
    }

    private val electricInfoRegion = GuiElementInfoRegion(0, 0, 56, 9, null, 0, 0, this)
    private val processInfoRegion = GuiElementInfoRegion(0, 0, 52, 25, null, 0, 0, this)
    private val batteryInfoRegion = GuiElementInfoRegion(0, 0, 18, 18, null, 0, 0, this)
    private val moduleInfoRegion = GuiElementInfoRegion(0, 0, 18, 21 * 4, null, 0, 0, this)
    private val fluidTankRegion = GuiElementInfoRegion(0, 0, 16, 38, null, 0, 0, this)

    private val header_type: Int
    private val split_type: Int

    @JvmField
    protected var header: Int

    @JvmField
protected var moduleInfoX: Int

    @JvmField
protected var moduleInfoY: Int

    @JvmField
    protected var moduleList: Array<ItemStack>? = null

    init {
        this.xSize = if (isModuleSupport()) 176 + 20 else 176
        this.ySize = 166
        this.header_type = header
        this.split_type = split
        this.header = -1
        moduleInfoX = 0
        moduleInfoY = 0
    }

    override fun initGui() {
        super.initGui()

        electricInfoRegion.tooltipStrings = ArrayList<String?>()
        electricInfoRegion.parentWidth = this.width
        electricInfoRegion.parentHeight = this.height
        this.infoRegions.add(electricInfoRegion)

        this.processInfoRegion.tooltipStrings = ArrayList<String?>()
        this.processInfoRegion.parentWidth = this.width
        this.processInfoRegion.parentHeight = this.height
        this.infoRegions.add(this.processInfoRegion)

        this.fluidTankRegion.tooltipStrings = ArrayList<String?>()
        this.fluidTankRegion.parentWidth = this.width
        this.fluidTankRegion.parentHeight = this.height
        this.infoRegions.add(this.fluidTankRegion)

        var desc: MutableList<String?> = ArrayList<String?>()
        if (getBatterySlot() != null) {
            desc.add(GCCoreUtil.translate("gui.battery_slot.desc.0"))
            desc.add(GCCoreUtil.translate("gui.battery_slot.desc.1"))
            batteryInfoRegion.tooltipStrings = desc
            batteryInfoRegion.xPosition = (this.width - this.xSize) / 2 + getBatterySlot()!!.xPos
            batteryInfoRegion.yPosition = (this.height - this.ySize) / 2 + getBatterySlot()!!.yPos
            batteryInfoRegion.parentWidth = this.width
            batteryInfoRegion.parentHeight = this.height
            this.infoRegions.add(batteryInfoRegion)
        }

        if (isModuleSupport() && moduleList != null) {
            desc = ArrayList<String?>()
            desc.add(TextFormatting.GREEN.toString() + GCCoreUtil.translate("gui.available_modules.desc"))
            desc.add("")
            for (module: ItemStack in (moduleList)!!) desc.add("- " + module.getDisplayName())
            //desc.add("- " + new ItemStack(GSItems.UPGRADES, 1, 3).getDisplayName());
            moduleInfoRegion.tooltipStrings = desc
            moduleInfoRegion.xPosition = (this.width - this.xSize) / 2 + moduleInfoX
            moduleInfoRegion.yPosition = (this.height - this.ySize) / 2 + moduleInfoY
            moduleInfoRegion.parentWidth = this.width
            moduleInfoRegion.parentHeight = this.height
            this.infoRegions.add(moduleInfoRegion)
        }
    }

    override fun drawGuiContainerForegroundLayer(par1: Int, par2: Int) {
        //int containerWidth = (this.width - this.xSize) / 2;
        //int containerHeight = (this.height - this.ySize) / 2;

        if (getStyle() == Style.MODERN) {
            var xOffset: Int = 0
            var yOffset: Int = 0
            var xInvOffset: Int = 0
            var yInvOffset: Int = 0

            if (header == 0) {
                xOffset = -15
                yOffset = -3
            }
            if (header == 4) {
                xOffset = 30
                yOffset = -3
            }
            if (header_type == 3) {
                xInvOffset = -8
                yInvOffset = 3
            }
            if (header_type == 4) {
                xInvOffset = -5
                yInvOffset = 2
            }

            this.fontRenderer.drawString(
                TextFormatting.WHITE.toString() + this.getName(),
                176 / 2 - fontRenderer.getStringWidth(this.getName()) / 2 + xOffset,
                5 + yOffset,
                4210752
            )
            this.fontRenderer.drawString(
                TextFormatting.WHITE.toString() + GCCoreUtil.translate("container.inventory"),
                15 + xInvOffset,
                this.ySize - 24 * 3 - 13 + yInvOffset,
                4210752
            )
        } else {
            this.fontRenderer.drawString(
                TextFormatting.DARK_GRAY.toString() + this.getName(),
                176 / 2 - fontRenderer.getStringWidth(this.getName()) / 2,
                5,
                4210752
            )
            this.fontRenderer.drawString(
                TextFormatting.DARK_GRAY.toString() + GCCoreUtil.translate("container.inventory"),
                8,
                this.ySize - 24 * 3 - 8,
                4210752
            )
        }
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        this.mc.renderEngine.bindTexture(getTexture())
        GlStateManager.color(1f, 1f, 1f)

        val containerWidth: Int = (this.width - this.xSize) / 2
        val containerHeight: Int = (this.height - this.ySize) / 2

        val xSize: Int = 176
        //HEADER
        if (header == -1 || getStyle() == Style.CLASSIC) this.drawTexturedModalRect(
            containerWidth,
            containerHeight,
            0,
            0,
            xSize,
            12
        )
        else this.drawTexturedModalRect(containerWidth, containerHeight, 0, 149 + (13 * header), xSize, 12)

        //BODY
        //(float x, float y, float width, float height, float u, float v, float uWidth, float vHeight, boolean invertX, boolean invertY, float texSizeX, float texSizeY)
        Utils.drawTexturedModalRect(
            containerWidth.toFloat(),
            (containerHeight + 12).toFloat(),
            xSize.toFloat(),
            (this.ySize - 27 * 4).toFloat(),
            0f,
            12f,
            xSize.toFloat(),
            12f,
            false,
            false,
            256f,
            256f
        )

        renderInventoryPanel(containerWidth, containerHeight, header_type, split_type)
        //FOOTER
        this.drawTexturedModalRect(containerWidth, containerHeight + this.ySize, 0, 60 - 12, xSize, 13)

        if (isModuleSupport()) {
            renderUpgradesPanel(containerWidth, containerHeight)
        }


        // Slots
        for (i: Int in this.inventorySlots.inventorySlots.indices) {
            val x: Int = this.inventorySlots.getSlot(i).xPos - 1
            val y: Int = this.inventorySlots.getSlot(i).yPos - 1


            /* if(!(this.inventorySlots.getSlot(i).inventory instanceof InventoryPlayer))
	        {*/
            GL11.glPushMatrix()

            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
            if (this.inventorySlots.getSlot(i) is SlotUpgrades) {
                this.drawTexturedModalRect(containerWidth + x, containerHeight + y, 84, 62, 18, 18)
            } else if (this.inventorySlots.getSlot(i) is SlotSpecific) {
                val slot: SlotSpecific = this.inventorySlots.getSlot(i) as SlotSpecific

                if (slot.isItemValid(ItemStack(GCItems.battery))) this.drawTexturedModalRect(
                    containerWidth + x,
                    containerHeight + y,
                    21,
                    62,
                    18,
                    18
                )
                else if (slot.isItemValid(ItemStack(Items.DYE, 1, 0)) && slot.getStack()
                        .getItemDamage() == 15
                ) this.drawTexturedModalRect(containerWidth + x, containerHeight + y, 105, 62, 18, 18)
                else this.drawTexturedModalRect(containerWidth + x, containerHeight + y, 0, 62, 18, 18)
            } else this.drawTexturedModalRect(containerWidth + x, containerHeight + y, 0, 62, 18, 18)

            GL11.glPopMatrix()
            //}
        }

        if (GalaxySpace.debug) GSUtils.renderDebugGui(this, containerWidth, containerHeight)
        this.mc.renderEngine.bindTexture(getTexture())
    }

    private fun renderInventoryPanel(x: Int, y: Int, header_type: Int, split_type: Int) {
        val xSize: Int = 176

        if (getStyle() == Style.CLASSIC) {
            Utils.drawTexturedModalRect(
                x.toFloat(),
                (y + this.ySize - 27 * 4).toFloat(),
                xSize.toFloat(),
                115f,
                0f,
                12f,
                xSize.toFloat(),
                12f,
                false,
                false,
                256f,
                256f
            )
        } else {
            if (header_type == -1) {
                Utils.drawTexturedModalRect(
                    x.toFloat(),
                    (y + this.ySize / 2 + 1).toFloat(),
                    xSize.toFloat(),
                    (this.ySize / 2).toFloat(),
                    0f,
                    12f,
                    xSize.toFloat(),
                    12f,
                    false,
                    false,
                    256f,
                    256f
                )
            } else {
                //SPLIT

                this.drawTexturedModalRect(x, y + this.ySize - 27 * 4 + 11, 0, 244 - (13 * split_type), xSize, 13)
                //HEADER
                this.drawTexturedModalRect(x, y + this.ySize - 27 * 4 + 23, 0, 149 + (13 * header_type), xSize, 12)

                Utils.drawTexturedModalRect(
                    x.toFloat(),
                    (y + this.ySize - 27 * 4 + 35).toFloat(),
                    xSize.toFloat(),
                    80f,
                    0f,
                    12f,
                    xSize.toFloat(),
                    12f,
                    false,
                    false,
                    256f,
                    256f
                )
            }
        }
    }

    private fun renderUpgradesPanel(x: Int, y: Int) {
        val xSize: Int = 176

        this.drawTexturedModalRect(x + xSize - 1, y + 12, 0, 120, 26, 6)
        Utils.drawTexturedModalRect(
            (x + xSize - 1).toFloat(),
            (y + 18).toFloat(),
            26f,
            (20 * 4).toFloat(),
            0f,
            126f,
            26f,
            12f,
            false,
            false,
            256f,
            256f
        )
        this.drawTexturedModalRect(x + xSize - 1, y + 24 * 4, 0, 147 - 6, 26, 7)
    }

    protected fun renderProgressArray(
        x: Int,
        y: Int,
        size: Int,
        ticks: Int,
        max: Int,
        invertX: Boolean = false,
        invertY: Boolean = false
    ) {
        Utils.drawTexturedModalRect(
            x.toFloat(),
            y.toFloat(),
            size.toFloat(),
            15f,
            181f,
            109f,
            8f,
            15f,
            invertX,
            invertY,
            256f,
            256f
        )
        if (invertX) Utils.drawTexturedModalRect(
            (x - 8).toFloat(),
            y.toFloat(),
            8f,
            15f,
            209f,
            109f,
            8f,
            15f,
            invertX,
            invertY,
            256f,
            256f
        )
        else Utils.drawTexturedModalRect(
            (x + size).toFloat(),
            y.toFloat(),
            8f,
            15f,
            209f,
            109f,
            8f,
            15f,
            invertX,
            invertY,
            256f,
            256f
        )

        var scale: Int = 0
        var percent: Int = 0
        val processDesc: MutableList<String?> = ArrayList<String?>()
        processDesc.clear()

        if (ticks > 0) {
            scale = (ticks.toDouble() / max.toDouble() * (size + 8)).toInt()

            Utils.drawTexturedModalRect(
                x.toFloat(),
                y.toFloat(),
                scale.toFloat(),
                15f,
                181f,
                125f,
                6f,
                15f,
                false,
                invertY,
                256f,
                256f
            )
            if (scale > size - 8) if (invertX) Utils.drawTexturedModalRect(
                (x - 8).toFloat(),
                y.toFloat(),
                8f,
                16f,
                209f,
                125f,
                (scale - size).toFloat(),
                16f,
                invertX,
                invertY,
                256f,
                256f
            )
            else this.drawTexturedModalRect(x + size, y, 209, 125, scale - size, 16)


            percent = (ticks.toDouble() / max.toDouble() * 100).toInt()


            processDesc.add(GCCoreUtil.translate("gui.electric_compressor.desc.0") + ": " + percent + "%")
            this.processInfoRegion.tooltipStrings = processDesc
            this.processInfoRegion.xPosition = x
            this.processInfoRegion.yPosition = y
        }
    }

    protected fun renderEnergyBar(x: Int, y: Int, scale: Int, energy: Float, maxEnergy: Float) {
        val electricityDesc: MutableList<String?> = ArrayList<String?>()
        electricityDesc.add(GCCoreUtil.translate("gui.energy_storage.desc.0"))
        EnergyDisplayHelper.getEnergyDisplayTooltip(energy, maxEnergy, electricityDesc)
        electricInfoRegion.tooltipStrings = electricityDesc
        electricInfoRegion.xPosition = x + 10
        electricInfoRegion.yPosition = y

        this.drawTexturedModalRect(x, y, 180, 56, 9, 8)
        this.drawTexturedModalRect(x + 11, y, 180, 47, 56, 9)
        if (energy > 0) {
            this.drawTexturedModalRect(x - 1, y, 180, 7, 11, 10)
            this.drawTexturedModalRect(x + 12, y + 1, 180, 0, scale, 7)
        }
    }

    protected fun renderFluidTank(x: Int, y: Int, tank: FluidTank, scale: Int, element: GuiElementInfoRegion) {
        this.drawTexturedModalRect(x, y, 180, 67, 20, 42)
        GSUtils.displayGauge(x + 1, y - 19, scale, tank.getFluid(), 0)
        this.mc.renderEngine.bindTexture(this.getTexture())
        drawTexturedModalRect(x, y, 200, 67, 16, 42)

        val processDesc: MutableList<String?> = ArrayList<String?>()
        processDesc.clear()

        val fuelLevel: Int = if (tank != null && tank.getFluid() != null) tank.getFluid()!!.amount else 0
        val fuelCapacity: Int = if (tank != null) tank.getCapacity() else 0
        if (tank.getFluid() != null) processDesc.add(
            EnumColor.YELLOW.toString() + tank.getFluid()!!.getLocalizedName() + ": " + fuelLevel + " / " + fuelCapacity
        )

        element.tooltipStrings = processDesc
        element.xPosition = x
        element.yPosition = y
    }

    protected abstract fun isModuleSupport(): Boolean
    protected abstract fun getName(): String?
    protected abstract fun getBatterySlot(): Slot?

    protected fun getStyle(): Style {
        return if (GSConfigCore.enableModernGUI) Style.MODERN else Style.CLASSIC
    }

    protected fun getTexture(): ResourceLocation {
        return if (GSConfigCore.enableModernGUI) GSConstants.GUI_MACHINE_MODERN else GSConstants.GUI_MACHINE_CLASSIC
    }
}
