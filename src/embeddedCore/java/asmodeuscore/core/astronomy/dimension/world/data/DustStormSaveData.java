package asmodeuscore.core.astronomy.dimension.world.data;

import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

public class DustStormSaveData extends WorldSavedData {

	private boolean isDustStorm;
	public float prevStormStrength, stormStrength = 0.0F;
	public int tickDustStorm = 0;
	public int tickClearWeather = 24000 * 3;
	
	public DustStormSaveData(String name) {
		super(name);
	}
	
	public static DustStormSaveData get(World world, String name) {
		name += "_stormdust";
		MapStorage storage = world.getPerWorldStorage();
		DustStormSaveData instance = (DustStormSaveData) storage.getOrLoadData(DustStormSaveData.class, name);

		if (instance == null) {
			instance = new DustStormSaveData(name);
			storage.setData(name, instance);
		}
		return instance;
	}	
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		isDustStorm = nbt.getBoolean("isDustStorm");
		tickDustStorm = nbt.getInteger("tickDustStorm");
		tickClearWeather = nbt.getInteger("tickClearWeather");
		prevStormStrength = nbt.getFloat("prevStormStrength");
		stormStrength = nbt.getFloat("stormStrength");			
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setBoolean("isDustStorm", isDustStorm);
		compound.setInteger("tickDustStorm", tickDustStorm);
		compound.setInteger("tickClearWeather", tickClearWeather);
		compound.setFloat("prevStormStrength", prevStormStrength);
		compound.setFloat("stormStrength", stormStrength);
		return compound;
	}
	
	public float getStormStrength(float delta)
    {
        return this.prevStormStrength + (this.stormStrength - this.prevStormStrength) * delta;
    }
	
	public void setStormStrength(float delta)
	{
		this.stormStrength = delta;
	}
	
	public boolean isDustStorm()
	{
		return this.isDustStorm;
	}
	
	public void setDustStorm(boolean status)
	{
		this.isDustStorm = status;
	}
	
	public static void calculateWeather(DustStormSaveData data, Random rand) {
		data.prevStormStrength = data.stormStrength;
		
		if(!data.isDustStorm()) {
			int clearWeather = data.tickClearWeather;

			if(clearWeather >= 0) {
				data.tickClearWeather--;
				if (data.stormStrength > 0.0F) 
					data.stormStrength = (float)((double)data.stormStrength - 0.01D);
				
				if(clearWeather <= 0) {
					data.setDustStorm(true);
					data.tickDustStorm = rand.nextInt(24000) + 12000;
					
				 	  
				}
			}
		}
		else {
			int stormDust = data.tickDustStorm;
			if(stormDust >= 0) {
				data.tickDustStorm--;						
				data.stormStrength = (float)((double)data.stormStrength + 0.01D);
				
				if(stormDust <= 0) {
					data.setDustStorm(false);
					data.tickClearWeather = rand.nextInt(168000) + 12000;							
					    
				}
			}
		}
		
		data.stormStrength = MathHelper.clamp(data.stormStrength, 0.0F, 1.0F);					 
		data.markDirty();
	}
}
