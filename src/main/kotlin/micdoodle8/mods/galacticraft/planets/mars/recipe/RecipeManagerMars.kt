package micdoodle8.mods.galacticraft.planets.mars.recipe

import micdoodle8.mods.galacticraft.api.recipe.CompressorRecipes
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.planets.mars.blocks.MarsBlocks
import micdoodle8.mods.galacticraft.planets.mars.items.MarsItems
import micdoodle8.mods.galacticraft.planets.mars.util.MarsUtil
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.FurnaceRecipes
import net.minecraftforge.oredict.OreDictionary
import java.util.HashMap

object RecipeManagerMars {
    @JvmStatic
    fun loadCompatibilityRecipes() {
    }

    @JvmStatic
    fun addUniversalRecipes() {
        OreDictionary.registerOre("ingotDesh", ItemStack(MarsItems.marsItemBasic, 1, 2))
        OreDictionary.registerOre("compressedDesh", ItemStack(MarsItems.marsItemBasic, 1, 5))

        CompressorRecipes.addShapelessRecipe(
            ItemStack(MarsItems.marsItemBasic, 1, 3),
            ItemStack(GCItems.heavyPlatingTier1),
            ItemStack(GCItems.itemBasicMoon, 1, 1)
        )
        CompressorRecipes.addShapelessRecipe(
            ItemStack(MarsItems.marsItemBasic, 1, 5),
            ItemStack(MarsItems.marsItemBasic, 1, 2)
        )

        // Smelting
        FurnaceRecipes.instance()
            .addSmeltingRecipe(ItemStack(MarsItems.marsItemBasic, 1, 0), ItemStack(MarsItems.marsItemBasic, 1, 2), 0.2f)
        FurnaceRecipes.instance()
            .addSmeltingRecipe(ItemStack(MarsBlocks.marsBlock, 1, 4), ItemStack(MarsBlocks.marsBlock, 1, 9), 0.0f)
        FurnaceRecipes.instance()
            .addSmeltingRecipe(ItemStack(MarsBlocks.marsBlock, 1, 0), ItemStack(GCItems.basicItem, 1, 3), 1.0f)
        FurnaceRecipes.instance()
            .addSmeltingRecipe(ItemStack(MarsBlocks.marsBlock, 1, 1), ItemStack(GCItems.basicItem, 1, 4), 1.0f)
        FurnaceRecipes.instance()
            .addSmeltingRecipe(ItemStack(MarsBlocks.marsBlock, 1, 2), ItemStack(MarsItems.marsItemBasic, 1, 2), 0.2f)
        FurnaceRecipes.instance()
            .addSmeltingRecipe(ItemStack(MarsBlocks.marsBlock, 1, 3), ItemStack(Items.IRON_INGOT), 0.2f)

        // Schematic
        var input: HashMap<Int, ItemStack?> = HashMap<Int, ItemStack?>()
        input.put(1, ItemStack(GCItems.partNoseCone))
        input.put(2, ItemStack(MarsItems.marsItemBasic, 1, 3))
        input.put(3, ItemStack(MarsItems.marsItemBasic, 1, 3))
        input.put(4, ItemStack(MarsItems.marsItemBasic, 1, 3))
        input.put(5, ItemStack(MarsItems.marsItemBasic, 1, 3))
        input.put(6, ItemStack(MarsItems.marsItemBasic, 1, 3))
        input.put(7, ItemStack(MarsItems.marsItemBasic, 1, 3))
        input.put(8, ItemStack(MarsItems.marsItemBasic, 1, 3))
        input.put(9, ItemStack(MarsItems.marsItemBasic, 1, 3))
        input.put(10, ItemStack(MarsItems.marsItemBasic, 1, 3))
        input.put(11, ItemStack(MarsItems.marsItemBasic, 1, 3))
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
        MarsUtil.addRocketBenchT2Recipe(ItemStack(MarsItems.rocketMars, 1, 0), input)

        var input2: HashMap<Int, ItemStack?> = HashMap<Int, ItemStack?>(input)
        input2.put(19, ItemStack(Blocks.CHEST))
        input2.put(20, ItemStack.EMPTY)
        input2.put(21, ItemStack.EMPTY)
        MarsUtil.addRocketBenchT2Recipe(ItemStack(MarsItems.rocketMars, 1, 1), input2)

        input2 = HashMap<Int, ItemStack?>(input)
        input2.put(19, ItemStack.EMPTY)
        input2.put(20, ItemStack(Blocks.CHEST))
        input2.put(21, ItemStack.EMPTY)
        MarsUtil.addRocketBenchT2Recipe(ItemStack(MarsItems.rocketMars, 1, 1), input2)

        input2 = HashMap<Int, ItemStack?>(input)
        input2.put(19, ItemStack.EMPTY)
        input2.put(20, ItemStack.EMPTY)
        input2.put(21, ItemStack(Blocks.CHEST))
        MarsUtil.addRocketBenchT2Recipe(ItemStack(MarsItems.rocketMars, 1, 1), input2)

        input2 = HashMap<Int, ItemStack?>(input)
        input2.put(19, ItemStack(Blocks.CHEST))
        input2.put(20, ItemStack(Blocks.CHEST))
        input2.put(21, ItemStack.EMPTY)
        MarsUtil.addRocketBenchT2Recipe(ItemStack(MarsItems.rocketMars, 1, 2), input2)

        input2 = HashMap<Int, ItemStack?>(input)
        input2.put(19, ItemStack(Blocks.CHEST))
        input2.put(20, ItemStack.EMPTY)
        input2.put(21, ItemStack(Blocks.CHEST))
        MarsUtil.addRocketBenchT2Recipe(ItemStack(MarsItems.rocketMars, 1, 2), input2)

        input2 = HashMap<Int, ItemStack?>(input)
        input2.put(19, ItemStack.EMPTY)
        input2.put(20, ItemStack(Blocks.CHEST))
        input2.put(21, ItemStack(Blocks.CHEST))
        MarsUtil.addRocketBenchT2Recipe(ItemStack(MarsItems.rocketMars, 1, 2), input2)

        input2 = HashMap<Int, ItemStack?>(input)
        input2.put(19, ItemStack(Blocks.CHEST))
        input2.put(20, ItemStack(Blocks.CHEST))
        input2.put(21, ItemStack(Blocks.CHEST))
        MarsUtil.addRocketBenchT2Recipe(ItemStack(MarsItems.rocketMars, 1, 3), input2)

        //
        input = HashMap<Int, ItemStack?>()
        input.put(1, ItemStack(GCItems.partNoseCone))
        input.put(2, ItemStack(GCItems.basicItem, 1, 14))
        input.put(3, ItemStack(MarsItems.marsItemBasic, 1, 3))
        input.put(4, ItemStack(MarsItems.marsItemBasic, 1, 3))
        input.put(5, ItemStack(MarsItems.marsItemBasic, 1, 3))
        input.put(6, ItemStack(MarsItems.marsItemBasic, 1, 3))
        input.put(7, ItemStack(MarsItems.marsItemBasic, 1, 3))
        input.put(8, ItemStack(MarsItems.marsItemBasic, 1, 3))
        input.put(9, ItemStack(GCItems.partFins))
        input.put(10, ItemStack(GCItems.partFins))
        input.put(11, ItemStack(GCItems.rocketEngine))
        input.put(12, ItemStack(GCItems.partFins))
        input.put(13, ItemStack(GCItems.partFins))

        input2 = HashMap<Int, ItemStack?>(input)
        input2.put(14, ItemStack(Blocks.CHEST))
        input2.put(15, ItemStack.EMPTY)
        input2.put(16, ItemStack.EMPTY)
        MarsUtil.adCargoRocketRecipe(ItemStack(MarsItems.rocketMars, 1, 11), input2)

        input2 = HashMap<Int, ItemStack?>(input)
        input2.put(14, ItemStack.EMPTY)
        input2.put(15, ItemStack(Blocks.CHEST))
        input2.put(16, ItemStack.EMPTY)
        MarsUtil.adCargoRocketRecipe(ItemStack(MarsItems.rocketMars, 1, 11), input2)

        input2 = HashMap<Int, ItemStack?>(input)
        input2.put(14, ItemStack.EMPTY)
        input2.put(15, ItemStack.EMPTY)
        input2.put(16, ItemStack(Blocks.CHEST))
        MarsUtil.adCargoRocketRecipe(ItemStack(MarsItems.rocketMars, 1, 11), input2)

        input2 = HashMap<Int, ItemStack?>(input)
        input2.put(14, ItemStack(Blocks.CHEST))
        input2.put(15, ItemStack(Blocks.CHEST))
        input2.put(16, ItemStack.EMPTY)
        MarsUtil.adCargoRocketRecipe(ItemStack(MarsItems.rocketMars, 1, 12), input2)

        input2 = HashMap<Int, ItemStack?>(input)
        input2.put(14, ItemStack(Blocks.CHEST))
        input2.put(15, ItemStack.EMPTY)
        input2.put(16, ItemStack(Blocks.CHEST))
        MarsUtil.adCargoRocketRecipe(ItemStack(MarsItems.rocketMars, 1, 12), input2)

        input2 = HashMap<Int, ItemStack?>(input)
        input2.put(14, ItemStack.EMPTY)
        input2.put(15, ItemStack(Blocks.CHEST))
        input2.put(16, ItemStack(Blocks.CHEST))
        MarsUtil.adCargoRocketRecipe(ItemStack(MarsItems.rocketMars, 1, 12), input2)

        input2 = HashMap<Int, ItemStack?>(input)
        input2.put(14, ItemStack(Blocks.CHEST))
        input2.put(15, ItemStack(Blocks.CHEST))
        input2.put(16, ItemStack(Blocks.CHEST))
        MarsUtil.adCargoRocketRecipe(ItemStack(MarsItems.rocketMars, 1, 13), input2)
    }
}
