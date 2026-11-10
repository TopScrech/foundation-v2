package galaxyspace.systems.SolarSystem.moons.europa.items

import galaxyspace.systems.SolarSystem.moons.europa.blocks.EuropaBlocks

import galaxyspace.systems.SolarSystem.moons.europa.blocks.EuropaBlocks.EnumEuropaBlocks
import galaxyspace.core.prefab.items.GSItemBlockDesc
import net.minecraft.block.Block
import net.minecraft.item.ItemStack

open class ItemBlocksEuropa constructor(block: Block?) : GSItemBlockDesc((block)!!) {
    init {
        setMaxDamage(0)
        setHasSubtypes(true)
    }

    override fun getMetadata(meta: Int): Int {
        return meta
    }

    override fun getTranslationKey(`is`: ItemStack): String {
        val metadata: Int = `is`.getItemDamage()

        return "tile." + (EnumEuropaBlocks.Companion.byMetadata(metadata))!!.getName()
    }
}
