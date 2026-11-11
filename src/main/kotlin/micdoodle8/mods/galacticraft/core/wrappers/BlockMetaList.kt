package micdoodle8.mods.galacticraft.core.wrappers

import net.minecraft.world.level.block.Block
import java.util.Arrays

open class BlockMetaList
constructor(private val block: Block, private val metaList: MutableList<Int>) {
    constructor(blockID: Block, vararg metadata: Int?) : this(blockID, Arrays.asList<Int>(*metadata))

    open fun getBlock(): Block? {
        return this.block
    }

    open fun getMetaList(): MutableList<Int>? {
        return this.metaList
    }

    open fun addMetadata(meta: Int) {
        this.metaList.add(meta)
    }

    open fun removeMetadata(meta: Int) {
        this.metaList.removeAt(meta)
    }

    override fun hashCode(): Int {
        return this.block.hashCode()
    }

    override fun equals(obj: Any?): Boolean {
        if (obj is BlockMetaList) {
            return obj === this
        }

        return false
    }
}
