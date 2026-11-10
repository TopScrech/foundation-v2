package micdoodle8.mods.galacticraft.core.client.jei.refinery

import javax.annotation.Nonnull

import mezz.jei.api.IGuiHelper
import mezz.jei.api.gui.*
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.recipe.IRecipeCategory
import mezz.jei.api.recipe.IRecipeWrapper
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.client.jei.RecipeCategories
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation

open class RefineryRecipeCategory constructor(guiHelper: IGuiHelper) : IRecipeCategory<IRecipeWrapper> {
    @Nonnull
    private val background: IDrawable

    @Nonnull
    private val localizedName: String

    @Nonnull
    private val oilBar: IDrawableAnimated

    @Nonnull
    private val fuelBar: IDrawableAnimated

    init {
        this.background = guiHelper.createDrawable(refineryGuiTex, 3, 4, 168, 64)
        this.localizedName = GCCoreUtil.translate("tile.refinery.name")

        val progressBarDrawableOil: IDrawableStatic = guiHelper.createDrawable(refineryGuiTex, 176, 0, 16, 38)
        this.oilBar =
            guiHelper.createAnimatedDrawable(progressBarDrawableOil, 70, IDrawableAnimated.StartDirection.TOP, true)
        val progressBarDrawableFuel: IDrawableStatic = guiHelper.createDrawable(refineryGuiTex, 192, 0, 16, 38)
        this.fuelBar = guiHelper.createAnimatedDrawable(
            progressBarDrawableFuel,
            70,
            IDrawableAnimated.StartDirection.BOTTOM,
            false
        )
    }

    @Nonnull
    override fun getUid(): String {
        return RecipeCategories.REFINERY_ID
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
        this.oilBar.draw(minecraft, 40, 24)
        this.fuelBar.draw(minecraft, 114, 24)
    }

    override fun setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: IRecipeWrapper, ingredients: IIngredients) {
        val itemstacks: IGuiItemStackGroup = recipeLayout.getItemStacks()

        itemstacks.init(0, true, 39, 2)
        itemstacks.init(1, false, 113, 2)

        itemstacks.set(ingredients)
    }

    override fun getModName(): String {
        return GalacticraftCore.NAME
    }

    companion object {
        private val refineryGuiTex = ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/refinery_recipe.png")
    }
}
