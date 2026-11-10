package micdoodle8.mods.galacticraft.core.world.gen


open class StructureVillagePieceWeightMoon
constructor(
    @JvmField var villagePieceClass: Class<out StructureComponentVillage>?,
    @JvmField val villagePieceWeight: Int,
    par3: Int
) {
    @JvmField
    var villagePiecesSpawned: Int = 0

    @JvmField
    var villagePiecesLimit: Int

    init {
        this.villagePiecesLimit = (par3 / 1.5).toInt()
    }

    open fun canSpawnMoreVillagePiecesOfType(par1: Int): Boolean {
        return this.villagePiecesLimit == 0 || this.villagePiecesSpawned < this.villagePiecesLimit
    }

    open fun canSpawnMoreVillagePieces(): Boolean {
        return this.villagePiecesLimit == 0 || this.villagePiecesSpawned < this.villagePiecesLimit
    }
}
