package galaxyspace.systems.SolarSystem.planets.overworld.gui

import foundation.item.FoundationItemVariants
import java.util.ArrayList

import galaxyspace.GalaxySpace
import galaxyspace.core.GSItems
import galaxyspace.core.client.gui.tile.GuiTileBase
import galaxyspace.core.util.GSUtils
import galaxyspace.systems.SolarSystem.planets.overworld.inventory.ContainerAdvCircuitFabricator
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityAdvCircuitFabricator
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementInfoRegion
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.world.entity.player.Inventory as InventoryPlayer
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.item.Items
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.world.phys.Vec3 as Vec3d
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import org.lwjgl.opengl.GL11

@OnlyIn(Dist.CLIENT)
open class GuiAdvCircuitFabricator constructor(
    par1InventoryPlayer: InventoryPlayer,
    private val tileEntity: TileEntityAdvCircuitFabricator
) : GuiTileBase(ContainerAdvCircuitFabricator(par1InventoryPlayer, tileEntity), 2, 1) {
    private val processInfoRegion = GuiElementInfoRegion(0, 0, 53, 12, null, 0, 0, this)

    init {
        this.ySize = 195
        this.header = 3
        moduleInfoX = this.inventorySlots.getSlotFromInventory(tileEntity, 7)!!.xPos
        moduleInfoY = this.inventorySlots.getSlotFromInventory(tileEntity, 7)!!.yPos
        moduleList = arrayOf<ItemStack>(ItemStack(GSItems.UPGRADES, 1, 2), ItemStack(GSItems.UPGRADES, 1, 3))
    }

    public override fun initGui() {
        super.initGui()

        val desc: MutableList<String?> = ArrayList<String?>()
        desc.add(GCCoreUtil.translate("gui.battery_slot.desc.0"))
        desc.add(GCCoreUtil.translate("gui.battery_slot.desc.1"))
        this.infoRegions.add(
            GuiElementInfoRegion(
                (this.width - this.xSize) / 2 + 5,
                (this.height - this.ySize) / 2 + 68,
                18,
                18,
                desc,
                this.width,
                this.height,
                this
            )
        )
        this.processInfoRegion.tooltipStrings = ArrayList<String?>()
        this.processInfoRegion.xPosition = (this.width - this.xSize) / 2 + 87
        this.processInfoRegion.yPosition = (this.height - this.ySize) / 2 + 19
        this.processInfoRegion.parentWidth = this.width
        this.processInfoRegion.parentHeight = this.height
        this.infoRegions.add(this.processInfoRegion)
    }

    protected override fun drawGuiContainerForegroundLayer(par1: Int, par2: Int) {
        super.drawGuiContainerForegroundLayer(par1, par2)
        var displayText: String

        if (this.tileEntity.processTicks > 0) {
            displayText = EnumColor.BRIGHT_GREEN.toString() + GCCoreUtil.translate("gui.status.running.name")
        } else {
            displayText = EnumColor.ORANGE.toString() + GCCoreUtil.translate("gui.status.idle.name")
        }

        val str: String = GCCoreUtil.translate("gui.message.status.name") + ":"
        this.fontRenderer.drawString(
            EnumColor.WHITE.toString() + str,
            115 - this.fontRenderer.getStringWidth(str) / 2,
            90,
            4210752
        )
        displayText = this.tileEntity.getGUIstatus(displayText, null, true)
        this.fontRenderer.drawString(displayText, 115 - this.fontRenderer.getStringWidth(displayText) / 2, 100, 4210752)
    }

    protected override fun drawGuiContainerBackgroundLayer(par1: Float, par2: Int, par3: Int) {
        super.drawGuiContainerBackgroundLayer(par1, par2, par3)
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)

        val containerWidth: Int = (this.width - this.xSize) / 2
        val containerHeight: Int = (this.height - this.ySize) / 2

        this.drawTexturedModalRect(containerWidth + 85, containerHeight + 22, 184, 47, 58, 9)
        this.mc.renderEngine.bindTexture(circuitFabricatorTexture)
        this.drawTexturedModalRect(containerWidth + 30, containerHeight + 22, 0, 0, 131, 61)

        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        this.mc.renderEngine.bindTexture(this.getTexture())

        var scale: Int = 0
        if (this.tileEntity.processTicks > 0) {
            scale = (this.tileEntity.processTicks * 100) / this.tileEntity.getProcessTimeRequired()
        }

        val processDesc: MutableList<String?> = ArrayList<String?>()
        processDesc.clear()
        processDesc.add(GCCoreUtil.translate("gui.electric_compressor.desc.0") + ": " + scale + "%")
        this.processInfoRegion.tooltipStrings = processDesc

        if (this.tileEntity.processTicks > 0) {
            this.mc.renderEngine.bindTexture(circuitFabricatorTexture)
            scale = (this.tileEntity.processTicks * 52) / this.tileEntity.getProcessTimeRequired()
            this.drawTexturedModalRect(
                containerWidth + 86,
                containerHeight + 23,
                192,
                1 + this.tileEntity.processTicks % 9 / 3 * 10,
                scale,
                7
            )
        }

        this.mc.renderEngine.bindTexture(this.getTexture())

        //Energy
        this.renderEnergyBar(
            containerWidth + 5,
            containerHeight + 92,
            this.tileEntity.getScaledElecticalLevel(54),
            this.tileEntity.getEnergyStoredGC(),
            this.tileEntity.getMaxEnergyStoredGC()
        )

        GSUtils.renderItemIntoSlot(
            this.mc.getRenderItem(),
            ItemStack(Items.DIAMOND),
            containerWidth + this.inventorySlots.getSlot(1).xPos,
            containerHeight + this.inventorySlots.getSlot(1).yPos,
            Vec3d(0.4, 0.4, 0.4)
        )
        GSUtils.renderItemIntoSlot(
            this.mc.getRenderItem(),
            FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 2),
            containerWidth + this.inventorySlots.getSlot(2).xPos,
            containerHeight + this.inventorySlots.getSlot(2).yPos,
            Vec3d(0.4, 0.4, 0.4)
        )
        GSUtils.renderItemIntoSlot(
            this.mc.getRenderItem(),
            FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 2),
            containerWidth + this.inventorySlots.getSlot(3).xPos,
            containerHeight + this.inventorySlots.getSlot(3).yPos,
            Vec3d(0.4, 0.4, 0.4)
        )
        GSUtils.renderItemIntoSlot(
            this.mc.getRenderItem(),
            ItemStack(Items.REDSTONE),
            containerWidth + this.inventorySlots.getSlot(4).xPos,
            containerHeight + this.inventorySlots.getSlot(4).yPos,
            Vec3d(0.4, 0.4, 0.4)
        )


        GSUtils.renderItemIntoSlot(
            this.mc.getRenderItem(),
            ItemStack(Blocks.REDSTONE_TORCH),
            containerWidth + this.inventorySlots.getSlot(5).xPos,
            containerHeight + this.inventorySlots.getSlot(5).yPos,
            Vec3d(1.0, 1.0, 1.0)
        )
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

    companion object {
        private val circuitFabricatorTexture =
            ResourceLocation.fromNamespaceAndPath(GalaxySpace.ASSET_PREFIX, "textures/gui/circuit_fabricator.png")
    }
}
