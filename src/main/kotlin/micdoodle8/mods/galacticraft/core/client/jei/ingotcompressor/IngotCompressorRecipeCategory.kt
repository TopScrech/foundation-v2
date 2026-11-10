package micdoodle8.mods.galacticraft.core.client.jei.ingotcompressor

import javax.annotation.Nonnull

import mezz.jei.api.IGuiHelper
import mezz.jei.api.gui.*
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.recipe.IRecipeCategory
import mezz.jei.api.recipe.IRecipeWrapper
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.client.jei.GalacticraftJEI
import micdoodle8.mods.galacticraft.core.client.jei.RecipeCategories
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.Minecraft
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation

open class IngotCompressorRecipeCategory constructor(guiHelper: IGuiHelper) : IRecipeCategory<IRecipeWrapper> {
    @Nonnull
    private val background: IDrawable

    @Nonnull
    private val backgroundBlank: IDrawable

    @Nonnull
    private val localizedName: String

    @Nonnull
    private val progressBar: IDrawableAnimated

    private var drawNothing = false

    init {
        this.background = guiHelper.createDrawable(compressorTex, 18, 17, 137, 78)
        this.backgroundBlank = guiHelper.createDrawable(compressorTexBlank, 18, 17, 137, 78)
        this.localizedName = GCCoreUtil.translate("tile.machine.3.name")

        val progressBarDrawable: IDrawableStatic = guiHelper.createDrawable(compressorTex, 176, 13, 52, 17)
        this.progressBar =
            guiHelper.createAnimatedDrawable(progressBarDrawable, 70, IDrawableAnimated.StartDirection.LEFT, false)
    }

    @Nonnull
    override fun getUid(): String {
        return RecipeCategories.INGOT_COMPRESSOR_ID
    }

    @Nonnull
    override fun getTitle(): String {
        return this.localizedName
    }

    @Nonnull
    override fun getBackground(): IDrawable {
        if (this.drawNothing) {
            return this.backgroundBlank
        }
        return this.background
    }

    override fun drawExtras(@Nonnull minecraft: Minecraft) {
        if (!this.drawNothing) this.progressBar.draw(minecraft, 59, 19)
    }

    override fun setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: IRecipeWrapper, ingredients: IIngredients) {
        this.drawNothing = GalacticraftJEI.hidden.contains(recipeWrapper)
        if (this.drawNothing) return

        val itemstacks: IGuiItemStackGroup = recipeLayout.getItemStacks()

        for (j: Int in 0..8) {
            itemstacks.init(j, true, j % 3 * 18, j / 3 * 18)
        }

        itemstacks.init(9, false, 119, 20)

        if (ConfigManagerCore.quickMode) {
            val output: MutableList<ItemStack> = ingredients.getOutputs<ItemStack?>(ItemStack::class.java).get(0)
            val stackOutput: ItemStack = output.get(0)
            if (stackOutput.getItem().getTranslationKey(stackOutput).contains("compressed")) {
                val stackDoubled: ItemStack = stackOutput.copy()
                stackDoubled.setCount(stackOutput.getCount() * 2)
                output.set(0, stackDoubled)
            }
        }

        itemstacks.set(ingredients)
    }

    override fun getModName(): String {
        return GalacticraftCore.NAME
    }

    companion object {
        private val compressorTex = ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/ingot_compressor.png")
        private val compressorTexBlank =
            ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/ingot_compressor_blank.png")
    }
}
