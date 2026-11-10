package micdoodle8.mods.galacticraft.core.items

import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem

interface ISortableItem {
    fun getCategory(meta: Int): EnumSortCategoryItem?
}
