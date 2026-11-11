package micdoodle8.mods.galacticraft.planets.asteroids.items

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.items.ISortableItem
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.EnumRarity
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class ItemHeavyNoseCone constructor(assetName: String?) : Item(), ISortableItem {
    //    public IIcon[] icons;
    init {
        this.setMaxDamage(0)
        this.setHasSubtypes(true)
        this.setTranslationKey(assetName)
        //this.setTextureName(Constants.TEXTURE_PREFIX + assetName);
    }

    /*@OnlyIn(Dist.CLIENT)
    public IIcon getIconFromDamageForRenderPass(int par1, int par2)
    {
        return par2 == 0 ? this.icons[0] : this.icons[1];
    }*/
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
        this.icons = new IIcon[2];
        this.icons[0] = iconRegister.registerIcon(GalacticraftPlanets.TEXTURE_PREFIX + "heavyNoseCone");
        this.icons[1] = iconRegister.registerIcon(GalacticraftPlanets.TEXTURE_PREFIX + "heavyNoseCone.0");
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
    override fun getMetadata(par1: Int): Int {
        return par1
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem {
        return EnumSortCategoryItem.GENERAL
    }
}
