package galaxyspace.systems.SolarSystem.planets.overworld.gui

import java.util.ArrayList
import java.util.List
import asmodeuscore.core.utils.Utils
import net.minecraftforge.fluids.FluidStack

import galaxyspace.GalaxySpace
import galaxyspace.core.GSItems
import galaxyspace.core.client.gui.tile.GuiTileBase
import galaxyspace.core.network.packet.GSPacketSimple
import galaxyspace.core.network.packet.GSPacketSimple.GSEnumSimplePacket
import galaxyspace.core.util.GSUtils
import galaxyspace.systems.SolarSystem.planets.overworld.inventory.ContainerLiquidSeparator
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityLiquidSeparator
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementInfoRegion
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.gui.GuiButton
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import net.minecraft.util.text.TextFormatting
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11

@SideOnly(Side.CLIENT)
open class GuiLiquidSeparator constructor(
    par1InventoryPlayer: InventoryPlayer?,
    private val tileEntity: TileEntityLiquidSeparator
) : GuiTileBase(ContainerLiquidSeparator((par1InventoryPlayer)!!, tileEntity), 3, 0) {
    private val baseTankRegion = GuiElementInfoRegion(
        (this.width - this.xSize) / 2 + 7,
        (this.height - this.ySize) / 2 + 28,
        16,
        38,
        ArrayList<String?>(),
        this.width,
        this.height,
        this
    )

    private val water1TankRegion = GuiElementInfoRegion(
        (this.width - this.xSize) / 2 + 7,
        (this.height - this.ySize) / 2 + 28,
        16,
        38,
        ArrayList<String?>(),
        this.width,
        this.height,
        this
    )
    private val water2TankRegion = GuiElementInfoRegion(
        (this.width - this.xSize) / 2 + 7,
        (this.height - this.ySize) / 2 + 28,
        16,
        38,
        ArrayList<String?>(),
        this.width,
        this.height,
        this
    )

    private var reverseButton: GuiButton? = null

    @JvmField
    val BTN_REVERSE: Int = 8

    init {
        this.ySize = 205
        moduleInfoX = this.inventorySlots.getSlotFromInventory(tileEntity, 4)!!.xPos
        moduleInfoY = this.inventorySlots.getSlotFromInventory(tileEntity, 4)!!.yPos
        moduleList = arrayOf<ItemStack>(ItemStack(GSItems.UPGRADES, 1, 2), ItemStack(GSItems.UPGRADES, 1, 3))
    }

    public override fun initGui() {
        super.initGui()


        /*
        
        List<String?> baseTankDesc = new ArrayList<String?>();
        int baseLevel = this.tileEntity.baseTank != null && this.tileEntity.baseTank.getFluid() != null ? this.tileEntity.baseTank.getFluid().amount : 0;
        int baseCapacity = this.tileEntity.baseTank != null ? this.tileEntity.baseTank.getCapacity() : 0;
        baseTankDesc.add(EnumColor.YELLOW + GCCoreUtil.translate("gui.message.fuel.name") + ": " + baseLevel + " / " + baseCapacity);
        this.baseTankRegion.tooltipStrings = baseTankDesc;
        this.baseTankRegion.xPosition = (this.width / 2 ) - 9;
        this.baseTankRegion.yPosition = (this.height - this.ySize) / 2 + 40;*/
        this.baseTankRegion.parentWidth = this.width
        this.baseTankRegion.parentHeight = this.height
        this.infoRegions.add(this.baseTankRegion)
        /*
        baseTankDesc = new ArrayList<String?>();
        int fuelLevel = this.tileEntity.waterTank1 != null && this.tileEntity.waterTank1.getFluid() != null ? this.tileEntity.waterTank1.getFluid().amount : 0;
        int fuelCapacity = this.tileEntity.waterTank1 != null ? this.tileEntity.waterTank1.getCapacity() : 0;
        baseTankDesc.add(EnumColor.YELLOW + GCCoreUtil.translate("gui.message.fuel.name") + ": " + fuelLevel + " / " + fuelCapacity);
        this.water1TankRegion.tooltipStrings = baseTankDesc;
        this.water1TankRegion.xPosition = (this.width / 2 + this.xSize / 2) - 30;
        this.water1TankRegion.yPosition = (this.height - this.ySize) / 2 + 40;*/
        this.water1TankRegion.parentWidth = this.width
        this.water1TankRegion.parentHeight = this.height
        this.infoRegions.add(this.water1TankRegion)
        /*
        baseTankDesc = new ArrayList<String?>();
        int fuel1Level = this.tileEntity.waterTank2 != null && this.tileEntity.waterTank2.getFluid() != null ? this.tileEntity.waterTank2.getFluid().amount : 0;
        int fuel1Capacity = this.tileEntity.waterTank2 != null ? this.tileEntity.waterTank2.getCapacity() : 0;
        baseTankDesc.add(EnumColor.YELLOW + GCCoreUtil.translate("gui.message.fuel.name") + ": " + fuel1Level + " / " + fuel1Capacity);
        this.water2TankRegion.tooltipStrings = baseTankDesc;
        this.water2TankRegion.xPosition = (this.width / 2) - 78;
        this.water2TankRegion.yPosition = (this.height - this.ySize) / 2 + 40;*/
        this.water2TankRegion.parentWidth = this.width
        this.water2TankRegion.parentHeight = this.height
        this.infoRegions.add(this.water2TankRegion)


        this.buttonList.clear()
        val var5: Int = (this.width - this.xSize) / 2
        val var6: Int = (this.height - this.ySize) / 2

        reverseButton =
            GuiButton(BTN_REVERSE, var5 + 110, var6 + 65, 20, 20, GCCoreUtil.translate("gui.button.disable.name"))

        this.buttonList.add(reverseButton)
    }

    protected override fun drawGuiContainerForegroundLayer(par1: Int, par2: Int) {
        super.drawGuiContainerForegroundLayer(par1, par2)
        val displayText: String

        reverseButton!!.displayString = if (this.tileEntity.getReverse()) "R-" else "R+"

        if (this.tileEntity.processTicks > 0) {
            displayText = EnumColor.BRIGHT_GREEN.toString() + GCCoreUtil.translate("gui.status.running.name")
        } else {
            displayText = EnumColor.ORANGE.toString() + GCCoreUtil.translate("gui.status.idle.name")
        }

        val color: TextFormatting = if (getStyle() == Style.MODERN) TextFormatting.WHITE else TextFormatting.DARK_GRAY
        val str: String = color.toString() + GCCoreUtil.translate("gui.message.status.name") + ": " + displayText
        this.fontRenderer.drawString(str, 128 - this.fontRenderer.getStringWidth(str) / 2, 95, 4210752)
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


        //Process Bar
        var scale: Int
        if (this.tileEntity.processTicks > 0) {
            scale =
                (this.tileEntity.processTicks.toDouble() / this.tileEntity.processTimeRequired.toDouble() * 100).toInt()
        } else {
            scale = 100
        }

        this.drawTexturedModalRect(
            containerWidth + (if (!this.tileEntity.getReverse()) 110 else 35),
            containerHeight + 44,
            192,
            109,
            26,
            15
        )
        this.drawTexturedModalRect(
            containerWidth + (if (!this.tileEntity.getReverse()) 35 else 110),
            containerHeight + 44,
            181,
            142,
            26,
            15
        )


        // Utils.drawTexturedModalRect(x, y, size, 15, 181, 109, 8, 15, invertX, invertY, 256, 256);
        if (this.tileEntity.processTicks > 0) {
            scale =
                (this.tileEntity.processTicks.toDouble() / this.tileEntity.processTimeRequired.toDouble() * 26).toInt()
            this.drawTexturedModalRect(
                containerWidth + (if (!this.tileEntity.getReverse()) 110 else 35),
                containerHeight + 44,
                192,
                109 + 16,
                26,
                16
            )
            this.drawTexturedModalRect(
                containerWidth + (if (!this.tileEntity.getReverse()) 35 else 110),
                containerHeight + 44,
                181,
                142 + 16,
                26,
                16
            )
        }


        //Energy
        this.renderEnergyBar(
            containerWidth + 6,
            containerHeight + 95,
            this.tileEntity.getScaledElecticalLevel(55),
            this.tileEntity.getEnergyStoredGC(),
            this.tileEntity.getMaxEnergyStoredGC()
        )


        //FluidTank
        this.renderFluidTank(
            containerWidth + this.xSize / 2 - 20,
            containerHeight + 38,
            this.tileEntity.baseTank,
            tileEntity.getScaledTankLevel(tileEntity.baseTank, 38),
            baseTankRegion
        )
        this.renderFluidTank(
            containerWidth + this.xSize - 33 - 19,
            containerHeight + 38,
            this.tileEntity.waterTank1,
            tileEntity.getScaledTankLevel(tileEntity.waterTank1, 38),
            water1TankRegion
        )
        this.renderFluidTank(
            containerWidth + 9,
            containerHeight + 38,
            this.tileEntity.waterTank2,
            tileEntity.getScaledTankLevel(tileEntity.waterTank2, 38),
            water2TankRegion
        )
        /*
        this.drawTexturedModalRect(containerWidth + this.xSize / 2 - 20, containerHeight + 38, 180, 67, 20, 42);	
        this.drawTexturedModalRect(containerWidth + this.xSize - 33 - 19, containerHeight + 38, 180, 67, 20, 42);	
        this.drawTexturedModalRect(containerWidth + 9, containerHeight + 38, 180, 67, 20, 42);	
        
        FluidStack fluid = this.tileEntity.baseTank.getFluid();        
        FluidStack fluid1 = this.tileEntity.waterTank1.getFluid();     
        FluidStack fluid2 = this.tileEntity.waterTank2.getFluid();   
        
	    GSUtils.displayGauge(containerWidth + this.xSize / 2 - 8, containerHeight + 20, tileEntity.getScaledTankLevel(tileEntity.baseTank, 38), tileEntity.baseTank.getFluid(), 0);
	    GSUtils.displayGauge(containerWidth + this.xSize - 31, containerHeight + 20, tileEntity.getScaledTankLevel(tileEntity.waterTank1, 38), tileEntity.waterTank1.getFluid(), 0);
		GSUtils.displayGauge(containerWidth + 10, containerHeight + 20, tileEntity.getScaledTankLevel(tileEntity.waterTank2, 38), tileEntity.waterTank2.getFluid(), 0);
			
        
        this.mc.renderEngine.bindTexture(this.getTexture());
        drawTexturedModalRect(containerWidth + this.xSize / 2 - 20, containerHeight + 38, 200, 67, 16, 42);
        drawTexturedModalRect(containerWidth + this.xSize - 33 - 19, containerHeight + 38, 200, 67, 16, 42);
        drawTexturedModalRect(containerWidth + 9, containerHeight + 38, 200, 67, 16, 42);
        
		
        
        //Info FLuidTank
		List<String?> baseTankDesc = new ArrayList<String?>();
        int baseLevel = this.tileEntity.baseTank != null && this.tileEntity.baseTank.getFluid() != null ? this.tileEntity.baseTank.getFluid().amount : 0;
        int baseCapacity = this.tileEntity.baseTank != null ? this.tileEntity.baseTank.getCapacity() : 0;
        if(fluid != null) baseTankDesc.add(EnumColor.YELLOW + this.tileEntity.baseTank.getFluid().getLocalizedName() + ": " + baseLevel + " / " + baseCapacity);       
        this.baseTankRegion.tooltipStrings = baseTankDesc;
        
        List<String?> fuelTankDesc = new ArrayList<String?>();
        int fuelLevel = this.tileEntity.waterTank1 != null && this.tileEntity.waterTank1.getFluid() != null ? this.tileEntity.waterTank1.getFluid().amount : 0;
        int fuelCapacity = this.tileEntity.waterTank1 != null ? this.tileEntity.waterTank1.getCapacity() : 0;
        if(fluid1 != null) fuelTankDesc.add(EnumColor.YELLOW + this.tileEntity.waterTank1.getFluid().getLocalizedName() + ": " + fuelLevel + " / " + fuelCapacity);       
        this.water1TankRegion.tooltipStrings = fuelTankDesc;
        
        List<String?> fuel1TankDesc = new ArrayList<String?>();
        int fuel1Level = this.tileEntity.waterTank2 != null && this.tileEntity.waterTank2.getFluid() != null ? this.tileEntity.waterTank2.getFluid().amount : 0;
        int fuel1Capacity = this.tileEntity.waterTank2 != null ? this.tileEntity.waterTank2.getCapacity() : 0;
        if(fluid2 != null) fuel1TankDesc.add(EnumColor.YELLOW + this.tileEntity.waterTank2.getFluid().getLocalizedName() + ": " + fuel1Level + " / " + fuel1Capacity);       
        this.water2TankRegion.tooltipStrings = fuel1TankDesc;
      */
        if (GalaxySpace.debug) GSUtils.renderDebugGui(this, containerWidth, containerHeight)
    }

    override fun actionPerformed(btn: GuiButton) {
        when (btn.id) {
            BTN_REVERSE -> (GalaxySpace.packetPipeline)!!.sendToServer(
                GSPacketSimple(
                    GSEnumSimplePacket.S_REVERSE_SEPATATOR,
                    GCCoreUtil.getDimensionID(this.mc.world),
                    *arrayOf<Any>(
                        BlockVec3(
                            this.tileEntity.getPos().getX(),
                            this.tileEntity.getPos().getY(),
                            this.tileEntity.getPos().getZ()
                        )
                    )
                )
            )
        }
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
