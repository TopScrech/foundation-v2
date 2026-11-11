package micdoodle8.mods.galacticraft.core.world.gen.dungeon

import foundation.level.horizontalStructureBounds

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.level.block.Blocks
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.levelgen.structure.BoundingBox
import java.util.Random
import kotlin.math.abs
import kotlin.math.max

open class RoomEntrance : SizedPiece {
    private val range = 4

    constructor()

    constructor(
        world: World?,
        configuration: DungeonConfiguration,
        rand: Random,
        blockPosX: Int,
        blockPosZ: Int
    ) : super(
        configuration,
        rand.nextInt(4) + 6,
        rand.nextInt(2) + 5,
        rand.nextInt(4) + 6,
        EnumFacing.Plane.HORIZONTAL.random(rand)
    ) {
        this.setCoordBaseMode(EnumFacing.SOUTH)

        this.boundingBox = BoundingBox(
            blockPosX - range,
            configuration.getYPosition(),
            blockPosZ - range,
            blockPosX + range,
            150,
            blockPosZ + range
        )
    }

    override fun addComponentParts(
        worldIn: World,
        randomIn: Random,
        structureBoundingBoxIn: BoundingBox
    ): Boolean {
        var block1: IBlockState

        var maxLevel: Int = 0

        for (i: Int in -range..range) {
            for (k: Int in -range..range) {
                var j: Int = this.boundingBox.getYSpan()

                while (j >= 0) {
                    j--

                    val block: Block = getBlockStateFromPos(worldIn, i + range, j, k + range, boundingBox).getBlock()

                    if (Blocks.AIR !== block && block != null) {
                        break
                    }
                }

                maxLevel = max(maxLevel, j + 3)
            }
        }

        val startX: Int = range - this.sizeX / 2
        val startZ: Int = range - this.sizeZ / 2
        val endX: Int = range + this.sizeX / 2
        val endZ: Int = range + this.sizeZ / 2

        for (i: Int in startX..endX) {
            for (j: Int in 0..this.sizeY) {
                for (k: Int in startZ..endZ) {
                    if (i == startX || i == endX || j == 0 || j == this.sizeY || k == startZ || k == endZ) {
                        this.setBlockState(worldIn, (this.configuration)!!.getBrickBlock(), i, j, k, boundingBox)
                    } else {
                        this.setBlockState(worldIn, Blocks.AIR.defaultBlockState(), i, j, k, boundingBox)
                    }
                }
            }
        }

        for (i: Int in -range..<range) {
            for (k: Int in -range..<range) {
                val xDev: Double = i / 10.0
                val zDev: Double = k / 10.0
                val distance: Double = xDev * xDev + zDev * zDev
                val depth: Int = abs(0.5 / (distance + .00001)).toInt()
                var helper: Int = 0
                var j: Int = maxLevel
                while (j > 1 && helper <= depth) {
                    block1 = this.getBlockStateFromPos(worldIn, i + range, j, k + range, boundingBox)
                    if (block1 === (this.configuration)!!.getBrickBlock() || j != this.sizeY) {
                        this.setBlockState(worldIn, Blocks.AIR.defaultBlockState(), i + range, j, k + range, boundingBox)
                        helper++
                    }
                    j--
                }
            }
        }

        return true
    }

    override fun getNextPiece(startPiece: DungeonStart?, rand: Random?): Piece? {
        if ((startPiece)!!.attachedComponents.isEmpty()) {
            return getCorridor((rand)!!, (startPiece)!!, 10, false)
        }

        return null
    }

    override fun getExtension(direction: EnumFacing, length: Int, width: Int): BoundingBox {
        val blockX: Int
        val blockZ: Int
        val sizeX: Int
        val sizeZ: Int
        val startX: Int = this.boundingBox.minX() + range - this.sizeX / 2
        val startZ: Int = this.boundingBox.minZ() + range - this.sizeZ / 2
        val endX: Int = this.boundingBox.minX() + range + this.sizeX / 2
        val endZ: Int = this.boundingBox.minZ() + range + this.sizeZ / 2
        when (direction) {
            EnumFacing.NORTH -> {
                sizeX = width
                sizeZ = length
                blockX = startX + (endX - startX) / 2 - sizeX / 2
                blockZ = startZ - sizeZ
            }

            EnumFacing.EAST -> {
                sizeX = length
                sizeZ = width
                blockX = endX
                blockZ = startZ + (endZ - startZ) / 2 - sizeZ / 2
            }

            EnumFacing.SOUTH -> {
                sizeX = width
                sizeZ = length
                blockX = startX + (endX - startX) / 2 - sizeX / 2
                blockZ = endZ
            }

            EnumFacing.WEST -> {
                sizeX = length
                sizeZ = width
                blockX = startX - sizeX
                blockZ = startZ + (endZ - startZ) / 2 - sizeZ / 2
            }

            else -> {
                sizeX = length
                sizeZ = width
                blockX = startX - sizeX
                blockZ = startZ + (endZ - startZ) / 2 - sizeZ / 2
            }
        }
        return horizontalStructureBounds(blockX, blockZ, blockX + sizeX, blockZ + sizeZ)
    }
}
