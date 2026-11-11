package galaxyspace.systems.ACentauriSystem.core

import galaxyspace.systems.ACentauriSystem.core.ACBlocks
import galaxyspace.systems.ACentauriSystem.planets.proxima_b.blocks.Proxima_B_Blocks
import galaxyspace.systems.ACentauriSystem.planets.proxima_b.blocks.Proxima_B_Logs
import galaxyspace.systems.ACentauriSystem.planets.proxima_b.blocks.Proxima_B_Dandelions
import galaxyspace.systems.ACentauriSystem.planets.proxima_b.blocks.Proxima_B_Grass
import galaxyspace.systems.ACentauriSystem.planets.proxima_b.items.ItemBlocksProxima_B
import galaxyspace.core.prefab.items.GSItemBlockDesc
import galaxyspace.systems.ACentauriSystem.planets.proxima_b.items.ItemBlocksProxima_B_Dandelions
import micdoodle8.mods.galacticraft.core.GCBlocks
import net.minecraft.world.level.block.Block
import net.minecraft.item.ItemBlock
import net.minecraft.world.item.ItemStack
import net.minecraftforge.oredict.OreDictionary

object ACBlocks {
    @JvmField
    val PROXIMA_B_BLOCKS: Block = Proxima_B_Blocks().setHardness(3.0f)

    @JvmField
    val PROXIMA_B_LOG_1: Block = Proxima_B_Logs("proxima_b_log_1")

    @JvmField
    val PROXIMA_B_LOG_2: Block = Proxima_B_Logs("proxima_b_log_2")

    @JvmField
    val PROXIMA_B_MUSHROOM_LOG: Block = Proxima_B_Logs("proxima_b_mushroom_log")

    @JvmField
    val PROXIMA_B_DANDELIONS: Block = Proxima_B_Dandelions()

    @JvmField
    val PROXIMA_B_GRASS: Block = Proxima_B_Grass()

    @JvmStatic
    fun initialize() {
        registerBlock(PROXIMA_B_BLOCKS, ItemBlocksProxima_B::class.java)
        registerBlock(PROXIMA_B_LOG_1, GSItemBlockDesc::class.java)
        registerBlock(PROXIMA_B_LOG_2, GSItemBlockDesc::class.java)
        registerBlock(PROXIMA_B_MUSHROOM_LOG, GSItemBlockDesc::class.java)
        registerBlock(PROXIMA_B_GRASS, GSItemBlockDesc::class.java)
        registerBlock(PROXIMA_B_DANDELIONS, ItemBlocksProxima_B_Dandelions::class.java)
    }

    @JvmStatic
    fun oreDictRegistration() {
        OreDictionary.registerOre("oreGold", ItemStack(PROXIMA_B_BLOCKS, 1, 5))
        OreDictionary.registerOre("oreTin", ItemStack(PROXIMA_B_BLOCKS, 1, 6))
        OreDictionary.registerOre("oreCopper", ItemStack(PROXIMA_B_BLOCKS, 1, 7))
        OreDictionary.registerOre("oreCoal", ItemStack(PROXIMA_B_BLOCKS, 1, 8))
        OreDictionary.registerOre("oreSilicon", ItemStack(PROXIMA_B_BLOCKS, 1, 9))
        OreDictionary.registerOre("oreDiamond", ItemStack(PROXIMA_B_BLOCKS, 1, 10))
    }

    @JvmStatic
    fun registerBlock(block: Block, itemClass: Class<out ItemBlock>?) {
        GCBlocks.registerBlock(block, itemClass)
    }
}
