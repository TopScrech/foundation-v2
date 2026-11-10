package galaxyspace.systems.SolarSystem.planets.ceres.items

import galaxyspace.systems.SolarSystem.planets.ceres.blocks.CeresBlocks

import galaxyspace.core.prefab.items.GSItemBlockDesc
import galaxyspace.systems.SolarSystem.planets.ceres.blocks.CeresBlocks.EnumCeresBlocks
import net.minecraft.block.Block
import net.minecraft.item.ItemStack

open class ItemBlocksCeres constructor(block: Block?) : GSItemBlockDesc((block)!!) {
    init {
        setMaxDamage(0)
        setHasSubtypes(true)
    }

    override fun getMetadata(meta: Int): Int {
        return meta
    }

    override fun getTranslationKey(`is`: ItemStack): String {
        val metadata: Int = `is`.getItemDamage()

        return "tile." + (EnumCeresBlocks.Companion.byMetadata(metadata))!!.getName()
    }
}
