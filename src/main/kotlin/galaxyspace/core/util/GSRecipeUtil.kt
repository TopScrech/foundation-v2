package galaxyspace.core.util

import javax.annotation.Nonnull

import galaxyspace.core.util.GSRecipeUtil
import micdoodle8.mods.galacticraft.api.recipe.INasaWorkbenchRecipe
import java.util.ArrayList

object GSRecipeUtil {
    private val rocketBenchConeRecipes: MutableList<INasaWorkbenchRecipe?> = ArrayList<INasaWorkbenchRecipe?>()
    private val rocketBenchBodyRecipes: MutableList<INasaWorkbenchRecipe?> = ArrayList<INasaWorkbenchRecipe?>()
    private val rocketBenchEngineRecipes: MutableList<INasaWorkbenchRecipe?> = ArrayList<INasaWorkbenchRecipe?>()
    private val rocketBenchBoosterRecipes: MutableList<INasaWorkbenchRecipe?> = ArrayList<INasaWorkbenchRecipe?>()
    private val rocketBenchFinsRecipes: MutableList<INasaWorkbenchRecipe?> = ArrayList<INasaWorkbenchRecipe?>()
    private val rocketBenchOxTankRecipes: MutableList<INasaWorkbenchRecipe?> = ArrayList<INasaWorkbenchRecipe?>()

    private val rocketBenchT2Recipes: MutableList<INasaWorkbenchRecipe?> = ArrayList<INasaWorkbenchRecipe?>()

    /*
	public static ItemStack findMatchingConeRecipe(InventorySchematicCone craftMatrix)
    {
        for (INasaWorkbenchRecipe recipe : GSRecipeUtil.getConeRecipes())
        {
            if (recipe.matches(craftMatrix))
            {
                return recipe.getRecipeOutput();
            }
        }

        return ItemStack.EMPTY;
    }
	
	public static ItemStack findMatchingBodyRecipe(InventorySchematicBody craftMatrix)
    {
        for (INasaWorkbenchRecipe recipe : GSRecipeUtil.getBodyRecipes())
        {
            if (recipe.matches(craftMatrix))
            {
                return recipe.getRecipeOutput();
            }
        }

        return ItemStack.EMPTY;
    }
	
	public static ItemStack findMatchingEngineRecipe(InventorySchematicEngine craftMatrix)
    {
        for (INasaWorkbenchRecipe recipe : GSRecipeUtil.getEngineRecipes())
        {
            if (recipe.matches(craftMatrix))
            {
                return recipe.getRecipeOutput();
            }
        }

        return ItemStack.EMPTY;
    }
	
	public static ItemStack findMatchingBoosterRecipe(InventorySchematicBooster craftMatrix)
    {
        for (INasaWorkbenchRecipe recipe : GSRecipeUtil.getBoosterRecipes())
        {
            if (recipe.matches(craftMatrix))
            {
                return recipe.getRecipeOutput();
            }
        }

        return ItemStack.EMPTY;
    }
	
	public static ItemStack findMatchingFinsRecipe(InventorySchematicFins craftMatrix)
    {
        for (INasaWorkbenchRecipe recipe : GSRecipeUtil.getFinsRecipes())
        {
            if (recipe.matches(craftMatrix))
            {
                return recipe.getRecipeOutput();
            }
        }

        return ItemStack.EMPTY;
    }

	public static ItemStack findMatchingOxTankRecipe(InventorySchematicOxTank craftMatrix)
    {
        for (INasaWorkbenchRecipe recipe : GSRecipeUtil.getOxTankRecipes())
        {
            if (recipe.matches(craftMatrix))
            {
                return recipe.getRecipeOutput();
            }
        }

        return ItemStack.EMPTY;
    }
	
	@Nonnull
	public static ItemStack findMatchingSpaceshipT2Recipe(InventorySchematicTier2Rocket inventoryRocketBench)
	{
		for (INasaWorkbenchRecipe recipe : GSRecipeUtil.getRocketT2Recipes())
		{
			if (recipe.matches(inventoryRocketBench))
			{
				return recipe.getRecipeOutput();
			}
		}

		return ItemStack.EMPTY;
	}
    */
    // -----------------------------------------------------------------------------------------
    @JvmStatic
    fun addConeRecipe(recipe: INasaWorkbenchRecipe?) {
        rocketBenchConeRecipes.add(recipe)
    }

    @JvmStatic
    fun getConeRecipes(): MutableList<INasaWorkbenchRecipe?> {
        return rocketBenchConeRecipes
    }

    @JvmStatic
    fun addBodyRecipe(recipe: INasaWorkbenchRecipe?) {
        rocketBenchBodyRecipes.add(recipe)
    }

    @JvmStatic
    fun getBodyRecipes(): MutableList<INasaWorkbenchRecipe?> {
        return rocketBenchBodyRecipes
    }

    @JvmStatic
    fun addEngineRecipe(recipe: INasaWorkbenchRecipe?) {
        rocketBenchEngineRecipes.add(recipe)
    }

    @JvmStatic
    fun getEngineRecipes(): MutableList<INasaWorkbenchRecipe?> {
        return rocketBenchEngineRecipes
    }

    @JvmStatic
    fun addBoosterRecipe(recipe: INasaWorkbenchRecipe?) {
        rocketBenchBoosterRecipes.add(recipe)
    }

    @JvmStatic
    fun getBoosterRecipes(): MutableList<INasaWorkbenchRecipe?> {
        return rocketBenchBoosterRecipes
    }

    @JvmStatic
    fun addFinsRecipe(recipe: INasaWorkbenchRecipe?) {
        rocketBenchFinsRecipes.add(recipe)
    }

    @JvmStatic
    fun getFinsRecipes(): MutableList<INasaWorkbenchRecipe?> {
        return rocketBenchFinsRecipes
    }

    @JvmStatic
    fun addOxTankRecipe(recipe: INasaWorkbenchRecipe?) {
        rocketBenchOxTankRecipes.add(recipe)
    }

    @JvmStatic
    fun getOxTankRecipes(): MutableList<INasaWorkbenchRecipe?> {
        return rocketBenchOxTankRecipes
    }

    @JvmStatic
    fun addT2RocketRecipe(recipe: INasaWorkbenchRecipe?) {
        rocketBenchT2Recipes.add(recipe)
    }

    @JvmStatic
    fun getRocketT2Recipes(): MutableList<INasaWorkbenchRecipe?> {
        return rocketBenchT2Recipes
    }
}
