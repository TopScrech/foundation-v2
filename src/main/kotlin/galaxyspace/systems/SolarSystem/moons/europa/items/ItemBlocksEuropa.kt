package galaxyspace.systems.SolarSystem.moons.europa.items

import foundation.item.legacyItemValue

import galaxyspace.systems.SolarSystem.moons.europa.blocks.EuropaBlocks

import galaxyspace.systems.SolarSystem.moons.europa.blocks.EuropaBlocks.EnumEuropaBlocks
import galaxyspace.core.prefab.items.GSItemBlockDesc
import net.minecraft.world.level.block.Block
import net.minecraft.world.item.ItemStack

open class ItemBlocksEuropa constructor(block: Block?) : GSItemBlockDesc((block)!!) {
    init {
        setMaxDamage(0)
        setHasSubtypes(true)
    }

    override fun getMetadata(meta: Int): Int {
        return meta
    }

    override fun getTranslationKey(`is`: ItemStack): String {
        val metadata: Int = `is`.legacyItemValue

        return "tile." + (EnumEuropaBlocks.Companion.byMetadata(metadata))!!.getName()
    }
}
