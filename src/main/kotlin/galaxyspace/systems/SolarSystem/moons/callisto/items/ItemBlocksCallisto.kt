package galaxyspace.systems.SolarSystem.moons.callisto.items

import foundation.item.legacyItemValue

import galaxyspace.systems.SolarSystem.moons.callisto.blocks.CallistoBlocks

import galaxyspace.core.prefab.items.GSItemBlockDesc
import galaxyspace.systems.SolarSystem.moons.callisto.blocks.CallistoBlocks.EnumCallistoBlocks
import net.minecraft.world.level.block.Block
import net.minecraft.world.item.ItemStack

open class ItemBlocksCallisto constructor(block: Block?) : GSItemBlockDesc((block)!!) {
    init {
        setMaxDamage(0)
        setHasSubtypes(true)
    }

    override fun getMetadata(meta: Int): Int {
        return meta
    }

    override fun getTranslationKey(`is`: ItemStack): String {
        val metadata: Int = `is`.legacyItemValue

        return "tile." + (EnumCallistoBlocks.Companion.byMetadata(metadata))!!.getName()
    }
}
