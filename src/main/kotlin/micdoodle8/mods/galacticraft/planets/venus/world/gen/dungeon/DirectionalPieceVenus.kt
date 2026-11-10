package micdoodle8.mods.galacticraft.planets.venus.world.gen.dungeon

import java.util.Random

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.world.gen.structure.StructureBoundingBox
import net.minecraft.world.gen.structure.template.TemplateManager
import java.util.*

abstract class DirectionalPieceVenus : PieceVenus {
    private var direction: EnumFacing? = null

    constructor()

    constructor(configuration: DungeonConfigurationVenus?, direction: EnumFacing?) : super(configuration) {
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

    override fun readStructureFromNBT(tagCompound: NBTTagCompound, manager: TemplateManager) {
        super.readStructureFromNBT(tagCompound, manager)

        if (tagCompound.hasKey("direction")) {
            this.direction = EnumFacing.byIndex(tagCompound.getInteger("direction"))
        } else {
            this.direction = EnumFacing.NORTH
        }
    }

    fun getCorridor(rand: Random, startPiece: DungeonStartVenus, maxAttempts: Int, small: Boolean): PieceVenus? {
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
                5
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

        return CorridorVenus(
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
