package micdoodle8.mods.galacticraft.core.network

import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import java.io.IOException
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.GCLog
import micdoodle8.mods.galacticraft.core.util.WorldUtil
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.network.NetHandlerPlayServer
import net.minecraft.network.NetworkManager
import net.minecraft.network.PacketBuffer
import net.minecraftforge.eventbus.api.listener.SubscribeEvent
import net.minecraftforge.fml.common.network.FMLEventChannel
import net.minecraftforge.fml.common.network.FMLNetworkEvent
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.lang.Exception
import java.lang.RuntimeException
import java.util.ArrayList
import java.util.Collections

open class ConnectionPacket {
    open fun handle(payload: ByteBuf, player: EntityPlayer?) {
        val packetId: Int = payload.readByte().toInt()
        var data: MutableList<Any?> = ArrayList<Any?>()
        when (packetId) {
            101 -> {
                val length: Int = payload.readInt()
                var i: Int = 0
                while (i < length) {
                    data.add(payload.readInt())
                    i++
                }
                WorldUtil.decodePlanetsListClient((data as MutableList<Any>))
            }

            102 -> {
                val llength: Int = payload.readInt()
                var i: Int = 0
                while (i < llength) {
                    data.add(payload.readInt())
                    i++
                }
                WorldUtil.decodeSpaceStationListClient(data)
            }

            103 -> try {
                data =
                    NetworkUtil.decodeData(((PacketSimple.EnumSimplePacket.C_UPDATE_CONFIGS.getDecodeClasses())!! as Array<Class<*>>), payload)
                ConfigManagerCore.saveClientConfigOverrideable()
                ConfigManagerCore.setConfigOverride(data)
                if (ConfigManagerCore.enableDebug) {
                    GCLog.info("Server-set configs received OK on client.")
                }
            } catch (e: Exception) {
                System.err.println("[Galacticraft] Error handling connection packet - maybe the player's Galacticraft version does not match the server version?")
                e.printStackTrace()
            }

            else -> {}
        }
        if (payload.readInt() != 3519) {
            GCLog.severe("Packet completion problem for connection packet " + packetId + " - maybe the player's Galacticraft version does not match the server version?")
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    open fun onPacketData(event: FMLNetworkEvent.ClientCustomPacketEvent) {
        val pkt: FMLProxyPacket = event.getPacket()

        onFMLProxyPacketData(event.getManager(), pkt, Minecraft.getInstance().player)
    }

    @SubscribeEvent
    open fun onPacketData(event: FMLNetworkEvent.ServerCustomPacketEvent) {
        val pkt: FMLProxyPacket = event.getPacket()

        onFMLProxyPacketData(event.getManager(), pkt, (event.getHandler() as NetHandlerPlayServer).player)
    }

    open fun onFMLProxyPacketData(manager: NetworkManager?, packet: FMLProxyPacket, player: EntityPlayer?) {
        try {
            if ((packet == null) || (packet.payload() == null)) {
                throw RuntimeException("Empty packet sent to Galacticraft channel")
            }
            val data: ByteBuf = packet.payload()
            this.handle(data, player)
        } catch (e: Exception) {
            GCLog.severe("GC login packet handler: Failed to read packet")
            GCLog.severe(e.toString())
            e.printStackTrace()
        }
    }

    companion object {
        const val CHANNEL: String = "galacticraft"

        @JvmField
        var bus: FMLEventChannel? = null

        @JvmStatic
        fun createDimPacket(dims: Array<Int>): FMLProxyPacket {
            val data: ArrayList<Int> = ArrayList<Int>()
            for (dim: Int in dims) {
                data.add(dim)
            }
            return createPacket(101.toByte(), data)
        }

        @JvmStatic
        fun createSSPacket(dims: Array<Int>): FMLProxyPacket {
            val data: ArrayList<Int> = ArrayList<Int>()
            Collections.addAll<Int>(data, *dims)
            return createPacket(102.toByte(), data)
        }

        @JvmStatic
        fun createPacket(packetId: Byte, data: MutableCollection<Int>): FMLProxyPacket {
            val payload: PacketBuffer = PacketBuffer(Unpooled.buffer())

            payload.writeByte(packetId.toInt())
            payload.writeInt(data.size)
            for (i: Int in data) {
                payload.writeInt(i)
            }
            payload.writeInt(3519) //signature
            return FMLProxyPacket(payload, CHANNEL)
        }

        @JvmStatic
        fun createConfigPacket(data: MutableList<Any?>): FMLProxyPacket {
            val payload: PacketBuffer = PacketBuffer(Unpooled.buffer())
            payload.writeByte(103)
            try {
                NetworkUtil.encodeData(payload, data)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            payload.writeInt(3519) //signature
            return FMLProxyPacket(payload, CHANNEL)
        }
    }
}
