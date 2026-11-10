package micdoodle8.mods.galacticraft.api.client.tabs

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.RenderItem
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.client.FMLClientHandler

abstract class AbstractTab(id: Int, posX: Int, posY: Int, @JvmField var renderStack: ItemStack) :
    GuiButton(id, posX, posY, 28, 32, "") {
    @JvmField
    var texture: ResourceLocation = ResourceLocation("textures/gui/container/creative_inventory/tabs.png")

    @JvmField
    var potionOffsetLast: Int = 0

    @JvmField
    protected var itemRender: RenderItem

    init {
        this.itemRender = FMLClientHandler.instance().getClient().getRenderItem()
    }

    override fun drawButton(mc: Minecraft, mouseX: Int, mouseY: Int, partialTicks: Float) {
        var newPotionOffset: Int = TabRegistry.Companion.getPotionOffsetNEI()
        val screen: GuiScreen? = FMLClientHandler.instance().getClient().currentScreen
        if (screen is GuiInventory) {
            newPotionOffset += TabRegistry.Companion.getRecipeBookOffset(screen) - TabRegistry.Companion.recipeBookOffset
        }
        if (newPotionOffset != this.potionOffsetLast) {
            this.x += newPotionOffset - this.potionOffsetLast
            this.potionOffsetLast = newPotionOffset
        }
        if (this.visible) {
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)

            val yTexPos: Int = if (this.enabled) 3 else 32
            val ySize: Int = if (this.enabled) 25 else 32
            val xOffset: Int = if (this.id == 2) 0 else 1
            val yPos: Int = this.y + (if (this.enabled) 3 else 0)

            mc.renderEngine.bindTexture(this.texture)
            this.drawTexturedModalRect(this.x, yPos, xOffset * 28, yTexPos, 28, ySize)

            RenderHelper.enableGUIStandardItemLighting()
            this.zLevel = 100.0f
            this.itemRender.zLevel = 100.0f
            GlStateManager.enableLighting()
            GlStateManager.enableRescaleNormal()
            this.itemRender.renderItemAndEffectIntoGUI(this.renderStack, this.x + 6, this.y + 8)
            this.itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, this.renderStack, this.x + 6, this.y + 8, null)
            GlStateManager.disableLighting()
            GlStateManager.enableBlend()
            this.itemRender.zLevel = 0.0f
            this.zLevel = 0.0f
            RenderHelper.disableStandardItemLighting()
        }
    }

    override fun mousePressed(mc: Minecraft, mouseX: Int, mouseY: Int): Boolean {
        val inWindow: Boolean =
            this.enabled && this.visible && mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height

        if (inWindow) {
            this.onTabClicked()
        }

        return inWindow
    }

    abstract fun onTabClicked()

    abstract fun shouldAddToList(): Boolean
}
