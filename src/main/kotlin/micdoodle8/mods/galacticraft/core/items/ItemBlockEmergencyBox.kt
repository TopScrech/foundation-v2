package micdoodle8.mods.galacticraft.core.items

import foundation.item.legacyItemValue

import net.minecraft.world.level.block.Block
import net.minecraft.world.item.ItemStack

open class ItemBlockEmergencyBox constructor(block: Block?) : ItemBlockDesc(block) {
    init {
        this.setMaxDamage(0)
        this.setHasSubtypes(true)
    }

    public override fun getTranslationKey(par1ItemStack: ItemStack): String? {
        if (par1ItemStack.legacyItemValue == 1) {
            return this.getBlock().getTranslationKey() + ".filled"
        }

        return this.getBlock().getTranslationKey()
    }

    override fun getMetadata(damage: Int): Int {
        return damage
    }
}
