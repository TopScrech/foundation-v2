package foundation.item

import micdoodle8.mods.galacticraft.core.items.ItemOxygenTank
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase
import net.minecraft.world.item.ItemStack

/** Read the shared 1.12 item value from its explicit native domain while its consumers are migrated */
val ItemStack.legacyItemValue: Int
    get() = when (item) {
        is FoundationVariantItem -> FoundationItemVariants.metadata(this)
        is ItemOxygenTank -> ItemOxygenTank.used(this)
        is ItemElectricBase -> get(FoundationItemComponents.LEGACY_METADATA) ?: (item as ItemElectricBase).legacyDisplayValue(this)
        else -> get(FoundationItemComponents.LEGACY_METADATA) ?: damageValue
    }
