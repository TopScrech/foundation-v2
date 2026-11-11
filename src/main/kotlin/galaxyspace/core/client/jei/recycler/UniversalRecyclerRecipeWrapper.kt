package galaxyspace.core.client.jei.recycler

import java.util.Arrays

import javax.annotation.Nonnull

import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.recipe.IRecipeWrapper
import net.minecraft.client.Minecraft
import net.minecraft.world.item.ItemStack
import net.minecraftforge.fluids.FluidStack
import java.util.*

open class UniversalRecyclerRecipeWrapper constructor(
    @field:Nonnull @param:Nonnull private val input: ItemStack,
    @field:Nonnull @param:Nonnull private val output: ItemStack,
    private val output_2: ItemStack?,
    private val fluid: FluidStack?,
    private val chance: Int,
    private val chance_2: Int
) : IRecipeWrapper {
    override fun getIngredients(ingredients: IIngredients) {
        ingredients.setInput<ItemStack?>(ItemStack::class.java, this.input)
        ingredients.setOutputs<ItemStack?>(ItemStack::class.java, Arrays.asList<ItemStack?>(this.output, this.output_2))
    }

    open fun getFluidStack(): FluidStack? {
        return this.fluid
    }

    open fun getChance(): Int {
        return this.chance
    }

    open fun getChance_2(): Int {
        return this.chance_2
    }

    override fun drawInfo(minecraft: Minecraft, recipeWidth: Int, recipeHeight: Int, mouseX: Int, mouseY: Int) {
        if (getChance() < 100 || getChance_2() < 100) {
            minecraft.fontRenderer.drawString("Chances: ", recipeWidth / 2 - 20, 4, 0xFFFFFF)
            minecraft.fontRenderer.drawString(
                "Primary slot: " + getChance() + "%",
                recipeWidth / 2 - 4 * 8 - 4,
                13,
                0xFFFFFF
            )

            if (this.output_2 != ItemStack.EMPTY) minecraft.fontRenderer.drawString(
                "Secondary slot: " + getChance_2() + "%",
                recipeWidth / 2 - 4 * 9 - 4,
                17 + 5,
                0xFFFFFF
            )
        }


        //if(getFluidStack() != null) {
        //	minecraft.fontRenderer.drawString("Fluid: " + getFluidStack().getLocalizedName(), 36, 64, 0xFFFFFF);    		
        //}
    }
}
