package galaxyspace.systems.SolarSystem.planets.overworld.gui

import galaxyspace.GalaxySpace
import galaxyspace.core.GSItems
import galaxyspace.core.client.gui.tile.GuiTileBase
import galaxyspace.core.util.GSUtils
import galaxyspace.systems.SolarSystem.planets.overworld.inventory.ContainerAssembler
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityAssembler
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.world.entity.player.Inventory as InventoryPlayer
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.ChatFormatting as TextFormatting
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

@OnlyIn(Dist.CLIENT)
open class GuiAssembler constructor(
    par1InventoryPlayer: InventoryPlayer?, //private static final ResourceLocation electricFurnaceTexture = new ResourceLocation(GalaxySpace.ASSET_PREFIX, "textures/gui/assembly_machine.png");
    private val tileEntity: TileEntityAssembler
) : GuiTileBase(ContainerAssembler((par1InventoryPlayer)!!, tileEntity), 2, 1) {
    //private GuiElementInfoRegion electricInfoRegion = new GuiElementInfoRegion(0, 0, 56, 9, null, 0, 0, this);
    //private GuiElementInfoRegion processInfoRegion = new GuiElementInfoRegion(0, 0, 52, 25, null, 0, 0, this);
    init {
        this.ySize = 199
        moduleList = arrayOf<ItemStack>(ItemStack(GSItems.UPGRADES, 1, 2), ItemStack(GSItems.UPGRADES, 1, 3))
    }

    public override fun initGui() {
        super.initGui()
        /*this.electricInfoRegion.tooltipStrings = new ArrayList<String?>();
        this.electricInfoRegion.xPosition = (this.width - this.xSize) / 2 + 17;
        this.electricInfoRegion.yPosition = (this.height - this.ySize) / 2 + 95;
        this.electricInfoRegion.parentWidth = this.width;
        this.electricInfoRegion.parentHeight = this.height;
        this.infoRegions.add(this.electricInfoRegion);*/
        /*
        List<String?> desc = new ArrayList<String?>();
        desc.add(GCCoreUtil.translate("gui.battery_slot.desc.0"));
        desc.add(GCCoreUtil.translate("gui.battery_slot.desc.1"));
        this.infoRegions.add(new GuiElementInfoRegion((this.width - this.xSize) / 2 + 77, (this.height - this.ySize) / 2 + 93, 18, 18, desc, this.width, this.height, this));
      
        this.processInfoRegion.tooltipStrings = new ArrayList<String?>();
        this.processInfoRegion.xPosition = (this.width - this.xSize) / 2 + 77;
        this.processInfoRegion.yPosition = (this.height - this.ySize) / 2 + 30;
        this.processInfoRegion.parentWidth = this.width;
        this.processInfoRegion.parentHeight = this.height;
        this.infoRegions.add(this.processInfoRegion);
        
        desc = new ArrayList<String?>();
        desc.add(EnumColor.BRIGHT_GREEN + GCCoreUtil.translate("gui.available_modules.desc"));
        desc.add("");
        desc.add("- " + new ItemStack(GSItems.UPGRADES, 1, 2).getDisplayName());
        desc.add("- " + new ItemStack(GSItems.UPGRADES, 1, 3).getDisplayName());
        this.infoRegions.add(new GuiElementInfoRegion((this.width + this.xSize) / 2, (this.height - this.ySize) / 2 + 16, 18, 21 * 4, desc, this.width, this.height, this));
       */
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
        this.fontRenderer.drawString(str, 135 - this.fontRenderer.getStringWidth(str) / 2, 85, 4210752)
        //		str = "" + this.tileEntity.storage.getMaxExtract();
//		this.fontRendererObj.drawString(str, 120 - this.fontRendererObj.getStringWidth(str) / 2, 85, 4210752);
//		//		str = ElectricityDisplay.getDisplay(this.tileEntity.getVoltage(), ElectricUnit.VOLTAGE);
//		this.fontRendererObj.drawString(str, 120 - this.fontRendererObj.getStringWidth(str) / 2, 95, 4210752);
    }

    /**
     * Draw the background layer for the GuiContainer (everything behind the
     * items)
     */
    protected override fun drawGuiContainerBackgroundLayer(par1: Float, par2: Int, par3: Int) {
        super.drawGuiContainerBackgroundLayer(par1, par2, par3)
        this.mc.renderEngine.bindTexture(getTexture())
        /* GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        this.drawTexturedModalRect(containerWidth, containerHeight, 0, 0, this.xSize, this.ySize);*/
        val containerWidth: Int = (this.width - this.xSize) / 2
        val containerHeight: Int = (this.height - this.ySize) / 2


        //arrow
        //this.drawTexturedModalRect(containerWidth + 82, containerHeight + 38, 176, 73, 52, 16);
        var scale: Int
        /*
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
            scale = 0;
        }

        List<String?> processDesc = new ArrayList<String?>();
        processDesc.clear();
        processDesc.add(GCCoreUtil.translate("gui.electric_compressor.desc.0") + ": " + scale + "%");
        this.processInfoRegion.tooltipStrings = processDesc;

        if (this.tileEntity.processTicks > 0)
        {
            scale = (int) ((double) this.tileEntity.processTicks / (double) this.tileEntity.processTimeRequired * 54);
            this.drawTexturedModalRect(containerWidth + 82, containerHeight + 38, 176, 13, scale, 17);
        }
*/
        this.renderProgressArray(
            containerWidth + 86,
            containerHeight + 38,
            35,
            tileEntity.processTicks,
            tileEntity.processTimeRequired
        )
        this.renderEnergyBar(
            containerWidth + 5,
            containerHeight + 85,
            this.tileEntity.getScaledElecticalLevel(54),
            this.tileEntity.getEnergyStoredGC(),
            this.tileEntity.getMaxEnergyStoredGC()
        )


        /*if (this.tileEntity.getEnergyStoredGC() > 0)
        {
            scale = this.tileEntity.getScaledElecticalLevel(54);
            this.drawTexturedModalRect(containerWidth + 116 - 98, containerHeight + 96, 176, 30, scale, 7);
            this.drawTexturedModalRect(containerWidth + 4, containerHeight + 95, 176, 37, 11, 10);
        }*/

        //assembler elements
        this.drawTexturedModalRect(containerWidth + 94, containerHeight + 29, 200, 19, 17, 13)
        this.drawTexturedModalRect(containerWidth + 98, containerHeight + 50, 197, 40, 16, 6)
        if (this.tileEntity.processTicks > this.tileEntity.processTimeRequired / 2) {
            this.drawTexturedModalRect(containerWidth + 94, containerHeight + 29, 182, 19, 17, 13)
            this.drawTexturedModalRect(containerWidth + 98, containerHeight + 50, 180, 40, 16, 6)
        }


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
