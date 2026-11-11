package micdoodle8.mods.galacticraft.core.tile

import foundation.block.getStateFromMeta

import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.blocks.BlockLandingPadFull
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.util.ITickable
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import java.util.ArrayList

open class TileEntityBuggyFuelerSingle : TileEntity(), ITickable {
    private var corner = 0

    override fun update() {
        if (!this.world.isRemote && this.corner == 0) {
            val attachedLaunchPads: ArrayList<TileEntity> = ArrayList<TileEntity>()

            for (x: Int in this.getPos().getX() - 1..<this.getPos().getX() + 2) {
                for (z: Int in this.getPos().getZ() - 1..<this.getPos().getZ() + 2) {
                    val tile: TileEntity? = this.world.getBlockEntity(BlockPos(x, this.getPos().getY(), z))

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

                this.world.setBlock(
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
