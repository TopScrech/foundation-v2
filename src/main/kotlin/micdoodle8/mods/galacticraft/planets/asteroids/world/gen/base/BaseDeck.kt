package micdoodle8.mods.galacticraft.planets.asteroids.world.gen.base

import foundation.block.LegacyVanillaBlockStates
import foundation.block.LegacyVanillaBlockIds

import foundation.block.getStateFromMeta

import foundation.persistence.containsTag
import foundation.persistence.listOfType

import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.planets.asteroids.blocks.AsteroidBlocks
import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.item.Items
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.nbt.ListTag as NBTTagList
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.levelgen.structure.BoundingBox
import net.minecraft.world.gen.structure.template.TemplateManager
import java.lang.Exception
import java.util.ArrayList
import java.util.Random

open class BaseDeck : SizedPiece {
    @JvmField
    protected var roomsOnSide: Int = 0

    @JvmField
    protected var roomDepth: Int = 0

    @JvmField
    protected var largeRoomPosA: Int = 0

    @JvmField
    protected var largeRoomPosB: Int = 0

    @JvmField
    protected var deckTier: Int =
        0 // flag 1 means lowest deck in a stack, flag 2 is for highest deck, 0 is for middle decks - flag 4 is for control room
    private var otherDecks: ArrayList<BaseDeck>? = null

    enum class EnumBaseType
        (
        @JvmField val height: Int,
        @JvmField val width: Int,
        @JvmField val wall: IBlockState,
        @JvmField val treasure: ItemStack
    ) {
        HUMANOID(5, 3, (GCBlocks.basicBlock)!!.getStateFromMeta(4), ItemStack(Items.GHAST_TEAR, 3, 0)),
        AVIAN(4, 3, (GCBlocks.blockMoon)!!.getStateFromMeta(4), ItemStack(AsteroidsItems.strangeSeed, 1, 1)),
        TUNNELER(4, 4, (GCBlocks.blockMoon)!!.getStateFromMeta(4), ItemStack(AsteroidsItems.strangeSeed, 1, 0))
    }

    constructor()

    constructor(
        configuration: BaseConfiguration,
        rand: Random,
        blockPosX: Int,
        blockPosY: Int,
        blockPosZ: Int,
        tier: Int,
        dir: EnumFacing?
    ) : super(
        configuration,
        configuration.getCorridorWidth() + 1,
        configuration.getRoomHeight() + 1,
        configuration.getCorridorLength(),
        dir
    ) {
        this.roomsOnSide = configuration.getRoomsNo()
        this.roomDepth = configuration.getRoomDepth() + (if (tier == 0) 1 else 0)
        this.deckTier = tier
        this.setCoordBaseMode(EnumFacing.NORTH)
        if ((this.getDirection())!!.getAxis() == EnumFacing.Axis.X) {
            val w: Int = this.sizeX
            this.sizeX = this.sizeZ
            this.sizeZ = w
        }
        var xMin: Int = blockPosX - this.sizeX / 2
        var zMin: Int = blockPosZ - this.sizeZ / 2
        this.boundingBox =
            BoundingBox(xMin, blockPosY, zMin, xMin + this.sizeX, blockPosY + this.sizeY, zMin + this.sizeZ)
        if (this.roomsOnSide <= 1) {
            largeRoomPosA = 0
            largeRoomPosB = 2
        } else {
            largeRoomPosA = rand.nextInt(this.roomsOnSide - 1)
            largeRoomPosB = largeRoomPosA + 1 + rand.nextInt(this.roomsOnSide - largeRoomPosA - 1)
        }

        if (tier == 1)  //1 means lowest tier and there are going to be some higher ones
        {
            this.createOtherDecks(configuration, rand, blockPosX, blockPosY, blockPosZ)
        }


        //Special settings for Control Room
        if ((tier and 4) == 4) {
            this.roomDepth -= 2
            if ((this.getDirection())!!.getAxis() == EnumFacing.Axis.X) {
                this.sizeZ += 4
            } else {
                this.sizeX += 4
            }
            xMin = blockPosX - this.sizeX / 2
            zMin = blockPosZ - this.sizeZ / 2
            when (this.getDirection()) {
                EnumFacing.NORTH -> this.sizeZ++
                EnumFacing.SOUTH -> {
                    zMin--
                    this.sizeZ++
                }

                EnumFacing.WEST -> this.sizeX++
                EnumFacing.EAST -> {
                    xMin--
                    this.sizeX++
                }

                else -> {
                    xMin--
                    this.sizeX++
                }
            }
            this.boundingBox = BoundingBox(
                xMin,
                blockPosY,
                zMin,
                xMin + this.sizeX,
                blockPosY + this.sizeY,
                zMin + this.sizeZ
            )
        }
    }

    private fun createOtherDecks(
        configuration: BaseConfiguration,
        rand: Random,
        blockPosX: Int,
        blockPosY: Int,
        blockPosZ: Int
    ) {
        this.otherDecks = ArrayList<BaseDeck>()
        if ((this.configuration)!!.isHangarDeck()) {
            this.otherDecks!!.add(
                BaseDeck(
                    configuration,
                    rand,
                    blockPosX,
                    blockPosY + this.sizeY + 1,
                    blockPosZ,
                    6,
                    this.direction
                )
            )
        } else {
            this.otherDecks!!.add(
                BaseDeck(
                    configuration,
                    rand,
                    blockPosX,
                    blockPosY + this.sizeY,
                    blockPosZ,
                    0,
                    this.direction
                )
            )
            this.otherDecks!!.add(
                BaseDeck(
                    configuration,
                    rand,
                    blockPosX,
                    blockPosY + this.sizeY + this.sizeY,
                    blockPosZ,
                    2,
                    this.direction
                )
            )
        }
    }

    override fun writeStructureToNBT(tagCompound: NBTTagCompound) {
        super.writeStructureToNBT(tagCompound)

        val details: Int = this.deckTier + (this.largeRoomPosA shl 4) + (this.largeRoomPosB shl 8)
        tagCompound.putInt("dD", details)
        if (this.otherDecks != null) {
            val tagList: NBTTagList = NBTTagList()
            for (deck: BaseDeck in (this.otherDecks)!!) {
                val tag1: NBTTagCompound = NBTTagCompound()
                deck.writeStructureToNBT(tag1)
                tagList.add(tag1)
            }
            tagCompound.put("oD", tagList)
        }
    }

    override fun readStructureFromNBT(tagCompound: NBTTagCompound, manager: TemplateManager) {
        super.readStructureFromNBT(tagCompound, manager)
        try {
            val details: Int = tagCompound.getIntOr("dD", 0)
            this.deckTier = details and 15
            this.largeRoomPosB = details shr 8
            this.largeRoomPosA = (details shr 4) and 15
            this.roomsOnSide = (configuration)!!.getRoomsNo()
            this.roomDepth =
                (configuration)!!.getRoomDepth() + (if (this.deckTier == 0) 1 else 0) - (if ((this.deckTier and 4) == 4) 3 else 0)

            if (tagCompound.containsTag("oD")) {
                val tagList: NBTTagList = tagCompound.listOfType("oD", 10)
                this.otherDecks = ArrayList<BaseDeck>()
                for (i: Int in 0..<tagList.size) {
                    val tagAt: NBTTagCompound = tagList.getCompoundOrEmpty(i)
                    val deck: BaseDeck = BaseDeck()
                    deck.readStructureFromNBT(tagAt, manager)
                    this.otherDecks!!.add(deck)
                }
            }
        } catch (e: Exception) {
            System.err.println("Failed to read Abandoned Base configuration from NBT")
            System.err.println(tagCompound.toString())
        }
    }

    override fun addComponentParts(worldIn: World, randomIn: Random, chunkBounds: BoundingBox): Boolean {
        var block1: IBlockState?
        val blockWall: IBlockState = ((this.configuration)!!.getWallBlock())!!
        val blockGrid: IBlockState = (AsteroidBlocks.blockWalkway)!!.defaultBlockState()
        val blockStair: Block = (GCBlocks.moonStoneStairs)!!
        val blockAir: IBlockState = Blocks.AIR.defaultBlockState()
        val directionNS: Boolean = (this.getDirection())!!.getAxis() == EnumFacing.Axis.Z
        val ceilingSpacer: Int = (if (directionNS) this.sizeZ else this.sizeX) / this.roomsOnSide + 1
        var ceilingDeco: Int = ceilingSpacer

        val startX: Int = 0
        val startZ: Int = 0
        var endX: Int = this.sizeX
        var endZ: Int = this.sizeZ
        var endY: Int = this.sizeY
        if ((this.configuration)!!.getDeckType() == EnumBaseType.AVIAN && !(this.configuration)!!.isHangarDeck()) endY--

        //This is the central corridor of every base
        for (x: Int in startX..endX) {
            for (z: Int in startZ..endZ) {
                for (y: Int in 0..endY) {
                    if (x == startX || x == endX || y == 0 || y == endY || z == startZ || z == endZ) {
                        var skipCap: Boolean = false
                        if ((this.configuration)!!.isHangarDeck() && this.deckTier == 1 && (this.getDirection() == EnumFacing.NORTH && z == endZ || this.getDirection() == EnumFacing.SOUTH && z == startZ || this.getDirection() == EnumFacing.WEST && x == endX || this.getDirection() == EnumFacing.EAST && x == startX)) {
                            //Make airlock at the hangar end, if it's a hangar deck
                            val blockLintel: IBlockState = (GCBlocks.airLockFrame)!!.defaultBlockState()
                            val blockAirlock: IBlockState = (GCBlocks.airLockFrame)!!.getStateFromMeta(1)
                            if ((!directionNS && (z == startZ + 1 || z == endZ - 1) || directionNS && (x == startX + 1 || x == endX - 1)) && y < sizeY - 1) {
                                this.setBlockState(worldIn, blockLintel, x, y, z, chunkBounds)
                            } else if (y >= endY - 1 || directionNS && (x == startX || x == endX) || !directionNS && (z == startZ || z == endZ)) {
                                this.setBlockState(worldIn, blockWall, x, y, z, chunkBounds)
                            } else if (y == 0 || y == endY - 2) {
                                //Test mid point bottom
                                if (y == 0 && (!directionNS && z == (startZ + endZ) / 2 || directionNS && x == (startX + endX) / 2)) this.setBlockState(
                                    worldIn,
                                    blockAirlock,
                                    x,
                                    y,
                                    z,
                                    chunkBounds
                                )
                                else this.setBlockState(worldIn, blockLintel, x, y, z, chunkBounds)
                                skipCap = true
                            } else {
                                this.setBlockState(worldIn, blockAir, x, y, z, chunkBounds)
                            }
                        } else if (!((y == 0 && (this.deckTier and 1) == 1 || y == endY && (this.deckTier and 1) == 2) && (directionNS && (z == startZ || z == endZ) || !directionNS && (x == startX || x == endX)))) {
                            if (y == 0 && (this.deckTier and 1) == 1 && (this.configuration)!!.getDeckType() == EnumBaseType.HUMANOID && (directionNS && (x == startX + 2 + (if ((this.configuration)!!.isHangarDeck()) 1 else 0) || x == startX + 4 + (if ((this.configuration)!!.isHangarDeck()) 1 else 0)) || !directionNS && (z == startZ + 2 + (if ((this.configuration)!!.isHangarDeck()) 1 else 0) || z == startZ + 4 + (if ((this.configuration)!!.isHangarDeck()) 1 else 0)))) {
                                this.setBlockState(worldIn, blockGrid, x, y, z, chunkBounds)
                            } else if (!(this.configuration)!!.isHangarDeck() && (this.getDirection() == EnumFacing.SOUTH && z == endZ || this.getDirection() == EnumFacing.NORTH && z == startZ || this.getDirection() == EnumFacing.EAST && x == endX || this.getDirection() == EnumFacing.WEST && x == startX)) {
                                //Some end windows at the ends of the corridors
                                var windowOrWall: IBlockState = blockWall
                                val blockGlass: IBlockState
                                when ((this.configuration)!!.getDeckType()) {
                                    EnumBaseType.AVIAN -> blockGlass = (GCBlocks.spaceGlassTinVanilla)!!.defaultBlockState()
                                    EnumBaseType.TUNNELER -> blockGlass = (GCBlocks.spaceGlassTinStrong)!!.defaultBlockState()
                                    EnumBaseType.HUMANOID -> blockGlass = (GCBlocks.spaceGlassTinClear)!!.defaultBlockState()
                                    else -> blockGlass = (GCBlocks.spaceGlassTinClear)!!.defaultBlockState()
                                }

                                val w: Int
                                val startW: Int
                                val endW: Int
                                if (directionNS) {
                                    w = x
                                    startW = startX
                                    endW = endX
                                } else {
                                    w = z
                                    startW = startZ
                                    endW = endZ
                                }

                                if ((this.configuration)!!.getDeckType() == EnumBaseType.AVIAN) {
                                    if (y == 2 && w == startW + 2) {
                                        windowOrWall = blockGlass
                                    }
                                } else {
                                    val edge: Int =
                                        if ((this.configuration)!!.getDeckType() == EnumBaseType.TUNNELER) 1 else 0
                                    if ((y == 2 || y == 3) && w > startW + edge && w < endW - edge) {
                                        windowOrWall = blockGlass
                                    }
                                }

                                this.setBlockState(worldIn, windowOrWall, x, y, z, chunkBounds)
                            } else {
                                this.setBlockState(worldIn, blockWall, x, y, z, chunkBounds)
                            }
                        }


                        //Special case: cap the grid ends
                        val hangarOffset: Int = (if ((this.configuration)!!.isHangarDeck()) 1 else 0)
                        if (y == 0 && (this.deckTier and 1) == 1 && (this.configuration)!!.getDeckType() == EnumBaseType.HUMANOID && (directionNS && (x >= startX + 2 + hangarOffset && x <= startX + 4 + hangarOffset) || !directionNS && (z >= startZ + 2 + hangarOffset && z <= startZ + 4 + hangarOffset))) {
                            if (directionNS && (z == startZ || z == endZ) || !directionNS && (x == startX || x == endX)) {
                                if (!skipCap) {
                                    this.setBlockState(worldIn, blockWall, x, y, z, chunkBounds)
                                }
                            }
                        }
                    } else if (((this.configuration)!!.getDeckType())!!.ordinal >= EnumBaseType.AVIAN.ordinal && (y == 1 || y == endY - 1)) {
                        //Internal decoration - deck corridor corners
                        var top: Int = if (y == 1) 0 else 4
                        if ((this.configuration)!!.getDeckType() == EnumBaseType.TUNNELER) top++

                        if (directionNS) {
                            if (x == startX + 1) {
                                this.setBlockState(
                                    worldIn,
                                    blockStair.getStateFromMeta(0 xor top),
                                    x,
                                    y,
                                    z,
                                    chunkBounds
                                )
                            } else if (x == endX - 1) {
                                this.setBlockState(
                                    worldIn,
                                    blockStair.getStateFromMeta(1 xor top),
                                    x,
                                    y,
                                    z,
                                    chunkBounds
                                )
                            } else if ((this.configuration)!!.getDeckType() == EnumBaseType.AVIAN) {
                                if (z == ceilingDeco && top >= 4) {
                                    this.setBlockState(
                                        worldIn,
                                        blockStair.getStateFromMeta(3 xor top),
                                        x,
                                        y,
                                        z,
                                        chunkBounds
                                    )
                                } else if (z == ceilingDeco + 1 && top >= 4) {
                                    this.setBlockState(
                                        worldIn,
                                        blockStair.getStateFromMeta(2 xor top),
                                        x,
                                        y,
                                        z,
                                        chunkBounds
                                    )
                                    if (x >= endX - 2) ceilingDeco += ceilingSpacer
                                } else {
                                    this.setBlockState(worldIn, blockAir, x, y, z, chunkBounds)
                                }
                            } else {
                                this.setBlockState(worldIn, blockAir, x, y, z, chunkBounds)
                            }
                        } else {
                            if (z == startZ + 1) {
                                this.setBlockState(
                                    worldIn,
                                    blockStair.getStateFromMeta(2 xor top),
                                    x,
                                    y,
                                    z,
                                    chunkBounds
                                )
                            } else if (z == endZ - 1) {
                                this.setBlockState(
                                    worldIn,
                                    blockStair.getStateFromMeta(3 xor top),
                                    x,
                                    y,
                                    z,
                                    chunkBounds
                                )
                            } else if ((this.configuration)!!.getDeckType() == EnumBaseType.AVIAN) {
                                if (x == ceilingDeco && top == 4) {
                                    this.setBlockState(
                                        worldIn,
                                        blockStair.getStateFromMeta(1 xor top),
                                        x,
                                        y,
                                        z,
                                        chunkBounds
                                    )
                                } else if (x == ceilingDeco + 1 && top == 4) {
                                    this.setBlockState(worldIn, blockStair.getStateFromMeta(top), x, y, z, chunkBounds)
                                    if (z >= endZ - 2) ceilingDeco += ceilingSpacer
                                } else {
                                    this.setBlockState(worldIn, blockAir, x, y, z, chunkBounds)
                                }
                            } else {
                                this.setBlockState(worldIn, blockAir, x, y, z, chunkBounds)
                            }
                        }
                    } else {
                        this.setBlockState(worldIn, blockAir, x, y, z, chunkBounds)
                    }
                }


                //Infill for the extra height layer
                if (endY != this.sizeY) {
                    this.setBlockState(worldIn, blockWall, x, this.sizeY, z, chunkBounds)
                }
            }
        }

        var leftX: Int =
            if ((this.deckTier and 4) == 4 && (this.direction == EnumFacing.SOUTH || this.direction == EnumFacing.EAST)) 3 else 2
        var rightX: Int = leftX
        var leftZ: Int = leftX
        var rightZ: Int = rightX
        for (i: Int in 0..<this.roomsOnSide) {
            val largeRoom: Boolean = (i == largeRoomPosA || i == largeRoomPosB)
            val roomsize: Int = if (largeRoom) ROOMLARGE else ROOMSMALL
            val largeRoomOffset: Int = if (largeRoom) 2 else 1

            if (directionNS) {
                this.makeDoorway(worldIn, this.sizeX, leftZ + largeRoomOffset, directionNS, chunkBounds)
                this.makeDoorway(worldIn, 0, rightZ + largeRoomOffset, directionNS, chunkBounds)

                leftZ += roomsize
                rightZ += roomsize
            } else {
                this.makeDoorway(worldIn, leftX + largeRoomOffset, this.sizeZ, directionNS, chunkBounds)
                this.makeDoorway(worldIn, rightX + largeRoomOffset, 0, directionNS, chunkBounds)

                leftX += roomsize
                rightX += roomsize
            }
        }

        if (directionNS) {
            this.setCoordBaseMode(this.direction)
        } else {
            this.setCoordBaseMode((this.direction)!!.getOpposite()) //bug in Vanilla?
        }
        if ((this.configuration)!!.isHangarDeck() && this.deckTier == 1) {
            //Create an access hole between the floors
            val width: Int = if (directionNS) this.sizeX else this.sizeZ
            this.setBlockState(worldIn, blockAir, 0, this.sizeY, 1, chunkBounds)
            this.setBlockState(worldIn, blockAir, 1, this.sizeY, 1, chunkBounds)
            this.setBlockState(worldIn, blockAir, 1, this.sizeY - 1, 1, chunkBounds)
            this.setBlockState(worldIn, blockAir, width - 1, this.sizeY - 1, 1, chunkBounds)
            this.setBlockState(worldIn, blockAir, width - 1, this.sizeY, 1, chunkBounds)
            this.setBlockState(worldIn, blockAir, width, this.sizeY, 1, chunkBounds)
        }

        //Special settings for Control Room
        if ((this.deckTier and 4) == 4) {
            val blockGlass: IBlockState
            when ((this.configuration)!!.getDeckType()) {
                EnumBaseType.AVIAN -> blockGlass = (GCBlocks.spaceGlassTinVanilla)!!.defaultBlockState()
                EnumBaseType.TUNNELER -> blockGlass = (GCBlocks.spaceGlassTinStrong)!!.defaultBlockState()
                EnumBaseType.HUMANOID -> blockGlass = (GCBlocks.spaceGlassTinClear)!!.defaultBlockState()
                else -> blockGlass = (GCBlocks.spaceGlassTinClear)!!.defaultBlockState()
            }
            if (!directionNS) {
                val w: Int = endX
                endX = endZ
                endZ = w
            }
            for (y: Int in 1..4) {
                for (x: Int in 2..<endX - 1) {
                    this.setBlockState(worldIn, blockGlass, x, y, endZ, chunkBounds)
                }
            }


            //Create two levers
            var facing: Int = 0
            when (this.direction) {
                EnumFacing.NORTH -> {}
                EnumFacing.SOUTH -> facing = 2
                EnumFacing.EAST -> facing = 1
                EnumFacing.WEST -> facing = 3
            
else -> {}
}

            var lever: IBlockState =
                (GCBlocks.concealedDetector)!!.getStateFromMeta(8 + facing + (if ((this.configuration)!!.getDeckType() == EnumBaseType.HUMANOID) 0 else 4))
            this.setBlockState(worldIn, lever, endX / 2 - 2, this.sizeY - 1, endZ, chunkBounds)
            this.setBlockState(worldIn, lever, endX / 2 + 2, this.sizeY - 1, endZ, chunkBounds)
            lever = LegacyVanillaBlockStates.state(LegacyVanillaBlockIds.LEVER, if ((this.direction)!!.getAxis() == EnumFacing.Axis.Z) 4 else 3)
            this.setBlockState(worldIn, lever, endX / 2 - 2, this.sizeY - 1, endZ - 1, chunkBounds)
            this.setBlockState(worldIn, lever, endX / 2 + 2, this.sizeY - 1, endZ - 1, chunkBounds)


            //Create two redstone blocks
            this.setBlockState(
                worldIn,
                (GCBlocks.concealedRedstone)!!.getStateFromMeta(15),
                endX / 2 - 2,
                this.sizeY,
                endZ,
                chunkBounds
            )
            this.setBlockState(
                worldIn,
                (GCBlocks.concealedRedstone)!!.getStateFromMeta(15),
                endX / 2 + 2,
                this.sizeY,
                endZ,
                chunkBounds
            )

            //Create an access hole between the floors
            this.setBlockState(worldIn, blockAir, 2, 0, 1, chunkBounds)
            this.setBlockState(worldIn, blockAir, 3, 0, 1, chunkBounds)
            this.setBlockState(worldIn, blockAir, 11, 0, 1, chunkBounds)
            this.setBlockState(worldIn, blockAir, 12, 0, 1, chunkBounds)
        }
        this.setCoordBaseMode(EnumFacing.NORTH)

        return true
    }

    open fun getRooms(roomIndex: Int, startPiece: BaseStart?, rand: Random?): MutableList<Piece?> {
        var roomIndex: Int = roomIndex
        val rooms: MutableList<Piece?> = ArrayList<Piece?>()

        val directionNS: Boolean = (this.getDirection())!!.getAxis() == EnumFacing.Axis.Z
        val left: EnumFacing = if (directionNS) EnumFacing.WEST else EnumFacing.SOUTH
        val right: EnumFacing = if (directionNS) EnumFacing.EAST else EnumFacing.NORTH
        var leftX: Int = if (directionNS) this.boundingBox.maxX() + 1 else this.boundingBox.minX()
        var leftZ: Int = if (directionNS) this.boundingBox.minZ() else this.boundingBox.maxZ() + 1
        var rightX: Int = if (directionNS) this.boundingBox.minX() - this.roomDepth - 1 else this.boundingBox.minX()
        var rightZ: Int = if (directionNS) this.boundingBox.minZ() else this.boundingBox.minZ() - this.roomDepth - 1
        //now offset effect of moving control room 1 position North or West, in two cases
        if ((this.deckTier and 4) == 4 && this.direction == EnumFacing.SOUTH) {
            rightZ++
            leftZ++
        }
        if ((this.deckTier and 4) == 4 && this.direction == EnumFacing.EAST) {
            rightX++
            leftX++
        }

        for (i: Int in 0..<this.roomsOnSide) {
            val largeRoom: Boolean = (i == largeRoomPosA || i == largeRoomPosB)
            val roomsize: Int = if (largeRoom) ROOMLARGE else ROOMSMALL
            rooms.add(this.getRoom(roomIndex++, left, leftX, leftZ, largeRoom, true, rand))
            rooms.add(this.getRoom(roomIndex++, right, rightX, rightZ, largeRoom, false, rand))
            if (directionNS) {
                leftZ += roomsize
                rightZ += roomsize
            } else {
                leftX += roomsize
                rightX += roomsize
            }
        }

        if ((this.configuration)!!.isHangarDeck() && (this.deckTier and 1) == 1) {
            var blockX: Int = 0
            var blockZ: Int = 0
            when (this.direction) {
                EnumFacing.NORTH -> {
                    blockX = this.boundingBox.minX() + (this.sizeX - BaseHangar.Companion.HANGARWIDTH) / 2
                    blockZ = this.boundingBox.maxZ() + 1
                }

                EnumFacing.SOUTH -> {
                    blockX = this.boundingBox.minX() + (this.sizeX - BaseHangar.Companion.HANGARWIDTH) / 2
                    blockZ = this.boundingBox.minZ() - 1 - BaseHangar.Companion.HANGARLENGTH
                }

                EnumFacing.WEST -> {
                    blockX = this.boundingBox.maxX() + 1
                    blockZ = this.boundingBox.minZ() + (this.sizeZ - BaseHangar.Companion.HANGARWIDTH) / 2
                }

                EnumFacing.EAST -> {
                    blockX = this.boundingBox.minX() - 1 - BaseHangar.Companion.HANGARLENGTH
                    blockZ = this.boundingBox.minZ() + (this.sizeZ - BaseHangar.Companion.HANGARWIDTH) / 2
                }

                else -> {
                    blockX = this.boundingBox.minX() - 1 - BaseHangar.Companion.HANGARLENGTH
                    blockZ = this.boundingBox.minZ() + (this.sizeZ - BaseHangar.Companion.HANGARWIDTH) / 2
                }
            }

            rooms.add(BaseHangar((this.configuration)!!, rand, blockX, blockZ, (this.direction)!!.getOpposite()))
        }

        if (((this.configuration)!!.isHangarDeck() || (this.configuration)!!.getDeckType() == EnumBaseType.HUMANOID) && (this.deckTier and 1) == 1) {
            var blockX: Int = this.boundingBox.minX()
            var blockZ: Int = this.boundingBox.minZ()
            var sX: Int = this.sizeX
            var sZ: Int = this.sizeZ
            when (this.direction) {
                EnumFacing.NORTH -> {
                    blockZ++
                    sZ--
                    if (!(this.configuration)!!.isHangarDeck()) sZ--
                }

                EnumFacing.SOUTH -> {
                    sZ--
                    if (!(this.configuration)!!.isHangarDeck()) {
                        blockZ++
                        sZ--
                    }
                }

                EnumFacing.WEST -> {
                    blockX++
                    sX--
                    if (!(this.configuration)!!.isHangarDeck()) sX--
                }

                EnumFacing.EAST -> {
                    sX--
                    if (!(this.configuration)!!.isHangarDeck()) {
                        blockX++
                        sX--
                    }
                }

                else -> {
                    sX--
                    if (!(this.configuration)!!.isHangarDeck()) {
                        blockX++
                        sX--
                    }
                }
            }
            rooms.add(BasePlate(this.configuration, blockX, this.boundingBox.minY() - 1, blockZ, sX, sZ, this.direction))
        }

        if (this.otherDecks != null) {
            for (deck: BaseDeck in (this.otherDecks)!!) {
                rooms.add(deck)
                val newRooms: MutableList<Piece?> = deck.getRooms(roomIndex, startPiece, rand)
                rooms.addAll(newRooms)
                roomIndex += newRooms.size
            }
        }

        return rooms
    }

    protected open fun makeDoorway(
        worldIn: World,
        x: Int,
        z: Int,
        directionNS: Boolean,
        chunkBounds: BoundingBox
    ) {
//        System.out.println("Making doorway at " + x + "," + z + " NS:" + directionNS + " Tier " + this.deckTier);
        var x: Int = x
        var z: Int = z
        val blockLintel: IBlockState = (GCBlocks.airLockFrame)!!.defaultBlockState()
        val blockAirlock: IBlockState = (GCBlocks.airLockFrame)!!.getStateFromMeta(1)
        val blockStair: Block = (GCBlocks.moonStoneStairs)!!
        val blockAir: IBlockState = Blocks.AIR.defaultBlockState()
        val meta: Int = if (directionNS) 2 else 0

        when ((this.configuration)!!.getDeckType()) {
            EnumBaseType.HUMANOID -> {
                if (directionNS) z-- else x--
                this.setBlockState(worldIn, blockLintel, x, 1, z, chunkBounds)
                this.setBlockState(worldIn, blockLintel, x, 2, z, chunkBounds)
                this.setBlockState(worldIn, blockLintel, x, 3, z, chunkBounds)
                if (directionNS) z++ else x++
                this.setBlockState(worldIn, blockAir, x, 1, z, chunkBounds)
                this.setBlockState(worldIn, blockAir, x, 2, z, chunkBounds)
                this.setBlockState(worldIn, blockAirlock, x, 0, z, chunkBounds)
                this.setBlockState(worldIn, blockLintel, x, 3, z, chunkBounds)
                if (directionNS) z++ else x++
                this.setBlockState(worldIn, blockLintel, x, 1, z, chunkBounds)
                this.setBlockState(worldIn, blockLintel, x, 2, z, chunkBounds)
                this.setBlockState(worldIn, blockLintel, x, 3, z, chunkBounds)
            }

            EnumBaseType.AVIAN -> {
                this.setBlockState(worldIn, blockStair.getStateFromMeta(0 + meta), x, this.sizeY - 4, z, chunkBounds)
                this.setBlockState(worldIn, blockAir, x, this.sizeY - 3, z, chunkBounds)
                this.setBlockState(worldIn, blockStair.getStateFromMeta(4 + meta), x, this.sizeY - 2, z, chunkBounds)
                if (directionNS) z++ else x++
                this.setBlockState(worldIn, blockStair.getStateFromMeta(1 + meta), x, this.sizeY - 4, z, chunkBounds)
                this.setBlockState(worldIn, blockAir, x, this.sizeY - 3, z, chunkBounds)
                this.setBlockState(worldIn, blockStair.getStateFromMeta(5 + meta), x, this.sizeY - 2, z, chunkBounds)
            }

            EnumBaseType.TUNNELER -> {
                if (directionNS) z-- else x--
                this.setBlockState(worldIn, blockStair.getStateFromMeta(1 + meta), x, 2, z, chunkBounds)
                this.setBlockState(worldIn, blockStair.getStateFromMeta(5 + meta), x, 3, z, chunkBounds)
                if (directionNS) z++ else x++
                this.setBlockState(worldIn, blockAir, x, 2, z, chunkBounds)
                this.setBlockState(worldIn, blockAir, x, 3, z, chunkBounds)
                if (directionNS) z++ else x++
                this.setBlockState(worldIn, blockStair.getStateFromMeta(0 + meta), x, 2, z, chunkBounds)
                this.setBlockState(worldIn, blockStair.getStateFromMeta(4 + meta), x, 3, z, chunkBounds)
            }

            else -> {}
        }
    }

    protected open fun getRoom(
        i: Int,
        dir: EnumFacing,
        blockX: Int,
        blockZ: Int,
        large: Boolean,
        left: Boolean,
        rand: Random?
    ): Piece {
        var sX: Int = if (large) ROOMLARGE else ROOMSMALL
        var sZ: Int = sX
        if (dir.getAxis() == EnumFacing.Axis.Z) {
            sZ = this.roomDepth
        } else {
            sX = this.roomDepth
        }
        val sY: Int = this.sizeY
        return BaseRoom(
            this.configuration,
            rand,
            blockX,
            this.boundingBox.minY(),
            blockZ,
            sX,
            sY,
            sZ,
            dir,
            (this.configuration)!!.getRandomRoom(i),
            if (left) (i == 0) else (i == this.roomsOnSide - 1),
            if (left) (i == this.roomsOnSide - 1) else (i == 0),
            this.deckTier
        )
    }

    companion object {
        const val ROOMSMALL: Int = 6

        const val ROOMLARGE: Int = 8
    }
}
