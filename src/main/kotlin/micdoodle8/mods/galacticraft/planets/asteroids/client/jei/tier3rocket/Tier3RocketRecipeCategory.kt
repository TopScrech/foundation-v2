package micdoodle8.mods.galacticraft.planets.asteroids.client.jei.tier3rocket

import javax.annotation.Nonnull

import mezz.jei.api.IGuiHelper
import mezz.jei.api.gui.IDrawable
import mezz.jei.api.gui.IGuiItemStackGroup
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.recipe.IRecipeCategory
import mezz.jei.api.recipe.IRecipeWrapper
import micdoodle8.mods.galacticraft.core.client.jei.RecipeCategories
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import net.minecraft.resources.Identifier as ResourceLocation

open class Tier3RocketRecipeCategory constructor(guiHelper: IGuiHelper) : IRecipeCategory<IRecipeWrapper> {
    @Nonnull
    private val background: IDrawable

    @Nonnull
    private val localizedName: String

    init {
        this.background = guiHelper.createDrawable(rocketGuiTexture, 0, 0, 168, 126)
        this.localizedName = GCCoreUtil.translate("tile.rocket_workbench.name")
    }

    @Nonnull
    override fun getUid(): String {
        return RecipeCategories.ROCKET_T3_ID
    }

    @Nonnull
    override fun getTitle(): String {
        return this.localizedName
    }

    @Nonnull
    override fun getBackground(): IDrawable {
        return this.background
    }


    override fun setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: IRecipeWrapper, ingredients: IIngredients) {
        val itemstacks: IGuiItemStackGroup = recipeLayout.getItemStacks()

        itemstacks.init(0, true, 44, 0)
        itemstacks.init(1, true, 35, 18)
        itemstacks.init(2, true, 35, 36)
        itemstacks.init(3, true, 35, 54)
        itemstacks.init(4, true, 35, 72)
        itemstacks.init(5, true, 35, 90)
        itemstacks.init(6, true, 53, 18)
        itemstacks.init(7, true, 53, 36)
        itemstacks.init(8, true, 53, 54)
        itemstacks.init(9, true, 53, 72)
        itemstacks.init(10, true, 53, 90)
        itemstacks.init(11, true, 17, 72) // Booster left
        itemstacks.init(12, true, 17, 90)
        itemstacks.init(13, true, 71, 90)
        itemstacks.init(14, true, 44, 108) // Rocket
        itemstacks.init(15, true, 71, 72) // Booster right
        itemstacks.init(16, true, 17, 108)
        itemstacks.init(17, true, 71, 108)
        itemstacks.init(18, true, 89, 7)
        itemstacks.init(19, true, 115, 7)
        itemstacks.init(20, true, 141, 7)
        itemstacks.init(21, false, 138, 95)

        itemstacks.set(ingredients)
    }

    override fun getModName(): String {
        return GalacticraftPlanets.NAME
    }

    companion object {
        private val rocketGuiTexture =
            ResourceLocation.fromNamespaceAndPath(GalacticraftPlanets.ASSET_PREFIX, "textures/gui/schematic_rocket_t3_recipe.png")
    }
}
