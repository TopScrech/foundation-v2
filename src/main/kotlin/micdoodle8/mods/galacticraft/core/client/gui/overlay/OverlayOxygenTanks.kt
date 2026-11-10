package micdoodle8.mods.galacticraft.core.client.gui.overlay

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.util.ClientUtil
import micdoodle8.mods.galacticraft.core.util.ColorUtil
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11
import kotlin.math.max
import kotlin.math.min

@SideOnly(Side.CLIENT)
object OverlayOxygenTanks : Overlay() {
    private val guiTexture = ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/gui.png")

    private val minecraft: Minecraft = FMLClientHandler.instance().getClient()

    /**
     * Render the GUI that displays oxygen level in tanks
     */
    @JvmStatic
    fun renderOxygenTankIndicator(
        heatLevel: Int,
        oxygenInTank1: Int,
        oxygenInTank2: Int,
        right: Boolean,
        top: Boolean,
        invalid: Boolean
    ) {
        val scaledresolution: ScaledResolution =
            ClientUtil.getScaledRes(minecraft, minecraft.displayWidth, minecraft.displayHeight)
        val i: Int = scaledresolution.getScaledWidth()
        val j: Int = scaledresolution.getScaledHeight()
        minecraft.entityRenderer.setupOverlayRendering()
        GlStateManager.enableBlend()
        GlStateManager.disableAlpha()
        GlStateManager.depthMask(false)
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        GlStateManager.disableAlpha()
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(guiTexture)
        val tessellator: Tessellator = Tessellator.getInstance()
        val worldRenderer: BufferBuilder = tessellator.getBuffer()
        GlStateManager.enableDepth()
        GlStateManager.enableAlpha()
        GlStateManager.disableLighting()
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)

        var minLeftX: Int = 0
        var maxLeftX: Int = 0
        var minRightX: Int = 0
        var maxRightX: Int = 0
        var bottomY: Double = 0.0
        var topY: Double = 0.0
        val zLevel: Double = -190.0

        if (right) {
            minLeftX = i - 59
            maxLeftX = i - 40
            minRightX = i - 39
            maxRightX = i - 20
        } else {
            minLeftX = 10
            maxLeftX = 29
            minRightX = 30
            maxRightX = 49
        }

        if (top) {
            topY = 10.5
        } else {
            topY = (j - 57).toDouble()
        }

        bottomY = topY + 46.5

        val texMod: Float = 0.00390625f
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        worldRenderer.pos(minLeftX.toDouble(), bottomY, zLevel).tex((66 * texMod).toDouble(), (47 * texMod).toDouble())
            .endVertex()
        worldRenderer.pos((minLeftX + 9).toDouble(), bottomY, zLevel)
            .tex(((66 + 9) * texMod).toDouble(), (47 * texMod).toDouble()).endVertex()
        worldRenderer.pos((minLeftX + 9).toDouble(), topY, zLevel)
            .tex(((66 + 9) * texMod).toDouble(), (47 * 2 * texMod).toDouble()).endVertex()
        worldRenderer.pos(minLeftX.toDouble(), topY, zLevel).tex((66 * texMod).toDouble(), (47 * 2 * texMod).toDouble())
            .endVertex()
        tessellator.draw()

        val heatLevelScaled: Int = min(max(heatLevel, 1), 45)
        val heatLeveLScaledMax: Int = min(heatLevelScaled + 2, 45)
        val heatLevelScaledMin: Int = max(heatLeveLScaledMax - 2, 0)

        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        worldRenderer.pos((minLeftX + 1).toDouble(), bottomY - heatLevelScaledMin, zLevel)
            .tex((76 * texMod).toDouble(), ((48 + 45 - heatLevelScaled) * texMod).toDouble()).endVertex()
        worldRenderer.pos((minLeftX + 8).toDouble(), bottomY - heatLevelScaledMin, zLevel)
            .tex(((76 + 7) * texMod).toDouble(), ((48 + 45 - heatLevelScaled) * texMod).toDouble()).endVertex()
        worldRenderer.pos((minLeftX + 8).toDouble(), bottomY - heatLeveLScaledMax, zLevel)
            .tex(((76 + 7) * texMod).toDouble(), ((48 + 45 - heatLevelScaled) * texMod).toDouble()).endVertex()
        worldRenderer.pos((minLeftX + 1).toDouble(), bottomY - heatLeveLScaledMax, zLevel)
            .tex((76 * texMod).toDouble(), ((48 + 45 - heatLevelScaled) * texMod).toDouble()).endVertex()
        tessellator.draw()

        if (invalid) {
            GlStateManager.color(1f, 0f, 0f)
            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
            worldRenderer.pos((minLeftX - 5).toDouble(), bottomY - heatLevelScaledMin + 3, zLevel)
                .tex((84 * texMod).toDouble(), (47 * texMod).toDouble()).endVertex()
            worldRenderer.pos((minLeftX - 1).toDouble(), bottomY - heatLevelScaledMin + 3, zLevel)
                .tex(((84 + 5) * texMod).toDouble(), (47 * texMod).toDouble()).endVertex()
            worldRenderer.pos((minLeftX - 1).toDouble(), bottomY - heatLeveLScaledMax - 3, zLevel)
                .tex(((84 + 5) * texMod).toDouble(), ((47 + 9) * texMod).toDouble()).endVertex()
            worldRenderer.pos((minLeftX - 5).toDouble(), bottomY - heatLeveLScaledMax - 3, zLevel)
                .tex((84 * texMod).toDouble(), ((47 + 9) * texMod).toDouble()).endVertex()
            tessellator.draw()
            GlStateManager.color(1f, 1f, 1f)
        }

        minLeftX += 10
        maxLeftX += 10
        minRightX += 10
        maxRightX += 10

        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        worldRenderer.pos(minRightX.toDouble(), bottomY, zLevel).tex((85 * texMod).toDouble(), (47 * texMod).toDouble())
            .endVertex()
        worldRenderer.pos(maxRightX.toDouble(), bottomY, zLevel)
            .tex(((85 + 19) * texMod).toDouble(), (47 * texMod).toDouble()).endVertex()
        worldRenderer.pos(maxRightX.toDouble(), topY, zLevel)
            .tex(((85 + 19) * texMod).toDouble(), (0 * texMod).toDouble()).endVertex()
        worldRenderer.pos(minRightX.toDouble(), topY, zLevel).tex((85 * texMod).toDouble(), (0 * texMod).toDouble())
            .endVertex()
        tessellator.draw()
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        worldRenderer.pos(minLeftX.toDouble(), bottomY, zLevel).tex((85 * texMod).toDouble(), (47 * texMod).toDouble())
            .endVertex()
        worldRenderer.pos(maxLeftX.toDouble(), bottomY, zLevel)
            .tex(((85 + 19) * texMod).toDouble(), (47 * texMod).toDouble()).endVertex()
        worldRenderer.pos(maxLeftX.toDouble(), topY, zLevel)
            .tex(((85 + 19) * texMod).toDouble(), (0 * texMod).toDouble()).endVertex()
        worldRenderer.pos(minLeftX.toDouble(), topY, zLevel).tex((85 * texMod).toDouble(), (0 * texMod).toDouble())
            .endVertex()
        tessellator.draw()
        GlStateManager.depthMask(true)

        if (oxygenInTank1 > 0) {
            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
            worldRenderer.pos((minLeftX + 1).toDouble(), topY + 1 + oxygenInTank1 / 2, zLevel)
                .tex((105 * 0.00390625f).toDouble(), (oxygenInTank1 / 2 * 0.00390625f).toDouble()).endVertex()
            worldRenderer.pos((maxLeftX - 1).toDouble(), topY + 1 + oxygenInTank1 / 2, zLevel)
                .tex(((105 + 17) * 0.00390625f).toDouble(), (oxygenInTank1 / 2 * 0.00390625f).toDouble()).endVertex()
            worldRenderer.pos((maxLeftX - 1).toDouble(), topY + 1, zLevel)
                .tex(((105 + 17) * 0.00390625f).toDouble(), (1 * 0.00390625f).toDouble()).endVertex()
            worldRenderer.pos((minLeftX + 1).toDouble(), topY + 1, zLevel)
                .tex((105 * 0.00390625f).toDouble(), (1 * 0.00390625f).toDouble()).endVertex()
            tessellator.draw()

            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
            worldRenderer.pos(minLeftX.toDouble(), topY + 1 + oxygenInTank1 / 2, zLevel)
                .tex((66 * 0.00390625f).toDouble(), (oxygenInTank1 / 2 * 0.00390625f).toDouble()).endVertex()
            worldRenderer.pos((maxLeftX - 1).toDouble(), topY + 1 + oxygenInTank1 / 2, zLevel)
                .tex(((66 + 17) * 0.00390625f).toDouble(), (oxygenInTank1 / 2 * 0.00390625f).toDouble()).endVertex()
            worldRenderer.pos((maxLeftX - 1).toDouble(), topY + 1 + oxygenInTank1 / 2 - 1, zLevel)
                .tex(((66 + 17) * 0.00390625f).toDouble(), (1 * 0.00390625f).toDouble()).endVertex()
            worldRenderer.pos(minLeftX.toDouble(), topY + 1 + oxygenInTank1 / 2 - 1, zLevel)
                .tex((66 * 0.00390625f).toDouble(), (1 * 0.00390625f).toDouble()).endVertex()
            tessellator.draw()
        }

        if (oxygenInTank2 > 0) {
            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
            worldRenderer.pos((minRightX + 1).toDouble(), topY + 1 + oxygenInTank2 / 2, 0.0)
                .tex((105 * 0.00390625f).toDouble(), (oxygenInTank2 / 2 * 0.00390625f).toDouble()).endVertex()
            worldRenderer.pos((maxRightX - 1).toDouble(), topY + 1 + oxygenInTank2 / 2, 0.0)
                .tex(((105 + 17) * 0.00390625f).toDouble(), (oxygenInTank2 / 2 * 0.00390625f).toDouble()).endVertex()
            worldRenderer.pos((maxRightX - 1).toDouble(), topY + 1, 0.0)
                .tex(((105 + 17) * 0.00390625f).toDouble(), (1 * 0.00390625f).toDouble()).endVertex()
            worldRenderer.pos((minRightX + 1).toDouble(), topY + 1, 0.0)
                .tex((105 * 0.00390625f).toDouble(), (1 * 0.00390625f).toDouble()).endVertex()
            tessellator.draw()

            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
            worldRenderer.pos(minRightX.toDouble(), topY + 1 + oxygenInTank2 / 2, 0.0)
                .tex((66 * 0.00390625f).toDouble(), (oxygenInTank2 / 2 * 0.00390625f).toDouble()).endVertex()
            worldRenderer.pos((maxRightX - 1).toDouble(), topY + 1 + oxygenInTank2 / 2, 0.0)
                .tex(((66 + 17) * 0.00390625f).toDouble(), (oxygenInTank2 / 2 * 0.00390625f).toDouble()).endVertex()
            worldRenderer.pos((maxRightX - 1).toDouble(), topY + 1 + oxygenInTank2 / 2 - 1, 0.0)
                .tex(((66 + 17) * 0.00390625f).toDouble(), (oxygenInTank2 / 2 * 0.00390625f).toDouble()).endVertex()
            worldRenderer.pos(minRightX.toDouble(), topY + 1 + oxygenInTank2 / 2 - 1, 0.0)
                .tex((66 * 0.00390625f).toDouble(), (oxygenInTank2 / 2 * 0.00390625f).toDouble()).endVertex()
            tessellator.draw()
        }

        if (invalid) {
            val value: String = GCCoreUtil.translate("gui.warning.invalid_thermal")
            minecraft.fontRenderer.drawString(
                value,
                minLeftX - 18 - minecraft.fontRenderer.getStringWidth(value),
                bottomY.toInt() - heatLevelScaled - minecraft.fontRenderer.FONT_HEIGHT / 2 - 1,
                ColorUtil.to32BitColor(255, 255, 10, 10)
            )
        }
        GlStateManager.disableBlend()
    }
}
