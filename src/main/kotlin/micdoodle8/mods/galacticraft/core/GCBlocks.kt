package micdoodle8.mods.galacticraft.core

import micdoodle8.mods.galacticraft.core.blocks.BlockBasicMoon.EnumBlockBasicMoon
import micdoodle8.mods.galacticraft.core.blocks.BlockSpaceGlass.GlassFrame
import micdoodle8.mods.galacticraft.core.blocks.BlockSpaceGlass.GlassType

import com.google.common.collect.Lists
import com.google.common.base.Function
import com.google.common.collect.Maps
import com.google.common.collect.ObjectArrays
import com.google.common.collect.Ordering
import micdoodle8.mods.galacticraft.core.blocks.BlockAirLockFrame
import micdoodle8.mods.galacticraft.core.blocks.BlockAirLockWall
import micdoodle8.mods.galacticraft.core.blocks.BlockAluminumWire
import micdoodle8.mods.galacticraft.core.blocks.BlockBasic
import micdoodle8.mods.galacticraft.core.blocks.BlockBasicMoon
import micdoodle8.mods.galacticraft.core.blocks.BlockBossSpawner
import micdoodle8.mods.galacticraft.core.blocks.BlockBreathableAir
import micdoodle8.mods.galacticraft.core.blocks.BlockBrightAir
import micdoodle8.mods.galacticraft.core.blocks.BlockBrightBreathableAir
import micdoodle8.mods.galacticraft.core.blocks.BlockBrightLamp
import micdoodle8.mods.galacticraft.core.blocks.BlockCargoLoader
import micdoodle8.mods.galacticraft.core.blocks.BlockCheese
import micdoodle8.mods.galacticraft.core.blocks.BlockConcealedDetector
import micdoodle8.mods.galacticraft.core.blocks.BlockConcealedRedstone
import micdoodle8.mods.galacticraft.core.blocks.BlockConcealedRepeater
import micdoodle8.mods.galacticraft.core.blocks.BlockCrafting
import micdoodle8.mods.galacticraft.core.blocks.BlockDish
import micdoodle8.mods.galacticraft.core.blocks.BlockDoubleSlabGC
import micdoodle8.mods.galacticraft.core.blocks.BlockEmergencyBox
import micdoodle8.mods.galacticraft.core.blocks.BlockEnclosed
import micdoodle8.mods.galacticraft.core.blocks.BlockFallenMeteor
import micdoodle8.mods.galacticraft.core.blocks.BlockFluidGC
import micdoodle8.mods.galacticraft.core.blocks.BlockFluidPipe
import micdoodle8.mods.galacticraft.core.blocks.BlockFluidTank
import micdoodle8.mods.galacticraft.core.blocks.BlockFuelLoader
import micdoodle8.mods.galacticraft.core.blocks.BlockGlowstoneTorch
import micdoodle8.mods.galacticraft.core.blocks.BlockGrating
import micdoodle8.mods.galacticraft.core.blocks.BlockLandingPad
import micdoodle8.mods.galacticraft.core.blocks.BlockLandingPadFull
import micdoodle8.mods.galacticraft.core.blocks.BlockMachine
import micdoodle8.mods.galacticraft.core.blocks.BlockMachine2
import micdoodle8.mods.galacticraft.core.blocks.BlockMachine3
import micdoodle8.mods.galacticraft.core.blocks.BlockMachineTiered
import micdoodle8.mods.galacticraft.core.blocks.BlockMulti
import micdoodle8.mods.galacticraft.core.blocks.BlockNasaWorkbench
import micdoodle8.mods.galacticraft.core.blocks.BlockOxygenCollector
import micdoodle8.mods.galacticraft.core.blocks.BlockOxygenCompressor
import micdoodle8.mods.galacticraft.core.blocks.BlockOxygenDetector
import micdoodle8.mods.galacticraft.core.blocks.BlockOxygenDistributor
import micdoodle8.mods.galacticraft.core.blocks.BlockOxygenSealer
import micdoodle8.mods.galacticraft.core.blocks.BlockPanelLighting
import micdoodle8.mods.galacticraft.core.blocks.BlockParaChest
import micdoodle8.mods.galacticraft.core.blocks.BlockPlatform
import micdoodle8.mods.galacticraft.core.blocks.BlockRefinery
import micdoodle8.mods.galacticraft.core.blocks.BlockScreen
import micdoodle8.mods.galacticraft.core.blocks.BlockSlabGC
import micdoodle8.mods.galacticraft.core.blocks.BlockSolar
import micdoodle8.mods.galacticraft.core.blocks.BlockSpaceGlass
import micdoodle8.mods.galacticraft.core.blocks.BlockSpaceStationBase
import micdoodle8.mods.galacticraft.core.blocks.BlockSpinThruster
import micdoodle8.mods.galacticraft.core.blocks.BlockStairsGC
import micdoodle8.mods.galacticraft.core.blocks.BlockTelemetry
import micdoodle8.mods.galacticraft.core.blocks.BlockTier1TreasureChest
import micdoodle8.mods.galacticraft.core.blocks.BlockUnlitTorch
import micdoodle8.mods.galacticraft.core.blocks.BlockWallGC
import micdoodle8.mods.galacticraft.core.blocks.ISortableBlock
import micdoodle8.mods.galacticraft.core.items.ItemBlockAirLock
import micdoodle8.mods.galacticraft.core.items.ItemBlockAluminumWire
import micdoodle8.mods.galacticraft.core.items.ItemBlockArclamp
import micdoodle8.mods.galacticraft.core.items.ItemBlockBase
import micdoodle8.mods.galacticraft.core.items.ItemBlockCargoLoader
import micdoodle8.mods.galacticraft.core.items.ItemBlockCheese
import micdoodle8.mods.galacticraft.core.items.ItemBlockCreativeGC
import micdoodle8.mods.galacticraft.core.items.ItemBlockDesc
import micdoodle8.mods.galacticraft.core.items.ItemBlockEmergencyBox
import micdoodle8.mods.galacticraft.core.items.ItemBlockEnclosed
import micdoodle8.mods.galacticraft.core.items.ItemBlockGC
import micdoodle8.mods.galacticraft.core.items.ItemBlockGlassGC
import micdoodle8.mods.galacticraft.core.items.ItemBlockLandingPad
import micdoodle8.mods.galacticraft.core.items.ItemBlockMachine
import micdoodle8.mods.galacticraft.core.items.ItemBlockMoon
import micdoodle8.mods.galacticraft.core.items.ItemBlockNasaWorkbench
import micdoodle8.mods.galacticraft.core.items.ItemBlockOxygenCompressor
import micdoodle8.mods.galacticraft.core.items.ItemBlockPanel
import micdoodle8.mods.galacticraft.core.items.ItemBlockSlabGC
import micdoodle8.mods.galacticraft.core.items.ItemBlockSolar
import micdoodle8.mods.galacticraft.core.items.ItemBlockThruster
import micdoodle8.mods.galacticraft.core.items.ItemBlockWallGC
import micdoodle8.mods.galacticraft.core.util.CompatibilityManager
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.GCLog
import micdoodle8.mods.galacticraft.core.util.StackSorted
import net.minecraft.block.Block
import net.minecraft.block.material.MapColor
import net.minecraft.block.material.Material
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraftforge.oredict.OreDictionary
import net.minecraftforge.registries.IForgeRegistry
import java.lang.Exception
import java.lang.RuntimeException
import java.lang.reflect.Constructor
import java.util.ArrayList
import java.util.Comparator
import java.util.HashMap

object GCBlocks {
    @JvmField
    var breatheableAir: Block? = null

    @JvmField
    var brightAir: Block? = null

    @JvmField
    var brightBreatheableAir: Block? = null

    @JvmField
    var brightLamp: Block? = null

    @JvmField
    var treasureChestTier1: Block? = null

    @JvmField
    var landingPad: Block? = null

    @JvmField
    var unlitTorch: Block? = null

    @JvmField
    var unlitTorchLit: Block? = null

    @JvmField
    var oxygenDistributor: Block? = null

    @JvmField
    var oxygenPipe: Block? = null

    @JvmField
    var oxygenPipePull: Block? = null

    @JvmField
    var oxygenCollector: Block? = null

    @JvmField
    var oxygenCompressor: Block? = null

    @JvmField
    var oxygenSealer: Block? = null

    @JvmField
    var oxygenDetector: Block? = null

    @JvmField
    var nasaWorkbench: Block? = null

    @JvmField
    var fallenMeteor: Block? = null

    @JvmField
    var basicBlock: Block? = null

    @JvmField
    var airLockFrame: Block? = null

    @JvmField
    var airLockSeal: Block? = null

    @JvmField
    var spaceGlassClear: BlockSpaceGlass? = null

    @JvmField
    var spaceGlassVanilla: BlockSpaceGlass? = null

    @JvmField
    var spaceGlassStrong: BlockSpaceGlass? = null

    @JvmField
    var spaceGlassTinClear: BlockSpaceGlass? = null

    @JvmField
    var spaceGlassTinVanilla: BlockSpaceGlass? = null

    @JvmField
    var spaceGlassTinStrong: BlockSpaceGlass? = null

    @JvmField
    var crafting: Block? = null

    @JvmField
    var crudeOil: Block? = null

    @JvmField
    var fuel: Block? = null

    @JvmField
    var refinery: Block? = null

    @JvmField
    var fuelLoader: Block? = null

    @JvmField
    var landingPadFull: Block? = null

    @JvmField
    var spaceStationBase: Block? = null

    @JvmField
    var fakeBlock: Block? = null

    @JvmField
    var sealableBlock: Block? = null

    @JvmField
    var cargoLoader: Block? = null

    @JvmField
    var parachest: Block? = null

    @JvmField
    var solarPanel: Block? = null

    @JvmField
    var radioTelescope: Block? = null

    @JvmField
    var machineBase: Block? = null

    @JvmField
    var machineBase2: Block? = null

    @JvmField
    var machineBase3: Block? = null

    @JvmField
    var machineTiered: Block? = null

    @JvmField
    var aluminumWire: Block? = null

    @JvmField
    var panelLighting: Block? = null

    @JvmField
    var glowstoneTorch: Block? = null

    @JvmField
    var blockMoon: Block? = null

    @JvmField
    var cheeseBlock: Block? = null

    @JvmField
    var spinThruster: Block? = null

    @JvmField
    var screen: Block? = null

    @JvmField
    var telemetry: Block? = null

    @JvmField
    var fluidTank: Block? = null

    @JvmField
    var bossSpawner: Block? = null

    @JvmField
    var slabGCHalf: Block? = null

    @JvmField
    var slabGCDouble: Block? = null

    @JvmField
    var tinStairs1: Block? = null

    @JvmField
    var tinStairs2: Block? = null

    @JvmField
    var moonStoneStairs: Block? = null

    @JvmField
    var moonBricksStairs: Block? = null

    @JvmField
    var wallGC: Block? = null

    @JvmField
    var concealedRedstone: Block? = null

    @JvmField
    var concealedRepeater_Powered: Block? = null

    @JvmField
    var concealedRepeater_Unpowered: Block? = null

    @JvmField
    var concealedDetector: Block? = null

    @JvmField
    var platform: Block? = null

    @JvmField
    var emergencyBox: Block? = null

    @JvmField
    var grating: Block? = null

    @JvmField
    var gratingWater: Block? = null

    @JvmField
    var gratingLava: Block? = null

    @JvmField
    val machine: Material = Material(MapColor.IRON)

    @JvmField
    var hiddenBlocks: ArrayList<Block?> = ArrayList<Block?>()

    @JvmField
    var otherModTorchesLit: ArrayList<Block?> = ArrayList<Block?>()

    @JvmField
    var otherModTorchesUnlit: ArrayList<Block?> = ArrayList<Block?>()

    @JvmField
    var sortMapBlocks: MutableMap<EnumSortCategoryBlock, MutableList<StackSorted?>?> =
        Maps.newHashMap<EnumSortCategoryBlock, MutableList<StackSorted?>?>()

    @JvmField
    var itemChanges: HashMap<Block?, Block?> = HashMap<Block?, Block?>(4, 1.0f)

    @JvmStatic
    fun initBlocks() {
        breatheableAir = BlockBreathableAir("breatheable_air")
        brightAir = BlockBrightAir("bright_air")
        brightBreatheableAir = BlockBrightBreathableAir("bright_breathable_air")
        brightLamp = BlockBrightLamp("arclamp")
        treasureChestTier1 = BlockTier1TreasureChest("treasure_chest")
        landingPad = BlockLandingPad("landing_pad")
        landingPadFull = BlockLandingPadFull("landing_pad_full")
        unlitTorch = BlockUnlitTorch(false, "unlit_torch")
        unlitTorchLit = BlockUnlitTorch(true, "unlit_torch_lit")
        oxygenDistributor = BlockOxygenDistributor("distributor")
        oxygenPipe = BlockFluidPipe("fluid_pipe", BlockFluidPipe.EnumPipeMode.NORMAL)
        oxygenPipePull = BlockFluidPipe("fluid_pipe_pull", BlockFluidPipe.EnumPipeMode.PULL)
        oxygenCollector = BlockOxygenCollector("collector")
        nasaWorkbench = BlockNasaWorkbench("rocket_workbench")
        fallenMeteor = BlockFallenMeteor("fallen_meteor")
        basicBlock = BlockBasic("basic_block_core")
        airLockFrame = BlockAirLockFrame("air_lock_frame")
        airLockSeal = BlockAirLockWall("air_lock_seal")
        //These glass types have to be registered as 6 separate blocks, (a) to allow different coloring of each one and (b) because the Forge MultiLayer custom model does not allow for different textures to be set for different variants
        spaceGlassVanilla = BlockSpaceGlass(
            "space_glass_vanilla",
            BlockSpaceGlass.GlassType.VANILLA,
            BlockSpaceGlass.GlassFrame.PLAIN,
            null
        ).setHardness(0.3f).setResistance(3f) as BlockSpaceGlass
        spaceGlassClear = BlockSpaceGlass(
            "space_glass_clear",
            BlockSpaceGlass.GlassType.CLEAR,
            BlockSpaceGlass.GlassFrame.PLAIN,
            null
        ).setHardness(0.3f).setResistance(3f) as BlockSpaceGlass
        spaceGlassStrong = BlockSpaceGlass(
            "space_glass_strong",
            BlockSpaceGlass.GlassType.STRONG,
            BlockSpaceGlass.GlassFrame.PLAIN,
            null
        ).setHardness(4f).setResistance(35f) as BlockSpaceGlass
        spaceGlassTinVanilla = BlockSpaceGlass(
            "space_glass_vanilla_tin",
            BlockSpaceGlass.GlassType.VANILLA,
            BlockSpaceGlass.GlassFrame.TIN_DECO,
            spaceGlassVanilla
        ).setHardness(0.3f).setResistance(4f) as BlockSpaceGlass
        spaceGlassTinClear = BlockSpaceGlass(
            "space_glass_clear_tin",
            BlockSpaceGlass.GlassType.CLEAR,
            BlockSpaceGlass.GlassFrame.TIN_DECO,
            spaceGlassClear
        ).setHardness(0.3f).setResistance(4f) as BlockSpaceGlass
        spaceGlassTinStrong = BlockSpaceGlass(
            "space_glass_strong_tin",
            BlockSpaceGlass.GlassType.STRONG,
            BlockSpaceGlass.GlassFrame.TIN_DECO,
            spaceGlassStrong
        ).setHardness(4f).setResistance(35f) as BlockSpaceGlass
        crafting = BlockCrafting("magnetic_table")
        refinery = BlockRefinery("refinery")
        oxygenCompressor = BlockOxygenCompressor(false, "oxygen_compressor")
        fuelLoader = BlockFuelLoader("fuel_loader")
        spaceStationBase = BlockSpaceStationBase("space_station_base")
        fakeBlock = BlockMulti("block_multi")
        oxygenSealer = BlockOxygenSealer("sealer")
        sealableBlock = BlockEnclosed("enclosed")
        oxygenDetector = BlockOxygenDetector("oxygen_detector")
        cargoLoader = BlockCargoLoader("cargo")
        parachest = BlockParaChest("parachest")
        solarPanel = BlockSolar("solar")
        radioTelescope = BlockDish("dishbase")
        machineBase = BlockMachine("machine")
        machineBase2 = BlockMachine2("machine2")
        machineBase3 = BlockMachine3("machine3")
        machineTiered = BlockMachineTiered("machine_tiered")
        aluminumWire = BlockAluminumWire("aluminum_wire")
        panelLighting = BlockPanelLighting("panel_lighting")
        glowstoneTorch = BlockGlowstoneTorch("glowstone_torch")
        blockMoon = BlockBasicMoon("basic_block_moon")
        cheeseBlock = BlockCheese("cheese")
        spinThruster = BlockSpinThruster("spin_thruster")
        screen = BlockScreen("view_screen")
        telemetry = BlockTelemetry("telemetry")
        fluidTank = BlockFluidTank("fluid_tank")
        bossSpawner = BlockBossSpawner("boss_spawner")
        slabGCHalf = BlockSlabGC("slab_gc_half", Material.ROCK)
        slabGCDouble = BlockDoubleSlabGC("slab_gc_double", Material.ROCK)
        tinStairs1 = BlockStairsGC(
            "tin_stairs_1",
            basicBlock!!.getDefaultState().withProperty(
                BlockBasic.Companion.BASIC_TYPE,
                BlockBasic.EnumBlockBasic.ALUMINUM_DECORATION_BLOCK_0
            )
        ).setHardness(2.0f)
        tinStairs2 = BlockStairsGC(
            "tin_stairs_2",
            basicBlock!!.getDefaultState().withProperty(
                BlockBasic.Companion.BASIC_TYPE,
                BlockBasic.EnumBlockBasic.ALUMINUM_DECORATION_BLOCK_1
            )
        ).setHardness(2.0f)
        moonStoneStairs = BlockStairsGC(
            "moon_stairs_stone",
            blockMoon!!.getDefaultState()
                .withProperty(
                    BlockBasicMoon.Companion.BASIC_TYPE_MOON,
                    BlockBasicMoon.EnumBlockBasicMoon.MOON_STONE
                )
        ).setHardness(1.5f)
        moonBricksStairs = BlockStairsGC(
            "moon_stairs_brick",
            blockMoon!!.getDefaultState()
                .withProperty(
                    BlockBasicMoon.Companion.BASIC_TYPE_MOON,
                    BlockBasicMoon.EnumBlockBasicMoon.MOON_DUNGEON_BRICK
                )
        ).setHardness(4.0f)
        wallGC = BlockWallGC("wall_gc")
        concealedRedstone = BlockConcealedRedstone("concealed_redstone")
        concealedRepeater_Powered = BlockConcealedRepeater("concealed_repeater_pow", true)
        concealedRepeater_Unpowered = BlockConcealedRepeater("concealed_repeater", false)
        concealedDetector = BlockConcealedDetector("concealed_detector")
        platform = BlockPlatform("platform")
        emergencyBox = BlockEmergencyBox("emergency_box")
        grating = BlockGrating("grating", if (ConfigManagerCore.allowLiquidGratings) Material.CARPET else Material.IRON)
        gratingWater = BlockGrating("grating1", Material.WATER)
        gratingLava = BlockGrating("grating2", Material.LAVA).setLightLevel(1.0f)

        // Hide certain items from NEI
        hiddenBlocks.add(airLockSeal)
        hiddenBlocks.add(oxygenPipePull)
        hiddenBlocks.add(unlitTorch)
        hiddenBlocks.add(unlitTorchLit)
        hiddenBlocks.add(landingPadFull)
        hiddenBlocks.add(spaceStationBase)
        hiddenBlocks.add(bossSpawner)
        hiddenBlocks.add(slabGCDouble)
        hiddenBlocks.add(concealedRepeater_Powered)

        // Register blocks before register ores, so that the ItemStack picks up the correct item
        registerBlocks()
        setHarvestLevels()
    }

    @JvmStatic
    fun oreDictRegistrations() {
        OreDictionary.registerOre("oreCopper", ItemStack(basicBlock, 1, 5))
        OreDictionary.registerOre("oreCopper", ItemStack(blockMoon, 1, 0))
        OreDictionary.registerOre("oreTin", ItemStack(basicBlock, 1, 6))
        OreDictionary.registerOre("oreTin", ItemStack(blockMoon, 1, 1))
        OreDictionary.registerOre("oreAluminum", ItemStack(basicBlock, 1, 7))
        OreDictionary.registerOre("oreAluminium", ItemStack(basicBlock, 1, 7))
        OreDictionary.registerOre("oreNaturalAluminum", ItemStack(basicBlock, 1, 7))
        OreDictionary.registerOre("oreSilicon", ItemStack(basicBlock, 1, 8))
        OreDictionary.registerOre("oreCheese", ItemStack(blockMoon, 1, 2))

        OreDictionary.registerOre("blockCopper", ItemStack(basicBlock, 1, 9))
        OreDictionary.registerOre("blockTin", ItemStack(basicBlock, 1, 10))
        OreDictionary.registerOre("blockAluminum", ItemStack(basicBlock, 1, 11))
        OreDictionary.registerOre("blockAluminium", ItemStack(basicBlock, 1, 11))
        OreDictionary.registerOre("blockSilicon", ItemStack(basicBlock, 1, 13))

        OreDictionary.registerOre(
            "turfMoon",
            ItemStack(blockMoon, 1, BlockBasicMoon.EnumBlockBasicMoon.MOON_TURF.getMeta())
        )
    }

    @JvmStatic
    fun finalizeSort() {
        val itemOrderListBlocks: MutableList<StackSorted?> = Lists.newArrayList<StackSorted?>()
        for (type: EnumSortCategoryBlock in EnumSortCategoryBlock.entries) {
            if (!GalacticraftCore.Companion.isPlanetsLoaded && type == EnumSortCategoryBlock.EGG) continue
            val stackSorteds: MutableList<StackSorted?>? = sortMapBlocks.get(type)
            if (stackSorteds != null) {
                itemOrderListBlocks.addAll(stackSorteds)
            } else {
                println("ERROR: null sort stack: " + type.toString())
            }
        }
        val tabSorterBlocks: Comparator<ItemStack>? = Ordering.explicit<StackSorted?>(itemOrderListBlocks)
            .onResultOf<ItemStack?>(Function { input: ItemStack? ->
                StackSorted(
                    input!!.getItem(),
                    input.getItemDamage()
                )
            })
        (GalacticraftCore.Companion.galacticraftBlocksTab)!!.setTabSorter(tabSorterBlocks)
    }

    @JvmStatic
    private fun setHarvestLevel(block: Block?, toolClass: String, level: Int, meta: Int) {
        block!!.setHarvestLevel(toolClass, level, block.getStateFromMeta(meta))
    }

    @JvmStatic
    fun doOtherModsTorches(registry: IForgeRegistry<Block?>) {
        val torch: BlockUnlitTorch
        val torchLit: BlockUnlitTorch

        if (CompatibilityManager.isTConstructLoaded) {
            var modTorch: Block? = null
            try {
                //tconstruct.world.TinkerWorld.stoneTorch
                val clazz: Class<*> = Class.forName("slimeknights.tconstruct.gadgets.TinkerGadgets")
                modTorch = clazz.getDeclaredField("stoneTorch").get(null) as Block?
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (modTorch != null) {
                torch = BlockUnlitTorch(false, "unlit_torch_stone")
                torchLit = BlockUnlitTorch(true, "unlit_torch_stone_lit")
                hiddenBlocks.add(torch)
                hiddenBlocks.add(torchLit)
                otherModTorchesUnlit.add(torch)
                otherModTorchesLit.add(torchLit)
                registerBlock(torch, ItemBlockGC::class.java)
                registerBlock(torchLit, ItemBlockGC::class.java)
                registry.register(torch)
                registry.register(torchLit)
                BlockUnlitTorch.register(torch, torchLit, modTorch)
                GCLog.info("Galacticraft: activating Tinker's Construct compatibility.")
            }
        }
    }

    @JvmStatic
    fun registerFuel() {
        fuel = BlockFluidGC((GCFluids.fluidFuel)!!, "fuel")
        (fuel as BlockFluidGC).setQuantaPerBlock(3)
        ((fuel)!!.setTranslationKey("fuel"))!!
        registerBlock(fuel, ItemBlockGC::class.java)
    }

    @JvmStatic
    fun registerOil() {
        crudeOil = BlockFluidGC((GCFluids.fluidOil)!!, "oil")
        (crudeOil as BlockFluidGC).setQuantaPerBlock(3)
        ((crudeOil)!!.setTranslationKey("crude_oil_still"))!!
        registerBlock(crudeOil, ItemBlockGC::class.java)
    }

    @JvmStatic
    fun setHarvestLevels() {
        setHarvestLevel(basicBlock, "pickaxe", 1, 5) //Copper ore
        setHarvestLevel(basicBlock, "pickaxe", 1, 6) //Tin ore
        setHarvestLevel(basicBlock, "pickaxe", 1, 7) //Aluminium ore
        setHarvestLevel(basicBlock, "pickaxe", 2, 8) //Silicon ore
        setHarvestLevel(fallenMeteor, "pickaxe", 3, 0)
        setHarvestLevel(blockMoon, "pickaxe", 1, 0) //Copper ore
        setHarvestLevel(blockMoon, "pickaxe", 1, 1) //Tin ore
        setHarvestLevel(blockMoon, "pickaxe", 1, 2) //Cheese ore
        setHarvestLevel(blockMoon, "shovel", 0, 3) //Moon dirt
        setHarvestLevel(blockMoon, "pickaxe", 0, 4) //Moon rock

        setHarvestLevel(slabGCHalf, "pickaxe", 1, 0)
        setHarvestLevel(slabGCHalf, "pickaxe", 1, 1)
        setHarvestLevel(slabGCHalf, "pickaxe", 1, 2)
        setHarvestLevel(slabGCHalf, "pickaxe", 3, 3)
        setHarvestLevel(slabGCHalf, "pickaxe", 1, 4)
        setHarvestLevel(slabGCHalf, "pickaxe", 3, 5)
        setHarvestLevel(slabGCHalf, "pickaxe", 1, 6)

        setHarvestLevel(slabGCDouble, "pickaxe", 1, 0)
        setHarvestLevel(slabGCDouble, "pickaxe", 1, 1)
        setHarvestLevel(slabGCDouble, "pickaxe", 1, 2)
        setHarvestLevel(slabGCDouble, "pickaxe", 3, 3)
        setHarvestLevel(slabGCDouble, "pickaxe", 1, 4)
        setHarvestLevel(slabGCDouble, "pickaxe", 3, 5)
        setHarvestLevel(slabGCDouble, "pickaxe", 1, 6)

        setHarvestLevel(tinStairs1, "pickaxe", 1, 0)
        setHarvestLevel(tinStairs1, "pickaxe", 1, 0)

        setHarvestLevel(moonStoneStairs, "pickaxe", 1, 0)
        setHarvestLevel(moonBricksStairs, "pickaxe", 3, 0)

        setHarvestLevel(wallGC, "pickaxe", 1, 0)
        setHarvestLevel(wallGC, "pickaxe", 1, 1)
        setHarvestLevel(wallGC, "pickaxe", 1, 2)
        setHarvestLevel(wallGC, "pickaxe", 3, 3)
        setHarvestLevel(wallGC, "pickaxe", 0, 4)
        setHarvestLevel(wallGC, "pickaxe", 3, 5)

        setHarvestLevel(wallGC, "shovel", 0, 5)

        setHarvestLevel(blockMoon, "pickaxe", 3, 14) //Moon dungeon brick (actually unharvestable)
    }

    @JvmStatic
    fun registerBlock(block: Block?, itemClass: Class<out ItemBlock>?, vararg itemCtorArgs: Any?) {
        val name: String = (block)!!.getTranslationKey().substring(5)
        if (block!!.getRegistryName() == null) {
            block.setRegistryName(name)
        }
        GCCoreUtil.registerGalacticraftBlock(name, block)

        if (itemClass != null) {
            var item: ItemBlock? = null
            val ctorArgClasses: Array<Class<*>?> = arrayOfNulls<Class<*>>(itemCtorArgs.size + 1)
            ctorArgClasses[0] = Block::class.java
            for (idx: Int in 1..<ctorArgClasses.size) {
                ctorArgClasses[idx] = itemCtorArgs[idx - 1]!!.javaClass
            }

            try {
                val constructor: Constructor<out ItemBlock?> = itemClass.getConstructor(*ctorArgClasses)
                item = constructor.newInstance(*ObjectArrays.concat<Any?>(block, itemCtorArgs))
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (item != null) {
                GCCoreUtil.registerGalacticraftItem(name, item)
                if (item.getRegistryName() == null) {
                    item.setRegistryName(name)
                }
            }
        }
    }

    @JvmStatic
    fun registerBlocks(registry: IForgeRegistry<Block?>) {
        for (block: Block? in GalacticraftCore.Companion.blocksList) {
            registry.register(block)
        }


        //Complete registration of various types of torches
        BlockUnlitTorch.register((unlitTorch as BlockUnlitTorch?)!!, (unlitTorchLit as BlockUnlitTorch?)!!, Blocks.TORCH)
    }

    @JvmField
    var registeringSorted: Boolean = false

    @JvmStatic
    fun registerSorted(block: Block) {
        if (block is ISortableBlock) {
            val item: Item = Item.getItemFromBlock(block)
            if (item === Items.AIR) {
                return
            }
            val sortableBlock: ISortableBlock = block as ISortableBlock
            val blocks: NonNullList<ItemStack> = NonNullList.create<ItemStack>()
            registeringSorted = true
            block.getSubBlocks(net.minecraft.creativetab.CreativeTabs.SEARCH, blocks)
            registeringSorted = false
            if (blocks.isEmpty()) {
                blocks.add(ItemStack(block))
            }
            for (stack: ItemStack in blocks) {
                val categoryBlock: EnumSortCategoryBlock? = sortableBlock.getCategory(stack.getItemDamage())
                if (!sortMapBlocks.containsKey(categoryBlock)) {
                    sortMapBlocks.put((categoryBlock)!!, ArrayList<StackSorted?>())
                }
                sortMapBlocks.get(categoryBlock)!!.add(StackSorted(stack.getItem(), stack.getItemDamage()))
            }
        } else if (block.getCreativeTab() === GalacticraftCore.Companion.galacticraftBlocksTab) {
            throw RuntimeException(block.javaClass.toString() + " must inherit " + ISortableBlock::class.java.getSimpleName() + "!")
        }
    }

    @JvmStatic
    fun registerBlocks() {
        registerBlock(landingPad, ItemBlockLandingPad::class.java)
        registerBlock(landingPadFull, ItemBlockGC::class.java)
        registerBlock(unlitTorch, ItemBlockGC::class.java)
        registerBlock(unlitTorchLit, ItemBlockGC::class.java)
        registerBlock(breatheableAir, null)
        registerBlock(brightAir, null)
        registerBlock(brightBreatheableAir, null)
        registerBlock(oxygenDistributor, ItemBlockDesc::class.java)
        registerBlock(oxygenCollector, ItemBlockDesc::class.java)
        registerBlock(oxygenCompressor, ItemBlockOxygenCompressor::class.java)
        registerBlock(oxygenSealer, ItemBlockDesc::class.java)
        registerBlock(oxygenDetector, ItemBlockDesc::class.java)
        registerBlock(aluminumWire, ItemBlockAluminumWire::class.java)
        registerBlock(oxygenPipe, ItemBlockDesc::class.java)
        registerBlock(oxygenPipePull, ItemBlockDesc::class.java)
        registerBlock(refinery, ItemBlockDesc::class.java)
        registerBlock(fuelLoader, ItemBlockDesc::class.java)
        registerBlock(cargoLoader, ItemBlockCargoLoader::class.java)
        registerBlock(nasaWorkbench, ItemBlockNasaWorkbench::class.java)
        registerBlock(basicBlock, ItemBlockBase::class.java)
        registerBlock(airLockFrame, ItemBlockAirLock::class.java)
        registerBlock(airLockSeal, ItemBlockGC::class.java)
        registerBlock(spaceGlassClear, ItemBlockGlassGC::class.java)
        registerBlock(spaceGlassVanilla, ItemBlockGlassGC::class.java)
        registerBlock(spaceGlassStrong, ItemBlockGlassGC::class.java)
        registerBlock(spaceGlassTinClear, null) //The corresponding item is already registered
        registerBlock(spaceGlassTinVanilla, null) //The corresponding item is already registered
        registerBlock(spaceGlassTinStrong, null) //The corresponding item is already registered
        registerBlock(crafting, ItemBlockDesc::class.java)
        registerBlock(sealableBlock, ItemBlockEnclosed::class.java)
        registerBlock(spaceStationBase, ItemBlockGC::class.java)
        registerBlock(fakeBlock, null)
        registerBlock(parachest, ItemBlockDesc::class.java)
        registerBlock(solarPanel, ItemBlockSolar::class.java)
        registerBlock(radioTelescope, ItemBlockGC::class.java)
        registerBlock(machineBase, ItemBlockMachine::class.java)
        registerBlock(machineBase2, ItemBlockMachine::class.java)
        registerBlock(machineBase3, ItemBlockMachine::class.java)
        registerBlock(machineTiered, ItemBlockMachine::class.java)
        registerBlock(panelLighting, ItemBlockPanel::class.java)
        registerBlock(glowstoneTorch, ItemBlockDesc::class.java)
        registerBlock(fallenMeteor, ItemBlockDesc::class.java)
        registerBlock(blockMoon, ItemBlockMoon::class.java)
        registerBlock(cheeseBlock, ItemBlockCheese::class.java)
        registerBlock(spinThruster, ItemBlockThruster::class.java)
        registerBlock(screen, ItemBlockDesc::class.java)
        registerBlock(telemetry, ItemBlockDesc::class.java)
        registerBlock(brightLamp, ItemBlockArclamp::class.java)
        registerBlock(treasureChestTier1, ItemBlockDesc::class.java)
        registerBlock(fluidTank, ItemBlockDesc::class.java)
        registerBlock(bossSpawner, ItemBlockGC::class.java)
        registerBlock(tinStairs1, ItemBlockGC::class.java)
        registerBlock(tinStairs2, ItemBlockGC::class.java)
        registerBlock(moonStoneStairs, ItemBlockGC::class.java)
        registerBlock(moonBricksStairs, ItemBlockGC::class.java)
        registerBlock(wallGC, ItemBlockWallGC::class.java)
        registerBlock(slabGCHalf, ItemBlockSlabGC::class.java, slabGCHalf, slabGCDouble)
        registerBlock(slabGCDouble, ItemBlockSlabGC::class.java, slabGCHalf, slabGCDouble)
        registerBlock(concealedRedstone, ItemBlockGC::class.java)
        registerBlock(concealedRepeater_Powered, ItemBlockGC::class.java)
        registerBlock(concealedRepeater_Unpowered, ItemBlockGC::class.java)
        registerBlock(concealedDetector, ItemBlockCreativeGC::class.java)
        registerBlock(platform, ItemBlockDesc::class.java)
        registerBlock(emergencyBox, ItemBlockEmergencyBox::class.java)
        registerBlock(grating, ItemBlockGC::class.java)
        registerBlock(gratingWater, null)
        registerBlock(gratingLava, null)
        //        GCCoreUtil.sortBlock(GCBlocks.aluminumWire, 0, new StackSorted(GCBlocks.landingPad, 1));
//        GCCoreUtil.sortBlock(GCBlocks.aluminumWire, 1, new StackSorted(GCBlocks.aluminumWire, 0));
//        GCCoreUtil.sortBlock(GCBlocks.oxygenPipe, 0, new StackSorted(GCBlocks.aluminumWire, 1));
    }
}
