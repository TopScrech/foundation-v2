package micdoodle8.mods.galacticraft.core.client.gui.container

import micdoodle8.mods.galacticraft.api.recipe.ISchematicResultPage
import micdoodle8.mods.galacticraft.api.recipe.SchematicRegistry
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.inventory.ContainerSchematicTier1Rocket
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.gui.GuiButton
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import org.lwjgl.opengl.GL11

open class GuiSchematicTier1Rocket constructor(par1InventoryPlayer: InventoryPlayer, pos: BlockPos) :
    GuiPositionedContainer(ContainerSchematicTier1Rocket(par1InventoryPlayer, pos), pos), ISchematicResultPage {
    private var pageIndex = 0

    init {
        this.ySize = 221
    }

    override fun initGui() {
        super.initGui()
        val backButton: GuiButton
        this.buttonList.add(
            GuiButton(
                0,
                this.width / 2 - 130,
                this.height / 2 - 110,
                40,
                20,
                GCCoreUtil.translate("gui.button.back.name")
            ).also { backButton = it })
        this.buttonList.add(
            GuiButton(
                1,
                this.width / 2 - 130,
                this.height / 2 - 110 + 25,
                40,
                20,
                GCCoreUtil.translate("gui.button.next.name")
            )
        )
        backButton.enabled = false
    }

    override fun actionPerformed(par1GuiButton: GuiButton) {
        if (par1GuiButton.enabled) {
            when (par1GuiButton.id) {
                0 -> SchematicRegistry.flipToLastPage(this, this.pageIndex)
                1 -> SchematicRegistry.flipToNextPage(this, this.pageIndex)
            }
        }
    }

    override fun drawGuiContainerForegroundLayer(par1: Int, par2: Int) {
        this.fontRenderer.drawString(GCCoreUtil.translate("schematic.rocket_t1.name"), 7, -20 + 27, 4210752)
        this.fontRenderer.drawString(GCCoreUtil.translate("container.inventory"), 8, 202 - 104 + 2 + 27, 4210752)
    }

    override fun drawGuiContainerBackgroundLayer(par1: Float, par2: Int, par3: Int) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        this.mc.renderEngine.bindTexture(rocketBenchTexture)
        val var5: Int = (this.width - this.xSize) / 2
        val var6: Int = (this.height - this.ySize) / 2
        this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize)
    }

    override fun setPageIndex(index: Int) {
        this.pageIndex = index
    }

    companion object {
        private val rocketBenchTexture = ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/rocketbench.png")
    }
}
