package asmodeuscore.core.astronomy.gui.book

import org.lwjgl.opengl.GL11
import asmodeuscore.core.astronomy.gui.book.Page_WithScroll
import asmodeuscore.core.astronomy.gui.book.Page_WithCraftMatrix.Recipe_Type
import asmodeuscore.core.astronomy.gui.book.Page_WithCraftMatrix
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.item.crafting.CraftingManager
import net.minecraft.item.crafting.IRecipe
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.resources.Identifier as ResourceLocation

abstract class Page_WithCraftMatrix : Page_WithScroll() {
    @JvmField
    protected var mc: Minecraft = Minecraft.getInstance()
    abstract override fun titlePage(): String?

    abstract override fun iconTitle(): ResourceLocation?

    abstract override fun getMaxScroll(): Int

    abstract override fun getCategory(): String?

    abstract fun getItem(): ItemStack?

    override fun itemIcon(): ItemStack {
        return (this.getItem())!!
    }

    open fun getRecipe(): Array<ItemStack> {
        val rec: IRecipe? = CraftingManager.getRecipe(Item.REGISTRY.getNameForObject(this.getItem()!!.getItem()))
        val items: Array<ItemStack> = (arrayOfNulls<ItemStack>(9) as Array<ItemStack>)
        if (rec != null) {
            var i: Int = 0

            for (s: Ingredient in rec.getIngredients()) {
                items[i] = s.getMatchingStacks()[0]
                i++
            }
        }

        return items
    }

    abstract fun getRecipeType(): Recipe_Type?

    override fun drawPage(x: Int, y: Int, font: FontRenderer?, mouseX: Int, mouseY: Int) {
        if (this.getScroll() > 0) {
            super.drawPage(x, y, font, mouseX, mouseY)
        }

        RenderHelper.enableGUIStandardItemLighting()
        this.mc.getRenderItem().renderItemAndEffectIntoGUI(this.getRecipeType()!!.getItem(), x + 265, y + 47)
        RenderHelper.disableStandardItemLighting()
        this.drawText("Recipe for: " + this.getRecipeType()!!.getItem().getDisplayName(), x + 248, y - 40, 0, (font)!!)
        if ((this.getRecipeType() == Recipe_Type.CRAFTING_TABLE || this.getRecipeType() == Recipe_Type.COMPRESSOR)
            && this.getRecipe().size > 0
        ) {
            this.drawText("Components: ", x + 275, y - 25, 0, (font)!!)
            this.mc.renderEngine.bindTexture(bookPageTexture)
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)

            for (i: Int in 0..2) {
                for (k: Int in 0..2) {
                    if (this.checkClick(mouseX, mouseY, x + 310 + k * 25, y + 82 + i * 25, 16, 16)) {
                        this.drawTexturedModalRect(
                            (x + 308 + k * 25).toFloat(),
                            (y + 80 + i * 25).toFloat(),
                            20.0f,
                            20.0f,
                            473.0f,
                            94.0f,
                            20.0f,
                            20.0f,
                            false,
                            false,
                            576.0f,
                            250.0f
                        )
                    } else {
                        this.drawTexturedModalRect(
                            (x + 308 + k * 25).toFloat(),
                            (y + 80 + i * 25).toFloat(),
                            20.0f,
                            20.0f,
                            452.0f,
                            94.0f,
                            20.0f,
                            20.0f,
                            false,
                            false,
                            576.0f,
                            250.0f
                        )
                    }
                }
            }

            var z: Int = 0
            var j: Int = 0

            for (s: ItemStack in this.getRecipe()) {
                RenderHelper.enableGUIStandardItemLighting()
                this.mc.getRenderItem().renderItemAndEffectIntoGUI(s, x + 310 + j * 25, y + 82 + z * 25)
                RenderHelper.disableStandardItemLighting()
                if (this.checkClick(mouseX, mouseY, x + 310 + j * 25, y + 82 + z * 25, 16, 16)) {
                    this.drawToolTip(x + 308 + j * 25, y + 80 + z * 25, s.getDisplayName())
                }

                if (++j % 3 == 0) {
                    z++
                    j = 0
                }
            }

            this.mc.getTextureManager().bindTexture(bookPageTexture)
            this.drawTexturedModalRect(
                (x + 257).toFloat(),
                (y + 36).toFloat(),
                4.0f,
                188.0f,
                492.0f,
                10.0f,
                14.0f,
                160.0f,
                false,
                false,
                512.0f,
                256.0f
            )
        }

        val str: String? = GCCoreUtil.translate("book.page." + this.titlePage() + ".text")
        this.drawText((str)!!, x, y - 40, this.getScroll(), (font)!!)
    }

    protected fun checkClick(mouseX: Int, mouseY: Int, x: Int, y: Int, sizeX: Int, sizeY: Int): Boolean {
        return mouseX >= x && mouseX <= x + sizeX && mouseY >= y && mouseY <= y + sizeY
    }

    protected fun drawToolTip(mousePosX: Int, mousePosY: Int, tooltip: String) {
        this.drawToolTip(this.mc, mousePosX, mousePosY, tooltip, this.mc.fontRenderer.getStringWidth(tooltip), 8)
    }

    enum class Recipe_Type(private val item: ItemStack) {
        CRAFTING_TABLE(ItemStack(Blocks.CRAFTING_TABLE)),
        NASA_WORKBENCH(ItemStack(GCBlocks.nasaWorkbench)),
        COMPRESSOR(ItemStack(GCBlocks.machineBase, 1, 12));

        fun getItem(): ItemStack {
            return this.item
        }
    }

    companion object {
        @JvmField
        val bookPageTexture: ResourceLocation = ResourceLocation.fromNamespaceAndPath("asmodeuscore", "textures/gui/tablet.png")
    }
}
