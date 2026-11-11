package micdoodle8.mods.galacticraft.core.blocks

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import net.minecraft.block.BlockStairs
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.world.level.block.Mirror
import net.minecraft.world.level.block.Rotation
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter as IBlockAccess

open class BlockStairsGC constructor(name: String?, state: IBlockState) : BlockStairs(state), ISortableBlock {
    init {
        this.setTranslationKey(name)
        this.useNeighborBrightness = true
    }

    public override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftBlocksTab
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.STAIRS
    }

    //Override the interaction with StructuredComponent to do nothing here, otherwise messes up our Abandoned Base generation code merged from 1.8.9
    override fun withRotation(state: IBlockState, rot: Rotation): IBlockState {
        return state
    }

    //Override the interaction with StructuredComponent to do nothing here, otherwise messes up our Abandoned Base generation code merged from 1.8.9
    override fun withMirror(state: IBlockState, mirrorIn: Mirror): IBlockState {
        return state
    }

    override fun getActualState(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos): IBlockState {
        return state.setValue(SHAPE, getStairsShape(state, worldIn, pos))
    }

    companion object {
        //Correct bugged version in vanilla Minecraft
        @JvmStatic
        private fun getStairsShape(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos): EnumShape {
            val enumfacing: EnumFacing = state.getValue(FACING) as EnumFacing
            val iblockstate: IBlockState = worldIn.getBlockState(pos.offset(enumfacing))
            val top: Boolean = state.getValue(HALF) == EnumHalf.TOP

            if (isBlockStairs(iblockstate) && state.getValue(HALF) == iblockstate.getValue(HALF)) {
                val enumfacing1: EnumFacing = iblockstate.getValue(FACING) as EnumFacing

                if (enumfacing1.getAxis() != (state.getValue(FACING) as EnumFacing).getAxis() && isDifferentStairs(
                        state,
                        worldIn,
                        pos,
                        enumfacing1.getOpposite()
                    )
                ) {
                    if (enumfacing1 == (if (top) enumfacing.rotateY() else enumfacing.rotateYCCW())) {
                        return EnumShape.OUTER_LEFT
                    }

                    return EnumShape.OUTER_RIGHT
                }
            }

            val iblockstate1: IBlockState = worldIn.getBlockState(pos.offset(enumfacing.getOpposite()))

            if (isBlockStairs(iblockstate1) && state.getValue(HALF) == iblockstate1.getValue(HALF)) {
                val enumfacing2: EnumFacing = iblockstate1.getValue(FACING) as EnumFacing

                if (enumfacing2.getAxis() != (state.getValue(FACING) as EnumFacing).getAxis() && isDifferentStairs(
                        state,
                        worldIn,
                        pos,
                        enumfacing2
                    )
                ) {
                    if (enumfacing2 == (if (top) enumfacing.rotateY() else enumfacing.rotateYCCW())) {
                        return EnumShape.INNER_LEFT
                    }

                    return EnumShape.INNER_RIGHT
                }
            }

            return EnumShape.STRAIGHT
        }

        @JvmStatic
        private fun isDifferentStairs(
            state: IBlockState,
            worldIn: IBlockAccess,
            pos: BlockPos,
            side: EnumFacing
        ): Boolean {
            val iblockstate: IBlockState = worldIn.getBlockState(pos.offset(side))
            return !isBlockStairs(iblockstate) || iblockstate.getValue(FACING) != state.getValue(
                FACING
            ) || iblockstate.getValue(HALF) != state.getValue(HALF)
        }
    }
}
