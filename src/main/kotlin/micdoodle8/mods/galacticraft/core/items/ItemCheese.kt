package micdoodle8.mods.galacticraft.core.items

import foundation.item.FoundationItemProperties
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.Item
import net.minecraft.world.item.Rarity

open class ItemCheese(properties: Item.Properties, val wolfFavorite: Boolean = false) : Item(properties), ISortableItem {
    constructor(nutrition: Int, saturationModifier: Float, wolfFavorite: Boolean) : this(
        FoundationItemProperties.named("galacticraftcore", "cheese_curd").rarity(Rarity.RARE)
            .food(FoodProperties.Builder().nutrition(nutrition).saturationModifier(saturationModifier).build())
            .overrideDescription("item.cheese_curd.name"),
        wolfFavorite
    )
    constructor(nutrition: Int, wolfFavorite: Boolean) : this(nutrition, 0.6f, wolfFavorite)
    override fun getCategory(meta: Int): EnumSortCategoryItem = EnumSortCategoryItem.GENERAL
}
