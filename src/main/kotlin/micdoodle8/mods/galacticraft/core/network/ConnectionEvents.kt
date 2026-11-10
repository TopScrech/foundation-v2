package micdoodle8.mods.galacticraft.core.network

import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ServerConnectionFromClientEvent

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.dimension.SpaceRace
import micdoodle8.mods.galacticraft.core.dimension.SpaceRaceManager
import micdoodle8.mods.galacticraft.core.dimension.SpaceStationWorldData
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderSpaceStation
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.tick.TickHandlerClient
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.GCLog
import micdoodle8.mods.galacticraft.core.util.PlayerUtil
import micdoodle8.mods.galacticraft.core.util.WorldUtil
import micdoodle8.mods.galacticraft.core.world.ChunkLoadingCallback
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.network.EnumConnectionState
import net.minecraft.network.EnumPacketDirection
import net.minecraft.network.INetHandler
import net.minecraft.network.Packet
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.PlayerEvent
import net.minecraftforge.fml.common.network.FMLNetworkEvent
import org.apache.logging.log4j.LogManager
import java.lang.IllegalArgumentException

open class ConnectionEvents {
    @SubscribeEvent
    open fun onPlayerLogout(event: PlayerEvent.PlayerLoggedOutEvent) {
        ChunkLoadingCallback.onPlayerLogout(event.player)
    }

    @SubscribeEvent
    open fun onPlayerLogin(event: PlayerEvent.PlayerLoggedInEvent) {
        ChunkLoadingCallback.onPlayerLogin(event.player)

        if (event.player is EntityPlayerMP) {
            val thePlayer: EntityPlayerMP = event.player as EntityPlayerMP
            val stats: GCPlayerStats = (GCPlayerStats.get(thePlayer))!!
            SpaceStationWorldData.checkAllStations(thePlayer, stats)
            (GalacticraftCore.packetPipeline)!!.sendTo(
                PacketSimple(
                    PacketSimple.EnumSimplePacket.C_UPDATE_SPACESTATION_CLIENT_ID,
                    GCCoreUtil.getDimensionID(thePlayer.world),
                    arrayOf<Any>(WorldUtil.spaceStationDataToString((stats.getSpaceStationDimensionData())!!))
                ), thePlayer
            )
            val raceForPlayer: SpaceRace? = SpaceRaceManager.getSpaceRaceFromPlayer(PlayerUtil.getName(thePlayer))
            if (raceForPlayer != null) {
                SpaceRaceManager.sendSpaceRaceData(thePlayer.server, thePlayer, raceForPlayer)
            }
        }

        if (event.player.world.provider is WorldProviderSpaceStation && event.player is EntityPlayerMP) {
            (event.player.world.provider as WorldProviderSpaceStation).getSpinManager()
                .sendPackets(event.player as EntityPlayerMP)
        }
    }

    @SubscribeEvent
    open fun onConnectionReceived(event: FMLNetworkEvent.ServerConnectionFromClientEvent) {
        if (ConfigManagerCore.enableDebug) {
            val idList: Array<Int> = WorldUtil.getPlanetList().get(0) as Array<Int>
            var ids: String = ""
            for (j: Int in idList.indices) {
                ids += idList[j].toString() + " "
            }
            GCLog.info("Galacticraft server sending dimension IDs to connecting client: " + ids)
        }
        event.getManager().sendPacket(ConnectionPacket.Companion.createDimPacket(WorldUtil.getPlanetListInts()))
        event.getManager().sendPacket(ConnectionPacket.Companion.createSSPacket(WorldUtil.getSpaceStationListInts()))
        event.getManager()
            .sendPacket(ConnectionPacket.Companion.createConfigPacket(ConfigManagerCore.getServerConfigOverride()))
    }

    @SubscribeEvent
    open fun onConnectionOpened(event: FMLNetworkEvent.ClientConnectedToServerEvent) {
        if (!event.isLocal()) {
            clientConnected = true
        }
    }

    @SubscribeEvent
    open fun onConnectionClosed(event: FMLNetworkEvent.ClientDisconnectionFromServerEvent?) {
        TickHandlerClient.Companion.menuReset = true
        if (clientConnected) {
            clientConnected = false
            WorldUtil.unregisterPlanets()
            WorldUtil.unregisterSpaceStations()
            ConfigManagerCore.restoreClientConfigOverrideable()
        }
    }

    companion object {
        private var clientConnected = false

        init {
            EnumConnectionState.STATES_BY_CLASS.put(PacketSimple::class.java, EnumConnectionState.PLAY)
            registerPacket(EnumPacketDirection.CLIENTBOUND, PacketSimple::class.java)
        }

        @JvmStatic
        protected fun registerPacket(
            direction: EnumPacketDirection?,
            packetClass: Class<out Packet<out INetHandler>?>?
        ): EnumConnectionState {
            var bimap: BiMap<Int, Class<out Packet<*>?>?>? = EnumConnectionState.PLAY.directionMaps.get(direction)

            if (bimap == null) {
                bimap = HashBiMap.create<Int, Class<out Packet<*>?>?>()
                EnumConnectionState.PLAY.directionMaps.put(direction, bimap)
            }

            if ((bimap)!!.containsValue(packetClass)) {
                val s: String =
                    direction.toString() + " packet " + packetClass + " is already known to ID " + (bimap)!!.inverse()
                        .get(packetClass)
                LogManager.getLogger().fatal(s)
                throw IllegalArgumentException(s)
            } else {
                (bimap)!!.put((bimap)!!.size, packetClass)
                return EnumConnectionState.PLAY
            }
        }
    }
}
