package galaxyspace.systems.SolarSystem.planets.overworld.blocks.machines

import foundation.block.LegacyBlockMetadata

import galaxyspace.GalaxySpace
import asmodeuscore.api.item.IShiftDescription
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityAdvOxygenStorageModule
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.machines.BlockOxygenStorageModule
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.blocks.BlockTileGC
import micdoodle8.mods.galacticraft.core.blocks.ISortableBlock
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.properties.IntegerProperty as PropertyInteger
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.world.level.block.state.BlockState as IBlockState
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

open class BlockOxygenStorageModule constructor(tier: Int) : BlockTileGC(GCBlocks.machine), IShiftDescription,
    ISortableBlock, LegacyBlockMetadata {
    private val tier: Int

    init {
        this.setTranslationKey("oxygen_storage_module_" + tier)
        this.setHardness(1.0f)
        this.setSoundType(SoundType.METAL)
        this.tier = tier
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
        //TileBaseUniversalElectrical.onUseWrenchBlock(state, world, pos, state.getValue(FACING));
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
        if (!(world)!!.isClientSide) {
            (entityPlayer)!!.openGui(GalaxySpace.instance, -1, world, (pos)!!.getX(), (pos)!!.getY(), (pos)!!.getZ())
        }

        return true
    }

    override fun createNewTileEntity(world: World, meta: Int): TileEntity? {
        return TileEntityAdvOxygenStorageModule(this.tier)
    }

    override fun getShiftDescription(meta: Int): String? {
        return GCCoreUtil.translate(this.getTranslationKey() + ".desc")
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
        return BlockStateContainer(this, FACING, FILL_VALUE)
    }

    override fun getActualState(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos): IBlockState {
        val tile: TileEntity? = worldIn.getBlockEntity(pos)


        val energyLevel: Int = (tile as TileEntityAdvOxygenStorageModule).scaledOxygenLevel
        return state.setValue(FILL_VALUE, energyLevel)
    }

    companion object {
        @JvmField
        val FACING: EnumProperty<Direction> = BlockStateProperties.HORIZONTAL_FACING

        @JvmField
        val FILL_VALUE: PropertyInteger = PropertyInteger.create("fill_value", 0, 16)
    }
}
