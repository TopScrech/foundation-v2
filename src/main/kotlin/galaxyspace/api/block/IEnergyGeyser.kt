package galaxyspace.api.block

import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World

interface IEnergyGeyser {
    fun isWorkGeyser(world: World?, state: IBlockState?, pos: BlockPos?): Boolean
}
