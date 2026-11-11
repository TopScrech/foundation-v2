package galaxyspace.systems.SolarSystem.planets.overworld.inventory

import foundation.inventory.FoundationCraftingInventory
import net.minecraft.world.inventory.AbstractContainerMenu

open class RocketAssemblyInventory : FoundationCraftingInventory(2, 8) {
    @JvmField var craftingEventHandler: AbstractContainerMenu? = null
    override fun eventHandler(): AbstractContainerMenu? = craftingEventHandler
}
