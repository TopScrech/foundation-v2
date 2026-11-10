package galaxyspace.core.client.jei.assembler

import javax.annotation.Nonnull

import galaxyspace.core.client.jei.GalaxySpaceJEI
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.recipe.IRecipeWrapper
import mezz.jei.api.recipe.IStackHelper
import micdoodle8.mods.galacticraft.api.recipe.ShapelessOreRecipeGC
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.FurnaceRecipes
import java.awt.Color

open class AssemblerShapelessRecipeWrapper constructor(
    private val stackHelper: IStackHelper,
    @field:Nonnull @param:Nonnull private val recipe: ShapelessOreRecipeGC
) : IRecipeWrapper {
    override fun getIngredients(ingredients: IIngredients) {
        val inputs: MutableList<MutableList<ItemStack?>?> =
            this.stackHelper.expandRecipeItemStackInputs(recipe.getInput())
        ingredients.setInputLists<ItemStack?>(ItemStack::class.java, inputs)
        ingredients.setOutput<ItemStack?>(ItemStack::class.java, this.recipe.getRecipeOutput())
    }

    override fun drawInfo(mc: Minecraft, recipeWidth: Int, recipeHeight: Int, mouseX: Int, mouseY: Int) {
        if (GalaxySpaceJEI.Companion.hidden.contains(this)) {
            val fontRendererObj: FontRenderer = mc.fontRenderer
            var experienceString: String = "Asteroids Challenge"
            GCCoreUtil.drawStringCentered(experienceString, 69, 8, Color.gray.getRGB(), fontRendererObj)
            experienceString = "game mode only!"
            GCCoreUtil.drawStringCentered(experienceString, 69, 20, Color.gray.getRGB(), fontRendererObj)
            return
        }
        val furnaceRecipes: FurnaceRecipes = FurnaceRecipes.instance()
        var experience: Float = 0f

        try {
            experience = furnaceRecipes.getSmeltingExperience(this.recipe.getRecipeOutput())
        } catch (e: Exception) {
        }

        if (experience > 0) {
            val experienceString: String =
                GCCoreUtil.translateWithFormat("gui.jei.category.smelting.experience", experience)
            val fontRendererObj: FontRenderer = mc.fontRenderer
            val stringWidth: Int = fontRendererObj.getStringWidth(experienceString)
            fontRendererObj.drawString(experienceString, recipeWidth + 6 - stringWidth, 8, Color.gray.getRGB())
        }
    }

    open fun matches(@Nonnull test: ShapelessOreRecipeGC): Boolean {
        return this.recipe === test
    }
}
