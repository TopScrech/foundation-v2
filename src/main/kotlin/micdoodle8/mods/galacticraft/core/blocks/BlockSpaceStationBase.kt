package micdoodle8.mods.galacticraft.core.blocks

import micdoodle8.mods.galacticraft.core.tile.IMultiBlock
import micdoodle8.mods.galacticraft.core.tile.TileEntitySpaceStationBase
import net.minecraft.block.BlockContainer
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.core.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.level.Level as World

open class BlockSpaceStationBase constructor(assetName: String?) : BlockContainer(Material.ROCK),
    ITileEntityProvider {
    init {
        this.setHardness(-1f)
        this.setTranslationKey(assetName)
    }

    override fun getBlockHardness(blockState: IBlockState, worldIn: World, pos: BlockPos): Float {
        return -1.0f
    }

    override fun getRenderType(state: IBlockState): EnumBlockRenderType {
        return EnumBlockRenderType.MODEL
    }

    override fun isFullCube(state: IBlockState): Boolean {
        return false
    }

    override fun isOpaqueCube(state: IBlockState): Boolean {
        return false
    }

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        val tileAt: TileEntity? = worldIn.getBlockEntity(pos)

        if (tileAt is IMultiBlock) {
            (tileAt as IMultiBlock).onDestroy(tileAt)
        }

        super.breakBlock(worldIn, pos, state)
    }

    override fun createNewTileEntity(world: World, meta: Int): TileEntity? {
        return TileEntitySpaceStationBase()
    }

    override fun onBlockPlacedBy(
        worldIn: World,
        pos: BlockPos,
        state: IBlockState,
        placer: EntityLivingBase,
        stack: ItemStack
    ) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack)

        val tile: TileEntity? = worldIn.getBlockEntity(pos)

        if (tile is IMultiBlock) {
            (tile as IMultiBlock).onCreate(worldIn, pos)
        }
    }

    override fun getPickBlock(
        state: IBlockState,
        target: RayTraceResult,
        world: World,
        pos: BlockPos,
        player: EntityPlayer
    ): ItemStack {
        return ItemStack.EMPTY
    }
}
