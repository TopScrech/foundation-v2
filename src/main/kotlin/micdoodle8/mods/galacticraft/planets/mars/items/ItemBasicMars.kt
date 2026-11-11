package micdoodle8.mods.galacticraft.planets.mars.items

import foundation.item.FoundationItemProperties
import foundation.item.FoundationItemVariant
import foundation.item.FoundationItemVariants
import foundation.item.FoundationVariantMaterial
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.items.ISortableItem
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.TooltipDisplay
import java.util.function.Consumer

open class ItemBasicMars(properties: Item.Properties) : FoundationVariantMaterial(properties), ISortableItem {
    constructor(name: String?) : this(FoundationItemProperties.named(Constants.MOD_ID_PLANETS, requireNotNull(name))
        .rarity(Rarity.RARE)
        .component(DataComponents.ITEM_MODEL, Identifier.parse("galacticraftplanets:item_basic_mars"))
        .component(DataComponents.ITEM_NAME, Component.translatable("item.raw_desh.name").withStyle(ChatFormatting.BLUE)))

    override fun variant(metadata: Int): FoundationItemVariant? {
        val name = names.getOrNull(metadata) ?: return null
        val model = name
        return FoundationItemVariant("item.$name.name", Identifier.parse("galacticraftplanets:item/$model"), ChatFormatting.BLUE)
    }

    override fun creativeMetadata(): List<Int> = names.indices.toList()

    override fun appendHoverText(stack: ItemStack, context: TooltipContext, display: TooltipDisplay, builder: Consumer<Component>, flag: TooltipFlag) {
        if (FoundationItemVariants.metadata(stack) == 3) builder.accept(Component.translatable("item.tier2.desc"))
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem = when (meta) {
        2 -> EnumSortCategoryItem.INGOT
        3, 5 -> EnumSortCategoryItem.PLATE
        else -> EnumSortCategoryItem.GENERAL
    }

    // Native smelting recipes own experience; retain the declaration until those recipes are migrated
    open fun getSmeltingExperience(stack: ItemStack): Float = when (FoundationItemVariants.metadata(stack)) {
        3 -> 1.5f
        5 -> 1f
        else -> -1f
    }

    companion object {
        @JvmField
        var names: Array<String> = arrayOf(
            "raw_desh",
            "desh_stick",
            "ingot_desh",
            "reinforced_plate_t2",
            "slimeling_cargo",
            "compressed_desh",
            "fluid_manip"
        )
    }
}
