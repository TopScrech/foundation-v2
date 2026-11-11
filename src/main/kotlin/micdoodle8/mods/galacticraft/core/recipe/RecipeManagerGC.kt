package micdoodle8.mods.galacticraft.core.recipe

import foundation.item.FoundationItemVariants

import micdoodle8.mods.galacticraft.core.blocks.BlockEnclosed.EnumEnclosedBlockType
import net.minecraftforge.oredict.RecipeSorter.Category

import appeng.api.AEApi
import appeng.api.util.AEColor
import appeng.api.features.IGrinderRegistry
import micdoodle8.mods.galacticraft.api.GalacticraftRegistry
import micdoodle8.mods.galacticraft.api.recipe.CircuitFabricatorRecipes
import micdoodle8.mods.galacticraft.api.recipe.CompressorRecipes
import micdoodle8.mods.galacticraft.api.recipe.IRecipeUpdatable
import micdoodle8.mods.galacticraft.api.recipe.SpaceStationRecipe
import micdoodle8.mods.galacticraft.api.world.SpaceStationType
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.blocks.BlockEnclosed
import micdoodle8.mods.galacticraft.core.items.ItemBasic
import micdoodle8.mods.galacticraft.core.tick.TickHandlerClient
import micdoodle8.mods.galacticraft.core.util.CompatibilityManager
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.GCLog
import micdoodle8.mods.galacticraft.core.util.RecipeUtil
import micdoodle8.mods.galacticraft.planets.asteroids.blocks.AsteroidBlocks
import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems
import micdoodle8.mods.galacticraft.planets.mars.items.MarsItems
import micdoodle8.mods.galacticraft.planets.venus.VenusBlocks
import micdoodle8.mods.galacticraft.planets.venus.VenusItems
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.item.Items
import net.minecraft.world.item.ItemStack
import net.minecraft.item.crafting.CraftingManager
import net.minecraft.item.crafting.FurnaceRecipes
import net.minecraft.item.crafting.IRecipe
import net.minecraftforge.fml.LogicalSide as Side
import net.minecraftforge.oredict.OreDictionary
import net.minecraftforge.oredict.RecipeSorter
import java.lang.Exception
import java.lang.reflect.Method
import java.util.ArrayList
import java.util.Arrays
import java.util.HashMap
import java.util.Optional

object RecipeManagerGC {
    @JvmField
    var aluminumIngots: ArrayList<ItemStack> = ArrayList<ItemStack>()
    private var configSaved_RequireGCmetals = true
    private var configSaved_QuickMode = false
    private var configSaved_Silicon = "-"

    @JvmStatic
    fun loadCompatibilityRecipes() {
        if (CompatibilityManager.isAppEngLoaded()) {
            addAppEngRecipes()
        }

        if (CompatibilityManager.modAALoaded) {
            CompatModuleActuallyAdditions.addRecipes()
        }

        addExNihiloHeatSource()
    }

    @JvmStatic
    fun addUniversalRecipes() {
        RecipeSorter.register(
            "galacticraftcore:shapedore",
            OreRecipeUpdatable::class.java,
            RecipeSorter.Category.SHAPED,
            "after:minecraft:shaped before:minecraft:shapeless"
        )

        val meteoricIronIngot: Any = FoundationItemVariants.stack(requireNotNull(GCItems.itemBasicMoon), 1, 0)
        val meteoricIronPlate: Any = FoundationItemVariants.stack(requireNotNull(GCItems.itemBasicMoon), 1, 1)
        val deshIngot: Any? = if (GalacticraftCore.isPlanetsLoaded) FoundationItemVariants.stack(requireNotNull(MarsItems.marsItemBasic), 1, 2) else GCItems.heavyPlatingTier1

        FurnaceRecipes.instance()
            .addSmeltingRecipe(ItemStack(GCBlocks.basicBlock, 1, 5), FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 3), 0.5f)
        FurnaceRecipes.instance()
            .addSmeltingRecipe(ItemStack(GCBlocks.basicBlock, 1, 6), FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 4), 0.5f)
        FurnaceRecipes.instance()
            .addSmeltingRecipe(ItemStack(GCBlocks.basicBlock, 1, 7), FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 5), 0.5f)
        FurnaceRecipes.instance()
            .addSmeltingRecipe(ItemStack(GCItems.meteorChunk, 1, 0), ItemStack(GCItems.meteorChunk, 1, 1), 0.1f)
        FurnaceRecipes.instance().addSmelting(GCItems.meteoricIronRaw, FoundationItemVariants.stack(requireNotNull(GCItems.itemBasicMoon), 1, 0), 1.0f)
        FurnaceRecipes.instance()
            .addSmeltingRecipe(ItemStack(GCBlocks.blockMoon, 1, 0), FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 3), 1.0f)
        FurnaceRecipes.instance()
            .addSmeltingRecipe(ItemStack(GCBlocks.blockMoon, 1, 1), FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 4), 1.0f)
        FurnaceRecipes.instance()
            .addSmeltingRecipe(ItemStack(GCBlocks.blockMoon, 1, 2), ItemStack(GCItems.cheeseCurd), 1.0f)
        //Recycling: smelt tin/copper canisters back into ingots
        FurnaceRecipes.instance()
            .addSmeltingRecipe(foundation.item.FoundationItemVariants.stack(requireNotNull(GCItems.canister), 1, 0), FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 3, 4), 1.0f)
        FurnaceRecipes.instance()
            .addSmeltingRecipe(foundation.item.FoundationItemVariants.stack(requireNotNull(GCItems.canister), 1, 1), FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 3, 3), 1.0f)
        if (CompatibilityManager.useAluDust()) {
            FurnaceRecipes.instance()
                .addSmeltingRecipe(ItemStack(GCItems.ic2compat, 1, 0), FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 5), 1.0f)
        }
        FurnaceRecipes.instance()
            .addSmeltingRecipe(foundation.item.FoundationItemVariants.stack(requireNotNull(GCItems.foodItem), 1, 6), foundation.item.FoundationItemVariants.stack(requireNotNull(GCItems.foodItem), 1, 7), 1.0f)

        var input: HashMap<Int, ItemStack?> = HashMap<Int, ItemStack?>()
        input.put(1, ItemStack(GCItems.partNoseCone))
        input.put(2, ItemStack(GCItems.heavyPlatingTier1))
        input.put(3, ItemStack(GCItems.heavyPlatingTier1))
        input.put(4, ItemStack(GCItems.heavyPlatingTier1))
        input.put(5, ItemStack(GCItems.heavyPlatingTier1))
        input.put(6, ItemStack(GCItems.heavyPlatingTier1))
        input.put(7, ItemStack(GCItems.heavyPlatingTier1))
        input.put(8, ItemStack(GCItems.heavyPlatingTier1))
        input.put(9, ItemStack(GCItems.heavyPlatingTier1))
        input.put(10, ItemStack(GCItems.partFins))
        input.put(11, ItemStack(GCItems.partFins))
        input.put(12, ItemStack(GCItems.rocketEngine))
        input.put(13, ItemStack(GCItems.partFins))
        input.put(14, ItemStack(GCItems.partFins))
        input.put(15, ItemStack.EMPTY)
        input.put(16, ItemStack.EMPTY)
        input.put(17, ItemStack.EMPTY)
        RecipeUtil.addRocketBenchRecipe(ItemStack(GCItems.rocketTier1, 1, 0), input)

        var input2: HashMap<Int, ItemStack?> = HashMap<Int, ItemStack?>(input)
        input2.put(15, ItemStack(Blocks.CHEST))
        input2.put(16, ItemStack.EMPTY)
        input2.put(17, ItemStack.EMPTY)
        RecipeUtil.addRocketBenchRecipe(ItemStack(GCItems.rocketTier1, 1, 1), input2)

        input2 = HashMap<Int, ItemStack?>(input)
        input2.put(15, ItemStack.EMPTY)
        input2.put(16, ItemStack(Blocks.CHEST))
        input2.put(17, ItemStack.EMPTY)
        RecipeUtil.addRocketBenchRecipe(ItemStack(GCItems.rocketTier1, 1, 1), input2)

        input2 = HashMap<Int, ItemStack?>(input)
        input2.put(15, ItemStack.EMPTY)
        input2.put(16, ItemStack.EMPTY)
        input2.put(17, ItemStack(Blocks.CHEST))
        RecipeUtil.addRocketBenchRecipe(ItemStack(GCItems.rocketTier1, 1, 1), input2)

        input2 = HashMap<Int, ItemStack?>(input)
        input2.put(15, ItemStack(Blocks.CHEST))
        input2.put(16, ItemStack(Blocks.CHEST))
        input2.put(17, ItemStack.EMPTY)
        RecipeUtil.addRocketBenchRecipe(ItemStack(GCItems.rocketTier1, 1, 2), input2)

        input2 = HashMap<Int, ItemStack?>(input)
        input2.put(15, ItemStack(Blocks.CHEST))
        input2.put(16, ItemStack.EMPTY)
        input2.put(17, ItemStack(Blocks.CHEST))
        RecipeUtil.addRocketBenchRecipe(ItemStack(GCItems.rocketTier1, 1, 2), input2)

        input2 = HashMap<Int, ItemStack?>(input)
        input2.put(15, ItemStack.EMPTY)
        input2.put(16, ItemStack(Blocks.CHEST))
        input2.put(17, ItemStack(Blocks.CHEST))
        RecipeUtil.addRocketBenchRecipe(ItemStack(GCItems.rocketTier1, 1, 2), input2)

        input2 = HashMap<Int, ItemStack?>(input)
        input2.put(15, ItemStack(Blocks.CHEST))
        input2.put(16, ItemStack(Blocks.CHEST))
        input2.put(17, ItemStack(Blocks.CHEST))
        RecipeUtil.addRocketBenchRecipe(ItemStack(GCItems.rocketTier1, 1, 3), input2)

        //
        input = HashMap<Int, ItemStack?>()
        input.put(1, ItemStack(GCItems.heavyPlatingTier1))
        input.put(2, ItemStack(GCItems.heavyPlatingTier1))
        input.put(3, ItemStack(GCItems.heavyPlatingTier1))
        input.put(4, ItemStack(GCItems.heavyPlatingTier1))
        input.put(5, ItemStack(GCItems.heavyPlatingTier1))
        input.put(6, ItemStack(GCItems.partBuggy, 1, 1))
        input.put(7, ItemStack(GCItems.heavyPlatingTier1))
        input.put(8, ItemStack(GCItems.heavyPlatingTier1))
        input.put(9, ItemStack(GCItems.heavyPlatingTier1))
        input.put(10, ItemStack(GCItems.heavyPlatingTier1))
        input.put(11, ItemStack(GCItems.heavyPlatingTier1))
        input.put(12, ItemStack(GCItems.heavyPlatingTier1))
        input.put(13, ItemStack(GCItems.partBuggy))
        input.put(14, ItemStack(GCItems.partBuggy))
        input.put(15, ItemStack(GCItems.partBuggy))
        input.put(16, ItemStack(GCItems.partBuggy))
        input.put(17, ItemStack.EMPTY)
        input.put(18, ItemStack.EMPTY)
        input.put(19, ItemStack.EMPTY)
        RecipeUtil.addBuggyBenchRecipe(ItemStack(GCItems.buggy, 1, 0), input)

        input2 = HashMap<Int, ItemStack?>(input)
        input2.put(17, ItemStack(GCItems.partBuggy, 1, 2))
        input2.put(18, ItemStack.EMPTY)
        input2.put(19, ItemStack.EMPTY)
        RecipeUtil.addBuggyBenchRecipe(ItemStack(GCItems.buggy, 1, 1), input2)

        input2 = HashMap<Int, ItemStack?>(input)
        input2.put(17, ItemStack.EMPTY)
        input2.put(18, ItemStack(GCItems.partBuggy, 1, 2))
        input2.put(19, ItemStack.EMPTY)
        RecipeUtil.addBuggyBenchRecipe(ItemStack(GCItems.buggy, 1, 1), input2)

        input2 = HashMap<Int, ItemStack?>(input)
        input2.put(17, ItemStack.EMPTY)
        input2.put(18, ItemStack.EMPTY)
        input2.put(19, ItemStack(GCItems.partBuggy, 1, 2))
        RecipeUtil.addBuggyBenchRecipe(ItemStack(GCItems.buggy, 1, 1), input2)

        input2 = HashMap<Int, ItemStack?>(input)
        input2.put(17, ItemStack(GCItems.partBuggy, 1, 2))
        input2.put(18, ItemStack(GCItems.partBuggy, 1, 2))
        input2.put(19, ItemStack.EMPTY)
        RecipeUtil.addBuggyBenchRecipe(ItemStack(GCItems.buggy, 1, 2), input2)

        input2 = HashMap<Int, ItemStack?>(input)
        input2.put(17, ItemStack(GCItems.partBuggy, 1, 2))
        input2.put(18, ItemStack.EMPTY)
        input2.put(19, ItemStack(GCItems.partBuggy, 1, 2))
        RecipeUtil.addBuggyBenchRecipe(ItemStack(GCItems.buggy, 1, 2), input2)

        input2 = HashMap<Int, ItemStack?>(input)
        input2.put(17, ItemStack.EMPTY)
        input2.put(18, ItemStack(GCItems.partBuggy, 1, 2))
        input2.put(19, ItemStack(GCItems.partBuggy, 1, 2))
        RecipeUtil.addBuggyBenchRecipe(ItemStack(GCItems.buggy, 1, 2), input2)

        input2 = HashMap<Int, ItemStack?>(input)
        input2.put(17, ItemStack(GCItems.partBuggy, 1, 2))
        input2.put(18, ItemStack(GCItems.partBuggy, 1, 2))
        input2.put(19, ItemStack(GCItems.partBuggy, 1, 2))
        RecipeUtil.addBuggyBenchRecipe(ItemStack(GCItems.buggy, 1, 3), input2)

        aluminumIngots.addAll(OreDictionary.getOres("ingotAluminum"))
        val addedList: ArrayList<ItemStack> = ArrayList<ItemStack>()
        for (ingotNew: ItemStack in OreDictionary.getOres("ingotAluminium")) {
            var flag: Boolean = false
            for (ingotDone: ItemStack in aluminumIngots) {
                if (ItemStack.areItemStacksEqual(ingotNew, ingotDone)) {
                    flag = true
                    break
                }
            }
            if (!flag) {
                addedList.add(ingotNew)
                OreDictionary.registerOre("ingotAluminum", ingotNew)
            }
        }
        if (addedList.size > 0) {
            aluminumIngots.addAll(addedList)
            addedList.clear()
        }
        for (ingotNew: ItemStack in OreDictionary.getOres("ingotNaturalAluminum")) {
            for (ingotDone: ItemStack in aluminumIngots) {
                if (!ItemStack.areItemStacksEqual(ingotNew, ingotDone)) {
                    addedList.add(ingotNew)
                }
            }
        }
        if (addedList.size > 0) {
            aluminumIngots.addAll(addedList)
        }

        val spaceStationRequirements: HashMap<Any?,Int> = HashMap<Any?,Int>(4, 1.0f)
        spaceStationRequirements.put("ingotTin", 32)
        spaceStationRequirements.put(aluminumIngots, 16)
        spaceStationRequirements.put(FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, ItemBasic.WAFER_ADVANCED), 1)
        spaceStationRequirements.put(Items.IRON_INGOT, 24)
        GalacticraftRegistry.registerSpaceStation(
            SpaceStationType(
                ConfigManagerCore.idDimensionOverworldOrbit,
                ConfigManagerCore.idDimensionOverworld,
                SpaceStationRecipe(spaceStationRequirements)
            )
        )

        //EmergencyKit

        CompressorRecipes.addShapelessRecipe(FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 6), "ingotCopper", "ingotCopper")
        CompressorRecipes.addShapelessRecipe(FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 7), "ingotTin", "ingotTin")
        CompressorRecipes.addShapelessRecipe(FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 8), "ingotAluminum", "ingotAluminum")

        /*        // Support for all the spellings of Aluminum
        for (ItemStack stack : aluminumIngots)
        {
          	CompressorRecipes.addShapelessRecipe(new ItemStack(GCItems.basicItem, 1, 8), stack, stack);
        }
*/
        if (OreDictionary.getOres("ingotBronze").size > 0) {
            CompressorRecipes.addShapelessRecipe(FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 10), "ingotBronze", "ingotBronze")
        }
        if (OreDictionary.getOres("ingotSteel").size > 0) {
            CompressorRecipes.addShapelessRecipe(FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 9), "ingotSteel", "ingotSteel")
            CompressorRecipes.steelIngotsPresent = true
        }
        CompressorRecipes.steelRecipeGC =
            Arrays.asList<ItemStack?>(ItemStack(Items.COAL), FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 11), ItemStack(Items.COAL))
        CompressorRecipes.addShapelessRecipe(
            FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 9),
            Items.COAL,
            FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 11),
            Items.COAL
        )
        CompressorRecipes.addShapelessRecipe(
            FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 10),
            FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 6),
            FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 7)
        )
        CompressorRecipes.addShapelessRecipe(FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 11), Items.IRON_INGOT, Items.IRON_INGOT)
        CompressorRecipes.addShapelessRecipe(FoundationItemVariants.stack(requireNotNull(GCItems.itemBasicMoon), 1, 1), meteoricIronIngot)
        CompressorRecipes.addRecipe(
            ItemStack(GCItems.heavyPlatingTier1, 2, 0),
            "XYZ",
            "XYZ",
            'X',
            FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 9),
            'Y',
            FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 8),
            'Z',
            FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 10)
        )

        val solarPanels: ItemStack = FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 9, 12)
        val basicWafers: ItemStack = FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 3, 13)
        val advancedWafers: ItemStack = FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 14)
        val silicon: ItemStack = FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 2)
        CircuitFabricatorRecipes.addRecipe(
            solarPanels,
            Arrays.asList<Any?>(
                ItemStack(Items.DIAMOND),
                silicon,
                silicon,
                ItemStack(Items.REDSTONE),
                ItemStack(Items.DYE, 1, 4)
            )
        )
        CircuitFabricatorRecipes.addRecipe(
            basicWafers,
            Arrays.asList<Any?>(
                ItemStack(Items.DIAMOND),
                silicon,
                silicon,
                ItemStack(Items.REDSTONE),
                ItemStack(Blocks.REDSTONE_TORCH)
            )
        )
        CircuitFabricatorRecipes.addRecipe(
            advancedWafers,
            Arrays.asList<Any?>(
                ItemStack(Items.DIAMOND),
                silicon,
                silicon,
                ItemStack(Items.REDSTONE),
                ItemStack(Items.REPEATER)
            )
        )
    }

    @JvmStatic
    fun setConfigurableRecipes() {
        if (GCCoreUtil.getEffectiveSide() == Side.CLIENT && CompatibilityManager.modJEILoaded) TickHandlerClient.Companion.updateJEIhiding =
            true

        // Update Aluminium Wire and Battery crafting recipes and GC Advanced Metals recipes
        val aluWire: ItemStack = ItemStack(GCBlocks.aluminumWire, if (ConfigManagerCore.quickMode) 9 else 6, 0)
        val battery: ItemStack = ItemStack(GCItems.battery, if (ConfigManagerCore.quickMode) 3 else 2, 100)
        val doQuickMode: Boolean = ConfigManagerCore.quickMode != configSaved_QuickMode
        configSaved_QuickMode = ConfigManagerCore.quickMode

        val meteoricIronIngot: ItemStack = FoundationItemVariants.stack(requireNotNull(GCItems.itemBasicMoon), 1, 0)
        val meteoricIronPlate: ItemStack = FoundationItemVariants.stack(requireNotNull(GCItems.itemBasicMoon), 1, 1)
        val doMetalsToOreDict: Boolean =
            configSaved_RequireGCmetals && !ConfigManagerCore.recipesRequireGCAdvancedMetals
        val doMetalsToGC: Boolean = !configSaved_RequireGCmetals && ConfigManagerCore.recipesRequireGCAdvancedMetals
        configSaved_RequireGCmetals = ConfigManagerCore.recipesRequireGCAdvancedMetals

        val doSilicon: Boolean = ConfigManagerCore.otherModsSilicon != configSaved_Silicon
        configSaved_Silicon = (ConfigManagerCore.otherModsSilicon)!!

        if (doQuickMode || doMetalsToOreDict || doMetalsToGC) {
            for (recipe: IRecipe? in CraftingManager.REGISTRY) {
                if (recipe is IRecipeUpdatable) {
                    if (doQuickMode) {
                        val test: ItemStack = recipe.getRecipeOutput()
                        if (ItemStack.areItemsEqual(test, aluWire)) {
                            test.setCount(aluWire.getCount())
                        } else if (ItemStack.areItemsEqual(test, battery)) {
                            test.setCount(battery.getCount())
                        }
                    }
                    if (doMetalsToOreDict) {
                        recipe.replaceInput(meteoricIronIngot, OreDictionary.getOres("ingotMeteoricIron"))
                        recipe.replaceInput(meteoricIronPlate, OreDictionary.getOres("compressedMeteoricIron"))
                        if (GalacticraftCore.isPlanetsLoaded) {
                            recipe.replaceInput(
                                FoundationItemVariants.stack(requireNotNull(MarsItems.marsItemBasic), 1, 2),
                                OreDictionary.getOres("ingotDesh")
                            )
                            recipe.replaceInput(
                                FoundationItemVariants.stack(requireNotNull(MarsItems.marsItemBasic), 1, 5),
                                OreDictionary.getOres("compressedDesh")
                            )
                            recipe.replaceInput(
                                FoundationItemVariants.stack(requireNotNull(AsteroidsItems.basicItem), 1, 0),
                                OreDictionary.getOres("ingotTitanium")
                            )
                            recipe.replaceInput(
                                FoundationItemVariants.stack(requireNotNull(AsteroidsItems.basicItem), 1, 6),
                                OreDictionary.getOres("compressedTitanium")
                            )
                            recipe.replaceInput(
                                FoundationItemVariants.stack(requireNotNull(VenusItems.basicItem), 1, 1),
                                OreDictionary.getOres("ingotLead")
                            )
                        }
                    } else if (doMetalsToGC) {
                        recipe.replaceInput(meteoricIronIngot)
                        recipe.replaceInput(meteoricIronPlate)
                        if (GalacticraftCore.isPlanetsLoaded) {
                            recipe.replaceInput(FoundationItemVariants.stack(requireNotNull(MarsItems.marsItemBasic), 1, 2))
                            recipe.replaceInput(FoundationItemVariants.stack(requireNotNull(MarsItems.marsItemBasic), 1, 5))
                            recipe.replaceInput(FoundationItemVariants.stack(requireNotNull(AsteroidsItems.basicItem), 1, 0))
                            recipe.replaceInput(FoundationItemVariants.stack(requireNotNull(AsteroidsItems.basicItem), 1, 6))
                            recipe.replaceInput(FoundationItemVariants.stack(requireNotNull(VenusItems.basicItem), 1, 1))
                        }
                    }
                }
            }
        }

        if (doSilicon) {
            val siliconGC: ItemStack = FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 2)
            var needNewList: Boolean = true
            var silicons: MutableList<ItemStack> = OreDictionary.getOres(ConfigManagerCore.otherModsSilicon)
            if (silicons.size > 0) {
                for (s: ItemStack in silicons) {
                    if (ItemStack.areItemsEqual(s, siliconGC)) {
                        needNewList = false
                        break
                    }
                }
            }
            if (needNewList) {
                val newList: MutableList<ItemStack> = ArrayList<ItemStack>(1 + silicons.size)
                newList.add(siliconGC)
                if (silicons.size > 0) newList.addAll(silicons)
                silicons = newList
            }
            CircuitFabricatorRecipes.replaceRecipeIngredient(siliconGC, (silicons as MutableList<ItemStack?>))
        }

        if (doMetalsToOreDict || doMetalsToGC) {
            if (ConfigManagerCore.recipesRequireGCAdvancedMetals) {
                CompressorRecipes.replaceRecipeIngredient(meteoricIronIngot)
            } else {
                CompressorRecipes.replaceRecipeIngredient(meteoricIronIngot, OreDictionary.getOres("ingotMeteoricIron"))
            }
            if (GalacticraftCore.isPlanetsLoaded) {
                setConfigurableRecipesPlanets()
            }


            // Update Advanced Wafer in space station recipe
            val sswafer: ItemStack = FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, ItemBasic.WAFER_ADVANCED)
            for (station: SpaceStationType in GalacticraftRegistry.getSpaceStationData()) {
                val ssrecipe: HashMap<Any?,Int> = (station.getRecipeForSpaceStation())!!.getInput()
                if (doMetalsToOreDict) {
                    var found: ItemStack? = null
                    for (test: Any? in ssrecipe.keys) {
                        if (test is ItemStack && ItemStack.areItemsEqual(test, sswafer)) {
                            found = test
                            break
                        }
                    }
                    if (found != null) {
                        ssrecipe.remove(found)
                        ssrecipe.put(OreDictionary.getOres("waferAdvanced"), 1)
                    }
                } else if (doMetalsToGC) {
                    var found: Any? = null
                    for (test: Any? in ssrecipe.keys) {
                        if (test is MutableList<*> && OreRecipeUpdatable.Companion.itemListContains(test, sswafer)) {
                            found = test
                            break
                        }
                    }
                    if (found != null) {
                        ssrecipe.remove(found)
                        ssrecipe.put(sswafer, 1)
                    }
                }
            }
        }
    }

    @JvmStatic
    private fun setConfigurableRecipesPlanets() {
        if (ConfigManagerCore.recipesRequireGCAdvancedMetals) {
            CompressorRecipes.replaceRecipeIngredient(FoundationItemVariants.stack(requireNotNull(MarsItems.marsItemBasic), 1, 2))
            CompressorRecipes.replaceRecipeIngredient(FoundationItemVariants.stack(requireNotNull(AsteroidsItems.basicItem), 1, 0))
        } else {
            CompressorRecipes.replaceRecipeIngredient(
                FoundationItemVariants.stack(requireNotNull(MarsItems.marsItemBasic), 1, 2),
                OreDictionary.getOres("ingotDesh")
            )
            CompressorRecipes.replaceRecipeIngredient(
                FoundationItemVariants.stack(requireNotNull(AsteroidsItems.basicItem), 1, 0),
                OreDictionary.getOres("ingotTitanium")
            )
        }
    }

    @JvmStatic
    private fun addAppEngRecipes() {
        RecipeUtil.addRecipe(
            ItemStack(
                GCBlocks.sealableBlock,
                1,
                BlockEnclosed.EnumEnclosedBlockType.ME_CABLE.getMeta()
            ),
            arrayOf<Any?>(
                "XYX",
                'Y',
                AEApi.instance().definitions().parts().cableGlass().stack(AEColor.TRANSPARENT, 1),
                'X',
                ItemStack(GCBlocks.basicBlock, 1, 4)
            )
        )
        val a: IGrinderRegistry = AEApi.instance().registries().grinder()
        var officialAE2API: Boolean = false
        try {
            officialAE2API = a.javaClass.getMethod("builder") != null
        } catch (ignore: Exception) {
        }
        if (officialAE2API) {
            a.addRecipe(
                a.builder().withInput(ItemStack(GCBlocks.basicBlock, 1, 7))
                    .withOutput(ItemStack(GCItems.ic2compat, 1, 0))
                    .withFirstOptional(ItemStack(GCItems.ic2compat, 1, 0), 0.8f).withTurns(8).build()
            )
            if (GalacticraftCore.isPlanetsLoaded) {
                a.addRecipe(
                    a.builder().withInput(ItemStack(AsteroidBlocks.blockBasic, 1, 3))
                        .withOutput(ItemStack(GCItems.ic2compat, 1, 0))
                        .withFirstOptional(ItemStack(GCItems.ic2compat, 1, 0), 0.9f).withTurns(8).build()
                )
                a.addRecipe(
                    a.builder().withInput(ItemStack(VenusBlocks.venusBlock, 1, 6))
                        .withOutput(ItemStack(GCItems.ic2compat, 1, 0))
                        .withFirstOptional(ItemStack(GCItems.ic2compat, 1, 0), 1.0f).withTurns(8).build()
                )
                // Grind Ilmenite Ore for 1 Titanium Dust, 1 Iron Dust
                val ironDust: Optional<ItemStack?> = AEApi.instance().definitions().materials().ironDust().maybeStack(1)
                a.addRecipe(
                    a.builder().withInput(ItemStack(AsteroidBlocks.blockBasic, 1, 4))
                        .withOutput(FoundationItemVariants.stack(requireNotNull(AsteroidsItems.basicItem), 1, 9)).withFirstOptional(ironDust.get(), 1.0f)
                        .withTurns(8).build()
                )
            }
        } else {
            GCLog.debug("Failed to add Applied Energistics 2 Recipes")
        }
    }

    @JvmStatic
    private fun addExNihiloHeatSource() {
        try {
            val registry: Class<*> = Class.forName("exnihilo.registries.HeatRegistry")
            val m: Method = registry.getMethod("register", Block::class.java, Float::class.javaPrimitiveType)
            m.invoke(null, GCBlocks.unlitTorchLit, 0.1f)
            for (torch: Block? in GCBlocks.otherModTorchesLit) {
                m.invoke(null, torch, 0.1f)
            }
            GCLog.info("Successfully added space torches as heat sources for Ex Nihilo crucibles etc")
        } catch (e: Throwable) {
        }
    }
}
