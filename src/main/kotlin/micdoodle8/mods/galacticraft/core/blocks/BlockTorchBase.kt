package micdoodle8.mods.galacticraft.core.blocks

import com.google.common.base.Predicate
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyDirection
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

open class BlockTorchBase constructor(materialIn: Material) : Block(materialIn) {
    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        when (state.getValue(FACING)) {
            EnumFacing.EAST -> return TORCH_EAST_AABB
            EnumFacing.WEST -> return TORCH_WEST_AABB
            EnumFacing.SOUTH -> return TORCH_SOUTH_AABB
            EnumFacing.NORTH -> return TORCH_NORTH_AABB
            else -> return STANDING_AABB
        }
    }

    override fun getCollisionBoundingBox(
        blockState: IBlockState,
        worldIn: IBlockAccess,
        pos: BlockPos
    ): AxisAlignedBB? {
        return NULL_AABB
    }

    override fun getStateForPlacement(
        worldIn: World,
        pos: BlockPos,
        facing: EnumFacing,
        hitX: Float,
        hitY: Float,
        hitZ: Float,
        meta: Int,
        placer: EntityLivingBase,
        hand: EnumHand
    ): IBlockState {
        if (this.canPlaceAt(worldIn, pos, facing)) {
            return this.getDefaultState().withProperty(FACING, facing)
        } else {
            for (enumfacing: EnumFacing in EnumFacing.Plane.HORIZONTAL) {
                if (worldIn.isSideSolid(pos.offset(enumfacing.getOpposite()), enumfacing, true)) {
                    return this.getDefaultState().withProperty(FACING, enumfacing)
                }
            }

            return this.getDefaultState()
        }
    }

    protected open fun canPlaceAt(worldIn: World, pos: BlockPos, facing: EnumFacing): Boolean {
        val blockpos: BlockPos = pos.offset(facing.getOpposite())
        val flag: Boolean = facing.getAxis().isHorizontal()
        return flag && worldIn.isSideSolid(
            blockpos,
            facing,
            true
        ) || facing == EnumFacing.UP && this.canPlaceOn(worldIn, blockpos)
    }

    protected open fun canPlaceOn(worldIn: World, pos: BlockPos): Boolean {
        val state: IBlockState = worldIn.getBlockState(pos)
        val block: Block = state.getBlock()
        if (block.isSideSolid(state, worldIn, pos, EnumFacing.UP)) {
            return true
        } else {
            return block.canPlaceTorchOnTop(state, worldIn, pos)
        }
    }

    protected open fun checkForDrop(worldIn: World, pos: BlockPos, state: IBlockState): Boolean {
        if (state.getBlock() === this && this.canPlaceAt(
                worldIn,
                pos,
                state.getValue(FACING) as EnumFacing
            )
        ) {
            return true
        } else {
            if (worldIn.getBlockState(pos).getBlock() === this) {
                this.dropBlockAsItem(worldIn, pos, state, 0)
                worldIn.setBlockToAir(pos)
            }

            return false
        }
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        var iblockstate: IBlockState = this.getDefaultState()

        when (meta) {
            1 -> iblockstate = iblockstate.withProperty(FACING, EnumFacing.EAST)
            2 -> iblockstate = iblockstate.withProperty(FACING, EnumFacing.WEST)
            3 -> iblockstate = iblockstate.withProperty(FACING, EnumFacing.SOUTH)
            4 -> iblockstate = iblockstate.withProperty(FACING, EnumFacing.NORTH)
            5 -> iblockstate = iblockstate.withProperty(FACING, EnumFacing.UP)
            else -> iblockstate = iblockstate.withProperty(FACING, EnumFacing.UP)
        }

        return iblockstate
    }

    override fun getMetaFromState(state: IBlockState): Int {
        var i: Int = 0

        when (state.getValue(FACING) as EnumFacing) {
            EnumFacing.EAST -> i = i or 1
            EnumFacing.WEST -> i = i or 2
            EnumFacing.SOUTH -> i = i or 3
            EnumFacing.NORTH -> i = i or 4
            EnumFacing.DOWN, EnumFacing.UP -> i = i or 5
            else -> i = i or 5
        }

        return i
    }

    override fun getBlockFaceShape(
        worldIn: IBlockAccess,
        state: IBlockState,
        pos: BlockPos,
        face: EnumFacing
    ): BlockFaceShape {
        return BlockFaceShape.UNDEFINED
    }

    companion object {
        @JvmField
        val FACING: PropertyDirection =
            PropertyDirection.create("facing", Predicate { facing: EnumFacing? -> facing != EnumFacing.DOWN })

        @JvmField
        protected val STANDING_AABB: AxisAlignedBB = AxisAlignedBB(
            0.4000000059604645,
            0.0,
            0.4000000059604645,
            0.6000000238418579,
            0.6000000238418579,
            0.6000000238418579
        )

        @JvmField
        protected val TORCH_NORTH_AABB: AxisAlignedBB = AxisAlignedBB(
            0.3499999940395355,
            0.20000000298023224,
            0.699999988079071,
            0.6499999761581421,
            0.800000011920929,
            1.0
        )

        @JvmField
        protected val TORCH_SOUTH_AABB: AxisAlignedBB = AxisAlignedBB(
            0.3499999940395355,
            0.20000000298023224,
            0.0,
            0.6499999761581421,
            0.800000011920929,
            0.30000001192092896
        )

        @JvmField
        protected val TORCH_WEST_AABB: AxisAlignedBB = AxisAlignedBB(
            0.699999988079071,
            0.20000000298023224,
            0.3499999940395355,
            1.0,
            0.800000011920929,
            0.6499999761581421
        )

        @JvmField
        protected val TORCH_EAST_AABB: AxisAlignedBB = AxisAlignedBB(
            0.0,
            0.20000000298023224,
            0.3499999940395355,
            0.30000001192092896,
            0.800000011920929,
            0.6499999761581421
        )
    }
}
