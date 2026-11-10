package micdoodle8.mods.galacticraft.core.entities


interface IBubbleProvider {
    fun getBubbleSize(): Float

    fun setBubbleVisible(shouldRender: Boolean)

    fun getBubbleVisible(): Boolean
}
