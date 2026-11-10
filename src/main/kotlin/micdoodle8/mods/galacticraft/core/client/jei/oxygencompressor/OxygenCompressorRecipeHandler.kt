package micdoodle8.mods.galacticraft.core.client.jei.oxygencompressor

import javax.annotation.Nonnull

import mezz.jei.api.recipe.IRecipeHandler
import mezz.jei.api.recipe.IRecipeWrapper
import micdoodle8.mods.galacticraft.core.client.jei.RecipeCategories

open class OxygenCompressorRecipeHandler : IRecipeHandler<OxygenCompressorRecipeWrapper> {
    @Nonnull
    override fun getRecipeClass(): Class<OxygenCompressorRecipeWrapper> {
        return OxygenCompressorRecipeWrapper::class.java
    }

    override fun getRecipeCategoryUid(recipe: OxygenCompressorRecipeWrapper): String {
        return RecipeCategories.OXYGEN_COMPRESSOR_ID
    }

    @Nonnull
    override fun getRecipeWrapper(@Nonnull recipe: OxygenCompressorRecipeWrapper): IRecipeWrapper {
        return recipe
    }

    override fun isRecipeValid(@Nonnull recipe: OxygenCompressorRecipeWrapper): Boolean {
        return true
    }
}
