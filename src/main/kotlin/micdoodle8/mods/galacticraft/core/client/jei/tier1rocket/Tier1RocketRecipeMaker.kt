package micdoodle8.mods.galacticraft.core.client.jei.tier1rocket

import java.util.ArrayList

import micdoodle8.mods.galacticraft.api.GalacticraftRegistry
import micdoodle8.mods.galacticraft.api.recipe.INasaWorkbenchRecipe
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.item.ItemStack

object Tier1RocketRecipeMaker {
    @JvmStatic
    fun getRecipesList(): MutableList<INasaWorkbenchRecipe?> {
        val recipes: MutableList<INasaWorkbenchRecipe?> = ArrayList<INasaWorkbenchRecipe?>()

        var chestCount: Int = -1
        for (recipe in GalacticraftRegistry.getRocketT1Recipes()) {
            val chests: Int = countChests((recipe)!!)
            if (chests == chestCount) continue
            chestCount = chests
            recipes.add(recipe)
        }

        return recipes
    }

    @JvmStatic
    fun countChests(recipe: INasaWorkbenchRecipe): Int {
        var count: Int = 0
        val chest: ItemStack = ItemStack(Blocks.CHEST)
        for (e: MutableMap.MutableEntry<Int, ItemStack?> in (recipe.getRecipeInput())!!.entries) {
            if (ItemStack.areItemsEqual(chest, e.value)) count++
        }
        return count
    }
}
