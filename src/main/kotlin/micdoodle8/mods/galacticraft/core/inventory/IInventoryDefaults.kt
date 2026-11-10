package micdoodle8.mods.galacticraft.core.inventory

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextComponentString
import net.minecraft.util.text.TextComponentTranslation

/*
 * IInventory providing defaults for the eight methods
 * which we normally don't use in tiles and entities.
 * 
 * (It's not necessary to use the fields system where
 * we use a Container for the inventory.)
 * 
 * Override .hasCustomName() if true is required.
 */
interface IInventoryDefaults : IInventory {
    //We don't use these because we use forge containers
    override fun openInventory(player: EntityPlayer) {
    }

    //We don't use these because we use forge containers
    override fun closeInventory(player: EntityPlayer) {
    }

    override fun getField(id: Int): Int {
        return 0
    }

    override fun setField(id: Int, value: Int) {
    }

    override fun getFieldCount(): Int {
        return 0
    }

    override fun clear() {
    }

    /**
     * Override this and return true IF the inventory .getName() is
     * ALREADY a localized name e.g. by GCCoreUtil.translate()
     *
     */
    override fun hasCustomName(): Boolean {
        return false
    }

    override fun getDisplayName(): ITextComponent? {
        return if (this.hasCustomName()) TextComponentString(this.getName()) else TextComponentTranslation(
            this.getName(),
            *arrayOfNulls<Any>(0)
        )
    }
}
