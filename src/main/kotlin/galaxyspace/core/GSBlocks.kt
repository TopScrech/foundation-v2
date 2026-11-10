package galaxyspace.core

import galaxyspace.systems.SolarSystem.planets.overworld.blocks.machines.BlockAdvCircuitFabricator
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.machines.BlockAdvWindGenerator
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.machines.BlockAssembler
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.machines.BlockEnergyPad
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.machines.BlockFuelGenerator
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.machines.BlockGasBurner
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.machines.BlockGasCollector
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.machines.BlockGasGenerator
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.machines.BlockGravitationModule
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.machines.BlockHydroponicBase
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.machines.BlockHydroponicFarm
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.machines.BlockLiquidExtractor
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.machines.BlockLiquidSeparator
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.machines.BlockModernSolarPanel
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.machines.BlockModernStorageModule
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.machines.BlockModificationTable
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.machines.BlockOxygenStorageModule
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.machines.BlockPanelController
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.machines.BlockPlanetShield
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.machines.BlockRadiationStabiliser
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.machines.BlockResearchTable
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.machines.BlockRocketAssembler
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.machines.BlockSingleSolarPanel
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.machines.BlockSolarWindPanel
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.machines.BlockUniversalRecycler
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.machines.BlockWindGenerator

import galaxyspace.core.prefab.blocks.DungeonBlocks
import galaxyspace.core.prefab.blocks.GSBlockMulti
import galaxyspace.core.prefab.items.GSItemBlockDesc
import galaxyspace.core.prefab.items.ItemBlockDungeonBlocks
import galaxyspace.systems.SolarSystem.moons.callisto.blocks.CallistoBlocks
import galaxyspace.systems.SolarSystem.moons.callisto.items.ItemBlocksCallisto
import galaxyspace.systems.SolarSystem.moons.enceladus.blocks.EnceladusBlocks
import galaxyspace.systems.SolarSystem.moons.enceladus.blocks.EnceladusCrystal
import galaxyspace.systems.SolarSystem.moons.enceladus.items.ItemBlocksEnceladus
import galaxyspace.systems.SolarSystem.moons.europa.blocks.EuropaBlockGeyser
import galaxyspace.systems.SolarSystem.moons.europa.blocks.EuropaBlockUWGeyser
import galaxyspace.systems.SolarSystem.moons.europa.blocks.EuropaBlocks
import galaxyspace.systems.SolarSystem.moons.europa.items.ItemBlocksEuropa
import galaxyspace.systems.SolarSystem.moons.ganymede.blocks.GanymedeBlocks
import galaxyspace.systems.SolarSystem.moons.ganymede.items.ItemBlocksGanymede
import galaxyspace.systems.SolarSystem.moons.io.blocks.BlockBossSpawnerIo
import galaxyspace.systems.SolarSystem.moons.io.blocks.BlockTier5TreasureChest
import galaxyspace.systems.SolarSystem.moons.io.blocks.IoBlocks
import galaxyspace.systems.SolarSystem.moons.io.items.ItemBlocksIo
import galaxyspace.systems.SolarSystem.moons.miranda.blocks.MirandaBlocks
import galaxyspace.systems.SolarSystem.moons.miranda.items.ItemBlocksMiranda
import galaxyspace.systems.SolarSystem.moons.phobos.blocks.PhobosBlocks
import galaxyspace.systems.SolarSystem.moons.phobos.items.ItemBlocksPhobos
import galaxyspace.systems.SolarSystem.moons.titan.blocks.TitanBlocks
import galaxyspace.systems.SolarSystem.moons.titan.items.ItemBlocksTitan
import galaxyspace.systems.SolarSystem.moons.triton.blocks.TritonBlocks
import galaxyspace.systems.SolarSystem.moons.triton.items.ItemBlocksTriton
import galaxyspace.systems.SolarSystem.planets.ceres.blocks.BlockBossSpawnerCeres
import galaxyspace.systems.SolarSystem.planets.ceres.blocks.BlockTier4TreasureChest
import galaxyspace.systems.SolarSystem.planets.ceres.blocks.CeresBlocks
import galaxyspace.systems.SolarSystem.planets.ceres.items.ItemBlocksCeres
import galaxyspace.systems.SolarSystem.planets.haumea.blocks.HaumeaBlocks
import galaxyspace.systems.SolarSystem.planets.haumea.items.ItemBlocksHaumea
import galaxyspace.systems.SolarSystem.planets.mars.blocks.MarsOresBlocks
import galaxyspace.systems.SolarSystem.planets.mars.items.ItemBlocksMarsOres
import galaxyspace.systems.SolarSystem.planets.mercury.blocks.MercuryBlocks
import galaxyspace.systems.SolarSystem.planets.mercury.items.ItemBlocksMercury
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.*
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.machines.*
import galaxyspace.systems.SolarSystem.planets.overworld.items.*
import galaxyspace.systems.SolarSystem.planets.pluto.blocks.PlutoBlocks
import galaxyspace.systems.SolarSystem.planets.pluto.items.ItemBlocksPluto
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.planets.mars.blocks.MarsBlocks
import net.minecraft.block.Block
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary

object GSBlocks {
    //public static final List<Block> GS_BLOCKS = new ArrayList<Block>();
    @JvmField
    val MERCURY_BLOCKS: Block = MercuryBlocks().setHardness(3.0f)

    @JvmField
    val OVERWORLD_ORES: Block = BlockOres().setHardness(2.0f)

    @JvmField
    val DECO_METALS: Block = BlockDecoMetals().setHardness(1.0f)

    @JvmField
    val METALS: Block = BlockMetals().setHardness(1.0f)

    @JvmField
    val SURFACE_ICE: Block = BlockSurfaceIce()

    @JvmField
    val FUTURE_GLASS_COLORED: Block = BlockFutureGlasses().setHardness(0.2f)

    @JvmField
    val FUTURE_GLASS_BASIC: Block = BlockFutureGlass().setHardness(0.2f)

    @JvmField
    val MACHINE_FRAMES: Block = BlockMachineFrames().setHardness(1.0f)

    @JvmField
    val FUEL_GENERATOR: Block = BlockFuelGenerator().setHardness(1.0f)

    @JvmField
    val ASSEMBLER: Block = BlockAssembler()

    @JvmField
    val MODERN_SOLAR_PANEL: Block = BlockModernSolarPanel()

    @JvmField
    val WIND_GENERATOR: Block = BlockWindGenerator()

    @JvmField
    val ROCKET_ASSEMBLER: Block = BlockRocketAssembler()

    @JvmField
    val UNIVERSAL_RECYCLER: Block = BlockUniversalRecycler()

    @JvmField
    val LIQUID_EXTRACTOR: Block = BlockLiquidExtractor()

    @JvmField
    val LIQUID_SEPARATOR: Block = BlockLiquidSeparator()

    @JvmField
    val HYDROPONIC_BASE: Block = BlockHydroponicBase()

    @JvmField
    val HYDROPONIC_FARM: Block = BlockHydroponicFarm()

    @JvmField
    val GRAVITATION_MODULE: Block = BlockGravitationModule()

    @JvmField
    val RADIATION_STABILISER: Block = BlockRadiationStabiliser()

    @JvmField
    val PANEL_CONTROLLER: Block = BlockPanelController()

    @JvmField
    val SINGLE_SOLARPANEL: Block = BlockSingleSolarPanel("single_solarpanel", 1)

    @JvmField
    val MODERN_SINGLE_SOLARPANEL: Block = BlockSingleSolarPanel("modern_single_solarpanel", 2)

    @JvmField
    val PLANET_SHIELD: Block = BlockPlanetShield()

    @JvmField
    val MODERN_STORAGE_MODULE: Block = BlockModernStorageModule()

    @JvmField
    val ADVANCED_LANDING_PAD_SINGLE: Block = BlockAdvancedLandingPad()

    @JvmField
    val ADVANCED_LANDING_PAD: Block = BlockAdvancedLandingPadFull()

    @JvmField
    val MODIFICATION_TABLE: Block = BlockModificationTable()

    @JvmField
    val GAS_BURNER: Block = BlockGasBurner()

    @JvmField
    val OXYGEN_STORAGE_MODULE: Block = BlockOxygenStorageModule(1)

    @JvmField
    val SOLARWIND_PANEL: Block = BlockSolarWindPanel()

    @JvmField
    val ADVANCED_CIRCUIT_FABRICATOR: Block = BlockAdvCircuitFabricator()

    @JvmField
    val RESEARCH_TABLE: Block = BlockResearchTable()

    @JvmField
    val ENERGY_PAD: Block = BlockEnergyPad()

    @JvmField
    val GAS_COLLECTOR: Block = BlockGasCollector()

    @JvmField
    val GAS_GENERATOR: Block = BlockGasGenerator()

    @JvmField
    val ADVANCED_WIND_GENERATOR: Block = BlockAdvWindGenerator()

    @JvmField
    val FAKE_BLOCK: Block = GSBlockMulti()

    @JvmField
    val DUNGEON_BLOCKS: Block = DungeonBlocks().setHardness(1.0f)

    @JvmField
    val TREASURE_CHEST_TIER_4: Block = BlockTier4TreasureChest("treasure_t4")

    @JvmField
    val TREASURE_CHEST_TIER_5: Block = BlockTier5TreasureChest("treasure_t5")

    @JvmField
    val BOSS_SPAWNER_CERES: Block = BlockBossSpawnerCeres("boss_spawner_ceres")

    @JvmField
    val BOSS_SPAWNER_IO: Block = BlockBossSpawnerIo("boss_spawner_io")

    @JvmField
    val DRY_LEAVES: Block = BlockDryLeaves()

    @JvmField
    val MARS_ORES: Block = MarsOresBlocks().setHardness(2.0f)

    @JvmField
    val CERES_BLOCKS: Block = CeresBlocks().setHardness(3.0f)

    @JvmField
    val PLUTO_BLOCKS: Block = PlutoBlocks().setHardness(3.0f)

    @JvmField
    val HAUMEA_BLOCKS: Block = HaumeaBlocks().setHardness(3.0f)

    @JvmField
    val PHOBOS_BLOCKS: Block = PhobosBlocks().setHardness(2.0f)

    @JvmField
    val IO_BLOCKS: Block = IoBlocks().setHardness(3.0f)

    @JvmField
    val EUROPA_BLOCKS: Block = EuropaBlocks().setHardness(3.0f)

    @JvmField
    val EUROPA_GEYSER: Block = EuropaBlockGeyser()

    @JvmField
    val EUROPA_UWGEYSER: Block = EuropaBlockUWGeyser()

    @JvmField
    val GANYMEDE_BLOCKS: Block = GanymedeBlocks().setHardness(3.0f)

    @JvmField
    val CALLISTO_BLOCKS: Block = CallistoBlocks().setHardness(3.0f)

    @JvmField
    val ENCELADUS_BLOCKS: Block = EnceladusBlocks().setHardness(3.0f)

    @JvmField
    val ENCELADUS_CRYSTAL: Block = EnceladusCrystal().setHardness(1.0f)

    @JvmField
    val TITAN_BLOCKS: Block = TitanBlocks().setHardness(3.0f)

    @JvmField
    val MIRANDA_BLOCKS: Block = MirandaBlocks().setHardness(3.0f)

    @JvmField
    val TRITON_BLOCKS: Block = TritonBlocks().setHardness(3.0f)

    @JvmStatic
    fun initialize() {
        registerBlock(MACHINE_FRAMES, ItemBlocksMachineFrames::class.java)
        registerBlock(FUEL_GENERATOR, GSItemBlockDesc::class.java)
        registerBlock(GAS_GENERATOR, GSItemBlockDesc::class.java)
        registerBlock(WIND_GENERATOR, GSItemBlockDesc::class.java)
        registerBlock(SOLARWIND_PANEL, GSItemBlockDesc::class.java)
        registerBlock(ADVANCED_WIND_GENERATOR, GSItemBlockDesc::class.java)
        registerBlock(MODERN_SOLAR_PANEL, GSItemBlockDesc::class.java)

        registerBlock(ASSEMBLER, GSItemBlockDesc::class.java)
        registerBlock(UNIVERSAL_RECYCLER, GSItemBlockDesc::class.java)
        registerBlock(ROCKET_ASSEMBLER, GSItemBlockDesc::class.java)
        registerBlock(LIQUID_EXTRACTOR, GSItemBlockDesc::class.java)
        registerBlock(LIQUID_SEPARATOR, GSItemBlockDesc::class.java)
        registerBlock(GAS_COLLECTOR, GSItemBlockDesc::class.java)
        registerBlock(GAS_BURNER, GSItemBlockDesc::class.java)
        registerBlock(HYDROPONIC_BASE, GSItemBlockDesc::class.java)
        registerBlock(HYDROPONIC_FARM, GSItemBlockDesc::class.java)
        registerBlock(RADIATION_STABILISER, GSItemBlockDesc::class.java)
        registerBlock(MODIFICATION_TABLE, GSItemBlockDesc::class.java)

        registerBlock(OXYGEN_STORAGE_MODULE, GSItemBlockDesc::class.java)
        registerBlock(ADVANCED_CIRCUIT_FABRICATOR, GSItemBlockDesc::class.java)

        registerBlock(PANEL_CONTROLLER, GSItemBlockDesc::class.java)
        registerBlock(MODERN_STORAGE_MODULE, GSItemBlockDesc::class.java)
        registerBlock(PLANET_SHIELD, GSItemBlockDesc::class.java)

        registerBlock(GRAVITATION_MODULE, GSItemBlockDesc::class.java)
        registerBlock(ENERGY_PAD, GSItemBlockDesc::class.java)
        registerBlock(SINGLE_SOLARPANEL, GSItemBlockDesc::class.java)
        registerBlock(MODERN_SINGLE_SOLARPANEL, GSItemBlockDesc::class.java)
        registerBlock(ADVANCED_LANDING_PAD_SINGLE, GSItemBlockDesc::class.java)
        registerBlock(ADVANCED_LANDING_PAD, null)
        registerBlock(FAKE_BLOCK, null)

        //registerBlock(RESEARCH_TABLE, GSItemBlockDesc.class);
        registerBlock(DUNGEON_BLOCKS, ItemBlockDungeonBlocks::class.java)
        registerBlock(TREASURE_CHEST_TIER_4, GSItemBlockDesc::class.java)
        registerBlock(TREASURE_CHEST_TIER_5, GSItemBlockDesc::class.java)
        registerBlock(BOSS_SPAWNER_CERES, null)
        registerBlock(BOSS_SPAWNER_IO, null)

        registerBlock(DRY_LEAVES, GSItemBlockDesc::class.java)
        registerBlock(FUTURE_GLASS_BASIC, GSItemBlockDesc::class.java)
        registerBlock(FUTURE_GLASS_COLORED, ItemBlocksFutureGlasses::class.java)

        registerBlock(OVERWORLD_ORES, ItemBlocksOres::class.java)
        registerBlock(DECO_METALS, ItemBlocksDecoMetals::class.java)
        registerBlock(METALS, ItemBlocksMetals::class.java)
        registerBlock(SURFACE_ICE, ItemBlocksIce::class.java)

        registerBlock(MERCURY_BLOCKS, ItemBlocksMercury::class.java)
        registerBlock(MARS_ORES, ItemBlocksMarsOres::class.java)

        registerBlock(CERES_BLOCKS, ItemBlocksCeres::class.java)

        registerBlock(PLUTO_BLOCKS, ItemBlocksPluto::class.java)
        registerBlock(HAUMEA_BLOCKS, ItemBlocksHaumea::class.java)

        registerBlock(PHOBOS_BLOCKS, ItemBlocksPhobos::class.java)
        registerBlock(IO_BLOCKS, ItemBlocksIo::class.java)

        registerBlock(EUROPA_BLOCKS, ItemBlocksEuropa::class.java)
        registerBlock(EUROPA_GEYSER, GSItemBlockDesc::class.java)
        registerBlock(EUROPA_UWGEYSER, GSItemBlockDesc::class.java)

        registerBlock(GANYMEDE_BLOCKS, ItemBlocksGanymede::class.java)

        registerBlock(CALLISTO_BLOCKS, ItemBlocksCallisto::class.java)

        registerBlock(ENCELADUS_BLOCKS, ItemBlocksEnceladus::class.java)
        registerBlock(ENCELADUS_CRYSTAL, GSItemBlockDesc::class.java)

        registerBlock(TITAN_BLOCKS, ItemBlocksTitan::class.java)

        registerBlock(MIRANDA_BLOCKS, ItemBlocksMiranda::class.java)
        registerBlock(TRITON_BLOCKS, ItemBlocksTriton::class.java)
    }

    @JvmStatic
    fun oreDictRegistration() {
        OreDictionary.registerOre("oreNickel", ItemStack(MERCURY_BLOCKS, 1, 3))
        OreDictionary.registerOre("oreIron", ItemStack(MERCURY_BLOCKS, 1, 4))
        OreDictionary.registerOre("oreMagnesium", ItemStack(MERCURY_BLOCKS, 1, 5))

        OreDictionary.registerOre("oreCobalt", ItemStack(OVERWORLD_ORES, 1, 0))
        OreDictionary.registerOre("oreNickel", ItemStack(OVERWORLD_ORES, 1, 1))
        OreDictionary.registerOre("oreUranium", ItemStack(OVERWORLD_ORES, 1, 2))

        OreDictionary.registerOre("oreDiamond", ItemStack(MARS_ORES, 1, 0))
        OreDictionary.registerOre("oreGold", ItemStack(MARS_ORES, 1, 1))
        OreDictionary.registerOre("oreCoal", ItemStack(MARS_ORES, 1, 2))
        OreDictionary.registerOre("oreRedstone", ItemStack(MARS_ORES, 1, 3))
        OreDictionary.registerOre("oreSilicon", ItemStack(MARS_ORES, 1, 4))
        OreDictionary.registerOre("oreAluminum", ItemStack(MARS_ORES, 1, 5))

        OreDictionary.registerOre("oreDolomite", ItemStack(CERES_BLOCKS, 1, 2))
        OreDictionary.registerOre("oreMeteroricIron", ItemStack(CERES_BLOCKS, 1, 3))

        OreDictionary.registerOre("oreAluminum", ItemStack(HAUMEA_BLOCKS, 1, 3))

        OreDictionary.registerOre("oreIron", ItemStack(PHOBOS_BLOCKS, 1, 2))
        OreDictionary.registerOre("oreMeteroricIron", ItemStack(PHOBOS_BLOCKS, 1, 3))
        OreDictionary.registerOre("oreNickel", ItemStack(PHOBOS_BLOCKS, 1, 4))
        OreDictionary.registerOre("oreDesh", ItemStack(PHOBOS_BLOCKS, 1, 5))

        OreDictionary.registerOre("oreCopper", ItemStack(IO_BLOCKS, 1, 3))
        OreDictionary.registerOre("oreSulfur", ItemStack(IO_BLOCKS, 1, 4))
        OreDictionary.registerOre("oreVolcanic", ItemStack(IO_BLOCKS, 1, 5))

        OreDictionary.registerOre("oreEmerald", ItemStack(EUROPA_BLOCKS, 1, 3))
        OreDictionary.registerOre("oreSilicon", ItemStack(EUROPA_BLOCKS, 1, 4))
        OreDictionary.registerOre("oreAluminum", ItemStack(EUROPA_BLOCKS, 1, 5))

        OreDictionary.registerOre("oreMagnesium", ItemStack(GANYMEDE_BLOCKS, 1, 2))
        OreDictionary.registerOre("oreIlmenite", ItemStack(GANYMEDE_BLOCKS, 1, 3))

        OreDictionary.registerOre("oreCoal", ItemStack(ENCELADUS_BLOCKS, 1, 2))

        OreDictionary.registerOre("oreSapphire", ItemStack(TITAN_BLOCKS, 1, 3))
        OreDictionary.registerOre("oreEmerald", ItemStack(TITAN_BLOCKS, 1, 4))
        OreDictionary.registerOre("oreDiamond", ItemStack(TITAN_BLOCKS, 1, 5))
        OreDictionary.registerOre("oreCoal", ItemStack(TITAN_BLOCKS, 1, 6))
        OreDictionary.registerOre("oreLapis", ItemStack(TITAN_BLOCKS, 1, 7))
        OreDictionary.registerOre("oreRedstone", ItemStack(TITAN_BLOCKS, 1, 8))

        OreDictionary.registerOre("oreIron", ItemStack(MIRANDA_BLOCKS, 1, 3))
        OreDictionary.registerOre("oreDolomite", ItemStack(MIRANDA_BLOCKS, 1, 4))
        OreDictionary.registerOre("oreDiamond", ItemStack(MIRANDA_BLOCKS, 1, 5))
        OreDictionary.registerOre("oreQuartz", ItemStack(MIRANDA_BLOCKS, 1, 6))
        OreDictionary.registerOre("oreCobalt", ItemStack(MIRANDA_BLOCKS, 1, 7))
        OreDictionary.registerOre("oreNickel", ItemStack(MIRANDA_BLOCKS, 1, 8))
        /*
	
		OreDictionary.registerOre("oreUranium", new ItemStack(TritonBlocks, 1, 5));
*/
        OreDictionary.registerOre("blockGlass", ItemStack(FUTURE_GLASS_BASIC, 1, 0))
        OreDictionary.registerOre("blockGlass", ItemStack(FUTURE_GLASS_COLORED, 1, OreDictionary.WILDCARD_VALUE))

        OreDictionary.registerOre("cobblestone", ItemStack(MarsBlocks.marsBlock, 1, 4))
        OreDictionary.registerOre("cobblestone", ItemStack(HAUMEA_BLOCKS, 1, 2))
    }

    @JvmStatic
    fun registerBlock(block: Block, itemClass: Class<out ItemBlock>?) {
        GCBlocks.registerBlock(block, itemClass)
    }
}
