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

open class ItemHeavyDutyPlates(properties: Item.Properties) : FoundationVariantMaterial(properties), ISortableItem {
    constructor() : this(FoundationItemProperties.named("galaxyspace", "hdp")
        .component(DataComponents.ITEM_MODEL, Identifier.parse("galaxyspace:hdp"))
        .overrideDescription("item.hdp.name"))

    override fun variant(metadata: Int): FoundationItemVariant? {
        val name = names.getOrNull(metadata) ?: return null
        return FoundationItemVariant("item.hdp.name", Identifier.parse("galaxyspace:item/hdp/$name"))
    }

    override fun creativeMetadata(): List<Int> = names.indices.toList()
    override fun getCategory(meta: Int): EnumSortCategoryItem = EnumSortCategoryItem.PLATE

    override fun appendHoverText(stack: ItemStack, context: TooltipContext, display: TooltipDisplay, builder: Consumer<Component>, flag: TooltipFlag) {
        val meta = FoundationItemVariants.metadata(stack)
        if (meta in names.indices) builder.accept(Component.translatable("gui.tier${meta + 4}.desc"))
    }

    companion object {
        @JvmField
        var names: Array<String> = arrayOf(
            "hdp4",
            "hdp5",
            "hdp6"
        )
    }
}
