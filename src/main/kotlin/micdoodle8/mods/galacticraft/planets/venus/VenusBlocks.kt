package micdoodle8.mods.galacticraft.planets.venus

import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.items.ItemBlockDesc
import micdoodle8.mods.galacticraft.core.items.ItemBlockGC
import micdoodle8.mods.galacticraft.planets.venus.blocks.BlockBasicVenus
import micdoodle8.mods.galacticraft.planets.venus.blocks.BlockBossSpawnerVenus
import micdoodle8.mods.galacticraft.planets.venus.blocks.BlockCrashedProbe
import micdoodle8.mods.galacticraft.planets.venus.blocks.BlockGeothermalGenerator
import micdoodle8.mods.galacticraft.planets.venus.blocks.BlockScorchedRock
import micdoodle8.mods.galacticraft.planets.venus.blocks.BlockSolarArrayController
import micdoodle8.mods.galacticraft.planets.venus.blocks.BlockSolarArrayModule
import micdoodle8.mods.galacticraft.planets.venus.blocks.BlockSpout
import micdoodle8.mods.galacticraft.planets.venus.blocks.BlockTier3TreasureChest
import micdoodle8.mods.galacticraft.planets.venus.blocks.BlockTorchWeb
import micdoodle8.mods.galacticraft.planets.venus.items.ItemBlockBasicVenus
import micdoodle8.mods.galacticraft.planets.venus.items.ItemBlockTorchWeb
import net.minecraft.block.Block
import net.minecraft.item.ItemBlock
import net.minecraftforge.oredict.OreDictionary

object VenusBlocks {
    @JvmField
    var venusBlock: Block? = null

    @JvmField
    var spout: Block? = null

    @JvmField
    var bossSpawner: Block? = null

    @JvmField
    var treasureChestTier3: Block? = null

    @JvmField
    var torchWeb: Block? = null

    @JvmField
    var sulphuricAcid: Block? = null

    @JvmField
    var geothermalGenerator: Block? = null

    @JvmField
    var crashedProbe: Block? = null

    @JvmField
    var scorchedRock: Block? = null

    @JvmField
    var solarArrayModule: Block? = null

    @JvmField
    var solarArrayController: Block? = null

    @JvmStatic
    fun initBlocks() {
        venusBlock = BlockBasicVenus("venus")
        spout = BlockSpout("spout")
        bossSpawner = BlockBossSpawnerVenus("boss_spawner_venus")
        treasureChestTier3 = BlockTier3TreasureChest("treasure_t3")
        torchWeb = BlockTorchWeb("web_torch")
        geothermalGenerator = BlockGeothermalGenerator("geothermal_generator")
        crashedProbe = BlockCrashedProbe("crashed_probe")
        scorchedRock = BlockScorchedRock("venus_rock_scorched")
        solarArrayModule = BlockSolarArrayModule("solar_array_module")
        solarArrayController = BlockSolarArrayController("solar_array_controller")

        GCBlocks.hiddenBlocks.add(bossSpawner)

        registerBlocks()
        setHarvestLevels()
    }

    @JvmStatic
    private fun setHarvestLevel(block: Block?, toolClass: String, level: Int, meta: Int) {
        block!!.setHarvestLevel(toolClass, level, block.getStateFromMeta(meta))
    }

    @JvmStatic
    private fun setHarvestLevel(block: Block?, toolClass: String, level: Int) {
        block!!.setHarvestLevel(toolClass, level)
    }

    @JvmStatic
    fun setHarvestLevels() {
        setHarvestLevel(venusBlock, "pickaxe", 1, 0)
        setHarvestLevel(venusBlock, "pickaxe", 1, 1)
        setHarvestLevel(venusBlock, "pickaxe", 1, 2)
        setHarvestLevel(venusBlock, "pickaxe", 1, 3)
        setHarvestLevel(spout, "pickaxe", 1)
    }

    @JvmStatic
    fun registerBlock(block: Block?, itemClass: Class<out ItemBlock>?) {
        GCBlocks.registerBlock(block, itemClass)
    }

    @JvmStatic
    fun registerBlocks() {
        registerBlock(venusBlock, ItemBlockBasicVenus::class.java)
        registerBlock(spout, ItemBlockGC::class.java)
        registerBlock(bossSpawner, ItemBlockGC::class.java)
        registerBlock(treasureChestTier3, ItemBlockDesc::class.java)
        registerBlock(torchWeb, ItemBlockTorchWeb::class.java)
        registerBlock(geothermalGenerator, ItemBlockDesc::class.java)
        registerBlock(crashedProbe, ItemBlockDesc::class.java)
        registerBlock(scorchedRock, ItemBlockGC::class.java)
        registerBlock(solarArrayModule, ItemBlockDesc::class.java)
        registerBlock(solarArrayController, ItemBlockDesc::class.java)
    }

    @JvmStatic
    fun oreDictRegistration() {
        OreDictionary.registerOre("oreCopper", BlockBasicVenus.EnumBlockBasicVenus.ORE_COPPER.getItemStack())
        OreDictionary.registerOre("oreTin", BlockBasicVenus.EnumBlockBasicVenus.ORE_TIN.getItemStack())
        OreDictionary.registerOre("oreAluminum", BlockBasicVenus.EnumBlockBasicVenus.ORE_ALUMINUM.getItemStack())
        OreDictionary.registerOre("oreAluminium", BlockBasicVenus.EnumBlockBasicVenus.ORE_ALUMINUM.getItemStack())
        OreDictionary.registerOre("oreNaturalAluminum", BlockBasicVenus.EnumBlockBasicVenus.ORE_ALUMINUM.getItemStack())
        OreDictionary.registerOre("oreQuartz", BlockBasicVenus.EnumBlockBasicVenus.ORE_QUARTZ.getItemStack())
        OreDictionary.registerOre("oreLead", BlockBasicVenus.EnumBlockBasicVenus.ORE_GALENA.getItemStack())
        OreDictionary.registerOre("oreSilicon", BlockBasicVenus.EnumBlockBasicVenus.ORE_SILICON.getItemStack())
        OreDictionary.registerOre("oreSolar", BlockBasicVenus.EnumBlockBasicVenus.ORE_SOLAR_DUST.getItemStack())

        OreDictionary.registerOre("blockLead", BlockBasicVenus.EnumBlockBasicVenus.LEAD_BLOCK.getItemStack())
    }
}
