package micdoodle8.mods.galacticraft.core


object Constants {
    const val MOD_ID_CORE: String = "galacticraftcore"

    const val MOD_ID_PLANETS: String = "galacticraftplanets"

    const val MOD_NAME_SIMPLE: String = "Galacticraft"

    const val LOCALMAJVERSION: Int = 4

    const val LOCALMINVERSION: Int = 0

    const val LOCALPATCHVERSION: Int = 2

    const val LOCALBUILDVERSION: Int = 0

    const val COMBINEDVERSION: String = "$LOCALMAJVERSION.$LOCALMINVERSION.$LOCALPATCHVERSION.$LOCALBUILDVERSION"

    const val MCVERSION: String = "[1.12.2]"

    const val DEPENDENCIES_FORGE: String = "required-after:forge@[14.23.1.2555,); " //Keep the space at the end!

    const val DEPENDENCIES_MICCORE: String = "required-after:micdoodlecore; " //Keep the space at the end!

    const val DEPENDENCIES_MODS: String = "after:tconstruct; after:mantle;"

    const val CONFIG_CATEGORY_DIMENSIONS: String = "dimensions"

    const val CONFIG_CATEGORY_ENTITIES: String = "entities"

    const val CONFIG_CATEGORY_SCHEMATIC: String = "schematic"

    const val CONFIG_CATEGORY_GENERAL: String = "general"

    const val CONFIG_CATEGORY_WORLDGEN: String = "worldgen"

    const val CONFIG_CATEGORY_ACHIEVEMENTS: String = "achievements"

    const val CONFIG_CATEGORY_CLIENT: String = "client"

    const val CONFIG_CATEGORY_SERVER: String = "server"

    const val CONFIG_CATEGORY_CONTROLS: String = "controls"

    const val CONFIG_CATEGORY_KEYS: String = "keybindings_initial"

    const val CONFIG_CATEGORY_DIFFICULTY: String = "difficulty"

    const val CONFIG_CATEGORY_COMPATIBILITY: String = "compatibility"

    const val OVERWORLD_SKYPROVIDER_STARTHEIGHT: Int = 200

    const val OVERWORLD_CLOUD_HEIGHT: Int = 130

    @JvmField
    val LOX_GAS_RATIO: Float = 5f / 54

    const val CONFIG_FILE: String = "Galacticraft/core.conf"

    const val POWER_CONFIG_FILE: String = "Galacticraft/power-GC3.conf"

    const val CHUNKLOADER_CONFIG_FILE: String = "Galacticraft/chunkloading.conf"

    const val ASSET_PREFIX: String = "galacticraftcore"

    @JvmField
    val TEXTURE_PREFIX: String = ASSET_PREFIX + ":"

    const val PREFIX: String = "micdoodle8."

    const val GCDATAFOLDER: String = "../galacticraft/"

    const val GEAR_ID_OXYGEN_MASK: Int = 0

    const val GEAR_ID_OXYGEN_GEAR: Int = 1

    const val GEAR_ID_OXYGEN_TANK_LIGHT: Int = 2

    const val GEAR_ID_OXYGEN_TANK_MEDIUM: Int = 3

    const val GEAR_ID_OXYGEN_TANK_HEAVY: Int = 4

    const val GEAR_ID_OXYGEN_TANK_INFINITE: Int = 5

    const val GEAR_ID_THERMAL_PADDING_T1_HELMET: Int = 6

    const val GEAR_ID_THERMAL_PADDING_T1_CHESTPLATE: Int = 7

    const val GEAR_ID_THERMAL_PADDING_T1_LEGGINGS: Int = 8

    const val GEAR_ID_THERMAL_PADDING_T1_BOOTS: Int = 9

    const val GEAR_ID_THERMAL_PADDING_T2_HELMET: Int = 10

    const val GEAR_ID_THERMAL_PADDING_T2_CHESTPLATE: Int = 11

    const val GEAR_ID_THERMAL_PADDING_T2_LEGGINGS: Int = 12

    const val GEAR_ID_THERMAL_PADDING_T2_BOOTS: Int = 13

    const val GEAR_ID_PARACHUTE: Int = 14

    const val GEAR_ID_FREQUENCY_MODULE: Int = 15

    const val GEAR_ID_SHIELD_CONTROLLER: Int = 16

    const val RENDERDISTANCE_SHORT: Double =
        16384.0 //128 squared (8 chunks range), used for small + high frequency TESR tiles like Fluid Pipes

    const val RENDERDISTANCE_MEDIUM: Double =
        65536.0 //256 squared (16 chunks range), used for standard block-sized TESR tiles like Fluid Tanks or Treasure Chests, also heavy render burden large tiles like the Dish and Display Screen

    const val RENDERDISTANCE_LONG: Double =
        262144.0 //512 squared (32 chunks max range), used for uncommon, large or extra bright TESR tiles like Oxygen Bubble Distributors, Solar Panels or Panel Lighting

    @JvmField
    val RADIANS_TO_DEGREES: Float = 180f / 3.1415927f

    @JvmField
    val RADIANS_TO_DEGREES_D: Double = 180.0 / Math.PI

    @JvmField
    val twoPI: Float = Math.PI.toFloat() * 2f

    @JvmField
    val halfPI: Float = Math.PI.toFloat() / 2f

    const val PERMISSION_CREATE_STATION: String = "galacticraft.station.create"
}
