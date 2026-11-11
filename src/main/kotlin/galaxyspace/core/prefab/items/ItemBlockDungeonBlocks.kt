package galaxyspace.core.prefab.items

import foundation.item.legacyItemValue

import galaxyspace.core.prefab.blocks.DungeonBlocks

import galaxyspace.core.prefab.blocks.DungeonBlocks.EnumDungeonBlocks
import galaxyspace.core.prefab.items.GSItemBlockDesc
import net.minecraft.world.level.block.Block
import net.minecraft.world.item.ItemStack

open class ItemBlockDungeonBlocks constructor(block: Block?) : GSItemBlockDesc((block)!!) {
    init {
        setMaxDamage(0)
        setHasSubtypes(true)
    }

    override fun getMetadata(meta: Int): Int {
        return meta
    }

    override fun getTranslationKey(`is`: ItemStack): String {
        val metadata: Int = `is`.legacyItemValue
        if (metadata > EnumDungeonBlocks.entries.size) return "tile." + (EnumDungeonBlocks.Companion.byMetadata(0))!!
            .getName()
        return "tile." + (EnumDungeonBlocks.Companion.byMetadata(`is`.legacyItemValue))!!.getName()
    }
}
