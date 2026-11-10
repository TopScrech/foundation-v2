package micdoodle8.mods.galacticraft.core.blocks

import net.minecraft.block.material.Material

open class BlockDoubleSlabGC constructor(name: String?, material: Material?) : BlockSlabGC((material)!!) {
    init {
        this.setTranslationKey(name)
    }

    override fun isDouble(): Boolean {
        return true
    }
}
