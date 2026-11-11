package micdoodle8.mods.galacticraft.core.client.gui.element

import micdoodle8.mods.galacticraft.core.client.gui.container.GuiContainerGC
import micdoodle8.mods.galacticraft.core.util.ColorUtil
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.RenderHelper
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL12

@OnlyIn(Dist.CLIENT)
open class GuiElementInfoRegion constructor(
    @JvmField var xPosition: Int,
    @JvmField var yPosition: Int,
    width: Int,
    height: Int,
    tooltipStrings: MutableList<String?>?,
    parentWidth: Int,
    parentHeight: Int,
    parentGui: GuiContainerGC
) : Gui() {
    @JvmField
    protected var width: Int = 200

    @JvmField
    protected var height: Int = 20

    @JvmField
    var enabled: Boolean = true

    @JvmField
    var drawRegion: Boolean = false

    @JvmField
    var withinRegion: Boolean = false

    @JvmField
    var tooltipStrings: MutableList<String?>?

    @JvmField
    var parentWidth: Int

    @JvmField
    var parentHeight: Int

    @JvmField
    var parentGui: GuiContainerGC

    init {
        this.width = width
        this.height = height
        this.tooltipStrings = tooltipStrings
        this.parentWidth = parentWidth
        this.parentHeight = parentHeight
        this.parentGui = parentGui
    }

    protected open fun getHoverState(par1: Boolean): Int {
        var b0: Byte = 1

        if (!this.enabled) {
            b0 = 0
        } else if (par1) {
            b0 = 2
        }

        return b0.toInt()
    }

    open fun drawRegion(par2: Int, par3: Int) {
        GL11.glDisable(GL12.GL_RESCALE_NORMAL)
        RenderHelper.disableStandardItemLighting()
        GL11.glDisable(GL11.GL_LIGHTING)
        GL11.glDisable(GL11.GL_DEPTH_TEST)

        this.withinRegion =
            par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height

        if (this.drawRegion) {
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
            val k: Int = this.getHoverState(this.withinRegion)
            drawRect(
                this.xPosition,
                this.yPosition,
                this.xPosition + this.width,
                this.yPosition + this.height,
                ColorUtil.to32BitColor(100 * k, 255, 0, 0)
            )
        }

        if (this.tooltipStrings != null && !this.tooltipStrings!!.isEmpty() && this.withinRegion) {
            var k: Int = 0
            val iterator: MutableIterator<String> = (this.tooltipStrings!!.iterator() as MutableIterator<String>)

            while (iterator.hasNext()) {
                val s: String = iterator.next()
                val l: Int = FMLClientHandler.instance().getClient().fontRenderer.getStringWidth(s)

                if (l > k) {
                    k = l
                }
            }

            var i1: Int = par2 + 12
            var j1: Int = par3 - 12
            var k1: Int = 8

            if (this.tooltipStrings!!.size > 1) {
                k1 += (this.tooltipStrings!!.size - 1) * 10
            }

            if (i1 + k > this.parentWidth) {
                i1 -= 28 + k
            }

            if (this.parentGui.getTooltipOffset(par2, par3) > 0) {
                j1 -= k1 + 9
            }

            this.zLevel = 300.0f
            //            GuiElementInfoRegion.itemRenderer.zLevel = 300.0F;
            val l1: Int = -267386864
            this.drawGradientRect(i1 - 3, j1 - 4, i1 + k + 3, j1 - 3, l1, l1)
            this.drawGradientRect(i1 - 3, j1 + k1 + 3, i1 + k + 3, j1 + k1 + 4, l1, l1)
            this.drawGradientRect(i1 - 3, j1 - 3, i1 + k + 3, j1 + k1 + 3, l1, l1)
            this.drawGradientRect(i1 - 4, j1 - 3, i1 - 3, j1 + k1 + 3, l1, l1)
            this.drawGradientRect(i1 + k + 3, j1 - 3, i1 + k + 4, j1 + k1 + 3, l1, l1)
            val i2: Int = 1347420415
            val j2: Int = (i2 and 16711422) shr 1 or (i2 and -16777216)
            this.drawGradientRect(i1 - 3, j1 - 3 + 1, i1 - 3 + 1, j1 + k1 + 3 - 1, i2, j2)
            this.drawGradientRect(i1 + k + 2, j1 - 3 + 1, i1 + k + 3, j1 + k1 + 3 - 1, i2, j2)
            this.drawGradientRect(i1 - 3, j1 - 3, i1 + k + 3, j1 - 3 + 1, i2, i2)
            this.drawGradientRect(i1 - 3, j1 + k1 + 2, i1 + k + 3, j1 + k1 + 3, j2, j2)

            for (k2: Int in this.tooltipStrings!!.indices) {
                val s1: String = (this.tooltipStrings!!.get(k2))!!
                FMLClientHandler.instance().getClient().fontRenderer.drawStringWithShadow(
                    s1,
                    i1.toFloat(),
                    j1.toFloat(),
                    -1
                )

                j1 += 10
            }

            this.zLevel = 0.0f
            //            GuiElementInfoRegion.itemRenderer.zLevel = 0.0F;
        }

        GL11.glEnable(GL11.GL_LIGHTING)
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        RenderHelper.enableStandardItemLighting()
        GL11.glEnable(GL12.GL_RESCALE_NORMAL)
    }
}
