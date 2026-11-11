package galaxyspace.core.configs

import java.util.ArrayList

import galaxyspace.core.util.GSConstants
import micdoodle8.mods.galacticraft.core.Constants
import foundation.config.FoundationConfigElement as ConfigElement
import foundation.config.FoundationConfiguration as Configuration
import foundation.config.FoundationConfigProperty as Property
import foundation.config.FoundationConfigEntry as IConfigElement
import foundation.logging.FoundationLog as FMLLog
import org.apache.logging.log4j.Level
import java.io.File

open class GSConfigDimensions
constructor(file: File) {
    init {
        if (!loaded) {
            config = Configuration(file)
            syncConfig(true)
        }
    }

    companion object {
        @JvmField
        var loaded: Boolean = false

        @JvmField
        var config: Configuration? = null

        // DIMENSIONS
        @JvmField
var dimensionIDMercury: Int = 0

        @JvmField
var dimensionIDVenus: Int = 0

        @JvmField
var dimensionIDCeres: Int = 0

        @JvmField
var dimensionIDJupiter: Int = 0

        @JvmField
var dimensionIDPluto: Int = 0

        @JvmField
var dimensionIDKuiperBelt: Int = 0

        @JvmField
var dimensionIDHaumea: Int = 0

        @JvmField
var dimensionIDMakemake: Int = 0

        @JvmField
var dimensionIDTest: Int = 0

        @JvmField
var dimensionIDPhobos: Int = 0

        @JvmField
var dimensionIDDeimos: Int = 0

        @JvmField
var dimensionIDIo: Int = 0

        @JvmField
var dimensionIDEuropa: Int = 0

        @JvmField
var dimensionIDGanymede: Int = 0

        @JvmField
var dimensionIDCallisto: Int = 0

        @JvmField
var dimensionIDEnceladus: Int = 0

        @JvmField
var dimensionIDTitan: Int = 0

        @JvmField
var dimensionIDOberon: Int = 0

        @JvmField
var dimensionIDMiranda: Int = 0

        @JvmField
var dimensionIDProteus: Int = 0

        @JvmField
var dimensionIDTriton: Int = 0

        @JvmField
var enableMercury: Boolean = false

        @JvmField
var enableVenus: Boolean = false

        @JvmField
var enableCeres: Boolean = false

        @JvmField
var enableKuiperBelt: Boolean = false

        @JvmField
var enablePluto: Boolean = false

        @JvmField
var enableHaumea: Boolean = false

        @JvmField
var enableMakemake: Boolean = false

        @JvmField
var enablePhobos: Boolean = false

        @JvmField
var enableDeimos: Boolean = false

        @JvmField
var enableEuropa: Boolean = false

        @JvmField
var enableCallisto: Boolean = false

        @JvmField
var enableIo: Boolean = false

        @JvmField
var enableGanymede: Boolean = false

        @JvmField
var enableEnceladus: Boolean = false

        @JvmField
var enableTitan: Boolean = false

        @JvmField
var enableOberon: Boolean = false

        @JvmField
var enableMiranda: Boolean = false

        @JvmField
var enableProteus: Boolean = false

        @JvmField
var enableTriton: Boolean = false

        @JvmField
var enableJupiter: Boolean = false

        @JvmField
var enableSaturn: Boolean = false

        @JvmField
var enableUranus: Boolean = false

        @JvmField
var enableNeptune: Boolean = false

        @JvmField
var enableJupiterMoons: Boolean = false

        @JvmField
var enableSaturnMoons: Boolean = false

        @JvmField
var enableUranusMoons: Boolean = false

        @JvmField
var enableNeptuneMoons: Boolean = false

        @JvmField
        var enableVenusSpaceStation: Boolean = false

        @JvmField
        var enableMarsSpaceStation: Boolean = false

        @JvmField
        var idDimensionMarsOrbit: Int = 0

        @JvmField
        var idDimensionMarsOrbitStatic: Int = 0

        @JvmField
        var idDimensionVenusOrbit: Int = 0

        @JvmField
        var idDimensionVenusOrbitStatic: Int = 0


        @JvmStatic
        fun syncConfig(load: Boolean) {
            val propOrder: MutableList<String?> = ArrayList<String?>()

            try {
                var prop: Property

                if (!config!!.isChild) {
                    if (load) {
                        config!!.load()
                    }
                }


                prop = config!!.get(Constants.CONFIG_CATEGORY_DIMENSIONS, "dimensionIDMercury", -1005)
                prop.setComment("Dimension ID for Mercury")
                prop.setLanguageKey("gc.configgui.dimensionIDMerucry").setRequiresMcRestart(true)
                dimensionIDMercury = prop.getInt()
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_DIMENSIONS, "dimensionIDVenus", -1006)
                prop.setComment("Dimension ID for Venus")
                prop.setLanguageKey("gc.configgui.dimensionIDVenus").setRequiresMcRestart(true)
                dimensionIDVenus = prop.getInt()
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_DIMENSIONS, "dimensionIDCeres", -1007)
                prop.setComment("Dimension ID for Ceres")
                prop.setLanguageKey("gc.configgui.dimensionIDCeres").setRequiresMcRestart(true)
                dimensionIDCeres = prop.getInt()
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_DIMENSIONS, "dimensionIDPluto", -1008)
                prop.setComment("Dimension ID for Pluto")
                prop.setLanguageKey("gc.configgui.dimensionIDPluto").setRequiresMcRestart(true)
                dimensionIDPluto = prop.getInt()
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_DIMENSIONS, "dimensionIDKuiperBelt", -1009)
                prop.setComment("Dimension ID for Kuiper Belt")
                prop.setLanguageKey("gc.configgui.dimensionIDKuiperBelt").setRequiresMcRestart(true)
                dimensionIDKuiperBelt = prop.getInt()
                propOrder.add(prop.getName())


                /*prop = config.get(Constants.CONFIG_CATEGORY_DIMENSIONS, "dimensionIDMakemake", -1011);
            prop.setComment("Dimension ID for Makemake");
            prop.setLanguageKey("gc.configgui.dimensionIDMakemake").setRequiresMcRestart(true);
            dimensionIDMakemake = prop.getInt();
            propOrder.add(prop.getName());*/
                prop = config!!.get(Constants.CONFIG_CATEGORY_DIMENSIONS, "dimensionIDPhobos", -1012)
                prop.setComment("Dimension ID for Phobos (Mars moon)")
                prop.setLanguageKey("gc.configgui.dimensionIDPhobos").setRequiresMcRestart(true)
                dimensionIDPhobos = prop.getInt()
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_DIMENSIONS, "dimensionIDDeimos", -1013)
                prop.setComment("Dimension ID for Deimos (Mars moon)")
                prop.setLanguageKey("gc.configgui.dimensionIDDeimos").setRequiresMcRestart(true)
                dimensionIDDeimos = prop.getInt()
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_DIMENSIONS, "dimensionIDIo", -1014)
                prop.setComment("Dimension ID for Io (Jupiter moon)")
                prop.setLanguageKey("gc.configgui.dimensionIDIo").setRequiresMcRestart(true)
                dimensionIDIo = prop.getInt()
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_DIMENSIONS, "dimensionIDEuropa", -1015)
                prop.setComment("Dimension ID for Europa (Jupiter moon)")
                prop.setLanguageKey("gc.configgui.dimensionIDEuropa").setRequiresMcRestart(true)
                dimensionIDEuropa = prop.getInt()
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_DIMENSIONS, "dimensionIDGanymede", -1016)
                prop.setComment("Dimension ID for Ganymede (Jupiter moon)")
                prop.setLanguageKey("gc.configgui.dimensionIDGanymede").setRequiresMcRestart(true)
                dimensionIDGanymede = prop.getInt()
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_DIMENSIONS, "dimensionIDEnceladus", -1017)
                prop.setComment("Dimension ID for Enceladus (Saturn moon)")
                prop.setLanguageKey("gc.configgui.dimensionIDEnceladus").setRequiresMcRestart(true)
                dimensionIDEnceladus = prop.getInt()
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_DIMENSIONS, "dimensionIDTitan", -1018)
                prop.setComment("Dimension ID for Titan (Saturn moon)")
                prop.setLanguageKey("gc.configgui.dimensionIDTitan").setRequiresMcRestart(true)
                dimensionIDTitan = prop.getInt()
                propOrder.add(prop.getName())

                /*prop = config.get(Constants.CONFIG_CATEGORY_DIMENSIONS, "dimensionIDOberon", -1019);
            prop.setComment("Dimension ID for Oberon (Uranus moon)");
            prop.setLanguageKey("gc.configgui.dimensionIDOberon").setRequiresMcRestart(true);
            dimensionIDOberon = prop.getInt();
            propOrder.add(prop.getName());
            
            prop = config.get(Constants.CONFIG_CATEGORY_DIMENSIONS, "dimensionIDProteus", -1020);
            prop.setComment("Dimension ID for Proteus (Neptune moon)");
            prop.setLanguageKey("gc.configgui.dimensionIDProteus").setRequiresMcRestart(true);
            dimensionIDProteus = prop.getInt();
            propOrder.add(prop.getName());*/
                prop = config!!.get(Constants.CONFIG_CATEGORY_DIMENSIONS, "dimensionIDTriton", -1021)
                prop.setComment("Dimension ID for Triton (Neptune moon)")
                prop.setLanguageKey("gc.configgui.dimensionIDTriton").setRequiresMcRestart(true)
                dimensionIDTriton = prop.getInt()
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_DIMENSIONS, "dimensionIDCallisto", -1022)
                prop.setComment("Dimension ID for Callisto (Jupiter Moon)")
                prop.setLanguageKey("gc.configgui.dimensionIDCallisto").setRequiresMcRestart(true)
                dimensionIDCallisto = prop.getInt()
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_DIMENSIONS, "dimensionIDHaumea", -1023)
                prop.setComment("Dimension ID for Haumea")
                prop.setLanguageKey("gc.configgui.dimensionIDHaumea").setRequiresMcRestart(true)
                dimensionIDHaumea = prop.getInt()
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_DIMENSIONS, "dimensionIDMiranda", -1024)
                prop.setComment("Dimension ID for Miranda (Uranus moon)")
                prop.setLanguageKey("gc.configgui.dimensionIDMiranda").setRequiresMcRestart(true)
                dimensionIDMiranda = prop.getInt()
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_DIMENSIONS, "dimensionIDJupiter", -1026)
                prop.setComment("Dimension ID for Jupiter (Only for Space Station)")
                prop.setLanguageKey("gc.configgui.dimensionIDJupiter").setRequiresMcRestart(true)
                dimensionIDJupiter = prop.getInt()
                propOrder.add(prop.getName())

                prop = config!!.get(GSConstants.CATEGORY_DEVELOMPENT, "dimensionIDTest", -1234)
                prop.setComment("Dimension ID for Test planet (only for debug)")
                prop.setLanguageKey("gc.configgui.dimensionIDTest").setRequiresMcRestart(true)
                dimensionIDTest = prop.getInt()
                propOrder.add(prop.getName())

                /*
            prop = config.get(Constants.CONFIG_CATEGORY_DIMENSIONS, "dimensionIDEris", -1030);
            prop.comment = "Dimension ID for Eris";
            prop.setLanguageKey("gc.configgui.dimensionIDEris").setRequiresMcRestart(true);
            dimensionIDEris = prop.getInt();
            propOrder.add(prop.getName());
            */
                // Space Stations -----------------------------------------------------------------
                prop = config!!.get(Constants.CONFIG_CATEGORY_DIMENSIONS, "idDimensionMarsOrbit", -1127)
                prop.setComment("WorldProvider ID for Mars Space Stations (advanced: do not change unless you have conflicts)")
                prop.setLanguageKey("gc.configgui.idDimensionMarsOrbit").setRequiresMcRestart(true)
                idDimensionMarsOrbit = prop.getInt()
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_DIMENSIONS, "idDimensionMarsOrbitStatic", -1126)
                prop.setComment("WorldProvider ID for Static Mars Space Stations (advanced: do not change unless you have conflicts)")
                prop.setLanguageKey("gc.configgui.idDimensionMarsOrbitStatic").setRequiresMcRestart(true)
                idDimensionMarsOrbitStatic = prop.getInt()
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_DIMENSIONS, "idDimensionVenusOrbit", -1128)
                prop.setComment("WorldProvider ID for Venus Space Stations (advanced: do not change unless you have conflicts)")
                prop.setLanguageKey("gc.configgui.idDimensionVenusOrbit").setRequiresMcRestart(true)
                idDimensionVenusOrbit = prop.getInt()
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_DIMENSIONS, "idDimensionVenusOrbitStatic", -1129)
                prop.setComment("WorldProvider ID for Static Venus Space Stations (advanced: do not change unless you have conflicts)")
                prop.setLanguageKey("gc.configgui.idDimensionVenusOrbitStatic").setRequiresMcRestart(true)
                idDimensionVenusOrbitStatic = prop.getInt()
                propOrder.add(prop.getName())


                //----------------------------------------------------------------------------------
                prop = config!!.get(Constants.CONFIG_CATEGORY_GENERAL, "enableMercury", true)
                prop.setComment("Enable/Disable Mercury")
                prop.setLanguageKey("gc.configgui.enableMercury").setRequiresMcRestart(true)
                enableMercury = prop.getBoolean(true)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_GENERAL, "enableVenus", true)
                prop.setComment("Enable/Disable Venus")
                prop.setLanguageKey("gc.configgui.enableVenus").setRequiresMcRestart(true)
                enableVenus = prop.getBoolean(true)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_GENERAL, "enableCeres", true)
                prop.setComment("Enable/Disable Ceres")
                prop.setLanguageKey("gc.configgui.enableCeres").setRequiresMcRestart(true)
                enableCeres = prop.getBoolean(true)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_GENERAL, "enablePluto", true)
                prop.setComment("Enable/Disable Pluto")
                prop.setLanguageKey("gc.configgui.enablePluto").setRequiresMcRestart(true)
                enablePluto = prop.getBoolean(true)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_GENERAL, "enableKuiperBelt", true)
                prop.setComment("Enable/Disable Kuiper Belt")
                prop.setLanguageKey("gc.configgui.enableKuiperBelt").setRequiresMcRestart(true)
                enableKuiperBelt = prop.getBoolean(true)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_GENERAL, "enableHaumea", true)
                prop.setComment("Enable/Disable Haumea")
                prop.setLanguageKey("gc.configgui.enableHaumea").setRequiresMcRestart(true)
                enableHaumea = prop.getBoolean(true)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_GENERAL, "enableMakemake", true)
                prop.setComment("Enable/Disable Makemake")
                prop.setLanguageKey("gc.configgui.enableMakemake").setRequiresMcRestart(true)
                enableMakemake = prop.getBoolean(true)
                propOrder.add(prop.getName())
                /*
            prop = config.get(Constants.CONFIG_CATEGORY_GENERAL, "enableEris", true);
            prop.comment = "Enable/Disable Eris";
            prop.setLanguageKey("gc.configgui.enableEris").setRequiresMcRestart(true);
            enableEris = prop.getBoolean(true);
            propOrder.add(prop.getName());
            */
                prop = config!!.get(Constants.CONFIG_CATEGORY_GENERAL, "enablePhobos", true)
                prop.setComment("Enable/Disable Phobos (Mars Moon)")
                prop.setLanguageKey("gc.configgui.enablePhobos").setRequiresMcRestart(true)
                enablePhobos = prop.getBoolean(true)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_GENERAL, "enableDeimos", true)
                prop.setComment("Enable/Disable Deimos (Mars Moon)")
                prop.setLanguageKey("gc.configgui.enableDeimos").setRequiresMcRestart(true)
                enableDeimos = prop.getBoolean(true)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_GENERAL, "enableEuropa", true)
                prop.setComment("Enable/Disable Europa (Jupiter Moon)")
                prop.setLanguageKey("gc.configgui.enableEuropa").setRequiresMcRestart(true)
                enableEuropa = prop.getBoolean(true)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_GENERAL, "enableIo", true)
                prop.setComment("Enable/Disable Io (Jupiter Moon)")
                prop.setLanguageKey("gc.configgui.enableIo").setRequiresMcRestart(true)
                enableIo = prop.getBoolean(true)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_GENERAL, "enableGanymede", true)
                prop.setComment("Enable/Disable Ganymede (Jupiter Moon)")
                prop.setLanguageKey("gc.configgui.enableGanymede").setRequiresMcRestart(true)
                enableGanymede = prop.getBoolean(true)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_GENERAL, "enableCallisto", true)
                prop.setComment("Enable/Disable Callisto (Jupiter Moon)")
                prop.setLanguageKey("gc.configgui.enableCallisto").setRequiresMcRestart(true)
                enableCallisto = prop.getBoolean(true)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_GENERAL, "enableEnceladus", true)
                prop.setComment("Enable/Disable Enceladus (Saturn Moon)")
                prop.setLanguageKey("gc.configgui.enableEnceladus").setRequiresMcRestart(true)
                enableEnceladus = prop.getBoolean(true)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_GENERAL, "enableTitan", true)
                prop.setComment("Enable/Disable Titan (Saturn Moon)")
                prop.setLanguageKey("gc.configgui.enableTitan").setRequiresMcRestart(true)
                enableTitan = prop.getBoolean(true)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_GENERAL, "enableMiranda", true)
                prop.setComment("Enable/Disable Miranda (Uranus Moon)")
                prop.setLanguageKey("gc.configgui.enableMiranda").setRequiresMcRestart(true)
                enableMiranda = prop.getBoolean(true)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_GENERAL, "enableOberon", true)
                prop.setComment("Enable/Disable Oberon (Uranus Moon)")
                prop.setLanguageKey("gc.configgui.enableOberon").setRequiresMcRestart(true)
                enableOberon = prop.getBoolean(true)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_GENERAL, "enableProteus", true)
                prop.setComment("Enable/Disable Proteus (Neptune Moon)")
                prop.setLanguageKey("gc.configgui.enableProteus").setRequiresMcRestart(true)
                enableProteus = prop.getBoolean(true)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_GENERAL, "enableTriton", true)
                prop.setComment("Enable/Disable Triton (Neptune Moon)")
                prop.setLanguageKey("gc.configgui.enableTriton").setRequiresMcRestart(true)
                enableTriton = prop.getBoolean(true)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_GENERAL, "enableVenusSpaceStation", true)
                prop.setComment("Enable/Disable Venus Space Station")
                prop.setLanguageKey("gc.configgui.enableVenusSpaceStation").setRequiresMcRestart(true)
                enableVenusSpaceStation = prop.getBoolean(true)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_GENERAL, "enableMarsSpaceStation", true)
                prop.setComment("Enable/Disable Mars Space Station")
                prop.setLanguageKey("gc.configgui.enableMarsSpaceStation").setRequiresMcRestart(true)
                enableMarsSpaceStation = prop.getBoolean(true)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_GENERAL, "enableJupiter", true)
                prop.setComment("Enable/Disable Jupiter (only on map for moons)")
                prop.setLanguageKey("gc.configgui.enableJupiter").setRequiresMcRestart(true)
                enableJupiter = prop.getBoolean(true)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_GENERAL, "enableSaturn", true)
                prop.setComment("Enable/Disable Saturn (only on map for moons)")
                prop.setLanguageKey("gc.configgui.enableSaturn").setRequiresMcRestart(true)
                enableSaturn = prop.getBoolean(true)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_GENERAL, "enableUranus", true)
                prop.setComment("Enable/Disable Uranus (only on map for moons)")
                prop.setLanguageKey("gc.configgui.enableUranus").setRequiresMcRestart(true)
                enableUranus = prop.getBoolean(true)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_GENERAL, "enableNeptune", true)
                prop.setComment("Enable/Disable Neptune (only on map for moons)")
                prop.setLanguageKey("gc.configgui.enableNeptune").setRequiresMcRestart(true)
                enableNeptune = prop.getBoolean(true)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_GENERAL, "enableJupiterMoons", true)
                prop.setComment("Force Enable Jupiter Moons (for starmaker pack use)")
                prop.setLanguageKey("gc.configgui.enableJupiterMoons").setRequiresMcRestart(true)
                enableJupiterMoons = prop.getBoolean(false)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_GENERAL, "enableSaturnMoons", true)
                prop.setComment("Force Enable Saturn Moons (for starmaker pack use)")
                prop.setLanguageKey("gc.configgui.enableSaturnMoons").setRequiresMcRestart(true)
                enableSaturnMoons = prop.getBoolean(false)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_GENERAL, "enableUranusMoons", true)
                prop.setComment("Force Enable Uranus Moons (for starmaker pack use)")
                prop.setLanguageKey("gc.configgui.enableUranusMoons").setRequiresMcRestart(true)
                enableUranusMoons = prop.getBoolean(false)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_GENERAL, "enableNeptuneMoons", true)
                prop.setComment("Force Enable Neptune Moons (for starmaker pack use)")
                prop.setLanguageKey("gc.configgui.enableNeptuneMoons").setRequiresMcRestart(true)
                enableNeptuneMoons = prop.getBoolean(false)
                propOrder.add(prop.getName())

                config!!.setCategoryPropertyOrder(Configuration.CATEGORY_GENERAL, propOrder)

                if (config!!.hasChanged()) {
                    config!!.save()
                }
            } catch (e: Exception) {
                FMLLog.log(Level.ERROR, e, "GalaxySpace (Planets) has a problem loading it's config")
            }
        }

        @JvmStatic
        fun getConfigElements(): MutableList<IConfigElement?> {
            val list: MutableList<IConfigElement?> = ArrayList<IConfigElement?>()
            list.addAll(ConfigElement(config!!.getCategory(Constants.CONFIG_CATEGORY_DIFFICULTY)).getChildElements())
            list.addAll(ConfigElement(config!!.getCategory(Constants.CONFIG_CATEGORY_GENERAL)).getChildElements())
            list.addAll(ConfigElement(config!!.getCategory(Constants.CONFIG_CATEGORY_CLIENT)).getChildElements())
            list.addAll(ConfigElement(config!!.getCategory(Constants.CONFIG_CATEGORY_CONTROLS)).getChildElements())
            list.addAll(ConfigElement(config!!.getCategory(Constants.CONFIG_CATEGORY_COMPATIBILITY)).getChildElements())
            list.addAll(ConfigElement(config!!.getCategory(Constants.CONFIG_CATEGORY_WORLDGEN)).getChildElements())
            list.addAll(ConfigElement(config!!.getCategory(Constants.CONFIG_CATEGORY_SERVER)).getChildElements())
            list.addAll(ConfigElement(config!!.getCategory(Constants.CONFIG_CATEGORY_DIMENSIONS)).getChildElements())
            list.addAll(ConfigElement(config!!.getCategory(Constants.CONFIG_CATEGORY_SCHEMATIC)).getChildElements())
            list.addAll(ConfigElement(config!!.getCategory(Constants.CONFIG_CATEGORY_ACHIEVEMENTS)).getChildElements())
            list.addAll(ConfigElement(config!!.getCategory(Constants.CONFIG_CATEGORY_ENTITIES)).getChildElements())

            return list
        }
    }
}
