package galaxyspace.systems.SolarSystem.planets.haumea.recipes

import foundation.item.FoundationItemVariants
import galaxyspace.core.GSBlocks
import galaxyspace.systems.SolarSystem.planets.haumea.recipes.CraftingRecipesHaumea
import micdoodle8.mods.galacticraft.core.GCItems
import net.minecraft.world.item.ItemStack
import net.minecraftforge.fml.common.registry.GameRegistry

object CraftingRecipesHaumea {
    @JvmStatic
    fun loadRecipes() {
        addBlockSmelting()
        addBlockRecipes()
    }

    @JvmStatic
    private fun addBlockRecipes() {
    }

    @JvmStatic
    private fun addBlockSmelting() {
        GameRegistry.addSmelting(ItemStack(GSBlocks.HAUMEA_BLOCKS, 1, 2), ItemStack(GSBlocks.HAUMEA_BLOCKS, 1, 1), 1.0f)
        GameRegistry.addSmelting(ItemStack(GSBlocks.HAUMEA_BLOCKS, 1, 3), FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 5), 1.0f)
    }
}
