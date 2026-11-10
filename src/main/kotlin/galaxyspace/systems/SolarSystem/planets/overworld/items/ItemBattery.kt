package galaxyspace.systems.SolarSystem.planets.overworld.items

import galaxyspace.core.util.GSCreativeTabs
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase
import micdoodle8.mods.galacticraft.core.items.ISortableItem
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList

open class ItemBattery constructor(name: String, energy: Float) : ItemElectricBase(), ISortableItem {
    private var energyMax = 0f

    init {
        this.setTranslationKey(name)
        this.setMaxStackSize(1)
        this.setCreativeTab(GSCreativeTabs.GSItemsTab)
        this.energyMax = energy
    }

    override fun getSubItems(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        if (tab === GSCreativeTabs.GSItemsTab || tab === CreativeTabs.SEARCH) {
            list.add(ItemStack(this, 1, 0))
            list.add(ItemStack(this, 1, this.getMaxDamage()))
        }
    }

    override fun getMaxElectricityStored(theItem: ItemStack?): Float {
        return this.energyMax
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem {
        return EnumSortCategoryItem.GENERAL
    }

    override fun getItemStackLimit(stack: ItemStack): Int {
        if (stack.getItemDamage() < 100 || stack.hasTagCompound() && stack.getTagCompound()!!.hasKey("electricity")) {
            return 1
        }
        return this.getItemStackLimit()
    }
}
