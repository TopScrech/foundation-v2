package galaxyspace.systems.ACentauriSystem.planets.proxima_b.items

import galaxyspace.systems.ACentauriSystem.planets.proxima_b.blocks.Proxima_B_Dandelions
import galaxyspace.core.prefab.items.GSItemBlockDesc
import galaxyspace.systems.ACentauriSystem.planets.proxima_b.items.ItemBlocksProxima_B_Dandelions
import net.minecraft.block.Block
import net.minecraft.item.ItemStack

open class ItemBlocksProxima_B_Dandelions constructor(block: Block?) : GSItemBlockDesc((block)!!) {
    init {
        setMaxDamage(0)
        setHasSubtypes(true)
    }

    override fun getMetadata(meta: Int): Int {
        return meta
    }

    override fun getTranslationKey(`is`: ItemStack): String {
        val metadata: Int = `is`.getItemDamage()

        if (metadata > Proxima_B_Dandelions.EnumBlockDandelions.entries.size) return "tile." + (Proxima_B_Dandelions.EnumBlockDandelions.Companion.byMetadata(
            0
        ))!!.getName()

        return "tile." + (Proxima_B_Dandelions.EnumBlockDandelions.Companion.byMetadata(metadata))!!.getName()
    }
}
