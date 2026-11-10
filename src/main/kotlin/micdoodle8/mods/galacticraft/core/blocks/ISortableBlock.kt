package micdoodle8.mods.galacticraft.core.blocks

import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock

interface ISortableBlock {
    fun getCategory(meta: Int): EnumSortCategoryBlock?
}
