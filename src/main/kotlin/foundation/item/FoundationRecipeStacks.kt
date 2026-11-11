package foundation.item

import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack

object FoundationRecipeStacks {
    /** Retain the recipe-only exception for a lone legacy NoPlacing tag */
    fun matches(first: ItemStack, second: ItemStack): Boolean {
        if (ItemStack.isSameItemSameComponents(first, second)) return true
        if (first.isEmpty || second.isEmpty || !ItemStack.isSameItem(first, second)) return false
        val firstData = first.get(DataComponents.CUSTOM_DATA)
        val secondData = second.get(DataComponents.CUSTOM_DATA)
        val tagged = when {
            firstData == null && secondData != null -> second
            secondData == null && firstData != null -> first
            else -> return false
        }
        val data = requireNotNull(tagged.get(DataComponents.CUSTOM_DATA)).copyTag()
        if (data.size() != 1 || !data.contains("NoPlacing")) return false
        val normalized = tagged.copy().also { it.remove(DataComponents.CUSTOM_DATA) }
        return if (tagged === first) ItemStack.isSameItemSameComponents(normalized, second)
        else ItemStack.isSameItemSameComponents(first, normalized)
    }
}
