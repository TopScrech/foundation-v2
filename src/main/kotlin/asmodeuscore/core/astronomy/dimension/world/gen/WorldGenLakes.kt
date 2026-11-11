package asmodeuscore.core.astronomy.dimension.world.gen

import foundation.block.getStateFromMeta

import foundation.level.offset

import net.minecraft.world.level.block.Block
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.level.block.Blocks
import net.minecraft.core.BlockPos
import net.minecraft.world.EnumSkyBlock
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.gen.feature.WorldGenerator
import net.minecraftforge.common.ForgeModContainer
import java.util.Random

open class WorldGenLakes constructor(
    private val block: Block,
    private val meta: Int,
    private val spreadBlock: Block,
    private val spreadBlockMeta: Int
) : WorldGenerator() {
    private var addBlock: IBlockState? = null
    private var chance = 0

    open fun addAdditions(state: IBlockState?, chance: Int): WorldGenLakes {
        this.addBlock = state
        this.chance = chance
        return this
    }

    override fun generate(worldIn: World, rand: Random, position: BlockPos): Boolean {
        var position: BlockPos = position
        position = position.offset(-8, 0, -8)
        while (position.getY() > 5
            && worldIn.isEmptyBlock(position)
        ) {
            position = position.below()
        }

        if (position.getY() <= 4) {
            return false
        } else {
            position = position.below(4)
            val aboolean: BooleanArray = BooleanArray(2048)
            val i: Int = rand.nextInt(4) + 4

            for (j: Int in 0..<i) {
                val d0: Double = rand.nextDouble() * 6.0 + 3.0
                val d1: Double = rand.nextDouble() * 4.0 + 2.0
                val d2: Double = rand.nextDouble() * 6.0 + 3.0
                val d3: Double = rand.nextDouble() * (16.0 - d0 - 2.0) + 1.0 + d0 / 2.0
                val d4: Double = rand.nextDouble() * (8.0 - d1 - 4.0) + 2.0 + d1 / 2.0
                val d5: Double = rand.nextDouble() * (16.0 - d2 - 2.0) + 1.0 + d2 / 2.0

                for (l: Int in 1..14) {
                    for (i1: Int in 1..14) {
                        for (j1: Int in 1..6) {
                            val d6: Double = (l.toDouble() - d3) / (d0 / 2.0)
                            val d7: Double = (j1.toDouble() - d4) / (d1 / 2.0)
                            val d8: Double = (i1.toDouble() - d5) / (d2 / 2.0)
                            val d9: Double = d6 * d6 + d7 * d7 + d8 * d8

                            if (d9 < 1.0) {
                                aboolean[(l * 16 + i1) * 8 + j1] = true
                            }
                        }
                    }
                }
            }

            for (k1: Int in 0..15) {
                for (l2: Int in 0..15) {
                    for (k: Int in 0..7) {
                        val flag: Boolean = !aboolean[(k1 * 16 + l2) * 8 + k]
                                && (k1 < 15 && aboolean[((k1 + 1) * 16 + l2) * 8 + k] || k1 > 0 && aboolean[((k1 - 1) * 16 + l2) * 8 + k] || l2 < 15 && aboolean[(k1 * 16 + l2 + 1) * 8 + k] || l2 > 0 && aboolean[(k1 * 16 + (l2 - 1)) * 8 + k] || k < 7 && aboolean[(k1 * 16 + l2) * 8 + k + 1] || k > 0 && aboolean[(k1 * 16 + l2) * 8 + (k - 1)])

                        if (flag) {
                            val material: Material = worldIn.getBlockState(position.offset(k1, k, l2)).getMaterial()

                            if (k >= 4 && material.isLiquid()) {
                                return false
                            }

                            if (k < 4 && !material.isSolid() && worldIn.getBlockState(position.offset(k1, k, l2))
                                    .getBlock() !== this.block
                            ) {
                                return false
                            }
                        }
                    }
                }
            }

            for (l1: Int in 0..15) {
                for (i3: Int in 0..15) {
                    for (i4: Int in 0..7) {
                        if (aboolean[(l1 * 16 + i3) * 8 + i4]) {
                            worldIn.setBlock(
                                position.offset(l1, i4, i3),
                                if (i4 >= 4) Blocks.AIR.defaultBlockState() else this.block.defaultBlockState(), 2
                            )
                        }
                    }
                }
            }

            for (i2: Int in 0..15) {
                for (j3: Int in 0..15) {
                    for (j4: Int in 4..7) {
                        if (aboolean[(i2 * 16 + j3) * 8 + j4]) {
                            val blockpos: BlockPos = position.offset(i2, j4 - 1, j3)

                            if (worldIn.getBlockState(blockpos).getBlock() === Blocks.DIRT
                                && worldIn.getLightFor(EnumSkyBlock.SKY, position.offset(i2, j4, j3)) > 0
                            ) {
                                val biome: Biome = worldIn.getBiome(blockpos)

                                if (biome.topBlock.getBlock() === Blocks.MYCELIUM) {
                                    worldIn.setBlock(blockpos, Blocks.MYCELIUM.defaultBlockState(), 2)
                                } else {
                                    worldIn.setBlock(blockpos, Blocks.GRASS.defaultBlockState(), 2)
                                }
                            }
                        }
                    }
                }
            }

            if (this.block.getStateFromMeta(meta).getMaterial() === Material.LAVA) {
                for (j2: Int in 0..15) {
                    for (k3: Int in 0..15) {
                        for (k4: Int in 0..7) {
                            val flag1: Boolean = !aboolean[(j2 * 16 + k3) * 8 + k4]
                                    && (j2 < 15 && aboolean[((j2 + 1) * 16 + k3) * 8 + k4] || j2 > 0 && aboolean[((j2 - 1) * 16 + k3) * 8 + k4] || k3 < 15 && aboolean[(j2 * 16 + k3 + 1) * 8 + k4] || k3 > 0 && aboolean[(j2 * 16 + (k3 - 1)) * 8 + k4] || k4 < 7 && aboolean[(j2 * 16 + k3) * 8 + k4 + 1] || k4 > 0 && aboolean[(j2 * 16 + k3) * 8 + (k4 - 1)])

                            if (flag1 && (k4 < 4 || rand.nextInt(2) != 0)
                                && worldIn.getBlockState(position.offset(j2, k4, k3)).getMaterial().isSolid()
                            ) {
                                if (this.chance > 0 && this.chance <= 100 && rand.nextInt(101 - this.chance) == 0) worldIn.setBlock(
                                    position.offset(j2, k4, k3),
                                    this.addBlock,
                                    2
                                )
                                else worldIn.setBlock(
                                    position.offset(j2, k4, k3),
                                    this.spreadBlock.getStateFromMeta(spreadBlockMeta),
                                    2
                                )
                            }
                        }
                    }
                }
            }

            if (this.block.getStateFromMeta(meta).getMaterial() === Material.WATER) {
                for (k2: Int in 0..15) {
                    for (l3: Int in 0..15) {
                        val l4: Int = 4

                        if (worldIn.canBlockFreezeWater(position.offset(k2, 4, l3))) {
                            val flag: Int = if (ForgeModContainer.fixVanillaCascading) 2 or 16 else 2 // Forge:
                            // With
                            // bit
                            // 5
                            // unset,
                            // it
                            // will
                            // notify
                            // neighbors
                            // and
                            // load
                            // adjacent
                            // chunks.
                            worldIn.setBlock(position.offset(k2, 4, l3), Blocks.ICE.defaultBlockState(), flag) // Forge
                        }
                    }
                }
            }

            return true
        }
    }
}
