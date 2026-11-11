package micdoodle8.mods.galacticraft.core.client.gui.container

import net.minecraft.client.gui.inventory.GuiContainer

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.energy.EnergyDisplayHelper
import micdoodle8.mods.galacticraft.core.inventory.ContainerEnergyStorageModule
import micdoodle8.mods.galacticraft.core.tile.TileEntityEnergyStorageModule
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.world.entity.player.Inventory as InventoryPlayer
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import org.lwjgl.opengl.GL11

@OnlyIn(Dist.CLIENT)
open class GuiEnergyStorageModule constructor(
    par1InventoryPlayer: InventoryPlayer?,
    private val tileEntity: TileEntityEnergyStorageModule
) : GuiContainerGC(ContainerEnergyStorageModule((par1InventoryPlayer)!!, tileEntity)) {
    /**
     * Draw the foreground layer for the GuiContainer (everything in front of
     * the items)
     */
    override fun drawGuiContainerForegroundLayer(par1: Int, par2: Int) {
        this.fontRenderer.drawString(
            this.tileEntity.getName(),
            this.xSize / 2 - this.fontRenderer.getStringWidth(this.tileEntity.getName()) / 2,
            6,
            4210752
        )
        var energy: Float = this.tileEntity.getEnergyStoredGC()
        if (energy + 49 > this.tileEntity.getMaxEnergyStoredGC()) {
            energy = this.tileEntity.getMaxEnergyStoredGC()
        }
        var displayStr: String = EnergyDisplayHelper.getEnergyDisplayS(energy)
        this.fontRenderer.drawString(displayStr, 122 - this.fontRenderer.getStringWidth(displayStr) / 2, 25, 4210752)
        displayStr =
            GCCoreUtil.translate("gui.message.of.name") + " " + EnergyDisplayHelper.getEnergyDisplayS(this.tileEntity.getMaxEnergyStoredGC())
        this.fontRenderer.drawString(displayStr, 122 - this.fontRenderer.getStringWidth(displayStr) / 2, 34, 4210752)
        displayStr =
            GCCoreUtil.translate("gui.max_output.desc") + ": " + EnergyDisplayHelper.getEnergyDisplayS(this.tileEntity.storage.getMaxExtract()) + "/t"
        this.fontRenderer.drawString(displayStr, 114 - this.fontRenderer.getStringWidth(displayStr) / 2, 64, 4210752)
        this.fontRenderer.drawString(GCCoreUtil.translate("container.inventory"), 8, this.ySize - 96 + 2, 4210752)
    }

    /**
     * Draw the background layer for the GuiContainer (everything behind the
     * items)
     */
    override fun drawGuiContainerBackgroundLayer(par1: Float, par2: Int, par3: Int) {
        this.mc.renderEngine.bindTexture(batteryBoxTexture)
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)

        val containerWidth: Int = (this.width - this.xSize) / 2
        val containerHeight: Int = (this.height - this.ySize) / 2
        // Background energy bar
        this.drawTexturedModalRect(containerWidth, containerHeight, 0, 0, this.xSize, this.ySize)
        // Foreground energy bar
        val scale: Int =
            ((this.tileEntity.getEnergyStoredGC() + 49) / this.tileEntity.getMaxEnergyStoredGC() * 72).toInt()
        this.drawTexturedModalRect(containerWidth + 87, containerHeight + 52, 176, 0, scale, 3)
    }

    companion object {
        private val batteryBoxTexture =
            ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/gui/energy_storage_module.png")
    }
}
