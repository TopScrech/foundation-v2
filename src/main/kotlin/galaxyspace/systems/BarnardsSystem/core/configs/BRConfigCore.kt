package galaxyspace.systems.BarnardsSystem.core.configs

import galaxyspace.systems.BarnardsSystem.core.configs.BRConfigCore
import galaxyspace.GalaxySpace
import micdoodle8.mods.galacticraft.core.Constants
import net.minecraftforge.common.config.ConfigElement
import net.minecraftforge.common.config.Configuration
import net.minecraftforge.common.config.Property
import net.minecraftforge.fml.client.config.IConfigElement
import net.minecraftforge.fml.common.FMLLog
import org.apache.logging.log4j.Level
import java.io.File
import java.lang.Exception
import java.util.ArrayList

open class BRConfigCore constructor(file: File) {
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
        var enableBarnardsSystems: Boolean = false

        @JvmField
        var enableGreenBarnardaC: Boolean = false

        @JvmField
        var survivalModeOnBarnarda: Boolean = false

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

                prop = config!!.get(Constants.CONFIG_CATEGORY_GENERAL, "enableBarnardsSystems", true)
                prop.setComment("Enable/Disable Barnards System.")
                prop.setLanguageKey("gc.configgui.enableBarnardsSystems").setRequiresMcRestart(true)
                enableBarnardsSystems = prop.getBoolean(true)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_CLIENT, "enableGreenBarnardaC", false)
                prop.setComment("Enable/Disable old green color for grass on Barnarda C.")
                prop.setLanguageKey("gc.configgui.enableGreenBarnardaC").setRequiresMcRestart(true)
                enableGreenBarnardaC = prop.getBoolean(false)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_DIFFICULTY, "survivalModeOnBarnarda", false)
                prop.setComment("!!!!!EXPERIMENTAL!!!! Enable/Disable start your survival game on Barnarda C.")
                prop.setLanguageKey("gc.configgui.survivalModeOnBarnarda").setRequiresMcRestart(true)
                survivalModeOnBarnarda = prop.getBoolean(false)
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
            return list
        }
    }
}
