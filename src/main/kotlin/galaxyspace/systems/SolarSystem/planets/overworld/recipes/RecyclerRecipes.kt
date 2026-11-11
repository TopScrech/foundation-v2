package galaxyspace.systems.SolarSystem.planets.overworld.recipes

import galaxyspace.systems.SolarSystem.planets.overworld.recipes.RecyclerRecipes.RecycleRecipe
import galaxyspace.systems.SolarSystem.planets.overworld.recipes.RecyclerRecipes
import net.minecraft.world.item.ItemStack
import net.minecraftforge.fluids.FluidStack
import java.util.ArrayList

open class RecyclerRecipes {
    private val recipes: MutableList<RecycleRecipe> = ArrayList<RecycleRecipe>()

    open fun addNewRecipe(stack: ItemStack?, result: ItemStack?, fluidstack: FluidStack?) {
        this.recipes.add(RecycleRecipe(stack, result, fluidstack))
    }

    open fun addNewRecipe(stack: ItemStack?, result: ItemStack?, chance: Int, fluidstack: FluidStack?) {
        this.recipes.add(RecycleRecipe(stack, result, fluidstack, chance))
    }

    open fun addNewRecipe(
        stack: ItemStack?,
        result: ItemStack?,
        result_2: ItemStack?,
        chance: Int,
        chance_2: Int,
        fluidstack: FluidStack?
    ) {
        this.recipes.add(RecycleRecipe(stack, result, result_2, fluidstack, chance, chance_2))
    }

    open fun removeRecipe(stack: ItemStack) {
        recipes.remove(getRecipe(stack))
    }

    open fun getRecipes(): MutableList<RecycleRecipe> {
        return this.recipes
    }

    open fun getRecipe(stack: ItemStack): RecycleRecipe? {
        for (recipe: RecycleRecipe in this.recipes) if (recipe.getInput()!!.isItemEqual(stack)) return recipe

        return null
    }

    open inner class RecycleRecipe {
        private var input: ItemStack? = ItemStack.EMPTY
        private var output: ItemStack? = ItemStack.EMPTY
        private var output_2: ItemStack? = ItemStack.EMPTY
        private val fluid: FluidStack?
        private var chance_procent = 100
        private var chance_procent_2 = 100

        @JvmOverloads
        constructor(input: ItemStack?, output: ItemStack?, fluid: FluidStack?, chance: Int = 100) {
            this.input = input
            this.output = output
            this.fluid = fluid
            this.chance_procent = chance
        }

        constructor(
            input: ItemStack?,
            output: ItemStack?,
            output_2: ItemStack?,
            fluid: FluidStack?,
            chance: Int,
            chance_2: Int
        ) {
            this.input = input
            this.output = output
            this.fluid = fluid
            this.chance_procent = chance
            this.output_2 = output_2
            this.chance_procent_2 = chance_2
        }

        open fun getInput(): ItemStack? {
            return this.input
        }

        open fun getOutput(): ItemStack? {
            return this.output
        }

        open fun getOutput_2(): ItemStack? {
            return this.output_2
        }

        open fun getFluidStack(): FluidStack? {
            return this.fluid
        }

        open fun hasChance(): Boolean {
            return chance_procent != 100
        }

        open fun getChance(): Int {
            return this.chance_procent
        }

        open fun hasChance_2(): Boolean {
            return this.chance_procent_2 != 100
        }

        open fun getChance_2(): Int {
            return this.chance_procent_2
        }
    }

    companion object {
        private val recyclerBase = RecyclerRecipes()

        @JvmStatic
        fun recycling(): RecyclerRecipes {
            return recyclerBase
        }
    }
}
