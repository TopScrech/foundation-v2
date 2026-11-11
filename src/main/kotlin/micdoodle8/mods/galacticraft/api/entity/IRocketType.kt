package micdoodle8.mods.galacticraft.api.entity

import net.minecraft.network.chat.Component

interface IRocketType {
    enum class EnumRocketType
        (
        private val index: Int,
        private val tooltip: String,
        private val preFueled: Boolean,
        private val inventorySpace: Int
    ) {
        DEFAULT(0, "", false, 2),
        INVENTORY27(1, "gui.rocket_type.0", false, 20),
        INVENTORY36(2, "gui.rocket_type.1", false, 38),
        INVENTORY54(3, "gui.rocket_type.2", false, 56),
        PREFUELED(4, "gui.rocket_type.3", true, 2);

        fun getTooltip(): String {
            return if (tooltip.isEmpty()) "" else Component.translatable(tooltip).string
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
