package micdoodle8.mods.galacticraft.core.client.gui.container

import micdoodle8.mods.galacticraft.api.recipe.ISchematicResultPage
import micdoodle8.mods.galacticraft.api.recipe.SchematicRegistry
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.inventory.ContainerBuggyBench
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.gui.GuiButton
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.client.FMLClientHandler
import org.lwjgl.opengl.GL11

open class GuiSchematicBuggy constructor(par1InventoryPlayer: InventoryPlayer, pos: BlockPos) :
    GuiPositionedContainer(
        ContainerBuggyBench(
            par1InventoryPlayer,
            pos,
            FMLClientHandler.instance().getClient().player
        ), pos
    ), ISchematicResultPage {
    private var pageIndex = 0

    init {
        this.ySize = 221
    }

    override fun initGui() {
        super.initGui()
        this.buttonList.clear()
        this.buttonList.add(
            GuiButton(
                0,
                this.width / 2 - 130,
                this.height / 2 - 110,
                40,
                20,
                GCCoreUtil.translate("gui.button.back.name")
            )
        )
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
        this.fontRenderer.drawString(GCCoreUtil.translate("schematic.moonbuggy.name"), 7, -20 + 27, 4210752)
        this.fontRenderer.drawString(GCCoreUtil.translate("container.inventory"), 8, 202 - 104 + 2 + 27, 4210752)
    }

    override fun drawGuiContainerBackgroundLayer(par1: Float, par2: Int, par3: Int) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        this.mc.renderEngine.bindTexture(buggyBenchTexture)
        val var5: Int = (this.width - this.xSize) / 2
        val var6: Int = (this.height - 221) / 2
        this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize, 220)
    }

    override fun setPageIndex(index: Int) {
        this.pageIndex = index
    }

    companion object {
        private val buggyBenchTexture = ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/buggybench.png")
    }
}
