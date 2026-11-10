package galaxyspace.systems.SolarSystem.moons.enceladus.world.gen

import galaxyspace.core.GSBlocks
import galaxyspace.systems.SolarSystem.moons.enceladus.world.gen.MapGenRavineEnceladus
import micdoodle8.mods.galacticraft.api.prefab.world.gen.MapGenBaseMeta
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import net.minecraft.world.biome.Biome
import net.minecraft.world.chunk.ChunkPrimer
import java.util.Random

open class MapGenRavineEnceladus : MapGenBaseMeta() {
    private val rs = FloatArray(1024)

    protected open fun addTunnel(
        randLong: Long, x: Int, z: Int, primer: ChunkPrimer,
        p_180707_6_: Double, p_180707_8_: Double, p_180707_10_: Double, p_180707_12_: Float, p_180707_13_: Float,
        p_180707_14_: Float, p_180707_15_: Int, p_180707_16_: Int, p_180707_17_: Double
    ) {
        var p_180707_6_: Double = p_180707_6_
        var p_180707_8_: Double = p_180707_8_
        var p_180707_10_: Double = p_180707_10_
        var p_180707_13_: Float = p_180707_13_
        var p_180707_14_: Float = p_180707_14_
        var p_180707_15_: Int = p_180707_15_
        var p_180707_16_: Int = p_180707_16_
        val random: Random = Random(randLong)
        val d0: Double = (x * 16 + 8).toDouble()
        val d1: Double = (z * 16 + 8).toDouble()
        var f: Float = 0.0f
        var f1: Float = 0.0f

        if (p_180707_16_ <= 0) {
            val i: Int = this.range * 860 - 16
            p_180707_16_ = i - random.nextInt(i / 4)
        }

        var flag1: Boolean = false

        if (p_180707_15_ == -1) {
            p_180707_15_ = p_180707_16_ / 2
            flag1 = true
        }

        var f2: Float = 1.0f

        for (j: Int in 0..255) {
            if (j == 0 || random.nextInt(3) == 0) {
                f2 = 1.0f + random.nextFloat() * random.nextFloat()
            }

            this.rs[j] = f2 * f2
        }

        while (p_180707_15_ < p_180707_16_) {
            //SIZE
            var d9: Double = 2.5 + (MathHelper.sin(p_180707_15_.toFloat() * Math.PI.toFloat() / p_180707_16_.toFloat())
                    * p_180707_12_).toDouble()
            var d2: Double = d9 * p_180707_17_
            d9 = d9 * (random.nextFloat().toDouble() * 0.25 + 0.75)
            d2 = d2 * (random.nextFloat().toDouble() * 0.25 + 0.75)
            val f3: Float = MathHelper.cos(p_180707_14_)
            val f4: Float = MathHelper.sin(p_180707_14_)
            p_180707_6_ += (MathHelper.cos(p_180707_13_) * f3).toDouble()
            p_180707_8_ += f4.toDouble()
            p_180707_10_ += (MathHelper.sin(p_180707_13_) * f3).toDouble()
            p_180707_14_ = p_180707_14_ * 0.7f
            p_180707_14_ = p_180707_14_ + f1 * 0.05f
            p_180707_13_ += f * 0.05f
            f1 = f1 * 0.8f
            f = f * 0.5f
            f1 = f1 + (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0f
            f = f + (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0f

            if (flag1 || random.nextInt(4) != 0) {
                val d3: Double = p_180707_6_ - d0
                val d4: Double = p_180707_10_ - d1
                val d5: Double = (p_180707_16_ - p_180707_15_).toDouble()
                val d6: Double = (p_180707_12_ + 2.0f + 16.0f).toDouble()

                if (d3 * d3 + d4 * d4 - d5 * d5 > d6 * d6) {
                    return
                }

                if (p_180707_6_ >= d0 - 16.0 - d9 * 2.0 && p_180707_10_ >= d1 - 16.0 - d9 * 2.0 && p_180707_6_ <= d0 + 16.0 + d9 * 2.0 && p_180707_10_ <= d1 + 16.0 + d9 * 2.0) {
                    var k2: Int = MathHelper.floor(p_180707_6_ - d9) - x * 16 - 1
                    var k: Int = MathHelper.floor(p_180707_6_ + d9) - x * 16 + 1
                    var l2: Int = MathHelper.floor(p_180707_8_ - d2) - 1
                    var l: Int = MathHelper.floor(p_180707_8_ + d2) + 1
                    var i3: Int = MathHelper.floor(p_180707_10_ - d9) - z * 16 - 1
                    var i1: Int = MathHelper.floor(p_180707_10_ + d9) - z * 16 + 1

                    if (k2 < 0) {
                        k2 = 0
                    }

                    if (k > 16) {
                        k = 16
                    }

                    if (l2 < 1) {
                        l2 = 1
                    }

                    if (l > 248) {
                        l = 248
                    }

                    if (i3 < 0) {
                        i3 = 0
                    }

                    if (i1 > 16) {
                        i1 = 16
                    }

                    var flag2: Boolean = false

                    var j1: Int = k2
                    while (!flag2 && j1 < k) {
                        var k1: Int = i3
                        while (!flag2 && k1 < i1) {
                            var l1: Int = l + 1
                            while (!flag2 && l1 >= l2 - 1) {
                                if (l1 >= 0 && l1 < 256) {
                                    if (isOceanBlock(primer, j1, l1, k1, x, z)) {
                                        flag2 = true
                                    }

                                    if (l1 != l2 - 1 && j1 != k2 && j1 != k - 1 && k1 != i3 && k1 != i1 - 1) {
                                        l1 = l2
                                    }
                                }
                                --l1
                            }
                            ++k1
                        }
                        ++j1
                    }

                    if (!flag2) {
                        for (j3: Int in k2..<k) {
                            val d10: Double = ((j3 + x * 16).toDouble() + 0.5 - p_180707_6_) / d9

                            for (i2: Int in i3..<i1) {
                                val d7: Double = ((i2 + z * 16).toDouble() + 0.5 - p_180707_10_) / d9
                                var flag: Boolean = false

                                if (d10 * d10 + d7 * d7 < 1.0) {
                                    for (j2: Int in l downTo l2 + 1) {
                                        val d8: Double = ((j2 - 1).toDouble() + 0.5 - p_180707_8_) / d2

                                        if ((d10 * d10 + d7 * d7) * this.rs[j2 - 1].toDouble() + d8 * d8 / 6.0 < 1.0) {
                                            if (isTopBlock(primer, j3, j2, i2, x, z)) {
                                                flag = true
                                            }

                                            digBlock(primer, j3, j2, i2, x, z, flag)
                                        }
                                    }
                                }
                            }
                        }

                        if (flag1) {
                            break
                        }
                    }
                }
            }
            ++p_180707_15_
        }
    }

    override fun recursiveGenerate(world: World?, chunkX: Int, chunkZ: Int, x: Int, z: Int, primer: ChunkPrimer?) {
        if (this.rand.nextInt(190) == 0) {
            val d0: Double = (chunkX * 16 + this.rand.nextInt(16)).toDouble()
            val d1: Double = (this.rand.nextInt(10) + 90).toDouble()
            val d2: Double = (chunkZ * 16 + this.rand.nextInt(16)).toDouble()
            val b0: Byte = 1

            for (i1: Int in 0..<b0) {
                val f: Float = this.rand.nextFloat() * Math.PI.toFloat() * 2.0f
                val f1: Float = (this.rand.nextFloat() - 0.5f) * 2.0f / 8.0f
                val f2: Float = (this.rand.nextFloat() * 2.0f + this.rand.nextFloat()) * 2.0f
                this.addTunnel(this.rand.nextLong(), x, z, (primer)!!, d0, d1, d2, f2, f, f1, 0, 0, 6.0)
            }
        }
    }

    protected open fun isOceanBlock(data: ChunkPrimer, x: Int, y: Int, z: Int, chunkX: Int, chunkZ: Int): Boolean {
        val block: Block = data.getBlockState(x, y, z).getBlock()
        return block === Blocks.FLOWING_WATER || block === Blocks.WATER
    }


    private fun isTopBlock(data: ChunkPrimer, x: Int, y: Int, z: Int, chunkX: Int, chunkZ: Int): Boolean {
        val biome: Biome = (world)!!.getBiome(BlockPos(x + chunkX * 16, 0, z + chunkZ * 16))
        val state: IBlockState = data.getBlockState(x, y, z)
        return state.getBlock() === biome.topBlock
    }

    protected open fun digBlock(
        data: ChunkPrimer,
        x: Int,
        y: Int,
        z: Int,
        chunkX: Int,
        chunkZ: Int,
        foundTop: Boolean
    ) {
        val biome: Biome = (world)!!.getBiome(BlockPos(x + chunkX * 16, 0, z + chunkZ * 16))
        val state: IBlockState = data.getBlockState(x, y, z)
        val top: IBlockState = biome.topBlock
        val filler: IBlockState = biome.fillerBlock

        if (state.getBlock() === GSBlocks.ENCELADUS_BLOCKS || state.getBlock() === Blocks.PACKED_ICE || state.getBlock() === top.getBlock() || state.getBlock() === filler.getBlock()) {
            data.setBlockState(x, y, z, AIR)

            if (foundTop && data.getBlockState(x, y - 1, z).getBlock() === filler.getBlock()) {
                data.setBlockState(x, y - 1, z, top.getBlock().getDefaultState())
            }

            if (data.getBlockState(x, y - 1, z).getBlock() === GSBlocks.ENCELADUS_BLOCKS) {
                data.setBlockState(x, y - 1, z, Blocks.PACKED_ICE.getDefaultState())
            }
            //}
        }
    }

    companion object {
        @JvmField
        protected val FLOWING_LAVA: IBlockState = Blocks.FLOWING_LAVA.getDefaultState()

        @JvmField
        protected val AIR: IBlockState = Blocks.AIR.getDefaultState()
    }
}
