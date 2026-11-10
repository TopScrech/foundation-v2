package asmodeuscore;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import asmodeuscore.api.IBodies;
import asmodeuscore.api.IBodiesHandler;
import asmodeuscore.core.astronomy.BodiesRegistry;
import asmodeuscore.core.astronomy.SpaceData;
import asmodeuscore.core.command.CommandFrozenStormToggle;
import asmodeuscore.core.command.CommandMeteoricRainToggle;
import asmodeuscore.core.command.CommandDustStormToggle;
import asmodeuscore.core.configs.AsmodeusConfig;
import asmodeuscore.core.event.AsmodeusEvent;
import asmodeuscore.core.handler.capabilities.ACCapabilityStatsHandler;
import asmodeuscore.core.network.packet.AsmodeusChannelHandler;
import asmodeuscore.core.prefab.entities.EntityAdvMeteor;
import asmodeuscore.core.proxy.CommonProxy;
import asmodeuscore.core.registers.potions.ACPotions;
import asmodeuscore.core.utils.Utils;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(
		   modid = AsmodeusCore.MODID,
		   version = AsmodeusCore.VERSION,
		   dependencies = "required-after:forge@[14.23.1.2555,); after:planetprogression@[0.4.6,); after:galacticraftcore@[4.0.2.261,]; after:galacticraftplanets;",
		   acceptedMinecraftVersions = "[1.12.2]",
		   name = AsmodeusCore.NAME,
		   guiFactory = "asmodeuscore.core.client.gui.ACConfigGuiFactory"
		)
public class AsmodeusCore {
	public static final int major_version = 1;
	public static final int minor_version = 0;
	public static final int build_version = 4;
	
	public static final String NAME = "AsmodeusCore";
	public static final String MODID = "asmodeuscore";
    public static final String VERSION = major_version + "." + minor_version + "." + build_version;
    
    public static final String ASSET_PREFIX = MODID;
    public static final String TEXTURE_PREFIX = ASSET_PREFIX + ":";
    
    public static AsmodeusChannelHandler packetPipeline;
    
    public static List<IBodies> bodies = new ArrayList<IBodies>();
    
    //---------------------------------------------
    
    @Instance(AsmodeusCore.MODID)
    public static AsmodeusCore INSTANCE;
    
    @SidedProxy(clientSide="asmodeuscore.core.proxy.ClientProxy", serverSide="asmodeuscore.core.proxy.CommonProxy")
    public static CommonProxy proxy;
    
    @EventHandler
    public void construct(FMLConstructionEvent event) 
    {    	
    	
    	for (ASMData data : event.getASMHarvestedData().getAll(IBodiesHandler.class.getName())) {
    		IBodies body;
    		try {
    			
    			body = (IBodies) Class.forName(data.getClassName()).newInstance();	
    			bodies.add(body);
    		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				e.printStackTrace();
			}
    	}  
    	
    	Collections.sort(bodies, new Comparator<IBodies>() {
            @Override
            public int compare(IBodies lhs, IBodies rhs) {
            	int x = rhs.getPriority();
            	int y = lhs.getPriority();
                return (x < y) ? -1 : ((x == y) ? 0 : 1);
            }
        });
    }
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) 
    {  
    	this.initModInfo(event.getModMetadata());
    	
    	new AsmodeusConfig(new File(event.getModConfigurationDirectory(), "AsmodeusCore/core.conf"));

    	ACCapabilityStatsHandler.register();
    	ACPotions.initialize();
    	    	   	
    	packetPipeline = AsmodeusChannelHandler.init();
    	
    	proxy.preload();
    	proxy.register_event(new AsmodeusEvent());
/*
		for(IBodies list : AsmodeusCore.bodies)
		{
			list.preInitialization(event);
			
    		if(list.canRegister()) 
    			list.preInit(event);
		}
		*/
		//Galaxies NEW_GALAXY = EnumHelper.addEnum(Galaxies.class, "NEW_GALAXY", new Class<?>[]{String.class}, "new_galaxy");
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    { 
    	proxy.load();
    	BodiesRegistry.registerCelestialBody(SpaceData.getRegisteredCelestialBodies());
    	SpaceData.refreshGalaxies();
    	
    	GCCoreUtil.registerGalacticraftNonMobEntity(EntityAdvMeteor.class, "adv_meteor", 150, 5, true);
    	/*
    	for(IBodies list : AsmodeusCore.bodies)
    		if(list.canRegister()) 
    			list.init(event);*/
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) 
    {
    	proxy.postload();  
    	/*
    	for(IBodies list : AsmodeusCore.bodies)
    		if(list.canRegister()) 
    			list.postInit(event);
    	*/
    }
    
    @EventHandler
    public void onServerStarting(FMLServerStartingEvent event)
    {
    	event.registerServerCommand(new CommandDustStormToggle());
    	event.registerServerCommand(new CommandFrozenStormToggle());
    	event.registerServerCommand(new CommandMeteoricRainToggle());
    }
    
    private void initModInfo(ModMetadata info)
    {
        info.autogenerated = false;
        info.modId = AsmodeusCore.MODID;
        info.name = AsmodeusCore.NAME;
        info.version = AsmodeusCore.VERSION;
        info.description = "Technical lib for BlesseNtumble (Vi[Told]) mods.";
        info.authorList = Arrays.asList("Vi[Told]");
    }
    
    @Mod.EventBusSubscriber(modid = AsmodeusCore.MODID)
    public static class RegistrationHandler
    {
    	@SubscribeEvent
        public static void registerBiomes(RegistryEvent.Register<Biome> event)
        {
    		event.getRegistry().register(Utils.WE_Biome);
    		event.getRegistry().register(Utils.WE_Biome_2);
    	
    		if (!ConfigManagerCore.disableBiomeTypeRegistrations)
    	    {
    			BiomeDictionary.addTypes(Utils.WE_Biome, BiomeDictionary.Type.COLD, BiomeDictionary.Type.DRY, BiomeDictionary.Type.DEAD);
    	    }
        }
    }

}
