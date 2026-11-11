package micdoodle8.mods.galacticraft.planets.mars.blocks

import foundation.block.getStateFromMeta

import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.blocks.BlockStairsGC
import micdoodle8.mods.galacticraft.core.items.ItemBlockDesc
import micdoodle8.mods.galacticraft.core.items.ItemBlockGC
import micdoodle8.mods.galacticraft.planets.mars.items.ItemBlockEgg
import micdoodle8.mods.galacticraft.planets.mars.items.ItemBlockMachine
import micdoodle8.mods.galacticraft.planets.mars.items.ItemBlockMars
import net.minecraft.world.level.block.Block
import net.minecraft.item.ItemBlock
import net.minecraft.world.item.ItemStack
import net.minecraftforge.oredict.OreDictionary

object MarsBlocks {
    @JvmField
    var marsBlock: Block? = null

    @JvmField
    var blockSludge: Block? = null

    @JvmField
    var vine: Block? = null

    @JvmField
    var rock: Block? = null

    @JvmField
    var treasureChestTier2: Block? = null

    @JvmField
    var machine: Block? = null

    @JvmField
    var machineT2: Block? = null

    @JvmField
    var creeperEgg: Block? = null

    @JvmField
    var marsCobblestoneStairs: Block? = null

    @JvmField
    var marsBricksStairs: Block? = null

    @JvmField
    var bossSpawner: Block? = null

    @JvmStatic
    fun initBlocks() {
        marsBlock = BlockBasicMars("mars").setHardness(2.2f)
        vine = BlockCavernousVine("cavern_vines").setHardness(0.1f)
        rock = BlockSlimelingEgg("slimeling_egg").setHardness(0.75f)
        treasureChestTier2 = BlockTier2TreasureChest("treasure_t2")
        machine = BlockMachineMars("mars_machine").setHardness(1.8f)
        machineT2 = BlockMachineMarsT2("mars_machine_t2").setHardness(1.8f)
        creeperEgg = BlockCreeperEgg("creeper_egg").setHardness(-1.0f)
        bossSpawner = BlockBossSpawnerMars("boss_spawner_mars")
        marsCobblestoneStairs = BlockStairsGC(
            "mars_stairs_cobblestone",
            marsBlock!!.defaultBlockState().setValue(
                BlockBasicMars.BASIC_TYPE,
                BlockBasicMars.EnumBlockBasic.COBBLESTONE
            )
        ).setHardness(1.5f)
        marsBricksStairs = BlockStairsGC(
            "mars_stairs_brick",
            marsBlock!!.defaultBlockState().setValue(
                BlockBasicMars.BASIC_TYPE,
                BlockBasicMars.EnumBlockBasic.DUNGEON_BRICK
            )
        ).setHardness(4.0f)

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
        setHarvestLevel(marsBlock, "pickaxe", 1, 0) //Copper ore
        setHarvestLevel(marsBlock, "pickaxe", 1, 1) //Tin ore
        setHarvestLevel(marsBlock, "pickaxe", 3, 2) //Desh ore
        setHarvestLevel(marsBlock, "pickaxe", 1, 3) //Iron ore
        setHarvestLevel(marsBlock, "pickaxe", 0, 4) //Cobblestone
        setHarvestLevel(marsBlock, "pickaxe", 3, 7) //Dungeon brick
        setHarvestLevel(marsBlock, "pickaxe", 0, 8) //Decoration block
        setHarvestLevel(marsBlock, "pickaxe", 1, 9) //Stone
        setHarvestLevel(marsBlock, "shovel", 0, 5) //Top dirt
        setHarvestLevel(marsBlock, "shovel", 0, 6) //Dirt
        setHarvestLevel(rock, "pickaxe", 3)
        //        setHarvestLevel(MarsBlocks.marsCobblestoneStairs, "pickaxe", 0);
//        setHarvestLevel(MarsBlocks.marsBricksStairs, "pickaxe", 3);
    }

    @JvmStatic
    fun registerBlock(block: Block?, itemClass: Class<out ItemBlock>?) {
        GCBlocks.registerBlock(block, itemClass)
    }

    @JvmStatic
    fun registerBlocks() {
        registerBlock(treasureChestTier2, ItemBlockDesc::class.java)
        registerBlock(marsBlock, ItemBlockMars::class.java)
        registerBlock(vine, ItemBlockDesc::class.java)
        registerBlock(rock, ItemBlockEgg::class.java)
        registerBlock(creeperEgg, ItemBlockDesc::class.java)
        registerBlock(machine, ItemBlockMachine::class.java)
        registerBlock(machineT2, ItemBlockMachine::class.java)
        registerBlock(bossSpawner, ItemBlockGC::class.java)
        registerBlock(marsCobblestoneStairs, ItemBlockGC::class.java)
        registerBlock(marsBricksStairs, ItemBlockGC::class.java)
    }

    @JvmStatic
    fun oreDictRegistration() {
        OreDictionary.registerOre("oreCopper", ItemStack(marsBlock, 1, 0))
        OreDictionary.registerOre("oreTin", ItemStack(marsBlock, 1, 1))
        OreDictionary.registerOre("oreIron", ItemStack(marsBlock, 1, 3))
        OreDictionary.registerOre("oreDesh", ItemStack(marsBlock, 1, 2))
        OreDictionary.registerOre("blockDesh", ItemStack(marsBlock, 1, 8))
    }
}
