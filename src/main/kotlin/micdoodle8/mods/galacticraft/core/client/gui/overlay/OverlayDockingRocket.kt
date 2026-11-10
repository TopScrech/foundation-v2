package micdoodle8.mods.galacticraft.core.client.gui.overlay

import micdoodle8.mods.galacticraft.api.prefab.entity.EntitySpaceshipBase.EnumLaunchPhase

import micdoodle8.mods.galacticraft.api.prefab.entity.EntityAutoRocket
import micdoodle8.mods.galacticraft.api.prefab.entity.EntitySpaceshipBase
import micdoodle8.mods.galacticraft.core.util.ClientUtil
import micdoodle8.mods.galacticraft.core.util.ColorUtil
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

@SideOnly(Side.CLIENT)
object OverlayDockingRocket : Overlay() {
    private val minecraft: Minecraft = FMLClientHandler.instance().getClient()

    private var screenTicks: Long = 0

    /**
     * Render the GUI when player is docking a vehicle
     */
    @JvmStatic
    fun renderDockingOverlay() {
        screenTicks++
        val scaledresolution: ScaledResolution =
            ClientUtil.getScaledRes(minecraft, minecraft.displayWidth, minecraft.displayHeight)
        val width: Int = scaledresolution.getScaledWidth()
        val height: Int = scaledresolution.getScaledHeight()
        minecraft.entityRenderer.setupOverlayRendering()

        if (minecraft.player.getRidingEntity() is EntityAutoRocket) {
            val rocket: EntityAutoRocket? = minecraft.player.getRidingEntity() as EntityAutoRocket?

            if (rocket!!.launchPhase == EntitySpaceshipBase.EnumLaunchPhase.LANDING.ordinal && rocket.targetVec != null) {
                val dX: Double = Math.round((rocket.posX - ((rocket.targetVec)!!.getX())!!) * 100.0) / 100.0
                val dY: Double = Math.round((rocket.posY - ((rocket.targetVec)!!.getY())!!) * 100.0) / 100.0
                val dZ: Double = Math.round((rocket.posZ - ((rocket.targetVec)!!.getZ())!!) * 100.0) / 100.0
                val dXStr: String? = dX.toString()
                val dYStr: String? = dY.toString()
                val dZStr: String? = dZ.toString()

                val targetMotionY: Double =
                    Math.round(max((rocket.posY - ((rocket.targetVec)!!.getY())!!) / -100.0, -0.9) * 100.0) / 100.0
                val currentMotionY: Double = Math.round(rocket.motionY * 100.0) / 100.0
                val dMY: Double = floor((targetMotionY - currentMotionY) * 300)
                val dMotionY: Int = max(1.0, min(255.0, dMY)).toInt()
                val dMotionYN: Int = max(1.0, min(255.0, -dMY)).toInt()
                val targetMotionYStr: String =
                    GCCoreUtil.translate("gui.docking_rocket.target_vel.name") + ": " + String.format(
                        "%.2f",
                        targetMotionY
                    )
                val currentMotionYStr: String =
                    GCCoreUtil.translate("gui.docking_rocket.current_vel.name") + ": " + String.format(
                        "%.2f",
                        currentMotionY
                    )

                val red: Int = ColorUtil.to32BitColor(if (dMY > 0) 0 else dMotionYN, 255, 255, 255)
                val green: Int = ColorUtil.to32BitColor(if (dMY < 0) 0 else dMotionY, 255, 255, 255)
                val grey: Int = ColorUtil.to32BitColor(255, 220, 220, 220)

                //                if (dMY > 25)
//                {
//                    String warning = GCCoreUtil.translateWithFormat("gui.docking_rocket.warning.name.0", GameSettings.getKeyDisplayString(KeyHandlerClient.spaceKey.getKeyCode()));
//                    OverlayDockingRocket.minecraft.fontRenderer.drawString(warning, width / 2 - OverlayDockingRocket.minecraft.fontRenderer.getStringWidth(warning) / 2, height / 3 - 50, green);
//                }
//                else if (dMY < -25)
//                {
//                    String warning2 = GCCoreUtil.translateWithFormat("gui.docking_rocket.warning.name.1", GameSettings.getKeyDisplayString(KeyHandlerClient.leftShiftKey.getKeyCode()));
//                    OverlayDockingRocket.minecraft.fontRenderer.drawString(warning2, width / 2 - OverlayDockingRocket.minecraft.fontRenderer.getStringWidth(warning2) / 2, height / 3 - 35, red);
//                }
                minecraft.fontRenderer.drawString(
                    targetMotionYStr,
                    width - minecraft.fontRenderer.getStringWidth(targetMotionYStr) - 50,
                    height / 3 + 50,
                    grey
                )
                minecraft.fontRenderer.drawString(
                    currentMotionYStr,
                    width - minecraft.fontRenderer.getStringWidth(currentMotionYStr) - 50,
                    height / 3 + 35,
                    grey
                )

                minecraft.fontRenderer.drawString(
                    GCCoreUtil.translate("gui.docking_rocket.distance_from.name"),
                    50,
                    height / 3 + 15,
                    grey
                )
                minecraft.fontRenderer.drawString("X: " + dXStr, 50, height / 3 + 35, if (abs(dX) > 15) red else grey)
                minecraft.fontRenderer.drawString(
                    "Y: " + dYStr,
                    50,
                    height / 3 + 45,
                    if (abs(dY) > 50 || abs(dY) < 1.9) grey else if (screenTicks / 10 % 2 == 0L) red else grey
                )
                minecraft.fontRenderer.drawString("Z: " + dZStr, 50, height / 3 + 55, if (abs(dZ) > 15) red else grey)
            }
        }
    }
}
