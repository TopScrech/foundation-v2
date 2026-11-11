package micdoodle8.mods.galacticraft.api.event.wgen

import net.minecraft.core.BlockPos
import net.minecraft.util.RandomSource
import net.minecraft.world.level.WorldGenLevel
import net.minecraftforge.eventbus.api.bus.EventBus
import net.minecraftforge.eventbus.api.event.MutableEvent

/** Events surrounding a planet or moon decoration pass */
abstract class GCCoreEventPopulate(
    @JvmField val world: WorldGenLevel?,
    @JvmField val rand: RandomSource?,
    @JvmField val pos: BlockPos?
) : MutableEvent() {
    abstract fun post(): Boolean

    class Pre(world: WorldGenLevel?, rand: RandomSource?, pos: BlockPos?) : GCCoreEventPopulate(world, rand, pos) {
        override fun post(): Boolean = BUS.post(this)
        companion object {
            @JvmField val BUS: EventBus<Pre> = EventBus.create(Pre::class.java)
        }
    }

    class Post(world: WorldGenLevel?, rand: RandomSource?, pos: BlockPos?) : GCCoreEventPopulate(world, rand, pos) {
        override fun post(): Boolean = BUS.post(this)
        companion object {
            @JvmField val BUS: EventBus<Post> = EventBus.create(Post::class.java)
        }
    }
}
