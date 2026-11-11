package galaxyspace.core.client.gui.book.pages.general

import asmodeuscore.api.space.IBookPage
import asmodeuscore.core.utils.BookUtils.Book_Cateroies
import galaxyspace.GalaxySpace
import galaxyspace.api.IPage
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.resources.Identifier as ResourceLocation
import org.lwjgl.opengl.GL11

@IPage
open class Page_AboutGS : IBookPage {
    private val mc: Minecraft = Minecraft.getInstance()
    override fun titlePage(): String {
        return "about_addon"
    }

    override fun iconTitle(): ResourceLocation? {
        return null
    }

    override fun drawPage(x: Int, y: Int, font: FontRenderer?, mouseX: Int, mouseY: Int) {
        //super.drawPage(x, y, font, mouseX, mouseY);

        GlStateManager.pushMatrix()
        this.mc.getTextureManager().bindTexture(mainmenu1)
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 1f)
        this.drawTexturedModalRect(
            (x + 250).toFloat(),
            (y + 40).toFloat(),
            290f,
            50f,
            440f,
            5f,
            290f,
            50f,
            false,
            false,
            512f,
            512f
        )
        this.drawTexturedModalRect(
            (x + 120).toFloat(),
            (y + 40).toFloat(),
            180f,
            50f,
            250f,
            5f,
            180f,
            50f,
            false,
            false,
            512f,
            512f
        )

        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1f)
        this.drawTexturedModalRect(
            (x + 117).toFloat(),
            (y + 37).toFloat(),
            180f,
            50f,
            250f,
            5f,
            180f,
            50f,
            false,
            false,
            512f,
            512f
        )
        GL11.glColor4f(0.0f, 0.6f, 1.0f, 1f)
        this.drawTexturedModalRect(
            (x + 247).toFloat(),
            (y + 37).toFloat(),
            290f,
            50f,
            440f,
            5f,
            290f,
            50f,
            false,
            false,
            512f,
            512f
        )
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1f)
        GlStateManager.popMatrix()


        val str: String? = GCCoreUtil.translate("book.page." + titlePage() + ".text")
        this.drawText((str)!!, x, y, 0, (font)!!)
    }

    override fun mouseClick(mouseX: Int, mouseY: Int, mouseButton: Int, x: Int, y: Int) {
        //super.mouseClick(mouseX, mouseY, mouseButton, x, y);
    }

    override fun getCategory(): String {
        return Book_Cateroies.GENERAL.getName()
    } /*
	@Override
	public int getMaxScroll() {
		return 0;
	}*/

    companion object {
        private val mainmenu1 = ResourceLocation.parse(GalaxySpace.ASSET_PREFIX + ":" + "textures/gui/guimenu.png")
    }
}
