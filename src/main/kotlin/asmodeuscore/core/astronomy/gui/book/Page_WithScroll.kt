package asmodeuscore.core.astronomy.gui.book

import asmodeuscore.api.space.IBookPage
import asmodeuscore.core.astronomy.gui.book.Page_WithScroll
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.util.ResourceLocation
import org.lwjgl.input.Mouse

abstract class Page_WithScroll : IBookPage {
    private val mc: Minecraft = Minecraft.getMinecraft()

    init {
        scroll = 0
    }

    abstract override fun titlePage(): String?

    abstract override fun iconTitle(): ResourceLocation?

    override fun drawPage(x: Int, y: Int, font: FontRenderer?, mouseX: Int, mouseY: Int) {
        if (Mouse.hasWheel()) {
            val dWheel: Int = Mouse.getDWheel()
            if (dWheel > 0 && scroll > 0) {
                scroll--
            } else if (dWheel < 0 && scroll < this.getMaxScroll()) {
                scroll++
            }
        }

        this.mc.getTextureManager().bindTexture(bookPageTexture)
        this.drawTexturedModalRect(
            (x + 417).toFloat(),
            (y + 36).toFloat(),
            17.0f,
            186.0f,
            492.0f,
            0.0f,
            14.0f,
            185.0f,
            false,
            false,
            512.0f,
            256.0f
        )
        this.drawTexturedModalRect(
            (x + 417).toFloat(),
            (y + 36).toFloat(),
            17.0f,
            28.0f,
            400.0f,
            68.0f,
            15.0f,
            28.0f,
            false,
            false,
            512.0f,
            256.0f
        )
        val pos: Int =
            if (this.getMaxScroll() > 0) Math.round((116 / this.getMaxScroll()).toFloat()) else this.getMaxScroll()
        this.drawTexturedModalRect(
            (x + 420).toFloat(),
            (y + 58 + scroll * pos).toFloat(),
            15.0f,
            28.0f,
            429.0f,
            68.0f,
            14.0f,
            28.0f,
            false,
            false,
            512.0f,
            256.0f
        )
        this.drawTexturedModalRect(
            (x + 419).toFloat(),
            (y + 194).toFloat(),
            16.0f,
            28.0f,
            400.0f,
            68.0f,
            14.0f,
            28.0f,
            true,
            true,
            512.0f,
            256.0f
        )
    }

    override fun mouseClick(mouseX: Int, mouseY: Int, mouseButton: Int, x: Int, y: Int) {
        if (this.checkClick(mouseX, mouseY, x + 420, y + 40, 10, 15) && scroll > 0) {
            scroll--
        }

        if (this.checkClick(mouseX, mouseY, x + 420, y + 198, 10, 20) && scroll < this.getMaxScroll()) {
            scroll++
        }
    }

    abstract override fun getCategory(): String?

    abstract fun getMaxScroll(): Int

    fun setScroll(count: Int): Int {
        scroll = count
        return count
    }

    fun getScroll(): Int {
        return scroll
    }

    private fun checkClick(mouseX: Int, mouseY: Int, x: Int, y: Int, sizeX: Int, sizeY: Int): Boolean {
        return mouseX >= x && mouseX <= x + sizeX && mouseY >= y && mouseY <= y + sizeY
    }

    companion object {
        private var scroll = 0
        private const val maxscroll = 1

        @JvmField
        var bookPageTexture: ResourceLocation = ResourceLocation("asmodeuscore", "textures/gui/tablet.png")
    }
}
