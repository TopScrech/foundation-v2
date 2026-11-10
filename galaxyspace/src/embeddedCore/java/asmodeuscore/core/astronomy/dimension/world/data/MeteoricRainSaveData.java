package asmodeuscore.core.astronomy.dimension.world.data;

import java.util.Random;

import asmodeuscore.api.dimension.IProviderWeather;
import asmodeuscore.core.astronomy.WeatherData.WeatherType;
import asmodeuscore.core.prefab.entities.EntityAdvMeteor;
import micdoodle8.mods.galacticraft.core.GCBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class MeteoricRainSaveData extends WorldSavedData {

	public int tickMeteoricRain = 0;
	public int tickClearWeather = 24000 * 5;
	
	public MeteoricRainSaveData(String name) {
		super(name);
	}

	public static MeteoricRainSaveData get(World world, String name) {
		name += "_meteoricrain";
		
		MapStorage storage = world.getPerWorldStorage();
		MeteoricRainSaveData instance = (MeteoricRainSaveData) storage.getOrLoadData(MeteoricRainSaveData.class, name);

		if (instance == null) {
			instance = new MeteoricRainSaveData(name);
			storage.setData(name, instance);
		}
		return instance;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		tickMeteoricRain = nbt.getInteger("tickMeteoricRain");
		tickClearWeather = nbt.getInteger("tickClearWeather");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setInteger("tickMeteoricRain", tickMeteoricRain);
		compound.setInteger("tickClearWeather", tickClearWeather);
		return compound;
	}	
	
	public boolean isMeteoricStorm()
	{
		return this.tickMeteoricRain > 0;
	}
	
	public static void calculateWeather(MeteoricRainSaveData data, Random rand) {
		
		if(!data.isMeteoricStorm()) {
			int clearWeather = data.tickClearWeather;

			if(clearWeather >= 0) {
				data.tickClearWeather--;
								
				if(clearWeather <= 0) {
					data.tickMeteoricRain = rand.nextInt(6000) + 6000;
					
				 	  
				}
			}
		}
		else {
			int meteoricRain = data.tickMeteoricRain;
			if(meteoricRain >= 0) {
				data.tickMeteoricRain--;					
				
				
				if(meteoricRain <= 0) {
					data.tickClearWeather = rand.nextInt(168000) + 12000;
				}
			}
		}
						 
		data.markDirty();
	}
	
	public static void spawnMeteor(MeteoricRainSaveData data) {
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		
		if(!data.isMeteoricStorm()) return;
		
		for(EntityPlayerMP player : server.getPlayerList().getPlayers()) {
			throwMeteors(player, 12, 3);
		}
	}
	
	private static void throwMeteors(EntityPlayerMP player, int freq, int chanceSpawnBlock)
	{
	    World world = player.world;
	    if (world.provider instanceof IProviderWeather && !world.isRemote)
	    {
	    	if (((IProviderWeather) world.provider).getWeather().getWeatherType() == WeatherType.METEORIC_RAIN)
	        {	    			   
	    		if (world.rand.nextInt(freq) == 0)
	    		{
	    			final EntityPlayer closestPlayer = world.getClosestPlayerToEntity(player, 100);

	    			if (closestPlayer == null || closestPlayer.getEntityId() <= player.getEntityId())
	    			{
	    				int x, y, z;
	    				double motX, motZ;
	    				x = world.rand.nextInt(60) - 30;
	    				y = world.rand.nextInt(20) + 200;
	    				z = world.rand.nextInt(60) - 30;
	    				motX = world.rand.nextDouble() * 1;
	    				motZ = world.rand.nextDouble() * 1;

	    				IBlockState block = null;
	    				if(chanceSpawnBlock > 0)
	    					block = world.rand.nextInt(100) <= chanceSpawnBlock ? GCBlocks.fallenMeteor.getDefaultState() : null;
	    				
	    				final EntityAdvMeteor meteor = new EntityAdvMeteor(world, player.posX + x, player.posY + y, player.posZ + z, motX - 0.5D, 0, motZ - 0.5D, world.rand.nextInt(3)).setSpawnBlock(block);
    				
	    				world.spawnEntity(meteor);
	    				
	    			}
	    		}	               
	        }
	    }
	 }
}
