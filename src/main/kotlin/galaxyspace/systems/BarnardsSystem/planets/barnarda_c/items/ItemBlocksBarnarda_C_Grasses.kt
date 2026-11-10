package galaxyspace.systems.BarnardsSystem.planets.barnarda_c.items

import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Grass.EnumBlockGrass

import galaxyspace.core.prefab.items.GSItemBlockDesc
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Grass
import net.minecraft.block.Block
import net.minecraft.item.ItemStack

open class ItemBlocksBarnarda_C_Grasses constructor(block: Block?) : GSItemBlockDesc((block)!!) {
    init {
        setMaxDamage(0)
        setHasSubtypes(true)
    }

    override fun getMetadata(meta: Int): Int {
        return meta
    }

    override fun getTranslationKey(`is`: ItemStack): String {
        val metadata: Int = `is`.getItemDamage()

        if (metadata > Barnarda_C_Grass.EnumBlockGrass.entries.size) return "tile." + (Barnarda_C_Grass.EnumBlockGrass.byMetadata(
            0
        ))!!.getName()

        return "tile." + (Barnarda_C_Grass.EnumBlockGrass.byMetadata(metadata))!!.getName()
    }
}
