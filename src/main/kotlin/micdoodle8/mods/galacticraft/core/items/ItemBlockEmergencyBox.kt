package micdoodle8.mods.galacticraft.core.items

import net.minecraft.block.Block
import net.minecraft.item.ItemStack

open class ItemBlockEmergencyBox constructor(block: Block?) : ItemBlockDesc(block) {
    init {
        this.setMaxDamage(0)
        this.setHasSubtypes(true)
    }

    public override fun getTranslationKey(par1ItemStack: ItemStack): String? {
        if (par1ItemStack.getItemDamage() == 1) {
            return this.getBlock().getTranslationKey() + ".filled"
        }

        return this.getBlock().getTranslationKey()
    }

    override fun getMetadata(damage: Int): Int {
        return damage
    }
}
