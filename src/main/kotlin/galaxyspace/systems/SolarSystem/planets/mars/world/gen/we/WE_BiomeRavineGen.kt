package galaxyspace.systems.SolarSystem.planets.mars.world.gen.we

import galaxyspace.GalaxySpace
import asmodeuscore.core.utils.worldengine.additions.WE_GeneratorData
import asmodeuscore.core.utils.worldengine.additions.WE_CreateChunkGen_InXZ
import net.minecraft.block.BlockFalling
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.util.math.MathHelper
import java.util.ArrayList
import java.util.Random

open class WE_BiomeRavineGen constructor() : WE_CreateChunkGen_InXZ() {
    @JvmField
    var replaceBlocksList: MutableList<IBlockState?> = (ArrayList<Any?>() as MutableList<IBlockState?>)

    //-//
    @JvmField
    var caveBlock: IBlockState = Blocks.AIR.getDefaultState()

    @JvmField
    var lavaBlock: IBlockState = Blocks.LAVA.getDefaultState()

    @JvmField
    var lavaMaxY: Int = 12

    @JvmField
    var range: Int = 8

    @JvmField
    var field_75046_d: FloatArray = FloatArray(1024)

    @JvmField
    var rand: Random = Random()

    init {
        replaceBlocksList.add(Blocks.STONE.getDefaultState())
    }

    override fun gen(data: WE_GeneratorData?) {
        rand.setSeed((((data)!!.chunkProvider)!!.world)!!.getSeed())
        val rx: Long = rand.nextLong()
        val rz: Long = rand.nextLong()
        for (cx in (data)!!.chunk_X / 16L - range.toLong()..(data)!!.chunk_X / 16L + range.toLong()) for (cz in (data)!!.chunk_Z / 16L - range.toLong()..(data)!!.chunk_Z / 16L + range.toLong()) {
            val nv1: Long = cx * rx
            val nv2: Long = cz * rz
            rand.setSeed(nv1 xor nv2 xor (((data)!!.chunkProvider)!!.world)!!.getSeed())

            if (rand.nextInt(30) == 0) {
                val gx: Double = cx.toDouble() * 16.0 + rand.nextInt(16).toDouble()
                val gy: Double = rand.nextInt(rand.nextInt(40) + 8).toDouble() + 20.0
                val gz: Double = cz.toDouble() * 16.0 + rand.nextInt(16).toDouble()
                val b0: Byte = 1

                for (i1: Int in 0..<b0) {
                    val x: Float = rand.nextFloat() * 2.0f * Math.PI.toFloat()
                    val x1: Float = (rand.nextFloat() - 0.5f) * 0.25f
                    val x2: Float = (rand.nextFloat() * 2.0f + rand.nextFloat()) * 2.0f

                    caveGen_func((data)!!, rand.nextLong(), gx, gy, gz, x2, x, x1, 0, 0, 6.0)
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
        val d4: Double = data.chunk_X.toDouble() + 8.0
        val d5: Double = data.chunk_Z.toDouble() + 8.0
        var f3: Float = 0.0f
        var f4: Float = 0.0f
        val r: Random = Random(rs)


        if (in2 <= 0) {
            val p: Int = range * 16 - 16
            in2 = p - r.nextInt(p / 4)
        }

        var flag1: Boolean = false
        if (in1 == -1) {
            in1 = in2 / 2
            flag1 = true
        }

        var f5: Float = 1.0f
        for (k1: Int in 0..255) {
            if (k1 == 0 || r.nextInt(3) == 0) f5 = 1.0f + r.nextFloat() * r.nextFloat() * 1.0f
            field_75046_d[k1] = f5 * f5
        }

        while (in1 < in2) {
            var d12: Double =
                1.5 + MathHelper.sin(in1.toFloat() * Math.PI.toFloat() / in2.toFloat()).toDouble() * fn1.toDouble()
            var d6: Double = d12 * dn
            d12 *= r.nextFloat().toDouble() * 0.25 + 0.75
            d6 *= r.nextFloat().toDouble() * 0.25 + 0.75

            val f6: Float = MathHelper.cos(fn3)
            val f7: Float = MathHelper.sin(fn3)
            gx += MathHelper.cos(fn2).toDouble() * f6.toDouble()
            gy += f7.toDouble()
            gz += MathHelper.sin(fn2).toDouble() * f6.toDouble()

            fn3 *= 0.7f
            fn3 += f4 * 0.05f
            fn2 += f3 * 0.05f

            f4 *= 0.8f
            f3 *= 0.5f
            f4 += (r.nextFloat() - r.nextFloat()) * r.nextFloat() * 2.0f
            f3 += (r.nextFloat() - r.nextFloat()) * r.nextFloat() * 4.0f

            if (flag1 || r.nextInt(4) != 0) {
                val d7: Double = gx - d4
                val d8: Double = gz - d5
                val d9: Double = in2.toDouble() - in1.toDouble()
                val d10: Double = fn1.toDouble() + 18.0


                if (d7 * d7 + d8 * d8 - d9 * d9 > d10 * d10) return

                if (gx >= d4 - 16.0 - d12 * 2.0 && gz >= d5 - 16.0 - d12 * 2.0 && gx <= d4 + 16.0 + d12 * 2.0 && gz <= d5 + 16.0 + d12 * 2.0) {
                    var i4: Int = MathHelper.floor(gx - d12) - data.chunk_X.toInt() - 1
                    var l1: Int = MathHelper.floor(gx + d12) - data.chunk_X.toInt() + 1
                    var j4: Int = MathHelper.floor(gy - d6) - 1
                    var i2: Int = MathHelper.floor(gy + d6) + 1
                    var k4: Int = MathHelper.floor(gz - d12) - data.chunk_Z.toInt() - 1
                    var j2: Int = MathHelper.floor(gz + d12) - data.chunk_Z.toInt() + 1

                    if (i4 < 0) i4 = 0
                    if (l1 > 16) l1 = 16
                    if (j4 < 1) j4 = 1
                    if (i2 > 248) i2 = 248
                    if (k4 < 0) k4 = 0
                    if (j2 > 16) j2 = 16

                    var flag2: Boolean = false
                    var k2: Int
                    var j3: Int
                    //-//
                    k2 = i4
                    while (!flag2 && k2 < l1) {
                        var l2: Int = k4
                        while (!flag2 && l2 < j2) {
                            var i3: Int = i2 + 1
                            while (!flag2 && i3 >= j4 - 1) {
                                j3 = (k2 * 16 + l2) * 256 + i3
                                if (i3 >= 0 && i3 < 256) {
                                    if (isOceanBlock(data, j3, k2, i3, l2)) flag2 = true
                                    if (i3 != j4 - 1 && k2 != i4 && k2 != l1 - 1 && l2 != k4 && l2 != j2 - 1) i3 = j4
                                }
                                --i3
                            }
                            ++l2
                        }
                        ++k2
                    }
                    //-//
                    if (!flag2) {
                        k2 = i4
                        while (k2 < l1) {
                            val d13: Double = (k2.toDouble() + data.chunk_X.toDouble() + 0.5 - gx) / d12
                            j3 = k4
                            while (j3 < j2) {
                                val d14: Double = (j3.toDouble() + data.chunk_Z.toDouble() + 0.5 - gz) / d12
                                var k3: Int = (k2 * 16 + j3) * 256 + i2
                                if (d13 * d13 + d14 * d14 < 1.0) for (l3: Int in i2 - 1 downTo j4) {
                                    val d11: Double = (l3.toDouble() + 0.5 - gy) / d6
                                    if ((d13 * d13 + d14 * d14) * field_75046_d[l3].toDouble() + d11 * d11 / 6.0 < 1.0) digBlock(
                                        data,
                                        k3,
                                        k2,
                                        l3,
                                        j3
                                    )
                                    --k3
                                }
                                ++j3
                            }
                            ++k2
                        }
                        //-//
                        if (flag1) break
                    }
                }
            }
            ++in1
        }
    }

    open fun digBlock(data: WE_GeneratorData, index: Int, x: Int, y: Int, z: Int) {
        //GalaxySpace.debug(this.replaceBlocksList + "");
        if ((data.primer)!!.getBlockState(x, y, z).getBlock() is BlockFalling && (data.primer)!!.getBlockState(x, y + 1, z)
                .getBlock() is BlockFalling
        ) return

        for (i: Int in replaceBlocksList.indices) if ((data.primer)!!.getBlockState(x, y, z) === replaceBlocksList.get(i)) {
            if (y <= lavaMaxY) {
                (data.primer)!!.setBlockState(x, y, z, lavaBlock)
            } else {
                (data.primer)!!.setBlockState(x, y, z, caveBlock)
            }
            break
        }
    }

    open fun isOceanBlock(data: WE_GeneratorData, index: Int, x: Int, y: Int, z: Int): Boolean {
        if ((data.primer)!!.getBlockState(x, y, z) != null) return (data.primer)!!.getBlockState(x, y, z).getMaterial()
            .isLiquid()
        else return false
    }

    open fun addReplacingBlock(block: IBlockState?) {
        replaceBlocksList.add(block)
    }
}
