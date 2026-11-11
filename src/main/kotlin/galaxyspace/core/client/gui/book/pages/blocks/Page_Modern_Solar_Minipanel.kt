package galaxyspace.core.client.gui.book.pages.blocks

import asmodeuscore.core.utils.BookUtils.Book_Cateroies
import galaxyspace.api.IPage
import galaxyspace.core.GSBlocks
import galaxyspace.core.client.gui.book.BookRegister
import galaxyspace.core.client.gui.book.pages.Page_AssemblerCraft
import net.minecraft.world.item.ItemStack
import net.minecraft.resources.Identifier as ResourceLocation

@IPage
open class Page_Modern_Solar_Minipanel : Page_AssemblerCraft() {
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

    override fun getItem(): ItemStack {
        return ItemStack(GSBlocks.MODERN_SINGLE_SOLARPANEL)
    }

    override fun getRecipeType(): Recipe_Type? {
        return BookRegister.ASSEMBLER
    }
}
