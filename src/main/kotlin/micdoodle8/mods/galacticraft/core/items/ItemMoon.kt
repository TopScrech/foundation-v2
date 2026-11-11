package micdoodle8.mods.galacticraft.core.items

import foundation.item.FoundationItemProperties
import foundation.item.FoundationItemVariant
import foundation.item.FoundationItemVariants
import foundation.item.FoundationVariantItem
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity

open class ItemMoon(properties: Item.Properties) : ItemDesc(properties), ISortableItem, FoundationVariantItem {
    constructor(name: String?) : this(
        FoundationItemProperties.named(Constants.MOD_ID_CORE, requireNotNull(name))
            .rarity(Rarity.RARE)
            .component(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(Constants.MOD_ID_CORE, "item_basic_moon"))
            .component(DataComponents.ITEM_NAME, Component.translatable("item.meteoric_iron_ingot.name").withStyle(ChatFormatting.BLUE))
    )

    override fun variant(metadata: Int): FoundationItemVariant? {
        val name = names.getOrNull(metadata) ?: return null
        val model = if (metadata == 0) "item_basic_moon" else name
        return FoundationItemVariant("item.$name.name", Identifier.fromNamespaceAndPath(Constants.MOD_ID_CORE, "item/$model"), ChatFormatting.BLUE)
    }

    override fun getName(stack: ItemStack): Component {
        if (stack.get(DataComponents.ITEM_NAME) != components().get(DataComponents.ITEM_NAME)) return super.getName(stack)
        return variant(FoundationItemVariants.metadata(stack))?.name() ?: super.getName(stack)
    }

    override fun creativeMetadata(): List<Int> = names.indices.toList()

    fun creativeStacks(): List<ItemStack> = FoundationItemVariants.creativeStacks(this)

    override fun getCategory(meta: Int): EnumSortCategoryItem = when (meta) {
        0 -> EnumSortCategoryItem.INGOT
        2 -> EnumSortCategoryItem.GENERAL
        else -> EnumSortCategoryItem.PLATE
    }

    override fun getShiftDescription(meta: Int): String =
        if (meta == 2) Component.translatable("item.lunar_sapphire.description").string else ""

    override fun showDescription(meta: Int): Boolean = meta == 2

    // Retained for the smelting recipe migration, where experience is now stored on the recipe
    open fun getSmeltingExperience(stack: ItemStack): Float =
        if (FoundationItemVariants.metadata(stack) == 1) 1f else -1f

    companion object {
        @JvmField
        var names: Array<String> = arrayOf("meteoric_iron_ingot", "compressed_meteoric_iron", "lunar_sapphire")
    }
}
