package micdoodle8.mods.galacticraft.core.tile

import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World

open class TileEntityThruster : TileEntity() {
    //    @Override
    //    public boolean canUpdate()
    //    {
    //        return false;
    //    }
    override fun shouldRefresh(world: World, pos: BlockPos, oldState: IBlockState, newSate: IBlockState): Boolean {
        return oldState.getBlock() !== newSate.getBlock()
    }
}
