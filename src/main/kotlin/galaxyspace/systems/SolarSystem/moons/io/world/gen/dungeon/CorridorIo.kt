package galaxyspace.systems.SolarSystem.moons.io.world.gen.dungeon

import galaxyspace.systems.SolarSystem.moons.io.world.gen.dungeon.CorridorIo
import galaxyspace.systems.SolarSystem.moons.io.world.gen.dungeon.RoomEmptyIo
import galaxyspace.systems.SolarSystem.moons.io.world.gen.dungeon.RoomSpawnerIo
import galaxyspace.systems.SolarSystem.moons.io.world.gen.dungeon.RoomChestIo
import galaxyspace.systems.SolarSystem.moons.io.world.gen.dungeon.RoomBossIo
import kotlin.math.pow
import galaxyspace.systems.SolarSystem.moons.io.world.gen.dungeon.DungeonStartIo
import galaxyspace.systems.SolarSystem.moons.io.world.gen.dungeon.SizedPieceIo
import galaxyspace.systems.SolarSystem.moons.io.world.gen.dungeon.PieceIo
import asmodeuscore.core.astronomy.dimension.world.gen.dungeons.standart.DungeonConfiguration
import net.minecraft.init.Blocks
import net.minecraft.util.EnumFacing
import net.minecraft.world.World
import net.minecraft.world.gen.structure.StructureBoundingBox
import java.lang.Exception
import java.lang.reflect.Constructor
import java.util.Random
import kotlin.math.abs
import kotlin.math.max

open class CorridorIo : SizedPieceIo {
    private var corridorConfiguration: DungeonConfiguration? = null

    constructor()

    constructor(
        corridorConfiguration: DungeonConfiguration,
        rand: Random?,
        blockPosX: Int,
        blockPosZ: Int,
        sizeX: Int,
        sizeY: Int,
        sizeZ: Int,
        direction: EnumFacing?
    ) : super(corridorConfiguration, sizeX, sizeY, sizeZ, direction) {
        this.corridorConfiguration = corridorConfiguration

        this.setCoordBaseMode(EnumFacing.SOUTH)
        this.boundingBox = StructureBoundingBox(
            blockPosX,
            corridorConfiguration.getYPosition(),
            blockPosZ,
            blockPosX + sizeX,
            corridorConfiguration.getYPosition() + sizeY,
            blockPosZ + sizeZ
        )
    }

    override fun addComponentParts(
        worldIn: World,
        randomIn: Random,
        structureBoundingBoxIn: StructureBoundingBox
    ): Boolean {
        if (this.corridorConfiguration == null) return false

        for (i: Int in 0..<this.boundingBox.getXSize()) {
            for (j: Int in 0..<this.boundingBox.getYSize()) {
                for (k: Int in 0..<this.boundingBox.getZSize()) {
                    //GEN ROOM
                    if (((this.getDirection())!!
                            .getAxis() == EnumFacing.Axis.Z && ((i == 0 || i == 1) || (i == this.boundingBox.getXSize() - 1 || i == this.boundingBox.getXSize() - 2))) || j == 0 || j == this.boundingBox.getYSize() - 1 ||
                        ((this.getDirection())!!
                            .getAxis() == EnumFacing.Axis.X && ((k == 0 || k == 1) || (k == this.boundingBox.getZSize() - 1 || k == this.boundingBox.getZSize() - 2)))
                    ) {
                        if (j == this.corridorConfiguration!!.getHallwayHeight()) this.setBlockState(
                            worldIn,
                            this.corridorConfiguration!!.getOtherBlock(false),
                            i,
                            j,
                            k,
                            boundingBox
                        )
                        else if (j == 0) this.setBlockState(
                            worldIn,
                            this.corridorConfiguration!!.getOtherBlock(true),
                            i,
                            j,
                            k,
                            boundingBox
                        )
                        else this.setBlockState(worldIn, this.corridorConfiguration!!.getBrickBlock(), i, j, k, boundingBox)
                    } else {
                        this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), i, j, k, this.boundingBox)
                    }
                    /*
                	if(this.getDirection().getAxis() == EnumFacing.Axis.Z && (k + 1) % 8 == 0 && k != this.boundingBox.getZSize() - 1)
                    {
                		if (i == 3 || i == this.boundingBox.getXSize() - 1 - 3)
                        {
                			this.setBlockState(worldIn, this.corridorConfiguration.getBrickBlock(), i, j, k, this.boundingBox);
                        }
                    }   
                	
                	if (this.getDirection().getAxis() == EnumFacing.Axis.X && (i + 1) % 8 == 0 && i != this.boundingBox.getXSize() - 1)
                    {
                		if (i == 3 || i == this.boundingBox.getXSize() - 1 - 3)
                        {
                			this.setBlockState(worldIn, this.corridorConfiguration.getBrickBlock(), i, j, k, this.boundingBox);
                        }
                    }
                	*/
                    //DECORATE
                    if ((this.getDirection())!!
                            .getAxis() == EnumFacing.Axis.Z && (k + 1) % 8 == 0 && k != this.boundingBox.getZSize() - 1
                    ) {
                        if (j == 2) {
                            if (i == 0 || i == this.boundingBox.getXSize() - 1) {
                                this.setBlockState(worldIn, Blocks.LAVA.getDefaultState(), i, j, k, this.boundingBox)
                            } else if (i == 1 || i == this.boundingBox.getXSize() - 2) {
                                this.setBlockState(
                                    worldIn,
                                    Blocks.IRON_BARS.getDefaultState(),
                                    i,
                                    j,
                                    k,
                                    this.boundingBox
                                )
                            }
                        }

                        if (this.boundingBox.getXSize() >= 10 && (i == 3 || i == this.boundingBox.getXSize() - 1 - 3)) {
                            this.setBlockState(worldIn, this.corridorConfiguration!!.getBrickBlock(), i, j, k, this.boundingBox)
                        }
                    } else if ((this.getDirection())!!
                            .getAxis() == EnumFacing.Axis.X && (i + 1) % 8 == 0 && i != this.boundingBox.getXSize() - 1
                    ) {
                        if (j == 2) {
                            if (k == 0 || k == this.boundingBox.getZSize() - 1) {
                                this.setBlockState(worldIn, Blocks.LAVA.getDefaultState(), i, j, k, this.boundingBox)
                            } else if (k == 1 || k == this.boundingBox.getZSize() - 2) {
                                this.setBlockState(
                                    worldIn,
                                    Blocks.IRON_BARS.getDefaultState(),
                                    i,
                                    j,
                                    k,
                                    this.boundingBox
                                )
                            }
                        }

                        if (this.boundingBox.getZSize() >= 10 && (k == 3 || k == this.boundingBox.getZSize() - 1 - 3)) {
                            this.setBlockState(worldIn, this.corridorConfiguration!!.getBrickBlock(), i, j, k, this.boundingBox)
                        }
                    }
                }
            }
        }

        return true
    }

    private fun <T : SizedPieceIo?> getRoom(clazz: Class<*>, startPiece: DungeonStartIo, rand: Random?): T? {
        try {
            val c0: Constructor<*> = clazz.getConstructor(
                DungeonConfiguration::class.java,
                Random::class.java,
                Integer.TYPE,
                Integer.TYPE,
                EnumFacing::class.java
            )
            val dummy: T? = c0.newInstance(this.corridorConfiguration, rand, 0, 0, (this.getDirection())!!.getOpposite()) as T?
            val extension: StructureBoundingBox = getExtension(
                (this.getDirection())!!,
                if ((getDirection())!!.getAxis() == EnumFacing.Axis.X) dummy!!.getSizeX() else dummy!!.getSizeZ(),
                if ((getDirection())!!.getAxis() == EnumFacing.Axis.X) dummy.getSizeZ() else dummy.getSizeX()
            )
            if (startPiece.checkIntersection(extension)) {
                return null
            }
            val sizeX: Int = extension.maxX - extension.minX
            val sizeZ: Int = extension.maxZ - extension.minZ
            val sizeY: Int = dummy.getSizeY()
            val blockX: Int = extension.minX
            val blockZ: Int = extension.minZ
            val c1: Constructor<*> = clazz.getConstructor(
                DungeonConfiguration::class.java,
                Random::class.java,
                Integer.TYPE,
                Integer.TYPE,
                Integer.TYPE,
                Integer.TYPE,
                Integer.TYPE,
                EnumFacing::class.java
            )
            return c1.newInstance(
                this.corridorConfiguration,
                rand,
                blockX,
                blockZ,
                sizeX,
                sizeY,
                sizeZ,
                (this.getDirection())!!.getOpposite()
            ) as T?
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    override fun getNextPiece(startPiece: DungeonStartIo?, rand: Random?): PieceIo? {
        if ((startPiece)!!.attachedComponents.size > 2 && (startPiece)!!.attachedComponents.get((startPiece)!!.attachedComponents.size - 2) is RoomBossIo) {
            try {
                return getRoom<SizedPieceIo?>((this.corridorConfiguration!!.getTreasureRoom())!!, (startPiece)!!, rand)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            val bossRoomChance: Int = max((1.0 / ((startPiece)!!.attachedComponents.size / 55.0).pow(2.0)).toInt(), 5)
            val bossRoom: Boolean = (rand)!!.nextInt(bossRoomChance) == 0
            if (bossRoom) {
                try {
                    return getRoom<SizedPieceIo?>((this.corridorConfiguration!!.getBossRoom())!!, (startPiece)!!, rand)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                val extension: StructureBoundingBox =
                    getExtension((this.getDirection())!!, (rand)!!.nextInt(4) + 10, (rand)!!.nextInt(4) + 10)

                if ((startPiece)!!.checkIntersection(extension)) {
                    return null
                }

                val sizeX: Int = extension.maxX - extension.minX
                val sizeZ: Int = extension.maxZ - extension.minZ
                val sizeY: Int = corridorConfiguration!!.getRoomHeight()
                val blockX: Int = extension.minX
                val blockZ: Int = extension.minZ

                if (abs((startPiece)!!.getBoundingBox().maxZ - boundingBox.minZ) > 200) {
                    return null
                }

                if (abs((startPiece)!!.getBoundingBox().maxX - boundingBox.minX) > 200) {
                    return null
                }

                val lastPiece: PieceIo? =
                    if ((startPiece)!!.attachedComponents.size <= 2) null else (startPiece)!!.attachedComponents.get((startPiece)!!.attachedComponents.size - 2) as PieceIo?

                if (lastPiece !is RoomSpawnerIo) {
                    return RoomSpawnerIo(
                        this.corridorConfiguration,
                        rand,
                        blockX,
                        blockZ,
                        sizeX,
                        sizeY,
                        sizeZ,
                        (this.getDirection())!!.getOpposite()
                    )
                } else {
                    if ((rand)!!.nextInt(2) == 0) {
                        return RoomEmptyIo(
                            (this.corridorConfiguration)!!,
                            rand,
                            blockX,
                            blockZ,
                            sizeX,
                            sizeY,
                            sizeZ,
                            (this.getDirection())!!.getOpposite()
                        )
                    } else {
                        return RoomChestIo(
                            this.corridorConfiguration,
                            rand,
                            blockX,
                            blockZ,
                            sizeX,
                            sizeY,
                            sizeZ,
                            (this.getDirection())!!.getOpposite()
                        )
                    }
                }
            }
        }

        return null
    }
}
