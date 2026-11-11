package galaxyspace.systems.SolarSystem.planets.ceres.world.gen.dungeon

import galaxyspace.core.GSBlocks
import galaxyspace.systems.SolarSystem.planets.ceres.world.gen.dungeon.RoomBossCeres
import galaxyspace.systems.SolarSystem.planets.ceres.blocks.CeresBlocks.EnumCeresBlocks
import galaxyspace.systems.SolarSystem.planets.ceres.blocks.CeresBlocks
import asmodeuscore.core.astronomy.dimension.world.gen.dungeons.standart.DungeonConfiguration
import asmodeuscore.core.astronomy.dimension.world.gen.dungeons.standart.RoomBoss
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.tile.TileEntityDungeonSpawner
import net.minecraft.world.level.block.Blocks
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.levelgen.structure.BoundingBox
import java.util.Random

open class RoomBossCeres : RoomBoss {
    constructor()

    constructor(
        configuration: DungeonConfiguration?,
        rand: Random?,
        blockPosX: Int,
        blockPosZ: Int,
        sizeX: Int,
        sizeY: Int,
        sizeZ: Int,
        entranceDir: EnumFacing
    ) : super((configuration)!!, rand, blockPosX, blockPosZ, sizeX, sizeY, sizeZ, entranceDir)

    constructor(
        configuration: DungeonConfiguration?,
        rand: Random?,
        blockPosX: Int,
        blockPosZ: Int,
        entranceDir: EnumFacing
    ) : super((configuration)!!, rand, blockPosX, blockPosZ, 24, 11, 24, entranceDir)

    override fun addComponentParts(worldIn: World, random: Random, chunkBox: BoundingBox): Boolean {
        for (i: Int in 0..this.sizeX) {
            for (j: Int in 0..this.sizeY) {
                for (k: Int in 0..this.sizeZ) {
                    if (i == 0 || i == this.sizeX || j == 0 || k == 0 || k == this.sizeZ) {
                        var placeBlock: Boolean = true
                        if ((getDirection())!!.getAxis() == EnumFacing.Axis.Z) {
                            val start: Int = (this.boundingBox.maxX() - this.boundingBox.minX()) / 2 - 1
                            val end: Int = (this.boundingBox.maxX() - this.boundingBox.minX()) / 2 + 1
                            if (i > start && i <= end && j < 3 && j > 0) {
                                if (getDirection() == EnumFacing.SOUTH && k == 0) {
                                    placeBlock = false
                                } else if (getDirection() == EnumFacing.NORTH && k == this.sizeZ) {
                                    placeBlock = false
                                }
                            }
                        } else {
                            val start: Int = (this.boundingBox.maxZ() - this.boundingBox.minZ()) / 2 - 1
                            val end: Int = (this.boundingBox.maxZ() - this.boundingBox.minZ()) / 2 + 1
                            if (k > start && k <= end && j < 3 && j > 0) {
                                if (getDirection() == EnumFacing.EAST && i == 0) {
                                    placeBlock = false
                                } else if (getDirection() == EnumFacing.WEST && i == this.sizeX) {
                                    placeBlock = false
                                }
                            }
                        }
                        if (placeBlock) {
                            if (j == 0) {
                                if (i == this.sizeX / 2 && k > 1 && k < this.sizeZ - 1) {
                                    this.setBlockState(
                                        worldIn,
                                        GSBlocks.FUTURE_GLASS_BASIC.defaultBlockState(),
                                        i,
                                        j,
                                        k,
                                        chunkBox
                                    )
                                    this.setBlockState(
                                        worldIn,
                                        Blocks.PACKED_ICE.defaultBlockState(),
                                        i,
                                        j - 1,
                                        k,
                                        chunkBox
                                    )
                                } else if (k == this.sizeZ / 2 && i > 1 && i < this.sizeX - 1) {
                                    this.setBlockState(
                                        worldIn,
                                        GSBlocks.FUTURE_GLASS_BASIC.defaultBlockState(),
                                        i,
                                        j,
                                        k,
                                        chunkBox
                                    )
                                    this.setBlockState(
                                        worldIn,
                                        Blocks.PACKED_ICE.defaultBlockState(),
                                        i,
                                        j - 1,
                                        k,
                                        chunkBox
                                    )
                                } else if ((i == 3 || i == this.sizeX - 3) && (k == 3 || k == this.sizeZ - 3)) this.setBlockState(
                                    worldIn,
                                    Blocks.GLOWSTONE.defaultBlockState(),
                                    i,
                                    j,
                                    k,
                                    chunkBox
                                )
                                else this.setBlockState(
                                    worldIn,
                                    GSBlocks.CERES_BLOCKS.defaultBlockState()
                                        .setValue(
                                            CeresBlocks.BASIC_TYPE,
                                            EnumCeresBlocks.CERES_DUNGEON_FLOOR
                                        ),
                                    i,
                                    j,
                                    k,
                                    boundingBox
                                )
                            } else this.setBlockState(worldIn, (this.configuration)!!.getBrickBlock(), i, j, k, chunkBox)
                        } else {
                            this.setBlockState(worldIn, Blocks.AIR.defaultBlockState(), i, j, k, chunkBox)
                        }
                    } else if (j == this.sizeY) {
                        if ((i <= 2 || k <= 2 || i >= this.sizeX - 2 || k >= this.sizeZ - 2) && random.nextInt(4) == 0) {
                            this.setBlockState(worldIn, Blocks.GLOWSTONE.defaultBlockState(), i, j, k, chunkBox)
                        } else {
                            this.setBlockState(
                                worldIn,
                                GSBlocks.CERES_BLOCKS.defaultBlockState()
                                    .setValue(
                                        CeresBlocks.BASIC_TYPE,
                                        EnumCeresBlocks.CERES_DUNGEON_TOP
                                    ),
                                i,
                                j,
                                k,
                                boundingBox
                            )


                            //this.setBlockState(worldIn, this.configuration.getBrickBlock(), i, j, k, chunkBox);
                        }
                    } else {
                        if ((i == 2 || i == 4 || i == this.sizeX - 2 || i == this.sizeX - 4) &&
                            (k == 2 || k == 4 && i != 4 && i != this.sizeX - 4 || k == this.sizeZ - 2 || k == this.sizeZ - 4 && i != 4 && i != this.sizeX - 4)
                        ) this.setBlockState(worldIn, Blocks.PACKED_ICE.defaultBlockState(), i, j, k, chunkBox)
                        else this.setBlockState(worldIn, Blocks.AIR.defaultBlockState(), i, j, k, chunkBox)
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
            worldIn.setBlock(blockpos, GSBlocks.BOSS_SPAWNER_CERES.defaultBlockState(), 2)
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
}
