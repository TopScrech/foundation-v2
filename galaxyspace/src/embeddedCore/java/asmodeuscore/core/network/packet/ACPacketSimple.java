package asmodeuscore.core.network.packet;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import asmodeuscore.core.astronomy.dimension.world.data.FrozenStormSaveData;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import asmodeuscore.core.astronomy.SpaceData;
import asmodeuscore.core.astronomy.SpaceData.Engine_Type;
import asmodeuscore.core.astronomy.dimension.world.data.DustStormSaveData;
import asmodeuscore.core.astronomy.gui.screen.NewGuiCelestialSelection;
import asmodeuscore.core.event.AsmodeusClientEvent;
import asmodeuscore.core.handler.ColorBlockHandler;
import io.netty.buffer.ByteBuf;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.Satellite;
import micdoodle8.mods.galacticraft.api.galaxies.SolarSystem;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats;
import micdoodle8.mods.galacticraft.core.network.NetworkUtil;
import micdoodle8.mods.galacticraft.core.network.PacketSimple;
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket;
import micdoodle8.mods.galacticraft.core.tick.TickHandlerServer;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import micdoodle8.mods.galacticraft.core.util.GCLog;
import micdoodle8.mods.galacticraft.core.util.PlayerUtil;
import micdoodle8.mods.galacticraft.core.util.WorldUtil;
import micdoodle8.mods.galacticraft.core.wrappers.ScheduledDimensionChange;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ACPacketSimple extends PacketBase implements Packet<INetHandler>
{
    public static enum ACEnumSimplePacket
    {
        // SERVER
    	
    	 /**
         * Updates the gravity side of an artifical gravity block
         * - BlockVec3 pos: position of the block
         * - Integer gravityStrength
         */

        //CLIENT
        C_UPDATE_DIMENSION_LIST(Side.CLIENT, String.class, String.class, Boolean.class, Integer.class, Integer.class, Integer.class, Boolean.class),	
        C_UPDATE_OXYGEN_STATUS(Side.CLIENT, Boolean.class),
        C_UPDATE_WORLD(Side.CLIENT),
        C_GET_CELESTIAL_BODY_LIST(Side.CLIENT),
        C_UPDATE_STORM_USER(Side.CLIENT, String.class, Boolean.class, Integer.class, Integer.class, Float.class, Float.class, Integer.class),
        //SERVER
        S_TELEPORT_ENTITY(Side.SERVER, Integer.class, Integer.class);
        
        
        private Side targetSide;
        private Class<?>[] decodeAs;

        private ACEnumSimplePacket(Side targetSide, Class<?>... decodeAs)
        {
            this.targetSide = targetSide;
            this.decodeAs = decodeAs;
        }

        public Side getTargetSide()
        {
            return this.targetSide;
        }

        public Class<?>[] getDecodeClasses()
        {
            return this.decodeAs;
        }
    }

    private ACEnumSimplePacket type;
    private List<Object> data;
    static private String spamCheckString;

    public ACPacketSimple()
    {
    	super();
    }
    
    public ACPacketSimple(ACEnumSimplePacket packetType, int dimID, Object... data)
    {
        this(packetType, dimID, Arrays.asList(data));
    }

    public ACPacketSimple(ACEnumSimplePacket packetType, World world, Object[] data)
    {
        this(packetType, GCCoreUtil.getDimensionID(world), Arrays.asList(data));
    }
    
    public ACPacketSimple(ACEnumSimplePacket packetType, int dimID, List<Object> data)
    {
        super(dimID);
        if (packetType.getDecodeClasses().length != data.size())
        {
            GCLog.info("Simple Packet Core found data length different than packet type");
            new RuntimeException().printStackTrace();
        }

        this.type = packetType;
        this.data = data;
    }

    @Override
    public void encodeInto(ByteBuf buffer)
    {
    	super.encodeInto(buffer);
        buffer.writeInt(this.type.ordinal());

        try
        {
            NetworkUtil.encodeData(buffer, this.data);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void decodeInto(ByteBuf buffer)
    {
    	super.decodeInto(buffer);
        this.type = ACEnumSimplePacket.values()[buffer.readInt()];

        try
        {
            if (this.type.getDecodeClasses().length > 0)
            {
                this.data = NetworkUtil.decodeData(this.type.getDecodeClasses(), buffer);
            }
            if (buffer.readableBytes() > 0)
            {
                GCLog.severe("AsmodeusCore packet length problem for packet type " + this.type.toString());
            }
        }
        catch (Exception e)
        {
            System.err.println("[AsmodeusCore] Error handling simple packet type: " + this.type.toString() + " " + buffer.toString());
            e.printStackTrace();
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void handleClientSide(EntityPlayer player)
    {
    	EntityPlayerSP playerBaseClient = null;

        if (player instanceof EntityPlayerSP)
        {
            playerBaseClient = (EntityPlayerSP) player;
        }
        /*if(ACCompatibilityManager.isGalacticraftLoaded()) {
        	GCPlayerStatsClient stats = null;
        	stats = GCPlayerStatsClient.get(playerBaseClient);
        }*/
        /*else
        {/*
            if (type != EnumSimplePacket.C_UPDATE_SPACESTATION_LIST && type != EnumSimplePacket.C_UPDATE_PLANETS_LIST && type != EnumSimplePacket.C_UPDATE_CONFIGS)
            {
                return;
            }
        }*/
        switch (this.type)
        {
	        case C_UPDATE_STORM_USER:
	        	String name = (String) this.data.get(0);
	    		boolean isDustStorm = (boolean) this.data.get(1);
	    		int tickStorm = (int) this.data.get(2);
	    		int clearTick = (int) this.data.get(3);
	    		float prevStrenghtStorm = (float) this.data.get(4);
	    		float strenghtStorm = (float) this.data.get(5);
	    		int type_storm = (int) this.data.get(6);


                if(type_storm == 0) {
                    DustStormSaveData msd = DustStormSaveData.get(player.world, name);
                    msd.setDustStorm(isDustStorm);
                    msd.tickDustStorm = tickStorm;
                    msd.tickClearWeather = clearTick;
                    msd.prevStormStrength = prevStrenghtStorm;
                    msd.stormStrength = strenghtStorm;
                }

                if(type_storm == 1) {
                    FrozenStormSaveData msd = FrozenStormSaveData.get(player.world, name);
                    msd.setFrozenStorm(isDustStorm);
                    msd.tickStorm = tickStorm;
                    msd.tickClearWeather = clearTick;
                    msd.prevStormStrength = prevStrenghtStorm;
                    msd.stormStrength = strenghtStorm;
                }
	    		
	    		//player.world.getPerWorldStorage().setData(msd.mapName, msd);
	    		break;
       		case C_UPDATE_DIMENSION_LIST:
	            if (String.valueOf(this.data.get(0)).equals(PlayerUtil.getName(player)))
	            {
	                String dimensionList = (String) this.data.get(1);
	                if (ConfigManagerCore.enableDebug)
	                {
	                    if (!dimensionList.equals(ACPacketSimple.spamCheckString))
	                    {
	                        GCLog.info("DEBUG info: " + dimensionList);
	                        ACPacketSimple.spamCheckString = dimensionList;
	                    }
	                }
	                final String[] destinations = dimensionList.split("\\?");
	                List<CelestialBody> possibleCelestialBodies = Lists.newArrayList();
	                Map<Integer, Map<String, NewGuiCelestialSelection.StationDataGUI>> spaceStationData = Maps.newHashMap();
	//                Map<String, String> spaceStationNames = Maps.newHashMap();
	//                Map<String, Integer> spaceStationIDs = Maps.newHashMap();
	//                Map<String, Integer> spaceStationHomes = Maps.newHashMap();
	
	                for (String str : destinations)
	                {
	                    CelestialBody celestialBody = WorldUtil.getReachableCelestialBodiesForName(str);
	
	                    if (celestialBody == null && str.contains("$"))
	                    {
	                        String[] values = str.split("\\$");
	
	                        int homePlanetID = Integer.parseInt(values[4]);
	
	                        for (Satellite satellite : GalaxyRegistry.getRegisteredSatellites().values())
	                        {
	                            if (satellite.getParentPlanet().getDimensionID() == homePlanetID)
	                            {
	                                celestialBody = satellite;
	                                break;
	                            }
	                        }
	
	                        if (!spaceStationData.containsKey(homePlanetID))
	                        {
	                            spaceStationData.put(homePlanetID, new HashMap<String, NewGuiCelestialSelection.StationDataGUI>());
	                        }
	
	                        spaceStationData.get(homePlanetID).put(values[1], new NewGuiCelestialSelection.StationDataGUI(values[2], Integer.parseInt(values[3])));
	
	//                        spaceStationNames.put(values[1], values[2]);
	//                        spaceStationIDs.put(values[1], Integer.parseInt(values[3]));
	//                        spaceStationHomes.put(values[1], Integer.parseInt(values[4]));
	                    }
	
	                    if (celestialBody != null)
	                    {
	                        possibleCelestialBodies.add(celestialBody);
	                    }
	                }
	
	                if (FMLClientHandler.instance().getClient().world != null)
	                {	
	                	SpaceData data = new SpaceData();
                    	data.addToMap(data.rocket_tier, (int) this.data.get(3));
                    	data.addToMap(data.rocket_engine, Engine_Type.byID((int) this.data.get(4)));
						data.addToMap(data.rocket_count_fuel, (int) this.data.get(5));
						data.addToMap(data.second_passenger, (boolean) this.data.get(6));

	                    if (!(FMLClientHandler.instance().getClient().currentScreen instanceof NewGuiCelestialSelection))
	                    {
	                    	
	                    	
	                        NewGuiCelestialSelection gui = new NewGuiCelestialSelection(false, possibleCelestialBodies, (Boolean) this.data.get(2), data);
	                        gui.spaceStationMap = spaceStationData;
	//                        gui.spaceStationNames = spaceStationNames;
	//                        gui.spaceStationIDs = spaceStationIDs;
	                        FMLClientHandler.instance().getClient().displayGuiScreen(gui);
	                    }
	                    else
	                    {
	                        ((NewGuiCelestialSelection) FMLClientHandler.instance().getClient().currentScreen).possibleBodies = possibleCelestialBodies;
	                        ((NewGuiCelestialSelection) FMLClientHandler.instance().getClient().currentScreen).spaceStationMap = spaceStationData;
	                        ((NewGuiCelestialSelection) FMLClientHandler.instance().getClient().currentScreen).data = data;
	//                        ((GuiCelestialSelection) FMLClientHandler.instance().getClient().currentScreen).spaceStationNames = spaceStationNames;
	//                        ((GuiCelestialSelection) FMLClientHandler.instance().getClient().currentScreen).spaceStationIDs = spaceStationIDs;
	                    }
	                }
	            }
	            break;       
       		case C_UPDATE_WORLD:
       			
       			//FIXME: NOT WORK ON DEDICATED SERVERS!!!
        		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        		
        		//System.out.println(player.world);
        		if(server == null)
        			break;
        		
        		if(playerBaseClient == null)
        			break;
        		
         		World world = server.getWorld(this.getDimensionID());
         		if(world != null)
     			{	
         			ColorBlockHandler.world = world;
     			}
         		break;
       		case C_UPDATE_OXYGEN_STATUS:
        		AsmodeusClientEvent.inOxygenBlock = (Boolean)this.data.get(0);
        		break;        		
       		case C_GET_CELESTIAL_BODY_LIST:
                String str = "";
                
                List<Collection<? extends CelestialBody>> bodies = Arrays.asList(
                		GalaxyRegistry.getRegisteredPlanets().values(), 
                		GalaxyRegistry.getRegisteredMoons().values(), 
                		GalaxyRegistry.getRegisteredSatellites().values());
                
                for(Collection<? extends CelestialBody> body : bodies) {
                	for (CelestialBody cBody : body) {
                		
                		int size = ByteBufUtils.varIntByteCount(str.getBytes(StandardCharsets.UTF_8).length);
                		if(size >= 2 && size < 3)
                		{
                			GalacticraftCore.packetPipeline.sendToServer(new PacketSimple(EnumSimplePacket.S_COMPLETE_CBODY_HANDSHAKE, getDimensionID(), new Object[] { str }));
                            str = "";
                		}
                		
                		str = str.concat(cBody.getUnlocalizedName() + ";");             		
                		
                	}
                }                

                for (SolarSystem solarSystem : GalaxyRegistry.getRegisteredSolarSystems().values())
                {
                	int size = ByteBufUtils.varIntByteCount(str.getBytes(StandardCharsets.UTF_8).length);
            		if(size >= 2 && size < 3)
            		{
            			GalacticraftCore.packetPipeline.sendToServer(new PacketSimple(EnumSimplePacket.S_COMPLETE_CBODY_HANDSHAKE, getDimensionID(), new Object[] { str }));
                        str = "";
            		}
                    str = str.concat(solarSystem.getUnlocalizedName() + ";");
                }

                GalacticraftCore.packetPipeline.sendToServer(new PacketSimple(EnumSimplePacket.S_COMPLETE_CBODY_HANDSHAKE, getDimensionID(), new Object[] { str }));
                break;
        	default:
        		break;
        }
    }

    @Override
    public void handleServerSide(EntityPlayer player)
    {
        EntityPlayerMP playerBase = PlayerUtil.getPlayerBaseServerFromPlayer(player, false);

        if (playerBase == null)
        {
            return;
        }
        
        GCPlayerStats stats = GCPlayerStats.get(playerBase);
        TileEntity tileEntity;
        
        switch (this.type)
        {       
        	case S_TELEPORT_ENTITY:
        		stats.setFuelLevel((Integer) this.data.get(1));
        		TickHandlerServer.scheduleNewDimensionChange(new ScheduledDimensionChange(playerBase, (Integer) this.data.get(0)));
            	break;
        	default:
        		break;
        }
    }

	/*
     *
	 * BEGIN "net.minecraft.network.Packet" IMPLEMENTATION
	 * 
	 * This is for handling server->client packets before the player has joined the world
	 * 
	 */

    @Override
    public void readPacketData(PacketBuffer var1)
    {
        this.decodeInto(var1);
    }

    @Override
    public void writePacketData(PacketBuffer var1)
    {
        this.encodeInto(var1);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void processPacket(INetHandler var1)
    {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
        {
            this.handleClientSide(FMLClientHandler.instance().getClientPlayerEntity());
        }
    }

	/*
	 * 
	 * END "net.minecraft.network.Packet" IMPLEMENTATION
	 * 
	 * This is for handling server->client packets before the player has joined the world
	 * 
	 */
}
