package galaxyspace.core.network.packet

import io.netty.channel.ChannelHandlerContext
import galaxyspace.core.network.packet.GalaxySpacePacketHandler
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.SimpleChannelInboundHandler
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.network.IPacket
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.network.INetHandler
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.relauncher.Side
import java.lang.Exception

@Sharable
open class GalaxySpacePacketHandler : SimpleChannelInboundHandler<IPacket>() {
    @Throws(Exception::class)
    override fun channelRead0(ctx: ChannelHandlerContext, msg: IPacket) {
        val netHandler: INetHandler? = ctx.channel().attr<INetHandler?>(NetworkRegistry.NET_HANDLER).get()
        val player: EntityPlayer? = (GalacticraftCore.proxy)!!.getPlayerFromNetHandler(netHandler)

        when (FMLCommonHandler.instance().getEffectiveSide()) {
            Side.CLIENT -> msg.handleClientSide(player)
            Side.SERVER -> msg.handleServerSide(player)
            else -> {}
        }
    }
}
