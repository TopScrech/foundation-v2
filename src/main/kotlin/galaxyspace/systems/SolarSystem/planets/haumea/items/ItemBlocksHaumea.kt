package galaxyspace.systems.SolarSystem.planets.haumea.items

import foundation.item.legacyItemValue

import galaxyspace.systems.SolarSystem.planets.haumea.blocks.HaumeaBlocks.EnumHaumeaBlocks
import galaxyspace.core.prefab.items.GSItemBlockDesc
import net.minecraft.world.level.block.Block
import net.minecraft.world.item.ItemStack

open class ItemBlocksHaumea constructor(block: Block?) : GSItemBlockDesc((block)!!) {
    init {
        setMaxDamage(0)
        setHasSubtypes(true)
    }

    override fun getMetadata(meta: Int): Int {
        return meta
    }

    override fun getTranslationKey(`is`: ItemStack): String {
        val metadata: Int = `is`.legacyItemValue

        return "tile." + (EnumHaumeaBlocks.Companion.byMetadata(metadata))!!.getName()
    }
}
