package asmodeuscore.core.astronomy.gui.screen

import net.minecraft.client.renderer.GlStateManager.DestFactor
import net.minecraft.client.renderer.GlStateManager.SourceFactor

import asmodeuscore.core.astronomy.gui.screen.GuiCustomTeleporting.TeleportData
import asmodeuscore.core.astronomy.gui.screen.GuiCustomTeleporting
import asmodeuscore.core.utils.Utils
import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiTeleporting
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderSpaceStation
import micdoodle8.mods.galacticraft.core.tick.TickHandlerClient
import micdoodle8.mods.galacticraft.core.util.ColorUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.ArrayList

@SideOnly(Side.CLIENT)
open class GuiCustomTeleporting constructor(private val dimID: Int) : GuiTeleporting(dimID) {
    private var data = TeleportData(
        ResourceLocation("asmodeuscore:textures/gui/celestial_teleport.png"),
        arrayOf<String>("Taking one small step", "Taking one giant leap", "Prepare for entry!"),
        -1
    )
    private val teleportMessage: String
    private val teleportMinecraft: Minecraft = Minecraft.getMinecraft()
    private val timer = 0

    init {
        for (data: TeleportData in list) {
            if (data.dimid == dimID) {
                this.data = data
                break
            }

            this.data = list.get(0)
        }

        this.teleportMessage = this.data.texts[(Math.random() * this.data.texts.size.toDouble()).toInt()]
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        GlStateManager.disableLighting()
        GlStateManager.disableFog()
        val tessellator: Tessellator = Tessellator.getInstance()
        val bufferbuilder: BufferBuilder = tessellator.getBuffer()
        val res: ScaledResolution = ScaledResolution(this.teleportMinecraft)
        this.teleportMinecraft.getTextureManager().bindTexture(this.data.image)
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        Utils.drawTexturedModalRect(
            0.0f,
            0.0f,
            this.width.toFloat(),
            this.height.toFloat(),
            0.0f,
            0.0f,
            this.width.toFloat(),
            this.height.toFloat(),
            false,
            false,
            this.width.toFloat(),
            this.height.toFloat()
        )
        val size: Int = this.teleportMessage.length * 5 + 40
        this.drawHorizontalGradientRect(
            res.getScaledWidth() / 2 - size - 40,
            res.getScaledHeight() - 50,
            res.getScaledWidth() / 2 - size,
            res.getScaledHeight() - 20,
            Utils.getIntColorWHC(0, 0, 0, 0),
            -1442840576
        )
        drawRect(
            res.getScaledWidth() / 2 - size,
            res.getScaledHeight() - 50,
            res.getScaledWidth() / 2 + size,
            res.getScaledHeight() - 20,
            -1442840576
        )
        this.drawHorizontalGradientRect(
            res.getScaledWidth() / 2 + size,
            res.getScaledHeight() - 50,
            res.getScaledWidth() / 2 + size + 40,
            res.getScaledHeight() - 20,
            -1442840576,
            Utils.getIntColorWHC(0, 0, 0, 0)
        )
        this.drawCenteredString(
            this.fontRenderer,
            this.teleportMessage,
            this.width / 2,
            this.height - 40,
            ColorUtil.to32BitColor(255, 255, 255, 255)
        )
    }

    override fun updateScreen() {
        if (this.teleportMinecraft.player != null && this.teleportMinecraft.player.world != null && this.teleportMinecraft.player.world.provider.getDimension() == this.dimID && (this.teleportMinecraft.player.world.provider is WorldProviderSpaceStation || this.teleportMinecraft.player.posY - this.teleportMinecraft.player.lastTickPosY < 0.0)) {
            this.teleportMinecraft.displayGuiScreen(null)
            TickHandlerClient.teleportingGui = null
        }
    }

    private fun drawHorizontalGradientRect(
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
        startColor: Int,
        endColor: Int
    ) {
        val f: Float = (startColor shr 24 and 0xFF).toFloat() / 255.0f
        val f1: Float = (startColor shr 16 and 0xFF).toFloat() / 255.0f
        val f2: Float = (startColor shr 8 and 0xFF).toFloat() / 255.0f
        val f3: Float = (startColor and 0xFF).toFloat() / 255.0f
        val f4: Float = (endColor shr 24 and 0xFF).toFloat() / 255.0f
        val f5: Float = (endColor shr 16 and 0xFF).toFloat() / 255.0f
        val f6: Float = (endColor shr 8 and 0xFF).toFloat() / 255.0f
        val f7: Float = (endColor and 0xFF).toFloat() / 255.0f
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
        bufferbuilder.pos(right.toDouble(), top.toDouble(), this.zLevel.toDouble()).color(f5, f6, f7, f4).endVertex()
        bufferbuilder.pos(left.toDouble(), top.toDouble(), this.zLevel.toDouble()).color(f1, f3, f3, f).endVertex()
        bufferbuilder.pos(left.toDouble(), bottom.toDouble(), this.zLevel.toDouble()).color(f1, f3, f3, f).endVertex()
        bufferbuilder.pos(right.toDouble(), bottom.toDouble(), this.zLevel.toDouble()).color(f5, f6, f7, f4).endVertex()
        tessellator.draw()
        GlStateManager.shadeModel(7424)
        GlStateManager.disableBlend()
        GlStateManager.enableAlpha()
        GlStateManager.enableTexture2D()
    }

    open class TeleportData constructor(
        @field:kotlin.jvm.JvmField  val image: ResourceLocation,
        @field:kotlin.jvm.JvmField  val texts: Array<String>,
        @field:kotlin.jvm.JvmField  val dimid: Int
    )

    companion object {
        private val list: MutableList<TeleportData> = ArrayList<TeleportData>()

        @JvmStatic
        fun getList(): MutableList<TeleportData> {
            return list
        }
    }
}
