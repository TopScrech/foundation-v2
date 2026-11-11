package micdoodle8.mods.galacticraft.core.blocks

import foundation.block.LegacyBlockMetadata

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.items.IShiftDescription
import micdoodle8.mods.galacticraft.core.tile.TileEntityArclamp
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.RedstoneUtil
import net.minecraft.world.level.block.Block
import net.minecraft.block.ITileEntityProvider
import net.minecraft.world.level.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.core.Direction
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.world.InteractionHand as EnumHand
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World

open class BlockBrightLamp constructor(assetName: String?) : BlockAdvanced(Material.GLASS), IShiftDescription,
    ITileEntityProvider, ISortableBlock, LegacyBlockMetadata {
    //Metadata: bits 0-2 are the side of the base plate using standard side convention (0-5)
    init {
        this.setDefaultState(
            this.blockState.getBaseState().setValue(FACING, EnumFacing.UP)
        ) //.setValue(ACTIVE, true));
        this.setHardness(0.1f)
        this.setSoundType(SoundType.METAL)
        this.setTranslationKey(assetName)
        this.setLightLevel(0.9f)
    }

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        when (state.getValue(FACING)) {
            EnumFacing.EAST -> return EAST_AABB
            EnumFacing.WEST -> return WEST_AABB
            EnumFacing.SOUTH -> return SOUTH_AABB
            EnumFacing.NORTH -> return NORTH_AABB
            EnumFacing.DOWN -> return DOWN_AABB
            EnumFacing.UP -> return UP_AABB
            else -> return UP_AABB
        }
    }

    override fun getLightValue(state: IBlockState, world: IBlockAccess, pos: BlockPos): Int {
        val block: Block = state.getBlock()
        if (block !== this) {
            return block.getLightValue(state)
        }

        /**
         * Gets the light value of the specified block coords. Args: x, y, z
         */
        if (world is World) {
            return if (RedstoneUtil.isBlockReceivingRedstone(world, pos)) 0 else this.lightValue
        }

        return 0
    }

    override fun getLightOpacity(state: IBlockState, world: IBlockAccess, pos: BlockPos): Int {
        return 1
    }

    override fun getCollisionBoundingBox(
        blockState: IBlockState,
        worldIn: IBlockAccess,
        pos: BlockPos
    ): AxisAlignedBB? {
        val boundsMin: Double = 0.2
        val boundsMax: Double = 0.8
        return AxisAlignedBB(
            pos.getX() + boundsMin,
            pos.getY() + boundsMin,
            pos.getZ() + boundsMin,
            pos.getX() + boundsMax,
            pos.getY() + boundsMax,
            pos.getZ() + boundsMax
        )
    }

    override fun isOpaqueCube(state: IBlockState): Boolean {
        return false
    }

    override fun isFullCube(state: IBlockState): Boolean {
        return false
    }

    override fun getBlockFaceShape(
        worldIn: IBlockAccess,
        state: IBlockState,
        pos: BlockPos,
        face: EnumFacing
    ): BlockFaceShape {
        return BlockFaceShape.UNDEFINED
    }

    override fun canPlaceBlockAt(worldIn: World, pos: BlockPos): Boolean {
        for (side: EnumFacing in EnumFacing.values()) {
            val offsetPos: BlockPos = pos.offset(side)
            val state: IBlockState = worldIn.getBlockState(offsetPos)
            if (state.getBlock().isSideSolid(state, worldIn, offsetPos, side.getOpposite())) {
                return true
            }
        }
        return false
    }

    override fun onBlockPlacedBy(
        worldIn: World,
        pos: BlockPos,
        state: IBlockState,
        placer: EntityLivingBase,
        stack: ItemStack
    ) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack)
    }

    override fun getStateForPlacement(
        world: World,
        pos: BlockPos,
        facing: EnumFacing,
        hitX: Float,
        hitY: Float,
        hitZ: Float,
        meta: Int,
        placer: EntityLivingBase,
        hand: EnumHand
    ): IBlockState {
        val opposite: EnumFacing = facing.getOpposite()
        val offsetPos: BlockPos = pos.offset(opposite)
        val state: IBlockState = world.getBlockState(offsetPos)
        if (state.getBlock().isSideSolid(state, world, offsetPos, facing)) {
            return this.defaultBlockState().setValue(FACING, opposite)
        }

        return this.defaultBlockState().setValue(FACING, facing)
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which
     * neighbor changed (coordinates passed are their own) Args: x, y, z,
     * neighbor blockID
     */
    override fun neighborChanged(state: IBlockState, worldIn: World, pos: BlockPos, blockIn: Block, fromPos: BlockPos) {
        val side: EnumFacing = state.getValue(FACING)

        val offsetPos: BlockPos = pos.offset(side)
        val state1: IBlockState = worldIn.getBlockState(offsetPos)
        if (state1.getBlock().isSideSolid(state1, worldIn, offsetPos, EnumFacing.from3DDataValue(side.getIndex() xor 1))) {
            return
        }

        this.dropBlockAsItem(worldIn, pos, state, 0)
        worldIn.setBlockAndUpdate(pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState())
    }

    //    @Override
    //    public RayTraceResult collisionRayTrace(World worldIn, BlockPos pos, Vec3d start, Vec3d end)
    //    {
    //        EnumFacing side = worldIn.getBlockState(pos).getValue(FACING);
    //        float var8 = 0.3F;
    //
    //        if (side == EnumFacing.WEST)
    //        {
    //            this.setBlockBounds(0.0F, 0.2F, 0.5F - var8, var8 * 2.0F, 0.8F, 0.5F + var8);
    //        }
    //        else if (side == EnumFacing.EAST)
    //        {
    //            this.setBlockBounds(1.0F - var8 * 2.0F, 0.2F, 0.5F - var8, 1.0F, 0.8F, 0.5F + var8);
    //        }
    //        else if (side == EnumFacing.NORTH)
    //        {
    //            this.setBlockBounds(0.5F - var8, 0.2F, 0.0F, 0.5F + var8, 0.8F, var8 * 2.0F);
    //        }
    //        else if (side == EnumFacing.SOUTH)
    //        {
    //            this.setBlockBounds(0.5F - var8, 0.2F, 1.0F - var8 * 2.0F, 0.5F + var8, 0.8F, 1.0F);
    //        }
    //        else if (side == EnumFacing.DOWN)
    //        {
    //            this.setBlockBounds(0.5F - var8, 0.0F, 0.5F - var8, 0.5F + var8, 0.6F, 0.5F + var8);
    //        }
    //        else
    //        {
    //            this.setBlockBounds(0.5F - var8, 0.4F, 0.5F - var8, 0.5F + var8, 1.0F, 0.5F + var8);
    //        }
    //
    //        return super.collisionRayTrace(worldIn, pos, start, end);
    //    }
    override fun onUseWrench(
        world: World?,
        pos: BlockPos?,
        entityPlayer: EntityPlayer?,
        hand: EnumHand?,
        heldItem: ItemStack?,
        side: EnumFacing?,
        hitX: Float,
        hitY: Float,
        hitZ: Float
    ): Boolean {
        if (!(world)!!.isClientSide) {
            val tile: TileEntity? = (world)!!.getBlockEntity(pos)
            if (tile is TileEntityArclamp) {
                tile.facingChanged()
            }
        }
        return true
    }

    override fun createNewTileEntity(world: World, meta: Int): TileEntity? {
        return TileEntityArclamp()
    }

    public override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.Companion.galacticraftBlocksTab
    }

    override fun getShiftDescription(meta: Int): String? {
        return GCCoreUtil.translate(this.getTranslationKey() + ".description")
    }

    override fun showDescription(meta: Int): Boolean {
        return true
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        val enumfacing: EnumFacing = EnumFacing.from3DDataValue(meta)
        return this.defaultBlockState().setValue(FACING, enumfacing)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(FACING)).getIndex()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, FACING) //, ACTIVE });
    }

    //    @Override
    //    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    //    {
    //        return state.setValue(ACTIVE, ((TileEntityArclamp) worldIn.getBlockEntity(pos)).getEnabled());
    //    }
    //
    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.MACHINE
    }

    override fun getRenderType(state: IBlockState): EnumBlockRenderType {
        return EnumBlockRenderType.MODEL
    }

    companion object {
        @JvmField
        val FACING: EnumProperty<Direction> = BlockStateProperties.FACING

        //    public static final PropertyBool ACTIVE = PropertyBool.create("active");
        @JvmField
        protected val DOWN_AABB: AxisAlignedBB = AxisAlignedBB(0.2, 0.0, 0.2, 0.8, 0.6, 0.8)

        @JvmField
        protected val UP_AABB: AxisAlignedBB = AxisAlignedBB(0.2, 0.4, 0.2, 0.8, 1.0, 0.8)

        @JvmField
        protected val NORTH_AABB: AxisAlignedBB = AxisAlignedBB(0.2, 0.2, 0.0, 0.8, 0.8, 0.6)

        @JvmField
        protected val SOUTH_AABB: AxisAlignedBB = AxisAlignedBB(0.2, 0.2, 0.4, 0.8, 0.8, 1.0)

        @JvmField
        protected val WEST_AABB: AxisAlignedBB = AxisAlignedBB(0.0, 0.2, 0.2, 0.6, 0.8, 0.8)

        @JvmField
        protected val EAST_AABB: AxisAlignedBB = AxisAlignedBB(0.4, 0.2, 0.2, 1.0, 0.8, 0.8)
    }
}
