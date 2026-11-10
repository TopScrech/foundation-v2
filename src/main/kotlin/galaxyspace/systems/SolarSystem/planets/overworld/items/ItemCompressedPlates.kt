package galaxyspace.systems.SolarSystem.planets.overworld.items

import galaxyspace.core.util.GSCreativeTabs
import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemCompressedPlates
import micdoodle8.mods.galacticraft.core.items.ISortableItem
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList

open class ItemCompressedPlates constructor() : Item(), ISortableItem {
    init {
        this.setMaxDamage(0)
        this.setHasSubtypes(true)
        this.setMaxStackSize(64)
        this.setTranslationKey("compressed_plates")
        this.setCreativeTab(GSCreativeTabs.GSItemsTab)
    }

    override fun getSubItems(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        if (tab === GSCreativeTabs.GSItemsTab || tab === CreativeTabs.SEARCH) {
            for (i: Int in names.indices) {
                list.add(ItemStack(this, 1, i))
            }
        }
    }

    override fun getTranslationKey(par1ItemStack: ItemStack): String {
        return "item." + names[par1ItemStack.getItemDamage()]
    }

    override fun getMetadata(par1: Int): Int {
        return par1
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem {
        return EnumSortCategoryItem.PLATE
    }

    companion object {
        @JvmField
        var names: Array<String> = arrayOf<String>(
            "compressed_coal",
            "compressed_cobaltum",
            "compressed_magnesium",
            "compressed_nickel",
            "compressed_sdhd120",  //"compressed_lead"
        )
    }
}
