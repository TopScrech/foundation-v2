package micdoodle8.mods.galacticraft.core.client.jei.ingotcompressor

import java.util.Arrays

import javax.annotation.Nonnull

import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.recipe.IRecipeWrapper
import micdoodle8.mods.galacticraft.api.recipe.ShapedRecipesGC
import micdoodle8.mods.galacticraft.core.client.jei.GalacticraftJEI
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.world.item.ItemStack
import net.minecraft.item.crafting.FurnaceRecipes
import java.awt.Color
import java.util.*

open class IngotCompressorShapedRecipeWrapper constructor(@field:Nonnull @param:Nonnull private val recipe: ShapedRecipesGC) :
    IRecipeWrapper {
    override fun getIngredients(ingredients: IIngredients) {
        ingredients.setInputs<ItemStack?>(ItemStack::class.java, Arrays.asList<ItemStack?>(*recipe.recipeItems))
        ingredients.setOutput<ItemStack?>(ItemStack::class.java, recipe.getRecipeOutput())
    }

    override fun drawInfo(mc: Minecraft, recipeWidth: Int, recipeHeight: Int, mouseX: Int, mouseY: Int) {
        if (GalacticraftJEI.hidden.contains(this)) {
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

    open fun matches(test: ShapedRecipesGC?): Boolean {
        return this.recipe === test
    }
}
