package asmodeuscore.core.astronomy.dimension.world.gen

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import net.minecraft.world.gen.feature.WorldGenerator
import java.util.Random

abstract class WorldGenCustomStructure : WorldGenerator() {
    /**
     * @return 3d matrix
     */
    protected abstract fun getLayersMatrix(): Array<Array<ByteArray?>?>

    /**
     * @return true, если нужна центровка
     */
    protected fun needCentering(): Boolean {
        return true
    }

    protected open fun rotate(): Int {
        return 0
    }

    /**
     * @param x, y, z - позиция блока
     * @param matrixValue - значение матрицы для этого блока
     * @return
     */
    protected abstract fun generateBlock(world: World?, random: Random?, position: BlockPos?, matrixValue: Int)

    override fun generate(world: World, rand: Random, position: BlockPos): Boolean {
        val matrix: Array<Array<ByteArray?>?> = getLayersMatrix()
        for (py: Int in matrix.indices) {
            for (px: Int in matrix[py]!!.indices) {
                for (pz: Int in matrix[py]!![px]!!.indices) {
                    var px2: Int = px
                    val py2: Int = py
                    var pz2: Int = pz
                    if (needCentering()) {
                        px2 -= matrix[py]!!.size / 2
                        pz2 -= matrix[py]!![px]!!.size / 2
                    }

                    var matrix1: Int = matrix[py]!![px]!![pz].toInt()

                    when (rotate()) {
                        0 -> {}
                        1 -> matrix1 = matrix[py]!![pz]!![px].toInt()
                        2 -> {
                            matrix1 =
                                matrix[py]!![(matrix[py]!!.size - 1) - px]!![(matrix[py]!![px]!!.size - 1) - pz].toInt()
                            matrix1 =
                                matrix[py]!![(matrix[py]!![px]!!.size - 1) - pz]!![(matrix[py]!!.size - 1) - px].toInt()
                        }

                        3 -> matrix1 =
                            matrix[py]!![(matrix[py]!![px]!!.size - 1) - pz]!![(matrix[py]!!.size - 1) - px].toInt()
                    }
                    generateBlock(
                        world,
                        rand,
                        BlockPos(position.getX() + px2, position.getY() + py2, position.getZ() + pz2),
                        matrix1
                    )
                }
            }
        }
        return true
    }
}
