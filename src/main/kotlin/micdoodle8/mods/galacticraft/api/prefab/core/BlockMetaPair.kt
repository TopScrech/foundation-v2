package micdoodle8.mods.galacticraft.api.prefab.core

import foundation.block.getStateFromMeta

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState as IBlockState

/**
 * Do not include this prefab class in your released mod download.
 */
open class BlockMetaPair
constructor(private val block: Block, private val metadata: Byte) {
    open fun getBlock(): Block? {
        return this.block
    }

    open fun getMetadata(): Byte {
        return this.metadata
    }

    open fun getBlockState(): IBlockState {
        return this.block.getStateFromMeta(this.metadata.toInt())
    }
}
