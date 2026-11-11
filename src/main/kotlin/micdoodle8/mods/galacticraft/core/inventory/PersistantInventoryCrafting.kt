package micdoodle8.mods.galacticraft.core.inventory

import foundation.inventory.FoundationCraftingInventory
import net.minecraft.world.inventory.AbstractContainerMenu

open class PersistantInventoryCrafting : FoundationCraftingInventory(3, 3) {
    @JvmField var craftingEventHandler: AbstractContainerMenu? = null
    override fun eventHandler(): AbstractContainerMenu? = craftingEventHandler
}
