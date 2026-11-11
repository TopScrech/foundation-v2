package micdoodle8.mods.galacticraft.planets.asteroids.world.gen.base

import foundation.item.FoundationItemVariants
import foundation.block.LegacyVanillaBlockStates
import foundation.block.LegacyVanillaBlockIds

import foundation.block.getStateFromMeta

import micdoodle8.mods.galacticraft.planets.asteroids.world.gen.base.BaseDeck.EnumBaseType

import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.blocks.BlockMulti
import micdoodle8.mods.galacticraft.core.entities.EntityHangingSchematic
import micdoodle8.mods.galacticraft.core.items.ItemCanisterGeneric
import micdoodle8.mods.galacticraft.core.tile.IMultiBlock
import micdoodle8.mods.galacticraft.core.tile.TileEntityCargoLoader
import micdoodle8.mods.galacticraft.core.tile.TileEntityCrafting
import micdoodle8.mods.galacticraft.core.tile.TileEntityEnergyStorageModule
import micdoodle8.mods.galacticraft.core.tile.TileEntityFluidTank
import micdoodle8.mods.galacticraft.planets.asteroids.blocks.AsteroidBlocks
import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems
import micdoodle8.mods.galacticraft.planets.mars.MarsModule
import micdoodle8.mods.galacticraft.planets.mars.blocks.BlockMachineMars
import micdoodle8.mods.galacticraft.planets.mars.blocks.MarsBlocks
import micdoodle8.mods.galacticraft.planets.mars.items.MarsItems
import net.minecraft.world.level.block.Block
import net.minecraft.block.ITileEntityProvider
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.item.Items
import net.minecraft.init.PotionTypes
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.potion.PotionUtils
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.tileentity.TileEntityBrewingStand
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.levelgen.structure.BoundingBox
import net.minecraft.world.gen.structure.template.TemplateManager
import net.minecraftforge.fluids.FluidStack
import java.lang.Exception
import java.util.LinkedList
import java.util.Random

open class BaseRoom : SizedPiece {
    private var type: EnumRoomType? = null
    private var nearEnd = false
    private var farEnd = false
    private var deckTier = 0

    constructor()

    constructor(
        configuration: BaseConfiguration?,
        rand: Random?,
        blockPosX: Int,
        yPos: Int,
        blockPosZ: Int,
        sizeX: Int,
        sizeY: Int,
        sizeZ: Int,
        entranceDir: EnumFacing,
        roomType: EnumRoomType?,
        near: Boolean,
        far: Boolean,
        deckTier: Int
    ) : super(configuration, sizeX, sizeY, sizeZ, entranceDir.getOpposite()) {
        this.setCoordBaseMode(this.direction)
        this.type = roomType
        this.nearEnd = near
        this.farEnd = far
        this.deckTier = deckTier

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

        val details: Int = this.deckTier + (if (this.nearEnd) 16 else 0) + (if (this.farEnd) 32 else 0)
        tagCompound.putInt("brT", type!!.ordinal)
        tagCompound.putInt("brD", details)
    }

    override fun readStructureFromNBT(tagCompound: NBTTagCompound, manager: TemplateManager) {
        super.readStructureFromNBT(tagCompound, manager)
        try {
            val typeNo: Int = tagCompound.getIntOr("brT", 0)
            if (typeNo < EnumRoomType.entries.size) {
                this.type = EnumRoomType.entries[typeNo]
            } else this.type = EnumRoomType.EMPTY

            val details: Int = tagCompound.getIntOr("brD", 0)
            this.deckTier = details and 15
            this.nearEnd = (details and 16) == 16
            this.farEnd = (details and 32) == 32
        } catch (e: Exception) {
            System.err.println("Failed to read Abandoned Base configuration from NBT")
            System.err.println(tagCompound.toString())
        }
    }

    override fun addComponentParts(worldIn: World, random: Random, chunkBoundary: BoundingBox): Boolean {
        val blockAir: IBlockState = Blocks.AIR.defaultBlockState()
        val blockStair: Block = (GCBlocks.moonStoneStairs)!!

        val axisEW: Boolean = (getDirection())!!.getAxis() == EnumFacing.Axis.X
        val maxX: Int = if (axisEW) this.sizeZ else this.sizeX
        val maxZ: Int = if (axisEW) this.sizeX else this.sizeZ
        val randomInt: Int = random.nextInt(99)
        for (xx: Int in 0..maxX) {
            val near: Boolean = this.nearEnd && xx == maxX
            val far: Boolean = this.farEnd && xx == 0
            for (yy: Int in 0..this.sizeY) {
                for (zz: Int in 0..maxZ) {
                    //5 outer walls of the room
                    if (xx == 0 || xx == maxX || yy == 0 || yy == this.sizeY || zz == maxZ) {
                        val xEntrance: Boolean = if (maxX > 6) (xx > 2 && xx < maxX - 2) else (xx > 1 && xx < maxX - 1)
                        if (this.type!!.blockEntrance != null && yy == 0 && zz == 0 && xEntrance && (this.configuration)!!.getDeckType() != BaseDeck.EnumBaseType.TUNNELER) {
                            this.setBlockState(worldIn, this.type!!.blockEntrance, xx, yy, zz, chunkBoundary)
                            this.setBlockState(
                                worldIn,
                                (this.configuration)!!.getWallBlock(),
                                xx,
                                yy - 1,
                                zz,
                                chunkBoundary
                            )
                        } else if (!((zz == maxZ || near || far) && (yy == 0 && (this.deckTier and 1) == 1 || yy == this.sizeY && (this.deckTier and 2) == 2 || (zz == maxZ && (near || far)))) || zz == 0 && yy == 0) {
                            this.setBlockState(worldIn, (this.configuration)!!.getWallBlock(), xx, yy, zz, chunkBoundary)
                        } else if (yy == this.sizeY && (this.deckTier and 2) == 2 && (this.configuration)!!.isHangarDeck() && zz < 3) {
                            if (xx == 0 || xx == maxX) {
                                this.setBlockState(
                                    worldIn,
                                    (this.configuration)!!.getWallBlock(),
                                    xx,
                                    yy,
                                    zz,
                                    chunkBoundary
                                )
                            }
                        }
                    } else {
                        //Room internals
                        if ((xx > 1 && xx < maxX - 1) && (zz > 0 && zz < maxZ - 1) || (yy > 1 && yy < this.sizeY - 1) || this.type!!.doEntryWallsToo) {
                            val blockpos: BlockPos = BlockPos(
                                this.getXWithOffset(xx, zz),
                                this.getYWithOffset(yy),
                                this.getZWithOffset(xx, zz)
                            )
                            if (chunkBoundary.isInside(blockpos)) {
                                this.buildRoomContents(worldIn, xx, yy, zz, maxX - 1, maxZ - 1, blockpos, randomInt)
                            }
                        } else if ((this.configuration)!!.getDeckType() == BaseDeck.EnumBaseType.TUNNELER && (yy == 1 || yy == this.sizeY - 1)) {
                            var meta: Int = 1
                            if (xx == 1) meta = 3
                            else if (xx == maxX - 1) meta = 2
                            else if (zz == 0) meta = 0
                            if (this.direction == EnumFacing.NORTH) {
                                meta = meta xor (3 xor meta / 2)
                            } else if (this.direction == EnumFacing.SOUTH) {
                                meta = meta xor 2 + meta / 2
                            } else if (this.direction == EnumFacing.EAST) meta = meta xor 1
                            meta += if (yy == 1) 0 else 4
                            this.setBlockState(worldIn, blockStair.getStateFromMeta(meta), xx, yy, zz, chunkBoundary)
                        } else if (yy == 1 && this.type!!.blockFloor != null) {
                            val xEntrance: Boolean =
                                if (maxX > 6) (xx > 2 && xx < maxX - 2) else (xx > 1 && xx < maxX - 1)
                            if (zz == 0 && (this.type!!.blockEntrance != null && xEntrance)) {
                                this.setBlockState(worldIn, blockAir, xx, yy, zz, chunkBoundary)
                            } else {
                                this.setBlockState(worldIn, this.type!!.blockFloor, xx, yy, zz, chunkBoundary)
                            }
                        } else {
                            this.setBlockState(worldIn, blockAir, xx, yy, zz, chunkBoundary)
                        }
                    }
                }
            }
        }

        return true
    }

    /**
     * Room contents boundaries are:
     * x from 1 to maxX
     * y from 1 to this.sizeY - 1
     * z from 1 to maxZ
     */
    private fun buildRoomContents(
        worldIn: World,
        x: Int,
        y: Int,
        z: Int,
        maxX: Int,
        maxZ: Int,
        blockpos: BlockPos,
        randomInt: Int
    ) {
        var state: IBlockState = Blocks.AIR.defaultBlockState()
        val semirand: Int = ((blockpos.getY() * 379 + blockpos.getX()) * 373 + blockpos.getZ()) * 7 and 15

        var facing: Int = 0
        var facing1: Int = 0
        var facing2: Int = 0
        var facingLamp: Int = 2
        var facingScreen: Int = 2
        when (this.direction) {
            EnumFacing.WEST -> {
                facing1 = 3
                facing2 = 2
                facingLamp = 3
                facingScreen = 5
            }

            EnumFacing.EAST -> {
                facing = 2
                facing1 = 1
                facing2 = 0
                facingLamp = 2
                facingScreen = 4
            }

            EnumFacing.NORTH -> {
                facing = 3
                facing1 = 2
                facing2 = 1
                facingLamp = 5
                facingScreen = 2
            }

            EnumFacing.SOUTH -> {
                facing = 1
                facing1 = 0
                facing2 = 3
                facingLamp = 4
                facingScreen = 3
            }
        
else -> {}
}

        //Offset from centre - used for some rooms
        val ox: Int = maxX / 2 - 3
        val xx: Int = x - ox

        do {
when (this.type) {
            EnumRoomType.EMPTY ->             //Pillars in corners
                if ((z == 1 || z == maxZ - 1) && (x == 2 || x == maxX - 1)) {
                    state = (GCBlocks.wallGC)!!.getStateFromMeta(2)
                } else if (y == 1) {
                    //Some random Netherwart
                    if (semirand < 2) {
                        state = (AsteroidBlocks.spaceWart)!!.getStateFromMeta(semirand)
                    }
                }

            EnumRoomType.STORE -> if (maxX >= 6 && y == 1 && maxZ >= 5) {
                //An actual landing pad (indoors!)
                if (xx >= 3 && xx <= 5 && z >= 2 && z <= 4) {
                    state = (GCBlocks.landingPad)!!.defaultBlockState()
                } else if (xx == 2) {
                    //Cargo loaders and unloaders, these will contain treasure items but only accessible when powered
                    when (z) {
                        2 -> state = (GCBlocks.cargoLoader)!!.getStateFromMeta(facing)
                        3 -> state = (GCBlocks.aluminumWire)!!.getStateFromMeta(1)
                        4 -> state = (GCBlocks.cargoLoader)!!.getStateFromMeta(4 + (facing xor 2))
                        else -> {}
                    }
                }
            }

            EnumRoomType.POWER -> {
                facing = (facing + 1) % 4
                when (y) {
                    1 ->                 //Layer 1: wall blocks with sealable alu wire in the centre
                        if (z == 3 && xx >= 4 && x <= maxX - 2) state = (GCBlocks.sealableBlock)!!.getStateFromMeta(14)
                        else if (xx >= 3 && z > 1 && (z < maxZ - 1 || z == 4)) state = ((this.configuration)!!.getWallBlock())!!

                    2 ->                 //Layer 2: tier 1 storage and alu wire and a switch
                        if (z == 2) {
                            if (xx == 3) state = (GCBlocks.machineTiered)!!.getStateFromMeta(0 + facing)
                            else if (xx > 3 && x < maxX - 2) state = (GCBlocks.aluminumWire)!!.getStateFromMeta(0)
                            else if (x == maxX - 2) state = (GCBlocks.aluminumWire)!!.getStateFromMeta(2)
                            else if ((xx == 3 || x == maxX - 1)) state = (AsteroidBlocks.blockMinerBase)!!.defaultBlockState()
                        } else if (z == 3 && x == maxX - 2) state = (GCBlocks.aluminumWire)!!.getStateFromMeta(0)
                        else if (z == 4 && (xx == 3 || x == maxX - 1)) state =
                            (AsteroidBlocks.blockMinerBase)!!.defaultBlockState()

                    3 ->                 //Layer 3: tier 2 storage and alu wire
                        if (z == 2 && xx == 4) state = (GCBlocks.aluminumWire)!!.getStateFromMeta(0)
                        else if (z == 3 && x == maxX - 2) state =
                            (GCBlocks.machineTiered)!!.getStateFromMeta(8 + (facing xor 2))
                        else if (z == 3 && x == maxX - 1) state = (GCBlocks.aluminumWire)!!.getStateFromMeta(1)
                        else if (z == 3 && xx >= 4 && x < maxX - 2) state = (GCBlocks.aluminumWire)!!.getStateFromMeta(0)
                        else if ((z == 2 || z == 4) && (xx == 3 || x == maxX - 1)) state =
                            (AsteroidBlocks.blockMinerBase)!!.defaultBlockState()

                    4 ->                 //Layer 4: tier 2 storage and alu wire
                        if (z == 3) {
                            if (x == maxX - 3) state = (GCBlocks.machineTiered)!!.getStateFromMeta(8 + facing)
                            else if (x == maxX - 1 || x == maxX - 2) state = (GCBlocks.aluminumWire)!!.getStateFromMeta(1)
                        } else if ((z == 2 || z == 4) && xx >= 3 && x < maxX) state =
                            (AsteroidBlocks.blockMinerBase)!!.defaultBlockState()
                }
            }

            EnumRoomType.ENGINEERING -> if (y == 1) {
                val closerz: Int = if (maxZ <= 5) 2 else 3
                if ((z == maxZ - 2 && x == maxX / 2 + 1 || (z == closerz && (x == 2 || x == maxX - 1) && maxX > 5)) && maxZ > 3) {
                    state = (GCBlocks.nasaWorkbench)!!.defaultBlockState()
                } else {
                    state = (AsteroidBlocks.blockBasic)!!.getStateFromMeta(6)
                }
            }

            EnumRoomType.MEDICAL -> {
                var zTable: Int = maxZ - 3
                var zTank: Int = maxZ - 1
                var zLight: Int = zTable
                if (zTable <= 1) {
                    if (zTable < 0) {
                        //Too small to build the room
                        break
                    }


                    //Small medical room: bring everything closer together
                    zTable++
                    zLight++
                    zTank = maxZ
                }
                if (y == 1) {
                    //Operating table at y == 1
                    if (z == zTable && x <= maxX - 1 && x >= maxX - 3) {
                        state = (GCBlocks.crafting)!!.getStateFromMeta(1)
                    } else  //Tiled floor
                        state = Blocks.IRON_TRAPDOOR.defaultBlockState()
                } else if (y == 2 || y == 3) {
                    //Operating table at y == 2
                    if (y == 2 && z == zTable && x <= maxX - 1 && x >= maxX - 3) {
                        state = (GCBlocks.landingPad)!!.defaultBlockState()
                    } else if (z == zTank) {
                        //Fluid tanks, 2 tanks high - these will be filled with Bacterial Sludge below
                        if ((maxX - x) % 2 == 1) {
                            state = (GCBlocks.fluidTank)!!.defaultBlockState()
                        }
                    }
                } else if ((y == 4 || y == 5) && (z == zLight || z == zLight - 1)) {
                    //Lighting
                    if (x == maxX) state =
                        (GCBlocks.concealedDetector)!!.getStateFromMeta(8 + facing + (if ((this.configuration)!!.getDeckType() == BaseDeck.EnumBaseType.HUMANOID) 0 else 4))
                    else if (x == maxX - 1) state = (GCBlocks.brightLamp)!!.getStateFromMeta(facingLamp)
                }
            }

            EnumRoomType.CREW -> if (y == 1) {
                if (x == 2 || x == maxX - 1) {
                    if ((z % 2) == 1) {
                        state = (GCBlocks.wallGC)!!.getStateFromMeta(if (z == 1) 3 else 2)
                    }
                } else {
                    state = LegacyVanillaBlockStates.state(LegacyVanillaBlockIds.CARPET, 7)
                }
            } else if (y == 2) {
                if (x == 2 || x == maxX - 1) {
                    if (z == 1) {
                        state = Blocks.BREWING_STAND.defaultBlockState()
                    } else if (z > 2 && z < maxZ) {
                        state = (GCBlocks.landingPad)!!.getStateFromMeta(1)
                    }
                }
            }

            EnumRoomType.CONTROL -> if (y == 1) {
                if (x == maxX / 2 + 1 && z == maxZ - 2) {
                    state = (AsteroidBlocks.blockBasic)!!.getStateFromMeta(6)
                } else {
                    state = (GCBlocks.slabGCHalf)!!.getStateFromMeta(6)
                }
            } else if (y <= 3) {
                if ((x == 1 || x == maxX) && !(z == 0 || z == maxZ)) {
                    state = (GCBlocks.telemetry)!!.defaultBlockState()
                } else if ((x == 2 || x == maxX - 1) && !(z == 0 || z == maxZ)) {
                    state = (GCBlocks.screen)!!.getStateFromMeta((if (x == 2) facingLamp else facingLamp xor 1))
                } else if (z == maxZ && x > 2 && x < maxX - 1) {
                    state = (GCBlocks.screen)!!.getStateFromMeta(facingScreen)
                } else if (x == maxX / 2 + 1 && z == maxZ - 2 && y == 2) {
                    state = (GCBlocks.landingPad)!!.getStateFromMeta(1)
                }
            }

            EnumRoomType.CRYO -> {
                val xEntrance: Boolean = if (maxX > 5) (x > 2 && x < maxX - 1) else (x > 1 && x < maxX)
                val highEntrance: Boolean =
                    (this.configuration)!!.isHangarDeck() && (this.configuration)!!.getDeckType() == BaseDeck.EnumBaseType.AVIAN
                if (y == 1) {
                    //Build a dark plinth for it all at y == 1
                    if (z == 1 && x > 1 && x < maxX) {
                        state = (GCBlocks.slabGCHalf)!!.getStateFromMeta(6)
                    } else if (z != 0 || !xEntrance || highEntrance) {
                        state = (AsteroidBlocks.blockBasic)!!.getStateFromMeta(6)
                    }
                } else if (z == 0 && (y > (if (highEntrance) 5 else 3) || !xEntrance)) {
                    //Dark ceiling and entrance wall
                    state = (AsteroidBlocks.blockBasic)!!.getStateFromMeta(6)
                } else if (y <= 4 && (z == maxZ || x == 1 || x == maxX)) {
                    //Around the walls: Cryo Chambers alternated with dark blocks
                    if (z == maxZ && x % 2 == 0 && x < maxX) {
                        if (y == 2) {
                            state =
                                (MarsBlocks.machine)!!.getStateFromMeta(BlockMachineMars.Companion.CRYOGENIC_CHAMBER_METADATA + facing1)
                        }
                    } else if (z < maxZ && z > 1 && (maxZ - z) % 2 == 0) {
                        if (y == 2) {
                            state =
                                (MarsBlocks.machine)!!.getStateFromMeta(BlockMachineMars.Companion.CRYOGENIC_CHAMBER_METADATA + (if (x == 1) facing else facing2))
                        }
                    } else {
                        state = (AsteroidBlocks.blockBasic)!!.getStateFromMeta(6)
                    }
                } else if (y > 5 || y == (this.configuration)!!.getRoomHeight() || (z == maxZ || x == 1 || x == maxX)) {
                    //Dark top section of walls and ceiling
                    state = (AsteroidBlocks.blockBasic)!!.getStateFromMeta(6)
                }
            }

            else -> {}
        }
} while (false)

        worldIn.setBlock(blockpos, state, 2)
        if (state.getBlock() is ITileEntityProvider) {
            val tile: TileEntity? = worldIn.getBlockEntity(blockpos)

            if (tile is IMultiBlock) {
                val positions: MutableList<BlockPos> = LinkedList<BlockPos>()
                (tile as IMultiBlock).getPositions(blockpos, (positions as MutableList<BlockPos?>?))
                for (pos: BlockPos in positions) {
                    worldIn.setBlock(
                        pos,
                        (GCBlocks.fakeBlock)!!.defaultBlockState()
                            .setValue(
                                BlockMulti.MULTI_TYPE,
                                ((tile as IMultiBlock).getMultiType())!!
                            ),
                        2
                    )
                }
            }

            if (tile is TileEntityFluidTank) {
                tile.fill(null, FluidStack(MarsModule.Companion.sludge, 16000), true)
            } else if (tile is TileEntityCargoLoader) {
                val loader: TileEntityCargoLoader = tile
                loader.locked = true
                //Looks like the food supplies have gone off!
                loader.addCargo(ItemStack(Items.POISONOUS_POTATO, 64, 0), true)
                loader.addCargo(ItemStack(Items.POISONOUS_POTATO, 64, 0), true)
                loader.addCargo(ItemStack(Items.POISONOUS_POTATO, 64, 0), true)
                loader.addCargo(ItemStack(Items.POISONOUS_POTATO, 64, 0), true)
                loader.addCargo(ItemStack(Items.ROTTEN_FLESH, 64, 0), true)
                loader.addCargo(ItemStack(GCItems.flagPole, semirand % 31 + 2, 0), true)
                loader.addCargo(FoundationItemVariants.stack(requireNotNull(MarsItems.marsItemBasic), semirand % 2 + 1, 4), true) //Slimeling Inventory Bag
                loader.addCargo(FoundationItemVariants.stack(requireNotNull(AsteroidsItems.basicItem), semirand % 23 + 41, 7), true) //Thermal cloth
                loader.addCargo(ItemStack(GCItems.oilCanister, 1, ItemCanisterGeneric.EMPTY), true)
                loader.addCargo(ItemStack(GCItems.oilCanister, 1, ItemCanisterGeneric.EMPTY), true)
                loader.addCargo(ItemStack(GCItems.oilCanister, 1, ItemCanisterGeneric.EMPTY), true)
                loader.addCargo(ItemStack(GCItems.oilCanister, 1, ItemCanisterGeneric.EMPTY), true)
                loader.addCargo(ItemStack(GCItems.oilCanister, 1, ItemCanisterGeneric.EMPTY), true)
                loader.addCargo(((this.configuration)!!.getDeckType())!!.treasure.copy(), true)
            } else if (tile is TileEntityCrafting) {
                when (semirand % 4) {
                    0 -> {}
                    1 -> {
                        tile.craftMatrix.setInventorySlotContents(1, ItemStack(Items.IRON_INGOT))
                        tile.craftMatrix.setInventorySlotContents(3, ItemStack(Items.IRON_INGOT))
                    }

                    2 -> {
                        //Creeper or Zombie head
                        val slot: Int = semirand % 9
                        tile.craftMatrix.setInventorySlotContents(
                            slot,
                            ItemStack(Items.SKULL, 1, if (semirand % 13 < 6) 4 else 2)
                        )
                    }

                    3 -> {
                        tile.craftMatrix.setInventorySlotContents(0, ItemStack(Items.IRON_INGOT))
                        tile.craftMatrix.setInventorySlotContents(1, ItemStack(Items.IRON_INGOT))
                        tile.craftMatrix.setInventorySlotContents(3, ItemStack(Items.IRON_INGOT))
                        tile.craftMatrix.setInventorySlotContents(4, ItemStack(Items.STICK))
                        tile.craftMatrix.setInventorySlotContents(7, ItemStack(Items.STICK))
                    }
                }
            } else if (tile is TileEntityBrewingStand) {
                val stand: TileEntityBrewingStand = tile
                stand.setInventorySlotContents(
                    0,
                    PotionUtils.addPotionToItemStack(ItemStack(Items.POTIONITEM), PotionTypes.POISON)
                )
                stand.setInventorySlotContents(
                    1,
                    PotionUtils.addPotionToItemStack(ItemStack(Items.POTIONITEM), PotionTypes.WEAKNESS)
                )
                stand.setInventorySlotContents(
                    2,
                    PotionUtils.addPotionToItemStack(ItemStack(Items.POTIONITEM), PotionTypes.HARMING)
                )
            } else if (tile is TileEntityEnergyStorageModule) {
                val store: TileEntityEnergyStorageModule = tile
                if (semirand % 3 == 1) {
                    store.setItem(1, ItemStack(GCItems.battery, 1, 100))
                }
            }
        }

        if (this.type == EnumRoomType.ENGINEERING && y == (if ((this.configuration)!!.getRoomHeight() <= 4) 2 else 3) && z == maxZ && (x == 3 || x == 6)) {
            var hangingDirection: EnumFacing? = this.direction
            if (hangingDirection == EnumFacing.WEST || hangingDirection == EnumFacing.EAST) {
                //Apparently we have our North and our South reversed ?  So we don't want the opposite for North and South!
                hangingDirection = hangingDirection.getOpposite()
            }
            val entityhanging: EntityHangingSchematic =
                EntityHangingSchematic(worldIn, blockpos, (hangingDirection)!!, x / 3 - 1)
            if (entityhanging != null) {
                worldIn.addFreshEntity(entityhanging)
            }
        }
    }


    enum class EnumRoomType
        (floorBlock: IBlockState?, entrance: IBlockState?, doWalls: Boolean) {
        ENGINEERING(
            (AsteroidBlocks.blockBasic)!!.getStateFromMeta(6),
            (AsteroidBlocks.blockBasic)!!.getStateFromMeta(6),
            false
        ),
        POWER(null, null, false),
        STORE(null, null, false),
        EMPTY(null, null, false),
        MEDICAL(Blocks.IRON_TRAPDOOR.defaultBlockState(), LegacyVanillaBlockStates.state(LegacyVanillaBlockIds.IRON_TRAPDOOR, 8), false),
        CREW(null, null, false),
        CRYO((AsteroidBlocks.blockBasic)!!.getStateFromMeta(6), (AsteroidBlocks.blockBasic)!!.getStateFromMeta(6), true),
        CONTROL((GCBlocks.slabGCHalf)!!.getStateFromMeta(6), (AsteroidBlocks.blockBasic)!!.getStateFromMeta(6), false);

        @JvmField
        val blockFloor: IBlockState?

        @JvmField
        val blockEntrance: IBlockState?

        @JvmField
        val doEntryWallsToo: Boolean

        init {
            this.blockFloor = floorBlock
            this.blockEntrance = entrance
            this.doEntryWallsToo = doWalls
        }
    }
}
