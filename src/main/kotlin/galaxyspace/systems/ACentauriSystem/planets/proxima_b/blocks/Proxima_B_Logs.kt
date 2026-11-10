package galaxyspace.systems.ACentauriSystem.planets.proxima_b.blocks

import galaxyspace.systems.ACentauriSystem.planets.proxima_b.blocks.Proxima_B_Logs
import galaxyspace.core.util.GSCreativeTabs
import net.minecraft.block.BlockRotatedPillar
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.EnumFacing
import net.minecraft.util.Rotation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

open class Proxima_B_Logs constructor(name: String) : BlockRotatedPillar(Material.WOOD) {
    init {
        setTranslationKey(name)
        setHarvestLevel("axe", 2)
        setRegistryName(name)
        setCreativeTab(GSCreativeTabs.GSBlocksTab)
        setHardness(2.0f)
        setSoundType(SoundType.WOOD)
    }

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        val i: Int = 4
        val j: Int = 5

        if (worldIn.isAreaLoaded(pos.add(-5, -5, -5), pos.add(5, 5, 5))) {
            for (blockpos: BlockPos in BlockPos.getAllInBox(pos.add(-4, -4, -4), pos.add(4, 4, 4))) {
                val iblockstate: IBlockState = worldIn.getBlockState(blockpos)

                if (iblockstate.getBlock().isLeaves(iblockstate, worldIn, blockpos)) {
                    iblockstate.getBlock().beginLeavesDecay(iblockstate, worldIn, blockpos)
                }
            }
        }
    }

    override fun getStateForPlacement(
        worldIn: World,
        pos: BlockPos,
        facing: EnumFacing,
        hitX: Float,
        hitY: Float,
        hitZ: Float,
        meta: Int,
        placer: EntityLivingBase
    ): IBlockState {
        return this.getStateFromMeta(meta).withProperty(AXIS, facing.getAxis())
    }

    override fun withRotation(state: IBlockState, rot: Rotation): IBlockState {
        when (rot) {
            Rotation.COUNTERCLOCKWISE_90, Rotation.CLOCKWISE_90 -> when (state.getValue(AXIS) as EnumFacing.Axis) {
                EnumFacing.Axis.X -> return state.withProperty(
                    AXIS,
                    EnumFacing.Axis.Z
                )

                EnumFacing.Axis.Z -> return state.withProperty(
                    AXIS,
                    EnumFacing.Axis.X
                )

                else -> return state
            }

            else -> return state
        }
    }

    override fun canSustainLeaves(state: IBlockState, world: IBlockAccess, pos: BlockPos): Boolean {
        return true
    }

    override fun isWood(world: IBlockAccess, pos: BlockPos): Boolean {
        return true
    }
}
