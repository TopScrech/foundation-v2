package micdoodle8.mods.galacticraft.core.recipe

import foundation.item.FoundationItemVariants
import micdoodle8.mods.galacticraft.api.recipe.INasaWorkbenchRecipe
import net.minecraft.world.Container
import net.minecraft.world.item.ItemStack
import java.util.HashMap

/** Slot-addressed NASA recipes retain their layout rather than using a vanilla 3 by 3 grid */
open class NasaWorkbenchRecipe(output: ItemStack, input: HashMap<Int, ItemStack?>) : INasaWorkbenchRecipe {
    private val output = output.copy()
    private val input: HashMap<Int, ItemStack?> = HashMap<Int, ItemStack?>().also { snapshot ->
        input.forEach { (slot, ingredient) ->
            require(slot >= 0) { "Negative recipe slot $slot" }
            snapshot[slot] = requireNotNull(ingredient) { "Recipe contains null ingredient" }.copy()
        }
    }

    override fun matches(inventory: Container?): Boolean = inventory != null && input.all { (slot, ingredient) ->
        slot < inventory.containerSize && matchesIngredient(inventory.getItem(slot), requireNotNull(ingredient))
    }

    override fun getRecipeSize(): Int = input.size
    override fun getRecipeOutput(): ItemStack = output.copy()
    override fun getRecipeInput(): HashMap<Int, ItemStack?> = HashMap<Int, ItemStack?>().also { snapshot ->
        input.forEach { (slot, ingredient) -> snapshot[slot] = requireNotNull(ingredient).copy() }
    }

    companion object {
        /** Counts and unrelated custom data do not affect these legacy single-item ingredients */
        @JvmStatic
        fun matchesIngredient(target: ItemStack, ingredient: ItemStack?): Boolean {
            if (ingredient == null) return false
            return FoundationItemVariants.sameVariant(target, ingredient)
        }
    }
}
