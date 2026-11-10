package galaxyspace.systems.ACentauriSystem.planets.proxima_b.recipes

import galaxyspace.core.GSItems
import galaxyspace.systems.ACentauriSystem.core.ACBlocks
import galaxyspace.systems.ACentauriSystem.planets.proxima_b.recipes.CraftingRecipesProximaB
import galaxyspace.systems.SolarSystem.planets.overworld.recipes.RecyclerRecipes
import micdoodle8.mods.galacticraft.core.GCItems
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fml.common.registry.GameRegistry

object CraftingRecipesProximaB {
    @JvmStatic
    fun loadRecipes() {
        addBlockRecipes()
        addItemRecipes()
        addBlockSmelting()
        addCompressor()
        addAssembly()
        addRecycler()
    }

    @JvmStatic
    private fun addBlockRecipes() {
    }

    @JvmStatic
    private fun addItemRecipes() {
    }

    @JvmStatic
    private fun addBlockSmelting() {
        GameRegistry.addSmelting(ItemStack(ACBlocks.PROXIMA_B_BLOCKS, 1, 5), ItemStack(Items.GOLD_INGOT, 1, 0), 1.0f)
        GameRegistry.addSmelting(ItemStack(ACBlocks.PROXIMA_B_BLOCKS, 1, 6), ItemStack(GCItems.basicItem, 1, 4), 1.0f)
        GameRegistry.addSmelting(ItemStack(ACBlocks.PROXIMA_B_BLOCKS, 1, 7), ItemStack(GCItems.basicItem, 1, 3), 1.0f)
    }

    @JvmStatic
    private fun addCompressor() {
    }

    @JvmStatic
    private fun addAssembly() {
    }

    @JvmStatic
    private fun addRecycler() {
        RecyclerRecipes.recycling().addNewRecipe(
            ItemStack(ACBlocks.PROXIMA_B_LOG_2, 1, 0),
            ItemStack(Blocks.LOG, 1, 0),
            FluidStack(FluidRegistry.WATER, 10)
        )
        RecyclerRecipes.recycling()
            .addNewRecipe(ItemStack(ACBlocks.PROXIMA_B_LOG_1, 1, 0), ItemStack(Items.COAL, 1, 1), 60, null)

        RecyclerRecipes.recycling()
            .addNewRecipe(ItemStack(ACBlocks.PROXIMA_B_BLOCKS, 1, 4), ItemStack(Items.FLINT, 1, 0), 20, null)
        RecyclerRecipes.recycling().addNewRecipe(
            ItemStack(ACBlocks.PROXIMA_B_BLOCKS, 1, 3),
            ItemStack(ACBlocks.PROXIMA_B_BLOCKS, 1, 0),
            FluidStack(FluidRegistry.WATER, 10)
        )
        RecyclerRecipes.recycling()
            .addNewRecipe(ItemStack(ACBlocks.PROXIMA_B_BLOCKS, 1, 0), ItemStack(GSItems.BASIC, 1, 6), 30, null)
    }
}
