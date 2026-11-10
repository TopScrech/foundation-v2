package micdoodle8.mods.galacticraft.core.tile

import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.blocks.BlockLandingPadFull
import net.minecraft.block.state.IBlockState
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ITickable
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.ArrayList

open class TileEntityBuggyFuelerSingle : TileEntity(), ITickable {
    private var corner = 0

    override fun update() {
        if (!this.world.isRemote && this.corner == 0) {
            val attachedLaunchPads: ArrayList<TileEntity> = ArrayList<TileEntity>()

            for (x: Int in this.getPos().getX() - 1..<this.getPos().getX() + 2) {
                for (z: Int in this.getPos().getZ() - 1..<this.getPos().getZ() + 2) {
                    val tile: TileEntity? = this.world.getTileEntity(BlockPos(x, this.getPos().getY(), z))

                    if (tile is TileEntityBuggyFuelerSingle && !tile.isInvalid() && tile.corner == 0) {
                        attachedLaunchPads.add(tile)
                    }
                }
            }

            if (attachedLaunchPads.size == 9) {
                for (tile: TileEntity in attachedLaunchPads) {
                    this.world.markTileEntityForRemoval(tile)
                    (tile as TileEntityBuggyFuelerSingle).corner = 1
                }

                this.world.setBlockState(
                    this.getPos(),
                    (GCBlocks.landingPadFull)!!.getStateFromMeta(BlockLandingPadFull.EnumLandingPadFullType.BUGGY_PAD.getMeta()),
                    2
                )
            }
        }
    }

    override fun shouldRefresh(world: World, pos: BlockPos, oldState: IBlockState, newSate: IBlockState): Boolean {
        return oldState.getBlock() !== newSate.getBlock()
    }
}
