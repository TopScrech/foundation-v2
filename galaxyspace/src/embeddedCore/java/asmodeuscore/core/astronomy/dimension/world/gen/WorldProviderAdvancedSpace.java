package asmodeuscore.core.astronomy.dimension.world.gen;

import asmodeuscore.api.dimension.IAdvancedSpace;
import asmodeuscore.api.space.IExBody;
import asmodeuscore.core.astronomy.BodiesData;
import asmodeuscore.core.astronomy.BodiesRegistry;
import galaxyspace.core.configs.GSConfigCore;
import micdoodle8.mods.galacticraft.api.galaxies.IChildBody;
import micdoodle8.mods.galacticraft.api.galaxies.Moon;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.WorldProviderSpace;
import micdoodle8.mods.galacticraft.api.world.IExitHeight;
import micdoodle8.mods.galacticraft.api.world.ISolarLevel;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import net.minecraft.util.math.MathHelper;

public abstract class WorldProviderAdvancedSpace extends WorldProviderSpace implements ISolarLevel, IExitHeight, IAdvancedSpace {
    
	private BodiesData data = BodiesRegistry.getData(this.getCelestialBody());
	
	@Override
    public float getGravity() {
		
		if(this.getCelestialBody() instanceof IExBody)
			return ((IExBody)this.getCelestialBody()).getGravity();
		
        return 0.0058F;
    }

	@Override
	public float getThermalLevelModifier()
	{
		if(enableAdvancedThermalLevel() && this.world != null) {		
		    float angle = this.world.getCelestialAngle(this.getDayLength());		    
		    float value = MathHelper.cos(angle * (float) Math.PI * 2.0F) * getThermalValueMod();
		    
		    float def = this.getCelestialBody().atmosphere.thermalLevel();
		    if(def < 0.0) value *= -1;
		    
		    if(def == 0) return value;
		    if(value * def + def < -9.102F) return -9.102F;
		    
		    return def == 0 ? value : value * def + def;
		}		
	    return this.getCelestialBody().atmosphere.thermalLevel();
	}

	protected float getThermalValueMod()
	{
		return 0.5F;
	}
	
	@Override
    public long getDayLength() {
		
		if(this.getCelestialBody() instanceof IExBody)
			return ((IExBody)this.getCelestialBody()).getDayLength();
		
        return 24000L;
    }
   
    @Override
    public boolean canSpaceshipTierPass(int tier) {
        return tier >= this.getCelestialBody().getTierRequirement();
    }
    
	public float getSolarSizeForMoon()
	{
	    return 1.0F / ((Moon)this.getCelestialBody()).getParentPlanet().getRelativeDistanceFromCenter().unScaledDistance;
	}

	@Override
	public double getSolarEnergyMultiplier()
	{
		double solarMultiplier = -0.5D;
		if (solarMultiplier < 0D)
		{
			double s = this.getCelestialBody() instanceof IChildBody ? this.getSolarSizeForMoon() : this.getSolarSize();
			solarMultiplier += s * s * ConfigManagerCore.spaceStationEnergyScalar;
		}
		return solarMultiplier;
	}
	
	@Override
	public double getSolarWindMultiplier() 
	{
		double solarMultiplier = -1D;
		if (solarMultiplier < 0D)
		{
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
    public int getMoonPhase(long time)
    {
    	long timeday = this.getDayLength() > 0 ? this.getDayLength() : 1;
        return (int)(time / timeday % 8L + 8L) % 8;
    }

	public boolean enableAdvancedThermalLevel() {
		return GSConfigCore.enableAdvancedThermalSystem;
	}
	
}
