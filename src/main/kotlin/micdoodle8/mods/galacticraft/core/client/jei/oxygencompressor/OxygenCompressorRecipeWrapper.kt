package micdoodle8.mods.galacticraft.core.client.jei.oxygencompressor

import javax.annotation.Nonnull

import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.recipe.BlankRecipeWrapper
import mezz.jei.api.recipe.wrapper.ICraftingRecipeWrapper
import net.minecraft.item.ItemStack

open class OxygenCompressorRecipeWrapper constructor(@field:Nonnull @param:Nonnull private val output: ItemStack) :
    BlankRecipeWrapper(), ICraftingRecipeWrapper {
    override fun getIngredients(ingredients: IIngredients) {
        ingredients.setOutput<ItemStack?>(ItemStack::class.java, this.output)
    }
}
