package micdoodle8.mods.galacticraft.planets.asteroids.items

import foundation.item.FoundationItemProperties
import foundation.item.FoundationTitaniumTools
import micdoodle8.mods.galacticraft.core.items.ISortableItem
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.Rarity

open class ItemHoeAsteroids(properties: Item.Properties) : Item(properties), ISortableItem {
    constructor(assetName: String?) : this(
        foundation.item.FoundationSteelTools.hoe(FoundationItemProperties.named("galacticraftplanets", requireNotNull(assetName)), FoundationTitaniumTools.BASE_MATERIAL)
            .rarity(Rarity.RARE).overrideDescription("item.$assetName.name")
    )

    override fun getCategory(meta: Int): EnumSortCategoryItem = EnumSortCategoryItem.TOOLS
}
