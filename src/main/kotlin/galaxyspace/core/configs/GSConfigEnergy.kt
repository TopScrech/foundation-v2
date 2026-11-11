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

open class GSConfigEnergy
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

        @JvmField
        var coefficientFuelGenerator: Float = 1.0f

        @JvmField
        var coefficientThermodynamicGenerator: Float = 1f

        @JvmField
        var coefficientSolarWindPanel: Float = 1f

        @JvmField
        var coefficientSolarPanel: Float = 1f

        @JvmField
        var coefficientWindTurbine: Float = 1f

        @JvmField
        var coefficientSpeedRocketAssembly: Float = 1f

        @JvmField
        var coefficientGasGenerator: Float = 1.0f

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


                prop = config!!.get(
                    Constants.CONFIG_CATEGORY_GENERAL,
                    "coefficientGasGenerator",
                    coefficientGasGenerator.toDouble()
                )
                prop.setComment("Gaseous Generator power factor")
                prop.setLanguageKey("gc.configgui.coefficientGasGenerator").setRequiresMcRestart(false)
                coefficientGasGenerator = prop.getDouble().toFloat()
                propOrder.add(prop.getName())

                prop = config!!.get(
                    Constants.CONFIG_CATEGORY_GENERAL,
                    "coefficientFuelGenerator",
                    coefficientFuelGenerator.toDouble()
                )
                prop.setComment("Fuel Generator power factor")
                prop.setLanguageKey("gc.configgui.coefficientFuelGenerator").setRequiresMcRestart(false)
                coefficientFuelGenerator = prop.getDouble().toFloat()
                propOrder.add(prop.getName())

                prop = config!!.get(
                    Constants.CONFIG_CATEGORY_GENERAL,
                    "coefficientThermodynamicGenerator",
                    coefficientThermodynamicGenerator.toDouble()
                )
                prop.setComment("Thermodynamic Generator power factor")
                prop.setLanguageKey("gc.configgui.coefficientThermodynamicGenerator").setRequiresMcRestart(false)
                coefficientThermodynamicGenerator = prop.getDouble().toFloat()
                propOrder.add(prop.getName())

                prop = config!!.get(
                    Constants.CONFIG_CATEGORY_GENERAL,
                    "coefficientSolarWindPanel",
                    coefficientSolarWindPanel.toDouble()
                )
                prop.setComment("Solar Wind Panel power factor")
                prop.setLanguageKey("gc.configgui.coefficientSolarWindPanel").setRequiresMcRestart(false)
                coefficientSolarWindPanel = prop.getDouble().toFloat()
                propOrder.add(prop.getName())

                prop = config!!.get(
                    Constants.CONFIG_CATEGORY_GENERAL,
                    "coefficientSolarPanel",
                    coefficientSolarPanel.toDouble()
                )
                prop.setComment("Gybrid Solar Panel power factor")
                prop.setLanguageKey("gc.configgui.coefficientSolarPanel").setRequiresMcRestart(false)
                coefficientSolarPanel = prop.getDouble().toFloat()
                propOrder.add(prop.getName())

                prop = config!!.get(
                    Constants.CONFIG_CATEGORY_GENERAL,
                    "coefficientWindTurbine",
                    coefficientWindTurbine.toDouble()
                )
                prop.setComment("Wind Turbine power factor")
                prop.setLanguageKey("gc.configgui.coefficientWindTurbine").setRequiresMcRestart(false)
                coefficientWindTurbine = prop.getDouble().toFloat()
                propOrder.add(prop.getName())

                prop = config!!.get(
                    Constants.CONFIG_CATEGORY_GENERAL,
                    "coefficientSpeedRocketAssembly",
                    coefficientSpeedRocketAssembly.toDouble()
                )
                prop.setComment("Rocket Assembly craft speed factor")
                prop.setLanguageKey("gc.configgui.coefficientSpeedRocketAssembly").setRequiresMcRestart(false)
                coefficientSpeedRocketAssembly = prop.getDouble().toFloat()
                propOrder.add(prop.getName())

                config!!.setCategoryPropertyOrder(Configuration.CATEGORY_GENERAL, propOrder)

                if (config!!.hasChanged()) {
                    config!!.save()
                }
            } catch (e: Exception) {
                FMLLog.log(Level.ERROR, e, "GalaxySpace (Energy) has a problem loading it's config")
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
