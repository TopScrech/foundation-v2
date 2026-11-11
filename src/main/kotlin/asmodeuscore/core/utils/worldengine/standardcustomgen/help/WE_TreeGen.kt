package asmodeuscore.core.utils.worldengine.standardcustomgen.help

import foundation.block.getStateFromMeta

import java.util.ArrayList
import java.util.Random

import net.minecraft.world.level.block.Block
import net.minecraft.block.BlockSapling
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.Blocks
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import net.minecraft.world.gen.feature.WorldGenerator
import java.util.*
import kotlin.math.abs

open class WE_TreeGen constructor(doBlockNotify: Boolean) : WorldGenerator(doBlockNotify) {
    @JvmField
    var bWood: Block = Blocks.LOG

    @JvmField
    var bLeaves: Block = Blocks.LEAVES

    @JvmField
    var bSapling: Block = Blocks.SAPLING

    @JvmField
    var bVine: Block = Blocks.VINE

    @JvmField
    var bCocoa: Block? = Blocks.COCOA

    @JvmField
    var minTreeHeight: Int = 4

    @JvmField
    var metaWood: Int = 0

    @JvmField
    var metaLeaves: Int = 0

    @JvmField
    var vinesGrow: Boolean = false

    @JvmField
    var ab: MutableList<Block?> = ArrayList<Block?>()

    @JvmField
    var am: MutableList<Material?> = ArrayList<Material?>()

    open fun cb(b: Block): Boolean {
        for (i: Int in this.ab.indices) {
            if (b === this.ab.get(i)) {
                return true
            }
        }

        for (ix: Int in this.am.indices) {
            if (b.getMaterial(b.defaultBlockState()) === this.am.get(ix)) {
                return true
            }
        }

        return false
    }

    open fun isReplaceable(world: World, pos: BlockPos): Boolean {
        val b: Block = world.getBlockState(pos).getBlock()
        return b.isAir(b.defaultBlockState(), world, pos) || b.isLeaves(
            b.defaultBlockState(),
            world,
            pos
        ) || b.isWood(world, pos) || this.cb(b)
    }

    init {
        this.ab.add(Blocks.GRASS)
        this.ab.add(Blocks.DIRT)
        this.ab.add(Blocks.LOG)
        this.ab.add(Blocks.LOG2)
        this.ab.add(Blocks.SAPLING)
        this.ab.add(Blocks.VINE)
        this.am.add(Material.AIR)
        this.am.add(Material.LEAVES)
    }

    override fun generate(world: World, rand: Random, position: BlockPos): Boolean {
        val l: Int = rand.nextInt(3) + this.minTreeHeight
        var flag: Boolean = true
        if (position.getY() >= 1 && position.getY() + l + 1 <= 256) {
            for (i1: Int in position.getY()..position.getY() + 1 + l) {
                var b0: Byte = 1
                if (i1 == position.getY()) {
                    b0 = 0
                }

                if (i1 >= position.getY() + 1 + l - 2) {
                    b0 = 2
                }

                var j1: Int = position.getX() - b0
                while (j1 <= position.getX() + b0 && flag) {
                    var k1: Int = position.getZ() - b0
                    while (k1 <= position.getZ() + b0 && flag) {
                        if (i1 >= 0 && i1 < 256) {
                            val block: Block = world.getBlockState(BlockPos(j1, i1, k1)).getBlock()
                            if (!this.isReplaceable(world, BlockPos(j1, i1, k1))) {
                                flag = false
                            }
                        } else {
                            flag = false
                        }
                        k1++
                    }
                    j1++
                }
            }

            if (!flag) {
                return false
            } else {
                val block2: Block = world.getBlockState(BlockPos(position.below())).getBlock()
                val isSoil: Boolean = block2.canSustainPlant(
                    block2.defaultBlockState(),
                    world,
                    position.below(),
                    EnumFacing.UP,
                    this.bSapling as BlockSapling?
                )
                if (isSoil && position.getY() < 256 - l - 1) {
                    block2.onPlantGrow(block2.defaultBlockState(), world, position.below(), position)
                    val b0x: Byte = 3
                    val b1: Byte = 0

                    for (k1x: Int in position.getY() - b0x + l..position.getY() + l) {
                        val i3: Int = k1x - position.getY() - l
                        val l1: Int = b1 + 1 - i3 / 2

                        for (i2: Int in position.getX() - l1..position.getX() + l1) {
                            val j2: Int = i2 - position.getX()

                            for (k2: Int in position.getZ() - l1..position.getZ() + l1) {
                                val l2: Int = k2 - position.getZ()
                                if (abs(j2) != l1 || abs(l2) != l1 || rand.nextInt(2) != 0 && i3 != 0) {
                                    val block1: Block = world.getBlockState(BlockPos(i2, k1x, k2)).getBlock()
                                    if (block1.isAir(block1.defaultBlockState(), world, BlockPos(i2, k1x, k2))
                                        || block1.isLeaves(block1.defaultBlockState(), world, BlockPos(i2, k1x, k2))
                                    ) {
                                        this.setBlockAndNotifyAdequately(
                                            world,
                                            BlockPos(i2, k1x, k2),
                                            this.bLeaves.getStateFromMeta(this.metaLeaves)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    for (var21: Int in 0..<l) {
                        val block: Block = world.getBlockState(position.above(var21)).getBlock()
                        if (block.isAir(
                                block.defaultBlockState(),
                                world,
                                position.above(var21)
                            ) || block.isLeaves(block.defaultBlockState(), world, position.above(var21))
                        ) {
                            this.setBlockAndNotifyAdequately(
                                world,
                                position.above(var21),
                                this.bWood.getStateFromMeta(this.metaWood)
                            )
                            if (this.vinesGrow && var21 > 0) {
                                if (rand.nextInt(3) > 0 && world.isEmptyBlock(
                                        BlockPos(
                                            position.getX() - 1,
                                            position.getY() + var21,
                                            position.getZ()
                                        )
                                    )
                                ) {
                                    this.setBlockAndNotifyAdequately(
                                        world,
                                        BlockPos(position.getX() - 1, position.getY() + var21, position.getZ()),
                                        this.bVine.getStateFromMeta(8)
                                    )
                                }

                                if (rand.nextInt(3) > 0 && world.isEmptyBlock(
                                        BlockPos(
                                            position.getX() + 1,
                                            position.getY() + var21,
                                            position.getZ()
                                        )
                                    )
                                ) {
                                    this.setBlockAndNotifyAdequately(
                                        world,
                                        BlockPos(position.getX() + 1, position.getY() + var21, position.getZ()),
                                        this.bVine.getStateFromMeta(2)
                                    )
                                }

                                if (rand.nextInt(3) > 0 && world.isEmptyBlock(
                                        BlockPos(
                                            position.getX(),
                                            position.getY() + var21,
                                            position.getZ() - 1
                                        )
                                    )
                                ) {
                                    this.setBlockAndNotifyAdequately(
                                        world,
                                        BlockPos(position.getX(), position.getY() + var21, position.getZ() - 1),
                                        this.bVine.getStateFromMeta(1)
                                    )
                                }

                                if (rand.nextInt(3) > 0 && world.isEmptyBlock(
                                        BlockPos(
                                            position.getX(),
                                            position.getY() + var21,
                                            position.getZ() + 1
                                        )
                                    )
                                ) {
                                    this.setBlockAndNotifyAdequately(
                                        world,
                                        BlockPos(position.getX(), position.getY() + var21, position.getZ() + 1),
                                        this.bVine.getStateFromMeta(4)
                                    )
                                }
                            }
                        }
                    }

                    if (this.vinesGrow) {
                        for (var22: Int in position.getY() - 3 + l..position.getY() + l) {
                            val i3: Int = var22 - position.getY() - l
                            val l1: Int = 2 - i3 / 2

                            for (i2: Int in position.getX() - l1..position.getX() + l1) {
                                for (j2: Int in position.getZ() - l1..position.getZ() + l1) {
                                    if (world.getBlockState(BlockPos(i2, var22, j2))
                                            .getBlock()
                                            .isLeaves(
                                                world.getBlockState(BlockPos(i2, var22, j2)),
                                                world,
                                                BlockPos(i2, var22, j2)
                                            )
                                    ) {
                                        if (rand.nextInt(4) == 0
                                            && world.getBlockState(BlockPos(i2 - 1, var22, j2))
                                                .getBlock()
                                                .isAir(
                                                    world.getBlockState(BlockPos(i2 - 1, var22, j2)),
                                                    world,
                                                    BlockPos(i2 - 1, var22, j2)
                                                )
                                        ) {
                                            this.growVines(world, i2 - 1, var22, j2, 8)
                                        }

                                        if (rand.nextInt(4) == 0
                                            && world.getBlockState(BlockPos(i2 + 1, var22, j2))
                                                .getBlock()
                                                .isAir(
                                                    world.getBlockState(BlockPos(i2 + 1, var22, j2)),
                                                    world,
                                                    BlockPos(i2 + 1, var22, j2)
                                                )
                                        ) {
                                            this.growVines(world, i2 + 1, var22, j2, 2)
                                        }

                                        if (rand.nextInt(4) == 0
                                            && world.getBlockState(BlockPos(i2, var22, j2 - 1))
                                                .getBlock()
                                                .isAir(
                                                    world.getBlockState(BlockPos(i2, var22, j2 - 1)),
                                                    world,
                                                    BlockPos(i2, var22, j2 - 1)
                                                )
                                        ) {
                                            this.growVines(world, i2, var22, j2 - 1, 1)
                                        }

                                        if (rand.nextInt(4) == 0
                                            && world.getBlockState(BlockPos(i2, var22, j2 + 1))
                                                .getBlock()
                                                .isAir(
                                                    world.getBlockState(BlockPos(i2, var22, j2 + 1)),
                                                    world,
                                                    BlockPos(i2, var22, j2 + 1)
                                                )
                                        ) {
                                            this.growVines(world, i2, var22, j2 + 1, 4)
                                        }
                                    }
                                }
                            }
                        }

                        if (rand.nextInt(5) == 0 && l > 5) {
                            for (var23: Int in 0..1) {
                                for (i3: Int in 0..3) {
                                    if (rand.nextInt(4 - var23) == 0) {
                                        val var28: Int = rand.nextInt(3)
                                    }
                                }
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

    open fun growVines(world: World, x: Int, y: Int, z: Int, meta: Int) {
        var y: Int = y
        this.setBlockAndNotifyAdequately(world, BlockPos(x, y, z), this.bVine.getStateFromMeta(meta))
        var i1: Int = 4

        while (true) {
            y--
            if (!world.getBlockState(BlockPos(x, y, z)).getBlock()
                    .isAir(world.getBlockState(BlockPos(x, y, z)), world, BlockPos(x, y, z)) || i1 <= 0
            ) {
                return
            }

            this.setBlockAndNotifyAdequately(world, BlockPos(x, y, z), this.bVine.getStateFromMeta(meta))
            i1--
        }
    }
}
