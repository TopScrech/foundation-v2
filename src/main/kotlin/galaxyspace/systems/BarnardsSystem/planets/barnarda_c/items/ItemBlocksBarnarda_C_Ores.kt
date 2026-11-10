package galaxyspace.systems.BarnardsSystem.planets.barnarda_c.items

import galaxyspace.core.prefab.items.GSItemBlockDesc
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Ores.EnumBlockBarnardaCOres
import net.minecraft.block.Block
import net.minecraft.item.ItemStack

open class ItemBlocksBarnarda_C_Ores constructor(block: Block?) : GSItemBlockDesc((block)!!) {
    init {
        setMaxDamage(0)
        setHasSubtypes(true)
    }

    override fun getMetadata(meta: Int): Int {
        return meta
    }

    override fun getTranslationKey(`is`: ItemStack): String {
        val metadata: Int = `is`.getItemDamage()

        if (metadata > EnumBlockBarnardaCOres.entries.size) return "tile." + (EnumBlockBarnardaCOres.byMetadata(0))!!
            .getName()

        return "tile." + (EnumBlockBarnardaCOres.byMetadata(metadata))!!.getName()
    }
}
