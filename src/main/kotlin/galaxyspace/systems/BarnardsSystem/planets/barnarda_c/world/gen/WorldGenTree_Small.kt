package galaxyspace.systems.BarnardsSystem.planets.barnarda_c.world.gen

import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Logs.EnumAxis

import asmodeuscore.core.astronomy.dimension.world.gen.WorldGenCustomStructure
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Logs
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.world.gen.WorldGenTree_Small
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.Random

open class WorldGenTree_Small constructor(
    private val log: IBlockState,
    private val leaves: IBlockState,
    private val rotate: Int
) : WorldGenCustomStructure() {
    @JvmField
    var MATRIX: Array<Array<ByteArray?>?> = arrayOf<Array<ByteArray?>?>(
        arrayOf<ByteArray?>(
            byteArrayOf(0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 2, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0),
        ), arrayOf<ByteArray?>(
            byteArrayOf(0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 2, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0),
        ), arrayOf<ByteArray?>(
            byteArrayOf(0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 1, 0, 0),
            byteArrayOf(0, 0, 0, 2, 0, 0),
            byteArrayOf(0, 0, 0, 1, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0),
        ), arrayOf<ByteArray?>(
            byteArrayOf(0, 0, 0, 1, 0, 0),
            byteArrayOf(0, 0, 0, 0, 1, 0),
            byteArrayOf(0, 0, 3, 2, 3, 1),
            byteArrayOf(0, 0, 0, 0, 1, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0),
        ), arrayOf<ByteArray?>(
            byteArrayOf(0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 1, 1, 1, 0, 0),
            byteArrayOf(1, 2, 1, 1, 1, 0),
            byteArrayOf(1, 2, 1, 0, 0, 0),
            byteArrayOf(1, 3, 1, 0, 0, 0),
            byteArrayOf(0, 1, 0, 0, 0, 0),
        ), arrayOf<ByteArray?>(
            byteArrayOf(0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 1, 1, 1, 0, 0),
            byteArrayOf(1, 2, 1, 0, 0, 0),
            byteArrayOf(0, 1, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0),
        ), arrayOf<ByteArray?>(
            byteArrayOf(0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 1, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0, 0),
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
            2 -> (world)!!.setBlockState(
                position,
                this.log.withProperty(
                    Barnarda_C_Logs.LOG_AXIS,
                    Barnarda_C_Logs.EnumAxis.Y
                )
            )

            3 -> (world)!!.setBlockState(
                position,
                this.log.withProperty(
                    Barnarda_C_Logs.LOG_AXIS,
                    Barnarda_C_Logs.EnumAxis.X
                )
            )

            1, 7 -> (world)!!.setBlockState(position, this.leaves)
            else -> {}
        }
    }
}
