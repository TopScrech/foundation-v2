package galaxyspace.core.client.jei.nasaworkbench.tier2rocket

import java.util.ArrayList

import galaxyspace.core.util.GSRecipeUtil
import micdoodle8.mods.galacticraft.api.recipe.INasaWorkbenchRecipe
import micdoodle8.mods.galacticraft.core.client.jei.tier1rocket.Tier1RocketRecipeMaker

object RocketTier2RecipeMaker {
    @JvmStatic
    fun getRecipesList(): MutableList<INasaWorkbenchRecipe?> {
        val recipes: MutableList<INasaWorkbenchRecipe?> = ArrayList<INasaWorkbenchRecipe?>()

        var chestCount: Int = -1
        for (recipe in GSRecipeUtil.getRocketT2Recipes()) {
            val chests: Int = Tier1RocketRecipeMaker.countChests((recipe)!!)
            if (chests == chestCount) continue
            chestCount = chests
            recipes.add(recipe)
        }

        return recipes
    }
}
