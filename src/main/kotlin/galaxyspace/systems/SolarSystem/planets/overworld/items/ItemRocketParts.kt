package galaxyspace.systems.SolarSystem.planets.overworld.items

import foundation.item.FoundationItemProperties
import foundation.item.FoundationItemVariant
import foundation.item.FoundationItemVariants
import foundation.item.FoundationVariantMaterial
import micdoodle8.mods.galacticraft.core.items.ISortableItem
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.TooltipDisplay
import java.util.function.Consumer

open class ItemRocketParts(properties: Item.Properties) : FoundationVariantMaterial(properties), ISortableItem {
    constructor() : this(FoundationItemProperties.named("galaxyspace", "rocket_parts")
        .component(DataComponents.ITEM_MODEL, Identifier.parse("galaxyspace:rocket_parts"))
        .overrideDescription("item.rocket_cone.name"))

    override fun variant(metadata: Int): FoundationItemVariant? {
        val model = names.getOrNull(metadata) ?: return null
        val part = arrayOf("rocket_cone", "rocket_body", "rocket_engine", "rocket_booster", "rocket_stabiliser")[metadata % 5]
        return FoundationItemVariant("item.$part.name", Identifier.parse("galaxyspace:item/rocket_parts/$model"))
    }

    override fun creativeMetadata(): List<Int> = names.indices.toList()

    override fun appendHoverText(stack: ItemStack, context: TooltipContext, display: TooltipDisplay, builder: Consumer<Component>, flag: TooltipFlag) {
        val meta = FoundationItemVariants.metadata(stack)
        if (meta in names.indices) builder.accept(Component.translatable("gui.tier${meta / 5 + 3}.desc"))
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem = EnumSortCategoryItem.GENERAL

    companion object {
        @JvmField
        var names: Array<String> = arrayOf(
            "rocket_cone_tier_3",
            "rocket_body_tier_3",
            "rocket_engine_tier_3",
            "rocket_booster_tier_3",
            "rocket_stabiliser_tier_3",
            "rocket_cone_tier_4",
            "rocket_body_tier_4",
            "rocket_engine_tier_4",
            "rocket_booster_tier_4",
            "rocket_stabiliser_tier_4",
            "rocket_cone_tier_5",
            "rocket_body_tier_5",
            "rocket_engine_tier_5",
            "rocket_booster_tier_5",
            "rocket_stabiliser_tier_5",
            "rocket_cone_tier_6",
            "rocket_body_tier_6",
            "rocket_engine_tier_6",
            "rocket_booster_tier_6",
            "rocket_stabiliser_tier_6"
        )
    }
}
