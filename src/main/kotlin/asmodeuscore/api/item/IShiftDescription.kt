package asmodeuscore.api.item


interface IShiftDescription {
    fun getShiftDescription(meta: Int): String?
    fun getDescription(meta: Int): String?

    fun showDescription(meta: Int): Boolean
}
