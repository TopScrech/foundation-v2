package micdoodle8.mods.galacticraft.core.items

import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import net.minecraft.item.EnumRarity
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class ItemFuel constructor(assetName: String?) : Item() {
    init {
        this.setTranslationKey(assetName)
    }

    /*@Override
    @OnlyIn(Dist.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister)
    {
        this.itemIcon = par1IconRegister.registerIcon(Constants.TEXTURE_PREFIX + "fuel_flow");
    }*/
    @OnlyIn(Dist.CLIENT)
    override fun getRarity(par1ItemStack: ItemStack): EnumRarity {
        return (ClientProxyCore.galacticraftItem)!!
    }
}
