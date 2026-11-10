package asmodeuscore.core.astronomy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import asmodeuscore.api.space.ICelestialRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.IChildBody;
import micdoodle8.mods.galacticraft.api.galaxies.Planet;
import micdoodle8.mods.galacticraft.api.galaxies.SolarSystem;

public class SpaceData {
	
	public enum Engine_Type {
		
		FUEL_ENGINE(0, "fuel_engine"),
		ION_ENGINE(1, "ion_engine"),
		PLASMA_ENGINE(2, "plasma_engine"),
		SUBLIGHT_ENGINE(3, "sublight_engine"),
		BLACKHOLE_ENGINE(4, "blackhole_engine");
		
		String name;
		int id;
		Engine_Type(int id, String name)
		{
			this.id = id;
			this.name = name;
		}
		
		public String getName()
		{
			return this.name;
		}
		
		public int getID()
		{
			return this.id;
		}
		
		private final static Engine_Type[] values = values();
		public static Engine_Type byID(int id) { return values[id % values.length]; }

	}
	
	////////////////////////////////////////////////////////////////////////////////////
	public final String rocket_tier = "rocket_tier";
	public final String rocket_engine = "rocket_engine";
	public final String rocket_count_fuel = "rocket_count_fuel";
	public final String second_passenger = "second_passenger";

	private static Map<String, Object> map = new HashMap<String, Object>();
	
	public SpaceData()
	{
	}
	
	public void addToMap(String name, Object obj)
	{
		map.put(name, obj);
	}
	
	public Map getMap()
	{
		return this.map;
	}
	
	public Object getValue(String name)
	{		
		return map.get(name);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	static HashMap<SolarSystem, List<CelestialBody>> solarSystemList = Maps.newHashMap();
	static HashMap<Planet, List<CelestialBody>> moonList = Maps.newHashMap();
	 
	public static void refreshGalaxies()
    {
		GalaxyRegistry.refreshGalaxies();
		
		moonList.clear();
		solarSystemList.clear();
		
		for (CelestialBody body : getRegisteredCelestialBodies().values())
        {
			if(body instanceof ICelestialRegistry) {
				if(body instanceof IChildBody)
				{
					Planet planet = ((IChildBody) body).getParentPlanet();					
					List<CelestialBody> listOfMoons = moonList.get(planet);
					if (listOfMoons == null) {
						listOfMoons = new ArrayList<CelestialBody>();
					}
					listOfMoons.add(body);
					moonList.put(planet, listOfMoons);
				}
				else
				{
		            SolarSystem solarSystem = ((ICelestialRegistry)body).getParentSolarSystem();
		            List<CelestialBody> planetList = solarSystemList.get(solarSystem);
		            if (planetList == null)
		            {
		                planetList = new ArrayList<CelestialBody>();
		            }
		            planetList.add(body);
		            solarSystemList.put(solarSystem, planetList);
				}
			}
        }
    }
	
	public static List<CelestialBody> getBodiesForSolarSystem(SolarSystem solarSystem)
    {
        List<CelestialBody> solarSystemListLocal = solarSystemList.get(solarSystem);

        if (solarSystemListLocal == null)
        {
            return new ArrayList<CelestialBody>();
        }

        return ImmutableList.copyOf(solarSystemListLocal);
    }
	
	public static List<CelestialBody> getMoonsForPlanet(Planet planet)
    {
        List<CelestialBody> moonListLocal = moonList.get(planet);

        if (moonListLocal == null)
        {
            return new ArrayList<CelestialBody>();
        }

        return ImmutableList.copyOf(moonListLocal);
    }
	
	static int maxCelestialBodiesID = 0;
	//static int maxCometID = 0;
	static HashMap<String, CelestialBody> celestialbodies = Maps.newHashMap();
    static BiMap<String, Integer> celestialbodiesIDs = HashBiMap.create();
    /*
    static Map<String, Comet> comets = Maps.newHashMap();
    static BiMap<String, Integer> cometsIDs = HashBiMap.create();
    */
    ////////////////////////////////////////////////////////////////////////////////////
    public static boolean registerCelestialBody(CelestialBody body)
    {
        if (celestialbodiesIDs.containsKey(body.getName()))
        {
            return false;
        }

        celestialbodies.put(body.getName(), body);
        celestialbodiesIDs.put(body.getName(), ++maxCelestialBodiesID);

        return true;
    }
    
    public static Map<String, CelestialBody> getRegisteredCelestialBodies()
    {
        return ImmutableMap.copyOf(celestialbodies);
    }
    
    public static Map<String, Integer> getRegisteredCelestialBodiesIDs()
    {
        return ImmutableMap.copyOf(celestialbodiesIDs);
    }
    
    public static int getCelestialBodyID(String bodyName)
    {
        return celestialbodiesIDs.get(bodyName);
    }
    ////////////////////////////////////////////////////////////////////////////////////
    /*
    public static boolean registerComet(Comet comet)
    {
        if (cometsIDs.containsKey(comet.getName()))        
            return false;
        

        comets.put(comet.getName(), comet);
        cometsIDs.put(comet.getName(), ++maxCometID);

        return true;
    }
    
    public static Map<String, Comet> getRegisteredComets()
    {
        return ImmutableMap.copyOf(comets);
    }
    
    public static Map<String, Integer> getRegisteredCometsIDs()
    {
        return ImmutableMap.copyOf(cometsIDs);
    }
    
    public static int getCometID(String cometName)
    {
        return cometsIDs.get(cometName);
    }
    */
}
