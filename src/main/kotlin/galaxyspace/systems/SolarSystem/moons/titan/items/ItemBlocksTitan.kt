package galaxyspace.systems.SolarSystem.moons.titan.items

import galaxyspace.systems.SolarSystem.moons.titan.blocks.TitanBlocks

import galaxyspace.core.prefab.items.GSItemBlockDesc
import galaxyspace.systems.SolarSystem.moons.titan.blocks.TitanBlocks.EnumTitanBlocks
import net.minecraft.block.Block
import net.minecraft.item.ItemStack

open class ItemBlocksTitan constructor(block: Block?) : GSItemBlockDesc((block)!!) {
    init {
        setMaxDamage(0)
        setHasSubtypes(true)
    }

    override fun getMetadata(meta: Int): Int {
        return meta
    }

    override fun getTranslationKey(`is`: ItemStack): String {
        val metadata: Int = `is`.getItemDamage()

        return "tile." + (EnumTitanBlocks.Companion.byMetadata(metadata))!!.getName()
    }
}
