package galaxyspace.systems.SolarSystem.planets.overworld.items

import galaxyspace.core.prefab.items.GSItemBlockDesc
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.BlockMetals
import net.minecraft.block.Block
import net.minecraft.item.ItemStack

open class ItemBlocksMetals constructor(block: Block?) : GSItemBlockDesc((block)!!) {
    init {
        setMaxDamage(0)
        setHasSubtypes(true)
    }

    override fun getMetadata(meta: Int): Int {
        return meta
    }

    override fun getTranslationKey(`is`: ItemStack): String {
        val metadata: Int = `is`.getItemDamage()
        return "tile." + (BlockMetals.EnumBlockMetals.byMetadata(metadata))!!.getName().replace("_1", "").replace("_2", "")
    }
}
