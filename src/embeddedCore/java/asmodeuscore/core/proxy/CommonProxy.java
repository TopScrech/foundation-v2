package asmodeuscore.core.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.common.MinecraftForge;

public class CommonProxy {

	public void preload() {}
	public void load() {}
	public void postload() {}
	
	public void register_event(Object obj)
	{
    	MinecraftForge.EVENT_BUS.register(obj);
	}
	
	public EntityPlayer getPlayerFromNetHandler(INetHandler handler)
    {
        if (handler instanceof NetHandlerPlayServer)
        {
            return ((NetHandlerPlayServer) handler).player;
        }
        else
        {
            return null;
        }
    }
	
	public void registerVariants() {}
}
