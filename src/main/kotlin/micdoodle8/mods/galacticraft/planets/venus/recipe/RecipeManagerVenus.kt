package micdoodle8.mods.galacticraft.planets.venus.recipe

import java.util.Arrays

import micdoodle8.mods.galacticraft.api.recipe.CircuitFabricatorRecipes
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.planets.venus.VenusBlocks
import micdoodle8.mods.galacticraft.planets.venus.VenusItems
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.FurnaceRecipes
import net.minecraftforge.oredict.OreDictionary
import java.util.*

object RecipeManagerVenus {
    @JvmStatic
    fun loadCompatibilityRecipes() {
    }

    @JvmStatic
    fun addUniversalRecipes() {
        OreDictionary.registerOre("ingotLead", ItemStack(VenusItems.basicItem, 1, 1))
        OreDictionary.registerOre("waferSolarThin", ItemStack(VenusItems.basicItem, 1, 6))

        // Smelting
        FurnaceRecipes.instance()
            .addSmeltingRecipe(ItemStack(VenusBlocks.venusBlock, 1, 6), ItemStack(GCItems.basicItem, 1, 5), 0.5f)
        FurnaceRecipes.instance()
            .addSmeltingRecipe(ItemStack(VenusBlocks.venusBlock, 1, 7), ItemStack(GCItems.basicItem, 1, 3), 0.5f)
        FurnaceRecipes.instance()
            .addSmeltingRecipe(ItemStack(VenusBlocks.venusBlock, 1, 8), ItemStack(VenusItems.basicItem, 1, 1), 1.0f)
        FurnaceRecipes.instance()
            .addSmeltingRecipe(ItemStack(VenusBlocks.venusBlock, 1, 9), ItemStack(Items.QUARTZ), 0.2f)
        FurnaceRecipes.instance()
            .addSmeltingRecipe(ItemStack(VenusBlocks.venusBlock, 1, 11), ItemStack(GCItems.basicItem, 1, 4), 0.5f)

        val solarPanels: ItemStack = ItemStack(VenusItems.basicItem, 3, 6)
        val silicon: ItemStack = ItemStack(GCItems.basicItem, 1, 2)
        CircuitFabricatorRecipes.addRecipe(
            solarPanels,
            Arrays.asList<Any?>(
                ItemStack(Items.DIAMOND),
                silicon,
                silicon,
                ItemStack(Items.REDSTONE),
                ItemStack(VenusItems.basicItem, 1, 4)
            )
        )
    }
}
