package asmodeuscore.core.utils

import asmodeuscore.core.utils.ACAttributePlayer
import net.minecraft.entity.ai.attributes.BaseAttribute
import net.minecraft.entity.ai.attributes.IAttribute

object ACAttributePlayer {
    @JvmField
    val RADIATION_LVL: IAttribute = object : BaseAttribute(null, "radiation", 0.0) {
        override fun clampValue(value: Double): Double {
            return value
        }
    }.setShouldWatch(true)

    @JvmField
    val TEST: IAttribute = object : BaseAttribute(null, "test", 0.0) {
        override fun clampValue(value: Double): Double {
            return value
        }
    }.setShouldWatch(true)
}
