package galaxyspace.core.configs

import java.util.ArrayList
import java.util.List

import galaxyspace.core.util.GSConstants
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.util.GCLog
import net.minecraftforge.common.config.ConfigElement
import net.minecraftforge.common.config.Configuration
import net.minecraftforge.common.config.Property
import net.minecraftforge.fml.client.config.IConfigElement
import net.minecraftforge.fml.common.FMLLog
import org.apache.logging.log4j.Level
import org.lwjgl.input.Keyboard
import java.io.File

open class GSConfigCore
constructor(file: File) {
    init {
        config = Configuration(file)
        syncConfig(true)
    }

    companion object {
        @JvmField
        var config: Configuration? = null


        @JvmField
        var enableCheckVersion: Boolean = false

        @JvmField
        var enableDungeonsGeneration: Boolean = false

        @JvmField
        var enableNewMenu: Boolean = false

        @JvmField
        var enableMarsWorldEngine: Boolean = false


        @JvmField
        var idSolarRadiation: Int = 29

        @JvmField
        var idAntiRadiation: Int = 30

        @JvmField
        var speedTimeTravel: Int = 1

        @JvmField
        var guiIDGuideBook: Int = 1001

        @JvmField
        var enableMethaneParticle: Boolean = false

        @JvmField
        var enableUnreachable: Boolean = false

        @JvmField
        var enableWorldEngine: Boolean = false

        @JvmField
        var enablePlateOreDict: Boolean = false

        @JvmField
        var enableSpaceSuitHUD: Boolean = false

        @JvmField
        var enableModernGUI: Boolean = false

        //Hardmode
        @JvmField
        var enableRadiationSystem: Boolean = false

        @JvmField
        var enablePressureSystem: Boolean = false

        @JvmField
        var enableAdvancedRocketCraft: Boolean = false

        @JvmField
        var enableAdvancedThermalSystem: Boolean = false

        @JvmField
        var enableZeroGravityOnAsteroids: Boolean = false

        @JvmField
        var enableOxygenForPlantsAndFoods: Boolean = false

        @JvmField
        var enableSolarRadiationOnMoon: Boolean = false

        @JvmField
        var enableGasExplosion: Boolean = false

        @JvmField
        var enableExtraWeatherOnBodies: Boolean = false

        @JvmField
        var enableRadiationEffectForAndroid: Boolean = false

        @JvmField
        var enablePressureEffectForAndroid: Boolean = false

        //
        @JvmField
        var spacesuit_pos: String? = "center"

        @JvmField
        var enableDebug: Boolean = false

        @JvmField
        var radiation_armor: Array<String?>? = null

        @JvmField
        var unsuffocation_entity: Array<String?>? = null

        @JvmField
var keyOverrideToggleHelmet: String? = null

        @JvmField
var keyOverrideToggleChest: String? = null

        @JvmField
var keyOverrideToggleLegs: String? = null

        @JvmField
var keyOverrideToggleBoots: String? = null

        @JvmField
var keyOverrideToggleHelmetI: Int = 0

        @JvmField
var keyOverrideToggleChestI: Int = 0

        @JvmField
var keyOverrideToggleLegsI: Int = 0

        @JvmField
var keyOverrideToggleBootsI: Int = 0

        @JvmField
        var storageOxygenTankTier4: Int = 3500

        @JvmField
        var storageOxygenTankTier5: Int = 4000

        @JvmField
        var storageOxygenTankTier6: Int = 4500

        @JvmField
        var storageOxygenTankTierEPP: Int = 2500

        @JvmField
        var rechargeOxygenTankTierEPP: Int = 2

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

                prop = config!!.get(GSConstants.CATEGORY_HARDMODE, "enableOxygenForPlantsAndFoods", true)
                prop.setComment("Enable/Disable the need oxygen for plants and foods.")
                prop.setLanguageKey("gc.configgui.enableOxygenForPlantsAndFoods").setRequiresMcRestart(true)
                enableOxygenForPlantsAndFoods = prop.getBoolean(true)
                propOrder.add(prop.getName())

                prop = config!!.get(GSConstants.CATEGORY_HARDMODE, "enableGasExplosion", true)
                prop.setComment("Enable/Disable explosion gas (Nature gas, etc) from fire, torch, magma.")
                prop.setLanguageKey("gc.configgui.enableGasExplosion").setRequiresMcRestart(true)
                enableGasExplosion = prop.getBoolean(true)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_GENERAL, "enableCheckVersion", true)
                prop.setComment("Enable/Disable Check Version.")
                prop.setLanguageKey("gc.configgui.enableCheckVersion").setRequiresMcRestart(false)
                enableCheckVersion = prop.getBoolean(true)
                propOrder.add(prop.getName())

                prop = config!!.get(GSConstants.CATEGORY_HARDMODE, "enableAdvancedRocketCraft", true)
                prop.setComment("Enable/Disable advanced craft for rocket tier 2-6.")
                prop.setLanguageKey("gc.configgui.enableAdvancedRocketCraft").setRequiresMcRestart(true)
                enableAdvancedRocketCraft = prop.getBoolean(true)
                propOrder.add(prop.getName())

                prop = config!!.get(GSConstants.CATEGORY_HARDMODE, "enableAdvancedThermalSystem", true)
                prop.setComment("Enable/Disable advanced thermal system on celestial bodies.")
                prop.setLanguageKey("gc.configgui.enableAdvancedThermalSystem").setRequiresMcRestart(false)
                enableAdvancedThermalSystem = prop.getBoolean(true)
                propOrder.add(prop.getName())

                prop = config!!.get(GSConstants.CATEGORY_HARDMODE, "enableZeroGravityOnAsteroids", false)
                prop.setComment("Enable/Disable zero gravity (like Kuiper Belt) on Astreroids.")
                prop.setLanguageKey("gc.configgui.enableZeroGravityOnAsteroids").setRequiresMcRestart(true)
                enableZeroGravityOnAsteroids = prop.getBoolean(false)
                propOrder.add(prop.getName())





                prop = config!!.get(Constants.CONFIG_CATEGORY_WORLDGEN, "enableDungeonsGeneration", true)
                prop.setComment("Enable/Disable Dungeons Generation on Planets/Moon (Global Config).")
                prop.setLanguageKey("gc.configgui.enableDungeonsGeneration").setRequiresMcRestart(false)
                enableDungeonsGeneration = prop.getBoolean(true)
                propOrder.add(prop.getName())

                prop = config!!.get(GSConstants.CATEGORY_HARDMODE, "enableRadiationSystem", true)
                prop.setComment("Enable/Disable solar radiation system.")
                prop.setLanguageKey("gc.configgui.enableRadiationSystem").setRequiresMcRestart(false)
                enableRadiationSystem = prop.getBoolean(true)
                propOrder.add(prop.getName())

                prop = config!!.get(GSConstants.CATEGORY_HARDMODE, "enablePressureSystem", true)
                prop.setComment("Enable/Disable atmosphere pressure system.")
                prop.setLanguageKey("gc.configgui.enablePressureSystem").setRequiresMcRestart(false)
                enablePressureSystem = prop.getBoolean(true)
                propOrder.add(prop.getName())

                prop = config!!.get(GSConstants.CATEGORY_HARDMODE, "enableSolarRadiationOnMoon", true)
                prop.setComment("Enable/Disable solar radiation on Moon.")
                prop.setLanguageKey("gc.configgui.enableSolarRadiationOnMoon").setRequiresMcRestart(true)
                enableSolarRadiationOnMoon = prop.getBoolean(true)
                propOrder.add(prop.getName())

                prop = config!!.get(GSConstants.CATEGORY_HARDMODE, "enableExtraWeatherOnBodies", true)
                prop.setComment("Enable/Disable extra weather on celestial bodies (Dust Storm, Meteoric Rain, etc).")
                prop.setLanguageKey("gc.configgui.enableExtraWeatherOnBodies").setRequiresMcRestart(true)
                enableExtraWeatherOnBodies = prop.getBoolean(true)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_CLIENT, "enableNewMenu", true)
                prop.setComment("Enable/Disable new Main Menu.")
                prop.setLanguageKey("gc.configgui.enableNewMenu").setRequiresMcRestart(false)
                enableNewMenu = prop.getBoolean(true)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_CLIENT, "enableModernGUI", true)
                prop.setComment("Enable/Disable modern GUI elements.")
                prop.setLanguageKey("gc.configgui.enableModernGUI").setRequiresMcRestart(false)
                enableModernGUI = prop.getBoolean(true)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_CLIENT, "enableMethaneParticle", true)
                prop.setComment("Enable/Disable Methane Particles.")
                prop.setLanguageKey("gc.configgui.enableMethaneParticle").setRequiresMcRestart(false)
                enableMethaneParticle = prop.getBoolean(true)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_DIMENSIONS, "enableMarsWorldEngine", true)
                prop.setComment("Enable/Disable New Mars worldgen (WE).")
                prop.setLanguageKey("gc.configgui.enableMarsWorldEngine").setRequiresMcRestart(true)
                enableMarsWorldEngine = prop.getBoolean(true)
                propOrder.add(prop.getName())

                /*
            prop = config.get(Constants.CONFIG_CATEGORY_WORLDGEN, "Other mods ores for GC to generate on GS planets", new String [] { });
            prop.setComment("Enter IDs of other mods' ores here for Galacticraft to generate them on GalaxySpace planets. Format is BlockName or BlockName:metadata. Use optional parameters at end of each line: /RARE /UNCOMMON or /COMMON for rarity in a chunk; /DEEP /SHALLOW or /BOTH for height; /SINGLE /STANDARD or /LARGE for clump size; /XTRARANDOM for ores sometimes there sometimes not at all.  /ONLYMOON or /ONLYMARS if wanted on one planet only.  If nothing specified, defaults are /COMMON, /BOTH and /STANDARD.  Repeat lines to generate a huge quantity of ores.");
            prop.setComment("/ONLYPHOBOS, /ONLYDEIMOS, /ONLYEUROPA, /ONLYIO, /ONLYENCELADUS, /ONLYVENUS, /ONLYMERCURY, /ONLYCERES if wanted on one planet only.");
            prop.setLanguageKey("gc.configgui.otherModOreGenIDs");
            oregenIDs = prop.getStringList();
            propOrder.add(prop.getName());
            
            
            prop = config.get(Constants.CONFIG_CATEGORY_DIMENSIONS, "enableSkyOverworld", true);
            prop.setComment("Enable/Disable new render sky on Overworld.");
            prop.setLanguageKey("gc.configgui.enableSkyOverworld").setRequiresMcRestart(true);
            enableSkyOverworld = prop.getBoolean(true);
            propOrder.add(prop.getName());
            */
                prop = config!!.get(Constants.CONFIG_CATEGORY_GENERAL, "idSolarRadiation", idSolarRadiation)
                prop.setComment("ID Potion 'Solar Radiation'")
                prop.setLanguageKey("gc.configgui.idSolarRadiation").setRequiresMcRestart(false)
                idSolarRadiation = prop.getInt()
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_GENERAL, "idAntiRadiation", idAntiRadiation)
                prop.setComment("ID Potion 'Anti Radiation'")
                prop.setLanguageKey("gc.configgui.idAntiRadiation").setRequiresMcRestart(false)
                idAntiRadiation = prop.getInt()
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_GENERAL, "speedTimeTravel", speedTimeTravel)
                prop.setComment("Time Travel speed factor.")
                prop.setLanguageKey("gc.configgui.speedTimeTravel").setRequiresMcRestart(false)
                speedTimeTravel = prop.getInt()
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_GENERAL, "guiIDGuideBook", guiIDGuideBook)
                prop.setComment("ID gui for Guide Book.")
                prop.setLanguageKey("gc.configgui.guiIDGuideBook").setRequiresMcRestart(false)
                guiIDGuideBook = prop.getInt()
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_DIMENSIONS, "enableUnreachable", true)
                prop.setComment("Enable/Disable Unreachable Planets/Moons")
                prop.setLanguageKey("gc.configgui.enableUnreachable").setRequiresMcRestart(true)
                enableUnreachable = prop.getBoolean(true)
                propOrder.add(prop.getName())
                /*
            prop = config.get(Constants.CONFIG_CATEGORY_DIFFICULTY, "enableHardMode", true);
            prop.setComment("Enable/Disable Hard Mode (Death of Atm. Pressure)");
            prop.setLanguageKey("gc.configgui.enableHardMode").setRequiresMcRestart(true);
            enableHardMode = prop.getBoolean(true);
            propOrder.add(prop.getName());
            */
                prop = config!!.get(Constants.CONFIG_CATEGORY_WORLDGEN, "enableWorldEngine", true)
                prop.setComment("Enable/Disable 'World Engine' - advanced world generation")
                prop.setLanguageKey("gc.configgui.enableWorldEngine").setRequiresMcRestart(true)
                enableWorldEngine = prop.getBoolean(true)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_GENERAL, "enablePlateOreDict", true)
                prop.setComment("Enable/Disable register OreDict for GC Compressed Plate")
                prop.setLanguageKey("gc.configgui.enablePlateOreDict").setRequiresMcRestart(false)
                enablePlateOreDict = prop.getBoolean(true)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_CLIENT, "enableSpaceSuitHUD", true)
                prop.setComment("Enable/Disable space suit HUD.")
                prop.setLanguageKey("gc.configgui.enableSpaceSuitHUD").setRequiresMcRestart(false)
                enableSpaceSuitHUD = prop.getBoolean(true)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_CLIENT, "spacesuit_pos", spacesuit_pos)
                prop.setComment("Set HUD position. (up or top, center, down or bottom)")
                prop.setLanguageKey("gc.configgui.spacesuit_pos").setRequiresMcRestart(false)
                spacesuit_pos = prop.getString()
                propOrder.add(prop.getName())

                prop = config!!.get(GSConstants.CATEGORY_DEVELOMPENT, "enableDebug", false)
                prop.setComment("Enable/Disable Debug mode")
                prop.setLanguageKey("gc.configgui.enableDebug").setRequiresMcRestart(false)
                enableDebug = prop.getBoolean(false)
                propOrder.add(prop.getName())

                prop = config!!.get(
                    Constants.CONFIG_CATEGORY_GENERAL,
                    "Radiation and Pressure Armor List",
                    arrayOfNulls<String>(0)
                )
                prop.setComment("List armor with protect radiation and pressure. Format: 'modid:item' ")
                prop.setLanguageKey("gc.configgui.armorIDs").setRequiresMcRestart(false)
                radiation_armor = prop.getStringList()
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_KEYS, "Toggle Helmet Key", "KEY_F")
                prop.setComment("Leave 'KEY_' value, adding the intended keyboard character to replace the letter. Values 0-9 and A-Z are accepted")
                prop.setLanguageKey("gc.configgui.keyOverrideToggleHelmet").setRequiresMcRestart(false)
                keyOverrideToggleHelmet = prop.getString()
                keyOverrideToggleHelmetI = parseKeyValue(keyOverrideToggleHelmet)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_KEYS, "Toggle Chestplate Key", "KEY_G")
                prop.setComment("Leave 'KEY_' value, adding the intended keyboard character to replace the letter. Values 0-9 and A-Z are accepted")
                prop.setLanguageKey("gc.configgui.keyOverrideToggleChest").setRequiresMcRestart(false)
                keyOverrideToggleChest = prop.getString()
                keyOverrideToggleChestI = parseKeyValue(keyOverrideToggleChest)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_KEYS, "Toggle Legs Key", "KEY_H")
                prop.setComment("Leave 'KEY_' value, adding the intended keyboard character to replace the letter. Values 0-9 and A-Z are accepted")
                prop.setLanguageKey("gc.configgui.keyOverrideToggleLegs").setRequiresMcRestart(false)
                keyOverrideToggleLegs = prop.getString()
                keyOverrideToggleLegsI = parseKeyValue(keyOverrideToggleLegs)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_KEYS, "Toggle Boots Key", "KEY_J")
                prop.setComment("Leave 'KEY_' value, adding the intended keyboard character to replace the letter. Values 0-9 and A-Z are accepted")
                prop.setLanguageKey("gs.configgui.keyOverrideToggleBoots").setRequiresMcRestart(false)
                keyOverrideToggleBoots = prop.getString()
                keyOverrideToggleBootsI = parseKeyValue(keyOverrideToggleBoots)
                propOrder.add(prop.getName())


                prop = config!!.get(
                    Constants.CONFIG_CATEGORY_ENTITIES, "Entity List",
                    arrayOf<String>(
                        "matteroverdrive:rogue_android",
                        "matteroverdrive:ranged_rogue_android"
                    )
                )

                prop.setComment("List entity for ignore suffocation damage. Format: 'modid:entity' ")
                prop.setLanguageKey("gs.configgui.unsuffocation_entity").setRequiresMcRestart(false)
                unsuffocation_entity = prop.getStringList()
                propOrder.add(prop.getName())

                prop = config!!.get(GSConstants.CATEGORY_ITEMS, "storageOxygenTankTier4", storageOxygenTankTier4)
                prop.setComment("Amount storage for Oxygen Tank Tier 4")
                prop.setLanguageKey("gs.configgui.storageOxygenTankTier4").setRequiresMcRestart(true)
                storageOxygenTankTier4 = prop.getInt()
                propOrder.add(prop.getName())

                prop = config!!.get(GSConstants.CATEGORY_ITEMS, "storageOxygenTankTier5", storageOxygenTankTier5)
                prop.setComment("Amount storage for Oxygen Tank Tier 5")
                prop.setLanguageKey("gs.configgui.storageOxygenTankTier5").setRequiresMcRestart(true)
                storageOxygenTankTier5 = prop.getInt()
                propOrder.add(prop.getName())

                prop = config!!.get(GSConstants.CATEGORY_ITEMS, "storageOxygenTankTier6", storageOxygenTankTier6)
                prop.setComment("Amount storage for Oxygen Tank Tier 6")
                prop.setLanguageKey("gs.configgui.storageOxygenTankTier6").setRequiresMcRestart(true)
                storageOxygenTankTier6 = prop.getInt()
                propOrder.add(prop.getName())

                prop = config!!.get(GSConstants.CATEGORY_ITEMS, "storageOxygenTankTierEPP", storageOxygenTankTierEPP)
                prop.setComment("Amount storage for Oxygen Tank Tier EPP")
                prop.setLanguageKey("gs.configgui.storageOxygenTankTierEPP").setRequiresMcRestart(true)
                storageOxygenTankTierEPP = prop.getInt()
                propOrder.add(prop.getName())

                prop = config!!.get(GSConstants.CATEGORY_ITEMS, "rechargeOxygenTankTierEPP", rechargeOxygenTankTierEPP)
                prop.setComment("Amount recharge per tick for Oxygen Tank Tier EPP")
                prop.setLanguageKey("gs.configgui.rechargeOxygenTankTierEPP").setRequiresMcRestart(true)
                rechargeOxygenTankTierEPP = prop.getInt()
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_COMPATIBILITY, "enableRadiationEffectForAndroid", true)
                prop.setComment("Enable/Disable radiation effect for Android from MatterOverdrive.")
                prop.setLanguageKey("gc.configgui.enableRadiationEffectForAndroid").setRequiresMcRestart(true)
                enableRadiationEffectForAndroid = prop.getBoolean(false)
                propOrder.add(prop.getName())

                prop = config!!.get(Constants.CONFIG_CATEGORY_COMPATIBILITY, "enablePressureEffectForAndroid", true)
                prop.setComment("Enable/Disable pressure effect for Android from MatterOverdrive.")
                prop.setLanguageKey("gc.configgui.enablePressureEffectForAndroid").setRequiresMcRestart(true)
                enablePressureEffectForAndroid = prop.getBoolean(false)
                propOrder.add(prop.getName())

                config!!.setCategoryPropertyOrder(Configuration.CATEGORY_GENERAL, propOrder)

                config!!.save()
            } catch (e: Exception) {
                FMLLog.log(Level.ERROR, e, "GalaxySpace (Core) has a problem loading it's config")
            }
        }

        @JvmStatic
        fun getConfigElements(): MutableList<IConfigElement?> {
            val list: MutableList<IConfigElement?> = ArrayList<IConfigElement?>()
            list.addAll(ConfigElement(config!!.getCategory(Constants.CONFIG_CATEGORY_DIFFICULTY)).getChildElements())
            list.addAll(ConfigElement(config!!.getCategory(Constants.CONFIG_CATEGORY_GENERAL)).getChildElements())
            list.addAll(ConfigElement(config!!.getCategory(Constants.CONFIG_CATEGORY_CLIENT)).getChildElements())
            //list.addAll(new ConfigElement(config.getCategory(Constants.CONFIG_CATEGORY_CONTROLS)).getChildElements());
            //list.addAll(new ConfigElement(config.getCategory(Constants.CONFIG_CATEGORY_COMPATIBILITY)).getChildElements());
            list.addAll(ConfigElement(config!!.getCategory(Constants.CONFIG_CATEGORY_WORLDGEN)).getChildElements())
            //list.addAll(new ConfigElement(config.getCategory(Constants.CONFIG_CATEGORY_SERVER)).getChildElements());
            list.addAll(ConfigElement(config!!.getCategory(Constants.CONFIG_CATEGORY_DIMENSIONS)).getChildElements())
            //list.addAll(new ConfigElement(config.getCategory(Constants.CONFIG_CATEGORY_SCHEMATIC)).getChildElements());
            //list.addAll(new ConfigElement(config.getCategory(Constants.CONFIG_CATEGORY_ACHIEVEMENTS)).getChildElements());
            //list.addAll(new ConfigElement(config.getCategory(Constants.CONFIG_CATEGORY_ENTITIES)).getChildElements());
            list.addAll(ConfigElement(config!!.getCategory(Constants.CONFIG_CATEGORY_KEYS)).getChildElements())
            list.addAll(ConfigElement(config!!.getCategory(GSConstants.CATEGORY_HARDMODE)).getChildElements())
            list.addAll(ConfigElement(config!!.getCategory(GSConstants.CATEGORY_DEVELOMPENT)).getChildElements())
            list.addAll(ConfigElement(config!!.getCategory(GSConstants.CATEGORY_ITEMS)).getChildElements())

            return list
        }

        @JvmStatic
        private fun parseKeyValue(key: String?): Int {
            if (key == "KEY_A") {
                return Keyboard.KEY_A
            } else if (key == "KEY_B") {
                return Keyboard.KEY_B
            } else if (key == "KEY_C") {
                return Keyboard.KEY_C
            } else if (key == "KEY_D") {
                return Keyboard.KEY_D
            } else if (key == "KEY_E") {
                return Keyboard.KEY_E
            } else if (key == "KEY_F") {
                return Keyboard.KEY_F
            } else if (key == "KEY_G") {
                return Keyboard.KEY_G
            } else if (key == "KEY_H") {
                return Keyboard.KEY_H
            } else if (key == "KEY_I") {
                return Keyboard.KEY_I
            } else if (key == "KEY_J") {
                return Keyboard.KEY_J
            } else if (key == "KEY_K") {
                return Keyboard.KEY_K
            } else if (key == "KEY_L") {
                return Keyboard.KEY_L
            } else if (key == "KEY_M") {
                return Keyboard.KEY_M
            } else if (key == "KEY_N") {
                return Keyboard.KEY_N
            } else if (key == "KEY_O") {
                return Keyboard.KEY_O
            } else if (key == "KEY_P") {
                return Keyboard.KEY_P
            } else if (key == "KEY_Q") {
                return Keyboard.KEY_Q
            } else if (key == "KEY_R") {
                return Keyboard.KEY_R
            } else if (key == "KEY_S") {
                return Keyboard.KEY_S
            } else if (key == "KEY_T") {
                return Keyboard.KEY_T
            } else if (key == "KEY_U") {
                return Keyboard.KEY_U
            } else if (key == "KEY_V") {
                return Keyboard.KEY_V
            } else if (key == "KEY_W") {
                return Keyboard.KEY_W
            } else if (key == "KEY_X") {
                return Keyboard.KEY_X
            } else if (key == "KEY_Y") {
                return Keyboard.KEY_Y
            } else if (key == "KEY_Z") {
                return Keyboard.KEY_Z
            } else if (key == "KEY_1") {
                return Keyboard.KEY_1
            } else if (key == "KEY_2") {
                return Keyboard.KEY_2
            } else if (key == "KEY_3") {
                return Keyboard.KEY_3
            } else if (key == "KEY_4") {
                return Keyboard.KEY_4
            } else if (key == "KEY_5") {
                return Keyboard.KEY_5
            } else if (key == "KEY_6") {
                return Keyboard.KEY_6
            } else if (key == "KEY_7") {
                return Keyboard.KEY_7
            } else if (key == "KEY_8") {
                return Keyboard.KEY_8
            } else if (key == "KEY_9") {
                return Keyboard.KEY_9
            } else if (key == "KEY_0") {
                return Keyboard.KEY_0
            }

            GCLog.severe("Failed to parse keyboard key: " + key + "... Use values A-Z or 0-9")

            return 0
        }
    }
}
