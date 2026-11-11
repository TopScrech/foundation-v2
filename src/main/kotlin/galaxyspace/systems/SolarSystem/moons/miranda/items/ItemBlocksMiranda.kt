package galaxyspace.systems.SolarSystem.moons.miranda.items

import foundation.item.legacyItemValue

import galaxyspace.systems.SolarSystem.moons.miranda.blocks.MirandaBlocks

import galaxyspace.core.prefab.items.GSItemBlockDesc
import galaxyspace.systems.SolarSystem.moons.miranda.blocks.MirandaBlocks.EnumMirandaBlocks
import net.minecraft.world.level.block.Block
import net.minecraft.world.item.ItemStack

open class ItemBlocksMiranda constructor(block: Block?) : GSItemBlockDesc((block)!!) {
    init {
        setMaxDamage(0)
        setHasSubtypes(true)
    }

    override fun getMetadata(meta: Int): Int {
        return meta
    }

    override fun getTranslationKey(`is`: ItemStack): String {
        val metadata: Int = `is`.legacyItemValue

        if (metadata == 9 || metadata == 10) return "tile.miranda_grunt"
        if (metadata == 11 || metadata == 12) return "tile.miranda_subgrunt"
        if (metadata == 13 || metadata == 14) return "tile.miranda_stone"

        if (metadata > EnumMirandaBlocks.entries.size) return "tile." + (EnumMirandaBlocks.Companion.byMetadata(0))!!
            .getName()
        else return "tile." + (EnumMirandaBlocks.Companion.byMetadata(metadata))!!.getName()
    }
}
