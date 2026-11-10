package galaxyspace.core.prefab.world.gen

import galaxyspace.core.GSFluids
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import net.minecraft.world.chunk.IChunkProvider
import net.minecraft.world.gen.IChunkGenerator
import net.minecraftforge.fml.common.IWorldGenerator
import java.util.Random
import kotlin.math.max

open class WorldGenNaturalGas constructor(
    private val minGenerateLevel: Int,
    private val maxGenerateLevel: Int,
    private val amountPerVein: Int,
    private val replaceBlock: IBlockState?,
    private val chance: Int,
    private val dimId: Int
) : IWorldGenerator {
    override fun generate(
        random: Random, chunkX: Int, chunkZ: Int, world: World, chunkGenerator: IChunkGenerator?,
        chunkProvider: IChunkProvider?
    ) {
        if (world.provider.getDimension() == this.dimId) {
            if (random.nextInt(100) < chance) {
                val x: Int = chunkX * 16 + random.nextInt(16)
                val z: Int = chunkZ * 16 + random.nextInt(16)
                val y: Int =
                    random.nextInt(max(this.maxGenerateLevel - this.minGenerateLevel, 0)) + this.minGenerateLevel
                this.generateOre(world, random, x, y, z)
            }
        }
    }

    private fun generateOre(par1World: World, par2Random: Random, par3: Int, par4: Int, par5: Int): Boolean {
        val var6: Float = par2Random.nextFloat() * Math.PI.toFloat()
        val var7: Double = (par3 + 8 + MathHelper.sin(var6) * this.amountPerVein / 8.0f).toDouble()
        val var9: Double = (par3 + 8 - MathHelper.sin(var6) * this.amountPerVein / 8.0f).toDouble()
        val var11: Double = (par5 + 8 + MathHelper.cos(var6) * this.amountPerVein / 8.0f).toDouble()
        val var13: Double = (par5 + 8 - MathHelper.cos(var6) * this.amountPerVein / 8.0f).toDouble()
        val var15: Double = (par4 + par2Random.nextInt(3) - 2).toDouble()
        val var17: Double = (par4 + par2Random.nextInt(3) - 2).toDouble()

        for (var19: Int in 0..this.amountPerVein) {
            val var20: Double = var7 + (var9 - var7) * var19 / this.amountPerVein
            val var22: Double = var15 + (var17 - var15) * var19 / this.amountPerVein
            val var24: Double = var11 + (var13 - var11) * var19 / this.amountPerVein
            val var26: Double = par2Random.nextDouble() * this.amountPerVein / 16.0
            val var28: Double = (MathHelper.sin(var19 * Math.PI.toFloat() / this.amountPerVein) + 1.0f) * var26 + 1.0
            val var30: Double = (MathHelper.sin(var19 * Math.PI.toFloat() / this.amountPerVein) + 1.0f) * var26 + 1.0
            val var32: Int = MathHelper.floor(var20 - var28 / 2.0)
            val var33: Int = MathHelper.floor(var22 - var30 / 2.0)
            val var34: Int = MathHelper.floor(var24 - var28 / 2.0)
            val var35: Int = MathHelper.floor(var20 + var28 / 2.0)
            val var36: Int = MathHelper.floor(var22 + var30 / 2.0)
            val var37: Int = MathHelper.floor(var24 + var28 / 2.0)

            for (var38: Int in var32..var35) {
                val var39: Double = (var38 + 0.5 - var20) / (var28 / 2.0)

                if (var39 * var39 < 1.0) {
                    for (var41: Int in var33..var36) {
                        val var42: Double = (var41 + 0.5 - var22) / (var30 / 2.0)

                        if (var39 * var39 + var42 * var42 < 1.0) {
                            for (var44: Int in var34..var37) {
                                val var45: Double = (var44 + 0.5 - var24) / (var28 / 2.0)

                                val pos: BlockPos = BlockPos(var38, var41, var44)
                                val state: IBlockState = par1World.getBlockState(pos)

                                if (var39 * var39 + var42 * var42 + var45 * var45 < 1.0 && state === this.replaceBlock) {
                                    par1World.setBlockState(
                                        BlockPos(var38, var41, var44),
                                        (GSFluids.BLOCK_NATURE_GAS)!!.getDefaultState(),
                                        2
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        return true
    }
}
