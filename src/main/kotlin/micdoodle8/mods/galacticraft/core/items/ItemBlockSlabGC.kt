package micdoodle8.mods.galacticraft.core.items

import micdoodle8.mods.galacticraft.core.blocks.BlockDoubleSlabGC
import micdoodle8.mods.galacticraft.core.blocks.BlockSlabGC
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import net.minecraft.block.Block
import net.minecraft.item.EnumRarity
import net.minecraft.item.ItemSlab
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class ItemBlockSlabGC constructor(block: Block, singleSlab: BlockSlabGC, doubleSlab: BlockDoubleSlabGC) :
    ItemSlab(block, singleSlab, doubleSlab) {
    override fun getMetadata(meta: Int): Int {
        return meta and 7
    }

    @SideOnly(Side.CLIENT)
    override fun getRarity(par1ItemStack: ItemStack): EnumRarity {
        return (ClientProxyCore.galacticraftItem)!!
    }

    public override fun getTranslationKey(itemStack: ItemStack): String {
        val slab: BlockSlabGC = Block.getBlockFromItem(itemStack.getItem()) as BlockSlabGC
        return super.getTranslationKey() + "." + slab.getTranslationKey(itemStack.getItemDamage())
    }
}
