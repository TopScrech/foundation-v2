package asmodeuscore.core.configs;

import static net.minecraftforge.common.config.Configuration.CATEGORY_GENERAL;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;

import asmodeuscore.core.utils.ACCompatibilityManager;
import micdoodle8.mods.galacticraft.core.Constants;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.common.FMLLog;

public class AsmodeusConfig
{
	
	private static final String CATEGORY_MAP = "galaxymap";
    public static boolean loaded;

    public static Configuration config;

    public AsmodeusConfig(File file)
    {
        if (!AsmodeusConfig.loaded)
        {
        	AsmodeusConfig.config = new Configuration(file);
        	AsmodeusConfig.syncConfig(true);
        }
    }

    public static boolean enableDebug;
    public static boolean enableRenderAtmosphere;
    
    public static int speedTimeTravel = 1;
        
    public static boolean enableNewGalaxyMap = true;
    public static boolean enableNewTierSystem = true;
    public static boolean enable2DGalaxyMap = false;
    public static boolean enableImgOnGalaxyMap = true;
    public static boolean enableTimeTravelSystem = true;
    public static boolean enableDynamicImgOnGalaxyMap = true;
    
    public static boolean enableRequiredEquip = false;
    public static boolean enableRotateStars = false;
    public static boolean enableColorStars = false;
    public static boolean enableSkyOverworld = true;
    public static boolean enableSkyMoon= true;
    public static boolean enableSkyAsteroids = true;
    public static boolean enableSkyOverworldOrbit = true;
    public static boolean enableCloudsVenus = true;
    public static boolean enableBetterMoonOnSky = false;
    public static boolean enableRenderBody = true;
    public static boolean enableRenderDegreeSymbol = true;
    public static boolean enableWeatherDamage = true;
	public static boolean enableSublightEngine = true;

    public static int tempPosX = 0;
    public static int tempPosY = 0;
    public static int renderBodyPosY = 255;


	public static float stormDamageRate = 1.0F;
	public static float toxicRainDamageRate = 1.0F;
	public static float atmospherePressureDamageRate = 1.0F;
	public static float solarRadiationDamageRate = 1.0F;

	public static String[] celestialBodyTiers;

	public static String[] asteroidsCoreBlocks;
    /*
    private static void setConfigBoolean(Property prop, List<String> propOrder, String category, String confname, String desc, boolean conf, boolean mcrestart)
    {
    	prop = config.get(category, confname, conf);
        prop.setComment(desc);
        prop.setLanguageKey("ac.configgui." + confname).setRequiresMcRestart(mcrestart);
        conf = prop.getBoolean(conf);
        propOrder.add(prop.getName()); 
    }
   */
    public static void syncConfig(boolean load)
    {
        List<String> propOrder = new ArrayList<String>();

        try
        {
            Property prop = null;

            if (!config.isChild)
            {
                if (load)
                {
                    config.load();
                }
            }            
            
            //setConfigBoolean(prop, propOrder, Constants.CONFIG_CATEGORY_GENERAL, "enableRequiredEquip", "Enable/Disable required equipement on Galaxy map. (If false - you can travel on planet without equip.)", enableRequiredEquip, false);
            //setConfigBoolean(prop, propOrder, Constants.CONFIG_CATEGORY_GENERAL, "enableNewGalaxyMap", "Enable/Disable New Galaxy Map.", enableNewGalaxyMap, false);
            
            if(ACCompatibilityManager.isGalacticraftLoaded()) 
            {
	            prop = config.get(Constants.CONFIG_CATEGORY_GENERAL, "speedTimeTravel", speedTimeTravel);
	            prop.setComment("Time Travel speed factor.");
	            prop.setLanguageKey("ac.configgui.speedTimeTravel");
	            speedTimeTravel = prop.getInt();
	            propOrder.add(prop.getName());
	            
	            prop = config.get(Constants.CONFIG_CATEGORY_CLIENT, "renderBodyPosY", renderBodyPosY);
	            prop.setComment("Pos Y for render body on horizon.");
	            prop.setLanguageKey("ac.configgui.renderBodyPosY");
	            renderBodyPosY = prop.getInt();
	            propOrder.add(prop.getName());
            
	            prop = config.get(Constants.CONFIG_CATEGORY_GENERAL, "enableRenderDegreeSymbol", true);
	            prop.setComment("Enable/Disable render degree symbol.");
	            prop.setLanguageKey("ac.configgui.enableRenderDegreeSymbol");
	            enableRenderDegreeSymbol = prop.getBoolean(true);
	            propOrder.add(prop.getName());
	            
	            prop = config.get(Constants.CONFIG_CATEGORY_CLIENT, "enableRenderBody", true);
	            prop.setComment("Enable/Disable render body on horizon.");
	            prop.setLanguageKey("ac.configgui.enableRenderBody");
	            enableRenderBody = prop.getBoolean(true);
	            propOrder.add(prop.getName());
	            
	            prop = config.get(CATEGORY_MAP, "enableNewGalaxyMap", true);
	            prop.setComment("Enable/Disable New Galaxy Map.");
	            prop.setLanguageKey("ac.configgui.enableNewGalaxyMap");
	            enableNewGalaxyMap = prop.getBoolean(true);
	            propOrder.add(prop.getName());
            
	            prop = config.get(CATEGORY_MAP, "enableNewTierSystem", true);
	            prop.setComment("Enable/Disable New Tier System.");
	            prop.setLanguageKey("ac.configgui.enableNewTierSystem").setRequiresMcRestart(true);
	            enableNewTierSystem = prop.getBoolean(true);
	            propOrder.add(prop.getName());
            
	            prop = config.get(CATEGORY_MAP, "enable2DGalaxyMap", false);
	            prop.setComment("Enable/Disable 2D Galaxy Map. (like in GC2)");
	            prop.setLanguageKey("ac.configgui.enable2DGalaxyMap");
	            enable2DGalaxyMap = prop.getBoolean(false);
	            propOrder.add(prop.getName());
            
	            prop = config.get(CATEGORY_MAP, "enableImgOnGalaxyMap", true);
	            prop.setComment("Enable/Disable background image on Galaxy Map. (If false, returned black color and grid)");
	            prop.setLanguageKey("ac.configgui.enableImgOnGalaxyMap");
	            enableImgOnGalaxyMap = prop.getBoolean(true);
	            propOrder.add(prop.getName());

	            prop = config.get(CATEGORY_MAP, "enableTimeTravelSystem", true);
	            prop.setComment("Enable/Disable Time Travel System on Galaxy Map.");
	            prop.setLanguageKey("ac.configgui.enableTimeTravelSystem");
	            enableTimeTravelSystem = prop.getBoolean(true);
	            propOrder.add(prop.getName());
   
	            prop = config.get(CATEGORY_MAP, "enableDynamicImgOnGalaxyMap", true);
	            prop.setComment("Enable/Disable dynamic image on map. (If disabled, image will static)");
	            prop.setLanguageKey("ac.configgui.enableDynamicImgOnGalaxyMap");
	            enableDynamicImgOnGalaxyMap = prop.getBoolean(true);
	            propOrder.add(prop.getName());   
	            
	            prop = config.get(Constants.CONFIG_CATEGORY_GENERAL, "tempPosX", tempPosX);
	            prop.setComment("Offset temperature panel position X.");
	            prop.setLanguageKey("ac.configgui.tempPosX");
	            tempPosX = prop.getInt();
	            propOrder.add(prop.getName());
	            
	            prop = config.get(Constants.CONFIG_CATEGORY_GENERAL, "tempPosY", tempPosY);
	            prop.setComment("Offset temperature panel position Y.");
	            prop.setLanguageKey("ac.configgui.tempPosY");
	            tempPosY = prop.getInt();
	            propOrder.add(prop.getName());
	            
	            prop = config.get(Constants.CONFIG_CATEGORY_CLIENT, "enableSkyOverworld", enableSkyOverworld);
	            prop.setComment("Enable/Disable Advanced Sky Provider on Overworld (Not support shaders).");
	            prop.setLanguageKey("gc.configgui.enableSkyOverworld").setRequiresMcRestart(false);
	            enableSkyOverworld = prop.getBoolean(true);
	            propOrder.add(prop.getName());   
	            
	            prop = config.get(Constants.CONFIG_CATEGORY_CLIENT, "enableSkyOverworldOrbit", enableSkyOverworldOrbit);
	            prop.setComment("Enable/Disable Advanced Sky Provider on Orbit Overworld (Not support shaders).");
	            prop.setLanguageKey("gc.configgui.enableSkyOverworldOrbit").setRequiresMcRestart(false);
	            enableSkyOverworldOrbit = prop.getBoolean(true);
	            propOrder.add(prop.getName());   
	            
	            prop = config.get(Constants.CONFIG_CATEGORY_CLIENT, "enableSkyMoon", enableSkyMoon);
	            prop.setComment("Enable/Disable Advanced Sky Provider on Moon (Not support shaders).");
	            prop.setLanguageKey("gc.configgui.enableSkyMoon").setRequiresMcRestart(false);
	            enableSkyMoon = prop.getBoolean(true);
	            propOrder.add(prop.getName());  
	            
	            prop = config.get(Constants.CONFIG_CATEGORY_CLIENT, "enableSkyAsteroids", enableSkyAsteroids);
	            prop.setComment("Enable/Disable Advanced Sky Provider on Asteroids (Not support shaders).");
	            prop.setLanguageKey("gc.configgui.enableSkyAsteroids").setRequiresMcRestart(false);
	            enableSkyAsteroids = prop.getBoolean(true);
	            propOrder.add(prop.getName());  
	            
	            prop = config.get(Constants.CONFIG_CATEGORY_CLIENT, "enableCloudsVenus", enableCloudsVenus);
	            prop.setComment("Enable/Disable Render Clouds on Venus.");
	            prop.setLanguageKey("gc.configgui.enableCloudsVenus").setRequiresMcRestart(false);
	            enableCloudsVenus = prop.getBoolean(true);
	            propOrder.add(prop.getName()); 
	            
	            prop = config.get(Constants.CONFIG_CATEGORY_CLIENT, "enableBetterMoonOnSky", enableBetterMoonOnSky);
	            prop.setComment("Enable/Disable Render Clouds on Venus.");
	            prop.setLanguageKey("gc.configgui.enableBetterMoonOnSky").setRequiresMcRestart(false);
	            enableBetterMoonOnSky = prop.getBoolean(true);
	            propOrder.add(prop.getName());

				prop = config.get(Constants.CONFIG_CATEGORY_GENERAL, "celestialBodyTiers",
						new String[]
								{
										"planet.example:5"
								});

				prop.setComment("Set custom tier for Celestial Bodies. Example: planet.mars:5");
				prop.setLanguageKey("gc.configgui.сelestialBodyTiers").setRequiresMcRestart(true);
				celestialBodyTiers = prop.getStringList();
				propOrder.add(prop.getName());

				prop = config.get(Constants.CONFIG_CATEGORY_WORLDGEN, "asteroidsCoreBlocks",
						new String[]
								{
										"minecraft:air:0:5:0.2"
								});

				prop.setComment("Add custom core blocks in asteroids (GC Asteroids dimension)  Example: modid:block:meta:probability:thickness");
				prop.setLanguageKey("gc.configgui.asteroidsCoreBlocks").setRequiresMcRestart(true);
				asteroidsCoreBlocks = prop.getStringList();
				propOrder.add(prop.getName());
            }
            
            prop = config.get(Constants.CONFIG_CATEGORY_CLIENT, "enableRenderAtmosphere", true);
            prop.setComment("Enable/Disable render planet atmosphere on sky.");
            prop.setLanguageKey("ac.configgui.enableRenderAtmosphere");
            enableRenderAtmosphere = prop.getBoolean(true);
            propOrder.add(prop.getName()); 
            

            prop = config.get(Constants.CONFIG_CATEGORY_CLIENT, "enableRotateStars", true);
            prop.setComment("Enable/Disable rotate stars on sky.");
            prop.setLanguageKey("ac.configgui.enableRotateStars");
            enableRotateStars = prop.getBoolean(true);
            propOrder.add(prop.getName());
            
            prop = config.get(Constants.CONFIG_CATEGORY_CLIENT, "enableColorStars", false);
            prop.setComment("Enable/Disable render color stars on sky.");
            prop.setLanguageKey("ac.configgui.enableColorStars");
            enableColorStars = prop.getBoolean(false);
            propOrder.add(prop.getName());

			prop = config.get(Constants.CONFIG_CATEGORY_GENERAL, "enableDebug", false);
			prop.setComment("Enable/Disable debug mode.");
			prop.setLanguageKey("ac.configgui.enableDebug");
			enableDebug = prop.getBoolean(false);
			propOrder.add(prop.getName());

			prop = config.get(Constants.CONFIG_CATEGORY_DIMENSIONS, "enableWeatherDamage", true);
			prop.setComment("Enable/Disable damage from weather (acid_rain, dust_storm, etc).");
			prop.setLanguageKey("ac.configgui.enableWeatherDamage");
			enableWeatherDamage = prop.getBoolean(true);
			propOrder.add(prop.getName());

			prop = config.get(CATEGORY_MAP, "enableSublightEngine", true);
			prop.setComment("Enable/Disable required sublight engine for travel in other star systems.");
			prop.setLanguageKey("ac.configgui.enableSublightEngine");
			enableSublightEngine = prop.getBoolean(true);
			propOrder.add(prop.getName());

			prop = config.get(Constants.CONFIG_CATEGORY_DIFFICULTY, "stormDamageRate", stormDamageRate);
			prop.setComment("Damage rate for duststorms on celestial bodies.");
			prop.setLanguageKey("ac.configgui.stormDamageRate");
			stormDamageRate = (float) prop.getDouble();
			propOrder.add(prop.getName());

			prop = config.get(Constants.CONFIG_CATEGORY_DIFFICULTY, "toxicRainDamageRate", toxicRainDamageRate);
			prop.setComment("Damage rate for toxic rain on celestial bodies.");
			prop.setLanguageKey("ac.configgui.toxicRainDamageRate");
			toxicRainDamageRate = (float) prop.getDouble();
			propOrder.add(prop.getName());

			prop = config.get(Constants.CONFIG_CATEGORY_DIFFICULTY, "atmospherePressureDamageRate", atmospherePressureDamageRate);
			prop.setComment("Damage rate for atmosphere pressure on celestial bodies.");
			prop.setLanguageKey("ac.configgui.atmospherePressureDamageRate");
			atmospherePressureDamageRate = (float) prop.getDouble();
			propOrder.add(prop.getName());

			prop = config.get(Constants.CONFIG_CATEGORY_DIFFICULTY, "solarRadiationDamageRate", solarRadiationDamageRate);
			prop.setComment("Damage rate for solar radiation on celestial bodies.");
			prop.setLanguageKey("ac.configgui.solarRadiationDamageRate");
			solarRadiationDamageRate = (float) prop.getDouble();
			propOrder.add(prop.getName());


			config.setCategoryPropertyOrder(CATEGORY_GENERAL, propOrder);

            if (config.hasChanged())
            {
                config.save();
            }
        }
        catch (final Exception e)
        {
            FMLLog.log(Level.ERROR, e, "Asmodeus Core has a problem loading it's config");
        }
    }
    

    public static List<IConfigElement> getConfigElements()
    {
        List<IConfigElement> list = new ArrayList<IConfigElement>();
        list.addAll(new ConfigElement(config.getCategory(Constants.CONFIG_CATEGORY_DIFFICULTY)).getChildElements());
        list.addAll(new ConfigElement(config.getCategory(Constants.CONFIG_CATEGORY_GENERAL)).getChildElements());
        list.addAll(new ConfigElement(config.getCategory(Constants.CONFIG_CATEGORY_CLIENT)).getChildElements());
        //list.addAll(new ConfigElement(config.getCategory(Constants.CONFIG_CATEGORY_CONTROLS)).getChildElements());
        //list.addAll(new ConfigElement(config.getCategory(Constants.CONFIG_CATEGORY_COMPATIBILITY)).getChildElements());
        //list.addAll(new ConfigElement(config.getCategory(Constants.CONFIG_CATEGORY_WORLDGEN)).getChildElements());
        //list.addAll(new ConfigElement(config.getCategory(Constants.CONFIG_CATEGORY_SERVER)).getChildElements());
        //list.addAll(new ConfigElement(config.getCategory(Constants.CONFIG_CATEGORY_DIMENSIONS)).getChildElements());
        //list.addAll(new ConfigElement(config.getCategory(Constants.CONFIG_CATEGORY_SCHEMATIC)).getChildElements());
        //list.addAll(new ConfigElement(config.getCategory(Constants.CONFIG_CATEGORY_ACHIEVEMENTS)).getChildElements());
        //list.addAll(new ConfigElement(config.getCategory(Constants.CONFIG_CATEGORY_ENTITIES)).getChildElements());
        //list.addAll(new ConfigElement(config.getCategory(Constants.CONFIG_CATEGORY_KEYS)).getChildElements());
        list.addAll(new ConfigElement(config.getCategory(CATEGORY_MAP)).getChildElements());
        list.addAll(new ConfigElement(config.getCategory("development")).getChildElements());

        return list;
    }
    
}
