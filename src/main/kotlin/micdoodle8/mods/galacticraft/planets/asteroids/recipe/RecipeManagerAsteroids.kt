package micdoodle8.mods.galacticraft.planets.asteroids.recipe

import micdoodle8.mods.galacticraft.api.GalacticraftRegistry
import micdoodle8.mods.galacticraft.api.recipe.CompressorRecipes
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.recipe.NasaWorkbenchRecipe
import micdoodle8.mods.galacticraft.core.util.RecipeUtil
import micdoodle8.mods.galacticraft.planets.asteroids.blocks.AsteroidBlocks
import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems
import micdoodle8.mods.galacticraft.planets.mars.items.MarsItems
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.FurnaceRecipes
import net.minecraft.item.crafting.Ingredient
import net.minecraft.util.NonNullList
import java.util.HashMap

object RecipeManagerAsteroids {
    @JvmStatic
    fun addUniversalRecipes() {
        val titaniumIngot: ItemStack = ItemStack(AsteroidsItems.basicItem, 1, 0)
        val platingTier3: ItemStack = ItemStack(AsteroidsItems.basicItem, 1, 5)

        FurnaceRecipes.instance()
            .addSmeltingRecipe(ItemStack(AsteroidBlocks.blockBasic, 1, 3), ItemStack(GCItems.basicItem, 1, 5), 0.5f)
        FurnaceRecipes.instance().addSmeltingRecipe(
            ItemStack(AsteroidBlocks.blockBasic, 1, 4),
            ItemStack(AsteroidsItems.basicItem, 1, 0),
            0.5f
        )
        FurnaceRecipes.instance()
            .addSmeltingRecipe(ItemStack(AsteroidBlocks.blockBasic, 1, 5), ItemStack(Items.IRON_INGOT), 0.5f)
        FurnaceRecipes.instance()
            .addSmeltingRecipe(ItemStack(AsteroidsItems.basicItem, 1, 3), ItemStack(Items.IRON_INGOT), 0.5f)
        FurnaceRecipes.instance().addSmeltingRecipe(
            ItemStack(AsteroidsItems.basicItem, 1, 4),
            ItemStack(AsteroidsItems.basicItem, 1, 0),
            0.5f
        )
        FurnaceRecipes.instance().addSmeltingRecipe(
            ItemStack(AsteroidsItems.basicItem, 1, 9),
            ItemStack(AsteroidsItems.basicItem, 1, 0),
            0.5f
        )

        //Cobblestone->Gravel, Gravel->Sand, Sand->Clay
        CompressorRecipes.addRecipeAdventure(
            ItemStack(Blocks.GRAVEL, 9, 0),
            "XXX",
            "XXX",
            "XXX",
            'X',
            ItemStack(Blocks.COBBLESTONE, 1)
        )
        CompressorRecipes.addRecipeAdventure(
            ItemStack(Blocks.SAND, 9, 0),
            "XXX",
            "XXX",
            "XXX",
            'X',
            ItemStack(Blocks.GRAVEL, 1)
        )
        CompressorRecipes.addRecipeAdventure(
            ItemStack(Blocks.CLAY, 8, 0),
            "XXX",
            "XBX",
            "XXX",
            'X',
            ItemStack(Blocks.SAND),
            'B',
            ItemStack(Items.WATER_BUCKET)
        )
        CompressorRecipes.addRecipeAdventure(
            ItemStack(Blocks.SOUL_SAND, 4, 0),
            "XFX",
            "FEF",
            "XFX",
            'X',
            ItemStack(Blocks.SAND),
            'F',
            ItemStack(Items.ROTTEN_FLESH),
            'E',
            ItemStack(Items.FERMENTED_SPIDER_EYE)
        )
        CompressorRecipes.addRecipeAdventure(
            ItemStack(Blocks.OBSIDIAN, 1, 0),
            "XXX",
            "XBX",
            "XXX",
            'X',
            ItemStack(Blocks.STONE),
            'B',
            ItemStack(Items.BLAZE_POWDER)
        )
        //Charcoal into coal
        CompressorRecipes.addShapelessAdventure(
            ItemStack(Items.COAL, 2, 0),
            ItemStack(Items.COAL, 1, 1),
            ItemStack(AsteroidBlocks.blockBasic, 1, 0),
            ItemStack(Items.COAL, 1, 1)
        )
        CompressorRecipes.addShapelessAdventure(
            ItemStack(Items.COAL, 2, 0),
            ItemStack(Items.COAL, 1, 1),
            ItemStack(AsteroidBlocks.blockBasic, 1, 1),
            ItemStack(Items.COAL, 1, 1)
        )
        CompressorRecipes.addShapelessAdventure(
            ItemStack(Items.COAL, 2, 0),
            ItemStack(Items.COAL, 1, 1),
            ItemStack(AsteroidBlocks.blockBasic, 1, 2),
            ItemStack(Items.COAL, 1, 1)
        )
        //Splintered ice into Ice
        CompressorRecipes.addShapelessRecipe(
            ItemStack(Blocks.ICE),
            ItemStack(AsteroidBlocks.blockDenseIce),
            ItemStack(AsteroidBlocks.blockDenseIce)
        )

        CompressorRecipes.addShapelessRecipe(ItemStack(AsteroidsItems.basicItem, 1, 6), titaniumIngot, titaniumIngot)
        CompressorRecipes.addShapelessRecipe(
            platingTier3,
            ItemStack(MarsItems.marsItemBasic, 1, 3),
            ItemStack(MarsItems.marsItemBasic, 1, 5)
        )

        var input: HashMap<Int, ItemStack?> = HashMap<Int, ItemStack?>()
        val plateTier3: ItemStack = ItemStack(AsteroidsItems.basicItem, 1, 5)
        val rocketFinsTier2: ItemStack = ItemStack(AsteroidsItems.basicItem, 1, 2)
        input.put(1, ItemStack(AsteroidsItems.heavyNoseCone))
        input.put(2, plateTier3)
        input.put(3, plateTier3)
        input.put(4, plateTier3)
        input.put(5, plateTier3)
        input.put(6, plateTier3)
        input.put(7, plateTier3)
        input.put(8, plateTier3)
        input.put(9, plateTier3)
        input.put(10, plateTier3)
        input.put(11, plateTier3)
        input.put(12, ItemStack(GCItems.rocketEngine, 1, 1))
        input.put(13, rocketFinsTier2)
        input.put(14, rocketFinsTier2)
        input.put(15, ItemStack(AsteroidsItems.basicItem, 1, 1))
        input.put(16, ItemStack(GCItems.rocketEngine, 1, 1))
        input.put(17, rocketFinsTier2)
        input.put(18, rocketFinsTier2)
        input.put(19, ItemStack.EMPTY)
        input.put(20, ItemStack.EMPTY)
        input.put(21, ItemStack.EMPTY)
        GalacticraftRegistry.addT3RocketRecipe(NasaWorkbenchRecipe(ItemStack(AsteroidsItems.tier3Rocket, 1, 0), input))

        var input2: HashMap<Int, ItemStack?> = HashMap<Int, ItemStack?>(input)
        input2.put(19, ItemStack(Blocks.CHEST))
        input2.put(20, ItemStack.EMPTY)
        input2.put(21, ItemStack.EMPTY)
        GalacticraftRegistry.addT3RocketRecipe(NasaWorkbenchRecipe(ItemStack(AsteroidsItems.tier3Rocket, 1, 1), input2))

        input2 = HashMap<Int, ItemStack?>(input)
        input2.put(19, ItemStack.EMPTY)
        input2.put(20, ItemStack(Blocks.CHEST))
        input2.put(21, ItemStack.EMPTY)
        GalacticraftRegistry.addT3RocketRecipe(NasaWorkbenchRecipe(ItemStack(AsteroidsItems.tier3Rocket, 1, 1), input2))

        input2 = HashMap<Int, ItemStack?>(input)
        input2.put(19, ItemStack.EMPTY)
        input2.put(20, ItemStack.EMPTY)
        input2.put(21, ItemStack(Blocks.CHEST))
        GalacticraftRegistry.addT3RocketRecipe(NasaWorkbenchRecipe(ItemStack(AsteroidsItems.tier3Rocket, 1, 1), input2))

        input2 = HashMap<Int, ItemStack?>(input)
        input2.put(19, ItemStack(Blocks.CHEST))
        input2.put(20, ItemStack(Blocks.CHEST))
        input2.put(21, ItemStack.EMPTY)
        GalacticraftRegistry.addT3RocketRecipe(NasaWorkbenchRecipe(ItemStack(AsteroidsItems.tier3Rocket, 1, 2), input2))

        input2 = HashMap<Int, ItemStack?>(input)
        input2.put(19, ItemStack(Blocks.CHEST))
        input2.put(20, ItemStack.EMPTY)
        input2.put(21, ItemStack(Blocks.CHEST))
        GalacticraftRegistry.addT3RocketRecipe(NasaWorkbenchRecipe(ItemStack(AsteroidsItems.tier3Rocket, 1, 2), input2))

        input2 = HashMap<Int, ItemStack?>(input)
        input2.put(19, ItemStack.EMPTY)
        input2.put(20, ItemStack(Blocks.CHEST))
        input2.put(21, ItemStack(Blocks.CHEST))
        GalacticraftRegistry.addT3RocketRecipe(NasaWorkbenchRecipe(ItemStack(AsteroidsItems.tier3Rocket, 1, 2), input2))

        input2 = HashMap<Int, ItemStack?>(input)
        input2.put(19, ItemStack(Blocks.CHEST))
        input2.put(20, ItemStack(Blocks.CHEST))
        input2.put(21, ItemStack(Blocks.CHEST))
        GalacticraftRegistry.addT3RocketRecipe(NasaWorkbenchRecipe(ItemStack(AsteroidsItems.tier3Rocket, 1, 3), input2))

        input = HashMap<Int, ItemStack?>()
        input.put(1, ItemStack(GCItems.heavyPlatingTier1))
        input.put(3, ItemStack(GCItems.heavyPlatingTier1))
        input.put(5, ItemStack(GCItems.heavyPlatingTier1))
        input.put(11, ItemStack(GCItems.heavyPlatingTier1))
        input.put(2, ItemStack(AsteroidsItems.orionDrive))
        input.put(4, ItemStack(AsteroidsItems.orionDrive))
        input.put(9, ItemStack(AsteroidsItems.orionDrive))
        input.put(10, ItemStack(AsteroidsItems.orionDrive))
        input.put(12, ItemStack(AsteroidsItems.orionDrive))
        input.put(6, ItemStack(GCItems.basicItem, 1, 14))
        input.put(7, ItemStack(Blocks.CHEST))
        input.put(8, ItemStack(Blocks.CHEST))
        input.put(13, ItemStack(AsteroidsItems.basicItem, 1, 8))
        input.put(14, ItemStack(GCItems.flagPole))
        GalacticraftRegistry.addAstroMinerRecipe(NasaWorkbenchRecipe(ItemStack(AsteroidsItems.astroMiner, 1, 0), input))

        //All this is for NEI's benefit
        val list1: NonNullList<Ingredient?> = NonNullList.create<Ingredient?>()
        val list2: NonNullList<Ingredient?> = NonNullList.create<Ingredient?>()
        val list3: NonNullList<Ingredient?> = NonNullList.create<Ingredient?>()
        list1.add(Ingredient.fromStacks(ItemStack(AsteroidsItems.canisterLOX, 1, 751)))
        list2.add(Ingredient.fromStacks(ItemStack(AsteroidsItems.canisterLOX, 1, 834)))
        list3.add(Ingredient.fromStacks(ItemStack(AsteroidsItems.canisterLOX, 1, 918)))
        list1.add(Ingredient.fromStacks(ItemStack(GCItems.oxTankHeavy, 1, 2700)))
        list2.add(Ingredient.fromStacks(ItemStack(GCItems.oxTankMedium, 1, 1800)))
        list3.add(Ingredient.fromStacks(ItemStack(GCItems.oxTankLight, 1, 900)))
        RecipeUtil.addCustomRecipe(CanisterRecipes(ItemStack(GCItems.oxTankHeavy, 1, 0), list1))
        RecipeUtil.addCustomRecipe(CanisterRecipes(ItemStack(GCItems.oxTankMedium, 1, 0), list2))
        RecipeUtil.addCustomRecipe(CanisterRecipes(ItemStack(GCItems.oxTankLight, 1, 0), list3))
    }

    @JvmStatic
    fun loadCompatibilityRecipes() {
        val titaniumIngot: ItemStack = ItemStack(AsteroidsItems.basicItem, 1, 0)
    }
}
