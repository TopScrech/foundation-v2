package galaxyspace.systems.SolarSystem.planets.overworld.items

import foundation.item.legacyItemValue

import galaxyspace.core.prefab.items.GSItemBlockDesc
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.BlockSurfaceIce
import net.minecraft.world.level.block.Block
import net.minecraft.world.item.ItemStack

open class ItemBlocksIce constructor(block: Block?) : GSItemBlockDesc((block)!!) {
    init {
        setMaxDamage(0)
        setHasSubtypes(true)
    }

    override fun getMetadata(meta: Int): Int {
        return meta
    }

    override fun getTranslationKey(`is`: ItemStack): String {
        val metadata: Int = `is`.legacyItemValue

        return "tile." + (BlockSurfaceIce.EnumBlockIce.byMetadata(metadata))!!.getName()
    }
}
