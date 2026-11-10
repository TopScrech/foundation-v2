package galaxyspace.core.client.gui.book.pages.mechanics

import asmodeuscore.api.space.IBookPage
import asmodeuscore.core.utils.BookUtils.Book_Cateroies
import galaxyspace.api.IPage
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.util.ResourceLocation

@IPage
open class Page_CorrosiveAtmosphere : IBookPage {
    private val mc: Minecraft = Minecraft.getMinecraft()

    override fun titlePage(): String {
        return "corrosive_atmosphere"
    }

    override fun iconTitle(): ResourceLocation? {
        return null
    }

    override fun drawPage(x: Int, y: Int, font: FontRenderer?, mouseX: Int, mouseY: Int) {
        this.drawText(GCCoreUtil.translate("book.page.corrosive_atmosphere.text"), x, y - 40, 0, (font)!!)
    }

    override fun mouseClick(mouseX: Int, mouseY: Int, mouseButton: Int, x: Int, z: Int) {
    }

    override fun getCategory(): String {
        return Book_Cateroies.MECHANICS.getName()
    }
}
