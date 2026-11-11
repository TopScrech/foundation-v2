package micdoodle8.mods.galacticraft.core.tick

import micdoodle8.mods.galacticraft.api.prefab.entity.EntitySpaceshipBase.EnumLaunchPhase
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket
import net.minecraftforge.fml.common.gameevent.TickEvent.Type

import micdoodle8.mods.galacticraft.api.prefab.entity.EntityAutoRocket
import micdoodle8.mods.galacticraft.api.prefab.entity.EntitySpaceshipBase
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.client.KeyHandler
import micdoodle8.mods.galacticraft.core.client.gui.GuiIdsCore
import micdoodle8.mods.galacticraft.core.entities.EntityBuggy
import micdoodle8.mods.galacticraft.core.entities.IControllableEntity
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStatsClient
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.PlayerUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.player.LocalPlayer as EntityPlayerSP
import net.minecraft.client.settings.KeyBinding
import net.minecraft.world.entity.Entity
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.input.Keyboard

open class KeyHandlerClient constructor() : KeyHandler(
    (arrayOf<KeyBinding?>(galaxyMap, openFuelGui, toggleAdvGoggles) as Array<KeyBinding>),
    booleanArrayOf(false, false, false),
    (getVanillaKeyBindings() as Array<KeyBinding>),
    booleanArrayOf(false, true, true, true, true, true, true)
) {
    override fun keyDown(types: TickEvent.Type?, kb: KeyBinding?, tickEnd: Boolean, isRepeat: Boolean) {
        if (mc.player != null && tickEnd) {
            val playerBase: EntityPlayerSP? = PlayerUtil.getPlayerBaseClientFromPlayer(mc.player, false)

            if (playerBase == null) {
                return
            }

            val stats: GCPlayerStatsClient = (GCPlayerStatsClient.get(playerBase))!!

            if ((kb)!!.getKeyCode() == galaxyMap.getKeyCode()) {
                if (mc.currentScreen == null) {
                    mc.player.openGui(
                        GalacticraftCore.instance,
                        GuiIdsCore.GALAXY_MAP,
                        mc.world,
                        mc.player.x.toInt(),
                        mc.player.y.toInt(),
                        mc.player.z.toInt()
                    )
                }
            } else if ((kb)!!.getKeyCode() == openFuelGui.getKeyCode()) {
                if (playerBase.getVehicle() is EntitySpaceshipBase || playerBase.getVehicle() is EntityBuggy) {
                    (GalacticraftCore.packetPipeline)!!.sendToServer(
                        PacketSimple(
                            PacketSimple.EnumSimplePacket.S_OPEN_FUEL_GUI,
                            GCCoreUtil.getDimensionID(mc.world),
                            arrayOf<Any?>(PlayerUtil.getName(playerBase))
                        )
                    )
                }
            } else if ((kb)!!.getKeyCode() == toggleAdvGoggles.getKeyCode()) {
                if (playerBase != null) {
                    stats.setUsingAdvancedGoggles(!stats.isUsingAdvancedGoggles())
                }
            }
        }

        if (mc.player != null && mc.currentScreen == null) {
            var keyNum: Int = -1

            if (kb === accelerateKey) {
                keyNum = 0
            } else if (kb === decelerateKey) {
                keyNum = 1
            } else if (kb === leftKey) {
                keyNum = 2
            } else if (kb === rightKey) {
                keyNum = 3
            } else if (kb === spaceKey) {
                keyNum = 4
            } else if (kb === leftShiftKey) {
                keyNum = 5
            }

            val entityTest: Entity? = mc.player.getVehicle()
            if (entityTest != null && entityTest is IControllableEntity && keyNum != -1) {
                val entity: IControllableEntity = entityTest as IControllableEntity

                if ((kb)!!.getKeyCode() == mc.gameSettings.keyBindInventory.getKeyCode()) {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindInventory.getKeyCode(), false)
                }

                entity.pressKey(keyNum)
            } else if (entityTest != null && entityTest is EntityAutoRocket) {
                val autoRocket: EntityAutoRocket = entityTest

                if (autoRocket.launchPhase == EntitySpaceshipBase.EnumLaunchPhase.LANDING.ordinal) {
                    if (kb === leftShiftKey) {
                        autoRocket.motionY -= 0.02
                        (GalacticraftCore.packetPipeline)!!.sendToServer(
                            PacketSimple(
                                PacketSimple.EnumSimplePacket.S_UPDATE_SHIP_MOTION_Y,
                                GCCoreUtil.getDimensionID(mc.world),
                                arrayOf<Any>(autoRocket.getEntityId(), false)
                            )
                        )
                    }

                    if (kb === spaceKey) {
                        autoRocket.motionY += 0.02
                        (GalacticraftCore.packetPipeline)!!.sendToServer(
                            PacketSimple(
                                PacketSimple.EnumSimplePacket.S_UPDATE_SHIP_MOTION_Y,
                                GCCoreUtil.getDimensionID(mc.world),
                                arrayOf<Any>(autoRocket.getEntityId(), true)
                            )
                        )
                    }
                }
            }
        }
    }

    override fun keyUp(types: TickEvent.Type?, kb: KeyBinding?, tickEnd: Boolean) {
    }

    companion object {
        @JvmField
        var galaxyMap: KeyBinding

        @JvmField
        var openFuelGui: KeyBinding

        @JvmField
        var toggleAdvGoggles: KeyBinding

        init {
            galaxyMap = KeyBinding(
                GCCoreUtil.translate("keybind.map.name"),
                if (ConfigManagerCore.keyOverrideMapI == 0) Keyboard.KEY_M else ConfigManagerCore.keyOverrideMapI,
                Constants.MOD_NAME_SIMPLE
            )
            openFuelGui = KeyBinding(
                GCCoreUtil.translate("keybind.spaceshipinv.name"),
                if (ConfigManagerCore.keyOverrideFuelLevelI == 0) Keyboard.KEY_F else ConfigManagerCore.keyOverrideFuelLevelI,
                Constants.MOD_NAME_SIMPLE
            )
            toggleAdvGoggles = KeyBinding(
                GCCoreUtil.translate("keybind.sensortoggle.name"),
                if (ConfigManagerCore.keyOverrideToggleAdvGogglesI == 0) Keyboard.KEY_K else ConfigManagerCore.keyOverrideToggleAdvGogglesI,
                Constants.MOD_NAME_SIMPLE
            )
            // See ConfigManagerCore.class for actual defaults. These do nothing
        }

        @JvmField
        var accelerateKey: KeyBinding? = null

        @JvmField
        var decelerateKey: KeyBinding? = null

        @JvmField
        var leftKey: KeyBinding? = null

        @JvmField
        var rightKey: KeyBinding? = null

        @JvmField
        var upKey: KeyBinding? = null

        @JvmField
        var downKey: KeyBinding? = null

        @JvmField
        var spaceKey: KeyBinding? = null

        @JvmField
        var leftShiftKey: KeyBinding? = null
        private val mc: Minecraft = Minecraft.getInstance()

        @JvmStatic
        private fun getVanillaKeyBindings(): Array<KeyBinding?> {
            val invKey: KeyBinding? = mc.gameSettings.keyBindInventory
            accelerateKey = mc.gameSettings.keyBindForward
            decelerateKey = mc.gameSettings.keyBindBack
            leftKey = mc.gameSettings.keyBindLeft
            rightKey = mc.gameSettings.keyBindRight
            upKey = mc.gameSettings.keyBindForward
            downKey = mc.gameSettings.keyBindBack
            spaceKey = mc.gameSettings.keyBindJump
            leftShiftKey = mc.gameSettings.keyBindSneak
            return arrayOf<KeyBinding?>(invKey, accelerateKey, decelerateKey, leftKey, rightKey, spaceKey, leftShiftKey)
        }
    }
}
