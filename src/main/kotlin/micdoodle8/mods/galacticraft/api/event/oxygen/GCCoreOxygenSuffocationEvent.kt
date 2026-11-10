package micdoodle8.mods.galacticraft.api.event.oxygen

import net.minecraft.entity.EntityLivingBase
import net.minecraft.world.WorldProvider
import net.minecraftforge.event.entity.living.LivingEvent
import net.minecraftforge.fml.common.eventhandler.Cancelable

/**
 * Entity Living suffocation events.
 *
 *
 * Be sure to make the proper checks before cancelling oxygen events... world
 * providers, armor equipped, etc.
 */
abstract class GCCoreOxygenSuffocationEvent(entity: EntityLivingBase) : LivingEvent(entity) {
    @JvmField
    val provider: WorldProvider?

    init {
        this.provider = entity.world.provider
    }

    /**
     * This event is posted just before the living entity suffocates
     *
     *
     * Set the event as cancelled to stop the living entity from suffocating
     *
     *
     * IF THE Pre EVENT IS CANCELLED, THE "WARNING: OXYGEN SETUP INVALID!" HUD MESSAGE WILL NOT BE SHOWN
     */
    @Cancelable
    open class Pre constructor(entity: EntityLivingBase) : GCCoreOxygenSuffocationEvent(entity)


    /**
     * This event is called after the living entity takes damage from oxygen
     * suffocation
     *
     *
     * The event is not called if the pre event was canceled
     */
    open class Post constructor(entity: EntityLivingBase) : GCCoreOxygenSuffocationEvent(entity)
}
