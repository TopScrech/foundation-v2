package galaxyspace.systems.SolarSystem.planets.overworld.items

import foundation.item.FoundationItemProperties
import foundation.item.FoundationItemVariant
import foundation.item.FoundationVariantMaterial
import micdoodle8.mods.galacticraft.core.items.ISortableItem
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.item.Item

open class ItemIngots(properties: Item.Properties) : FoundationVariantMaterial(properties), ISortableItem {
    constructor() : this(FoundationItemProperties.named("galaxyspace", "ingots")
        .component(DataComponents.ITEM_MODEL, Identifier.parse("galaxyspace:ingots"))
        .overrideDescription("item.cobaltum_ingot.name"))

    override fun variant(metadata: Int): FoundationItemVariant? {
        val name = names.getOrNull(metadata) ?: return null
        return FoundationItemVariant("item.${name}_ingot.name", Identifier.parse("galaxyspace:item/ingots/$name"))
    }

    override fun creativeMetadata(): List<Int> = names.indices.toList()
    override fun getCategory(meta: Int): EnumSortCategoryItem = EnumSortCategoryItem.INGOT

    companion object {
        @JvmField
        var names: Array<String> = arrayOf(
            "cobaltum",
            "magnesium",
            "nickel"
        )
    }
}
