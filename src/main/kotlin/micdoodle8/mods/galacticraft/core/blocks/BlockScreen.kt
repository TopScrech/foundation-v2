package micdoodle8.mods.galacticraft.core.blocks

import foundation.block.LegacyBlockMetadata
import foundation.block.getStateFromMeta

import micdoodle8.mods.galacticraft.api.block.IPartialSealableBlock
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.items.IShiftDescription
import micdoodle8.mods.galacticraft.core.tile.TileEntityScreen
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.world.level.block.Block
import net.minecraft.block.ITileEntityProvider
import net.minecraft.world.level.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.properties.BooleanProperty as PropertyBool
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
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.world.InteractionHand as EnumHand
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth as MathHelper
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World

open class BlockScreen constructor(assetName: String?) : BlockAdvanced(Material.CIRCUITS), IShiftDescription,
    IPartialSealableBlock, ITileEntityProvider, ISortableBlock, LegacyBlockMetadata {
    //Metadata: 0-5 = direction of screen back;  bit 3 = reserved for future use
    init {
        this.setDefaultState(
            this.blockState.getBaseState().setValue(FACING, EnumFacing.NORTH)
                .setValue(LEFT, false).setValue(RIGHT, false)
                .setValue(UP, false).setValue(DOWN, false)
        )
        this.setHardness(0.1f)
        this.setSoundType(SoundType.GLASS)
        this.setTranslationKey(assetName)
    }

    override fun isSideSolid(
        base_state: IBlockState,
        world: IBlockAccess,
        pos: BlockPos,
        direction: EnumFacing
    ): Boolean {
        return direction.ordinal != getMetaFromState(world.getBlockState(pos))
    }

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        (worldIn.getBlockEntity(pos) as TileEntityScreen).breakScreen(state)
        super.breakBlock(worldIn, pos, state)
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
        return if (face.ordinal == getMetaFromState(state)) BlockFaceShape.UNDEFINED else BlockFaceShape.BOWL
    }

    override fun onBlockPlacedBy(
        worldIn: World,
        pos: BlockPos,
        state: IBlockState,
        placer: EntityLivingBase,
        stack: ItemStack
    ) {
        val angle: Int = MathHelper.floor(placer.yRot * 4.0f / 360.0f + 0.5) and 3
        val change: Int = EnumFacing.byHorizontalIndex(angle).getOpposite().getIndex()
        worldIn.setBlock(pos, getStateFromMeta(change), 3)
    }

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
        val change: Int = (world)!!.getBlockState(pos)
            .getValue(micdoodle8.mods.galacticraft.core.blocks.BlockScreen.Companion.FACING)
            .rotateY().getIndex()
        (world)!!.setBlock(pos, this.getStateFromMeta(change), 3)
        return true
    }

    override fun createNewTileEntity(world: World, meta: Int): TileEntity? {
        return TileEntityScreen()
    }

    public override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftBlocksTab
    }

    override fun onMachineActivated(
        world: World?,
        pos: BlockPos?,
        state: IBlockState?,
        entityPlayer: EntityPlayer?,
        hand: EnumHand?,
        heldItem: ItemStack?,
        side: EnumFacing?,
        hitX: Float,
        hitY: Float,
        hitZ: Float
    ): Boolean {
        val tile: TileEntity? = (world)!!.getBlockEntity(pos)
        if (tile is TileEntityScreen) {
            tile.changeChannel()
            return true
        }
        return false
    }

    override fun neighborChanged(state: IBlockState, worldIn: World, pos: BlockPos, blockIn: Block, fromPos: BlockPos) {
        val tile: TileEntity? = worldIn.getBlockEntity(pos)
        if (tile is TileEntityScreen) {
            tile.refreshConnections(true)
        }
    }

    override fun getShiftDescription(meta: Int): String? {
        return GCCoreUtil.translate(this.getTranslationKey() + ".description")
    }

    override fun showDescription(meta: Int): Boolean {
        return true
    }

    override fun isSealed(worldIn: World?, pos: BlockPos?, direction: EnumFacing?): Boolean {
        return true
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

    override fun getStateFromMeta(meta: Int): IBlockState {
        val enumfacing: EnumFacing = EnumFacing.from3DDataValue(meta)
        return this.defaultBlockState().setValue(FACING, enumfacing)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(FACING)).getIndex()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, FACING, LEFT, RIGHT, UP, DOWN)
    }

    override fun getActualState(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos): IBlockState {
        val screen: TileEntityScreen? = worldIn.getBlockEntity(pos) as TileEntityScreen?
        return state.setValue(LEFT, screen!!.connectedLeft)
            .setValue(RIGHT, screen.connectedRight)
            .setValue(UP, screen.connectedUp)
            .setValue(DOWN, screen.connectedDown)
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.MACHINE
    }

    companion object {
        @JvmField
        val FACING: EnumProperty<Direction> = BlockStateProperties.FACING

        @JvmField
        val LEFT: PropertyBool = PropertyBool.create("left")

        @JvmField
        val RIGHT: PropertyBool = PropertyBool.create("right")

        @JvmField
        val UP: PropertyBool = PropertyBool.create("up")

        @JvmField
        val DOWN: PropertyBool = PropertyBool.create("down")

        protected const val boundsFront: Float = 0.094f

        @JvmField
        protected val boundsBack: Float = 1.0f - boundsFront

        @JvmField
        protected val DOWN_AABB: AxisAlignedBB = AxisAlignedBB(0.0, 0.0, 0.0, 1.0, boundsBack.toDouble(), 1.0)

        @JvmField
        protected val UP_AABB: AxisAlignedBB = AxisAlignedBB(0.0, boundsFront.toDouble(), 0.0, 1.0, 1.0, 1.0)

        @JvmField
        protected val NORTH_AABB: AxisAlignedBB = AxisAlignedBB(0.0, 0.0, boundsFront.toDouble(), 1.0, 1.0, 1.0)

        @JvmField
        protected val SOUTH_AABB: AxisAlignedBB = AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, boundsBack.toDouble())

        @JvmField
        protected val WEST_AABB: AxisAlignedBB = AxisAlignedBB(boundsFront.toDouble(), 0.0, 0.0, 1.0, 1.0, 1.0)

        @JvmField
        protected val EAST_AABB: AxisAlignedBB = AxisAlignedBB(0.0, 0.0, 0.0, boundsBack.toDouble(), 1.0, 1.0)
    }
}
