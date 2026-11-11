package micdoodle8.mods.galacticraft.planets.asteroids.items

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

open class ItemBasicAsteroids(properties: Item.Properties) : FoundationVariantMaterial(properties), ISortableItem {
    constructor(name: String?) : this(FoundationItemProperties.named(Constants.MOD_ID_PLANETS, requireNotNull(name))
        .rarity(Rarity.RARE)
        .component(DataComponents.ITEM_MODEL, Identifier.parse("galacticraftplanets:item_basic_asteroids"))
        .component(DataComponents.ITEM_NAME, Component.translatable("item.ingot_titanium.name").withStyle(ChatFormatting.BLUE)))

    override fun variant(metadata: Int): FoundationItemVariant? {
        val name = names.getOrNull(metadata) ?: return null
        val model = if (metadata == 5) "item_basic_asteroids" else name
        return FoundationItemVariant("item.$name.name", Identifier.parse("galacticraftplanets:item/$model"), ChatFormatting.BLUE)
    }

    override fun creativeMetadata(): List<Int> = names.indices.toList()

    override fun appendHoverText(stack: ItemStack, context: TooltipContext, display: TooltipDisplay, builder: Consumer<Component>, flag: TooltipFlag) {
        if (FoundationItemVariants.metadata(stack) == 5) builder.accept(Component.translatable("item.tier3.desc"))
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem = when (meta) {
        0 -> EnumSortCategoryItem.INGOT
        5, 6 -> EnumSortCategoryItem.PLATE
        else -> EnumSortCategoryItem.GENERAL
    }

    // Native smelting recipes own experience; retain the declaration until those recipes are migrated
    open fun getSmeltingExperience(stack: ItemStack): Float = when (FoundationItemVariants.metadata(stack)) {
        5 -> 2f
        6 -> 1f
        else -> -1f
    }

    companion object {
        @JvmField
        var names: Array<String> = arrayOf(
            "ingot_titanium",
            "engine_t2",
            "rocket_fins_t2",
            "shard_iron",
            "shard_titanium",
            "reinforced_plate_t3",
            "compressed_titanium",
            "thermal_cloth",
            "beam_core",
            "dust_titanium"
        )
    }
}
