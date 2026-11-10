package asmodeuscore.core.prefab.celestialbody;

import asmodeuscore.api.dimension.IAdvancedSpace.ClassBody;
import asmodeuscore.api.dimension.IAdvancedSpace.TypeBody;
import asmodeuscore.api.space.IExBody;
import micdoodle8.mods.galacticraft.api.galaxies.Moon;
import micdoodle8.mods.galacticraft.api.galaxies.Planet;

public class ExMoon extends Moon implements IExBody {

	private ClassBody classplanet = ClassBody.SELENA;
	private float gravity;
	private long day;
	private float solarradiation;
	private float atm_pressure, water_pressure, orbit_eccentricityX = 1.0F, orbit_eccentricityY = 1.0F, orbit_offsetX = 0.0F, orbit_offsetY = 0.0F;
	
	
	public ExMoon(String moonName) {
		super(moonName);
	}
	
	public ExMoon setParentPlanet(Planet planet) {
		this.parentPlanet = planet;
		return this;
	}

	public ExMoon setClassPlanet(ClassBody classbody)
	{
		this.classplanet = classbody;
		return this;
	}
	
	public ExMoon setGravity(float gravity)
	{
		this.gravity = gravity;
		return this;
	}
	
	public ExMoon setDayLength(long lenght)
	{
		this.day = lenght;
		return this;
	}
	
	public ExMoon setAtmosphericPressure(float value)
	{
		this.atm_pressure = value;
		return this;
	}
	
	public ExMoon setWaterPressure(float value)
	{
		this.water_pressure = value;
		return this;
	}
	
	public ExMoon setSolarRadiation(float mod)
	{
		this.solarradiation = mod;
		return this;
	}

	public ExMoon setOrbitEccentricity(float eccentricityX, float eccentricityY)
	{
		this.orbit_eccentricityX = eccentricityX;
		this.orbit_eccentricityY = eccentricityY; 
		return this;
	}
	
	public ExMoon setOrbitOffset(float x, float y)
	{
		this.orbit_offsetX = x;
		this.orbit_offsetY = y;
		return this;
	}
	
	
	
	public TypeBody getType() {return TypeBody.MOON; }
	@Override
	public float getSolarRadiationMod() {return this.solarradiation;}	
	@Override
	public ClassBody getClassPlanet() { return this.classplanet; }
	@Override
	public float getGravity() { return this.gravity; }
	@Override
	public long getDayLength() { return this.day; }
	@Override
	public float getAtmosphericPressure() { return this.atm_pressure; }
	@Override
	public float getXOrbitEccentricity() { return this.orbit_eccentricityX; }
	@Override
	public float getYOrbitEccentricity() { return this.orbit_eccentricityY; }
	@Override
	public float getXOrbitOffset() {return this.orbit_offsetX;}
	@Override
	public float getYOrbitOffset() {return this.orbit_offsetY;}
	@Override
	public float getWaterPressure() { return this.water_pressure; }
	
}
