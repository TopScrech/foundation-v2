package micdoodle8.mods.galacticraft.planets.venus.world.gen.dungeon

import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.tile.TileEntityTreasureChest
import micdoodle8.mods.galacticraft.planets.venus.VenusBlocks
import micdoodle8.mods.galacticraft.planets.venus.blocks.BlockTier3TreasureChest
import net.minecraft.world.level.block.Blocks
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.levelgen.structure.BoundingBox
import net.minecraft.world.gen.structure.StructureComponent
import net.minecraft.world.storage.loot.LootTableList
import java.util.Random

open class RoomTreasureVenus : SizedPieceVenus {
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
        rand.nextInt(4) + 6,
        configuration.getRoomHeight(),
        rand.nextInt(4) + 6,
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
                            this.setBlockState(worldIn, Blocks.AIR.defaultBlockState(), i, j, k, boundingBox)
                        }
                    } else if ((i == 1 && k == 1) || (i == 1 && k == this.sizeZ - 1) || (i == this.sizeX - 1 && k == 1) || (i == this.sizeX - 1 && k == this.sizeZ - 1)) {
                        this.setBlockState(worldIn, Blocks.GLOWSTONE.defaultBlockState(), i, j, k, boundingBox)
                    } else if (i == this.sizeX / 2 && j == 1 && k == this.sizeZ / 2) {
                        val blockpos: BlockPos =
                            BlockPos(this.getXWithOffset(i, k), this.getYWithOffset(j), this.getZWithOffset(i, k))
                        if (boundingBox.isInside(blockpos)) {
                            worldIn.setBlock(
                                blockpos,
                                (VenusBlocks.treasureChestTier3)!!.defaultBlockState().setValue(
                                    micdoodle8.mods.galacticraft.core.blocks.BlockTier1TreasureChest.FACING,
                                    (this.getDirection())!!.getOpposite()
                                ),
                                2
                            )
                            val treasureChest: TileEntityTreasureChest? =
                                worldIn.getBlockEntity(blockpos) as TileEntityTreasureChest?
                            if (treasureChest != null) {
                                var chesttype: ResourceLocation? = TABLE_TIER_3_DUNGEON
                                if (worldIn.provider is IGalacticraftWorldProvider) {
                                    chesttype = (worldIn.provider as IGalacticraftWorldProvider).getDungeonChestType()
                                }
                                treasureChest.setLootTable(chesttype, random.nextLong())
                            }
                        }
                    } else {
                        this.setBlockState(worldIn, Blocks.AIR.defaultBlockState(), i, j, k, boundingBox)
                    }
                }
            }
        }

        return true
    }

    override fun getNextPiece(startPiece: DungeonStartVenus?, rand: Random?): PieceVenus? {
        if ((startPiece)!!.attachedComponents.size > 2) {
            val component: StructureComponent? =
                (startPiece)!!.attachedComponents.get((startPiece)!!.attachedComponents.size - 3)
            if (component is RoomBossVenus) {
                val blockpos: BlockPos = BlockPos(
                    this.getXWithOffset(this.sizeX / 2, this.sizeZ / 2),
                    this.getYWithOffset(1),
                    this.getZWithOffset(this.sizeX / 2, this.sizeZ / 2)
                )
                component.setChestPos(BlockPos(blockpos))
            }
        }
        return null
    }

    companion object {
        @JvmField
        var VENUSCHEST: ResourceLocation = ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "dungeon_tier_3")

        @JvmField
        val TABLE_TIER_3_DUNGEON: ResourceLocation = LootTableList.register(VENUSCHEST)
    }
}
