package asmodeuscore.api.space;

import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.SolarSystem;

public interface ICelestialRegistry {

	public boolean canRegistry();
	
	public SolarSystem getParentSolarSystem();
	default public CelestialBody getParentBody() {return null;}
}
