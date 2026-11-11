package galaxyspace.systems.SolarSystem.planets.overworld.recipes.schematic

import foundation.item.FoundationItemVariants
import galaxyspace.core.GSItems
import galaxyspace.core.util.GSRecipeUtil
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.recipe.NasaWorkbenchRecipe
import micdoodle8.mods.galacticraft.planets.mars.items.MarsItems
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.item.ItemStack
import java.util.HashMap

object SchematicTier2Recipe {
    @JvmStatic
    fun registerRecipeWorkBench() {
        // Schematic
        val input: HashMap<Int, ItemStack?> = HashMap<Int, ItemStack?>()
        input.put(1, ItemStack(GCItems.partNoseCone))
        input.put(2, FoundationItemVariants.stack(requireNotNull(MarsItems.marsItemBasic), 1, 3))
        input.put(3, FoundationItemVariants.stack(requireNotNull(MarsItems.marsItemBasic), 1, 3))
        input.put(4, FoundationItemVariants.stack(requireNotNull(MarsItems.marsItemBasic), 1, 3))
        input.put(5, FoundationItemVariants.stack(requireNotNull(MarsItems.marsItemBasic), 1, 3))
        input.put(6, FoundationItemVariants.stack(requireNotNull(MarsItems.marsItemBasic), 1, 3))
        input.put(7, FoundationItemVariants.stack(requireNotNull(MarsItems.marsItemBasic), 1, 3))
        input.put(8, FoundationItemVariants.stack(requireNotNull(MarsItems.marsItemBasic), 1, 3))
        input.put(9, FoundationItemVariants.stack(requireNotNull(MarsItems.marsItemBasic), 1, 3))
        input.put(10, FoundationItemVariants.stack(requireNotNull(MarsItems.marsItemBasic), 1, 3))
        input.put(11, FoundationItemVariants.stack(requireNotNull(MarsItems.marsItemBasic), 1, 3))
        input.put(12, ItemStack(GCItems.rocketEngine, 1, 1))
        input.put(13, ItemStack(GCItems.partFins))
        input.put(14, ItemStack(GCItems.partFins))
        input.put(15, ItemStack(GCItems.rocketEngine))
        input.put(16, ItemStack(GCItems.rocketEngine, 1, 1))
        input.put(17, ItemStack(GCItems.partFins))
        input.put(18, ItemStack(GCItems.partFins))
        input.put(19, ItemStack.EMPTY)
        input.put(20, ItemStack.EMPTY)
        input.put(21, ItemStack.EMPTY)
        input.put(22, FoundationItemVariants.stack(GSItems.ROCKET_MODULES, 1, 1))
        input.put(23, FoundationItemVariants.stack(GSItems.ROCKET_MODULES, 1, 3))
        input.put(24, FoundationItemVariants.stack(GSItems.ROCKET_MODULES, 1, 3))
        input.put(25, ItemStack(GCItems.rocketTier1, 1, 0))
        GSRecipeUtil.addT2RocketRecipe(NasaWorkbenchRecipe(ItemStack(MarsItems.rocketMars, 1, 0), input))

        var input2: HashMap<Int, ItemStack?> = HashMap<Int, ItemStack?>(input)
        input2.put(25, ItemStack(GCItems.rocketTier1, 1, 1))
        GSRecipeUtil.addT2RocketRecipe(NasaWorkbenchRecipe(ItemStack(MarsItems.rocketMars, 1, 0), input2))

        input2 = HashMap<Int, ItemStack?>(input)
        input2.put(25, ItemStack(GCItems.rocketTier1, 1, 2))
        GSRecipeUtil.addT2RocketRecipe(NasaWorkbenchRecipe(ItemStack(MarsItems.rocketMars, 1, 0), input2))

        input2 = HashMap<Int, ItemStack?>(input)
        input2.put(25, ItemStack(GCItems.rocketTier1, 1, 3))
        GSRecipeUtil.addT2RocketRecipe(NasaWorkbenchRecipe(ItemStack(MarsItems.rocketMars, 1, 0), input2))

        for (i: Int in 0..3) {
            input2 = HashMap<Int, ItemStack?>(input)
            input2.put(19, ItemStack(Blocks.CHEST))
            input2.put(20, ItemStack.EMPTY)
            input2.put(21, ItemStack.EMPTY)
            input2.put(25, ItemStack(GCItems.rocketTier1, 1, i))
            GSRecipeUtil.addT2RocketRecipe(NasaWorkbenchRecipe(ItemStack(MarsItems.rocketMars, 1, 1), input2))

            input2 = HashMap<Int, ItemStack?>(input)
            input2.put(19, ItemStack.EMPTY)
            input2.put(20, ItemStack(Blocks.CHEST))
            input2.put(21, ItemStack.EMPTY)
            input2.put(25, ItemStack(GCItems.rocketTier1, 1, i))
            GSRecipeUtil.addT2RocketRecipe(NasaWorkbenchRecipe(ItemStack(MarsItems.rocketMars, 1, 1), input2))

            input2 = HashMap<Int, ItemStack?>(input)
            input2.put(19, ItemStack.EMPTY)
            input2.put(20, ItemStack.EMPTY)
            input2.put(21, ItemStack(Blocks.CHEST))
            input2.put(25, ItemStack(GCItems.rocketTier1, 1, i))
            GSRecipeUtil.addT2RocketRecipe(NasaWorkbenchRecipe(ItemStack(MarsItems.rocketMars, 1, 1), input2))

            input2 = HashMap<Int, ItemStack?>(input)
            input2.put(19, ItemStack(Blocks.CHEST))
            input2.put(20, ItemStack(Blocks.CHEST))
            input2.put(21, ItemStack.EMPTY)
            input2.put(25, ItemStack(GCItems.rocketTier1, 1, i))
            GSRecipeUtil.addT2RocketRecipe(NasaWorkbenchRecipe(ItemStack(MarsItems.rocketMars, 1, 2), input2))

            input2 = HashMap<Int, ItemStack?>(input)
            input2.put(19, ItemStack(Blocks.CHEST))
            input2.put(20, ItemStack.EMPTY)
            input2.put(21, ItemStack(Blocks.CHEST))
            input2.put(25, ItemStack(GCItems.rocketTier1, 1, i))
            GSRecipeUtil.addT2RocketRecipe(NasaWorkbenchRecipe(ItemStack(MarsItems.rocketMars, 1, 2), input2))

            input2 = HashMap<Int, ItemStack?>(input)
            input2.put(19, ItemStack.EMPTY)
            input2.put(20, ItemStack(Blocks.CHEST))
            input2.put(21, ItemStack(Blocks.CHEST))
            input2.put(25, ItemStack(GCItems.rocketTier1, 1, i))
            GSRecipeUtil.addT2RocketRecipe(NasaWorkbenchRecipe(ItemStack(MarsItems.rocketMars, 1, 2), input2))

            input2 = HashMap<Int, ItemStack?>(input)
            input2.put(19, ItemStack(Blocks.CHEST))
            input2.put(20, ItemStack(Blocks.CHEST))
            input2.put(21, ItemStack(Blocks.CHEST))
            input2.put(25, ItemStack(GCItems.rocketTier1, 1, i))
            GSRecipeUtil.addT2RocketRecipe(NasaWorkbenchRecipe(ItemStack(MarsItems.rocketMars, 1, 3), input2))
        }
    }
}
