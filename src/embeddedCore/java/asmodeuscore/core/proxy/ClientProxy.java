package asmodeuscore.core.proxy;

import asmodeuscore.core.event.AsmodeusClientEvent;
import asmodeuscore.core.handler.ColorBlockHandler;
import asmodeuscore.core.handler.GalaxyMapHandler;
import asmodeuscore.core.handler.ProviderFogHandler;
import asmodeuscore.core.prefab.entities.EntityAdvMeteor;
import asmodeuscore.core.utils.ACCompatibilityManager;
import micdoodle8.mods.galacticraft.core.client.render.entities.RenderMeteor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy{

	public static boolean enableInfoOnMap = true;
	public static boolean smallInfoOnMap = Minecraft.getMinecraft().gameSettings.guiScale == 0;
	public static boolean renderRingZones = true;
	
	@Override
	public void preload() 	
	{
		//register_event(new GalaxyMapHandler());
		if(ACCompatibilityManager.isGalacticraftLoaded())
			register_event(new AsmodeusClientEvent());	
		
		register_event(new ColorBlockHandler());
		register_event(new GalaxyMapHandler());
		register_event(new ProviderFogHandler());
		
		RenderingRegistry.registerEntityRenderingHandler(EntityAdvMeteor.class, (RenderManager manager) -> new RenderMeteor(manager));
	}
	
	@Override
	public void load() {}
	
	@Override
	public void postload() {
	}
	
	@Override
    public EntityPlayer getPlayerFromNetHandler(INetHandler handler)
    {
        if (handler instanceof NetHandlerPlayServer)
        {
            return ((NetHandlerPlayServer) handler).player;
        }
        else
        {
            return FMLClientHandler.instance().getClientPlayerEntity();
        }
    }
}
