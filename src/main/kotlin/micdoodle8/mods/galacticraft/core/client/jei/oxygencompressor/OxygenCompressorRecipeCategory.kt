package micdoodle8.mods.galacticraft.core.client.jei.oxygencompressor

import javax.annotation.Nonnull

import mezz.jei.api.IGuiHelper
import mezz.jei.api.gui.*
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.recipe.BlankRecipeCategory
import mezz.jei.api.recipe.IRecipeWrapper
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.client.jei.RecipeCategories
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation

open class OxygenCompressorRecipeCategory constructor(guiHelper: IGuiHelper) : BlankRecipeCategory<IRecipeWrapper>() {
    @Nonnull
    private val background: IDrawable

    @Nonnull
    private val localizedName: String

    private val oxygenBar: IDrawableAnimated
    private val oxygenLabel: IDrawable

    init {
        this.background = guiHelper.createDrawable(compressorTexture, 3, 4, 168, 80)
        this.localizedName = GCCoreUtil.translate("tile.oxygen_compressor.0.name")

        val progressBarDrawableO2: IDrawableStatic = guiHelper.createDrawable(compressorTexture, 197, 7, 54, 7)
        this.oxygenBar =
            guiHelper.createAnimatedDrawable(progressBarDrawableO2, 70, IDrawableAnimated.StartDirection.RIGHT, true)
        this.oxygenLabel = guiHelper.createDrawable(compressorTexture, 187, 0, 10, 10)
    }

    @Nonnull
    override fun getUid(): String {
        return RecipeCategories.OXYGEN_COMPRESSOR_ID
    }

    @Nonnull
    override fun getTitle(): String {
        return this.localizedName
    }

    @Nonnull
    override fun getBackground(): IDrawable {
        return this.background
    }

    override fun drawExtras(@Nonnull minecraft: Minecraft) {
        this.oxygenLabel.draw(minecraft, 97, 15)
        this.oxygenBar.draw(minecraft, 110, 16)
    }

    override fun setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: IRecipeWrapper, ingredients: IIngredients) {
        val itemstacks: IGuiItemStackGroup = recipeLayout.getItemStacks()

        itemstacks.init(0, false, 129, 61)

        if (recipeWrapper is OxygenCompressorRecipeWrapper) {
            itemstacks.set(ingredients)
        }
    }

    override fun getModName(): String {
        return GalacticraftCore.NAME
    }

    companion object {
        private val compressorTexture = ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/compressor.png")
    }
}
