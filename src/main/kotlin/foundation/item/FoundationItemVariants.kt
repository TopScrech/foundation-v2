package foundation.item

import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

/** An explicit definition for every retained variant of an item */
data class FoundationItemVariant(val translationKey: String, val model: Identifier, val color: ChatFormatting? = null) {
    fun name(): Component = Component.translatable(translationKey).also { component -> color?.let { component.withStyle(it) } }
}

interface FoundationVariantItem {
    fun variant(metadata: Int): FoundationItemVariant?
    fun creativeMetadata(): List<Int>
    fun configureVariant(stack: ItemStack, metadata: Int) = Unit
}

object FoundationItemVariants {
    fun metadata(stack: ItemStack): Int = stack.getOrDefault(FoundationItemComponents.LEGACY_METADATA, 0)

    fun sameVariant(first: ItemStack, second: ItemStack): Boolean {
        if (first.isEmpty || second.isEmpty) return first.isEmpty && second.isEmpty
        return ItemStack.isSameItem(first, second) && metadata(first) == metadata(second) && first.damageValue == second.damageValue &&
            first.getOrDefault(FoundationItemComponents.OXYGEN_USED, 0) == second.getOrDefault(FoundationItemComponents.OXYGEN_USED, 0) &&
            (first.item !is micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase || first.legacyItemValue == second.legacyItemValue)
    }

    fun stack(item: Item, count: Int, metadata: Int): ItemStack =
        if (count <= 0) ItemStack.EMPTY else apply(ItemStack(item, count), metadata)

    fun apply(stack: ItemStack, metadata: Int): ItemStack {
        if (stack.isEmpty) return stack
        require(metadata >= 0) { "Negative item variant $metadata" }
        val item = stack.item as? FoundationVariantItem ?: error("Item ${stack.item.descriptionId} has no declared variants")
        val definition = requireNotNull(item.variant(metadata)) { "Unknown variant $metadata for ${stack.item.descriptionId}" }
        if (metadata == 0) stack.remove(FoundationItemComponents.LEGACY_METADATA)
        else stack.set(FoundationItemComponents.LEGACY_METADATA, metadata)
        stack.set(DataComponents.ITEM_NAME, definition.name())
        item.configureVariant(stack, metadata)
        return stack
    }

    fun creativeStacks(item: Item): List<ItemStack> {
        require(item is FoundationVariantItem)
        return item.creativeMetadata().map { stack(item, 1, it) }
    }
}
