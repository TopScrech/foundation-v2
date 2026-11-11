package micdoodle8.mods.galacticraft.core.util

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.item.ItemStack
import net.minecraft.core.NonNullList
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.lang.Exception
import java.util.Collections
import java.util.Comparator

open class CreativeTabGC constructor(
    par1: Int,
    par2Str: String,
    private var itemForTab: ItemStack?,
    private var tabSorter: Comparator<ItemStack>?
) : CreativeTabs(par1, par2Str) {
    @OnlyIn(Dist.CLIENT)
    public override fun createIcon(): ItemStack? {
        return this.itemForTab
    }

    open fun setItemForTab(itemForTab: ItemStack?) {
        this.itemForTab = itemForTab
    }

    @OnlyIn(Dist.CLIENT)
    public override fun getTranslationKey(): String {
        return "item_group." + this.getTabLabel()
    }

    override fun displayAllRelevantItems(list: NonNullList<ItemStack>) {
        super.displayAllRelevantItems(list)
        if (this.tabSorter != null) {
            try {
                Collections.sort<ItemStack?>(list, tabSorter)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    open fun setTabSorter(tabSorter: Comparator<ItemStack>?) {
        this.tabSorter = tabSorter
    }
}
