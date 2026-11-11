package galaxyspace.core.util

import net.minecraft.resources.Identifier as ResourceLocation

object GSConstants {
    const val GEAR_ID_OXYGEN_TANK_4: Int = 40

    const val GEAR_ID_OXYGEN_TANK_5: Int = 41

    const val GEAR_ID_OXYGEN_TANK_6: Int = 42

    const val GEAR_ID_OXYGEN_TANK_EPP: Int = 43

    const val GEAR_ID_TEMP_SHIELD_CONTROLLER: Int = 44

    const val GEAR_ID_THERMAL_PADDING_T3_HELMET: Int = 45

    const val GEAR_ID_THERMAL_PADDING_T3_CHESTPLATE: Int = 46

    const val GEAR_ID_THERMAL_PADDING_T3_LEGGINGS: Int = 47

    const val GEAR_ID_THERMAL_PADDING_T3_BOOTS: Int = 48

    const val GEAR_ID_THERMAL_PADDING_T4_HELMET: Int = 49

    const val GEAR_ID_THERMAL_PADDING_T4_CHESTPLATE: Int = 50

    const val GEAR_ID_THERMAL_PADDING_T4_LEGGINGS: Int = 51

    const val GEAR_ID_THERMAL_PADDING_T4_BOOTS: Int = 52

    const val CATEGORY_HARDMODE: String = "hardmode"

    const val CATEGORY_DEVELOMPENT: String = "development"

    const val CATEGORY_ITEMS: String = "items"

    @JvmField
    val GUI_MACHINE_CLASSIC: ResourceLocation =
        ResourceLocation.fromNamespaceAndPath("galaxyspace", "textures/gui/classic_gui.png")

    @JvmField
    val GUI_MACHINE_MODERN: ResourceLocation = ResourceLocation.fromNamespaceAndPath("galaxyspace", "textures/gui/modern_gui.png")
}
