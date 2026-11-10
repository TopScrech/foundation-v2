package galaxyspace.systems.SolarSystem.planets.overworld.recipes.schematic

import galaxyspace.core.GSItems
import galaxyspace.core.util.GSRecipeUtil
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.recipe.NasaWorkbenchRecipe
import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import java.util.HashMap

object SchematicOxTankRecipe {
    @JvmStatic
    fun registerRecipeWorkBench() {
        val input: HashMap<Int, ItemStack?> = HashMap<Int, ItemStack?>()
        input.put(1, ItemStack(Blocks.WOOL, 1, 14))
        input.put(2, ItemStack(AsteroidsItems.basicItem, 1, 6))
        input.put(3, ItemStack(AsteroidsItems.basicItem, 1, 6))
        input.put(4, ItemStack(AsteroidsItems.basicItem, 1, 6))
        input.put(5, ItemStack(GCBlocks.oxygenPipe, 1, 0))
        input.put(6, ItemStack(GCItems.oxygenConcentrator, 1, 0))
        input.put(7, ItemStack(GCItems.oxTankLight, 1, (GCItems.oxTankLight)!!.getMaxDamage()))
        input.put(8, ItemStack(AsteroidsItems.basicItem, 1, 6))
        input.put(9, ItemStack(Blocks.WOOL, 1, 14))
        input.put(10, ItemStack(AsteroidsItems.basicItem, 1, 6))
        input.put(11, ItemStack(AsteroidsItems.basicItem, 1, 6))
        input.put(12, ItemStack(AsteroidsItems.basicItem, 1, 6))
        GSRecipeUtil.addOxTankRecipe(
            NasaWorkbenchRecipe(
                ItemStack(
                    GSItems.OXYGENTANK_TIER_EPP,
                    1,
                    GSItems.OXYGENTANK_TIER_EPP.getMaxDamage()
                ), input
            )
        )
    }
}
