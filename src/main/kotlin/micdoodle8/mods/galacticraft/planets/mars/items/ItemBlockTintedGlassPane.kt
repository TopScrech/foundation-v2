package micdoodle8.mods.galacticraft.planets.mars.items

import foundation.item.legacyItemValue

import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import net.minecraft.world.level.block.Block
import net.minecraft.item.EnumRarity
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemDye
import net.minecraft.world.item.ItemStack
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class ItemBlockTintedGlassPane constructor(block: Block) : ItemBlock(block) {
    init {
        this.setMaxDamage(0)
        this.setHasSubtypes(true)
    }

    override fun getMetadata(damage: Int): Int {
        return damage
    }

    /*@Override
    @OnlyIn(Dist.CLIENT)
    public IIcon getIconFromDamage(int par1)
    {
        return this.getBlock().getIcon(0, par1);
    }*/
    public override fun getTranslationKey(itemstack: ItemStack): String {
        return this.getBlock().getTranslationKey() + "." + ItemDye.DYE_COLORS[itemstack.legacyItemValue.inv() and 15]
    }

    @OnlyIn(Dist.CLIENT)
    override fun getRarity(par1ItemStack: ItemStack): EnumRarity {
        return (ClientProxyCore.galacticraftItem)!!
    }

    public override fun getTranslationKey(): String {
        return this.getBlock().getTranslationKey() + ".0"
    }
}
