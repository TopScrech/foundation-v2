package micdoodle8.mods.galacticraft.planets.mars.client.jei.cargorocket

import java.util.ArrayList

import micdoodle8.mods.galacticraft.api.GalacticraftRegistry
import micdoodle8.mods.galacticraft.api.recipe.INasaWorkbenchRecipe
import micdoodle8.mods.galacticraft.core.client.jei.tier1rocket.Tier1RocketRecipeMaker

object CargoRocketRecipeMaker {
    @JvmStatic
    fun getRecipesList(): MutableList<INasaWorkbenchRecipe?> {
        val recipes: MutableList<INasaWorkbenchRecipe?> = ArrayList<INasaWorkbenchRecipe?>()

        var chestCount: Int = -1
        for (recipe in GalacticraftRegistry.getCargoRocketRecipes()) {
            val chests: Int = Tier1RocketRecipeMaker.countChests((recipe)!!)
            if (chests == chestCount) continue
            chestCount = chests
            recipes.add(recipe)
        }

        return recipes
    }
}
