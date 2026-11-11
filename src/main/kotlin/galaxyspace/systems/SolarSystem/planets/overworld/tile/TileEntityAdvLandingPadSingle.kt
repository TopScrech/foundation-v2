package galaxyspace.systems.SolarSystem.planets.overworld.tile

import galaxyspace.core.GSBlocks
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityAdvLandingPadSingle
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.util.ITickable
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import java.util.ArrayList

open class TileEntityAdvLandingPadSingle : TileEntity(), ITickable {
    private var corner = 0

    override fun update() {
        if (!this.world.isRemote && this.corner == 0) {
            val attachedLaunchPads: ArrayList<TileEntity> = ArrayList<TileEntity>()

            for (x: Int in this.getPos().getX() - 2..<this.getPos().getX() + 3) {
                for (z: Int in this.getPos().getZ() - 2..<this.getPos().getZ() + 3) {
                    val tile: TileEntity? = this.world.getBlockEntity(BlockPos(x, this.getPos().getY(), z))

                    if (tile is TileEntityAdvLandingPadSingle && !tile.isInvalid() && tile.corner == 0) {
                        attachedLaunchPads.add(tile)
                    }
                }
            }

            if (attachedLaunchPads.size == 25) {
                for (tile: TileEntity in attachedLaunchPads) {
                    this.world.markTileEntityForRemoval(tile)
                    (tile as TileEntityAdvLandingPadSingle).corner = 1
                }

                this.world.setBlock(this.getPos(), GSBlocks.ADVANCED_LANDING_PAD.defaultBlockState(), 2)
            }
        }
    }

    override fun shouldRefresh(world: World, pos: BlockPos, oldState: IBlockState, newSate: IBlockState): Boolean {
        return oldState.getBlock() !== newSate.getBlock()
    }
}
