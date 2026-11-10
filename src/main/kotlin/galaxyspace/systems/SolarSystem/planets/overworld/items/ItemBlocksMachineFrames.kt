package galaxyspace.systems.SolarSystem.planets.overworld.items

import galaxyspace.core.prefab.items.GSItemBlockDesc
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.BlockMachineFrames
import net.minecraft.block.Block
import net.minecraft.item.ItemStack

open class ItemBlocksMachineFrames constructor(block: Block?) : GSItemBlockDesc((block)!!) {
    init {
        setMaxDamage(0)
        setHasSubtypes(true)
    }

    override fun getMetadata(meta: Int): Int {
        return meta
    }

    override fun getTranslationKey(`is`: ItemStack): String {
        val metadata: Int = `is`.getItemDamage()
        return "tile." + (BlockMachineFrames.EnumBlockMachineFrames.byMetadata(metadata))!!.getName()
    }
}
