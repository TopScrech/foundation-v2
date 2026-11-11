package galaxyspace.systems.SolarSystem.planets.overworld.items

import foundation.item.legacyItemValue

import galaxyspace.core.util.GSCreativeTabs
import micdoodle8.mods.galacticraft.core.items.ISortableItem
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.core.NonNullList
import net.minecraft.world.level.Level as World
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class ItemUpgrades constructor() : Item(), ISortableItem {
    init {
        this.setMaxDamage(0)
        this.setHasSubtypes(true)
        this.setMaxStackSize(8)
        this.setTranslationKey("upgrades")
        this.setCreativeTab(GSCreativeTabs.GSItemsTab)
    }

    override fun getSubItems(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        if (tab === GSCreativeTabs.GSItemsTab || tab === CreativeTabs.SEARCH) {
            for (i: Int in names.indices) list.add(ItemStack(this, 1, i))
        }
    }


    @OnlyIn(Dist.CLIENT)
    override fun addInformation(stack: ItemStack, worldIn: World?, list: MutableList<String?>, flagIn: ITooltipFlag) {
        val n: Int = stack.legacyItemValue
    }

    override fun getTranslationKey(par1ItemStack: ItemStack): String {
        return "item." + names[par1ItemStack.legacyItemValue]
    }

    override fun getMetadata(meta: Int): Int {
        return meta
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem {
        return EnumSortCategoryItem.GENERAL
    }

    companion object {
        @JvmField
        var names: Array<String> = arrayOf<String>(
            "module_expander",  // 0
            "module_stabiliser",  // 1
            "module_speed",  // 2
            "module_energy" // 3
        )
    }
}
