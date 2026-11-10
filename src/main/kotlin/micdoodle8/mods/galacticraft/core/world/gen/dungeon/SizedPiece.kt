package micdoodle8.mods.galacticraft.core.world.gen.dungeon

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.world.gen.structure.template.TemplateManager

abstract class SizedPiece : DirectionalPiece {
    @JvmField
    protected var sizeX: Int = 0

    @JvmField
    var sizeY: Int = 0

    @JvmField
    protected var sizeZ: Int = 0

    constructor()

    constructor(
        configuration: DungeonConfiguration?,
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

    override fun readStructureFromNBT(nbt: NBTTagCompound, manager: TemplateManager) {
        super.readStructureFromNBT(nbt, manager)

        this.sizeX = nbt.getInteger("sizeX")
        this.sizeY = nbt.getInteger("sizeY")
        this.sizeZ = nbt.getInteger("sizeZ")
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
