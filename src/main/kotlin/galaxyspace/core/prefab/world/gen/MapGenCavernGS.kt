package galaxyspace.core.prefab.world.gen

import foundation.block.getStateFromMeta

import micdoodle8.mods.galacticraft.api.prefab.world.gen.MapGenBaseMeta
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.level.block.Blocks
import net.minecraft.util.Mth as MathHelper
import net.minecraft.world.level.Level as World
import net.minecraft.world.chunk.ChunkPrimer
import java.util.Random
import kotlin.math.abs

open class MapGenCavernGS constructor(private val vines: IBlockState, vararg state: IBlockState?) :
    MapGenBaseMeta() {
    private val blocks: Array<IBlockState?>

    init {
        this.blocks = (state as Array<IBlockState?>)
    }

    override fun generate(world: World, chunkX: Int, chunkZ: Int, primer: ChunkPrimer?) {
        val var6: Int = this.range
        this.world = world
        this.rand.setSeed(world.getSeed())
        val var7: Long = this.rand.nextLong()
        val var9: Long = this.rand.nextLong()

        for (var11: Int in chunkX - var6..chunkX + var6) {
            for (var12: Int in chunkZ - var6..chunkZ + var6) {
                val var13: Long = var11 * var7
                val var15: Long = var12 * var9
                this.rand.setSeed(var13 xor var15 xor world.getSeed())
                this.recursiveGenerate(world, var11, var12, chunkX, chunkZ, primer)
            }
        }
    }

    override fun recursiveGenerate(
        par1World: World?,
        xChunkCoord: Int,
        zChunkCoord: Int,
        origXChunkCoord: Int,
        origZChunkCoord: Int,
        primer: ChunkPrimer?
    ) {
        if (this.rand.nextInt(100) == 0) {
            val xPos: Double = (xChunkCoord * 16 + this.rand.nextInt(16)).toDouble()
            val yPos: Double = 25.0
            val zPos: Double = (zChunkCoord * 16 + this.rand.nextInt(16)).toDouble()
            this.generateLargeCaveNode(this.rand.nextLong(), origXChunkCoord, origZChunkCoord, (primer)!!, xPos, yPos, zPos)
        }
    }

    protected open fun generateLargeCaveNode(
        par1: Long,
        origXChunkCoord: Int,
        origZChunkCoord: Int,
        primer: ChunkPrimer,
        xPos: Double,
        yPos: Double,
        zPos: Double
    ) {
        this.generateCaveNode(
            par1,
            origXChunkCoord,
            origZChunkCoord,
            primer,
            xPos,
            yPos,
            zPos,
            1.0f + this.rand.nextFloat() * 6.0f,
            10.0f,
            10.0f,
            -1,
            -1,
            0.2
        )
    }

    protected open fun generateCaveNode(
        par1: Long,
        origXChunkCoord: Int,
        origZChunkCoord: Int,
        primer: ChunkPrimer,
        xPos: Double,
        yPos: Double,
        zPos: Double,
        par12: Float,
        par13: Float,
        par14: Float,
        par15: Int,
        par16: Int,
        heightMultiplier: Double
    ) {
        var par13: Float = par13
        var par14: Float = par14
        var par15: Int = par15
        var par16: Int = par16
        val var19: Double = (origXChunkCoord * 16 + 8).toDouble()
        val var21: Double = (origZChunkCoord * 16 + 8).toDouble()
        var var23: Float = 0.0f
        var var24: Float = 0.0f
        val var25: Random = Random(par1)

        if (par16 <= 0) {
            val var26: Int = this.range * 16 - 16
            par16 = var26 - var25.nextInt(var26 / 4)
        }

        var var54: Boolean = false

        if (par15 == -1) {
            par15 = par16 / 2
            var54 = true
        }

        val var27: Int = var25.nextInt(par16 / 2) + par16 / 4

        val var28: Boolean = var25.nextInt(6) == 0
        while (par15 < par16) {
            val caveWidth: Double = 40.0
            val caveHeight: Double = caveWidth * heightMultiplier

            if (var28) {
                par14 *= 0.92f
            } else {
                par14 *= 0.7f
            }

            par14 += var24 * 0.1f
            par13 += var23 * 0.1f
            var24 *= 0.9f
            var23 *= 0.75f
            var24 += (var25.nextFloat() - var25.nextFloat()) * var25.nextFloat() * 2.0f
            var23 += (var25.nextFloat() - var25.nextFloat()) * var25.nextFloat() * 4.0f

            if (!var54 && par15 == var27 && par12 > 1.0f && par16 > 0) {
                return
            }

            if (var54 || var25.nextInt(4) != 0) {
                val var35: Double = xPos - var19
                val var37: Double = zPos - var21
                val var39: Double = (par16 - par15).toDouble()
                val var41: Double = (par12 + 2.0f + 16.0f).toDouble()

                if (var35 * var35 + var37 * var37 - var39 * var39 > var41 * var41) {
                    return
                }

                if (xPos >= var19 - 16.0 - caveWidth * 2.0 && zPos >= var21 - 16.0 - caveWidth * 2.0 && xPos <= var19 + 16.0 + caveWidth * 2.0 && zPos <= var21 + 16.0 + caveWidth * 2.0)  // CHECKED
                {
                    var caveMinX: Int = MathHelper.floor(xPos - caveWidth) - origXChunkCoord * 16 - 1
                    var caveMaxX: Int = MathHelper.floor(xPos + caveWidth) - origXChunkCoord * 16 + 1
                    var caveMinY: Int = MathHelper.floor(yPos - caveHeight) - 1
                    var caveMaxY: Int = MathHelper.floor(yPos + caveHeight) + 1
                    var caveMinZ: Int = MathHelper.floor(zPos - caveWidth) - origZChunkCoord * 16 - 1
                    var caveMaxZ: Int = MathHelper.floor(zPos + caveWidth) - origZChunkCoord * 16 + 1

                    if (caveMinX < 0) {
                        caveMinX = 0
                    }

                    if (caveMaxX > 16) {
                        caveMaxX = 16
                    }

                    if (caveMinY < 1) {
                        caveMinY = 1
                    }

                    if (caveMaxY > 65) {
                        caveMaxY = 65
                    }

                    if (caveMinZ < 0) {
                        caveMinZ = 0
                    }

                    if (caveMaxZ > 16) {
                        caveMaxZ = 16
                    }

                    val isBlockWater: Boolean = false
                    var var42: Int
                    var var45: Int

                    var42 = caveMinX
                    while (!isBlockWater && var42 < caveMaxX) {
                        var var43: Int = caveMinZ
                        while (!isBlockWater && var43 < caveMaxZ) {
                            var var44: Int = caveMaxY + 1
                            while (!isBlockWater && var44 >= caveMinY - 1) {
                                var45 = (var42 * 16 + var43) * 128 + var44

                                if (var44 >= 0 && var44 < 128) {
                                    if (var44 != caveMinY - 1 && var42 != caveMinX && var42 != caveMaxX - 1 && var43 != caveMinZ && var43 != caveMaxZ - 1) {
                                        var44 = caveMinY
                                    }
                                }
                                --var44
                            }
                            ++var43
                        }
                        ++var42
                    }

                    var42 = caveMinX
                    while (var42 < caveMaxX) {
                        val var59: Double = (var42 + origXChunkCoord * 16 + 0.5 - xPos) / caveWidth

                        var45 = caveMinZ
                        while (var45 < caveMaxZ) {
                            val var46: Double = (var45 + origZChunkCoord * 16 + 0.5 - zPos) / caveWidth

                            if (var59 * var59 + var46 * var46 < 1.0) {
                                for (var50: Int in caveMaxY - 1 downTo caveMinY) {
                                    val var51: Double = (var50 + 0.5 - yPos) / caveHeight

                                    if (var59 * var59 + var51 * var51 + var46 * var46 < 1.0) {
                                        if (var51 > -0.7) {
                                            val block: IBlockState = primer.getBlockState(var42, var50, var45)

                                            var flag: Boolean = false
                                            for (state: IBlockState? in this.blocks) if (block == state) flag = true

                                            if (flag) {
                                                primer.setBlockState(var42, var50, var45, Blocks.AIR.defaultBlockState())
                                            }
                                        }
                                    }
                                }
                            }
                            ++var45
                        }
                        ++var42
                    }

                    var42 = caveMinX
                    while (var42 < caveMaxX) {
                        val var59: Double = (var42 + origXChunkCoord * 16 + 0.5 - xPos) / caveWidth

                        var45 = caveMinZ
                        while (var45 < caveMaxZ) {
                            val var46: Double = (var45 + origZChunkCoord * 16 + 0.5 - zPos) / caveWidth

                            if (var59 * var59 + var46 * var46 < 1.0) {
                                for (var50: Int in caveMaxY - 1 downTo caveMinY) {
                                    val var51: Double = (var50 + 0.5 - yPos) / caveHeight

                                    if (var59 * var59 + var51 * var51 + var46 * var46 < 1.0) {
                                        if (var51 > -0.7) {
                                            var block: Block = primer.getBlockState(var42, var50, var45).getBlock()
                                            val blockAbove: Block =
                                                primer.getBlockState(var42, var50 + 1, var45).getBlock()
                                            val blockBelow: Block =
                                                primer.getBlockState(var42, var50 - 1, var45).getBlock()

                                            if (Blocks.AIR === block) {
                                                if (blockAbove === this.blocks[2]!!.getBlock() && this.rand.nextInt(200) == 0) {
                                                    var modifier: Int = 0

                                                    while (Blocks.AIR === block && var50 + modifier > 0) {
                                                        primer.setBlockState(
                                                            var42,
                                                            var50 + modifier,
                                                            var45,
                                                            this.vines.getBlock().getStateFromMeta(abs(modifier) % 3)
                                                        )
                                                        modifier--
                                                        block = primer.getBlockState(var42, var50 + modifier, var45)
                                                            .getBlock()
                                                    }
                                                } /*
                                                else if (blockBelow == MarsBlocks.marsBlock && this.rand.nextInt(200) == 0)
                                                {
                                                    primer.setBlockState(var42, var50, var45, MarsBlocks.blockSludge.getStateFromMeta(0));
                                                }*/
                                            }
                                        }
                                    }
                                }
                            }
                            ++var45
                        }
                        ++var42
                    }
                }
            }
            ++par15
        }
    }
}
