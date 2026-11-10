package micdoodle8.mods.galacticraft.core.entities


interface ITumblable {
    fun setTumbling(value: Float)

    fun getTumbleAngle(partial: Float): Float

    fun getTumbleAxisX(): Float

    fun getTumbleAxisZ(): Float
}
