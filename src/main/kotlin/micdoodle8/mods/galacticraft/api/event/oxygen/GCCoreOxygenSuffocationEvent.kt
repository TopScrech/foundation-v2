package micdoodle8.mods.galacticraft.api.event.oxygen

import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.Level
import net.minecraftforge.event.entity.living.LivingEvent
import net.minecraftforge.eventbus.api.bus.CancellableEventBus
import net.minecraftforge.eventbus.api.bus.EventBus
import net.minecraftforge.eventbus.api.event.MutableEvent
import net.minecraftforge.eventbus.api.event.characteristic.Cancellable

/** Posted before oxygen damage and, when the damage was not cancelled, afterwards */
abstract class GCCoreOxygenSuffocationEvent(private val living: LivingEntity) : MutableEvent(), LivingEvent {
    @JvmField val level: Level = living.level()
    override fun getEntity(): LivingEntity = living
    abstract fun post(): Boolean

    /** Return true from a listener to prevent suffocation and suppress the invalid-oxygen warning */
    class Pre(entity: LivingEntity) : GCCoreOxygenSuffocationEvent(entity), Cancellable {
        override fun post(): Boolean = BUS.post(this)
        companion object {
            @JvmField val BUS: CancellableEventBus<Pre> = CancellableEventBus.create(Pre::class.java)
        }
    }

    class Post(entity: LivingEntity) : GCCoreOxygenSuffocationEvent(entity) {
        override fun post(): Boolean = BUS.post(this)
        companion object {
            @JvmField val BUS: EventBus<Post> = EventBus.create(Post::class.java)
        }
    }
}
