package asmodeuscore.core.utils.worldengine.standardcustomgen

import java.util.ArrayList
import java.util.Random

import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import net.minecraft.world.chunk.IChunkProvider
import net.minecraft.world.gen.IChunkGenerator
import net.minecraftforge.fml.common.IWorldGenerator
import java.util.*

open class WE_GrassGen : IWorldGenerator {
    @JvmField
    var flowerList: MutableList<IBlockState?> = ArrayList<IBlockState?>()

    @JvmField
    var blocksForFlower: MutableList<Int> = ArrayList<Int>()

    @JvmField
    var waterGen: MutableList<Boolean> = ArrayList<Boolean>()

    @JvmField
    var surface: MutableList<IBlockState?> = ArrayList<IBlockState?>()

    open fun add(flower: IBlockState?, count: Int, onWater: Boolean, surf: IBlockState?) {
        this.flowerList.add(flower)
        this.blocksForFlower.add(count)
        this.waterGen.add(onWater)
        this.surface.add(surf)
    }

    override fun generate(
        random: Random,
        chunkX: Int,
        chunkZ: Int,
        world: World,
        chunkGenerator: IChunkGenerator?,
        chunkProvider: IChunkProvider?
    ) {
        for (i: Int in this.flowerList.indices) {
            if (random.nextInt(this.blocksForFlower.get(i)!!) <= 256) {
                var i2: Int = 0
                while (i2.toFloat() < this.blocksForFlower.get(i)!!.toFloat()) {
                    val x: Int = chunkX + random.nextInt(16) + 8
                    val z: Int = chunkZ + random.nextInt(16) + 8
                    val pos: BlockPos = world.getTopSolidOrLiquidBlock(BlockPos(x, 0, z))
                    if (this.waterGen.get(i)) {
                        val state: IBlockState = world.getBlockState(pos)
                        if (state.getBlock() === this.surface.get(i)!!.getBlock() && world.isEmptyBlock(pos.above())) {
                            world.setBlock(pos.above(), this.flowerList.get(i), 2)
                        }
                    } else {
                        val state: IBlockState = world.getBlockState(pos.below())
                        if (state.getBlock() === this.surface.get(i)!!.getBlock()) {
                            world.setBlock(pos, this.flowerList.get(i), 2)
                        }
                    }
                    i2++
                }
            }
        }
    }
}
