package micdoodle8.mods.galacticraft.planets.asteroids.items

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.items.ISortableItem
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.EnumRarity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class ItemBasicAsteroids constructor(name: String?) : Item(), ISortableItem {
    //    protected IIcon[] icons = new IIcon[ItemBasicAsteroids.names.length];
    init {
        this.setMaxDamage(0)
        this.setHasSubtypes(true)
        this.setTranslationKey(name)
    }

    @SideOnly(Side.CLIENT)
    override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftItemsTab
    }

    @SideOnly(Side.CLIENT)
    override fun getRarity(par1ItemStack: ItemStack): EnumRarity {
        return (ClientProxyCore.galacticraftItem)!!
    }

    /*@Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {
        int i = 0;

        for (String name : ItemBasicAsteroids.names)
        {
            this.icons[i++] = iconRegister.registerIcon(GalacticraftPlanets.TEXTURE_PREFIX + name);
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

    @SideOnly(Side.CLIENT)
    override fun addInformation(
        par1ItemStack: ItemStack,
        worldIn: World?,
        tooltip: MutableList<String?>,
        flagIn: ITooltipFlag
    ) {
        if (par1ItemStack != null && par1ItemStack.getItemDamage() == 5) {
            tooltip.add(GCCoreUtil.translate("item.tier3.desc"))
        }
    }

    override fun getMetadata(par1: Int): Int {
        return par1
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem {
        when (meta) {
            0 -> return EnumSortCategoryItem.INGOT
            5, 6 -> return EnumSortCategoryItem.PLATE
        }
        return EnumSortCategoryItem.GENERAL
    }

    override fun getSmeltingExperience(item: ItemStack): Float {
        when (item.getItemDamage()) {
            5 -> return 2f
            6 -> return 1f
        }
        return -1f
    }

    companion object {
        @JvmField
        var names: Array<String> = arrayOf<String>(
            "ingot_titanium",
            "engine_t2",
            "rocket_fins_t2",
            "shard_iron",
            "shard_titanium",
            "reinforced_plate_t3",
            "compressed_titanium",
            "thermal_cloth",
            "beam_core",
            "dust_titanium"
        )
    }
}
