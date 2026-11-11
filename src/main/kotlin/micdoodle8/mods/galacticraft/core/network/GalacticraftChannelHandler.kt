package micdoodle8.mods.galacticraft.core.network

import io.netty.buffer.ByteBuf
import java.util.EnumMap
import io.netty.channel.ChannelHandlerContext
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.util.GCLog
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.network.FMLEmbeddedChannel
import net.minecraftforge.fml.common.network.FMLIndexedMessageToMessageCodec
import net.minecraftforge.fml.common.network.FMLOutboundHandler
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.LogicalSide as Side
import java.lang.Exception

open class GalacticraftChannelHandler private constructor() : FMLIndexedMessageToMessageCodec<IPacket>() {
    private var channels: EnumMap<Side?, FMLEmbeddedChannel?>? = null

    init {
        this.addDiscriminator(0, PacketSimple::class.java)
        this.addDiscriminator(1, PacketRotateRocket::class.java)
        this.addDiscriminator(2, PacketDynamic::class.java)
        this.addDiscriminator(3, PacketFluidNetworkUpdate::class.java)
        this.addDiscriminator(4, PacketEntityUpdate::class.java)
        this.addDiscriminator(5, PacketDynamicInventory::class.java)
    }

    @Throws(Exception::class)
    override fun encodeInto(ctx: ChannelHandlerContext?, msg: IPacket, target: ByteBuf?) {
        msg.encodeInto(target)
    }

    override fun decodeInto(ctx: ChannelHandlerContext?, source: ByteBuf?, msg: IPacket) {
        msg.decodeInto(source)
    }

    /**
     * Send this message to everyone.
     *
     *
     * Adapted from CPW's code in
     * cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper
     *
     * @param message The message to send
     */
    open fun sendToAll(message: IPacket?) {
        this.channels!!.get(Side.SERVER)!!
            .attr<FMLOutboundHandler.OutboundTarget?>(FMLOutboundHandler.FML_MESSAGETARGET)
            .set(FMLOutboundHandler.OutboundTarget.ALL)
        this.channels!!.get(Side.SERVER)!!.writeOutbound(message)
    }

    /**
     * Send this message to the specified player.
     *
     *
     * Adapted from CPW's code in
     * cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper
     *
     * @param message The message to send
     * @param player  The player to send it to
     */
    open fun sendTo(message: IPacket?, player: EntityPlayerMP?) {
        this.channels!!.get(Side.SERVER)!!
            .attr<FMLOutboundHandler.OutboundTarget?>(FMLOutboundHandler.FML_MESSAGETARGET)
            .set(FMLOutboundHandler.OutboundTarget.PLAYER)
        this.channels!!.get(Side.SERVER)!!.attr<Any?>(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player)
        this.channels!!.get(Side.SERVER)!!.writeOutbound(message)
    }

    /**
     * Send this message to everyone within a certain range of a point.
     *
     *
     * Adapted from CPW's code in
     * cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper
     *
     * @param message The message to send
     * @param point   The
     * [NetworkRegistry.TargetPoint]
     * around which to send
     */
    open fun sendToAllAround(message: IPacket?, point: NetworkRegistry.TargetPoint?) {
        try {
            this.channels!!.get(Side.SERVER)!!
                .attr<FMLOutboundHandler.OutboundTarget?>(FMLOutboundHandler.FML_MESSAGETARGET)
                .set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT)
            this.channels!!.get(Side.SERVER)!!.attr<Any?>(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(point)
            this.channels!!.get(Side.SERVER)!!.writeOutbound(message)
        } catch (e: Exception) {
            GCLog.severe("Forge error when sending network packet to nearby players - this is not a Galacticraft bug, does another mod make fake players?")
            e.printStackTrace()
        }
    }

    /**
     * Send this message to everyone within the supplied dimension.
     *
     *
     * Adapted from CPW's code in
     * cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper
     *
     * @param message     The message to send
     * @param dimensionID The dimension id to target
     */
    open fun sendToDimension(message: IPacket?, dimensionID: Int) {
        try {
            this.channels!!.get(Side.SERVER)!!
                .attr<FMLOutboundHandler.OutboundTarget?>(FMLOutboundHandler.FML_MESSAGETARGET)
                .set(FMLOutboundHandler.OutboundTarget.DIMENSION)
            this.channels!!.get(Side.SERVER)!!.attr<Any?>(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(dimensionID)
            this.channels!!.get(Side.SERVER)!!.writeOutbound(message)
        } catch (e: Exception) {
            GCLog.severe("Forge error when sending network packet to all players in dimension - this is not a Galacticraft bug, does another mod make fake players?")
            e.printStackTrace()
        }
    }

    /**
     * Send this message to the server.
     *
     *
     * Adapted from CPW's code in
     * cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper
     *
     * @param message The message to send
     */
    open fun sendToServer(message: IPacket?) {
        if (FMLCommonHandler.instance().getSide() != Side.CLIENT) {
            return
        }
        this.channels!!.get(Side.CLIENT)!!
            .attr<FMLOutboundHandler.OutboundTarget?>(FMLOutboundHandler.FML_MESSAGETARGET)
            .set(FMLOutboundHandler.OutboundTarget.TOSERVER)
        this.channels!!.get(Side.CLIENT)!!.writeOutbound(message)
    }

    companion object {
        @JvmStatic
        fun init(): GalacticraftChannelHandler {
            val channelHandler: GalacticraftChannelHandler = GalacticraftChannelHandler()
            channelHandler.channels =
                NetworkRegistry.INSTANCE.newChannel(Constants.MOD_ID_CORE, channelHandler, GalacticraftPacketHandler())
            return channelHandler
        }
    }
}
