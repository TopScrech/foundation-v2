package micdoodle8.mods.galacticraft.core.util

import net.minecraft.block.Block
import net.minecraft.item.Item
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder

open class StackSorted
constructor(private val item: Item?, private val meta: Int) {
    constructor(block: Block, meta: Int) : this(Item.getItemFromBlock(block), meta)

    open fun getItem(): Item? {
        return item
    }

    open fun getMeta(): Int {
        return meta
    }

    override fun equals(obj: Any?): Boolean {
        if (obj !is StackSorted) {
            return false
        }
        if (obj === this) {
            return true
        }

        val other: StackSorted = obj
        return EqualsBuilder()
            .append(item, other.item)
            .append(meta, other.meta)
            .isEquals()
    }

    override fun hashCode(): Int {
        return HashCodeBuilder()
            .append(item)
            .append(meta)
            .toHashCode()
    }

    override fun toString(): String {
        return "Item:(" + item + "),Meta:(" + meta + ")"
    }
}
