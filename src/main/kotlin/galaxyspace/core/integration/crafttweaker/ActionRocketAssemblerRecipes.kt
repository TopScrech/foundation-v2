package galaxyspace.core.integration.crafttweaker

import crafttweaker.api.item.IItemStack
import crafttweaker.IAction
import crafttweaker.api.minecraft.CraftTweakerMC
import galaxyspace.systems.SolarSystem.planets.overworld.recipes.RocketAssemblyRecipes
import net.minecraft.item.ItemStack

open class ActionRocketAssemblerRecipes {
    open class Add constructor(private val output: IItemStack?, vararg inputs: IItemStack?) : IAction {
        private val inputs: Array<IItemStack?>

        init {
            this.inputs = (inputs as Array<IItemStack?>)
        }

        override fun apply() {
            val staks: Array<ItemStack?>? = CraftTweakerMC.getItemStacks(*inputs)
            RocketAssemblyRecipes.addRecipe(CraftTweakerMC.getItemStack(output), staks as Any?)
        }

        override fun describe(): String {
            return "Added Rocket Assembly recipe for " + this.output
        }
    }

    open class Remove constructor(private val item: IItemStack?) : IAction {
        override fun apply() {
            RocketAssemblyRecipes.removeRecipe(CraftTweakerMC.getItemStack(item))
        }

        override fun describe(): String {
            return "Removed Rocket Assembly recipe for " + this.item
        }
    }
}
