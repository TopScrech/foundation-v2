package micdoodle8.mods.galacticraft.core.client.jei

import java.util.Arrays

import javax.annotation.Nonnull

import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.recipe.BlankRecipeWrapper
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper
import micdoodle8.mods.galacticraft.core.recipe.ShapedRecipeNBT
import net.minecraft.item.ItemStack
import java.util.*

open class NBTSensitiveShapedRecipeWrapper constructor(@field:Nonnull @param:Nonnull private val recipe: ShapedRecipeNBT) :
    BlankRecipeWrapper(), IShapedCraftingRecipeWrapper {
    override fun getIngredients(ingredients: IIngredients) {
        val recipeItems: MutableList<ItemStack?> = Arrays.asList<ItemStack?>(*recipe.recipeItems)
        val recipeOutput: ItemStack? = recipe.getRecipeOutput()
        try {
            ingredients.setInputs<ItemStack?>(ItemStack::class.java, recipeItems)
            if (recipeOutput != null) {
                ingredients.setOutput<ItemStack?>(ItemStack::class.java, recipeOutput)
            }
        } catch (e: RuntimeException) {
            e.printStackTrace()
        }
    }

    override fun getWidth(): Int {
        return 3
    }

    override fun getHeight(): Int {
        return 3
    }
}
