package micdoodle8.mods.galacticraft.core.network

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.SimpleChannelInboundHandler
import java.util.concurrent.ConcurrentHashMap
import com.google.common.collect.ImmutableMap
import com.google.common.collect.Queues
import kotlin.concurrent.Volatile
import com.google.common.collect.Maps
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.tick.TickHandlerClient
import micdoodle8.mods.galacticraft.core.tick.TickHandlerServer
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.network.INetHandler
import net.minecraft.world.level.Level as World
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.LogicalSide as Side
import java.lang.Exception
import java.util.Queue

@Sharable
open class GalacticraftPacketHandler constructor() : SimpleChannelInboundHandler<IPacket>() {
    private val packetMap: Map<Side?, MutableMap<Int, Queue<PacketPlayerPair?>>>

    init {
        val map = HashMap<Side?, MutableMap<Int, Queue<PacketPlayerPair?>>>()
        for (side in Side.entries) {
            map[side] = ConcurrentHashMap<Int, Queue<PacketPlayerPair?>>()
        }
        packetMap = ImmutableMap.copyOf(map)
        if (GCCoreUtil.getEffectiveSide() == Side.CLIENT) {
            TickHandlerClient.Companion.addPacketHandler(this)
        }
        TickHandlerServer.addPacketHandler(this)
    }

    open fun unload(world: World) {
        val side: Side = if (world.isClientSide) Side.CLIENT else Side.SERVER
        val dimId: Int = GCCoreUtil.getDimensionID(world)
        val queue: Queue<PacketPlayerPair?> = getQueue(side, dimId)
        queue.clear()
    }

    open fun tick(world: World) {
        var pair: PacketPlayerPair?
        val side: Side = if (world.isClientSide) Side.CLIENT else Side.SERVER
        val dimID: Int = GCCoreUtil.getDimensionID(world)
        val queue: Queue<PacketPlayerPair?> = getQueue(side, dimID)
        while ((queue.poll().also { pair = it }) != null) {
            when (side) {
                Side.CLIENT -> pair!!.getPacket()!!.handleClientSide(((pair)!!.getPlayer())!!)
                Side.SERVER -> pair!!.getPacket()!!.handleServerSide(((pair)!!.getPlayer())!!)
            
else -> {}
}
        }
    }

    @Throws(Exception::class)
    override fun channelRead0(ctx: ChannelHandlerContext, msg: IPacket) {
        val netHandler: INetHandler? = ctx.channel().attr<INetHandler?>(NetworkRegistry.NET_HANDLER).get()
        val side: Side? = ctx.channel().attr<Side?>(NetworkRegistry.CHANNEL_SOURCE).get()
        val player: EntityPlayer? = (GalacticraftCore.proxy)!!.getPlayerFromNetHandler(netHandler)

        if (player == null) {
            return
        }

        if (side != null) {
            getQueue(side, msg.getDimensionID()).add(PacketPlayerPair(msg, player))
            livePacketCount++
        }
    }

    private fun getQueue(side: Side, dimID: Int): Queue<PacketPlayerPair?> {
        val map: MutableMap<Int, Queue<PacketPlayerPair?>> = packetMap.get(side)!!
        if (!map.containsKey(dimID)) {
            map.put(dimID, Queues.newConcurrentLinkedQueue<PacketPlayerPair?>())
        }
        return map.get(dimID)!!
    }

    private inner class PacketPlayerPair
        (private var packet: IPacket, private var player: EntityPlayer?) {
        fun getPacket(): IPacket? {
            return packet
        }

        fun setPacket(packet: IPacket) {
            this.packet = packet
        }

        fun getPlayer(): EntityPlayer? {
            return player
        }

        fun setPlayer(player: EntityPlayer?) {
            this.player = player
        }
    }

    companion object {
        @Volatile
        private var livePacketCount = 0
    }
}
