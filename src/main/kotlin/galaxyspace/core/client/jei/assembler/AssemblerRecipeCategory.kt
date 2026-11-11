package galaxyspace.core.client.jei.assembler

import mezz.jei.api.gui.IDrawable
import mezz.jei.api.gui.IDrawableAnimated
import mezz.jei.api.gui.IDrawableStatic
import mezz.jei.api.gui.IGuiItemStackGroup
import mezz.jei.api.gui.IRecipeLayout

import javax.annotation.Nonnull

import galaxyspace.GalaxySpace
import galaxyspace.core.GSBlocks
import galaxyspace.core.client.jei.GSRecipeCategories
import galaxyspace.core.configs.GSConfigCore
import galaxyspace.core.util.GSConstants
import mezz.jei.api.IGuiHelper
import mezz.jei.api.gui.*
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.recipe.IRecipeCategory
import mezz.jei.api.recipe.IRecipeWrapper
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import net.minecraft.client.Minecraft
import net.minecraft.world.item.ItemStack
import net.minecraft.resources.Identifier as ResourceLocation

open class AssemblerRecipeCategory constructor(guiHelper: IGuiHelper) : IRecipeCategory<IRecipeWrapper> {
    @Nonnull
    private val backgroundTop: IDrawable

    @Nonnull
    private val background: IDrawable

    @Nonnull
    private val backgroundBottom: IDrawable

    @Nonnull
    private val slot: IDrawable

    @Nonnull
    private val blankArrow: IDrawable

    @Nonnull
    private val localizedName: String

    @Nonnull
    private val progressBar: IDrawableAnimated


    init {
        val gui: ResourceLocation =
            if (GSConfigCore.enableModernGUI) GSConstants.GUI_MACHINE_MODERN else GSConstants.GUI_MACHINE_CLASSIC

        this.backgroundTop = guiHelper.createDrawable(gui, 0, 0, 176, 12)
        this.background = guiHelper.createDrawable(gui, 0, 0, 176, 92)
        this.backgroundBottom = guiHelper.createDrawable(gui, 0, 31, 176, 30)
        this.slot = guiHelper.createDrawable(gui, 0, 62, 18, 18)
        this.blankArrow = guiHelper.createDrawable(gui, 181, 109, 36, 15)

        this.localizedName = GSBlocks.ASSEMBLER.getLocalizedName() //GCCoreUtil.translate("tile.machine.3.name");

        //ResourceLocation resourceLocation, int u, int v, int width, int height
        val progressBarDrawable: IDrawableStatic =
            guiHelper.createDrawable(gui, 181, 125, 36, 16) //guiHelper.createDrawable(compressorTex, 180, 15, 52, 17);
        this.progressBar =
            guiHelper.createAnimatedDrawable(progressBarDrawable, 70, IDrawableAnimated.StartDirection.LEFT, false)
    }

    @Nonnull
    override fun getUid(): String {
        return GSRecipeCategories.ASSEMBLER
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
        this.backgroundBottom.draw(minecraft, 0, 57)

        for (i: Int in 0..2) for (j: Int in 0..2) this.slot.draw(minecraft, i * 19 + 10, j * 19 + 20)

        this.slot.draw(minecraft, 139, 39)
        this.blankArrow.draw(minecraft, 85, 39)
        this.progressBar.draw(minecraft, 85, 38)
    }

    override fun setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: IRecipeWrapper, ingredients: IIngredients) {
        val itemstacks: IGuiItemStackGroup = recipeLayout.getItemStacks()

        for (j: Int in 0..8) {
            itemstacks.init(j, true, j % 3 * 19 + 10, j / 3 * 19 + 20)
        }

        itemstacks.init(9, false, 139, 39)

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
        return GalaxySpace.NAME
    }
}
