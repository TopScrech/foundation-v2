package galaxyspace.core.client.gui.book.pages.general

import asmodeuscore.api.space.IBookPage
import asmodeuscore.core.utils.BookUtils.Book_Cateroies
import galaxyspace.api.IPage
import net.minecraft.client.gui.FontRenderer
import net.minecraft.resources.Identifier as ResourceLocation

@IPage
open class Page_CraftTweaker : IBookPage {
    override fun titlePage(): String {
        return "craft_tweaker_compatability"
    }

    override fun iconTitle(): ResourceLocation? {
        return null
    }

    override fun drawPage(x: Int, y: Int, font: FontRenderer?, mouseX: Int, mouseY: Int) {
    }

    override fun mouseClick(mouseX: Int, mouseY: Int, mouseButton: Int, x: Int, z: Int) {
    }

    override fun getCategory(): String {
        return Book_Cateroies.GENERAL.getName()
    }
}
