package micdoodle8.mods.galacticraft.core.world.gen.dungeon

import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.core.Direction as EnumFacing
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

        tagCompound.putInt("sizeX", this.sizeX)
        tagCompound.putInt("sizeY", this.sizeY)
        tagCompound.putInt("sizeZ", this.sizeZ)
    }

    override fun readStructureFromNBT(nbt: NBTTagCompound, manager: TemplateManager) {
        super.readStructureFromNBT(nbt, manager)

        this.sizeX = nbt.getIntOr("sizeX", 0)
        this.sizeY = nbt.getIntOr("sizeY", 0)
        this.sizeZ = nbt.getIntOr("sizeZ", 0)
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
