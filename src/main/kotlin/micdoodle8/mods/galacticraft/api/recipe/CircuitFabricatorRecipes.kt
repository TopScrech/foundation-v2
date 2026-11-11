package micdoodle8.mods.galacticraft.api.recipe

import foundation.item.FoundationRecipeStacks
import micdoodle8.mods.galacticraft.core.recipe.NasaWorkbenchRecipe
import net.minecraft.core.NonNullList
import net.minecraft.world.item.ItemStack
import java.util.ArrayList

object CircuitFabricatorRecipes {
    private data class Recipe(val inputs: NonNullList<Any>, val output: ItemStack)
    private val recipes = ArrayList<Recipe>()

    @JvmField
    var slotValidItems: ArrayList<ArrayList<ItemStack?>> = ArrayList(5)

    /** Each of the five slots accepts one stack, a nonempty stack list, or null for an empty slot */
    @JvmStatic
    fun addRecipe(output: ItemStack?, inputList: MutableList<Any?>) {
        require(output != null && !output.isEmpty) { "Circuit fabricator recipe requires an output" }
        require(inputList.size == 5) { "Circuit fabricator recipe requires five inputs" }
        val inputs = snapshotInputs(inputList)
        recipes.add(Recipe(inputs, output.copy()))
        rebuildValidItems()
    }

    private fun snapshotIngredient(value: Any?): Any = when (value) {
        null -> ItemStack.EMPTY
        is ItemStack -> value.copy()
        is List<*> -> {
            require(value.isNotEmpty()) { "Circuit ingredient alternatives cannot be empty" }
            value.mapTo(ArrayList<ItemStack>()) {
                require(it is ItemStack && !it.isEmpty) { "Circuit ingredient alternatives must be nonempty item stacks" }
                it.copy()
            }
        }
        else -> error("Invalid circuit fabricator ingredient $value")
    }

    private fun snapshotInputs(values: List<Any?>): NonNullList<Any> = NonNullList.create<Any>().also { result ->
        values.forEach { result.add(snapshotIngredient(it)) }
    }

    private fun alternatives(ingredient: Any?): List<ItemStack> = when (ingredient) {
        is ItemStack -> listOf(ingredient)
        is List<*> -> ingredient.map { it as ItemStack }
        else -> error("Invalid stored circuit ingredient")
    }

    private fun rebuildValidItems() {
        slotValidItems.clear()
        repeat(5) { slotValidItems.add(ArrayList()) }
        recipes.forEach { recipe ->
            recipe.inputs.forEachIndexed { slot, ingredient ->
                alternatives(ingredient).forEach { stack ->
                    if (!stack.isEmpty && slotValidItems[slot].none {
                        it != null && NasaWorkbenchRecipe.matchesIngredient(it, stack)
                    }) slotValidItems[slot].add(stack.copy())
                }
            }
        }
    }

    @JvmStatic
    fun getOutputForInput(inputList: MutableList<ItemStack>): ItemStack {
        if (inputList.size != 5) return ItemStack.EMPTY
        return recipes.firstOrNull { recipe ->
            recipe.inputs.indices.all { slot ->
                alternatives(recipe.inputs[slot]).any { FoundationRecipeStacks.matches(it, inputList[slot]) }
            }
        }?.output?.copy() ?: ItemStack.EMPTY
    }

    @JvmStatic
    fun getRecipes(): MutableList<MutableList<Any?>> = recipes.mapTo(ArrayList()) { ArrayList<Any?>(snapshotInputs(it.inputs)) }

    @JvmStatic
    fun getOutput(count: Int): ItemStack = recipes[count].output.copy()

    @JvmStatic
    fun removeRecipe(match: ItemStack) {
        recipes.removeAll { ItemStack.matches(match, it.output) }
        rebuildValidItems()
    }

    @JvmStatic
    fun replaceRecipeIngredient(ingredient: ItemStack?, replacement: MutableList<ItemStack?>) {
        if (ingredient == null) return
        val normalized = snapshotIngredient(replacement)
        recipes.forEach { recipe ->
            recipe.inputs.indices.forEach { slot ->
                if (alternatives(recipe.inputs[slot]).any { ItemStack.isSameItemSameComponents(it, ingredient) }) {
                    recipe.inputs[slot] = snapshotIngredient(normalized)
                }
            }
        }
        rebuildValidItems()
    }
}
