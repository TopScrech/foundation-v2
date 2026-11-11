package asmodeuscore.core.configs

import java.util.List

import asmodeuscore.core.configs.AsmodeusConfig
import asmodeuscore.core.utils.ACCompatibilityManager
import micdoodle8.mods.galacticraft.core.Constants
import foundation.config.FoundationConfigElement as ConfigElement
import foundation.config.FoundationConfiguration as Configuration
import foundation.config.FoundationConfigProperty as Property
import foundation.config.FoundationConfigEntry as IConfigElement
import foundation.logging.FoundationLog as FMLLog
import org.apache.logging.log4j.Level
import java.io.File
import java.lang.Exception
import java.util.ArrayList

open class AsmodeusConfig
constructor(file: File) {
    init {
        if (!loaded) {
            config = Configuration(file)
            syncConfig(true)
        }
    }

    companion object {
        private const val CATEGORY_MAP = "galaxymap"

        @JvmField
        var loaded: Boolean = false

        @JvmField
        var config: Configuration? = null

        @JvmField
        var enableDebug: Boolean = false

        @JvmField
        var enableRenderAtmosphere: Boolean = false

        @JvmField
        var speedTimeTravel: Int = 1

        @JvmField
        var enableNewGalaxyMap: Boolean = true

        @JvmField
        var enableNewTierSystem: Boolean = true

        @JvmField
        var enable2DGalaxyMap: Boolean = false

        @JvmField
        var enableImgOnGalaxyMap: Boolean = true

        @JvmField
        var enableTimeTravelSystem: Boolean = true

        @JvmField
        var enableDynamicImgOnGalaxyMap: Boolean = true

        @JvmField
        var enableRequiredEquip: Boolean = false

        @JvmField
        var enableRotateStars: Boolean = false

        @JvmField
        var enableColorStars: Boolean = false

        @JvmField
        var enableSkyOverworld: Boolean = true

        @JvmField
        var enableSkyMoon: Boolean = true

        @JvmField
        var enableSkyAsteroids: Boolean = true

        @JvmField
        var enableSkyOverworldOrbit: Boolean = true

        @JvmField
        var enableCloudsVenus: Boolean = true

        @JvmField
        var enableBetterMoonOnSky: Boolean = false

        @JvmField
        var enableRenderBody: Boolean = true

        @JvmField
        var enableRenderDegreeSymbol: Boolean = true

        @JvmField
        var enableWeatherDamage: Boolean = true

        @JvmField
        var enableSublightEngine: Boolean = true

        @JvmField
        var tempPosX: Int = 0

        @JvmField
        var tempPosY: Int = 0

        @JvmField
        var renderBodyPosY: Int = 255


        @JvmField
        var stormDamageRate: Float = 1.0f

        @JvmField
        var toxicRainDamageRate: Float = 1.0f

        @JvmField
        var atmospherePressureDamageRate: Float = 1.0f

        @JvmField
        var solarRadiationDamageRate: Float = 1.0f

        @JvmField
        var celestialBodyTiers: Array<String?>? = null

        @JvmField
        var asteroidsCoreBlocks: Array<String?>? = null

        /*
    private static void setConfigBoolean(Property prop, List<String?> propOrder, String category, String confname, String desc, boolean conf, boolean mcrestart)
    {
    	prop = config.get(category, confname, conf);
        prop.setComment(desc);
        prop.setLanguageKey("ac.configgui." + confname).setRequiresMcRestart(mcrestart);
        conf = prop.getBoolean(conf);
        propOrder.add(prop.getName()); 
    }
   */
        @JvmStatic
        fun syncConfig(load: Boolean) {
            val propOrder: MutableList<String?> = ArrayList<String?>()

            try {
                var prop: Property? = null

                if (!config!!.isChild) {
                    if (load) {
                        config!!.load()
                    }
                }


                //setConfigBoolean(prop, propOrder, Constants.CONFIG_CATEGORY_GENERAL, "enableRequiredEquip", "Enable/Disable required equipement on Galaxy map. (If false - you can travel on planet without equip.)", enableRequiredEquip, false);
                //setConfigBoolean(prop, propOrder, Constants.CONFIG_CATEGORY_GENERAL, "enableNewGalaxyMap", "Enable/Disable New Galaxy Map.", enableNewGalaxyMap, false);
                if (ACCompatibilityManager.isGalacticraftLoaded()) {
                    prop = config!!.get(Constants.CONFIG_CATEGORY_GENERAL, "speedTimeTravel", speedTimeTravel)
                    prop.setComment("Time Travel speed factor.")
                    prop.setLanguageKey("ac.configgui.speedTimeTravel")
                    speedTimeTravel = prop.getInt()
                    propOrder.add(prop.getName())

                    prop = config!!.get(Constants.CONFIG_CATEGORY_CLIENT, "renderBodyPosY", renderBodyPosY)
                    prop.setComment("Pos Y for render body on horizon.")
                    prop.setLanguageKey("ac.configgui.renderBodyPosY")
                    renderBodyPosY = prop.getInt()
                    propOrder.add(prop.getName())

                    prop = config!!.get(Constants.CONFIG_CATEGORY_GENERAL, "enableRenderDegreeSymbol", true)
                    prop.setComment("Enable/Disable render degree symbol.")
                    prop.setLanguageKey("ac.configgui.enableRenderDegreeSymbol")
                    enableRenderDegreeSymbol = prop.getBoolean(true)
                    propOrder.add(prop.getName())

                    prop = config!!.get(Constants.CONFIG_CATEGORY_CLIENT, "enableRenderBody", true)
                    prop.setComment("Enable/Disable render body on horizon.")
                    prop.setLanguageKey("ac.configgui.enableRenderBody")
                    enableRenderBody = prop.getBoolean(true)
                    propOrder.add(prop.getName())

                    prop = config!!.get(CATEGORY_MAP, "enableNewGalaxyMap", true)
                    prop.setComment("Enable/Disable New Galaxy Map.")
                    prop.setLanguageKey("ac.configgui.enableNewGalaxyMap")
                    enableNewGalaxyMap = prop.getBoolean(true)
                    propOrder.add(prop.getName())

                    prop = config!!.get(CATEGORY_MAP, "enableNewTierSystem", true)
                    prop.setComment("Enable/Disable New Tier System.")
                    prop.setLanguageKey("ac.configgui.enableNewTierSystem").setRequiresMcRestart(true)
                    enableNewTierSystem = prop.getBoolean(true)
                    propOrder.add(prop.getName())

                    prop = config!!.get(CATEGORY_MAP, "enable2DGalaxyMap", false)
                    prop.setComment("Enable/Disable 2D Galaxy Map. (like in GC2)")
                    prop.setLanguageKey("ac.configgui.enable2DGalaxyMap")
                    enable2DGalaxyMap = prop.getBoolean(false)
                    propOrder.add(prop.getName())

                    prop = config!!.get(CATEGORY_MAP, "enableImgOnGalaxyMap", true)
                    prop.setComment("Enable/Disable background image on Galaxy Map. (If false, returned black color and grid)")
                    prop.setLanguageKey("ac.configgui.enableImgOnGalaxyMap")
                    enableImgOnGalaxyMap = prop.getBoolean(true)
                    propOrder.add(prop.getName())

                    prop = config!!.get(CATEGORY_MAP, "enableTimeTravelSystem", true)
                    prop.setComment("Enable/Disable Time Travel System on Galaxy Map.")
                    prop.setLanguageKey("ac.configgui.enableTimeTravelSystem")
                    enableTimeTravelSystem = prop.getBoolean(true)
                    propOrder.add(prop.getName())

                    prop = config!!.get(CATEGORY_MAP, "enableDynamicImgOnGalaxyMap", true)
                    prop.setComment("Enable/Disable dynamic image on map. (If disabled, image will static)")
                    prop.setLanguageKey("ac.configgui.enableDynamicImgOnGalaxyMap")
                    enableDynamicImgOnGalaxyMap = prop.getBoolean(true)
                    propOrder.add(prop.getName())

                    prop = config!!.get(Constants.CONFIG_CATEGORY_GENERAL, "tempPosX", tempPosX)
                    prop.setComment("Offset temperature panel position X.")
                    prop.setLanguageKey("ac.configgui.tempPosX")
                    tempPosX = prop.getInt()
                    propOrder.add(prop.getName())

                    prop = config!!.get(Constants.CONFIG_CATEGORY_GENERAL, "tempPosY", tempPosY)
                    prop.setComment("Offset temperature panel position Y.")
                    prop.setLanguageKey("ac.configgui.tempPosY")
                    tempPosY = prop.getInt()
                    propOrder.add(prop.getName())

                    prop = config!!.get(Constants.CONFIG_CATEGORY_CLIENT, "enableSkyOverworld", enableSkyOverworld)
                    prop.setComment("Enable/Disable Advanced Sky Provider on Overworld (Not support shaders).")
                    prop.setLanguageKey("gc.configgui.enableSkyOverworld").setRequiresMcRestart(false)
                    enableSkyOverworld = prop.getBoolean(true)
                    propOrder.add(prop.getName())

                    prop = config!!.get(
                        Constants.CONFIG_CATEGORY_CLIENT,
                        "enableSkyOverworldOrbit",
                        enableSkyOverworldOrbit
                    )
                    prop.setComment("Enable/Disable Advanced Sky Provider on Orbit Overworld (Not support shaders).")
                    prop.setLanguageKey("gc.configgui.enableSkyOverworldOrbit").setRequiresMcRestart(false)
                    enableSkyOverworldOrbit = prop.getBoolean(true)
                    propOrder.add(prop.getName())

                    prop = config!!.get(Constants.CONFIG_CATEGORY_CLIENT, "enableSkyMoon", enableSkyMoon)
                    prop.setComment("Enable/Disable Advanced Sky Provider on Moon (Not support shaders).")
                    prop.setLanguageKey("gc.configgui.enableSkyMoon").setRequiresMcRestart(false)
                    enableSkyMoon = prop.getBoolean(true)
                    propOrder.add(prop.getName())

                    prop = config!!.get(Constants.CONFIG_CATEGORY_CLIENT, "enableSkyAsteroids", enableSkyAsteroids)
                    prop.setComment("Enable/Disable Advanced Sky Provider on Asteroids (Not support shaders).")
                    prop.setLanguageKey("gc.configgui.enableSkyAsteroids").setRequiresMcRestart(false)
                    enableSkyAsteroids = prop.getBoolean(true)
                    propOrder.add(prop.getName())

                    prop = config!!.get(Constants.CONFIG_CATEGORY_CLIENT, "enableCloudsVenus", enableCloudsVenus)
                    prop.setComment("Enable/Disable Render Clouds on Venus.")
                    prop.setLanguageKey("gc.configgui.enableCloudsVenus").setRequiresMcRestart(false)
                    enableCloudsVenus = prop.getBoolean(true)
                    propOrder.add(prop.getName())

                    prop =
                        config!!.get(Constants.CONFIG_CATEGORY_CLIENT, "enableBetterMoonOnSky", enableBetterMoonOnSky)
                    prop.setComment("Enable/Disable Render Clouds on Venus.")
                    prop.setLanguageKey("gc.configgui.enableBetterMoonOnSky").setRequiresMcRestart(false)
                    enableBetterMoonOnSky = prop.getBoolean(true)
                    propOrder.add(prop.getName())

                    prop = config!!.get(
                        Constants.CONFIG_CATEGORY_GENERAL, "celestialBodyTiers",
                        arrayOf<String>(
                            "planet.example:5"
                        )
                    )

                    prop.setComment("Set custom tier for Celestial Bodies. Example: planet.mars:5")
                    prop.setLanguageKey("gc.configgui.сelestialBodyTiers").setRequiresMcRestart(true)
                    celestialBodyTiers = prop.getStringList()
                    propOrder.add(prop.getName())

                    prop = config!!.get(
                        Constants.CONFIG_CATEGORY_WORLDGEN, "asteroidsCoreBlocks",
                        arrayOf<String>(
                            "minecraft:air:0:5:0.2"
                        )
                    )

                    prop.setComment("Add custom core blocks in asteroids (GC Asteroids dimension)  Example: modid:block:meta:probability:thickness")
                    prop.setLanguageKey("gc.configgui.asteroidsCoreBlocks").setRequiresMcRestart(true)
                    asteroidsCoreBlocks = prop.getStringList()
                    propOrder.add(prop.getName())
                }

                prop = config!!.get(Constants.CONFIG_CATEGORY_CLIENT, "enableRenderAtmosphere", true)
                prop.setComment("Enable/Disable render planet atmosphere on sky.")
                prop.setLanguageKey("ac.configgui.enableRenderAtmosphere")
                enableRenderAtmosphere = prop.getBoolean(true)
                propOrder.add(prop.getName())


                prop = config!!.get(Constants.CONFIG_CATEGORY_CLIENT, "enableRotateStars", true)
                prop.setComment("Enable/Disable rotate stars on sky.")
                prop.setLanguageKey("ac.configgui.enableRotateStars")
                enableRotateStars = prop.getBoolean(true)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_CLIENT, "enableColorStars", false)
                prop.setComment("Enable/Disable render color stars on sky.")
                prop.setLanguageKey("ac.configgui.enableColorStars")
                enableColorStars = prop.getBoolean(false)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_GENERAL, "enableDebug", false)
                prop.setComment("Enable/Disable debug mode.")
                prop.setLanguageKey("ac.configgui.enableDebug")
                enableDebug = prop.getBoolean(false)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_DIMENSIONS, "enableWeatherDamage", true)
                prop.setComment("Enable/Disable damage from weather (acid_rain, dust_storm, etc).")
                prop.setLanguageKey("ac.configgui.enableWeatherDamage")
                enableWeatherDamage = prop.getBoolean(true)
                propOrder.add(prop.getName())

                prop = config!!.get(CATEGORY_MAP, "enableSublightEngine", true)
                prop.setComment("Enable/Disable required sublight engine for travel in other star systems.")
                prop.setLanguageKey("ac.configgui.enableSublightEngine")
                enableSublightEngine = prop.getBoolean(true)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_DIFFICULTY, "stormDamageRate", stormDamageRate.toDouble())
                prop.setComment("Damage rate for duststorms on celestial bodies.")
                prop.setLanguageKey("ac.configgui.stormDamageRate")
                stormDamageRate = prop.getDouble().toFloat()
                propOrder.add(prop.getName())

                prop = config!!.get(
                    Constants.CONFIG_CATEGORY_DIFFICULTY,
                    "toxicRainDamageRate",
                    toxicRainDamageRate.toDouble()
                )
                prop.setComment("Damage rate for toxic rain on celestial bodies.")
                prop.setLanguageKey("ac.configgui.toxicRainDamageRate")
                toxicRainDamageRate = prop.getDouble().toFloat()
                propOrder.add(prop.getName())

                prop = config!!.get(
                    Constants.CONFIG_CATEGORY_DIFFICULTY,
                    "atmospherePressureDamageRate",
                    atmospherePressureDamageRate.toDouble()
                )
                prop.setComment("Damage rate for atmosphere pressure on celestial bodies.")
                prop.setLanguageKey("ac.configgui.atmospherePressureDamageRate")
                atmospherePressureDamageRate = prop.getDouble().toFloat()
                propOrder.add(prop.getName())

                prop = config!!.get(
                    Constants.CONFIG_CATEGORY_DIFFICULTY,
                    "solarRadiationDamageRate",
                    solarRadiationDamageRate.toDouble()
                )
                prop.setComment("Damage rate for solar radiation on celestial bodies.")
                prop.setLanguageKey("ac.configgui.solarRadiationDamageRate")
                solarRadiationDamageRate = prop.getDouble().toFloat()
                propOrder.add(prop.getName())


                config!!.setCategoryPropertyOrder(Configuration.CATEGORY_GENERAL, propOrder)

                if (config!!.hasChanged()) {
                    config!!.save()
                }
            } catch (e: Exception) {
                FMLLog.log(Level.ERROR, e, "Asmodeus Core has a problem loading it's config")
            }
        }


        @JvmStatic
        fun getConfigElements(): MutableList<IConfigElement?> {
            val list: MutableList<IConfigElement?> = ArrayList<IConfigElement?>()
            list.addAll(ConfigElement(config!!.getCategory(Constants.CONFIG_CATEGORY_DIFFICULTY)).getChildElements())
            list.addAll(ConfigElement(config!!.getCategory(Constants.CONFIG_CATEGORY_GENERAL)).getChildElements())
            list.addAll(ConfigElement(config!!.getCategory(Constants.CONFIG_CATEGORY_CLIENT)).getChildElements())
            //list.addAll(new ConfigElement(config.getCategory(Constants.CONFIG_CATEGORY_CONTROLS)).getChildElements());
            //list.addAll(new ConfigElement(config.getCategory(Constants.CONFIG_CATEGORY_COMPATIBILITY)).getChildElements());
            //list.addAll(new ConfigElement(config.getCategory(Constants.CONFIG_CATEGORY_WORLDGEN)).getChildElements());
            //list.addAll(new ConfigElement(config.getCategory(Constants.CONFIG_CATEGORY_SERVER)).getChildElements());
            //list.addAll(new ConfigElement(config.getCategory(Constants.CONFIG_CATEGORY_DIMENSIONS)).getChildElements());
            //list.addAll(new ConfigElement(config.getCategory(Constants.CONFIG_CATEGORY_SCHEMATIC)).getChildElements());
            //list.addAll(new ConfigElement(config.getCategory(Constants.CONFIG_CATEGORY_ACHIEVEMENTS)).getChildElements());
            //list.addAll(new ConfigElement(config.getCategory(Constants.CONFIG_CATEGORY_ENTITIES)).getChildElements());
            //list.addAll(new ConfigElement(config.getCategory(Constants.CONFIG_CATEGORY_KEYS)).getChildElements());
            list.addAll(ConfigElement(config!!.getCategory(CATEGORY_MAP)).getChildElements())
            list.addAll(ConfigElement(config!!.getCategory("development")).getChildElements())

            return list
        }
    }
}
