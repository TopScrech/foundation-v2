package galaxyspace.core.util

import net.minecraft.entity.ai.attributes.BaseAttribute
import net.minecraft.entity.ai.attributes.IAttribute

object GSAttributePlayer {
    @JvmField
    val TOGGLE_HELMET: IAttribute = object : BaseAttribute(null, "helmetkey", 0.0) {
        override fun clampValue(value: Double): Double {
            return value
        }
    }.setShouldWatch(true)

    @JvmField
    val TOGGLE_CHEST: IAttribute = object : BaseAttribute(null, "chestkey", 0.0) {
        override fun clampValue(value: Double): Double {
            return value
        }
    }.setShouldWatch(true)

    @JvmField
    val TOGGLE_LEGS: IAttribute = object : BaseAttribute(null, "legskey", 0.0) {
        override fun clampValue(value: Double): Double {
            return value
        }
    }.setShouldWatch(true)

    @JvmField
    val TOGGLE_BOOTS: IAttribute = object : BaseAttribute(null, "bootskey", 0.0) {
        override fun clampValue(value: Double): Double {
            return value
        }
    }.setShouldWatch(true)
}
