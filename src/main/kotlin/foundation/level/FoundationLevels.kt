package foundation.level

import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import net.minecraftforge.event.level.LevelEvent
import net.minecraftforge.fml.LogicalSide
import net.minecraftforge.fml.util.thread.EffectiveSide
import net.minecraftforge.server.ServerLifecycleHooks
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap

/** Tracks loaded client levels without linking common classes to the Minecraft client */
object FoundationLevels {
    private val clients = ConcurrentHashMap<ResourceKey<Level>, WeakReference<Level>>()

    /** Called from mod construction before either logical side loads a level */
    fun install() = Unit

    init {
        LevelEvent.Load.BUS.addListener { event: LevelEvent.Load ->
            val level = event.getLevel() as? Level
            if (level != null && level.isClientSide) clients[level.dimension()] = WeakReference(level)
        }
        LevelEvent.Unload.BUS.addListener { event: LevelEvent.Unload ->
            val level = event.getLevel() as? Level
            if (level != null && level.isClientSide) {
                clients.computeIfPresent(level.dimension()) { _, current ->
                    if (current.get() === level) null else current
                }
            }
        }
    }

    fun get(key: ResourceKey<Level>): Level? = if (EffectiveSide.get() == LogicalSide.SERVER) {
        ServerLifecycleHooks.getCurrentServer()?.getLevel(key)
    } else {
        clients[key]?.get()
    }
}
