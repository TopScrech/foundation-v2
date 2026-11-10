package galaxyspace.systems.SolarSystem.moons.phobos.items

import galaxyspace.systems.SolarSystem.moons.phobos.blocks.PhobosBlocks

import galaxyspace.systems.SolarSystem.moons.phobos.blocks.PhobosBlocks.EnumPhobosBlocks
import galaxyspace.core.prefab.items.GSItemBlockDesc
import net.minecraft.block.Block
import net.minecraft.item.ItemStack

open class ItemBlocksPhobos constructor(block: Block?) : GSItemBlockDesc((block)!!) {
    init {
        setMaxDamage(0)
        setHasSubtypes(true)
    }

    override fun getMetadata(meta: Int): Int {
        return meta
    }

    override fun getTranslationKey(`is`: ItemStack): String {
        val metadata: Int = `is`.getItemDamage()

        return "tile." + (EnumPhobosBlocks.Companion.byMetadata(metadata))!!.getName()
    }
}
