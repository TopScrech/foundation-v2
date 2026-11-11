package galaxyspace.core.prefab.inventory

import foundation.inventory.FoundationCraftingInventory
import net.minecraft.world.inventory.AbstractContainerMenu

open class GSPersistantInventoryCrafting : FoundationCraftingInventory(3, 3) {
    @JvmField var gs_eventHandler: AbstractContainerMenu? = null
    override fun eventHandler(): AbstractContainerMenu? = gs_eventHandler
}
