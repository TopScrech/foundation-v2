package micdoodle8.mods.galacticraft.core.client.gui.overlay

import micdoodle8.mods.galacticraft.api.prefab.entity.EntitySpaceshipBase
import micdoodle8.mods.galacticraft.core.util.ClientUtil
import micdoodle8.mods.galacticraft.core.util.ColorUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11
import kotlin.math.floor

@SideOnly(Side.CLIENT)
object OverlayLaunchCountdown : Overlay() {
    private val minecraft: Minecraft = FMLClientHandler.instance().getClient()

    @JvmStatic
    fun renderCountdownOverlay() {
        GlStateManager.disableLighting()
        var count: Int = (minecraft.player.getRidingEntity() as EntitySpaceshipBase).timeUntilLaunch / 2

        count = floor((count / 10.0f).toDouble()).toInt()

        val scaledresolution: ScaledResolution =
            ClientUtil.getScaledRes(minecraft, minecraft.displayWidth, minecraft.displayHeight)
        val width: Int = scaledresolution.getScaledWidth()
        val height: Int = scaledresolution.getScaledHeight()
        minecraft.entityRenderer.setupOverlayRendering()

        GL11.glPushMatrix()

        if (count <= 10) {
            GL11.glScalef(4.0f, 4.0f, 0.0f)

            minecraft.fontRenderer.drawString(
                count.toString(),
                width / 8 - minecraft.fontRenderer.getStringWidth(count.toString()) / 2,
                height / 20,
                ColorUtil.to32BitColor(255, 255, 0, 0)
            )
        } else {
            GL11.glScalef(2.0f, 2.0f, 0.0f)

            minecraft.fontRenderer.drawString(
                count.toString(),
                width / 4 - minecraft.fontRenderer.getStringWidth(count.toString()) / 2,
                height / 8,
                ColorUtil.to32BitColor(255, 255, 0, 0)
            )
        }

        GL11.glPopMatrix()
        GlStateManager.enableLighting()
    }
}
