package micdoodle8.mods.galacticraft.api.event.wgen

import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.eventhandler.Event
import java.util.Random

/**
 * Event is thrown when a chunk is populated on planets.
 *
 *
 * If you're adding your own dimensions, make sure you post these two events to
 * the forge event bus when decorating your planet/moon
 */
open class GCCoreEventPopulate constructor(
    @JvmField val world: World?,
    @JvmField val rand: Random?,
    @JvmField val pos: BlockPos?
) : Event() {
    open class Pre constructor(world: World?, rand: Random?, pos: BlockPos?) :
        GCCoreEventPopulate(world, rand, pos)


    open class Post constructor(world: World?, rand: Random?, pos: BlockPos?) :
        GCCoreEventPopulate(world, rand, pos)
}
