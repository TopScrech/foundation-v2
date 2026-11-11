package galaxyspace.systems.SolarSystem.planets.overworld.items

import foundation.item.FoundationItemProperties
import foundation.item.FoundationItemVariant
import foundation.item.FoundationItemVariants
import foundation.item.FoundationVariantMaterial
import micdoodle8.mods.galacticraft.api.item.IKeyItem
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

open class ItemTierKeysChest(properties: Item.Properties) : FoundationVariantMaterial(properties), IKeyItem, ISortableItem {
    constructor() : this(
        FoundationItemProperties.named("galaxyspace", "dungeon_keys").stacksTo(1)
            .component(DataComponents.ITEM_MODEL, Identifier.parse("galaxyspace:dungeon_keys"))
    )

    override fun variant(metadata: Int): FoundationItemVariant? {
        val model = keys.getOrNull(metadata) ?: return null
        return FoundationItemVariant("item.dungeon_keys.name", Identifier.parse("galaxyspace:item/keys/$model"))
    }
    override fun creativeMetadata(): List<Int> = keys.indices.toList()
    override fun getTier(stack: ItemStack?): Int {
        if (stack == null || stack.isEmpty || stack.item !== this) return 0
        val metadata = FoundationItemVariants.metadata(stack)
        return if (variant(metadata) != null) metadata + 4 else 0
    }
    override fun getCategory(meta: Int): EnumSortCategoryItem = EnumSortCategoryItem.KEYS
    override fun appendHoverText(stack: ItemStack, context: TooltipContext, display: TooltipDisplay, builder: Consumer<Component>, flag: TooltipFlag) {
        val tier = getTier(stack)
        if (tier > 0) builder.accept(Component.translatable("gui.tier$tier.desc"))
    }
    companion object {
        @JvmField var keys: Array<String> = arrayOf("key_tier_4", "key_tier_5")
    }
}
