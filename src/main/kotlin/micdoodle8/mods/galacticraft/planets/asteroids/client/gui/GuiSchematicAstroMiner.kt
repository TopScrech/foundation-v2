package micdoodle8.mods.galacticraft.planets.asteroids.client.gui

import micdoodle8.mods.galacticraft.api.recipe.ISchematicResultPage
import micdoodle8.mods.galacticraft.api.recipe.SchematicRegistry
import micdoodle8.mods.galacticraft.core.client.gui.container.GuiPositionedContainer
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import micdoodle8.mods.galacticraft.planets.asteroids.inventory.ContainerSchematicAstroMiner
import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems
import net.minecraft.client.gui.GuiButton
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import org.lwjgl.opengl.GL11

open class GuiSchematicAstroMiner constructor(par1InventoryPlayer: InventoryPlayer, pos: BlockPos) :
    GuiPositionedContainer(ContainerSchematicAstroMiner(par1InventoryPlayer, pos), pos), ISchematicResultPage {
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
        this.fontRenderer.drawString(
            (AsteroidsItems.astroMiner)!!.getItemStackDisplayName(
                ItemStack(
                    AsteroidsItems.astroMiner,
                    1,
                    0
                )
            ), 7, -20 + 25, 4210752
        )
        this.fontRenderer.drawString(GCCoreUtil.translate("container.inventory"), 8, 220 - 104 + 2 - 16, 4210752)
    }

    override fun drawGuiContainerBackgroundLayer(par1: Float, par2: Int, par3: Int) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        this.mc.renderEngine.bindTexture(schematicTexture)
        val var5: Int = (this.width - this.xSize) / 2
        val var6: Int = (this.height - this.ySize) / 2
        this.drawTexturedModalRect(var5, var6, 0, 26, this.xSize, this.ySize)
    }

    override fun setPageIndex(index: Int) {
        this.pageIndex = index
    }

    companion object {
        @JvmField
        val schematicTexture: ResourceLocation =
            ResourceLocation(GalacticraftPlanets.ASSET_PREFIX, "textures/gui/schematic_astro_miner.png")
    }
}
