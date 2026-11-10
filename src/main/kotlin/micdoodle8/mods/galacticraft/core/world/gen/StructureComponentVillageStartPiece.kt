package micdoodle8.mods.galacticraft.core.world.gen

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.biome.BiomeProvider
import net.minecraft.world.gen.structure.template.TemplateManager
import java.util.ArrayList
import java.util.Random

open class StructureComponentVillageStartPiece : StructureComponentVillageWell {
    @JvmField
    var biomeProvider: BiomeProvider? = null

    @JvmField
    var terrainType: Int = 0

    @JvmField
    var structVillagePieceWeight: StructureVillagePieceWeightMoon? = null

    @JvmField
    var structureVillageWeightedPieceList: ArrayList<StructureVillagePieceWeightMoon?>? = null

    @JvmField
    var field_74932_i: ArrayList<Any?> = ArrayList<Any?>()

    @JvmField
    var field_74930_j: ArrayList<Any?> = ArrayList<Any?>()

    constructor()

    constructor(
        biomeProvider: BiomeProvider?,
        par2: Int,
        par3Random: Random,
        par4: Int,
        par5: Int,
        par6ArrayList: ArrayList<StructureVillagePieceWeightMoon?>?,
        par7: Int
    ) : super(null, 0, par3Random, par4, par5) {
        this.biomeProvider = biomeProvider
        this.structureVillageWeightedPieceList = par6ArrayList
        this.terrainType = par7
        this.startPiece = this
    }

    override fun writeStructureToNBT(nbt: NBTTagCompound) {
        super.writeStructureToNBT(nbt)

        nbt.setInteger("TerrainType", this.terrainType)
    }

    override fun readStructureFromNBT(nbt: NBTTagCompound, manager: TemplateManager) {
        super.readStructureFromNBT(nbt, manager)

        this.terrainType = nbt.getInteger("TerrainType")
    }

    open fun getBiomeProvider(): BiomeProvider? {
        return this.biomeProvider
    }
}
