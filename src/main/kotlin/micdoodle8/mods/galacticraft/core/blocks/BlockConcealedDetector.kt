package micdoodle8.mods.galacticraft.core.blocks

import foundation.block.LegacyBlockMetadata

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.tile.TileEntityPlayerDetector
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.RedstoneUtil
import net.minecraft.world.level.block.Block
import net.minecraft.block.ITileEntityProvider
import net.minecraft.world.level.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.properties.Property as IProperty
import net.minecraft.world.level.block.state.properties.BooleanProperty as PropertyBool
import net.minecraft.world.level.block.state.properties.IntegerProperty as PropertyInteger
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import java.util.Random

open class BlockConcealedDetector constructor(assetName: String?) : Block(Material.IRON), ISortableBlock,
    ITileEntityProvider, LegacyBlockMetadata {
    init {
        this.setHardness(1.0f)
        this.setSoundType(SoundType.METAL)
        this.blockResistance = 15f
        this.setTranslationKey(assetName)
        this.setDefaultState(
            this.blockState.getBaseState().setValue(FACING, 0).setValue(VARIANT, 0)
                .setValue(DETECTED, false)
        )
    }

    public override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.Companion.galacticraftBlocksTab
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.DECORATION
    }

    override fun getStateForPlacement(
        worldIn: World,
        pos: BlockPos,
        facing: EnumFacing,
        hitX: Float,
        hitY: Float,
        hitZ: Float,
        damage: Int,
        placer: EntityLivingBase
    ): IBlockState {
        return this.defaultBlockState()
            .setValue(FACING, placer.getHorizontalFacing().getHorizontalIndex())
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        val facing: Int = meta and 3
        val `var`: Int = (meta shr 2) and 1
        return this.defaultBlockState().setValue(FACING, facing).setValue(VARIANT, `var`)
            .setValue(DETECTED, meta >= 8)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return state.getValue(FACING) + state.getValue(VARIANT) * 4 + (if (state.getValue(
                DETECTED
            )
        ) 8 else 0)
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, *arrayOf<IProperty<*>>(FACING, VARIANT, DETECTED))
    }

    override fun getLightOpacity(state: IBlockState): Int {
        return 0
    }

    override fun isOpaqueCube(state: IBlockState): Boolean {
        return true
    }

    override fun isFullCube(state: IBlockState): Boolean {
        return true
    }

    override fun isSideSolid(state: IBlockState, world: IBlockAccess, pos: BlockPos, side: EnumFacing): Boolean {
        return true
    }

    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity? {
        return TileEntityPlayerDetector()
    }

    override fun canProvidePower(state: IBlockState): Boolean {
        return true
    }

    override fun getWeakPower(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos, side: EnumFacing): Int {
        if (worldIn is World && RedstoneUtil.isBlockReceivingDirectRedstone(worldIn, pos)) return 0

        return if (worldIn.getBlockState(pos).getValue(DETECTED)) 0 else 15
    }

    open fun updateState(worldObj: World, pos: BlockPos, result: Boolean) {
        val bs: IBlockState = worldObj.getBlockState(pos)
        if (result != bs.getValue(DETECTED) as Boolean) {
            worldObj.setBlock(pos, bs.setValue(DETECTED, result), 3)
        }
    }

    override fun quantityDropped(random: Random): Int {
        return 0
    }

    override fun canSilkHarvest(): Boolean {
        return false
    }

    companion object {
        @JvmField
        val VARIANT: PropertyInteger = PropertyInteger.create("var", 0, 1)

        @JvmField
        val FACING: PropertyInteger = PropertyInteger.create("facing", 0, 3)

        @JvmField
        val DETECTED: PropertyBool = PropertyBool.create("det")
    }
}
