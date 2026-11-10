package micdoodle8.mods.galacticraft.planets.mars.world.gen

import net.minecraft.block.Block
import net.minecraft.block.BlockCocoa
import net.minecraft.block.BlockSapling
import net.minecraft.block.BlockVine
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyBool
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.gen.feature.WorldGenerator
import java.util.Random
import kotlin.math.abs

open class WorldGenTerraformTree constructor(
    par1: Boolean,
    private val minTreeHeight: Int,
    private val metaWood: Int,
    private val metaLeaves: Int,
    private val vinesGrow: Boolean
) : WorldGenerator(par1) {
    constructor(par1: Boolean, sapling: ItemStack?) : this(par1, 4, 0, 0, false)

    override fun generate(worldIn: World, rand: Random, position: BlockPos): Boolean {
        val i: Int = rand.nextInt(3) + this.minTreeHeight
        var flag: Boolean = true

        if (position.getY() >= 1 && position.getY() + i + 1 <= 256) {
            var b0: Byte
            var l: Int

            for (j: Int in position.getY()..position.getY() + 1 + i) {
                b0 = 1

                if (j == position.getY()) {
                    b0 = 0
                }

                if (j >= position.getY() + 1 + i - 2) {
                    b0 = 2
                }

                var k: Int = position.getX() - b0
                while (k <= position.getX() + b0 && flag) {
                    l = position.getZ() - b0
                    while (l <= position.getZ() + b0 && flag) {
                        if (j >= 0 && j < 256) {
                            if (!this.isReplaceable(worldIn, BlockPos(k, j, l))) {
                                flag = false
                            }
                        } else {
                            flag = false
                        }
                        ++l
                    }
                    ++k
                }
            }

            if (!flag) {
                return false
            } else {
                val down: BlockPos = position.down()
                val block1: Block = worldIn.getBlockState(down).getBlock()
                val isSoil: Boolean = block1.canSustainPlant(
                    worldIn.getBlockState(down),
                    worldIn,
                    down,
                    EnumFacing.UP,
                    Blocks.SAPLING as BlockSapling?
                )

                if (isSoil && position.getY() < 256 - i - 1) {
                    block1.onPlantGrow(worldIn.getBlockState(down), worldIn, down, position)
                    b0 = 3
                    val b1: Byte = 0
                    var i1: Int
                    var j1: Int
                    var k1: Int
                    var l1: Int
                    var blockpos1: BlockPos

                    l = position.getY() - b0 + i
                    while (l <= position.getY() + i) {
                        i1 = l - (position.getY() + i)
                        j1 = b1 + 1 - i1 / 2

                        k1 = position.getX() - j1
                        while (k1 <= position.getX() + j1) {
                            l1 = k1 - position.getX()

                            for (i2: Int in position.getZ() - j1..position.getZ() + j1) {
                                val j2: Int = i2 - position.getZ()

                                if (abs(l1) != j1 || abs(j2) != j1 || rand.nextInt(2) != 0 && i1 != 0) {
                                    blockpos1 = BlockPos(k1, l, i2)
                                    val state: IBlockState = worldIn.getBlockState(blockpos1)
                                    val block: Block = state.getBlock()

                                    if (block.isAir(state, worldIn, blockpos1) || block.isLeaves(
                                            state,
                                            worldIn,
                                            blockpos1
                                        ) || block.getMaterial(state) === Material.VINE
                                    ) {
                                        this.setBlockAndNotifyAdequately(
                                            worldIn,
                                            blockpos1,
                                            Blocks.LEAVES.getStateFromMeta(this.metaLeaves)
                                        )
                                    }
                                }
                            }
                            ++k1
                        }
                        ++l
                    }

                    l = 0
                    while (l < i) {
                        val upN: BlockPos = position.up(l)
                        val state2: IBlockState = worldIn.getBlockState(upN)
                        val block2: Block = state2.getBlock()

                        if (block2.isAir(state2, worldIn, upN) || block2.isLeaves(
                                state2,
                                worldIn,
                                upN
                            ) || block2.getMaterial(state2) === Material.VINE
                        ) {
                            this.setBlockAndNotifyAdequately(
                                worldIn,
                                position.up(l),
                                Blocks.LOG.getStateFromMeta(this.metaWood)
                            )

                            if (this.vinesGrow && l > 0) {
                                if (rand.nextInt(3) > 0 && worldIn.isAirBlock(position.add(-1, l, 0))) {
                                    this.func_181647_a(worldIn, position.add(-1, l, 0), BlockVine.EAST)
                                }

                                if (rand.nextInt(3) > 0 && worldIn.isAirBlock(position.add(1, l, 0))) {
                                    this.func_181647_a(worldIn, position.add(1, l, 0), BlockVine.WEST)
                                }

                                if (rand.nextInt(3) > 0 && worldIn.isAirBlock(position.add(0, l, -1))) {
                                    this.func_181647_a(worldIn, position.add(0, l, -1), BlockVine.SOUTH)
                                }

                                if (rand.nextInt(3) > 0 && worldIn.isAirBlock(position.add(0, l, 1))) {
                                    this.func_181647_a(worldIn, position.add(0, l, 1), BlockVine.NORTH)
                                }
                            }
                        }
                        ++l
                    }

                    if (this.vinesGrow) {
                        l = position.getY() - 3 + i
                        while (l <= position.getY() + i) {
                            i1 = l - (position.getY() + i)
                            j1 = 2 - i1 / 2

                            k1 = position.getX() - j1
                            while (k1 <= position.getX() + j1) {
                                l1 = position.getZ() - j1
                                while (l1 <= position.getZ() + j1) {
                                    val blockpos3: BlockPos = BlockPos(k1, l, l1)

                                    val state3: IBlockState = worldIn.getBlockState(blockpos3)
                                    if (state3.getBlock().isLeaves(state3, worldIn, blockpos3)) {
                                        val blockpos4: BlockPos = blockpos3.west()
                                        blockpos1 = blockpos3.east()
                                        val blockpos5: BlockPos = blockpos3.north()
                                        val blockpos2: BlockPos = blockpos3.south()

                                        val state4: IBlockState = worldIn.getBlockState(blockpos4)
                                        if (rand.nextInt(4) == 0 && state4.getBlock()
                                                .isAir(state4, worldIn, blockpos4)
                                        ) {
                                            this.func_181647_a(worldIn, blockpos4, BlockVine.EAST)
                                        }

                                        val state1: IBlockState = worldIn.getBlockState(blockpos1)
                                        if (rand.nextInt(4) == 0 && state1.getBlock()
                                                .isAir(state1, worldIn, blockpos1)
                                        ) {
                                            this.func_181647_a(worldIn, blockpos1, BlockVine.WEST)
                                        }

                                        val state5: IBlockState = worldIn.getBlockState(blockpos5)
                                        if (rand.nextInt(4) == 0 && state5.getBlock()
                                                .isAir(state5, worldIn, blockpos5)
                                        ) {
                                            this.func_181647_a(worldIn, blockpos5, BlockVine.SOUTH)
                                        }

                                        val state2: IBlockState = worldIn.getBlockState(blockpos2)
                                        if (rand.nextInt(4) == 0 && state2.getBlock()
                                                .isAir(state2, worldIn, blockpos2)
                                        ) {
                                            this.func_181647_a(worldIn, blockpos2, BlockVine.NORTH)
                                        }
                                    }
                                    ++l1
                                }
                                ++k1
                            }
                            ++l
                        }

                        if (rand.nextInt(5) == 0 && i > 5) {
                            l = 0
                            while (l < 2) {
                                for (enumfacing: EnumFacing in EnumFacing.Plane.HORIZONTAL) {
                                    if (rand.nextInt(4 - l) == 0) {
                                        j1 = rand.nextInt(3)
                                        this.func_181652_a(
                                            worldIn,
                                            rand.nextInt(3),
                                            position.add(
                                                enumfacing.getXOffset(),
                                                i - 5 + l,
                                                enumfacing.getZOffset()
                                            ),
                                            enumfacing
                                        )
                                    }
                                }
                                ++l
                            }
                        }
                    }

                    return true
                } else {
                    return false
                }
            }
        } else {
            return false
        }
    }

    private fun func_181647_a(world: World, pos: BlockPos, facing: PropertyBool) {
        var pos: BlockPos = pos
        val iblockstate: IBlockState = Blocks.VINE.getDefaultState().withProperty(facing, true)
        this.setBlockAndNotifyAdequately(world, pos, iblockstate)
        var i: Int = 4

        pos = pos.down()
        while (world.getBlockState(pos).getBlock().isAir(world.getBlockState(pos), world, pos) && i > 0) {
            this.setBlockAndNotifyAdequately(world, pos, iblockstate)
            pos = pos.down()
            --i
        }
    }

    private fun func_181652_a(p_181652_1_: World, p_181652_2_: Int, p_181652_3_: BlockPos, p_181652_4_: EnumFacing) {
        this.setBlockAndNotifyAdequately(
            p_181652_1_,
            p_181652_3_,
            Blocks.COCOA.getDefaultState().withProperty(BlockCocoa.AGE, p_181652_2_)
                .withProperty(BlockCocoa.FACING, p_181652_4_)
        )
    }

    protected open fun func_150523_a(state: IBlockState): Boolean {
        val block: Block = state.getBlock()
        return block.getMaterial(state) === Material.AIR || block.getMaterial(state) === Material.LEAVES || block === Blocks.GRASS || block === Blocks.DIRT || block === Blocks.LOG || block === Blocks.LOG2 || block === Blocks.SAPLING || block === Blocks.VINE
    }

    open fun isReplaceable(world: World, pos: BlockPos): Boolean {
        val state: IBlockState = world.getBlockState(pos)
        return state.getBlock().isAir(state, world, pos) || state.getBlock()
            .isLeaves(state, world, pos) || state.getBlock().isWood(world, pos) || func_150523_a(state)
    }
}
