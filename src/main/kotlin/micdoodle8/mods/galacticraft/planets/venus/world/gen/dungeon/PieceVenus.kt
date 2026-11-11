package micdoodle8.mods.galacticraft.planets.venus.world.gen.dungeon

import foundation.level.horizontalStructureBounds

import java.util.Random

import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.world.level.levelgen.structure.BoundingBox
import net.minecraft.world.gen.structure.StructureComponent
import net.minecraft.world.gen.structure.template.TemplateManager
import java.util.*

abstract class PieceVenus : StructureComponent {
    @JvmField
    protected var configuration: DungeonConfigurationVenus? = null

    constructor()

    constructor(configuration: DungeonConfigurationVenus?) {
        this.configuration = configuration
    }

    override fun writeStructureToNBT(tagCompound: NBTTagCompound) {
        this.configuration!!.writeToNBT(tagCompound)
    }

    override fun readStructureFromNBT(tagCompound: NBTTagCompound, manager: TemplateManager) {
        this.configuration = DungeonConfigurationVenus()
        this.configuration!!.readFromNBT(tagCompound)
    }

    protected open fun getExtension(direction: EnumFacing, length: Int, width: Int): BoundingBox {
        val blockX: Int
        val blockZ: Int
        val sizeX: Int
        val sizeZ: Int
        when (direction) {
            EnumFacing.NORTH -> {
                sizeX = width
                sizeZ = length
                blockX = this.boundingBox.minX() + (this.boundingBox.maxX() - this.boundingBox.minX()) / 2 - sizeX / 2
                blockZ = this.boundingBox.minZ() - sizeZ
            }

            EnumFacing.EAST -> {
                sizeX = length
                sizeZ = width
                blockX = this.boundingBox.maxX()
                blockZ = this.boundingBox.minZ() + (this.boundingBox.maxZ() - this.boundingBox.minZ()) / 2 - sizeZ / 2
            }

            EnumFacing.SOUTH -> {
                sizeX = width
                sizeZ = length
                blockX = this.boundingBox.minX() + (this.boundingBox.maxX() - this.boundingBox.minX()) / 2 - sizeX / 2
                blockZ = this.boundingBox.maxZ()
            }

            EnumFacing.WEST -> {
                sizeX = length
                sizeZ = width
                blockX = this.boundingBox.minX() - sizeX
                blockZ = this.boundingBox.minZ() + (this.boundingBox.maxZ() - this.boundingBox.minZ()) / 2 - sizeZ / 2
            }

            else -> {
                sizeX = length
                sizeZ = width
                blockX = this.boundingBox.minX() - sizeX
                blockZ = this.boundingBox.minZ() + (this.boundingBox.maxZ() - this.boundingBox.minZ()) / 2 - sizeZ / 2
            }
        }
        return horizontalStructureBounds(blockX, blockZ, blockX + sizeX, blockZ + sizeZ)
    }

    open fun getNextPiece(startPiece: DungeonStartVenus?, rand: Random?): PieceVenus? {
        return null
    }
}
