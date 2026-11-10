package micdoodle8.mods.galacticraft.core.tile

import micdoodle8.mods.galacticraft.core.blocks.BlockMulti.EnumBlockMultiType

import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.blocks.BlockMulti
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.LinkedList

open class TileEntitySpaceStationBase constructor() : TileEntityMulti(null), IMultiBlock {
    private var initialised = false

    override fun update() {
        if (!this.initialised) {
            this.initialised = this.initialiseMultiTiles(this.getPos(), this.world)
        }
    }

    override fun onActivated(entityPlayer: EntityPlayer?): Boolean {
        return false
    }

    override fun onCreate(world: World?, placedPosition: BlockPos?) {
        this.mainBlockPosition = placedPosition
        this.markDirty()

        val positions: MutableList<BlockPos> = LinkedList<BlockPos>()
        this.getPositions(placedPosition, (positions as MutableList<BlockPos?>?))
        (GCBlocks.fakeBlock as BlockMulti).makeFakeBlock((world)!!, positions, placedPosition, this.getMultiType())
    }

    override fun getMultiType(): BlockMulti.EnumBlockMultiType {
        return BlockMulti.EnumBlockMultiType.SPACE_STATION_BASE
    }

    override fun getPositions(placedPosition: BlockPos?, positions: MutableList<BlockPos?>?) {
        val buildHeight: Int = this.world.getHeight() - 1

        for (y: Int in 1..2) {
            if ((placedPosition)!!.getY() + y > buildHeight) {
                return
            }
            (positions)!!.add(BlockPos((placedPosition)!!.getX(), (placedPosition)!!.getY() + y, (placedPosition)!!.getZ()))
        }
    }

    override fun onDestroy(callingBlock: TileEntity?) {
        val thisBlock: BlockPos = getPos()
        val positions: MutableList<BlockPos> = LinkedList<BlockPos>()
        this.getPositions(thisBlock, (positions as MutableList<BlockPos?>?))

        for (pos: BlockPos in positions) {
            val stateAt: IBlockState = this.world.getBlockState(pos)

            if (stateAt.getBlock() === GCBlocks.fakeBlock && stateAt.getValue(BlockMulti.MULTI_TYPE) as BlockMulti.EnumBlockMultiType == BlockMulti.EnumBlockMultiType.SPACE_STATION_BASE) {
                this.world.setBlockToAir(pos)
            }
        }
        this.world.destroyBlock(this.getPos(), false)
    }
}
