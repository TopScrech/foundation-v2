package galaxyspace.systems.SolarSystem.moons.triton.items

import galaxyspace.core.prefab.items.GSItemBlockDesc
import galaxyspace.systems.SolarSystem.moons.triton.blocks.TritonBlocks.EnumTritonBlocks
import net.minecraft.block.Block
import net.minecraft.item.ItemStack

open class ItemBlocksTriton constructor(block: Block?) : GSItemBlockDesc((block)!!) {
    init {
        setMaxDamage(0)
        setHasSubtypes(true)
    }

    override fun getMetadata(meta: Int): Int {
        return meta
    }

    override fun getTranslationKey(`is`: ItemStack): String {
        val metadata: Int = `is`.getItemDamage()

        return "tile." + (EnumTritonBlocks.Companion.byMetadata(metadata))!!.getName()
    }
}
