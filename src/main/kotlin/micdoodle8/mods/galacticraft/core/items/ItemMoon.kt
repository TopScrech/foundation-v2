package micdoodle8.mods.galacticraft.core.items

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.EnumRarity
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class ItemMoon constructor(str: String?) : ItemDesc(), ISortableItem {
    //    protected IIcon[] icons = new IIcon[ItemMoon.names.length];
    init {
        this.setMaxDamage(0)
        this.setHasSubtypes(true)
        this.setTranslationKey(str)
    }

    /*@Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {
        int i = 0;

        for (String name : ItemMoon.names)
        {
            this.icons[i++] = iconRegister.registerIcon("galacticraftmoon:" + name);
        }
    }

    @Override
    public IIcon getIconFromDamage(int damage)
    {
        if (this.icons.length > damage)
        {
            return this.icons[damage];
        }

        return super.getIconFromDamage(damage);
    }*/
    override fun getSubItems(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        if (tab === GalacticraftCore.galacticraftItemsTab || tab === CreativeTabs.SEARCH) {
            for (i: Int in names.indices) {
                list.add(ItemStack(this, 1, i))
            }
        }
    }

    public override fun getTranslationKey(par1ItemStack: ItemStack): String {
        if (names.size > par1ItemStack.getItemDamage()) {
            return "item." + names[par1ItemStack.getItemDamage()]
        }

        return "unnamed"
    }

    override fun getMetadata(par1: Int): Int {
        return par1
    }

    override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftItemsTab
    }

    @SideOnly(Side.CLIENT)
    override fun getRarity(par1ItemStack: ItemStack): EnumRarity {
        return (ClientProxyCore.galacticraftItem)!!
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem {
        when (meta) {
            0 -> return EnumSortCategoryItem.INGOT
            2 -> return EnumSortCategoryItem.GENERAL
            else -> return EnumSortCategoryItem.PLATE
        }
    }

    override fun getShiftDescription(meta: Int): String? {
        if (meta == 2) {
            return GCCoreUtil.translate("item.lunar_sapphire.description")
        }

        return ""
    }

    override fun showDescription(meta: Int): Boolean {
        return meta == 2
    }

    override fun getSmeltingExperience(item: ItemStack): Float {
        when (item.getItemDamage()) {
            1 -> return 1f
        }
        return -1f
    }

    companion object {
        @JvmField
        var names: Array<String> = arrayOf<String>("meteoric_iron_ingot", "compressed_meteoric_iron", "lunar_sapphire")
    }
}
