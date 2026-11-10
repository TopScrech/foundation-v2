package micdoodle8.mods.galacticraft.core.blocks

import com.google.common.collect.Lists
import com.google.common.collect.Sets
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import net.minecraft.block.Block
import net.minecraft.block.BlockRedstoneRepeater
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.IProperty
import net.minecraft.block.properties.PropertyInteger
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.init.Blocks
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

open class BlockConcealedRedstone constructor(assetName: String?) : Block(Material.IRON), ISortableBlock {
    private var canProvidePower = true
    private val blocksNeedingUpdate: MutableSet<BlockPos?> = Sets.newHashSet<BlockPos?>()

    init {
        this.setHardness(1.0f)
        this.setSoundType(SoundType.METAL)
        this.blockResistance = 15f
        this.setTranslationKey(assetName)
        this.setDefaultState(this.blockState.getBaseState().withProperty(POWER, 0))
    }

    public override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.Companion.galacticraftBlocksTab
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.DECORATION
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.getDefaultState().withProperty(POWER, meta)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(POWER) as Int)
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, *arrayOf<IProperty<*>>(POWER))
    }

    private fun updateSurroundingRedstone(worldIn: World, pos: BlockPos, state: IBlockState): IBlockState {
        var state: IBlockState = state
        state = this.calculateCurrentChanges(worldIn, pos, pos, state)
        val list: MutableList<BlockPos> = Lists.newArrayList<BlockPos>(this.blocksNeedingUpdate)
        this.blocksNeedingUpdate.clear()

        for (blockpos: BlockPos in list) {
            worldIn.notifyNeighborsOfStateChange(blockpos, this, false)
        }

        return state
    }

    private fun calculateCurrentChanges(
        worldIn: World,
        pos1: BlockPos,
        pos2: BlockPos,
        state: IBlockState
    ): IBlockState {
        var state: IBlockState = state
        val iblockstate: IBlockState = state
        val currentPower: Int = (state.getValue(POWER) as Int)
        var maxPower: Int = 0
        maxPower = this.getMaxCurrentStrength(worldIn, pos2, maxPower)
        this.canProvidePower = false
        val k: Int = worldIn.getRedstonePowerFromNeighbors(pos1)
        this.canProvidePower = true

        if (k > 0 && k > maxPower - 1) {
            maxPower = k
        }

        var l: Int = 0

        for (enumfacing: EnumFacing in EnumFacing.Plane.HORIZONTAL) {
            val blockpos: BlockPos = pos1.offset(enumfacing)
            val flag: Boolean = blockpos.getX() != pos2.getX() || blockpos.getZ() != pos2.getZ()

            if (flag) {
                l = this.getMaxCurrentStrength(worldIn, blockpos, l)
            }

            val bs: IBlockState = worldIn.getBlockState(blockpos)
            val bsUp: IBlockState = worldIn.getBlockState(pos1.up())
            if (bs.getBlock().isNormalCube(bs) && !bsUp.getBlock().isNormalCube(bsUp)) {
                if (flag && pos1.getY() >= pos2.getY()) {
                    l = this.getMaxCurrentStrength(worldIn, blockpos.up(), l)
                }
            } else if (!bs.getBlock().isNormalCube(bs) && flag && pos1.getY() <= pos2.getY()) {
                l = this.getMaxCurrentStrength(worldIn, blockpos.down(), l)
            }
        }

        if (l > maxPower) {
            maxPower = l - 1
        } else if (maxPower > 0) {
            maxPower--
        } else {
            maxPower = 0
        }

        if (k > maxPower - 1) {
            maxPower = k
        }

        if (currentPower != maxPower) {
            state = state.withProperty(POWER, maxPower)

            if (worldIn.getBlockState(pos1) === iblockstate) {
                worldIn.setBlockState(pos1, state, 2)
            }

            this.blocksNeedingUpdate.add(pos1)

            for (enumfacing1: EnumFacing in EnumFacing.VALUES) {
                this.blocksNeedingUpdate.add(pos1.offset(enumfacing1))
            }
        }

        return state
    }

    private fun notifyWireNeighborsOfStateChange(worldIn: World, pos: BlockPos) {
        val b: Block = worldIn.getBlockState(pos).getBlock()
        if (b === this || b === Blocks.REDSTONE_WIRE) {
            worldIn.notifyNeighborsOfStateChange(pos, this, false)

            for (enumfacing: EnumFacing in EnumFacing.VALUES) {
                worldIn.notifyNeighborsOfStateChange(pos.offset(enumfacing), this, false)
            }
        }
    }

    override fun onBlockAdded(worldIn: World, pos: BlockPos, state: IBlockState) {
        if (!worldIn.isRemote) {
            this.updateSurroundingRedstone(worldIn, pos, state)

            for (enumfacing: EnumFacing in EnumFacing.Plane.VERTICAL) {
                worldIn.notifyNeighborsOfStateChange(pos.offset(enumfacing), this, false)
            }

            for (enumfacing1: EnumFacing in EnumFacing.Plane.HORIZONTAL) {
                this.notifyWireNeighborsOfStateChange(worldIn, pos.offset(enumfacing1))
            }

            this.notifyNeighbors(worldIn, pos)
        }
    }

    private fun notifyNeighbors(worldIn: World, pos: BlockPos) {
        for (enumfacing2: EnumFacing in EnumFacing.Plane.HORIZONTAL) {
            val blockpos: BlockPos = pos.offset(enumfacing2)

            val bs: IBlockState = worldIn.getBlockState(blockpos)
            if (bs.getBlock().isNormalCube(bs)) {
                this.notifyWireNeighborsOfStateChange(worldIn, blockpos.up())
            } else {
                this.notifyWireNeighborsOfStateChange(worldIn, blockpos.down())
            }
        }
    }

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        super.breakBlock(worldIn, pos, state)

        if (!worldIn.isRemote) {
            for (enumfacing: EnumFacing in EnumFacing.VALUES) {
                worldIn.notifyNeighborsOfStateChange(pos.offset(enumfacing), this, false)
            }

            this.updateSurroundingRedstone(worldIn, pos, state)

            for (enumfacing1: EnumFacing in EnumFacing.Plane.HORIZONTAL) {
                this.notifyWireNeighborsOfStateChange(worldIn, pos.offset(enumfacing1))
            }

            this.notifyNeighbors(worldIn, pos)
        }
    }

    private fun getMaxCurrentStrength(worldIn: World, pos: BlockPos, strength: Int): Int {
        if (worldIn.getBlockState(pos).getBlock() !== this) {
            return strength
        } else {
            val i: Int = worldIn.getBlockState(pos).getValue(POWER) as Int
            return if (i > strength) i else strength
        }
    }

    override fun neighborChanged(state: IBlockState, worldIn: World, pos: BlockPos, blockIn: Block, fromPos: BlockPos) {
        if (!worldIn.isRemote) {
            this.updateSurroundingRedstone(worldIn, pos, state)
        }
    }

    override fun getStrongPower(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos, side: EnumFacing): Int {
        return if (!this.canProvidePower) 0 else this.getWeakPower(state, worldIn, pos, side)
    }

    override fun getWeakPower(state: IBlockState, blockAccess: IBlockAccess, pos: BlockPos, side: EnumFacing): Int {
        if (!this.canProvidePower) {
            return 0
        } else {
            return (state.getValue(POWER) as Int)
        }
    }

    override fun canProvidePower(state: IBlockState): Boolean {
        return this.canProvidePower
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

    companion object {
        @JvmField
        val POWER: PropertyInteger = PropertyInteger.create("power", 0, 15)

        @JvmStatic
        protected fun canRedstoneConnect(world: IBlockAccess, pos: BlockPos, side: EnumFacing?): Boolean {
            val state: IBlockState = world.getBlockState(pos)
            if (state.getBlock() === Blocks.REDSTONE_WIRE || state.getBlock() === GCBlocks.concealedRedstone) {
                return true
            } else if (Blocks.UNPOWERED_REPEATER.isAssociatedBlock(state.getBlock())) {
                val direction: EnumFacing = state.getValue(BlockRedstoneRepeater.FACING) as EnumFacing
                return direction == side || direction.getOpposite() == side
            } else {
                return state.getBlock().canConnectRedstone(state, world, pos, side)
            }
        }
    }
}
