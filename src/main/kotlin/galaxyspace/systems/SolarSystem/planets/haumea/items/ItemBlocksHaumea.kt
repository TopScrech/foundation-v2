package galaxyspace.systems.SolarSystem.planets.haumea.items

import galaxyspace.systems.SolarSystem.planets.haumea.blocks.HaumeaBlocks.EnumHaumeaBlocks
import galaxyspace.core.prefab.items.GSItemBlockDesc
import net.minecraft.block.Block
import net.minecraft.item.ItemStack

open class ItemBlocksHaumea constructor(block: Block?) : GSItemBlockDesc((block)!!) {
    init {
        setMaxDamage(0)
        setHasSubtypes(true)
    }

    override fun getMetadata(meta: Int): Int {
        return meta
    }

    override fun getTranslationKey(`is`: ItemStack): String {
        val metadata: Int = `is`.getItemDamage()

        return "tile." + (EnumHaumeaBlocks.Companion.byMetadata(metadata))!!.getName()
    }
}
