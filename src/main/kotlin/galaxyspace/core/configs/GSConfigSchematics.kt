package galaxyspace.core.configs

import java.util.ArrayList

import micdoodle8.mods.galacticraft.core.Constants
import foundation.config.FoundationConfigElement as ConfigElement
import foundation.config.FoundationConfiguration as Configuration
import foundation.config.FoundationConfigProperty as Property
import foundation.config.FoundationConfigEntry as IConfigElement
import foundation.logging.FoundationLog as FMLLog
import org.apache.logging.log4j.Level
import java.io.File

open class GSConfigSchematics
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
        var idSchematicCone: Int = 11

        @JvmField
        var idSchematicBody: Int = 12

        @JvmField
        var idSchematicEngine: Int = 13

        @JvmField
        var idSchematicBooster: Int = 14

        @JvmField
        var idSchematicFins: Int = 15

        @JvmField
        var idSchematicOxTank: Int = 16

        @JvmField
        var enableDuplicateSchematic: Boolean = false

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

                prop = config!!.get(Constants.CONFIG_CATEGORY_GENERAL, "enableDuplicateSchematic", true)
                prop.setComment("Enable/Disable make duplicate schematic.")
                prop.setLanguageKey("gc.configgui.enableDuplicateSchematic").setRequiresMcRestart(true)
                enableDuplicateSchematic = prop.getBoolean(true)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_SCHEMATIC, "idSchematicCone", 11)
                prop.setComment("Schematic ID for Cone (Rocket Detail), must be unique.")
                prop.setLanguageKey("gc.configgui.idSchematicCone").setRequiresMcRestart(false)
                idSchematicCone = prop.getInt(11)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_SCHEMATIC, "idSchematicBody", 12)
                prop.setComment("Schematic ID for Body (Rocket Detail), must be unique.")
                prop.setLanguageKey("gc.configgui.idSchematicBody").setRequiresMcRestart(false)
                idSchematicBody = prop.getInt(12)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_SCHEMATIC, "idSchematicEngine", 13)
                prop.setComment("Schematic ID for Engine (Rocket Detail), must be unique.")
                prop.setLanguageKey("gc.configgui.idSchematicEngine").setRequiresMcRestart(false)
                idSchematicEngine = prop.getInt(13)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_SCHEMATIC, "idSchematicBooster", 14)
                prop.setComment("Schematic ID for Booster (Rocket Detail), must be unique.")
                prop.setLanguageKey("gc.configgui.idSchematicBooster").setRequiresMcRestart(false)
                idSchematicBooster = prop.getInt(14)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_SCHEMATIC, "idSchematicFins", 15)
                prop.setComment("Schematic ID for Fins (Rocket Detail), must be unique.")
                prop.setLanguageKey("gc.configgui.idSchematicFins").setRequiresMcRestart(false)
                idSchematicFins = prop.getInt(15)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_SCHEMATIC, "idSchematicOxTank", 16)
                prop.setComment("Schematic ID for Oxygen PPE Tank, must be unique.")
                prop.setLanguageKey("gc.configgui.idSchematicOxTank").setRequiresMcRestart(false)
                idSchematicOxTank = prop.getInt(16)
                propOrder.add(prop.getName())

                config!!.setCategoryPropertyOrder(Configuration.CATEGORY_GENERAL, propOrder)

                if (config!!.hasChanged()) {
                    config!!.save()
                }
            } catch (e: Exception) {
                FMLLog.log(Level.ERROR, e, "GalaxySpace (Schematics) has a problem loading it's config")
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
