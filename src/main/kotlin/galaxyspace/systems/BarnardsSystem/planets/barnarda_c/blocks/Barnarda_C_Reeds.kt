package galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks

import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Reeds
import net.minecraft.block.Block
import net.minecraft.block.BlockLiquid
import net.minecraft.block.material.Material
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class Barnarda_C_Reeds constructor() : Block(Material.WATER) {
    init {
        this.setTranslationKey("barnarda_c_reeds")
        setDefaultState(blockState.getBaseState().withProperty(BlockLiquid.LEVEL, 0))
    }


    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, BlockLiquid.LEVEL)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return 0
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.getDefaultState().withProperty(BlockLiquid.LEVEL, meta)
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

    @SideOnly(Side.CLIENT)
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
