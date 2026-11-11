package galaxyspace.systems.SolarSystem.planets.overworld.recipes

import micdoodle8.mods.galacticraft.core.items.ItemOxygenTank

import foundation.item.FoundationItemVariants

import java.util.Arrays
import java.util.HashMap
import java.util.LinkedList

import galaxyspace.core.GSBlocks
import galaxyspace.core.GSFluids
import galaxyspace.core.GSItems
import galaxyspace.core.configs.GSConfigCore
import galaxyspace.core.configs.GSConfigSchematics
import galaxyspace.core.util.GSRecipeUtil
import galaxyspace.systems.SolarSystem.moons.miranda.blocks.MirandaBlocks
import galaxyspace.systems.SolarSystem.moons.miranda.blocks.MirandaBlocks.EnumMirandaBlocks
import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemBasicGS
import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemBasicGS.BasicItems
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityFuelGenerator
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityGasGenerator
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityHydroponicBase
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityLiquidExtractor
import micdoodle8.mods.galacticraft.api.recipe.CircuitFabricatorRecipes
import micdoodle8.mods.galacticraft.api.recipe.CompressorRecipes
import micdoodle8.mods.galacticraft.api.recipe.INasaWorkbenchRecipe
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.recipe.NasaWorkbenchRecipe
import micdoodle8.mods.galacticraft.core.recipe.ShapedRecipeNBT
import micdoodle8.mods.galacticraft.core.tile.TileEntityDeconstructor
import micdoodle8.mods.galacticraft.core.util.RecipeUtil
import micdoodle8.mods.galacticraft.planets.asteroids.AsteroidsModule
import micdoodle8.mods.galacticraft.planets.asteroids.blocks.AsteroidBlocks
import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems
import micdoodle8.mods.galacticraft.planets.mars.blocks.MarsBlocks
import micdoodle8.mods.galacticraft.planets.mars.items.MarsItems
import micdoodle8.mods.galacticraft.planets.venus.VenusBlocks
import micdoodle8.mods.galacticraft.planets.venus.VenusItems
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.item.Items
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.oredict.OreDictionary
import java.util.*

object CraftingRecipesOverworld {
    private val plateIron = if (GSConfigCore.enablePlateOreDict) "plateIron" else "compressedIron"
    private val plateSteel = if (GSConfigCore.enablePlateOreDict) "plateSteel" else "compressedSteel"
    private val plateTitanium = if (GSConfigCore.enablePlateOreDict) "plateTitanium" else "compressedTitanium"
    private val plateBronze = if (GSConfigCore.enablePlateOreDict) "plateBronze" else "compressedBronze"
    private val plateTin = if (GSConfigCore.enablePlateOreDict) "plateTin" else "compressedTin"
    private val plateDesh = if (GSConfigCore.enablePlateOreDict) "plateDesh" else "compressedDesh"
    private val plateAluminum = if (GSConfigCore.enablePlateOreDict) "plateAluminum" else "compressedAluminum"
    private val plateCopper = if (GSConfigCore.enablePlateOreDict) "plateCopper" else "compressedCopper"
    private const val blockCopper = "blockCopper"

    @JvmStatic
    fun loadRecipes() {
        addBlockRecipes()
        addItemRecipes()
        addBlockSmelting()
        addCompressor()
        addAssembly()
        addRocketAssembly()
        addRecycler()
        addCircuitFabricator()
        addOtherRecipes()
    }

    @JvmStatic
    private fun addBlockRecipes() {
        RecipeUtil.addRecipe(
            ItemStack(GSBlocks.DECO_METALS, 4, 0),
            arrayOf<Any?>("YY ", "YYX", "   ", 'X', "plateCobalt", 'Y', Blocks.STONE)
        )
        RecipeUtil.addRecipe(
            ItemStack(GSBlocks.DECO_METALS, 4, 1),
            arrayOf<Any?>("YY ", "YYX", "   ", 'X', "plateMagnesium", 'Y', Blocks.STONE)
        )
        RecipeUtil.addRecipe(
            ItemStack(GSBlocks.DECO_METALS, 4, 2),
            arrayOf<Any?>("YY ", "YYX", "   ", 'X', "plateNickel", 'Y', Blocks.STONE)
        )
        RecipeUtil.addRecipe(
            ItemStack(GSBlocks.DECO_METALS, 4, 3),
            arrayOf<Any?>("YY ", "YYX", "   ", 'X', plateCopper, 'Y', Blocks.STONE)
        )

        RecipeUtil.addRecipe(
            ItemStack(GSBlocks.DECO_METALS, 4, 4),
            arrayOf<Any?>("YY ", "YY ", " X ", 'X', "plateCobalt", 'Y', Blocks.STONE)
        )
        RecipeUtil.addRecipe(
            ItemStack(GSBlocks.DECO_METALS, 4, 5),
            arrayOf<Any?>("YY ", "YY ", " X ", 'X', "plateMagnesium", 'Y', Blocks.STONE)
        )
        RecipeUtil.addRecipe(
            ItemStack(GSBlocks.DECO_METALS, 4, 6),
            arrayOf<Any?>("YY ", "YY ", " X ", 'X', "plateNickel", 'Y', Blocks.STONE)
        )
        RecipeUtil.addRecipe(
            ItemStack(GSBlocks.DECO_METALS, 4, 7),
            arrayOf<Any?>("YY ", "YY ", " X ", 'X', plateCopper, 'Y', Blocks.STONE)
        )
        RecipeUtil.addRecipe(
            ItemStack(GSBlocks.DECO_METALS, 4, 8),
            arrayOf<Any?>("YY ", "YYX", "   ", 'X', plateAluminum, 'Y', Blocks.STONE)
        )
        RecipeUtil.addRecipe(
            ItemStack(GSBlocks.DECO_METALS, 4, 9),
            arrayOf<Any?>("YY ", "YY ", " X ", 'X', plateAluminum, 'Y', Blocks.STONE)
        )


        RecipeUtil.addRecipe(ItemStack(GSBlocks.METALS, 1, 0), (arrayOf<Any>("XXX", "XXX", "XXX", 'X', "ingotCobalt") as Array<Any?>))
        RecipeUtil.addRecipe(ItemStack(GSBlocks.METALS, 1, 1), (arrayOf<Any>("XXX", "XXX", "XXX", 'X', "ingotNickel") as Array<Any?>))
        RecipeUtil.addRecipe(ItemStack(GSBlocks.METALS, 1, 2), (arrayOf<Any>("XXX", "XXX", "XXX", 'X', "ingotMagnesium") as Array<Any?>))

        RecipeUtil.addRecipe(FoundationItemVariants.stack(GSItems.INGOTS, 9, 0), (arrayOf<Any>("X", 'X', ItemStack(GSBlocks.METALS, 1, 0)) as Array<Any?>))
        RecipeUtil.addRecipe(FoundationItemVariants.stack(GSItems.INGOTS, 9, 2), (arrayOf<Any>("X", 'X', ItemStack(GSBlocks.METALS, 1, 1)) as Array<Any?>))
        RecipeUtil.addRecipe(FoundationItemVariants.stack(GSItems.INGOTS, 9, 1), (arrayOf<Any>("X", 'X', ItemStack(GSBlocks.METALS, 1, 2)) as Array<Any?>))

        RecipeUtil.addRecipe(
            ItemStack(GSBlocks.FUTURE_GLASS_BASIC, 8, 0),
            arrayOf<Any?>("XXX", "XWX", "XXX", 'W', ItemStack(GCItems.flagPole, 1, 0), 'X', Blocks.GLASS)
        )

        for (var2: Int in 0..15) RecipeUtil.addRecipe(
            ItemStack(GSBlocks.FUTURE_GLASS_COLORED, 8, var2),
            (arrayOf<Any>(
                "XXX",
                "XWX",
                "XXX",
                'X',
                ItemStack(GSBlocks.FUTURE_GLASS_BASIC, 1, 0),
                'W',
                ItemStack(Items.DYE, 1, var2)
            ) as Array<Any?>)
        )


        RecipeUtil.addRecipe(
            ItemStack(GSBlocks.SOLARWIND_PANEL, 1, 0),
            arrayOf<Any?>(
                "XYX",
                "XZX",
                "VWV",
                'V',
                ItemStack(GCBlocks.aluminumWire, 1, 0),
                'W',
                ItemStack(GSBlocks.MACHINE_FRAMES, 1, 0),
                'X',
                plateSteel,
                'Y',
                BasicItems.SOLARFLARES.getItemStack(),
                'Z',
                GCItems.flagPole
            )
        )
        RecipeUtil.addRecipe(
            ItemStack(GSBlocks.FUEL_GENERATOR, 1, 0),
            arrayOf<Any?>(
                "WYW",
                "WZW",
                "XXX",
                'W',
                foundation.item.FoundationItemVariants.stack(requireNotNull(GCItems.canister), 1, 1),
                'X',
                plateSteel,
                'Y',
                ItemStack(Blocks.GLASS_PANE, 1, 0),
                'Z',
                ItemStack(GCBlocks.machineBase, 1, 0)
            )
        )
        RecipeUtil.addRecipe(
            ItemStack(GSBlocks.ASSEMBLER, 1, 0),
            arrayOf<Any?>(
                "VWV",
                "XYX",
                "ZWC",
                'V',
                plateSteel,
                'W',
                ItemStack(GCBlocks.aluminumWire, 1, 0),
                'X',
                FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 13),
                'Y',
                ItemStack(Blocks.CRAFTING_TABLE, 1, 0),
                'Z',
                ItemStack(GCBlocks.machineBase, 1, 12),
                'C',
                ItemStack(GCBlocks.machineBase2, 1, 4)
            )
        )

        RecipeUtil.addRecipe(
            ItemStack(GSBlocks.ROCKET_ASSEMBLER, 1, 0),
            (arrayOf<Any>(
                "VWV",
                "XYZ",
                "BNB",
                'V',
                FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 14),
                'W',
                ItemStack(GCBlocks.nasaWorkbench, 1, 0),
                'X',
                ItemStack(GCBlocks.machineBase2, 1, 0),
                'Y',
                ItemStack(GSBlocks.MACHINE_FRAMES, 1, 0),
                'Z',
                ItemStack(GCBlocks.machineBase2, 1, 4),
                'B',
                ItemStack(GCBlocks.aluminumWire, 1, 1),
                'N',
                ItemStack(GSBlocks.ASSEMBLER)
            ) as Array<Any?>)
        )

        RecipeUtil.addRecipe(
            ItemStack(GSBlocks.MACHINE_FRAMES, 1, 0),
            arrayOf<Any?>(
                "XYX",
                "WVW",
                "XYX",
                'X',
                plateSteel,
                'Y',
                FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 13),
                'W',
                ItemStack(GCBlocks.aluminumWire, 1, 0),
                'V',
                ItemStack(Blocks.REDSTONE_BLOCK, 1, 0)
            )
        )
        RecipeUtil.addRecipe(
            ItemStack(GSBlocks.MACHINE_FRAMES, 1, 1),
            arrayOf<Any?>(
                "XYX",
                "WVW",
                "XYX",
                'X',
                plateTitanium,
                'Y',
                FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 14),
                'W',
                ItemStack(GCBlocks.aluminumWire, 1, 1),
                'V',
                ItemStack(GSBlocks.MACHINE_FRAMES, 1, 0)
            )
        )
        RecipeUtil.addRecipe(
            ItemStack(GSBlocks.MACHINE_FRAMES, 1, 2),
            (arrayOf<Any>(
                "XYX",
                "WVW",
                "XYX",
                'X',
                "plateMagnesium",
                'Y',
                BasicItems.WAFER_MODERN.getItemStack(),
                'W',
                ItemStack(GCBlocks.aluminumWire, 1, 1),
                'V',
                ItemStack(GSBlocks.MACHINE_FRAMES, 1, 1)
            ) as Array<Any?>)
        )

        RecipeUtil.addRecipe(
            ItemStack(GSBlocks.MODERN_STORAGE_MODULE, 1, 0),
            (arrayOf<Any>(
                "XYX",
                "WVW",
                "XYX",
                'X',
                FoundationItemVariants.stack(GSItems.INGOTS, 1, 1),
                'Y',
                ItemStack(GCBlocks.machineTiered, 1, 8),
                'W',
                BasicItems.WAFER_MODERN.getItemStack(),
                'V',
                ItemStack(GSBlocks.MACHINE_FRAMES, 1, 2)
            ) as Array<Any?>)
        )
        RecipeUtil.addRecipe(
            ItemStack(GSBlocks.OXYGEN_STORAGE_MODULE, 1, 0),
            (arrayOf<Any>(
                "XYX",
                "WVW",
                "XZX",
                'X',
                ItemStack(GCBlocks.machineBase2, 1, 8),
                'Y',
                ItemStack(GCItems.oxygenConcentrator, 1, 0),
                'W',
                ItemStack(GCBlocks.oxygenPipe, 1, 0),
                'V',
                ItemStack(GSBlocks.MACHINE_FRAMES, 1, 1),
                'Z',
                BasicItems.WAFER_MODERN.getItemStack()
            ) as Array<Any?>)
        )
        RecipeUtil.addRecipe(
            ItemStack(GSBlocks.MODERN_SOLAR_PANEL, 1, 0),
            (arrayOf<Any>(
                "XYX",
                "WVW",
                "CZC",
                'X',
                ItemStack(Items.GOLD_INGOT, 1, 0),
                'Y',
                FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 1),
                'W',
                ItemStack(GCItems.flagPole, 1, 0),
                'V',
                ItemStack(GSBlocks.MACHINE_FRAMES, 1, 2),
                'Z',
                ItemStack(GCBlocks.solarPanel, 1, 4),
                'C',
                ItemStack(GCBlocks.aluminumWire, 1, 1)
            ) as Array<Any?>)
        )
        RecipeUtil.addRecipe(
            ItemStack(GSBlocks.GRAVITATION_MODULE, 1, 0),
            (arrayOf<Any>(
                "XYX",
                "WVW",
                "CZC",
                'X',
                FoundationItemVariants.stack(GSItems.COMPRESSED_PLATES, 1, 4),
                'Y',
                BasicItems.UNKNOW_CRYSTAL.getItemStack(),
                'W',
                ItemStack(GCBlocks.aluminumWire, 1, 1),
                'V',
                ItemStack(GSBlocks.MACHINE_FRAMES, 1, 0),
                'Z',
                ItemStack(GSBlocks.MACHINE_FRAMES, 1, 2),
                'C',
                FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 14)
            ) as Array<Any?>)
        )
        RecipeUtil.addRecipe(
            ItemStack(GSBlocks.WIND_GENERATOR, 1, 0),
            (arrayOf<Any>(
                "XYX",
                "MVM",
                "CZC",
                'X',
                "ingotAluminum",
                'Y',
                ItemStack(GCItems.oxygenFan, 1, 0),
                'V',
                ItemStack(GCItems.flagPole, 1, 0),
                'M',
                ItemStack(GCBlocks.aluminumWire, 1, 0),
                'Z',
                ItemStack(GSBlocks.MACHINE_FRAMES, 1, 0),
                'C',
                "compressedMeteoricIron"
            ) as Array<Any?>)
        )
        RecipeUtil.addRecipe(
            ItemStack(GSBlocks.UNIVERSAL_RECYCLER, 1, 0),
            (arrayOf<Any>(
                "XYX",
                "MZM",
                "CVC",
                'X',
                "ingotCobalt",
                'Y',
                ItemStack(GCBlocks.fluidTank, 1, 0),
                'V',
                ItemStack(GCBlocks.machineTiered, 1, 4),
                'C',
                ItemStack(GCBlocks.aluminumWire, 1, 1),
                'Z',
                ItemStack(GSBlocks.MACHINE_FRAMES, 1, 0),
                'M',
                BasicItems.WAFER_MODERN.getItemStack()
            ) as Array<Any?>)
        )
        RecipeUtil.addRecipe(
            ItemStack(GSBlocks.LIQUID_EXTRACTOR, 1, 0),
            arrayOf<Any?>(
                "XZX",
                "MCM",
                "VBV",
                'X',
                plateSteel,
                'Z',
                ItemStack(GCBlocks.fluidTank, 1, 0),
                'M',
                ItemStack(GCBlocks.aluminumWire, 1, 1),
                'C',
                ItemStack(GSBlocks.MACHINE_FRAMES, 1, 0),
                'V',
                FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 14),
                'B',
                ItemStack(GCBlocks.oxygenPipe, 1, 0)
            )
        )
        RecipeUtil.addRecipe(
            ItemStack(GSBlocks.LIQUID_SEPARATOR, 1, 0),
            (arrayOf<Any>(
                "XZX",
                "ZCZ",
                "VBV",
                'X',
                FoundationItemVariants.stack(requireNotNull(MarsItems.marsItemBasic), 1, 6),
                'Z',
                ItemStack(GCBlocks.fluidTank, 1, 0),
                'C',
                ItemStack(GSBlocks.MACHINE_FRAMES, 1, 0),
                'V',
                BasicItems.WAFER_MODERN.getItemStack(),
                'B',
                ItemStack(GCBlocks.aluminumWire, 1, 1)
            ) as Array<Any?>)
        )

        //RecipeUtil.addRecipe(new ItemStack(GSBlocks.AdvFuelLoader, 1, 0), new Object[] { "XZX", "ACA", "VBV", 'X', new ItemStack(GSItems.HeavyDutyPlates, 1, 0), 'Z', new ItemStack(GCItems.canister, 1, 0), 'C', new ItemStack(GSBlocks.MACHINE_FRAMES, 1, 2), 'B', new ItemStack(GCItems.oilCanister, 1, GCItems.oilCanister.getMaxDamage()), 'V', new ItemStack(GCBlocks.aluminumWire, 1, 1), 'A', new ItemStack(MarsBlocks.hydrogenPipe, 1, 0)});
        RecipeUtil.addRecipe(
            ItemStack(GSBlocks.HYDROPONIC_BASE, 1, 0),
            arrayOf<Any?>(
                "ABC",
                "DED",
                "DFD",
                'A',
                FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 20),
                'B',
                ItemStack(GCBlocks.fluidTank, 1, 0),
                'C',
                ItemStack(GCItems.oxygenConcentrator, 1, 0),
                'D',
                plateSteel,
                'E',
                ItemStack(GSBlocks.MACHINE_FRAMES, 1, 0),
                'F',
                ItemStack(GCBlocks.aluminumWire, 1, 1)
            )
        )
        RecipeUtil.addRecipe(
            ItemStack(GSBlocks.HYDROPONIC_FARM, 1, 0),
            (arrayOf<Any>(
                "XXX",
                "XCX",
                "VBV",
                'X',
                ItemStack(Blocks.GLASS, 1, 0),
                'B',
                ItemStack(GSBlocks.MACHINE_FRAMES, 1, 0),
                'C',
                "dirt",
                'V',
                ItemStack(GCBlocks.aluminumWire, 1, 1)
            ) as Array<Any?>)
        )

        RecipeUtil.addRecipe(
            ItemStack(GSBlocks.RADIATION_STABILISER, 1, 0),
            arrayOf<Any?>(
                "XYX",
                "ZCZ",
                "XBX",
                'X',
                BasicItems.DOLOMITE_CRYSTAL.getItemStack(),
                'B',
                BasicItems.WAFER_MODERN.getItemStack(),
                'C',
                ItemStack(GSBlocks.MACHINE_FRAMES, 1, 0),
                'Z',
                ItemStack(GCBlocks.aluminumWire, 1, 1),
                'Y',
                blockCopper
            )
        )

        RecipeUtil.addRecipe(
            ItemStack(GSBlocks.PANEL_CONTROLLER, 1, 0),
            arrayOf<Any?>(
                "XXX",
                "ZCV",
                "NBN",
                'X',
                plateTitanium,
                'B',
                BasicItems.DOLOMITE_CRYSTAL.getItemStack(),
                'C',
                ItemStack(GSBlocks.MACHINE_FRAMES, 1, 2),
                'N',
                ItemStack(GCBlocks.aluminumWire, 1, 1),
                'Z',
                BasicItems.WAFER_MODERN.getItemStack(),
                'V',
                ItemStack(GCBlocks.screen, 1, 0)
            )
        )

        RecipeUtil.addRecipe(
            ItemStack(GSBlocks.MODIFICATION_TABLE, 1, 0),
            arrayOf<Any?>(
                "XYX",
                "ZCZ",
                "VBV",
                'V',
                plateBronze,
                'X',
                FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 14),
                'B',
                BasicItems.WAFER_MODERN.getItemStack(),
                'C',
                ItemStack(GSBlocks.MACHINE_FRAMES, 1, 0),
                'Z',
                ItemStack(Items.REDSTONE, 1, 0),
                'Y',
                "plateCobalt"
            )
        )

        RecipeUtil.addRecipe(
            ItemStack(GSBlocks.GAS_BURNER, 1, 0),
            (arrayOf<Any>(
                "XYX",
                "ZCZ",
                "VBV",
                'V',
                ItemStack(GCBlocks.aluminumWire, 1, 1),
                'X',
                ItemStack(GCBlocks.fluidTank, 1, 0),
                'B',
                BasicItems.WAFER_MODERN.getItemStack(),
                'C',
                ItemStack(GSBlocks.MACHINE_FRAMES, 1, 0),
                'Z',
                ItemStack(Items.FLINT_AND_STEEL, 1, 0),
                'Y',
                ItemStack(GCItems.oxygenVent, 1, 0)
            ) as Array<Any?>)
        )
        RecipeUtil.addRecipe(
            ItemStack(GSBlocks.ADVANCED_CIRCUIT_FABRICATOR, 1, 0),
            arrayOf<Any?>(
                "XYX",
                "ZCZ",
                "XBX",
                'X',
                plateDesh,
                'B',
                BasicItems.WAFER_MODERN.getItemStack(),
                'C',
                ItemStack(GSBlocks.MACHINE_FRAMES, 1, 1),
                'Z',
                BasicItems.DOLOMITE_CRYSTAL.getItemStack(),
                'Y',
                ItemStack(GCBlocks.machineBase2, 1, 4)
            )
        )

        RecipeUtil.addRecipe(
            ItemStack(GSBlocks.PLANET_SHIELD, 1, 0),
            (arrayOf<Any>(
                "XYX",
                "VCV",
                "ZZZ",
                'V',
                ItemStack(GCBlocks.aluminumWire, 1, 1),
                'X',
                FoundationItemVariants.stack(requireNotNull(MarsItems.marsItemBasic), 1, 6),
                'C',
                ItemStack(GSBlocks.MACHINE_FRAMES, 1, 2),
                'Z',
                FoundationItemVariants.stack(GSItems.HDP, 1, 0),
                'Y',
                ItemStack(GSBlocks.GRAVITATION_MODULE, 1, 0)
            ) as Array<Any?>)
        )
        RecipeUtil.addRecipe(
            ItemStack(GSBlocks.ENERGY_PAD, 1, 0),
            (arrayOf<Any>(
                "XYX",
                "VCV",
                "ZWZ",
                'W',
                ItemStack(GCBlocks.aluminumWire, 1, 1),
                'Z',
                FoundationItemVariants.stack(requireNotNull(MarsItems.marsItemBasic), 1, 5),
                'C',
                BasicItems.WAFER_MODERN.getItemStack(),
                'X',
                ItemStack(GSBlocks.FUTURE_GLASS_BASIC, 1, 0),
                'Y',
                ItemStack(GCBlocks.platform, 1, 0),
                'V',
                Items.REDSTONE
            ) as Array<Any?>)
        )

        RecipeUtil.addRecipe(
            ItemStack(GSBlocks.GAS_COLLECTOR, 1, 0),
            arrayOf<Any?>(
                "XYX",
                "ZCV",
                "XWX",
                'W',
                foundation.item.FoundationItemVariants.stack(requireNotNull(GCItems.canister), 1, 0),
                'Z',
                ItemStack(GCItems.oxygenFan, 1, 0),
                'C',
                ItemStack(GSBlocks.MACHINE_FRAMES, 1, 1),
                'X',
                plateDesh,
                'Y',
                ItemStack(GCItems.oxygenVent, 1, 0),
                'V',
                FoundationItemVariants.stack(requireNotNull(MarsItems.marsItemBasic), 1, 6)
            )
        )
        RecipeUtil.addRecipe(
            ItemStack(GSBlocks.GAS_GENERATOR, 1, 0),
            (arrayOf<Any>(
                "WYW",
                "WZW",
                "XVX",
                'W',
                foundation.item.FoundationItemVariants.stack(requireNotNull(GCItems.canister), 1, 0),
                'X',
                FoundationItemVariants.stack(requireNotNull(GCItems.itemBasicMoon), 1, 1),
                'Y',
                ItemStack(GCBlocks.spaceGlassClear, 1, OreDictionary.WILDCARD_VALUE),
                'Z',
                ItemStack(GSBlocks.FUEL_GENERATOR, 1, 0),
                'V',
                BasicItems.WAFER_MODERN.getItemStack()
            ) as Array<Any?>)
        )
        RecipeUtil.addRecipe(
            ItemStack(GSBlocks.ADVANCED_WIND_GENERATOR, 1, 0),
            arrayOf<Any?>(
                "XYX",
                "MYM",
                "CZC",
                'X',
                "ingotLead",
                'Y',
                FoundationItemVariants.stack(requireNotNull(MarsItems.marsItemBasic), 1, 1),
                'M',
                ItemStack(GCBlocks.aluminumWire, 1, 1),
                'Z',
                ItemStack(GSBlocks.MACHINE_FRAMES, 1, 1),
                'C',
                plateDesh
            )
        )
    }

    @JvmStatic
    private fun addItemRecipes() {
        RecipeUtil.addRecipe(ItemStack(GSItems.COBALT_HELMET, 1), (arrayOf<Any>("XXX", "X X", 'X', "ingotCobalt") as Array<Any?>))
        RecipeUtil.addRecipe(ItemStack(GSItems.COBALT_CHEST, 1), (arrayOf<Any>("X X", "XXX", "XXX", 'X', "ingotCobalt") as Array<Any?>))
        RecipeUtil.addRecipe(ItemStack(GSItems.COBALT_LEGS, 1), (arrayOf<Any>("XXX", "X X", "X X", 'X', "ingotCobalt") as Array<Any?>))
        RecipeUtil.addRecipe(ItemStack(GSItems.COBALT_BOOTS, 1), (arrayOf<Any>("X X", "X X", 'X', "ingotCobalt") as Array<Any?>))

        RecipeUtil.addRecipe(
            ItemStack(GSItems.COBALT_SWORD, 1),
            (arrayOf<Any>(" X ", " X ", " Y ", 'X', "ingotCobalt", 'Y', "stickWood") as Array<Any?>)
        )
        RecipeUtil.addRecipe(
            ItemStack(GSItems.COBALT_AXE, 1),
            (arrayOf<Any>("XX ", "XY ", " Y ", 'X', "ingotCobalt", 'Y', "stickWood") as Array<Any?>)
        )
        RecipeUtil.addRecipe(
            ItemStack(GSItems.COBALT_PICKAXE, 1),
            (arrayOf<Any>("XXX", " Y ", " Y ", 'X', "ingotCobalt", 'Y', "stickWood") as Array<Any?>)
        )
        RecipeUtil.addRecipe(
            ItemStack(GSItems.COBALT_SPADE, 1),
            (arrayOf<Any>(" X ", " Y ", " Y ", 'X', "ingotCobalt", 'Y', "stickWood") as Array<Any?>)
        )
        RecipeUtil.addRecipe(
            ItemStack(GSItems.COBALT_HOE, 1),
            (arrayOf<Any>("XX ", " Y ", " Y ", 'X', "ingotCobalt", 'Y', "stickWood") as Array<Any?>)
        )

        //RecipeUtil.addRecipe(BasicItems.DOLOMITE_CRYSTAL.getItemStack(), new Object[] { "XZX", "CYC", "BBB", 'X', new ItemStack(GCBlocks.aluminumWire, 1, 1), 'Y', new ItemStack(MarsItems.marsItemBasic, 1, 6), 'Z', Items.REDSTONE, 'C', new ItemStack(GSItems.INGOTS, 1, 0), 'B', plateSteel });
        RecipeUtil.addRecipe(
            BasicItems.ANTIRADIATION_TABLETS.getItemStack(3),
            arrayOf<Any?>(
                "XXX",
                "CYC",
                "XXX",
                'X',
                plateIron,
                'C',
                foundation.item.FoundationItemVariants.stack(requireNotNull(GCItems.canister), 1, 0),
                'Y',
                Items.MILK_BUCKET
            )
        )
        RecipeUtil.addRecipe(
            ItemStack(GSItems.JETPACK, 1, GSItems.JETPACK.getMaxDamage()),
            arrayOf<Any?>(
                "ABA",
                "CDC",
                "EFE",
                'A',
                GCBlocks.oxygenPipe,
                'B',
                ItemStack(GCItems.battery, 1, OreDictionary.WILDCARD_VALUE),
                'C',
                plateSteel,
                'D',
                GCItems.steelChestplate,
                'E',
                ItemStack(GCItems.fuelCanister, 1, 1),
                'F',
                ItemStack(GCBlocks.aluminumWire, 1, 1)
            )
        )


        RecipeUtil.addRecipe(
            ItemStack(GSItems.THERMAL_PADDING_3, 1, 0),
            (arrayOf<Any>("XXX", "X X", 'X', BasicItems.THERMAL_CLOTH_T3.getItemStack()) as Array<Any?>)
        )
        RecipeUtil.addRecipe(
            ItemStack(GSItems.THERMAL_PADDING_3, 1, 1),
            (arrayOf<Any>("X X", "XXX", "XXX", 'X', BasicItems.THERMAL_CLOTH_T3.getItemStack()) as Array<Any?>)
        )
        RecipeUtil.addRecipe(
            ItemStack(GSItems.THERMAL_PADDING_3, 1, 2),
            (arrayOf<Any>("XXX", "X X", "X X", 'X', BasicItems.THERMAL_CLOTH_T3.getItemStack()) as Array<Any?>)
        )
        RecipeUtil.addRecipe(
            ItemStack(GSItems.THERMAL_PADDING_3, 1, 3),
            (arrayOf<Any>("X X", "X X", 'X', BasicItems.THERMAL_CLOTH_T3.getItemStack()) as Array<Any?>)
        )

        RecipeUtil.addRecipe(
            ItemStack(GSItems.THERMAL_PADDING_4, 1, 0),
            (arrayOf<Any>("XXX", "X X", 'X', BasicItems.THERMAL_CLOTH_T4.getItemStack()) as Array<Any?>)
        )
        RecipeUtil.addRecipe(
            ItemStack(GSItems.THERMAL_PADDING_4, 1, 1),
            (arrayOf<Any>("X X", "XXX", "XXX", 'X', BasicItems.THERMAL_CLOTH_T4.getItemStack()) as Array<Any?>)
        )
        RecipeUtil.addRecipe(
            ItemStack(GSItems.THERMAL_PADDING_4, 1, 2),
            (arrayOf<Any>("XXX", "X X", "X X", 'X', BasicItems.THERMAL_CLOTH_T4.getItemStack()) as Array<Any?>)
        )
        RecipeUtil.addRecipe(
            ItemStack(GSItems.THERMAL_PADDING_4, 1, 3),
            (arrayOf<Any>("X X", "X X", 'X', BasicItems.THERMAL_CLOTH_T4.getItemStack()) as Array<Any?>)
        )

        RecipeUtil.addRecipe(
            ItemStack(GSItems.SPACE_SUIT_LIGHT_HELMET),
            arrayOf<Any?>(
                "XVX",
                "YZY",
                "XWX",
                'X',
                GCItems.canvas,
                'Y',
                plateAluminum,
                'V',
                ItemStack(GSItems.ADVANCED_BATTERY, 1, GSItems.ADVANCED_BATTERY.getMaxDamage()),
                'Z',
                ItemStack(GSItems.COBALT_HELMET),
                'W',
                ItemStack(Blocks.STAINED_GLASS_PANE, 1, OreDictionary.WILDCARD_VALUE)
            )
        )
        RecipeUtil.addRecipe(
            ItemStack(GSItems.SPACE_SUIT_LIGHT_BODY),
            arrayOf<Any?>(
                "XVX",
                "YZY",
                "XWX",
                'X',
                plateAluminum,
                'Y',
                GCItems.canvas,
                'Z',
                ItemStack(GSItems.COBALT_CHEST),
                'W',
                "plateNickel",
                'V',
                ItemStack(GSItems.ADVANCED_BATTERY, 1, GSItems.ADVANCED_BATTERY.getMaxDamage())
            )
        )
        RecipeUtil.addRecipe(
            ItemStack(GSItems.SPACE_SUIT_LIGHT_LEGGINS),
            arrayOf<Any?>(
                "XVX",
                "YZY",
                "XWX",
                'X',
                plateAluminum,
                'Y',
                GCItems.canvas,
                'Z',
                ItemStack(GSItems.COBALT_LEGS),
                'W',
                "plateNickel",
                'V',
                ItemStack(GSItems.ADVANCED_BATTERY, 1, GSItems.ADVANCED_BATTERY.getMaxDamage())
            )
        )
        RecipeUtil.addRecipe(
            ItemStack(GSItems.SPACE_SUIT_LIGHT_BOOTS),
            arrayOf<Any?>(
                "WVW",
                "XZX",
                "Y Y",
                'X',
                plateAluminum,
                'Y',
                GCItems.canvas,
                'Z',
                ItemStack(GSItems.COBALT_BOOTS),
                'W',
                "plateNickel",
                'V',
                ItemStack(GSItems.ADVANCED_BATTERY, 1, GSItems.ADVANCED_BATTERY.getMaxDamage())
            )
        )

        /*
      // int var2 = 100;
       for (int var2 = 0; var2 < 100; var2 += 100)
       {

               RecipeUtil.addRecipe(new ItemStack(GSItems.JetPack, 1, 100), new Object[] { "ABA", "CDC", "EFE", 'A', MarsBlocks.hydrogenPipe, 'B', new ItemStack(GCItems.battery, 1, var2), 'C', "compressedMeteoricIron", 'D', GCItems.steelChestplate, 'E', new ItemStack(GCItems.fuelCanister, 1, 1), 'F', new ItemStack(GCBlocks.aluminumWire, 1, 1) });
               RecipeUtil.addRecipe(new ItemStack(GSItems.PlasmaSword, 1), new Object[] { " X ", " X ", "ZVZ", 'X', "plateCobalt", 'Z', new ItemStack(GCBlocks.aluminumWire, 1, 0), 'V', new ItemStack(GCItems.battery, 1, var2) });
               RecipeUtil.addRecipe(new ItemStack(GSItems.PlasmaAxe, 1), new Object[] { "XX ", "XY ", "ZVZ", 'X', "plateCobalt", 'Z', new ItemStack(GCBlocks.aluminumWire, 1, 0), 'V', new ItemStack(GCItems.battery, 1, var2), 'Y', GCItems.flagPole });
               RecipeUtil.addRecipe(new ItemStack(GSItems.PlasmaPickaxe, 1), new Object[] { "XXX", " Y ", "ZVZ", 'X', "plateCobalt", 'Z', new ItemStack(GCBlocks.aluminumWire, 1, 0), 'V', new ItemStack(GCItems.battery, 1, var2), 'Y', GCItems.flagPole });
               RecipeUtil.addRecipe(new ItemStack(GSItems.PlasmaShovel, 1), new Object[] { " X ", " Y ", "ZVZ", 'X', "plateCobalt", 'Z', new ItemStack(GCBlocks.aluminumWire, 1, 0), 'V', new ItemStack(GCItems.battery, 1, var2), 'Y', GCItems.flagPole });
               RecipeUtil.addRecipe(new ItemStack(GSItems.PlasmaHoe, 1), new Object[] { "XX ", " Y ", "ZVZ", 'X', "plateCobalt", 'Z', new ItemStack(GCBlocks.aluminumWire, 1, 0), 'V', new ItemStack(GCItems.battery, 1, var2), 'Y', GCItems.flagPole });

       }
      */
        RecipeUtil.addRecipe(
            ItemStack(GSItems.ADVANCED_BATTERY, 1, GSItems.ADVANCED_BATTERY.getMaxDamage()),
            arrayOf<Any?>(
                " T ",
                "TRT",
                "TCT",
                'T',
                "plateNickel",
                'R',
                ItemStack(GCItems.battery, 1, (GCItems.battery)!!.getMaxDamage()),
                'C',
                Items.REDSTONE
            )
        )
        RecipeUtil.addRecipe(
            ItemStack(GSItems.MODERN_BATTERY, 1, GSItems.MODERN_BATTERY.getMaxDamage()),
            arrayOf<Any?>(
                "RTR",
                "TYT",
                "TCT",
                'T',
                plateTitanium,
                'R',
                ItemStack(GSItems.ADVANCED_BATTERY, 1, GSItems.ADVANCED_BATTERY.getMaxDamage()),
                'C',
                Items.REDSTONE,
                'Y',
                BasicItems.SULFUR.getItemStack()
            )
        )
        RecipeUtil.addRecipe(
            ItemStack(GSItems.EXTRA_BATTERY, 1, GSItems.EXTRA_BATTERY.getMaxDamage()),
            arrayOf<Any?>(
                "RTR",
                "TYT",
                "TCT",
                'T',
                FoundationItemVariants.stack(GSItems.HDP, 1, 0),
                'R',
                ItemStack(GSItems.MODERN_BATTERY, 1, GSItems.MODERN_BATTERY.getMaxDamage()),
                'C',
                Items.REDSTONE,
                'Y',
                BasicItems.SULFUR.getItemStack()
            )
        )
        RecipeUtil.addRecipe(
            ItemStack(GSItems.ULTIMATE_BATTERY, 1, GSItems.ULTIMATE_BATTERY.getMaxDamage()),
            arrayOf<Any?>(
                "RTR",
                "TYT",
                "TCT",
                'T',
                FoundationItemVariants.stack(GSItems.HDP, 1, 0),
                'R',
                ItemStack(GSItems.EXTRA_BATTERY, 1, GSItems.EXTRA_BATTERY.getMaxDamage()),
                'C',
                Items.REDSTONE,
                'Y',
                BasicItems.UNKNOW_CRYSTAL.getItemStack()
            )
        )

        //RecipeUtil.addRecipe(new ItemStack(GSItems.QuantBow, 1), new Object[] { " XY", "XTY", " XY", 'X', "ingotCobalt", 'Y', Items.string, 'T', Items.blaze_rod });
        RecipeUtil.addRecipe(
            BasicItems.PART_SOLARFLARES.getItemStack(),
            arrayOf<Any?>(
                "X  ",
                "YX ",
                "VZX",
                'Y',
                plateSteel,
                'X',
                GCItems.flagPole,
                'V',
                FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 0),
                'Z',
                plateIron
            )
        )
        RecipeUtil.addRecipe(
            BasicItems.SOLARFLARES.getItemStack(),
            arrayOf<Any?>("YXY", "XYX", "YXY", 'X', BasicItems.PART_SOLARFLARES.getItemStack(), 'Y', GCItems.flagPole)
        )
        RecipeUtil.addRecipe(
            FoundationItemVariants.stack(GSItems.ROCKET_MODULES, 1, 0),
            arrayOf<Any?>(
                "ZXX",
                "XYV",
                "BNB",
                'Z',
                FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 19),
                'X',
                FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 9),
                'Y',
                ItemStack(GCItems.partBuggy, 1, 1),
                'V',
                FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 6),
                'B',
                GCItems.flagPole,
                'N',
                ItemStack(GCItems.rocketEngine, 1, 0)
            )
        )
        RecipeUtil.addRecipe(
            FoundationItemVariants.stack(GSItems.ROCKET_MODULES, 1, 1),
            arrayOf<Any?>(
                "ZXZ",
                "YVY",
                "ZYZ",
                'Z',
                ItemStack(Items.STRING, 1, 0),
                'X',
                ItemStack(GCItems.parachute, 1, OreDictionary.WILDCARD_VALUE),
                'Y',
                GCItems.canvas,
                'V',
                FoundationItemVariants.stack(GSItems.ROCKET_MODULES, 1, 0)
            )
        )
        RecipeUtil.addRecipe(
            FoundationItemVariants.stack(GSItems.ROCKET_MODULES, 1, 2),
            arrayOf<Any?>(
                "ZXZ",
                "YWY",
                "ZZZ",
                'Z',
                plateSteel,
                'X',
                ItemStack(Blocks.CHEST, 1, 0),
                'Y',
                ItemStack(GCItems.flagPole, 1, 0),
                'W',
                ItemStack(GCItems.partBuggy, 1, 1)
            )
        )

        RecipeUtil.addRecipe(
            ItemOxygenTank.stack(requireNotNull(GSItems.OXYGENTANK_TIER_4), 1, ItemOxygenTank.capacityOf(requireNotNull(GSItems.OXYGENTANK_TIER_4))),
            arrayOf<Any?>(
                "XYX",
                "ZCZ",
                "ZZZ",
                'X',
                ItemStack(Blocks.WOOL, 1, 11),
                'Y',
                GCBlocks.oxygenPipe,
                'C',
                ItemStack(GCItems.oxTankHeavy, 1, OreDictionary.WILDCARD_VALUE),
                'Z',
                FoundationItemVariants.stack(requireNotNull(AsteroidsItems.basicItem), 1, 6)
            )
        )
        RecipeUtil.addRecipe(
            ItemOxygenTank.stack(requireNotNull(GSItems.OXYGENTANK_TIER_5), 1, ItemOxygenTank.capacityOf(requireNotNull(GSItems.OXYGENTANK_TIER_5))),
            arrayOf<Any?>(
                "XYX",
                "ZCZ",
                "ZZZ",
                'X',
                ItemStack(GCItems.oxygenConcentrator, 1, 0),
                'Y',
                GCBlocks.oxygenPipe,
                'C',
                ItemStack(GSItems.OXYGENTANK_TIER_4, 1, OreDictionary.WILDCARD_VALUE),
                'Z',
                FoundationItemVariants.stack(requireNotNull(AsteroidsItems.basicItem), 1, 6)
            )
        )
        RecipeUtil.addRecipe(
            ItemOxygenTank.stack(requireNotNull(GSItems.OXYGENTANK_TIER_6), 1, ItemOxygenTank.capacityOf(requireNotNull(GSItems.OXYGENTANK_TIER_6))),
            arrayOf<Any?>(
                "XYA",
                "ZCZ",
                "ZZZ",
                'X',
                ItemStack(GCItems.oxygenVent, 1, 0),
                'Y',
                GCBlocks.oxygenPipe,
                'C',
                ItemStack(GSItems.OXYGENTANK_TIER_5, 1, OreDictionary.WILDCARD_VALUE),
                'Z',
                FoundationItemVariants.stack(requireNotNull(AsteroidsItems.basicItem), 1, 6),
                'A',
                GCItems.oxygenConcentrator
            )
        )

        RecipeUtil.addRecipe(
            BasicItems.MODULE_BASE.getItemStack(),
            arrayOf<Any?>(
                "XYX",
                "CZC",
                "XBX",
                'X',
                plateTin,
                'Y',
                Items.GOLD_INGOT,
                'Z',
                FoundationItemVariants.stack(GSItems.COMPRESSED_PLATES, 1, 1),
                'C',
                Items.REDSTONE,
                'B',
                FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 13)
            )
        )

        RecipeUtil.addRecipe(
            ItemStack(GSItems.UPGRADES, 1, 0),
            (arrayOf<Any>(
                " Z ",
                "XYX",
                "BCB",
                'X',
                ItemStack(GCBlocks.aluminumWire, 1, 1),
                'Y',
                BasicItems.MODULE_BASE.getItemStack(),
                'Z',
                BasicItems.DOLOMITE_CRYSTAL.getItemStack(),
                'C',
                BasicItems.VOLCANIC_STONE.getItemStack(),
                'B',
                "ingotCobalt"
            ) as Array<Any?>)
        )
        RecipeUtil.addRecipe(
            ItemStack(GSItems.UPGRADES, 1, 1),
            (arrayOf<Any>(
                " Z ",
                "XYX",
                "BCB",
                'X',
                ItemStack(GCBlocks.aluminumWire, 1, 1),
                'Y',
                BasicItems.MODULE_BASE.getItemStack(),
                'Z',
                BasicItems.DOLOMITE_CRYSTAL.getItemStack(),
                'C',
                BasicItems.UNKNOW_CRYSTAL.getItemStack(),
                'B',
                "ingotMagnesium"
            ) as Array<Any?>)
        )
        RecipeUtil.addRecipe(
            ItemStack(GSItems.UPGRADES, 1, 2),
            (arrayOf<Any>(
                " Z ",
                "XYX",
                "BCB",
                'X',
                ItemStack(GCBlocks.aluminumWire, 1, 1),
                'Y',
                BasicItems.MODULE_BASE.getItemStack(),
                'Z',
                ItemStack(AsteroidsItems.canisterLN2, 1, 1),
                'C',
                BasicItems.WAFER_MODERN.getItemStack(),
                'B',
                "ingotNickel"
            ) as Array<Any?>)
        )
        RecipeUtil.addRecipe(
            ItemStack(GSItems.UPGRADES, 1, 3),
            (arrayOf<Any>(
                " Z ",
                "XYX",
                "BCB",
                'X',
                ItemStack(GCBlocks.aluminumWire, 1, 1),
                'Y',
                BasicItems.MODULE_BASE.getItemStack(),
                'Z',
                FoundationItemVariants.stack(requireNotNull(VenusItems.basicItem), 1, 4),
                'C',
                BasicItems.WAFER_MODERN.getItemStack(),
                'B',
                "ingotCobalt"
            ) as Array<Any?>)
        )

        RecipeUtil.addRecipe(
            BasicItems.THERMAL_CLOTH_T3.getItemStack(),
            (arrayOf<Any>(
                "XZX",
                "CYC",
                "XZX",
                'X',
                BasicItems.DOLOMITE_MEAL.getItemStack(),
                'Y',
                ItemStack(AsteroidsItems.canisterLN2, 1, 1),
                'Z',
                BasicItems.VOLCANIC_STONE.getItemStack(),
                'C',
                FoundationItemVariants.stack(requireNotNull(VenusItems.basicItem), 1, 3)
            ) as Array<Any?>)
        )
        RecipeUtil.addRecipe(
            BasicItems.THERMAL_CLOTH_T4.getItemStack(),
            (arrayOf<Any>(
                "XZX",
                "CYC",
                "XZX",
                'X',
                FoundationItemVariants.stack(requireNotNull(VenusItems.basicItem), 1, 4),
                'Y',
                BasicItems.ICE_BUCKET.getItemStack(),
                'Z',
                BasicItems.UNKNOW_CRYSTAL.getItemStack(),
                'C',
                BasicItems.THERMAL_CLOTH_T3.getItemStack()
            ) as Array<Any?>)
        )

        RecipeUtil.addRecipe(
            BasicItems.WOLF_THERMAL_SUIT.getItemStack(),
            (arrayOf<Any>("X X", "XXX", "X X", 'X', FoundationItemVariants.stack(requireNotNull(AsteroidsItems.basicItem), 1, 7)) as Array<Any?>)
        )
        RecipeUtil.addRecipe(
            BasicItems.ANIMAL_CAGE.getItemStack(),
            arrayOf<Any?>(
                "XXX",
                "ZYX",
                "XXX",
                'X',
                plateSteel,
                'Z',
                ItemStack(GCBlocks.grating, 1, 0),
                'Y',
                ItemStack(Blocks.CARPET, 1, OreDictionary.WILDCARD_VALUE)
            )
        )

        RecipeUtil.addCustomRecipe(
            ShapedRecipeNBT(
                BasicItems.ADVANCED_EMERGENCY_KIT.getItemStack(),
                ItemBasicGS.getRecipe()
            )
        )
        RecipeUtil.addCustomRecipe(
            ShapedRecipeNBT(
                BasicItems.COLONIST_KIT.getItemStack(),
                ItemBasicGS.getColonistKitRecipe()
            )
        )

        RecipeUtil.addRecipe(
            FoundationItemVariants.stack(GSItems.ROCKET_MODULES, 1, 8),
            (arrayOf<Any>(
                "XY ",
                "   ",
                "   ",
                'X',
                BasicItems.BLANK_SCHEMATIC.getItemStack(),
                'Y',
                ItemStack(AsteroidsItems.tier3Rocket, 1, OreDictionary.WILDCARD_VALUE)
            ) as Array<Any?>)
        )
        RecipeUtil.addRecipe(
            FoundationItemVariants.stack(GSItems.ROCKET_MODULES, 1, 9),
            (arrayOf<Any>(
                "XY ",
                "   ",
                "   ",
                'X',
                BasicItems.BLANK_SCHEMATIC.getItemStack(),
                'Y',
                ItemStack(GSItems.ROCKET_TIER_4, 1, OreDictionary.WILDCARD_VALUE)
            ) as Array<Any?>)
        )
        RecipeUtil.addRecipe(
            FoundationItemVariants.stack(GSItems.ROCKET_MODULES, 1, 10),
            (arrayOf<Any>(
                "XY ",
                "   ",
                "   ",
                'X',
                BasicItems.BLANK_SCHEMATIC.getItemStack(),
                'Y',
                ItemStack(GSItems.ROCKET_TIER_5, 1, OreDictionary.WILDCARD_VALUE)
            ) as Array<Any?>)
        )

        RecipeUtil.addRecipe(
            BasicItems.IRON_FAN.getItemStack(),
            arrayOf<Any?>(
                "XYX",
                "YZY",
                "XYX",
                'X',
                plateIron,
                'Y',
                Items.REDSTONE,
                'Z',
                FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 13)
            )
        )
        RecipeUtil.addRecipe(
            BasicItems.STEEL_FAN.getItemStack(),
            arrayOf<Any?>(
                " X ",
                "YZY",
                " W ",
                'X',
                FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 14),
                'Y',
                FoundationItemVariants.stack(requireNotNull(MarsItems.marsItemBasic), 1, 3),
                'Z',
                ItemStack(GCItems.oxygenFan, 1, 0),
                'W',
                Items.REDSTONE
            )
        )

        // Metal ingot and nugget conversions are loaded from native recipe data

        RecipeUtil.addRecipe(
            ItemStack(GSItems.GAS_EXTRACTOR, 1, GSItems.GAS_EXTRACTOR.getMaxDamage()),
            arrayOf<Any?>(
                "XY ",
                "YZC",
                " CV",
                'X',
                ItemStack(GCBlocks.oxygenPipe, 1, 0),
                'Y',
                "dyeCyan",
                'Z',
                ItemStack(GCItems.oxygenVent, 1, 0),
                'C',
                plateSteel,
                'V',
                FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 14)
            )
        )
    }

    @JvmStatic
    private fun addBlockSmelting() {
        GameRegistry.addSmelting(ItemStack(GSBlocks.OVERWORLD_ORES, 1, 0), FoundationItemVariants.stack(GSItems.INGOTS, 1, 0), 1.0f)
        GameRegistry.addSmelting(ItemStack(GSBlocks.OVERWORLD_ORES, 1, 1), FoundationItemVariants.stack(GSItems.INGOTS, 1, 2), 1.0f)
        GameRegistry.addSmelting(BasicItems.ICE_BUCKET.getItemStack(), ItemStack(Items.WATER_BUCKET), 0.0f)
        GameRegistry.addSmelting(BasicItems.HEMATITE.getItemStack(), ItemStack(Items.IRON_INGOT, 2), 1.0f)
        GameRegistry.addSmelting(BasicItems.RAW_PLASTIC.getItemStack(), BasicItems.PLASTIC.getItemStack(), 0.0f)
    }

    @JvmStatic
    private fun addCompressor() {
        CompressorRecipes.addShapelessRecipe(
            FoundationItemVariants.stack(GSItems.COMPRESSED_PLATES, 1, 0),
            Items.COAL,
            Items.COAL,
            Items.COAL,
            Items.COAL,
            Items.COAL,
            Items.COAL,
            Items.COAL,
            Items.COAL,
            Items.COAL
        )
        CompressorRecipes.addShapelessRecipe(
            FoundationItemVariants.stack(requireNotNull(GCItems.itemBasicMoon), 1, 0),
            BasicItems.METEORIC_IRON_FRAGMENTS.getItemStack(),
            BasicItems.METEORIC_IRON_FRAGMENTS.getItemStack(),
            BasicItems.METEORIC_IRON_FRAGMENTS.getItemStack(),
            BasicItems.METEORIC_IRON_FRAGMENTS.getItemStack(),
            BasicItems.METEORIC_IRON_FRAGMENTS.getItemStack(),
            BasicItems.METEORIC_IRON_FRAGMENTS.getItemStack(),
            BasicItems.METEORIC_IRON_FRAGMENTS.getItemStack(),
            BasicItems.METEORIC_IRON_FRAGMENTS.getItemStack(),
            BasicItems.METEORIC_IRON_FRAGMENTS.getItemStack()
        )

        CompressorRecipes.addShapelessRecipe(FoundationItemVariants.stack(GSItems.COMPRESSED_PLATES, 1, 0), Blocks.COAL_BLOCK)
        CompressorRecipes.addShapelessRecipe(FoundationItemVariants.stack(GSItems.COMPRESSED_PLATES, 1, 1), "ingotCobalt", "ingotCobalt")
        CompressorRecipes.addShapelessRecipe(
            FoundationItemVariants.stack(GSItems.COMPRESSED_PLATES, 1, 2),
            "ingotMagnesium",
            "ingotMagnesium"
        )
        CompressorRecipes.addShapelessRecipe(FoundationItemVariants.stack(GSItems.COMPRESSED_PLATES, 1, 3), "ingotNickel", "ingotNickel")
        CompressorRecipes.addShapelessRecipe(
            ItemStack(Items.COAL),
            MarsItems.carbonFragments,
            MarsItems.carbonFragments,
            MarsItems.carbonFragments,
            MarsItems.carbonFragments,
            MarsItems.carbonFragments,
            MarsItems.carbonFragments,
            MarsItems.carbonFragments,
            MarsItems.carbonFragments,
            MarsItems.carbonFragments
        )
    }

    @JvmStatic
    private fun addCircuitFabricator() {
        val modernWafers: ItemStack = BasicItems.WAFER_MODERN.getItemStack()

        CircuitFabricatorRecipes.addRecipe(
            modernWafers,
            Arrays.asList<Any?>(
                ItemStack(Items.DIAMOND),
                FoundationItemVariants.stack(requireNotNull(GCItems.itemBasicMoon), 1, 2),
                FoundationItemVariants.stack(requireNotNull(GCItems.itemBasicMoon), 1, 2),
                ItemStack(Items.REDSTONE),
                FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 14)
            )
        )
    }

    @JvmStatic
    private fun addAssembly() {
        if (GSConfigSchematics.enableDuplicateSchematic) {
            AssemblyRecipes.addShapelessRecipe(
                BasicItems.BLANK_SCHEMATIC.getItemStack(),
                "compressedMeteoricIron",
                ItemStack(Items.DYE, 1, 0),
                "compressedMeteoricIron",
                ItemStack(Items.DYE, 1, 5),
                ItemStack(Items.PAPER, 1, 0),
                ItemStack(Items.DYE, 1, 5),
                "compressedMeteoricIron",
                ItemStack(Items.DYE, 1, 0),
                "compressedMeteoricIron"
            )

            for (i: Int in 0..5) AssemblyRecipes.addShapelessRecipe(
                foundation.item.FoundationItemVariants.stack(requireNotNull(GSItems.SCHEMATICS), 2, i),
                BasicItems.BLANK_SCHEMATIC.getItemStack(),
                foundation.item.FoundationItemVariants.stack(requireNotNull(GSItems.SCHEMATICS), 1, i)
            )

            AssemblyRecipes.addShapelessRecipe(
                foundation.item.FoundationItemVariants.stack(requireNotNull(GCItems.schematic), 2, 0),
                BasicItems.BLANK_SCHEMATIC.getItemStack(),
                foundation.item.FoundationItemVariants.stack(requireNotNull(GCItems.schematic), 1, 0)
            )
            AssemblyRecipes.addShapelessRecipe(
                foundation.item.FoundationItemVariants.stack(requireNotNull(GCItems.schematic), 2, 1),
                BasicItems.BLANK_SCHEMATIC.getItemStack(),
                foundation.item.FoundationItemVariants.stack(requireNotNull(GCItems.schematic), 1, 1)
            )
        }

        AssemblyRecipes.addShapelessRecipe(
            FoundationItemVariants.stack(GSItems.COMPRESSED_PLATES, 1, 4),
            plateBronze,
            "plateCoal",
            plateAluminum
        )
        AssemblyRecipes.addShapelessRecipe(
            BasicItems.MODULE_SMALLCANISTER.getItemStack(),
            plateSteel,
            plateSteel,
            plateSteel,
            plateSteel,
            Blocks.GLASS_PANE,
            plateSteel,
            plateSteel,
            plateSteel,
            plateSteel
        )
        AssemblyRecipes.addShapelessRecipe(
            FoundationItemVariants.stack(GSItems.ROCKET_MODULES, 1, 3),
            FoundationItemVariants.stack(GSItems.COMPRESSED_PLATES, 1, 4),
            BasicItems.MODULE_SMALLCANISTER.getItemStack(),
            FoundationItemVariants.stack(GSItems.COMPRESSED_PLATES, 1, 4)
        )

        AssemblyRecipes.addShapelessRecipe(
            ItemStack(GSItems.SPACE_SUIT_HELMET, 1),
            FoundationItemVariants.stack(GSItems.COMPRESSED_PLATES, 1, 4),
            plateDesh,
            FoundationItemVariants.stack(GSItems.COMPRESSED_PLATES, 1, 4),
            plateDesh,
            ItemStack(Blocks.STAINED_GLASS_PANE, 1, OreDictionary.WILDCARD_VALUE),
            plateDesh,
            FoundationItemVariants.stack(GSItems.COMPRESSED_PLATES, 1, 4),
            GCItems.oxygenConcentrator,
            FoundationItemVariants.stack(GSItems.COMPRESSED_PLATES, 1, 4)
        )
        AssemblyRecipes.addShapelessRecipe(
            ItemStack(GSItems.SPACE_SUIT_BODY, 1),
            plateDesh,
            "ingotNickel",
            plateDesh,
            FoundationItemVariants.stack(GSItems.COMPRESSED_PLATES, 1, 4),
            ItemStack(GCItems.steelChestplate, 1, 0),
            FoundationItemVariants.stack(GSItems.COMPRESSED_PLATES, 1, 4),
            plateDesh,
            FoundationItemVariants.stack(GSItems.COMPRESSED_PLATES, 1, 4),
            plateDesh
        )
        AssemblyRecipes.addShapelessRecipe(
            ItemStack(GSItems.SPACE_SUIT_LEGGINS, 1),
            FoundationItemVariants.stack(GSItems.COMPRESSED_PLATES, 1, 4),
            plateDesh,
            FoundationItemVariants.stack(GSItems.COMPRESSED_PLATES, 1, 4),
            plateDesh,
            "ingotNickel",
            plateDesh,
            plateDesh,
            "ingotNickel",
            plateDesh
        )
        AssemblyRecipes.addShapelessRecipe(
            ItemStack(GSItems.SPACE_SUIT_BOOTS, 1),
            plateDesh,
            FoundationItemVariants.stack(GSItems.COMPRESSED_PLATES, 1, 4),
            plateDesh,
            plateDesh,
            FoundationItemVariants.stack(GSItems.COMPRESSED_PLATES, 1, 4),
            plateDesh,
            "ingotNickel",
            FoundationItemVariants.stack(GSItems.COMPRESSED_PLATES, 1, 4),
            "ingotNickel"
        )

        AssemblyRecipes.addShapelessRecipe(
            FoundationItemVariants.stack(GSItems.HDP, 1, 0),
            "plateMagnesium",
            FoundationItemVariants.stack(GSItems.COMPRESSED_PLATES, 1, 4),
            FoundationItemVariants.stack(requireNotNull(AsteroidsItems.basicItem), 1, 5)
        )
        AssemblyRecipes.addShapelessRecipe(
            FoundationItemVariants.stack(GSItems.HDP, 1, 1),
            "plateCobalt",
            FoundationItemVariants.stack(GSItems.HDP, 1, 0),
            "plateNickel",
            BasicItems.DOLOMITE_MEAL.getItemStack()
        )
        AssemblyRecipes.addShapelessRecipe(
            FoundationItemVariants.stack(GSItems.HDP, 1, 2),
            FoundationItemVariants.stack(GSItems.COMPRESSED_PLATES, 1, 4),
            FoundationItemVariants.stack(GSItems.HDP, 1, 1),
            BasicItems.VOLCANIC_STONE.getItemStack()
        )

        AssemblyRecipes.addShapelessRecipe(
            ItemStack(GSBlocks.SINGLE_SOLARPANEL, 1, 0),
            "blockGlass",
            "blockGlass",
            "blockGlass",
            FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 0),
            FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 0),
            FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 0),
            FoundationItemVariants.stack(requireNotNull(MarsItems.marsItemBasic), 1, 5),
            BasicItems.WAFER_MODERN.getItemStack(),
            FoundationItemVariants.stack(requireNotNull(MarsItems.marsItemBasic), 1, 5)
        )
        AssemblyRecipes.addShapelessRecipe(
            ItemStack(GSBlocks.MODERN_SINGLE_SOLARPANEL, 1, 0),
            BasicItems.DOLOMITE_MEAL.getItemStack(),
            ItemStack(Items.GOLD_INGOT, 1, 0),
            BasicItems.DOLOMITE_MEAL.getItemStack(),
            FoundationItemVariants.stack(GSItems.COMPRESSED_PLATES, 1, 1),
            ItemStack(GSBlocks.SINGLE_SOLARPANEL, 1, 0),
            FoundationItemVariants.stack(GSItems.COMPRESSED_PLATES, 1, 1)
        )

        AssemblyRecipes.addShapelessRecipe(
            ItemStack(GSBlocks.ADVANCED_LANDING_PAD_SINGLE, 5, 0),
            plateTitanium,
            plateTitanium,
            plateTitanium,
            FoundationItemVariants.stack(GSItems.COMPRESSED_PLATES, 1, 4),
            FoundationItemVariants.stack(GSItems.COMPRESSED_PLATES, 1, 4),
            FoundationItemVariants.stack(GSItems.COMPRESSED_PLATES, 1, 4),
            ItemStack(GCBlocks.basicBlock, 1, 12),
            ItemStack(GCBlocks.basicBlock, 1, 12),
            ItemStack(GCBlocks.basicBlock, 1, 12)
        )

        if (!GSConfigCore.enableAdvancedRocketCraft) {
            for (i: Int in 0..2) {
                AssemblyRecipes.addRecipe(
                    FoundationItemVariants.stack(GSItems.ROCKET_PARTS, 1, 5 * i + 5),
                    *arrayOf<Any>(
                        " X ",
                        "XYX",
                        "ZWZ",
                        'X',
                        FoundationItemVariants.stack(GSItems.HDP, 1, i),
                        'Y',
                        ItemStack(GCItems.partBuggy, 1, 1),
                        'Z',
                        ItemStack(GCBlocks.sealableBlock, 1, 14),
                        'W',
                        ItemOxygenTank.stack(requireNotNull(GCItems.oxTankHeavy), 1, 0)
                    )
                )
                AssemblyRecipes.addRecipe(
                    FoundationItemVariants.stack(GSItems.ROCKET_PARTS, 1, 5 * i + 6),
                    *arrayOf<Any>(
                        "XYX",
                        "ZCW",
                        "XAX",
                        'X',
                        FoundationItemVariants.stack(GSItems.HDP, 1, i),
                        'Y',
                        FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 20),
                        'Z',
                        ItemStack(GCItems.oxygenConcentrator, 1, 0),
                        'C',
                        FoundationItemVariants.stack(GSItems.ROCKET_MODULES, 1, 3),
                        'W',
                        FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 14),
                        'A',
                        FoundationItemVariants.stack(requireNotNull(AsteroidsItems.basicItem), 1, 1)
                    )
                )
                AssemblyRecipes.addRecipe(
                    FoundationItemVariants.stack(GSItems.ROCKET_PARTS, 1, 5 * i + 7),
                    *arrayOf<Any>(
                        "XYX",
                        "XCX",
                        "ZWZ",
                        'X',
                        FoundationItemVariants.stack(GSItems.HDP, 1, i),
                        'Y',
                        ItemStack(Items.FLINT_AND_STEEL, 1, 0),
                        'Z',
                        ItemStack(GCBlocks.oxygenPipe, 1, 0),
                        'C',
                        FoundationItemVariants.stack(GSItems.ROCKET_MODULES, 1, 3),
                        'W',
                        FoundationItemVariants.stack(GSItems.ROCKET_PARTS, 1, 8)
                    )
                )
                AssemblyRecipes.addRecipe(
                    FoundationItemVariants.stack(GSItems.ROCKET_PARTS, 1, 5 * i + 8),
                    *arrayOf<Any>(
                        "XYX",
                        "XCX",
                        " W ",
                        'X',
                        FoundationItemVariants.stack(GSItems.HDP, 1, i),
                        'Y',
                        ItemStack(GCBlocks.oxygenPipe, 1, 0),
                        'C',
                        ItemStack((GCItems.fuelCanister)!!.setContainerItem(null), 1, 1),
                        'W',
                        FoundationItemVariants.stack(requireNotNull(AsteroidsItems.basicItem), 1, 1)
                    )
                )
                AssemblyRecipes.addRecipe(
                    FoundationItemVariants.stack(GSItems.ROCKET_PARTS, 1, 5 * i + 9),
                    *arrayOf<Any>(
                        " Y ",
                        "XYX",
                        "X X",
                        'X',
                        FoundationItemVariants.stack(GSItems.HDP, 1, i),
                        'Y',
                        FoundationItemVariants.stack(requireNotNull(AsteroidsItems.basicItem), 1, 5)
                    )
                )
            }
        }

        AssemblyRecipes.addRecipe(
            BasicItems.PLASTIC_FAN.getItemStack(),
            *arrayOf<Any?>(
                "XYX",
                "ZWZ",
                "XCX",
                'X',
                BasicItems.PLASTIC.getItemStack(),
                'Y',
                ItemStack(GCBlocks.aluminumWire, 1, 1),
                'Z',
                FoundationItemVariants.stack(GSItems.HDP, 1, 0),
                'W',
                ItemStack(GSItems.BASIC, 1, 5),
                'C',
                Items.REDSTONE
            )
        )
        AssemblyRecipes.addRecipe(
            BasicItems.CARBON_FAN.getItemStack(),
            *arrayOf<Any>(
                "X X",
                " Y ",
                "X X",
                'X',
                FoundationItemVariants.stack(GSItems.HDP, 1, 2),
                'Y',
                BasicItems.PLASTIC_FAN.getItemStack()
            )
        )
        AssemblyRecipes.addRecipe(
            ItemStack(GSItems.GEOLOGICAL_SCANNER, 1, GSItems.GEOLOGICAL_SCANNER.getMaxDamage()),
            *arrayOf<Any?>(
                "XYX",
                "ZCV",
                "XWX",
                'X',
                FoundationItemVariants.stack(requireNotNull(AsteroidsItems.basicItem), 1, 6),
                'Y',
                FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 19),
                'Z',
                BasicItems.WAFER_MODERN.getItemStack(),
                'C',
                ItemStack(GCBlocks.spaceGlassClear, 1, 0),
                'V',
                GCItems.sensorLens,
                'W',
                ItemStack(GSItems.ADVANCED_BATTERY, 1, GSItems.ADVANCED_BATTERY.getMaxDamage())
            )
        )
    }

    @JvmStatic
    private fun addRocketAssembly() {
        if (GSConfigCore.enableAdvancedRocketCraft) addRocketRecipe(
            (AsteroidsItems.tier3Rocket)!!,
            GSItems.ROCKET_PARTS,
            0,
            FoundationItemVariants.stack(GSItems.ROCKET_MODULES, 1, 2)
        )
        addRocketRecipe(GSItems.ROCKET_TIER_4, GSItems.ROCKET_PARTS, 5, FoundationItemVariants.stack(GSItems.ROCKET_MODULES, 1, 8))
        addRocketRecipe(GSItems.ROCKET_TIER_5, GSItems.ROCKET_PARTS, 10, FoundationItemVariants.stack(GSItems.ROCKET_MODULES, 1, 9))
        addRocketRecipe(GSItems.ROCKET_TIER_6, GSItems.ROCKET_PARTS, 15, FoundationItemVariants.stack(GSItems.ROCKET_MODULES, 1, 10))
    }

    @JvmStatic
    private fun addRecycler() {
        RecyclerRecipes.recycling()
            .addNewRecipe(BasicItems.DOLOMITE_CRYSTAL.getItemStack(), BasicItems.DOLOMITE_MEAL.getItemStack(6), null)
        RecyclerRecipes.recycling()
            .addNewRecipe(ItemStack(Blocks.PACKED_ICE, 1), ItemStack.EMPTY, FluidStack(FluidRegistry.WATER, 500))
        RecyclerRecipes.recycling()
            .addNewRecipe(ItemStack(GCBlocks.blockMoon, 1, 4), FoundationItemVariants.stack(requireNotNull(GCItems.itemBasicMoon), 1, 2), 5, null)
        RecyclerRecipes.recycling().addNewRecipe(
            ItemStack(GCBlocks.blockMoon, 1, 5),
            ItemStack(GCBlocks.blockMoon, 1, 3),
            FluidStack(GSFluids.Helium3, 10)
        )
        RecyclerRecipes.recycling()
            .addNewRecipe(ItemStack(MarsBlocks.marsBlock, 20, 5), BasicItems.HEMATITE.getItemStack(), null)
        RecyclerRecipes.recycling().addNewRecipe(
            BasicItems.SULFUR.getItemStack(),
            ItemStack(Items.GUNPOWDER, 1, 0),
            FluidStack(FluidRegistry.getFluid("sulphuricacid"), 20)
        )
        RecyclerRecipes.recycling()
            .addNewRecipe(ItemStack(GSBlocks.SURFACE_ICE, 1), ItemStack.EMPTY, FluidStack(FluidRegistry.WATER, 100))
        RecyclerRecipes.recycling().addNewRecipe(
            ItemStack(GSBlocks.EUROPA_BLOCKS, 1, 2),
            BasicItems.HEMATITE.getItemStack(),
            20,
            FluidStack(FluidRegistry.WATER, 100)
        )
        RecyclerRecipes.recycling()
            .addNewRecipe(ItemStack(GSBlocks.MERCURY_BLOCKS, 1, 0), ItemStack.EMPTY, FluidStack(GSFluids.Helium3, 100))
        RecyclerRecipes.recycling()
            .addNewRecipe(ItemStack(GSBlocks.MERCURY_BLOCKS, 1, 2), BasicItems.SULFUR.getItemStack(), 35, null)
        RecyclerRecipes.recycling()
            .addNewRecipe(ItemStack(VenusBlocks.venusBlock, 20, 0), ItemStack(VenusBlocks.venusBlock, 1, 8), 50, null)
        RecyclerRecipes.recycling().addNewRecipe(
            BasicItems.OXYGEN_ICE_CRYSTAL.getItemStack(),
            ItemStack.EMPTY,
            FluidRegistry.getFluidStack("oxygen", 25)
        )
        RecyclerRecipes.recycling().addNewRecipe(
            BasicItems.NITROGEN_ICE_CRYSTAL.getItemStack(),
            ItemStack.EMPTY,
            FluidRegistry.getFluidStack("nitrogen", 20)
        )
        RecyclerRecipes.recycling().addNewRecipe(
            BasicItems.METHANE_ICE_CRYSTAL.getItemStack(),
            ItemStack.EMPTY,
            FluidRegistry.getFluidStack("methane", 20)
        )
        RecyclerRecipes.recycling().addNewRecipe(
            BasicItems.HYDROGEN_ICE_CRYSTAL.getItemStack(),
            ItemStack.EMPTY,
            FluidRegistry.getFluidStack("hydrogen", 20)
        )
        RecyclerRecipes.recycling().addNewRecipe(
            BasicItems.DRY_ICE_CRYSTAL.getItemStack(),
            ItemStack.EMPTY,
            FluidStack(FluidRegistry.WATER, 50)
        )
        RecyclerRecipes.recycling().addNewRecipe(
            ItemStack(GSBlocks.ENCELADUS_BLOCKS, 1, 0),
            ItemStack(Items.SNOWBALL, 4, 0),
            FluidStack(FluidRegistry.WATER, 200)
        )
        RecyclerRecipes.recycling()
            .addNewRecipe(ItemStack(GSBlocks.ENCELADUS_CRYSTAL, 1, 0), BasicItems.UNKNOW_CRYSTAL.getItemStack(4), null)

        RecyclerRecipes.recycling().addNewRecipe(
            ItemStack(GCItems.oilCanister, 1, 1),
            BasicItems.RAW_PLASTIC.getItemStack(),
            BasicItems.RAW_PLASTIC.getItemStack(),
            100,
            10,
            FluidStack(GSFluids.Ethane, 100)
        )
        RecyclerRecipes.recycling()
            .addNewRecipe(ItemStack(GSItems.ETHANE_CANISTER, 1, 1), BasicItems.RAW_PLASTIC.getItemStack(3), null)

        RecyclerRecipes.recycling().addNewRecipe(
            ItemStack(GCItems.meteoricIronRaw, 2, 0),
            ItemStack(Items.IRON_INGOT, 1, 0),
            FoundationItemVariants.stack(GSItems.NUGGETS, 1, 2),
            100,
            30,
            null
        )
    }


    @JvmStatic
    private fun addOtherRecipes() {
        TileEntityLiquidExtractor.addBlockAndFluid(Blocks.ICE, FluidStack(FluidRegistry.WATER, 50))
        TileEntityLiquidExtractor.addBlockAndFluid(Blocks.PACKED_ICE, FluidStack(FluidRegistry.WATER, 30))
        TileEntityLiquidExtractor.addBlockAndFluid(AsteroidBlocks.blockDenseIce, FluidStack(FluidRegistry.WATER, 100))
        TileEntityLiquidExtractor.addBlockAndFluid(GSBlocks.SURFACE_ICE, FluidStack(FluidRegistry.WATER, 100))
        TileEntityLiquidExtractor.addBlockAndFluid(
            GSBlocks.MIRANDA_BLOCKS.defaultBlockState().setValue(
                MirandaBlocks.BASIC_TYPE,
                EnumMirandaBlocks.MIRANDA_ICE
            ).getBlock(), FluidStack(FluidRegistry.WATER, 100)
        )

        TileEntityHydroponicBase.addPlant(
            ItemStack(Items.WHEAT_SEEDS),
            ItemStack(Items.WHEAT),
            ItemStack(Items.WHEAT_SEEDS),
            100,
            Blocks.WHEAT,
            7,
            booleanArrayOf(false, true)
        )
        TileEntityHydroponicBase.addPlant(
            ItemStack(Items.CARROT),
            ItemStack(Items.CARROT),
            ItemStack.EMPTY,
            100,
            Blocks.CARROTS,
            3,
            booleanArrayOf(true, false)
        )
        TileEntityHydroponicBase.addPlant(
            ItemStack(Items.POTATO),
            ItemStack(Items.POTATO),
            ItemStack(Items.POISONOUS_POTATO),
            2,
            Blocks.POTATOES,
            3,
            booleanArrayOf(true, false)
        )
        TileEntityHydroponicBase.addPlant(
            ItemStack(Items.MELON_SEEDS),
            ItemStack(Blocks.MELON_BLOCK),
            ItemStack.EMPTY,
            100,
            Blocks.MELON_STEM,
            1,
            booleanArrayOf(false, false)
        )
        TileEntityHydroponicBase.addPlant(
            ItemStack(Items.PUMPKIN_SEEDS),
            ItemStack(Blocks.PUMPKIN),
            ItemStack.EMPTY,
            100,
            Blocks.PUMPKIN_STEM,
            3,
            booleanArrayOf(false, false)
        )
        TileEntityHydroponicBase.addPlant(
            ItemStack(Items.BEETROOT_SEEDS),
            ItemStack(Items.BEETROOT),
            ItemStack(Items.BEETROOT_SEEDS),
            100,
            Blocks.BEETROOTS,
            3,
            booleanArrayOf(false, true)
        )
        TileEntityHydroponicBase.addPlant(
            ItemStack(Blocks.CACTUS),
            ItemStack(Blocks.CACTUS, 2, 0),
            null,
            100,
            Blocks.CACTUS,
            0,
            booleanArrayOf(false, false)
        )
        TileEntityHydroponicBase.addPlant(
            ItemStack(Blocks.BROWN_MUSHROOM),
            ItemStack(Blocks.BROWN_MUSHROOM, 2, 0),
            null,
            100,
            Blocks.BROWN_MUSHROOM,
            0,
            booleanArrayOf(false, false)
        )


        TileEntityFuelGenerator.registerNewFuel(FluidRegistry.LAVA, 5, 0.6f)

        FluidRegistry.getRegisteredFluids().forEach { (name: String?, fluid: Fluid?) ->
            if (name!!.contains("fuel")) TileEntityFuelGenerator.registerNewFuel(fluid, 20, 1.2f)
            if (name.contains("oil")) TileEntityFuelGenerator.registerNewFuel(fluid, 10, 0.8f)
        }

        TileEntityFuelGenerator.registerNewFuel((GSFluids.LiquidEthaneMethane)!!, 20, 1.3f)
        TileEntityFuelGenerator.registerNewFuel((AsteroidsModule.fluidLiquidMethane)!!, 50, 1.4f)
        TileEntityFuelGenerator.registerNewFuel((GSFluids.HeliumHydrogen)!!, 50, 2.0f)

        TileEntityGasGenerator.registerNewFuel((GSFluids.NatureGas)!!, 2, 0.8f, 1)
        TileEntityGasGenerator.registerNewFuel(FluidRegistry.getFluid("hydrogen"), 4, 1.1f, 3)
        TileEntityGasGenerator.registerNewFuel(FluidRegistry.getFluid("methane"), 4, 1.2f, 3)

        TileEntityDeconstructor.addSalvage(FoundationItemVariants.stack(GSItems.HDP, 1, 0))
        TileEntityDeconstructor.addSalvage(FoundationItemVariants.stack(GSItems.HDP, 1, 1))
        TileEntityDeconstructor.addSalvage(FoundationItemVariants.stack(GSItems.HDP, 1, 2))

        TileEntityDeconstructor.knownRecipes.addAll((GSRecipeUtil.getBodyRecipes() as Collection<INasaWorkbenchRecipe>))
        TileEntityDeconstructor.knownRecipes.addAll((GSRecipeUtil.getBoosterRecipes() as Collection<INasaWorkbenchRecipe>))
        TileEntityDeconstructor.knownRecipes.addAll((GSRecipeUtil.getConeRecipes() as Collection<INasaWorkbenchRecipe>))
        TileEntityDeconstructor.knownRecipes.addAll((GSRecipeUtil.getEngineRecipes() as Collection<INasaWorkbenchRecipe>))
        TileEntityDeconstructor.knownRecipes.addAll((GSRecipeUtil.getFinsRecipes() as Collection<INasaWorkbenchRecipe>))

        for (i: Int in 0..1) {
            val knownRecipes: MutableList<INasaWorkbenchRecipe?> = LinkedList<INasaWorkbenchRecipe?>()
            val input: HashMap<Int, ItemStack?> = HashMap<Int, ItemStack?>()

            input.put(0, FoundationItemVariants.stack(GSItems.ROCKET_PARTS, 1, 5 * i + 5))
            input.put(1, FoundationItemVariants.stack(GSItems.ROCKET_PARTS, 1, 5 * i + 6))
            input.put(2, FoundationItemVariants.stack(GSItems.ROCKET_PARTS, 1, 5 * i + 7))
            input.put(3, FoundationItemVariants.stack(GSItems.ROCKET_PARTS, 1, 5 * i + 8))
            input.put(4, FoundationItemVariants.stack(GSItems.ROCKET_PARTS, 1, 5 * i + 9))

            var rocket: Item = GSItems.ROCKET_TIER_4
            if (i == 1) rocket = GSItems.ROCKET_TIER_5
            if (i == 2) rocket = GSItems.ROCKET_TIER_6
            for (k: Int in 0..2) knownRecipes.add(NasaWorkbenchRecipe(ItemStack(rocket, 1, k), input))

            TileEntityDeconstructor.addSalvage(FoundationItemVariants.stack(GSItems.ROCKET_PARTS, 1, 5 * i + 5))
            TileEntityDeconstructor.addSalvage(FoundationItemVariants.stack(GSItems.ROCKET_PARTS, 1, 5 * i + 6))
            TileEntityDeconstructor.addSalvage(FoundationItemVariants.stack(GSItems.ROCKET_PARTS, 1, 5 * i + 7))
            TileEntityDeconstructor.addSalvage(FoundationItemVariants.stack(GSItems.ROCKET_PARTS, 1, 5 * i + 8))
            TileEntityDeconstructor.addSalvage(FoundationItemVariants.stack(GSItems.ROCKET_PARTS, 1, 5 * i + 9))
            TileEntityDeconstructor.knownRecipes.addAll((knownRecipes as Collection<INasaWorkbenchRecipe>))
        }
    }

    @JvmStatic
    private fun addRocketRecipe(rocket: Item, parts: Item, metafirstparts: Int, key: ItemStack) {
        RocketAssemblyRecipes.addShapelessRecipe(
            ItemStack(rocket, 1, 0),
            FoundationItemVariants.stack(parts, 1, metafirstparts),
            FoundationItemVariants.stack(parts, 1, metafirstparts + 1),
            FoundationItemVariants.stack(parts, 1, metafirstparts + 1),
            FoundationItemVariants.stack(parts, 1, metafirstparts + 2),
            FoundationItemVariants.stack(parts, 1, metafirstparts + 3),
            FoundationItemVariants.stack(parts, 1, metafirstparts + 3),
            FoundationItemVariants.stack(parts, 1, metafirstparts + 4),
            FoundationItemVariants.stack(parts, 1, metafirstparts + 4),
            key
        )
        RocketAssemblyRecipes.addShapelessRecipe(
            ItemStack(rocket, 1, 1),
            FoundationItemVariants.stack(parts, 1, metafirstparts),
            FoundationItemVariants.stack(parts, 1, metafirstparts + 1),
            FoundationItemVariants.stack(parts, 1, metafirstparts + 1),
            FoundationItemVariants.stack(parts, 1, metafirstparts + 2),
            FoundationItemVariants.stack(parts, 1, metafirstparts + 3),
            FoundationItemVariants.stack(parts, 1, metafirstparts + 3),
            FoundationItemVariants.stack(parts, 1, metafirstparts + 4),
            FoundationItemVariants.stack(parts, 1, metafirstparts + 4),
            key,
            ItemStack(Blocks.CHEST, 1, 0)
        )
        RocketAssemblyRecipes.addShapelessRecipe(
            ItemStack(rocket, 1, 2),
            FoundationItemVariants.stack(parts, 1, metafirstparts),
            FoundationItemVariants.stack(parts, 1, metafirstparts + 1),
            FoundationItemVariants.stack(parts, 1, metafirstparts + 1),
            FoundationItemVariants.stack(parts, 1, metafirstparts + 2),
            FoundationItemVariants.stack(parts, 1, metafirstparts + 3),
            FoundationItemVariants.stack(parts, 1, metafirstparts + 3),
            FoundationItemVariants.stack(parts, 1, metafirstparts + 4),
            FoundationItemVariants.stack(parts, 1, metafirstparts + 4),
            key,
            ItemStack(Blocks.CHEST, 1, 0),
            ItemStack(Blocks.CHEST, 1, 0)
        )
        RocketAssemblyRecipes.addShapelessRecipe(
            ItemStack(rocket, 1, 3),
            FoundationItemVariants.stack(parts, 1, metafirstparts),
            FoundationItemVariants.stack(parts, 1, metafirstparts + 1),
            FoundationItemVariants.stack(parts, 1, metafirstparts + 1),
            FoundationItemVariants.stack(parts, 1, metafirstparts + 2),
            FoundationItemVariants.stack(parts, 1, metafirstparts + 3),
            FoundationItemVariants.stack(parts, 1, metafirstparts + 3),
            FoundationItemVariants.stack(parts, 1, metafirstparts + 4),
            FoundationItemVariants.stack(parts, 1, metafirstparts + 4),
            key,
            ItemStack(Blocks.CHEST, 1, 0),
            ItemStack(Blocks.CHEST, 1, 0),
            ItemStack(Blocks.CHEST, 1, 0)
        )
    }
}
