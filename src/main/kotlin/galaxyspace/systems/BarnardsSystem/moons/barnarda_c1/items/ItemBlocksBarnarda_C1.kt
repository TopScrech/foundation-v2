package galaxyspace.systems.BarnardsSystem.moons.barnarda_c1.items

import foundation.item.legacyItemValue

import galaxyspace.systems.BarnardsSystem.moons.barnarda_c1.blocks.Barnarda_C1_Blocks

import galaxyspace.systems.BarnardsSystem.moons.barnarda_c1.blocks.Barnarda_C1_Blocks.EnumBlockBarnardaC1
import galaxyspace.core.prefab.items.GSItemBlockDesc
import galaxyspace.systems.BarnardsSystem.moons.barnarda_c1.items.ItemBlocksBarnarda_C1
import net.minecraft.world.level.block.Block
import net.minecraft.world.item.ItemStack

open class ItemBlocksBarnarda_C1 constructor(block: Block?) : GSItemBlockDesc((block)!!) {
    init {
        setMaxDamage(0)
        setHasSubtypes(true)
    }

    override fun getMetadata(meta: Int): Int {
        return meta
    }

    override fun getTranslationKey(`is`: ItemStack): String {
        val metadata: Int = `is`.legacyItemValue

        if (metadata > EnumBlockBarnardaC1.entries.size) return "tile." + (EnumBlockBarnardaC1.Companion.byMetadata(0))!!
            .getName()

        return "tile." + (EnumBlockBarnardaC1.Companion.byMetadata(metadata))!!.getName()
    }
}
