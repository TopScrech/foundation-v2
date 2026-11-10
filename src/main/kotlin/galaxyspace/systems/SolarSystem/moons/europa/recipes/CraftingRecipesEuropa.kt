package galaxyspace.systems.SolarSystem.moons.europa.recipes

import galaxyspace.core.GSBlocks
import galaxyspace.systems.SolarSystem.moons.europa.recipes.CraftingRecipesEuropa
import micdoodle8.mods.galacticraft.core.GCItems
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.registry.GameRegistry

object CraftingRecipesEuropa {
    @JvmStatic
    fun loadRecipes() {
        addBlockSmelting()
    }

    @JvmStatic
    private fun addBlockSmelting() {
        GameRegistry.addSmelting(ItemStack(GSBlocks.EUROPA_BLOCKS, 1, 5), ItemStack(GCItems.basicItem, 1, 5), 1.0f)
    }
}
