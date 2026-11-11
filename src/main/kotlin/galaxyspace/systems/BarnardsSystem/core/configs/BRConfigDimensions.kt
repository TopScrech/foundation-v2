package galaxyspace.systems.BarnardsSystem.core.configs

import galaxyspace.systems.BarnardsSystem.core.configs.BRConfigDimensions
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

open class BRConfigDimensions constructor(file: File) {
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
        var dimensionIDBarnardaC: Int = 0

        @JvmField
        var dimensionIDBarnardaC1: Int = 0

        @JvmField
        var dimensionIDBarnardaC2: Int = 0

        @JvmField
        var enableBarnardaC: Boolean = false

        @JvmField
        var enableBarnardaC1: Boolean = false

        @JvmField
        var enableBarnardaC2: Boolean = false

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

                prop = config!!.get(Constants.CONFIG_CATEGORY_DIMENSIONS, "dimensionIDBarnardaC", -1030)
                prop.setComment("Dimension ID for Barnarda C")
                prop.setLanguageKey("gc.configgui.dimensionIDBarnardaC").setRequiresMcRestart(true)
                dimensionIDBarnardaC = prop.getInt()
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_DIMENSIONS, "dimensionIDBarnardaC1", -1031)
                prop.setComment("Dimension ID for Barnarda C1 (Moon)")
                prop.setLanguageKey("gc.configgui.dimensionIDBarnardaC1").setRequiresMcRestart(true)
                dimensionIDBarnardaC1 = prop.getInt()
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_DIMENSIONS, "dimensionIDBarnardaC2", -1032)
                prop.setComment("Dimension ID for Barnarda C2 (Moon)")
                prop.setLanguageKey("gc.configgui.dimensionIDBarnardaC2").setRequiresMcRestart(true)
                dimensionIDBarnardaC2 = prop.getInt()
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_GENERAL, "enableBarnardaC", true)
                prop.setComment("Enable/Disable Barnarda C planet.")
                prop.setLanguageKey("gc.configgui.enableBarnardaC").setRequiresMcRestart(true)
                enableBarnardaC = prop.getBoolean(true)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_GENERAL, "enableBarnardaC1", true)
                prop.setComment("Enable/Disable Barnarda C1 moon.")
                prop.setLanguageKey("gc.configgui.enableBarnardaC1").setRequiresMcRestart(true)
                enableBarnardaC1 = prop.getBoolean(true)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_GENERAL, "enableBarnardaC2", true)
                prop.setComment("Enable/Disable Barnarda C2 moon.")
                prop.setLanguageKey("gc.configgui.enableBarnardaC2").setRequiresMcRestart(true)
                enableBarnardaC2 = prop.getBoolean(true)
                propOrder.add(prop.getName())

                config!!.setCategoryPropertyOrder(Configuration.CATEGORY_GENERAL, propOrder)

                if (config!!.hasChanged()) {
                    config!!.save()
                }
            } catch (e: Exception) {
                FMLLog.log(Level.ERROR, e, "GalaxySpace (Barnards Module) has a problem loading it's config")
            }
        }

        @JvmStatic
        fun getConfigElements(): MutableList<IConfigElement?> {
            val list: MutableList<IConfigElement?> = ArrayList<IConfigElement?>()
            list.addAll(ConfigElement(config!!.getCategory(Constants.CONFIG_CATEGORY_GENERAL)).getChildElements())
            list.addAll(ConfigElement(config!!.getCategory(Constants.CONFIG_CATEGORY_DIMENSIONS)).getChildElements())

            return list
        }
    }
}
