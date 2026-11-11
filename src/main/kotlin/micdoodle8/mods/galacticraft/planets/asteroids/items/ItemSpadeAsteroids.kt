package micdoodle8.mods.galacticraft.planets.asteroids.items

import foundation.item.FoundationItemProperties
import foundation.item.FoundationTitaniumTools
import micdoodle8.mods.galacticraft.core.items.ISortableItem
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.Rarity

open class ItemSpadeAsteroids(properties: Item.Properties) : Item(properties), ISortableItem {
    constructor(assetName: String?) : this(
        FoundationItemProperties.named("galacticraftplanets", requireNotNull(assetName)).shovel(FoundationTitaniumTools.HARVEST_MATERIAL, 1.5f, -3f)
            .rarity(Rarity.RARE).overrideDescription("item.$assetName.name")
    )

    override fun getCategory(meta: Int): EnumSortCategoryItem = EnumSortCategoryItem.TOOLS
}
