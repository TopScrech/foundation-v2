package micdoodle8.mods.galacticraft.planets.venus.blocks

import foundation.block.LegacyBlockMetadata

import micdoodle8.mods.galacticraft.api.block.IPartialSealableBlock
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.blocks.BlockTileGC
import micdoodle8.mods.galacticraft.core.blocks.ISortableBlock
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseUniversalElectrical
import micdoodle8.mods.galacticraft.core.items.IShiftDescription
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import micdoodle8.mods.galacticraft.planets.GuiIdsPlanets
import micdoodle8.mods.galacticraft.planets.venus.tile.TileEntitySolarArrayController
import net.minecraft.block.ITileEntityProvider
import net.minecraft.world.level.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.core.Direction
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

open class BlockSolarArrayController constructor(assetName: String?) : BlockTileGC(Material.IRON),
    IShiftDescription, IPartialSealableBlock, ISortableBlock, ITileEntityProvider, LegacyBlockMetadata {
    init {
        this.setHardness(1.0f)
        this.setSoundType(SoundType.METAL)
        this.setTranslationKey(assetName)
    }

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        return AABB
    }

    public override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftBlocksTab
    }

    override fun onBlockPlacedBy(
        worldIn: World,
        pos: BlockPos,
        state: IBlockState,
        placer: EntityLivingBase,
        stack: ItemStack
    ) {
        val angle: Int = MathHelper.floor(placer.yRot * 4.0f / 360.0f + 0.5) and 3
        val change: Int = EnumFacing.byHorizontalIndex(angle).getOpposite().getHorizontalIndex()
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
        val state: IBlockState = (world)!!.getBlockState(pos)
        TileBaseUniversalElectrical.onUseWrenchBlock(state, (world)!!, (pos)!!, state.getValue(FACING))
        return true
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
        (entityPlayer)!!.openGui(
            GalacticraftPlanets.instance,
            GuiIdsPlanets.MACHINE_VENUS,
            world,
            (pos)!!.getX(),
            (pos)!!.getY(),
            (pos)!!.getZ()
        )
        return true
    }

    override fun createTileEntity(world: World, state: IBlockState): TileEntity? {
        return TileEntitySolarArrayController()
    }

    override fun isFullCube(state: IBlockState): Boolean {
        return false
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

    override fun getStateFromMeta(meta: Int): IBlockState {
        val enumfacing: EnumFacing = EnumFacing.byHorizontalIndex(meta % 4)
        return this.defaultBlockState().setValue(FACING, enumfacing)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return state.getValue(FACING).getHorizontalIndex()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, FACING)
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.MACHINE
    }

    companion object {
        @JvmField
        val FACING: EnumProperty<Direction> = BlockStateProperties.HORIZONTAL_FACING

        @JvmField
        protected val AABB: AxisAlignedBB = AxisAlignedBB(0.0, 0.25, 0.0, 1.0, 0.78125, 1.0)
    }
}
