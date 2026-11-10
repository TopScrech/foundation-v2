package micdoodle8.mods.galacticraft.core.blocks

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.items.IShiftDescription
import micdoodle8.mods.galacticraft.core.tile.TileEntityTreasureChest
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.block.Block
import net.minecraft.block.BlockContainer
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyDirection
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.particle.ParticleManager
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.passive.EntityOcelot
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.InventoryHelper
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

open class BlockTier1TreasureChest constructor(assetName: String?) : BlockContainer(Material.WOOD),
    ITileEntityProvider, IShiftDescription, ISortableBlock {
    init {
        this.setSoundType(SoundType.WOOD)
        this.setDefaultState(
            this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH)
        )
        //        this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
        this.setHardness(100000.0f)
        this.setTranslationKey(assetName)
    }

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        return AABB
    }

    public override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftBlocksTab
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

    override fun getRenderType(state: IBlockState): EnumBlockRenderType {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED
    }

    //    @Override
    //    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos)
    //    {
    //        if (worldIn.getBlockState(pos.north()).getBlock() == this)
    //        {
    //            this.setBlockBounds(0.0625F, 0.0F, 0.0F, 0.9375F, 0.875F, 0.9375F);
    //        }
    //        else if (worldIn.getBlockState(pos.south()).getBlock() == this)
    //        {
    //            this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 1.0F);
    //        }
    //        else if (worldIn.getBlockState(pos.west()).getBlock() == this)
    //        {
    //            this.setBlockBounds(0.0F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
    //        }
    //        else if (worldIn.getBlockState(pos.east()).getBlock() == this)
    //        {
    //            this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 1.0F, 0.875F, 0.9375F);
    //        }
    //        else
    //        {
    //            this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
    //        }
    //    }
    override fun onBlockAdded(worldIn: World, pos: BlockPos, state: IBlockState) {
        this.checkForSurroundingChests(worldIn, pos, state)
        val iterator: MutableIterator<*> = EnumFacing.Plane.HORIZONTAL.iterator()

        while (iterator.hasNext()) {
            val enumfacing: EnumFacing = iterator.next() as EnumFacing
            val blockpos1: BlockPos = pos.offset(enumfacing)
            val iblockstate1: IBlockState = worldIn.getBlockState(blockpos1)

            if (iblockstate1.getBlock() === this) {
                this.checkForSurroundingChests(worldIn, blockpos1, iblockstate1)
            }
        }
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
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing())
    }

    override fun onBlockPlacedBy(
        worldIn: World,
        pos: BlockPos,
        state: IBlockState,
        placer: EntityLivingBase,
        stack: ItemStack
    ) {
        var state: IBlockState = state
        val enumfacing: EnumFacing =
            EnumFacing.byHorizontalIndex(MathHelper.floor((placer.rotationYaw * 4.0f / 360.0f).toDouble() + 0.5) and 3)
                .getOpposite()
        state = state.withProperty(FACING, enumfacing)
        val blockpos1: BlockPos = pos.north()
        val blockpos2: BlockPos = pos.south()
        val blockpos3: BlockPos = pos.west()
        val blockpos4: BlockPos = pos.east()
        val flag: Boolean = this === worldIn.getBlockState(blockpos1).getBlock()
        val flag1: Boolean = this === worldIn.getBlockState(blockpos2).getBlock()
        val flag2: Boolean = this === worldIn.getBlockState(blockpos3).getBlock()
        val flag3: Boolean = this === worldIn.getBlockState(blockpos4).getBlock()

        if (!flag && !flag1 && !flag2 && !flag3) {
            worldIn.setBlockState(pos, state, 3)
        } else if (enumfacing.getAxis() == EnumFacing.Axis.X && (flag || flag1)) {
            if (flag) {
                worldIn.setBlockState(blockpos1, state, 3)
            } else {
                worldIn.setBlockState(blockpos2, state, 3)
            }

            worldIn.setBlockState(pos, state, 3)
        } else if (enumfacing.getAxis() == EnumFacing.Axis.Z && (flag2 || flag3)) {
            if (flag2) {
                worldIn.setBlockState(blockpos3, state, 3)
            } else {
                worldIn.setBlockState(blockpos4, state, 3)
            }

            worldIn.setBlockState(pos, state, 3)
        }

        if (stack.hasDisplayName()) {
            val tileentity: TileEntity? = worldIn.getTileEntity(pos)

            if (tileentity is TileEntityTreasureChest) {
                tileentity.setCustomName(stack.getDisplayName())
            }
        }
    }

    open fun checkForSurroundingChests(worldIn: World, pos: BlockPos, state: IBlockState): IBlockState? {
        var state: IBlockState = state
        if (worldIn.isRemote) {
            return state
        } else {
            val iblockstate1: IBlockState = worldIn.getBlockState(pos.north())
            val iblockstate2: IBlockState = worldIn.getBlockState(pos.south())
            val iblockstate3: IBlockState = worldIn.getBlockState(pos.west())
            val iblockstate4: IBlockState = worldIn.getBlockState(pos.east())
            var enumfacing: EnumFacing = state.getValue(FACING) as EnumFacing
            val block: Block = iblockstate1.getBlock()
            val block1: Block = iblockstate2.getBlock()
            val block2: Block = iblockstate3.getBlock()
            val block3: Block = iblockstate4.getBlock()

            if (block !== this && block1 !== this) {
                val flag: Boolean = block.isFullBlock(iblockstate1)
                val flag1: Boolean = block1.isFullBlock(iblockstate2)

                if (block2 === this || block3 === this) {
                    val blockpos2: BlockPos = if (block2 === this) pos.west() else pos.east()
                    val iblockstate7: IBlockState = worldIn.getBlockState(blockpos2.north())
                    val iblockstate8: IBlockState = worldIn.getBlockState(blockpos2.south())
                    enumfacing = EnumFacing.SOUTH
                    val enumfacing2: EnumFacing

                    if (block2 === this) {
                        enumfacing2 = iblockstate3.getValue(FACING) as EnumFacing
                    } else {
                        enumfacing2 = iblockstate4.getValue(FACING) as EnumFacing
                    }

                    if (enumfacing2 == EnumFacing.NORTH) {
                        enumfacing = EnumFacing.NORTH
                    }

                    val block6: Block = iblockstate7.getBlock()
                    val block7: Block = iblockstate8.getBlock()

                    if ((flag || block6.isFullBlock(iblockstate7)) && !flag1 && !block7.isFullBlock(iblockstate8)) {
                        enumfacing = EnumFacing.SOUTH
                    }

                    if ((flag1 || block7.isFullBlock(iblockstate8)) && !flag && !block6.isFullBlock(iblockstate7)) {
                        enumfacing = EnumFacing.NORTH
                    }
                }
            } else {
                val blockpos1: BlockPos = if (block === this) pos.north() else pos.south()
                val iblockstate5: IBlockState = worldIn.getBlockState(blockpos1.west())
                val iblockstate6: IBlockState = worldIn.getBlockState(blockpos1.east())
                enumfacing = EnumFacing.EAST
                val enumfacing1: EnumFacing

                if (block === this) {
                    enumfacing1 = iblockstate1.getValue(FACING) as EnumFacing
                } else {
                    enumfacing1 = iblockstate2.getValue(FACING) as EnumFacing
                }

                if (enumfacing1 == EnumFacing.WEST) {
                    enumfacing = EnumFacing.WEST
                }

                val block4: Block = iblockstate5.getBlock()
                val block5: Block = iblockstate6.getBlock()

                if ((block2.isFullBlock(iblockstate3) || block4.isFullBlock(iblockstate5)) && !block3.isFullBlock(
                        iblockstate4
                    ) && !block5.isFullBlock(iblockstate6)
                ) {
                    enumfacing = EnumFacing.EAST
                }

                if ((block3.isFullBlock(iblockstate4) || block5.isFullBlock(iblockstate6)) && !block2.isFullBlock(
                        iblockstate3
                    ) && !block4.isFullBlock(iblockstate5)
                ) {
                    enumfacing = EnumFacing.WEST
                }
            }

            state = state.withProperty(FACING, enumfacing)
            worldIn.setBlockState(pos, state, 3)
            return state
        }
    }

    open fun correctFacing(worldIn: World, pos: BlockPos, state: IBlockState): IBlockState? {
        var enumfacing: EnumFacing? = null
        val iterator: MutableIterator<*> = EnumFacing.Plane.HORIZONTAL.iterator()

        while (iterator.hasNext()) {
            val enumfacing1: EnumFacing = iterator.next() as EnumFacing
            val iblockstate1: IBlockState = worldIn.getBlockState(pos.offset(enumfacing1))

            if (iblockstate1.getBlock() === this) {
                return state
            }

            if (iblockstate1.getBlock().isFullBlock(iblockstate1)) {
                if (enumfacing != null) {
                    enumfacing = null
                    break
                }

                enumfacing = enumfacing1
            }
        }

        if (enumfacing != null) {
            return state.withProperty(FACING, enumfacing.getOpposite())
        } else {
            var enumfacing2: EnumFacing = state.getValue(FACING) as EnumFacing
            var state1: IBlockState = worldIn.getBlockState(pos.offset(enumfacing2))

            if (state1.getBlock().isFullBlock(state1)) {
                enumfacing2 = enumfacing2.getOpposite()
            }

            state1 = worldIn.getBlockState(pos.offset(enumfacing2))
            if (state1.getBlock().isFullBlock(state1)) {
                enumfacing2 = enumfacing2.rotateY()
            }

            state1 = worldIn.getBlockState(pos.offset(enumfacing2))
            if (state1.getBlock().isFullBlock(state1)) {
                enumfacing2 = enumfacing2.getOpposite()
            }

            return state.withProperty(FACING, enumfacing2)
        }
    }

    override fun canPlaceBlockAt(worldIn: World, pos: BlockPos): Boolean {
        var i: Int = 0
        val blockpos1: BlockPos = pos.west()
        val blockpos2: BlockPos = pos.east()
        val blockpos3: BlockPos = pos.north()
        val blockpos4: BlockPos = pos.south()

        if (worldIn.getBlockState(blockpos1).getBlock() === this) {
            if (this.isDoubleChest(worldIn, blockpos1)) {
                return false
            }

            ++i
        }

        if (worldIn.getBlockState(blockpos2).getBlock() === this) {
            if (this.isDoubleChest(worldIn, blockpos2)) {
                return false
            }

            ++i
        }

        if (worldIn.getBlockState(blockpos3).getBlock() === this) {
            if (this.isDoubleChest(worldIn, blockpos3)) {
                return false
            }

            ++i
        }

        if (worldIn.getBlockState(blockpos4).getBlock() === this) {
            if (this.isDoubleChest(worldIn, blockpos4)) {
                return false
            }

            ++i
        }

        return i <= 1
    }

    private fun isDoubleChest(worldIn: World, pos: BlockPos): Boolean {
        if (worldIn.getBlockState(pos).getBlock() !== this) {
            return false
        } else {
            val iterator: MutableIterator<*> = EnumFacing.Plane.HORIZONTAL.iterator()
            var enumfacing: EnumFacing

            do {
                if (!iterator.hasNext()) {
                    return false
                }

                enumfacing = iterator.next() as EnumFacing
            } while (worldIn.getBlockState(pos.offset(enumfacing)).getBlock() !== this)

            return true
        }
    }

    override fun neighborChanged(state: IBlockState, worldIn: World, pos: BlockPos, blockIn: Block, fromPos: BlockPos) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos)
        val tileentity: TileEntity? = worldIn.getTileEntity(pos)

        if (tileentity is TileEntityTreasureChest) {
            tileentity.updateContainingBlockInfo()
        }
    }

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        val tileentity: TileEntity? = worldIn.getTileEntity(pos)

        if (tileentity is IInventory) {
            InventoryHelper.dropInventoryItems(worldIn, pos, tileentity as IInventory)
            worldIn.updateComparatorOutputLevel(pos, this)
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
        if (worldIn.isRemote) {
            return true
        } else {
            val tile: TileEntity? = worldIn.getTileEntity(pos)
            playerIn.displayGUIChest(tile as IInventory?)
            return true
        }
    }

    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity? {
        return TileEntityTreasureChest()
    }

    open fun isProvidingWeakPower(worldIn: IBlockAccess, pos: BlockPos, state: IBlockState, side: EnumFacing?): Int {
        if (!this.canProvidePower(state)) {
            return 0
        } else {
            var i: Int = 0
            val tileentity: TileEntity? = worldIn.getTileEntity(pos)

            if (tileentity is TileEntityTreasureChest) {
                i = tileentity.numPlayersUsing
            }

            return MathHelper.clamp(i, 0, 15)
        }
    }

    open fun isProvidingStrongPower(worldIn: IBlockAccess, pos: BlockPos, state: IBlockState, side: EnumFacing?): Int {
        return if (side == EnumFacing.UP) this.isProvidingWeakPower(worldIn, pos, state, side) else 0
    }

    private fun isBlocked(worldIn: World, pos: BlockPos): Boolean {
        return this.isBelowSolidBlock(worldIn, pos) || this.isOcelotSittingOnChest(worldIn, pos)
    }

    private fun isBelowSolidBlock(worldIn: World, pos: BlockPos): Boolean {
        return worldIn.isSideSolid(pos.up(), EnumFacing.DOWN, false)
    }

    private fun isOcelotSittingOnChest(worldIn: World, pos: BlockPos): Boolean {
        val iterator: MutableIterator<*> = worldIn.getEntitiesWithinAABB<EntityOcelot?>(
            EntityOcelot::class.java,
            AxisAlignedBB(
                pos.getX().toDouble(),
                (pos.getY() + 1).toDouble(),
                pos.getZ().toDouble(),
                (pos.getX() + 1).toDouble(),
                (pos.getY() + 2).toDouble(),
                (pos.getZ() + 1).toDouble()
            )
        ).iterator()
        var entityocelot: EntityOcelot

        do {
            if (!iterator.hasNext()) {
                return false
            }

            val entity: Entity = iterator.next() as Entity
            entityocelot = entity as EntityOcelot
        } while (!entityocelot.isSitting())

        return true
    }

    override fun hasComparatorInputOverride(state: IBlockState): Boolean {
        return true
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        var enumfacing: EnumFacing = EnumFacing.byIndex(meta)

        if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
            enumfacing = EnumFacing.NORTH
        }

        return this.getDefaultState().withProperty(FACING, enumfacing)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(FACING)).getIndex()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, FACING)
    }

    override fun getShiftDescription(meta: Int): String? {
        return GCCoreUtil.translate(this.getTranslationKey() + ".description")
    }

    override fun showDescription(meta: Int): Boolean {
        return true
    }

    override fun addDestroyEffects(world: World, pos: BlockPos, manager: ParticleManager): Boolean {
        return false
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.TREASURE
    }

    companion object {
        @JvmField
        val FACING: PropertyDirection = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL)

        @JvmField
        protected val AABB: AxisAlignedBB = AxisAlignedBB(0.0625, 0.0, 0.0625, 0.9375, 0.875, 0.9375)
    }
}
