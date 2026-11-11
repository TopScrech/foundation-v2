package galaxyspace.systems.TauCetiSystem.core

import galaxyspace.systems.TauCetiSystem.core.TCBlocks
import galaxyspace.systems.TauCetiSystem.planets.tauceti_f.blocks.TauCeti_F_Watergrass
import galaxyspace.systems.TauCetiSystem.planets.tauceti_f.blocks.TauCeti_F_Corals
import galaxyspace.systems.TauCetiSystem.planets.tauceti_f.blocks.TauCeti_F_Blocks
import galaxyspace.systems.TauCetiSystem.planets.tauceti_f.items.ItemBlocksTauCeti_F
import galaxyspace.systems.TauCetiSystem.planets.tauceti_f.items.ItemBlocksTauCeti_F_Dandelions
import galaxyspace.systems.TauCetiSystem.planets.tauceti_f.items.ItemBlocksTauCeti_F_Corals
import micdoodle8.mods.galacticraft.core.GCBlocks
import net.minecraft.world.level.block.Block
import net.minecraft.item.ItemBlock

object TCBlocks {
    @JvmField
    val TAUCETI_F_BLOCKS: Block = TauCeti_F_Blocks()

    @JvmField
    val TAUCETI_F_WATERGRASS: Block = TauCeti_F_Watergrass()

    @JvmField
    val TAUCETI_F_CORALS: Block = TauCeti_F_Corals()

    @JvmStatic
    fun initialize() {
        registerBlock(TAUCETI_F_BLOCKS, ItemBlocksTauCeti_F::class.java)
        registerBlock(TAUCETI_F_WATERGRASS, ItemBlocksTauCeti_F_Dandelions::class.java)
        registerBlock(TAUCETI_F_CORALS, ItemBlocksTauCeti_F_Corals::class.java)
    }

    @JvmStatic
    fun oreDictRegistration() {
    }

    @JvmStatic
    private fun registerBlock(block: Block, itemClass: Class<out ItemBlock>) {
        GCBlocks.registerBlock(block, itemClass)
    }
}
