package galaxyspace.systems.BarnardsSystem.moons.barnarda_c1.recipes

import galaxyspace.systems.SolarSystem.planets.overworld.recipes.RecyclerRecipes

import galaxyspace.systems.BarnardsSystem.core.BRBlocks
import galaxyspace.systems.BarnardsSystem.core.BRItems
import galaxyspace.systems.BarnardsSystem.moons.barnarda_c1.recipes.CraftingRecipesBarnarda_C1
import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemBasicGS
import micdoodle8.mods.galacticraft.api.recipe.CircuitFabricatorRecipes
import micdoodle8.mods.galacticraft.core.GCItems
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.registry.GameRegistry
import java.util.Arrays

object CraftingRecipesBarnarda_C1 {
    @JvmStatic
    fun loadRecipes() {
        addBlockRecipes()
        addItemRecipes()
        addBlockSmelting()
        addCircuitFabricator()
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
        GameRegistry.addSmelting(ItemStack(BRBlocks.BARNARDA_C1_BLOCKS, 1, 5), ItemStack(Items.IRON_INGOT, 1, 0), 1.0f)
        GameRegistry.addSmelting(ItemStack(BRBlocks.BARNARDA_C1_BLOCKS, 1, 6), ItemStack(GCItems.basicItem, 1, 3), 1.0f)
        GameRegistry.addSmelting(ItemStack(BRBlocks.BARNARDA_C1_BLOCKS, 1, 7), ItemStack(GCItems.basicItem, 1, 4), 1.0f)
    }

    @JvmStatic
    private fun addCircuitFabricator() {
        val modernWafers: ItemStack = ItemBasicGS.BasicItems.WAFER_MODERN.getItemStack()

        CircuitFabricatorRecipes.addRecipe(
            modernWafers,
            Arrays.asList<Any?>(
                ItemStack(Items.DIAMOND),
                ItemStack(BRItems.BASIC, 1, 3),
                ItemStack(BRItems.BASIC, 1, 3),
                ItemStack(Items.REDSTONE),
                ItemStack(GCItems.basicItem, 1, 14)
            )
        )
    }

    @JvmStatic
    private fun addRecycler() {
        //RecyclerRecipes.recycling().addNewRecipe(new ItemStack(BRItems.BASIC, 1, 3),  new ItemStack(GCItems.itemBasicMoon, 1, 2), null);
    }
}
