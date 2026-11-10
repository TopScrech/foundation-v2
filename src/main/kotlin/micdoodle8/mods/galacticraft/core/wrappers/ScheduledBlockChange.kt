package micdoodle8.mods.galacticraft.core.wrappers

import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos

open class ScheduledBlockChange
constructor(
    private var changePosition: BlockPos?,
    private var change: IBlockState?,
    private var changeUpdateFlag: Int
) {
    constructor(changePosition: BlockVec3, change: IBlockState?) : this(changePosition.toBlockPos(), change, 3)

    open fun getChangePosition(): BlockPos? {
        return this.changePosition
    }

    open fun setChangePosition(changePosition: BlockPos?) {
        this.changePosition = changePosition
    }

    open fun getChangeState(): IBlockState? {
        return this.change
    }

    open fun setChangeState(change: IBlockState?) {
        this.change = change
    }

    open fun getChangeUpdateFlag(): Int {
        return changeUpdateFlag
    }

    open fun setChangeUpdateFlag(changeUpdateFlag: Int) {
        this.changeUpdateFlag = changeUpdateFlag
    }
}
