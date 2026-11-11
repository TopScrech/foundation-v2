package micdoodle8.mods.galacticraft.core.client.gui.overlay

import micdoodle8.mods.galacticraft.core.util.ClientUtil
import micdoodle8.mods.galacticraft.core.util.ColorUtil
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import kotlin.math.sin

@OnlyIn(Dist.CLIENT)
object OverlayOxygenWarning : Overlay() {
    private var screenTicks: Long = 0

    /**
     * Render the GUI when player is in inventory
     */
    @JvmStatic
    fun renderOxygenWarningOverlay() {
        screenTicks++
        val mc: Minecraft = Minecraft.getInstance()
        val scaledresolution: ScaledResolution = ClientUtil.getScaledRes(mc, mc.displayWidth, mc.displayHeight)
        val width: Int = scaledresolution.getScaledWidth()
        val height: Int = scaledresolution.getScaledHeight()
        mc.entityRenderer.setupOverlayRendering()
        GlStateManager.enableColorMaterial()
        GlStateManager.pushMatrix()
        GlStateManager.scale(2.0f, 2.0f, 0.0f)
        GlStateManager.enableBlend()
        mc.fontRenderer.drawString(
            GCCoreUtil.translate("gui.warning"),
            width / 4 - mc.fontRenderer.getStringWidth(GCCoreUtil.translate("gui.warning")) / 2,
            height / 8 - 20,
            ColorUtil.to32BitColor(255, 255, 0, 0)
        )
        val alpha: Int = (200 * (sin((screenTicks / 20.0f).toDouble()) * 0.5f + 0.5f)).toInt() + 5
        mc.fontRenderer.drawString(
            GCCoreUtil.translate("gui.oxygen.warning"),
            width / 4 - mc.fontRenderer.getStringWidth(GCCoreUtil.translate("gui.oxygen.warning")) / 2,
            height / 8,
            ColorUtil.to32BitColor(alpha, 255, 0, 0)
        )
        GlStateManager.popMatrix()
    }
}
