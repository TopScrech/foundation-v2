package galaxyspace.systems.SolarSystem.planets.overworld.recipes.schematic

import micdoodle8.mods.galacticraft.core.items.ItemOxygenTank

import foundation.item.FoundationItemVariants
import galaxyspace.core.GSItems
import galaxyspace.core.util.GSRecipeUtil
import galaxyspace.core.GSBlocks
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.recipe.NasaWorkbenchRecipe
import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems
import net.minecraft.world.item.ItemStack
import java.util.HashMap

object SchematicConeRecipe {
    @JvmStatic
    fun registerRecipeWorkBench() {
        for (k: Int in 0..3) {
            val plate: ItemStack
            if (k == 0) plate = FoundationItemVariants.stack(requireNotNull(AsteroidsItems.basicItem), 1, 5)
            else plate = FoundationItemVariants.stack(GSItems.HDP, 1, k - 1)

            val input: HashMap<Int, ItemStack?> = HashMap<Int, ItemStack?>()
            input.put(1, plate)
            input.put(2, plate)
            input.put(3, ItemStack(GSBlocks.FUTURE_GLASS_BASIC, 1, 0))
            input.put(4, plate)
            input.put(5, plate)
            input.put(6, plate)
            input.put(7, plate)
            input.put(8, plate)
            input.put(9, ItemStack(GCItems.oxygenVent, 1, 0))
            input.put(10, ItemStack(GCItems.oxygenFan, 1, 0))
            input.put(11, ItemStack(GCBlocks.sealableBlock, 1, 14))
            input.put(12, ItemStack(GCItems.rocketEngine, 1, 0))
            input.put(13, ItemStack(GCBlocks.screen, 1, 0))
            input.put(14, ItemStack(GCItems.partBuggy, 1, 1))
            input.put(15, ItemOxygenTank.stack(requireNotNull(GCItems.oxTankHeavy), 1, 0))
            input.put(16, FoundationItemVariants.stack(GSItems.ROCKET_MODULES, 1, 2))
            input.put(17, ItemStack(GCItems.oxygenVent, 1, 0))
            input.put(18, ItemStack(GCItems.oxygenFan, 1, 0))
            input.put(19, ItemStack(GCBlocks.sealableBlock, 1, 14))
            input.put(20, ItemStack(GCItems.rocketEngine, 1, 0))
            input.put(21, plate)
            input.put(22, plate)
            input.put(23, plate)
            input.put(24, plate)
            GSRecipeUtil.addConeRecipe(NasaWorkbenchRecipe(FoundationItemVariants.stack(GSItems.ROCKET_PARTS, 1, k * 5), input))
        }
    }
}
