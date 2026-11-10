package micdoodle8.mods.galacticraft.core.world.gen.dungeon

import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.tile.TileEntityDungeonSpawner
import net.minecraft.init.Blocks
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.gen.structure.StructureBoundingBox
import net.minecraft.world.gen.structure.template.TemplateManager
import java.util.Random

open class RoomBoss : SizedPiece {
    private var chestPos: BlockPos? = null

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
        rand.nextInt(6) + 14,
        rand.nextInt(2) + 8,
        rand.nextInt(6) + 14,
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
        this.sizeZ = sizeZ
        this.sizeY = sizeY
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

    override fun addComponentParts(worldIn: World, random: Random, chunkBox: StructureBoundingBox): Boolean {
        for (i: Int in 0..this.sizeX) {
            for (j: Int in 0..this.sizeY) {
                for (k: Int in 0..this.sizeZ) {
                    if (i == 0 || i == this.sizeX || j == 0 || j == this.sizeY || k == 0 || k == this.sizeZ) {
                        var placeBlock: Boolean = true
                        if ((getDirection())!!.getAxis() == EnumFacing.Axis.Z) {
                            val start: Int = (this.boundingBox.maxX - this.boundingBox.minX) / 2 - 1
                            val end: Int = (this.boundingBox.maxX - this.boundingBox.minX) / 2 + 1
                            if (i > start && i <= end && j < 3 && j > 0) {
                                if (getDirection() == EnumFacing.SOUTH && k == 0) {
                                    placeBlock = false
                                } else if (getDirection() == EnumFacing.NORTH && k == this.sizeZ) {
                                    placeBlock = false
                                }
                            }
                        } else {
                            val start: Int = (this.boundingBox.maxZ - this.boundingBox.minZ) / 2 - 1
                            val end: Int = (this.boundingBox.maxZ - this.boundingBox.minZ) / 2 + 1
                            if (k > start && k <= end && j < 3 && j > 0) {
                                if (getDirection() == EnumFacing.EAST && i == 0) {
                                    placeBlock = false
                                } else if (getDirection() == EnumFacing.WEST && i == this.sizeX) {
                                    placeBlock = false
                                }
                            }
                        }
                        if (placeBlock) {
                            this.setBlockState(worldIn, (this.configuration)!!.getBrickBlock(), i, j, k, chunkBox)
                        } else {
                            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), i, j, k, chunkBox)
                        }
                    } else if ((i == 1 && k == 1) || (i == 1 && k == this.sizeZ - 1) || (i == this.sizeX - 1 && k == 1) || (i == this.sizeX - 1 && k == this.sizeZ - 1)) {
                        this.setBlockState(worldIn, Blocks.FLOWING_LAVA.getDefaultState(), i, j, k, chunkBox)
                    } else if (j % 3 == 0 && j >= 2 && ((i == 1 || i == this.sizeX - 1 || k == 1 || k == this.sizeZ - 1) || (i == 2 && k == 2) || (i == 2 && k == this.sizeZ - 2) || (i == this.sizeX - 2 && k == 2) || (i == this.sizeX - 2 && k == this.sizeZ - 2))) {
                        // Horizontal bars
                        this.setBlockState(worldIn, Blocks.IRON_BARS.getDefaultState(), i, j, k, chunkBox)
                    } else if ((i == 1 && k == 2) || (i == 2 && k == 1) ||
                        (i == 1 && k == this.sizeZ - 2) || (i == 2 && k == this.sizeZ - 1) ||
                        (i == this.sizeX - 1 && k == 2) || (i == this.sizeX - 2 && k == 1) ||
                        (i == this.sizeX - 1 && k == this.sizeZ - 2) || (i == this.sizeX - 2 && k == this.sizeZ - 1)
                    ) {
                        // Vertical bars
                        this.setBlockState(worldIn, Blocks.IRON_BARS.getDefaultState(), i, j, k, chunkBox)
                    } else {
                        this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), i, j, k, chunkBox)
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
        if (chunkBox.isVecInside(blockpos)) {
            worldIn.setBlockState(blockpos, (GCBlocks.bossSpawner)!!.getDefaultState(), 2)
            val spawner: TileEntityDungeonSpawner<*>? = worldIn.getTileEntity(blockpos) as TileEntityDungeonSpawner<*>?
            if (spawner != null) {
                spawner.setRoom(
                    Vector3(
                        (this.boundingBox.minX + 1).toDouble(),
                        (this.boundingBox.minY + 1).toDouble(),
                        (this.boundingBox.minZ + 1).toDouble()
                    ), Vector3((this.sizeX - 1).toDouble(), (this.sizeY - 1).toDouble(), (this.sizeZ - 1).toDouble())
                )
                spawner.setChestPos(this.chestPos)
            }
        }

        return true
    }

    override fun writeStructureToNBT(tagCompound: NBTTagCompound) {
        super.writeStructureToNBT(tagCompound)

        if (this.chestPos != null) {
            tagCompound.setInteger("chestX", this.chestPos!!.getX())
            tagCompound.setInteger("chestY", this.chestPos!!.getY())
            tagCompound.setInteger("chestZ", this.chestPos!!.getZ())
        }
    }

    override fun readStructureFromNBT(nbt: NBTTagCompound, manager: TemplateManager) {
        super.readStructureFromNBT(nbt, manager)
        this.chestPos = BlockPos(nbt.getInteger("chestX"), nbt.getInteger("chestY"), nbt.getInteger("chestZ"))
    }

    override fun getNextPiece(startPiece: DungeonStart?, rand: Random?): Piece? {
        return getCorridor((rand)!!, (startPiece)!!, 10, true)
    }

    open fun getChestPos(): BlockPos? {
        return chestPos
    }

    open fun setChestPos(chestPos: BlockPos?) {
        this.chestPos = chestPos
    }
}
