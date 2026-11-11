package micdoodle8.mods.galacticraft.planets.venus.world.gen.dungeon

import foundation.persistence.containsTag

import java.util.Random

import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.tile.TileEntityDungeonSpawner
import micdoodle8.mods.galacticraft.core.util.GCLog
import micdoodle8.mods.galacticraft.planets.venus.VenusBlocks
import micdoodle8.mods.galacticraft.planets.venus.blocks.BlockTorchWeb
import net.minecraft.world.level.block.Blocks
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.levelgen.structure.BoundingBox
import net.minecraft.world.gen.structure.template.TemplateManager
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt

open class RoomBossVenus : SizedPieceVenus {
    private var exitDirection: EnumFacing? = null
    private var chestPos: BlockPos? = null

    constructor()

    constructor(
        configuration: DungeonConfigurationVenus,
        rand: Random,
        blockPosX: Int,
        blockPosZ: Int,
        entranceDir: EnumFacing
    ) : this(
        configuration,
        rand,
        blockPosX,
        blockPosZ,
        rand.nextInt(6) + 20,
        rand.nextInt(2) + 10,
        rand.nextInt(6) + 20,
        entranceDir
    )

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
        this.sizeZ = sizeZ
        this.sizeY = sizeY
        val yPos: Int = configuration.getYPosition()

        this.boundingBox = BoundingBox(
            blockPosX,
            yPos - 2,
            blockPosZ,
            blockPosX + this.sizeX,
            yPos + this.sizeY,
            blockPosZ + this.sizeZ
        )
    }

    override fun writeStructureToNBT(tagCompound: NBTTagCompound) {
        super.writeStructureToNBT(tagCompound)

        if (this.exitDirection != null) {
            tagCompound.putInt("direction_exit", this.exitDirection!!.ordinal)
        }


        tagCompound.putBoolean("chestPosNull", this.chestPos == null)
        if (this.chestPos != null) {
            tagCompound.putInt("chestX", this.chestPos!!.getX())
            tagCompound.putInt("chestY", this.chestPos!!.getY())
            tagCompound.putInt("chestZ", this.chestPos!!.getZ())
        }
    }

    override fun readStructureFromNBT(tagCompound: NBTTagCompound, manager: TemplateManager) {
        super.readStructureFromNBT(tagCompound, manager)

        if (tagCompound.containsTag("direction_exit")) {
            this.exitDirection = EnumFacing.from3DDataValue(tagCompound.getIntOr("direction_exit", 0))
        } else {
            this.exitDirection = null
        }

        if (tagCompound.containsTag("chestPosNull") && !tagCompound.getBooleanOr("chestPosNull", false)) {
            this.chestPos = BlockPos(
                tagCompound.getIntOr("chestX", 0),
                tagCompound.getIntOr("chestY", 0),
                tagCompound.getIntOr("chestZ", 0)
            )
        }
    }

    override fun addComponentParts(worldIn: World, random: Random, chunkBox: BoundingBox): Boolean {
        val roomBounds = foundation.level.StructureBoundsAccumulator()

        for (i: Int in 0..this.sizeX) {
            for (j: Int in 0..this.sizeY) {
                for (k: Int in 0..this.sizeZ) {
                    val dX: Double = (i - this.sizeX / 2).toDouble()
                    val dZ: Double = (k - this.sizeZ / 2).toDouble()
                    val dXZ: Double = sqrt(dX * dX + dZ * dZ)

                    val f: Double = -((dXZ * 1.5) / (this.sizeX / 2 - 1)).pow(6.0) + this.sizeY - 1

                    if (j == 0) {
                        this.setBlockState(worldIn, (this.configuration)!!.getBrickBlockFloor(), i, j, k, chunkBox)
                    } else if (j < f) {
                        this.setBlockState(worldIn, Blocks.AIR.defaultBlockState(), i, j, k, chunkBox)

                        if (j + 1 >= f && (dXZ > 5) && random.nextInt(12) == 0) {
                            val distFromFloor: Int = random.nextInt(5) + 2
                            for (j0: Int in j downTo distFromFloor + 1) {
                                val webType: BlockTorchWeb.EnumWebType
                                if (j0 == distFromFloor + 1) {
                                    webType = BlockTorchWeb.EnumWebType.WEB_1
                                } else {
                                    webType = BlockTorchWeb.EnumWebType.WEB_0
                                }
                                this.setBlockState(
                                    worldIn,
                                    (VenusBlocks.torchWeb)!!.defaultBlockState()
                                        .setValue(
                                            BlockTorchWeb.WEB_TYPE,
                                            webType
                                        ),
                                    i,
                                    j0,
                                    k,
                                    chunkBox
                                )
                            }
                        }

                        roomBounds.include(i, j, k)
                    } else {
                        var placeBlock: Boolean = true

                        var start: Int = (this.boundingBox.maxX() - this.boundingBox.minX()) / 2 - 1
                        var end: Int = (this.boundingBox.maxX() - this.boundingBox.minX()) / 2 + 1
                        if (i > start && i <= end && j < 5 && j > 2) {
                            if ((getDirection() == EnumFacing.SOUTH || (this.exitDirection != null && this.exitDirection == EnumFacing.SOUTH)) && k < 7) {
                                placeBlock = false
                            }
                            if ((getDirection() == EnumFacing.NORTH || (this.exitDirection != null && this.exitDirection == EnumFacing.NORTH)) && k > this.sizeZ - 7) {
                                placeBlock = false
                            }
                        }

                        start = (this.boundingBox.maxZ() - this.boundingBox.minZ()) / 2 - 1
                        end = (this.boundingBox.maxZ() - this.boundingBox.minZ()) / 2 + 1
                        if (k > start && k <= end && j < 5 && j > 2) {
                            if ((getDirection() == EnumFacing.EAST || (this.exitDirection != null && this.exitDirection == EnumFacing.EAST)) && i < 7) {
                                placeBlock = false
                            }
                            if ((getDirection() == EnumFacing.WEST || (this.exitDirection != null && this.exitDirection == EnumFacing.WEST)) && i > this.sizeX - 7) {
                                placeBlock = false
                            }
                        }

                        if (placeBlock) {
                            this.setBlockState(worldIn, (this.configuration)!!.getBrickBlock(), i, j, k, chunkBox)
                        } else {
                            this.setBlockState(worldIn, Blocks.AIR.defaultBlockState(), i, j, k, chunkBox)
                        }
                    }
                }
            }
        }

        val spawnerX: Int = this.sizeX / 2
        val spawnerY: Int = 1
        val spawnerZ: Int = this.sizeZ / 2
        val blockpos: BlockPos = BlockPos(
            this.getXWithOffset(spawnerX, spawnerZ),
            this.getYWithOffset(spawnerY),
            this.getZWithOffset(spawnerX, spawnerZ)
        )
        //Is this position inside the chunk currently being generated?
        if (chunkBox.isInside(blockpos)) {
            worldIn.setBlock(blockpos, (VenusBlocks.bossSpawner)!!.defaultBlockState(), 2)
            val spawner: TileEntityDungeonSpawner<*>? = worldIn.getBlockEntity(blockpos) as TileEntityDungeonSpawner<*>?
            if (spawner != null) {
                val box = roomBounds.bounds()
                if (box == null || box.getXSpan() > 10000 || box.getYSpan() > 10000 || box.getZSpan() > 10000) {
                    GCLog.severe("Failed to set correct boss room size. This is a bug!")
                } else {
                    spawner.setRoom(
                        Vector3(
                            (box.minX() + this.boundingBox.minX()).toDouble(),
                            (box.minY() + this.boundingBox.minY()).toDouble(),
                            (box.minZ() + this.boundingBox.minZ()).toDouble()
                        ),
                        Vector3(
                            (box.maxX() - box.minX() + 1).toDouble(),
                            (box.maxY() - box.minY() + 1).toDouble(),
                            (box.maxZ() - box.minZ() + 1).toDouble()
                        )
                    )
                    spawner.setChestPos(this.chestPos)
                }
            }
        }

        return true
    }

    open fun getChestPos(): BlockPos? {
        return chestPos
    }

    open fun setChestPos(chestPos: BlockPos?) {
        this.chestPos = chestPos
    }

    override fun getNextPiece(startPiece: DungeonStartVenus?, rand: Random?): PieceVenus? {
        val corridor: DirectionalPieceVenus? = getCorridor((rand)!!, (startPiece)!!, 10, true) as DirectionalPieceVenus?
        this.exitDirection = if (corridor == null) null else (corridor.getDirection())!!.getOpposite()
        return corridor
    }
}
