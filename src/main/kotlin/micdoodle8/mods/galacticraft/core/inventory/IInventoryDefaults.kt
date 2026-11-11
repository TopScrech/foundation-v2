package micdoodle8.mods.galacticraft.core.inventory

import net.minecraft.world.Container
import net.minecraft.world.Nameable
import net.minecraft.world.item.ItemStack

/** Native inventory defaults shared by the project's machines and vehicles */
interface IInventoryDefaults : Container, Nameable {
    override fun getMaxStackSize(): Int = 64

    override fun clearContent() {
        for (slot in 0 until containerSize) {
            if (!getItem(slot).isEmpty) setItem(slot, ItemStack.EMPTY)
        }
    }
}
