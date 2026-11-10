package micdoodle8.mods.galacticraft.core.client.jei.circuitfabricator

import java.util.ArrayList

import micdoodle8.mods.galacticraft.api.recipe.CircuitFabricatorRecipes

object CircuitFabricatorRecipeMaker {
    @JvmStatic
    fun getRecipesList(): MutableList<CircuitFabricatorRecipeWrapper?> {
        val recipes: MutableList<CircuitFabricatorRecipeWrapper?> = ArrayList<CircuitFabricatorRecipeWrapper?>()

        var count: Int = 0
        for (entry: MutableList<Any?> in CircuitFabricatorRecipes.getRecipes()) {
            val wrapper: CircuitFabricatorRecipeWrapper =
                CircuitFabricatorRecipeWrapper(entry, (CircuitFabricatorRecipes.getOutput(count))!!)
            recipes.add(wrapper)
            count++
        }

        return recipes
    }
}
