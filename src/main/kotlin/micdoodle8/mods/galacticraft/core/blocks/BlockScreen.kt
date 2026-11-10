package micdoodle8.mods.galacticraft.core.blocks

import micdoodle8.mods.galacticraft.api.block.IPartialSealableBlock
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.items.IShiftDescription
import micdoodle8.mods.galacticraft.core.tile.TileEntityScreen
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.block.Block
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyBool
import net.minecraft.block.properties.PropertyDirection
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

open class BlockScreen constructor(assetName: String?) : BlockAdvanced(Material.CIRCUITS), IShiftDescription,
    IPartialSealableBlock, ITileEntityProvider, ISortableBlock {
    //Metadata: 0-5 = direction of screen back;  bit 3 = reserved for future use
    init {
        this.setDefaultState(
            this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH)
                .withProperty(LEFT, false).withProperty(RIGHT, false)
                .withProperty(UP, false).withProperty(DOWN, false)
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
        (worldIn.getTileEntity(pos) as TileEntityScreen).breakScreen(state)
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
        val angle: Int = MathHelper.floor(placer.rotationYaw * 4.0f / 360.0f + 0.5) and 3
        val change: Int = EnumFacing.byHorizontalIndex(angle).getOpposite().getIndex()
        worldIn.setBlockState(pos, getStateFromMeta(change), 3)
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
        (world)!!.setBlockState(pos, this.getStateFromMeta(change), 3)
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
        val tile: TileEntity? = (world)!!.getTileEntity(pos)
        if (tile is TileEntityScreen) {
            tile.changeChannel()
            return true
        }
        return false
    }

    override fun neighborChanged(state: IBlockState, worldIn: World, pos: BlockPos, blockIn: Block, fromPos: BlockPos) {
        val tile: TileEntity? = worldIn.getTileEntity(pos)
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
        val enumfacing: EnumFacing = EnumFacing.byIndex(meta)
        return this.getDefaultState().withProperty(FACING, enumfacing)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(FACING)).getIndex()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, FACING, LEFT, RIGHT, UP, DOWN)
    }

    override fun getActualState(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos): IBlockState {
        val screen: TileEntityScreen? = worldIn.getTileEntity(pos) as TileEntityScreen?
        return state.withProperty(LEFT, screen!!.connectedLeft)
            .withProperty(RIGHT, screen.connectedRight)
            .withProperty(UP, screen.connectedUp)
            .withProperty(DOWN, screen.connectedDown)
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.MACHINE
    }

    companion object {
        @JvmField
        val FACING: PropertyDirection = PropertyDirection.create("facing")

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
