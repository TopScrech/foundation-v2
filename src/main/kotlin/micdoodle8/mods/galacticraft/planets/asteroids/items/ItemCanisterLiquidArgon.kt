package micdoodle8.mods.galacticraft.planets.asteroids.items

import foundation.item.legacyItemValue

import micdoodle8.mods.galacticraft.core.items.ItemCanisterGeneric
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level as World
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class ItemCanisterLiquidArgon constructor(assetName: String?) : ItemCanisterGeneric(assetName) {
    //    protected IIcon[] icons = new IIcon[7];
    init {
        this.setAllowedFluid("liquidargon")
        //this.setTextureName(GalacticraftPlanets.TEXTURE_PREFIX + assetName);
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
        if (itemStack.getMaxDamage() - itemStack.legacyItemValue == 0) {
            return "item.emptyGasCanister"
        }

        if (itemStack.legacyItemValue == 1) {
            return "item.canister.liquidArgon.full"
        }

        return "item.canister.liquidArgon.partial"
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
            tooltip.add(GCCoreUtil.translate("item.canister.liquid_argon.name") + ": " + (par1ItemStack.getMaxDamage() - par1ItemStack.legacyItemValue))
        }
    }
}
