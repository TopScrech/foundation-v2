package micdoodle8.mods.galacticraft.core.client.jei.refinery

import javax.annotation.Nonnull

import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.recipe.IRecipeWrapper
import net.minecraft.world.item.ItemStack

open class RefineryRecipeWrapper constructor(
    @field:Nonnull @param:Nonnull private val input: ItemStack,
    @field:Nonnull @param:Nonnull private val output: ItemStack
) : IRecipeWrapper {
    override fun getIngredients(ingredients: IIngredients) {
        ingredients.setInput<ItemStack?>(ItemStack::class.java, this.input)
        ingredients.setOutput<ItemStack?>(ItemStack::class.java, this.output)
    }
}
