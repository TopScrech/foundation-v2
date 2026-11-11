package micdoodle8.mods.galacticraft.planets.asteroids.world.gen.base

import foundation.persistence.containsTag

import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.world.level.levelgen.structure.BoundingBox
import net.minecraft.world.gen.structure.template.TemplateManager
import java.util.Random

abstract class SizedPiece : Piece {
    @JvmField
    protected var direction: EnumFacing? = null

    @JvmField
    protected var sizeX: Int = 0

    @JvmField
    protected var sizeY: Int = 0

    @JvmField
    protected var sizeZ: Int = 0

    constructor()

    constructor(configuration: BaseConfiguration?, sizeX: Int, sizeY: Int, sizeZ: Int, direction: EnumFacing?) : super(
        configuration
    ) {
        this.direction = direction
        this.sizeX = sizeX
        this.sizeY = sizeY
        this.sizeZ = sizeZ
    }

    fun getDirection(): EnumFacing? {
        return direction
    }

    fun setDirection(direction: EnumFacing?) {
        this.direction = direction
    }

    override fun writeStructureToNBT(tagCompound: NBTTagCompound) {
        super.writeStructureToNBT(tagCompound)

        tagCompound.putInt("dir", this.direction!!.ordinal)
        tagCompound.putInt("sX", this.sizeX)
        tagCompound.putInt("sY", this.sizeY)
        tagCompound.putInt("sZ", this.sizeZ)
    }

    override fun readStructureFromNBT(tagCompound: NBTTagCompound, manager: TemplateManager) {
        super.readStructureFromNBT(tagCompound, manager)

        this.sizeX = tagCompound.getIntOr("sX", 0)
        this.sizeY = tagCompound.getIntOr("sY", 0)
        this.sizeZ = tagCompound.getIntOr("sZ", 0)

        if (tagCompound.containsTag("dir")) {
            this.direction = EnumFacing.from3DDataValue(tagCompound.getIntOr("dir", 0))
        } else {
            this.direction = EnumFacing.NORTH
        }
    }

    fun getSizeX(): Int {
        return sizeX
    }

    fun getSizeY(): Int {
        return sizeY
    }

    fun getSizeZ(): Int {
        return sizeZ
    }

    override fun getXWithOffset(x: Int, z: Int): Int {
        if (this.getCoordBaseMode() == null) {
            return x
        } else {
            when (this.getCoordBaseMode()) {
                EnumFacing.NORTH -> return this.boundingBox.minX() + x
                EnumFacing.SOUTH -> return this.boundingBox.maxX() - x
                EnumFacing.WEST -> return this.boundingBox.maxX() - z
                EnumFacing.EAST -> return this.boundingBox.minX() + z
                else -> return x
            }
        }
    }

    override fun getZWithOffset(x: Int, z: Int): Int {
        if (this.getCoordBaseMode() == null) {
            return z
        } else {
            when (this.getCoordBaseMode()) {
                EnumFacing.NORTH -> return this.boundingBox.minZ() + z
                EnumFacing.SOUTH -> return this.boundingBox.maxZ() - z
                EnumFacing.WEST -> return this.boundingBox.minZ() + x
                EnumFacing.EAST -> return this.boundingBox.maxZ() - x
                else -> return z
            }
        }
    }

    //Unused currently
    fun getDoorway(rand: Random, startPiece: BaseStart?, maxAttempts: Int, small: Boolean): Piece? {
        var randomDir: EnumFacing
        var blockX: Int
        var blockZ: Int
        var sizeX: Int
        var sizeZ: Int
        var valid: Boolean
        var attempts: Int = maxAttempts
        do {
            val randDir: Int = rand.nextInt(4)
            randomDir = EnumFacing.byHorizontalIndex(
                (if (randDir == getDirection()!!.getOpposite().getHorizontalIndex()) randDir + 1 else randDir) % 4
            )
            val extension: BoundingBox = getExtension(randomDir, 1, 3)
            blockX = extension.minX()
            blockZ = extension.minZ()
            sizeX = extension.maxX() - extension.minX()
            sizeZ = extension.maxZ() - extension.minZ()
            valid = true
            attempts--
        } while (!valid && attempts > 0)

        if (!valid) {
            return null
        }

        return BaseLinking(this.configuration, rand, blockX, this.boundingBox.minY(), blockZ, sizeX, 3, sizeZ, randomDir)
    }
}
