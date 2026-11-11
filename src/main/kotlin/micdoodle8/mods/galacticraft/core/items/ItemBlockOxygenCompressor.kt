package micdoodle8.mods.galacticraft.core.items

import foundation.item.legacyItemValue

import micdoodle8.mods.galacticraft.core.blocks.BlockOxygenCompressor
import net.minecraft.world.level.block.Block
import net.minecraft.world.item.ItemStack

open class ItemBlockOxygenCompressor constructor(block: Block?) : ItemBlockDesc(block) {
    init {
        this.setMaxDamage(0)
        this.setHasSubtypes(true)
    }

    override fun getMetadata(damage: Int): Int {
        return damage
    }

    public override fun getTranslationKey(itemstack: ItemStack): String {
        var metadata: Int = 0

        if (itemstack.legacyItemValue >= BlockOxygenCompressor.OXYGEN_DECOMPRESSOR_METADATA) {
            metadata = 1
        } else if (itemstack.legacyItemValue >= BlockOxygenCompressor.OXYGEN_COMPRESSOR_METADATA) {
            metadata = 0
        }

        return this.getBlock().getTranslationKey() + "." + metadata
    }

    public override fun getTranslationKey(): String {
        return this.getBlock().getTranslationKey() + ".0"
    }
}
