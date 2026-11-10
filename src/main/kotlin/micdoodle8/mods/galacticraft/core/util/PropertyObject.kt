package micdoodle8.mods.galacticraft.core.util

import com.google.common.base.Function
import com.google.common.base.Predicate
import com.google.common.base.Predicates
import net.minecraftforge.common.property.IUnlistedProperty
import java.util.Objects

/**
 * This class was originally created by <williewillus> as
 * part of the Botania mod API. Original source:
 * https://raw.githubusercontent.com/williewillus/Botania/MC18/src/main/java/vazkii/botania/api/state/PropertyObject.java
 *
 *
 * Use in Galacticraft is permitted under the Botania License:
 * http://botaniamod.net/license.php
</williewillus> */

open class PropertyObject<T> @JvmOverloads constructor(
    private val name: String?,
    private val clazz: Class<*>?,
    private val validator: Predicate<T?> = Predicates.alwaysTrue<T?>(),
    private val stringFunction: Function<T?, String?> = object : Function<T?, String?> {
        override fun apply(input: T?): String? {
            return Objects.toString(input)
        }
    }
) : IUnlistedProperty<T?> {
    override fun getName(): String? {
        return name
    }

    override fun isValid(value: T?): Boolean {
        return validator.apply(value)
    }

    override fun getType(): Class<T?> {
        return clazz as Class<T?>
    }

    override fun valueToString(value: T?): String? {
        return stringFunction.apply(value)
    }
}
