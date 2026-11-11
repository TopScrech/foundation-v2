package galaxyspace.systems.ACentauriSystem.planets.proxima_b.world.gen.features

import foundation.block.getStateFromMeta

import galaxyspace.systems.ACentauriSystem.core.ACBlocks
import asmodeuscore.core.astronomy.dimension.world.gen.WorldGenCustomStructure
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import java.util.Random

open class WorldGenTest constructor(mode: Int) : WorldGenCustomStructure() {
    private var mode = 0

    @JvmField
    var MATRIX: Array<Array<ByteArray?>?> = arrayOf<Array<ByteArray?>?>(
        arrayOf<ByteArray?>(
            byteArrayOf(0, 1, 1, 0, 1),
            byteArrayOf(0, 1, 1, 1, 0),
            byteArrayOf(1, 0, 0, 1, 1),
            byteArrayOf(1, 1, 1, 1, 1),
            byteArrayOf(0, 1, 0, 1, 0)
        ), arrayOf<ByteArray?>(
            byteArrayOf(0, 0, 0, 0, 1),
            byteArrayOf(0, 1, 1, 0, 0),
            byteArrayOf(0, 1, 0, 1, 0),
            byteArrayOf(0, 1, 1, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0)
        ), arrayOf<ByteArray?>(
            byteArrayOf(0, 0, 1, 0, 0),
            byteArrayOf(0, 1, 0, 1, 0),
            byteArrayOf(0, 0, 1, 0, 0),
            byteArrayOf(0, 0, 1, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0)
        ), arrayOf<ByteArray?>(
            byteArrayOf(0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 1, 1, 0),
            byteArrayOf(0, 1, 1, 1, 0),
            byteArrayOf(0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0)
        )
    )

    @JvmField
    var MATRIX_1: Array<Array<ByteArray?>?> = arrayOf<Array<ByteArray?>?>(
        arrayOf<ByteArray?>(
            byteArrayOf(0, 1, 0, 0, 0),
            byteArrayOf(1, 0, 1, 1, 0),
            byteArrayOf(1, 1, 1, 1, 1),
            byteArrayOf(1, 0, 1, 1, 1),
            byteArrayOf(0, 1, 1, 0, 1)
        ), arrayOf<ByteArray?>(
            byteArrayOf(0, 1, 0, 0, 0),
            byteArrayOf(0, 0, 1, 1, 0),
            byteArrayOf(1, 1, 1, 0, 1),
            byteArrayOf(0, 1, 0, 1, 0),
            byteArrayOf(0, 0, 1, 0, 0)
        ), arrayOf<ByteArray?>(
            byteArrayOf(0, 0, 0, 0, 0),
            byteArrayOf(0, 1, 1, 1, 0),
            byteArrayOf(0, 1, 1, 0, 1),
            byteArrayOf(0, 1, 1, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0)
        ), arrayOf<ByteArray?>(
            byteArrayOf(0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 1, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0)
        )
    )

    @JvmField
    var MATRIX_2: Array<Array<ByteArray?>?> = arrayOf<Array<ByteArray?>?>(
        arrayOf<ByteArray?>(
            byteArrayOf(0, 1, 1, 0, 1),
            byteArrayOf(1, 1, 1, 1, 1),
            byteArrayOf(0, 1, 1, 1, 1),
            byteArrayOf(1, 1, 1, 1, 1),
            byteArrayOf(0, 1, 1, 0, 0)
        ), arrayOf<ByteArray?>(
            byteArrayOf(0, 0, 1, 0, 0),
            byteArrayOf(0, 1, 1, 1, 0),
            byteArrayOf(0, 1, 1, 1, 1),
            byteArrayOf(0, 1, 1, 1, 1),
            byteArrayOf(0, 0, 0, 0, 0)
        ), arrayOf<ByteArray?>(
            byteArrayOf(0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 1, 0, 0),
            byteArrayOf(0, 0, 1, 1, 0),
            byteArrayOf(0, 1, 0, 1, 0),
            byteArrayOf(0, 0, 0, 0, 0)
        ), arrayOf<ByteArray?>(
            byteArrayOf(0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 1, 1, 0),
            byteArrayOf(0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0)
        )
    )

    @JvmField
    var MATRIX_3: Array<Array<ByteArray?>?> = arrayOf<Array<ByteArray?>?>(
        arrayOf<ByteArray?>(
            byteArrayOf(1, 0, 0, 1, 1),
            byteArrayOf(0, 1, 1, 0, 1),
            byteArrayOf(1, 0, 1, 1, 0),
            byteArrayOf(0, 0, 1, 1, 0),
            byteArrayOf(1, 1, 0, 0, 1)
        ), arrayOf<ByteArray?>(
            byteArrayOf(0, 1, 0, 0, 0),
            byteArrayOf(0, 1, 1, 1, 1),
            byteArrayOf(0, 1, 1, 1, 0),
            byteArrayOf(1, 1, 1, 1, 0),
            byteArrayOf(0, 0, 0, 1, 0)
        ), arrayOf<ByteArray?>(
            byteArrayOf(0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 1, 0, 0),
            byteArrayOf(0, 1, 1, 1, 0),
            byteArrayOf(0, 1, 1, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0)
        ), arrayOf<ByteArray?>(
            byteArrayOf(0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 1, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0)
        )
    )

    init {
        this.mode = mode
    }

    override fun getLayersMatrix(): Array<Array<ByteArray?>?> {
        when (this.mode) {
            0 -> return MATRIX
            1 -> return MATRIX_1
            2 -> return MATRIX_2
            3 -> return MATRIX_3
            else -> return MATRIX
        }
    }

    override fun generateBlock(world: World?, random: Random?, position: BlockPos?, matrixValue: Int) {
        when (matrixValue) {
            1 -> (world)!!.setBlockAndUpdate(position, ACBlocks.PROXIMA_B_LOG_1.getStateFromMeta(0))
            2 -> {}
        }
    }
}
