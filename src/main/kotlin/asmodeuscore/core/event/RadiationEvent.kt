package asmodeuscore.core.event

import asmodeuscore.core.event.RadiationEvent
import net.minecraft.entity.Entity
import net.minecraftforge.event.entity.EntityEvent
import net.minecraftforge.fml.common.eventhandler.Cancelable

@Cancelable
open class RadiationEvent constructor(
    entity: Entity?,
    private val radiationlevel: Int,
    private var coefradiation: Int
) : EntityEvent(entity) {
    open fun getRadiationLevel(): Int {
        return this.radiationlevel
    }

    open fun getRadiationSpeed(): Int {
        return this.coefradiation
    }

    open fun setRadiationSpeed(speed: Int): Int {
        return speed.also { this.coefradiation = it }
    }
}
