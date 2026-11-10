package micdoodle8.mods.galacticraft.planets.venus.world.gen

import java.util.Random

import micdoodle8.mods.galacticraft.planets.venus.VenusBlocks
import micdoodle8.mods.galacticraft.planets.venus.blocks.BlockBasicVenus
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.world.EnumSkyBlock
import net.minecraft.world.World
import net.minecraft.world.biome.Biome
import net.minecraft.world.gen.feature.WorldGenerator
import java.util.*

open class WorldGenLakesVenus constructor() : WorldGenerator() {
    override fun generate(worldIn: World, rand: Random, position: BlockPos): Boolean {
        var position: BlockPos = position
        position = position.add(-8, 0, -8)
        while (position.getY() > 5 && worldIn.isAirBlock(position)) {
            position = position.down()
        }

        if (position.getY() <= 4) {
            return false
        } else {
            position = position.down(4)
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
                        val flag: Boolean =
                            !aboolean[(k1 * 16 + l2) * 8 + k] && (k1 < 15 && aboolean[((k1 + 1) * 16 + l2) * 8 + k] || k1 > 0 && aboolean[((k1 - 1) * 16 + l2) * 8 + k] || l2 < 15 && aboolean[(k1 * 16 + l2 + 1) * 8 + k] || l2 > 0 && aboolean[(k1 * 16 + (l2 - 1)) * 8 + k] || k < 7 && aboolean[(k1 * 16 + l2) * 8 + k + 1] || k > 0 && aboolean[(k1 * 16 + l2) * 8 + (k - 1)])

                        if (flag) {
                            val state: IBlockState = worldIn.getBlockState(position.add(k1, k, l2))
                            val material: Material = state.getMaterial()

                            if (k >= 4 && material.isLiquid()) {
                                return false
                            }

                            if (k < 4 && !material.isSolid() && worldIn.getBlockState(position.add(k1, k, l2))
                                    .getBlock() !== Blocks.LAVA
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
                            worldIn.setBlockState(
                                position.add(l1, i4, i3),
                                if (i4 >= 4) Blocks.AIR.getDefaultState() else Blocks.LAVA.getDefaultState(),
                                2
                            )
                        }
                    }
                }
            }

            for (i2: Int in 0..15) {
                for (j3: Int in 0..15) {
                    for (j4: Int in 4..7) {
                        if (aboolean[(i2 * 16 + j3) * 8 + j4]) {
                            val blockpos: BlockPos = position.add(i2, j4 - 1, j3)

                            if (worldIn.getBlockState(blockpos).getBlock() === Blocks.DIRT && worldIn.getLightFor(
                                    EnumSkyBlock.SKY,
                                    position.add(i2, j4, j3)
                                ) > 0
                            ) {
                                val biomegenbase: Biome = worldIn.getBiome(blockpos)

                                if (biomegenbase.topBlock.getBlock() === Blocks.MYCELIUM) {
                                    worldIn.setBlockState(blockpos, Blocks.MYCELIUM.getDefaultState(), 2)
                                } else {
                                    worldIn.setBlockState(blockpos, Blocks.GRASS.getDefaultState(), 2)
                                }
                            }
                        }
                    }
                }
            }

            for (j2: Int in 0..15) {
                for (k3: Int in 0..15) {
                    for (k4: Int in 0..7) {
                        val flag1: Boolean =
                            !aboolean[(j2 * 16 + k3) * 8 + k4] && (j2 < 15 && aboolean[((j2 + 1) * 16 + k3) * 8 + k4] || j2 > 0 && aboolean[((j2 - 1) * 16 + k3) * 8 + k4] || k3 < 15 && aboolean[(j2 * 16 + k3 + 1) * 8 + k4] || k3 > 0 && aboolean[(j2 * 16 + (k3 - 1)) * 8 + k4] || k4 < 7 && aboolean[(j2 * 16 + k3) * 8 + k4 + 1] || k4 > 0 && aboolean[(j2 * 16 + k3) * 8 + (k4 - 1)])

                        val state: IBlockState = worldIn.getBlockState(position.add(j2, k4, k3))
                        if (flag1 && (k4 < 4 || rand.nextInt(2) != 0) && state.getMaterial().isSolid()) {
                            worldIn.setBlockState(
                                position.add(j2, k4, k3),
                                (VenusBlocks.venusBlock)!!.getDefaultState()
                                    .withProperty(
                                        BlockBasicVenus.BASIC_TYPE_VENUS,
                                        BlockBasicVenus.EnumBlockBasicVenus.ROCK_MAGMA
                                    ),
                                2
                            )
                        }
                    }
                }
            }

            return true
        }
    }
}
