package galaxyspace.core.client.gui.book.pages.blocks

import asmodeuscore.core.astronomy.gui.book.Page_WithCraftMatrix
import asmodeuscore.core.utils.BookUtils.Book_Cateroies
import galaxyspace.api.IPage
import galaxyspace.core.GSBlocks
import net.minecraft.client.gui.FontRenderer
import net.minecraft.world.item.ItemStack
import net.minecraft.resources.Identifier as ResourceLocation

@IPage
open class Page_Planetar_Shield : Page_WithCraftMatrix() {
    override fun titlePage(): String {
        return getItem().getTranslationKey() + ".name"
    }

    override fun rawTitle(): Boolean {
        return true
    }

    override fun iconTitle(): ResourceLocation? {
        return null
    }

    override fun getMaxScroll(): Int {
        return 0
    }

    override fun getCategory(): String {
        return Book_Cateroies.BLOCKS.getName()
    }

    override fun drawPage(x: Int, y: Int, font: FontRenderer?, mouseX: Int, mouseY: Int) {
        super.drawPage(x, y, font, mouseX, mouseY)
    }

    override fun getItem(): ItemStack {
        return ItemStack(GSBlocks.PLANET_SHIELD)
    }

    override fun getRecipeType(): Recipe_Type {
        return Recipe_Type.CRAFTING_TABLE
    }
}
