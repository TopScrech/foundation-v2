package galaxyspace.systems.ACentauriSystem.planets.proxima_b.world.gen.features

import foundation.block.getStateFromMeta

import galaxyspace.systems.ACentauriSystem.core.ACBlocks
import galaxyspace.systems.ACentauriSystem.planets.proxima_b.world.gen.features.WorldGenMushroom
import asmodeuscore.core.astronomy.dimension.world.gen.WorldGenCustomStructure
import micdoodle8.mods.galacticraft.planets.venus.VenusBlocks
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import java.util.Random

open class WorldGenMushroom : WorldGenCustomStructure() {
    @JvmField
    var MATRIX_SPHERE: Array<Array<ByteArray?>?> = arrayOf<Array<ByteArray?>?>(
        arrayOf<ByteArray?>(
            byteArrayOf(0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 1, 0, 0),
            byteArrayOf(0, 1, 1, 1, 0),
            byteArrayOf(0, 0, 1, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0)
        ), arrayOf<ByteArray?>(
            byteArrayOf(0, 0, 1, 0, 0),
            byteArrayOf(0, 1, 1, 1, 0),
            byteArrayOf(1, 1, 2, 1, 1),
            byteArrayOf(0, 1, 1, 1, 0),
            byteArrayOf(0, 0, 1, 0, 0)
        ), arrayOf<ByteArray?>(
            byteArrayOf(0, 0, 0, 0, 0),
            byteArrayOf(0, 1, 1, 1, 0),
            byteArrayOf(0, 1, 2, 1, 0),
            byteArrayOf(0, 1, 1, 1, 0),
            byteArrayOf(0, 0, 0, 0, 0)
        ), arrayOf<ByteArray?>(
            byteArrayOf(0, 0, 0, 0, 0),
            byteArrayOf(0, 0, 1, 0, 0),
            byteArrayOf(0, 1, 3, 1, 0),
            byteArrayOf(0, 0, 1, 0, 0),
            byteArrayOf(0, 0, 0, 0, 0)
        )
    )

    override fun getLayersMatrix(): Array<Array<ByteArray?>?> {
        return MATRIX_SPHERE
    }

    override fun generateBlock(world: World?, random: Random?, position: BlockPos?, matrixValue: Int) {
        when (matrixValue) {
            1 -> (world)!!.setBlockAndUpdate(position, ACBlocks.PROXIMA_B_BLOCKS.getStateFromMeta(12))
            2 -> if ((random)!!.nextInt(5) == 0) (world)!!.setBlockAndUpdate(position, (VenusBlocks.sulphuricAcid)!!.defaultBlockState())
            3 -> (world)!!.setBlockAndUpdate(position, ACBlocks.PROXIMA_B_BLOCKS.getStateFromMeta(13))
            else -> {}
        }
    }

    override fun generate(world: World, rand: Random, position: BlockPos): Boolean {
        this.genLegs(world, rand, position)

        val yX: Int = 5 + rand.nextInt(4)
        for (i: Int in 0..<yX / 2) world.setBlockAndUpdate(
            position.above(yX - i),
            ACBlocks.PROXIMA_B_MUSHROOM_LOG.getStateFromMeta(0)
        )


        return super.generate(world, rand, position.above(yX))
    }

    private fun genLegs(world: World, rand: Random, position: BlockPos) {
        var pos: BlockPos = position
        when (rand.nextInt(2)) {
            0 -> {
                world.setBlockAndUpdate(pos.east(), ACBlocks.PROXIMA_B_MUSHROOM_LOG.getStateFromMeta(0))
                world.setBlockAndUpdate(pos.south(3), ACBlocks.PROXIMA_B_MUSHROOM_LOG.getStateFromMeta(0))
                world.setBlockAndUpdate(pos.north(2).west(2), ACBlocks.PROXIMA_B_MUSHROOM_LOG.getStateFromMeta(0))
                pos = pos.above()
                world.setBlockAndUpdate(pos.east(), ACBlocks.PROXIMA_B_MUSHROOM_LOG.getStateFromMeta(0))
                world.setBlockAndUpdate(pos.south(2), ACBlocks.PROXIMA_B_MUSHROOM_LOG.getStateFromMeta(0))
                world.setBlockAndUpdate(pos.north(2).west(2), ACBlocks.PROXIMA_B_MUSHROOM_LOG.getStateFromMeta(0))
                pos = pos.above()
                world.setBlockAndUpdate(pos, ACBlocks.PROXIMA_B_MUSHROOM_LOG.getStateFromMeta(0))
                world.setBlockAndUpdate(pos.east(), ACBlocks.PROXIMA_B_MUSHROOM_LOG.getStateFromMeta(4))
                world.setBlockAndUpdate(pos.south(2), ACBlocks.PROXIMA_B_MUSHROOM_LOG.getStateFromMeta(0))
                world.setBlockAndUpdate(pos.north(1).west(1), ACBlocks.PROXIMA_B_MUSHROOM_LOG.getStateFromMeta(8))
                pos = pos.above()
                world.setBlockAndUpdate(pos, ACBlocks.PROXIMA_B_MUSHROOM_LOG.getStateFromMeta(0))
                world.setBlockAndUpdate(pos.south(), ACBlocks.PROXIMA_B_MUSHROOM_LOG.getStateFromMeta(8))
                pos = pos.above()
                world.setBlockAndUpdate(pos, ACBlocks.PROXIMA_B_MUSHROOM_LOG.getStateFromMeta(0))
            }

            1 -> {
                world.setBlockAndUpdate(pos.south(2).east(2), ACBlocks.PROXIMA_B_MUSHROOM_LOG.getStateFromMeta(0))
                world.setBlockAndUpdate(pos.west(2), ACBlocks.PROXIMA_B_MUSHROOM_LOG.getStateFromMeta(0))
                world.setBlockAndUpdate(pos.north(3), ACBlocks.PROXIMA_B_MUSHROOM_LOG.getStateFromMeta(0))
                pos = pos.above()
                world.setBlockAndUpdate(pos.south(2).east(2), ACBlocks.PROXIMA_B_MUSHROOM_LOG.getStateFromMeta(0))
                world.setBlockAndUpdate(pos.west(1), ACBlocks.PROXIMA_B_MUSHROOM_LOG.getStateFromMeta(0))
                world.setBlockAndUpdate(pos.north(3), ACBlocks.PROXIMA_B_MUSHROOM_LOG.getStateFromMeta(0))
                pos = pos.above()
                world.setBlockAndUpdate(pos, ACBlocks.PROXIMA_B_MUSHROOM_LOG.getStateFromMeta(0))
                world.setBlockAndUpdate(pos.south(1).east(1), ACBlocks.PROXIMA_B_MUSHROOM_LOG.getStateFromMeta(4))
                world.setBlockAndUpdate(pos.west(1), ACBlocks.PROXIMA_B_MUSHROOM_LOG.getStateFromMeta(4))
                world.setBlockAndUpdate(pos.north(3), ACBlocks.PROXIMA_B_MUSHROOM_LOG.getStateFromMeta(0))
                pos = pos.above()
                world.setBlockAndUpdate(pos, ACBlocks.PROXIMA_B_MUSHROOM_LOG.getStateFromMeta(0))
                world.setBlockAndUpdate(pos.north(2), ACBlocks.PROXIMA_B_MUSHROOM_LOG.getStateFromMeta(0))
                pos = pos.above()
                world.setBlockAndUpdate(pos, ACBlocks.PROXIMA_B_MUSHROOM_LOG.getStateFromMeta(0))
                world.setBlockAndUpdate(pos.north(), ACBlocks.PROXIMA_B_MUSHROOM_LOG.getStateFromMeta(4))
            }
        }
    }
}
