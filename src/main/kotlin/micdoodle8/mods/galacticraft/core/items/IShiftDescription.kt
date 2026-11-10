package micdoodle8.mods.galacticraft.core.items

interface IShiftDescription {
    fun getShiftDescription(meta: Int): String?

    fun showDescription(meta: Int): Boolean
}
