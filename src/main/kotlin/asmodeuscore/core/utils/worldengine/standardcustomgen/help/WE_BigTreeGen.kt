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
import net.minecraft.util.Mth as MathHelper
import net.minecraft.world.level.Level as World
import net.minecraft.world.gen.feature.WorldGenerator
import java.util.*
import kotlin.math.*

open class WE_BigTreeGen constructor(doBlockNotify: Boolean) : WorldGenerator(doBlockNotify) {
    @JvmField
    var bWood: Block = Blocks.LOG

    @JvmField
    var bLeaves: Block = Blocks.LEAVES

    @JvmField
    var bSapling: Block = Blocks.SAPLING

    @JvmField
    var otherCoordPairs: ByteArray = byteArrayOf(2, 0, 0, 1, 2, 1)

    @JvmField
    var trunkSize: Int = 1

    @JvmField
    var heightLimitLimit: Int = 12

    @JvmField
    var leafDistanceLimit: Int = 4

    @JvmField
    var metaLeaves: Int = 0

    @JvmField
    var metaWood: Int = 0

    @JvmField
    var heightAttenuation: Double = 0.618

    @JvmField
    var branchSlope: Double = 0.381

    @JvmField
    var scaleWidth: Double = 1.0

    @JvmField
    var leafDensity: Double = 1.0

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

    open fun isReplaceable(world: World, x: Int, y: Int, z: Int): Boolean {
        val b: Block = world.getBlockState(BlockPos(x, y, z)).getBlock()
        return b.isAir(b.defaultBlockState(), world, BlockPos(x, y, z))
                || b.isLeaves(b.defaultBlockState(), world, BlockPos(x, y, z))
                || b.isWood(world, BlockPos(x, y, z))
                || this.cb(b)
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
        var heightLimit: Int = 5 + rand.nextInt(this.heightLimitLimit)
        val basePos: IntArray = intArrayOf(position.getX(), position.getY(), position.getZ())
        val _aint: IntArray = intArrayOf(basePos[0], basePos[1], basePos[2])
        val _aint1: IntArray = intArrayOf(basePos[0], basePos[1] + heightLimit - 1, basePos[2])
        val block: Block = world.getBlockState(BlockPos(basePos[0], basePos[1] - 1, basePos[2])).getBlock()
        val br: Boolean
        if (!block.canSustainPlant(
                block.defaultBlockState(),
                world,
                BlockPos(basePos[0], basePos[1], basePos[2]).below(),
                EnumFacing.UP,
                this.bSapling as BlockSapling?
            )
        ) {
            br = false
        } else {
            val i: Int = this.checkBlockLine(world, _aint, _aint1)
            if (i == -1) {
                br = true
            } else if (i < 6) {
                br = false
            } else {
                heightLimit = i
                br = true
            }
        }

        if (!br) {
            return false
        } else {
            var height: Int = (heightLimit.toDouble() * this.heightAttenuation).toInt()
            if (height >= heightLimit) {
                height = heightLimit - 1
            }

            var ix: Int = (1.382 + (this.leafDensity * heightLimit.toDouble() / 13.0).pow(2.0)).toInt()
            if (ix < 1) {
                ix = 1
            }

            val aint: Array<IntArray?> = Array<IntArray?>(ix * heightLimit) { IntArray(4) }
            var j: Int = basePos[1] + heightLimit - this.leafDistanceLimit
            var k: Int = 1
            val l: Int = basePos[1] + height
            var i1: Int = j - basePos[1]
            aint[0]!![0] = basePos[0]
            aint[0]!![1] = j
            aint[0]!![2] = basePos[2]
            aint[0]!![3] = l
            j--

            while (i1 >= 0) {
                var j1: Int = 0
                var f2: Float
                if (i1.toDouble() < heightLimit.toDouble() * 0.3) {
                    f2 = -1.618f
                } else {
                    val f: Float = heightLimit.toFloat() / 2.0f
                    val f1: Float = heightLimit.toFloat() / 2.0f - i1.toFloat()
                    if (f1 == 0.0f) {
                        f2 = f
                    } else if (abs(f1) >= f) {
                        f2 = 0.0f
                    } else {
                        f2 = sqrt(abs(f).toDouble().pow(2.0) - abs(f1).toDouble().pow(2.0)).toFloat()
                    }

                    f2 *= 0.5f
                }

                if (f2 < 0.0f) {
                    j--
                    i1--
                } else {
                    val d0: Double = 0.5
                    while (j1 < ix) {
                        val d1: Double = this.scaleWidth * f2.toDouble() * (rand.nextFloat().toDouble() + 0.328)
                        val d2: Double = rand.nextFloat().toDouble() * 2.0 * Math.PI
                        val k1: Int = MathHelper.floor(d1 * sin(d2) + basePos[0].toDouble() + d0)
                        val l1: Int = MathHelper.floor(d1 * cos(d2) + basePos[2].toDouble() + d0)
                        val aint1: IntArray = intArrayOf(k1, j, l1)
                        val aint2: IntArray = intArrayOf(k1, j + this.leafDistanceLimit, l1)
                        if (this.checkBlockLine(world, aint1, aint2) == -1) {
                            val aint3: IntArray = intArrayOf(basePos[0], basePos[1], basePos[2])
                            val d3: Double = sqrt(
                                abs(basePos[0] - aint1[0]).toDouble().pow(2.0) + abs(basePos[2] - aint1[2]).toDouble()
                                    .pow(2.0)
                            )
                            val d4: Double = d3 * this.branchSlope
                            if (aint1[1].toDouble() - d4 > l.toDouble()) {
                                aint3[1] = l
                            } else {
                                aint3[1] = (aint1[1].toDouble() - d4).toInt()
                            }

                            if (this.checkBlockLine(world, aint3, aint1) == -1) {
                                aint[k]!![0] = k1
                                aint[k]!![1] = j
                                aint[k]!![2] = l1
                                aint[k]!![3] = aint3[1]
                                k++
                            }
                        }
                        j1++
                    }

                    j--
                    i1--
                }
            }

            val leafNodes: Array<IntArray> = ((Array<IntArray?>(k) { IntArray(4) } as Array<IntArray>?))!!
            System.arraycopy(aint, 0, leafNodes, 0, k)
            var i_1: Int = 0

            val j_1: Int = leafNodes.size
            while (i_1 < j_1) {
                var l_2: Int = leafNodes[i_1][1]

                val i1_2: Int = leafNodes[i_1][1] + this.leafDistanceLimit
                while (l_2 < i1_2) {
                    val p1: Float = (l_2 - leafNodes[i_1][1]).toFloat()
                    val p2: Float = if (!(p1 >= 0.0f) || !(p1 < this.leafDistanceLimit.toFloat()))
                        -1.0f
                    else
                        (if (p1 != 0.0f && p1 != (this.leafDistanceLimit - 1).toFloat()) 3.0f else 2.0f)
                    val b1_3: Byte = this.otherCoordPairs[1]
                    val b2_3: Byte = this.otherCoordPairs[4]
                    val aint_3: IntArray = intArrayOf(leafNodes[i_1][0], l_2, leafNodes[i_1][2])
                    val aint1_3: IntArray = intArrayOf(0, 0, 0)
                    val l_3: Int = (p2.toDouble() + 0.618).toInt()
                    var i1_3: Int = -l_3
                    var j1_3: Int = -l_3

                    aint1_3[1] = aint_3[1]
                    while (i1_3 <= l_3) {
                        aint1_3[b1_3.toInt()] = aint_3[b1_3.toInt()] + i1_3
                        j1_3 = -l_3

                        while (j1_3 <= l_3) {
                            val d0_3: Double =
                                (abs(i1_3).toDouble() + 0.5).pow(2.0) + (abs(j1_3).toDouble() + 0.5).pow(2.0)
                            if (d0_3 > (p2 * p2).toDouble()) {
                                j1_3++
                            } else {
                                aint1_3[b2_3.toInt()] = aint_3[b2_3.toInt()] + j1_3
                                val block1: Block =
                                    world.getBlockState(BlockPos(aint1_3[0], aint1_3[1], aint1_3[2])).getBlock()
                                if (!block1.isAir(
                                        block1.defaultBlockState(),
                                        world,
                                        BlockPos(aint1_3[0], aint1_3[1], aint1_3[2])
                                    )
                                    && !block1.isLeaves(
                                        block1.defaultBlockState(),
                                        world,
                                        BlockPos(aint1_3[0], aint1_3[1], aint1_3[2])
                                    )
                                ) {
                                    j1_3++
                                } else {
                                    val pos: BlockPos = BlockPos(aint1_3[0], aint1_3[1], aint1_3[2])
                                    this.setBlockAndNotifyAdequately(
                                        world,
                                        pos,
                                        this.bLeaves.getStateFromMeta(this.metaLeaves)
                                    )
                                    j1_3++
                                }
                            }
                        }
                        i1_3++
                    }
                    l_2++
                }
                i_1++
            }

            val aint_4: IntArray = intArrayOf(basePos[0], basePos[1], basePos[2])
            val aint1_4: IntArray = intArrayOf(basePos[0], basePos[1] + height, basePos[2])
            this.func_150530_a(world, aint_4, aint1_4, this.bWood)
            if (this.trunkSize == 2) {
                aint_4[0]++
                aint1_4[0]++
                this.func_150530_a(world, aint_4, aint1_4, this.bWood)
                aint_4[2]++
                aint1_4[2]++
                this.func_150530_a(world, aint_4, aint1_4, this.bWood)
                aint_4[0]--
                aint1_4[0]--
                this.func_150530_a(world, aint_4, aint1_4, this.bWood)
            }

            var i_5: Int = 0

            val aint_5: IntArray = intArrayOf(basePos[0], basePos[1], basePos[2])
            while (i_5 < leafNodes.size) {
                val aint1_5: IntArray = leafNodes[i_5]
                val aint2_5: IntArray = intArrayOf(aint1_5[0], aint1_5[1], aint1_5[2])
                aint_5[1] = aint1_5[3]
                val k_5: Int = aint_5[1] - basePos[1]
                if (k_5.toDouble() >= heightLimit.toDouble() * 0.2) {
                    this.func_150530_a(world, aint_5, aint2_5, this.bWood)
                }
                i_5++
            }

            return true
        }
    }

    open fun func_150530_a(worldObj: World, p_150530_1_: IntArray, p_150530_2_: IntArray, p_150530_3_: Block) {
        val aint2: IntArray = intArrayOf(0, 0, 0)
        var b0: Byte = 0

        var b1: Byte
        b1 = 0
        while (b0 < 3) {
            aint2[b0.toInt()] = p_150530_2_[b0.toInt()] - p_150530_1_[b0.toInt()]
            if (abs(aint2[b0.toInt()]) > abs(aint2[b1.toInt()])) {
                b1 = b0
            }
            b0++
        }

        if (aint2[b1.toInt()] != 0) {
            val b2: Byte = this.otherCoordPairs[b1.toInt()]
            val b3: Byte = this.otherCoordPairs[b1 + 3]
            val b4: Byte
            if (aint2[b1.toInt()] > 0) {
                b4 = 1
            } else {
                b4 = -1
            }

            val d0: Double = aint2[b2.toInt()].toDouble() / aint2[b1.toInt()].toDouble()
            val d1: Double = aint2[b3.toInt()].toDouble() / aint2[b1.toInt()].toDouble()
            val aint3: IntArray = intArrayOf(0, 0, 0)
            var i: Int = 0

            val j: Int = aint2[b1.toInt()] + b4
            while (i != j) {
                aint3[b1.toInt()] = MathHelper.floor((p_150530_1_[b1.toInt()] + i).toDouble() + 0.5)
                aint3[b2.toInt()] = MathHelper.floor(p_150530_1_[b2.toInt()].toDouble() + i.toDouble() * d0 + 0.5)
                aint3[b3.toInt()] = MathHelper.floor(p_150530_1_[b3.toInt()].toDouble() + i.toDouble() * d1 + 0.5)
                var b5: Byte = 0
                val k: Int = abs(aint3[0] - p_150530_1_[0])
                val l: Int = abs(aint3[2] - p_150530_1_[2])
                val i1: Int = max(k, l)
                if (i1 > 0) {
                    if (k == i1) {
                        b5 = 4
                    } else if (l == i1) {
                        b5 = 8
                    }
                }

                this.setBlockAndNotifyAdequately(
                    worldObj,
                    BlockPos(aint3[0], aint3[1], aint3[2]),
                    p_150530_3_.getStateFromMeta(b5.toInt())
                )
                i += b4.toInt()
            }
        }
    }

    open fun checkBlockLine(worldObj: World, p_76496_1_: IntArray, p_76496_2_: IntArray): Int {
        val aint2: IntArray = intArrayOf(0, 0, 0)
        var b0: Byte = 0

        var b1: Byte
        b1 = 0
        while (b0 < 3) {
            aint2[b0.toInt()] = p_76496_2_[b0.toInt()] - p_76496_1_[b0.toInt()]
            if (abs(aint2[b0.toInt()]) > abs(aint2[b1.toInt()])) {
                b1 = b0
            }
            b0++
        }

        if (aint2[b1.toInt()] == 0) {
            return -1
        } else {
            val b2: Byte = this.otherCoordPairs[b1.toInt()]
            val b3: Byte = this.otherCoordPairs[b1 + 3]
            val b4: Byte
            if (aint2[b1.toInt()] > 0) {
                b4 = 1
            } else {
                b4 = -1
            }

            val d0: Double = aint2[b2.toInt()].toDouble() / aint2[b1.toInt()].toDouble()
            val d1: Double = aint2[b3.toInt()].toDouble() / aint2[b1.toInt()].toDouble()
            val aint3: IntArray = intArrayOf(0, 0, 0)
            var i: Int = 0

            val j: Int
            j = aint2[b1.toInt()] + b4
            while (i != j) {
                aint3[b1.toInt()] = p_76496_1_[b1.toInt()] + i
                aint3[b2.toInt()] = MathHelper.floor(p_76496_1_[b2.toInt()].toDouble() + i.toDouble() * d0)
                aint3[b3.toInt()] = MathHelper.floor(p_76496_1_[b3.toInt()].toDouble() + i.toDouble() * d1)
                val block: Block = worldObj.getBlockState(BlockPos(aint3[0], aint3[1], aint3[2])).getBlock()
                if (!this.isReplaceable(worldObj, aint3[0], aint3[1], aint3[2])) {
                    break
                }
                i += b4.toInt()
            }

            return if (i == j) -1 else abs(i)
        }
    }
}
