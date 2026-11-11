package galaxyspace.systems.SolarSystem.moons.io.world.gen.dungeon

import galaxyspace.systems.SolarSystem.moons.io.world.gen.dungeon.RoomEmptyIo
import galaxyspace.systems.SolarSystem.moons.io.world.gen.dungeon.DungeonStartIo
import galaxyspace.systems.SolarSystem.moons.io.world.gen.dungeon.SizedPieceIo
import galaxyspace.systems.SolarSystem.moons.io.world.gen.dungeon.PieceIo
import asmodeuscore.core.astronomy.dimension.world.gen.dungeons.standart.DungeonConfiguration
import net.minecraft.world.level.block.Blocks
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.levelgen.structure.BoundingBox
import java.util.Random
import kotlin.math.abs

open class RoomEmptyIo : SizedPieceIo {
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
        rand.nextInt(4) + 14,
        configuration.getRoomHeight(),
        rand.nextInt(4) + 14,
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

        this.boundingBox = BoundingBox(
            blockPosX,
            yPos,
            blockPosZ,
            blockPosX + this.sizeX,
            yPos + this.sizeY,
            blockPosZ + this.sizeZ
        )
    }

    override fun addComponentParts(worldIn: World, random: Random, boundingBox: BoundingBox): Boolean {
        for (i: Int in 0..this.sizeX) {
            for (j: Int in 0..this.sizeY) {
                for (k: Int in 0..this.sizeZ) {
                    if (i == 0 || i == this.sizeX || j == 0 || j == this.sizeY || k == 0 || k == this.sizeZ) {
                        var placeBlock: Boolean = true
                        if ((getDirection())!!.getAxis() == EnumFacing.Axis.Z) {
                            val start: Int = (this.boundingBox.maxX() - this.boundingBox.minX()) / 2 - 1
                            val end: Int = (this.boundingBox.maxX() - this.boundingBox.minX()) / 2 + 1
                            if (i > start && i <= end && j < (this.configuration)!!.getHallwayHeight() && j > 0) {
                                if (getDirection() == EnumFacing.SOUTH && k == 0) {
                                    placeBlock = false
                                } else if (getDirection() == EnumFacing.NORTH && k == this.sizeZ) {
                                    placeBlock = false
                                }
                            }
                        } else {
                            val start: Int = (this.boundingBox.maxZ() - this.boundingBox.minZ()) / 2 - 1
                            val end: Int = (this.boundingBox.maxZ() - this.boundingBox.minZ()) / 2 + 1
                            if (k > start && k <= end && j < (this.configuration)!!.getHallwayHeight() && j > 0) {
                                if (getDirection() == EnumFacing.EAST && i == 0) {
                                    placeBlock = false
                                } else if (getDirection() == EnumFacing.WEST && i == this.sizeX) {
                                    placeBlock = false
                                }
                            }
                        }
                        if (placeBlock) {
                            if (this.configuration != null) {
                                if (j == (this.configuration)!!.getHallwayHeight() && (this.configuration)!!.getOtherBlock(false) != null) this.setBlockState(
                                    worldIn,
                                    (this.configuration)!!.getOtherBlock(false),
                                    i,
                                    j,
                                    k,
                                    boundingBox
                                )
                                else if (j == 0 && (this.configuration)!!.getOtherBlock(true) != null) this.setBlockState(
                                    worldIn,
                                    (this.configuration)!!.getOtherBlock(true),
                                    i,
                                    j,
                                    k,
                                    boundingBox
                                )
                                else if ((this.configuration)!!.getBrickBlock() != null) this.setBlockState(
                                    worldIn,
                                    (this.configuration)!!.getBrickBlock(),
                                    i,
                                    j,
                                    k,
                                    boundingBox
                                )
                            }
                        } else {
                            this.setBlockState(worldIn, Blocks.AIR.defaultBlockState(), i, j, k, boundingBox)
                        }
                    } else {
                        this.setBlockState(worldIn, Blocks.AIR.defaultBlockState(), i, j, k, boundingBox)
                    }
                }
            }
        }

        return true
    }

    override fun getNextPiece(startPiece: DungeonStartIo?, rand: Random?): PieceIo? {
        if (abs((startPiece)!!.getBoundingBox().maxZ() - boundingBox.minZ()) > 200) {
            return null
        }

        if (abs((startPiece)!!.getBoundingBox().maxX() - boundingBox.minX()) > 200) {
            return null
        }

        return getCorridor((rand)!!, (startPiece)!!, 10, false, 8)
    }
}
