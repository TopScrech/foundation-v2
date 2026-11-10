package galaxyspace.systems.BarnardsSystem.planets.barnarda_c.items

import galaxyspace.core.prefab.items.GSItemBlockDesc
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Falling_Blocks.EnumFallingBlockBarnardaC
import net.minecraft.block.Block
import net.minecraft.item.ItemStack

open class ItemBlocksBarnarda_C_Falling constructor(block: Block?) : GSItemBlockDesc((block)!!) {
    init {
        setMaxDamage(0)
        setHasSubtypes(true)
    }

    override fun getMetadata(meta: Int): Int {
        return meta
    }

    override fun getTranslationKey(`is`: ItemStack): String {
        val metadata: Int = `is`.getItemDamage()

        if (metadata > EnumFallingBlockBarnardaC.entries.size) return "tile." + (EnumFallingBlockBarnardaC.byMetadata(0))!!
            .getName()

        return "tile." + (EnumFallingBlockBarnardaC.byMetadata(metadata))!!.getName()
    }
}
