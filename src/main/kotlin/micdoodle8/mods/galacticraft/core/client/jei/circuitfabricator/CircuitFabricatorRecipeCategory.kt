package micdoodle8.mods.galacticraft.core.client.jei.circuitfabricator

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
import net.minecraft.resources.Identifier as ResourceLocation

open class CircuitFabricatorRecipeCategory constructor(guiHelper: IGuiHelper) : IRecipeCategory<IRecipeWrapper> {
    @Nonnull
    private val background: IDrawable

    @Nonnull
    private val localizedName: String

    @Nonnull
    private val progressBar: IDrawableAnimated

    init {
        this.background = guiHelper.createDrawable(circuitFabTex, 3, 4, 168, 101)
        this.localizedName = GCCoreUtil.translate("tile.machine2.5.name")

        val progressBarDrawable: IDrawableStatic = guiHelper.createDrawable(circuitFabTex, 176, 17, 51, 10)
        this.progressBar =
            guiHelper.createAnimatedDrawable(progressBarDrawable, 70, IDrawableAnimated.StartDirection.LEFT, false)
    }

    @Nonnull
    override fun getUid(): String {
        return RecipeCategories.CIRCUIT_FABRICATOR_ID
    }

    @Nonnull
    override fun getTitle(): String {
        return this.localizedName
    }

    @Nonnull
    override fun getBackground(): IDrawable {
        return this.background
    }

    override fun drawExtras(minecraft: Minecraft) {
        this.progressBar.draw(minecraft, 85, 16)
    }

    override fun setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: IRecipeWrapper, ingredients: IIngredients) {
        val itemstacks: IGuiItemStackGroup = recipeLayout.getItemStacks()

        itemstacks.init(0, true, 11, 12)
        itemstacks.init(1, true, 70, 41)
        itemstacks.init(2, true, 70, 59)
        itemstacks.init(3, true, 118, 41)
        itemstacks.init(4, true, 141, 15)
        itemstacks.init(5, false, 148, 81)
        itemstacks.set(ingredients)
    }

    override fun getModName(): String {
        return GalacticraftCore.NAME
    }

    companion object {
        private val circuitFabTex = ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/gui/circuit_fabricator.png")
    }
}
