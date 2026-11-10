package galaxyspace.systems.BarnardsSystem.planets.barnarda_c.world.gen

import asmodeuscore.core.astronomy.dimension.world.gen.WorldGenCustomStructure
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.world.gen.WorldGenTree_Swampland
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.Random

open class WorldGenTree_Swampland constructor(
    private val log: IBlockState,
    private val leaves: IBlockState,
    private val rotate: Int
) : WorldGenCustomStructure() {
    @JvmField
    var MATRIX: Array<Array<ByteArray?>?> = arrayOf<Array<ByteArray?>?>(
        arrayOf<ByteArray?>(
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 2, 2, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        ), arrayOf<ByteArray?>(
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 2, 3, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        ), arrayOf<ByteArray?>(
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 1, 3, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        ), arrayOf<ByteArray?>(
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 1, 3, 3, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        ), arrayOf<ByteArray?>(
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 2, 3, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        ), arrayOf<ByteArray?>(
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 1, 2, 1, 1, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        ), arrayOf<ByteArray?>(
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 1, 1, 2, 1, 1, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        ), arrayOf<ByteArray?>(
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        ), arrayOf<ByteArray?>(
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        ), arrayOf<ByteArray?>(
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        )

    )

    override fun getLayersMatrix(): Array<Array<ByteArray?>?> {
        return MATRIX
    }

    override fun rotate(): Int {
        return this.rotate
    }

    override fun generateBlock(world: World?, random: Random?, position: BlockPos?, matrixValue: Int) {
        when (matrixValue) {
            2, 3, 4 -> (world)!!.setBlockState(position, this.log)
            1, 7 -> (world)!!.setBlockState(position, this.leaves)
            else -> {}
        }
    }
}
