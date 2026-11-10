package asmodeuscore.core.astronomy.gui.overlay

import org.lwjgl.opengl.GL11
import asmodeuscore.core.astronomy.gui.overlay.OverlayDetectors
import asmodeuscore.core.configs.AsmodeusConfig
import asmodeuscore.core.event.AsmodeusClientEvent
import micdoodle8.mods.galacticraft.core.client.gui.overlay.Overlay
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStatsClient
import micdoodle8.mods.galacticraft.core.util.ClientUtil
import micdoodle8.mods.galacticraft.core.util.ColorUtil
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.OxygenUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@SideOnly(Side.CLIENT)
object OverlayDetectors : Overlay() {
    private val minecraft: Minecraft = FMLClientHandler.instance().getClient()

    @JvmStatic
    fun renderPressureIndicator(pressureLevel: Float, invalid: Boolean, right: Boolean, top: Boolean) {
        var pressureLevel: Float = pressureLevel
        val player: EntityPlayerSP = minecraft.player
        val guiTexture: ResourceLocation = ResourceLocation("asmodeuscore", "textures/gui/gui.png")
        val scaledresolution: ScaledResolution =
            ClientUtil.getScaledRes(minecraft, minecraft.displayWidth, minecraft.displayHeight)
        val width: Int = scaledresolution.getScaledWidth()
        val height: Int = scaledresolution.getScaledHeight()
        minecraft.entityRenderer.setupOverlayRendering()
        GlStateManager.enableBlend()
        GlStateManager.disableAlpha()
        GlStateManager.depthMask(false)
        GlStateManager.blendFunc(770, 771)
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        GlStateManager.disableAlpha()
        GlStateManager.enableDepth()
        GlStateManager.enableAlpha()
        GlStateManager.disableLighting()
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(guiTexture)
        var minLeftX: Int = 0
        var maxLeftX: Int = 0
        var minRightX: Int = 0
        var maxRightX: Int = 0
        var bottomY: Double = 0.0
        var topY: Double = 0.0
        val zLevel: Double = -190.0
        val texMod: Float = 0.00390625f
        if (top) {
            topY = 0.0
        } else {
            topY = (height - 68).toDouble()
        }

        if (OxygenUtil.noAtmosphericCombustion(player.world.provider)) {
            bottomY = topY + 57.5
        } else {
            bottomY = topY + 5.5
        }

        if (right) {
            minLeftX = width - 49
            maxLeftX = width - 40
            minRightX = width - 59
            maxRightX = width - 74
        } else {
            minLeftX = 10
            val var24: Int = 29
            minRightX = 10
            val var27: Int = 49
        }

        if (pressureLevel > 48.0f) {
            pressureLevel = 48.0f
        }

        pressureLevel *= 0.5f
        drawTexturedModalRect(
            (minRightX.toFloat()).toDouble(),
            (bottomY.toFloat() + 1.0f).toDouble(),
            49.0f,
            9.0f,
            0.0f,
            0.0f,
            64.0f,
            9.0f,
            false,
            false,
            256.0f,
            256.0f
        )
        drawTexturedModalRect(
            (minRightX.toFloat()).toDouble(),
            (bottomY.toFloat() + 2.0f).toDouble(),
            pressureLevel,
            6.0f,
            0.0f,
            9.0f,
            pressureLevel + 14.0f,
            6.0f,
            false,
            false,
            256.0f,
            256.0f
        )
        drawTexturedModalRect(
            (minRightX.toFloat()).toDouble(),
            (bottomY.toFloat() + 4.0f).toDouble(),
            50.0f,
            5.0f,
            0.0f,
            15.0f,
            64.0f,
            5.0f,
            false,
            false,
            256.0f,
            256.0f
        )
        if (invalid) {
            val value: String = GCCoreUtil.translate("gui.warning.atmosphericpressure")
            if (right) {
                minecraft.fontRenderer
                    .drawString(
                        value,
                        minLeftX + 40 - minecraft.fontRenderer.getStringWidth(value),
                        bottomY.toInt() + 14 - minecraft.fontRenderer.FONT_HEIGHT / 2 - 1,
                        ColorUtil.to32BitColor(255, 255, 10, 10)
                    )
            } else {
                minecraft.fontRenderer
                    .drawString(
                        value,
                        minLeftX + 60,
                        bottomY.toInt() + 14 - minecraft.fontRenderer.FONT_HEIGHT / 2 - 1,
                        ColorUtil.to32BitColor(255, 255, 10, 10)
                    )
            }
        }

        GlStateManager.color(1.0f, 1.0f, 1.0f)
        GlStateManager.disableBlend()
    }

    @JvmStatic
    fun renderWaterPressureIndicator(pressureLevel: Float, invalid: Boolean, right: Boolean, top: Boolean) {
        var pressureLevel: Float = pressureLevel
        val player: EntityPlayerSP? = minecraft.player
        val guiTexture: ResourceLocation = ResourceLocation("asmodeuscore", "textures/gui/gui.png")
        val scaledresolution: ScaledResolution =
            ClientUtil.getScaledRes(minecraft, minecraft.displayWidth, minecraft.displayHeight)
        val width: Int = scaledresolution.getScaledWidth()
        val height: Int = scaledresolution.getScaledHeight()
        minecraft.entityRenderer.setupOverlayRendering()
        GlStateManager.enableBlend()
        GlStateManager.disableAlpha()
        GlStateManager.depthMask(false)
        GlStateManager.blendFunc(770, 771)
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        GlStateManager.disableAlpha()
        GlStateManager.enableDepth()
        GlStateManager.enableAlpha()
        GlStateManager.disableLighting()
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(guiTexture)
        var minLeftX: Int = 0
        var maxLeftX: Int = 0
        var minRightX: Int = 0
        var maxRightX: Int = 0
        val bottomY: Double = 0.0
        var topY: Double = 0.0
        val zLevel: Double = -190.0
        val texMod: Float = 0.00390625f
        if (top) {
            topY = 10.0
        } else {
            topY = (height - 60).toDouble()
        }

        if (right) {
            minLeftX = width - 49
            maxLeftX = width - 40
            minRightX = width - 79
            maxRightX = width - 74
        } else {
            minLeftX = 10
            val var23: Int = 29
            minRightX = 79
            val var26: Int = 49
        }

        if (pressureLevel > 52.0f) {
            pressureLevel = 52.0f
        }

        drawTexturedModalRect(
            (minRightX.toFloat()).toDouble(),
            (topY.toFloat() + 0.5f).toDouble(),
            16.0f,
            56.0f,
            73.0f,
            55.0f,
            16.0f,
            56.0f,
            false,
            false,
            256.0f,
            256.0f
        )
        GlStateManager.color(1.0f, 0.0f, 0.0f)
        drawTexturedModalRect(
            (minRightX.toFloat() + 1.0f).toDouble(),
            (topY.toFloat() + 1.0f + pressureLevel + 0.5f).toDouble(),
            6.0f,
            1.0f,
            16.0f,
            9.0f,
            1.0f,
            1.0f,
            false,
            false,
            256.0f,
            256.0f
        )
        GlStateManager.color(1.0f, 1.0f, 1.0f)
        if (invalid) {
            val value: String = GCCoreUtil.translate("gui.warning.waterpressure")
            if (right) {
                minecraft.fontRenderer
                    .drawString(
                        value,
                        minLeftX - 40 - minecraft.fontRenderer.getStringWidth(value),
                        bottomY.toInt() + 54 - minecraft.fontRenderer.FONT_HEIGHT / 2 - 1,
                        ColorUtil.to32BitColor(255, 255, 10, 10)
                    )
            } else {
                minecraft.fontRenderer
                    .drawString(
                        value,
                        minLeftX + 60,
                        bottomY.toInt() + 14 - minecraft.fontRenderer.FONT_HEIGHT / 2 - 1,
                        ColorUtil.to32BitColor(255, 255, 10, 10)
                    )
            }
        }

        GlStateManager.color(1.0f, 1.0f, 1.0f)
        GlStateManager.disableBlend()
    }

    @JvmStatic
    fun renderRadiationIndicator(radLevel: Int, invalid: Boolean, right: Boolean, top: Boolean) {
        val player: EntityPlayerSP = minecraft.player
        val guiTexture: ResourceLocation = ResourceLocation("asmodeuscore", "textures/gui/gui.png")
        val scaledresolution: ScaledResolution =
            ClientUtil.getScaledRes(minecraft, minecraft.displayWidth, minecraft.displayHeight)
        val width: Int = scaledresolution.getScaledWidth()
        val height: Int = scaledresolution.getScaledHeight()
        minecraft.entityRenderer.setupOverlayRendering()
        GlStateManager.enableBlend()
        GlStateManager.disableAlpha()
        GlStateManager.depthMask(false)
        GlStateManager.blendFunc(770, 771)
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        GlStateManager.disableAlpha()
        GlStateManager.enableDepth()
        GlStateManager.enableAlpha()
        GlStateManager.disableLighting()
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(guiTexture)
        var minLeftX: Int = 0
        var maxLeftX: Int = 0
        var minRightX: Int = 0
        var maxRightX: Int = 0
        var bottomY: Double = 0.0
        var topY: Double = 0.0
        val zLevel: Double = -190.0
        val texMod: Float = 0.00390625f
        if (top) {
            topY = 0.0
        } else {
            topY = (height - 68).toDouble()
        }

        if (OxygenUtil.noAtmosphericCombustion(player.world.provider)) {
            bottomY = topY + 10.5
        } else {
            bottomY = topY + 5.5
        }

        if (right) {
            minLeftX = width - 49
            maxLeftX = width - 40
            minRightX = width - 69
            maxRightX = width - 69
        } else {
            minLeftX = 10
            val var25: Int = 29
            minRightX = 69
            maxRightX = 69
        }

        val tessellator: Tessellator = Tessellator.getInstance()
        val worldRenderer: BufferBuilder = tessellator.getBuffer()
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX)
        worldRenderer.pos(maxRightX.toDouble(), bottomY + 56.0, zLevel)
            .tex((65.0f * texMod).toDouble(), (56.0f * texMod).toDouble()).endVertex()
        worldRenderer.pos((maxRightX + 8).toDouble(), bottomY + 56.0, zLevel)
            .tex((73.0f * texMod).toDouble(), (56.0f * texMod).toDouble()).endVertex()
        worldRenderer.pos((maxRightX + 8).toDouble(), bottomY, zLevel)
            .tex((73.0f * texMod).toDouble(), (0.0f * texMod).toDouble()).endVertex()
        worldRenderer.pos(maxRightX.toDouble(), bottomY, zLevel)
            .tex((65.0f * texMod).toDouble(), (0.0f * texMod).toDouble()).endVertex()
        tessellator.draw()
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX)
        worldRenderer.pos(minRightX.toDouble(), bottomY + 56.0, zLevel)
            .tex((73.0f * texMod).toDouble(), (56.0f * texMod).toDouble()).endVertex()
        worldRenderer.pos((maxRightX + 8).toDouble(), bottomY + 56.0, zLevel)
            .tex((81.0f * texMod).toDouble(), (56.0f * texMod).toDouble()).endVertex()
        worldRenderer.pos((maxRightX + 8).toDouble(), bottomY + 56.0 - radLevel.toDouble(), zLevel)
            .tex((81.0f * texMod).toDouble(), ((56 - radLevel).toFloat() * texMod).toDouble())
            .endVertex()
        worldRenderer.pos(minRightX.toDouble(), bottomY + 56.0 - radLevel.toDouble(), zLevel)
            .tex((73.0f * texMod).toDouble(), ((56 - radLevel).toFloat() * texMod).toDouble())
            .endVertex()
        tessellator.draw()
        if (invalid) {
            val value: String = GCCoreUtil.translate("gui.warning.solarradiation")
            if (right) {
                minecraft.fontRenderer
                    .drawString(
                        value,
                        minLeftX - 22 - minecraft.fontRenderer.getStringWidth(value),
                        bottomY.toInt() + 14 - minecraft.fontRenderer.FONT_HEIGHT / 2 - 1,
                        ColorUtil.to32BitColor(255, 255, 255, 10)
                    )
            } else {
                minecraft.fontRenderer
                    .drawString(
                        value,
                        minLeftX + 75,
                        bottomY.toInt() + 7 - minecraft.fontRenderer.FONT_HEIGHT / 2 - 1,
                        ColorUtil.to32BitColor(255, 255, 255, 10)
                    )
            }
        }

        GlStateManager.color(1.0f, 1.0f, 1.0f)
        GlStateManager.disableBlend()
    }

    @JvmStatic
    fun renderInfoPanel(tempLevel: Float, invalid: Boolean, right: Boolean, top: Boolean) {
        val player: EntityPlayerSP = minecraft.player
        val stats: GCPlayerStatsClient? = GCPlayerStatsClient.get(player)
        val guiTexture: ResourceLocation = ResourceLocation("asmodeuscore", "textures/gui/gui.png")
        val scaledresolution: ScaledResolution =
            ClientUtil.getScaledRes(minecraft, minecraft.displayWidth, minecraft.displayHeight)
        val width: Int = scaledresolution.getScaledWidth()
        val height: Int = scaledresolution.getScaledHeight()
        minecraft.entityRenderer.setupOverlayRendering()
        GlStateManager.enableBlend()
        GlStateManager.disableAlpha()
        GlStateManager.depthMask(false)
        GlStateManager.blendFunc(770, 771)
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        GlStateManager.disableAlpha()
        GlStateManager.enableDepth()
        GlStateManager.enableAlpha()
        GlStateManager.disableLighting()
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(guiTexture)
        var minLeftX: Int = 0
        var maxLeftX: Int = 0
        var minRightX: Int = 0
        var maxRightX: Int = 0
        var bottomY: Double = 0.0
        var topY: Double = 0.0
        val zLevel: Double = -190.0
        val texMod: Float = 0.00390625f
        topY = if (top) 0.0 else (height - 160).toDouble()
        if (!OxygenUtil.noAtmosphericCombustion(player.world.provider)) {
            bottomY = topY + 15.5
        } else {
            bottomY = topY + 67.0
        }

        bottomY += AsmodeusConfig.tempPosY.toDouble()
        if (right) {
            minLeftX = width - 49
            maxLeftX = width - 40
            minRightX = width - 72 + AsmodeusConfig.tempPosX
            maxRightX = width - 69
        } else {
            val var23: Int = 10
            val var25: Int = 29
            minRightX = 3
            val var28: Int = 69
        }

        drawTexturedModalRect(
            (minRightX.toFloat()).toDouble(),
            (bottomY.toFloat() + 1.0f).toDouble(),
            65.0f,
            26.0f,
            0.0f,
            21.0f,
            65.0f,
            22.0f,
            !right,
            false,
            256.0f,
            256.0f
        )
        drawTexturedModalRect(
            (minRightX.toFloat() + 50.0f + (if (!right) -5 else 3).toFloat()).toDouble(),
            (bottomY.toFloat() + 6.0f).toDouble(),
            12.0f,
            15.0f,
            81.0f,
            0.0f,
            12.0f,
            14.0f,
            !right,
            false,
            256.0f,
            256.0f
        )
        val degree: String = if (AsmodeusConfig.enableRenderDegreeSymbol) "°" else ""
        if (player != null && AsmodeusClientEvent.inOxygenBlock) {
            minecraft.fontRenderer.drawString(
                "20.0" + degree + "C",
                minRightX + 10 + (if (!right) 12 else 0),
                bottomY.toInt() + 10,
                65280
            )
        } else {
            minecraft.fontRenderer.drawString(
                String.format("%.1f", tempLevel) + degree + "C",
                minRightX + 5 + (if (!right) 5 else 0),
                bottomY.toInt() + 10,
                16777215
            )
        }

        GlStateManager.color(1.0f, 1.0f, 1.0f)
        GlStateManager.disableBlend()
    }

    @JvmStatic
    fun drawTexturedModalRect(
        xOffset: Double,
        yOffset: Double,
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
        val zLevel: Double = -200.0
        GL11.glShadeModel(7424)
        GL11.glEnable(3042)
        GL11.glEnable(3008)
        GL11.glEnable(3553)
        val texModX: Float = 1.0f / texSizeX
        val texModY: Float = 1.0f / texSizeY
        val tessellator: Tessellator = Tessellator.getInstance()
        val worldRenderer: BufferBuilder = tessellator.getBuffer()
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX)
        val height0: Float = if (invertY) 0.0f else vHeight
        val height1: Float = if (invertY) vHeight else 0.0f
        val width0: Float = if (invertX) uWidth else 0.0f
        val width1: Float = if (invertX) 0.0f else uWidth
        worldRenderer.pos(xOffset, yOffset + height.toDouble(), zLevel)
            .tex(((u + width0) * texModX).toDouble(), ((v + height0) * texModY).toDouble()).endVertex()
        worldRenderer.pos(xOffset + width.toDouble(), yOffset + height.toDouble(), zLevel)
            .tex(((u + width1) * texModX).toDouble(), ((v + height0) * texModY).toDouble())
            .endVertex()
        worldRenderer.pos(xOffset + width.toDouble(), yOffset, zLevel)
            .tex(((u + width1) * texModX).toDouble(), ((v + height1) * texModY).toDouble()).endVertex()
        worldRenderer.pos(xOffset, yOffset, zLevel)
            .tex(((u + width0) * texModX).toDouble(), ((v + height1) * texModY).toDouble()).endVertex()
        tessellator.draw()
    }
}
