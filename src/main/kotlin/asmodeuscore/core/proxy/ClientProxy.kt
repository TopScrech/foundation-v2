package asmodeuscore.core.proxy

import asmodeuscore.core.utils.ACCompatibilityManager
import asmodeuscore.core.event.AsmodeusClientEvent
import asmodeuscore.core.handler.ColorBlockHandler
import asmodeuscore.core.handler.ProviderFogHandler
import asmodeuscore.core.prefab.entities.EntityAdvMeteor
import asmodeuscore.core.handler.GalaxyMapHandler
import micdoodle8.mods.galacticraft.core.client.render.entities.RenderMeteor
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.network.INetHandler
import net.minecraft.network.NetHandlerPlayServer
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.client.registry.IRenderFactory
import net.minecraftforge.fml.client.registry.RenderingRegistry

open class ClientProxy : CommonProxy() {
    override fun preload() {
        //register_event(new GalaxyMapHandler());
        if (ACCompatibilityManager.isGalacticraftLoaded()) register_event(AsmodeusClientEvent())

        register_event(ColorBlockHandler())
        register_event(GalaxyMapHandler())
        register_event(ProviderFogHandler())

        RenderingRegistry.registerEntityRenderingHandler<EntityAdvMeteor>(
            EntityAdvMeteor::class.java,
            IRenderFactory { manager: RenderManager? -> RenderMeteor((manager)!!) })
    }

    override fun load() {}

    override fun postload() {
    }

    override fun getPlayerFromNetHandler(handler: INetHandler?): EntityPlayer? {
        if (handler is NetHandlerPlayServer) {
            return handler.player
        } else {
            return FMLClientHandler.instance().getClientPlayerEntity()
        }
    }

    companion object {
        @JvmField
        var enableInfoOnMap: Boolean = true

        @JvmField
        var smallInfoOnMap: Boolean = Minecraft.getInstance().gameSettings.guiScale == 0

        @JvmField
        var renderRingZones: Boolean = true
    }
}
