package galaxyspace.systems.SolarSystem.moons.io.world.gen.dungeon

import galaxyspace.systems.SolarSystem.moons.io.world.gen.dungeon.DungeonStartIo
import galaxyspace.systems.SolarSystem.moons.io.world.gen.dungeon.PieceIo
import asmodeuscore.core.astronomy.dimension.world.gen.dungeons.standart.DungeonConfiguration
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.world.gen.structure.StructureBoundingBox
import net.minecraft.world.gen.structure.StructureComponent
import net.minecraft.world.gen.structure.template.TemplateManager
import java.util.Random

abstract class PieceIo : StructureComponent {
    @JvmField
    protected var configuration: DungeonConfiguration? = null

    constructor()

    constructor(configuration: DungeonConfiguration?) {
        this.configuration = configuration
    }

    override fun writeStructureToNBT(tagCompound: NBTTagCompound) {
        this.configuration!!.writeToNBT(tagCompound)
    }

    override fun readStructureFromNBT(tagCompound: NBTTagCompound, manager: TemplateManager) {
        if (this.configuration == null) {
            this.configuration = DungeonConfiguration()
            this.configuration!!.readFromNBT(tagCompound)
        }
    }

    protected open fun getExtension(direction: EnumFacing, length: Int, width: Int): StructureBoundingBox {
        val blockX: Int
        val blockZ: Int
        val sizeX: Int
        val sizeZ: Int
        when (direction) {
            EnumFacing.NORTH -> {
                sizeX = width
                sizeZ = length
                blockX = this.boundingBox.minX + (this.boundingBox.maxX - this.boundingBox.minX) / 2 - sizeX / 2
                blockZ = this.boundingBox.minZ - sizeZ
            }

            EnumFacing.EAST -> {
                sizeX = length
                sizeZ = width
                blockX = this.boundingBox.maxX
                blockZ = this.boundingBox.minZ + (this.boundingBox.maxZ - this.boundingBox.minZ) / 2 - sizeZ / 2
            }

            EnumFacing.SOUTH -> {
                sizeX = width
                sizeZ = length
                blockX = this.boundingBox.minX + (this.boundingBox.maxX - this.boundingBox.minX) / 2 - sizeX / 2
                blockZ = this.boundingBox.maxZ
            }

            EnumFacing.WEST -> {
                sizeX = length
                sizeZ = width
                blockX = this.boundingBox.minX - sizeX
                blockZ = this.boundingBox.minZ + (this.boundingBox.maxZ - this.boundingBox.minZ) / 2 - sizeZ / 2
            }

            else -> {
                sizeX = length
                sizeZ = width
                blockX = this.boundingBox.minX - sizeX
                blockZ = this.boundingBox.minZ + (this.boundingBox.maxZ - this.boundingBox.minZ) / 2 - sizeZ / 2
            }
        }
        return StructureBoundingBox(blockX, blockZ, blockX + sizeX, blockZ + sizeZ)
    }

    open fun getNextPiece(startPiece: DungeonStartIo?, rand: Random?): PieceIo? {
        return null
    }
}
