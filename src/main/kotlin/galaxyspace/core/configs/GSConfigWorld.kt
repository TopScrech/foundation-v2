package galaxyspace.core.configs

import java.util.ArrayList

import micdoodle8.mods.galacticraft.core.Constants
import foundation.config.FoundationConfiguration as Configuration
import foundation.config.FoundationConfigProperty as Property
import foundation.logging.FoundationLog as FMLLog
import org.apache.logging.log4j.Level
import java.io.File

open class GSConfigWorld constructor(file: File) {
    init {
        config = Configuration(file)
        syncConfig(true)
    }

    companion object {
        @JvmField
        var config: Configuration? = null


        @JvmField
        var enableOresGeneration: Boolean = false

        @JvmField
        var enableMarsNewOres: Boolean = false

        @JvmField
        var enableOverworldOres: Boolean = false

        @JvmField
        var enableNatureGasGen: Boolean = false

        @JvmField
        var OREGEN_SETTINGS: Array<String?>? = null

        @JvmStatic
        fun syncConfig(load: Boolean) {
            val propOrder: MutableList<String?> = ArrayList<String?>()

            try {
                propOrder.clear()
                var prop: Property
                if (!config!!.isChild) {
                    if (load) {
                        config!!.load()
                    }
                }

                prop = config!!.get(Constants.CONFIG_CATEGORY_WORLDGEN, "enableOverworldOres", true)
                prop.setComment("Enable/Disable Generation Ores on Overworld.")
                prop.setLanguageKey("gc.configgui.enableOverworldOres").setRequiresMcRestart(false)
                enableOverworldOres = prop.getBoolean(true)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_WORLDGEN, "enableOresGeneration", true)
                prop.setComment("Enable/Disable Generation Ores on Planets/Moon (Global Config).")
                prop.setLanguageKey("gc.configgui.enableOresGeneration").setRequiresMcRestart(false)
                enableOresGeneration = prop.getBoolean(true)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_WORLDGEN, "enableNatureGasGen", true)
                prop.setComment("Enable/Disable Generation Nature Gas on Overworld.")
                prop.setLanguageKey("gc.configgui.enableNatureGasGen").setRequiresMcRestart(false)
                enableNatureGasGen = prop.getBoolean(true)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_WORLDGEN, "enableMarsNewOres", true)
                prop.setComment("Enable/Disable New Mars oregen (diamonds, coal, gold, etc).")
                prop.setLanguageKey("gc.configgui.enableMarsNewOres").setRequiresMcRestart(true)
                enableMarsNewOres = prop.getBoolean(true)
                propOrder.add(prop.getName())

                prop = config!!.get(
                    Constants.CONFIG_CATEGORY_WORLDGEN, "oreGenSettings",
                    arrayOf<String>(
                        "-31:minecraft:ice:0:minecraft:ice:0:1:1:0:100"
                    )
                )

                prop.setComment("!!BETA TEST!! Add custom oregen in celestial bodies (exclude Overworld, Asteroids.)  Example: dimensionid:modid:block:meta:replacemodid:replaceblock:replacemeta:veinsize:amountperchunk:minY:maxY")
                prop.setLanguageKey("gc.configgui.oreGenSettings").setRequiresMcRestart(true)
                OREGEN_SETTINGS = prop.getStringList()
                propOrder.add(prop.getName())

                config!!.setCategoryPropertyOrder(Configuration.CATEGORY_GENERAL, propOrder)
                config!!.save()
            } catch (e: Exception) {
                FMLLog.log(Level.ERROR, e, "GalaxySpace (Core) has a problem loading it's config")
            }
        }
    }
}
