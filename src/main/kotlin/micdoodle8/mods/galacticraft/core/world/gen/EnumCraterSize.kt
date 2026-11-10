package micdoodle8.mods.galacticraft.core.world.gen


enum class EnumCraterSize
    (@JvmField val MIN_SIZE: Int, @JvmField val MAX_SIZE: Int, private val PROBABILITY: Int) {
    SMALL(8, 12, 14),
    MEDIUM(13, 17, 8),
    LARGE(18, 25, 2),
    EXTREME(26, 30, 1);

    companion object {
        @JvmField
        var sizeArray: Array<EnumCraterSize>

        init {
            var amount: Int = 0
            for (c: EnumCraterSize in entries) {
                amount += c.PROBABILITY
            }
            sizeArray = (arrayOfNulls<EnumCraterSize>(amount) as Array<EnumCraterSize>)
            var pointer: Int = 0
            for (c: EnumCraterSize in entries) {
                for (i: Int in 0..<c.PROBABILITY) {
                    sizeArray[pointer] = c
                    pointer++
                }
            }
        }
    }
}
