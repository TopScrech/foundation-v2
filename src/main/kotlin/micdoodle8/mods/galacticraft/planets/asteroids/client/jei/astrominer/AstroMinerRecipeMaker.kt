package micdoodle8.mods.galacticraft.planets.asteroids.client.jei.astrominer

import java.util.ArrayList

import micdoodle8.mods.galacticraft.api.GalacticraftRegistry
import micdoodle8.mods.galacticraft.api.recipe.INasaWorkbenchRecipe

object AstroMinerRecipeMaker {
    @JvmStatic
    fun getRecipesList(): MutableList<AstroMinerRecipeWrapper?> {
        val recipes: MutableList<AstroMinerRecipeWrapper?> = ArrayList<AstroMinerRecipeWrapper?>()

        for (recipe in GalacticraftRegistry.getAstroMinerRecipes()) {
            val wrapper: AstroMinerRecipeWrapper = AstroMinerRecipeWrapper((recipe)!!)
            recipes.add(wrapper)
        }

        return recipes
    }
}
