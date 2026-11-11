package micdoodle8.mods.galacticraft.core.client.gui.container

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.energy.EnergyDisplayHelper
import micdoodle8.mods.galacticraft.core.inventory.ContainerCoalGenerator
import micdoodle8.mods.galacticraft.core.tile.TileEntityCoalGenerator
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.world.entity.player.Inventory as InventoryPlayer
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import org.lwjgl.opengl.GL11

@OnlyIn(Dist.CLIENT)
open class GuiCoalGenerator constructor(
    playerInventory: InventoryPlayer?,
    private val tileEntity: TileEntityCoalGenerator
) : GuiContainerGC(ContainerCoalGenerator((playerInventory)!!, tileEntity)) {
    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
        this.fontRenderer.drawString(this.tileEntity.getName(), 55, 6, 4210752)
        var displayText: String = GCCoreUtil.translate("gui.status.generating.name")

        if (this.tileEntity.heatGJperTick <= 0 || this.tileEntity.heatGJperTick < TileEntityCoalGenerator.MIN_GENERATE_GJ_PER_TICK) {
            displayText = GCCoreUtil.translate("gui.status.not_generating.name")
        }

        this.fontRenderer.drawString(displayText, 122 - this.fontRenderer.getStringWidth(displayText) / 2, 33, 4210752)

        if (this.tileEntity.heatGJperTick < TileEntityCoalGenerator.MIN_GENERATE_GJ_PER_TICK) {
            displayText =
                GCCoreUtil.translate("gui.status.hull_heat.name") + ": " + (this.tileEntity.heatGJperTick / TileEntityCoalGenerator.MIN_GENERATE_GJ_PER_TICK * 100).toInt() + "%"
        } else {
            displayText =
                EnergyDisplayHelper.getEnergyDisplayS(this.tileEntity.heatGJperTick - TileEntityCoalGenerator.MIN_GENERATE_GJ_PER_TICK) + "/t"
        }

        this.fontRenderer.drawString(displayText, 122 - this.fontRenderer.getStringWidth(displayText) / 2, 45, 4210752)
        this.fontRenderer.drawString(GCCoreUtil.translate("container.inventory"), 8, this.ySize - 96 + 2, 4210752)
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        this.mc.renderEngine.bindTexture(coalGeneratorTexture)
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)

        val containerWidth: Int = (this.width - this.xSize) / 2
        val containerHeight: Int = (this.height - this.ySize) / 2
        this.drawTexturedModalRect(containerWidth, containerHeight, 0, 0, this.xSize, this.ySize)
    }

    companion object {
        private val coalGeneratorTexture = ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/gui/coal_generator.png")
    }
}
