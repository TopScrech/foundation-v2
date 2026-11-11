package micdoodle8.mods.galacticraft.core.items

import foundation.item.legacyItemValue

import micdoodle8.mods.galacticraft.core.blocks.BlockDoubleSlabGC
import micdoodle8.mods.galacticraft.core.blocks.BlockSlabGC
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import net.minecraft.world.level.block.Block
import net.minecraft.item.EnumRarity
import net.minecraft.item.ItemSlab
import net.minecraft.world.item.ItemStack
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class ItemBlockSlabGC constructor(block: Block, singleSlab: BlockSlabGC, doubleSlab: BlockDoubleSlabGC) :
    ItemSlab(block, singleSlab, doubleSlab) {
    override fun getMetadata(meta: Int): Int {
        return meta and 7
    }

    @OnlyIn(Dist.CLIENT)
    override fun getRarity(par1ItemStack: ItemStack): EnumRarity {
        return (ClientProxyCore.galacticraftItem)!!
    }

    public override fun getTranslationKey(itemStack: ItemStack): String {
        val slab: BlockSlabGC = Block.getBlockFromItem(itemStack.getItem()) as BlockSlabGC
        return super.getTranslationKey() + "." + slab.getTranslationKey(itemStack.legacyItemValue)
    }
}
