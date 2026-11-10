package micdoodle8.mods.galacticraft.core.blocks

import com.google.common.base.Predicate
import micdoodle8.mods.galacticraft.api.block.IOxygenReliantBlock
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.util.OxygenUtil
import net.minecraft.block.Block
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.IProperty
import net.minecraft.block.properties.PropertyDirection
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumParticleTypes
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.ArrayList
import java.util.HashMap
import java.util.Random

open class BlockUnlitTorch constructor(lit: Boolean, assetName: String?) : BlockTorchBase(Material.CIRCUITS),
    IOxygenReliantBlock {
    @JvmField
    var lit: Boolean

    @JvmField
    var litVersion: Block? = null

    @JvmField
    var unlitVersion: Block? = null

    @JvmField
    var fallback: Block? = null

    init {
        this.setTickRandomly(true)
        this.lit = lit
        this.setLightLevel(if (lit) 0.9375f else 0.2f)
        this.setHardness(0.0f)
        this.setSoundType(SoundType.WOOD)
        this.setTranslationKey(assetName)
        this.setDefaultState(
            this.blockState.getBaseState().withProperty(FACING, EnumFacing.UP)
        )
    }

    override fun getCollisionBoundingBox(
        blockState: IBlockState,
        worldIn: IBlockAccess,
        pos: BlockPos
    ): AxisAlignedBB? {
        return null
    }

    override fun isOpaqueCube(state: IBlockState): Boolean {
        return false
    }

    override fun isFullCube(state: IBlockState): Boolean {
        return false
    }

    override fun canPlaceBlockAt(worldIn: World, pos: BlockPos): Boolean {
        for (enumfacing: EnumFacing in FACING.getAllowedValues()) {
            if (this.canPlaceAt(worldIn, pos, enumfacing)) {
                return true
            }
        }

        return false
    }

    override fun updateTick(worldIn: World, pos: BlockPos, state: IBlockState, rand: Random) {
        if (state.getBlock().getMetaFromState(state) == 0) {
            this.onBlockAdded(worldIn, pos, state)
        } else {
            this.checkOxygen(worldIn, pos, state)
        }
    }

    override fun onBlockAdded(worldIn: World, pos: BlockPos, state: IBlockState) {
        if (this.checkForDrop(worldIn, pos, state)) {
            this.checkOxygen(worldIn, pos, state)
        }
    }

    override fun neighborChanged(state: IBlockState, worldIn: World, pos: BlockPos, blockIn: Block, fromPos: BlockPos) {
        if (this.checkForDrop(worldIn, pos, state)) {
            val enumfacing: EnumFacing = state.getValue(FACING)
            val enumfacingAxis: EnumFacing.Axis = enumfacing.getAxis()
            val enumfacing1: EnumFacing = enumfacing.getOpposite()
            var flag: Boolean = false

            if (enumfacingAxis.isHorizontal() && !worldIn.isSideSolid(pos.offset(enumfacing1), enumfacing, true)) {
                flag = true
            } else if (enumfacingAxis.isVertical() && !this.canPlaceOn(worldIn, pos.offset(enumfacing1))) {
                flag = true
            }

            if (flag) {
                this.dropBlockAsItem(worldIn, pos, state, 0)
                worldIn.setBlockToAir(pos)
            } else {
                this.checkOxygen(worldIn, pos, state)
            }
        }
    }

    private fun checkOxygen(world: World, pos: BlockPos, state: IBlockState) {
        if (world.provider is IGalacticraftWorldProvider) {
            if (OxygenUtil.checkTorchHasOxygen(world, pos)) {
                this.onOxygenAdded(world, pos, state)
            } else {
                this.onOxygenRemoved(world, pos, state)
            }
        } else {
            val enumfacing: EnumFacing = state.getValue(FACING)
            world.setBlockState(
                pos,
                this.fallback!!.getDefaultState().withProperty(FACING, enumfacing),
                2
            )
        }
    }

    @SideOnly(Side.CLIENT)
    override fun randomDisplayTick(stateIn: IBlockState, worldIn: World, pos: BlockPos, rand: Random) {
        val enumfacing: EnumFacing = stateIn.getValue(FACING)
        val d0: Double = pos.getX().toDouble() + 0.5
        val d1: Double = pos.getY().toDouble() + 0.7
        val d2: Double = pos.getZ().toDouble() + 0.5
        val d3: Double = 0.22
        val d4: Double = 0.27

        if (enumfacing.getAxis().isHorizontal()) {
            val enumfacing1: EnumFacing = enumfacing.getOpposite()
            worldIn.spawnParticle(
                EnumParticleTypes.SMOKE_NORMAL,
                d0 + d4 * enumfacing1.getXOffset() as Double,
                d1 + d3,
                d2 + d4 * enumfacing1.getZOffset() as Double,
                0.0,
                0.0,
                0.0,
                *IntArray(0)
            )
            if (this === GCBlocks.unlitTorchLit) {
                worldIn.spawnParticle(
                    EnumParticleTypes.FLAME,
                    d0 + d4 * enumfacing1.getXOffset() as Double,
                    d1 + d3,
                    d2 + d4 * enumfacing1.getZOffset() as Double,
                    0.0,
                    0.0,
                    0.0,
                    *IntArray(0)
                )
            }
        } else {
            worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, 0.0, 0.0, 0.0, *IntArray(0))
            if (this === GCBlocks.unlitTorchLit) {
                worldIn.spawnParticle(EnumParticleTypes.FLAME, d0, d1, d2, 0.0, 0.0, 0.0, *IntArray(0))
            }
        }
    }

    override fun onOxygenRemoved(world: World?, pos: BlockPos?, state: IBlockState?) {
        if ((world)!!.provider is IGalacticraftWorldProvider) {
            val enumfacing: EnumFacing = (state)!!.getValue(FACING)
            (world)!!.setBlockState(
                pos,
                this.unlitVersion!!.getDefaultState().withProperty(FACING, enumfacing),
                2
            )
        }
    }

    override fun onOxygenAdded(world: World?, pos: BlockPos?, state: IBlockState?) {
        if ((world)!!.provider is IGalacticraftWorldProvider) {
            val enumfacing: EnumFacing = (state)!!.getValue(FACING)
            (world)!!.setBlockState(
                pos,
                this.litVersion!!.getDefaultState().withProperty(FACING, enumfacing),
                2
            )
        }
    }

    override fun getDrops(
        world: IBlockAccess,
        pos: BlockPos,
        state: IBlockState,
        fortune: Int
    ): MutableList<ItemStack?> {
        val ret: ArrayList<ItemStack?> = ArrayList<ItemStack?>()
        ret.add(ItemStack(this.litVersion))
        return ret
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, *arrayOf<IProperty<*>>(FACING))
    }

    @SideOnly(Side.CLIENT)
    public override fun getRenderLayer(): BlockRenderLayer {
        return BlockRenderLayer.CUTOUT
    }

    companion object {
        @JvmField
        val FACING: PropertyDirection =
            PropertyDirection.create("facing", Predicate { facing: EnumFacing? -> facing != EnumFacing.DOWN })

        @JvmField
        var registeredTorches: HashMap<Block?, Block?> = HashMap<Block?, Block?>()

        @JvmStatic
        fun register(unlittorch: BlockUnlitTorch, littorch: BlockUnlitTorch, vanillatorch: Block) {
            littorch.litVersion = littorch
            littorch.unlitVersion = unlittorch
            littorch.fallback = vanillatorch
            unlittorch.litVersion = littorch
            unlittorch.unlitVersion = unlittorch
            unlittorch.fallback = vanillatorch
            registeredTorches.put(littorch, vanillatorch)
            GCBlocks.itemChanges.put(unlittorch, littorch)
        }
    }
}
