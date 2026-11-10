package galaxyspace.systems.SolarSystem.planets.ceres.world.gen.dungeon

import galaxyspace.core.GSBlocks
import galaxyspace.systems.SolarSystem.planets.ceres.world.gen.dungeon.RoomEmptyCeres
import galaxyspace.systems.SolarSystem.planets.ceres.blocks.CeresBlocks.EnumCeresBlocks
import galaxyspace.systems.SolarSystem.planets.ceres.blocks.CeresBlocks
import asmodeuscore.core.astronomy.dimension.world.gen.dungeons.standart.DungeonConfiguration
import asmodeuscore.core.astronomy.dimension.world.gen.dungeons.standart.DungeonStart
import asmodeuscore.core.astronomy.dimension.world.gen.dungeons.standart.Piece
import asmodeuscore.core.astronomy.dimension.world.gen.dungeons.standart.SizedPiece
import net.minecraft.init.Blocks
import net.minecraft.util.EnumFacing
import net.minecraft.world.World
import net.minecraft.world.gen.structure.StructureBoundingBox
import java.util.Random
import kotlin.math.abs

open class RoomEmptyCeres : SizedPiece {
    constructor()

    constructor(
        configuration: DungeonConfiguration,
        rand: Random,
        blockPosX: Int,
        blockPosZ: Int,
        entranceDir: EnumFacing
    ) : this(
        configuration,
        rand,
        blockPosX,
        blockPosZ,
        rand.nextInt(4) + 6,
        configuration.getRoomHeight(),
        rand.nextInt(4) + 6,
        entranceDir
    )

    constructor(
        configuration: DungeonConfiguration,
        rand: Random?,
        blockPosX: Int,
        blockPosZ: Int,
        sizeX: Int,
        sizeY: Int,
        sizeZ: Int,
        entranceDir: EnumFacing
    ) : super(configuration, sizeX, sizeY, sizeZ, entranceDir.getOpposite()) {
        this.setCoordBaseMode(EnumFacing.SOUTH)
        this.sizeX = sizeX
        this.sizeY = sizeY
        this.sizeZ = sizeZ
        val yPos: Int = configuration.getYPosition()

        this.boundingBox = StructureBoundingBox(
            blockPosX,
            yPos,
            blockPosZ,
            blockPosX + this.sizeX,
            yPos + this.sizeY,
            blockPosZ + this.sizeZ
        )
    }

    override fun addComponentParts(worldIn: World, random: Random, boundingBox: StructureBoundingBox): Boolean {
        for (i: Int in 0..this.sizeX) {
            for (j: Int in 0..this.sizeY) {
                for (k: Int in 0..this.sizeZ) {
                    if (i == 0 || i == this.sizeX || j == 0 || j == this.sizeY || k == 0 || k == this.sizeZ) {
                        var placeBlock: Boolean = true
                        if ((getDirection())!!.getAxis() == EnumFacing.Axis.Z) {
                            val start: Int = (this.boundingBox.maxX - this.boundingBox.minX) / 2 - 1
                            val end: Int = (this.boundingBox.maxX - this.boundingBox.minX) / 2 + 1
                            if (i > start && i <= end && j < (this.configuration)!!.getHallwayHeight() && j > 0) {
                                if (getDirection() == EnumFacing.SOUTH && k == 0) {
                                    placeBlock = false
                                } else if (getDirection() == EnumFacing.NORTH && k == this.sizeZ) {
                                    placeBlock = false
                                }
                            }
                        } else {
                            val start: Int = (this.boundingBox.maxZ - this.boundingBox.minZ) / 2 - 1
                            val end: Int = (this.boundingBox.maxZ - this.boundingBox.minZ) / 2 + 1
                            if (k > start && k <= end && j < (this.configuration)!!.getHallwayHeight() && j > 0) {
                                if (getDirection() == EnumFacing.EAST && i == 0) {
                                    placeBlock = false
                                } else if (getDirection() == EnumFacing.WEST && i == this.sizeX) {
                                    placeBlock = false
                                }
                            }
                        }
                        if (placeBlock) {
                            if (j == (this.configuration)!!.getHallwayHeight()) this.setBlockState(
                                worldIn,
                                GSBlocks.CERES_BLOCKS.getDefaultState()
                                    .withProperty(
                                        CeresBlocks.BASIC_TYPE,
                                        EnumCeresBlocks.CERES_DUNGEON_TOP
                                    ),
                                i,
                                j,
                                k,
                                boundingBox
                            )
                            else if (j == 0) this.setBlockState(
                                worldIn,
                                GSBlocks.CERES_BLOCKS.getDefaultState()
                                    .withProperty(
                                        CeresBlocks.BASIC_TYPE,
                                        EnumCeresBlocks.CERES_DUNGEON_FLOOR
                                    ),
                                i,
                                j,
                                k,
                                boundingBox
                            )
                            else this.setBlockState(worldIn, (this.configuration)!!.getBrickBlock(), i, j, k, boundingBox)
                        } else {
                            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), i, j, k, boundingBox)
                        }
                    } else {
                        this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), i, j, k, boundingBox)
                    }
                }
            }
        }

        return true
    }

    override fun getNextPiece(startPiece: DungeonStart?, rand: Random?): Piece? {
        if (abs((startPiece)!!.getBoundingBox().maxZ - boundingBox.minZ) > 200) {
            return null
        }

        if (abs((startPiece)!!.getBoundingBox().maxX - boundingBox.minX) > 200) {
            return null
        }

        return getCorridor((rand)!!, (startPiece)!!, 10, false)
    }
}
