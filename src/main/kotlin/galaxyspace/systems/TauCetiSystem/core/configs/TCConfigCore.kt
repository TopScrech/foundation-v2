package galaxyspace.systems.TauCetiSystem.core.configs

import galaxyspace.systems.TauCetiSystem.core.configs.TCConfigCore
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

open class TCConfigCore constructor(file: File) {
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

        @JvmField
        var enableTauCetiSystems: Boolean = false

        @JvmField
        var dimensionIDTauCetiF: Int = 0


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

                prop = config!!.get(Constants.CONFIG_CATEGORY_GENERAL, "enableTauCetiSystems", true)
                prop.setComment("Enable/Disable Tau Ceti System.")
                prop.setLanguageKey("gc.configgui.enableTauCetiSystems").setRequiresMcRestart(true)
                enableTauCetiSystems = prop.getBoolean(true)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_DIMENSIONS, "dimensionIDTauCetiF", -1338)
                prop.setComment("Dimension ID for Tau Ceti F")
                prop.setLanguageKey("gc.configgui.dimensionIDTauCetiF").setRequiresMcRestart(true)
                dimensionIDTauCetiF = prop.getInt()
                propOrder.add(prop.getName())

                config!!.setCategoryPropertyOrder(Configuration.CATEGORY_GENERAL, propOrder)

                if (config!!.hasChanged()) {
                    config!!.save()
                }
            } catch (e: Exception) {
                FMLLog.log(Level.ERROR, e, "GalaxySpace (Tau Ceti Module) has a problem loading it's config")
            }
        }

        @JvmStatic
        fun getConfigElements(): MutableList<IConfigElement?> {
            val list: MutableList<IConfigElement?> = ArrayList<IConfigElement?>()
            list.addAll(ConfigElement(config!!.getCategory(Constants.CONFIG_CATEGORY_GENERAL)).getChildElements())

            return list
        }
    }
}
