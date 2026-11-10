package galaxyspace.systems.BarnardsSystem.planets.barnarda_c.recipes

import galaxyspace.systems.BarnardsSystem.core.BRBlocks
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.recipes.CraftingRecipesBarnarda_C
import galaxyspace.core.GSItems
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.util.RecipeUtil
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.registry.GameRegistry

object CraftingRecipesBarnarda_C {
    @JvmStatic
    fun loadRecipes() {
        addBlockRecipes()
        addItemRecipes()
        addBlockSmelting()
    }

    @JvmStatic
    private fun addBlockRecipes() {
        RecipeUtil.addRecipe(
            ItemStack(BRBlocks.BARNARDA_C_BLOCKS, 1, 5),
            (arrayOf<Any>("XX", "XX", 'X', ItemStack(BRBlocks.BARNARDA_C_BLOCKS, 1, 1)) as Array<Any?>)
        )
        RecipeUtil.addRecipe(
            ItemStack(BRBlocks.BARNARDA_C_BLOCKS, 4, 6),
            (arrayOf<Any>("X", 'X', ItemStack(BRBlocks.BARNARDA_C_VIOLET_LOG, 1, 0)) as Array<Any?>)
        )
    }

    @JvmStatic
    private fun addItemRecipes() {
    }

    @JvmStatic
    private fun addBlockSmelting() {
        GameRegistry.addSmelting(ItemStack(BRBlocks.BARNARDA_C_ORES, 1, 1), ItemStack(Items.IRON_INGOT, 1, 0), 1.0f)
        GameRegistry.addSmelting(ItemStack(BRBlocks.BARNARDA_C_ORES, 1, 2), ItemStack(Items.GOLD_INGOT, 1, 0), 1.0f)
        GameRegistry.addSmelting(ItemStack(BRBlocks.BARNARDA_C_ORES, 1, 7), ItemStack(GCItems.basicItem, 1, 3), 1.0f)
        GameRegistry.addSmelting(ItemStack(BRBlocks.BARNARDA_C_ORES, 1, 8), ItemStack(GCItems.basicItem, 1, 4), 1.0f)
        GameRegistry.addSmelting(ItemStack(BRBlocks.BARNARDA_C_ORES, 1, 9), ItemStack(GCItems.basicItem, 1, 5), 1.0f)
        GameRegistry.addSmelting(ItemStack(BRBlocks.BARNARDA_C_ORES, 1, 11), ItemStack(GSItems.INGOTS, 1, 0), 1.0f)
        GameRegistry.addSmelting(ItemStack(BRBlocks.BARNARDA_C_ORES, 1, 12), ItemStack(GSItems.INGOTS, 1, 2), 1.0f)

        GameRegistry.addSmelting(
            ItemStack(BRBlocks.BARNARDA_C_FALLING_BLOCKS, 1, 0),
            ItemStack(Blocks.GLASS, 1, 0),
            1.0f
        )
        GameRegistry.addSmelting(
            ItemStack(BRBlocks.BARNARDA_C_BLOCKS, 1, 4),
            ItemStack(BRBlocks.BARNARDA_C_BLOCKS, 1, 1),
            0.1f
        )

        GameRegistry.addSmelting(ItemStack(BRBlocks.BARNARDA_C_VIOLET_LOG, 1, 0), ItemStack(Items.COAL, 1, 1), 0.1f)
    }
}
