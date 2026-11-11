package micdoodle8.mods.galacticraft.core.blocks

import foundation.block.getMetaFromState

import foundation.level.offset

import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.GCFluids
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import net.minecraft.block.BlockLiquid
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.client.player.LocalPlayer as EntityPlayerSP
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.world.InteractionHand as EnumHand
import net.minecraft.sounds.SoundSource as SoundCategory
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3 as Vec3d
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import net.minecraftforge.common.property.IExtendedBlockState
import net.minecraftforge.fluids.BlockFluidBase
import net.minecraftforge.fluids.BlockFluidClassic
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.IFluidBlock
import java.util.Random
import kotlin.math.atan2
import kotlin.math.min

open class BlockFluidGC constructor(private val fluid: Fluid, private val galacticraftFluidName: String) : BlockFluidClassic(
    fluid,
    if (galacticraftFluidName.startsWith("oil") || galacticraftFluidName.startsWith("fuel")) GCFluids.materialOil else Material.WATER
) {
    init {
        if (galacticraftFluidName.startsWith("oil")) {
            this.needsRandomTick = true
        }

        this.setTranslationKey(galacticraftFluidName)
    }

    override fun isEntityInsideMaterial(
        world: IBlockAccess,
        pos: BlockPos,
        state: IBlockState,
        entity: Entity,
        yToTest: Double,
        material: Material,
        testingHead: Boolean
    ): Boolean? {
        return true
    }

    override fun onBlockActivated(
        worldIn: World,
        pos: BlockPos,
        state: IBlockState,
        playerIn: EntityPlayer,
        hand: EnumHand,
        side: EnumFacing,
        hitX: Float,
        hitY: Float,
        hitZ: Float
    ): Boolean {
        if (worldIn.isClientSide && this.galacticraftFluidName.startsWith("oil") && playerIn is EntityPlayerSP) {
            ClientProxyCore.playerClientHandler.onBuild(7, playerIn)
        }

        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, side, hitX, hitY, hitZ)
    }

    override fun randomDisplayTick(stateIn: IBlockState, worldIn: World, pos: BlockPos, rand: Random) {
        super.randomDisplayTick(stateIn, worldIn, pos, rand)

        if (this.galacticraftFluidName.startsWith("oil") && rand.nextInt(1200) == 0) {
            worldIn.playSound(
                null,
                (pos.getX() + 0.5f).toDouble(),
                (pos.getY() + 0.5f).toDouble(),
                (pos.getZ() + 0.5f).toDouble(),
                SoundEvents.BLOCK_LAVA_AMBIENT,
                SoundCategory.BLOCKS,
                rand.nextFloat() * 0.25f + 0.75f,
                0.00001f + rand.nextFloat() * 0.5f
            )
        }
        if (this.galacticraftFluidName == "oil" && rand.nextInt(10) == 0) {
            val below: BlockPos = pos.below()
            val state: IBlockState = worldIn.getBlockState(below)
            if (state.getBlock()
                    .isSideSolid(state, worldIn, below, EnumFacing.UP) && !worldIn.getBlockState(pos.below(2))
                    .getMaterial().blocksMovement()
            ) {
                (GalacticraftCore.Companion.proxy)!!.spawnParticle(
                    "oilDrip",
                    Vector3(
                        (pos.getX() + rand.nextFloat()).toDouble(),
                        pos.getY() - 1.05,
                        (pos.getZ() + rand.nextFloat()).toDouble()
                    ),
                    Vector3(0.0, 0.0, 0.0),
                    arrayOf<Any?>()
                )
            }
        }
    }

    override fun canDisplace(world: IBlockAccess, pos: BlockPos): Boolean {
        if (world.getBlockState(pos).getMaterial().isLiquid()) {
            return false
        }

        return super.canDisplace(world, pos)
    }

    override fun displaceIfPossible(world: World, pos: BlockPos): Boolean {
        if (world.getBlockState(pos).getMaterial().isLiquid()) {
            return false
        }

        return super.displaceIfPossible(world, pos)
    }

    override fun isFlammable(world: IBlockAccess, pos: BlockPos, face: EnumFacing): Boolean {
        if (this.galacticraftFluidName.startsWith("fuel")) {
            (world as World).createExplosion(
                null,
                pos.getX().toDouble(),
                pos.getY().toDouble(),
                pos.getZ().toDouble(),
                6.0f,
                true
            )
            return true
        }
        return (this.galacticraftFluidName.startsWith("oil"))
    }

    override fun getExtendedState(oldState: IBlockState, world: IBlockAccess, pos: BlockPos): IBlockState {
        var state: IExtendedBlockState = oldState as IExtendedBlockState
        state = state.setValue(FLOW_DIRECTION, getFlowDirection(state, world, pos).toFloat())
        val upBlockState: Array<Array<IBlockState?>?> = Array<Array<IBlockState?>?>(3) { arrayOfNulls<IBlockState>(3) }
        val height: Array<FloatArray?> = Array<FloatArray?>(3) { FloatArray(3) }
        val corner: Array<FloatArray?> = Array<FloatArray?>(2) { FloatArray(2) }
        upBlockState[1]!![1] = world.getBlockState(pos.below(this.densityDir))
        height[1]!![1] = this.getFluidHeightForRender(world, pos, (upBlockState[1]!![1])!!)

        if (height[1]!![1] == 1f) {
            for (i: Int in 0..1) {
                for (j: Int in 0..1) {
                    corner[i]!![j] = 1f
                }
            }
        } else {
            for (i: Int in 0..2) {
                for (j: Int in 0..2) {
                    if (i != 1 || j != 1) {
                        upBlockState[i]!![j] = world.getBlockState(pos.offset(i - 1, 0, j - 1).down(this.densityDir))
                        height[i]!![j] =
                            this.getFluidHeightForRender(world, pos.offset(i - 1, 0, j - 1), (upBlockState[i]!![j])!!)
                    }
                }
            }
            for (i: Int in 0..1) {
                for (j: Int in 0..1) {
                    corner[i]!![j] = this.getFluidHeightAverage(
                        height[i]!![j],
                        height[i]!![j + 1],
                        height[i + 1]!![j],
                        height[i + 1]!![j + 1]
                    )
                }
            }
            //check for downflow above corners
            val n: Boolean = this.isFluid(upBlockState[0]!![1])
            val s: Boolean = this.isFluid(upBlockState[2]!![1])
            val w: Boolean = this.isFluid(upBlockState[1]!![0])
            val e: Boolean = this.isFluid(upBlockState[1]!![2])
            val nw: Boolean = this.isFluid(upBlockState[0]!![0])
            val ne: Boolean = this.isFluid(upBlockState[0]!![2])
            val sw: Boolean = this.isFluid(upBlockState[2]!![0])
            val se: Boolean = this.isFluid(upBlockState[2]!![2])

            if (nw || n || w) {
                corner[0]!![0] = 0.999f
            }
            if (ne || n || e) {
                corner[0]!![1] = 0.999f
            }
            if (sw || s || w) {
                corner[1]!![0] = 0.999f
            }
            if (se || s || e) {
                corner[1]!![1] = 0.999f
            }
        }
        state = state.setValue(LEVEL_CORNERS[0], corner[0]!![0])
        state = state.setValue(LEVEL_CORNERS[1], corner[0]!![1])
        state = state.setValue(LEVEL_CORNERS[2], corner[1]!![1])
        state = state.setValue(LEVEL_CORNERS[3], corner[1]!![0])
        return state
    }

    private fun isFluid(state: IBlockState?): Boolean {
        return state!!.getMaterial().isLiquid() || state.getBlock() is IFluidBlock
    }

    override fun getFluidHeightForRender(world: IBlockAccess, pos: BlockPos, up: IBlockState): Float {
        val here: IBlockState = world.getBlockState(pos)

        if (here.getBlock() === this) {
            if (up!!.getMaterial().isLiquid() || up.getBlock() is IFluidBlock) {
                return 1f
            }
            if (this.getMetaFromState(here) == this.getMaxRenderHeightMeta()) {
                return 0.875f
            }
        }
        if (here.getBlock() is BlockLiquid) {
            return min(1 - BlockLiquid.getLiquidHeightPercent(here.getValue(BlockLiquid.LEVEL)), 14f / 16)
        }
        return if (!here.getMaterial().isSolid() && up!!.getBlock() === this) 1f else this.getQuantaPercentage(
            world,
            pos
        ) * 0.875f
    }

    companion object {
        @JvmStatic
        fun getFlowDirection(state: IBlockState, world: IBlockAccess?, pos: BlockPos?): Double {
            if (!state.getMaterial().isLiquid()) {
                return -1000.0
            }
            val vec: Vec3d = (state.getBlock() as BlockFluidBase).getFlowVector(world, pos)
            return if (vec.x == 0.0 && vec.z == 0.0) -1000.0 else atan2(vec.z, vec.x) - Math.PI / 2.0
        }
    }
}
