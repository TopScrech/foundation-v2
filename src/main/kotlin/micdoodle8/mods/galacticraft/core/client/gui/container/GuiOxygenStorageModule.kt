package micdoodle8.mods.galacticraft.core.client.gui.container

import java.util.ArrayList

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementInfoRegion
import micdoodle8.mods.galacticraft.core.inventory.ContainerOxygenStorageModule
import micdoodle8.mods.galacticraft.core.tile.TileEntityOxygenStorageModule
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.world.entity.player.Inventory as InventoryPlayer
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import org.lwjgl.opengl.GL11

@OnlyIn(Dist.CLIENT)
open class GuiOxygenStorageModule constructor(
    par1InventoryPlayer: InventoryPlayer?,
    private val tileEntity: TileEntityOxygenStorageModule
) : GuiContainerGC(ContainerOxygenStorageModule((par1InventoryPlayer)!!, tileEntity)) {
    override fun initGui() {
        super.initGui()
        val oxygenSlotDesc: MutableList<String?> = ArrayList<String?>()
        oxygenSlotDesc.add(GCCoreUtil.translate("gui.oxygen_slot.desc.0"))
        oxygenSlotDesc.add(GCCoreUtil.translate("gui.oxygen_slot.desc.1"))
        this.infoRegions.add(
            GuiElementInfoRegion(
                (this.width - this.xSize) / 2 + 16,
                (this.height - this.ySize) / 2 + 21,
                18,
                18,
                oxygenSlotDesc,
                this.width,
                this.height,
                this
            )
        )
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of
     * the items)
     */
    override fun drawGuiContainerForegroundLayer(par1: Int, par2: Int) {
        val guiTitle: String = GCCoreUtil.translate("tile.machine2.6.name")
        this.fontRenderer.drawString(
            guiTitle,
            this.xSize / 2 - this.fontRenderer.getStringWidth(guiTitle) / 2,
            6,
            4210752
        )
        val displayJoules: String = (this.tileEntity.getOxygenStored() + 0.5f).toInt()
            .toString() + " " + GCCoreUtil.translate("gui.message.of.name")
        val displayMaxJoules: String = "" + this.tileEntity.getMaxOxygenStored()
        val maxOutputLabel: String =
            GCCoreUtil.translate("gui.max_output.desc") + ": " + TileEntityOxygenStorageModule.OUTPUT_PER_TICK * 20 + GCCoreUtil.translate(
                "gui.per_second"
            )

        this.fontRenderer.drawString(
            displayJoules,
            122 - this.fontRenderer.getStringWidth(displayJoules) / 2 - 35,
            30,
            4210752
        )
        this.fontRenderer.drawString(
            displayMaxJoules,
            122 - this.fontRenderer.getStringWidth(displayMaxJoules) / 2 - 35,
            40,
            4210752
        )
        this.fontRenderer.drawString(
            maxOutputLabel,
            122 - this.fontRenderer.getStringWidth(maxOutputLabel) / 2 - 35,
            60,
            4210752
        )
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
        val scale: Int = (this.tileEntity.getOxygenStored().toDouble() / this.tileEntity.getMaxOxygenStored()
            .toDouble() * 72).toInt()
        this.drawTexturedModalRect(containerWidth + 52, containerHeight + 52, 176, 0, scale, 3)
    }

    companion object {
        private val batteryBoxTexture =
            ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/gui/oxygen_storage_module.png")
    }
}
