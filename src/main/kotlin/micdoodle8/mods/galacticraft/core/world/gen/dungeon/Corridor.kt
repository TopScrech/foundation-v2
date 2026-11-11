package micdoodle8.mods.galacticraft.core.world.gen.dungeon

import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.blocks.BlockUnlitTorch
import net.minecraft.world.level.block.Blocks
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.levelgen.structure.BoundingBox
import java.lang.Exception
import java.lang.reflect.Constructor
import java.util.Random
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.pow

open class Corridor : SizedPiece {
    constructor()

    constructor(
        configuration: DungeonConfiguration,
        rand: Random?,
        blockPosX: Int,
        blockPosZ: Int,
        sizeX: Int,
        sizeY: Int,
        sizeZ: Int,
        direction: EnumFacing?
    ) : super(configuration, sizeX, sizeY, sizeZ, direction) {
        this.setCoordBaseMode(EnumFacing.SOUTH)
        this.boundingBox = BoundingBox(
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
        structureBoundingBoxIn: BoundingBox
    ): Boolean {
        for (i: Int in 0..<this.boundingBox.getXSpan()) {
            for (j: Int in 0..<this.boundingBox.getYSpan()) {
                for (k: Int in 0..<this.boundingBox.getZSpan()) {
                    if (((this.getDirection())!!
                            .getAxis() == EnumFacing.Axis.Z && (i == 0 || i == this.boundingBox.getXSpan() - 1)) || j == 0 || j == this.boundingBox.getYSpan() - 1 ||
                        ((this.getDirection())!!
                            .getAxis() == EnumFacing.Axis.X && (k == 0 || k == this.boundingBox.getZSpan() - 1))
                    ) {
                        this.setBlockState(worldIn, (this.configuration)!!.getBrickBlock(), i, j, k, this.boundingBox)
                    } else {
                        if (j == this.boundingBox.getYSpan() - 2) {
                            if ((this.getDirection())!!
                                    .getAxis() == EnumFacing.Axis.Z && (k + 1) % 4 == 0 && (i == 1 || i == this.boundingBox.getXSpan() - 2)
                            ) {
                                this.setBlockState(
                                    worldIn,
                                    (GCBlocks.unlitTorch)!!.defaultBlockState().setValue(
                                        BlockUnlitTorch.FACING,
                                        if (i == 1) EnumFacing.WEST.getOpposite() else EnumFacing.EAST.getOpposite()
                                    ),
                                    i,
                                    j,
                                    k,
                                    this.boundingBox
                                )
                                continue
                            } else if ((this.getDirection())!!
                                    .getAxis() == EnumFacing.Axis.X && (i + 1) % 4 == 0 && (k == 1 || k == this.boundingBox.getZSpan() - 2)
                            ) {
                                this.setBlockState(
                                    worldIn,
                                    (GCBlocks.unlitTorch)!!.defaultBlockState().setValue(
                                        BlockUnlitTorch.FACING,
                                        if (k == 1) EnumFacing.NORTH.getOpposite() else EnumFacing.SOUTH.getOpposite()
                                    ),
                                    i,
                                    j,
                                    k,
                                    this.boundingBox
                                )
                                continue
                            }
                        }

                        this.setBlockState(worldIn, Blocks.AIR.defaultBlockState(), i, j, k, this.boundingBox)
                    }
                }
            }
        }

        return true
    }

    private fun <T : SizedPiece?> getRoom(clazz: Class<*>, startPiece: DungeonStart, rand: Random?): T? {
        try {
            val c0: Constructor<*> = clazz.getConstructor(
                DungeonConfiguration::class.java,
                Random::class.java,
                Integer.TYPE,
                Integer.TYPE,
                EnumFacing::class.java
            )
            val dummy: T? = c0.newInstance(this.configuration, rand, 0, 0, (this.getDirection())!!.getOpposite()) as T?
            val extension: BoundingBox = getExtension(
                (this.getDirection())!!,
                if ((getDirection())!!.getAxis() == EnumFacing.Axis.X) dummy!!.getSizeX() else dummy!!.getSizeZ(),
                if ((getDirection())!!.getAxis() == EnumFacing.Axis.X) dummy.getSizeZ() else dummy.getSizeX()
            )
            if (startPiece.checkIntersection(extension)) {
                return null
            }
            val sizeX: Int = extension.maxX() - extension.minX()
            val sizeZ: Int = extension.maxZ() - extension.minZ()
            val sizeY: Int = dummy.sizeY
            val blockX: Int = extension.minX()
            val blockZ: Int = extension.minZ()
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

    override fun getNextPiece(startPiece: DungeonStart?, rand: Random?): Piece? {
        if ((startPiece)!!.attachedComponents.size > 2 && (startPiece)!!.attachedComponents.get((startPiece)!!.attachedComponents.size - 2) is RoomBoss) {
            try {
                return getRoom<SizedPiece?>(((this.configuration)!!.getTreasureRoom())!!, (startPiece)!!, rand)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            val bossRoomChance: Int = max((1.0 / ((startPiece)!!.attachedComponents.size / 55.0).pow(2.0)).toInt(), 5)
            val bossRoom: Boolean = (rand)!!.nextInt(bossRoomChance) == 0
            if (bossRoom) {
                try {
                    return getRoom<SizedPiece?>(((this.configuration)!!.getBossRoom())!!, (startPiece)!!, rand)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                val extension: BoundingBox =
                    getExtension((this.getDirection())!!, (rand)!!.nextInt(4) + 6, (rand)!!.nextInt(4) + 6)

                if ((startPiece)!!.checkIntersection(extension)) {
                    return null
                }

                val sizeX: Int = extension.maxX() - extension.minX()
                val sizeZ: Int = extension.maxZ() - extension.minZ()
                val sizeY: Int = (configuration)!!.getRoomHeight()
                val blockX: Int = extension.minX()
                val blockZ: Int = extension.minZ()

                if (abs((startPiece)!!.getBoundingBox().maxZ() - boundingBox.minZ()) > 200) {
                    return null
                }

                if (abs((startPiece)!!.getBoundingBox().maxX() - boundingBox.minX()) > 200) {
                    return null
                }

                when ((rand)!!.nextInt(3)) {
                    0 -> return RoomSpawner(
                        this.configuration,
                        rand,
                        blockX,
                        blockZ,
                        sizeX,
                        sizeY,
                        sizeZ,
                        (this.getDirection())!!.getOpposite()
                    )

                    1 -> return RoomChest(
                        this.configuration,
                        rand,
                        blockX,
                        blockZ,
                        sizeX,
                        sizeY,
                        sizeZ,
                        (this.getDirection())!!.getOpposite()
                    )

                    2 -> return RoomEmpty(
                        (this.configuration)!!,
                        rand,
                        blockX,
                        blockZ,
                        sizeX,
                        sizeY,
                        sizeZ,
                        (this.getDirection())!!.getOpposite()
                    )

                    else -> return RoomEmpty(
                        (this.configuration)!!,
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

        return null
    }
}
