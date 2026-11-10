package micdoodle8.mods.galacticraft.core.tile

import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.ArrayList
import java.util.HashSet

open class AirLockProtocol
constructor(private val head: TileEntity) {
    private var adjacentAirLocks: ArrayList<BlockPos>
    private val checked: HashSet<BlockPos?>
    private val world: World
    private val maxLoops = 26

    private var airLocksDimension1Min = 0
    private var airLocksDimension1Max = 0
    private var airLocksDimension2Min = 0
    private var airLocksDimension2Max = 0

    @JvmField
    var minX: Int = 6000000

    @JvmField
    var maxX: Int = -6000000

    @JvmField
    var minY: Int = 6000000

    @JvmField
    var maxY: Int = -6000000

    @JvmField
    var minZ: Int = 6000000

    @JvmField
    var maxZ: Int = -6000000

    init {
        this.adjacentAirLocks = ArrayList<BlockPos>()
        this.checked = HashSet<BlockPos?>()
        this.world = head.getWorld()
    }

    private fun loopThrough(pos: BlockPos, loops: Int) {
        val xAligned: Int = this.head.getPos().getX()
        val zAligned: Int = this.head.getPos().getZ()
        for (x: Int in -1..1) {
            val xTest: Int = pos.getX() + x
            for (z: Int in -1..1) {
                val zTest: Int = pos.getZ() + z

                if ((xTest == xAligned || zTest == zAligned)) {
                    for (y: Int in -1..1) {
                        if (!(x == 0 && y == 0 && z == 0)) {
                            val testPos: BlockPos = BlockPos(xTest, pos.getY() + y, zTest)
                            if (!this.checked.contains(testPos)) {
                                this.checked.add(testPos)
                                if (this.world.getTileEntity(testPos) is TileEntityAirLock) {
                                    this.adjacentAirLocks.add(testPos)
                                    if (loops > 1) {
                                        this.loopThrough(testPos, loops - 1)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun loopThroughHorizontal(pos: BlockPos, loops: Int) {
        val yTest: Int = pos.getY()
        for (x: Int in -1..1) {
            val xTest: Int = pos.getX() + x
            for (z: Int in -1..1) {
                if (!(x == 0 && z == 0)) {
                    val testPos: BlockPos = BlockPos(xTest, yTest, pos.getZ() + z)
                    if (!this.checked.contains(testPos)) {
                        this.checked.add(testPos)
                        if (this.world.getTileEntity(testPos) is TileEntityAirLock) {
                            this.adjacentAirLocks.add(testPos)
                            if (loops > 1) {
                                this.loopThroughHorizontal(testPos, loops - 1)
                            }
                        }
                    }
                }
            }
        }
    }

    open fun calculate(horizontal: Boolean): Int {
        if (this.world.isRemote) {
            return -1
        }

        this.minX = 6000000
        this.maxX = -6000000
        this.minY = 6000000
        this.maxY = -6000000
        this.minZ = 6000000
        this.maxZ = -6000000

        this.adjacentAirLocks = ArrayList<BlockPos>()
        this.checked.clear()
        val headPos: BlockPos = this.head.getPos()
        this.checked.add(headPos)
        this.adjacentAirLocks.add(headPos)

        if (horizontal) {
            this.loopThroughHorizontal(headPos, this.maxLoops)
        } else {
            this.loopThrough(headPos, this.maxLoops)
        }

        for (airLock: BlockPos in this.adjacentAirLocks) {
            if (airLock.getX() < this.minX) {
                this.minX = airLock.getX()
            }

            if (airLock.getX() > this.maxX) {
                this.maxX = airLock.getX()
            }

            if (airLock.getY() < this.minY) {
                this.minY = airLock.getY()
            }

            if (airLock.getY() > this.maxY) {
                this.maxY = airLock.getY()
            }

            if (airLock.getZ() < this.minZ) {
                this.minZ = airLock.getZ()
            }

            if (airLock.getZ() > this.maxZ) {
                this.maxZ = airLock.getZ()
            }
        }

        val count: Int = this.maxX - this.minX + this.maxZ - this.minZ + this.maxY - this.minY

        if (count > 24 || this.maxX - this.minX <= 1 && this.maxZ - this.minZ <= 1 || !horizontal && this.maxY - this.minY <= 1) {
            return -1
        }

        if (horizontal && (this.maxX - this.minX <= 1 || this.maxZ - this.minZ <= 1)) {
            return -1
        }

        this.airLocksDimension1Min = 0
        this.airLocksDimension1Max = 0
        this.airLocksDimension2Min = 0
        this.airLocksDimension2Max = 0

        if (horizontal) {
            this.checkDimensionsHorizontal()
        } else {
            this.checkDimensions()
        }

        if (this.airLocksDimension2Max == 0 || this.airLocksDimension2Min == 0 || (this.airLocksDimension1Min == 0 || this.airLocksDimension1Max == 0) || this.airLocksDimension2Max != this.airLocksDimension2Min || this.airLocksDimension1Max != this.airLocksDimension1Min) {
            return -1
        }

        return this.adjacentAirLocks.size
    }

    private fun checkDimensions() {
        for (y: Int in this.minY..this.maxY) {
            val tileAt: TileEntity? = this.world.getTileEntity(BlockPos(this.minX, y, this.minZ))

            if (tileAt is TileEntityAirLock) {
                this.airLocksDimension1Min++
            }
        }

        for (y: Int in this.minY..this.maxY) {
            val tileAt: TileEntity? = this.world.getTileEntity(BlockPos(this.maxX, y, this.maxZ))

            if (tileAt is TileEntityAirLock) {
                this.airLocksDimension1Max++
            }
        }

        if (this.minX != this.maxX) {
            for (x: Int in this.minX..this.maxX) {
                val tileAt: TileEntity? = this.world.getTileEntity(BlockPos(x, this.maxY, this.maxZ))

                if (tileAt is TileEntityAirLock) {
                    this.airLocksDimension2Max++
                }
            }

            for (x: Int in this.minX..this.maxX) {
                val tileAt: TileEntity? = this.world.getTileEntity(BlockPos(x, this.minY, this.maxZ))

                if (tileAt is TileEntityAirLock) {
                    this.airLocksDimension2Min++
                }
            }
        } else if (this.minZ != this.maxZ) {
            for (z: Int in this.minZ..this.maxZ) {
                val tileAt: TileEntity? = this.world.getTileEntity(BlockPos(this.maxX, this.maxY, z))

                if (tileAt is TileEntityAirLock) {
                    this.airLocksDimension2Max++
                }
            }

            for (z: Int in this.minZ..this.maxZ) {
                val tileAt: TileEntity? = this.world.getTileEntity(BlockPos(this.maxX, this.minY, z))

                if (tileAt is TileEntityAirLock) {
                    this.airLocksDimension2Min++
                }
            }
        }
    }

    private fun checkDimensionsHorizontal() {
        if (this.minX != this.maxX) {
            for (x: Int in this.minX..this.maxX) {
                val tileAt: TileEntity? = this.world.getTileEntity(BlockPos(x, this.minY, this.maxZ))

                if (tileAt is TileEntityAirLock) {
                    this.airLocksDimension1Max++
                }
            }

            for (x: Int in this.minX..this.maxX) {
                val tileAt: TileEntity? = this.world.getTileEntity(BlockPos(x, this.minY, this.minZ))

                if (tileAt is TileEntityAirLock) {
                    this.airLocksDimension1Min++
                }
            }
        }

        if (this.minZ != this.maxZ) {
            for (z: Int in this.minZ..this.maxZ) {
                val tileAt: TileEntity? = this.world.getTileEntity(BlockPos(this.maxX, this.minY, z))

                if (tileAt is TileEntityAirLock) {
                    this.airLocksDimension2Max++
                }
            }

            for (z: Int in this.minZ..this.maxZ) {
                val tileAt: TileEntity? = this.world.getTileEntity(BlockPos(this.minX, this.minY, z))

                if (tileAt is TileEntityAirLock) {
                    this.airLocksDimension2Min++
                }
            }
        }
    }
}
