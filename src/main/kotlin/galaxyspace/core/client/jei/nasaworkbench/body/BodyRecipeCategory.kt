package galaxyspace.core.client.jei.nasaworkbench.body

import javax.annotation.Nonnull

import galaxyspace.GalaxySpace
import galaxyspace.core.client.jei.GSRecipeCategories
import mezz.jei.api.IGuiHelper
import mezz.jei.api.gui.IDrawable
import mezz.jei.api.gui.IGuiItemStackGroup
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.recipe.IRecipeCategory
import mezz.jei.api.recipe.IRecipeWrapper
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation

open class BodyRecipeCategory constructor(guiHelper: IGuiHelper) : IRecipeCategory<IRecipeWrapper> {
    @Nonnull
    private val background: IDrawable

    @Nonnull
    private val result: IDrawable

    @Nonnull
    private val localizedName: String

    init {
        this.background = guiHelper.createDrawable(rocketGuiTexture, 3, 4, 187, 151)
        this.localizedName = GCCoreUtil.translate("tile.rocket_workbench.name")

        this.result = guiHelper.createDrawable(rocketGuiTexture, 192, 21, 34, 34)
    }

    @Nonnull
    override fun getUid(): String {
        return GSRecipeCategories.BODY
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
        this.result.draw(minecraft, 135, 110)
    }

    override fun setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: IRecipeWrapper, ingredients: IIngredients) {
        val itemstacks: IGuiItemStackGroup = recipeLayout.getItemStacks()

        val xOffset: Int = 5
        val yOffset: Int = 10
        itemstacks.init(0, true, 13 + xOffset, 25 + yOffset)
        itemstacks.init(1, true, 13 + xOffset, 25 + 16 + yOffset)
        itemstacks.init(2, true, 13 + xOffset, 25 + 16 * 2 + yOffset)
        itemstacks.init(3, true, 13 + xOffset, 25 + 16 * 3 + yOffset)
        itemstacks.init(4, true, 13 + xOffset, 25 + 16 * 4 + yOffset)
        itemstacks.init(5, true, 13 + xOffset, 25 + 16 * 5 + yOffset)

        itemstacks.init(6, true, 29 + xOffset, 25 + yOffset)
        itemstacks.init(7, true, 29 + xOffset, 25 + 16 + yOffset)
        itemstacks.init(8, true, 29 + xOffset, 25 + 16 * 2 + yOffset)
        itemstacks.init(9, true, 29 + xOffset, 25 + 16 * 3 + yOffset)
        itemstacks.init(10, true, 29 + xOffset, 25 + 16 * 4 + yOffset)
        itemstacks.init(11, true, 29 + xOffset, 25 + 16 * 5 + yOffset)

        itemstacks.init(12, true, 45 + xOffset, 25 + yOffset)
        itemstacks.init(13, true, 45 + xOffset, 25 + 16 + yOffset)
        itemstacks.init(14, true, 45 + xOffset, 25 + 16 * 2 + yOffset)
        itemstacks.init(15, true, 45 + xOffset, 25 + 16 * 3 + yOffset)
        itemstacks.init(16, true, 45 + xOffset, 25 + 16 * 4 + yOffset)
        itemstacks.init(17, true, 45 + xOffset, 25 + 16 * 5 + yOffset)

        itemstacks.init(18, true, 61 + xOffset, 25 + yOffset)
        itemstacks.init(19, true, 61 + xOffset, 25 + 16 + yOffset)
        itemstacks.init(20, true, 61 + xOffset, 25 + 16 * 2 + yOffset)
        itemstacks.init(21, true, 61 + xOffset, 25 + 16 * 3 + yOffset)
        itemstacks.init(22, true, 61 + xOffset, 25 + 16 * 4 + yOffset)
        itemstacks.init(23, true, 61 + xOffset, 25 + 16 * 5 + yOffset)

        itemstacks.init(24, false, 143, 118)

        itemstacks.set(ingredients)
    }

    override fun getModName(): String {
        return GalaxySpace.NAME
    }

    companion object {
        private val rocketGuiTexture =
            ResourceLocation(GalaxySpace.ASSET_PREFIX, "textures/gui/schematics/schematic_clear.png")
    }
}
