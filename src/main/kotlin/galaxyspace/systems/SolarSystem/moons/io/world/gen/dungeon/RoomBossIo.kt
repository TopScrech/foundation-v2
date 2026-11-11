package galaxyspace.systems.SolarSystem.moons.io.world.gen.dungeon

import foundation.persistence.containsTag

import galaxyspace.core.GSBlocks
import galaxyspace.systems.SolarSystem.moons.io.blocks.IoBlocks.EnumIoBlocks
import galaxyspace.systems.SolarSystem.moons.io.blocks.IoBlocks
import galaxyspace.systems.SolarSystem.moons.io.world.gen.dungeon.RoomBossIo
import galaxyspace.systems.SolarSystem.moons.io.world.gen.dungeon.DungeonStartIo
import galaxyspace.systems.SolarSystem.moons.io.world.gen.dungeon.SizedPieceIo
import galaxyspace.systems.SolarSystem.moons.io.world.gen.dungeon.PieceIo
import galaxyspace.core.prefab.blocks.DungeonBlocks.EnumDungeonBlocks
import galaxyspace.core.prefab.blocks.DungeonBlocks
import galaxyspace.systems.SolarSystem.moons.io.world.gen.dungeon.DirectionalPieceIo
import asmodeuscore.core.astronomy.dimension.world.gen.dungeons.standart.DungeonConfiguration
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.tile.TileEntityDungeonSpawner
import net.minecraft.world.level.block.Blocks
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.levelgen.structure.BoundingBox
import net.minecraft.world.gen.structure.template.TemplateManager
import java.util.Random

open class RoomBossIo : SizedPieceIo {
    private var exitDirection: EnumFacing? = null
    private var chestPos: BlockPos? = null

    constructor()

    constructor(
        configuration: DungeonConfiguration,
        rand: Random,
        blockPosX: Int,
        blockPosZ: Int,
        entranceDir: EnumFacing
    ) : this(configuration, rand, blockPosX, blockPosZ, 24, rand.nextInt(2) + 12, 24, entranceDir)

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

        this.boundingBox = BoundingBox(
            blockPosX,
            yPos,
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
        for (i: Int in 0..this.sizeX) {
            for (j: Int in 0..this.sizeY) {
                for (k: Int in 0..this.sizeZ) {
                    if (i == 0 || i == this.sizeX || j == 0 || j == this.sizeY || k == 0 || k == this.sizeZ) {
                        var placeBlock: Boolean = true
                        if ((getDirection())!!.getAxis() == EnumFacing.Axis.Z) {
                            val start: Int = (this.boundingBox.maxX() - this.boundingBox.minX()) / 2 - 1
                            val end: Int = (this.boundingBox.maxX() - this.boundingBox.minX()) / 2 + 1
                            if (i > start && i <= end && j < 4 && j > 0) {
                                if (getDirection() == EnumFacing.SOUTH && (k == 0 || k == 2)) {
                                    placeBlock = false
                                } else if (getDirection() == EnumFacing.NORTH && (k == this.sizeZ || k == this.sizeZ - 2)) {
                                    placeBlock = false
                                }
                            }
                        } else {
                            val start: Int = (this.boundingBox.maxZ() - this.boundingBox.minZ()) / 2 - 1
                            val end: Int = (this.boundingBox.maxZ() - this.boundingBox.minZ()) / 2 + 1
                            if (k > start && k <= end && j < 4 && j > 0) {
                                if (getDirection() == EnumFacing.EAST && (i == 0 || i == 2)) {
                                    placeBlock = false
                                } else if (getDirection() == EnumFacing.WEST && (i == this.sizeX || i == this.sizeX - 2)) {
                                    placeBlock = false
                                }
                            }
                        }
                        if (placeBlock) {
                            if (j == 0) {
                                if (i > 2 && i < this.sizeX - 2 && k > 2 && k < this.sizeZ - 2) {
                                    this.setBlockState(
                                        worldIn,
                                        GSBlocks.FUTURE_GLASS_BASIC.defaultBlockState(),
                                        i,
                                        j,
                                        k,
                                        chunkBox
                                    )
                                    this.setBlockState(worldIn, Blocks.LAVA.defaultBlockState(), i, j - 1, k, chunkBox)
                                } else this.setBlockState(
                                    worldIn,
                                    GSBlocks.IO_BLOCKS.defaultBlockState().setValue(
                                        IoBlocks.BASIC_TYPE,
                                        EnumIoBlocks.IO_DUNGEON_FLOOR
                                    ),
                                    i,
                                    j,
                                    k,
                                    boundingBox
                                )
                            } else if (j == this.sizeY) {
                                if ((i <= 2 || k <= 2 || i >= this.sizeX - 2 || k >= this.sizeZ - 2) && random.nextInt(4) == 0) {
                                    this.setBlockState(worldIn, Blocks.GLOWSTONE.defaultBlockState(), i, j, k, chunkBox)
                                } else {
                                    this.setBlockState(
                                        worldIn,
                                        GSBlocks.IO_BLOCKS.defaultBlockState().setValue(
                                            IoBlocks.BASIC_TYPE,
                                            EnumIoBlocks.IO_DUNGEON_TOP
                                        ),
                                        i,
                                        j,
                                        k,
                                        boundingBox
                                    )
                                }
                            } else this.setBlockState(worldIn, (this.configuration)!!.getBrickBlock(), i, j, k, chunkBox)
                        } else {
                            this.setBlockState(worldIn, Blocks.AIR.defaultBlockState(), i, j, k, chunkBox)
                        }
                    } else if ((i == 2 || i == this.sizeX - 2) || (k == 2 || k == this.sizeZ - 2)) {
                        if (i % 2 == 0 && k % 2 == 0) this.setBlockState(
                            worldIn,
                            GSBlocks.DUNGEON_BLOCKS.defaultBlockState()
                                .setValue(
                                    DungeonBlocks.BASIC_TYPE,
                                    EnumDungeonBlocks.IO_BRICKS
                                ),
                            i,
                            j,
                            k,
                            chunkBox
                        )
                        else this.setBlockState(worldIn, Blocks.AIR.defaultBlockState(), i, j, k, chunkBox)
                    } else this.setBlockState(worldIn, Blocks.AIR.defaultBlockState(), i, j, k, chunkBox)


                    /*
                    else if (j == this.sizeY)
                    {
                        if ((i <= 2 || k <= 2 || i >= this.sizeX - 2 || k >= this.sizeZ - 2) && random.nextInt(4) == 0)
                        {
                            this.setBlockState(worldIn, Blocks.GLOWSTONE.defaultBlockState(), i, j, k, chunkBox);
                        }
                        else
                        {
                            this.setBlockState(worldIn, GSBlocks.CERES_BLOCKS.defaultBlockState().setValue(CeresBlocks.BASIC_TYPE, CeresBlocks.EnumCeresBlocks.CERES_DUNGEON_TOP), i, j, k, boundingBox);
                            
                            //this.setBlockState(worldIn, this.configuration.getBrickBlock(), i, j, k, chunkBox);
                        }
                    }*/
                    /* else if (j == 1 && (i <= 2 || k <= 2 || i >= this.sizeX - 2 || k >= this.sizeZ - 2) && random.nextInt(6) == 0)
                    {
                        this.setBlockState(worldIn, MarsBlocks.creeperEgg.defaultBlockState(), i, j, k, chunkBox);
                    }*/
                    /*  else
                    {
                    	if((i == 2 || i == 4 || i == this.sizeX - 2 || i == this.sizeX - 4) && 
                    			(k == 2 || k == 4 && i != 4 && i != this.sizeX - 4 || k == this.sizeZ - 2 || k == this.sizeZ - 4 && i != 4 && i != this.sizeX - 4))
                            this.setBlockState(worldIn, Blocks.PACKED_ICE.defaultBlockState(), i, j, k, chunkBox);
                    	else
                            this.setBlockState(worldIn, Blocks.AIR.defaultBlockState(), i, j, k, chunkBox);
                    }*/
                }
            }
        }

        val spawnerX: Int = this.sizeX / 2
        val spawnerY: Int = this.sizeY / 2
        val spawnerZ: Int = this.sizeZ / 2
        val blockpos: BlockPos = BlockPos(
            this.getXWithOffset(spawnerX, spawnerZ),
            this.getYWithOffset(spawnerY),
            this.getZWithOffset(spawnerX, spawnerZ)
        )
        //Is this position inside the chunk currently being generated?
        if (chunkBox.isInside(blockpos)) {
            worldIn.setBlock(blockpos, GSBlocks.BOSS_SPAWNER_IO.defaultBlockState(), 2)
            val spawner: TileEntityDungeonSpawner<*>? = worldIn.getBlockEntity(blockpos) as TileEntityDungeonSpawner<*>?
            if (spawner != null) {
                spawner.setRoom(
                    Vector3(
                        (this.boundingBox.minX() + 1).toDouble(),
                        (this.boundingBox.minY() + 1).toDouble(),
                        (this.boundingBox.minZ() + 1).toDouble()
                    ), Vector3((this.sizeX - 1).toDouble(), (this.sizeY - 1).toDouble(), (this.sizeZ - 1).toDouble())
                )
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

    override fun getNextPiece(startPiece: DungeonStartIo?, rand: Random?): PieceIo? {
        val corridor: DirectionalPieceIo? = getCorridor((rand)!!, (startPiece)!!, 10, true) as DirectionalPieceIo?
        this.exitDirection = if (corridor == null) null else (corridor.getDirection())!!.getOpposite()
        return corridor
    }
}
