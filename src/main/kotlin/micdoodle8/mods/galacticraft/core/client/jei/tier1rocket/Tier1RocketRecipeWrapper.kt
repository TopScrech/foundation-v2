package micdoodle8.mods.galacticraft.core.client.jei.tier1rocket

import javax.annotation.Nonnull

import com.google.common.collect.Lists
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.recipe.IRecipeWrapper
import micdoodle8.mods.galacticraft.api.recipe.INasaWorkbenchRecipe
import net.minecraft.world.item.ItemStack

open class Tier1RocketRecipeWrapper constructor(@field:Nonnull @param:Nonnull private val recipe: INasaWorkbenchRecipe) :
    IRecipeWrapper {
    override fun getIngredients(ingredients: IIngredients) {
        ingredients.setInputs<ItemStack?>(
            ItemStack::class.java,
            Lists.newArrayList<ItemStack?>((recipe.getRecipeInput())!!.values)
        )
        ingredients.setOutput<ItemStack?>(ItemStack::class.java, this.recipe.getRecipeOutput())
    }
}
