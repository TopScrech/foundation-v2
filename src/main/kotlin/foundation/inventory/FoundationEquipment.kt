package foundation.inventory

import micdoodle8.mods.galacticraft.api.inventory.IInventoryGC
import net.minecraft.world.item.ItemStack

object FoundationEquipment {
    fun equipOne(inventory: IInventoryGC, slot: Int, held: ItemStack): Boolean {
        if (held.isEmpty || slot !in 0 until inventory.containerSize || !inventory.getItem(slot).isEmpty) return false
        inventory.setItem(slot, held.copyWithCount(1))
        held.shrink(1)
        inventory.setChanged()
        return true
    }
}
