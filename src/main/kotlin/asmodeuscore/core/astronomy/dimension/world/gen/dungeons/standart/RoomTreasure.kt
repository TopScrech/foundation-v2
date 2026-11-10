package asmodeuscore.core.astronomy.dimension.world.gen.dungeons.standart

import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.blocks.BlockTier1TreasureChest
import micdoodle8.mods.galacticraft.core.tile.TileEntityTreasureChest
import micdoodle8.mods.galacticraft.core.world.gen.dungeon.RoomTreasure
import net.minecraft.init.Blocks
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.gen.structure.StructureBoundingBox
import net.minecraft.world.gen.structure.StructureComponent
import java.util.Random

open class RoomTreasure : SizedPiece {
    //public static ResourceLocation MOONCHEST = new ResourceLocation(Constants.ASSET_PREFIX, "dungeon_tier_20");
    //public static final ResourceLocation TABLE_TIER_1_DUNGEON = LootTableList.register(MOONCHEST);
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
                        this.setBlockState(worldIn, Blocks.GLOWSTONE.getDefaultState(), i, j, k, chunkBox)
                    } else if (i == this.sizeX / 2 && j == 1 && k == this.sizeZ / 2) {
                        val blockpos: BlockPos =
                            BlockPos(this.getXWithOffset(i, k), this.getYWithOffset(j), this.getZWithOffset(i, k))
                        if (chunkBox.isVecInside(blockpos)) {
                            worldIn.setBlockState(
                                blockpos,
                                (GCBlocks.treasureChestTier1)!!.getDefaultState().withProperty(
                                    BlockTier1TreasureChest.FACING,
                                    (this.getDirection())!!.getOpposite()
                                ),
                                2
                            )
                            val treasureChest: TileEntityTreasureChest? =
                                worldIn.getTileEntity(blockpos) as TileEntityTreasureChest?
                            if (treasureChest != null) {
                                var chesttype: ResourceLocation? = RoomTreasure.TABLE_TIER_1_DUNGEON
                                if (worldIn.provider is IGalacticraftWorldProvider) {
                                    chesttype = (worldIn.provider as IGalacticraftWorldProvider).getDungeonChestType()
                                }
                                treasureChest.setLootTable(chesttype, random.nextLong())
                            }
                        }
                    } else {
                        this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), i, j, k, chunkBox)
                    }
                }
            }
        }

        return true
    }

    override fun getNextPiece(startPiece: DungeonStart?, rand: Random?): Piece? {
        if ((startPiece)!!.attachedComponents.size > 2) {
            val component: StructureComponent? =
                (startPiece)!!.attachedComponents.get((startPiece)!!.attachedComponents.size - 3)
            if (component is RoomBoss) {
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
}
