package micdoodle8.mods.galacticraft.planets.venus.world.gen.dungeon

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.world.gen.structure.template.TemplateManager

abstract class SizedPieceVenus : DirectionalPieceVenus {
    @JvmField
    protected var sizeX: Int = 0

    @JvmField
    protected var sizeY: Int = 0

    @JvmField
    protected var sizeZ: Int = 0

    constructor()

    constructor(
        configuration: DungeonConfigurationVenus?,
        sizeX: Int,
        sizeY: Int,
        sizeZ: Int,
        direction: EnumFacing?
    ) : super(configuration, direction) {
        this.sizeX = sizeX
        this.sizeY = sizeY
        this.sizeZ = sizeZ
    }

    override fun writeStructureToNBT(tagCompound: NBTTagCompound) {
        super.writeStructureToNBT(tagCompound)

        tagCompound.setInteger("sizeX", this.sizeX)
        tagCompound.setInteger("sizeY", this.sizeY)
        tagCompound.setInteger("sizeZ", this.sizeZ)
    }

    override fun readStructureFromNBT(tagCompound: NBTTagCompound, manager: TemplateManager) {
        super.readStructureFromNBT(tagCompound, manager)

        this.sizeX = tagCompound.getInteger("sizeX")
        this.sizeY = tagCompound.getInteger("sizeY")
        this.sizeZ = tagCompound.getInteger("sizeZ")
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
}
