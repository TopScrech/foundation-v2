package galaxyspace.systems.SolarSystem.planets.overworld.gui.schematics

import galaxyspace.GalaxySpace
import galaxyspace.systems.SolarSystem.planets.overworld.inventory.schematics.ContainerSchematicTier2Rocket
import micdoodle8.mods.galacticraft.api.recipe.ISchematicResultPage
import micdoodle8.mods.galacticraft.api.recipe.SchematicRegistry
import micdoodle8.mods.galacticraft.core.client.gui.container.GuiContainerGC
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.planets.mars.items.MarsItems
import net.minecraft.client.gui.GuiButton
import net.minecraft.world.entity.player.Inventory as InventoryPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.core.BlockPos
import org.lwjgl.opengl.GL11

open class GuiSchematicTier2Rocket constructor(par1InventoryPlayer: InventoryPlayer, pos: BlockPos?) :
    GuiContainerGC(ContainerSchematicTier2Rocket(par1InventoryPlayer, pos)), ISchematicResultPage {
    private var pageIndex = 0

    init {
        this.xSize = 191
        this.ySize = 250
    }

    override fun initGui() {
        super.initGui()
        this.buttonList.clear()
        val back: GuiButton
        this.buttonList.add(
            GuiButton(
                0,
                this.width / 2 - 140,
                this.height / 2 - 30 + 27 - 12,
                40,
                20,
                GCCoreUtil.translate("gui.button.back.name")
            ).also { back = it })
        this.buttonList.add(
            GuiButton(
                1,
                this.width / 2 - 140,
                this.height / 2 - 30 + 27 + 12,
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
            EnumColor.WHITE.toString() + (MarsItems.rocketMars)!!.getItemStackDisplayName(
                ItemStack(
                    MarsItems.rocketMars,
                    1,
                    0
                )
            ), 7, -20 + 27, 4210752
        )
        this.fontRenderer.drawString(
            EnumColor.WHITE.toString() + GCCoreUtil.translate("container.inventory"),
            25,
            221 - 92 + 2 + 27,
            4210752
        )
    }

    override fun drawGuiContainerBackgroundLayer(par1: Float, par2: Int, par3: Int) {
        val var5: Int = (this.width - this.xSize) / 2
        val var6: Int = (this.height - this.ySize) / 2


        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        this.mc.renderEngine.bindTexture(SchematicTexture)
        this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize)

        for (i: Int in this.inventorySlots.inventorySlots.indices) {
            val x: Int = this.inventorySlots.getSlot(i).xPos
            val y: Int = this.inventorySlots.getSlot(i).yPos

            if (i == 0) {
                this.drawTexturedModalRect(var5 + x - 9, var6 + y - 9, 192, 21, 34, 34)
            } else if (i == 25) {
                GL11.glPushMatrix()
                this.drawTexturedModalRect(var5 + x - 9, var6 + y - 9, 192, 21, 34, 34)
                GL11.glColor4f(0.8f, 0.0f, 1.0f, 1.0f)
                this.drawTexturedModalRect(var5 + x - 9, var6 + y - 9, 192, 56, 34, 34)
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
                GL11.glPopMatrix()
            } else this.drawTexturedModalRect(var5 + x - 2, var6 + y - 2, 192, 0, 20, 21)
        }
    }

    override fun setPageIndex(index: Int) {
        this.pageIndex = index
    }

    companion object {
        private val SchematicTexture =
            ResourceLocation.fromNamespaceAndPath(GalaxySpace.ASSET_PREFIX, "textures/gui/schematics/schematic_clear.png")
    }
}
