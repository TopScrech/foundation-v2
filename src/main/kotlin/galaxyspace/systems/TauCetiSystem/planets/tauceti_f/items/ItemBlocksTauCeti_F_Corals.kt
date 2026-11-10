package galaxyspace.systems.TauCetiSystem.planets.tauceti_f.items

import galaxyspace.systems.TauCetiSystem.planets.tauceti_f.blocks.TauCeti_F_Corals.EnumBlockCorals
import galaxyspace.core.prefab.items.GSItemBlockDesc
import net.minecraft.block.Block
import net.minecraft.item.ItemStack

open class ItemBlocksTauCeti_F_Corals constructor(block: Block?) : GSItemBlockDesc((block)!!) {
    init {
        setMaxDamage(0)
        setHasSubtypes(true)
    }

    override fun getMetadata(meta: Int): Int {
        return meta
    }

    override fun getTranslationKey(`is`: ItemStack): String {
        val metadata: Int = `is`.getItemDamage()

        if (metadata > EnumBlockCorals.entries.size) return "tile." + (EnumBlockCorals.Companion.byMetadata(0))!!.getName()

        return "tile." + (EnumBlockCorals.Companion.byMetadata(metadata))!!.getName()
    }
}
