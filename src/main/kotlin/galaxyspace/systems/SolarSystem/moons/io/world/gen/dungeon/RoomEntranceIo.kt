package galaxyspace.systems.SolarSystem.moons.io.world.gen.dungeon

import galaxyspace.core.GSBlocks
import galaxyspace.GalaxySpace
import galaxyspace.systems.SolarSystem.moons.io.world.gen.dungeon.DungeonStartIo
import galaxyspace.systems.SolarSystem.moons.io.world.gen.dungeon.SizedPieceIo
import galaxyspace.systems.SolarSystem.moons.io.world.gen.dungeon.PieceIo
import galaxyspace.systems.SolarSystem.moons.io.world.gen.dungeon.RoomEntranceIo
import galaxyspace.core.prefab.blocks.DungeonBlocks.EnumDungeonBlocks
import galaxyspace.core.prefab.blocks.DungeonBlocks
import galaxyspace.systems.SolarSystem.moons.io.world.gen.dungeon.DirectionalPieceIo
import asmodeuscore.core.astronomy.dimension.world.gen.dungeons.standart.DungeonConfiguration
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.world.World
import net.minecraft.world.gen.structure.StructureBoundingBox
import net.minecraft.world.gen.structure.template.TemplateManager
import java.util.Random
import kotlin.math.abs
import kotlin.math.max

open class RoomEntranceIo : SizedPieceIo {
    private var exitDirection: EnumFacing? = null

    constructor()

    constructor(
        world: World?,
        configuration: DungeonConfiguration,
        rand: Random,
        blockPosX: Int,
        blockPosZ: Int
    ) : super(configuration, range, rand.nextInt(2) + 15, range, EnumFacing.Plane.HORIZONTAL.random(rand)) {
        this.setCoordBaseMode(EnumFacing.SOUTH)

        this.boundingBox = StructureBoundingBox(
            blockPosX - range,
            configuration.getYPosition() - 5,
            blockPosZ - range,
            blockPosX + range - 1,
            150,
            blockPosZ + range
        )
        (GalaxySpace.instance)!!.debug(blockPosX.toString() + " | " + blockPosZ)
    }

    override fun addComponentParts(
        worldIn: World,
        randomIn: Random,
        structureBoundingBoxIn: StructureBoundingBox
    ): Boolean {
        var block1: IBlockState

        var maxLevel: Int = 0

        for (i: Int in -range..range) {
            for (k: Int in -range..range) {
                var j: Int = this.boundingBox.getYSize()

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
                    if (j == this.sizeY && (i == startX + 3 || i == endX - 3) && (k == startZ + 3 || k == endZ - 3)) this.setBlockState(
                        worldIn,
                        Blocks.FLOWING_LAVA.getDefaultState(),
                        i,
                        j,
                        k,
                        boundingBox
                    )
                    else if (i == startX || i == endX || j == 0 || j == 5 || j == this.sizeY || k == startZ || k == endZ) {
                        if (j == this.sizeY) {
                            if ((this.configuration)!!.getOtherBlock(false) != null) this.setBlockState(
                                worldIn,
                                (this.configuration)!!.getOtherBlock(false),
                                i,
                                j,
                                k,
                                boundingBox
                            )
                        } else if (j == 0) this.setBlockState(
                            worldIn,
                            Blocks.LAVA.getDefaultState(),
                            i,
                            j,
                            k,
                            boundingBox
                        )
                        else if (j >= 5 && j <= 9 && (this.configuration)!!.getOtherBlock(true) != null) {
                            val l: Int = 5
                            if (j == 5) {
                                if (i > startX + 3 && i < endX - 3 && k > startZ + 3 && k < endZ - 3) {
                                    this.setBlockState(
                                        worldIn,
                                        (this.configuration)!!.getOtherBlock(true),
                                        i,
                                        j,
                                        k,
                                        boundingBox
                                    )
                                } else if (i != startX && i != endX && k != startZ && k != endZ) this.setBlockState(
                                    worldIn,
                                    Blocks.AIR.getDefaultState(),
                                    i,
                                    j,
                                    k,
                                    boundingBox
                                )
                                else this.setBlockState(
                                    worldIn,
                                    (this.configuration)!!.getBrickBlock(),
                                    i,
                                    j,
                                    k,
                                    boundingBox
                                )



                                when (exitDirection) {
                                    EnumFacing.EAST -> if (k > startZ + l && k < endZ - l && i < endX - l) this.setBlockState(
                                        worldIn,
                                        (this.configuration)!!.getOtherBlock(true),
                                        i,
                                        j,
                                        k,
                                        boundingBox
                                    )

                                    EnumFacing.NORTH -> if (i > startX + l && i < endX - l && k > endZ - l) this.setBlockState(
                                        worldIn,
                                        (this.configuration)!!.getOtherBlock(true),
                                        i,
                                        j,
                                        k,
                                        boundingBox
                                    ) // ----- V

                                    EnumFacing.SOUTH -> if (i > startX + l && i < endX - l && k < endZ - l) this.setBlockState(
                                        worldIn,
                                        (this.configuration)!!.getOtherBlock(true),
                                        i,
                                        j,
                                        k,
                                        boundingBox
                                    ) // ----- V

                                    EnumFacing.WEST -> if (k > startZ + l && k < endZ - l && i > endX - l) this.setBlockState(
                                        worldIn,
                                        (this.configuration)!!.getOtherBlock(true),
                                        i,
                                        j,
                                        k,
                                        boundingBox
                                    ) // ----- V

                                    else -> {}
                                }
                            } else {
                                this.setBlockState(worldIn, (this.configuration)!!.getBrickBlock(), i, j, k, boundingBox)

                                when (exitDirection) {
                                    EnumFacing.EAST -> if (k > startZ + l && k < endZ - l && i < endX - l) {
                                        this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), i, j, k, boundingBox)
                                    }

                                    EnumFacing.NORTH -> if (i > startX + l && i < endX - l && k > endZ - l) {
                                        this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), i, j, k, boundingBox)
                                    }

                                    EnumFacing.SOUTH -> if (i > startX + l && i < endX - l && k < endZ - l) {
                                        this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), i, j, k, boundingBox)
                                    }

                                    EnumFacing.WEST -> if (k > startZ + l && k < endZ - l && i > endX - l) {
                                        this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), i, j, k, boundingBox)
                                    }

                                    else -> {}
                                }
                            }
                        } else if (j == 1) {
                            this.setBlockState(
                                worldIn,
                                GSBlocks.DUNGEON_BLOCKS.getDefaultState()
                                    .withProperty(
                                        DungeonBlocks.BASIC_TYPE,
                                        EnumDungeonBlocks.IO_BRICKS
                                    ),
                                i,
                                j,
                                k,
                                boundingBox
                            )
                        } else if (j >= 2 && j <= 4 && worldIn.rand.nextInt(10) > 4) {
                            this.setBlockState(
                                worldIn,
                                GSBlocks.DUNGEON_BLOCKS.getDefaultState()
                                    .withProperty(
                                        DungeonBlocks.BASIC_TYPE,
                                        EnumDungeonBlocks.IO_BRICKS
                                    ),
                                i,
                                j,
                                k,
                                boundingBox
                            )
                        } else this.setBlockState(worldIn, (this.configuration)!!.getBrickBlock(), i, j, k, boundingBox)
                    } else {
                        this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), i, j, k, boundingBox)
                    }
                }
            }
        }

        for (i: Int in -range..<range) {
            for (k: Int in -range..<range) {
                val xDev: Double = i / 20.0
                val zDev: Double = k / 20.0
                val distance: Double = xDev * xDev + zDev * zDev
                val depth: Int = abs(0.5 / (distance * 2 + .00001)).toInt()
                var helper: Int = 0
                var j: Int = maxLevel
                while (j > 5 && helper <= depth) {
                    block1 = this.getBlockStateFromPos(worldIn, i + range, j, k + range, boundingBox)
                    if ((block1 === (this.configuration)!!.getBrickBlock() || block1 === (this.configuration)!!.getOtherBlock(
                            false
                        )) || j != this.sizeY || j != 5
                    ) {
                        this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), i + range, j, k + range, boundingBox)
                        helper++
                    }
                    j--
                }
            }
        }

        return true
    }

    override fun writeStructureToNBT(tagCompound: NBTTagCompound) {
        super.writeStructureToNBT(tagCompound)

        if (this.exitDirection != null) {
            tagCompound.setInteger("direction_exit", this.exitDirection!!.ordinal)
        }
    }

    override fun readStructureFromNBT(tagCompound: NBTTagCompound, manager: TemplateManager) {
        super.readStructureFromNBT(tagCompound, manager)

        if (tagCompound.hasKey("direction_exit")) {
            this.exitDirection = EnumFacing.byIndex(tagCompound.getInteger("direction_exit"))
        } else {
            this.exitDirection = null
        }
    }

    override fun getNextPiece(startPiece: DungeonStartIo?, rand: Random?): PieceIo? {
        if ((startPiece)!!.attachedComponents.isEmpty()) {
            val corridor: DirectionalPieceIo? = getCorridor((rand)!!, (startPiece)!!, 20, false, 12) as DirectionalPieceIo?
            this.exitDirection = if (corridor == null) null else (corridor.getDirection())!!.getOpposite()
            return corridor //getCorridor(rand, startPiece, 10, false);
        }

        return null
    }

    override fun getExtension(direction: EnumFacing, length: Int, width: Int): StructureBoundingBox {
        val blockX: Int
        val blockZ: Int
        val sizeX: Int
        val sizeZ: Int
        val startX: Int = this.boundingBox.minX + range - this.sizeX / 2
        val startZ: Int = this.boundingBox.minZ + range - this.sizeZ / 2
        val endX: Int = this.boundingBox.minX + range + this.sizeX / 2
        val endZ: Int = this.boundingBox.minZ + range + this.sizeZ / 2
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
        return StructureBoundingBox(blockX, blockZ, blockX + sizeX, blockZ + sizeZ)
    }

    companion object {
        private const val range = 18
    }
}
