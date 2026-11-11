package galaxyspace.systems.SolarSystem.moons.phobos.items

import foundation.item.legacyItemValue

import galaxyspace.systems.SolarSystem.moons.phobos.blocks.PhobosBlocks

import galaxyspace.systems.SolarSystem.moons.phobos.blocks.PhobosBlocks.EnumPhobosBlocks
import galaxyspace.core.prefab.items.GSItemBlockDesc
import net.minecraft.world.level.block.Block
import net.minecraft.world.item.ItemStack

open class ItemBlocksPhobos constructor(block: Block?) : GSItemBlockDesc((block)!!) {
    init {
        setMaxDamage(0)
        setHasSubtypes(true)
    }

    override fun getMetadata(meta: Int): Int {
        return meta
    }

    override fun getTranslationKey(`is`: ItemStack): String {
        val metadata: Int = `is`.legacyItemValue

        return "tile." + (EnumPhobosBlocks.Companion.byMetadata(metadata))!!.getName()
    }
}
