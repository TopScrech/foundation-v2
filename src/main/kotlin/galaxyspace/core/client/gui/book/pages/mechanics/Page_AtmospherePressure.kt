package galaxyspace.core.client.gui.book.pages.mechanics

import asmodeuscore.AsmodeusCore
import asmodeuscore.api.space.IBookPage
import asmodeuscore.core.utils.BookUtils.Book_Cateroies
import galaxyspace.GalaxySpace
import galaxyspace.api.IPage
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.util.ResourceLocation

@IPage
open class Page_AtmospherePressure : IBookPage {
    private val mc: Minecraft = Minecraft.getMinecraft()
    override fun titlePage(): String {
        return "atmosphere_pressure"
    }

    override fun iconTitle(): ResourceLocation? {
        return null
    }

    override fun drawPage(x: Int, y: Int, font: FontRenderer?, mouseX: Int, mouseY: Int) {
        val offsetY: Int = 10
        val scroll: Int = 0
        this.mc.getTextureManager().bindTexture(starTexture)
        this.drawTexturedModalRect(
            (x + 50).toFloat(),
            (y + 50 + offsetY).toFloat(),
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

        this.mc.getTextureManager().bindTexture(guiTexture)
        this.drawTexturedModalRect(
            (x + 55).toFloat(),
            (y + 70 + offsetY).toFloat(),
            60f,
            16f,
            0f,
            0f,
            64f,
            16f,
            false,
            false,
            256f,
            256f
        )

        val str: String = GCCoreUtil.translate("book.page.atmosphere_pressure.text")
        val str1: Array<String> = str.split("!n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        for (i: Int in scroll..<str1.size) {
            var strings: String = str1[i]
            strings = strings.replace("!c_o", EnumColor.ORANGE.toString() + "")
            strings = strings.replace("!c_w", EnumColor.WHITE.toString() + "")
            strings = strings.replace("!c_r", EnumColor.RED.toString() + "")
            strings = strings.replace("!c_y", EnumColor.YELLOW.toString() + "")
            strings = strings.replace("!c_g", EnumColor.BRIGHT_GREEN.toString() + "")
            if (y + 60 + (20 * i) > y + 125 + offsetY) (font)!!.drawSplitString(
                strings,
                x + 40,
                y + 60 + (20 * (i - scroll)),
                260,
                0xFFFFFF
            )
            else (font)!!.drawSplitString(strings, x + 140, y + 60 + (20 * (i - scroll)), 260, 0xFFFFFF)
        }
    }

    override fun mouseClick(mouseX: Int, mouseY: Int, mouseButton: Int, x: Int, z: Int) {
    }

    override fun getCategory(): String {
        return Book_Cateroies.MECHANICS.getName()
    }

    companion object {
        private val starTexture = ResourceLocation(GalaxySpace.ASSET_PREFIX, "textures/gui/book/star_bg.png")
        private val guiTexture = ResourceLocation(AsmodeusCore.ASSET_PREFIX, "textures/gui/gui.png")
    }
}
