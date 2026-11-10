package micdoodle8.mods.galacticraft.planets.venus.world.gen

import java.util.Random

import micdoodle8.mods.galacticraft.api.prefab.world.gen.MapGenBaseMeta
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.planets.venus.VenusBlocks
import micdoodle8.mods.galacticraft.planets.venus.blocks.BlockBasicVenus
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import net.minecraft.world.chunk.ChunkPrimer
import java.util.*

open class MapGenLavaVenus : MapGenBaseMeta() {
    protected open fun generateCaveNode(
        par1: Long,
        par3: Int,
        par4: Int,
        primer: ChunkPrimer,
        par6: Double,
        par8: Double,
        par10: Double,
        par12: Float,
        par13: Float,
        par14: Float,
        par15: Int,
        par16: Int,
        par17: Double
    ) {
        var par6: Double = par6
        var par8: Double = par8
        var par10: Double = par10
        var par13: Float = par13
        var par14: Float = par14
        var par15: Int = par15
        var par16: Int = par16
        val d4: Double = (par3 * 16 + 8).toDouble()
        val d5: Double = (par4 * 16 + 8).toDouble()
        var f3: Float = 0.0f
        var f4: Float = 0.0f
        val random: Random = Random(par1)

        if (par16 <= 0) {
            val j1: Int = this.range * 16 - 16
            par16 = j1 - random.nextInt(j1 / 4)
        }

        var flag: Boolean = false

        if (par15 == -1) {
            par15 = par16 / 2
            flag = true
        }

        val k1: Int = random.nextInt(par16 / 2) + par16 / 4

        val flag1: Boolean = random.nextInt(6) == 0
        while (par15 < par16) {
            val d6: Double = 0.5 + MathHelper.sin(par15 * Math.PI.toFloat() / par16) * par12 * 1.0f
            val d7: Double = d6 * par17
            val f5: Float = MathHelper.cos(par14)
            val f6: Float = MathHelper.sin(par14)
            par6 += (MathHelper.cos(par13) * f5).toDouble()
            par8 += f6.toDouble()
            par10 += (MathHelper.sin(par13) * f5).toDouble()

            if (flag1) {
                par14 *= 0.92f
            } else {
                par14 *= 0.7f
            }

            par14 += f4 * 0.1f
            par13 += f3 * 0.1f
            f4 *= 0.9f
            f3 *= 0.75f
            f4 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0f
            f3 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0f

            if (!flag && par15 == k1 && par12 > 1.0f && par16 > 0) {
                this.generateCaveNode(
                    random.nextLong(),
                    par3,
                    par4,
                    primer,
                    par6,
                    par8,
                    par10,
                    random.nextFloat() * 0.5f + 0.5f,
                    par13 - Constants.halfPI,
                    par14 / 3.0f,
                    par15,
                    par16,
                    1.0
                )
                this.generateCaveNode(
                    random.nextLong(),
                    par3,
                    par4,
                    primer,
                    par6,
                    par8,
                    par10,
                    random.nextFloat() * 0.5f + 0.5f,
                    par13 + Constants.halfPI,
                    par14 / 3.0f,
                    par15,
                    par16,
                    1.0
                )
                return
            }

            if (flag || random.nextInt(4) != 0) {
                val d8: Double = par6 - d4
                val d9: Double = par10 - d5
                val d10: Double = (par16 - par15).toDouble()
                val d11: Double = (par12 + 2.0f + 16.0f).toDouble()

                if (d8 * d8 + d9 * d9 - d10 * d10 > d11 * d11) {
                    return
                }

                if (par6 >= d4 - 16.0 - d6 * 2.0 && par10 >= d5 - 16.0 - d6 * 2.0 && par6 <= d4 + 16.0 + d6 * 2.0 && par10 <= d5 + 16.0 + d6 * 2.0) {
                    var l1: Int = MathHelper.floor(par6 - d6) - par3 * 16 - 1
                    var i2: Int = MathHelper.floor(par6 + d6) - par3 * 16 + 1
                    var j2: Int = MathHelper.floor(par8 - d7) - 1
                    val k2: Int = MathHelper.floor(par8 + d7) + 1
                    var l2: Int = MathHelper.floor(par10 - d6) - par4 * 16 - 1
                    var i3: Int = MathHelper.floor(par10 + d6) - par4 * 16 + 1

                    if (l1 < 0) {
                        l1 = 0
                    }

                    if (i2 > 16) {
                        i2 = 16
                    }

                    if (j2 < 1) {
                        j2 = 1
                    }

                    //                    if (k2 > 120)
//                    {
//                        k2 = 120;
//                    }
                    if (l2 < 0) {
                        l2 = 0
                    }

                    if (i3 > 16) {
                        i3 = 16
                    }

                    var j3: Int
                    j3 = l1
                    while (j3 < i2) {
                        for (l3: Int in l2..<i3) {
                            var i4: Int = k2 + 1
                            while (i4 >= j2 - 1) {
                                if (i4 >= 0 && i4 < 128) {
                                    if (i4 != j2 - 1 && j3 != l1 && j3 != i2 - 1 && l3 != l2 && l3 != i3 - 1) {
                                        i4 = j2
                                    }
                                }
                                --i4
                            }
                        }
                        ++j3
                    }

                    if (true) {
                        for (localY: Int in j2..<k2) {
                            val yfactor: Double = (localY + 0.5 - par8) / d7
                            val yfactorSq: Double = yfactor * yfactor

                            for (localX: Int in l1..<i2) {
                                val zfactor: Double = (localX + par3 * 16 + 0.5 - par6) / d6
                                val zfactorSq: Double = zfactor * zfactor

                                for (localZ: Int in l2..<i3) {
                                    val xfactor: Double = (localZ + par4 * 16 + 0.5 - par10) / d6
                                    val xfactorSq: Double = xfactor * xfactor

                                    if (xfactorSq + zfactorSq < 1.0) {
                                        if (yfactor > -0.7 && xfactorSq + yfactorSq + zfactorSq < 1.0) {
                                            val toReplace: IBlockState = primer.getBlockState(localX, localY, localZ)
                                            if (toReplace.getBlock() === VenusBlocks.venusBlock) {
                                                val type: BlockBasicVenus.EnumBlockBasicVenus =
                                                    toReplace.getValue(
                                                        BlockBasicVenus.BASIC_TYPE_VENUS
                                                    ) as BlockBasicVenus.EnumBlockBasicVenus
                                                if (type == BlockBasicVenus.EnumBlockBasicVenus.ROCK_HARD || type == BlockBasicVenus.EnumBlockBasicVenus.ROCK_SOFT) {
                                                    val north: IBlockState =
                                                        if (localZ == 0) Blocks.AIR.getDefaultState() else primer.getBlockState(
                                                            localX,
                                                            localY,
                                                            localZ - 1
                                                        )
                                                    val south: IBlockState =
                                                        if (localZ == 15) Blocks.AIR.getDefaultState() else primer.getBlockState(
                                                            localX,
                                                            localY,
                                                            localZ + 1
                                                        )
                                                    val east: IBlockState =
                                                        if (localX == 15) Blocks.AIR.getDefaultState() else primer.getBlockState(
                                                            localX + 1,
                                                            localY,
                                                            localZ
                                                        )
                                                    val west: IBlockState =
                                                        if (localX == 0) Blocks.AIR.getDefaultState() else primer.getBlockState(
                                                            localX - 1,
                                                            localY,
                                                            localZ
                                                        )
                                                    val below: IBlockState =
                                                        if (localY == 0) Blocks.AIR.getDefaultState() else primer.getBlockState(
                                                            localX,
                                                            localY - 1,
                                                            localZ
                                                        )
                                                    if (north.getBlock() === Blocks.AIR || south.getBlock() === Blocks.AIR || east.getBlock() === Blocks.AIR || west.getBlock() === Blocks.AIR || below.getBlock() === Blocks.AIR) {
                                                        primer.setBlockState(
                                                            localX,
                                                            localY,
                                                            localZ,
                                                            (VenusBlocks.venusBlock)!!.getDefaultState()
                                                                .withProperty(
                                                                    BlockBasicVenus.BASIC_TYPE_VENUS,
                                                                    BlockBasicVenus.EnumBlockBasicVenus.ROCK_MAGMA
                                                                )
                                                        )
                                                    } else {
                                                        primer.setBlockState(
                                                            localX,
                                                            localY,
                                                            localZ,
                                                            Blocks.LAVA.getDefaultState()
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (flag) {
                            break
                        }
                    }
                }
            }
            ++par15
        }
    }

    override fun recursiveGenerate(par1World: World?, par2: Int, par3: Int, par4: Int, par5: Int, primer: ChunkPrimer?) {
        var var7: Int = this.rand.nextInt(this.rand.nextInt(this.rand.nextInt(40) + 1) + 1)

        if (this.rand.nextInt(25) != 0) {
            var7 = 0
        }

        for (var8: Int in 0..<var7) {
            val var9: Double = (par2 * 16 + this.rand.nextInt(16)).toDouble()
            val var11: Double = this.rand.nextInt(this.rand.nextInt(120) + 8).toDouble()
            val var13: Double = (par3 * 16 + this.rand.nextInt(16)).toDouble()

            for (var16: Int in 0..0) {
                val var17: Float = this.rand.nextFloat() * Constants.twoPI
                val var18: Float = (this.rand.nextFloat() - 0.5f) * 2.0f / 8.0f

                this.generateCaveNode(
                    this.rand.nextLong(),
                    par4,
                    par5,
                    (primer)!!,
                    var9,
                    var11,
                    var13,
                    1.0f,
                    var17,
                    var18,
                    0,
                    0,
                    1.0
                )
            }
        }
    }
}
