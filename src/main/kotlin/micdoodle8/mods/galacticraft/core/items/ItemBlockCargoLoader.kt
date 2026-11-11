package micdoodle8.mods.galacticraft.core.items

import foundation.item.legacyItemValue

import net.minecraft.world.level.block.Block
import net.minecraft.world.item.ItemStack

open class ItemBlockCargoLoader constructor(block: Block?) : ItemBlockDesc(block) {
    init {
        this.setMaxDamage(0)
        this.setHasSubtypes(true)
    }

    public override fun getTranslationKey(par1ItemStack: ItemStack): String {
        var name: String = ""

        if (par1ItemStack.legacyItemValue < 4) {
            name = "loader"
        } else {
            name = "unloader"
        }

        return this.getBlock().getTranslationKey() + "." + name
    }

    override fun getMetadata(damage: Int): Int {
        return damage
    }
}
