package micdoodle8.mods.galacticraft.core.items

import foundation.item.legacyItemValue

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.EnumRarity
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.core.NonNullList
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class ItemRocketEngineGC constructor(assetName: String?) : Item(), ISortableItem {
    //    protected IIcon[] icons = new IIcon[ItemRocketEngineGC.names.length];
    init {
        this.setMaxDamage(0)
        this.setHasSubtypes(true)
        this.setTranslationKey(assetName)
        //this.setTextureName(Constants.TEXTURE_PREFIX + assetName);
    }

    override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftItemsTab
    }

    @OnlyIn(Dist.CLIENT)
    override fun getRarity(par1ItemStack: ItemStack): EnumRarity {
        return (ClientProxyCore.galacticraftItem)!!
    }

    /*@Override
    @OnlyIn(Dist.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {
        int i = 0;

        for (final String name : ItemRocketEngineGC.names)
        {
            this.icons[i++] = iconRegister.registerIcon(this.getIconString() + "." + name);
        }
    }*/
    public override fun getTranslationKey(itemStack: ItemStack): String {
        return this.getTranslationKey() + "." + names[itemStack.legacyItemValue]
    }

    /*@Override
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

    override fun getMetadata(par1: Int): Int {
        return par1
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem {
        return EnumSortCategoryItem.GENERAL
    }

    companion object {
        @JvmField
        val names: Array<String?> = arrayOf<String?>(
            "tier1engine",  // 0
            "tier1booster"
        ) // 1
    }
}
