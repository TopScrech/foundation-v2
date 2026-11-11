package galaxyspace.systems.SolarSystem.planets.overworld.blocks.machines

import foundation.block.LegacyBlockMetadata
import foundation.block.getStateFromMeta

import galaxyspace.GalaxySpace
import asmodeuscore.api.item.IShiftDescription
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityWindGenerator
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.machines.BlockAdvWindGenerator
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.blocks.BlockAdvancedTile
import micdoodle8.mods.galacticraft.core.blocks.BlockMulti
import micdoodle8.mods.galacticraft.core.blocks.ISortableBlock
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseUniversalElectrical
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.core.Direction
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.world.InteractionHand as EnumHand
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth as MathHelper
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World

open class BlockAdvWindGenerator constructor() : BlockAdvancedTile(GCBlocks.machine), IShiftDescription,
    ISortableBlock, LegacyBlockMetadata {
    init {
        this.setHardness(1.0f)
        this.setSoundType(SoundType.METAL)
        this.setTranslationKey("adv_wind_generator")
    }

    override fun getRenderType(state: IBlockState): EnumBlockRenderType {
        return EnumBlockRenderType.MODEL
    }

    override fun isOpaqueCube(state: IBlockState): Boolean {
        return false
    }

    override fun getLightOpacity(state: IBlockState, world: IBlockAccess, pos: BlockPos): Int {
        return 1
    }

    override fun onMachineActivated(
        world: World?,
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
        (playerIn)!!.openGui(GalaxySpace.instance, -1, world, (pos)!!.getX(), (pos)!!.getY(), (pos)!!.getZ())
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
        val change: Int = EnumFacing.byHorizontalIndex(angle).getOpposite().getHorizontalIndex()
        worldIn.setBlock(pos, getStateFromMeta(change), 3)
        BlockMulti.onPlacement(worldIn, pos, placer, this)
    }

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        val var9: TileEntity? = worldIn.getBlockEntity(pos)

        if (var9 is TileEntityWindGenerator) {
            var9.onDestroy(var9)
        }

        super.breakBlock(worldIn, pos, state)
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
        val metadata: Int = getMetaFromState((world)!!.getBlockState(pos))
        val change: Int = (world)!!.getBlockState(pos).getValue(FACING).rotateY().getHorizontalIndex()
        (world)!!.setBlock(pos, this.getStateFromMeta(metadata - (metadata % 4) + change), 3)

        val te: TileEntity? = (world)!!.getBlockEntity(pos)
        if (te is TileBaseUniversalElectrical) {
            te.updateFacing()
        }

        return true
    }

    override fun createNewTileEntity(world: World, meta: Int): TileEntity? {
        return TileEntityWindGenerator(true)
    }

    override fun getShiftDescription(meta: Int): String? {
        return GCCoreUtil.translate("tile.wind_generator.desc")
    }

    override fun getDescription(meta: Int): String? {
        return null
    }

    override fun showDescription(meta: Int): Boolean {
        return true
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.MACHINE
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

    companion object {
        @JvmField
        val FACING: EnumProperty<Direction> = BlockStateProperties.HORIZONTAL_FACING
    }
}
