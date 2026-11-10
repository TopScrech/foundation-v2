package asmodeuscore.api.space;

import asmodeuscore.api.dimension.IAdvancedSpace.ClassBody;

public interface IExBody {

	public ClassBody getClassPlanet();	
	public float getGravity();
	public long getDayLength();
	public float getAtmosphericPressure();
	public float getWaterPressure();
	public float getSolarRadiationMod();
	public default boolean isSolarRadiation() {	return getSolarRadiationMod() > 0.0F; }
	
	public default float getXOrbitEccentricity() { return 0.0F; }
	public default float getYOrbitEccentricity() { return 0.0F; }
	public default float getXOrbitOffset() {return 0.0F;}
	public default float getYOrbitOffset() {return 0.0F;}
}
