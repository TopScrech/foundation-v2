package galaxyspace.systems.SolarSystem.moons.phobos.recipes

import foundation.item.FoundationItemVariants
import galaxyspace.core.GSBlocks
import galaxyspace.systems.SolarSystem.moons.phobos.recipes.CraftingRecipesPhobos
import galaxyspace.core.GSItems
import galaxyspace.systems.SolarSystem.planets.overworld.recipes.RecyclerRecipes
import galaxyspace.core.GSFluids
import net.minecraft.world.item.Items
import net.minecraft.world.item.ItemStack
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fml.common.registry.GameRegistry

object CraftingRecipesPhobos {
    @JvmStatic
    fun loadRecipes() {
        addBlockSmelting()
        addRecycler()
    }

    @JvmStatic
    private fun addBlockSmelting() {
        GameRegistry.addSmelting(ItemStack(GSBlocks.PHOBOS_BLOCKS, 1, 2), ItemStack(Items.IRON_INGOT, 1, 0), 1.0f)
        GameRegistry.addSmelting(ItemStack(GSBlocks.PHOBOS_BLOCKS, 1, 4), FoundationItemVariants.stack(GSItems.INGOTS, 1, 2), 1.0f)
    }

    @JvmStatic
    private fun addRecycler() {
        RecyclerRecipes.recycling()
            .addNewRecipe(ItemStack(GSBlocks.PHOBOS_BLOCKS, 1, 0), ItemStack.EMPTY, FluidStack(GSFluids.Helium3, 10))
    }
}
