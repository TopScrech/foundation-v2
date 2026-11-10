package galaxyspace.core.prefab.world.gen.we

import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.chunk.IChunkProvider
import net.minecraft.world.gen.IChunkGenerator
import net.minecraftforge.fml.common.IWorldGenerator
import java.util.Random

open class WE_LakesGen : IWorldGenerator {
    @JvmField
var lakeBlock: IBlockState = Blocks.WATER.getDefaultState()

    @JvmField
var lakeBlock_ice: IBlockState = Blocks.ICE.getDefaultState()

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
        if (random.nextInt(chunksForLake) == 0) {
            val x: Int = chunkX + 8
            val z: Int = chunkZ + 8 //y = minY + random.nextInt(maxY - minY + 1);
            val y: Int = if (underground) minY + random.nextInt(maxY - minY + 1) else world.getTopSolidOrLiquidBlock(
                BlockPos(
                    x,
                    0,
                    z
                )
            ).getY()
            //-//
            if (y >= minY && y <= maxY) {
                lake(world, random, x, y, z)
            }
        }
    }

    open fun lake(world: World, random: Random, x: Int, y: Int, z: Int) {
        var x: Int = x
        var y: Int = y
        var z: Int = z
        x -= 8
        z -= 8
        while (y > 5 && world.isAirBlock(BlockPos(x, y, z))) --y

        if (y <= 4) return
        else {
            y -= 4

            val aboolean: BooleanArray = BooleanArray(2048)
            for (w: Int in 0..<random.nextInt(4) + 4) {
                val d0: Double = random.nextDouble() * 6 + 3
                val d1: Double = random.nextDouble() * 4 + 2
                val d2: Double = random.nextDouble() * 6 + 3
                val d3: Double = random.nextDouble() * (14 - d0) + 1 + d0 / 2
                val d4: Double = random.nextDouble() * (4 - d1) + 2 + d1 / 2
                val d5: Double = random.nextDouble() * (14 - d2) + 1 + d2 / 2
                //-//
                for (bx: Int in 1..14) for (bz: Int in 1..14) for (by: Int in 1..6) {
                    val d6: Double = (bx.toDouble() - d3) * 2 / d0
                    val d7: Double = (by.toDouble() - d4) * 2 / d1
                    val d8: Double = (bz.toDouble() - d5) * 2 / d2
                    val d9: Double = d6 * d6 + d7 * d7 + d8 * d8
                    //-//
                    if (d9 < 1) aboolean[(bx * 16 + bz) * 8 + by] = true
                }
            }

            for (bx: Int in 0..15) for (bz: Int in 0..15) {
                for (by: Int in 0..7) {
                    if (!aboolean[(bx * 16 + bz) * 8 + by] && (bx < 15 && aboolean[((bx + 1) * 16 + bz) * 8 + by] || bx > 0 && aboolean[((bx - 1) * 16 + bz) * 8 + by] || bz < 15 && aboolean[(bx * 16 + bz + 1) * 8 + by] || bz > 0 && aboolean[(bx * 16 + bz - 1) * 8 + by] || by < 7 && aboolean[(bx * 16 + bz) * 8 + by + 1] || by > 0 && aboolean[(bx * 16 + bz) * 8 + by - 1])) {
                        val material: Material = world.getBlockState(BlockPos(x + bx, y + by, z + bz)).getMaterial()
                        if (by >= 4 && material.isLiquid()) return
                        if (by < 4 && !material.isSolid() && (world.getBlockState(
                                BlockPos(
                                    x + bx,
                                    y + by,
                                    z + bz
                                )
                            ) !== lakeBlock)
                        ) return
                    }
                    //-//
                    if (aboolean[(bx * 16 + bz) * 8 + by]) world.setBlockState(
                        BlockPos(x + bx, y + by, z + bz),
                        if (by >= 4) Blocks.AIR.getDefaultState() else lakeBlock,
                        if (update) 3 else 2
                    )
                }
                if (y + 4 >= iceY + random.nextInt(random_iceY + 1)) world.setBlockState(
                    BlockPos(
                        x + bx,
                        y + 4,
                        z + bz
                    ), lakeBlock_ice, if (update) 3 else 2
                )
            }
        }
    }
}
