package micdoodle8.mods.galacticraft.planets.venus.client.gui

import org.lwjgl.opengl.GL11
import micdoodle8.mods.galacticraft.core.client.gui.container.GuiContainerGC
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import micdoodle8.mods.galacticraft.planets.venus.inventory.ContainerCrashedProbe
import micdoodle8.mods.galacticraft.planets.venus.tile.TileEntityCrashedProbe
import net.minecraft.world.entity.player.Inventory as InventoryPlayer
import net.minecraft.resources.Identifier as ResourceLocation

open class GuiCrashedProbe constructor(
    par1InventoryPlayer: InventoryPlayer?,
    private val geothermalGenerator: TileEntityCrashedProbe
) : GuiContainerGC(ContainerCrashedProbe((par1InventoryPlayer)!!, geothermalGenerator)) {
    init {
        this.ySize = 133
        this.xSize = 176
    }

    override fun initGui() {
        super.initGui()
    }

    override fun drawGuiContainerBackgroundLayer(var1: Float, var2: Int, var3: Int) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        this.mc.getTextureManager().bindTexture(guiTexture)
        val var5: Int = (this.width - this.xSize) / 2
        val var6: Int = (this.height - this.ySize) / 2
        this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize)
    }

    override fun drawGuiContainerForegroundLayer(par1: Int, par2: Int) {
        val displayString: String = this.geothermalGenerator.getName()
        this.fontRenderer.drawString(
            displayString,
            this.xSize / 2 - this.fontRenderer.getStringWidth(displayString) / 2,
            7,
            4210752
        )
        this.fontRenderer.drawString(GCCoreUtil.translate("container.inventory"), 8, this.ySize - 94, 4210752)
    }

    companion object {
        private val guiTexture = ResourceLocation.fromNamespaceAndPath(GalacticraftPlanets.ASSET_PREFIX, "textures/gui/crashed_probe.png")
    }
}
