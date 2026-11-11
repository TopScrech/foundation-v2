package micdoodle8.mods.galacticraft.core.items

import foundation.item.FoundationItemProperties
import foundation.item.FoundationItemVariant
import foundation.item.FoundationVariantMaterial
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponents
import net.minecraft.resources.Identifier
import net.minecraft.world.item.Item
import net.minecraft.world.item.Rarity

open class ItemCanister(properties: Item.Properties) : FoundationVariantMaterial(properties), ISortableItem {
    constructor(assetName: String?) : this(
        FoundationItemProperties.named("galacticraftcore", requireNotNull(assetName)).rarity(Rarity.RARE)
            .component(DataComponents.ITEM_MODEL, Identifier.parse("galacticraftcore:canister"))
    )

    override fun getCategory(meta: Int): EnumSortCategoryItem = EnumSortCategoryItem.CANISTER
    override fun creativeMetadata(): List<Int> = names.indices.toList()
    override fun variant(metadata: Int): FoundationItemVariant? {
        val name = names.getOrNull(metadata) ?: return null
        val model = if (metadata == 0) "canister" else "canister_$name"
        return FoundationItemVariant("item.canister.$name.name", Identifier.parse("galacticraftcore:item/$model"), ChatFormatting.BLUE)
    }

    companion object {
        @JvmField val names: Array<String> = arrayOf("tin", "copper")
    }
}
