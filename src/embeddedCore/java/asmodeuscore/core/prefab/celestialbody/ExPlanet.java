package asmodeuscore.core.prefab.celestialbody;

import asmodeuscore.api.dimension.IAdvancedSpace.ClassBody;
import asmodeuscore.api.dimension.IAdvancedSpace.TypeBody;
import asmodeuscore.api.space.IExBody;
import micdoodle8.mods.galacticraft.api.galaxies.Planet;
import micdoodle8.mods.galacticraft.api.galaxies.SolarSystem;

public class ExPlanet extends Planet implements IExBody {

	private ClassBody classplanet = ClassBody.SELENA;
	private float gravity;
	private long day;
	private float solarradiation;
	private float atm_pressure, water_pressure, orbit_eccentricityX = 1.0F, orbit_eccentricityY = 1.0F, orbit_offsetX = 0.0F, orbit_offsetY = 0.0F;
	
	public ExPlanet(String planetName) {
		super(planetName);
	}

	public ExPlanet setParentSolarSystem(SolarSystem system)
    {   
        return (ExPlanet) super.setParentSolarSystem(system);
    }
	
	public ExPlanet setOrbitEccentricity(float eccentricityX, float eccentricityY)
	{
		this.orbit_eccentricityX = eccentricityX;
		this.orbit_eccentricityY = eccentricityY; 
		return this;
	}
	
	public ExPlanet setOrbitOffset(float x, float y)
	{
		this.orbit_offsetX = x;
		this.orbit_offsetY = y;
		return this;
	}
	
	public ExPlanet setClassPlanet(ClassBody classbody)
	{
		this.classplanet = classbody;
		return this;
	}
	
	public ExPlanet setGravity(float gravity)
	{
		this.gravity = gravity;
		return this;
	}
	
	public ExPlanet setDayLength(long lenght)
	{
		this.day = lenght;
		return this;
	}
	
	public ExPlanet setAtmosphericPressure(float value)
	{
		this.atm_pressure = value;
		return this;
	}
	
	public ExPlanet setWaterPressure(float value)
	{
		this.water_pressure = value;
		return this;
	}
	
	public ExPlanet setSolarRadiation(float mod)
	{
		this.solarradiation = mod;
		return this;
	}

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

	public TypeBody getType() {return TypeBody.PLANET; }
	
	public float getXOrbitEccentricity() { return this.orbit_eccentricityX; }
	public float getYOrbitEccentricity() { return this.orbit_eccentricityY; }
	public float getXOrbitOffset() {return this.orbit_offsetX;}
	public float getYOrbitOffset() {return this.orbit_offsetY;}

	@Override
	public float getWaterPressure() { return this.water_pressure; }


}
