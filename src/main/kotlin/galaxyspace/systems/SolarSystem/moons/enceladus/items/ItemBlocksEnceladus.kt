package galaxyspace.systems.SolarSystem.moons.enceladus.items

import foundation.item.legacyItemValue

import galaxyspace.systems.SolarSystem.moons.enceladus.blocks.EnceladusBlocks

import galaxyspace.core.prefab.items.GSItemBlockDesc
import galaxyspace.systems.SolarSystem.moons.enceladus.blocks.EnceladusBlocks.EnumEnceladusBlocks
import net.minecraft.world.level.block.Block
import net.minecraft.world.item.ItemStack

open class ItemBlocksEnceladus constructor(block: Block?) : GSItemBlockDesc((block)!!) {
    init {
        setMaxDamage(0)
        setHasSubtypes(true)
    }

    override fun getMetadata(meta: Int): Int {
        return meta
    }

    override fun getTranslationKey(`is`: ItemStack): String {
        val metadata: Int = `is`.legacyItemValue
        return "tile." + (EnumEnceladusBlocks.Companion.byMetadata(metadata))!!.getName()
    }
}
