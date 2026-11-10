package asmodeuscore.core.proxy

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.network.INetHandler
import net.minecraft.network.NetHandlerPlayServer
import net.minecraftforge.common.MinecraftForge

open class CommonProxy {
    open fun preload() {}
    open fun load() {}
    open fun postload() {}

    open fun register_event(obj: Any?) {
        MinecraftForge.EVENT_BUS.register(obj)
    }

    open fun getPlayerFromNetHandler(handler: INetHandler?): EntityPlayer? {
        if (handler is NetHandlerPlayServer) {
            return handler.player
        } else {
            return null
        }
    }

    open fun registerVariants() {}
}
