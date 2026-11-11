package galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks

import foundation.block.LegacyBlockMetadata

import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Reeds
import net.minecraft.world.level.block.Block
import net.minecraft.block.BlockLiquid
import net.minecraft.block.material.Material
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.util.BlockRenderLayer
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class Barnarda_C_Reeds constructor() : Block(Material.WATER), LegacyBlockMetadata {
    init {
        this.setTranslationKey("barnarda_c_reeds")
        setDefaultState(blockState.getBaseState().setValue(BlockLiquid.LEVEL, 0))
    }


    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, BlockLiquid.LEVEL)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return 0
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.defaultBlockState().setValue(BlockLiquid.LEVEL, meta)
    }

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        return REED_AABB
    }

    override fun getCollisionBoundingBox(
        blockState: IBlockState,
        worldIn: IBlockAccess,
        pos: BlockPos
    ): AxisAlignedBB? {
        return NULL_AABB
    }

    override fun isOpaqueCube(state: IBlockState): Boolean {
        return false
    }

    override fun isFullCube(state: IBlockState): Boolean {
        return false
    }

    @OnlyIn(Dist.CLIENT)
    override fun getRenderLayer(): BlockRenderLayer {
        return BlockRenderLayer.CUTOUT_MIPPED
    }

    override fun canRenderInLayer(state: IBlockState, layer: BlockRenderLayer): Boolean {
        return (layer == BlockRenderLayer.CUTOUT_MIPPED ||
                layer == BlockRenderLayer.TRANSLUCENT)
    }

    companion object {
        @JvmField
        protected val REED_AABB: AxisAlignedBB = AxisAlignedBB(0.125, 0.0, 0.125, 0.875, 1.0, 0.875)
    }
}
