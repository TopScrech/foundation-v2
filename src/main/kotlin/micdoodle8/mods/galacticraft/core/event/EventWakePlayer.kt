package micdoodle8.mods.galacticraft.core.event

import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.core.BlockPos
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.fml.common.eventhandler.Cancelable

@Cancelable
open class EventWakePlayer constructor(
    player: EntityPlayer?,
    @JvmField val pos: BlockPos?,
    @JvmField val immediately: Boolean,
    @JvmField val updateWorld: Boolean,
    @JvmField val setSpawn: Boolean,
    @JvmField val bypassed: Boolean
) : PlayerEvent(player) {
    @JvmField
    var result: EntityPlayer.SleepResult? = null
}
