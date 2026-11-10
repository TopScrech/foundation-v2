package micdoodle8.mods.galacticraft.core.world.gen.dungeon

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.world.gen.structure.StructureBoundingBox
import net.minecraft.world.gen.structure.template.TemplateManager
import java.util.Random

abstract class DirectionalPiece : Piece {
    private var direction: EnumFacing? = null

    constructor()

    constructor(configuration: DungeonConfiguration?, direction: EnumFacing?) : super(configuration) {
        this.direction = direction
    }

    fun getDirection(): EnumFacing? {
        return direction
    }

    fun setDirection(direction: EnumFacing?) {
        this.direction = direction
    }

    override fun writeStructureToNBT(tagCompound: NBTTagCompound) {
        super.writeStructureToNBT(tagCompound)

        tagCompound.setInteger("direction", this.direction!!.ordinal)
    }

    override fun readStructureFromNBT(nbt: NBTTagCompound, manager: TemplateManager) {
        super.readStructureFromNBT(nbt, manager)

        if (nbt.hasKey("direction")) {
            this.direction = EnumFacing.byIndex(nbt.getInteger("direction"))
        } else {
            this.direction = EnumFacing.NORTH
        }
    }

    fun getCorridor(rand: Random, startPiece: DungeonStart, maxAttempts: Int, small: Boolean): Piece? {
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
            val extension: StructureBoundingBox = getExtension(
                randomDir,
                (this.configuration)!!.getHallwayLengthMin() + rand.nextInt((this.configuration)!!.getHallwayLengthMax() - (this.configuration)!!.getHallwayLengthMin()),
                3
            )
            blockX = extension.minX
            blockZ = extension.minZ
            sizeX = extension.maxX - extension.minX
            sizeZ = extension.maxZ - extension.minZ
            valid = !startPiece.checkIntersection(extension)
            attempts--
        } while (!valid && attempts > 0)

        if (!valid) {
            return null
        }

        return Corridor(
            (this.configuration)!!,
            rand,
            blockX,
            blockZ,
            sizeX,
            if (small) 3 else (this.configuration)!!.getHallwayHeight(),
            sizeZ,
            randomDir
        )
    }
}
