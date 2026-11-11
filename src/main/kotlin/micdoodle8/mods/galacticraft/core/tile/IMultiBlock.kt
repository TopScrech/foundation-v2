package micdoodle8.mods.galacticraft.core.tile

import micdoodle8.mods.galacticraft.core.blocks.BlockMulti
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.util.ITickable
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World

interface IMultiBlock : ITickable {
    /**
     * Called when activated
     */
    fun onActivated(entityPlayer: EntityPlayer?): Boolean

    /**
     * Called when this multiblock is created
     *
     * @param placedPosition - The position the block was placed at
     */
    fun onCreate(world: World?, placedPosition: BlockPos?)

    /**
     * Called when one of the multiblocks of this block is destroyed
     *
     * @param callingBlock - The tile entity who called the onDestroy function
     */
    fun onDestroy(callingBlock: TileEntity?)

    fun getPositions(placedPosition: BlockPos?, positions: MutableList<BlockPos?>?)

    fun getMultiType(): BlockMulti.EnumBlockMultiType?
}
