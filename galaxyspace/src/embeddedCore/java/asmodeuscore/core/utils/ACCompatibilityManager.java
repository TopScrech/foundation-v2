package asmodeuscore.core.utils;

import net.minecraftforge.fml.common.Loader;

public class ACCompatibilityManager {
	private static boolean addonEPLoaded = Loader.isModLoaded("extraplanets");
	private static boolean addonPPLoaded = Loader.isModLoaded("planetprogression");
	private static boolean galacticraftLoaded = Loader.isModLoaded("galacticraftcore");
	private static boolean beyondspaceLoaded = Loader.isModLoaded("beyondspace");
	private static boolean galaxyspaceLoaded = Loader.isModLoaded("galaxyspace");
	  	
    public static boolean isExtraPlanetsLoaded()
	{
		return ACCompatibilityManager.addonEPLoaded;
	}
	
	public static boolean isPlanetProgressionLoaded()
	{
		return ACCompatibilityManager.addonPPLoaded;
	}
	
	public static boolean isGalacticraftLoaded()
	{
		return ACCompatibilityManager.galacticraftLoaded;
	}
	
	public static boolean isBeyondSpaceLoaded()
	{
		return ACCompatibilityManager.beyondspaceLoaded;
	}
	
	public static boolean isGalaxySpaceLoaded()
	{
		return ACCompatibilityManager.galaxyspaceLoaded;
	}
		
}
