package micdoodle8.mods.galacticraft.core.inventory

import net.minecraft.inventory.IInventory

interface IInventorySettable : IInventory {
    fun setSizeInventory(size: Int)
}
