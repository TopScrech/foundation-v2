package micdoodle8.mods.galacticraft.api.tile

interface ILockable {
    fun getLocked(): Boolean

    fun clearLockedInventory()
}
