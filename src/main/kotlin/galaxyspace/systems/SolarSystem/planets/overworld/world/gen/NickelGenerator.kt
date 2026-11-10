package galaxyspace.systems.SolarSystem.planets.overworld.world.gen

import galaxyspace.core.GSBlocks
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.BlockOres.EnumBlockOres
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.BlockOres
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.planets.asteroids.blocks.AsteroidBlocks
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.chunk.IChunkProvider
import net.minecraft.world.gen.IChunkGenerator
import net.minecraftforge.fml.common.IWorldGenerator
import java.util.Random
import kotlin.math.max

open class NickelGenerator : IWorldGenerator {
    private val asteroidBlock: IBlockState = (AsteroidBlocks.blockBasic)!!.getStateFromMeta(0)
    private val nickelBlock: IBlockState = GSBlocks.OVERWORLD_ORES.getDefaultState()
        .withProperty(BlockOres.BASIC_TYPE, EnumBlockOres.NICKEL)
    private val copperBlock: IBlockState = (GCBlocks.basicBlock)!!.getStateFromMeta(5)

    private val maxGenerateLevel = 40
    private val minGenerateLevel = 5

    @JvmField
    var MATRIX: Array<Array<ByteArray?>?> = arrayOf<Array<ByteArray?>?>(
        arrayOf<ByteArray?>(
            byteArrayOf(0, 0, 0, 0),
            byteArrayOf(0, 1, 1, 0),
            byteArrayOf(0, 1, 1, 0),
            byteArrayOf(0, 0, 0, 0)
        ), arrayOf<ByteArray?>(
            byteArrayOf(0, 1, 1, 0),
            byteArrayOf(1, 2, 2, 1),
            byteArrayOf(1, 3, 2, 1),
            byteArrayOf(0, 1, 1, 0)
        ), arrayOf<ByteArray?>(
            byteArrayOf(1, 1, 1, 0),
            byteArrayOf(1, 2, 1, 0),
            byteArrayOf(1, 1, 1, 0),
            byteArrayOf(0, 0, 0, 0)
        ), arrayOf<ByteArray?>(
            byteArrayOf(0, 1, 0, 0),
            byteArrayOf(1, 1, 1, 0),
            byteArrayOf(0, 1, 0, 0),
            byteArrayOf(0, 0, 0, 0)
        )
    )

    override fun generate(
        random: Random,
        chunkX: Int,
        chunkZ: Int,
        world: World,
        chunkGenerator: IChunkGenerator?,
        chunkProvider: IChunkProvider?
    ) {
        if (world.provider.getDimension() == 0) {
            if (random.nextInt(100) < 85) {
                val x: Int = chunkX * 16 + random.nextInt(16) + 8
                val z: Int = chunkZ * 16 + random.nextInt(16) + 8
                val y: Int =
                    random.nextInt(max(this.maxGenerateLevel - this.minGenerateLevel, 0)) + this.minGenerateLevel
                val pos: BlockPos = BlockPos(x, y, z)
                this.generateOre(world, random, pos)
            }
        }
    }

    private fun generateOre(world: World, rand: Random, pos: BlockPos): Boolean {
        for (py: Int in MATRIX.indices) {
            for (px: Int in MATRIX[py]!!.indices) {
                for (pz: Int in MATRIX[py]!![px]!!.indices) {
                    var px2: Int = px
                    val py2: Int = py
                    var pz2: Int = pz

                    px2 -= MATRIX[py]!!.size / 2
                    pz2 -= MATRIX[py]!![px]!!.size / 2

                    var matrix1: Int = MATRIX[py]!![px]!![pz].toInt()

                    when (rand.nextInt(4)) {
                        0 -> {}
                        1 -> matrix1 = MATRIX[py]!![pz]!![px].toInt()
                        2 -> matrix1 =
                            MATRIX[py]!![(MATRIX[py]!!.size - 1) - px]!![(MATRIX[py]!![px]!!.size - 1) - pz].toInt()

                        3 -> matrix1 =
                            MATRIX[py]!![(MATRIX[py]!![px]!!.size - 1) - pz]!![(MATRIX[py]!!.size - 1) - px].toInt()
                    }
                    generateBlock(world, rand, BlockPos(pos.getX() + px2, pos.getY() + py2, pos.getZ() + pz2), matrix1)
                }
            }
        }

        return true
    }

    protected open fun generateBlock(world: World, random: Random, position: BlockPos, matrixValue: Int) {
        val rand_block: IBlockState = if (random.nextBoolean()) copperBlock else nickelBlock

        if (!world.getBlockState(position).isNormalCube()) return


        when (matrixValue) {
            1 -> world.setBlockState(position, asteroidBlock)
            2 -> world.setBlockState(position, nickelBlock)
            3 -> world.setBlockState(position, rand_block)
            else -> {}
        }
    }
}
