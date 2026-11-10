package micdoodle8.mods.galacticraft.planets.mars.items

import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import net.minecraft.block.Block
import net.minecraft.item.EnumRarity
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemDye
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class ItemBlockTintedGlassPane constructor(block: Block) : ItemBlock(block) {
    init {
        this.setMaxDamage(0)
        this.setHasSubtypes(true)
    }

    override fun getMetadata(damage: Int): Int {
        return damage
    }

    /*@Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int par1)
    {
        return this.getBlock().getIcon(0, par1);
    }*/
    public override fun getTranslationKey(itemstack: ItemStack): String {
        return this.getBlock().getTranslationKey() + "." + ItemDye.DYE_COLORS[itemstack.getItemDamage().inv() and 15]
    }

    @SideOnly(Side.CLIENT)
    override fun getRarity(par1ItemStack: ItemStack): EnumRarity {
        return (ClientProxyCore.galacticraftItem)!!
    }

    public override fun getTranslationKey(): String {
        return this.getBlock().getTranslationKey() + ".0"
    }
}
