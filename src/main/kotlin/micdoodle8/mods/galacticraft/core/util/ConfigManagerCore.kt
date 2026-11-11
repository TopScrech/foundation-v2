package micdoodle8.mods.galacticraft.core.util

import java.util.List
import java.util.Map

import com.google.common.primitives.Ints
import micdoodle8.mods.galacticraft.api.vector.BlockTuple
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.energy.EnergyConfigHandler
import micdoodle8.mods.galacticraft.core.recipe.RecipeManagerGC
import micdoodle8.mods.galacticraft.core.tick.TickHandlerClient
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.resources.Identifier as ResourceLocation
import foundation.config.FoundationConfigCategory as ConfigCategory
import foundation.config.FoundationConfigElement as ConfigElement
import foundation.config.FoundationConfiguration as Configuration
import foundation.config.FoundationConfigProperty as Property
import foundation.config.FoundationConfigEntry as IConfigElement
import foundation.logging.FoundationLog as FMLLog
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import org.lwjgl.input.Keyboard
import java.io.File
import java.lang.Exception
import java.lang.NumberFormatException
import java.util.ArrayList
import java.util.Arrays
import java.util.LinkedList
import java.util.TreeMap
import kotlin.math.max
import kotlin.math.min

object ConfigManagerCore {
    @JvmField
    var config: Configuration? = null

    // GAME CONTROL
    @JvmField
    var forceOverworldRespawn: Boolean = false

    @JvmField
    var hardMode: Boolean = false

    @JvmField
    var quickMode: Boolean = false

    @JvmField
    var challengeMode: Boolean = false
    private var challengeFlags = 0

    @JvmField
    var challengeRecipes: Boolean = false

    @JvmField
    var challengeMobDropsAndSpawning: Boolean = false

    @JvmField
    var challengeSpawnHandling: Boolean = false

    @JvmField
    var challengeAsteroidPopulation: Boolean = false

    @JvmField
    var disableRocketsToOverworld: Boolean = false

    @JvmField
    var disableSpaceStationCreation: Boolean = false

    @JvmField
    var spaceStationsRequirePermission: Boolean = false

    @JvmField
    var disableUpdateCheck: Boolean = false

    @JvmField
    var enableSpaceRaceManagerPopup: Boolean = false

    @JvmField
    var enableDebug: Boolean = false

    @JvmField
    var enableSealerEdgeChecks: Boolean = false

    @JvmField
    var disableLander: Boolean = false

    @JvmField
    var recipesRequireGCAdvancedMetals: Boolean = true

    @JvmField
    var allowLiquidGratings: Boolean = false

    //    public static int mapfactor;
    //    public static int mapsize;
    // DIMENSIONS
    @JvmField
    var idDimensionOverworld: Int = 0

    @JvmField
    var idDimensionOverworldOrbit: Int = 0

    @JvmField
    var idDimensionOverworldOrbitStatic: Int = 0

    @JvmField
    var idDimensionMoon: Int = 0

    @JvmField
    var biomeIDbase: Int = 102

    @JvmField
    var disableBiomeTypeRegistrations: Boolean = false

    @JvmField
    var staticLoadDimensions: IntArray = intArrayOf()

    @JvmField
    var disableRocketLaunchDimensions: IntArray? = intArrayOf(-1, 1)

    @JvmField
    var disableRocketLaunchAllNonGC: Boolean = false

    @JvmField
    var otherPlanetWorldBorders: Int = 0

    @JvmField
    var keepLoadedNewSpaceStations: Boolean = false

    // SCHEMATICS
    @JvmField
    var idSchematicRocketT1: Int = 0

    @JvmField
    var idSchematicMoonBuggy: Int = 0

    @JvmField
    var idSchematicAddSchematic: Int = 0

    // ACHIEVEMENTS
    @JvmField
    var idAchievBase: Int = 0

    // CLIENT / VISUAL FX
    @JvmField
    var moreStars: Boolean = false

    @JvmField
    var disableSpaceshipParticles: Boolean = false

    @JvmField
    var disableVehicleCameraChanges: Boolean = false

    @JvmField
    var oxygenIndicatorLeft: Boolean = false

    @JvmField
    var oxygenIndicatorBottom: Boolean = false

    @JvmField
    var overrideCapes: Boolean = false

    //DIFFICULTY
    @JvmField
    var dungeonBossHealthMod: Double = 0.0

    @JvmField
    var suffocationCooldown: Int = 0

    @JvmField
    var suffocationDamage: Int = 0

    @JvmField
    var rocketFuelFactor: Int = 0

    @JvmField
    var meteorSpawnMod: Double = 0.0

    @JvmField
    var meteorBlockDamageEnabled: Boolean = false

    @JvmField
    var disableSpaceshipGrief: Boolean = false

    @JvmField
    var spaceStationEnergyScalar: Double = 0.0

    // WORLDGEN
    @JvmField
    var enableCopperOreGen: Boolean = false

    @JvmField
    var enableTinOreGen: Boolean = false

    @JvmField
    var enableAluminumOreGen: Boolean = false

    @JvmField
    var enableSiliconOreGen: Boolean = false

    @JvmField
    var disableCheeseMoon: Boolean = false

    @JvmField
    var disableTinMoon: Boolean = false

    @JvmField
    var disableCopperMoon: Boolean = false

    @JvmField
    var disableMoonVillageGen: Boolean = false

    @JvmField
    var disableSapphireMoon: Boolean = false

    @JvmField
    var externalOilGen: IntArray? = null

    @JvmField
    var oilGenFactor: Double = 0.0

    @JvmField
    var retrogenOil: Boolean = false

    @JvmField
    var oregenIDs: Array<String?>? = arrayOf<String?>()

    @JvmField
    var enableOtherModsFeatures: Boolean = false

    @JvmField
    var whitelistCoFHCoreGen: Boolean = false

    @JvmField
    var enableThaumCraftNodes: Boolean = false

    //COMPATIBILITY
    @JvmField
    var sealableIDs: Array<String?>? = arrayOf<String?>()

    @JvmField
    var detectableIDs: Array<String?> = arrayOf<String?>()

    @JvmField
    var alternateCanisterRecipe: Boolean = false

    @JvmField
    var otherModsSilicon: String? = null

    @JvmField
    var useOldOilFluidID: Boolean = false

    @JvmField
    var useOldFuelFluidID: Boolean = false

    //KEYBOARD AND MOUSE
    @JvmField
    var keyOverrideMap: String? = null

    @JvmField
    var keyOverrideFuelLevel: String? = null

    @JvmField
    var keyOverrideToggleAdvGoggles: String? = null

    @JvmField
    var keyOverrideMapI: Int = 0

    @JvmField
    var keyOverrideFuelLevelI: Int = 0

    @JvmField
    var keyOverrideToggleAdvGogglesI: Int = 0

    @JvmField
    var mapMouseScrollSensitivity: Float = 0f

    @JvmField
    var invertMapMouseScroll: Boolean = false

    @JvmField
    var clientSave: ArrayList<Any?>? = null
    private val propOrder: MutableMap<String?, MutableList<String?>?> = TreeMap<String?, MutableList<String?>?>()
    private var currentCat: String? = null

    @JvmStatic
    fun initialize(file: File) {
        config = Configuration(file)
        syncConfig(true)
    }

    @JvmStatic
    fun forceSave() {
        config!!.save()
    }

    @JvmStatic
    fun syncConfig(load: Boolean) {
        try {
            propOrder.clear()
            var prop: Property

            if (!config!!.isChild) {
                if (load) {
                    config!!.load()
                }
            }

            prop = getConfig(Constants.CONFIG_CATEGORY_GENERAL, "Enable Debug Messages", false)
            prop.setComment("If this is enabled, debug messages will appear in the console. This is useful for finding bugs in the mod.")
            prop.setLanguageKey("gc.configgui.enable_debug")
            enableDebug = prop.getBoolean(false)
            finishProp(prop)

            prop = getConfig(Constants.CONFIG_CATEGORY_DIMENSIONS, "idDimensionOverworld", 0)
            prop.setComment("Dimension ID for the Overworld (as seen in the Celestial Map)")
            prop.setLanguageKey("gc.configgui.id_dimension_overworld").setRequiresMcRestart(true)
            idDimensionOverworld = prop.getInt()
            finishProp(prop)

            prop = getConfig(Constants.CONFIG_CATEGORY_DIMENSIONS, "idDimensionMoon", -28)
            prop.setComment("Dimension ID for the Moon")
            prop.setLanguageKey("gc.configgui.id_dimension_moon").setRequiresMcRestart(true)
            idDimensionMoon = prop.getInt()
            finishProp(prop)

            prop = getConfig(Constants.CONFIG_CATEGORY_DIMENSIONS, "idDimensionOverworldOrbit", -27)
            prop.setComment("WorldProvider ID for Overworld Space Stations (advanced: do not change unless you have conflicts)")
            prop.setLanguageKey("gc.configgui.id_dimension_overworld_orbit").setRequiresMcRestart(true)
            idDimensionOverworldOrbit = prop.getInt()
            finishProp(prop)

            prop = getConfig(Constants.CONFIG_CATEGORY_DIMENSIONS, "idDimensionOverworldOrbitStatic", -26)
            prop.setComment("WorldProvider ID for Static Space Stations (advanced: do not change unless you have conflicts)")
            prop.setLanguageKey("gc.configgui.id_dimension_overworld_orbit_static").setRequiresMcRestart(true)
            idDimensionOverworldOrbitStatic = prop.getInt()
            finishProp(prop)

            prop = getConfig(Constants.CONFIG_CATEGORY_DIMENSIONS, "biomeIDBase", 102)
            prop.setComment("Biome ID for Moon (Mars will be this + 1, Asteroids + 2 etc). Allowed range 40-250.")
            prop.setLanguageKey("gc.configgui.biome_id_base").setRequiresMcRestart(true)
            biomeIDbase = prop.getInt()
            if (biomeIDbase < 40 || biomeIDbase > 250) {
                biomeIDbase = 102
            }
            finishProp(prop)

            prop = getConfig(Constants.CONFIG_CATEGORY_DIMENSIONS, "Static Loaded Dimensions", staticLoadDimensions)
            prop.setComment("IDs to load at startup, and keep loaded until server stops. Can be added via /gckeeploaded")
            prop.setLanguageKey("gc.configgui.static_loaded_dimensions")
            staticLoadDimensions = prop.getIntList()
            finishProp(prop)

            prop = getConfig(Constants.CONFIG_CATEGORY_DIMENSIONS, "Set new Space Stations to be static loaded", true)
            prop.setComment("Set this to true to have an automatic /gckeeploaded for any new Space Station created.")
            prop.setLanguageKey("gc.configgui.static_loaded_new_ss")
            keepLoadedNewSpaceStations = prop.getBoolean()
            finishProp(prop)

            prop = ConfigManagerCore.getConfig(
                Constants.CONFIG_CATEGORY_DIMENSIONS,
                "Dimensions where rockets cannot launch",
                arrayOf<String>("1", "-1")
            )
            prop.setComment("IDs of dimensions where rockets should not launch - this should always include the Nether.")
            prop.setLanguageKey("gc.configgui.rocket_disabled_dimensions")
            disableRocketLaunchDimensions = prop.getIntList()
            disableRocketLaunchAllNonGC = searchAsterisk(prop.getStringList())
            finishProp(prop)

            prop = getConfig(Constants.CONFIG_CATEGORY_DIMENSIONS, "Disable rockets from returning to Overworld", false)
            prop.setComment("If true, rockets will be unable to reach the Overworld (only use this in special modpacks!)")
            prop.setLanguageKey("gc.configgui.rocket_disable_overworld_return")
            disableRocketsToOverworld = prop.getBoolean(false)
            finishProp(prop)

            prop = getConfig(
                Constants.CONFIG_CATEGORY_SERVER,
                "World border for landing location on other planets (Moon, Mars, etc)",
                0
            )
            prop.setComment("Set this to 0 for no borders (default).  If set to e.g. 2000, players will land on the Moon inside the x,z range -2000 to 2000.)")
            prop.setLanguageKey("gc.configgui.planet_worldborders")
            otherPlanetWorldBorders = prop.getInt(0)
            finishProp(prop)

            prop = getConfig(Constants.CONFIG_CATEGORY_GENERAL, "Force Overworld Spawn", false)
            prop.setComment("By default, you will respawn on Galacticraft dimensions if you die. If you are dying over and over on a planet, set this to true, and you will respawn back on the Overworld.")
            prop.setLanguageKey("gc.configgui.force_overworld_respawn")
            forceOverworldRespawn = prop.getBoolean(false)
            finishProp(prop)

            //
            prop = getConfig(Constants.CONFIG_CATEGORY_SCHEMATIC, "idSchematicRocketT1", 0)
            prop.setComment("Schematic ID for Tier 1 Rocket, must be unique.")
            prop.setLanguageKey("gc.configgui.id_schematic_rocket_t1")
            idSchematicRocketT1 = prop.getInt(0)
            finishProp(prop)

            prop = getConfig(Constants.CONFIG_CATEGORY_SCHEMATIC, "idSchematicMoonBuggy", 1)
            prop.setComment("Schematic ID for Moon Buggy, must be unique.")
            prop.setLanguageKey("gc.configgui.id_schematic_moon_buggy")
            idSchematicMoonBuggy = prop.getInt(1)
            finishProp(prop)

            prop = getConfig(Constants.CONFIG_CATEGORY_SCHEMATIC, "idSchematicAddSchematic", Int.MAX_VALUE)
            prop.setComment("Schematic ID for \"Add Schematic\" Page, must be unique")
            prop.setLanguageKey("gc.configgui.id_schematic_add_schematic")
            idSchematicAddSchematic = prop.getInt(Int.MAX_VALUE)
            finishProp(prop)

            //
            prop = getConfig(Constants.CONFIG_CATEGORY_ACHIEVEMENTS, "idAchievBase", 1784)
            prop.setComment("Base Achievement ID. All achievement IDs will start at this number.")
            prop.setLanguageKey("gc.configgui.id_achiev_base")
            idAchievBase = prop.getInt(1784)
            finishProp(prop)

            //Client side
            prop = getConfig(Constants.CONFIG_CATEGORY_CLIENT, "More Stars", true)
            prop.setComment("Setting this to false will revert night skies back to default minecraft star count")
            prop.setLanguageKey("gc.configgui.more_stars")
            moreStars = prop.getBoolean(true)
            finishProp(prop)

            prop = getConfig(Constants.CONFIG_CATEGORY_CLIENT, "Disable Spaceship Particles", false)
            prop.setComment("If you have FPS problems, setting this to true will help if rocket particles are in your sights")
            prop.setLanguageKey("gc.configgui.disable_spaceship_particles")
            disableSpaceshipParticles = prop.getBoolean(false)
            finishProp(prop)

            prop = getConfig(Constants.CONFIG_CATEGORY_CLIENT, "Disable Vehicle Third-Person and Zoom", false)
            prop.setComment("If you're using this mod in virtual reality, or if you don't want the camera changes when entering a Galacticraft vehicle, set this to true.")
            prop.setLanguageKey("gc.configgui.disable_vehicle_camera_changes")
            disableVehicleCameraChanges = prop.getBoolean(false)
            finishProp(prop)

            prop = getConfig(Constants.CONFIG_CATEGORY_CLIENT, "Minimap Left", false)
            prop.setComment("If true, this will move the Oxygen Indicator to the left side. You can combine this with \"Minimap Bottom\"")
            prop.setLanguageKey("gc.configgui.oxygen_indicator_left")
            oxygenIndicatorLeft = prop.getBoolean(false)
            finishProp(prop)

            prop = getConfig(Constants.CONFIG_CATEGORY_CLIENT, "Minimap Bottom", false)
            prop.setComment("If true, this will move the Oxygen Indicator to the bottom. You can combine this with \"Minimap Left\"")
            prop.setLanguageKey("gc.configgui.oxygen_indicator_bottom")
            oxygenIndicatorBottom = prop.getBoolean(false)
            finishProp(prop)

            //World gen
            prop = getConfig(Constants.CONFIG_CATEGORY_WORLDGEN, "Oil Generation Factor", 1.8)
            prop.setComment("Increasing this will increase amount of oil that will generate in each chunk.")
            prop.setLanguageKey("gc.configgui.oil_gen_factor")
            oilGenFactor = prop.getDouble(1.8)
            finishProp(prop)

            prop = getConfig(Constants.CONFIG_CATEGORY_WORLDGEN, "Oil gen in external dimensions", intArrayOf(0))
            prop.setComment("List of non-galacticraft dimension IDs to generate oil in.")
            prop.setLanguageKey("gc.configgui.external_oil_gen")
            externalOilGen = prop.getIntList()
            finishProp(prop)

            prop = getConfig(Constants.CONFIG_CATEGORY_WORLDGEN, "Retro Gen of GC Oil in existing map chunks", false)
            prop.setComment("If this is enabled, GC oil will be added to existing Overworld maps where possible.")
            prop.setLanguageKey("gc.configgui.enable_retrogen_oil")
            retrogenOil = prop.getBoolean(false)
            finishProp(prop)

            prop = getConfig(Constants.CONFIG_CATEGORY_WORLDGEN, "Enable Copper Ore Gen", true)
            prop.setComment("If this is enabled, copper ore will generate on the overworld.")
            prop.setLanguageKey("gc.configgui.enable_copper_ore_gen").setRequiresMcRestart(true)
            enableCopperOreGen = prop.getBoolean(true)
            finishProp(prop)

            prop = getConfig(Constants.CONFIG_CATEGORY_WORLDGEN, "Enable Tin Ore Gen", true)
            prop.setComment("If this is enabled, tin ore will generate on the overworld.")
            prop.setLanguageKey("gc.configgui.enable_tin_ore_gen").setRequiresMcRestart(true)
            enableTinOreGen = prop.getBoolean(true)
            finishProp(prop)

            prop = getConfig(Constants.CONFIG_CATEGORY_WORLDGEN, "Enable Aluminum Ore Gen", true)
            prop.setComment("If this is enabled, aluminum ore will generate on the overworld.")
            prop.setLanguageKey("gc.configgui.enable_aluminum_ore_gen").setRequiresMcRestart(true)
            enableAluminumOreGen = prop.getBoolean(true)
            finishProp(prop)

            prop = getConfig(Constants.CONFIG_CATEGORY_WORLDGEN, "Enable Silicon Ore Gen", true)
            prop.setComment("If this is enabled, silicon ore will generate on the overworld.")
            prop.setLanguageKey("gc.configgui.enable_silicon_ore_gen").setRequiresMcRestart(true)
            enableSiliconOreGen = prop.getBoolean(true)
            finishProp(prop)

            prop = getConfig(Constants.CONFIG_CATEGORY_WORLDGEN, "Disable Cheese Ore Gen on Moon", false)
            prop.setComment("Disable Cheese Ore Gen on Moon.")
            prop.setLanguageKey("gc.configgui.disable_cheese_moon")
            disableCheeseMoon = prop.getBoolean(false)
            finishProp(prop)

            prop = getConfig(Constants.CONFIG_CATEGORY_WORLDGEN, "Disable Tin Ore Gen on Moon", false)
            prop.setComment("Disable Tin Ore Gen on Moon.")
            prop.setLanguageKey("gc.configgui.disable_tin_moon")
            disableTinMoon = prop.getBoolean(false)
            finishProp(prop)

            prop = getConfig(Constants.CONFIG_CATEGORY_WORLDGEN, "Disable Copper Ore Gen on Moon", false)
            prop.setComment("Disable Copper Ore Gen on Moon.")
            prop.setLanguageKey("gc.configgui.disable_copper_moon")
            disableCopperMoon = prop.getBoolean(false)
            finishProp(prop)

            prop = getConfig(Constants.CONFIG_CATEGORY_WORLDGEN, "Disable Sapphire Ore Gen on Moon", false)
            prop.setComment("Disable Sapphire Ore Gen on Moon.")
            prop.setLanguageKey("gc.configgui.disable_sapphire_moon")
            disableSapphireMoon = prop.getBoolean(false)
            finishProp(prop)

            prop = getConfig(Constants.CONFIG_CATEGORY_WORLDGEN, "Disable Moon Village Gen", false)
            prop.setComment("If true, moon villages will not generate.")
            prop.setLanguageKey("gc.configgui.disable_moon_village_gen")
            disableMoonVillageGen = prop.getBoolean(false)
            finishProp(prop)

            prop = getConfig(Constants.CONFIG_CATEGORY_WORLDGEN, "Generate all other mods features on planets", false)
            prop.setComment("If this is enabled, other mods' standard ores and all other features (eg. plants) can generate on the Moon and planets. Apart from looking wrong, this make cause 'Already Decorating!' type crashes.  NOT RECOMMENDED!  See Wiki.")
            prop.setLanguageKey("gc.configgui.enable_other_mods_features")
            enableOtherModsFeatures = prop.getBoolean(false)
            finishProp(prop)

            prop = getConfig(
                Constants.CONFIG_CATEGORY_WORLDGEN,
                "Whitelist CoFHCore worldgen to generate its ores and lakes on planets",
                false
            )
            prop.setComment("If generate other mods features is disabled as recommended, this setting can whitelist CoFHCore custom worldgen on planets.")
            prop.setLanguageKey("gc.configgui.whitelist_co_f_h_core_gen")
            whitelistCoFHCoreGen = prop.getBoolean(false)
            finishProp(prop)

            prop = getConfig(
                Constants.CONFIG_CATEGORY_WORLDGEN,
                "Generate ThaumCraft wild nodes on planetary surfaces",
                true
            )
            prop.setComment("If ThaumCraft is installed, ThaumCraft wild nodes can generate on the Moon and planets.")
            prop.setLanguageKey("gc.configgui.enable_thaum_craft_nodes")
            enableThaumCraftNodes = prop.getBoolean(true)
            finishProp(prop)

            prop = getConfig(
                Constants.CONFIG_CATEGORY_WORLDGEN,
                "Other mods ores for GC to generate on the Moon and planets",
                arrayOf<String?>()
            )
            prop.setComment("Enter IDs of other mods' ores here for Galacticraft to generate them on the Moon and other planets. Format is BlockName or BlockName:metadata. Use optional parameters at end of each line: /RARE /UNCOMMON or /COMMON for rarity in a chunk; /DEEP /SHALLOW or /BOTH for height; /SINGLE /STANDARD or /LARGE for clump size; /XTRARANDOM for ores sometimes there sometimes not at all.  /ONLYMOON or /ONLYMARS if wanted on one planet only.  If nothing specified, defaults are /COMMON, /BOTH and /STANDARD.  Repeat lines to generate a huge quantity of ores.")
            prop.setLanguageKey("gc.configgui.other_mod_ore_gen_i_ds")
            oregenIDs = prop.getStringList()
            finishProp(prop)

            prop = getConfig(Constants.CONFIG_CATEGORY_COMPATIBILITY, "Use legacy oilgc fluid registration", false)
            prop.setComment("Set to true to make Galacticraft oil register as oilgc, for backwards compatibility with previously generated worlds.")
            prop.setLanguageKey("gc.configgui.use_old_oil_fluid_i_d")
            useOldOilFluidID = prop.getBoolean(false)
            finishProp(prop)

            prop = getConfig(Constants.CONFIG_CATEGORY_COMPATIBILITY, "Use legacy fuelgc fluid registration", false)
            prop.setComment("Set to true to make Galacticraft fuel register as fuelgc, for backwards compatibility with previously generated worlds.")
            prop.setLanguageKey("gc.configgui.use_old_fuel_fluid_i_d")
            useOldFuelFluidID = prop.getBoolean(false)
            finishProp(prop)

            prop = getConfig(Constants.CONFIG_CATEGORY_GENERAL, "Disable lander on Moon and other planets", false)
            prop.setComment("If this is true, the player will parachute onto the Moon instead - use only in debug situations.")
            prop.setLanguageKey("gc.configgui.disable_lander")
            disableLander = prop.getBoolean(false)
            finishProp(prop)

            //Server side
            prop = getConfig(Constants.CONFIG_CATEGORY_DIFFICULTY, "Disable Spaceship Explosion", false)
            prop.setComment("Spaceships will not explode on contact if set to true.")
            prop.setLanguageKey("gc.configgui.disable_spaceship_grief")
            disableSpaceshipGrief = prop.getBoolean(false)
            finishProp(prop)

            prop = getConfig(Constants.CONFIG_CATEGORY_SERVER, "Space Stations Require Permission", true)
            prop.setComment("While true, space stations require you to invite other players using /ssinvite <playername>")
            prop.setLanguageKey("gc.configgui.space_stations_require_permission")
            spaceStationsRequirePermission = prop.getBoolean(true)
            finishProp(prop)

            prop = getConfig(Constants.CONFIG_CATEGORY_SERVER, "Disable Space Station creation", false)
            prop.setComment("If set to true on a server, players will be completely unable to create space stations.")
            prop.setLanguageKey("gc.configgui.disable_space_station_creation")
            disableSpaceStationCreation = prop.getBoolean(false)
            finishProp(prop)

            prop = getConfig(Constants.CONFIG_CATEGORY_CLIENT, "Override Capes", true)
            prop.setComment("By default, Galacticraft will override capes with the mod's donor cape. Set to false to disable.")
            prop.setLanguageKey("gc.configgui.override_capes")
            overrideCapes = prop.getBoolean(true)
            finishProp(prop)

            prop = getConfig(Constants.CONFIG_CATEGORY_DIFFICULTY, "Space Station Solar Energy Multiplier", 2.0)
            prop.setComment("Solar panels will work (default 2x) more effective on space stations.")
            prop.setLanguageKey("gc.configgui.space_station_energy_scalar")
            spaceStationEnergyScalar = prop.getDouble(2.0)
            finishProp(prop)

            try {
                prop = ConfigManagerCore.getConfig(
                    Constants.CONFIG_CATEGORY_COMPATIBILITY,
                    "External Sealable IDs",
                    arrayOf<String>(Block.REGISTRY.getNameForObject(Blocks.GLASS_PANE).toString() + ":0")
                )
                prop.setComment("List non-opaque blocks from other mods (for example, special types of glass) that the Oxygen Sealer should recognize as solid seals. Format is BlockName or BlockName:metadata")
                prop.setLanguageKey("gc.configgui.sealable_i_ds").setRequiresMcRestart(true)
                sealableIDs = prop.getStringList()
                finishProp(prop)
            } catch (e: Exception) {
                FMLLog.severe("[Galacticraft] It appears you have installed the 'Dev' version of Galacticraft instead of the regular version (or vice versa).  Please re-install.")
            }

            prop = getConfig(
                Constants.CONFIG_CATEGORY_COMPATIBILITY, "External Detectable IDs", arrayOf<String?>(
                    Block.REGISTRY.getNameForObject(Blocks.COAL_ORE).getPath(),
                    Block.REGISTRY.getNameForObject(Blocks.DIAMOND_ORE).getPath(),
                    Block.REGISTRY.getNameForObject(Blocks.GOLD_ORE).getPath(),
                    Block.REGISTRY.getNameForObject(Blocks.IRON_ORE).getPath(),
                    Block.REGISTRY.getNameForObject(Blocks.LAPIS_ORE).getPath(),
                    Block.REGISTRY.getNameForObject(Blocks.REDSTONE_ORE).getPath(),
                    Block.REGISTRY.getNameForObject(Blocks.LIT_REDSTONE_ORE).getPath()
                )
            )
            prop.setComment("List blocks from other mods that the Sensor Glasses should recognize as solid blocks. Format is BlockName or BlockName:metadata.")
            prop.setLanguageKey("gc.configgui.detectable_i_ds").setRequiresMcRestart(true)
            detectableIDs = prop.getStringList()
            finishProp(prop)

            prop = getConfig(Constants.CONFIG_CATEGORY_DIFFICULTY, "Quick Game Mode", false)
            prop.setComment("Set this to true for less metal use in Galacticraft recipes (makes the game easier).")
            prop.setLanguageKey("gc.configgui.quick_mode")
            quickMode = prop.getBoolean(false)
            finishProp(prop)

            prop = getConfig(Constants.CONFIG_CATEGORY_DIFFICULTY, "Harder Difficulty", false)
            prop.setComment("Set this to true for increased difficulty in modpacks (see forum for more info).")
            prop.setLanguageKey("gc.configgui.hard_mode")
            hardMode = prop.getBoolean(false)
            finishProp(prop)

            prop = getConfig(Constants.CONFIG_CATEGORY_DIFFICULTY, "Adventure Game Mode", false)
            prop.setComment("Set this to true for a challenging adventure where the player starts the game stranded in the Asteroids dimension with low resources (only effective if Galacticraft Planets installed).")
            prop.setLanguageKey("gc.configgui.asteroids_start")
            challengeMode = prop.getBoolean(false)
            if (!GalacticraftCore.isPlanetsLoaded) {
                challengeMode = false
            }
            finishProp(prop)

            prop = getConfig(Constants.CONFIG_CATEGORY_DIFFICULTY, "Adventure Game Mode Flags", 15)
            prop.setComment("Add together flags 8, 4, 2, 1 to enable the four elements of adventure game mode. Default 15.  1 = extended compressor recipes.  2 = mob drops and spawning.  4 = more trees in hollow asteroids.  8 = start stranded in Asteroids.")
            prop.setLanguageKey("gc.configgui.asteroids_flags")
            challengeFlags = prop.getInt(15)
            finishProp(prop)

            prop = getConfig(Constants.CONFIG_CATEGORY_DIFFICULTY, "Suffocation Cooldown", 100)
            prop.setComment("Lower/Raise this value to change time between suffocation damage ticks (allowed range 50-250)")
            prop.setLanguageKey("gc.configgui.suffocation_cooldown")
            suffocationCooldown = min(max(50, prop.getInt(100)), 250)
            finishProp(prop)

            prop = getConfig(Constants.CONFIG_CATEGORY_DIFFICULTY, "Suffocation Damage", 2)
            prop.setComment("Change this value to modify the damage taken per suffocation tick")
            prop.setLanguageKey("gc.configgui.suffocation_damage")
            suffocationDamage = prop.getInt(2)
            finishProp(prop)

            prop = getConfig(Constants.CONFIG_CATEGORY_DIFFICULTY, "Dungeon Boss Health Modifier", 1.0)
            prop.setComment("Change this if you wish to balance the mod (if you have more powerful weapon mods).")
            prop.setLanguageKey("gc.configgui.dungeon_boss_health_mod")
            dungeonBossHealthMod = prop.getDouble(1.0)
            finishProp(prop)

            prop = getConfig(Constants.CONFIG_CATEGORY_SERVER, "Enable Sealed edge checks", true)
            prop.setComment("If this is enabled, areas sealed by Oxygen Sealers will run a seal check when the player breaks or places a block (or on block updates).  This should be enabled for a 100% accurate sealed status, but can be disabled on servers for performance reasons.")
            prop.setLanguageKey("gc.configgui.enable_sealer_edge_checks")
            enableSealerEdgeChecks = prop.getBoolean(true)
            finishProp(prop)

            prop = getConfig(Constants.CONFIG_CATEGORY_COMPATIBILITY, "Alternate recipe for canisters", false)
            prop.setComment("Enable this if the standard canister recipe causes a conflict.")
            prop.setLanguageKey("gc.configgui.alternate_canister_recipe").setRequiresMcRestart(true)
            alternateCanisterRecipe = prop.getBoolean(false)
            finishProp(prop)

            prop =
                getConfig(Constants.CONFIG_CATEGORY_COMPATIBILITY, "OreDict name of other mod's silicon", "itemSilicon")
            prop.setComment("This needs to match the OreDictionary name used in the other mod. Set a nonsense name to disable.")
            prop.setLanguageKey("gc.configgui.ore_dict_silicon")
            otherModsSilicon = prop.getString()
            finishProp(prop)

            prop = getConfig(Constants.CONFIG_CATEGORY_COMPATIBILITY, "Must use GC's own space metals in recipes", true)
            prop.setComment("Should normally be true. If you set this to false, in a modpack with other mods with the same metals, players may be able to craft advanced GC items without travelling to Moon, Mars, Asteroids etc.")
            prop.setLanguageKey("gc.configgui.disable_ore_dict_space_metals")
            recipesRequireGCAdvancedMetals = prop.getBoolean(true)
            finishProp(prop)

            prop = getConfig(Constants.CONFIG_CATEGORY_KEYS, "Open Galaxy Map", "KEY_M")
            prop.setComment("Default Map key on first Galacticraft run only. After first run, change keys by Minecraft in-game Controls menu.  Valid settings: KEY_ followed by 0-9 or A-Z.")
            prop.setLanguageKey("gc.configgui.override_map").setRequiresMcRestart(true)
            keyOverrideMap = prop.getString()
            keyOverrideMapI = parseKeyValue((keyOverrideMap)!!)
            finishProp(prop)

            prop = getConfig(Constants.CONFIG_CATEGORY_KEYS, "Open Rocket GUI", "KEY_G")
            prop.setComment("Default Rocket/Fuel key on first Galacticraft run only. After first run, change keys by Minecraft in-game Controls menu.  Valid settings: KEY_ followed by 0-9 or A-Z.")
            prop.setLanguageKey("gc.configgui.key_override_fuel_level").setRequiresMcRestart(true)
            keyOverrideFuelLevel = prop.getString()
            keyOverrideFuelLevelI = parseKeyValue((keyOverrideFuelLevel)!!)
            finishProp(prop)

            prop = getConfig(Constants.CONFIG_CATEGORY_KEYS, "Toggle Advanced Goggles", "KEY_K")
            prop.setComment("Default Goggles key on first Galacticraft run only. After first run, change keys by Minecraft in-game Controls menu.  Valid settings: KEY_ followed by 0-9 or A-Z.")
            prop.setLanguageKey("gc.configgui.key_override_toggle_adv_goggles").setRequiresMcRestart(true)
            keyOverrideToggleAdvGoggles = prop.getString()
            keyOverrideToggleAdvGogglesI = parseKeyValue((keyOverrideToggleAdvGoggles)!!)
            finishProp(prop)

            prop = getConfig(Constants.CONFIG_CATEGORY_COMPATIBILITY, "Rocket fuel factor", 1)

            prop.setLanguageKey("gc.configgui.rocket_fuel_factor")
            prop.setComment("The normal factor is 1  Increase this to 2 - 5 to increase GC rocket fuel requirements")
            rocketFuelFactor = prop.getInt(1)
            finishProp(prop)

            //            prop = getConfig(Constants.CONFIG_CATEGORY_GENERAL, "Map factor", 1);
//            prop.setComment("Allowed values 1-4 etc";
//            prop.setLanguageKey("gc.configgui.mapFactor");
//            mapfactor = prop.getInt(1);
//            finishProp(prop);
//            
//            prop = getConfig(Constants.CONFIG_CATEGORY_GENERAL, "Map size", 400);
//            prop.setComment("Suggested value 400";
//            prop.setLanguageKey("gc.configgui.mapSize");
//            mapsize = prop.getInt(400);
//            finishProp(prop);
//            
            prop = getConfig(Constants.CONFIG_CATEGORY_CONTROLS, "Map Scroll Mouse Sensitivity", 1.0)
            prop.setComment("Increase to make the mouse drag scroll more sensitive, decrease to lower sensitivity.")
            prop.setLanguageKey("gc.configgui.map_scroll_sensitivity")
            mapMouseScrollSensitivity = prop.getDouble(1.0).toFloat()
            finishProp(prop)

            prop = getConfig(Constants.CONFIG_CATEGORY_CONTROLS, "Map Scroll Mouse Invert", false)
            prop.setComment("Set to true to invert the mouse scroll feature on the galaxy map.")
            prop.setLanguageKey("gc.configgui.map_scroll_invert")
            invertMapMouseScroll = prop.getBoolean(false)
            finishProp(prop)

            prop = getConfig(Constants.CONFIG_CATEGORY_DIFFICULTY, "Meteor Spawn Modifier", 1.0)
            prop.setComment("Set to a value between 0.0 and 1.0 to decrease meteor spawn chance (all dimensions).")
            prop.setLanguageKey("gc.configgui.meteor_spawn_mod")
            meteorSpawnMod = prop.getDouble(1.0)
            finishProp(prop)

            prop = getConfig(Constants.CONFIG_CATEGORY_DIFFICULTY, "Meteor Block Damage Enabled", true)
            prop.setComment("Set to false to stop meteors from breaking blocks on contact.")
            prop.setLanguageKey("gc.configgui.meteor_block_damage")
            meteorBlockDamageEnabled = prop.getBoolean(true)
            finishProp(prop)

            prop = getConfig(Constants.CONFIG_CATEGORY_GENERAL, "Disable Update Check", false)
            prop.setComment("Update check will not run if this is set to true.")
            prop.setLanguageKey("gc.configgui.disable_update_check")
            disableUpdateCheck = prop.getBoolean(false)
            finishProp(prop)

            prop = getConfig(Constants.CONFIG_CATEGORY_GENERAL, "Allow liquids into Gratings", true)
            prop.setComment("Liquids will not flow into Grating block if this is set to false.")
            prop.setLanguageKey("gc.configgui.allow_liquids_grating").setRequiresMcRestart(true)
            allowLiquidGratings = prop.getBoolean(true)
            finishProp(prop)

            prop = getConfig(Constants.CONFIG_CATEGORY_WORLDGEN, "Disable Biome Type Registrations", false)
            prop.setComment("Biome Types will not be registered in the BiomeDictionary if this is set to true.")
            prop.setLanguageKey("gc.configgui.disable_biome_type_registrations")
            disableBiomeTypeRegistrations = prop.getBoolean(false)
            finishProp(prop)

            prop = getConfig(Constants.CONFIG_CATEGORY_GENERAL, "Enable Space Race Manager Popup", false)
            prop.setComment("Space Race Manager will show on-screen after login, if enabled.")
            prop.setLanguageKey("gc.configgui.enable_space_race_manager_popup")
            enableSpaceRaceManagerPopup = prop.getBoolean(false)
            finishProp(prop)

            //Cleanup older GC config files
            cleanConfig(config, propOrder)

            if (config!!.hasChanged()) {
                config!!.save()
            }

            challengeModeUpdate()
        } catch (e: Exception) {
            GCLog.severe("Problem loading core config (\"core.conf\")")
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun cleanConfig(config: Configuration?, propOrder: MutableMap<String?, MutableList<String?>?>) {
        val categoriesToRemove: MutableList<String?> = LinkedList<String?>()
        for (catName: String? in config!!.getCategoryNames()) {
            val newProps: MutableList<String?>? = propOrder.get(catName)
            if (newProps == null) {
                categoriesToRemove.add(catName)
            } else {
                val cat: ConfigCategory = config.getCategory(catName)
                val toRemove: MutableList<String?> = LinkedList<String?>()
                for (oldprop: String? in cat.keys) {
                    if (!newProps.contains(oldprop)) {
                        toRemove.add(oldprop)
                    }
                }
                for (removeMe: String? in toRemove) {
                    cat.remove(removeMe)
                }
                config.setCategoryPropertyOrder(catName, propOrder.get(catName))
            }
        }
        for (catName: String? in categoriesToRemove) {
            config.removeCategory(config.getCategory(catName))
        }
    }

    @JvmStatic
    private fun getConfig(cat: String, key: String, defaultValue: Int): Property {
        config!!.moveProperty(Constants.CONFIG_CATEGORY_GENERAL, key, cat)
        currentCat = cat
        return config!!.get(cat, key, defaultValue)
    }

    @JvmStatic
    private fun getConfig(cat: String, key: String, defaultValue: Double): Property {
        config!!.moveProperty(Constants.CONFIG_CATEGORY_GENERAL, key, cat)
        currentCat = cat
        return config!!.get(cat, key, defaultValue)
    }

    @JvmStatic
    private fun getConfig(cat: String, key: String, defaultValue: Boolean): Property {
        config!!.moveProperty(Constants.CONFIG_CATEGORY_GENERAL, key, cat)
        currentCat = cat
        return config!!.get(cat, key, defaultValue)
    }

    @JvmStatic
    private fun getConfig(cat: String, key: String, defaultValue: String): Property {
        config!!.moveProperty(Constants.CONFIG_CATEGORY_GENERAL, key, cat)
        config!!.moveProperty(Constants.CONFIG_CATEGORY_CONTROLS, key, cat)
        currentCat = cat
        return config!!.get(cat, key, defaultValue)
    }

    @JvmStatic
    private fun getConfig(cat: String, key: String, defaultValue: Array<out String?>): Property {
        config!!.moveProperty(Constants.CONFIG_CATEGORY_GENERAL, key, cat)
        currentCat = cat
        return config!!.get(cat, key, defaultValue)
    }

    @JvmStatic
    private fun getConfig(cat: String, key: String, defaultValue: IntArray?): Property {
        config!!.moveProperty(Constants.CONFIG_CATEGORY_GENERAL, key, cat)
        currentCat = cat
        return config!!.get(cat, key, defaultValue)
    }

    @JvmStatic
    private fun finishProp(prop: Property) {
        if (propOrder.get(currentCat) == null) {
            propOrder.put(currentCat, ArrayList<String?>())
        }
        propOrder.get(currentCat)!!.add(prop.getName())
    }

    @JvmStatic
    fun setLoaded(newID: Int): Boolean {
        var found: Boolean = false

        for (staticLoadDimension: Int in staticLoadDimensions) {
            if (staticLoadDimension == newID) {
                found = true
                break
            }
        }

        if (!found) {
            val oldIDs: IntArray = staticLoadDimensions
            staticLoadDimensions = IntArray(staticLoadDimensions.size + 1)
            System.arraycopy(oldIDs, 0, staticLoadDimensions, 0, oldIDs.size)

            staticLoadDimensions[staticLoadDimensions.size - 1] = newID
            val values: Array<String?> = arrayOfNulls<String>(staticLoadDimensions.size)
            Arrays.sort(staticLoadDimensions)

            for (i: Int in values.indices) {
                values[i] = staticLoadDimensions[i].toString()
            }

            val prop: Property =
                config!!.get(Constants.CONFIG_CATEGORY_DIMENSIONS, "Static Loaded Dimensions", staticLoadDimensions)
            prop.setComment("IDs to load at startup, and keep loaded until server stops. Can be added via /gckeeploaded")
            prop.setLanguageKey("gc.configgui.static_loaded_dimensions")
            prop.set(values)

            config!!.save()
        }

        return !found
    }

    @JvmStatic
    fun setUnloaded(idToRemove: Int): Boolean {
        var foundCount: Int = 0

        for (staticLoadDimension: Int in staticLoadDimensions) {
            if (staticLoadDimension == idToRemove) {
                foundCount++
            }
        }

        if (foundCount > 0) {
            val idArray: MutableList<Int> = ArrayList<Int>(Ints.asList(*staticLoadDimensions))
            idArray.removeAll(mutableSetOf<Int>(idToRemove))

            staticLoadDimensions = IntArray(idArray.size)

            for (i: Int in idArray.indices) {
                staticLoadDimensions[i] = idArray.get(i)!!
            }

            val values: Array<String?> = arrayOfNulls<String>(staticLoadDimensions.size)
            Arrays.sort(staticLoadDimensions)

            for (i: Int in values.indices) {
                values[i] = staticLoadDimensions[i].toString()
            }

            val prop: Property =
                config!!.get(Constants.CONFIG_CATEGORY_DIMENSIONS, "Static Loaded Dimensions", staticLoadDimensions)
            prop.setComment("IDs to load at startup, and keep loaded until server stops. Can be added via /gckeeploaded")
            prop.setLanguageKey("gc.configgui.static_loaded_dimensions")
            prop.set(values)

            config!!.save()
        }

        return foundCount > 0
    }

    @JvmStatic
    fun challengeModeUpdate() {
        if (challengeMode) {
            challengeRecipes = (challengeFlags and 1) > 0
            challengeMobDropsAndSpawning = (challengeFlags and 2) > 0
            challengeAsteroidPopulation = (challengeFlags and 4) > 0
            challengeSpawnHandling = (challengeFlags and 8) > 0
        } else {
            challengeRecipes = false
            challengeMobDropsAndSpawning = false
            challengeAsteroidPopulation = false
            challengeSpawnHandling = false
        }
    }

    /**
     * Note for this to be effective, the prop = config.get() call has to provide a String[] as the default values
     * If you use an Integer[] then the config parser deletes all non-numerical lines from the config before GC even sees them
     */
    @JvmStatic
    private fun searchAsterisk(strings: Array<String?>): Boolean {
        for (s: String? in strings) {
            if (s != null && "*" == s.trim { it <= ' ' }) {
                return true
            }
        }
        return false
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

    @JvmStatic
    fun stringToBlock(s: String, caller: String?, logging: Boolean): BlockTuple? {
        val lastColon: Int = s.lastIndexOf(':')
        var meta: Int = -1
        val name: String

        if (lastColon > 0) {
            try {
                meta = s.substring(lastColon + 1, s.length).toInt()
            } catch (ex: NumberFormatException) {
            }
        }

        if (meta == -1) {
            name = s
        } else {
            name = s.substring(0, lastColon)
        }

        var block: Block? = Block.getBlockFromName(name)
        if (block == null) {
            val item: Item? = Item.REGISTRY.getObject(ResourceLocation.parse(name))
            if (item is ItemBlock) {
                block = item.getBlock()
            }
            if (block == null) {
                if (logging) {
                    GCLog.severe("[config] " + caller + ": unrecognised block name '" + s + "'.")
                }
                return null
            }
        }
        try {
            name.toInt()
            val bName: String = Block.REGISTRY.getNameForObject(block).toString()
            if (logging) {
                GCLog.info("[config] " + caller + ": the use of numeric IDs is discouraged, please use " + bName + " instead of " + name)
            }
        } catch (ex: NumberFormatException) {
        }
        if (Blocks.AIR === block) {
            if (logging) {
                GCLog.info("[config] " + caller + ": not a good idea to specify air, skipping that!")
            }
            return null
        }

        return BlockTuple(block, meta)
    }

    @JvmStatic
    fun getServerConfigOverride(): MutableList<Any?> {
        val returnList: ArrayList<Any?> = ArrayList<Any?>()
        var modeFlags: Int = if (hardMode) 1 else 0
        modeFlags += if (quickMode) 2 else 0
        modeFlags += if (challengeMode) 4 else 0
        modeFlags += if (disableSpaceStationCreation) 8 else 0
        modeFlags += if (recipesRequireGCAdvancedMetals) 16 else 0
        modeFlags += if (challengeRecipes) 32 else 0
        modeFlags += if (allowLiquidGratings) 64 else 0
        returnList.add(modeFlags)
        returnList.add(dungeonBossHealthMod)
        returnList.add(suffocationDamage)
        returnList.add(suffocationCooldown)
        returnList.add(rocketFuelFactor)
        returnList.add(otherModsSilicon)
        //If changing this, update definition of EnumSimplePacket.C_UPDATE_CONFIGS - see comment in setConfigOverride() below
        EnergyConfigHandler.serverConfigOverride(returnList)

        returnList.add(detectableIDs.clone())

        //TODO Should this include any other client-side configurables too?
        return returnList
    }

    @JvmStatic
    @OnlyIn(Dist.CLIENT)
    fun setConfigOverride(configs: MutableList<Any?>) {
        var dataCount: Int = 0
        val modeFlag: Int = configs.get(dataCount++) as Int
        hardMode = (modeFlag and 1) != 0
        quickMode = (modeFlag and 2) != 0
        challengeMode = (modeFlag and 4) != 0
        disableSpaceStationCreation = (modeFlag and 8) != 0
        recipesRequireGCAdvancedMetals = (modeFlag and 16) != 0
        challengeRecipes = (modeFlag and 32) != 0
        allowLiquidGratings = (modeFlag and 64) != 0
        dungeonBossHealthMod = (configs.get(dataCount++) as kotlin.Double?)!!
        suffocationDamage = (configs.get(dataCount++) as kotlin.Int?)!!
        suffocationCooldown = (configs.get(dataCount++) as kotlin.Int?)!!
        rocketFuelFactor = (configs.get(dataCount++) as kotlin.Int?)!!
        otherModsSilicon = configs.get(dataCount++) as String?

        //If adding any additional data objects here, also remember to update the packet definition of EnumSimplePacket.C_UPDATE_CONFIGS in PacketSimple
        //Current working packet definition: Integer.class, Double.class, Integer.class, Integer.class, Integer.class, String.class, Float.class, Float.class, Float.class, Float.class, Integer.class, String[].class
        EnergyConfigHandler.setConfigOverride(
            (configs.get(dataCount++) as kotlin.Float?)!!,
            (configs.get(dataCount++) as kotlin.Float?)!!,
            (configs.get(dataCount++) as kotlin.Float?)!!,
            (configs.get(dataCount++) as kotlin.Float?)!!,
            (configs.get(dataCount++) as kotlin.Int?)!!
        )

        val sizeIDs: Int = configs.size - dataCount
        if (sizeIDs > 0) {
            val dataLast: Any? = configs.get(dataCount)
            if (dataLast is String) {
                detectableIDs = arrayOfNulls<String>(sizeIDs)
                for (j: Int in 0..<sizeIDs) detectableIDs[j] =
                    configs.get(dataCount++) as kotlin.String
            } else if ((dataLast is Array<*> && kotlin.String::class.java.isAssignableFrom(dataLast!!.javaClass.componentType))) {
                detectableIDs = (dataLast as Array<kotlin.String?>)
            }
            TickHandlerClient.registerDetectableBlocks(false)
        }

        challengeModeUpdate()
        RecipeManagerGC.setConfigurableRecipes()
    }

    @JvmStatic
    fun saveClientConfigOverrideable() {
        if (clientSave == null) {
            clientSave = getServerConfigOverride() as ArrayList<Any?>
        }
    }

    @JvmStatic
    fun restoreClientConfigOverrideable() {
        if (clientSave != null) {
            ConfigManagerCore.setConfigOverride(clientSave!!)
        }
    }

    @JvmStatic
    private fun parseKeyValue(key: String?): Int {
        return foundation.input.FoundationKeyCodes.fromLegacyName(key) ?: run {
            GCLog.severe("Failed to parse keyboard key: $key — use KEY_A through KEY_Z or KEY_0 through KEY_9")
            -1
        }
    }
}
