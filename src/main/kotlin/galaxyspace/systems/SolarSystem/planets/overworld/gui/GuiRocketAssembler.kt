package galaxyspace.systems.SolarSystem.planets.overworld.gui

import java.util.ArrayList

import galaxyspace.GalaxySpace
import galaxyspace.core.GSItems
import galaxyspace.core.prefab.inventory.SlotUpgrades
import galaxyspace.core.util.GSUtils
import galaxyspace.systems.SolarSystem.planets.overworld.inventory.ContainerRocketAssembler
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityRocketAssembler
import micdoodle8.mods.galacticraft.core.client.gui.container.GuiContainerGC
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementInfoRegion
import micdoodle8.mods.galacticraft.core.energy.EnergyDisplayHelper
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11

@SideOnly(Side.CLIENT)
open class GuiRocketAssembler constructor(
    par1InventoryPlayer: InventoryPlayer,
    private val tileEntity: TileEntityRocketAssembler
) : GuiContainerGC(ContainerRocketAssembler(par1InventoryPlayer, tileEntity)) {
    private val electricInfoRegion = GuiElementInfoRegion(0, 0, 56, 9, null, 0, 0, this)
    private val processInfoRegion = GuiElementInfoRegion(0, 0, 52, 9, null, 0, 0, this)

    init {
        this.xSize = 204
        this.ySize = 256
    }

    override fun initGui() {
        super.initGui()
        this.electricInfoRegion.tooltipStrings = ArrayList<String?>()
        this.electricInfoRegion.xPosition = (this.width - this.xSize) / 2 + 126
        this.electricInfoRegion.yPosition = (this.height - this.ySize) / 2 + 146
        this.electricInfoRegion.parentWidth = this.width
        this.electricInfoRegion.parentHeight = this.height
        this.infoRegions.add(this.electricInfoRegion)
        var desc: MutableList<String?> = ArrayList<String?>()
        desc.add(GCCoreUtil.translate("gui.battery_slot.desc.0"))
        desc.add(GCCoreUtil.translate("gui.battery_slot.desc.1"))
        this.infoRegions.add(
            GuiElementInfoRegion(
                (this.width - this.xSize) / 2 + this.inventorySlots.getSlot(0).xPos,
                (this.height - this.ySize) / 2 + +this.inventorySlots.getSlot(0).yPos,
                18,
                18,
                desc,
                this.width,
                this.height,
                this
            )
        )
        this.processInfoRegion.tooltipStrings = ArrayList<String?>()
        this.processInfoRegion.xPosition = (this.width - this.xSize) / 2 + 100
        this.processInfoRegion.yPosition = (this.height - this.ySize) / 2 + 90
        this.processInfoRegion.parentWidth = this.width
        this.processInfoRegion.parentHeight = this.height
        this.infoRegions.add(this.processInfoRegion)

        desc = ArrayList<String?>()
        desc.add(EnumColor.BRIGHT_GREEN.toString() + GCCoreUtil.translate("gui.available_modules.desc"))
        desc.add("")
        desc.add("- " + ItemStack(GSItems.UPGRADES, 1, 2).getDisplayName())
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
    }


    override fun drawGuiContainerForegroundLayer(par1: Int, par2: Int) {
        this.fontRenderer.drawString(EnumColor.WHITE.toString() + this.tileEntity.getName(), 19, 7, 4210752)
        val displayText: String

        if (this.tileEntity.processTicks > 0) {
            displayText = EnumColor.BRIGHT_GREEN.toString() + GCCoreUtil.translate("gui.status.running.name")
        } else {
            displayText = EnumColor.ORANGE.toString() + GCCoreUtil.translate("gui.status.idle.name")
        }

        val str: String =
            EnumColor.WHITE.toString() + GCCoreUtil.translate("gui.message.status.name") + ": " + displayText
        this.fontRenderer.drawString(str, 140 - this.fontRenderer.getStringWidth(str) / 2, 137, 4210752)
        this.fontRenderer.drawString(
            EnumColor.WHITE.toString() + GCCoreUtil.translate("container.inventory"),
            32,
            this.ySize - 91,
            4210752
        )
        //		str = "" + this.tileEntity.storage.getMaxExtract();
//		this.fontRendererObj.drawString(str, 120 - this.fontRendererObj.getStringWidth(str) / 2, 85, 4210752);
//		//		str = ElectricityDisplay.getDisplay(this.tileEntity.getVoltage(), ElectricUnit.VOLTAGE);
//		this.fontRendererObj.drawString(str, 120 - this.fontRendererObj.getStringWidth(str) / 2, 95, 4210752);
    }

    /**
     * Draw the background layer for the GuiContainer (everything behind the
     * items)
     */
    override fun drawGuiContainerBackgroundLayer(par1: Float, par2: Int, par3: Int) {
        this.mc.renderEngine.bindTexture(electricFurnaceTexture)
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)

        val containerWidth: Int = (this.width - this.xSize) / 2
        val containerHeight: Int = (this.height - this.ySize) / 2
        this.drawTexturedModalRect(containerWidth, containerHeight, 0, 0, this.xSize, this.ySize)
        var scale: Int

        val electricityDesc: MutableList<String?> = ArrayList<String?>()
        electricityDesc.add(GCCoreUtil.translate("gui.energy_storage.desc.0"))
        EnergyDisplayHelper.getEnergyDisplayTooltip(
            this.tileEntity.getEnergyStoredGC(),
            this.tileEntity.getMaxEnergyStoredGC(),
            electricityDesc
        )
        this.electricInfoRegion.tooltipStrings = electricityDesc

        if (this.tileEntity.processTicks > 0) {
            scale =
                (this.tileEntity.processTicks.toDouble() / this.tileEntity.processTimeRequired.toDouble() * 100).toInt()
        } else {
            scale = 0
        }

        val processDesc: MutableList<String?> = ArrayList<String?>()
        processDesc.clear()
        processDesc.add(GCCoreUtil.translate("gui.electric_compressor.desc.0") + ": " + scale + "%")
        this.processInfoRegion.tooltipStrings = processDesc

        this.drawTexturedModalRect(containerWidth + 100, containerHeight + 91, 204, 96, 52, 9)

        this.drawTexturedModalRect(containerWidth + 125, containerHeight + 147, 204, 96, 52, 9)
        this.drawTexturedModalRect(containerWidth + 110, containerHeight + 147, 217, 239, 11, 10)

        if (this.tileEntity.processTicks > 0) {
            scale =
                (this.tileEntity.processTicks.toDouble() / this.tileEntity.processTimeRequired.toDouble() * 50).toInt()
            GL11.glColor4f(0.0f, 1.0f, 0.0f, 1.0f)
            this.drawTexturedModalRect(containerWidth + 101, containerHeight + 92, 206, 230, scale, 7)
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        }

        if (this.tileEntity.getEnergyStoredGC() > 0) {
            scale = this.tileEntity.getScaledElecticalLevel(54)
            this.drawTexturedModalRect(containerWidth + 126, containerHeight + 148, 206, 230, scale, 7)
            this.drawTexturedModalRect(containerWidth + 110, containerHeight + 147, 206, 239, 11, 10)
        }

        for (i: Int in this.inventorySlots.inventorySlots.indices) {
            val x: Int = this.inventorySlots.getSlot(i).xPos
            val y: Int = this.inventorySlots.getSlot(i).yPos
            val slot: Slot = this.inventorySlots.inventorySlots.get(i) as Slot


            GL11.glPushMatrix()
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
            this.drawTexturedModalRect(
                containerWidth + x,
                containerHeight + y,
                228,
                if (slot.slotNumber == 0) 185 else 206,
                16,
                16
            )

            val color: FloatArray = FloatArray(3)

            if (this.inventorySlots.getSlot(i).inventory !is InventoryPlayer) {
                if (slot.slotNumber == 11 || slot.slotNumber == 12 || slot.slotNumber == 13) {
                    color[0] = 0.2f //R
                    color[1] = 1.0f //G
                    color[2] = 0.0f //B	        
                } else if (slot.slotNumber == 10) {
                    color[0] = 1.0f //R
                    color[1] = 0.0f //G
                    color[2] = 0.0f //B	    
                } else if (slot.slotNumber != 1) {
                    color[0] = 0.0f //R
                    color[1] = 0.9f //G
                    color[2] = 1.0f //B
                }

                GL11.glColor3f(color[0], color[1], color[2])
                if (slot.slotNumber == 1) {
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
                    this.drawTexturedModalRect(containerWidth + x - 9, containerHeight + y - 9, 215, 144, 34, 34)
                } else if (slot.slotNumber != 10) this.drawTexturedModalRect(
                    containerWidth + x - 2,
                    containerHeight + y - 3,
                    207,
                    182,
                    20,
                    21
                )
                else this.drawTexturedModalRect(containerWidth + x - 2, containerHeight + y - 3, 207, 203, 20, 21)

                if (this.inventorySlots.getSlot(i) is SlotUpgrades) {
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
                    this.drawTexturedModalRect(containerWidth + x - 2, containerHeight + y - 2, 225, 75, 20, 20)
                }
            } else {
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
                this.drawTexturedModalRect(containerWidth + x - 2, containerHeight + y - 3, 207, 182, 20, 21)
            }

            GL11.glPopMatrix()
        }

        if (GalaxySpace.debug) GSUtils.renderDebugGui(this, containerWidth, containerHeight)
    }

    companion object {
        private val electricFurnaceTexture =
            ResourceLocation(GalaxySpace.ASSET_PREFIX, "textures/gui/rocket_assembly.png")
    }
}
