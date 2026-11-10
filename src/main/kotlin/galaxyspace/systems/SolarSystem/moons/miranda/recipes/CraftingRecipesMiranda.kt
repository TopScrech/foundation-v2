package galaxyspace.systems.SolarSystem.moons.miranda.recipes

import galaxyspace.core.GSBlocks
import galaxyspace.core.GSItems
import galaxyspace.systems.SolarSystem.moons.miranda.recipes.CraftingRecipesMiranda
import galaxyspace.systems.SolarSystem.planets.overworld.recipes.RecyclerRecipes
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fml.common.registry.GameRegistry

object CraftingRecipesMiranda {
    @JvmStatic
    fun loadRecipes() {
        addBlockSmelting()
        addRecycler()
    }

    @JvmStatic
    private fun addBlockSmelting() {
        GameRegistry.addSmelting(ItemStack(GSBlocks.MIRANDA_BLOCKS, 1, 3), ItemStack(Items.IRON_INGOT, 1, 0), 1.0f)
        GameRegistry.addSmelting(ItemStack(GSBlocks.MIRANDA_BLOCKS, 1, 5), ItemStack(Items.DIAMOND, 1, 0), 1.0f)
        GameRegistry.addSmelting(ItemStack(GSBlocks.MIRANDA_BLOCKS, 1, 6), ItemStack(Items.QUARTZ, 1, 0), 1.0f)
        GameRegistry.addSmelting(ItemStack(GSBlocks.MIRANDA_BLOCKS, 1, 7), ItemStack(GSItems.INGOTS, 1, 0), 1.0f)
        GameRegistry.addSmelting(ItemStack(GSBlocks.MIRANDA_BLOCKS, 1, 8), ItemStack(GSItems.INGOTS, 1, 2), 1.0f)
    }

    @JvmStatic
    private fun addRecycler() {
        RecyclerRecipes.recycling().addNewRecipe(
            ItemStack(GSBlocks.MIRANDA_BLOCKS, 1, 15),
            ItemStack(Items.COAL, 1, 0),
            50,
            FluidStack(FluidRegistry.WATER, 10)
        )
    }
}
