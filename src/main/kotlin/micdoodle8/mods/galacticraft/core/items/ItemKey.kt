package micdoodle8.mods.galacticraft.core.items

import foundation.item.FoundationItemProperties
import micdoodle8.mods.galacticraft.api.item.IKeyItem
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity

open class ItemKey(properties: Item.Properties, private val tier: Int = 1) : Item(properties), IKeyItem, ISortableItem {
    constructor(assetName: String?) : this(
        FoundationItemProperties.named("galacticraftcore", requireNotNull(assetName)).stacksTo(1).rarity(Rarity.RARE)
            .overrideDescription("item.$assetName.t1.name")
    )
    init { require(tier > 0) }
    override fun getTier(stack: ItemStack?): Int = tier
    override fun getCategory(meta: Int): EnumSortCategoryItem = EnumSortCategoryItem.KEYS
    fun creativeStacks(): List<ItemStack> = listOf(ItemStack(this))
}
