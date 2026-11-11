package micdoodle8.mods.galacticraft.api.event.oxygen

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraftforge.eventbus.api.bus.EventBus
import net.minecraftforge.eventbus.api.event.MutableEvent

/** A normal-air neighbor changed at the edge of a pressurized volume */
class OxygenEdgeUpdateEvent(@JvmField val level: Level, pos: BlockPos) : MutableEvent() {
    @JvmField val pos: BlockPos = pos.immutable()
    fun post() { BUS.post(this) }
    companion object {
        @JvmField val BUS: EventBus<OxygenEdgeUpdateEvent> = EventBus.create(OxygenEdgeUpdateEvent::class.java)
    }
}
