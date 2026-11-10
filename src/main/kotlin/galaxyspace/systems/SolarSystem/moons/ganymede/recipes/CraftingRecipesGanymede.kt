package galaxyspace.systems.SolarSystem.moons.ganymede.recipes

import galaxyspace.core.GSBlocks
import galaxyspace.systems.SolarSystem.moons.ganymede.recipes.CraftingRecipesGanymede
import galaxyspace.core.GSItems
import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.registry.GameRegistry

object CraftingRecipesGanymede {
    @JvmStatic
    fun loadRecipes() {
        addBlockSmelting()
    }

    @JvmStatic
    private fun addBlockSmelting() {
        GameRegistry.addSmelting(ItemStack(GSBlocks.GANYMEDE_BLOCKS, 1, 2), ItemStack(GSItems.INGOTS, 1, 1), 1.0f)
        GameRegistry.addSmelting(
            ItemStack(GSBlocks.GANYMEDE_BLOCKS, 1, 3),
            ItemStack(AsteroidsItems.basicItem, 1, 0),
            1.0f
        )
    }
}
