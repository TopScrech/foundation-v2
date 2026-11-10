package galaxyspace.systems.BarnardsSystem.planets.barnarda_c.items

import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Glow_Dandelions.EnumBlockGlowDandelions

import galaxyspace.core.prefab.items.GSItemBlockDesc
import galaxyspace.systems.BarnardsSystem.core.BRBlocks
import net.minecraft.block.Block
import net.minecraft.item.ItemStack

open class ItemBlocksBarnarda_C_Glow_Dandelions constructor(block: Block?) : GSItemBlockDesc((block)!!) {
    init {
        setMaxDamage(0)
        setHasSubtypes(true)
    }

    override fun getMetadata(meta: Int): Int {
        return meta
    }

    override fun getTranslationKey(`is`: ItemStack): String {
        /*
        int metadata = is.getItemDamage();

        if(metadata > EnumBlockGlowDandelions.values().length)
        	return "tile." + EnumBlockGlowDandelions.byMetadata(0).getName();
        */
        return BRBlocks.BARNARDA_C_GLOW_DANDELIONS.getTranslationKey()
    }
}
