package galaxyspace.core.client.gui

import galaxyspace.GalaxySpace
import galaxyspace.core.util.GSThreadVersionCheck
import micdoodle8.mods.galacticraft.core.util.ClientUtil
import micdoodle8.mods.galacticraft.core.util.ColorUtil
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.resources.I18n
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import java.awt.Desktop
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URI
import java.net.URISyntaxException
import java.net.URL

open class GSGuiUpdate : GuiScreen() {
    override fun initGui() {
        super.initGui()
    }

    override fun drawScreen(parWidth: Int, parHeight: Int, p_73863_3_: Float) {
        this.drawDefaultBackground()

        val scaledresolution: ScaledResolution = ClientUtil.getScaledRes(mc, mc.displayWidth, mc.displayHeight)
        val width: Int = scaledresolution.getScaledWidth()
        val height: Int = scaledresolution.getScaledHeight()

        GL11.glPushMatrix()
        GL11.glEnable(GL11.GL_BLEND)
        //GL11.glColor4f(0.0F, 0.6F, 1.0F, 1);
        this.mc.getTextureManager().bindTexture(guiMain)

        this.drawTexturedModalRect(100, 15, 800, 470, 0, 0, 400, 350, false, false)

        val ver: String =
            GSThreadVersionCheck.remoteMajVer.toString() + "." + GSThreadVersionCheck.remoteMinVer + "." + GSThreadVersionCheck.remoteBuildVer
        this.fontRenderer.drawString(
            I18n.format("gs.success.name") + " " + ver,
            118,
            67,
            ColorUtil.to32BitColor(255, 150, 200, 255)
        )
        this.fontRenderer.drawString(
            I18n.format("gs.update.available"),
            280,
            37,
            ColorUtil.to32BitColor(255, if (mc.player.ticksExisted % 2 == 0) 150 else 50, 200, 255)
        )
        this.fontRenderer.drawString(
            "Download New Version",
            420,
            67,
            ColorUtil.to32BitColor(255, 0, if (mc.player.ticksExisted % 2 == 0) 255 else 150, 0)
        )
        this.fontRenderer.drawString("CHANGELOG", 302, 101, ColorUtil.to32BitColor(255, 150, 200, 255))

        this.start(145, 78)

        GL11.glDisable(GL11.GL_BLEND)
        GL11.glPopMatrix()
    }

    override fun mouseClicked(x: Int, y: Int, button: Int) {
        if (x > 414 && x < 543 && y > 64 && y < 78) {
            try {
                Desktop.getDesktop()
                    .browse(URI("https://forum.micdoodle8.com/index.php?threads/1-7-10-galaxy-space-stable.5298/"))
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: URISyntaxException) {
                e.printStackTrace()
            }
        }
    }

    override fun doesGuiPauseGame(): Boolean {
        return false
    }

    open fun drawTexturedModalRect(
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        u: Int,
        v: Int,
        uWidth: Int,
        vHeight: Int,
        invertX: Boolean,
        invertY: Boolean
    ) {
        this.drawTexturedModalRect(
            x.toFloat(),
            y.toFloat(),
            width.toFloat(),
            height.toFloat(),
            u.toFloat(),
            v.toFloat(),
            uWidth.toFloat(),
            vHeight.toFloat(),
            invertX,
            invertY,
            512f,
            512f
        )
    }

    open fun drawTexturedModalRect(
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        u: Float,
        v: Float,
        uWidth: Float,
        vHeight: Float,
        invertX: Boolean,
        invertY: Boolean,
        texSizeX: Float,
        texSizeY: Float
    ) {
        // GL11.glShadeModel(GL11.GL_FLAT);
        // GL11.glEnable(GL11.GL_BLEND);
        //GL11.glEnable(GL11.GL_ALPHA_TEST);
        //GL11.glEnable(GL11.GL_TEXTURE_2D);
        val texModX: Float = 1f / texSizeX
        val texModY: Float = 1f / texSizeY
        val tessellator: Tessellator = Tessellator.getInstance()
        val vertexbuffer: BufferBuilder = tessellator.getBuffer()
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX)
        val height0: Float = if (invertY) 0f else vHeight
        val height1: Float = if (invertY) vHeight else 0f
        val width0: Float = if (invertX) uWidth else 0f
        val width1: Float = if (invertX) 0f else uWidth
        vertexbuffer.pos(x.toDouble(), (y + height).toDouble(), this.zLevel.toDouble())
            .tex(((u + width0) * texModX).toDouble(), ((v + height0) * texModY).toDouble()).endVertex()
        vertexbuffer.pos((x + width).toDouble(), (y + height).toDouble(), this.zLevel.toDouble())
            .tex(((u + width1) * texModX).toDouble(), ((v + height0) * texModY).toDouble()).endVertex()
        vertexbuffer.pos((x + width).toDouble(), y.toDouble(), this.zLevel.toDouble())
            .tex(((u + width1) * texModX).toDouble(), ((v + height1) * texModY).toDouble()).endVertex()
        vertexbuffer.pos(x.toDouble(), y.toDouble(), this.zLevel.toDouble())
            .tex(((u + width0) * texModX).toDouble(), ((v + height1) * texModY).toDouble()).endVertex()
        tessellator.draw()
    }

    private fun start(x: Int, y: Int) {
        val scaledresolution: ScaledResolution = ClientUtil.getScaledRes(mc, mc.displayWidth, mc.displayHeight)

        val width: Int = scaledresolution.getScaledWidth()
        val height: Int = scaledresolution.getScaledHeight()

        try {
            val url: URL =
                URL("https://raw.githubusercontent.com/BlesseNtumble/GalaxySpace/1.12.2/1.12.2/changelog.txt")
            val http: HttpURLConnection = url.openConnection() as HttpURLConnection
            http.addRequestProperty("User-Agent", "Mozilla/4.76")
            val `in`: BufferedReader = BufferedReader(InputStreamReader(http.getInputStream()))
            var str: String?

            var i: Int = 0
            while ((`in`.readLine().also { str = it }) != null) {
                if (mc.getLanguageManager().getCurrentLanguage().getLanguageCode().startsWith("en_")) {
                    i++
                    var str2: String = String(str!!.toByteArray(), charset("UTF-8"))
                    if (str2.startsWith("e.")) {
                        str2 = (str)!!.replace("e.", "")
                        this.fontRenderer.drawString(
                            str2,
                            x.toFloat(),
                            (y + 12 * i - 185).toFloat(),
                            ColorUtil.to32BitColor(255, 150, 200, 255),
                            true
                        )
                    }
                } else if (mc.getLanguageManager().getCurrentLanguage().getLanguageCode().startsWith("ru_")) {
                    i++
                    var str2: String = String(str!!.toByteArray(), charset("UTF-8"))
                    if (str2.startsWith("r.")) {
                        str2 = str2.replace("r.", "")
                        this.fontRenderer.drawString(
                            str2,
                            x.toFloat(),
                            (y + 11 * i + 10).toFloat(),
                            ColorUtil.to32BitColor(255, 150, 200, 255),
                            true
                        )
                    }
                }
            }
            `in`.close()
        } catch (e: Exception) {
        }
    }

    companion object {
        @JvmField
        var guiMain: ResourceLocation =
            ResourceLocation(GalaxySpace.Companion.ASSET_PREFIX, "textures/gui/guiupdate.png")
    }
}
