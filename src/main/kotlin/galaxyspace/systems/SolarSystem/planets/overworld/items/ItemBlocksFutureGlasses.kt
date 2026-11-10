package galaxyspace.systems.SolarSystem.planets.overworld.items

import galaxyspace.systems.SolarSystem.planets.overworld.blocks.BlockFutureGlasses

import galaxyspace.core.prefab.items.GSItemBlockDesc
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.BlockFutureGlasses.EnumBlockFutureGlass
import net.minecraft.block.Block
import net.minecraft.item.ItemStack

open class ItemBlocksFutureGlasses constructor(block: Block?) : GSItemBlockDesc((block)!!) {
    init {
        setMaxDamage(0)
        setHasSubtypes(true)
    }

    override fun getMetadata(meta: Int): Int {
        return meta
    }

    override fun getTranslationKey(`is`: ItemStack): String {
        val metadata: Int = `is`.getItemDamage()

        if (metadata > EnumBlockFutureGlass.entries.toTypedArray().size) return "tile." + (EnumBlockFutureGlass.byMetadata(
            0
        ))!!.getName()
        else return "tile." + (EnumBlockFutureGlass.byMetadata(metadata))!!.getName()
    }
}
