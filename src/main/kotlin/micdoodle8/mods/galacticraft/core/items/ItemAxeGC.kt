package micdoodle8.mods.galacticraft.core.items

import foundation.item.FoundationItemProperties
import foundation.item.FoundationSteelTools
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.Rarity

open class ItemAxeGC(properties: Item.Properties) : Item(properties), ISortableItem {
    constructor(assetName: String?) : this(
        FoundationSteelTools.axe(FoundationItemProperties.named("galacticraftcore", requireNotNull(assetName)))
            .rarity(Rarity.RARE).overrideDescription("item.$assetName.name")
    )

    override fun getCategory(meta: Int): EnumSortCategoryItem = EnumSortCategoryItem.TOOLS
}
