package micdoodle8.mods.galacticraft.core.items

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.Entity
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class ItemOilCanister constructor(assetName: String?) : ItemCanisterGeneric(assetName), ISortableItem {
    //    protected IIcon[] icons = new IIcon[7];
    init {
        this.setAllowedFluid(if (ConfigManagerCore.useOldOilFluidID) "oilgc" else "oil")
        this.setContainerItem(this)
        //this.setTextureName(Constants.TEXTURE_PREFIX + assetName);
    }

    /*@Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {
        for (int i = 0; i < this.icons.length; i++)
        {
            this.icons[i] = iconRegister.registerIcon(this.getIconString() + "_" + i);
        }
    }*/
    public override fun getTranslationKey(itemStack: ItemStack): String {
        if (itemStack.getMaxDamage() - itemStack.getItemDamage() == 0) {
            return "item.empty_liquid_canister"
        }

        if (itemStack.getItemDamage() == 1) {
            return "item.oil_canister"
        }

        return "item.oil_canister_partial"
    }

    /*@Override
    public IIcon getIconFromDamage(int par1)
    {
        final int damage = 6 * par1 / this.getMaxDamage();

        if (this.icons.length > damage)
        {
            return this.icons[this.icons.length - damage - 1];
        }

        return super.getIconFromDamage(damage);
    }*/
    @SideOnly(Side.CLIENT)
    override fun addInformation(
        par1ItemStack: ItemStack,
        worldIn: World?,
        tooltip: MutableList<String?>,
        flagIn: ITooltipFlag
    ) {
        if (par1ItemStack.getMaxDamage() - par1ItemStack.getItemDamage() > 0) {
            tooltip.add(GCCoreUtil.translate("gui.message.oil.name") + ": " + (par1ItemStack.getMaxDamage() - par1ItemStack.getItemDamage()))
        }
    }

    @SideOnly(Side.CLIENT)
    override fun getSubItems(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        if (tab === GalacticraftCore.galacticraftItemsTab || tab === CreativeTabs.SEARCH) {
            list.add(ItemStack(this, 1, this.getMaxDamage()))
            list.add(ItemStack(this, 1, 1))
        }
    }

    override fun onUpdate(par1ItemStack: ItemStack, par2World: World, par3Entity: Entity, par4: Int, par5: Boolean) {
        if (ItemCanisterGeneric.Companion.EMPTY == par1ItemStack.getItemDamage()) {
            par1ItemStack.setTagCompound(null)
        } else if (par1ItemStack.getItemDamage() <= 0) {
            par1ItemStack.setItemDamage(1)
        }
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem {
        return EnumSortCategoryItem.CANISTER
    }
}
