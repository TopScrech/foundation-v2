package micdoodle8.mods.galacticraft.core.client.gui.overlay

import micdoodle8.mods.galacticraft.core.entities.EntityLander
import micdoodle8.mods.galacticraft.core.tick.KeyHandlerClient
import micdoodle8.mods.galacticraft.core.util.ClientUtil
import micdoodle8.mods.galacticraft.core.util.ColorUtil
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.settings.GameSettings
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.sin

@SideOnly(Side.CLIENT)
object OverlayLander : Overlay() {
    private val minecraft: Minecraft = FMLClientHandler.instance().getClient()

    private var screenTicks: Long = 0

    /**
     * Render the GUI when player is in inventory
     */
    @JvmStatic
    fun renderLanderOverlay() {
        screenTicks++
        val scaledresolution: ScaledResolution =
            ClientUtil.getScaledRes(minecraft, minecraft.displayWidth, minecraft.displayHeight)
        val width: Int = scaledresolution.getScaledWidth()
        val height: Int = scaledresolution.getScaledHeight()
        minecraft.entityRenderer.setupOverlayRendering()

        GL11.glPushMatrix()

        GL11.glScalef(2.0f, 2.0f, 0.0f)

        if (minecraft.player.getRidingEntity()!!.motionY < -2.0) {
            minecraft.fontRenderer.drawString(
                GCCoreUtil.translate("gui.warning"),
                width / 4 - minecraft.fontRenderer.getStringWidth(GCCoreUtil.translate("gui.warning")) / 2,
                height / 8 - 20,
                ColorUtil.to32BitColor(255, 255, 0, 0)
            )
            val alpha: Int = (255 * sin((screenTicks / 20.0f).toDouble())).toInt()
            val press1: String? = GCCoreUtil.translate("gui.lander.warning2")
            val press2: String? = GCCoreUtil.translate("gui.lander.warning3")
            minecraft.fontRenderer.drawString(
                press1 + GameSettings.getKeyDisplayString((KeyHandlerClient.spaceKey)!!.getKeyCode()) + press2,
                width / 4 - minecraft.fontRenderer.getStringWidth(
                    press1 + GameSettings.getKeyDisplayString((KeyHandlerClient.spaceKey)!!.getKeyCode()) + press2
                ) / 2,
                height / 8,
                ColorUtil.to32BitColor(alpha, alpha, alpha, alpha)
            )
        }

        GL11.glPopMatrix()

        if (minecraft.player.getRidingEntity()!!.motionY != 0.0) {
            val string: String =
                GCCoreUtil.translate("gui.lander.velocity") + ": " + Math.round((minecraft.player.getRidingEntity() as EntityLander).motionY * 1000) / 100.0 + " " + GCCoreUtil.translate(
                    "gui.lander.velocityu"
                )
            val color: Int = ColorUtil.to32BitColor(
                255,
                floor(abs(minecraft.player.getRidingEntity()!!.motionY) * 51.0).toInt(),
                255 - floor(abs(minecraft.player.getRidingEntity()!!.motionY) * 51.0).toInt(),
                0
            )
            minecraft.fontRenderer.drawString(
                string,
                width / 2 - minecraft.fontRenderer.getStringWidth(string) / 2,
                height / 3,
                color
            )
        }
    }
}
