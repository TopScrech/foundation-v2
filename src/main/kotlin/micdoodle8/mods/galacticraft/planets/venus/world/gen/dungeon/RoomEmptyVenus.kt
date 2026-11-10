package micdoodle8.mods.galacticraft.planets.venus.world.gen.dungeon

import net.minecraft.init.Blocks
import net.minecraft.util.EnumFacing
import net.minecraft.world.World
import net.minecraft.world.gen.structure.StructureBoundingBox
import java.util.Random
import kotlin.math.abs

open class RoomEmptyVenus : SizedPieceVenus {
    constructor()

    constructor(
        configuration: DungeonConfigurationVenus,
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
                            val venusConfig: DungeonConfigurationVenus = this.configuration as DungeonConfigurationVenus
                            this.setBlockState(
                                worldIn,
                                if (j == 0 || j == this.sizeY) venusConfig.getBrickBlockFloor() else ((this.configuration)!!.getBrickBlock())!!,
                                i,
                                j,
                                k,
                                boundingBox
                            )
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

    override fun getNextPiece(startPiece: DungeonStartVenus?, rand: Random?): PieceVenus? {
        if (abs((startPiece)!!.getBoundingBox().maxZ - boundingBox.minZ) > 200) {
            return null
        }

        if (abs((startPiece)!!.getBoundingBox().maxX - boundingBox.minX) > 200) {
            return null
        }

        return getCorridor((rand)!!, (startPiece)!!, 10, false)
    }
}
