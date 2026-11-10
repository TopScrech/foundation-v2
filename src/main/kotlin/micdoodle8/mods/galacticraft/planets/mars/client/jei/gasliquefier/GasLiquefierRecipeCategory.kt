package micdoodle8.mods.galacticraft.planets.mars.client.jei.gasliquefier

import javax.annotation.Nonnull

import mezz.jei.api.IGuiHelper
import mezz.jei.api.gui.*
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.recipe.IRecipeCategory
import mezz.jei.api.recipe.IRecipeWrapper
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.client.jei.RecipeCategories
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems
import net.minecraft.client.Minecraft
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation

open class GasLiquefierRecipeCategory constructor(guiHelper: IGuiHelper) : IRecipeCategory<IRecipeWrapper> {
    @Nonnull
    private val background: IDrawable

    @Nonnull
    private val localizedName: String

    @Nonnull
    private val methaneBarInput: IDrawableAnimated

    @Nonnull
    private val oxygenBarInput: IDrawableAnimated

    @Nonnull
    private val nitrogenBarInput: IDrawableAnimated

    @Nonnull
    private val fuelBarOutput: IDrawableAnimated

    @Nonnull
    private val oxygenBarOutput: IDrawableAnimated

    @Nonnull
    private val nitrogenBarOutput: IDrawableAnimated

    private var inputGas = 2
    private var outputGas = 2

    init {
        this.background = guiHelper.createDrawable(refineryGuiTex, 3, 4, 168, 64)
        this.localizedName = GCCoreUtil.translate("tile.mars_machine.4.name")

        val methaneBar: IDrawableStatic = guiHelper.createDrawable(gasesTex, 1, 0, 16, 38)
        this.methaneBarInput =
            guiHelper.createAnimatedDrawable(methaneBar, 70, IDrawableAnimated.StartDirection.TOP, true)
        val oxygenBar: IDrawableStatic = guiHelper.createDrawable(gasesTex, 18, 0, 16, 38)
        this.oxygenBarInput =
            guiHelper.createAnimatedDrawable(oxygenBar, 70, IDrawableAnimated.StartDirection.TOP, true)
        val atmosphereBar: IDrawableStatic = guiHelper.createDrawable(gasesTex, 34, 0, 16, 38)
        this.nitrogenBarInput =
            guiHelper.createAnimatedDrawable(atmosphereBar, 70, IDrawableAnimated.StartDirection.TOP, true)
        val fuelBar: IDrawableStatic = guiHelper.createDrawable(refineryGuiTex, 176, 0, 16, 38)
        this.fuelBarOutput =
            guiHelper.createAnimatedDrawable(fuelBar, 70, IDrawableAnimated.StartDirection.BOTTOM, false)
        this.oxygenBarOutput =
            guiHelper.createAnimatedDrawable(oxygenBar, 70, IDrawableAnimated.StartDirection.BOTTOM, false)
        val nitrogenBar: IDrawableStatic = guiHelper.createDrawable(gasesTex, 68, 0, 16, 38)
        this.nitrogenBarOutput =
            guiHelper.createAnimatedDrawable(nitrogenBar, 70, IDrawableAnimated.StartDirection.BOTTOM, false)
    }

    @Nonnull
    override fun getUid(): String {
        return RecipeCategories.GAS_LIQUEFIER_ID
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
        val input: IDrawableAnimated
        val output: IDrawableAnimated

        when (this.inputGas) {
            0 -> input = this.methaneBarInput
            1 -> input = this.oxygenBarInput
            else -> input = this.nitrogenBarInput
        }

        when (this.outputGas) {
            0 -> output = this.fuelBarOutput
            3 -> output = this.oxygenBarOutput
            else -> output = this.nitrogenBarOutput
        }

        input.draw(minecraft, 40, 24)
        output.draw(minecraft, if (this.outputGas == 0 || this.outputGas == 3) 124 else 105, 24)
    }

    override fun setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: IRecipeWrapper, ingredients: IIngredients) {
        val itemstacks: IGuiItemStackGroup = recipeLayout.getItemStacks()

        itemstacks.init(0, true, 39, 2)
        itemstacks.init(1, false, 104, 2)
        itemstacks.init(2, false, 123, 2)

        if (recipeWrapper is GasLiquefierRecipeWrapper) {
            val gasLiquefierRecipeWrapper: GasLiquefierRecipeWrapper = recipeWrapper
            val input: MutableList<ItemStack?> = ingredients.getInputs<ItemStack?>(ItemStack::class.java).get(0)
            val output: MutableList<ItemStack?> = ingredients.getOutputs<ItemStack?>(ItemStack::class.java).get(0)
            itemstacks.set(0, input)

            val inputItem: Item = input.get(0)!!.getItem()
            if (inputItem === AsteroidsItems.methaneCanister) {
                this.inputGas = 0
            } else if (inputItem === AsteroidsItems.canisterLOX) {
                this.inputGas = 1
            }

            val outputItem: Item = output.get(0)!!.getItem()
            if (outputItem === GCItems.fuelCanister) {
                this.outputGas = 0
            } else if (outputItem === AsteroidsItems.canisterLOX) {
                this.outputGas = 3
            } else {
                this.outputGas = 4
            }

            itemstacks.set(if (this.outputGas == 0 || this.outputGas == 3) 2 else 1, output)
        }
    }

    override fun getModName(): String {
        return GalacticraftPlanets.NAME
    }

    companion object {
        private val refineryGuiTex =
            ResourceLocation(GalacticraftPlanets.ASSET_PREFIX, "textures/gui/gas_liquefier_recipe.png")
        private val gasesTex =
            ResourceLocation(GalacticraftPlanets.ASSET_PREFIX, "textures/gui/gases_methane_oxygen_nitrogen.png")
    }
}
