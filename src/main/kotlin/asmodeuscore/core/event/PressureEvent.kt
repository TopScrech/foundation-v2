package asmodeuscore.core.event

import asmodeuscore.core.event.PressureEvent
import asmodeuscore.core.event.PressureEvent.WaterPressure
import net.minecraft.entity.EntityLivingBase
import net.minecraftforge.event.entity.EntityEvent
import net.minecraftforge.fml.common.eventhandler.Cancelable

@Cancelable
open class PressureEvent constructor(e: EntityLivingBase?, private val pressurelevel: Float) : EntityEvent(e) {
    open fun getPressureLevel(): Int {
        return this.pressurelevel.toInt()
    }

    open fun getPressureLevelF(): Float {
        return this.pressurelevel
    }

    open class WaterPressure constructor(e: EntityLivingBase?, private val water_pressure: Float) :
        EntityEvent(e) {
        open fun getWaterPressureLevel(): Float {
            return this.water_pressure
        }
    }
}
