package galaxyspace.core.client.jei.nasaworkbench.body

import java.util.ArrayList

import galaxyspace.core.util.GSRecipeUtil
import micdoodle8.mods.galacticraft.api.recipe.INasaWorkbenchRecipe

object BodyRecipeMaker {
    @JvmStatic
    fun getRecipesList(): MutableList<INasaWorkbenchRecipe?> {
        val recipes: MutableList<INasaWorkbenchRecipe?> = ArrayList<INasaWorkbenchRecipe?>()

        val chestCount: Int = -1
        for (recipe: INasaWorkbenchRecipe? in GSRecipeUtil.getBodyRecipes()) {
            //int chests = NoseConeRecipeMaker.countChests(recipe); 
            //  if (chests == chestCount)
            // continue;
            //  chestCount = chests;
            recipes.add(recipe)
        }

        return recipes
    } /*
    public static int countChests(INasaWorkbenchRecipe recipe)
    {
        int count = 0;
        ItemStack chest = new ItemStack(Blocks.CHEST);
        for (Entry<Integer, ItemStack> e : recipe.getRecipeInput().entrySet())
        {
            if (ItemStack.areItemsEqual(chest, e.getValue()))
                count++;
        }
        return count;
    }*/
}
