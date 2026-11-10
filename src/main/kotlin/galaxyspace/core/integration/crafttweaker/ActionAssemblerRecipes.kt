package galaxyspace.core.integration.crafttweaker

import crafttweaker.api.item.IItemStack
import crafttweaker.IAction
import crafttweaker.api.minecraft.CraftTweakerMC
import galaxyspace.systems.SolarSystem.planets.overworld.recipes.AssemblyRecipes
import net.minecraft.item.ItemStack
import java.util.ArrayList

open class ActionAssemblerRecipes {
    open class Add constructor(private val output: IItemStack?, vararg inputs: IItemStack?) : IAction {
        private val inputs: Array<IItemStack?>

        init {
            this.inputs = (inputs as Array<IItemStack?>)
        }

        override fun apply() {
            val list: ArrayList<ItemStack?> = ArrayList<ItemStack?>()
            for (stack: ItemStack? in CraftTweakerMC.getItemStacks(*inputs)) list.add(stack)

            AssemblyRecipes.addShapelessRecipe(CraftTweakerMC.getItemStack(output), *list.toTypedArray())
        }

        override fun describe(): String {
            return "Added Assembly recipe for " + this.output
        }
    }

    open class Remove constructor(private val item: IItemStack?) : IAction {
        override fun apply() {
            AssemblyRecipes.removeRecipe(CraftTweakerMC.getItemStack(item))
        }

        override fun describe(): String {
            return "Removed Assembly recipe for " + this.item
        }
    }
}
