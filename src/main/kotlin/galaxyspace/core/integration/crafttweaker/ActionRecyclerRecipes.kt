package galaxyspace.core.integration.crafttweaker

import crafttweaker.api.item.IItemStack
import crafttweaker.IAction
import crafttweaker.api.minecraft.CraftTweakerMC
import crafttweaker.api.liquid.ILiquidStack
import galaxyspace.systems.SolarSystem.planets.overworld.recipes.RecyclerRecipes

open class ActionRecyclerRecipes {
    open class Add constructor(
        private val stack: IItemStack?,
        private val result: IItemStack?,
        private val liquid: ILiquidStack?,
        private val chance: Int
    ) : IAction {
        override fun apply() {
            RecyclerRecipes.recycling().addNewRecipe(
                CraftTweakerMC.getItemStack(stack),
                CraftTweakerMC.getItemStack(result),
                chance,
                CraftTweakerMC.getLiquidStack(liquid)
            )
        }

        override fun describe(): String {
            return "Added Recycler recipe for " + this.liquid
        }
    }

    open class Remove constructor(private val stack: IItemStack?) : IAction {
        override fun apply() {
            RecyclerRecipes.recycling().removeRecipe(CraftTweakerMC.getItemStack(stack))
        }

        override fun describe(): String {
            return "Removed Recycler recipe for " + this.stack
        }
    }
}
