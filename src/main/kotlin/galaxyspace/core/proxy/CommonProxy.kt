package galaxyspace.core.proxy

import net.minecraftforge.client.event.TextureStitchEvent.Pre

import micdoodle8.mods.galacticraft.api.vector.Vector3
import net.minecraft.block.Block
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.Item
import net.minecraft.network.INetHandler
import net.minecraft.network.NetHandlerPlayServer
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.client.event.TextureStitchEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

open class CommonProxy {
    open fun preload(event: FMLPreInitializationEvent?) {
        //GameRegistry.registerFuelHandler(new GSFuelHandler());
    }


    open fun load() {
    }


    open fun postload() {
    }

    open fun registerItemRenderer(item: Item?, meta: Int, id: String?) {}

    open fun registerVariants() {
    }

    open fun getServerTile(source: TileEntity?): TileEntity? {
        return source
    }

    open fun getBlockRender(block: Block?): Int {
        return 0
    }

    open fun register_event(obj: Any?) {
        MinecraftForge.EVENT_BUS.register(obj)
    }


    open fun spawnParticle(particleID: String?, position: Vector3?, motion: Vector3?, otherInfo: Array<Any?>?) {
    }

    open fun getPlayerFromNetHandler(handler: INetHandler?): EntityPlayer? {
        if (handler is NetHandlerPlayServer) {
            return handler.player
        } else {
            return null
        }
    }


    open fun registerRender() {}


    open fun registerTexture(event: TextureStitchEvent.Pre?, texture: String?) {
    }

    open fun resetPlayerInAirTime(player: EntityPlayer?) {
        if (player is EntityPlayerMP) {
            player.connection.floatingTickCount = 0
            //ObfuscationReflectionHelper.setPrivateValue(NetHandlerPlayServer.class, ((EntityPlayerMP)player).connection, Integer.valueOf(0), new String[]{"field_147365_f", "floatingTickCount"});
        }
    }
}
