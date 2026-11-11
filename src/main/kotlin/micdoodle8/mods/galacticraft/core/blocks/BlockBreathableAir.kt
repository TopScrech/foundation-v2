package micdoodle8.mods.galacticraft.core.blocks

import foundation.block.FoundationBlockProperties
import foundation.block.LegacyBlockMetadata
import micdoodle8.mods.galacticraft.api.event.oxygen.OxygenEdgeUpdateEvent
import micdoodle8.mods.galacticraft.core.Constants
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.AirBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.material.PushReaction
import net.minecraft.world.level.redstone.Orientation

/** Pressurized air stores thermal control as a block-state property */
open class BlockBreathableAir(properties: BlockBehaviour.Properties) : AirBlock(
    properties.replaceable().noCollision().noLootTable().air()
        .strength(0f, 600f).pushReaction(PushReaction.POPPED)
), LegacyBlockMetadata {
    constructor(assetName: String) : this(FoundationBlockProperties.named(Constants.MOD_ID_CORE, assetName))

    init { registerDefaultState(stateDefinition.any().setValue(THERMAL, false)) }

    override fun getStateFromMeta(metadata: Int): BlockState = defaultBlockState().setValue(THERMAL, metadata and 1 != 0)

    override fun getMetaFromState(state: BlockState): Int = if (state.getValue(THERMAL)) 1 else 0

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(THERMAL)
    }

    override fun neighborChanged(state: BlockState, level: Level, pos: BlockPos, block: Block,
                                 orientation: Orientation?, movedByPiston: Boolean) {
        // Replacing pressurized air with more pressurized air must not recursively restart sealing
        if (block === Blocks.AIR || block === Blocks.CAVE_AIR || block === Blocks.VOID_AIR) {
            OxygenEdgeUpdateEvent(level, pos).post()
        }
    }

    companion object {
        @JvmField val THERMAL: BooleanProperty = BooleanProperty.create("thermal")
    }
}
