package micdoodle8.mods.galacticraft.core.event

import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.eventhandler.Event

open class EventLandingPadRemoval constructor(@JvmField val world: World?, @JvmField val pos: BlockPos?) :
    Event() {
    @JvmField
    var allow: Boolean = true
}
