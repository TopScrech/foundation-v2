package micdoodle8.mods.galacticraft.core.blocks

import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.Container as IInventory
import net.minecraft.inventory.InventoryHelper
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World

/**
 * An advanced block class that is to be extended for wrenching capabilities.
 */
abstract class BlockTileGC(material: Material?) : BlockAdvanced((material)!!), ITileEntityProvider {
    init {
        this.hasTileEntity = true
    }

    /**
     * ejects contained items into the world, and notifies neighbours of an
     * update, as appropriate
     *
     * Note: breakBlock is called when placing blocks
     * getTileEntity() at this point will give the NEW block's tileEntity, if the old block's tile is already invalid
     * so: do NOT invalidate old tileEntities before breaking blocks
     */
    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        if (worldIn.getBlockEntity(pos) is IInventory) {
            InventoryHelper.dropInventoryItems(worldIn, pos, worldIn.getBlockEntity(pos) as IInventory?)
        }
        super.breakBlock(worldIn, pos, state)
    }


    /**
     * Called when the block receives a BlockEvent - see World.addBlockEvent. By
     * default, passes it on to the tile entity at this location. Args: world,
     * x, y, z, blockID, EventID, event parameter
     */
    override fun eventReceived(
        state: IBlockState,
        worldIn: World,
        pos: BlockPos,
        eventID: Int,
        eventParam: Int
    ): Boolean {
        super.eventReceived(state, worldIn, pos, eventID, eventParam)
        val tileentity: TileEntity? = worldIn.getBlockEntity(pos)
        return tileentity != null && tileentity.receiveClientEvent(eventID, eventParam)
    }

    /**
     * Returns the TileEntity used by this block. You should use the metadata
     * sensitive version of this to get the maximum optimization!
     */
    override fun createNewTileEntity(world: World, meta: Int): TileEntity? {
        return null
    }
}
