package micdoodle8.mods.galacticraft.api.block

import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World

/**
 * If block requires updates when oxygen is added and removed, implement this
 * into your block class.
 *
 * It is recommended that blocks implementing this should be set to tick
 * randomly, and should override @updateTick() also to carry out oxygen checks.
 */
interface IOxygenReliantBlock {
    fun onOxygenRemoved(world: World?, pos: BlockPos?, currentState: IBlockState?)

    fun onOxygenAdded(world: World?, pos: BlockPos?, currentState: IBlockState?)
}
