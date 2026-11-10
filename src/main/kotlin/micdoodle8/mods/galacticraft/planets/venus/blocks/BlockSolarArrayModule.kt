package micdoodle8.mods.galacticraft.planets.venus.blocks

import com.google.common.collect.Lists
import micdoodle8.mods.galacticraft.api.block.IPartialSealableBlock
import micdoodle8.mods.galacticraft.api.transmission.tile.INetworkConnection
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.blocks.BlockAdvanced
import micdoodle8.mods.galacticraft.core.blocks.ISortableBlock
import micdoodle8.mods.galacticraft.core.items.IShiftDescription
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.planets.venus.tile.TileEntitySolarArrayModule
import net.minecraft.block.Block
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class BlockSolarArrayModule constructor(assetName: String?) : BlockAdvanced(Material.IRON), IShiftDescription,
    IPartialSealableBlock, ISortableBlock, ITileEntityProvider {
    init {
        this.setHardness(2.5f)
        this.setSoundType(SoundType.METAL)
        this.setTranslationKey(assetName)
    }

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        return AABB
    }

    override fun neighborChanged(state: IBlockState, worldIn: World, pos: BlockPos, blockIn: Block, fromPos: BlockPos) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos)

        val tile: TileEntity? = worldIn.getTileEntity(pos)

        if (tile is INetworkConnection) {
            (tile as INetworkConnection).refresh()
        }
    }

    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity? {
        return TileEntitySolarArrayModule()
    }

    override fun onBlockAdded(worldIn: World, pos: BlockPos, state: IBlockState) {
        super.onBlockAdded(worldIn, pos, state)

        if (!worldIn.isRemote) {
//            boolean added = false;
//            for (EnumFacing facing : EnumFacing.HORIZONTALS)
//            {
//                TileEntity tile = worldIn.getTileEntity(pos.offset(facing));
//                if (tile instanceof TileEntitySolarArrayController)
//                {
//                    ((TileEntitySolarArrayController) tile).addArrayModule(pos);
//                    added = true;
//                    break;
//                }
//            }
//            if (!added)
//            {
//
//            }
//            List<TileEntitySolarArrayController> controllers = Lists.newArrayList();
//            for (TileEntity tile : worldIn.loadedTileEntityList)
//            {
//                if (tile instanceof TileEntitySolarArrayController)
//                {
//                    BlockPos diff = tile.getPos().subtract(pos);
//                    if (Math.abs(diff.getX()) <= 16 && Math.abs(diff.getY()) <= 16 && Math.abs(diff.getZ()) <= 16)
//                    {
//                        controllers.add((TileEntitySolarArrayController) tile);
//                    }
//                }
//            }
//            for (TileEntitySolarArrayController controller : controllers)
//            {
//                controller.updateConnected(pos, controllers);
//            }
        }
    }

    @SideOnly(Side.CLIENT)
    public override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftBlocksTab
    }

    override fun getShiftDescription(meta: Int): String? {
        return GCCoreUtil.translate(this.getTranslationKey() + ".description")
    }

    override fun showDescription(meta: Int): Boolean {
        return true
    }

    override fun isSealed(worldIn: World?, pos: BlockPos?, direction: EnumFacing?): Boolean {
        return (direction)!!.getAxis() == EnumFacing.Axis.Y
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.MACHINE
    }

    override fun isOpaqueCube(state: IBlockState): Boolean {
        return false
    }

    override fun isFullCube(state: IBlockState): Boolean {
        return false
    }

    companion object {
        @JvmField
        protected val AABB: AxisAlignedBB = AxisAlignedBB(0.0, 0.375, 0.0, 1.0, 0.625, 1.0)
    }
}
