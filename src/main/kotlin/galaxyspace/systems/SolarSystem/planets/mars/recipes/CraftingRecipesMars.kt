package galaxyspace.systems.SolarSystem.planets.mars.recipes

import foundation.item.FoundationItemVariants
import galaxyspace.core.GSBlocks
import galaxyspace.systems.SolarSystem.planets.mars.recipes.CraftingRecipesMars
import galaxyspace.systems.SolarSystem.planets.overworld.recipes.RecyclerRecipes
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.planets.mars.blocks.MarsBlocks
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.item.Items
import net.minecraft.world.item.ItemStack
import net.minecraftforge.fml.common.registry.GameRegistry

object CraftingRecipesMars {
    @JvmStatic
    fun loadRecipes() {
        addBlockSmelting()
    }

    @JvmStatic
    private fun addBlockSmelting() {
        GameRegistry.addSmelting(ItemStack(GSBlocks.MARS_ORES, 1, 1), ItemStack(Items.GOLD_INGOT, 1, 0), 1.0f)
        GameRegistry.addSmelting(ItemStack(GSBlocks.MARS_ORES, 1, 5), FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 5), 1.0f)

        RecyclerRecipes.recycling()
            .addNewRecipe(ItemStack(MarsBlocks.marsBlock, 1, 4), ItemStack(Blocks.COBBLESTONE, 1, 0), null)
        RecyclerRecipes.recycling()
            .addNewRecipe(ItemStack(MarsBlocks.marsBlock, 1, 9), ItemStack(Blocks.STONE, 1, 0), null)
    }
}
