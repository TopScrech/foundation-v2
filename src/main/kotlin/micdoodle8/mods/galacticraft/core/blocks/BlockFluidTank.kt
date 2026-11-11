package micdoodle8.mods.galacticraft.core.blocks

import foundation.block.LegacyBlockMetadata

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.items.IShiftDescription
import micdoodle8.mods.galacticraft.core.tile.TileEntityFluidTank
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.FluidUtil
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.world.level.block.Block
import net.minecraft.block.ITileEntityProvider
import net.minecraft.world.level.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.properties.BooleanProperty as PropertyBool
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.util.BlockRenderLayer
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.world.InteractionHand as EnumHand
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import net.minecraftforge.fluids.FluidActionResult
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class BlockFluidTank constructor(assetName: String?) : Block(Material.GLASS), IShiftDescription,
    ISortableBlock, ITileEntityProvider, LegacyBlockMetadata {
    init {
        this.setHardness(3.0f)
        this.setResistance(8.0f)
        this.setSoundType(SoundType.GLASS)
        this.setTranslationKey(assetName)
    }

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        return BOUNDS
    }

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        val tile: TileEntity? = worldIn.getBlockEntity(pos)
        if (tile is TileEntityFluidTank) {
            val tank: TileEntityFluidTank = tile
            tank.onBreak()
        }
        super.breakBlock(worldIn, pos, state)
    }

    public override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.Companion.galacticraftBlocksTab
    }

    @OnlyIn(Dist.CLIENT)
    public override fun getRenderLayer(): BlockRenderLayer {
        return BlockRenderLayer.CUTOUT
    }

    override fun isFullCube(state: IBlockState): Boolean {
        return false
    }

    override fun isOpaqueCube(state: IBlockState): Boolean {
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

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.GENERAL
    }

    override fun getShiftDescription(meta: Int): String? {
        return GCCoreUtil.translate(this.getTranslationKey() + ".description")
    }

    override fun showDescription(meta: Int): Boolean {
        return true
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, UP, DOWN)
    }

    override fun getActualState(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos): IBlockState {
        val stateAbove: IBlockState = worldIn.getBlockState(pos.above())
        val stateBelow: IBlockState = worldIn.getBlockState(pos.below())
        return state.setValue(UP, stateAbove.getBlock() === this)
            .setValue(DOWN, stateBelow.getBlock() === this)
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.defaultBlockState()
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return 0
    }

    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity? {
        return TileEntityFluidTank()
    }

    //    @Override
    //    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos)
    //    {
    //        this.setBlockBounds((float) BOUNDS.minX, (float) BOUNDS.minY, (float) BOUNDS.minZ, (float) BOUNDS.maxX, (float) BOUNDS.maxY, (float) BOUNDS.maxZ);
    //    }
    //
    //    @Override
    //    public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity)
    //    {
    //        this.setBlockBoundsBasedOnState(worldIn, pos);
    //        super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
    //    }
    //
    //    @Override
    //    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    //    {
    //        this.setBlockBoundsBasedOnState(worldIn, pos);
    //        return super.getCollisionBoundingBox(worldIn, pos, state);
    //    }
    //
    //    @Override
    //    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos)
    //    {
    //        this.setBlockBoundsBasedOnState(worldIn, pos);
    //        return super.getSelectedBoundingBox(worldIn, pos);
    //    }
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
        if (super.onBlockActivated(worldIn, pos, state, playerIn, hand, side, hitX, hitY, hitZ)) {
            return true
        }

        if (hand == EnumHand.OFF_HAND) {
            return false
        }

        val current: ItemStack = playerIn.inventory.getSelectedItem()
        val slot: Int = playerIn.inventory.selectedSlot

        if (!current.isEmpty()) {
            val tile: TileEntity? = worldIn.getBlockEntity(pos)

            if (tile is TileEntityFluidTank) {
                val tank: TileEntityFluidTank = tile

                val forgeResult: FluidActionResult = FluidUtil.interactWithFluidHandler(
                    current,
                    tank.getCapability<IFluidHandler?>(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null),
                    playerIn
                )
                if (forgeResult.isSuccess()) {
                    playerIn.inventory.setItem(slot, forgeResult.result)
                    if (playerIn.inventoryContainer != null) {
                        playerIn.inventoryContainer.detectAndSendChanges()
                    }
                    return true
                }

                return false
            }
        }

        return false
    }

    override fun getLightValue(state: IBlockState, world: IBlockAccess, pos: BlockPos): Int {
        val tile: TileEntity? = world.getBlockEntity(pos)

        if (tile is TileEntityFluidTank) {
            val tank: TileEntityFluidTank = tile
            return if ((tank.fluidTank)!!.getFluid() == null || (tank.fluidTank)!!.getFluid()!!.amount == 0) 0 else (tank.fluidTank)!!.getFluid()!!
                .getFluid().getLuminosity((tank.fluidTank)!!.getFluid())
        }

        return 0
    }

    companion object {
        @JvmField
        val UP: PropertyBool = PropertyBool.create("up")

        @JvmField
        val DOWN: PropertyBool = PropertyBool.create("down")
        private val BOUNDS = AxisAlignedBB(0.05, 0.0, 0.05, 0.95, 1.0, 0.95)
    }
}
