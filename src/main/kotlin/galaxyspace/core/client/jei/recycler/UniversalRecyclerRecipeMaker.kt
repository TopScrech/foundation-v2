package galaxyspace.core.client.jei.recycler

import java.util.ArrayList

import galaxyspace.systems.SolarSystem.planets.overworld.recipes.RecyclerRecipes
import galaxyspace.systems.SolarSystem.planets.overworld.recipes.RecyclerRecipes.RecycleRecipe

object UniversalRecyclerRecipeMaker {
    @JvmStatic
    fun getRecipesList(): MutableList<UniversalRecyclerRecipeWrapper?> {
        val recipes: MutableList<UniversalRecyclerRecipeWrapper?> = ArrayList<UniversalRecyclerRecipeWrapper?>()

        for (entry: RecycleRecipe in RecyclerRecipes.recycling().getRecipes()) {
            recipes.add(
                UniversalRecyclerRecipeWrapper(
                    (entry.getInput())!!,
                    (entry.getOutput())!!,
                    entry.getOutput_2(),
                    entry.getFluidStack(),
                    entry.getChance(),
                    entry.getChance_2()
                )
            )
        }

        return recipes
    }
}
