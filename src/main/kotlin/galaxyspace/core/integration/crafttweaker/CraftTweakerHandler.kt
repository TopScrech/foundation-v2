package galaxyspace.core.integration.crafttweaker

import crafttweaker.api.item.IItemStack
import crafttweaker.api.liquid.ILiquidStack
import crafttweaker.api.block.IBlock
import crafttweaker.annotations.ZenRegister
import stanhebben.zenscript.annotations.ZenClass
import stanhebben.zenscript.annotations.ZenMethod
import crafttweaker.CraftTweakerAPI

@ZenRegister
@ZenClass("mods.galaxyspace")
object CraftTweakerHandler {
    //ADD
    @JvmStatic
    @ZenMethod
    fun addAssemblyRecipe(output: IItemStack?, vararg input: IItemStack?) {
        CraftTweakerAPI.apply(ActionAssemblerRecipes.Add(output, *input))
    }

    @JvmStatic
    @ZenMethod
    fun addFuelGeneratorRecipe(fluid: ILiquidStack?, burn_time: Int, mod_energy: Float) {
        CraftTweakerAPI.apply(ActionFuelGeneratorRecipes.Add(fluid, burn_time, mod_energy))
    }

    @JvmStatic
    @ZenMethod
    fun addGasGeneratorRecipe(fluid: ILiquidStack?, burn_time: Int, mod_energy: Float, drain_amount: Int) {
        CraftTweakerAPI.apply(ActionGasGeneratorRecipes.Add(fluid, burn_time, mod_energy, drain_amount))
    }

    @JvmStatic
    @ZenMethod
    fun addHydroponicFarmRecipe(
        seed: IItemStack?,
        product: IItemStack?,
        secproduct: IItemStack?,
        secchances: Int,
        stages: Int,
        block: IBlock?,
        product_rand: Boolean,
        secproduct_rand: Boolean
    ) {
        CraftTweakerAPI.apply(
            ActionHydroponicRecipes.Add(
                seed,
                product,
                secproduct,
                secchances,
                stages,
                block,
                product_rand,
                secproduct_rand
            )
        )
    }

    @JvmStatic
    @ZenMethod
    fun addLiquidExtractorRecipe(block: IBlock?, fluid: ILiquidStack?) {
        CraftTweakerAPI.apply(ActionLiquidExtractorRecipes.Add(block, fluid))
    }

    @JvmStatic
    @ZenMethod
    fun addRecyclerRecipe(stack: IItemStack?, result: IItemStack?, liquid: ILiquidStack?, chances: Int) {
        CraftTweakerAPI.apply(ActionRecyclerRecipes.Add(stack, result, liquid, chances))
    }

    @JvmStatic
    @ZenMethod
    fun addRocketAssemblyRecipe(output: IItemStack?, vararg input: IItemStack?) {
        CraftTweakerAPI.apply(ActionRocketAssemblerRecipes.Add(output, *input))
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    //REMOVE
    @JvmStatic
    @ZenMethod
    fun removeAssemblyRecipe(output: IItemStack?) {
        CraftTweakerAPI.apply(ActionAssemblerRecipes.Remove(output))
    }

    @JvmStatic
    @ZenMethod
    fun removeFuelGeneratorRecipe(fluid: ILiquidStack?) {
        CraftTweakerAPI.apply(ActionFuelGeneratorRecipes.Remove(fluid))
    }

    @JvmStatic
    @ZenMethod
    fun removeGasGeneratorRecipe(fluid: ILiquidStack?) {
        CraftTweakerAPI.apply(ActionGasGeneratorRecipes.Remove(fluid))
    }

    @JvmStatic
    @ZenMethod
    fun removeHydroponicFarmRecipe(seed: IItemStack?) {
        CraftTweakerAPI.apply(ActionHydroponicRecipes.Remove(seed))
    }

    @JvmStatic
    @ZenMethod
    fun removeRecyclerRecipe(stack: IItemStack?) {
        CraftTweakerAPI.apply(ActionRecyclerRecipes.Remove(stack))
    }

    @JvmStatic
    @ZenMethod
    fun removeRocketAssemblyRecipe(output: IItemStack?) {
        CraftTweakerAPI.apply(ActionRocketAssemblerRecipes.Remove(output))
    }
}
