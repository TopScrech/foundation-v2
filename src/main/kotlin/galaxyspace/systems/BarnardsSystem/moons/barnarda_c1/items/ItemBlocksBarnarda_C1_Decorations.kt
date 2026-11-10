package galaxyspace.systems.BarnardsSystem.moons.barnarda_c1.items

import galaxyspace.systems.BarnardsSystem.moons.barnarda_c1.blocks.Barnarda_C1_Decorations

import galaxyspace.systems.BarnardsSystem.moons.barnarda_c1.blocks.Barnarda_C1_Decorations.EnumBlockBarnardaC1Decoration
import galaxyspace.core.prefab.items.GSItemBlockDesc
import galaxyspace.systems.BarnardsSystem.moons.barnarda_c1.items.ItemBlocksBarnarda_C1_Decorations
import net.minecraft.block.Block
import net.minecraft.item.ItemStack

open class ItemBlocksBarnarda_C1_Decorations constructor(block: Block?) : GSItemBlockDesc((block)!!) {
    init {
        setMaxDamage(0)
        setHasSubtypes(true)
    }

    override fun getMetadata(meta: Int): Int {
        return meta
    }

    override fun getTranslationKey(`is`: ItemStack): String {
        val metadata: Int = `is`.getItemDamage()

        if (metadata > EnumBlockBarnardaC1Decoration.entries.size) return "tile." + (EnumBlockBarnardaC1Decoration.Companion.byMetadata(
            0
        ))!!.getName()

        return "tile." + EnumBlockBarnardaC1Decoration.entries[metadata % 5].getName()
    }
}
