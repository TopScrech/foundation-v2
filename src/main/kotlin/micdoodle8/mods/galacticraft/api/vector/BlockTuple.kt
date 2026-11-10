package micdoodle8.mods.galacticraft.api.vector

import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

open class BlockTuple
constructor(@JvmField var block: Block, @JvmField var meta: Int) {
    override fun toString(): String {
        val item: Item = Item.getItemFromBlock(this.block)
        if (item == null || item === Item.getItemFromBlock(Blocks.AIR)) return this.block.getTranslationKey() + ".name"
        return ItemStack(item, 1, this.meta).getTranslationKey() + ".name"
    }
}
