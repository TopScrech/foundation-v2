package asmodeuscore.api.space

import org.lwjgl.opengl.GL11
import micdoodle8.mods.galacticraft.core.util.ColorUtil
import micdoodle8.mods.galacticraft.core.util.EnumColor
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation

interface IBookPage {
    fun titlePage(): String?
    fun iconTitle(): ResourceLocation?
    fun drawPage(x: Int, y: Int, font: FontRenderer?, mouseX: Int, mouseY: Int)
    fun mouseClick(mouseX: Int, mouseY: Int, mouseButton: Int, x: Int, z: Int)
    fun getCategory(): String?

    fun itemIcon(): ItemStack {
        return ItemStack.EMPTY
    }

    fun hookBackButton(): Boolean {
        return false
    }

    fun rawTitle(): Boolean {
        return false
    }

    fun drawText(text: String, x: Int, y: Int, pos: Int, font: FontRenderer) {
        drawText(text, x, y, pos, 360, font)
    }

    fun drawText(text: String, x: Int, y: Int, pos: Int, size: Int, font: FontRenderer) {
        //String str = Utils.translate("book.page." + titlePage() + ".text");
        val str1: Array<String> = text.split("!n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        for (i: Int in pos..<str1.size) {
            var strings: String = str1[i]
            strings = strings.replace("!c_o", EnumColor.ORANGE.toString() + "")
            strings = strings.replace("!c_w", EnumColor.WHITE.toString() + "")
            strings = strings.replace("!c_r", EnumColor.RED.toString() + "")
            strings = strings.replace("!c_y", EnumColor.YELLOW.toString() + "")
            strings = strings.replace("!c_g", EnumColor.BRIGHT_GREEN.toString() + "")

            font.drawSplitString(strings, x + 40, y + 90 + (10 * (i - pos)), size, 0xFFFFFF)
        }
    }

    fun drawItemStack(mc: Minecraft, stack: ItemStack, x: Int, y: Int, mouseX: Int, mouseY: Int, tooltip: Boolean) {
        RenderHelper.enableGUIStandardItemLighting()
        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y)
        if (tooltip) {
            if (mouseX >= x && mouseX <= x + 16 && mouseY >= y && mouseY <= y + 16) this.drawToolTip(
                mc,
                mouseX,
                y,
                stack.getDisplayName()
            )
        }
        RenderHelper.disableStandardItemLighting()
    }

    fun drawTexturedModalRect(
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

    fun drawTexturedModalRect(
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
        val zLevel: Float = 0.0f

        val texModX: Float = 1f / texSizeX
        val texModY: Float = 1f / texSizeY
        val tessellator: Tessellator = Tessellator.getInstance()
        val vertexbuffer: BufferBuilder = tessellator.getBuffer()
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX)
        val height0: Float = if (invertY) 0f else vHeight
        val height1: Float = if (invertY) vHeight else 0f
        val width0: Float = if (invertX) uWidth else 0f
        val width1: Float = if (invertX) 0f else uWidth
        vertexbuffer.pos(x.toDouble(), (y + height).toDouble(), zLevel.toDouble())
            .tex(((u + width0) * texModX).toDouble(), ((v + height0) * texModY).toDouble()).endVertex()
        vertexbuffer.pos((x + width).toDouble(), (y + height).toDouble(), zLevel.toDouble())
            .tex(((u + width1) * texModX).toDouble(), ((v + height0) * texModY).toDouble()).endVertex()
        vertexbuffer.pos((x + width).toDouble(), y.toDouble(), zLevel.toDouble())
            .tex(((u + width1) * texModX).toDouble(), ((v + height1) * texModY).toDouble()).endVertex()
        vertexbuffer.pos(x.toDouble(), y.toDouble(), zLevel.toDouble())
            .tex(((u + width0) * texModX).toDouble(), ((v + height1) * texModY).toDouble()).endVertex()
        tessellator.draw()
    }

    fun drawToolTip(mc: Minecraft, mousePosX: Int, mousePosY: Int, tooltip: String) {
        this.drawToolTip(mc, mousePosX, mousePosY, tooltip, mc.fontRenderer.getStringWidth(tooltip), 8)
    }

    fun drawToolTip(mc: Minecraft, mousePosX: Int, mousePosY: Int, tooltip: String, width: Int, height: Int) {
        GL11.glDepthMask(true)
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glPushMatrix()
        GL11.glTranslatef(0f, 0f, 300f)
        val k: Int = width
        val j2: Int = mousePosX - k / 2
        val k2: Int = mousePosY - 12
        val i1: Int = height
        /*
        if (j2 + k > width)
        {
            j2 -= (j2 - width + k);
        }

        if (k2 + i1 + 6 > height)
        {
            k2 = height - i1 - 6;
        }
*/
        val j1: Int = ColorUtil.to32BitColor(190, 0, 153, 255)
        this.drawGradientRect1(j2 - 3, k2 - 4, j2 + k + 3, k2 - 3, j1, j1)
        this.drawGradientRect1(j2 - 3, k2 + i1 + 3, j2 + k + 3, k2 + i1 + 4, j1, j1)
        this.drawGradientRect1(j2 - 3, k2 - 3, j2 + k + 3, k2 + i1 + 3, j1, j1)
        this.drawGradientRect1(j2 - 4, k2 - 3, j2 - 3, k2 + i1 + 3, j1, j1)
        this.drawGradientRect1(j2 + k + 3, k2 - 3, j2 + k + 4, k2 + i1 + 3, j1, j1)
        val k1: Int = ColorUtil.to32BitColor(170, 0, 153, 255)
        val l1: Int = (k1 and 16711422) shr 1 or (k1 and -16777216)
        this.drawGradientRect1(j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + i1 + 3 - 1, k1, l1)
        this.drawGradientRect1(j2 + k + 2, k2 - 3 + 1, j2 + k + 3, k2 + i1 + 3 - 1, k1, l1)
        this.drawGradientRect1(j2 - 3, k2 - 3, j2 + k + 3, k2 - 3 + 1, k1, k1)
        this.drawGradientRect1(j2 - 3, k2 + i1 + 2, j2 + k + 3, k2 + i1 + 3, l1, l1)

        mc.fontRenderer.drawSplitString(tooltip, j2, k2, 150, ColorUtil.to32BitColor(255, 255, 255, 255))

        GL11.glPopMatrix()
    }

    fun drawGradientRect1(left: Int, top: Int, right: Int, bottom: Int, startColor: Int, endColor: Int) {
        val f: Float = (startColor shr 24 and 255).toFloat() / 255.0f
        val f1: Float = (startColor shr 16 and 255).toFloat() / 255.0f
        val f2: Float = (startColor shr 8 and 255).toFloat() / 255.0f
        val f3: Float = (startColor and 255).toFloat() / 255.0f
        val f4: Float = (endColor shr 24 and 255).toFloat() / 255.0f
        val f5: Float = (endColor shr 16 and 255).toFloat() / 255.0f
        val f6: Float = (endColor shr 8 and 255).toFloat() / 255.0f
        val f7: Float = (endColor and 255).toFloat() / 255.0f
        val zLevel: Float = 0.0f
        GlStateManager.disableTexture2D()
        GlStateManager.enableBlend()
        GlStateManager.disableAlpha()
        GlStateManager.tryBlendFuncSeparate(
            GlStateManager.SourceFactor.SRC_ALPHA,
            GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
            GlStateManager.SourceFactor.ONE,
            GlStateManager.DestFactor.ZERO
        )
        GlStateManager.shadeModel(7425)
        val tessellator: Tessellator = Tessellator.getInstance()
        val bufferbuilder: BufferBuilder = tessellator.getBuffer()
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR)
        bufferbuilder.pos(right.toDouble(), top.toDouble(), zLevel.toDouble()).color(f1, f2, f3, f).endVertex()
        bufferbuilder.pos(left.toDouble(), top.toDouble(), zLevel.toDouble()).color(f1, f2, f3, f).endVertex()
        bufferbuilder.pos(left.toDouble(), bottom.toDouble(), zLevel.toDouble()).color(f5, f6, f7, f4).endVertex()
        bufferbuilder.pos(right.toDouble(), bottom.toDouble(), zLevel.toDouble()).color(f5, f6, f7, f4).endVertex()
        tessellator.draw()
        GlStateManager.shadeModel(7424)
        GlStateManager.disableBlend()
        GlStateManager.enableAlpha()
        GlStateManager.enableTexture2D()
    }
}
