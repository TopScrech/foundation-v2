package galaxyspace.systems.SolarSystem.planets.mercury.recipes

import galaxyspace.systems.SolarSystem.planets.mercury.recipes.CraftingRecipesMercury
import galaxyspace.core.GSBlocks
import galaxyspace.core.GSItems
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.registry.GameRegistry

object CraftingRecipesMercury {
    @JvmStatic
    fun loadRecipes() {
        addBlockSmelting()
    }

    @JvmStatic
    private fun addBlockSmelting() {
        GameRegistry.addSmelting(ItemStack(GSBlocks.MERCURY_BLOCKS, 1, 3), ItemStack(GSItems.INGOTS, 1, 2), 1.0f)
        GameRegistry.addSmelting(ItemStack(GSBlocks.MERCURY_BLOCKS, 1, 4), ItemStack(Items.IRON_INGOT, 1, 0), 1.0f)
        GameRegistry.addSmelting(ItemStack(GSBlocks.MERCURY_BLOCKS, 1, 5), ItemStack(GSItems.INGOTS, 1, 1), 1.0f)
    }
}
