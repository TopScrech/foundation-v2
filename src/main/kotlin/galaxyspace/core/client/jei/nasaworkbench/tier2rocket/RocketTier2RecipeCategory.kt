package galaxyspace.core.client.jei.nasaworkbench.tier2rocket

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
import net.minecraft.resources.Identifier as ResourceLocation
import org.lwjgl.opengl.GL11

open class RocketTier2RecipeCategory constructor(guiHelper: IGuiHelper) : IRecipeCategory<IRecipeWrapper> {
    @Nonnull
    private val background: IDrawable

    @Nonnull
    private val result: IDrawable

    @Nonnull
    private val tier1: IDrawable

    @Nonnull
    private val localizedName: String

    init {
        this.background = guiHelper.createDrawable(rocketGuiTexture, 3, 4, 187, 151)
        this.localizedName = GCCoreUtil.translate("tile.rocket_workbench.name")

        this.result = guiHelper.createDrawable(rocketGuiTexture, 192, 21, 34, 34)
        this.tier1 = guiHelper.createDrawable(rocketGuiTexture, 192, 56, 34, 34)
    }

    @Nonnull
    override fun getUid(): String {
        return GSRecipeCategories.ROCKET_TIER_2
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
        GL11.glPushMatrix()
        this.result.draw(minecraft, 135, 50)
        GL11.glColor3f(0.5f, 0.0f, 1.0f)
        this.tier1.draw(minecraft, 135, 50)
        GL11.glColor3f(1.0f, 1.0f, 1.0f)
        GL11.glPopMatrix()
        this.result.draw(minecraft, 135, 110)
    }

    override fun setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: IRecipeWrapper, ingredients: IIngredients) {
        val itemstacks: IGuiItemStackGroup = recipeLayout.getItemStacks()

        val xOffset: Int = -10
        val yOffset: Int = 20

        itemstacks.init(0, true, 44 + xOffset, 0 + yOffset)
        itemstacks.init(1, true, 35 + xOffset, 18 + yOffset)
        itemstacks.init(2, true, 35 + xOffset, 36 + yOffset)
        itemstacks.init(3, true, 35 + xOffset, 54 + yOffset)
        itemstacks.init(4, true, 35 + xOffset, 72 + yOffset)
        itemstacks.init(5, true, 35 + xOffset, 90 + yOffset)
        itemstacks.init(6, true, 53 + xOffset, 18 + yOffset)
        itemstacks.init(7, true, 53 + xOffset, 36 + yOffset)
        itemstacks.init(8, true, 53 + xOffset, 54 + yOffset)
        itemstacks.init(9, true, 53 + xOffset, 72 + yOffset)
        itemstacks.init(10, true, 53 + xOffset, 90 + yOffset)
        itemstacks.init(11, true, 17 + xOffset, 72 + yOffset) // Booster left
        itemstacks.init(12, true, 17 + xOffset, 90 + yOffset)
        itemstacks.init(13, true, 71 + xOffset, 90 + yOffset)
        itemstacks.init(14, true, 44 + xOffset, 108 + yOffset) // Rocket
        itemstacks.init(15, true, 71 + xOffset, 72 + yOffset) // Booster right
        itemstacks.init(16, true, 17 + xOffset, 108 + yOffset)
        itemstacks.init(17, true, 71 + xOffset, 108 + yOffset)

        itemstacks.init(18, true, 115 + xOffset, 10 + yOffset)
        itemstacks.init(19, true, 115 + 25 + xOffset, 10 + yOffset)
        itemstacks.init(20, true, 115 + 25 * 2 + xOffset, 10 + yOffset)

        itemstacks.init(21, false, 143, 118)

        itemstacks.init(22, true, 75, 25)
        itemstacks.init(23, true, 75, 25 + 18)
        itemstacks.init(24, true, 75, 25 + 36)

        itemstacks.init(25, true, 143, 58)


        itemstacks.set(ingredients)
    }

    override fun getModName(): String {
        return GalaxySpace.NAME
    }

    companion object {
        private val rocketGuiTexture =
            ResourceLocation.fromNamespaceAndPath(GalaxySpace.ASSET_PREFIX, "textures/gui/schematics/schematic_clear.png")
    }
}
