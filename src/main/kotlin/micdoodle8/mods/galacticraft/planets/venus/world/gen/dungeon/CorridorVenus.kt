package micdoodle8.mods.galacticraft.planets.venus.world.gen.dungeon

import java.util.Random

import net.minecraft.init.Blocks
import net.minecraft.util.EnumFacing
import net.minecraft.world.World
import net.minecraft.world.gen.structure.StructureBoundingBox
import java.lang.reflect.Constructor
import java.util.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.pow

open class CorridorVenus : SizedPieceVenus {
    constructor()

    constructor(
        configuration: DungeonConfigurationVenus,
        rand: Random?,
        blockPosX: Int,
        blockPosZ: Int,
        sizeX: Int,
        sizeY: Int,
        sizeZ: Int,
        direction: EnumFacing?
    ) : super(configuration, sizeX, sizeY, sizeZ, direction) {
        this.setCoordBaseMode(EnumFacing.SOUTH)
        this.boundingBox = StructureBoundingBox(
            blockPosX,
            configuration.getYPosition(),
            blockPosZ,
            blockPosX + sizeX,
            configuration.getYPosition() + sizeY,
            blockPosZ + sizeZ
        )
    }

    override fun addComponentParts(
        worldIn: World,
        randomIn: Random,
        structureBoundingBoxIn: StructureBoundingBox
    ): Boolean {
        for (i: Int in 0..<this.boundingBox.getXSize()) {
            for (j: Int in 0..<this.boundingBox.getYSize()) {
                for (k: Int in 0..<this.boundingBox.getZSize()) {
                    if (j == 2 && (this.getDirection())!!
                            .getAxis() == EnumFacing.Axis.Z && (k + 1) % 4 == 0 && k != this.boundingBox.getZSize() - 1
                    ) {
                        if (i == 0 || i == this.boundingBox.getXSize() - 1) {
                            this.setBlockState(worldIn, Blocks.LAVA.getDefaultState(), i, j, k, this.boundingBox)
                        } else if (i == 1 || i == this.boundingBox.getXSize() - 2) {
                            this.setBlockState(worldIn, Blocks.IRON_BARS.getDefaultState(), i, j, k, this.boundingBox)
                        } else {
                            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), i, j, k, this.boundingBox)
                        }
                    } else if (j == 2 && (this.getDirection())!!
                            .getAxis() == EnumFacing.Axis.X && (i + 1) % 4 == 0 && i != this.boundingBox.getXSize() - 1
                    ) {
                        if (k == 0 || k == this.boundingBox.getZSize() - 1) {
                            this.setBlockState(worldIn, Blocks.LAVA.getDefaultState(), i, j, k, this.boundingBox)
                        } else if (k == 1 || k == this.boundingBox.getZSize() - 2) {
                            this.setBlockState(worldIn, Blocks.IRON_BARS.getDefaultState(), i, j, k, this.boundingBox)
                        } else {
                            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), i, j, k, this.boundingBox)
                        }
                    } else if (((this.getDirection())!!
                            .getAxis() == EnumFacing.Axis.Z && (i == 1 || i == this.boundingBox.getXSize() - 2)) || j == 0 || j == this.boundingBox.getYSize() - 1 ||
                        ((this.getDirection())!!
                            .getAxis() == EnumFacing.Axis.X && (k == 1 || k == this.boundingBox.getZSize() - 2))
                    ) {
                        val venusConfig: DungeonConfigurationVenus = this.configuration as DungeonConfigurationVenus
                        this.setBlockState(
                            worldIn,
                            if (j == 0 || j == this.boundingBox.getYSize() - 1) venusConfig.getBrickBlockFloor() else ((this.configuration)!!.getBrickBlock())!!,
                            i,
                            j,
                            k,
                            this.boundingBox
                        )
                    } else if (((this.getDirection())!!
                            .getAxis() == EnumFacing.Axis.Z && (i == 0 || i == this.boundingBox.getXSize() - 1)) ||
                        ((this.getDirection())!!
                            .getAxis() == EnumFacing.Axis.X && (k == 0 || k == this.boundingBox.getZSize() - 1))
                    ) {
                        val venusConfig: DungeonConfigurationVenus = this.configuration as DungeonConfigurationVenus
                        this.setBlockState(
                            worldIn,
                            if (j == 0 || j == this.boundingBox.getYSize() - 1) venusConfig.getBrickBlockFloor() else ((this.configuration)!!.getBrickBlock())!!,
                            i,
                            j,
                            k,
                            this.boundingBox
                        )
                    } else {
                        this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), i, j, k, this.boundingBox)
                    }
                }
            }
        }

        return true
    }

    private fun <T : SizedPieceVenus?> getRoom(clazz: Class<*>, startPiece: DungeonStartVenus, rand: Random?): T? {
        try {
            val c0: Constructor<*> = clazz.getConstructor(
                DungeonConfigurationVenus::class.java,
                Random::class.java,
                Integer.TYPE,
                Integer.TYPE,
                EnumFacing::class.java
            )
            val dummy: T? = c0.newInstance(this.configuration, rand, 0, 0, (this.getDirection())!!.getOpposite()) as T?
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
                DungeonConfigurationVenus::class.java,
                Random::class.java,
                Integer.TYPE,
                Integer.TYPE,
                Integer.TYPE,
                Integer.TYPE,
                Integer.TYPE,
                EnumFacing::class.java
            )
            return c1.newInstance(
                this.configuration,
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

    override fun getNextPiece(startPiece: DungeonStartVenus?, rand: Random?): PieceVenus? {
        if ((startPiece)!!.attachedComponents.size > 2 && (startPiece)!!.attachedComponents.get((startPiece)!!.attachedComponents.size - 2) is RoomBossVenus) {
            try {
                return getRoom<SizedPieceVenus?>(((this.configuration)!!.getTreasureRoom())!!, (startPiece)!!, rand)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            val bossRoomChance: Int = max((1.0 / ((startPiece)!!.attachedComponents.size / 55.0).pow(2.0)).toInt(), 5)
            val bossRoom: Boolean = (rand)!!.nextInt(bossRoomChance) == 0
            if (bossRoom) {
                try {
                    return getRoom<SizedPieceVenus?>(((this.configuration)!!.getBossRoom())!!, (startPiece)!!, rand)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                val extension: StructureBoundingBox =
                    getExtension((this.getDirection())!!, (rand)!!.nextInt(4) + 6, (rand)!!.nextInt(4) + 6)

                if ((startPiece)!!.checkIntersection(extension)) {
                    return null
                }

                val sizeX: Int = extension.maxX - extension.minX
                val sizeZ: Int = extension.maxZ - extension.minZ
                val sizeY: Int = (configuration)!!.getRoomHeight()
                val blockX: Int = extension.minX
                val blockZ: Int = extension.minZ

                if (abs((startPiece)!!.getBoundingBox().maxZ - boundingBox.minZ) > 200) {
                    return null
                }

                if (abs((startPiece)!!.getBoundingBox().maxX - boundingBox.minX) > 200) {
                    return null
                }

                val lastPiece: PieceVenus? =
                    if ((startPiece)!!.attachedComponents.size <= 2) null else (startPiece)!!.attachedComponents.get((startPiece)!!.attachedComponents.size - 2) as PieceVenus?

                if (lastPiece !is RoomSpawnerVenus) {
                    return RoomSpawnerVenus(
                        this.configuration,
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
                        return RoomEmptyVenus(
                            (this.configuration)!!,
                            rand,
                            blockX,
                            blockZ,
                            sizeX,
                            sizeY,
                            sizeZ,
                            (this.getDirection())!!.getOpposite()
                        )
                    } else {
                        return RoomChestVenus(
                            this.configuration,
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
