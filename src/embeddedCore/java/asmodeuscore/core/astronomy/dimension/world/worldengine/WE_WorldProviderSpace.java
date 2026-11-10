package asmodeuscore.core.astronomy.dimension.world.worldengine;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import asmodeuscore.api.dimension.IAdvancedSpace;
import asmodeuscore.api.dimension.IProviderFreeze;
import asmodeuscore.api.space.IExBody;
import asmodeuscore.core.astronomy.WeatherData;
import asmodeuscore.core.astronomy.WeatherData.WeatherType;
import asmodeuscore.core.utils.worldengine.WE_Biome;
import asmodeuscore.core.utils.worldengine.WE_WorldProvider;
import galaxyspace.core.configs.GSConfigCore;
import micdoodle8.mods.galacticraft.api.galaxies.IChildBody;
import micdoodle8.mods.galacticraft.api.galaxies.Moon;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeAdaptive;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeProviderDefault;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.api.world.EnumAtmosphericGas;
import micdoodle8.mods.galacticraft.api.world.IExitHeight;
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import micdoodle8.mods.galacticraft.api.world.ISolarLevel;
import micdoodle8.mods.galacticraft.core.event.EventHandlerGC;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import micdoodle8.mods.galacticraft.core.util.JavaUtil;
import net.minecraft.block.Block;
import net.minecraft.command.CommandTime;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.VillageCollection;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class WE_WorldProviderSpace extends WE_WorldProvider
		implements IGalacticraftWorldProvider, ISolarLevel, IExitHeight, IAdvancedSpace, IProviderFreeze {

	private long timeCurrentOffset = 0L;
	public long preTickTime = Long.MIN_VALUE;
	private long saveTCO = 0L;
	static Field tickCounter;
	
	static {
		try {
			tickCounter = VillageCollection.class
					.getDeclaredField(GCCoreUtil.isDeobfuscated() ? "tickCounter" : "field_75553_e");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	@Override
	public void init() {
		//super.init();
		
		this.hasSkyLight = true;
		if (this.getBiomeProviderClass() != null) {
			try {
				Class<? extends BiomeProvider> biomeProviderClass = this.getBiomeProviderClass();

				Constructor<?>[] constructors = biomeProviderClass.getConstructors();
				for (Constructor<?> constr : constructors) {
					System.out.println(constr.toString());
					if (Arrays.equals(constr.getParameterTypes(), new Object[] { World.class })) {
						this.biomeProvider = (BiomeProvider) constr.newInstance(this.world);
					} else if (constr.getParameterTypes().length == 0) {
						this.biomeProvider = (BiomeProvider) constr.newInstance();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			this.biomeProvider = new BiomeProviderDefault(this.getCelestialBody());
		}
				
		System.out.println("Init WE for: " + this.getCelestialBody().getName());
		System.out.println("Biome Provider: " + this.biomeProvider.toString());
		
		
	}
	
	@Override
	public IChunkGenerator createChunkGenerator() {
		System.out.println("WorldEngine: -Starting WorldEngine..."          );
		chunk_provider = new WE_ChunkProviderSpace(this);
		System.out.println("WorldEngine: -WorldEngine started successfully!");
		//-//
		return chunk_provider;
	}

	@Override
	public void updateWeather() {
		if (!this.world.isRemote) {
			long newTime = world.getWorldInfo().getWorldTime();
			if (this.preTickTime == Long.MIN_VALUE) {
				// First tick: get the timeCurrentOffset from saved ticks in villages.dat :)
				int savedTick = 0;
				try {
					tickCounter.setAccessible(true);
					savedTick = tickCounter.getInt(this.world.villageCollection);
					if (savedTick < 0)
						savedTick = 0;
				} catch (Exception ignore) {
				}
				this.timeCurrentOffset = savedTick - newTime;
			} else {
				// Detect jumps in world time (e.g. because of bed use on Overworld) and reverse
				// them for this world
				long diff = (newTime - this.preTickTime);
				if (diff > 1L) {
					this.timeCurrentOffset -= diff - 1L;
					this.saveTime();
				}
			}
			this.preTickTime = newTime;
			this.saveTCO = 0L;
		}

		if (this.shouldDisablePrecipitation()) {
			this.world.getWorldInfo().setRainTime(0);
			this.world.getWorldInfo().setRaining(false);
			this.world.getWorldInfo().setThunderTime(0);
			this.world.getWorldInfo().setThundering(false);
			this.world.rainingStrength = 0.0F;
			this.world.thunderingStrength = 0.0F;
		} else {
			this.updateWeatherOverride();
		}
	}

	@Override
	public float getGravity() {

		if (this.getCelestialBody() instanceof IExBody)
			return ((IExBody) this.getCelestialBody()).getGravity();

		return 0.058F;
	}

	@Override
	public float getThermalLevelModifier() {
		if (enableAdvancedThermalLevel() && this.world != null) {
			float angle = this.world.getCelestialAngle(this.getDayLength());
			float value = MathHelper.cos(angle * (float) Math.PI * 2.0F) * getThermalValueMod();

			float def = this.getCelestialBody().atmosphere.thermalLevel();
			if (def < 0.0)
				value *= -1;

		    if(value * def + def < -9.102F) return -9.102F;
		    
			return def == 0 ? value : value * def + def;
		}
		return this.getCelestialBody().atmosphere.thermalLevel();
	}
	
	@Override
	public boolean canSpaceshipTierPass(int tier) {
		return tier >= this.getCelestialBody().getTierRequirement();
	}

	public float getSolarSizeForMoon() {
		return 1.0F	/ ((Moon) this.getCelestialBody()).getParentPlanet().getRelativeDistanceFromCenter().unScaledDistance;
	}

	@Override
	public double getSolarEnergyMultiplier() {
		double solarMultiplier = -1D;
		if (solarMultiplier < 0D) {
			double s = this.getCelestialBody() instanceof IChildBody ? this.getSolarSizeForMoon() : this.getSolarSize();
			solarMultiplier += s * s * ConfigManagerCore.spaceStationEnergyScalar;
		}
		return solarMultiplier;
	}

	@Override
	public double getSolarWindMultiplier() {
		double solarMultiplier = -1D;
		if (solarMultiplier < 0D) {
			double s = this.getCelestialBody() instanceof IChildBody ? this.getSolarSizeForMoon() : this.getSolarSize();
			solarMultiplier = s * s;
		}
		return solarMultiplier;
	}

	@Override
	public double getYCoordinateToTeleport() {
		return 1000.0D;
	}
	
	@Override
	public int getMoonPhase(long time) {
		long timeday = this.getDayLength() > 0 ? this.getDayLength() : 1;
		return (int) (time / timeday % 8L + 8L) % 8;
	}
	
	@Override
	public String getSaveFolder() {
		return "DIM" + this.getCelestialBody().getDimensionID();
	}

	@Override
	public boolean isGasPresent(EnumAtmosphericGas gas) {
		return this.getCelestialBody().atmosphere.isGasPresent(gas);
	}

	@Override
	public boolean hasNoAtmosphere() {
		return this.getCelestialBody().atmosphere.hasNoGases();
	}

	@Override
	public boolean hasBreathableAtmosphere() {
		return this.getCelestialBody().atmosphere.isBreathable();
	}

	@Override
	public boolean shouldDisablePrecipitation() {
		return !this.getCelestialBody().atmosphere.hasPrecipitation();
	}

	@Override
	public float getSoundVolReductionAmount() {
		float d = this.getCelestialBody().atmosphere.relativeDensity();
		if (d <= 0.0F) {
			return 20.0F;
		}
		if (d > 5.0F) {
			return 0.2F;
		}
		return 1.0F / d;
	}

	@Override
	public double getMeteorFrequency() {
		float d = this.getCelestialBody().atmosphere.relativeDensity();
		if (d <= 0.0F) {
			return 5.0D;
		}
		return d * 100D;
	}

	@Override
	public float getWindLevel() {
		return this.getCelestialBody().atmosphere.windLevel();
	}

	@Override
	public boolean shouldCorrodeArmor() {
		return this.getCelestialBody().atmosphere.isCorrosive();
	}

	@Override
	public boolean canDoLightning(Chunk chunk) {
		return !this.shouldDisablePrecipitation();
	}

	@Override
	public boolean canDoRainSnowIce(Chunk chunk) {
		return !this.shouldDisablePrecipitation();
	}
	    
	@Override
    public float getSolarSize()
    {
        return 1.0F / this.getCelestialBody().getRelativeDistanceFromCenter().unScaledDistance;
    }
	
	@Override
    public float[] calcSunriseSunsetColors(float var1, float var2)
    {
        return this.hasSunset() ? super.calcSunriseSunsetColors(var1, var2) : null;
    }
	
	public boolean enableAdvancedThermalLevel() {
		return GSConfigCore.enableAdvancedThermalSystem;
	}

	protected float getThermalValueMod() {
		return 0.5F;
	}

	public long getDayLength() {

		if (this.getCelestialBody() instanceof IExBody)
			return ((IExBody) this.getCelestialBody()).getDayLength();

		return 24000L;
	}

	@Override
	public float calculateCelestialAngle(long par1, float par3) {
		
		par1 = this.getWorldTime();		
		long dayLen = this.getDayLength();
		if(dayLen == 0)
			dayLen = 1;
		int j = (int) (par1 % dayLen);
		float f1 = (j + par3) / dayLen - 0.25F;

		if (f1 < 0.0F) {
			++f1;
		}

		if (f1 > 1.0F) {
			--f1;
		}

		float f2 = 1.0F - (float)((Math.cos((double)f1 * Math.PI) + 1.0D) / 2.0D);
	    f1 = f1 + (f2 - f1) / 3.0F;
	    return f1;

	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public Vec3d getFogColor(float var1, float var2) {
		Vector3 fogColor = this.getFogColor();
		return new Vec3d(fogColor.floatX(), fogColor.floatY(), fogColor.floatZ());
	}

	@Override
	public Vec3d getSkyColor(Entity cameraEntity, float partialTicks) {
		Vector3 skyColor = this.getSkyColor();
		return new Vec3d(skyColor.floatX(), skyColor.floatY(), skyColor.floatZ());
	}

	@Override
    public boolean isSkyColored()
    {
        return true;
    }
	
	/**
     * Do not override this.
     * 
     * Returns true on clients (to allow rendering of sky etc, maybe even clouds).
     * Returns false on servers (to disable Nether Portal mob spawning and sleeping in beds).
     */
	@Override
    public boolean isSurfaceWorld()
    {
        return (this.world == null) ? false : true; //this.world.isRemote;
    }
	
	@Override
    public boolean canRespawnHere()
    {
        if (EventHandlerGC.bedActivated)
        {
            EventHandlerGC.bedActivated = false;
            return true;
        }
        return false;
    }
	
	/**
     * Do NOT override this in your add-ons.
     * 
     * This controls whether the player will respawn in the space dimension or the Overworld
     * in accordance with the 'Force Overworld Respawn' setting on core.conf.
     */
	@Override
    public int getRespawnDimension(EntityPlayerMP player)
    {
        return this.shouldForceRespawn() ? this.getDimension() : 0;
    }
	
	public boolean shouldForceRespawn()
    {
        return !ConfigManagerCore.forceOverworldRespawn;
    }

	@Override
	public boolean netherPortalsOperational() {
		return false;
	}

	@Override
	public float getArrowGravity() {
		return 0.005F;
	}
	
	public Class<? extends BiomeProvider> getBiomeProviderClass()
    {
        return WE_BiomeProvider.class;
    }
	
	@Override
    public BiomeProvider getBiomeProvider()
    {
        if (this.getBiomeProviderClass() == null)
        {
            BiomeAdaptive.setBody(this.getCelestialBody());
        }
        return biomeProvider;
    }
	
	@Override
    public boolean shouldMapSpin(String entity, double x, double y, double z)
    {
        return false;
    }
	
	@Override
    public void setWorldTime(long time)
    {
        world.getWorldInfo().setWorldTime(time);
        if (!world.isRemote)
        {
            if (JavaUtil.instance.isCalledBy(CommandTime.class))
            {
                this.timeCurrentOffset = this.saveTCO;
                this.saveTime();
                this.preTickTime = time;
            }
            else
            {
                long newTCO = time - world.getWorldInfo().getWorldTime();
                long diff = newTCO - this.timeCurrentOffset;
                if (diff > 1L || diff < -1L)
                {
                    this.timeCurrentOffset = newTCO; 
                    this.saveTime();
                    this.preTickTime = time;
                }
            }
            this.saveTCO = 0;
        }
    }
	
	@Override
    public long getWorldTime()
    {
        if (JavaUtil.instance.isCalledBy(CommandTime.class))
        {
            this.saveTCO  = this.timeCurrentOffset;
        }
        return world.getWorldInfo().getWorldTime() + this.timeCurrentOffset;
    }
	
	public void adjustTimeOffset(long diff) {
		if (diff != 0L) {
			this.timeCurrentOffset -= diff;
			this.preTickTime += diff;
			this.saveTime();
		}
	}

	public void adjustTime(long newTime) {
		long diff = newTime - this.preTickTime;
		if (diff != 0L) {
			this.timeCurrentOffset -= diff;
			this.preTickTime = newTime;
			this.saveTime();
		}
	}

	private void saveTime() {
		try {
			VillageCollection vc = this.world.villageCollection;
			tickCounter.setAccessible(true);
			tickCounter.setInt(vc, (int) (this.getWorldTime()));
			vc.markDirty();
		} catch (Exception ignore) {
		}
	}
	
    protected void updateWeatherOverride()
    {
        super.updateWeather();
    }
    
    @Override
	public List<Block> getSurfaceBlocks() {
		return null;
	}
	
	public abstract boolean hasSunset();
	public abstract Vector3 getFogColor();
	public abstract Vector3 getSkyColor();
	public abstract Class<? extends IChunkGenerator> getChunkProviderClass();
	
	public abstract void onChunkProvider(int cX, int cZ, ChunkPrimer primer);
	public abstract void onPopulate(int cX, int cZ);
	public abstract void recreateStructures(Chunk chunkIn, int x, int z);
}
