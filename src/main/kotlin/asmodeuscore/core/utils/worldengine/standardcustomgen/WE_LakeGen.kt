package asmodeuscore.core.utils.worldengine.standardcustomgen

import java.util.Random

import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.level.block.Blocks
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import net.minecraft.world.chunk.IChunkProvider
import net.minecraft.world.gen.IChunkGenerator
import net.minecraftforge.fml.common.IWorldGenerator
import java.util.*

open class WE_LakeGen : IWorldGenerator {
    @JvmField
    var lakeBlock: IBlockState = Blocks.WATER.defaultBlockState()

    @JvmField
    var lakeBlock_ice: IBlockState = Blocks.ICE.defaultBlockState()

    @JvmField
    var chunksForLake: Int = 12

    @JvmField
    var minY: Int = 0

    @JvmField
    var maxY: Int = 255

    @JvmField
    var iceY: Int = 111

    @JvmField
    var random_iceY: Int = 2

    @JvmField
    var iceGen: Boolean = true

    @JvmField
    var underground: Boolean = false

    @JvmField
    var update: Boolean = true

    override fun generate(
        random: Random,
        chunkX: Int,
        chunkZ: Int,
        world: World,
        chunkGenerator: IChunkGenerator?,
        chunkProvider: IChunkProvider?
    ) {
        if (random.nextInt(this.chunksForLake) == 0) {
            val x: Int = chunkX + 8
            val z: Int = chunkZ + 8
            val y: Int =
                if (this.underground) this.minY + random.nextInt(this.maxY - this.minY + 1) else world.getTopSolidOrLiquidBlock(
                    BlockPos(x, 0, z)
                ).getY()
            if (y >= this.minY && y <= this.maxY) {
                this.lake(world, random, x, y, z)
            }
        }
    }

    open fun lake(world: World, random: Random, x: Int, y: Int, z: Int) {
        var x: Int = x
        var y: Int = y
        var z: Int = z
        x -= 8
        z -= 8

        while (y > 5 && world.isEmptyBlock(BlockPos(x, y, z))) {
            y--
        }

        if (y > 4) {
            y -= 4
            val aboolean: BooleanArray = BooleanArray(2048)

            for (w: Int in 0..<random.nextInt(4) + 4) {
                val d0: Double = random.nextDouble() * 6.0 + 3.0
                val d1: Double = random.nextDouble() * 4.0 + 2.0
                val d2: Double = random.nextDouble() * 6.0 + 3.0
                val d3: Double = random.nextDouble() * (14.0 - d0) + 1.0 + d0 / 2.0
                val d4: Double = random.nextDouble() * (4.0 - d1) + 2.0 + d1 / 2.0
                val d5: Double = random.nextDouble() * (14.0 - d2) + 1.0 + d2 / 2.0

                for (bx: Int in 1..14) {
                    for (bz: Int in 1..14) {
                        for (by: Int in 1..6) {
                            val d6: Double = (bx.toDouble() - d3) * 2.0 / d0
                            val d7: Double = (by.toDouble() - d4) * 2.0 / d1
                            val d8: Double = (bz.toDouble() - d5) * 2.0 / d2
                            val d9: Double = d6 * d6 + d7 * d7 + d8 * d8
                            if (d9 < 1.0) {
                                aboolean[(bx * 16 + bz) * 8 + by] = true
                            }
                        }
                    }
                }
            }

            for (bx: Int in 0..15) {
                for (bz: Int in 0..15) {
                    for (byx: Int in 0..7) {
                        if (!aboolean[(bx * 16 + bz) * 8 + byx]
                            && (bx < 15 && aboolean[((bx + 1) * 16 + bz) * 8 + byx] || bx > 0 && aboolean[((bx - 1) * 16 + bz) * 8 + byx] || bz < 15 && aboolean[(bx * 16 + bz + 1) * 8 + byx] || bz > 0 && aboolean[(bx * 16 + bz - 1) * 8 + byx] || byx < 7 && aboolean[(bx * 16 + bz) * 8 + byx + 1] || byx > 0 && aboolean[(bx * 16 + bz) * 8 + byx - 1]
                                    )
                        ) {
                            val material: Material =
                                world.getBlockState(BlockPos(x + bx, y + byx, z + bz)).getMaterial()
                            if (byx >= 4 && material.isLiquid()) {
                                return
                            }

                            if (byx < 4 && !material.isSolid() && world.getBlockState(
                                    BlockPos(
                                        x + bx,
                                        y + byx,
                                        z + bz
                                    )
                                ) !== this.lakeBlock
                            ) {
                                return
                            }
                        }

                        if (aboolean[(bx * 16 + bz) * 8 + byx]) {
                            world.setBlock(
                                BlockPos(x + bx, y + byx, z + bz),
                                if (byx >= 4) Blocks.AIR.defaultBlockState() else this.lakeBlock,
                                if (this.update) 3 else 2
                            )
                        }
                    }

                    if (this.iceGen && y + 4 >= this.iceY + random.nextInt(this.random_iceY + 1)) {
                        world.setBlock(
                            BlockPos(x + bx, y + 4, z + bz),
                            this.lakeBlock_ice,
                            if (this.update) 3 else 2
                        )
                    }
                }
            }
        }
    }
}
