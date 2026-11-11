package galaxyspace.systems.SolarSystem.planets.pluto.items

import foundation.item.legacyItemValue

import galaxyspace.systems.SolarSystem.planets.pluto.blocks.PlutoBlocks

import galaxyspace.systems.SolarSystem.planets.pluto.blocks.PlutoBlocks.EnumPlutoBlocks
import galaxyspace.core.prefab.items.GSItemBlockDesc
import net.minecraft.world.level.block.Block
import net.minecraft.world.item.ItemStack

open class ItemBlocksPluto constructor(block: Block?) : GSItemBlockDesc((block)!!) {
    init {
        setMaxDamage(0)
        setHasSubtypes(true)
    }

    override fun getMetadata(meta: Int): Int {
        return meta
    }

    override fun getTranslationKey(`is`: ItemStack): String {
        val metadata: Int = `is`.legacyItemValue
        if (metadata <= 4) return "tile.pluto_grunt"

        return "tile." + (EnumPlutoBlocks.Companion.byMetadata(metadata))!!.getName()
    }
}
