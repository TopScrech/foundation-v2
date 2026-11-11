package micdoodle8.mods.galacticraft.core.items

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import net.minecraft.world.level.block.Block
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.EnumRarity
import net.minecraft.world.item.ItemStack
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class ItemBlockCheese constructor(par2Block: Block?) : ItemBlockDesc(par2Block) {
    init {
        this.setMaxStackSize(1)
    }

    override fun isEnchantable(stack: ItemStack): Boolean {
        return false
    }

    override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftBlocksTab
    }

    @OnlyIn(Dist.CLIENT)
    override fun getRarity(par1ItemStack: ItemStack): EnumRarity {
        return (ClientProxyCore.galacticraftItem)!!
    }
}
