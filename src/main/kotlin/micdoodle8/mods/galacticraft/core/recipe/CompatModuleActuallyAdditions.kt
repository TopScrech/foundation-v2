package micdoodle8.mods.galacticraft.core.recipe

import de.ellpeck.actuallyadditions.api.ActuallyAdditionsAPI
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.planets.mars.blocks.MarsBlocks
import micdoodle8.mods.galacticraft.planets.mars.items.MarsItems
import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary
import java.util.ArrayList

object CompatModuleActuallyAdditions {
    @JvmStatic
    fun addRecipes() {
        ActuallyAdditionsAPI.addCrusherRecipes(
            OreDictionary.getOres("oreAluminum"),
            OreDictionary.getOres("dustAluminum"),
            2,
            ArrayList<ItemStack?>(0),
            0,
            0
        )
        // Silicon ore :- gives 5 silicon  (normally 1-5 depending on fortune enchantment)
        ActuallyAdditionsAPI.addCrusherRecipe(
            ItemStack(GCBlocks.basicBlock, 1, 8),
            ItemStack(GCItems.basicItem, 5, 2),
            ItemStack.EMPTY,
            0
        )
        // Cheese ore :- gives 2 cheese curd (normally 1, fortune enchantment gives chance of 2)
        ActuallyAdditionsAPI.addCrusherRecipe(
            ItemStack(GCBlocks.blockMoon, 1, 2),
            ItemStack(GCItems.cheeseCurd, 2, 0),
            ItemStack.EMPTY,
            0
        )
        if (GalacticraftCore.isPlanetsLoaded) {
            // Desh ore :- gives 2 raw desh (normally 1, fortune enchantment gives chance of 2)
            ActuallyAdditionsAPI.addCrusherRecipe(
                ItemStack(MarsBlocks.marsBlock, 1, 2),
                ItemStack(MarsItems.marsItemBasic, 2, 0),
                ItemStack.EMPTY,
                0
            )
        }
    }
}
