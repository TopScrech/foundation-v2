package galaxyspace.core.client

import net.minecraftforge.fml.common.gameevent.TickEvent.Type

import galaxyspace.GalaxySpace
import galaxyspace.core.configs.GSConfigCore
import galaxyspace.core.network.packet.GSPacketSimple
import galaxyspace.core.network.packet.GSPacketSimple.GSEnumSimplePacket
import galaxyspace.systems.SolarSystem.planets.overworld.items.armor.ItemSpaceSuit
import micdoodle8.mods.galacticraft.core.client.KeyHandler
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.PlayerUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.input.Keyboard

open class GSKeyHandlerClient constructor() : KeyHandler(
    (arrayOf<KeyBinding?>(toggleHelmet, toggleChest, toggleLegs, toggleBoots) as Array<KeyBinding>),
    booleanArrayOf(false, false, false, false),
    (getVanillaKeyBindings() as Array<KeyBinding>),
    booleanArrayOf()
) {
    override fun keyDown(types: TickEvent.Type?, kb: KeyBinding?, tickEnd: Boolean, isRepeat: Boolean) {
        if (mc.player != null && tickEnd) {
            val playerBase: EntityPlayerSP? = PlayerUtil.getPlayerBaseClientFromPlayer(mc.player, false)

            if (playerBase != null && mc.inGameHasFocus) {
                if ((kb)!!.getKeyCode() == toggleHelmet.getKeyCode()) {
                    (GalaxySpace.Companion.packetPipeline)!!.sendToServer(
                        GSPacketSimple(
                            GSEnumSimplePacket.S_UPDATE_NBT_ITEM_IN_ARMOR,
                            GCCoreUtil.getDimensionID(mc.world),
                            *arrayOf<Any?>(3, ItemSpaceSuit.suit_buttons[0])
                        )
                    )
                }

                if ((kb)!!.getKeyCode() == toggleChest.getKeyCode()) {
                    (GalaxySpace.Companion.packetPipeline)!!.sendToServer(
                        GSPacketSimple(
                            GSEnumSimplePacket.S_UPDATE_NBT_ITEM_IN_ARMOR,
                            GCCoreUtil.getDimensionID(mc.world),
                            *arrayOf<Any?>(2, ItemSpaceSuit.suit_buttons[1])
                        )
                    )
                }

                if ((kb)!!.getKeyCode() == toggleLegs.getKeyCode()) {
                    (GalaxySpace.Companion.packetPipeline)!!.sendToServer(
                        GSPacketSimple(
                            GSEnumSimplePacket.S_UPDATE_NBT_ITEM_IN_ARMOR,
                            GCCoreUtil.getDimensionID(mc.world),
                            *arrayOf<Any?>(1, ItemSpaceSuit.suit_buttons[2])
                        )
                    )
                }

                if ((kb)!!.getKeyCode() == toggleBoots.getKeyCode()) {
                    (GalaxySpace.Companion.packetPipeline)!!.sendToServer(
                        GSPacketSimple(
                            GSEnumSimplePacket.S_UPDATE_NBT_ITEM_IN_ARMOR,
                            GCCoreUtil.getDimensionID(mc.world),
                            *arrayOf<Any?>(0, ItemSpaceSuit.suit_buttons[3])
                        )
                    )
                }
            }
        }
    }

    override fun keyUp(types: TickEvent.Type?, kb: KeyBinding?, tickEnd: Boolean) {
    }

    companion object {
        @JvmField
        var toggleHelmet: KeyBinding

        @JvmField
        var toggleChest: KeyBinding

        @JvmField
        var toggleLegs: KeyBinding

        @JvmField
        var toggleBoots: KeyBinding
        private val mc: Minecraft = Minecraft.getMinecraft()

        init {
            toggleHelmet = KeyBinding(
                GCCoreUtil.translate("keybind.togglehelmet.name"),
                if (GSConfigCore.keyOverrideToggleHelmetI == 0) Keyboard.KEY_F else GSConfigCore.keyOverrideToggleHelmetI,
                GalaxySpace.Companion.NAME
            )
            toggleChest = KeyBinding(
                GCCoreUtil.translate("keybind.togglechest.name"),
                if (GSConfigCore.keyOverrideToggleChestI == 0) Keyboard.KEY_G else GSConfigCore.keyOverrideToggleChestI,
                GalaxySpace.Companion.NAME
            )
            toggleLegs = KeyBinding(
                GCCoreUtil.translate("keybind.togglelegs.name"),
                if (GSConfigCore.keyOverrideToggleLegsI == 0) Keyboard.KEY_H else GSConfigCore.keyOverrideToggleLegsI,
                GalaxySpace.Companion.NAME
            )
            toggleBoots = KeyBinding(
                GCCoreUtil.translate("keybind.toggleboots.name"),
                if (GSConfigCore.keyOverrideToggleBootsI == 0) Keyboard.KEY_J else GSConfigCore.keyOverrideToggleBootsI,
                GalaxySpace.Companion.NAME
            )
        }

        @JvmStatic
        private fun getVanillaKeyBindings(): Array<KeyBinding?> {
            return arrayOf<KeyBinding?>()
        }
    }
}
