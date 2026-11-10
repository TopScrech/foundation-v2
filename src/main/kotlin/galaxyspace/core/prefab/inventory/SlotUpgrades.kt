package galaxyspace.core.prefab.inventory

import micdoodle8.mods.galacticraft.core.inventory.SlotSpecific
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack

open class SlotUpgrades : SlotSpecific {
    private var count = 0

    constructor(
        par2IInventory: IInventory?,
        id: Int,
        x: Int,
        y: Int,
        count: Int,
        vararg itemStacks: ItemStack?
    ) : super((par2IInventory)!!, id, x, y, *itemStacks) {
        this.count = count
    }

    constructor(
        inventory: IInventory?,
        id: Int,
        x: Int,
        y: Int,
        count: Int,
        vararg validClasses: Class<*>
    ) : super(inventory!!, id, x, y, *validClasses) {
        this.count = count
    }

    override fun getSlotStackLimit(): Int {
        if (this.count > 0) return this.count

        return this.inventory.getInventoryStackLimit()
    }
}
