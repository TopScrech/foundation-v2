package asmodeuscore.core.utils.worldengine.standardcustomgen

import foundation.block.LegacyVanillaBlockStates
import foundation.block.LegacyVanillaBlockIds

import foundation.block.getStateFromMeta

import foundation.math.LegacyTerrainMath

import java.util.ArrayList
import java.util.Random

import asmodeuscore.core.utils.worldengine.additions.WE_CreateChunkGen
import asmodeuscore.core.utils.worldengine.additions.WE_GeneratorData
import net.minecraft.block.BlockFalling
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.level.block.Blocks
import net.minecraft.util.Mth as MathHelper
import java.util.*

open class WE_CaveGen constructor() : WE_CreateChunkGen() {
    @JvmField
    var replaceBlocksList: MutableList<IBlockState?> = ArrayList<IBlockState?>()

    @JvmField
    var caveBlock: IBlockState = Blocks.AIR.defaultBlockState()

    @JvmField
    var lavaBlock: IBlockState = Blocks.LAVA.defaultBlockState()

    @JvmField
    var ignoreLiquid: Boolean = false

    @JvmField
    var lavaMaxY: Int = 12

    @JvmField
    var maxY: Int = 120

    @JvmField
    var range: Int = 8

    @JvmField
    var d6_range: Double = 1.5

    @JvmField
    var frequency: Int = 15

    @JvmField
    var rand: Random = Random()

    init {
        this.replaceBlocksList.add(Blocks.STONE.defaultBlockState())
        this.replaceBlocksList.add(LegacyVanillaBlockStates.state(LegacyVanillaBlockIds.STONE, 3))
        this.replaceBlocksList.add(LegacyVanillaBlockStates.state(LegacyVanillaBlockIds.STONE, 5))
    }

    override fun gen(data: WE_GeneratorData?) {
        this.rand.setSeed((((data)!!.chunkProvider)!!.world)!!.getSeed())
        val rx: Long = this.rand.nextLong()
        val rz: Long = this.rand.nextLong()

        for (cx in (data)!!.chunk_X / 16L - this.range.toLong()..(data)!!.chunk_X / 16L + this.range.toLong()) {
            for (cz in (data)!!.chunk_Z / 16L - this.range.toLong()..(data)!!.chunk_Z / 16L + this.range.toLong()) {
                val nv1: Long = cx * rx
                val nv2: Long = cz * rz
                this.rand.setSeed(nv1 xor nv2 xor (((data)!!.chunkProvider)!!.world)!!.getSeed())
                var e: Int = this.rand.nextInt(this.rand.nextInt(this.rand.nextInt(this.frequency) + 1) + 1)
                if (this.rand.nextInt(7) != 0) {
                    e = 0
                }

                for (i: Int in 0..<e) {
                    val gx: Double = cx.toDouble() * 16.0 + this.rand.nextInt(16).toDouble()
                    val gy: Double = this.rand.nextInt(this.rand.nextInt(this.maxY) + 16).toDouble()
                    val gz: Double = cz.toDouble() * 16.0 + this.rand.nextInt(16).toDouble()
                    var k: Int = 1
                    if (this.rand.nextInt(4) == 0) {
                        this.caveGen_func(
                            (data)!!,
                            this.rand.nextLong(),
                            gx,
                            gy,
                            gz,
                            1.0f + this.rand.nextFloat() * 6.0f,
                            0.0f,
                            0.0f,
                            -1,
                            -1,
                            0.5
                        )
                        k += this.rand.nextInt(4)
                    }

                    for (l1: Int in 0..<k) {
                        val x: Float = this.rand.nextFloat() * 2.0f * Math.PI.toFloat()
                        val x1: Float = (this.rand.nextFloat() - 0.5f) * 0.25f
                        var x2: Float = this.rand.nextFloat() * 2.0f + this.rand.nextFloat()
                        if (this.rand.nextInt(10) == 0) {
                            x2 *= this.rand.nextFloat() * this.rand.nextFloat() * 3.0f + 1.0f
                        }

                        this.caveGen_func((data)!!, this.rand.nextLong(), gx, gy, gz, x2, x, x1, 0, 0, 1.0)
                    }
                }
            }
        }
    }

    open fun caveGen_func(
        data: WE_GeneratorData,
        rs: Long,
        gx: Double,
        gy: Double,
        gz: Double,
        fn1: Float,
        fn2: Float,
        fn3: Float,
        in1: Int,
        in2: Int,
        dn: Double
    ) {
        var gx: Double = gx
        var gy: Double = gy
        var gz: Double = gz
        var fn2: Float = fn2
        var fn3: Float = fn3
        var in1: Int = in1
        var in2: Int = in2
        val cx: Double = data.chunk_X.toDouble() + 8.0
        val cz: Double = data.chunk_Z.toDouble() + 8.0
        var f3: Float = 0.0f
        var f4: Float = 0.0f
        val r: Random = Random(rs)
        if (in2 <= 0) {
            val p: Int = this.range * 16 - 16
            in2 = p - r.nextInt(p / 4)
        }

        var flag2: Boolean = false
        if (in1 == -1) {
            in1 = in2 / 2
            flag2 = true
        }

        val k1: Int = r.nextInt(in2 / 2) + in2 / 4

        val flag: Boolean = r.nextInt(6) == 0
        while (in1 < in2) {
            val d6: Double = this.d6_range + LegacyTerrainMath.sin(in1.toFloat() * Math.PI.toFloat() / in2.toFloat())
                .toDouble() * fn1.toDouble()
            val d7: Double = d6 * dn
            val f5: Float = LegacyTerrainMath.cos(fn3)
            val f6: Float = LegacyTerrainMath.sin(fn3)
            gx += LegacyTerrainMath.cos(fn2).toDouble() * f5.toDouble()
            gy += f6.toDouble()
            gz += LegacyTerrainMath.sin(fn2).toDouble() * f5.toDouble()
            if (flag) {
                fn3 *= 0.92f
            } else {
                fn3 *= 0.7f
            }

            fn3 += f4 * 0.1f
            fn2 += f3 * 0.1f
            f4 *= 0.9f
            f3 *= 0.75f
            f4 += (r.nextFloat() - r.nextFloat()) * r.nextFloat() * 2.0f
            f3 += (r.nextFloat() - r.nextFloat()) * r.nextFloat() * 4.0f
            if (!flag2 && in1 == k1 && fn1 > 1.0f && in2 > 0) {
                this.caveGen_func(
                    data,
                    r.nextLong(),
                    gx,
                    gy,
                    gz,
                    r.nextFloat() * 0.5f + 0.5f,
                    fn2 - (Math.PI / 2).toFloat(),
                    fn3 / 3.0f,
                    in1,
                    in2,
                    1.0
                )
                this.caveGen_func(
                    data,
                    r.nextLong(),
                    gx,
                    gy,
                    gz,
                    r.nextFloat() * 0.5f + 0.5f,
                    fn2 + (Math.PI / 2).toFloat(),
                    fn3 / 3.0f,
                    in1,
                    in2,
                    1.0
                )
                return
            }

            if (flag2 || r.nextInt(4) != 0) {
                val d8: Double = gx - cx
                val d9: Double = gz - cz
                val d10: Double = in2.toDouble() - in1.toDouble()
                val d11: Double = fn1.toDouble() + 18.0
                if (d8 * d8 + d9 * d9 - d10 * d10 > d11 * d11) {
                    return
                }

                if (gx >= cx - 16.0 - d6 * 2.0 && gz >= cz - 16.0 - d6 * 2.0 && gx <= cx + 16.0 + d6 * 2.0 && gz <= cz + 16.0 + d6 * 2.0) {
                    var i4: Int = MathHelper.floor(gx - d6) - data.chunk_X.toInt() - 1
                    var l1: Int = MathHelper.floor(gx + d6) - data.chunk_X.toInt() + 1
                    var j4: Int = MathHelper.floor(gy - d7) - 1
                    var i2: Int = MathHelper.floor(gy + d7) + 1
                    var k4: Int = MathHelper.floor(gz - d6) - data.chunk_Z.toInt() - 1
                    var j2: Int = MathHelper.floor(gz + d6) - data.chunk_Z.toInt() + 1
                    if (i4 < 0) {
                        i4 = 0
                    }

                    if (l1 > 16) {
                        l1 = 16
                    }

                    if (j4 < 1) {
                        j4 = 1
                    }

                    if (this.caveBlock.getMaterial() === Material.WATER && j4 < 10) {
                        j4 = 10
                    }

                    if (i2 > 248) {
                        i2 = 248
                    }

                    if (k4 < 0) {
                        k4 = 0
                    }

                    if (j2 > 16) {
                        j2 = 16
                    }

                    var flag3: Boolean = false

                    var k2: Int = i4
                    while (!flag3 && k2 < l1) {
                        var l2: Int = k4
                        while (!flag3 && l2 < j2) {
                            var i3: Int = i2 + 1
                            while (!flag3 && i3 >= j4 - 1) {
                                val j3: Int = (k2 * 16 + l2) * 256 + i3
                                if (i3 >= 0 && i3 < 256) {
                                    if (!this.ignoreLiquid && this.isOceanBlock(data, j3, k2, i3, l2)) {
                                        flag3 = true
                                    }

                                    if (i3 != j4 - 1 && k2 != i4 && k2 != l1 - 1 && l2 != k4 && l2 != j2 - 1) {
                                        i3 = j4
                                    }
                                }
                                i3--
                            }
                            l2++
                        }
                        k2++
                    }

                    if (!flag3) {
                        for (var62: Int in i4..<l1) {
                            val d13: Double = (var62.toDouble() + data.chunk_X.toDouble() + 0.5 - gx) / d6

                            for (j3: Int in k4..<j2) {
                                val d14: Double = (j3.toDouble() + data.chunk_Z.toDouble() + 0.5 - gz) / d6
                                var k3: Int = (var62 * 16 + j3) * 256 + i2
                                if (d13 * d13 + d14 * d14 < 1.0) {
                                    for (l3: Int in i2 - 1 downTo j4) {
                                        val d12: Double = (l3.toDouble() + 0.5 - gy) / d7
                                        if (d12 > -0.7 && d13 * d13 + d12 * d12 + d14 * d14 < 1.0) {
                                            this.digBlock(data, k3, var62, l3, j3)
                                        }

                                        k3--
                                    }
                                }
                            }
                        }

                        if (flag2) {
                            break
                        }
                    }
                }
            }
            in1++
        }
    }

    open fun digBlock(data: WE_GeneratorData, index: Int, x: Int, y: Int, z: Int) {
        if ((data.primer)!!.getBlockState(x, y, z).getBlock() !is BlockFalling || (data.primer)!!.getBlockState(x, y + 1, z)
                .getBlock() !is BlockFalling
        ) {
            for (i: Int in this.replaceBlocksList.indices) {
                if ((data.primer)!!.getBlockState(x, y, z) === this.replaceBlocksList.get(i)) {
                    if (y <= this.lavaMaxY) {
                        (data.primer)!!.setBlockState(x, y, z, this.lavaBlock)
                    } else {
                        (data.primer)!!.setBlockState(x, y, z, this.caveBlock)
                    }
                    break
                }
            }
        }
    }

    open fun isOceanBlock(data: WE_GeneratorData, index: Int, x: Int, y: Int, z: Int): Boolean {
        return if ((data.primer)!!.getBlockState(x, y, z) != null) (data.primer)!!.getBlockState(x, y, z).getMaterial()
            .isLiquid() else false
    }

    open fun addReplacingBlock(block: IBlockState?) {
        this.replaceBlocksList.add(block)
    }
}
