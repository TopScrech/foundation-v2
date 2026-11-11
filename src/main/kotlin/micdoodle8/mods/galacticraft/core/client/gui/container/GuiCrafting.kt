package micdoodle8.mods.galacticraft.core.client.gui.container

import micdoodle8.mods.galacticraft.core.inventory.ContainerCrafting
import micdoodle8.mods.galacticraft.core.tile.TileEntityCrafting
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.resources.I18n
import net.minecraft.world.entity.player.Inventory as InventoryPlayer
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

@OnlyIn(Dist.CLIENT)
open class GuiCrafting constructor(playerInv: InventoryPlayer, tile: TileEntityCrafting?) :
    GuiContainerGC(ContainerCrafting(playerInv, (tile)!!)) {
    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
        this.fontRenderer.drawString(I18n.format("container.crafting", *arrayOfNulls<Any>(0)), 28, 6, 4210752)
        this.fontRenderer.drawString(
            I18n.format("container.inventory", *arrayOfNulls<Any>(0)),
            8,
            this.ySize - 96 + 2,
            4210752
        )
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        this.mc.getTextureManager().bindTexture(craftingTableGuiTextures)
        val i: Int = (this.width - this.xSize) / 2
        val j: Int = (this.height - this.ySize) / 2
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize)
    }

    companion object {
        private val craftingTableGuiTextures = ResourceLocation.parse("textures/gui/container/crafting_table.png")
    }
}
