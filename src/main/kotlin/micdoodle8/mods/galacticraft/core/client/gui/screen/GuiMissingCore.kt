package micdoodle8.mods.galacticraft.core.client.gui.screen

import micdoodle8.mods.galacticraft.core.util.ColorUtil
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.util.text.TextFormatting
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.net.URI

@SideOnly(Side.CLIENT)
open class GuiMissingCore : GuiScreen() {
    private var urlX = 0
    private var urlY = 0
    private var urlWidth = 0
    private var urlHeight = 0

    override fun initGui() {
        super.initGui()
    }

    override fun drawScreen(par1: Int, par2: Int, par3: Float) {
        this.drawDefaultBackground()
        var offset: Int = this.height / 2 - 50
        this.drawCenteredString(
            this.fontRenderer,
            GCCoreUtil.translate("gui.missing_core.name.0"),
            this.width / 2,
            offset,
            0xFF5555
        )
        offset += 25
        this.drawCenteredString(
            this.fontRenderer,
            GCCoreUtil.translate("gui.missing_core.name.1"),
            this.width / 2,
            offset,
            0xFF5555
        )
        offset += 20
        this.drawCenteredString(
            this.fontRenderer,
            GCCoreUtil.translate("gui.missing_core.name.2"),
            this.width / 2,
            offset,
            0x999999
        )
        offset += 20
        val s: String = TextFormatting.UNDERLINE.toString() + GCCoreUtil.translate("gui.missing_core.name.3")
        this.urlX = this.width / 2 - this.fontRenderer.getStringWidth(s) / 2 - 10
        this.urlY = offset - 2
        this.urlWidth = this.fontRenderer.getStringWidth(s) + 20
        this.urlHeight = 14
        drawRect(
            this.urlX,
            this.urlY,
            this.urlX + this.urlWidth,
            this.urlY + this.urlHeight,
            ColorUtil.to32BitColor(50, 0, 0, 255)
        )
        this.drawCenteredString(this.fontRenderer, s, this.width / 2, offset, 0x999999)
    }

    override fun keyTyped(par1: Char, par2: Int) {
    }

    open fun actionPerformed() {
        this.actionPerformed(null)
    }

    override fun actionPerformed(par1GuiButton: GuiButton?) {
        FMLClientHandler.instance().getClient().displayGuiScreen(null as GuiScreen?)
    }

    override fun mouseClicked(x: Int, y: Int, which: Int) {
        if (x > this.urlX && x < this.urlX + this.urlWidth && y > this.urlY && y < this.urlY + this.urlHeight) {
            try {
                val oclass: Class<*> = Class.forName("java.awt.Desktop")
                val `object`: Any? = oclass.getMethod("getDesktop", *arrayOfNulls<Class<*>>(0))
                    .invoke(null as Any?, *arrayOfNulls<Any>(0))
                oclass.getMethod("browse", *arrayOf<Class<*>>(URI::class.java))
                    .invoke(`object`, *arrayOf<Any>(URI("http://micdoodle8.com/mods/galacticraft/downloads")))
            } catch (throwable: Throwable) {
                throwable.printStackTrace()
            }
        }
    }
}
