package galaxyspace.core.client.gui.book.pages.mechanics

import asmodeuscore.core.astronomy.gui.book.Page_WithScroll
import asmodeuscore.core.utils.BookUtils.Book_Cateroies
import galaxyspace.api.IPage
import galaxyspace.core.client.gui.book.BookRegister
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.util.ResourceLocation

@IPage
open class Page_TemperatureOnPlanet : Page_WithScroll() {
    private val mc: Minecraft = Minecraft.getMinecraft()

    override fun titlePage(): String {
        return "temp_on_planet"
    }

    override fun iconTitle(): ResourceLocation? {
        return null
    }

    override fun drawPage(x: Int, y: Int, font: FontRenderer?, mouseX: Int, mouseY: Int) {
        super.drawPage(x, y, font, mouseX, mouseY)
        val offsetY: Int = 10
        if (getScroll() < 2) {
            this.mc.getTextureManager().bindTexture(BookRegister.starTexture)
            this.drawTexturedModalRect(
                (x + 50).toFloat(),
                (y + 50 + offsetY - (getScroll() * 20)).toFloat(),
                70f,
                60f,
                0f,
                0f,
                256f,
                256f,
                false,
                false,
                256f,
                256f
            )

            this.mc.getTextureManager().bindTexture(BookRegister.guiTexture)
            this.drawTexturedModalRect(
                (x + 55).toFloat(),
                (y + 65 + offsetY - (getScroll() * 20)).toFloat(),
                60f,
                28f,
                0f,
                16f,
                64f,
                28f,
                false,
                false,
                256f,
                256f
            )

            this.drawTexturedModalRect(
                (x + 105).toFloat(),
                (y + 73 + offsetY - (getScroll() * 20)).toFloat(),
                6f,
                14f,
                81f,
                0f,
                6f,
                14f,
                false,
                false,
                256f,
                256f
            )
        }
        val str: String = GCCoreUtil.translate("book.page.temp_on_planet.text")
        val str1: Array<String> = str.split("!n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        for (i: Int in getScroll()..<str1.size) {
            var strings: String = str1[i]
            strings = strings.replace("!c_o", EnumColor.ORANGE.toString() + "")
            strings = strings.replace("!c_w", EnumColor.WHITE.toString() + "")
            strings = strings.replace("!c_r", EnumColor.RED.toString() + "")
            strings = strings.replace("!c_y", EnumColor.YELLOW.toString() + "")
            strings = strings.replace("!c_g", EnumColor.BRIGHT_GREEN.toString() + "")
            if (y + 60 + (20 * i) > y + 100 + offsetY) (font)!!.drawSplitString(
                strings,
                x + 40,
                y + 60 + (20 * (i - getScroll())),
                260,
                0xFFFFFF
            )
            else (font)!!.drawSplitString(strings, x + 140, y + 60 + (20 * (i - getScroll())), 260, 0xFFFFFF)
        }
    }

    override fun mouseClick(mouseX: Int, mouseY: Int, mouseButton: Int, x: Int, z: Int) {
    }

    override fun getCategory(): String {
        return Book_Cateroies.MECHANICS.getName()
    }

    override fun getMaxScroll(): Int {
        return 4
    }
}
