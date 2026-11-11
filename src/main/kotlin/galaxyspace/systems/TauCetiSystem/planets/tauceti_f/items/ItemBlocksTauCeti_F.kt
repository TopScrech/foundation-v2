package galaxyspace.systems.TauCetiSystem.planets.tauceti_f.items

import foundation.item.legacyItemValue

import galaxyspace.systems.TauCetiSystem.planets.tauceti_f.blocks.TauCeti_F_Blocks.EnumBlockTauCetiF
import galaxyspace.core.prefab.items.GSItemBlockDesc
import net.minecraft.world.level.block.Block
import net.minecraft.world.item.ItemStack

open class ItemBlocksTauCeti_F constructor(block: Block?) : GSItemBlockDesc((block)!!) {
    init {
        setMaxDamage(0)
        setHasSubtypes(true)
    }

    override fun getMetadata(meta: Int): Int {
        return meta
    }

    override fun getTranslationKey(`is`: ItemStack): String {
        val metadata: Int = `is`.legacyItemValue

        if (metadata > EnumBlockTauCetiF.entries.size) return "tile." + (EnumBlockTauCetiF.Companion.byMetadata(0))!!
            .getName()

        return "tile." + (EnumBlockTauCetiF.Companion.byMetadata(metadata))!!.getName()
    }
}
