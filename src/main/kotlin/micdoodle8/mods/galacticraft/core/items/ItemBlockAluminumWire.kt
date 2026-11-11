package micdoodle8.mods.galacticraft.core.items

import foundation.item.legacyItemValue

import micdoodle8.mods.galacticraft.core.blocks.BlockAluminumWire
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import net.minecraft.world.level.block.Block
import net.minecraft.item.EnumRarity
import net.minecraft.world.item.ItemStack
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class ItemBlockAluminumWire constructor(block: Block?) : ItemBlockDesc(block) {
    init {
        this.setMaxDamage(0)
        this.setHasSubtypes(true)
    }

    /*@Override
    @OnlyIn(Dist.CLIENT)
    public IIcon getIconFromDamage(int par1)
    {
        return this.getBlock().getIcon(0, par1);
    }*/
    public override fun getTranslationKey(itemstack: ItemStack): String {
        val name: String =
            micdoodle8.mods.galacticraft.core.blocks.BlockAluminumWire.EnumWireType.entries.toTypedArray()[itemstack.legacyItemValue].getName()
        return this.getBlock().getTranslationKey() + "." + name
    }

    @OnlyIn(Dist.CLIENT)
    override fun getRarity(par1ItemStack: ItemStack): EnumRarity {
        return (ClientProxyCore.galacticraftItem)!!
    }

    override fun getMetadata(damage: Int): Int {
        return damage
    }
}
