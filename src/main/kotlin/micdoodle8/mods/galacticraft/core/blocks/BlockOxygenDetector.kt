package micdoodle8.mods.galacticraft.core.blocks

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.items.IShiftDescription
import micdoodle8.mods.galacticraft.core.tile.TileEntityOxygenDetector
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.block.BlockContainer
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyBool
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

open class BlockOxygenDetector constructor(assetName: String?) : BlockContainer(Material.IRON),
    ITileEntityProvider, IShiftDescription, ISortableBlock {
    init {
        this.setHardness(1.0f)
        this.setSoundType(SoundType.METAL)
        this.setTranslationKey(assetName)
    }

    override fun getRenderType(state: IBlockState): EnumBlockRenderType {
        return EnumBlockRenderType.MODEL
    }

    public override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftBlocksTab
    }

    override fun createNewTileEntity(world: World, meta: Int): TileEntity? {
        return TileEntityOxygenDetector()
    }

    open fun updateOxygenState(worldIn: World, pos: BlockPos, valid: Boolean) {
        if (valid) {
            worldIn.setBlockState(pos, getStateFromMeta(1), 3)
        } else {
            worldIn.setBlockState(pos, getStateFromMeta(0), 3)
        }
    }

    override fun canProvidePower(state: IBlockState): Boolean {
        return true
    }

    override fun getWeakPower(
        blockState: IBlockState,
        blockAccess: IBlockAccess,
        pos: BlockPos,
        side: EnumFacing
    ): Int {
        return if (getMetaFromState(blockAccess.getBlockState(pos)) == 1) 15 else 0
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.getDefaultState().withProperty(ACTIVE, meta > 0)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return if (state.getValue(ACTIVE)) 1 else 0
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, ACTIVE)
    }

    override fun getShiftDescription(meta: Int): String? {
        return GCCoreUtil.translate(this.getTranslationKey() + ".description")
    }

    override fun showDescription(meta: Int): Boolean {
        return true
    }

    override fun isSideSolid(base_state: IBlockState, world: IBlockAccess, pos: BlockPos, side: EnumFacing): Boolean {
        return true
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.MACHINE
    }

    companion object {
        @JvmField
        val ACTIVE: PropertyBool = PropertyBool.create("active")
    }
}
