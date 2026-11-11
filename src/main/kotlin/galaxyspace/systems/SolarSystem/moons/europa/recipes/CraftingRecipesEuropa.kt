package galaxyspace.systems.SolarSystem.moons.europa.recipes

import foundation.item.FoundationItemVariants
import galaxyspace.core.GSBlocks
import galaxyspace.systems.SolarSystem.moons.europa.recipes.CraftingRecipesEuropa
import micdoodle8.mods.galacticraft.core.GCItems
import net.minecraft.world.item.ItemStack
import net.minecraftforge.fml.common.registry.GameRegistry

object CraftingRecipesEuropa {
    @JvmStatic
    fun loadRecipes() {
        addBlockSmelting()
    }

    @JvmStatic
    private fun addBlockSmelting() {
        GameRegistry.addSmelting(ItemStack(GSBlocks.EUROPA_BLOCKS, 1, 5), FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 5), 1.0f)
    }
}
