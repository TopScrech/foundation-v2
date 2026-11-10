package micdoodle8.mods.galacticraft.core.energy.grid

import java.util.ArrayList

import com.google.common.collect.Lists
import micdoodle8.mods.galacticraft.api.transmission.tile.INetworkConnection
import micdoodle8.mods.galacticraft.core.event.EventHandlerGC
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.world.ChunkEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

open class ChunkPowerHandler {
    @SubscribeEvent
    open fun onChunkLoad(event: ChunkEvent.Load) {
        if (!event.getWorld().isRemote && event.getChunk() != null) {
            try {
                val tileList: ArrayList<Any?> = Lists.newArrayList<Any?>()
                tileList.addAll(event.getChunk().getTileEntityMap().values)

                for (o: Any? in tileList) {
                    if (o is TileEntity) {
                        val tile: TileEntity = o

                        if (tile is INetworkConnection) {
                            (tile as INetworkConnection).refresh()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (ConfigManagerCore.retrogenOil && GCCoreUtil.getDimensionID(event.getWorld()) == 0) {
                EventHandlerGC.retrogenOil(event.getWorld(), event.getChunk())
            }
        }
    }

    companion object {
        private var initiated = false

        @JvmStatic
        fun initiate() {
            if (!initiated) {
                initiated = true
                MinecraftForge.EVENT_BUS.register(ChunkPowerHandler())
            }
        }
    }
}
