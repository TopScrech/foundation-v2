package galaxyspace.systems.BarnardsSystem.core

import galaxyspace.systems.BarnardsSystem.core.BRBlocks
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Ores
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Grass
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Blocks
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Falling_Blocks
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Logs
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Cactus
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Dandelions
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Glow_Dandelions
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Leaves
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Farmland
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Water_Grass
import galaxyspace.core.prefab.blocks.BlockDoorGS
import galaxyspace.core.prefab.blocks.BlockTrapDoorGS
import galaxyspace.systems.BarnardsSystem.moons.barnarda_c1.blocks.Barnarda_C1_Blocks
import galaxyspace.systems.BarnardsSystem.moons.barnarda_c1.blocks.Barnarda_C1_Decorations
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.items.ItemBlocksBarnarda_C_Grasses
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.items.ItemBlocksBarnarda_C
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.items.ItemBlocksBarnarda_C_Falling
import galaxyspace.core.prefab.items.GSItemBlockDesc
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.items.ItemBlocksBarnarda_C_Dandelions
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.items.ItemBlocksBarnarda_C_Glow_Dandelions
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.items.ItemBlocksBarnarda_C_Leaves
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.items.ItemBlocksBarnarda_C_Ores
import galaxyspace.systems.BarnardsSystem.moons.barnarda_c1.items.ItemBlocksBarnarda_C1
import galaxyspace.systems.BarnardsSystem.moons.barnarda_c1.items.ItemBlocksBarnarda_C1_Decorations
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Reeds
import micdoodle8.mods.galacticraft.core.GCBlocks
import net.minecraft.world.level.block.Block
import net.minecraft.item.ItemBlock
import net.minecraft.world.item.ItemStack
import net.minecraftforge.oredict.OreDictionary

object BRBlocks {
    @JvmField
    val BARNARDA_C_GRASS: Block = Barnarda_C_Grass()

    @JvmField
    val BARNARDA_C_BLOCKS: Block = Barnarda_C_Blocks()

    @JvmField
    val BARNARDA_C_FALLING_BLOCKS: Block = Barnarda_C_Falling_Blocks()

    @JvmField
    val BARNARDA_C_VIOLET_LOG: Block = Barnarda_C_Logs("barnarda_c_test_log", 1)

    @JvmField
    val BARNARDA_C_VIOLET_GLOW_LOG: Block = Barnarda_C_Logs("barnarda_c_test_glow_log", 1)

    @JvmField
    val BARNARDA_C_BIRCH_LOG: Block = Barnarda_C_Logs("barnarda_c_birch_log", 1)

    @JvmField
    val BARNARDA_C_CACTUS: Block = Barnarda_C_Cactus()

    @JvmField
    val BARNARDA_C_DANDELIONS: Block = Barnarda_C_Dandelions()

    @JvmField
    val BARNARDA_C_GLOW_DANDELIONS: Block = Barnarda_C_Glow_Dandelions()

    @JvmField
    val BARNARDA_C_LEAVES: Block = Barnarda_C_Leaves()

    //public static final Block BARNARDA_C_REEDS = new Barnarda_C_Reeds();
    @JvmField
    val BARNARDA_C_FARMLAND: Block = Barnarda_C_Farmland()

    @JvmField
    val BARNARDA_C_WATER_GRASS: Block = Barnarda_C_Water_Grass()

    @JvmField
    val BARNARDA_C_ORES: Block = Barnarda_C_Ores()

    @JvmField
    val BARNARDA_C_VIOLET_DOOR: BlockDoorGS = BlockDoorGS("barnarda_c_violet_door")

    @JvmField
    val BARNARDA_C_VIOLET_TRAPDOOR: BlockTrapDoorGS = BlockTrapDoorGS("barnarda_c_violet_trapdoor")

    @JvmField
    val BARNARDA_C1_BLOCKS: Block = Barnarda_C1_Blocks()

    @JvmField
    val BARNARDA_C1_DECORATIONS: Block = Barnarda_C1_Decorations()

    @JvmStatic
    fun initialize() {
        registerBlock(BARNARDA_C_GRASS, ItemBlocksBarnarda_C_Grasses::class.java)
        registerBlock(BARNARDA_C_BLOCKS, ItemBlocksBarnarda_C::class.java)
        registerBlock(BARNARDA_C_FALLING_BLOCKS, ItemBlocksBarnarda_C_Falling::class.java)
        registerBlock(BARNARDA_C_VIOLET_LOG, GSItemBlockDesc::class.java)
        registerBlock(BARNARDA_C_BIRCH_LOG, GSItemBlockDesc::class.java)
        registerBlock(BARNARDA_C_CACTUS, GSItemBlockDesc::class.java)
        registerBlock(BARNARDA_C_VIOLET_GLOW_LOG, GSItemBlockDesc::class.java)
        registerBlock(BARNARDA_C_DANDELIONS, ItemBlocksBarnarda_C_Dandelions::class.java)
        registerBlock(BARNARDA_C_GLOW_DANDELIONS, ItemBlocksBarnarda_C_Glow_Dandelions::class.java)
        registerBlock(BARNARDA_C_LEAVES, ItemBlocksBarnarda_C_Leaves::class.java)
        registerBlock(BARNARDA_C_FARMLAND, GSItemBlockDesc::class.java)
        registerBlock(BARNARDA_C_WATER_GRASS, GSItemBlockDesc::class.java)
        registerBlock(BARNARDA_C_ORES, ItemBlocksBarnarda_C_Ores::class.java)
        registerBlock(BARNARDA_C_VIOLET_DOOR, null)
        registerBlock(BARNARDA_C_VIOLET_TRAPDOOR, GSItemBlockDesc::class.java)

        registerBlock(BARNARDA_C1_BLOCKS, ItemBlocksBarnarda_C1::class.java)
        registerBlock(BARNARDA_C1_DECORATIONS, ItemBlocksBarnarda_C1_Decorations::class.java)
    }

    @JvmStatic
    fun oreDictRegistration() {
        OreDictionary.registerOre("dirt", ItemStack(BARNARDA_C_BLOCKS, 1, 0))
        OreDictionary.registerOre("stone", ItemStack(BARNARDA_C_BLOCKS, 1, 1))
        OreDictionary.registerOre("sandstone", ItemStack(BARNARDA_C_BLOCKS, 1, 2))
        OreDictionary.registerOre("dirt", ItemStack(BARNARDA_C_BLOCKS, 1, 3))
        OreDictionary.registerOre("cobblestone", ItemStack(BARNARDA_C_BLOCKS, 1, 4))
        OreDictionary.registerOre("plankWood", ItemStack(BARNARDA_C_BLOCKS, 1, 6))

        OreDictionary.registerOre("sand", ItemStack(BARNARDA_C_FALLING_BLOCKS, 1, 0))
        OreDictionary.registerOre("logWood", ItemStack(BARNARDA_C_VIOLET_LOG, 1, 0))

        OreDictionary.registerOre("oreCoal", ItemStack(BARNARDA_C_ORES, 1, 0))
        OreDictionary.registerOre("oreIron", ItemStack(BARNARDA_C_ORES, 1, 1))
        OreDictionary.registerOre("oreGold", ItemStack(BARNARDA_C_ORES, 1, 2))
        OreDictionary.registerOre("oreRedstone", ItemStack(BARNARDA_C_ORES, 1, 3))
        OreDictionary.registerOre("oreLapis", ItemStack(BARNARDA_C_ORES, 1, 4))
        OreDictionary.registerOre("oreDiamond", ItemStack(BARNARDA_C_ORES, 1, 5))
        OreDictionary.registerOre("oreSilicon", ItemStack(BARNARDA_C_ORES, 1, 6))
        OreDictionary.registerOre("oreCopper", ItemStack(BARNARDA_C_ORES, 1, 7))
        OreDictionary.registerOre("oreTin", ItemStack(BARNARDA_C_ORES, 1, 8))
        OreDictionary.registerOre("oreAluminum", ItemStack(BARNARDA_C_ORES, 1, 9))
        OreDictionary.registerOre("oreQuartz", ItemStack(BARNARDA_C_ORES, 1, 10))
        OreDictionary.registerOre("oreCobalt", ItemStack(BARNARDA_C_ORES, 1, 11))
        OreDictionary.registerOre("oreNickel", ItemStack(BARNARDA_C_ORES, 1, 12))

        OreDictionary.registerOre("stone", ItemStack(BARNARDA_C1_BLOCKS, 1, 3))
    }

    @JvmStatic
    fun registerBlock(block: Block, itemClass: Class<out ItemBlock>?) {
        GCBlocks.registerBlock(block, itemClass)
    }
}
