package micdoodle8.mods.galacticraft.planets.venus.world.gen.dungeon

import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.core.Direction as EnumFacing
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

        tagCompound.putInt("sizeX", this.sizeX)
        tagCompound.putInt("sizeY", this.sizeY)
        tagCompound.putInt("sizeZ", this.sizeZ)
    }

    override fun readStructureFromNBT(tagCompound: NBTTagCompound, manager: TemplateManager) {
        super.readStructureFromNBT(tagCompound, manager)

        this.sizeX = tagCompound.getIntOr("sizeX", 0)
        this.sizeY = tagCompound.getIntOr("sizeY", 0)
        this.sizeZ = tagCompound.getIntOr("sizeZ", 0)
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
