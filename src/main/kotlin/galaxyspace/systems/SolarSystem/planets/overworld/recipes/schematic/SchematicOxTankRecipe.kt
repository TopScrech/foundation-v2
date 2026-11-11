package galaxyspace.systems.SolarSystem.planets.overworld.recipes.schematic

import micdoodle8.mods.galacticraft.core.items.ItemOxygenTank

import foundation.item.FoundationItemVariants
import galaxyspace.core.GSItems
import galaxyspace.core.util.GSRecipeUtil
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.recipe.NasaWorkbenchRecipe
import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.item.ItemStack
import java.util.HashMap

object SchematicOxTankRecipe {
    @JvmStatic
    fun registerRecipeWorkBench() {
        val input: HashMap<Int, ItemStack?> = HashMap<Int, ItemStack?>()
        input.put(1, ItemStack(Blocks.WOOL, 1, 14))
        input.put(2, FoundationItemVariants.stack(requireNotNull(AsteroidsItems.basicItem), 1, 6))
        input.put(3, FoundationItemVariants.stack(requireNotNull(AsteroidsItems.basicItem), 1, 6))
        input.put(4, FoundationItemVariants.stack(requireNotNull(AsteroidsItems.basicItem), 1, 6))
        input.put(5, ItemStack(GCBlocks.oxygenPipe, 1, 0))
        input.put(6, ItemStack(GCItems.oxygenConcentrator, 1, 0))
        input.put(7, ItemOxygenTank.stack(requireNotNull(GCItems.oxTankLight), 1, ItemOxygenTank.capacityOf(requireNotNull(GCItems.oxTankLight))))
        input.put(8, FoundationItemVariants.stack(requireNotNull(AsteroidsItems.basicItem), 1, 6))
        input.put(9, ItemStack(Blocks.WOOL, 1, 14))
        input.put(10, FoundationItemVariants.stack(requireNotNull(AsteroidsItems.basicItem), 1, 6))
        input.put(11, FoundationItemVariants.stack(requireNotNull(AsteroidsItems.basicItem), 1, 6))
        input.put(12, FoundationItemVariants.stack(requireNotNull(AsteroidsItems.basicItem), 1, 6))
        GSRecipeUtil.addOxTankRecipe(
            NasaWorkbenchRecipe(
                ItemOxygenTank.stack(requireNotNull(GSItems.OXYGENTANK_TIER_EPP), 1, ItemOxygenTank.capacityOf(requireNotNull(GSItems.OXYGENTANK_TIER_EPP))), input
            )
        )
    }
}
