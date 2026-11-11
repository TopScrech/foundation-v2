package galaxyspace.systems.SolarSystem.planets.overworld.inventory

import foundation.inventory.FoundationRocketAssemblySlot
import galaxyspace.core.GSItems
import net.minecraft.world.Container

open class SlotRocketAssembly(container: Container, index: Int, x: Int, y: Int) :
    FoundationRocketAssemblySlot(container, index, x, y, GSItems.ROCKET_PARTS, GSItems.ROCKET_MODULES)
