package micdoodle8.mods.galacticraft.planets.venus.tick

import net.minecraftforge.fml.common.gameevent.TickEvent.Phase

import com.google.common.collect.Lists
import micdoodle8.mods.galacticraft.planets.venus.tile.SolarModuleNetwork
import micdoodle8.mods.galacticraft.planets.venus.tile.TileEntitySolarTransmitter
import net.minecraft.server.MinecraftServer
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.util.*

open class VenusTickHandlerServer {
    @SubscribeEvent
    open fun onServerTick(event: TickEvent.ServerTickEvent) {
        val server: MinecraftServer? = FMLCommonHandler.instance().getMinecraftServerInstance()
        //Prevent issues when clients switch to LAN servers
        if (server == null) {
            return
        }

        if (event.phase == TickEvent.Phase.END) {
            for (network: SolarModuleNetwork in ArrayList<SolarModuleNetwork>(solarModuleNetworks)) {
                if (!(network.getTransmitters())!!.isEmpty()) {
//                    network.tickEnd();
                } else {
                    solarModuleNetworks.remove(network)
                }
            }

            var maxPasses: Int = 10
            while (!solarTransmitterUpdates.isEmpty()) {
                val pass: LinkedList<TileEntitySolarTransmitter> = LinkedList<TileEntitySolarTransmitter>()
                pass.addAll((solarTransmitterUpdates as Collection<TileEntitySolarTransmitter>))
                solarTransmitterUpdates.clear()
                for (newTile: TileEntitySolarTransmitter in pass) {
                    if (!newTile.isInvalid()) {
                        newTile.refresh()
                    }
                }

                if (--maxPasses <= 0) {
                    break
                }
            }
        }
    }

    companion object {
        private val solarModuleNetworks: MutableList<SolarModuleNetwork> = Lists.newArrayList<SolarModuleNetwork>()

        @JvmField
        var solarTransmitterUpdates: LinkedList<TileEntitySolarTransmitter?> = LinkedList<TileEntitySolarTransmitter?>()

        @JvmStatic
        fun addSolarNetwork(network: SolarModuleNetwork?) {
            solarModuleNetworks.add(network!!)
        }

        @JvmStatic
        fun removeSolarNetwork(network: SolarModuleNetwork?) {
            solarModuleNetworks.remove(network)
        }
    }
}
