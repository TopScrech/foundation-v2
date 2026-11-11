package micdoodle8.mods.galacticraft.planets.asteroids.tile

import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.util.ITickable
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import java.util.ArrayList

open class TileEntityMinerBaseSingle : TileEntity(), ITickable {
    private var corner = 0

    override fun update() {
        if (!this.world.isRemote && this.corner == 0) {
            val attachedBaseBlocks: ArrayList<TileEntity> = ArrayList<TileEntity>()

            val thisX: Int = this.getPos().getX()
            val thisY: Int = this.getPos().getY()
            val thisZ: Int = this.getPos().getZ()

            var success: Boolean = true
            SEARCH@ for (x: Int in 0..1) {
                for (y: Int in 0..1) {
                    for (z: Int in 0..1) {
                        val pos: BlockPos = BlockPos(x + thisX, y + thisY, z + thisZ)
                        val tile: TileEntity? =
                            if (this.world.isBlockLoaded(pos, false)) this.world.getBlockEntity(pos) else null

                        if (tile is TileEntityMinerBaseSingle && !tile.isInvalid() && tile.corner == 0) {
                            attachedBaseBlocks.add(tile)
                        } else {
                            success = false
                            break@SEARCH
                        }
                    }
                }
            }

            if (success) {
                TileEntityMinerBase.Companion.addNewMinerBase(GCCoreUtil.getDimensionID(this), this.getPos())
                for (tile: TileEntity in attachedBaseBlocks) {
                    (tile as TileEntityMinerBaseSingle).corner = 1
                    this.world.setBlockAndUpdate(this.getPos(), net.minecraft.world.level.block.Blocks.AIR.defaultBlockState())
                }
                //Don't try setting a new block with a TileEntity, because new tiles can
                //get removed after the end of this tileEntity.update() tick - setting a new block
                //here would automatically invalidate this tile.
                //
                //(It's because if this tileEntity is now invalid, World.updateEntities() removes
                // *any* tileEntity at this position - see call to Chunk.removeTileEntity(pos)nee!)
                //
                //Equally if any others of these TileEntityMinerBaseSingle are ticked AFTER this
                //in the same server update tick, then those new ones will also be removed
                //because their TileEntityMinerBaseSingle is now, inevitably, invalid!
            }
        }
    }

    override fun shouldRefresh(world: World, pos: BlockPos, oldState: IBlockState, newSate: IBlockState): Boolean {
        return oldState.getBlock() !== newSate.getBlock()
    }
}
