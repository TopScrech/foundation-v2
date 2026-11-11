package micdoodle8.mods.galacticraft.planets.venus.recipe

import foundation.item.FoundationItemVariants
import java.util.Arrays

import micdoodle8.mods.galacticraft.api.recipe.CircuitFabricatorRecipes
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.planets.venus.VenusBlocks
import micdoodle8.mods.galacticraft.planets.venus.VenusItems
import net.minecraft.world.item.Items
import net.minecraft.world.item.ItemStack
import net.minecraft.item.crafting.FurnaceRecipes
import net.minecraftforge.oredict.OreDictionary
import java.util.*

object RecipeManagerVenus {
    @JvmStatic
    fun loadCompatibilityRecipes() {
    }

    @JvmStatic
    fun addUniversalRecipes() {
        OreDictionary.registerOre("ingotLead", FoundationItemVariants.stack(requireNotNull(VenusItems.basicItem), 1, 1))
        OreDictionary.registerOre("waferSolarThin", FoundationItemVariants.stack(requireNotNull(VenusItems.basicItem), 1, 6))

        // Smelting
        FurnaceRecipes.instance()
            .addSmeltingRecipe(ItemStack(VenusBlocks.venusBlock, 1, 6), FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 5), 0.5f)
        FurnaceRecipes.instance()
            .addSmeltingRecipe(ItemStack(VenusBlocks.venusBlock, 1, 7), FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 3), 0.5f)
        FurnaceRecipes.instance()
            .addSmeltingRecipe(ItemStack(VenusBlocks.venusBlock, 1, 8), FoundationItemVariants.stack(requireNotNull(VenusItems.basicItem), 1, 1), 1.0f)
        FurnaceRecipes.instance()
            .addSmeltingRecipe(ItemStack(VenusBlocks.venusBlock, 1, 9), ItemStack(Items.QUARTZ), 0.2f)
        FurnaceRecipes.instance()
            .addSmeltingRecipe(ItemStack(VenusBlocks.venusBlock, 1, 11), FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 4), 0.5f)

        val solarPanels: ItemStack = FoundationItemVariants.stack(requireNotNull(VenusItems.basicItem), 3, 6)
        val silicon: ItemStack = FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 2)
        CircuitFabricatorRecipes.addRecipe(
            solarPanels,
            Arrays.asList<Any?>(
                ItemStack(Items.DIAMOND),
                silicon,
                silicon,
                ItemStack(Items.REDSTONE),
                FoundationItemVariants.stack(requireNotNull(VenusItems.basicItem), 1, 4)
            )
        )
    }
}
