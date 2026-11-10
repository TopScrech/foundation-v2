package micdoodle8.mods.galacticraft.api.entity

import net.minecraft.util.text.translation.I18n

interface IRocketType {
    enum class EnumRocketType
        (
        private val index: Int,
        private val tooltip: String,
        private val preFueled: Boolean,
        private val inventorySpace: Int
    ) {
        DEFAULT(0, "", false, 2),
        INVENTORY27(1, I18n.translateToLocal("gui.rocket_type.0"), false, 20),
        INVENTORY36(2, I18n.translateToLocal("gui.rocket_type.1"), false, 38),
        INVENTORY54(3, I18n.translateToLocal("gui.rocket_type.2"), false, 56),
        PREFUELED(4, I18n.translateToLocal("gui.rocket_type.3"), true, 2);

        fun getTooltip(): String {
            return this.tooltip
        }

        fun getIndex(): Int {
            return this.index
        }

        fun getInventorySpace(): Int {
            return this.inventorySpace
        }

        fun getPreFueled(): Boolean {
            return this.preFueled
        }
    }

    fun getType(): EnumRocketType?

    fun getRocketTier(): Int
}
