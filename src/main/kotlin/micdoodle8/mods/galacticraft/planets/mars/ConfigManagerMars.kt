package micdoodle8.mods.galacticraft.planets.mars

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import net.minecraftforge.common.config.Configuration
import net.minecraftforge.common.config.Property
import net.minecraftforge.fml.common.FMLLog
import org.apache.logging.log4j.Level
import java.io.File
import java.lang.Exception

open class ConfigManagerMars
constructor(file: File, update: Boolean) {
    init {
        if (!loaded) {
            config = Configuration(file)
            syncConfig(true, update)
        }
    }

    companion object {
        @JvmField
        var loaded: Boolean = false

        @JvmField
        var config: Configuration? = null

        // DIMENSIONS
        @JvmField
        var dimensionIDMars: Int = 0

        // SCHEMATIC
        @JvmField
        var idSchematicRocketT2: Int = 0

        @JvmField
        var idSchematicCargoRocket: Int = 0

        // GENERAL
        @JvmField
        var launchControllerChunkLoad: Boolean = false

        @JvmField
        var launchControllerAllDims: Boolean = false

        // WGEN
        @JvmField
        var disableDeshGen: Boolean = false

        @JvmField
        var disableTinGen: Boolean = false

        @JvmField
        var disableCopperGen: Boolean = false

        @JvmField
        var disableIronGen: Boolean = false

        @JvmStatic
        fun syncConfig(load: Boolean, update: Boolean) {
            try {
                var prop: Property

                if (!config!!.isChild && load) {
                    config!!.load()
                }

                prop = config!!.get(Constants.CONFIG_CATEGORY_DIMENSIONS, "dimensionIDMars", -29)
                prop.setComment("Dimension ID for Mars")
                prop.setLanguageKey("gc.configgui.dimension_id_mars").setRequiresMcRestart(true)
                dimensionIDMars = prop.getInt()
                GalacticraftPlanets.finishProp(prop, Constants.CONFIG_CATEGORY_DIMENSIONS)

                //
                prop = config!!.get(Constants.CONFIG_CATEGORY_SCHEMATIC, "idSchematicRocketT2", 2)
                prop.setComment("Schematic ID for Tier 2 Rocket, must be unique.")
                prop.setLanguageKey("gc.configgui.id_schematic_rocket_t2")
                idSchematicRocketT2 = prop.getInt(2)
                GalacticraftPlanets.finishProp(prop, Constants.CONFIG_CATEGORY_SCHEMATIC)

                prop = config!!.get(Constants.CONFIG_CATEGORY_SCHEMATIC, "idSchematicCargoRocket", 3)
                prop.setComment("Schematic ID for Cargo Rocket, must be unique.")
                prop.setLanguageKey("gc.configgui.id_schematic_cargo_rocket")
                idSchematicCargoRocket = prop.getInt(3)
                GalacticraftPlanets.finishProp(prop, Constants.CONFIG_CATEGORY_SCHEMATIC)

                //
                prop = config!!.get(Constants.CONFIG_CATEGORY_GENERAL, "launchControllerChunkLoad", true)
                prop.setComment("Whether or not the launch controller acts as a chunk loader. Will cause issues if disabled!")
                prop.setLanguageKey("gc.configgui.launch_controller_chunk_load")
                launchControllerChunkLoad = prop.getBoolean(true)
                GalacticraftPlanets.finishProp(prop, Constants.CONFIG_CATEGORY_GENERAL)

                prop = config!!.get(Constants.CONFIG_CATEGORY_GENERAL, "launchControllerAllDims", false)
                prop.setComment("May rarely cause issues if enabled, depends on how the other mod's dimensions are.")
                prop.setLanguageKey("gc.configgui.launch_controller_all_dims")
                launchControllerAllDims = prop.getBoolean(false)
                GalacticraftPlanets.finishProp(prop, Constants.CONFIG_CATEGORY_GENERAL)

                prop = config!!.get(
                    if (update) Constants.CONFIG_CATEGORY_GENERAL else Constants.CONFIG_CATEGORY_WORLDGEN,
                    "Disable Iron Ore Gen on Mars",
                    false
                )
                prop.setComment("Disable Iron Ore Gen on Mars.")
                prop.setLanguageKey("gc.configgui.disable_iron_gen_mars")
                if (update) {
                    prop = config!!.get(
                        Constants.CONFIG_CATEGORY_WORLDGEN,
                        prop.getName(),
                        prop.getBoolean(),
                        prop.getComment()
                    )
                    prop.setLanguageKey(prop.getLanguageKey())
                    config!!.getCategory(Constants.CONFIG_CATEGORY_GENERAL).remove(prop.getName())
                }
                disableIronGen = prop.getBoolean(false)
                GalacticraftPlanets.finishProp(prop, Constants.CONFIG_CATEGORY_WORLDGEN)

                prop = config!!.get(
                    if (update) Constants.CONFIG_CATEGORY_GENERAL else Constants.CONFIG_CATEGORY_WORLDGEN,
                    "Disable Copper Ore Gen on Mars",
                    false
                )
                prop.setComment("Disable Copper Ore Gen on Mars.")
                prop.setLanguageKey("gc.configgui.disable_copper_gen_mars")
                if (update) {
                    prop = config!!.get(
                        Constants.CONFIG_CATEGORY_WORLDGEN,
                        prop.getName(),
                        prop.getBoolean(),
                        prop.getComment()
                    )
                    prop.setLanguageKey(prop.getLanguageKey())
                    config!!.getCategory(Constants.CONFIG_CATEGORY_GENERAL).remove(prop.getName())
                }
                disableCopperGen = prop.getBoolean(false)
                GalacticraftPlanets.finishProp(prop, Constants.CONFIG_CATEGORY_WORLDGEN)

                prop = config!!.get(
                    if (update) Constants.CONFIG_CATEGORY_GENERAL else Constants.CONFIG_CATEGORY_WORLDGEN,
                    "Disable Tin Ore Gen on Mars",
                    false
                )
                prop.setComment("Disable Tin Ore Gen on Mars.")
                prop.setLanguageKey("gc.configgui.disable_tin_gen_mars")
                if (update) {
                    prop = config!!.get(
                        Constants.CONFIG_CATEGORY_WORLDGEN,
                        prop.getName(),
                        prop.getBoolean(),
                        prop.getComment()
                    )
                    prop.setLanguageKey(prop.getLanguageKey())
                    config!!.getCategory(Constants.CONFIG_CATEGORY_GENERAL).remove(prop.getName())
                }
                disableTinGen = prop.getBoolean(false)
                GalacticraftPlanets.finishProp(prop, Constants.CONFIG_CATEGORY_WORLDGEN)

                prop = config!!.get(
                    if (update) Constants.CONFIG_CATEGORY_GENERAL else Constants.CONFIG_CATEGORY_WORLDGEN,
                    "Disable Desh Ore Gen on Mars",
                    false
                )
                prop.setComment("Disable Desh Ore Gen on Mars.")
                prop.setLanguageKey("gc.configgui.disable_desh_gen_mars")
                if (update) {
                    prop = config!!.get(
                        Constants.CONFIG_CATEGORY_WORLDGEN,
                        prop.getName(),
                        prop.getBoolean(),
                        prop.getComment()
                    )
                    prop.setLanguageKey(prop.getLanguageKey())
                    config!!.getCategory(Constants.CONFIG_CATEGORY_GENERAL).remove(prop.getName())
                }
                disableDeshGen = prop.getBoolean(false)
                GalacticraftPlanets.finishProp(prop, Constants.CONFIG_CATEGORY_WORLDGEN)
            } catch (e: Exception) {
                FMLLog.log(Level.ERROR, e, "Galacticraft Mars (Planets) has a problem loading its config")
            }
        }
    }
}
