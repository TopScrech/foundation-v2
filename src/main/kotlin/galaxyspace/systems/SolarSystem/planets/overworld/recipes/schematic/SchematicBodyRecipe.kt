package galaxyspace.systems.SolarSystem.planets.overworld.recipes.schematic

import foundation.item.FoundationItemVariants
import galaxyspace.core.GSItems
import galaxyspace.core.util.GSRecipeUtil
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.recipe.NasaWorkbenchRecipe
import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import java.util.HashMap

object SchematicBodyRecipe {
    @JvmStatic
    fun registerRecipeWorkBench() {
        for (k: Int in 0..3) {
            val input: HashMap<Int, ItemStack?> = HashMap<Int, ItemStack?>()

            val plate: ItemStack
            if (k == 0) plate = FoundationItemVariants.stack(requireNotNull(AsteroidsItems.basicItem), 1, 5)
            else plate = FoundationItemVariants.stack(GSItems.HDP, 1, k - 1)

            input.put(1, plate)
            input.put(2, plate)
            input.put(3, ItemStack(Item.getItemFromBlock(GCBlocks.spinThruster), 1, 0))
            input.put(4, plate)
            input.put(5, plate)
            input.put(6, plate)
            input.put(7, FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 20))
            input.put(8, FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 19))
            input.put(9, plate)
            input.put(10, FoundationItemVariants.stack(GSItems.ROCKET_MODULES, 1, 3))
            input.put(11, FoundationItemVariants.stack(GSItems.ROCKET_MODULES, 1, 3))

            if (k == 0) input.put(12, ItemStack(GCItems.rocketEngine, 1, 0))
            else input.put(12, FoundationItemVariants.stack(requireNotNull(AsteroidsItems.basicItem), 1, 1))

            input.put(13, FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 14))
            input.put(14, ItemStack(GCItems.oxygenConcentrator, 1, 0))
            input.put(15, plate)
            input.put(16, FoundationItemVariants.stack(GSItems.ROCKET_MODULES, 1, 3))
            input.put(17, FoundationItemVariants.stack(GSItems.ROCKET_MODULES, 1, 3))

            if (k == 0) input.put(18, ItemStack(GCItems.rocketEngine, 1, 0))
            else input.put(18, FoundationItemVariants.stack(requireNotNull(AsteroidsItems.basicItem), 1, 1))

            input.put(19, plate)
            input.put(20, plate)
            input.put(21, ItemStack(Item.getItemFromBlock(GCBlocks.spinThruster), 1, 0))
            input.put(22, plate)
            input.put(23, plate)
            input.put(24, plate)
            GSRecipeUtil.addBodyRecipe(NasaWorkbenchRecipe(FoundationItemVariants.stack(GSItems.ROCKET_PARTS, 1, k * 5 + 1), input))
        }
    }
}
