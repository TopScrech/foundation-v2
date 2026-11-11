package micdoodle8.mods.galacticraft.core.inventory

import net.minecraft.world.Container as IInventory

interface IInventorySettable : IInventory {
    fun setSizeInventory(size: Int)
}
