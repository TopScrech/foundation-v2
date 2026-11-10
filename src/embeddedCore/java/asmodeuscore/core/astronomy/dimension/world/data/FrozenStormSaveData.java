package asmodeuscore.core.astronomy.dimension.world.data;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

import java.util.Random;

public class FrozenStormSaveData extends WorldSavedData {

	private boolean isStorm;
	public float prevStormStrength, stormStrength = 0.0F;
	public int tickStorm = 0;
	public int tickClearWeather = 24000 * 5;

	public FrozenStormSaveData(String name) {
		super(name);
	}
	
	public static FrozenStormSaveData get(World world, String name) {
		name += "_frozenstorm";
		MapStorage storage = world.getPerWorldStorage();
		FrozenStormSaveData instance = (FrozenStormSaveData) storage.getOrLoadData(FrozenStormSaveData.class, name);

		if (instance == null) {
			instance = new FrozenStormSaveData(name);
			storage.setData(name, instance);
		}
		return instance;
	}	
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		isStorm = nbt.getBoolean("isFrozenStorm");
		tickStorm = nbt.getInteger("tickFrozenStorm");
		tickClearWeather = nbt.getInteger("tickClearWeather");
		prevStormStrength = nbt.getFloat("prevStormStrength");
		stormStrength = nbt.getFloat("stormStrength");			
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setBoolean("isFrozenStorm", isStorm);
		compound.setInteger("tickFrozenStorm", tickStorm);
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
	
	public boolean isFrozenStorm()
	{
		return this.isStorm;
	}
	
	public void setFrozenStorm(boolean status)
	{
		this.isStorm = status;
	}
	
	public static void calculateWeather(FrozenStormSaveData data, Random rand) {
		data.prevStormStrength = data.stormStrength;
		
		if(!data.isFrozenStorm()) {
			int clearWeather = data.tickClearWeather;

			if(clearWeather >= 0) {
				data.tickClearWeather--;
				if (data.stormStrength > 0.0F) 
					data.stormStrength = (float)((double)data.stormStrength - 0.01D);
				
				if(clearWeather <= 0) {
					data.setFrozenStorm(true);
					data.tickStorm = rand.nextInt(6000) + 12000;
					
				 	  
				}
			}
		}
		else {
			int stormDust = data.tickStorm;
			if(stormDust >= 0) {
				data.tickStorm--;
				data.stormStrength = (float)((double)data.stormStrength + 0.01D);
				
				if(stormDust <= 0) {
					data.setFrozenStorm(false);
					data.tickClearWeather = rand.nextInt(168000) + 12000;							
					    
				}
			}
		}
		
		data.stormStrength = MathHelper.clamp(data.stormStrength, 0.0F, 1.0F);					 
		data.markDirty();
	}
}
