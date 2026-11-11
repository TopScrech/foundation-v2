package micdoodle8.mods.galacticraft.api.event

import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.Level
import net.minecraftforge.event.entity.living.LivingEvent
import net.minecraftforge.eventbus.api.bus.CancellableEventBus
import net.minecraftforge.eventbus.api.event.MutableEvent
import net.minecraftforge.eventbus.api.event.characteristic.Cancellable

/** Return true from a listener to apply the dimension's normal gravity instead of freefall motion */
abstract class ZeroGravityEvent(private val living: LivingEntity) : MutableEvent(), LivingEvent {
    @JvmField val level: Level = living.level()
    override fun getEntity(): LivingEntity = living
    abstract fun post(): Boolean

    class InFreefall(entity: LivingEntity) : ZeroGravityEvent(entity), Cancellable {
        override fun post(): Boolean = BUS.post(this)
        companion object {
            @JvmField val BUS: CancellableEventBus<InFreefall> = CancellableEventBus.create(InFreefall::class.java)
        }
    }

    class Motion(entity: LivingEntity) : ZeroGravityEvent(entity), Cancellable {
        override fun post(): Boolean = BUS.post(this)
        companion object {
            @JvmField val BUS: CancellableEventBus<Motion> = CancellableEventBus.create(Motion::class.java)
        }
    }

    class SneakOverride(entity: LivingEntity) : ZeroGravityEvent(entity), Cancellable {
        override fun post(): Boolean = BUS.post(this)
        companion object {
            @JvmField val BUS: CancellableEventBus<SneakOverride> = CancellableEventBus.create(SneakOverride::class.java)
        }
    }
}
