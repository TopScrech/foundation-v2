package foundation.persistence

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.NumericTag

/** Retains legacy typed presence checks, including the numeric wildcard tag ID */
fun CompoundTag.containsTag(name: String, type: Int? = null): Boolean {
    val tag = get(name) ?: return false
    return when (type) {
        null -> true
        99 -> tag is NumericTag
        else -> tag.id.toInt() == type
    }
}

/** Old lists were homogeneous, so a mismatched element type produced an empty list */
fun CompoundTag.listOfType(name: String, type: Int): ListTag {
    val list = getList(name).orElse(null) ?: return ListTag()
    return if (list.all { it.id.toInt() == type }) list else ListTag()
}
