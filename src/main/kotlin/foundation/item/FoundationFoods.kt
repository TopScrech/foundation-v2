package foundation.item

import asmodeuscore.api.item.IItemSpaceFood
import micdoodle8.mods.galacticraft.core.items.ItemCheese
import micdoodle8.mods.galacticraft.core.items.ItemFood
import net.minecraft.core.component.DataComponents
import net.minecraft.tags.ItemTags
import net.minecraft.world.food.FoodData
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.ItemStack

/** Shared food queries for ordinary eating, environmental checks, modules, and pets */
object FoundationFoods {
    fun properties(stack: ItemStack): FoodProperties? {
        if (stack.isEmpty) return null
        (stack.item as? FoundationVariantItem)?.configureVariant(stack, FoundationItemVariants.metadata(stack))
        return stack.get(DataComponents.FOOD)
    }

    fun requiresBreathableAir(stack: ItemStack): Boolean =
        properties(stack) != null && stack.item !is ItemFood && stack.item !is IItemSpaceFood

    fun wolfHealingNutrition(stack: ItemStack): Int? {
        val food = properties(stack) ?: return null
        val meat = (stack.item as? ItemCheese)?.wolfFavorite == true || stack.`is`(ItemTags.WOLF_FOOD)
        return if (meat) food.nutrition() else null
    }

    /** Autofeeding historically applied nutrition directly, without normal use effects or container drops */
    fun consumeFirst(stacks: List<ItemStack>, hunger: FoodData): Boolean {
        if (!hunger.needsFood()) return false
        for (stack in stacks) {
            val food = properties(stack) ?: continue
            hunger.eat(food)
            stack.shrink(1)
            return true
        }
        return false
    }
}
