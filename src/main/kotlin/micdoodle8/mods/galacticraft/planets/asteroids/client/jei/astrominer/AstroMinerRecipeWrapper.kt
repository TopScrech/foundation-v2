package micdoodle8.mods.galacticraft.planets.asteroids.client.jei.astrominer

import javax.annotation.Nonnull

import com.google.common.collect.Lists
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.recipe.IRecipeWrapper
import micdoodle8.mods.galacticraft.api.recipe.INasaWorkbenchRecipe
import net.minecraft.item.ItemStack

open class AstroMinerRecipeWrapper constructor(@field:Nonnull @param:Nonnull private val recipe: INasaWorkbenchRecipe) :
    IRecipeWrapper {
    override fun getIngredients(ingredients: IIngredients) {
        ingredients.setInputs<ItemStack?>(
            ItemStack::class.java,
            Lists.newArrayList<ItemStack?>((this.recipe.getRecipeInput())!!.values)
        )
        ingredients.setOutput<ItemStack?>(ItemStack::class.java, this.recipe.getRecipeOutput())
    }
}
