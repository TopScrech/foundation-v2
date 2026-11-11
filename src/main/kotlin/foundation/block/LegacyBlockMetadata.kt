package foundation.block

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.ColorCollection
import net.minecraft.world.level.block.state.BlockState

/** The retained four-bit state mapping, implemented explicitly by each stateful mod block */
interface LegacyBlockMetadata {
    fun getStateFromMeta(metadata: Int): BlockState
    fun getMetaFromState(state: BlockState): Int
}

fun Block.getStateFromMeta(metadata: Int): BlockState = when {
    this is LegacyBlockMetadata -> getStateFromMeta(metadata)
    else -> LegacyVanillaBlockStates.state(this, metadata)
}

fun Block.getMetaFromState(state: BlockState): Int = when {
    this is LegacyBlockMetadata -> getMetaFromState(state)
    else -> LegacyVanillaBlockStates.metadata(this, state)
}

private fun ColorCollection<Block>.legacyColorFamily(): Int = when (this) {
    Blocks.WOOL -> 35
    Blocks.STAINED_GLASS -> 95
    Blocks.DYED_TERRACOTTA -> 159
    Blocks.STAINED_GLASS_PANE -> 160
    Blocks.CARPET -> 171
    Blocks.CONCRETE -> 251
    Blocks.CONCRETE_POWDER -> 252
    else -> error("This collection does not encode a legacy color index")
}

fun ColorCollection<Block>.getStateFromMeta(metadata: Int): BlockState =
    LegacyVanillaBlockStates.state(legacyColorFamily(), metadata)

fun ColorCollection<Block>.getMetaFromState(state: BlockState): Int =
    LegacyVanillaBlockStates.metadata(legacyColorFamily(), state)

fun ColorCollection<Block>.defaultBlockState(): BlockState = getStateFromMeta(0)
