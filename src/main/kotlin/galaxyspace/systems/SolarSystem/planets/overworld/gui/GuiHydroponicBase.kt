package galaxyspace.systems.SolarSystem.planets.overworld.gui

import java.util.ArrayList

import galaxyspace.GalaxySpace
import galaxyspace.core.client.gui.tile.GuiTileBase
import galaxyspace.core.util.GSUtils
import galaxyspace.systems.SolarSystem.planets.overworld.inventory.ContainerHydroponicBase
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityHydroponicBase
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementInfoRegion
import net.minecraft.world.entity.player.Inventory as InventoryPlayer
import net.minecraft.world.inventory.Slot
import org.lwjgl.opengl.GL11

open class GuiHydroponicBase constructor(
    par1InventoryPlayer: InventoryPlayer?,
    private val tileEntity: TileEntityHydroponicBase
) : GuiTileBase(ContainerHydroponicBase((par1InventoryPlayer)!!, tileEntity), 2, 1) {
    private val electricInfoRegion = GuiElementInfoRegion(0, 0, 56, 9, null, 0, 0, this)
    private val fluidTankRegion = GuiElementInfoRegion(0, 0, 16, 38, null, 0, 0, this)

    init {
        this.xSize = 173
        this.ySize = 225
    }

    public override fun initGui() {
        super.initGui()

        this.electricInfoRegion.tooltipStrings = ArrayList<String?>()
        this.electricInfoRegion.xPosition = (this.width - this.xSize) / 2 + 17
        this.electricInfoRegion.yPosition = (this.height - this.ySize) / 2 + 102
        this.electricInfoRegion.parentWidth = this.width
        this.electricInfoRegion.parentHeight = this.height
        this.infoRegions.add(this.electricInfoRegion)

        this.fluidTankRegion.parentWidth = this.width
        this.fluidTankRegion.parentHeight = this.height
        this.infoRegions.add(this.fluidTankRegion)
    }

    protected override fun drawGuiContainerForegroundLayer(par1: Int, par2: Int) {
        super.drawGuiContainerForegroundLayer(par1, par2)
    }

    protected override fun drawGuiContainerBackgroundLayer(par1: Float, par2: Int, par3: Int) {
        super.drawGuiContainerBackgroundLayer(par1, par2, par3)
        //this.mc.renderEngine.bindTexture(this.fuelGeneratorTexture);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)

        val containerWidth: Int = (this.width - this.xSize) / 2
        val containerHeight: Int = (this.height - this.ySize) / 2

        /*
        FluidStack water = this.tileEntity.waterTank.getFluid();
        
        int scale;
*/
        //Arrows
        if (tileEntity.getModuleLevel() > 0) for (i: Int in 0..<tileEntity.getModuleLevel()) {
            if (i == 0) this.renderProgressArray(
                containerWidth + 65,
                containerHeight + 76 - (22 * i),
                25,
                tileEntity.processTicks_0,
                this.tileEntity.processTimeRequired
            )
            if (i == 1) this.renderProgressArray(
                containerWidth + 65,
                containerHeight + 76 - (22 * i),
                25,
                tileEntity.processTicks_1,
                this.tileEntity.processTimeRequired
            )
            if (i == 2) this.renderProgressArray(
                containerWidth + 65,
                containerHeight + 76 - (22 * i),
                25,
                tileEntity.processTicks_2,
                this.tileEntity.processTimeRequired
            )
        }
        /*
        List<String?> electricityDesc = new ArrayList<String?>();
        electricityDesc.add(GCCoreUtil.translate("gui.energy_storage.desc.0"));
        EnergyDisplayHelper.getEnergyDisplayTooltip(this.tileEntity.getEnergyStoredGC(), this.tileEntity.getMaxEnergyStoredGC(), electricityDesc);
        this.electricInfoRegion.tooltipStrings = electricityDesc;

        int ticks = 0;
        for(int i = 0 ; i < this.tileEntity.getModuleLevel(); i++) {
        	
        	if(i == 0) ticks = tileEntity.processTicks_0;
        	else if(i == 1) ticks = tileEntity.processTicks_1;
        	else if(i == 2) ticks = tileEntity.processTicks_2;
        	
	        if (ticks > 0)
	        {
	            scale = (int) ((double) ticks / (double) this.tileEntity.processTimeRequired * 100);
	        }
	        else
	        {
	            scale = 100;
	        }
	        
	        if (tileEntity.getModuleLevel() > 0 && ticks > 0)
	        {
	        	scale = (int) ((double) ticks / (double) this.tileEntity.processTimeRequired * 36);
	             
	        	this.drawTexturedModalRect(containerWidth + 65, containerHeight + 56 - (22 * i), 192, 124, 36 - scale, 17);   
	        }
        }
        */
        //Energy
        this.renderEnergyBar(
            containerWidth + 5,
            containerHeight + 122,
            this.tileEntity.getScaledElecticalLevel(55),
            this.tileEntity.getEnergyStoredGC(),
            this.tileEntity.getMaxEnergyStoredGC()
        )
        /*this.drawTexturedModalRect(containerWidth + 16, containerHeight + 102, 192, 47, 56, 9);
        this.drawTexturedModalRect(containerWidth + 4, containerHeight + 102, 192, 56, 11, 10);
        if (this.tileEntity.getEnergyStoredGC() > 0)
        {
            scale = this.tileEntity.getScaledElecticalLevel(55);
            this.drawTexturedModalRect(containerWidth + 116 - 99, containerHeight + 103, 192, 0, scale, 7);
            this.drawTexturedModalRect(containerWidth + 3, containerHeight + 102, 192, 7, 11, 10);           
        }
        
        //Tanks 
        */
        this.renderFluidTank(
            containerWidth + 6,
            containerHeight + 63,
            this.tileEntity.waterTank,
            this.tileEntity.getScaledFluidLevel(38),
            fluidTankRegion
        )


        /*
		this.drawTexturedModalRect(containerWidth + 5, containerHeight + 43, 192, 66, 20, 42);
		//this.drawTexturedModalRect(containerWidth + this.xSize - 28, containerHeight + 40, 192, 66, 20, 42);
		
		//this.drawTexturedModalRect(containerWidth + 100, containerHeight + 43, 192, 141, 36, 16);
		
		GL11.glPushMatrix();		
	        final int watergraphLevel = this.tileEntity.getScaledFluidLevel(38);
	        if(water != null)
	        {
	    		
	        	GL11.glPushMatrix();
	        	GSUtils.displayGauge(containerWidth + 7, containerHeight + 25, watergraphLevel, this.tileEntity.waterTank.getFluid(), 0);
	 	        //GSUtils.drawFluid(water, (this.width - this.xSize) / 2 + 7, this.height / 2 - 57, 16, 38, this.tileEntity.waterTank.getCapacity());
	 			GL11.glPopMatrix();
	        }          
	     
        GL11.glPopMatrix();
        		
        this.mc.renderEngine.bindTexture(this.fuelGeneratorTexture);
        this.drawTexturedModalRect(containerWidth + 5, containerHeight + 43, 192+20, 66, 20, 42);
        //this.drawTexturedModalRect(containerWidth + 145, containerHeight + 40, 192+20, 66, 20, 42);
        */
        if (GalaxySpace.debug) GSUtils.renderDebugGui(this, containerWidth, containerHeight)
    }

    override fun isModuleSupport(): Boolean {
        return false
    }

    override fun getName(): String {
        return tileEntity.getName()
    }

    override fun getBatterySlot(): Slot? {
        return inventorySlots.getSlotFromInventory(tileEntity, 0)
    }
}
