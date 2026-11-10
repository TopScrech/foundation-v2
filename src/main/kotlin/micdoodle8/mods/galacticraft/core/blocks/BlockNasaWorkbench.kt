package micdoodle8.mods.galacticraft.core.blocks

import micdoodle8.mods.galacticraft.api.block.IPartialSealableBlock
import micdoodle8.mods.galacticraft.api.recipe.SchematicRegistry
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.items.IShiftDescription
import micdoodle8.mods.galacticraft.core.tile.IMultiBlock
import micdoodle8.mods.galacticraft.core.tile.TileEntityNasaWorkbench
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.block.Block
import net.minecraft.block.BlockContainer
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import kotlin.math.abs

open class BlockNasaWorkbench constructor(assetName: String?) : BlockContainer(Material.IRON), ITileEntityProvider,
    IShiftDescription, IPartialSealableBlock, ISortableBlock {
    init {
        //        this.setBlockBounds(-0.3F, 0.0F, -0.3F, 1.3F, 0.5F, 1.3F);
        this.setHardness(2.5f)
        this.setSoundType(SoundType.METAL)
        this.setTranslationKey(assetName)
    }

    public override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftBlocksTab
    }

    override fun getRenderType(state: IBlockState): EnumBlockRenderType {
        return EnumBlockRenderType.MODEL
    }

    override fun isFullCube(state: IBlockState): Boolean {
        return false
    }

    override fun getCollisionBoundingBox(
        blockState: IBlockState,
        worldIn: IBlockAccess,
        pos: BlockPos
    ): AxisAlignedBB? {
        return AxisAlignedBB(
            pos.getX().toDouble() + -0.0f,
            pos.getY().toDouble() + 0.0f,
            pos.getZ().toDouble() + -0.0f,
            pos.getX().toDouble() + 1.0f,
            pos.getY().toDouble() + 1.4f,
            pos.getZ().toDouble() + 1.0f
        )
    }

    //    @SideOnly(Side.CLIENT)
    //    @Override
    //    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos)
    //    {
    //        return this.getCollisionBoundingBox(worldIn, pos, worldIn.getBlockState(pos));
    //    }
    //
    //    @Override
    //    public RayTraceResult collisionRayTrace(World worldIn, BlockPos pos, Vec3d start, Vec3d end)
    //    {
    //        this.setBlockBounds(-0.0F, 0.0F, -0.0F, 1.0F, 1.4F, 1.0F);
    //
    //        final RayTraceResult r = super.collisionRayTrace(worldIn, pos, start, end);
    //
    //        this.setBlockBounds(-0.0F, 0.0F, -0.0F, 1.0F, 1.4F, 1.0F);
    //
    //        return r;
    //    }
    //
    //    @Override
    //    public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity)
    //    {
    //        this.setBlockBounds(-0.0F, 0.0F, -0.0F, 1.0F, 1.4F, 1.0F);
    //        super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
    //    }
    override fun canPlaceBlockAt(worldIn: World, pos: BlockPos): Boolean {
        return true
    }

    override fun onBlockPlacedBy(
        worldIn: World,
        pos: BlockPos,
        state: IBlockState,
        placer: EntityLivingBase,
        stack: ItemStack
    ) {
        BlockMulti.Companion.onPlacement(worldIn, pos, placer, this)
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack)
    }

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        val var9: TileEntity? = worldIn.getTileEntity(pos)

        var fakeBlockCount: Int = 0

        for (x: Int in -1..1) {
            for (y: Int in 0..3) {
                for (z: Int in -1..1) {
                    if (!(x == 0 && y == 0 && z == 0)) {
                        if (abs(x) != 1 || abs(z) != 1) {
                            val block: Block = worldIn.getBlockState(pos.add(x, y, z)).getBlock()

                            if ((y == 0 || y == 3) && x == 0 && z == 0) {
                                if (block === GCBlocks.fakeBlock) {
                                    fakeBlockCount++
                                }
                            } else if (y != 0 && y != 3) {
                                if (block === GCBlocks.fakeBlock) {
                                    fakeBlockCount++
                                }
                            }
                        }
                    }
                }
            }
        }

        if (fakeBlockCount >= 11 && var9 is IMultiBlock) {
            (var9 as IMultiBlock).onDestroy(var9)
        }

        super.breakBlock(worldIn, pos, state)
    }

    override fun onBlockActivated(
        worldIn: World,
        pos: BlockPos,
        state: IBlockState,
        playerIn: EntityPlayer,
        hand: EnumHand,
        side: EnumFacing,
        hitX: Float,
        hitY: Float,
        hitZ: Float
    ): Boolean {
        playerIn.openGui(
            GalacticraftCore.instance,
            (SchematicRegistry.getMatchingRecipeForID(0))!!.getGuiID(),
            worldIn,
            pos.getX(),
            pos.getY(),
            pos.getZ()
        )
        return true
    }

    //    @Override
    //    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos)
    //    {
    //        this.setBlockBounds(-0.0F, 0.0F, -0.0F, 1.0F, 1.4F, 1.0F);
    //    }
    override fun createNewTileEntity(world: World, meta: Int): TileEntity? {
        return TileEntityNasaWorkbench()
    }

    override fun getShiftDescription(meta: Int): String? {
        return GCCoreUtil.translate(this.getTranslationKey() + ".description")
    }

    override fun showDescription(meta: Int): Boolean {
        return true
    }

    override fun isOpaqueCube(state: IBlockState): Boolean {
        return false
    }

    override fun isSealed(worldIn: World?, pos: BlockPos?, direction: EnumFacing?): Boolean {
        return true
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.MACHINE
    }
}
