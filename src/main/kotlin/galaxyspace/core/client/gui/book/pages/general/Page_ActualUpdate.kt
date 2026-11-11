package galaxyspace.core.client.gui.book.pages.general

import asmodeuscore.core.astronomy.gui.book.Page_WithScroll
import asmodeuscore.core.utils.BookUtils.Book_Cateroies
import galaxyspace.GalaxySpace
import galaxyspace.api.IPage
import galaxyspace.core.util.GSThreadVersionCheck
import galaxyspace.core.util.GSUtils
import micdoodle8.mods.galacticraft.core.util.ColorUtil
import micdoodle8.mods.galacticraft.core.util.EnumColor
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.resources.I18n
import net.minecraft.resources.Identifier as ResourceLocation
import java.awt.Desktop
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException

@IPage
open class Page_ActualUpdate : Page_WithScroll {
    private val mc: Minecraft = Minecraft.getInstance()
    private val count = 0

    private var ex = false

    constructor()

    constructor(exit: Boolean) {
        this.ex = exit
    }

    override fun titlePage(): String {
        return "actual_update"
    }

    override fun iconTitle(): ResourceLocation? {
        return null
    }

    override fun hookBackButton(): Boolean {
        if (this.ex) {
            Minecraft.getInstance().setIngameFocus()
            return true
        }
        return false
    }

    override fun drawPage(x: Int, y: Int, font: FontRenderer?, mouseX: Int, mouseY: Int) {
        if (GSUtils.changelog.size > 12) super.drawPage(x, y, font, mouseX, mouseY)

        val ver: String =
            GSThreadVersionCheck.remoteMajVer.toString() + "." + GSThreadVersionCheck.remoteMinVer + "." + GSThreadVersionCheck.remoteBuildVer

        this.drawText("Current Version: " + EnumColor.YELLOW + GalaxySpace.VERSION, x, y - 45, 0, (font)!!)
        this.drawText(I18n.format("gs.success.name") + " " + EnumColor.YELLOW + ver, x, y - 35, 0, (font)!!)
        if (GSThreadVersionCheck.newversion) {
            (font)!!.drawString(
                I18n.format("gs.update.available"),
                x + 180,
                y + 40,
                ColorUtil.to32BitColor(255, if (mc.player.tickCount % 5 == 0) 150 else 50, 200, 255)
            )
        }

        this.drawText(EnumColor.BRIGHT_GREEN.toString() + "Changelog", x + 160, y - 40, 0, (font)!!)

        var i: Int = getScroll()
        var k: Int = 0
        while (i < GSUtils.changelog.size) {
            if (k < 12) this.drawText(
                (GSUtils.changelog.get(i))!!,
                x,
                y - 10 + ((i - getScroll()) * (font)!!.FONT_HEIGHT),
                0,
                (font)!!
            )
            i++
            k++
        }

        this.mc.getTextureManager().bindTexture(bookPageTexture)
        GlStateManager.color(1.0f, 1.0f, 1.0f)
        var uV: Int = 0
        val posX: Int = 280
        val posY: Int = 45
        if (mouseX >= x + posX && mouseX < x + posX + 120 && mouseY >= y + posY && mouseY <= y + posY + 16) uV = 17
        drawTexturedModalRect(
            (x + posX).toFloat(),
            (y + posY).toFloat(),
            120f,
            16f,
            400f,
            uV.toFloat(),
            87f,
            15f,
            false,
            false,
            512f,
            256f
        )

        this.drawText(EnumColor.YELLOW.toString() + "Download", x + 280, y - 40, 0, (font)!!)
    }

    override fun mouseClick(mouseX: Int, mouseY: Int, mouseButton: Int, x: Int, y: Int) {
        if (GSUtils.changelog.size > 12) super.mouseClick(mouseX, mouseY, mouseButton, x, y)

        if (checkClick(mouseX, mouseY, x + 280, y + 45, 120, 16)) try {
            Desktop.getDesktop()
                .browse(URI("https://www.curseforge.com/minecraft/mc-mods/galaxy-space-addon-for-galacticraft/files/all?page=1&pageSize=20&showAlphaFiles=show"))
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    private fun checkClick(mouseX: Int, mouseY: Int, x: Int, y: Int, sizeX: Int, sizeY: Int): Boolean {
        return mouseX >= x && mouseX <= x + sizeX && mouseY >= y && mouseY <= y + sizeY
    }

    override fun getCategory(): String {
        return Book_Cateroies.GENERAL.getName()
    }

    /*
	private void start() {
		final ScaledResolution scaledresolution = ClientUtil.getScaledRes(mc, mc.displayWidth, mc.displayHeight);

		final int width = scaledresolution.getScaledWidth();
		final int height = scaledresolution.getScaledHeight();

		try {
			URL url = new URL(
					"https://raw.githubusercontent.com/BlesseNtumble/GalaxySpace/1.12.2/1.12.2/changelog.txt");
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			http.addRequestProperty("User-Agent", "Mozilla/4.76");
			BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));
			String str;

			int i = 0;
			while ((str = in.readLine()) != null) {
				if (this.mc.getLanguageManager().getCurrentLanguage().getLanguageCode().startsWith("ru_")) {
					i++;
					String str2 = new String(str.getBytes(), "UTF-8");
					if (str2.startsWith("r.")) {
						str2 = str2.replace("r.", "");
						GSUtils.changelog.add(str2);
						if(str2.length() > 70) GSUtils.changelog.add("");
						//font.drawString(str2, x, y + 11 * i + 10,
								//ColorUtil.to32BitColor(255, 150, 200, 255), true);
					}
				} else {//if (this.mc.getLanguageManager().getCurrentLanguage().getLanguageCode().startsWith("en_")) {
					i++;
					String str2 = new String(str.getBytes(), "UTF-8");
					if (str2.startsWith("e.")) {
						str2 = str.replace("e.", "");
						GSUtils.changelog.add(str2);
						if(str2.length() > 70) GSUtils.changelog.add("");
						//font.drawString(str2, x, y + 12 * i - 185,
								//ColorUtil.to32BitColor(255, 150, 200, 255), true);
					}
				} 
			}
			count = i;
			in.close();
		} catch (Exception e) {
		}
	}*/
    override fun getMaxScroll(): Int {
        if (GSUtils.changelog.size > 12) return GSUtils.changelog.size - 12
        return 0
    }

    companion object {
        private val bookPageTexture = ResourceLocation.fromNamespaceAndPath(GalaxySpace.ASSET_PREFIX, "textures/gui/tablet.png")
    }
}
