package galaxyspace.systems.TauCetiSystem.planets.tauceti_f.items

import galaxyspace.systems.TauCetiSystem.planets.tauceti_f.blocks.TauCeti_F_Watergrass.EnumBlockDandelions

import galaxyspace.systems.TauCetiSystem.planets.tauceti_f.blocks.TauCeti_F_Watergrass
import galaxyspace.core.prefab.items.GSItemBlockDesc
import net.minecraft.block.Block
import net.minecraft.item.ItemStack

open class ItemBlocksTauCeti_F_Dandelions constructor(block: Block?) : GSItemBlockDesc((block)!!) {
    init {
        setMaxDamage(0)
        setHasSubtypes(true)
    }

    override fun getMetadata(meta: Int): Int {
        return meta
    }

    override fun getTranslationKey(`is`: ItemStack): String {
        val metadata: Int = `is`.getItemDamage()

        if (metadata > TauCeti_F_Watergrass.EnumBlockDandelions.entries.size) return "tile." + (TauCeti_F_Watergrass.EnumBlockDandelions.Companion.byMetadata(
            0
        ))!!.getName()

        return "tile." + (TauCeti_F_Watergrass.EnumBlockDandelions.Companion.byMetadata(metadata))!!.getName()
    }
}
