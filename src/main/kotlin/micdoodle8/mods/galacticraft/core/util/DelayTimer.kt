package micdoodle8.mods.galacticraft.core.util

import net.minecraft.world.World

open class DelayTimer
constructor(delay: Long) {
    private var lastMark = Long.MIN_VALUE
    private var internalDelay: Long = 1

    init {
        internalDelay = delay
    }

    @JvmOverloads
    open fun markTimeIfDelay(world: World?, delay: Long = internalDelay): Boolean {
        if (world == null) {
            return false
        }

        val currentTime: Long = world.getTotalWorldTime()

        if (currentTime < lastMark) {
            lastMark = currentTime
            return false
        } else if (lastMark + delay <= currentTime) {
            lastMark = currentTime
            return true
        } else {
            return false
        }
    }
}
