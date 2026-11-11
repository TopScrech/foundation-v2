package micdoodle8.mods.galacticraft.core.blocks

import foundation.block.LegacyBlockMetadata

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseUniversalElectrical
import micdoodle8.mods.galacticraft.core.items.IShiftDescription
import micdoodle8.mods.galacticraft.core.tile.IMachineSides
import micdoodle8.mods.galacticraft.core.tile.IMachineSidesProperties
import micdoodle8.mods.galacticraft.core.tile.TileEntityFuelLoader
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.WorldUtil
import net.minecraft.world.level.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.properties.EnumProperty as PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.world.InteractionHand as EnumHand
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth as MathHelper
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World

open class BlockFuelLoader constructor(assetName: String?) : BlockAdvancedTile(Material.ROCK), IShiftDescription,
    ISortableBlock, LegacyBlockMetadata {
    init {
        this.setHardness(1.0f)
        this.setSoundType(SoundType.METAL)
        this.setTranslationKey(assetName)
    }

    public override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.Companion.galacticraftBlocksTab
    }

    override fun createTileEntity(world: World, state: IBlockState): TileEntity? {
        return TileEntityFuelLoader()
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
        (entityPlayer)!!.openGui(GalacticraftCore.Companion.instance, -1, world, (pos)!!.getX(), (pos)!!.getY(), (pos)!!.getZ())
        return true
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

    override fun onBlockPlacedBy(
        worldIn: World,
        pos: BlockPos,
        state: IBlockState,
        placer: EntityLivingBase,
        stack: ItemStack
    ) {
        val angle: Int = MathHelper.floor(placer.yRot * 4.0f / 360.0f + 0.5) and 3
        worldIn.setBlock(
            pos,
            getStateFromMeta(EnumFacing.byHorizontalIndex(angle).getOpposite().getHorizontalIndex()),
            3
        )
        WorldUtil.markAdjacentPadForUpdate(worldIn, pos)
    }

    public override fun onPlayerDestroy(worldIn: World?, pos: BlockPos?, state: IBlockState?) {
        super.onPlayerDestroy(worldIn, pos, state)
        WorldUtil.markAdjacentPadForUpdate((worldIn)!!, (pos)!!)
    }

    override fun getShiftDescription(meta: Int): String? {
        return GCCoreUtil.translate(this.getTranslationKey() + ".description")
    }

    override fun showDescription(meta: Int): Boolean {
        return true
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        val enumfacing: EnumFacing = EnumFacing.byHorizontalIndex(meta)
        return this.defaultBlockState().setValue(FACING, enumfacing)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(FACING) as EnumFacing).getHorizontalIndex()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, FACING, SIDES)
    }

    override fun getActualState(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos): IBlockState {
        val tile: TileEntity? = worldIn.getBlockEntity(pos)

        return IMachineSides.addPropertyForTile(state, tile, MACHINESIDES_RENDERTYPE, (SIDES)!!)
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.MACHINE
    }

    override fun onSneakUseWrench(
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
        val tile: TileEntity? = (world)!!.getBlockEntity(pos)
        if (tile is IMachineSides) {
            (tile as IMachineSides).nextSideConfiguration(tile)
            return true
        }
        return false
    }

    companion object {
        @JvmField
        val FACING: EnumProperty<Direction> = BlockStateProperties.HORIZONTAL_FACING

        @JvmField
        var MACHINESIDES_RENDERTYPE: IMachineSidesProperties = IMachineSidesProperties.TWOFACES_HORIZ

        @JvmField
        val SIDES: PropertyEnum<IMachineSidesProperties.MachineSidesModel>? = MACHINESIDES_RENDERTYPE.asProperty
    }
}
