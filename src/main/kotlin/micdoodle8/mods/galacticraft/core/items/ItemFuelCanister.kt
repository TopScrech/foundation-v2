package micdoodle8.mods.galacticraft.core.items

import foundation.item.legacyItemValue

import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level as World
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class ItemFuelCanister constructor(assetName: String?) : ItemCanisterGeneric(assetName), ISortableItem {
    //    protected IIcon[] icons = new IIcon[7];
    init {
        this.setAllowedFluid(if (ConfigManagerCore.useOldFuelFluidID) "fuelgc" else "fuel")
        //        this.setTextureName(Constants.TEXTURE_PREFIX + assetName);
    }

    /*@Override
    @OnlyIn(Dist.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {
        for (int i = 0; i < this.icons.length; i++)
        {
            this.icons[i] = iconRegister.registerIcon(this.getIconString() + "_" + i);
        }
    }*/
    public override fun getTranslationKey(itemStack: ItemStack): String {
        if (itemStack.legacyItemValue == 1) {
            return "item.fuel_canister"
        }

        return "item.fuel_canister_partial"
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
    @OnlyIn(Dist.CLIENT)
    override fun addInformation(
        par1ItemStack: ItemStack,
        worldIn: World?,
        tooltip: MutableList<String?>,
        flagIn: ITooltipFlag
    ) {
        if (par1ItemStack.getMaxDamage() - par1ItemStack.legacyItemValue > 0) {
            tooltip.add(GCCoreUtil.translate("gui.message.fuel.name") + ": " + (par1ItemStack.getMaxDamage() - par1ItemStack.legacyItemValue))
        }
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem {
        return EnumSortCategoryItem.CANISTER
    }
}
