package galaxyspace.systems.SolarSystem.moons.callisto.items

import galaxyspace.systems.SolarSystem.moons.callisto.blocks.CallistoBlocks

import galaxyspace.core.prefab.items.GSItemBlockDesc
import galaxyspace.systems.SolarSystem.moons.callisto.blocks.CallistoBlocks.EnumCallistoBlocks
import net.minecraft.block.Block
import net.minecraft.item.ItemStack

open class ItemBlocksCallisto constructor(block: Block?) : GSItemBlockDesc((block)!!) {
    init {
        setMaxDamage(0)
        setHasSubtypes(true)
    }

    override fun getMetadata(meta: Int): Int {
        return meta
    }

    override fun getTranslationKey(`is`: ItemStack): String {
        val metadata: Int = `is`.getItemDamage()

        return "tile." + (EnumCallistoBlocks.Companion.byMetadata(metadata))!!.getName()
    }
}
