package micdoodle8.mods.galacticraft.planets.venus


import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.planets.mars.ConfigManagerMars
import foundation.config.FoundationConfiguration as Configuration
import foundation.config.FoundationConfigProperty as Property
import foundation.logging.FoundationLog as FMLLog
import org.apache.logging.log4j.Level
import java.io.File
import java.lang.Exception

open class ConfigManagerVenus
constructor(file: File) {
    init {
        if (!loaded) {
            if (file.exists()) {
                config = Configuration(file)
                syncConfig(true, true)
                // Persist imported values before removing the old file
                requireNotNull(ConfigManagerMars.config).save()
                file.delete()
                config = ConfigManagerMars.config
                syncConfig(false, false)
            } else {
                config = ConfigManagerMars.config
                syncConfig(true, false)
            }
        }
    }

    companion object {
        @JvmField
        var loaded: Boolean = false

        @JvmField
        var config: Configuration? = null

        // DIMENSIONS
        @JvmField
        var dimensionIDVenus: Int = 0

        @JvmField
        var disableAmbientLightning: Boolean = false

        @JvmField
        var disableAluminumGen: Boolean = false

        @JvmField
        var disableCopperGen: Boolean = false

        @JvmField
        var disableGalenaGen: Boolean = false

        @JvmField
        var disableQuartzGen: Boolean = false

        @JvmField
        var disableSiliconGen: Boolean = false

        @JvmField
        var disableTinGen: Boolean = false

        @JvmField
        var disableSolarGen: Boolean = false

        @JvmStatic
        fun syncConfig(load: Boolean, update: Boolean) {
            try {
                var prop: Property
                val propCopy: Property

                if (!config!!.isChild) {
                    if (update) {
                        config!!.load()
                    }
                }

                prop = config!!.get(Constants.CONFIG_CATEGORY_DIMENSIONS, "dimensionIDVenus", -31)
                prop.setComment("Dimension ID for Venus")
                prop.setLanguageKey("gc.configgui.dimension_id_venus").setRequiresMcRestart(true)
                if (update) {
                    propCopy = (ConfigManagerMars.config)!!.get(
                        Constants.CONFIG_CATEGORY_DIMENSIONS,
                        prop.getName(),
                        prop.getInt(),
                        prop.getComment()
                    )
                    propCopy.setLanguageKey(prop.getLanguageKey())
                    propCopy.setRequiresMcRestart(prop.requiresMcRestart())
                }
                dimensionIDVenus = prop.getInt()
                foundation.config.FoundationPlanetConfigOrder.finish(prop, Constants.CONFIG_CATEGORY_DIMENSIONS)

                var oldLightning: Boolean = false
                if (config!!.hasKey(Constants.CONFIG_CATEGORY_SCHEMATIC, "disableAmbientLightning")) {
                    oldLightning = config!!.get(Constants.CONFIG_CATEGORY_SCHEMATIC, "disableAmbientLightning", false)
                        .getBoolean(false)
                }
                prop = config!!.get(Constants.CONFIG_CATEGORY_GENERAL, "disableAmbientLightning", oldLightning)
                prop.setComment("Disables background thunder and lightning.")
                prop.setLanguageKey("gc.configgui.disable_ambient_lightning")
                disableAmbientLightning = prop.getBoolean(oldLightning)
                foundation.config.FoundationPlanetConfigOrder.finish(prop, Constants.CONFIG_CATEGORY_GENERAL)

                prop = config!!.get(Constants.CONFIG_CATEGORY_WORLDGEN, "Disable Aluminum Ore Gen on Venus", false)
                prop.setComment("Disable Aluminum Ore Gen on Venus.")
                prop.setLanguageKey("gc.configgui.disable_venus_aluminum_gen")
                disableAluminumGen = prop.getBoolean(false)
                foundation.config.FoundationPlanetConfigOrder.finish(prop, Constants.CONFIG_CATEGORY_WORLDGEN)

                prop = config!!.get(Constants.CONFIG_CATEGORY_WORLDGEN, "Disable Copper Ore Gen on Venus", false)
                prop.setComment("Disable Copper Ore Gen on Venus.")
                prop.setLanguageKey("gc.configgui.disable_venus_copper_gen")
                disableCopperGen = prop.getBoolean(false)
                foundation.config.FoundationPlanetConfigOrder.finish(prop, Constants.CONFIG_CATEGORY_WORLDGEN)

                prop = config!!.get(Constants.CONFIG_CATEGORY_WORLDGEN, "Disable Galena Ore Gen on Venus", false)
                prop.setComment("Disable Galena Ore Gen on Venus.")
                prop.setLanguageKey("gc.configgui.disable_venus_galena_gen")
                disableGalenaGen = prop.getBoolean(false)
                foundation.config.FoundationPlanetConfigOrder.finish(prop, Constants.CONFIG_CATEGORY_WORLDGEN)

                prop = config!!.get(Constants.CONFIG_CATEGORY_WORLDGEN, "Disable Quartz Ore Gen on Venus", false)
                prop.setComment("Disable Quartz Ore Gen on Venus.")
                prop.setLanguageKey("gc.configgui.disable_venus_quartz_gen")
                disableQuartzGen = prop.getBoolean(false)
                foundation.config.FoundationPlanetConfigOrder.finish(prop, Constants.CONFIG_CATEGORY_WORLDGEN)

                prop = config!!.get(Constants.CONFIG_CATEGORY_WORLDGEN, "Disable Silicon Ore Gen on Venus", false)
                prop.setComment("Disable Silicon Ore Gen on Venus.")
                prop.setLanguageKey("gc.configgui.disable_venus_silicon_gen")
                disableSiliconGen = prop.getBoolean(false)
                foundation.config.FoundationPlanetConfigOrder.finish(prop, Constants.CONFIG_CATEGORY_WORLDGEN)

                prop = config!!.get(Constants.CONFIG_CATEGORY_WORLDGEN, "Disable Tin Ore Gen on Venus", false)
                prop.setComment("Disable Tin Ore Gen on Venus.")
                prop.setLanguageKey("gc.configgui.disable_venus_tin_gen")
                disableTinGen = prop.getBoolean(false)
                foundation.config.FoundationPlanetConfigOrder.finish(prop, Constants.CONFIG_CATEGORY_WORLDGEN)

                prop = config!!.get(Constants.CONFIG_CATEGORY_WORLDGEN, "Disable Solar Ore Gen on Venus", false)
                prop.setComment("Disable Solar Ore Gen on Venus.")
                prop.setLanguageKey("gc.configgui.disable_venus_solar_gen")
                disableSolarGen = prop.getBoolean(false)
                foundation.config.FoundationPlanetConfigOrder.finish(prop, Constants.CONFIG_CATEGORY_WORLDGEN)
            } catch (e: Exception) {
                FMLLog.log(Level.ERROR, e, "Galacticraft Venus (Planets) has a problem loading its config")
                throw IllegalStateException("Could not apply planet configuration", e)
            }
        }
    }
}
