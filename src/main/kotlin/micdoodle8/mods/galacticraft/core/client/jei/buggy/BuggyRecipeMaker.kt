package micdoodle8.mods.galacticraft.core.client.jei.buggy

import java.util.ArrayList

import micdoodle8.mods.galacticraft.api.GalacticraftRegistry
import micdoodle8.mods.galacticraft.api.recipe.INasaWorkbenchRecipe
import micdoodle8.mods.galacticraft.core.GCItems
import net.minecraft.world.item.ItemStack

object BuggyRecipeMaker {
    @JvmStatic
    fun getRecipesList(): MutableList<INasaWorkbenchRecipe?> {
        val recipes: MutableList<INasaWorkbenchRecipe?> = ArrayList<INasaWorkbenchRecipe?>()

        var chestCount: Int = -1
        for (recipe in GalacticraftRegistry.getBuggyBenchRecipes()) {
            val chests: Int = countStorage((recipe)!!)
            if (chests == chestCount) continue
            chestCount = chests
            recipes.add(recipe)
        }

        return recipes
    }

    @JvmStatic
    fun countStorage(recipe: INasaWorkbenchRecipe): Int {
        var count: Int = 0
        val storage: ItemStack = ItemStack(GCItems.partBuggy, 1, 2)
        for (e: MutableMap.MutableEntry<Int, ItemStack?> in (recipe.getRecipeInput())!!.entries) {
            if (ItemStack.areItemsEqual(storage, e.value)) count++
        }
        return count
    }
}
