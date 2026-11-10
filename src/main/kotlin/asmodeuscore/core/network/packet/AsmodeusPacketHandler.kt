package asmodeuscore.core.network.packet

import asmodeuscore.AsmodeusCore
import io.netty.channel.ChannelHandlerContext
import asmodeuscore.core.network.packet.AsmodeusPacketHandler
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.SimpleChannelInboundHandler
import asmodeuscore.api.network.IMessage
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.network.INetHandler
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.relauncher.Side
import java.lang.Exception

@Sharable
open class AsmodeusPacketHandler : SimpleChannelInboundHandler<IMessage>() {
    @Throws(Exception::class)
    override fun channelRead0(ctx: ChannelHandlerContext, msg: IMessage) {
        val netHandler: INetHandler? = ctx.channel().attr<INetHandler?>(NetworkRegistry.NET_HANDLER).get()
        val player: EntityPlayer? = (AsmodeusCore.proxy)!!.getPlayerFromNetHandler(netHandler)

        when (FMLCommonHandler.instance().getEffectiveSide()) {
            Side.CLIENT -> msg.handleClientSide(player)
            Side.SERVER -> msg.handleServerSide(player)
            else -> {}
        }
    }
}
