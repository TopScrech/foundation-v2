package galaxyspace.systems.BarnardsSystem.planets.barnarda_c.items

import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Dandelions.EnumBlockDandelions

import galaxyspace.core.prefab.items.GSItemBlockDesc
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Dandelions
import net.minecraft.block.Block
import net.minecraft.item.ItemStack

open class ItemBlocksBarnarda_C_Dandelions constructor(block: Block?) : GSItemBlockDesc((block)!!) {
    init {
        setMaxDamage(0)
        setHasSubtypes(true)
    }

    override fun getMetadata(meta: Int): Int {
        return meta
    }

    override fun getTranslationKey(`is`: ItemStack): String {
        val metadata: Int = `is`.getItemDamage()

        if (metadata > Barnarda_C_Dandelions.EnumBlockDandelions.entries.size) return "tile." + (Barnarda_C_Dandelions.EnumBlockDandelions.byMetadata(
            0
        ))!!.getName()

        return "tile." + (Barnarda_C_Dandelions.EnumBlockDandelions.byMetadata(metadata))!!.getName()
    }
}
