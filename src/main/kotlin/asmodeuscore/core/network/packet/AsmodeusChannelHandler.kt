package asmodeuscore.core.network.packet

import asmodeuscore.AsmodeusCore
import asmodeuscore.core.network.packet.ACPacketSimple
import io.netty.buffer.ByteBuf
import java.util.EnumMap
import io.netty.channel.ChannelHandlerContext
import asmodeuscore.core.network.packet.AsmodeusChannelHandler
import asmodeuscore.core.network.packet.AsmodeusPacketHandler
import asmodeuscore.api.network.IMessage
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.network.FMLEmbeddedChannel
import net.minecraftforge.fml.common.network.FMLIndexedMessageToMessageCodec
import net.minecraftforge.fml.common.network.FMLOutboundHandler
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.relauncher.Side
import java.lang.Exception

open class AsmodeusChannelHandler private constructor() : FMLIndexedMessageToMessageCodec<IMessage>() {
    private var channels: EnumMap<Side?, FMLEmbeddedChannel?>? = null

    init {
        this.addDiscriminator(0, ACPacketSimple::class.java)
    }

    @Throws(Exception::class)
    override fun encodeInto(ctx: ChannelHandlerContext?, msg: IMessage, target: ByteBuf?) {
        msg.encodeInto(target)
    }

    override fun decodeInto(ctx: ChannelHandlerContext?, source: ByteBuf?, msg: IMessage) {
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
    open fun sendToAll(message: IMessage?) {
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
    open fun sendTo(message: IMessage?, player: EntityPlayerMP?) {
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
     * [cpw.mods.fml.common.network.NetworkRegistry.TargetPoint]
     * around which to send
     */
    open fun sendToAllAround(message: IMessage?, point: NetworkRegistry.TargetPoint?) {
        try {
            this.channels!!.get(Side.SERVER)!!
                .attr<FMLOutboundHandler.OutboundTarget?>(FMLOutboundHandler.FML_MESSAGETARGET)
                .set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT)
            this.channels!!.get(Side.SERVER)!!.attr<Any?>(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(point)
            this.channels!!.get(Side.SERVER)!!.writeOutbound(message)
        } catch (e: Exception) {
            println("Forge error when sending network packet to nearby players - this is not a Galacticraft bug, does another mod make fake players?")
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
     * @param dimensionId The dimension id to target
     */
    open fun sendToDimension(message: IMessage?, dimensionId: Int) {
        try {
            this.channels!!.get(Side.SERVER)!!
                .attr<FMLOutboundHandler.OutboundTarget?>(FMLOutboundHandler.FML_MESSAGETARGET)
                .set(FMLOutboundHandler.OutboundTarget.DIMENSION)
            this.channels!!.get(Side.SERVER)!!.attr<Any?>(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(dimensionId)
            this.channels!!.get(Side.SERVER)!!.writeOutbound(message)
        } catch (e: Exception) {
            println("Forge error when sending network packet to all players in dimension - this is not a Galacticraft bug, does another mod make fake players?")
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
    open fun sendToServer(message: IMessage?) {
        if (FMLCommonHandler.instance().getSide() != Side.CLIENT) return
        this.channels!!.get(Side.CLIENT)!!
            .attr<FMLOutboundHandler.OutboundTarget?>(FMLOutboundHandler.FML_MESSAGETARGET)
            .set(FMLOutboundHandler.OutboundTarget.TOSERVER)
        this.channels!!.get(Side.CLIENT)!!.writeOutbound(message)
    }

    companion object {
        @JvmStatic
        fun init(): AsmodeusChannelHandler {
            val channelHandler: AsmodeusChannelHandler = AsmodeusChannelHandler()
            channelHandler.channels =
                NetworkRegistry.INSTANCE.newChannel(AsmodeusCore.MODID, channelHandler, AsmodeusPacketHandler())
            return channelHandler
        }
    }
}
