package micdoodle8.mods.galacticraft.planets.mars.client.jei.methanesynthesizer

import javax.annotation.Nonnull

import mezz.jei.api.IGuiHelper
import mezz.jei.api.gui.*
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.recipe.IRecipeCategory
import mezz.jei.api.recipe.IRecipeWrapper
import micdoodle8.mods.galacticraft.core.client.jei.RecipeCategories
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems
import net.minecraft.client.Minecraft
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation

open class MethaneSynthesizerRecipeCategory constructor(guiHelper: IGuiHelper) : IRecipeCategory<IRecipeWrapper> {
    @Nonnull
    private val background: IDrawable

    @Nonnull
    private val localizedName: String

    @Nonnull
    private val hydrogenBarInput: IDrawableAnimated

    @Nonnull
    private val carbonDioxideBarInput: IDrawableAnimated

    @Nonnull
    private val methaneBarOutput: IDrawableAnimated

    @JvmField
    var fillAtmos: Boolean = false

    init {
        this.background = guiHelper.createDrawable(refineryGuiTex, 3, 4, 168, 66)
        this.localizedName = GCCoreUtil.translate("tile.mars_machine.5.name")

        val hydrogenBar: IDrawableStatic = guiHelper.createDrawable(gasesTex, 35, 0, 16, 38)
        this.hydrogenBarInput =
            guiHelper.createAnimatedDrawable(hydrogenBar, 70, IDrawableAnimated.StartDirection.TOP, true)
        val carbonDioxideBar: IDrawableStatic = guiHelper.createDrawable(gasesTex, 35, 0, 16, 20)
        this.carbonDioxideBarInput =
            guiHelper.createAnimatedDrawable(carbonDioxideBar, 70, IDrawableAnimated.StartDirection.TOP, true)
        val methaneBar: IDrawableStatic = guiHelper.createDrawable(gasesTex, 1, 0, 16, 38)
        this.methaneBarOutput =
            guiHelper.createAnimatedDrawable(methaneBar, 70, IDrawableAnimated.StartDirection.BOTTOM, false)
    }

    @Nonnull
    override fun getUid(): String {
        return RecipeCategories.METHANE_SYNTHESIZER_ID
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
        if (this.fillAtmos) {
            this.hydrogenBarInput.draw(minecraft, 29, 24)
            this.carbonDioxideBarInput.draw(minecraft, 50, 24)
        } else {
            this.hydrogenBarInput.draw(minecraft, 29, 24)
        }
        this.methaneBarOutput.draw(minecraft, 114, 24)
    }

    override fun setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: IRecipeWrapper, ingredients: IIngredients) {
        val itemstacks: IGuiItemStackGroup = recipeLayout.getItemStacks()

        itemstacks.init(0, true, 28, 2)
        itemstacks.init(1, true, 49, 2)
        itemstacks.init(2, true, 49, 48)
        itemstacks.init(3, false, 113, 2)

        if (recipeWrapper is MethaneSynthesizerRecipeWrapper) {
            val gasLiquefierRecipeWrapper: MethaneSynthesizerRecipeWrapper = recipeWrapper
            val input: MutableList<ItemStack?> = ingredients.getInputs<ItemStack?>(ItemStack::class.java).get(0)

            val inputItem: Item = input.get(0)!!.getItem()
            if (inputItem === AsteroidsItems.atmosphericValve) {
                this.fillAtmos = true
                itemstacks.set(1, input)
            } else {
                this.fillAtmos = false
                itemstacks.set(2, input)
            }

            itemstacks.set(3, ingredients.getOutputs<ItemStack?>(ItemStack::class.java).get(0))
        }
    }

    override fun getModName(): String {
        return GalacticraftPlanets.NAME
    }

    companion object {
        private val refineryGuiTex =
            ResourceLocation(GalacticraftPlanets.ASSET_PREFIX, "textures/gui/methane_synthesizer_recipe.png")
        private val gasesTex =
            ResourceLocation(GalacticraftPlanets.ASSET_PREFIX, "textures/gui/gases_methane_oxygen_nitrogen.png")
    }
}
