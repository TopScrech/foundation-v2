package micdoodle8.mods.galacticraft.planets.asteroids.blocks

import net.minecraft.network.chat.Component

import foundation.block.LegacyBlockMetadata

import foundation.level.offset

import micdoodle8.mods.galacticraft.api.transmission.tile.IConductor
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.blocks.BlockTileGC
import micdoodle8.mods.galacticraft.core.blocks.ISortableBlock
import micdoodle8.mods.galacticraft.core.energy.EnergyUtil
import micdoodle8.mods.galacticraft.core.energy.tile.EnergyStorageTile
import micdoodle8.mods.galacticraft.core.items.IShiftDescription
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.planets.asteroids.tile.TileEntityBeamReceiver
import net.minecraft.world.level.block.Block
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
import net.minecraft.core.NonNullList
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraft.core.BlockPos

import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class BlockBeamReceiver constructor(assetName: String?) : BlockTileGC(Material.IRON), IShiftDescription,
    ISortableBlock, LegacyBlockMetadata {
    init {
        this.setTranslationKey(assetName)
        this.setSoundType(SoundType.METAL)
    }

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        when (state.getValue(FACING)) {
            EnumFacing.UP -> return UP_AABB
            EnumFacing.DOWN -> return DOWN_AABB
            EnumFacing.EAST -> return EAST_AABB
            EnumFacing.WEST -> return WEST_AABB
            EnumFacing.SOUTH -> return SOUTH_AABB
            EnumFacing.NORTH -> return NORTH_AABB
            else -> return NORTH_AABB
        }
    }

    @OnlyIn(Dist.CLIENT)
    public override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftBlocksTab
    }

    override fun neighborChanged(state: IBlockState, worldIn: World, pos: BlockPos, blockIn: Block, fromPos: BlockPos) {
        val oldMeta: Int = getMetaFromState(worldIn.getBlockState(pos))
        val meta: Int = this.getMetadataFromAngle(worldIn, pos, EnumFacing.from3DDataValue(oldMeta).getOpposite())

        if (meta == -1) {
            worldIn.destroyBlock(pos, true)
        } else if (meta != oldMeta) {
            worldIn.setBlock(pos, getStateFromMeta(meta), 3)
            val thisTile: TileEntity? = worldIn.getBlockEntity(pos)
            if (thisTile is TileEntityBeamReceiver) {
                val thisReceiver: TileEntityBeamReceiver = thisTile
                thisReceiver.setFacing(EnumFacing.from3DDataValue(meta))
                thisReceiver.invalidateReflector()
                thisReceiver.initiateReflector()
            }
        }

        super.neighborChanged(state, worldIn, pos, blockIn, fromPos)
    }

    override fun onBlockAdded(world: World, pos: BlockPos, state: IBlockState) {
        val thisTile: TileEntity? = world.getBlockEntity(pos)
        if (thisTile is TileEntityBeamReceiver) {
            thisTile.setFacing(EnumFacing.from3DDataValue(getMetaFromState(state)))
        }
    }

    //    @Override
    //    public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos)
    //    {
    //        int meta = getMetaFromState(world.getBlockState(pos));
    //
    //        if (meta != -1)
    //        {
    //            EnumFacing dir = EnumFacing.from3DDataValue(meta);
    //
    //            switch (dir)
    //            {
    //            case UP:
    //                this.setBlockBounds(0.3F, 0.3F, 0.3F, 0.7F, 1.0F, 0.7F);
    //                break;
    //            case DOWN:
    //                this.setBlockBounds(0.2F, 0.0F, 0.2F, 0.8F, 0.42F, 0.8F);
    //                break;
    //            case EAST:
    //                this.setBlockBounds(0.58F, 0.2F, 0.2F, 1.0F, 0.8F, 0.8F);
    //                break;
    //            case WEST:
    //                this.setBlockBounds(0.0F, 0.2F, 0.2F, 0.42F, 0.8F, 0.8F);
    //                break;
    //            case NORTH:
    //                this.setBlockBounds(0.2F, 0.2F, 0.0F, 0.8F, 0.8F, 0.42F);
    //                break;
    //            case SOUTH:
    //                this.setBlockBounds(0.2F, 0.2F, 0.58F, 0.8F, 0.8F, 1.0F);
    //                break;
    //            default:
    //                break;
    //            }
    //        }
    //    }
    //
    //    @Override
    //    public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity)
    //    {
    //        this.setBlockBoundsBasedOnState(worldIn, pos);
    //        super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
    //    }
    private fun getMetadataFromAngle(world: World, pos: BlockPos, side: EnumFacing): Int {
        val direction: EnumFacing = side.getOpposite()

        var tileAt: TileEntity? = world.getBlockEntity(
            pos.offset(
                direction.getStepX(),
                direction.getStepY(),
                direction.getStepZ()
            )
        )

        if (tileAt is EnergyStorageTile) {
            if (tileAt.getModeFromDirection(direction.getOpposite()) != null) {
                return direction.ordinal
            } else {
                return -1
            }
        }

        if (EnergyUtil.otherModCanReceive(tileAt, direction.getOpposite())) {
            return direction.ordinal
        }

        for (adjacentDir: EnumFacing in EnumFacing.values()) {
            if (adjacentDir == direction) {
                continue
            }
            tileAt = world.getBlockEntity(
                pos.offset(
                    adjacentDir.getStepX(),
                    adjacentDir.getStepY(),
                    adjacentDir.getStepZ()
                )
            )

            if (tileAt is IConductor) {
                continue
            }

            if (tileAt is EnergyStorageTile && tileAt.getModeFromDirection(adjacentDir.getOpposite()) != null) {
                return adjacentDir.ordinal
            }

            if (EnergyUtil.otherModCanReceive(tileAt, adjacentDir.getOpposite())) {
                return adjacentDir.ordinal
            }
        }

        return -1
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
        return getStateFromMeta(this.getMetadataFromAngle(world, pos, facing))
    }

    override fun canPlaceBlockOnSide(worldIn: World, pos: BlockPos, side: EnumFacing): Boolean {
        if (this.getMetadataFromAngle(worldIn, pos, side) != -1) {
            return true
        }

        if (worldIn.isClientSide) {
            this.sendIncorrectSideMessage()
        }

        return false
    }

    @OnlyIn(Dist.CLIENT)
    private fun sendIncorrectSideMessage() {
        FMLClientHandler.instance()
            .getClient().player.sendMessage(Component.literal(EnumColor.RED.toString() + GCCoreUtil.translate("gui.receiver.cannot_attach")))
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
        return EnumBlockRenderType.INVISIBLE
    }

    override fun damageDropped(metadata: IBlockState): Int {
        return 0
    }

    override fun createTileEntity(world: World, metadata: IBlockState): TileEntity? {
        return TileEntityBeamReceiver()
    }

    @OnlyIn(Dist.CLIENT)
    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        list.add(ItemStack(this, 1, 0))
    }

    override fun onMachineActivated(
        worldIn: World?,
        pos: BlockPos?,
        state: IBlockState?,
        playerIn: EntityPlayer?,
        hand: EnumHand?,
        heldItem: ItemStack?,
        side: EnumFacing?,
        hitX: Float,
        hitY: Float,
        hitZ: Float
    ): Boolean {
        val tile: TileEntity? = (worldIn)!!.getBlockEntity(pos)

        if (tile is TileEntityBeamReceiver) {
            return tile.onMachineActivated(worldIn, pos, state, playerIn, hand, heldItem, side, hitX, hitY, hitZ)
        }

        return false
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
        return BlockStateContainer(this, FACING)
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.MACHINE
    }

    companion object {
        @JvmField
        val FACING: EnumProperty<Direction> = BlockStateProperties.FACING

        @JvmField
        protected val UP_AABB: AxisAlignedBB = AxisAlignedBB(0.3, 0.3, 0.3, 0.7, 1.0, 0.7)

        @JvmField
        protected val DOWN_AABB: AxisAlignedBB = AxisAlignedBB(0.2, 0.0, 0.2, 0.8, 0.42, 0.8)

        @JvmField
        protected val EAST_AABB: AxisAlignedBB = AxisAlignedBB(0.58, 0.2, 0.2, 1.0, 0.8, 0.8)

        @JvmField
        protected val WEST_AABB: AxisAlignedBB = AxisAlignedBB(0.0, 0.2, 0.2, 0.42, 0.8, 0.8)

        @JvmField
        protected val NORTH_AABB: AxisAlignedBB = AxisAlignedBB(0.2, 0.2, 0.0, 0.8, 0.8, 0.42)

        @JvmField
        protected val SOUTH_AABB: AxisAlignedBB = AxisAlignedBB(0.2, 0.2, 0.58, 0.8, 0.8, 1.0)
    }
}
