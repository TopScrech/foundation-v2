package galaxyspace.systems.SolarSystem.planets.overworld.items

import galaxyspace.core.util.GSCreativeTabs
import micdoodle8.mods.galacticraft.core.items.ISortableItem
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

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


    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack, worldIn: World?, list: MutableList<String?>, flagIn: ITooltipFlag) {
        val n: Int = stack.getItemDamage()
    }

    override fun getTranslationKey(par1ItemStack: ItemStack): String {
        return "item." + names[par1ItemStack.getItemDamage()]
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
