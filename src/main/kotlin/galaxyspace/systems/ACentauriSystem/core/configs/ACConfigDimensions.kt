package galaxyspace.systems.ACentauriSystem.core.configs

import galaxyspace.GalaxySpace
import galaxyspace.systems.ACentauriSystem.core.configs.ACConfigDimensions
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

open class ACConfigDimensions constructor(file: File) {
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
        var dimensionIDProxima_B: Int = 0


        @JvmField
        var enableProxima_B: Boolean = false

        @JvmField
        var enableProxima_C: Boolean = false

        @JvmField
        var enableProxima_D: Boolean = false


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


                prop = config!!.get(Constants.CONFIG_CATEGORY_DIMENSIONS, "dimensionIDProxima_B", -1025)
                prop.setComment("Dimension ID for Proxima B")
                prop.setLanguageKey("gc.configgui.dimensionIDProxima_B").setRequiresMcRestart(true)
                dimensionIDProxima_B = prop.getInt()
                propOrder.add(prop.getName())


                // Space Stations -----------------------------------------------------------------


                //----------------------------------------------------------------------------------
                prop = config!!.get(Constants.CONFIG_CATEGORY_GENERAL, "enableProxima_B", true)
                prop.setComment("Enable/Disable Proxima B")
                prop.setLanguageKey("gc.configgui.enableProxima_B").setRequiresMcRestart(true)
                enableProxima_B = prop.getBoolean(true)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_GENERAL, "enableProxima_C", true)
                prop.setComment("Enable/Disable Proxima C")
                prop.setLanguageKey("gc.configgui.enableProxima_C").setRequiresMcRestart(true)
                enableProxima_C = prop.getBoolean(true)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_GENERAL, "enableProxima_D", true)
                prop.setComment("Enable/Disable Proxima D")
                prop.setLanguageKey("gc.configgui.enableProxima_D").setRequiresMcRestart(true)
                enableProxima_D = prop.getBoolean(true)
                propOrder.add(prop.getName())


                config!!.setCategoryPropertyOrder(Configuration.CATEGORY_GENERAL, propOrder)

                if (config!!.hasChanged()) {
                    config!!.save()
                }
            } catch (e: Exception) {
                FMLLog.log(Level.ERROR, e, "GalaxySpace has a problem loading it's config")
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
