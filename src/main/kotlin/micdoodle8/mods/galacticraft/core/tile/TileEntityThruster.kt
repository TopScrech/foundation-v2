package micdoodle8.mods.galacticraft.core.tile

import net.minecraft.block.state.IBlockState
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

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
