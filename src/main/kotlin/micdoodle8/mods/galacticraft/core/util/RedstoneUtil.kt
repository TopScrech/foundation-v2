package micdoodle8.mods.galacticraft.core.util

import foundation.level.offset

import net.minecraft.world.level.block.Block
import net.minecraft.block.BlockAir
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World

object RedstoneUtil {
    /**
     * Returns true if the block is being directly powered with a redstone wire, lever, etc
     * (generally only used by actual redstone components for onward transmission of redstone signal)
     */
    @JvmStatic
    fun isBlockReceivingDirectRedstone(w: World?, pos: BlockPos): Boolean {
        if (w == null) {
            return false
        }

        // Check up/down without chunk load test first
        if (getStrongPower(w, pos.below(), EnumFacing.DOWN) > 0) {
            return true
        }
        if (getStrongPower(w, pos.above(), EnumFacing.UP) > 0) {
            return true
        }

        for (facing: EnumFacing in EnumFacing.HORIZONTALS) {
            if (getStrongPower_NoChunkLoad(w, pos.offset(facing, 1), facing) > 0) {
                return true
            }
        }

        return false
    }

    /**
     * Is this block powering in the specified direction Args: x, y, z, direction
     */
    @JvmStatic
    fun getStrongPower(w: World, pos: BlockPos, side: EnumFacing): Int {
        val bs: IBlockState = w.getBlockState(pos)
        return bs.getBlock().getStrongPower(bs, w, pos, side)
    }

    @JvmStatic
    fun getStrongPower_NoChunkLoad(w: World, pos: BlockPos, side: EnumFacing): Int {
        if (!w.isBlockLoaded(pos, false)) {
            return 0
        }
        val bs: IBlockState = w.getBlockState(pos)
        return bs.getBlock().getStrongPower(bs, w, pos, side)
    }

    /**
     * Returns true if the block is being INDIRECTLY powered e.g by redstone passing through another block
     * (similar to how a vanilla piston or redstone lamp responds)
     *
     * INEFFICIENT: (almost as bad as Vanilla!) examines redstone status of 30 nearby blocks if there is no redstone power
     *
     * Note: if the calling block is itself a redstone emitter (!!) its redstone output to neighbours will be ignored
     */
    @JvmStatic
    fun isBlockReceivingRedstone(w: World?, pos: BlockPos): Boolean {
        if (w == null) {
            return false
        }

        // Check up/down without chunk load test first
        if (getRedstonePowerIndirect(w, pos.above(1), EnumFacing.UP) > 0) {
            return true
        }
        if (getRedstonePowerIndirect(w, pos.below(1), EnumFacing.DOWN) > 0) {
            return true
        }

        for (facing: EnumFacing in EnumFacing.HORIZONTALS) {
            if (getRedstonePowerIndirect_NoChunkLoad(w, pos.offset(facing, 1), facing) > 0) {
                return true
            }
        }
        return false
    }

    @JvmStatic
    fun getRedstonePowerIndirect_NoChunkLoad(w: World, pos: BlockPos, side: EnumFacing): Int {
        if (!w.isBlockLoaded(pos, false)) {
            return 0
        }
        return getRedstonePowerIndirect(w, pos, side)
    }

    @JvmStatic
    fun getRedstonePowerIndirect(w: World, pos: BlockPos, facing: EnumFacing): Int {
        val bs: IBlockState = w.getBlockState(pos)
        val block: Block = bs.getBlock()
        if (block is BlockAir) {
            return 0
        }
        return if (block.shouldCheckWeakPower(bs, w, pos, facing)) getNeighbourPower_NoChunkLoad(
            w,
            pos,
            facing.getOpposite()
        ) else bs.getWeakPower(w, pos, facing)
    }

    /**
     * Similar to the vanilla method getStrongPower(BlockPos pos) but more efficient - doesn't backtrack, so 15% faster
     * (also the low level code here is faster...)
     */
    @JvmStatic
    fun getNeighbourPower_NoChunkLoad(w: World, pos: BlockPos, skip: EnumFacing?): Int {
        var i: Int = 0
        var p: Int
        var bs: IBlockState
        var sidePos: BlockPos

        if (skip != EnumFacing.DOWN) {
            sidePos = pos.offset(0, -1, 0)
            bs = w.getBlockState(sidePos)
            i = bs.getBlock().getStrongPower(bs, w, sidePos, EnumFacing.DOWN)
            if (i >= 15) {
                return i
            }
        }

        if (skip != EnumFacing.UP) {
            sidePos = pos.offset(0, 1, 0)
            bs = w.getBlockState(sidePos)
            p = bs.getBlock().getStrongPower(bs, w, sidePos, EnumFacing.UP)
            if (p >= 15) {
                return p
            }
            if (p > i) i = p
        }

        for (side: EnumFacing in EnumFacing.HORIZONTALS) {
            if (side == skip) {
                continue
            }
            //This is a slightly faster pos.offset(side)
            sidePos = pos.offset(side.getStepX(), 0, side.getStepZ())
            if (!w.isBlockLoaded(sidePos, false)) {
                continue
            }

            bs = w.getBlockState(sidePos)
            p = bs.getBlock().getStrongPower(bs, w, sidePos, side)
            if (p >= 15) {
                return p
            }
            if (p > i) i = p
        }

        return i
    }
}
